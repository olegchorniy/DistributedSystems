package kpi.ipt.labs.distributed.vertx.client;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.consul.ConsulServiceImporter;
import io.vertx.servicediscovery.types.HttpEndpoint;
import kpi.ipt.labs.distributed.vertx.NamesConstants;

public class NamesLoggerStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamesLoggerStarter.class);
    private static final String HOST = "localhost";

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        //deployDefaultNamesLogger(vertx);
        deployNamesLoggerWithServiceDiscovery(vertx);
    }

    private static void deployDefaultNamesLogger(Vertx vertx) {
        deployNamesLogger(vertx, Future.succeededFuture(new NamesClient(HOST, vertx)));
    }

    private static void deployNamesLoggerWithServiceDiscovery(Vertx vertx) {
        Future<NamesClient> clientFuture = Future.future();
        deployNamesLogger(vertx, clientFuture);

        ServiceDiscovery serviceDiscovery = ServiceDiscovery.create(vertx);

        JsonObject configuration = new JsonObject()
                .put("host", "localhost")
                .put("port", 8500)
                .put("scan-period", 2000);

        serviceDiscovery.registerServiceImporter(
                new ConsulServiceImporter(),
                configuration,
                serviceImporterResult -> {

                    if (!serviceImporterResult.succeeded()) {
                        LOGGER.error("Service importer registration failed");

                        clientFuture.fail(serviceImporterResult.cause());
                        return;
                    }

                    findNamesService(serviceDiscovery, httpClientResult -> {
                        if (!httpClientResult.succeeded()) {
                            LOGGER.error("Names-service not found");

                            clientFuture.fail(httpClientResult.cause());
                            return;
                        }

                        clientFuture.complete(new NamesClient(httpClientResult.result(), vertx));
                    });
                }
        );
    }

    private static void findNamesService(ServiceDiscovery serviceDiscovery, Handler<AsyncResult<HttpClient>> handler) {
        JsonObject serviceFilter = new JsonObject()
                .put("name", NamesConstants.SERVICE_NAME);

        HttpEndpoint.getClient(serviceDiscovery, serviceFilter, handler);
    }

    private static void deployNamesLogger(Vertx vertx, Future<NamesClient> clientFuture) {
        clientFuture.setHandler(namesClient -> {
            if (namesClient.succeeded()) {
                populateInitialData(namesClient.result());

                vertx.deployVerticle(new NamesLogger(namesClient.result()));
            } else {
                LOGGER.error("Failed to start names logger", namesClient.cause());
            }
        });
    }

    private static void populateInitialData(NamesClient client) {
        for (int i = 0; i < 3; i++) {
            client.addName("Name #" + i, res -> System.out.println(res.succeeded()));
        }
    }
}
