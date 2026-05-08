package co.icesi.exercise.services;

import co.icesi.exercise.model.Event;
import co.icesi.exercise.model.PhysicalSpace;
import co.icesi.exercise.repositories.EventRepository;
import co.icesi.exercise.repositories.PhysicalSpaceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PhysicalSpaceRepository physicalSpaceRepository;

    @InjectMocks
    private EventService eventService;

    private Event event;
    private PhysicalSpace space;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        event = new Event();
        space = new PhysicalSpace();

        space.setId(1);

        event.setId(1);
        event.setName("Event");
        event.setDescription("Description");
        event.setPhysicalSpace(space);
    }

    @Test
    void getAllEvents_ShouldReturnAllEvents() {
        when(eventRepository.findAll()).thenReturn(List.of(event));

        List<Event> events = eventService.getAllEvents();

        assertEquals(1,events.size());
        verify(eventRepository).findAll();
    }
    @Test
    void getEventById_ShouldReturnEvent() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        Event result = eventService.getEventById(1);

        assertEquals("Event", result.getName());
    }

    @Test
    void getEventById_ShouldThrowException() {
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            eventService.getEventById(1);
        });
    }

    @Test
    void getEventsByPhysicalSpaceId_ShouldReturnList() {
        when(eventRepository.findByPhysicalSpaceId(1)).thenReturn(List.of(event));

        List<Event> result = eventService.getEventsByPhysicalSpaceId(1);

        assertEquals(1, result.size());
        verify(eventRepository).findByPhysicalSpaceId(1);
    }

    // ✅ 5. createEvent OK
    @Test
    void createEvent_ShouldSaveEvent() {
        when(physicalSpaceRepository.findById(1)).thenReturn(Optional.of(space));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event result = eventService.createEvent(event, 1);

        assertNotNull(result);
        assertEquals(space, result.getPhysicalSpace());
        verify(eventRepository).save(event);
    }

    @Test
    void createEvent_ShouldThrowException() {
        when(physicalSpaceRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            eventService.createEvent(event, 1);
        });
    }

    @Test
    void updateEvent_ShouldUpdateWithoutChangingSpace() {
        Event updated = new Event();
        updated.setName("Nuevo");
        updated.setDescription("Nueva desc");

        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event result = eventService.updateEvent(1, updated, null);

        assertEquals("Nuevo", result.getName());
        verify(eventRepository).save(event);
    }

    @Test
    void updateEvent_ShouldUpdateWithNewSpace() {
        PhysicalSpace newSpace = new PhysicalSpace();
        newSpace.setId(2);

        Event updated = new Event();
        updated.setName("Nuevo");

        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(physicalSpaceRepository.findById(2)).thenReturn(Optional.of(newSpace));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event result = eventService.updateEvent(1, updated, 2);

        assertEquals(newSpace, result.getPhysicalSpace());
        verify(eventRepository).save(event);
    }

    @Test
    void updateEvent_ShouldThrowIfSpaceNotFound() {
        Event updated = new Event();
        updated.setName("Nuevo");

        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(physicalSpaceRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            eventService.updateEvent(1, updated, 2);
        });
    }

    @Test
    void updateEvent_ShouldThrowIfEventNotFound() {
        Event updated = new Event();

        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            eventService.updateEvent(1, updated, null);
        });
    }

    @Test
    void deleteEvent_ShouldDelete() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        eventService.deleteEvent(1);

        verify(eventRepository).delete(event);
    }

    @Test
    void deleteEvent_ShouldThrowException() {
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            eventService.deleteEvent(1);
        });
    }


}
