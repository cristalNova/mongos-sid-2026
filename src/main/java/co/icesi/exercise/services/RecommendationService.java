package co.icesi.exercise.services;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.Recommendation;
import co.icesi.exercise.repositories.AppUserRepository;
import co.icesi.exercise.repositories.RecommendationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecommendationService {

    @Autowired
    private RecommendationRepository recommendationRepository;
    @Autowired
    private AppUserRepository appUserRepository;

    public List<Recommendation> getAllRecommendations() {
        return recommendationRepository.findAll();
    }

    public Recommendation getRecommendationById(int id) {
        return recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recomendación no encontrada con id: " + id));
    }

    public List<Recommendation> getRecommendationsSentByUser(int senderId) {
        return recommendationRepository.findBySenderId(senderId);
    }

    public List<Recommendation> getRecommendationsReceivedByUser(int receiverId) {
        return recommendationRepository.findByReceiverId(receiverId);
    }

    @Transactional
    public Recommendation createRecommendation(Recommendation recommendation, int senderId, int receiverId) {
        AppUser sender = appUserRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario emisor no encontrado con id: " + senderId));
        AppUser receiver = appUserRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario receptor no encontrado con id: " + receiverId));

        recommendation.setSender(sender);
        recommendation.setReceiver(receiver);
        return recommendationRepository.save(recommendation);
    }

    @Transactional
    public Recommendation updateRecommendation(int id, Recommendation updatedRecommendation, Integer senderId, Integer receiverId) {
        Recommendation existingRecommendation = getRecommendationById(id);
        existingRecommendation.setMessage(updatedRecommendation.getMessage());
        existingRecommendation.setDate(updatedRecommendation.getDate());

        if (senderId != null) {
            AppUser sender = appUserRepository.findById(senderId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario emisor no encontrado con id: " + senderId));
            existingRecommendation.setSender(sender);
        }

        if (receiverId != null) {
            AppUser receiver = appUserRepository.findById(receiverId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario receptor no encontrado con id: " + receiverId));
            existingRecommendation.setReceiver(receiver);
        }

        return recommendationRepository.save(existingRecommendation);
    }

    @Transactional
    public void deleteRecommendation(int id) {
        Recommendation existingRecommendation = getRecommendationById(id);
        recommendationRepository.delete(existingRecommendation);
    }
}
