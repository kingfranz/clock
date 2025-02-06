package multiclock

import mu.KotlinLogging
import java.awt.event.MouseEvent
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Font
import java.awt.geom.RoundRectangle2D

private val logger = KotlinLogging.logger {}

class Horizontal(var winWidth: Double, var winHeight: Double): Base(winWidth, winHeight) {

    val barXofs = 20.0
    val barYofs = 20.0
    val txtFnt = Font("Arial", Font.PLAIN, 15)
    val numFnt = Font("Arial", Font.BOLD, 45)
    var moseHover = false

    override fun paintClock(g: Graphics2D) {
        val barWidth = winWidth - 2*barXofs
        val barHeight = winHeight - 2*barYofs - 15.0
        draw3DRect(g, 0.0, 0.0, winWidth, winHeight, true)
        val now = java.time.LocalDateTime.now()
        val barValue = (now.second.toDouble() + now.minute.toDouble()*60.0 + now.hour.toDouble()*3600.0) / 86400.0 * barWidth
        drawRect(g, barXofs, barYofs+15, barWidth, barHeight)
        drawGrad(g, barXofs, barYofs+15, barValue, barHeight, arrayOf(Color.DARK_GRAY, Color.LIGHT_GRAY),
            floatArrayOf(0.0f, 0.5f))
        drawText(g, "0", barXofs, barYofs+10, txtFnt)
        drawText(g, "6", barXofs+barWidth*0.25, barYofs+10, txtFnt, TextAlign.BOTTOM_CENTER)
        drawText(g, "12", barXofs+barWidth*0.50, barYofs+10, txtFnt, TextAlign.BOTTOM_CENTER)
        drawText(g, "18", barXofs+barWidth*0.75, barYofs+10, txtFnt, TextAlign.BOTTOM_CENTER)
        drawText(g, "24", barXofs+barWidth, barYofs+10, txtFnt, TextAlign.BOTTOM_RIGHT)
        if (moseHover) {
            drawText(g, "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}:${now.second.toString().padStart(2, '0')}", barXofs+barWidth/2, barYofs+barHeight/2, numFnt, TextAlign.TOP_CENTER)
        }
    }

    override fun mouseAction(e: MouseEvent, action: Int) {
        when(action) {
            MouseEvent.MOUSE_ENTERED -> moseHover = true
            MouseEvent.MOUSE_EXITED -> moseHover = false
            MouseEvent.MOUSE_DRAGGED -> {
                location = java.awt.Point(
                    location.x + e.x - (winWidth/2).toInt(),
                    location.y + e.y - (winHeight/2).toInt())
                repaint()
            }
        }
    }

    override fun canBeBigger(): Boolean {
        return winWidth < 500.0
    }

    override fun canBeSmaller(): Boolean {
        return winWidth > 100.0
    }

    override fun makeBigger() {
        winWidth *= 1.5
        winHeight *= 1.5
        size = Dimension(winWidth.toInt(), winHeight.toInt())
        repaint()
    }

    override fun makeSmaller() {
        winWidth *= 0.75
        winHeight *= 0.75
        size = Dimension(winWidth.toInt(), winHeight.toInt())
        repaint()
    }
}