package com.deverasrenato.caraoucoroa

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// Tela de abertura: a moeda gira devagar, aparece o nome e, em seguida, o app segue.
// Na primeira vez (ou se o usuário pediu) vai para as boas-vindas; senão, direto para o app.
class SplashActivity : AppCompatActivity() {

    private var animator: ValueAnimator? = null
    private var left = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        findViewById<TextView>(R.id.splash_version).text = getString(R.string.version_label, versionName)

        val coinImage = findViewById<ImageView>(R.id.splash_coin)
        coinImage.setImageBitmap(Coin.still(HEADS))

        // Um giro lento (4 meios giros em 2,6 segundos) que termina no mesmo lado em que começou
        val spin = ValueAnimator.ofFloat(0f, 1f)
        spin.duration = 2600
        spin.interpolator = LinearInterpolator()
        spin.addUpdateListener {
            val progress = it.animatedValue as Float
            coinImage.setImageBitmap(Coin.frame(HEADS, HEADS, progress, 4))
        }
        spin.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Uma pausa curta para ler o nome, depois segue
                coinImage.postDelayed({ goNext() }, 300)
            }
        })
        animator = spin
        spin.start()
    }

    // Vai para a próxima tela (só uma vez, mesmo que a animação termine de novo)
    private fun goNext() {
        if (left || isFinishing) return
        left = true
        val next = if (CoinStore.isWelcomeDue(this)) WelcomeActivity::class.java else MainActivity::class.java
        startActivity(Intent(this, next))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        animator?.cancel()
    }
}
