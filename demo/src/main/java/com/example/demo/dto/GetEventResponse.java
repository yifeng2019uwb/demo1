package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.Map;

/*
GET /events/{event_id}
Response: 200 — { event_id, customer_id, event_type, timestamp, metadata }
Response: 404 — if not found
 */
public record GetEventResponse(
    String event_id,
    String customer_id,
    String event_type,
    LocalDateTime timestamp,
    Map<String, String> metadata
) {
}
