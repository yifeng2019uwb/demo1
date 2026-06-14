package com.example.demo.service;

import java.util.UUID;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.data.domain.PageRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.dao.EventDao;
import com.example.demo.dto.CreateEventRequest;
import com.example.demo.dto.CreateEventResponse;
import com.example.demo.dto.GetEventResponse;
import com.example.demo.dto.GetSummaryResponse;
import com.example.demo.dto.ListTopEventsResponse;
import com.example.demo.model.Event;
import com.example.demo.NotFoundException;
import com.example.demo.ValidationException;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
public class EventServiceImp implements EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventDao eventDao;

    public EventServiceImp(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Override
    public CreateEventResponse createEvent(CreateEventRequest request) {
        validateUUID(request.customer_id());
        validateTimestamp(request.timestamp());
        Event event = new Event(request.customer_id(), request.event_type(), request.timestamp(), request.metadata());
        Event savedEvent = eventDao.save(event);
        log.atInfo().log("Event created with ID: {}", savedEvent.getId());
        return new CreateEventResponse(savedEvent.getId());
    }

    @Override
    public GetEventResponse getEvent(String event_id) {
        validateUUID(event_id);
        Optional<Event> event = eventDao.findEventById(event_id);
        log.atInfo().log("Event retrieved with ID: {} , result: {}", event_id, event.orElse(null));
        if (event.isPresent()) {
            return new GetEventResponse(
                event.get().getId(),
                event.get().getCustomerId(),
                event.get().getType(),
                event.get().getTimestamp(),
                event.get().getMetadata()
            );
        } else {
            log.atWarn().log("Event not found with ID: {}", event_id);
            throw new NotFoundException("Event not found with ID: " + event_id);
        }
    }

    @Override
    public GetSummaryResponse getSummary(String customer_id, LocalDate startTime, LocalDate endTime) {
        validateUUID(customer_id);
        validateTimestamp(startTime);
        validateTimestamp(endTime);
        if (startTime.isAfter(endTime)) {
            throw new ValidationException("Start time cannot be after end time");
        }
        List<Event> events = eventDao.findEventsByTimestampBetween(startTime, endTime);
        log.atInfo().log("Summary retrieved for customer: {}, start time: {}, end time: {}, event count: {}", customer_id, startTime, endTime, events.size());
        return new GetSummaryResponse(customer_id, events.size(), countEvents(events));
    }

    @Override
    public ListTopEventsResponse getTopEvents(int limit) {
        if (limit <= 0) {
            log.atError().log("Limit must be a positive integer {} ", limit);
            throw new ValidationException("Limit must be a positive integer");
        }
        if (limit > 100) {
            log.atError().log("Limit cannot exceed 100 {} ", limit);
            throw new ValidationException("Limit cannot exceed 100");
        }
        List<Event> events = eventDao.findTopEventsSortedByTimestampDesc(PageRequest.of(0, limit));
        log.atInfo().log("Top events retrieved with limit: {}, event count: {}", limit, events.size());
        return wrapperResponse(events);
    }
    
    private void validateTimestamp(LocalDate timestamp) {
        if (timestamp == null) {
            log.atError().log("Timestamp cannot be null");
            throw new ValidationException("Timestamp cannot be null");
        }
        if (timestamp.isAfter(LocalDate.now())) {
            log.atError().log("Timestamp cannot be in the future");
            throw new ValidationException("Timestamp cannot be in the future");
        }
    }

    private UUID validateUUID(String uuid) {
        if (uuid == null) {
            log.atError().log("UUID cannot be null");
            throw new ValidationException("UUID cannot be null");
        }
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            log.atError().log("Invalid UUID format");
            throw new ValidationException("Invalid UUID format");
        }
    }

    private Map<String, Integer> countEvents(List<Event> events) {
        Map<String, Integer> eventCount = new HashMap<>();
        for (Event event : events) {
            eventCount.put(event.getType(), eventCount.getOrDefault(event.getType(), 0) + 1);
        }
        return eventCount;
    }

    private ListTopEventsResponse wrapperResponse(List<Event> events) {
        List<ListTopEventsResponse.Item> items = countEvents(events).entrySet().stream()
            .map(e -> new ListTopEventsResponse.Item(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
        return new ListTopEventsResponse(items);
    }

}
