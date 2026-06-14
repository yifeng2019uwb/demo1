package com.example.demo.dto;

import java.util.Map;

/*
GET /summary?customer_id=cust-123&start_time=...&end_time=...
Response: 200 — {
  "customer_id": "cust-123",
  "total_events": 150,
  "event_breakdown": { "login": 100, "purchase": 25, "logout": 25 }
}
 */

public record GetSummaryResponse(
    String customer_id,
    Long total_events,
    Map<String, Long> event_breakdown
) {
}
