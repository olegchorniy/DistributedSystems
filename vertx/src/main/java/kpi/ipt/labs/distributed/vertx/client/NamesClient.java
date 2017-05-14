package kpi.ipt.labs.distributed.vertx.client;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import kpi.ipt.labs.distributed.vertx.NamesConstants;

import java.util.Collections;

public class NamesClient {

    private HttpClient client;

    public NamesClient(String defaultHost, Vertx vertx) {
        HttpClientOptions clientOptions = new HttpClientOptions()
                .setDefaultHost(defaultHost)
                .setDefaultPort(NamesConstants.SERVER_PORT);

        this.client = vertx.createHttpClient(clientOptions);
    }

    public void close() {
        client.close();
    }

    public void addName(String name, Handler<AsyncResult<Void>> handler) {
        client.put(NamesConstants.NAMES_ENDPOINT,
                response -> {
                    if (response.statusCode() == HttpResponseStatus.CREATED.code()) {
                        handler.handle(Future.succeededFuture());
                    } else {
                        handler.handle(Future.failedFuture(response.statusMessage()));
                    }
                })
                .exceptionHandler(exceptionHandler(handler))
                .end(Json.encode(Collections.singletonMap("name", name)));
    }

    public void getNames(Handler<AsyncResult<JsonArray>> handler) {
        Handler<Throwable> exceptionHandler = exceptionHandler(handler);

        client.get(NamesConstants.NAMES_ENDPOINT,
                response -> {
                    response.bodyHandler(body -> handler.handle(Future.succeededFuture(body.toJsonArray())));
                    response.exceptionHandler(exceptionHandler);
                })
                .exceptionHandler(exceptionHandler)
                .end();
    }

    private static <T> Handler<Throwable> exceptionHandler(Handler<AsyncResult<T>> handler) {
        return t -> handler.handle(Future.failedFuture(t));
    }
}
