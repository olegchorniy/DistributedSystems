package kpi.ipt.labs.distributed.vertx.client;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;

public class DefaultNamesClient extends NamesClient {

    private final String host;
    private final int port;

    public DefaultNamesClient(Vertx vertx, String host, int port) {
        super(vertx);
        this.host = host;
        this.port = port;
    }

    @Override
    protected Future<HttpClient> getHttpClient() {
        HttpClient httpClient = vertx.createHttpClient(defaultClientOptions(host, port));
        return Future.succeededFuture(httpClient);
    }

    private static HttpClientOptions defaultClientOptions(String defaultHost, int defaultPort) {
        return new HttpClientOptions()
                .setDefaultHost(defaultHost)
                .setDefaultPort(defaultPort);
    }
}
