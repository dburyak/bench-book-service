package dburyak.benchmark.book

import groovy.util.logging.Slf4j
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Requires
import io.vertx.core.Vertx

import javax.annotation.PostConstruct
import javax.inject.Inject

@Context
@Requires(beans = [Vertx])
@Slf4j
class App {
    private final Set<String> deploymentIds = Collections.synchronizedSet(new LinkedHashSet<>())

    @Inject
    Vertx vertx

    @Inject
    ApplicationContext applicationContext

    Integer numHttpVerticles

    @PostConstruct
    void start() {
        def runtime = Runtime.runtime
        def numCpu = runtime.availableProcessors()
        def (memMax, memFree, memAlloc) = [runtime.maxMemory(), runtime.freeMemory(), runtime.totalMemory()]
        def memMaxMb = toMegabytes memMax
        def memFreeMb = toMegabytes memFree
        def memAllocMb = toMegabytes memAlloc
        def memUsedMb = toMegabytes(memAlloc - memFree)
        log.info 'starting application : numCpu={}, memMb(max:alloc:free:used)={}:{}:{}:{}',
                numCpu, memMaxMb, memAllocMb, memFreeMb, memUsedMb

        // configure vertx-web mode
        if (!System.getProperty('vertxweb.environment')
                && !System.getenv('VERTXWEB_ENVIRONMENT')) {

            // no vertx-web env is configured, should run in prod mode
            def mode = 'production'
            log.debug 'set vertx-web to implicit mode : {}', mode
            System.setProperty('vertxweb.environment', mode)
        }
        def mode = System.getProperty('vertxweb.environment') ?: System.getenv('VERTXWEB_ENVIRONMENT')
        log.info 'vertx-web mode : {}', mode

        numHttpVerticles = numCpu
        log.debug 'num http verticles instances : {}', numHttpVerticles
        numHttpVerticles.times {
            vertx.deployVerticle(applicationContext.getBean(HttpServerVerticle)) { id ->
                if (id.succeeded()) {
                    def deployId = id.result()
                    deploymentIds << deployId
                    log.info 'http verticle deployed : deployId = {}', deployId
                } else {
                    log.error 'http verticles deployment failed :', id.cause()
                }
            }
        }
    }

    void stop() {
        applicationContext.stop()
    }

    private static def toMegabytes(Long bytes) {
        (bytes / (1024 * 1024)).round().toLong()
    }
}
