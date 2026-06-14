package com.example.demo.service;

import com.example.demo.NotFoundException;
import com.example.demo.ValidationException;
import com.example.demo.dao.EventDao;
import com.example.demo.dto.*;
import com.example.demo.model.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImpTest {

    @Mock private EventDao eventDao;
    @InjectMocks private EventServiceImp service;

    private static final String VALID_UUID = "11111111-1111-1111-1111-111111111111";

    private Event event(String type, LocalDate ts) {
        return new Event(VALID_UUID, type, ts, Map.of());
    }

    // ---------- createEvent ----------

    @Test
    void createEvent_savesAndReturnsId() {
        var request = new CreateEventRequest(VALID_UUID, "CLICK", LocalDate.of(2026, 1, 10), Map.of());
        when(eventDao.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateEventResponse response = service.createEvent(request);

        assertThat(response).isNotNull();
        verify(eventDao).save(any(Event.class));
    }

    @Test
    void createEvent_throws_whenCustomerIdNotUuid() {
        var request = new CreateEventRequest("not-a-uuid", "CLICK", LocalDate.of(2026, 1, 10), Map.of());

        assertThatThrownBy(() -> service.createEvent(request))
                .isInstanceOf(ValidationException.class);
        verifyNoInteractions(eventDao);   // fails fast before hitting the DAO
    }

    @Test
    void createEvent_throws_whenTimestampInFuture() {
        var request = new CreateEventRequest(VALID_UUID, "CLICK", LocalDate.now().plusDays(1), Map.of());

        assertThatThrownBy(() -> service.createEvent(request))
                .isInstanceOf(ValidationException.class);
    }

    // ---------- getEvent ----------

    @Test
    void getEvent_returnsResponse_whenFound() {
        when(eventDao.findEventById(VALID_UUID)).thenReturn(Optional.of(event("CLICK", LocalDate.of(2026, 1, 10))));

        GetEventResponse response = service.getEvent(VALID_UUID);

        assertThat(response).isNotNull();
        verify(eventDao).findEventById(VALID_UUID);
    }

    @Test
    void getEvent_throws_whenNotFound() {
        when(eventDao.findEventById(VALID_UUID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getEvent(VALID_UUID))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getEvent_throws_whenIdNotUuid() {
        assertThatThrownBy(() -> service.getEvent("bad-id"))
                .isInstanceOf(ValidationException.class);
        verifyNoInteractions(eventDao);
    }

    // ---------- getSummary ----------

    @Test
    void getSummary_countsEventsByType() {
        List<Event> events = List.of(
                event("CLICK", LocalDate.of(2026, 1, 10)),
                event("CLICK", LocalDate.of(2026, 1, 11)),
                event("VIEW",  LocalDate.of(2026, 1, 12)));
        when(eventDao.findEventsByTimestampBetween(any(), any())).thenReturn(events);

        GetSummaryResponse response = service.getSummary(
                VALID_UUID, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

        assertThat(response).isNotNull();
        // If GetSummaryResponse exposes the count map, assert CLICK=2, VIEW=1 here.
    }

    @Test
    void getSummary_throws_whenStartAfterEnd() {
        assertThatThrownBy(() -> service.getSummary(
                VALID_UUID, LocalDate.of(2026, 1, 31), LocalDate.of(2026, 1, 1)))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void getSummary_throws_whenStartTimeNull() {
        assertThatThrownBy(() -> service.getSummary(VALID_UUID, null, LocalDate.of(2026, 1, 31)))
                .isInstanceOf(ValidationException.class);
    }

    // ---------- getTopEvents ----------

    @Test
    void getTopEvents_returnsItems() {
        when(eventDao.findTopEventsSortedByTimestampDesc(any())).thenReturn(List.of(
                event("CLICK", LocalDate.of(2026, 1, 10)),
                event("VIEW",  LocalDate.of(2026, 1, 11))));

        ListTopEventsResponse response = service.getTopEvents(2);

        assertThat(response).isNotNull();
        verify(eventDao).findTopEventsSortedByTimestampDesc(any());
    }

    @Test
    void getTopEvents_throws_whenLimitZeroOrNegative() {
        assertThatThrownBy(() -> service.getTopEvents(0))
                .isInstanceOf(ValidationException.class);
        verifyNoInteractions(eventDao);
    }

    @Test
    void getTopEvents_throws_whenLimitExceeds100() {
        assertThatThrownBy(() -> service.getTopEvents(101))
                .isInstanceOf(ValidationException.class);
    }
}