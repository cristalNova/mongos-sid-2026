package co.icesi.exercise.services;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.nosql.EventDocument;
import co.icesi.exercise.model.nosql.SubscriptionDocument;
import co.icesi.exercise.repositories.AppUserRepository;
import co.icesi.exercise.repositories.nosql.EventMongoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventMongoRepository eventMongoRepository;
    @Autowired
    private AppUserRepository appUserRepository;

    public List<EventDocument> getAllEvents() {
        return eventMongoRepository.findAll();
    }

    public EventDocument getEventById(String id) {
        return eventMongoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado con id: " + id));
    }

    public EventDocument createEvent(EventDocument event) {
        return eventMongoRepository.save(event);
    }

    public EventDocument updateEvent(String id, EventDocument updated) {
        EventDocument existing = getEventById(id);
        existing.setName(updated.getName());
        existing.setDate(updated.getDate());
        existing.setDescription(updated.getDescription());
        if (updated.getPhysicalSpace() != null) {
            existing.setPhysicalSpace(updated.getPhysicalSpace());
        }
        return eventMongoRepository.save(existing);
    }

    public void deleteEvent(String id) {
        EventDocument existing = getEventById(id);
        eventMongoRepository.delete(existing);
    }

    public EventDocument subscribeUser(String eventId, int userId) {
        EventDocument event = getEventById(eventId);
        boolean alreadySubscribed = event.getSubscriptions().stream()
                .anyMatch(s -> s.getUserId() != null && s.getUserId() == userId);
        if (alreadySubscribed) {
            return event;
        }
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + userId));

        SubscriptionDocument sub = new SubscriptionDocument();
        sub.setUserId(user.getId());
        sub.setUserFirstName(user.getFirstName());
        sub.setUserLastName(user.getLastName());
        sub.setAttendance(false);

        event.getSubscriptions().add(sub);
        return eventMongoRepository.save(event);
    }

    public EventDocument unsubscribeUser(String eventId, int userId) {
        EventDocument event = getEventById(eventId);
        event.getSubscriptions().removeIf(s -> s.getUserId() != null && s.getUserId() == userId);
        return eventMongoRepository.save(event);
    }

    public EventDocument markAttendance(String eventId, int userId, boolean attended) {
        EventDocument event = getEventById(eventId);
        event.getSubscriptions().stream()
                .filter(s -> s.getUserId() != null && s.getUserId() == userId)
                .findFirst()
                .ifPresent(s -> s.setAttendance(attended));
        return eventMongoRepository.save(event);
    }
}
