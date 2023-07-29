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

class Utils {
  /* Extract query parameters */
  public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
    Map<String, String> queryPairs = new LinkedHashMap<String, String>();
    String[] pairs = query.split("&");
    for (String pair : pairs) {
      int idx = pair.indexOf("=");
      queryPairs.put(
          URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
          URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
    }
    return queryPairs;
  }

  /* Extract JSON body as `String` (method 1) */
  public static String convert(InputStream inputStream) throws IOException {

    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
      return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }
  }

  /* Extract JSON body as `String` (method 2) */
  public static String getBody(HttpExchange httpExchange) throws IOException {
    InputStreamReader inputStreamReader =
        new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

    int b;
    StringBuilder stringBuilder = new StringBuilder();
    while ((b = bufferedReader.read()) != -1) {
      stringBuilder.append((char) b);
    }

    bufferedReader.close();
    inputStreamReader.close();

    return stringBuilder.toString();
  }
}
