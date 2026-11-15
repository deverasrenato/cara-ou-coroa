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
            CoinStore.startNewGame(context)
            updateAll(context)
        }
    }

    private fun flip(context: Context) {
        // Se já está girando, ignora o toque
        if (isFlipping) return

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

                // Série terminada: tocar na moeda entende "jogar novamente". Primeiro o balão
                // do vencedor some; só depois a moeda gira.
                if (CoinStore.isSeriesFinished(appContext)) {
                    CoinStore.startNewGame(appContext)
                    for (id in ids) {
                        showResult(appContext, manager, id, false)
                    }
                    // Só espera o balão sumir quando ele existia (no modo de 1 giro não há balão)
                    if (!CoinStore.isSingleMode(appContext)) {
                        Thread.sleep(250)
                    }
                }

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
                vibrate(appContext, 40) // toquinho quando a moeda para

                // Mostra o placar novo, ainda sem a camada do vencedor (a moeda aparece parada)
                for (id in ids) {
                    showResult(appContext, manager, id, false)
                }

                // Se a série terminou, espera um instante e mostra o vencedor por cima da moeda
                if (CoinStore.isSeriesFinished(appContext) && !CoinStore.isSingleMode(appContext)) {
                    Thread.sleep(Coin.WINNER_DELAY_MS)
                    for (id in ids) {
                        showResult(appContext, manager, id, true)
                    }
                    vibrate(appContext, 150) // vibração mais longa para o vencedor
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
            val texts = currentBoard(context, "Toque para girar")
            for (i in 0..4) {
                views.setTextViewText(BOARD_IDS[i], texts[i])
            }
            // Nome do lado que saiu, embaixo da moeda; sem placar no modo de 1 giro
            views.setTextViewText(R.id.result_name, resultText(context))
            views.setViewVisibility(R.id.score_row, if (showScoreRow(context)) View.VISIBLE else View.GONE)

            // Camada do vencedor, por cima da moeda
            val winner = CoinStore.winnerSide(context)
            if (showWinner && winner >= 0 && !CoinStore.isSingleMode(context)) {
                views.setViewVisibility(R.id.winner_layer, View.VISIBLE)
                views.setTextViewText(R.id.winner_name, sideLabel(context, winner))
                // No modo "Cara ou Coroa?" (1 giro) não faz sentido escrever "venceu!"
                val suffixVisibility = if (CoinStore.isSingleMode(context)) View.GONE else View.VISIBLE
                views.setViewVisibility(R.id.winner_suffix, suffixVisibility)
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

            // Tocar em "Zerar tudo" (na camada do vencedor) abre a caixa de confirmação
            val resetIntent = Intent(context, ConfirmResetActivity::class.java)
            resetIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val resetPending = PendingIntent.getActivity(
                context, 3, resetIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.winner_reset_button, resetPending)

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
