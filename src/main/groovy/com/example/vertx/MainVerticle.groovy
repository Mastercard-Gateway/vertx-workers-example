package com.example.vertx

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

class MainVerticle extends AbstractVerticle {
    def log = LoggerFactory.getLogger(this.class)

    @Override
    void start(Future<Void> future) {
        JsonObject config = config()
        int port = config.getInteger('http.port', 8085)
        log.info("Starting MainVerticle on port ${port}")

        def server = vertx.createHttpServer()
        def router = Router.router(vertx)
        router.route().handler(BodyHandler.create())

        def orderCoffeeRoute = router.post("/coffee")
                .produces("application/json")
                .handler(this.&processCoffeeOrder)

        server.requestHandler(router.&accept).listen(port)
    }

    private void processCoffeeOrder(RoutingContext routingContext) {
        def requestBody = routingContext.getBodyAsJson()
        log.info("Processing request... ${requestBody}")

        def eb = vertx.eventBus()

        def message = new JsonObject()
        def correlationId = UUID.randomUUID().toString()
        message.put('id', correlationId)
        message.put('customer', requestBody.customer)
        message.put('coffee', requestBody.coffee)
        message.put('size', requestBody.size)

        eb.send('process.coffee.order', message)

        routingContext.response()
                .setChunked(true)
                .putHeader("Content-Type", "application/json")
                .end(Json.encode(new JsonObject()
                .put("order", "ok")
                .put('id', correlationId)
        ))
    }
}
