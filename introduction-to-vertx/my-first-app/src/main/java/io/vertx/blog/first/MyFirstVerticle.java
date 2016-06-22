package io.vertx.blog.first;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class MyFirstVerticle extends AbstractVerticle {

  private Map<Integer, Whisky> products = new LinkedHashMap<>();

  @Override
  public void start(Future<Void> fut) throws Exception {
    createSomeData();

    Router router = Router.router(vertx);

    router.route("/").handler(this::hello);
    router.route("/assets/*").handler(StaticHandler.create("assets"));
    router.get("/api/whiskies").handler(this::getAll);
    router.get("/api/whiskies/:id").handler(this::getOne);
    router.route("/api/whiskies*").handler(BodyHandler.create());
    router.post("/api/whiskies").handler(this::addOne);
    router.put("/api/whiskies/:id").handler(this::updateOne);
    router.delete("/api/whiskies/:id").handler(this::deleteOne);

    vertx
      .createHttpServer()
      .requestHandler(router::accept)
      .listen(
        config().getInteger("http.port", 8080),
        result -> {
        if (result.succeeded()) {
          fut.complete();
        } else {
          fut.fail(result.cause());
        }
      });
  }

  private void createSomeData() {
    Whisky bowmore = new Whisky("Bowmore 15 Years Laimrig", "Scotland, Islay");
    products.put(bowmore.getId(), bowmore);
    Whisky talisker = new Whisky("Talisker 57° North", "Scotland, Island");
    products.put(talisker.getId(), talisker);
  }

  private void hello(RoutingContext routingContext) {
    routingContext.response()
      .putHeader("content-type", "text/html")
      .end("<h1>Hello from my first Vert.x 3 application</h1>");
  }

  private void getAll(RoutingContext routingContext) {
    routingContext.response()
      .putHeader("content-type", "application/json; charset=UTF-8")
      .end(Json.encodePrettily(products.values()));
  }

  private void getOne(RoutingContext routingContext) {
    String id = routingContext.request().getParam("id");
    if (id == null) {
      routingContext.response().setStatusCode(400).end();
      return;
    }

    Whisky whisky = products.get(Integer.valueOf(id));
    if (whisky == null) {
      routingContext.response().setStatusCode(404).end();
      return;
    }

    routingContext.response()
      .putHeader("content-type", "application/json; charset=UTF-8")
      .end(Json.encodePrettily(whisky));
  }

  private void addOne(RoutingContext routingContext) {
    Whisky whisky = Json.decodeValue(routingContext.getBodyAsString(), Whisky.class);
    products.put(whisky.getId(), whisky);
    routingContext.response()
      .setStatusCode(201)
      .putHeader("content-type", "application/json; charset=UTF-8")
      .end(Json.encodePrettily(whisky));
  }

  private void updateOne(RoutingContext routingContext) {
    String id = routingContext.request().getParam("id");
    JsonObject json = routingContext.getBodyAsJson();
    if (id == null || json == null) {
      routingContext.response().setStatusCode(400).end();
      return;
    }

    Whisky whisky = products.get(Integer.valueOf(id));
    if (whisky == null) {
      routingContext.response().setStatusCode(404).end();
      return;
    }

    whisky.setName(json.getString("name"));
    whisky.setOrigin(json.getString("origin"));
    routingContext.response()
      .putHeader("content-type", "application/json; charset=UTF-8")
      .end(Json.encodePrettily(whisky));
  }

  private void deleteOne(RoutingContext routingContext) {
    String id = routingContext.request().getParam("id");
    if (id == null) {
      routingContext.response().setStatusCode(400).end();
      return;
    }

    products.remove(Integer.valueOf(id));
    routingContext.response().setStatusCode(204).end();
  }

}
