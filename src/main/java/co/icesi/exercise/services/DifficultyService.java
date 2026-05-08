package co.icesi.exercise.services;

import co.icesi.exercise.model.Difficulty;
import co.icesi.exercise.repositories.DifficultyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DifficultyService {
    @Autowired
    private DifficultyRepository difficultyRepository;

    public List<Difficulty> getAllDifficulties() {
        return difficultyRepository.findAll();
    }

    public Difficulty getDifficultyById(int id) {
        return difficultyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dificultad no encontrada con id: " + id));
    }

    @Transactional
    public Difficulty createDifficulty(Difficulty difficulty) {
        return difficultyRepository.save(difficulty);
    }

    @Transactional
    public Difficulty updateDifficulty(int id, Difficulty updatedDifficulty) {
        Difficulty existingDifficulty = getDifficultyById(id);
        existingDifficulty.setDifficultyName(updatedDifficulty.getDifficultyName());
        return difficultyRepository.save(existingDifficulty);
    }

    @Transactional
    public void deleteDifficulty(int id) {
        Difficulty existingDifficulty = getDifficultyById(id);
        difficultyRepository.delete(existingDifficulty);
    }
}
