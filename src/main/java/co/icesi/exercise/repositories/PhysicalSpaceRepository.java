package co.icesi.exercise.repositories;

import co.icesi.exercise.model.PhysicalSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhysicalSpaceRepository extends JpaRepository<PhysicalSpace, Integer> {
    List<PhysicalSpace> findByNameContainingIgnoreCase(String name);
}
