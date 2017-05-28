package kpi.ipt.labs.distributed.vertx.client;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;

import static kpi.ipt.labs.distributed.vertx.NamesConstants.SERVICE_NAME;

public abstract class DiscoveryUtils {

    private DiscoveryUtils() {
    }

    public static void findNamesService(ServiceDiscovery serviceDiscovery, Handler<AsyncResult<HttpClient>> handler) {
        JsonObject serviceFilter = new JsonObject()
                .put("name", SERVICE_NAME);

        HttpEndpoint.getClient(serviceDiscovery, serviceFilter, handler);
    }
}
