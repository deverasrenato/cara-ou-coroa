package com.deverasrenato.caraoucoroa

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

// Desenha a moeda em uma imagem (Bitmap). O app e o widget usam o mesmo desenho.
object Coin {
    const val IMAGE_SIZE = 240       // largura e altura da imagem, em pixels
    const val FRAME_COUNT = 18       // quantas imagens tem a animação
    const val FRAME_DELAY_MS = 70L   // tempo entre as imagens (18 x 70 = cerca de 1,3 s)
    const val WINNER_DELAY_MS = 1200L // quanto tempo a moeda fica visível antes de aparecer o vencedor

    private const val RADIUS = 70f

    // A "tinta" usada para desenhar (criada uma vez só)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Monta UM quadro da animação.
    // startSide: lado que estava para cima antes de girar
    // result:    lado que vai ficar para cima no final
    // progress:  0.0 (começo) até 1.0 (fim)
    fun frame(startSide: Int, result: Int, progress: Float): Bitmap {
        // Se o resultado é diferente do lado inicial, precisa de meio giro a mais
        val halfTurns = 8 + (if (startSide == result) 0 else 1)

        // Começa rápido e vai desacelerando
        val eased = 1f - (1f - progress) * (1f - progress)
        val angle = PI * halfTurns * eased

        // O cosseno vai de 1 até -1: positivo mostra o lado inicial, negativo mostra o outro
        val spin = cos(angle).toFloat()
        val side = if (spin >= 0f) startSide else 1 - startSide

        // A moeda sobe e desce, como se fosse jogada para cima
        val lift = sin(PI * progress).toFloat() * 60f

        return draw(side, spin, lift)
    }

    // Moeda parada, de frente
    fun still(side: Int): Bitmap {
        return draw(side, 1f, 0f)
    }

    // spin: 1 = de frente, 0 = de lado (fina), -1 = de frente de novo
    // lift: quantos pixels a moeda está acima do chão
    private fun draw(side: Int, spin: Float, lift: Float): Bitmap {
        val bitmap = Bitmap.createBitmap(IMAGE_SIZE, IMAGE_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val centerX = IMAGE_SIZE / 2f
        val ground = IMAGE_SIZE - 40f
        val centerY = ground - RADIUS - lift

        // Sombra no chão (diminui quando a moeda sobe)
        paint.style = Paint.Style.FILL
        paint.color = 0x40000000
        val shadowWidth = 60f * (1f - lift / 160f)
        canvas.drawOval(centerX - shadowWidth, ground - 6f, centerX + shadowWidth, ground + 6f, paint)

        // Quando gira, a moeda parece um círculo achatado na vertical
        canvas.save()
        canvas.translate(centerX, centerY)
        canvas.scale(1f, max(abs(spin), 0.06f))

        // Borda
        paint.color = 0xFFB8860B.toInt()
        canvas.drawCircle(0f, 0f, RADIUS, paint)

        // Corpo dourado
        paint.color = 0xFFFFC83D.toInt()
        canvas.drawCircle(0f, 0f, RADIUS - 7f, paint)

        // Anel decorativo
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        paint.color = 0xFFE0A526.toInt()
        canvas.drawCircle(0f, 0f, RADIUS - 16f, paint)

        // Texto do lado (CARA ou COROA)
        paint.style = Paint.Style.FILL
        paint.color = 0xFF7A5200.toInt()
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 28f
        val textOffset = -(paint.ascent() + paint.descent()) / 2f // centraliza na vertical
        canvas.drawText(sideName(side), 0f, textOffset, paint)

        canvas.restore()
        return bitmap
    }
}
