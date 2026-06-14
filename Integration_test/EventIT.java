import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.Random;

public class EventIT {
    static final String BASE_URL = "https://shark-app-kt3v6.ondigitalocean.app";
    static final String BASE_PATH = "/api/v1/events";
    static final HttpClient client = HttpClient.newHttpClient();

    static final UUID customer_id1 = UUID.randomUUID();
    static final UUID customer_id2 = UUID.randomUUID();
    static final Random rand = new Random();

    public static void main(String[] args) throws Exception {
        // Test code for event integration
        String eventId = createEvent_201();
        System.out.println("Event ID: " + eventId);
        getEvent_200(eventId);
        getsummaryEvent(customer_id1.toString());
        runComplexTest();
        listTopEventsResponse();

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
        // return response.body();
    }

    private static void getEvent_200(String eventId) throws Exception {
        System.out.println("==> GET " + BASE_PATH + "/" + eventId);
        var request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + BASE_PATH + "/" + eventId))
            .GET()
            .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status: " + response.statusCode() + "  Body: " + response.body());
        assert response.statusCode() == 200 : "Expected 200, got " + response.statusCode();
    }

    private static void getsummaryEvent(String customerId) throws Exception {
        String url = "%s%s/summary?customer_id=%s&start_time=2024-01-15&end_time=%s"
            .formatted(BASE_URL, BASE_PATH, customerId, LocalDate.now());
        System.out.println("==> GET " + BASE_PATH + "/summary");
        var request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status: " + response.statusCode() + "  Body: " + response.body());
        assert response.statusCode() == 200 : "Expected 200, got " + response.statusCode();
    }

    private static void listTopEventsResponse() throws Exception {
        String url = "%s%s/top-events?limit=%d"
            .formatted(BASE_URL, BASE_PATH, 10);
        System.out.println("==> GET " + url);
        var request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status: " + response.statusCode() + "  Body: " + response.body());
        assert response.statusCode() == 200 : "Expected 200, got " + response.statusCode();
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

    private static void runComplexTest() throws Exception {
        List<String> customers = initCustomerIds();
        List<String> eventTypes = getEventType();

        List<String> events = new ArrayList<>();
        int size = 10;
        String createEventUrl = BASE_URL + BASE_PATH;

        for (int i = 0; i < 10; i++) {
            String customer_id = customers.get(i%customers.size());
            String event_type = eventTypes.get(i%eventTypes.size());
            String body = """
            {
                "customer_id": "%s",
                "event_type": "%s",
                "timestamp": "2024-01-15",
                "metadata": %s
            }""".formatted(customer_id, event_type, mapToJson(buildMetaDataExample(event_type, customer_id)));
            
            var request = HttpRequest.newBuilder()
            .uri(URI.create(createEventUrl))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // ... rest of the logic
            System.out.println(response.body());
            events.add(response.body());
        }

        getsummaryEvent(customers.get(0));
        getsummaryEvent(customers.get(1));
        getsummaryEvent(customers.get(2));



    }
    private static List<String> initCustomerIds() {
        return List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }

    private static List<String> getEventType() {
        return List.of("LOGIN", "LOGOUT", "CLICK", "VIEW");
    }

    private static Map<String, String> buildMetaDataExample(String type, String id ) {
        return Map.of("event_type", type, "customer_id", id + rand.nextInt(100));
    }

    static String mapToJson(Map<String, String> map) {
        var sb = new StringBuilder("{");
        boolean first = true;
        for (var e : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(e.getKey()).append("\":\"").append(e.getValue()).append("\"");
            first = false;
        }
        return sb.append("}").toString();
    }
}
