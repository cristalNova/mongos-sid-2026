package co.icesi.exercise.services;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.Event;
import co.icesi.exercise.model.Subscription;
import co.icesi.exercise.model.SubscriptionPK;
import co.icesi.exercise.repositories.AppUserRepository;
import co.icesi.exercise.repositories.EventRepository;
import co.icesi.exercise.repositories.SubscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SubscriptionServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private Subscription subscription;
    private AppUser user;
    private Event event;
    private SubscriptionPK pk;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new AppUser();
        user.setId(1);

        event = new Event();
        event.setId(2);

        pk = new SubscriptionPK(1, 2);

        subscription = new Subscription();
        subscription.setSubscriptionId(pk);
        subscription.setUser(user);
        subscription.setEvent(event);
        subscription.setAttendance(true);
    }


    @Test
    void getAllSubscriptions_ShouldReturnList() {
        when(subscriptionRepository.findAll()).thenReturn(List.of(subscription));

        List<Subscription> result = subscriptionService.getAllSubscriptions();

        assertEquals(1, result.size());
        verify(subscriptionRepository).findAll();
    }

    @Test
    void getSubscriptionById_ShouldReturn() {
        when(subscriptionRepository.findById(pk)).thenReturn(Optional.of(subscription));

        Subscription result = subscriptionService.getSubscriptionById(1, 2);

        assertNotNull(result);
    }

    @Test
    void getSubscriptionById_ShouldThrowException() {
        when(subscriptionRepository.findById(pk)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionService.getSubscriptionById(1, 2);
        });
    }

    @Test
    void getSubscriptionsByUserId_ShouldReturnList() {
        when(subscriptionRepository.findByUserId(1)).thenReturn(List.of(subscription));

        List<Subscription> result = subscriptionService.getSubscriptionsByUserId(1);

        assertEquals(1, result.size());
        verify(subscriptionRepository).findByUserId(1);
    }
    
    @Test
    void getSubscriptionsByEventId_ShouldReturnList() {
        when(subscriptionRepository.findByEventId(2)).thenReturn(List.of(subscription));

        List<Subscription> result = subscriptionService.getSubscriptionsByEventId(2);

        assertEquals(1, result.size());
        verify(subscriptionRepository).findByEventId(2);
    }

    @Test
    void createSubscription_ShouldSave() {
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(eventRepository.findById(2)).thenReturn(Optional.of(event));
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenReturn(subscription);

        Subscription result = subscriptionService.createSubscription(1, 2, true);

        assertEquals(user, result.getUser());
        assertEquals(event, result.getEvent());
        assertTrue(result.getAttendance());

        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void createSubscription_ShouldThrowIfUserNotFound() {
        when(appUserRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionService.createSubscription(1, 2, true);
        });
    }

    @Test
    void createSubscription_ShouldThrowIfEventNotFound() {
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(eventRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionService.createSubscription(1, 2, true);
        });
    }

    @Test
    void updateAttendance_ShouldUpdate() {
        when(subscriptionRepository.findById(pk)).thenReturn(Optional.of(subscription));
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenReturn(subscription);

        Subscription result = subscriptionService.updateAttendance(1, 2, false);

        assertFalse(result.getAttendance());
        verify(subscriptionRepository).save(subscription);
    }


    @Test
    void updateAttendance_ShouldThrowException() {
        when(subscriptionRepository.findById(pk)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionService.updateAttendance(1, 2, false);
        });
    }


    @Test
    void deleteSubscription_ShouldDelete() {
        when(subscriptionRepository.findById(pk)).thenReturn(Optional.of(subscription));

        subscriptionService.deleteSubscription(1, 2);

        verify(subscriptionRepository).delete(subscription);
    }


    @Test
    void deleteSubscription_ShouldThrowException() {
        when(subscriptionRepository.findById(pk)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionService.deleteSubscription(1, 2);
        });
    }
}
