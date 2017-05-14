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

        vertx.setPeriodic(DELAY, id ->
                namesClient.getNames(namesFuture -> {
                    if (namesFuture.failed()) {
                        LOGGER.warn("Unable to retrieve names list");
                    } else {
                        JsonArray names = namesFuture.result();

                        LOGGER.info("Fetched names: {0}", names);
                    }
                }));

        for (int i = 0; i < 3; i++) {
            namesClient.addName("name #" + i, r -> System.out.println(r.succeeded()));
        }
    }

    @Override
    public void stop() throws Exception {
        this.namesClient.close();
    }

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new PeriodicNamesLogger());
    }
}
