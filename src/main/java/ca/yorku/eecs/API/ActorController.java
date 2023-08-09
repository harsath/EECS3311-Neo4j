package ca.yorku.eecs.API;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import ca.yorku.eecs.Utils;
import ca.yorku.eecs.ServerCalls.DatabaseExecutor;
import ca.yorku.eecs.models.ActorModel;

public class ActorController extends BaseController {
    public ActorController(DatabaseExecutor newDB) {
        super(newDB);
        // TODO Auto-generated constructor stub
    }

    protected void addActor(HttpExchange exchange) throws IOException {
        ActorModel actor = new ActorModel();
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

        if (jsonHttpBody.has("name")) {
            actor.setName(jsonHttpBody.getString("name"));
        }

        if (jsonHttpBody.has("actorId")) {
            actor.setId(jsonHttpBody.getString("actorId"));
        }

        if (actor.getId() != "" && actor.getName() != "") {
            /* Edge case */
            if (databaseExecutor.checkIfActorIdExists(actor)) {
                sendResponse(exchange, 400, "The 'actorId' already exists.");
                return;
            }
            /* Add to database, or send HTTP 500 in case of database error */
            try {
                databaseExecutor.addActor(actor);
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

    protected void getActor(HttpExchange exchange) throws IOException {
        ActorModel actor = new ActorModel();
        
        if (checkRequestMethod(exchange, "GET")) {
            sendResponse(exchange, 405, "Endpoint only accepts GET requests.");
            return;
        }
        String query = exchange.getRequestURI().getQuery();
        System.out.println("Query: " + query);
        if (query != null) {

            String field = query.split("=", 0)[0];

            if (field.equals("actorId")) {
                actor.setId(query.split("=")[1]);
                if (!databaseExecutor.checkIfActorIdExists(actor)) {
                    String err = "The 'actorId: " + actor.getId() + " does not exists.";
                    sendResponse(exchange, 404, err);
                    return;
                }

                JSONObject result = databaseExecutor.getActor(actor);

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
