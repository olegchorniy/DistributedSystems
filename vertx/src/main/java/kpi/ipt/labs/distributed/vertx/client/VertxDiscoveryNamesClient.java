package kpi.ipt.labs.distributed.vertx.client;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.servicediscovery.ServiceDiscovery;

public class VertxDiscoveryNamesClient extends NamesClient {

    public VertxDiscoveryNamesClient(Vertx vertx) {
        super(vertx);
    }

    @Override
    protected Future<HttpClient> getHttpClient() {
        Future<HttpClient> clientFuture = Future.future();

        ServiceDiscovery serviceDiscovery = ServiceDiscovery.create(vertx);
        DiscoveryUtils.findNamesService(serviceDiscovery, clientFuture.completer());

        return clientFuture;
    }
}
