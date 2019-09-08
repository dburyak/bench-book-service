package dburyak.benchmark.book.vertx

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Prototype
import io.micronaut.context.annotation.Requires
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions

@Factory
@Requires(beans = [Vertx])
class HttpServerFactory {

    @Prototype
    @Bean(preDestroy = 'close')
    HttpServer httpServer(Vertx vertx, HttpServerOptions httpServerOptions) {
        vertx.createHttpServer(httpServerOptions)
    }
}
