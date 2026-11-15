package com.deverasrenato.caraoucoroa

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
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

// Em que ponto está o jogo atual (veja CoinStore.gameState):
//   READY    = pronto: ainda não girou nada neste jogo
//   PLAYING  = jogando: já girou, mas a série não acabou
//   FINISHED = terminado: um lado venceu (nunca acontece no modo livre)
enum class GameState { READY, PLAYING, FINISHED }

// Guarda tudo o que o app precisa lembrar (o app e o widget leem daqui)
object CoinStore {
    private fun prefs(context: Context) =
        context.getSharedPreferences("coin", Context.MODE_PRIVATE)

    // ----- Estado do jogo -----
    //
    // REGRAS DE INÍCIO E FIM (a ideia é ter um lugar só que manda no jogo):
    //  - CONFIGURAÇÃO: modo, pergunta, textos e vibração. Só muda quando o usuário edita
    //    ou usa "Zerar tudo".
    //  - JOGO: placar, sequência e último lado. Começa do zero SEMPRE por startNewGame:
    //    ao salvar na janelinha, ao trocar o modo, ao jogar novamente (ou girar depois do
    //    fim), e ao sair do app no modo livre.
    //  - O jogo termina quando um lado chega à maioria (melhor de 5, ou 1 giro).
    //  - "Zerar tudo" (resetAll) apaga a configuração e o jogo.

    fun gameState(context: Context): GameState {
        if (getScore(context, HEADS) + getScore(context, TAILS) == 0) {
            return GameState.READY
        }
        if (isSeriesFinished(context)) {
            return GameState.FINISHED
        }
        return GameState.PLAYING
    }

    // ----- Último resultado -----

    // Lado que está para cima (CARA se o jogo ainda não começou)
    fun lastSide(context: Context): Int {
        return max(0, prefs(context).getInt("side", -1))
    }

    // Guarda o resultado de um giro e atualiza o placar e a sequência
    fun saveResult(context: Context, side: Int) {
        // Por segurança: se a série já tinha terminado, começa uma nova
        if (gameState(context) == GameState.FINISHED) {
            startNewGame(context)
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
    // 5 = melhor de 5 (o antigo "melhor de 7" foi removido; um 7 guardado vira o padrão)
    fun getSeriesSize(context: Context): Int {
        val size = prefs(context).getInt("series_size", 1)
        if (size == 0 || size == 5) {
            return size
        }
        return 1
    }

    // Modo "Cara ou Coroa?": um giro só, depois pergunta "Jogar novamente?"
    fun isSingleMode(context: Context): Boolean {
        return getSeriesSize(context) == 1
    }

    fun saveSeriesSize(context: Context, size: Int) {
        // Mudou o modo: começa um jogo novo
        if (size != getSeriesSize(context)) {
            startNewGame(context)
        }
        prefs(context).edit().putInt("series_size", size).apply()
    }

    fun isFreeMode(context: Context): Boolean {
        return getSeriesSize(context) == 0
    }

    // Quantas vitórias um lado precisa para ganhar (melhor de 5 = 3)
    fun winsNeeded(context: Context): Int {
        return getSeriesSize(context) / 2 + 1
    }

    // No modo livre a série nunca termina. No modo "Cara ou Coroa?" (1 giro) ela termina
    // no primeiro giro, porque winsNeeded é 1.
    fun isSeriesFinished(context: Context): Boolean {
        if (isFreeMode(context)) {
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

    // Zera o placar e a sequência (só startNewGame usa)
    private fun clearScores(context: Context) {
        prefs(context).edit()
            .putInt("score_0", 0)
            .putInt("score_1", 0)
            .putString("history", "")
            .apply()
    }

    // ÚNICA forma de começar um jogo novo: placar, sequência e último resultado zerados,
    // mantendo modo, pergunta e textos. Depois dela o estado é READY.
    fun startNewGame(context: Context) {
        clearScores(context)
        prefs(context).edit().remove("side").apply()
    }

    // "Zerar tudo": apaga TUDO (placar, pergunta, textos, modo). Volta a ser só cara ou coroa.
    // (a escolha de vibrar fica: é uma preferência do usuário, não parte do jogo)
    fun resetAll(context: Context) {
        val vibration = isVibrationOn(context)
        val seen = isWelcomeSeen(context)
        val always = isWelcomeAlways(context)
        prefs(context).edit().clear()
            .putBoolean("vibrate", vibration)
            .putBoolean("welcome_seen", seen)
            .putBoolean("welcome_always", always)
            .apply()
    }

    // ----- Boas-vindas (a tela que ensina a adicionar o widget) -----

    // O usuário já viu as boas-vindas alguma vez?
    fun isWelcomeSeen(context: Context): Boolean {
        return prefs(context).getBoolean("welcome_seen", false)
    }

    fun markWelcomeSeen(context: Context) {
        prefs(context).edit().putBoolean("welcome_seen", true).apply()
    }

    // O usuário pediu para ver as boas-vindas toda vez que abrir o app?
    fun isWelcomeAlways(context: Context): Boolean {
        return prefs(context).getBoolean("welcome_always", false)
    }

    fun saveWelcomeAlways(context: Context, always: Boolean) {
        prefs(context).edit().putBoolean("welcome_always", always).apply()
    }

    // Mostrar as boas-vindas agora? Sim na primeira vez, ou sempre se o usuário pediu
    fun isWelcomeDue(context: Context): Boolean {
        return isWelcomeAlways(context) || !isWelcomeSeen(context)
    }

    // ----- Vibração -----

    // Ligada por padrão
    fun isVibrationOn(context: Context): Boolean {
        return prefs(context).getBoolean("vibrate", true)
    }

    fun saveVibration(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean("vibrate", on).apply()
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

// Já saiu algum giro neste jogo? (some quando o usuário joga novamente)
fun hasPlayed(context: Context): Boolean {
    return CoinStore.gameState(context) != GameState.READY
}

// Nome do lado que saiu, escrito embaixo da moeda ("CARA" ou "COROA"); vazio antes do giro
fun resultText(context: Context): String {
    return if (hasPlayed(context)) sideName(CoinStore.lastSide(context)) else ""
}

// No modo de 1 giro não há placar: a linha do meio só aparece se o usuário digitou
// o texto do lado que saiu (por exemplo, "Sim")
fun showScoreRow(context: Context): Boolean {
    if (!CoinStore.isSingleMode(context)) {
        return true
    }
    return hasPlayed(context) && CoinStore.getLabel(context, CoinStore.lastSide(context)).isNotEmpty()
}

// Os 5 textos de agora: se o jogo está pronto, o convite (hint); senão, o placar
fun currentBoard(context: Context, hint: String): List<String> {
    if (CoinStore.gameState(context) == GameState.READY) {
        return readyBoard(context, hint)
    }
    return boardTexts(context, hint)
}

// Devolve os 5 textos de um jogo em andamento ou terminado (alguns podem ficar vazios)
fun boardTexts(context: Context, hint: String): List<String> {
    // Modo "1 vez": sem placar. Mostra a pergunta e, se o usuário digitou os textos,
    // o texto do lado que saiu (o nome CARA ou COROA aparece embaixo da moeda).
    if (CoinStore.isSingleMode(context)) {
        // Como no início: a pergunta (ou "Cara ou Coroa?") em cima e o convite embaixo.
        // O texto digitado para o lado que saiu (se houver) fica no meio.
        val question = CoinStore.getQuestion(context).ifEmpty { "Cara ou Coroa?" }
        val label = CoinStore.getLabel(context, CoinStore.lastSide(context))
        return listOf(question, "", label, "", hint)
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
        sideLabel(context, HEADS),
        // No meio, duas linhas: as bolinhas (● cara, ○ coroa) em cima dos números do placar
        "●   ○\n$headsScore x $tailsScore",
        sideLabel(context, TAILS),
        dots.joinToString(" ")
    )
}

// Os 5 textos antes do primeiro giro: só uma mensagem na primeira linha
fun startBoard(text: String): List<String> {
    return listOf(text, "", "", "", "")
}

// Os 5 textos quando o jogo está pronto, esperando o giro: se o usuário digitou uma
// pergunta, ela aparece em cima e o convite ("Toque para girar") na última linha
fun readyBoard(context: Context, hint: String): List<String> {
    val question = CoinStore.getQuestion(context)
    if (question.isEmpty()) {
        // No modo de 1 giro, sem pergunta, o nome do modo ocupa o lugar da pergunta
        if (CoinStore.isSingleMode(context)) {
            return listOf("Cara ou Coroa?", "", "", "", hint)
        }
        return startBoard(hint)
    }
    return listOf(question, "", "", "", hint)
}

// ----- Vibração (o usuário pode desligar no app) -----

// Vibra por alguns milissegundos, se o usuário não tiver desligado a vibração
fun vibrate(context: Context, millis: Long) {
    if (!CoinStore.isVibrationOn(context)) {
        return
    }
    val vibrator = context.getSystemService(Vibrator::class.java) ?: return
    vibrator.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
}

// ----- Opções de modo (1 vez / Livre / Melhor de 5) -----

// Marca no grupo de botões a opção do modo atual (o app e a janelinha usam os mesmos ids)
fun showSeriesSize(group: RadioGroup, size: Int) {
    if (size == 5) {
        group.check(R.id.series_5)
    } else if (size == 0) {
        group.check(R.id.series_free)
    } else {
        group.check(R.id.series_1)
    }
}

// Lê qual opção está marcada: 1 (só uma vez), 0 (livre) ou 5
fun readSeriesSize(group: RadioGroup): Int {
    return when (group.checkedRadioButtonId) {
        R.id.series_free -> 0
        R.id.series_5 -> 5
        else -> 1
    }
}

// ----- Zerar tudo (com confirmação, para ninguém apagar sem querer) -----

// Pergunta "Zerar tudo?". Se o usuário confirmar, apaga tudo, atualiza o widget
// e executa o que foi pedido em afterReset (por exemplo, limpar os campos da tela).
// afterClose (opcional) roda quando a caixa fecha, seja qual for a resposta.
fun askResetAll(
    activity: AppCompatActivity,
    afterReset: () -> Unit,
    afterClose: () -> Unit = {}
) {
    AlertDialog.Builder(activity)
        .setTitle("Zerar tudo?")
        .setMessage("Apaga o placar, a pergunta e os textos de cara e coroa, e volta ao modo \"Cara ou Coroa?\".")
        .setPositiveButton("Zerar") { _, _ ->
            CoinStore.resetAll(activity)
            CoinWidgetProvider.updateAll(activity)
            afterReset()
        }
        .setNegativeButton("Cancelar", null)
        .setOnDismissListener { afterClose() }
        .show()
}
