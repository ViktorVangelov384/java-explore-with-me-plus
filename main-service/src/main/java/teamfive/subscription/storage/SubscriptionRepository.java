package teamfive.subscription.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamfive.subscription.model.Subscription;
import teamfive.subscription.model.SubscriptionStatus;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("SELECT s FROM Subscription s JOIN FETCH s.follower JOIN FETCH s.following WHERE s.follower.id = :followerId AND s.following.id = :followingId")
    Optional<Subscription> findByFollowerIdAndFollowingId(@Param("followerId") Long followerId,
                                                          @Param("followingId") Long followingId);

    @Query("SELECT s FROM Subscription s JOIN FETCH s.following WHERE s.follower.id = :followerId AND s.status = :status")
    List<Subscription> findByFollowerIdAndStatus(@Param("followerId") Long followerId,
                                                 @Param("status") SubscriptionStatus status);

    @Query("SELECT s FROM Subscription s JOIN FETCH s.follower WHERE s.following.id = :followingId AND s.status = :status")
    List<Subscription> findByFollowingIdAndStatus(@Param("followingId") Long followingId,
                                                  @Param("status") SubscriptionStatus status);

    @Query("SELECT s FROM Subscription s WHERE s.follower.id = :followerId AND s.status = :status")
    Page<Subscription> findByFollowerIdAndStatus(@Param("followerId") Long followerId,
                                                 @Param("status") SubscriptionStatus status,
                                                 Pageable pageable);

    @Query("SELECT s FROM Subscription s WHERE s.following.id = :followingId AND s.status = :status")
    Page<Subscription> findByFollowingIdAndStatus(@Param("followingId") Long followingId,
                                                  @Param("status") SubscriptionStatus status,
                                                  Pageable pageable);

    boolean existsByFollowerIdAndFollowingIdAndStatus(Long followerId, Long followingId, SubscriptionStatus status);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.following.id = :userId AND s.status = 'ACTIVE'")
    Long countFollowersByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.follower.id = :userId AND s.status = 'ACTIVE'")
    Long countSubscriptionsByUserId(@Param("userId") Long userId);
}