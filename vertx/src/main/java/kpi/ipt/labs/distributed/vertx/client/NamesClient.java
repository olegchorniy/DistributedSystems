package kpi.ipt.labs.distributed.vertx.client;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import kpi.ipt.labs.distributed.vertx.NamesConstants;

import java.util.Collections;

public class NamesClient {

    private HttpClient client;
    private CircuitBreaker circuitBreaker;

    public NamesClient(String defaultHost, Vertx vertx) {
        HttpClientOptions clientOptions = new HttpClientOptions()
                .setDefaultHost(defaultHost)
                .setDefaultPort(NamesConstants.SERVER_PORT);

        this.client = vertx.createHttpClient(clientOptions);

        this.circuitBreaker = CircuitBreaker.create("get-names-circuit-breaker", vertx,
                new CircuitBreakerOptions()
                        .setMaxFailures(5)
                        .setTimeout(1000)
                        .setFallbackOnFailure(true)
                        .setResetTimeout(10000)
        );
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

    public void getNamesWithCircuitBreaker(Handler<AsyncResult<JsonArray>> handler) {
        Handler<Future<JsonArray>> command = cmdFuture -> {

            Handler<HttpClientResponse> responseHandler = response -> {
                response.exceptionHandler(cmdFuture::fail);
                response.bodyHandler(body -> cmdFuture.complete(body.toJsonArray()));
            };

            client.get(NamesConstants.NAMES_ENDPOINT, responseHandler)
                    .exceptionHandler(cmdFuture::fail)
                    .end();
        };

        this.circuitBreaker.executeWithFallback(command, throwable -> null)
                .setHandler(handler);
    }

    private static <T> Handler<Throwable> exceptionHandler(Handler<AsyncResult<T>> handler) {
        return t -> handler.handle(Future.failedFuture(t));
    }
}
