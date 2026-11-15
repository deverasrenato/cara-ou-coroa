package com.deverasrenato.caraoucoroa

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// Boas-vindas: explica como adicionar o widget (o melhor do app).
// Aparece na primeira vez. O botão de baixo liga e desliga "mostrar sempre que abrir".
class WelcomeActivity : AppCompatActivity() {

    private lateinit var toggle: TextView
    private var pulse: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        findViewById<ImageView>(R.id.welcome_logo).setImageBitmap(Coin.still(HEADS))
        findViewById<ImageView>(R.id.welcome_coin).setImageBitmap(Coin.still(HEADS))

        // O widget de exemplo cresce e diminui devagar, para chamar atenção
        val widget = findViewById<View>(R.id.welcome_widget)
        val animator = ObjectAnimator.ofPropertyValuesHolder(
            widget,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.05f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.05f)
        )
        animator.duration = 1000
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.start()
        pulse = animator

        toggle = findViewById(R.id.welcome_toggle)
        showToggle()
        toggle.setOnClickListener {
            CoinStore.saveWelcomeAlways(this, !CoinStore.isWelcomeAlways(this))
            showToggle()
        }

        // "Entendi": marca que o usuário já viu e abre o app
        findViewById<Button>(R.id.welcome_open).setOnClickListener {
            CoinStore.markWelcomeSeen(this)
            // Se veio da tela principal (para rever), só volta para ela
            if (!intent.getBooleanExtra(EXTRA_FROM_MAIN, false)) {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }
    }

    // Marcado (com o sinal de certo) = só na primeira vez. Desmarcado = toda vez que abrir.
    private fun showToggle() {
        if (CoinStore.isWelcomeAlways(this)) {
            toggle.setText(R.string.welcome_always)
            toggle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            toggle.setText(R.string.welcome_once)
            toggle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        pulse?.cancel()
    }

    companion object {
        const val EXTRA_FROM_MAIN = "from_main"
    }
}
