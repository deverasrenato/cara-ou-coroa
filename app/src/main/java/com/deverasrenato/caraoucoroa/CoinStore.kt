package com.deverasrenato.caraoucoroa

import android.content.Context
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.max

// Os dois lados da moeda
const val HEADS = 0 // cara
const val TAILS = 1 // coroa

// Texto que aparece escrito na moeda
fun sideName(side: Int): String {
    return if (side == HEADS) "CARA" else "COROA"
}

// Nome de um lado: o texto que o usuário digitou ou, se não digitou, CARA / COROA
fun sideLabel(context: Context, side: Int): String {
    val label = CoinStore.getLabel(context, side)
    if (label.isNotEmpty()) {
        return label
    }
    return sideName(side)
}

// Guarda tudo o que o app precisa lembrar (o app e o widget leem daqui)
object CoinStore {
    private fun prefs(context: Context) =
        context.getSharedPreferences("coin", Context.MODE_PRIVATE)

    // ----- Último resultado -----

    // -1 significa que o usuário ainda nunca girou a moeda
    fun hasResult(context: Context): Boolean {
        return prefs(context).getInt("side", -1) >= 0
    }

    fun lastSide(context: Context): Int {
        return max(0, prefs(context).getInt("side", -1))
    }

    // Guarda o resultado de um giro e atualiza o placar e a sequência
    fun saveResult(context: Context, side: Int) {
        // Por segurança: se a série já tinha terminado, começa uma nova
        // No modo "1 vez" cada giro vale sozinho: não acumula nada
        if (isSeriesFinished(context) || isSingleMode(context)) {
            resetScores(context)
        }

        // A sequência guarda só os últimos 7 giros (o máximo de uma série)
        var history = getHistory(context) + side
        if (history.length > 7) {
            history = history.takeLast(7)
        }

        prefs(context).edit()
            .putInt("side", side)
            .putInt("score_$side", getScore(context, side) + 1)
            .putString("history", history)
            .apply()
    }

    // ----- Modo de jogo -----

    // 1 = girar só 1 vez (padrão), 0 = livre (sem fim, conta para sempre),
    // 5 = melhor de 5, 7 = melhor de 7
    fun getSeriesSize(context: Context): Int {
        val size = prefs(context).getInt("series_size", 1)
        if (size == 0 || size == 5 || size == 7) {
            return size
        }
        return 1
    }

    // Modo de girar só uma vez: sem placar, mostra só o resultado do último giro
    fun isSingleMode(context: Context): Boolean {
        return getSeriesSize(context) == 1
    }

    fun saveSeriesSize(context: Context, size: Int) {
        // Mudou o modo: começa do zero, com o placar zerado
        if (size != getSeriesSize(context)) {
            resetScores(context)
        }
        prefs(context).edit().putInt("series_size", size).apply()
    }

    fun isFreeMode(context: Context): Boolean {
        return getSeriesSize(context) == 0
    }

    // Quantas vitórias um lado precisa para ganhar (melhor de 5 = 3, melhor de 7 = 4)
    fun winsNeeded(context: Context): Int {
        return getSeriesSize(context) / 2 + 1
    }

    // Nos modos "1 vez" e livre a série nunca termina (não há vencedor)
    fun isSeriesFinished(context: Context): Boolean {
        if (isFreeMode(context) || isSingleMode(context)) {
            return false
        }
        val needed = winsNeeded(context)
        return getScore(context, HEADS) >= needed || getScore(context, TAILS) >= needed
    }

    // Lado que ganhou a série, ou -1 se ainda não há vencedor
    fun winnerSide(context: Context): Int {
        if (!isSeriesFinished(context)) {
            return -1
        }
        if (getScore(context, HEADS) > getScore(context, TAILS)) {
            return HEADS
        }
        return TAILS
    }

    // ----- Placar e sequência -----

    // Quantas vezes esse lado já saiu
    fun getScore(context: Context, side: Int): Int {
        return prefs(context).getInt("score_$side", 0)
    }

    // O que saiu em cada giro, em ordem. Exemplo: "010" = cara, coroa, cara
    fun getHistory(context: Context): String {
        return prefs(context).getString("history", "") ?: ""
    }

    // "Jogar novamente": zera o placar e a sequência, mas mantém pergunta, textos e modo
    fun resetScores(context: Context) {
        prefs(context).edit()
            .putInt("score_0", 0)
            .putInt("score_1", 0)
            .putString("history", "")
            .apply()
    }

    // "Zerar tudo": apaga TUDO (placar, pergunta, textos, modo). Volta a ser só cara ou coroa.
    fun resetAll(context: Context) {
        prefs(context).edit().clear().apply()
    }

    // ----- Pergunta e textos de cada lado -----

    // Pergunta digitada pelo usuário (opcional). Fica até ele zerar.
    fun getQuestion(context: Context): String {
        return prefs(context).getString("question", "") ?: ""
    }

    fun saveQuestion(context: Context, text: String) {
        prefs(context).edit().putString("question", text.trim()).apply()
    }

    // Texto que o usuário digitou para cada lado (por exemplo, "Sim").
    // Vazio significa que ele não digitou nada.
    fun getLabel(context: Context, side: Int): String {
        return prefs(context).getString("label_$side", "") ?: ""
    }

    fun saveLabel(context: Context, side: Int, text: String) {
        prefs(context).edit().putString("label_$side", text.trim()).apply()
    }
}

// ----- Texto embaixo da moeda -----

// Os 5 textos fixos abaixo da moeda, sempre nos mesmos lugares (no app e no widget):
//   linha 1: a pergunta
//   placar em 3 colunas: [lado da cara]  [2 x 1]  [lado da coroa]
//   linha 3: o que saiu em cada giro (● = lado da cara, ○ = lado da coroa)
val BOARD_IDS = intArrayOf(
    R.id.line_1, R.id.score_left, R.id.score_center, R.id.score_right, R.id.line_3
)

// Devolve os 5 textos (alguns podem ficar vazios)
fun boardTexts(context: Context): List<String> {
    // Modo "1 vez": sem placar. Mostra a pergunta e, se o usuário digitou os textos,
    // o texto do lado que saiu (a moeda já mostra CARA ou COROA).
    if (CoinStore.isSingleMode(context)) {
        return listOf(
            CoinStore.getQuestion(context),
            "",
            CoinStore.getLabel(context, CoinStore.lastSide(context)),
            "",
            ""
        )
    }

    val headsScore = CoinStore.getScore(context, HEADS)
    val tailsScore = CoinStore.getScore(context, TAILS)

    // No modo livre mostramos só os últimos 5 giros; nas séries, todos
    var history = CoinStore.getHistory(context)
    if (CoinStore.isFreeMode(context)) {
        history = history.takeLast(5)
    }
    val dots = ArrayList<String>()
    for (letter in history) {
        dots.add(if (letter == '0') "●" else "○")
    }

    return listOf(
        CoinStore.getQuestion(context),
        "● " + sideLabel(context, HEADS),
        "$headsScore x $tailsScore",
        sideLabel(context, TAILS) + " ○",
        dots.joinToString(" ")
    )
}

// Os 5 textos antes do primeiro giro: só uma mensagem na primeira linha
fun startBoard(text: String): List<String> {
    return listOf(text, "", "", "", "")
}

// ----- Opções de modo (1 vez / Livre / Melhor de 5 / Melhor de 7) -----

// Marca no grupo de botões a opção do modo atual (o app e a janelinha usam os mesmos ids)
fun showSeriesSize(group: RadioGroup, size: Int) {
    if (size == 5) {
        group.check(R.id.series_5)
    } else if (size == 7) {
        group.check(R.id.series_7)
    } else if (size == 0) {
        group.check(R.id.series_free)
    } else {
        group.check(R.id.series_1)
    }
}

// Lê qual opção está marcada: 1 (só uma vez), 0 (livre), 5 ou 7
fun readSeriesSize(group: RadioGroup): Int {
    return when (group.checkedRadioButtonId) {
        R.id.series_free -> 0
        R.id.series_5 -> 5
        R.id.series_7 -> 7
        else -> 1
    }
}

// ----- Zerar tudo (com confirmação, para ninguém apagar sem querer) -----

// Pergunta "Zerar tudo?". Se o usuário confirmar, apaga tudo, atualiza o widget
// e executa o que foi pedido em afterReset (por exemplo, limpar os campos da tela).
fun askResetAll(activity: AppCompatActivity, afterReset: () -> Unit) {
    AlertDialog.Builder(activity)
        .setTitle("Zerar tudo?")
        .setMessage("Apaga o placar, a pergunta e os textos de cara e coroa, e volta a girar só 1 vez.")
        .setPositiveButton("Zerar") { _, _ ->
            CoinStore.resetAll(activity)
            CoinWidgetProvider.updateAll(activity)
            afterReset()
        }
        .setNegativeButton("Cancelar", null)
        .show()
}
