package multiclock

import java.time.Instant
import java.time.Duration
import kotlinx.coroutines.*
import java.awt.Graphics
import java.awt.Graphics2D

suspend inline fun timer(dur: Duration, pre: Boolean = false, block: () -> Unit) {
    timer(dur.toMillis(), pre, block)
}

suspend inline fun timer(ms: Long, pre: Boolean = false, block: () -> Unit) {
    if (pre)
        delay(ms)
    while (true) {
        val start = Instant.now()
        block()
        val stop = Instant.now()
        val diff = Duration.between(start, stop).toMillis()
        if (ms > diff)
            delay(ms - diff)
    }
}

inline fun Graphics.use(block: (Graphics2D) -> Unit) {
    try {
        block(this as Graphics2D)
    } catch (e: Throwable) {
        throw e
    } finally {
        dispose()
    }
}

