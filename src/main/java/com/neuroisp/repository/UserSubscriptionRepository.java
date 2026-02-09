package com.neuroisp.repository;

import com.neuroisp.entity.SubscriptionStatus;
import com.neuroisp.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSubscriptionRepository
        extends JpaRepository<UserSubscription, String> {

    Optional<UserSubscription> findByUsername(String username);
    List<UserSubscription> findByStatus(SubscriptionStatus status);

}
