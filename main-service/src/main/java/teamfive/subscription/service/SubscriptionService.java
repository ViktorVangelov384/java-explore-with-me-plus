package teamfive.subscription.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import teamfive.subscription.dto.SubscriptionDto;
import teamfive.subscription.dto.SubscriptionRequestDto;

import java.util.List;

public interface SubscriptionService {

    SubscriptionDto subscribe(Long followerId, SubscriptionRequestDto requestDto);

    void unsubscribe(Long followerId, Long followingId);

    List<SubscriptionDto> getUserSubscriptions(Long userId);

    List<SubscriptionDto> getUserFollowers(Long userId);

    Page<SubscriptionDto> getUserSubscriptions(Long userId, Pageable pageable);

    Page<SubscriptionDto> getUserFollowers(Long userId, Pageable pageable);

    boolean isSubscribed(Long followerId, Long followingId);

    Long getSubscriptionsCount(Long userId);

    Long getFollowersCount(Long userId);
}