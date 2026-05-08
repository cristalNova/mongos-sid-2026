package co.icesi.exercise.repositories;

import co.icesi.exercise.model.VisualSupport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisualSupportRepository extends JpaRepository<VisualSupport, Integer> {
    List<VisualSupport> findByExerciseId(Integer exerciseId);
}
