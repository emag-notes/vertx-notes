package io.vertx.blog.first;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;

public class MyFirstVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> fut) throws Exception {
    vertx
      .createHttpServer()
      .requestHandler(r ->
        r.response().end(
          "<h1>Hello from my first Vert.x 3 application</h1>"
        ))
      .listen(8080, result -> {
        if (result.succeeded()) {
          fut.complete();
        } else {
          fut.fail(result.cause());
        }
      });
  }

}
