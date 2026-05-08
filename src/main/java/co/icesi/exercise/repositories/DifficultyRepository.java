package co.icesi.exercise.repositories;

import co.icesi.exercise.model.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DifficultyRepository extends JpaRepository<Difficulty, Integer> {
}
