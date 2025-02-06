package multiclock

import mu.KotlinLogging
import java.awt.event.MouseEvent
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Font
import java.awt.geom.RoundRectangle2D

private val logger = KotlinLogging.logger {}

class Square(var sqSize: Double,
             var bgClr: Color = Color.GRAY,
             var hClr: Color = Color.RED,
             var mClr: Color = Color.ORANGE,
             var sClr: Color = Color.YELLOW): Base(sqSize,
                                                   sqSize,
                                                   0.0, 0.0,
                                                   RoundRectangle2D.Double(0.0,
                                                                           0.0,
                                                                           sqSize,
                                                                           sqSize,
                                                                           50.0, 50.0)) {

    val surround = 35.0
    val gap = 5.0
    val txtFnt = Font("Arial", Font.PLAIN, 20)
    val numFnt = Font("Arial", Font.PLAIN, 25)

    override fun paintClock(g: Graphics2D) {
        val graphSz = sqSize - 2*surround
        val barWidth = (sqSize - (2*surround + 4*gap)) / 3
        drawRoundedRect(g, 0.0, 0.0, sqSize, sqSize, 50.0)
        g.translate(surround, surround+graphSz-15)
        g.scale(1.0, -1.0)
        drawRect(g, 0.0, 0.0, graphSz, graphSz, bgClr, Color.BLACK, 1.0)
        val now = java.time.LocalDateTime.now()
        val seconds = now.second.toDouble() / 60.0 * graphSz
        val minutes = now.minute.toDouble() / 60.0 * graphSz
        val hours = now.hour.toDouble() / 24.0 * graphSz
        val secStr = now.second.toString().padStart(2, '0')
        val minStr = now.minute.toString().padStart(2, '0')
        val hrStr = now.hour.toString().padStart(2, '0')
        drawRect(g, gap, 0.0, barWidth, hours, hClr, Color.BLACK, 1.0)
        drawRect(g, gap*2+barWidth, 0.0, barWidth, minutes, mClr, Color.BLACK, 1.0)
        drawRect(g, gap*3+barWidth*2, 0.0, barWidth, seconds, sClr, Color.BLACK, 1.0)
        // tick marks
        drawRect(g, -15.0, graphSz*0.25, 15.0, 1.0, Color.GRAY, Color.GRAY, 1.0)
        drawRect(g, -15.0, graphSz*0.50, 15.0, 1.0, Color.GRAY, Color.GRAY, 1.0)
        drawRect(g, -15.0, graphSz*0.75, 15.0, 1.0, Color.GRAY, Color.GRAY, 1.0)
        drawRect(g, graphSz, graphSz*0.25, 15.0, 1.0, Color.GRAY, Color.GRAY, 1.0)
        drawRect(g, graphSz, graphSz*0.50, 15.0, 1.0, Color.GRAY, Color.GRAY, 1.0)
        drawRect(g, graphSz, graphSz*0.75, 15.0, 1.0, Color.GRAY, Color.GRAY, 1.0)
        // text
        g.scale(1.0, -1.0)
        drawText(g, "24", -5.0, -graphSz, txtFnt, TextAlign.TOP_RIGHT)
        drawText(g, "60", graphSz+5, -graphSz, txtFnt, TextAlign.TOP_LEFT)
        drawText(g, "0", -5.0, 0.0, txtFnt, TextAlign.BOTTOM_RIGHT)
        drawText(g, "0", graphSz+5, 0.0, txtFnt, TextAlign.BOTTOM_LEFT)
        drawText(g, hrStr, gap+barWidth/2, 10.0, numFnt, TextAlign.TOP_CENTER)
        drawText(g, minStr, gap*2+barWidth+barWidth/2, 10.0, numFnt, TextAlign.TOP_CENTER)
        drawText(g, secStr, gap*3+barWidth*2+barWidth/2, 10.0, numFnt, TextAlign.TOP_CENTER)
    }

    override fun mouseAction(e: MouseEvent, action: Int) {
        when(action) {
            MouseEvent.MOUSE_DRAGGED -> {
                location = java.awt.Point(
                    location.x + e.x - (width/2).toInt(),
                    location.y + e.y - (height/2).toInt())
                repaint()
            }
        }
    }

    override fun canBeBigger(): Boolean {
        return sqSize < 500.0
    }

    override fun canBeSmaller(): Boolean {
        return sqSize > 100.0
    }

    override fun makeBigger() {
        sqSize *= 1.5
        clipShape = RoundRectangle2D.Double(0.0, 0.0, sqSize, sqSize, 50.0, 50.0)
        size = Dimension(sqSize.toInt(), sqSize.toInt())
        repaint()
    }

    override fun makeSmaller() {
        sqSize *= 0.75
        clipShape = RoundRectangle2D.Double(0.0, 0.0, sqSize, sqSize, 50.0, 50.0)
        size = Dimension(sqSize.toInt(), sqSize.toInt())
        repaint()
    }
}