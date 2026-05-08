package co.icesi.exercise.services;

import co.icesi.exercise.model.Exercise;
import co.icesi.exercise.model.VisualSupport;
import co.icesi.exercise.repositories.ExerciseRepository;
import co.icesi.exercise.repositories.VisualSupportRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VisualSupportService {

    @Autowired
    private VisualSupportRepository visualSupportRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;

    public List<VisualSupport> getAllVisualSupports() {
        return visualSupportRepository.findAll();
    }

    public VisualSupport getVisualSupportById(int id) {
        return visualSupportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Apoyo visual no encontrado con id: " + id));
    }

    public List<VisualSupport> getVisualSupportsByExerciseId(int exerciseId) {
        return visualSupportRepository.findByExerciseId(exerciseId);
    }

    @Transactional
    public VisualSupport createVisualSupport(VisualSupport visualSupport, int exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException("Ejercicio no encontrado con id: " + exerciseId));
        visualSupport.setExercise(exercise);
        return visualSupportRepository.save(visualSupport);
    }

    @Transactional
    public VisualSupport updateVisualSupport(int id, VisualSupport updatedVisualSupport, Integer exerciseId) {
        VisualSupport existingVisualSupport = getVisualSupportById(id);
        existingVisualSupport.setSupportType(updatedVisualSupport.getSupportType());
        existingVisualSupport.setUrl(updatedVisualSupport.getUrl());

        if (exerciseId != null) {
            Exercise exercise = exerciseRepository.findById(exerciseId)
                    .orElseThrow(() -> new EntityNotFoundException("Ejercicio no encontrado con id: " + exerciseId));
            existingVisualSupport.setExercise(exercise);
        }

        return visualSupportRepository.save(existingVisualSupport);
    }

    @Transactional
    public void deleteVisualSupport(int id) {
        VisualSupport existingVisualSupport = getVisualSupportById(id);
        visualSupportRepository.delete(existingVisualSupport);
    }
}
