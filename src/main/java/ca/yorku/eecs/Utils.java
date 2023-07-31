package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONException;
import org.json.JSONObject;

class Utils {
  public static Map<String, String> splitQuery(String query)
      throws UnsupportedEncodingException {
    Map<String, String> queryPairs = new LinkedHashMap<String, String>();
    String[] pairs = query.split("&");
    for (String pair : pairs) {
      int idx = pair.indexOf("=");
      queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                     URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
    }
    return queryPairs;
  }

  public static String convert(InputStream inputStream) throws IOException {

    try (BufferedReader bufferedReader =
             new BufferedReader(new InputStreamReader(inputStream))) {
      return bufferedReader.lines().collect(
          Collectors.joining(System.lineSeparator()));
    }
  }

  public static String getBody(HttpExchange httpExchange) throws IOException {
    InputStreamReader inputStreamReader =
        new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

    int b;
    StringBuilder stringBuilder = new StringBuilder();
    while ((b = bufferedReader.read()) != -1) {
      stringBuilder.append((char)b);
    }

    bufferedReader.close();
    inputStreamReader.close();

    return stringBuilder.toString();
  }

  /* Convert raw JSON from `String` representation to `JSONObject` */
  public static JSONObject convertToJSONObject(String jsonString)
      throws JSONException {
    return new JSONObject(jsonString);
  }

  /* Check if a JSON property exists in the provided `JSONObject`  */
  public static boolean checkIfPropertyExists(JSONObject jsonObject,
                                              String property) {
    return jsonObject.has(property);
  }
}
