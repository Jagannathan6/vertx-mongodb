package com.vertx.verticle;

import com.fasterxml.jackson.databind.util.BeanUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.List;


public class MainVerticle extends AbstractVerticle {

    private MongoClient mongoClient;

    public void setMongoClient(MongoClient client) {
        mongoClient = client;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }


    private void createServer(Router router, Future future) {
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) {
                                future.complete();
                            } else {
                                future.fail(result.cause());
                            }
                        }
                );
    }

    public void start(Future<Void> future) {
        Router router = Router.router(vertx);
        router.route("/").handler(r ->{
            r.response().putHeader("Content-type",
                    "text/html")
                    .end("<h1> This is an example rest Application </h1>");
        });
        String uri = "mongodb://localhost:27017";
        String db = "test";
        JsonObject mongoconfig = new JsonObject()
                .put("connection_string", uri)
                .put("db_name", db);
        setMongoClient(MongoClient.createShared(vertx, mongoconfig));
        router.route("/v1/employees*").handler(BodyHandler.create());
        router.post("/v1/employees").handler(this::addOne);
        router.get("/v1/employees").handler(this::getAll);
        createServer(router, future);

    }

    private void getAll(RoutingContext routingContext) {
        getMongoClient().find("employee", new JsonObject(), results -> {
            if (results.succeeded()) {
                List<JsonObject> objects = results.result();
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(objects));
            }
        });
    }

    private void addOne(RoutingContext routingContext) {
        Employee employee = Json.decodeValue(routingContext.getBodyAsString(),
                Employee.class);
        JsonObject jsonObject = JsonObject.mapFrom(employee);
        getMongoClient().insert("employee", jsonObject, res -> {
            if (res.succeeded()) {
                String id = res.result();
                routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(employee));
            } else {
                res.cause().printStackTrace();
            }
        });
    }


}
