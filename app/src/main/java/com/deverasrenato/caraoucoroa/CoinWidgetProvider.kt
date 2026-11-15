package com.deverasrenato.caraoucoroa

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlin.random.Random

// O widget da tela inicial.
// Ao tocar nele, a moeda gira e mostra o resultado, sem abrir o app.
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

    // Chamado quando chega qualquer aviso para o widget (inclusive o toque)
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_FLIP) {
            flip(context)
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

                val startSide = CoinStore.lastSide(appContext)
                val result = if (Random.nextBoolean()) HEADS else TAILS

                // Trocar a imagem várias vezes seguidas cria a animação
                for (i in 1..Coin.FRAME_COUNT) {
                    val views = RemoteViews(appContext.packageName, R.layout.widget_coin)
                    val progress = i / Coin.FRAME_COUNT.toFloat()
                    views.setImageViewBitmap(R.id.coin_image, Coin.frame(startSide, result, progress))
                    views.setTextViewText(R.id.coin_text, "Girando...")
                    manager.partiallyUpdateAppWidget(ids, views)
                    Thread.sleep(Coin.FRAME_DELAY_MS)
                }

                CoinStore.save(appContext, result)
                for (id in ids) {
                    showResult(appContext, manager, id)
                }
            } finally {
                isFlipping = false
                pendingResult.finish()
            }
        }.start()
    }

    companion object {
        const val ACTION_FLIP = "com.deverasrenato.caraoucoroa.FLIP"

        @Volatile
        private var isFlipping = false

        // Desenha o widget parado, mostrando o último resultado
        fun showResult(context: Context, manager: AppWidgetManager, id: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_coin)

            val side = CoinStore.lastSide(context)
            views.setImageViewBitmap(R.id.coin_image, Coin.still(side))

            val text = if (CoinStore.hasResult(context)) {
                "Deu ${sideName(side)}!"
            } else {
                "Toque para girar"
            }
            views.setTextViewText(R.id.coin_text, text)

            // Tocar em qualquer lugar do widget envia o aviso ACTION_FLIP
            val intent = Intent(context, CoinWidgetProvider::class.java)
            intent.action = ACTION_FLIP
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            manager.updateAppWidget(id, views)
        }

        // Atualiza todos os widgets (usado quando o app gira a moeda)
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
