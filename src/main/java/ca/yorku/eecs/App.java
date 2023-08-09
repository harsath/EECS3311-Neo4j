package ca.yorku.eecs;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class App {
  static int PORT = 8080;

  public static void main(String[] args) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
    ProjectHttpHandler projectHttpHandler = new ProjectHttpHandler();
    server.createContext("/", projectHttpHandler);
    server.start();
    System.out.printf("Server started on port %d...\n", PORT);
  }
}
