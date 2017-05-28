package kpi.ipt.labs.distributed.vertx.client;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.consul.ConsulServiceImporter;

public class ConsulDiscoveryNamesClient extends NamesClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsulDiscoveryNamesClient.class);

    public ConsulDiscoveryNamesClient(Vertx vertx) {
        super(vertx);
    }

    @Override
    protected Future<HttpClient> getHttpClient() {
        ServiceDiscovery serviceDiscovery = ServiceDiscovery.create(vertx);

        JsonObject consulConfiguration = new JsonObject()
                .put("host", "localhost")
                .put("port", 8500)
                .put("scan-period", 2000);

        Future<HttpClient> clientFuture = Future.future();

        serviceDiscovery.registerServiceImporter(
                new ConsulServiceImporter(),
                consulConfiguration,
                res -> {

                    if (!res.succeeded()) {
                        LOGGER.error("Service importer registration failed");

                        clientFuture.fail(res.cause());
                        return;
                    }

                    DiscoveryUtils.findNamesService(serviceDiscovery, clientFuture.completer());
                }
        );

        return clientFuture;
    }
}
