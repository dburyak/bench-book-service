package dburyak.benchmark.book


import groovy.util.logging.Slf4j
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions

import static java.util.concurrent.TimeUnit.MILLISECONDS
import static java.util.concurrent.TimeUnit.SECONDS

@Slf4j
class App {
    static void main(String[] args) {
        log.info 'starting application'
        def numCpu = Runtime.getRuntime().availableProcessors()

        if (!System.properties.contains('vertxweb.environment')
                && !System.getenv().containsKey('VERTXWEB_ENVIRONMENT')) {

            // no vertx-web env is configured, should run in dev mode
            log.debug 'set vertx-web to implicit mode : development'
            System.setProperty('vertxweb.environment', 'development')
        }
        def mode = System.getProperty('vertxweb.environment') ?: System.getenv('VERTXWEB_ENVIRONMENT')
        log.info 'vertx-web mode : {}', mode

        def vertx = Vertx.vertx new VertxOptions().tap {
            maxEventLoopExecuteTime = 500
            maxEventLoopExecuteTimeUnit = MILLISECONDS

            workerPoolSize = numCpu * 2
            maxWorkerExecuteTime = 4
            maxWorkerExecuteTimeUnit = SECONDS
        }

        log.debug 'num main verticles instances : {}', numCpu
        vertx.deployVerticle MainVerticle, new DeploymentOptions().tap {
            instances = numCpu
        }, { id ->
            if (id.succeeded()) {
                log.info 'main verticles deployed : deployId = {}, numVerticles = {}', id.result(), numCpu
            } else {
                log.error 'main verticles deployment failed :', id.cause()
            }
        }
    }
}
