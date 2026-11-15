package com.deverasrenato.caraoucoroa

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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
    private lateinit var vibrationCheck: CheckBox

    // Número do giro atual. Quando o usuário troca o modo no meio de um giro, o número
    // aumenta e o giro antigo percebe que não vale mais (não guarda resultado).
    private var flipVersion = 0
    private var currentAnimator: ValueAnimator? = null

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
        vibrationCheck = findViewById(R.id.vibration_check)

        // Mostra a versão do app (vem do build.gradle.kts, em versionName)
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        findViewById<TextView>(R.id.version_text).text = getString(R.string.version_label, versionName)

        // Guarda na hora a escolha de vibrar ou não
        vibrationCheck.setOnCheckedChangeListener { _, checked ->
            CoinStore.saveVibration(this, checked)
        }

        flipButton.setOnClickListener { flip() }

        // "Salvar como layout": guarda o modo, a pergunta e os textos que estão nos campos agora
        findViewById<View>(R.id.save_preset_button).setOnClickListener {
            askSavePreset(
                this,
                readSeriesSize(seriesGroup),
                questionInput.text.toString(),
                headsInput.text.toString(),
                tailsInput.text.toString()
            ) { refreshPresets() }
        }

        // Rever as boas-vindas do widget
        findViewById<View>(R.id.welcome_review_button).setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.putExtra(WelcomeActivity.EXTRA_FROM_MAIN, true)
            startActivity(intent)
        }

        // Trocar "Quantas vezes jogar?" começa um jogo novo (placar zerado, moeda parada).
        // Só vale quando o modo realmente mudou: assim, marcar a opção ao abrir a tela não zera nada.
        seriesGroup.setOnCheckedChangeListener { group, _ ->
            val newSize = readSeriesSize(group)
            if (newSize != CoinStore.getSeriesSize(this)) {
                // Se a moeda estava girando, cancela: o resultado antigo não vale mais
                flipVersion++
                currentAnimator?.cancel()
                CoinStore.saveSeriesSize(this, newSize) // trocar o modo já começa um jogo novo
                flipButton.isEnabled = true
                showStillCoin()
                CoinWidgetProvider.updateAll(this)
            }
        }

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

        // Botão pequeno "Zerar tudo" na camada do vencedor (com a mesma confirmação)
        findViewById<TextView>(R.id.winner_reset_button).setOnClickListener {
            askResetAll(this, afterReset = {
                loadInputs()
                showStillCoin()
            })
        }

        // Zerar tudo: pergunta antes de apagar. Depois, limpa os campos da tela.
        resetButton.setOnClickListener {
            askResetAll(this, afterReset = {
                loadInputs()
                showStillCoin()
            })
        }
    }

    // Quando o usuário volta para o app (por exemplo, depois de usar o widget),
    // mostra tudo atualizado
    override fun onResume() {
        super.onResume()
        loadInputs()
        showStillCoin()
        refreshPresets()
    }

    // Desenha a fileira "Meus layouts". Tocar em um layout configura tudo e deixa o jogo pronto.
    private fun refreshPresets() {
        val container = findViewById<LinearLayout>(R.id.presets_container)
        fillPresetRow(this, container) {
            // Se a moeda estava girando, cancela: o resultado antigo não vale mais
            flipVersion++
            currentAnimator?.cancel()
            flipButton.isEnabled = true
            loadInputs()
            showStillCoin()
        }
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
            CoinStore.startNewGame(this)
            CoinWidgetProvider.updateAll(this)
        }
    }

    // Coloca nos campos da tela o que está guardado
    private fun loadInputs() {
        showSeriesSize(seriesGroup, CoinStore.getSeriesSize(this))
        vibrationCheck.isChecked = CoinStore.isVibrationOn(this)
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
        val texts = currentBoard(this, "Toque na moeda")
        for (i in 0..4) {
            findViewById<TextView>(BOARD_IDS[i]).text = texts[i]
        }
        // Nome do lado que saiu, embaixo da moeda
        findViewById<TextView>(R.id.result_name).text = resultText(this)
        // No modo de 1 giro não há placar: a linha some e o cartão fica menor
        findViewById<View>(R.id.score_row).visibility =
            if (showScoreRow(this)) View.VISIBLE else View.GONE
    }

    // Mostra (ou esconde) a camada do vencedor, por cima da moeda
    // No modo "Cara ou Coroa?" (1 giro) não há balão: o resultado escrito embaixo da moeda basta
    private fun showWinner(show: Boolean) {
        if (show && !CoinStore.isSingleMode(this)) {
            winnerName.text = sideLabel(this, CoinStore.winnerSide(this))
            winnerLayer.visibility = View.VISIBLE
        } else {
            winnerLayer.visibility = View.GONE
        }
    }

    private fun showStillCoin() {
        val side = CoinStore.lastSide(this)
        coinImage.setImageBitmap(Coin.still(side))
        // A moeda não tem mais texto: o leitor de tela diz qual lado está para cima
        coinImage.contentDescription = sideName(side)
        showBoard()
        showWinner(CoinStore.isSeriesFinished(this))
    }

    // "Jogar novamente?": zera o placar e começa outra série com as mesmas configurações
    private fun replay() {
        CoinStore.startNewGame(this)
        showStillCoin()
        CoinWidgetProvider.updateAll(this)
    }

    private fun flip() {
        // Guarda o que o usuário digitou antes de girar
        saveInputs()

        // Já está girando (ou fechando o balão): ignora o toque
        if (!flipButton.isEnabled) return
        flipButton.isEnabled = false

        // Série terminada: girar entende "jogar novamente". Primeiro o balão fecha (rápido);
        // só depois a moeda gira.
        if (CoinStore.isSeriesFinished(this)) {
            flipVersion++ // cancela o balão do vencedor que ainda não tinha aparecido
            if (winnerLayer.visibility == View.VISIBLE) {
                winnerLayer.animate().alpha(0f).setDuration(200).withEndAction {
                    winnerLayer.alpha = 1f
                    startNewRound()
                    spin()
                }.start()
            } else {
                startNewRound()
                spin()
            }
            return
        }
        spin()
    }

    // Começa outra série: zera o placar e tira a camada do vencedor
    private fun startNewRound() {
        CoinStore.startNewGame(this)
        showWinner(false)
        showBoard()
        CoinWidgetProvider.updateAll(this)
    }

    // A animação do giro e o que acontece quando a moeda para
    private fun spin() {
        val myVersion = flipVersion

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
                // O modo mudou durante o giro: este giro foi descartado
                if (myVersion != flipVersion) return
                CoinStore.saveResult(this@MainActivity, result)
                vibrate(this@MainActivity, 40) // toquinho quando a moeda para
                showBoard()
                flipButton.isEnabled = true
                // Deixa o widget igual ao app
                CoinWidgetProvider.updateAll(this@MainActivity)

                // Se a série terminou, espera um instante (para ver a moeda) e mostra o vencedor
                if (CoinStore.isSeriesFinished(this@MainActivity) && !CoinStore.isSingleMode(this@MainActivity)) {
                    coinImage.postDelayed({
                        if (myVersion != flipVersion) return@postDelayed
                        showWinner(true)
                        vibrate(this@MainActivity, 150) // vibração mais longa para o vencedor
                    }, Coin.WINNER_DELAY_MS)
                }
            }
        })
        currentAnimator = animator
        animator.start()
    }
}
