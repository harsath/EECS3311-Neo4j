package ca.yorku.eecs.API;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ca.yorku.eecs.API.ActorController;
import ca.yorku.eecs.API.MovieController;
import ca.yorku.eecs.ServerCalls.DatabaseExecutor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Handler implements HttpHandler {

    private DatabaseExecutor databaseExecutor = null;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        databaseExecutor = new DatabaseExecutor();
        ActorController actorController = new ActorController(databaseExecutor);
        MovieController movieController = new MovieController(databaseExecutor);
        BaconController baconController = new BaconController(databaseExecutor);
        RealtionshipController realtionshipController = new RealtionshipController(databaseExecutor);
        BaseController baseController = new BaseController(databaseExecutor);

        String requestPath = exchange.getRequestURI().getPath();
        /* this requested path will be diffrent for you as my base one was due to config errors: 
         * http://localhost:8080/bolt://localhost:7687/api/v1/
         */
        switch (requestPath.split("7687")[1]) {
            case "/api/v1/addActor":
                actorController.addActor(exchange);
                break;
            case "/api/v1/getActor":
                actorController.getActor(exchange);
                break;
            case "/api/v1/addMovie":
                movieController.addMovie(exchange);
                break;
            case "/api/v1/getMovie":
                movieController.getMovie(exchange);
                break;
            case "/api/v1/addRelationship":
                realtionshipController.addRelationship(exchange);
                break;
            case "/api/v1/hasRelationship":
                 realtionshipController.hasRelationship(exchange);
                break;
            case "/api/v1/computeBaconNumber":
                baconController.computeBaconNumber(exchange);
                break;
            case "/api/v1/computeBaconPath":
                 baconController.computeBaconPath(exchange);
                break;
            case "/api/v1/clearDatabase":
                baseController.clearDatabase(exchange);
                break;
            default:
                baseController.invalidRequest(exchange);
        }
    }
}
