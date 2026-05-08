package co.icesi.exercise.services;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.Event;
import co.icesi.exercise.model.Subscription;
import co.icesi.exercise.model.SubscriptionPK;
import co.icesi.exercise.repositories.AppUserRepository;
import co.icesi.exercise.repositories.EventRepository;
import co.icesi.exercise.repositories.SubscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private EventRepository eventRepository;

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public Subscription getSubscriptionById(int userId, int eventId) {
        SubscriptionPK subscriptionPK = new SubscriptionPK(userId, eventId);
        return subscriptionRepository.findById(subscriptionPK)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Suscripción no encontrada para userId=" + userId + " y eventId=" + eventId));
    }

    public List<Subscription> getSubscriptionsByUserId(int userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    public List<Subscription> getSubscriptionsByEventId(int eventId) {
        return subscriptionRepository.findByEventId(eventId);
    }

    @Transactional
    public Subscription createSubscription(int userId, int eventId, Boolean attendance) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado con id: " + eventId));

        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(new SubscriptionPK(userId, eventId));
        subscription.setUser(user);
        subscription.setEvent(event);
        subscription.setAttendance(attendance);

        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription updateAttendance(int userId, int eventId, Boolean attendance) {
        Subscription subscription = getSubscriptionById(userId, eventId);
        subscription.setAttendance(attendance);
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public void deleteSubscription(int userId, int eventId) {
        Subscription subscription = getSubscriptionById(userId, eventId);
        subscriptionRepository.delete(subscription);
    }
}
