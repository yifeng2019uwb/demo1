1. Apis:

POST /events
Body: {
  "customer_id": "cust-123",
  "event_type": "login",
  "timestamp": "2026-06-12T10:00:00Z",
  "metadata": { "ip": "10.0.0.1" }   ← optional
}
Response: 201 — { "event_id": "uuid" }


GET /events/{event_id}
Response: 200 — { event_id, customer_id, event_type, timestamp, metadata }
Response: 404 — if not found


GET /summary?customer_id=cust-123&start_time=...&end_time=...
Response: 200 — {
  "customer_id": "cust-123",
  "total_events": 150,
  "event_breakdown": { "login": 100, "purchase": 25, "logout": 25 }
}

GET /top-events?limit=10
Response: 200 — {
  "results": [
    { "event_type": "login", "count": 120 },
    { "event_type": "purchase", "count": 50 }
  ]
}

GET /health
Response: 200 — { "status": "healthy" }
