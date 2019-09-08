package dburyak.benchmark.book.util

import groovy.util.logging.Slf4j
import io.vertx.core.Vertx

@Slf4j
class LoggingVertx implements Vertx {

    @Delegate
    Vertx delegate

    void close() {
        log.debug 'closing vertx'
        delegate.close {
            if (it.succeeded()) {
                log.info 'vertx closed'
            } else {
                log.error 'failed to close vertx', it.cause()
            }
        }
    }
}
