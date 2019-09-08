package dburyak.benchmark.book

import groovy.util.logging.Slf4j
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Prototype
import io.micronaut.context.annotation.Requires
import io.vertx.config.ConfigRetriever
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router

import javax.inject.Inject

import static io.micronaut.http.MediaType.APPLICATION_JSON
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE
import static io.vertx.core.http.HttpMethod.GET

@Prototype
@Requires(beans = [HttpServer, Router])
@Slf4j
class HttpServerVerticle extends AbstractVerticle {
    @Inject
    private HttpServer server

    @Inject
    private Router router

    @Inject
    ApplicationContext applicationContext

    private UUID verticleId

    @Override
    void start(Future<Void> startFuture) {
        Future.<Map> future { f ->
            ConfigRetriever.create(vertx).getConfig(f)
        }.compose(cfg -> Future.<HttpServer> future { f ->
            setupRouter(cfg)
            server.requestHandler(router).listen((cfg['http.port'] ?: 8080) as Integer, f)
        }).setHandler { server ->
            if (server.succeeded()) {
                verticleId = UUID.randomUUID()
                def port = server.result().actualPort()
                log.debug 'http verticle started : verticleId = {}, port = {}', verticleId, port
            }
            startFuture.handle(server.map((Void) null))
        }
    }

    @Override
    void stop(Future<Void> stopFuture) {
        Future.<Void> future { f ->
            server.close(f)
        }.setHandler { stopRes ->
            log.debug 'http verticle stopped : {}', verticleId
            stopFuture.handle(stopRes)
        }
    }

    private setupRouter(Map cfg) {
        router.route(GET, '/author/random').produces(APPLICATION_JSON)
                .handler { routeCtx ->
                    def author = new JsonObject([author: 'Mark Twain'])
                    routeCtx.response().putHeader(CONTENT_TYPE, APPLICATION_JSON).end(author.encode())
                }
    }
}
