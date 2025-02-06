package multiclock

import mu.KotlinLogging
import java.awt.event.MouseEvent
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Font
import java.awt.geom.Ellipse2D
import kotlin.math.hypot

private val logger = KotlinLogging.logger {}

class Circular(var radius: Double,
               var bgClr: Color = Color.CYAN,
               var hClr: Color = Color.RED,
               var mClr: Color = Color.ORANGE,
               var sClr: Color = Color.YELLOW,
               var tickWidth: Double = 4.0): Base(radius*2,
                                                  radius*2,
                                                  0.0, 0.0,
                                                  Ellipse2D.Double(0.0,
                                                                   0.0,
                                                                   radius*2,
                                                                   radius*2)) {

    private fun drawBackground(g: Graphics2D) {
        logger.debug { "drawBackground" }
        drawCircle(g, 0.0, 0.0, radius, Color.WHITE, Color.BLACK, 4.0)
        g.create().use { g2d ->
            for (i in 0 until 12) {
                g2d.rotate(Math.toRadians(30.0))
                drawRect(g2d, -tickWidth/2, radius*0.9, tickWidth, radius, Color.BLACK, Color.BLACK, 1.0)
            }
        }
        drawCircle(g, 0.0, 0.0, radius*0.9, bgClr, Color.BLACK, 4.0)
    }
    private fun drawAnalog(g: Graphics2D) {
        logger.debug { "drawAnalog" }
        val now = java.time.LocalDateTime.now()
        val seconds = now.second.toDouble()
        val minutes = now.minute + seconds/60.0
        val hours = now.hour % 12 + minutes/60.0
        drawPie(g, 0.0, 0.0, radius*0.89, -90.0, hours*30, hClr)
        drawPie(g, 0.0, 0.0, radius*0.75, -90.0, minutes*6, mClr)
        drawPie(g, 0.0, 0.0, radius*0.60, -90.0, seconds*6, sClr)
    }

    private fun drawCenter(g: Graphics2D) {
        logger.debug { "drawCenter" }
        val sqWidth = radius * 0.8
        val sqHeight = radius * 0.6
        drawRoundedRect(g, -sqWidth/2, -sqHeight/2, sqWidth, sqHeight, sqHeight/4)
        val timeSz = hypot(sqWidth, sqHeight) * 0.17
        val dateSz = hypot(sqWidth, sqHeight) * 0.15
        val timeFnt = Font("Arial", Font.PLAIN, timeSz.toInt())
        val dateFnt = Font("Arial", Font.PLAIN, dateSz.toInt())
        val now = java.time.LocalDateTime.now()
        val timeStr = now.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))
        val dateStr = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        g.create().use { g2d ->
            g2d.scale(1.0, -1.0)
            drawText(g2d, timeStr, 0.0, -radius/12.0, timeFnt, TextAlign.BOTTOM_CENTER)
            drawText(g2d, dateStr, 0.0, radius/12.0, dateFnt, TextAlign.TOP_CENTER)
        }
    }

    override fun paintClock(g: Graphics2D) {
        g.create().use { g2d ->
            g2d.translate(radius, radius)
            g2d.scale(1.0, -1.0)
            drawBackground(g2d)
            drawAnalog(g2d)
            drawCenter(g2d)
    //        drawAxis(g2d)
        }
    }

    override fun mouseAction(e: MouseEvent, action: Int) {
        when (action) {
            MouseEvent.MOUSE_DRAGGED -> {
                location = java.awt.Point(
                    location.x + e.x - radius.toInt(),
                    location.y + e.y - radius.toInt())
                repaint()
            }
        }
    }

    override fun canBeBigger(): Boolean {
        return radius < 500.0
    }

    override fun canBeSmaller(): Boolean {
        return radius > 100.0
    }

    override fun makeBigger() {
        radius *= 1.5
        clipShape = Ellipse2D.Double(0.0, 0.0, radius*2, radius*2)
        size = Dimension(radius.toInt()*2, radius.toInt()*2)
        repaint()
    }

    override fun makeSmaller() {
        radius *= 0.75
        clipShape = Ellipse2D.Double(0.0, 0.0, radius*2, radius*2)
        size = Dimension(radius.toInt()*2, radius.toInt()*2)
        repaint()
    }
}