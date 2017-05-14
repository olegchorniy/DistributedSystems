package kpi.ipt.labs.distributed.vertx.client;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

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
        Vertx.vertx().deployVerticle(new PeriodicNamesLogger());
    }
}
