package com.deverasrenato.caraoucoroa

import android.os.Bundle
import android.widget.Button
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

// A "janelinha" que abre por cima da tela inicial quando o usuário toca no lápis do widget.
// Nela ele escolhe o modo (livre, melhor de 5 ou de 7), digita a pergunta (opcional) e
// o que cada lado significa, e salva. O widget volta zerado, esperando o toque na moeda.
// Também pode zerar tudo.
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

        // Meus layouts: tocar em um configura tudo, atualiza o widget e fecha a janelinha
        val presetsContainer = findViewById<LinearLayout>(R.id.presets_container)
        fillPresetRow(this, presetsContainer) { finish() }

        // "Salvar como layout": guarda o que está nos campos agora e redesenha a fileira
        findViewById<View>(R.id.quick_save_preset_button).setOnClickListener {
            askSavePreset(
                this,
                readSeriesSize(seriesGroup),
                questionInput.text.toString(),
                headsInput.text.toString(),
                tailsInput.text.toString()
            ) { fillPresetRow(this, presetsContainer) { finish() } }
        }

        flipButton.setOnClickListener {
            CoinStore.saveSeriesSize(this, readSeriesSize(seriesGroup))
            CoinStore.saveQuestion(this, questionInput.text.toString())
            CoinStore.saveLabel(this, HEADS, headsInput.text.toString())
            CoinStore.saveLabel(this, TAILS, tailsInput.text.toString())

            // Salvar começa um jogo novo: o widget volta zerado e espera o toque na moeda
            CoinStore.startNewGame(this)
            CoinWidgetProvider.updateAll(this)

            // Fecha a janelinha e volta para a tela inicial
            finish()
        }

        // Zerar tudo: pergunta antes de apagar. Depois de apagar, fecha a janelinha.
        resetButton.setOnClickListener {
            askResetAll(this, afterReset = { finish() })
        }
    }
}
