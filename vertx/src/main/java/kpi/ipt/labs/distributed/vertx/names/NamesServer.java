package kpi.ipt.labs.distributed.vertx.names;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import kpi.ipt.labs.distributed.vertx.NamesConstants;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;

public class NamesServer extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamesServer.class);

    private HttpServer server;
    private List<String> names;

    @Override
    public void start(Future<Void> completeFuture) throws Exception {
        LOGGER.info("Starting Names server");

        Router router = configureRouter();

        this.names = new CopyOnWriteArrayList<>();

        this.server = getVertx()
                .createHttpServer(serverOptions())
                .requestHandler(router::accept)
                .listen(NamesConstants.SERVER_PORT, res -> {
                    if (res.succeeded()) {
                        LOGGER.info("Server started successfully");

                        completeFuture.complete();
                    } else {
                        LOGGER.info("Server failed to start", res.cause());

                        completeFuture.fail(res.cause());
                    }
                });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        names.clear();
        server.close(stopFuture.completer());
    }

    private HttpServerOptions serverOptions() {
        return new HttpServerOptions();
    }

    private Router configureRouter() {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.get(NamesConstants.NAMES_ENDPOINT)
                .handler(this::getNames);

        router.put(NamesConstants.NAMES_ENDPOINT)
                .handler(this::putName);

        return router;
    }

    private void getNames(RoutingContext context) {
        context.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .end(Json.encode(names));
    }

    private void putName(RoutingContext context) {
        HttpServerResponse response = context.response();

        JsonObject body = getBody(context);
        if (body == null) {
            response.setStatusCode(BAD_REQUEST.code()).end();
            return;
        }

        String name = body.getString("name");
        LOGGER.info("Name {0} received", name);

        if (name == null) {
            response.setStatusCode(BAD_REQUEST.code()).end();
            return;
        }

        names.add(name);
        response.setStatusCode(CREATED.code()).end();
    }

    private JsonObject getBody(RoutingContext context) {
        Buffer body = context.getBody();
        if (body == null) {
            return null;
        }

        String bodyAsString = body.toString();
        if (bodyAsString.isEmpty()) {
            return null;
        }

        return new JsonObject(bodyAsString);
    }

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new NamesServer());
    }
}
