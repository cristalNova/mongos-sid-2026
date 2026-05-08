package co.icesi.exercise.services;

import co.icesi.exercise.model.Event;
import co.icesi.exercise.model.PhysicalSpace;
import co.icesi.exercise.repositories.EventRepository;
import co.icesi.exercise.repositories.PhysicalSpaceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private PhysicalSpaceRepository physicalSpaceRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(int id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado con id: " + id));
    }

    public List<Event> getEventsByPhysicalSpaceId(int physicalSpaceId) {
        return eventRepository.findByPhysicalSpaceId(physicalSpaceId);
    }

    @Transactional
    public Event createEvent(Event event, int physicalSpaceId) {
        PhysicalSpace physicalSpace = physicalSpaceRepository.findById(physicalSpaceId)
                .orElseThrow(() -> new EntityNotFoundException("Espacio físico no encontrado con id: " + physicalSpaceId));
        event.setPhysicalSpace(physicalSpace);
        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEvent(int id, Event updatedEvent, Integer physicalSpaceId) {
        Event existingEvent = getEventById(id);
        existingEvent.setName(updatedEvent.getName());
        existingEvent.setDate(updatedEvent.getDate());
        existingEvent.setDescription(updatedEvent.getDescription());

        if (physicalSpaceId != null) {
            PhysicalSpace physicalSpace = physicalSpaceRepository.findById(physicalSpaceId)
                    .orElseThrow(() -> new EntityNotFoundException("Espacio físico no encontrado con id: " + physicalSpaceId));
            existingEvent.setPhysicalSpace(physicalSpace);
        }

        return eventRepository.save(existingEvent);
    }

    @Transactional
    public void deleteEvent(int id) {
        Event existingEvent = getEventById(id);
        eventRepository.delete(existingEvent);
    }
}
