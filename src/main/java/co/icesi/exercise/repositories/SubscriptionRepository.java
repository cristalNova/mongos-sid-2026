package co.icesi.exercise.repositories;

import co.icesi.exercise.model.Subscription;
import co.icesi.exercise.model.SubscriptionPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionPK> {
    List<Subscription> findByUserId(Integer userId);
    List<Subscription> findByEventId(Integer eventId);
}
