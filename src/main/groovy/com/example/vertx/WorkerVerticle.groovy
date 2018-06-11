package com.example.vertx

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.EventBus
import io.vertx.core.logging.LoggerFactory

class WorkerVerticle extends AbstractVerticle {
    private final Random random = new Random()
    def log = LoggerFactory.getLogger(this.class)

    @Override
    void start(Future<Void> future) {
        EventBus eb = vertx.eventBus()
        eb.consumer('process.coffee.order', { message ->
            def body = message.body()
            log.info("Processing coffee order ${body['id']} for ${body['customer']}: ${body['coffee']}, ${body['size']}, instance:${this}")
            Thread.sleep(random.nextInt(10) * 1000)
            log.info("Order ${body['id']} complete! Calling ${body['customer']} instance:${this}")
        })
    }
}
