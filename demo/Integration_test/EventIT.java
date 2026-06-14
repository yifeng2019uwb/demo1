import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventIT {
    static final String BASE_URL = "https://shark-app-kt3v6.ondigitalocean.app";
    static final String BASE_PATH = "/api/v1/events";
    static final HttpClient client = HttpClient.newHttpClient();

    static final UUID customer_id1 = UUID.randomUUID();

    public static void main(String[] args) throws Exception {
        // Test code for event integration
        createEvent_201();
        System.out.println("\n==> All tests passed!");
    }

    private static String createEvent_201() throws Exception {
        System.out.println("==> POST " + BASE_PATH);
        String body = """
            {
                "customer_id": "%s",
                "event_type": "LOGIN",
                "timestamp": "2024-01-15",
                "metadata": %s
            }
            """.formatted(customer_id1.toString(), buildMetaDataJson("key", "value", 1));

        var request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + BASE_PATH))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status: " + response.statusCode() + "  Body: " + response.body());
        assert response.statusCode() == 201 : "Expected 200, got " + response.statusCode();

        String r = response.body();
        int start = r.indexOf("\"event_id\":\"") + 12;
        return r.substring(start, r.indexOf("\"", start));
    }

    static String buildMetaDataJson(String key, String value, int size) {
        var sb = new StringBuilder("{");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(key).append(i).append("\":\"").append(value).append(i).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }
}
