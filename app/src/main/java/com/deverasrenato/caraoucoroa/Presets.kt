package com.deverasrenato.caraoucoroa

import android.content.Context
import android.graphics.Typeface
import android.text.InputFilter
import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

// Um "layout" salvo: um conjunto pronto de modo + pergunta + textos de cara e coroa.
// Exemplo: nome "Delivery", modo 1 (Cara ou Coroa?), pergunta "Pedir delivery?", Sim / Não.
// mode: 1 = Cara ou Coroa?, 0 = livre, 5 = melhor de 5 (os mesmos valores de CoinStore.getSeriesSize)
data class Preset(
    val name: String,
    val mode: Int,
    val question: String,
    val heads: String,
    val tails: String
)

// Guarda até 5 layouts. Fica em um arquivo de preferências SEPARADO do jogo ("presets"),
// por isso o "Zerar tudo" não apaga os layouts salvos.
object PresetStore {
    const val MAX = 5 // quantidade de vagas
    const val NAME_MAX = 20 // letras do nome

    private fun prefs(context: Context) =
        context.getSharedPreferences("presets", Context.MODE_PRIVATE)

    private fun nameOfSlot(context: Context, slot: Int): String {
        return prefs(context).getString("p${slot}_name", "") ?: ""
    }

    // Os layouts salvos, na ordem das vagas (vaga vazia = nome vazio, fica de fora)
    fun getAll(context: Context): List<Preset> {
        val list = ArrayList<Preset>()
        val prefs = prefs(context)
        for (slot in 0 until MAX) {
            val name = nameOfSlot(context, slot)
            if (name.isNotEmpty()) {
                list.add(
                    Preset(
                        name,
                        prefs.getInt("p${slot}_mode", 1),
                        prefs.getString("p${slot}_question", "") ?: "",
                        prefs.getString("p${slot}_heads", "") ?: "",
                        prefs.getString("p${slot}_tails", "") ?: ""
                    )
                )
            }
        }
        return list
    }

    // Procura a vaga que tem esse nome (sem diferenciar maiúsculas). -1 se não achar.
    private fun slotWithName(context: Context, name: String): Int {
        for (slot in 0 until MAX) {
            if (nameOfSlot(context, slot).equals(name, ignoreCase = true)) {
                return slot
            }
        }
        return -1
    }

    // Já existe um layout com esse nome?
    fun exists(context: Context, name: String): Boolean {
        return slotWithName(context, name) >= 0
    }

    // Todas as vagas estão ocupadas?
    fun isFull(context: Context): Boolean {
        return getAll(context).size >= MAX
    }

    // Salva o layout. Se já existe um com o mesmo nome, ele é substituído;
    // senão usa a primeira vaga livre. Devolve false se não houver vaga.
    fun save(context: Context, preset: Preset): Boolean {
        var slot = slotWithName(context, preset.name)
        if (slot < 0) {
            for (i in 0 until MAX) {
                if (nameOfSlot(context, i).isEmpty()) {
                    slot = i
                    break
                }
            }
        }
        if (slot < 0) {
            return false
        }
        prefs(context).edit()
            .putString("p${slot}_name", preset.name)
            .putInt("p${slot}_mode", preset.mode)
            .putString("p${slot}_question", preset.question)
            .putString("p${slot}_heads", preset.heads)
            .putString("p${slot}_tails", preset.tails)
            .apply()
        return true
    }

    // Apaga o layout com esse nome (a vaga fica livre)
    fun delete(context: Context, name: String) {
        val slot = slotWithName(context, name)
        if (slot < 0) {
            return
        }
        prefs(context).edit()
            .remove("p${slot}_name")
            .remove("p${slot}_mode")
            .remove("p${slot}_question")
            .remove("p${slot}_heads")
            .remove("p${slot}_tails")
            .apply()
    }
}

// Escolher um layout: configura modo, pergunta e textos e começa um jogo novo,
// esperando o giro. O widget é atualizado junto.
fun applyPreset(context: Context, preset: Preset) {
    CoinStore.saveSeriesSize(context, preset.mode)
    CoinStore.saveQuestion(context, preset.question)
    CoinStore.saveLabel(context, HEADS, preset.heads)
    CoinStore.saveLabel(context, TAILS, preset.tails)
    // Se o modo já era o mesmo, saveSeriesSize não zera o jogo: por isso a chamada aqui
    CoinStore.startNewGame(context)
    CoinWidgetProvider.updateAll(context)
}

// Desenha a fileira de layouts (um "chip" para cada um) dentro do container.
// Tocar no chip aplica o layout e chama onApplied; segurar o dedo pergunta se quer apagar.
// Usada pelo app e pela janelinha do lápis.
fun fillPresetRow(activity: AppCompatActivity, container: LinearLayout, onApplied: () -> Unit) {
    container.removeAllViews()
    val density = activity.resources.displayMetrics.density
    val presets = PresetStore.getAll(activity)

    // Sem nenhum layout salvo: só uma dica
    if (presets.isEmpty()) {
        val hint = TextView(activity)
        hint.text = activity.getString(R.string.presets_empty)
        hint.setTextColor(activity.getColor(R.color.text_soft))
        hint.textSize = 13f
        container.addView(hint)
        return
    }

    for (preset in presets) {
        val chip = TextView(activity)
        chip.text = preset.name
        chip.setTextColor(activity.getColor(R.color.accent))
        chip.setTypeface(null, Typeface.BOLD)
        chip.textSize = 15f
        chip.maxLines = 1
        chip.setBackgroundResource(R.drawable.preset_chip)
        chip.setPadding((16 * density).toInt(), (10 * density).toInt(), (16 * density).toInt(), (10 * density).toInt())

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.marginEnd = (8 * density).toInt()
        chip.layoutParams = params

        chip.setOnClickListener {
            applyPreset(activity, preset)
            onApplied()
        }
        chip.setOnLongClickListener {
            askDeletePreset(activity, preset.name) {
                fillPresetRow(activity, container, onApplied)
            }
            true
        }
        container.addView(chip)
    }
}

// Pergunta "Apagar o layout X?" antes de apagar
fun askDeletePreset(activity: AppCompatActivity, name: String, afterDelete: () -> Unit) {
    AlertDialog.Builder(activity)
        .setTitle(activity.getString(R.string.delete_preset_title, name))
        .setPositiveButton("Apagar") { _, _ ->
            PresetStore.delete(activity, name)
            afterDelete()
        }
        .setNegativeButton("Cancelar", null)
        .show()
}

// Caixa "Salvar como layout": pede um nome e guarda o que está nos campos agora
// (modo, pergunta, textos). afterSave roda depois de salvar, para redesenhar a fileira.
fun askSavePreset(
    activity: AppCompatActivity,
    mode: Int,
    question: String,
    heads: String,
    tails: String,
    afterSave: () -> Unit
) {
    val density = activity.resources.displayMetrics.density

    // Campo de digitar o nome (sugestão: a própria pergunta)
    val input = EditText(activity)
    input.inputType = InputType.TYPE_CLASS_TEXT
    input.filters = arrayOf(InputFilter.LengthFilter(PresetStore.NAME_MAX))
    input.hint = activity.getString(R.string.preset_name_hint)
    input.setText(question.trim().take(PresetStore.NAME_MAX))
    input.setSelection(input.text.length)

    val box = FrameLayout(activity)
    val margin = (20 * density).toInt()
    box.setPadding(margin, (8 * density).toInt(), margin, 0)
    box.addView(input)

    AlertDialog.Builder(activity)
        .setTitle(R.string.save_preset_title)
        .setView(box)
        .setPositiveButton("Salvar") { _, _ ->
            val name = input.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(activity, R.string.preset_name_empty, Toast.LENGTH_SHORT).show()
            } else {
                val replacing = PresetStore.exists(activity, name)
                val saved = PresetStore.save(activity, Preset(name, mode, question.trim(), heads.trim(), tails.trim()))
                if (!saved) {
                    Toast.makeText(activity, R.string.preset_full, Toast.LENGTH_LONG).show()
                } else {
                    val message = if (replacing) R.string.preset_updated else R.string.preset_saved
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                    afterSave()
                }
            }
        }
        .setNegativeButton("Cancelar", null)
        .show()
}
