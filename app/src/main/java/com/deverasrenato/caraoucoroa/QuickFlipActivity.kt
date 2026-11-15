package com.deverasrenato.caraoucoroa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

// A "janelinha" que abre por cima da tela inicial quando o usuário toca no lápis do widget.
// Nela ele escolhe o modo (livre, melhor de 5 ou de 7), digita a pergunta (opcional) e
// o que cada lado significa, e manda girar. Também pode zerar tudo.
class QuickFlipActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_flip)

        val seriesGroup = findViewById<RadioGroup>(R.id.series_group)
        val questionInput = findViewById<EditText>(R.id.quick_question_input)
        val headsInput = findViewById<EditText>(R.id.quick_heads_input)
        val tailsInput = findViewById<EditText>(R.id.quick_tails_input)
        val flipButton = findViewById<Button>(R.id.quick_flip_button)
        val resetButton = findViewById<Button>(R.id.quick_reset_button)

        // Preenche com o que já estava guardado
        showSeriesSize(seriesGroup, CoinStore.getSeriesSize(this))
        questionInput.setText(CoinStore.getQuestion(this))
        headsInput.setText(CoinStore.getLabel(this, HEADS))
        tailsInput.setText(CoinStore.getLabel(this, TAILS))

        flipButton.setOnClickListener {
            CoinStore.saveSeriesSize(this, readSeriesSize(seriesGroup))
            CoinStore.saveQuestion(this, questionInput.text.toString())
            CoinStore.saveLabel(this, HEADS, headsInput.text.toString())
            CoinStore.saveLabel(this, TAILS, tailsInput.text.toString())

            // Apertar "Girar" aqui começa uma série nova, se a anterior já tinha um vencedor
            if (CoinStore.isSeriesFinished(this)) {
                CoinStore.resetScores(this)
            }

            // Pede para o widget girar a moeda (a animação acontece nele)
            val intent = Intent(this, CoinWidgetProvider::class.java)
            intent.action = CoinWidgetProvider.ACTION_FLIP
            sendBroadcast(intent)

            // Fecha a janelinha e volta para a tela inicial
            finish()
        }

        // Zerar tudo: pergunta antes de apagar. Depois de apagar, fecha a janelinha.
        resetButton.setOnClickListener {
            askResetAll(this) {
                finish()
            }
        }
    }
}
