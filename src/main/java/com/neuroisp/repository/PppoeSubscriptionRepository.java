package com.neuroisp.repository;

import com.neuroisp.dto.PppoeUserViewDTO;
import com.neuroisp.entity.PppoeSubscription;
import com.neuroisp.entity.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PppoeSubscriptionRepository
        extends JpaRepository<PppoeSubscription, String> {
    List<PppoeSubscription> findByStatusAndExpiryTimeBefore(
            SubscriptionStatus status,
            LocalDateTime time
    );

    @Query("""
    SELECT new com.neuroisp.dto.PppoeUserViewDTO(
        u.id,
        u.username,
        u.fullName,
        u.phoneNumber,
        p.name,
        s.status
    )
    FROM PppoeUser u
    LEFT JOIN PppoeSubscription s
        ON s.user = u
        AND s.status IN ('ACTIVE', 'PENDING', 'EXPIRED')
    LEFT JOIN s.pppoePackage p
""")
    List<PppoeUserViewDTO> fetchUsersWithSubscription();

    List<PppoeSubscription> findByUserIdAndStatusIn(
            String userId,
            List<SubscriptionStatus> statuses
    );
    List<PppoeSubscription> findByUserIdAndStatusInOrderByStartTimeAsc(
            String userId,
            List<SubscriptionStatus> statuses
    );
  
    Optional<PppoeSubscription> findTopByUserIdOrderByExpiryTimeDesc(String userId);

    Optional<Object> findTopByUserIdOrderByStartTimeDesc(String userId);
}
