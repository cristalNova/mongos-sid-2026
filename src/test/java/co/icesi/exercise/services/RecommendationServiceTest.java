package co.icesi.exercise.services;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.Recommendation;
import co.icesi.exercise.repositories.AppUserRepository;
import co.icesi.exercise.repositories.RecommendationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    private Recommendation recommendation;
    private AppUser sender;
    private AppUser receiver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sender = new AppUser();
        sender.setId(1);

        receiver = new AppUser();
        receiver.setId(2);

        recommendation = new Recommendation();
        recommendation.setId(1);
        recommendation.setMessage("Good Job");
        recommendation.setDate(Date.valueOf(LocalDate.now()));
        recommendation.setSender(sender);
        recommendation.setReceiver(receiver);
    }
    @Test
    void getAllRecommendations_ShouldReturnList() {
        when(recommendationRepository.findAll()).thenReturn(List.of(recommendation));

        List<Recommendation> result = recommendationService.getAllRecommendations();

        assertEquals(1, result.size());
        verify(recommendationRepository).findAll();
    }

    @Test
    void getRecommendationById_ShouldReturnRecommendation() {
        when(recommendationRepository.findById(1)).thenReturn(Optional.of(recommendation));

        Recommendation result = recommendationService.getRecommendationById(1);

        assertEquals("Good Job", result.getMessage());
    }

    @Test
    void getRecommendationById_ShouldThrowException() {
        when(recommendationRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            recommendationService.getRecommendationById(1);
        });
    }

    @Test
    void getRecommendationsSentByUser_ShouldReturnList() {
        when(recommendationRepository.findBySenderId(1))
                .thenReturn(List.of(recommendation));

        List<Recommendation> result =
                recommendationService.getRecommendationsSentByUser(1);

        assertEquals(1, result.size());
        verify(recommendationRepository).findBySenderId(1);
    }

    @Test
    void getRecommendationsReceivedByUser_ShouldReturnList() {
        when(recommendationRepository.findByReceiverId(2))
                .thenReturn(List.of(recommendation));

        List<Recommendation> result =
                recommendationService.getRecommendationsReceivedByUser(2);

        assertEquals(1, result.size());
        verify(recommendationRepository).findByReceiverId(2);
    }

    // ✅ 6. createRecommendation OK
    @Test
    void createRecommendation_ShouldSave() {
        when(appUserRepository.findById(1)).thenReturn(Optional.of(sender));
        when(appUserRepository.findById(2)).thenReturn(Optional.of(receiver));
        when(recommendationRepository.save(any(Recommendation.class)))
                .thenReturn(recommendation);

        Recommendation result =
                recommendationService.createRecommendation(recommendation, 1, 2);

        assertEquals(sender, result.getSender());
        assertEquals(receiver, result.getReceiver());

        verify(recommendationRepository).save(recommendation);
    }

    @Test
    void createRecommendation_ShouldThrowIfSenderNotFound() {
        when(appUserRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            recommendationService.createRecommendation(recommendation, 1, 2);
        });
    }

    @Test
    void createRecommendation_ShouldThrowIfReceiverNotFound() {
        when(appUserRepository.findById(1)).thenReturn(Optional.of(sender));
        when(appUserRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            recommendationService.createRecommendation(recommendation, 1, 2);
        });
    }

    @Test
    void updateRecommendation_ShouldUpdateWithoutChangingUsers() {
        Recommendation updated = new Recommendation();
        updated.setMessage("Nuevo mensaje");
        updated.setDate(Date.valueOf(LocalDate.now().plusDays(1)));

        when(recommendationRepository.findById(1)).thenReturn(Optional.of(recommendation));
        when(recommendationRepository.save(any(Recommendation.class)))
                .thenReturn(recommendation);

        Recommendation result =
                recommendationService.updateRecommendation(1, updated, null, null);

        assertEquals("Nuevo mensaje", result.getMessage());
        verify(recommendationRepository).save(recommendation);
    }

    @Test
    void updateRecommendation_ShouldUpdateSender() {
        AppUser newSender = new AppUser();
        newSender.setId(3);

        Recommendation updated = new Recommendation();
        updated.setMessage("Update");

        when(recommendationRepository.findById(1)).thenReturn(Optional.of(recommendation));
        when(appUserRepository.findById(3)).thenReturn(Optional.of(newSender));
        when(recommendationRepository.save(any(Recommendation.class)))
                .thenReturn(recommendation);

        Recommendation result =
                recommendationService.updateRecommendation(1, updated, 3, null);

        assertEquals(newSender, result.getSender());
        verify(recommendationRepository).save(recommendation);
    }

    @Test
    void updateRecommendation_ShouldUpdateReceiver() {
        AppUser newReceiver = new AppUser();
        newReceiver.setId(4);

        Recommendation updated = new Recommendation();

        when(recommendationRepository.findById(1)).thenReturn(Optional.of(recommendation));
        when(appUserRepository.findById(4)).thenReturn(Optional.of(newReceiver));
        when(recommendationRepository.save(any(Recommendation.class)))
                .thenReturn(recommendation);

        Recommendation result =
                recommendationService.updateRecommendation(1, updated, null, 4);

        assertEquals(newReceiver, result.getReceiver());
        verify(recommendationRepository).save(recommendation);
    }

    @Test
    void updateRecommendation_ShouldThrowIfSenderNotFound() {
        Recommendation updated = new Recommendation();

        when(recommendationRepository.findById(1)).thenReturn(Optional.of(recommendation));
        when(appUserRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            recommendationService.updateRecommendation(1, updated, 3, null);
        });
    }

    @Test
    void updateRecommendation_ShouldThrowIfReceiverNotFound() {
        Recommendation updated = new Recommendation();

        when(recommendationRepository.findById(1)).thenReturn(Optional.of(recommendation));
        when(appUserRepository.findById(4)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            recommendationService.updateRecommendation(1, updated, null, 4);
        });
    }

    @Test
    void updateRecommendation_ShouldThrowIfRecommendationNotFound() {
        Recommendation updated = new Recommendation();

        when(recommendationRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            recommendationService.updateRecommendation(1, updated, null, null);
        });
    }

    @Test
    void deleteRecommendation_ShouldDelete() {
        when(recommendationRepository.findById(1)).thenReturn(Optional.of(recommendation));

        recommendationService.deleteRecommendation(1);

        verify(recommendationRepository).delete(recommendation);
    }

    @Test
    void deleteRecommendation_ShouldThrowException() {
        when(recommendationRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            recommendationService.deleteRecommendation(1);
        });
    }
}
