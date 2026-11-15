package com.deverasrenato.caraoucoroa

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

// Tela principal do app
class MainActivity : AppCompatActivity() {

    private lateinit var coinImage: ImageView
    private lateinit var resultText: TextView
    private lateinit var flipButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        coinImage = findViewById(R.id.coin_image)
        resultText = findViewById(R.id.result_text)
        flipButton = findViewById(R.id.flip_button)

        showStillCoin()

        flipButton.setOnClickListener { flip() }
        coinImage.setOnClickListener { flip() }
    }

    // Quando o usuário volta para o app (por exemplo, depois de girar pelo widget),
    // mostra o resultado mais recente
    override fun onResume() {
        super.onResume()
        showStillCoin()
    }

    private fun showStillCoin() {
        val side = CoinStore.lastSide(this)
        coinImage.setImageBitmap(Coin.still(side))
        resultText.text = if (CoinStore.hasResult(this)) {
            "Deu ${sideName(side)}!"
        } else {
            "Toque na moeda"
        }
    }

    private fun flip() {
        flipButton.isEnabled = false
        resultText.text = "Girando..."

        val startSide = CoinStore.lastSide(this)
        val result = if (Random.nextBoolean()) HEADS else TAILS

        // O animador vai de 0 até 1 e, a cada passo, desenhamos um quadro da moeda
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = Coin.FRAME_COUNT * Coin.FRAME_DELAY_MS
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {
            val progress = it.animatedValue as Float
            coinImage.setImageBitmap(Coin.frame(startSide, result, progress))
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                CoinStore.save(this@MainActivity, result)
                resultText.text = "Deu ${sideName(result)}!"
                flipButton.isEnabled = true
                // Deixa o widget igual ao app
                CoinWidgetProvider.updateAll(this@MainActivity)
            }
        })
        animator.start()
    }
}
