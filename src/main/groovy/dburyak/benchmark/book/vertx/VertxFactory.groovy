package dburyak.benchmark.book.vertx

import dburyak.benchmark.book.util.LoggingVertx
import groovy.util.logging.Slf4j
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions

import javax.inject.Singleton

import static java.util.concurrent.TimeUnit.MILLISECONDS

@Factory
@Slf4j
class VertxFactory {

    @Singleton
    @Bean(preDestroy = 'close')
    Vertx vertx() {
        def runtime = Runtime.runtime
        def numCpu = runtime.availableProcessors()
        def opts = new VertxOptions().tap {
            maxEventLoopExecuteTime = 500
            maxEventLoopExecuteTimeUnit = MILLISECONDS

            workerPoolSize = numCpu * 4
            maxWorkerExecuteTime = 5_000
            maxWorkerExecuteTimeUnit = MILLISECONDS
        }
        new LoggingVertx(delegate: Vertx.vertx(opts))
    }
}
