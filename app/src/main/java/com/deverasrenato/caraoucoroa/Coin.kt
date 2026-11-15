package com.deverasrenato.caraoucoroa

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
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

    // Degradê dourado (grade de 200 x 200): claro no canto de cima à esquerda, escuro embaixo
    private val goldShader = RadialGradient(
        60f, 52f, 186f,
        intArrayOf(0xFFFFF0A8.toInt(), 0xFFFFD24F.toInt(), 0xFFF2AE12.toInt(), 0xFFC98500.toInt()),
        floatArrayOf(0f, 0.38f, 0.72f, 1f),
        Shader.TileMode.CLAMP
    )

    // Silhueta neutra de uma pessoa (cabeça e ombros), usada na CARA
    private val personPath = Path().apply {
        addCircle(100f, 70f, 27f, Path.Direction.CW)
        moveTo(48f, 152f)
        cubicTo(48f, 122f, 72f, 108f, 100f, 108f)
        cubicTo(128f, 108f, 152f, 122f, 152f, 152f)
        close()
    }

    // Silhueta simples de uma coroa (pontas e base), usada na COROA
    private val crownPath = Path().apply {
        moveTo(16f, 70f)
        lineTo(10f, 30f)
        lineTo(33f, 50f)
        lineTo(50f, 20f)
        lineTo(67f, 50f)
        lineTo(90f, 30f)
        lineTo(84f, 70f)
        close()
        addRoundRect(16f, 74f, 84f, 88f, 4f, 4f, Path.Direction.CW)
    }

    // Monta UM quadro da animação.
    // startSide: lado que estava para cima antes de girar
    // result:    lado que vai ficar para cima no final
    // progress:  0.0 (começo) até 1.0 (fim)
    // baseTurns: quantos meios giros dar (8 na jogada normal; a abertura usa menos, para girar devagar)
    fun frame(startSide: Int, result: Int, progress: Float, baseTurns: Int = 8): Bitmap {
        // Se o resultado é diferente do lado inicial, precisa de meio giro a mais
        val halfTurns = baseTurns + (if (startSide == result) 0 else 1)

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

        // O desenho da moeda foi feito numa grade de 200 x 200 (centro em 100,100, raio 98).
        // Aqui diminuímos para o tamanho real e movemos o centro da grade para o (0,0).
        canvas.scale(RADIUS / 98f, RADIUS / 98f)
        canvas.translate(-100f, -100f)

        // Corpo dourado com degradê (mais claro em cima, à esquerda)
        paint.style = Paint.Style.FILL
        paint.color = 0xFF000000.toInt() // a cor precisa estar opaca, senão o degradê fica transparente
        paint.shader = goldShader
        canvas.drawCircle(100f, 100f, 98f, paint)
        paint.shader = null

        // Aro claro na borda e anel mais escuro por dentro
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        paint.color = 0xB3FFF0A8.toInt()
        canvas.drawCircle(100f, 100f, 97f, paint)
        paint.strokeWidth = 3.5f
        paint.color = 0x4D8C5A00
        canvas.drawCircle(100f, 100f, 86f, paint)

        // Desenho em relevo baixo: pessoa (cara) ou coroa (coroa), sem texto
        paint.style = Paint.Style.FILL
        if (side == HEADS) {
            drawRelief(canvas, personPath, 1.05f, 98f, 100f)
        } else {
            drawRelief(canvas, crownPath, 1.7f, 54f, 50f)
        }

        canvas.restore()
        return bitmap
    }

    // Desenha a silhueta 3 vezes: sombra (um pouco para cima/esquerda),
    // luz (um pouco para baixo/direita) e a cor principal por cima. Isso cria o "relevo".
    // zoom, baseY e baseX dizem onde a silhueta fica dentro da moeda.
    private fun drawRelief(canvas: Canvas, shape: Path, zoom: Float, baseY: Float, baseX: Float) {
        paint.color = 0x807A4B00.toInt()
        drawShape(canvas, shape, zoom, baseY, baseX, -1.6f)
        paint.color = 0xCCFFF6C2.toInt()
        drawShape(canvas, shape, zoom, baseY, baseX, 1.6f)
        paint.color = 0xFFF3B51C.toInt()
        drawShape(canvas, shape, zoom, baseY, baseX, 0f)
    }

    private fun drawShape(canvas: Canvas, shape: Path, zoom: Float, baseY: Float, baseX: Float, shift: Float) {
        canvas.save()
        canvas.translate(shift, shift)
        canvas.translate(100f, 100f)
        canvas.scale(zoom, zoom)
        canvas.translate(-baseX, -baseY)
        canvas.drawPath(shape, paint)
        canvas.restore()
    }
}
