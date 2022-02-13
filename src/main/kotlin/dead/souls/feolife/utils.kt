package dead.souls.feolife

import mu.KLogger
import mu.KotlinLogging

inline fun <reified T : Any> T.logger(): Lazy<KLogger> = lazy { KotlinLogging.logger { T::class.java } }
