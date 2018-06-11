package com.example.vertx

import io.vertx.core.Vertx
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigStoreOptions
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.core.DeploymentOptions

class Application {

    public static void main(String[] args) {
        def log = LoggerFactory.getLogger(this.class)
        def vertx = Vertx.vertx()

        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setOptional(true)
                .setConfig(new JsonObject().put("path", "conf/config.json"))

        ConfigStoreOptions sysPropsStore = new ConfigStoreOptions().setType("sys")

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore)
                .addStore(sysPropsStore)

        ConfigRetriever retriever = ConfigRetriever.create(vertx, options)

        retriever.getConfig({ json ->
            if (json.succeeded()) {
                JsonObject config = json.result()
                log.debug("Starting the app with config: ${json}")
                vertx.deployVerticle('groovy:com.example.vertx.MainVerticle', new DeploymentOptions().setConfig(config))
                vertx.deployVerticle('groovy:com.example.vertx.WorkerVerticle', new DeploymentOptions()
                        .setConfig(config)
                        .setWorkerPoolName("coffee-making-pool")
                        .setWorkerPoolSize(5)
               //         .setMultiThreaded(true)
                        .setInstances(5)
                        .setWorker(true)
                )
            } else {
                log.error("Error retrieving configuration.")
            }
        })
    }
}
