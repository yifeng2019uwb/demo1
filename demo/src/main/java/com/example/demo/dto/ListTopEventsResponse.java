package com.example.demo.dto;

import java.util.List;

/*GET /top-events?limit=10
Response: 200 — {
  "results": [
    { "event_type": "login", "count": 120 },
    { "event_type": "purchase", "count": 50 }
  ]
} */

public record ListTopEventsResponse(
    List<Item> result
) {
    public record Item (
        String event_type,
        Long count
    ){}
}
