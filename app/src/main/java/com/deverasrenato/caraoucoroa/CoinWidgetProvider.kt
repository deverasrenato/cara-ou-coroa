package com.deverasrenato.caraoucoroa

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import kotlin.random.Random

// O widget da tela inicial.
// Ao tocar na moeda, ela gira e mostra o resultado, sem abrir o app.
class CoinWidgetProvider : AppWidgetProvider() {

    // Chamado quando o widget é colocado na tela
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (id in appWidgetIds) {
            showResult(context, appWidgetManager, id)
        }
    }

    // Chamado quando chega qualquer aviso para o widget (inclusive os toques)
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_FLIP) {
            flip(context)
        }
        if (intent.action == ACTION_REPLAY) {
            // "Jogar novamente?": zera o placar e mantém pergunta, textos e modo
            CoinStore.resetScores(context)
            updateAll(context)
        }
    }

    private fun flip(context: Context) {
        // Se já está girando, ignora o toque
        if (isFlipping) return

        // Série terminada: não gira mais. O usuário toca em "Jogar novamente?"
        if (CoinStore.isSeriesFinished(context)) return

        isFlipping = true

        // goAsync() faz o Android esperar a animação terminar
        val pendingResult = goAsync()
        val appContext = context.applicationContext

        // A animação "dorme" entre os quadros, então roda em outra thread
        Thread {
            try {
                val manager = AppWidgetManager.getInstance(appContext)
                val ids = manager.getAppWidgetIds(
                    ComponentName(appContext, CoinWidgetProvider::class.java)
                )

                val startSide = CoinStore.lastSide(appContext)
                val result = if (Random.nextBoolean()) HEADS else TAILS

                // Trocar a imagem várias vezes seguidas cria a animação
                for (i in 1..Coin.FRAME_COUNT) {
                    val views = RemoteViews(appContext.packageName, R.layout.widget_coin)
                    val progress = i / Coin.FRAME_COUNT.toFloat()
                    // Só a imagem muda durante o giro: o texto embaixo fica parado
                    views.setImageViewBitmap(R.id.coin_image, Coin.frame(startSide, result, progress))
                    manager.partiallyUpdateAppWidget(ids, views)
                    Thread.sleep(Coin.FRAME_DELAY_MS)
                }

                CoinStore.saveResult(appContext, result)

                // Mostra o placar novo, ainda sem a camada do vencedor (a moeda aparece parada)
                for (id in ids) {
                    showResult(appContext, manager, id, false)
                }

                // Se a série terminou, espera um instante e mostra o vencedor por cima da moeda
                if (CoinStore.isSeriesFinished(appContext)) {
                    Thread.sleep(Coin.WINNER_DELAY_MS)
                    for (id in ids) {
                        showResult(appContext, manager, id, true)
                    }
                }
            } finally {
                isFlipping = false
                pendingResult.finish()
            }
        }.start()
    }

    companion object {
        const val ACTION_FLIP = "com.deverasrenato.caraoucoroa.FLIP"
        const val ACTION_REPLAY = "com.deverasrenato.caraoucoroa.REPLAY"

        @Volatile
        private var isFlipping = false

        // Desenha o widget parado, mostrando o último resultado.
        // showWinner: mostra (ou não) a camada do vencedor, quando a série terminou.
        fun showResult(context: Context, manager: AppWidgetManager, id: Int, showWinner: Boolean = true) {
            val views = RemoteViews(context.packageName, R.layout.widget_coin)

            val side = CoinStore.lastSide(context)
            views.setImageViewBitmap(R.id.coin_image, Coin.still(side))

            // Os 5 textos abaixo da moeda
            val texts = if (CoinStore.hasResult(context)) {
                boardTexts(context)
            } else {
                startBoard("Toque para girar")
            }
            for (i in 0..4) {
                views.setTextViewText(BOARD_IDS[i], texts[i])
            }

            // Camada do vencedor, por cima da moeda
            val winner = CoinStore.winnerSide(context)
            if (showWinner && winner >= 0) {
                views.setViewVisibility(R.id.winner_layer, View.VISIBLE)
                views.setTextViewText(R.id.winner_name, sideLabel(context, winner))
            } else {
                views.setViewVisibility(R.id.winner_layer, View.GONE)
            }

            // Tocar na moeda envia o aviso ACTION_FLIP (gira na hora)
            val flipIntent = Intent(context, CoinWidgetProvider::class.java)
            flipIntent.action = ACTION_FLIP
            val flipPending = PendingIntent.getBroadcast(
                context, 0, flipIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.flip_area, flipPending)

            // Tocar em "Jogar novamente?" envia o aviso ACTION_REPLAY
            val replayIntent = Intent(context, CoinWidgetProvider::class.java)
            replayIntent.action = ACTION_REPLAY
            val replayPending = PendingIntent.getBroadcast(
                context, 2, replayIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.replay_button, replayPending)

            // Tocar no botão do lápis abre a janelinha para digitar a pergunta
            val editIntent = Intent(context, QuickFlipActivity::class.java)
            editIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val editPending = PendingIntent.getActivity(
                context, 1, editIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.edit_button, editPending)

            manager.updateAppWidget(id, views)
        }

        // Atualiza todos os widgets (usado quando o app muda alguma coisa)
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, CoinWidgetProvider::class.java)
            )
            for (id in ids) {
                showResult(context, manager, id)
            }
        }
    }
}
