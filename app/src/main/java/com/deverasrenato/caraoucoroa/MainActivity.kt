package com.deverasrenato.caraoucoroa

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

// Velocidade mínima do deslize para cima (em pixels por segundo)
private const val SWIPE_MIN_SPEED = 800f

// Tela principal do app
class MainActivity : AppCompatActivity() {

    private lateinit var coinImage: ImageView
    private lateinit var flipButton: Button
    private lateinit var resetButton: Button
    private lateinit var seriesGroup: RadioGroup
    private lateinit var questionInput: EditText
    private lateinit var headsInput: EditText
    private lateinit var tailsInput: EditText
    private lateinit var winnerLayer: View
    private lateinit var winnerName: TextView
    private lateinit var replayButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        coinImage = findViewById(R.id.coin_image)
        flipButton = findViewById(R.id.flip_button)
        resetButton = findViewById(R.id.reset_button)
        seriesGroup = findViewById(R.id.series_group)
        questionInput = findViewById(R.id.question_input)
        headsInput = findViewById(R.id.heads_input)
        tailsInput = findViewById(R.id.tails_input)
        winnerLayer = findViewById(R.id.winner_layer)
        winnerName = findViewById(R.id.winner_name)
        replayButton = findViewById(R.id.replay_button)

        flipButton.setOnClickListener { flip() }

        // Na moeda: tocar OU deslizar o dedo para cima gira (como jogar a moeda para o alto).
        // O GestureDetector reconhece cada gesto separado, então um deslize não conta como toque.
        val detector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean = true

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                flip()
                return true
            }

            override fun onFling(
                e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float
            ): Boolean {
                // velocityY negativo = dedo subindo
                if (velocityY < -SWIPE_MIN_SPEED && Math.abs(velocityY) > Math.abs(velocityX)) {
                    flip()
                    return true
                }
                return false
            }
        })
        coinImage.setOnTouchListener { view, event ->
            // A tela rola para cima e para baixo; pedimos para ela não roubar o deslize da moeda
            view.parent.requestDisallowInterceptTouchEvent(true)
            detector.onTouchEvent(event)
        }
        replayButton.setOnClickListener { replay() }

        // Zerar tudo: pergunta antes de apagar. Depois, limpa os campos da tela.
        resetButton.setOnClickListener {
            askResetAll(this) {
                loadInputs()
                showStillCoin()
            }
        }
    }

    // Quando o usuário volta para o app (por exemplo, depois de usar o widget),
    // mostra tudo atualizado
    override fun onResume() {
        super.onResume()
        loadInputs()
        showStillCoin()
    }

    // Ao sair da tela, guarda o que foi digitado e atualiza o widget
    override fun onPause() {
        super.onPause()
        saveInputs()
        CoinWidgetProvider.updateAll(this)
    }

    // Quando o app sai da tela (o usuário foi para outro app ou para a tela inicial),
    // o modo livre recomeça. Girar a tela do celular não conta como sair.
    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations && CoinStore.isFreeMode(this)) {
            CoinStore.resetScores(this)
            CoinWidgetProvider.updateAll(this)
        }
    }

    // Coloca nos campos da tela o que está guardado
    private fun loadInputs() {
        showSeriesSize(seriesGroup, CoinStore.getSeriesSize(this))
        questionInput.setText(CoinStore.getQuestion(this))
        headsInput.setText(CoinStore.getLabel(this, HEADS))
        tailsInput.setText(CoinStore.getLabel(this, TAILS))
    }

    // Guarda o que está nos campos da tela
    private fun saveInputs() {
        CoinStore.saveSeriesSize(this, readSeriesSize(seriesGroup))
        CoinStore.saveQuestion(this, questionInput.text.toString())
        CoinStore.saveLabel(this, HEADS, headsInput.text.toString())
        CoinStore.saveLabel(this, TAILS, tailsInput.text.toString())
    }

    // Escreve os 5 textos fixos abaixo da moeda (cada um sempre no mesmo lugar)
    private fun showBoard() {
        val texts = if (CoinStore.hasResult(this)) {
            boardTexts(this)
        } else {
            startBoard("Toque na moeda")
        }
        for (i in 0..4) {
            findViewById<TextView>(BOARD_IDS[i]).text = texts[i]
        }
    }

    // Mostra (ou esconde) a camada do vencedor, por cima da moeda
    private fun showWinner(show: Boolean) {
        if (show) {
            winnerName.text = sideLabel(this, CoinStore.winnerSide(this))
            winnerLayer.visibility = View.VISIBLE
        } else {
            winnerLayer.visibility = View.GONE
        }
    }

    private fun showStillCoin() {
        val side = CoinStore.lastSide(this)
        coinImage.setImageBitmap(Coin.still(side))
        showBoard()
        showWinner(CoinStore.isSeriesFinished(this))
    }

    // "Jogar novamente?": zera o placar e começa outra série com as mesmas configurações
    private fun replay() {
        CoinStore.resetScores(this)
        showStillCoin()
        CoinWidgetProvider.updateAll(this)
    }

    private fun flip() {
        // Guarda o que o usuário digitou antes de girar
        saveInputs()

        // Série terminada: não gira mais. O usuário toca em "Jogar novamente?"
        if (CoinStore.isSeriesFinished(this)) return

        flipButton.isEnabled = false

        val startSide = CoinStore.lastSide(this)
        val result = if (Random.nextBoolean()) HEADS else TAILS

        // O animador vai de 0 até 1 e, a cada passo, desenhamos um quadro da moeda.
        // O texto embaixo não muda durante o giro: só depois que a moeda para.
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = Coin.FRAME_COUNT * Coin.FRAME_DELAY_MS
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {
            val progress = it.animatedValue as Float
            coinImage.setImageBitmap(Coin.frame(startSide, result, progress))
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                CoinStore.saveResult(this@MainActivity, result)
                showBoard()
                flipButton.isEnabled = true
                // Deixa o widget igual ao app
                CoinWidgetProvider.updateAll(this@MainActivity)

                // Se a série terminou, espera um instante (para ver a moeda) e mostra o vencedor
                if (CoinStore.isSeriesFinished(this@MainActivity)) {
                    coinImage.postDelayed({ showWinner(true) }, Coin.WINNER_DELAY_MS)
                }
            }
        })
        animator.start()
    }
}
