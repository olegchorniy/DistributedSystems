package kpi.ipt.labs.distributed.vertx.logger;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import kpi.ipt.labs.distributed.vertx.client.ConsulDiscoveryNamesClient;
import kpi.ipt.labs.distributed.vertx.client.DefaultNamesClient;
import kpi.ipt.labs.distributed.vertx.client.VertxDiscoveryNamesClient;
import kpi.ipt.labs.distributed.vertx.server.NamesServer;

import static kpi.ipt.labs.distributed.vertx.NamesConstants.SERVER_PORT;

public class NamesLoggerStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamesLoggerStarter.class);
    private static final String HOST = "localhost";

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        //deployDefaultNamesLogger(vertx);
        //deployConsulDiscoveryNamesLogger(vertx);
        deployServerAndClientWithDefaultDiscovery(vertx);
    }

    private static void deployDefaultNamesLogger(Vertx vertx) {
        vertx.deployVerticle(new NamesLogger(new DefaultNamesClient(vertx, HOST, SERVER_PORT)));
    }

    private static void deployConsulDiscoveryNamesLogger(Vertx vertx) {
        vertx.deployVerticle(new NamesLogger(new ConsulDiscoveryNamesClient(vertx)));
    }

    private static void deployServerAndClientWithDefaultDiscovery(Vertx vertx) {
        vertx.deployVerticle(new NamesServer(), deploy -> {
            if (deploy.succeeded()) {
                vertx.deployVerticle(new NamesLogger(new VertxDiscoveryNamesClient(vertx)));
            } else {
                LOGGER.error("Server deploy failed");
            }
        });
    }
}
