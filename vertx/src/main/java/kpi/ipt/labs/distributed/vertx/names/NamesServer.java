package kpi.ipt.labs.distributed.vertx.names;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
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
import io.vertx.ext.consul.CheckOptions;
import io.vertx.ext.consul.ConsulClient;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.servicediscovery.types.HttpEndpoint;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static kpi.ipt.labs.distributed.vertx.NamesConstants.*;

public class NamesServer extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamesServer.class);

    private HttpServer server;
    private ConsulClient consulClient;
    private List<String> names;

    private String id;
    private boolean registered = false;

    @Override
    public void start(Future<Void> completeFuture) throws Exception {
        LOGGER.info("Starting Names server");

        this.id = SERVICE_NAME + ":localhost:" + SERVER_PORT;
        this.names = new CopyOnWriteArrayList<>();
        this.consulClient = ConsulClient.create(getVertx());

        Router router = configureRouter();

        this.server = getVertx()
                .createHttpServer(serverOptions())
                .requestHandler(router::accept)
                .listen(SERVER_PORT, res -> {
                    if (res.succeeded()) {
                        registerInConsul();

                        LOGGER.info("Server started successfully");
                        completeFuture.complete();
                    } else {
                        LOGGER.info("Server failed to start", res.cause());
                        completeFuture.fail(res.cause());
                    }
                });
    }

    private void registerInConsul() {

        CheckOptions checkOptions = new CheckOptions()
                .setName("Names service API")
                .setHttp("http://localhost:" + SERVER_PORT + HEALTH_ENDPOINT)
                .setInterval("2s");

        ServiceOptions serviceOptions = new ServiceOptions()
                .setId(this.id)
                .setName(SERVICE_NAME)
                .setPort(SERVER_PORT)
                .setTags(Collections.singletonList(HttpEndpoint.TYPE))
                .setCheckOptions(checkOptions);

        consulClient.registerService(serviceOptions, res -> {
            if (res.succeeded()) {
                registered = true;

                LOGGER.info("Successfully registered in Consul");
            } else {
                LOGGER.warn("Registration in Consul failed");
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        this.names.clear();

        Future<Void> deregisterFuture = Future.succeededFuture();
        Future<Void> serverClosingFuture = Future.future();

        if (this.registered) {
            deregisterFuture = Future.future();
            this.consulClient.deregisterService(this.id, deregisterFuture.completer());
        }

        this.server.close(serverClosingFuture.completer());

        CompositeFuture.all(deregisterFuture, serverClosingFuture).setHandler(result -> {
            if (result.succeeded()) {
                stopFuture.complete();
            } else {
                stopFuture.fail(result.cause());
            }
        });
    }

    private HttpServerOptions serverOptions() {
        return new HttpServerOptions();
    }

    private Router configureRouter() {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.get(NAMES_ENDPOINT)
                .handler(this::getNames);

        router.put(NAMES_ENDPOINT)
                .handler(this::putName);

        router.get(HEALTH_ENDPOINT)
                .handler(this::healthCheck);

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

    private void healthCheck(RoutingContext context) {
        LOGGER.info("Health check request");

        context.response()
                .setStatusCode(OK.code())
                .end();
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
