package dburyak.benchmark.book

import groovy.util.logging.Slf4j
import io.vertx.config.ConfigRetriever
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

import java.time.Duration
import java.time.Instant

import static io.vertx.core.http.HttpMethod.GET

@Slf4j
class MainVerticle extends AbstractVerticle {
    private UUID verticleId
    private HttpServer server
    private Router router
    private def rnd = new Random()

    @Override
    void start(Future<Void> startFuture) {
        Future.<Map> future { f ->
            ConfigRetriever.create(vertx).getConfig(f)
        }.compose(cfg -> {
            Future.<HttpServer> future { f ->
                server = vertx.createHttpServer()
                setupRouter(cfg)
                server.requestHandler(router).listen(cfg.'http.port' as Integer, f)
            }
        }).setHandler { server ->
            if (server.succeeded()) {
                verticleId = UUID.randomUUID()
                def port = server.result().actualPort()
                log.debug 'main verticle started : verticleId = {}, port = {}', verticleId, port
            }
            startFuture.handle(server.map(null))
        }
    }

    @Override
    void stop(Future<Void> stopFuture) {
        Future.<Void> future { f ->
            server.close(f)
        }.setHandler { stopRes ->
            log.debug 'verticle stopped : {}', verticleId
            stopFuture.handle(stopRes)
        }
    }

    private setupRouter(Map cfg) {
        def minTime = Duration.ofMillis(cfg.'response.time.minMs' as Long)
        def maxTime = Duration.ofMillis(cfg.'response.time.maxMs' as Long)

        router = Router.router(vertx)

        router.route(GET, '/author/random').produces('application/json').handler(timedHandler(minTime, maxTime) {
            new JsonObject([author: 'Mark Twain'])
        })
    }

    private Handler<RoutingContext> timedHandler(Duration minTime, Duration maxTime, Closure handler) {
        return { ctx ->
            def start = Instant.now()
            def duration = Duration.ofMillis(rnd.nextDouble() * (maxTime - minTime).toMillis() as Long) + minTime
            def end = start + duration
            def result = handler.call()
            def timeLeft = Duration.between(Instant.now(), end)
            if (timeLeft.isPositive()) {
                vertx.setTimer(timeLeft.toMillis()) {
                    ctx.response().putHeader('content-type', 'application/json').end(result.encode())
                }
            } else {
                ctx.response().putHeader('content-type', 'application/json').end(result.encode())
            }
        }
    }
}
