package ch.heigvd.dai;

import io.javalin.Javalin;

public class Main {
  static final int port = 8080;

  public static void main(String[] args) {
    Javalin app = Javalin.create(config -> {
      config.staticFiles.add("/public");
    });

    app.get("/", ctx -> ctx.redirect("/index.html"));
    app.get("/track-inventory", ctx -> ctx.result("Tracking inventory..."));
    app.get("/manage-suppliers", ctx -> ctx.result("Managing suppliers..."));
    app.get("/generate-reports", ctx -> ctx.result("Generating reports..."));

    app.start(port);
  }
}