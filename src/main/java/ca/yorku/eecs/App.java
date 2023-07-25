package ca.yorku.eecs;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class App {
  static int PORT = 8080;

  public static void main(String[] args) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
    // TODO: two lines of code are expected to be added here
    // please refer to the HTML server example
    server.start();
    System.out.printf("Server started on port %d...\n", PORT);
  }
}
