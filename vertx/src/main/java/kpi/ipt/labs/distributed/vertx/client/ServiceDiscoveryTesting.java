package kpi.ipt.labs.distributed.vertx.client;

import io.vertx.core.AsyncResult;
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


public class ServiceDiscoveryTesting {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamesLogger.class);

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();

        ServiceDiscovery serviceDiscovery = ServiceDiscovery.create(vertx);

        JsonObject configuration = new JsonObject()
                .put("host", "localhost")
                .put("port", 8500)
                .put("scan-period", 2000);

        serviceDiscovery.registerServiceImporter(
                new ConsulServiceImporter(),

                configuration,

                result -> System.out.println(result.succeeded()
                        ? "Import finished"
                        : "Import failed"
                )
        );
    }

    private static void exploreRecords(ServiceDiscovery serviceDiscovery) {
        serviceDiscovery.getRecords($ -> true, records -> {
            records.result().forEach(record -> {
                System.out.printf("Name = %s, location = %s, type = %s, status = %s%n",
                        record.getName(), record.getLocation(), record.getType(), record.getStatus());
            });
        });
    }

    private static void checkNamesService(ServiceDiscovery serviceDiscovery) {
        findNamesService(
                serviceDiscovery,
                result -> System.out.println(result.succeeded()
                        ? "Names-service found"
                        : "Failed to find service instance")
        );
    }

    private static void findNamesService(ServiceDiscovery serviceDiscovery, Handler<AsyncResult<HttpClient>> handler) {
        HttpEndpoint.getClient(
                serviceDiscovery,
                new JsonObject().put("name", NamesConstants.SERVICE_NAME),
                handler
        );
    }
}

