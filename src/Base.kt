package multiclock

import javax.swing.JWindow
import kotlinx.coroutines.*
import mu.KotlinLogging
import java.awt.event.MouseEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseMotionAdapter
import java.awt.image.BufferedImage
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.Font
import java.awt.Image
import java.awt.Shape
import java.awt.font.TextLayout
import java.awt.image.ImageObserver
import java.time.Duration

private val logger = KotlinLogging.logger {}

abstract class Base(width: Double,
                    height: Double,
                    x: Double? = null,
                    y: Double? = null,
                    var clipShape: Shape? = null): ImageObserver, JWindow() {

    companion object {
        var nextClk = 0
    }

    open fun mouseAction(e: MouseEvent, action: Int) {
        //logger.info { "mouseAction" }
    }

    abstract fun canBeSmaller(): Boolean
    abstract fun canBeBigger(): Boolean

    abstract fun makeSmaller()
    abstract fun makeBigger()

    fun mkPopup(e: MouseEvent) {
        val popup = java.awt.PopupMenu()
        popup.add(java.awt.MenuItem("Circular")).addActionListener {
            nextClk = 0
        }
        popup.add(java.awt.MenuItem("Square")).addActionListener {
            nextClk = 1
        }
        popup.add(java.awt.MenuItem("Horizontal")).addActionListener {
            nextClk = 2
        }
        popup.add(java.awt.MenuItem("AlwaysOnTop" + (if(isAlwaysOnTop)" *" else ""))).addActionListener {
            if(isAlwaysOnTop) {
                isAlwaysOnTop = false
            } else {
                isAlwaysOnTop = true
            }
        }
        if(canBeSmaller()) {
            popup.add(java.awt.MenuItem("Make it Smaller")).addActionListener {
                makeSmaller()
            }
        }
        if(canBeBigger()) {
            popup.add(java.awt.MenuItem("Make it Bigger")).addActionListener {
                makeBigger()
            }
        }
        popup.add(java.awt.MenuItem("Exit")).addActionListener {
            System.exit(0)
        }
        add(popup)
        popup.show(this, e.x, e.y)
    }

    init {
        logger.info {"mkWindow"}
        layout = null
        location = java.awt.Point(x?.toInt()?:0, y?.toInt()?:0)
        isVisible = true
        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent) {
            //    logger.info {"mouseDragged"}
                mouseAction(e, MouseEvent.MOUSE_DRAGGED)
            }
        })
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
              //  logger.info {"mousePressed"}
                if (e.button == MouseEvent.BUTTON3) {
                    mkPopup(e)
                }
                else
                    mouseAction(e, MouseEvent.MOUSE_PRESSED)
            }

            override fun mouseClicked(e: MouseEvent?) {
                super.mouseClicked(e)
                //logger.info {"mouseClicked"}
                mouseAction(e!!, MouseEvent.MOUSE_CLICKED)
            }

            override fun mouseEntered(e: MouseEvent?) {
                super.mouseEntered(e)
                //logger.info {"mouseEntered"}
                mouseAction(e!!, MouseEvent.MOUSE_ENTERED)
            }

            override fun mouseExited(e: MouseEvent?) {
                super.mouseExited(e)
                //logger.info {"mouseExited"}
                mouseAction(e!!, MouseEvent.MOUSE_EXITED)
            }

            override fun mouseReleased(e: MouseEvent?) {
                super.mouseReleased(e)
//                logger.info {"mouseReleased"}
                mouseAction(e!!, MouseEvent.MOUSE_RELEASED)
            }
        })
        addComponentListener(object : java.awt.event.ComponentAdapter() {
            override fun componentResized(e: java.awt.event.ComponentEvent) {
  //              logger.info {"componentResized"}
                if (clipShape != null) {
                    shape = clipShape
                }
            }
        })
        size = java.awt.Dimension(width.toInt(), height.toInt())
    }

    private fun setPaintOpts(g2d: Graphics2D) {
//        logger.info {"setPaintOpts"}
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE)
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, 140)
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE)

    }

    abstract fun paintClock(g: Graphics2D)

    override fun paint(g: Graphics) {
        //logger.info { "paint" }
        val g2d = g as Graphics2D
        setPaintOpts(g2d)
        val buffer = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val bg: Graphics2D = buffer.createGraphics()
        setPaintOpts(bg)
        paintClock(bg)
        g2d.drawImage(buffer, 0, 0, this)
        bg.dispose()
    }

    override fun imageUpdate(img: Image?, infoflags: Int, x: Int, y: Int, width: Int, height: Int): Boolean {
        var flags = ""
        if (infoflags and ImageObserver.ERROR != 0) flags += "ERROR "
        if (infoflags and ImageObserver.ABORT != 0) flags += "ABORT "
        if (infoflags and ImageObserver.ALLBITS != 0) flags += "ALLBITS "
        if (infoflags and ImageObserver.FRAMEBITS != 0) flags += "FRAMEBITS "
        if (infoflags and ImageObserver.HEIGHT != 0) flags += "HEIGHT "
        if (infoflags and ImageObserver.PROPERTIES != 0) flags += "PROPERTIES "
        if (infoflags and ImageObserver.SOMEBITS != 0) flags += "SOMEBITS "
        if (infoflags and ImageObserver.WIDTH != 0) flags += "WIDTH "
        logger.info { "imageUpdate: $flags" }
        return true
    }

    suspend fun start() = coroutineScope {
        try {
            logger.info { "start" }
            Base.nextClk = -1
            launch {
                timer(Duration.ofMillis(1000)) {
                    try {
                        repaint()
                        if(Base.nextClk != -1) {
                            return@launch
                        }
                    } catch (e: Exception) {
                        logger.error(e) { "Error in start: $e" }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error in start2: $e" }
        }
    }

    fun drawCircle(g: Graphics2D,
                   x: Double, y: Double, radius: Double,
                   bg: Color = Color.WHITE, fg: Color = Color.BLACK,
                   width: Double = 1.0) {
        g.color = bg
        g.fillOval((x - radius).toInt(),
            (y - radius).toInt(),
            (radius * 2).toInt(),
            (radius * 2).toInt())
        g.color = fg
        g.stroke = java.awt.BasicStroke(width.toFloat())
        g.drawOval((x - radius).toInt(),
            (y - radius).toInt(),
            (radius * 2).toInt(),
            (radius * 2).toInt())
    }

    fun drawRect(g: Graphics2D,
                 x: Double, y: Double, width: Double, height: Double,
                 bg: Color = Color.WHITE, fg: Color = Color.BLACK,
                 strokeWidth: Double = 1.0) {
        g.color = bg
        g.fillRect(x.toInt(), y.toInt(), width.toInt(), height.toInt())
        g.color = fg
        g.stroke = java.awt.BasicStroke(strokeWidth.toFloat())
        g.drawRect(x.toInt(), y.toInt(), width.toInt(), height.toInt())
    }

    fun drawGrad(g: Graphics2D,
                 x: Double, y: Double, width: Double, height: Double,
                 colors: Array<Color>, fractions: FloatArray) {
        val grad = java.awt.LinearGradientPaint(
            x.toFloat(), y.toFloat(), (x+width).toFloat(), (y).toFloat(),
            fractions, colors)
        g.paint = grad
        g.fillRect((x+1).toInt(), (y+1).toInt(), (width-1).toInt(), (height-1).toInt())
    }

    fun draw3DRect(g: Graphics2D,
                   x: Double, y: Double, width: Double, height: Double,
                   raised: Boolean = true,
                   bg: Color = Color.WHITE, fg: Color = Color.BLACK,
                   strokeWidth: Double = 1.0) {
        g.color = bg
        g.fill3DRect(x.toInt(), y.toInt(), width.toInt(), height.toInt(), raised)
        g.color = fg
        g.stroke = java.awt.BasicStroke(strokeWidth.toFloat())
        g.draw3DRect(x.toInt(), y.toInt(), width.toInt(), height.toInt(), raised)
    }

    fun drawLine(g: Graphics2D,
                 x1: Double, y1: Double, x2: Double, y2: Double,
                 clr: Color = Color.BLACK,
                 strokeWidth: Double = 1.0) {
        g.color = clr
        g.stroke = java.awt.BasicStroke(strokeWidth.toFloat())
        g.drawLine(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
    }

    fun drawAxis(g: Graphics2D) {
        drawLine(g, -100.0, 0.0, 100.0, 0.0, Color.RED, 4.0)
        drawLine(g, 0.0, -100.0, 0.0, 100.0, Color.BLUE, 4.0)
    }

    fun drawRoundedRect(g: Graphics2D,
                         x: Double, y: Double, width: Double, height: Double, arcradius: Double,
                         bg: Color = Color.WHITE, fg: Color = Color.BLACK,
                         strokeWidth: Double = 1.0) {
        g.color = bg
        g.fillRoundRect(x.toInt(), y.toInt(), width.toInt(), height.toInt(), arcradius.toInt(), arcradius.toInt())
        g.color = fg
        g.stroke = java.awt.BasicStroke(strokeWidth.toFloat())
        g.drawRoundRect(x.toInt(), y.toInt(), width.toInt(), height.toInt(), arcradius.toInt(), arcradius.toInt())
    }

    enum class TextAlign {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        MID_LEFT, MID_CENTER, MID_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
    }

    fun drawText(g: Graphics2D,
                 text: String,
                 x: Double, y: Double,
                 font: Font,
                 align: TextAlign = TextAlign.BOTTOM_LEFT,
                 clr: Color = Color.BLACK) {
        g.color = clr
        g.font = font
        val slen = g.fontMetrics.stringWidth(text)
        val sheight = TextLayout(text, font, g.fontRenderContext).getOutline(null).bounds.height
        val x1 = when (align) {
            TextAlign.TOP_CENTER, TextAlign.MID_CENTER, TextAlign.BOTTOM_CENTER -> x - slen / 2
            TextAlign.TOP_RIGHT, TextAlign.MID_RIGHT, TextAlign.BOTTOM_RIGHT -> x - slen
            else -> x
        }
        val y1 = when (align) {
            TextAlign.MID_LEFT, TextAlign.MID_CENTER, TextAlign.MID_RIGHT -> y + sheight / 2
            TextAlign.BOTTOM_LEFT, TextAlign.BOTTOM_CENTER, TextAlign.BOTTOM_RIGHT -> y
            else -> y + sheight
        }
        g.drawString(text, x1.toInt(), y1.toInt())
    }

    fun drawPie(g: Graphics2D,
                x: Double, y: Double, radius: Double,
                startAngle: Double, arcAngle: Double,
                bg: Color = Color.WHITE, fg: Color = Color.BLACK,
                width: Double = 1.0) {
        g.color = bg
        g.fillArc((x - radius).toInt(),
            (y - radius).toInt(),
            (radius * 2).toInt(),
            (radius * 2).toInt(),
            startAngle.toInt(),
            arcAngle.toInt())
        g.color = fg
        g.stroke = java.awt.BasicStroke(width.toFloat())
        g.drawArc((x - radius).toInt(),
            (y - radius).toInt(),
            (radius * 2).toInt(),
            (radius * 2).toInt(),
            startAngle.toInt(),
            arcAngle.toInt())
    }
}