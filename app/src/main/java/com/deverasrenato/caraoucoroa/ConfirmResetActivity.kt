package com.deverasrenato.caraoucoroa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// Aberta pelo botão "Zerar tudo" do widget. O widget não consegue mostrar uma caixa de
// confirmação, então esta tela transparente mostra a pergunta "Zerar tudo?" por cima da
// tela inicial e fecha sozinha quando o usuário responde.
class ConfirmResetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askResetAll(this, afterReset = {}, afterClose = { finish() })
    }
}
