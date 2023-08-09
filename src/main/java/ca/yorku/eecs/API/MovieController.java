package ca.yorku.eecs.API;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import ca.yorku.eecs.Utils;
import ca.yorku.eecs.ServerCalls.DatabaseExecutor;
import ca.yorku.eecs.models.ActorModel;
import ca.yorku.eecs.models.MovieModel;

public class MovieController extends BaseController {

    public MovieController(DatabaseExecutor newDB) {
        super(newDB);
        //TODO Auto-generated constructor stub
    }

    protected void addMovie(HttpExchange exchange) throws IOException {
        MovieModel movie = new MovieModel();
        /* Unsupported HTTP method to this API endpoint */
        if (checkRequestMethod(exchange, "PUT")) {
            sendResponse(exchange, 405, "Endpoint only accepts PUT requests.");
            return;
        }
        String requestBody = Utils.getBody(exchange);
        JSONObject jsonHttpBody;
        /* Invalid JSON format */
        try {
            jsonHttpBody = Utils.convertToJSONObject(requestBody);
        } catch (JSONException e) {
            sendResponse(exchange, 400, "Invalid JSON string.");
            return;
        }
        /* Check if JSON property 'name' exists */
        if (jsonHttpBody.has("name")) {
           movie.setName(jsonHttpBody.getString("name"));
        }
        /* Check if JSON property 'movieId' exists */
        if (jsonHttpBody.has("movieId")) {
           movie.setId(jsonHttpBody.getString("movieId"));
        }

        if (movie.getId() != "" && movie.getName() != "") {
            /* Edge case */
            if (databaseExecutor.checkIfMovieIdExists(movie)) {
                sendResponse(exchange, 400, "The 'movieId' already exists.");
                return;
            }
            /* Add to database, or send HTTP 500 in case of database error */
            try {
                databaseExecutor.addMovie(movie);
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Database error.");
                return;
            }
            sendResponse(exchange, 200, "Added successfully.");
        } else {
            sendResponse(exchange, 400, "Must include Json:");
            return;
        }
    }

    protected void getMovie(HttpExchange exchange) throws IOException {
        MovieModel movie = new MovieModel();
        
        if (checkRequestMethod(exchange, "GET")) {
            sendResponse(exchange, 405, "Endpoint only accepts GET requests.");
            return;
        }
        String query = exchange.getRequestURI().getQuery();
        System.out.println("Query: " + query);
        if (query != null) {

            String field = query.split("=", 0)[0];

            if (field.equals("movieId")) {
                movie.setId(query.split("=")[1]);
                if (!databaseExecutor.checkIfMovieIdExists(movie)) {
                    String err = "The 'movieId: " + movie.getId() + " does not exists.";
                    sendResponse(exchange, 404, err);
                    return;
                }

                JSONObject result = databaseExecutor.getMovie(movie);

                sendResponse(exchange, 200, result.toString());

                return;
            } else {
                sendResponse(exchange, 400, "Incorrect Field: " + field);
                return;
            }

        } else {
            sendResponse(exchange, 400, "No Query");
            return;
        }
    }

}
