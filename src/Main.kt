package multiclock

import kotlinx.coroutines.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main(): Unit = runBlocking {
    try {
        logger.info { "Starting MultiClock" }
        while (true) {
            var clk: Base? = null
            when (Base.nextClk) {
                0 -> clk = Circular(125.0*1.5)
                1 -> clk = Square(250.0)
                2 -> clk = Horizontal(250.0, 100.0)
            }
            if (clk != null) {
                clk.start()
                clk.dispose()
            }
        }
    } catch (e: Exception) {
        logger.error(e) { "Error in MultiClock" }
    }
}