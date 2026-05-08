package co.icesi.exercise.repositories;

import co.icesi.exercise.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findByPhysicalSpaceId(Integer physicalSpaceId);
}
