
import com.google.common.io.ByteStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YandexTranslate {

    private static final Pattern PATTERN = Pattern.compile("^.*charset=(.*).*$");
    private static final String DEFAULT_CONTENT_ENCODING = "UTF-8";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Program started. Enter your request" +
                "to translate or \"STOP\" to stop it. ");
        while (true) {
            String request = scanner.nextLine();
            if (request.equals("STOP")) break;
            String answer = getYandexTranslate(request);
            String result = parseJSON(answer);
            System.out.println(result);
        }
        System.out.println("Goodbye!");
    }

    private static String getYandexTranslate (String text) throws IOException {
        String address = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" +
                "trnsl.1.1.20190207T165449Z.737ba9e0a47e2876.a03251d1f9b9a5a9aa4689e3857432209e983672" +
                "&text=" + text + "&lang=en-ru";
        HttpURLConnection connection = ((HttpURLConnection) new URL(address).openConnection());
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.connect();
        try (InputStream stream = connection.getInputStream()) {
            return contentStream(connection, stream);
        }
    }

    private static String parseJSON(String JSON) {
        JsonElement jsonelement = new JsonParser().parse(JSON);
        JsonObject jsonobject = jsonelement.getAsJsonObject();
        JsonArray jsonarray = jsonobject.getAsJsonArray("text");
        return jsonarray.get(0).toString();
    }

    private static String contentStream(HttpURLConnection connection, InputStream stream) throws IOException {
        String charsetName = DEFAULT_CONTENT_ENCODING;
        Matcher matcher = PATTERN.matcher(connection.getContentType());
        if (matcher.find()) charsetName = matcher.group(1);
        byte[] content = ByteStreams.toByteArray(stream);
        return new String(content, charsetName);
    }
}
