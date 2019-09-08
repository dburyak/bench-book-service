package dburyak.benchmark.book.vertx

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Prototype
import io.vertx.core.Vertx
import io.vertx.ext.web.Router

@Factory
class RouterFactory {

    @Prototype
    Router router(Vertx vertx) {
        def router = Router.router(vertx)
        router
    }
}
