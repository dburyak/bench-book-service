package dburyak.benchmark.book.vertx


import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Prototype
import io.vertx.core.http.HttpServerOptions

@Factory
class HttpServerOptionsFactory {

    @Prototype
    HttpServerOptions httpServerOptions() {
        new HttpServerOptions()
    }
}
