package kpi.ipt.labs.distributed.vertx.client;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.consul.ConsulServiceImporter;
import io.vertx.servicediscovery.types.HttpEndpoint;
import kpi.ipt.labs.distributed.vertx.NamesConstants;

public class PeriodicNamesLogger extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicNamesLogger.class);
    private static final String HOST = "localhost";
    private static final long DELAY = 3000L;

    private NamesClient namesClient;

    @Override
    public void start() throws Exception {
        Vertx vertx = getVertx();

        this.namesClient = new NamesClient(HOST, vertx);

        vertx.setPeriodic(DELAY, this::printNames);
    }

    private void printNames(long id) {
        this.namesClient.getNamesWithCircuitBreaker(namesFuture -> {
            if (namesFuture.failed()) {
                LOGGER.warn("Unable to retrieve names list");
            } else {
                JsonArray names = namesFuture.result();

                LOGGER.info("Fetched names: {0}", names);
            }
        });
    }

    @Override
    public void stop() throws Exception {
        this.namesClient.close();
    }

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        //vertx.deployVerticle(new PeriodicNamesLogger());

        ServiceDiscovery serviceDiscovery = ServiceDiscovery.create(vertx);

        serviceDiscovery.registerServiceImporter(
                new ConsulServiceImporter(),
                new JsonObject()
                        .put("host", "localhost")
                        .put("port", 8500)
                        .put("scan-period", 2000),
                result -> {
                    if (result.succeeded()) {
                        System.out.println("Import finished");
                        findNamesService(serviceDiscovery, client -> {

                            new NamesClient(client.result(), vertx)
                                    .getNames(
                                            names -> System.out.println(names.result())
                                    );
                        });
                    } else {
                        System.out.println("Import failed");
                    }
                }
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
        findNamesService(serviceDiscovery, result -> {
            if (result.succeeded()) {
                System.out.println("HttpClient created");
                System.out.println(result.result());
            } else {
                System.out.println("Failed to find service instance");
            }
        });
    }

    private static void findNamesService(ServiceDiscovery serviceDiscovery, Handler<AsyncResult<HttpClient>> handler) {
        HttpEndpoint.getClient(
                serviceDiscovery,
                new JsonObject().put("name", NamesConstants.SERVICE_NAME),
                handler
        );
    }
}
