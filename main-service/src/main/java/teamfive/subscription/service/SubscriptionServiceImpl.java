package teamfive.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamfive.exception.AlreadySubscribedException;
import teamfive.exception.NotFoundException;
import teamfive.exception.SelfSubscriptionException;
import teamfive.subscription.dto.SubscriptionDto;
import teamfive.subscription.dto.SubscriptionRequestDto;
import teamfive.subscription.mapper.SubscriptionMapper;
import teamfive.subscription.model.Subscription;
import teamfive.subscription.model.SubscriptionStatus;
import teamfive.subscription.storage.SubscriptionRepository;
import teamfive.user.model.User;
import teamfive.user.repository.UserRepository;
import teamfive.user.service.UserService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SubscriptionDto subscribe(Long followerId, SubscriptionRequestDto requestDto) {
        log.info("Создание подписки: followerId={}, followingId={}",
                followerId, requestDto.getFollowingId());

        Long followingId = requestDto.getFollowingId();

        if (!userService.existsById(followerId)) {
            throw new NotFoundException("Пользователь-подписчик не найден");
        }
        if (!userService.existsById(followingId)) {
            throw new NotFoundException("Пользователь для подписки не найден");
        }

        if (followerId.equals(followingId)) {
            throw new SelfSubscriptionException("Невозможно подписаться на самого себя. User ID: " + followerId);
        }

        if (subscriptionRepository.existsByFollowerIdAndFollowingIdAndStatus(
                followerId, followingId, SubscriptionStatus.ACTIVE)) {
            throw new AlreadySubscribedException("Подписка уже существует. Follower ID: " + followerId + ", Following ID: " + followingId);
        }


        Optional<Subscription> existingSubscription =
                subscriptionRepository.findByFollowerIdAndFollowingId(followerId, followingId);

        Subscription subscription;
        if (existingSubscription.isPresent()) {
            subscription = existingSubscription.get();
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            log.info("Восстановление существующей подписки с ID: {}", subscription.getId());
        } else {
            User follower = userRepository.getReferenceById(followerId);
            User following = userRepository.getReferenceById(followingId);

            subscription = Subscription.builder()
                    .follower(follower)
                    .following(following)
                    .status(SubscriptionStatus.ACTIVE)
                    .build();

            log.info("Создание новой подписки");
        }

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        log.info("Подписка успешно создана/восстановлена с ID: {}", savedSubscription.getId());

        return subscriptionMapper.toDto(savedSubscription);
    }

    @Override
    @Transactional
    public void unsubscribe(Long followerId, Long followingId) {
        log.info("Отмена подписки: followerId={}, followingId={}", followerId, followingId);

        if (!userService.existsById(followerId)) {
            throw new NotFoundException("Пользователь-подписчик не найден");
        }
        if (!userService.existsById(followerId)) {
            throw new NotFoundException("Пользователь-подписчик не найден");
        }

        Subscription subscription = subscriptionRepository
                .findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Подписка пользователя %d на пользователя %d не найдена",
                                followerId, followingId)
                ));

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);
        subscriptionRepository.flush();

        log.info("Подписка успешно отменена с ID: {}", subscription.getId());
    }

    @Override
    public List<SubscriptionDto> getUserSubscriptions(Long userId) {
        log.info("Получение списка подписок пользователя с ID: {}", userId);

        List<Subscription> subscriptions = subscriptionRepository
                .findByFollowerIdAndStatus(userId, SubscriptionStatus.ACTIVE);

        log.info("Найдено {} подписок для пользователя с ID: {}", subscriptions.size(), userId);

        return subscriptionMapper.toDtoList(subscriptions);
    }

    @Override
    public List<SubscriptionDto> getUserFollowers(Long userId) {
        log.info("Получение списка подписчиков пользователя с ID: {}", userId);

        List<Subscription> followers = subscriptionRepository
                .findByFollowingIdAndStatus(userId, SubscriptionStatus.ACTIVE);

        log.info("Найдено {} подписчиков для пользователя с ID: {}", followers.size(), userId);

        return subscriptionMapper.toDtoList(followers);
    }

    @Override
    public Page<SubscriptionDto> getUserSubscriptions(Long userId, Pageable pageable) {
        log.info("Получение подписок пользователя с ID: {} с пагинацией: {}", userId, pageable);

        Page<Subscription> subscriptions = subscriptionRepository
                .findByFollowerIdAndStatus(userId, SubscriptionStatus.ACTIVE, pageable);

        log.info("Найдено {} подписок для пользователя с ID: {}", subscriptions.getTotalElements(), userId);

        return subscriptionMapper.toDtoPage(subscriptions);
    }

    @Override
    public Page<SubscriptionDto> getUserFollowers(Long userId, Pageable pageable) {
        log.info("Получение подписчиков пользователя с ID: {} с пагинацией: {}", userId, pageable);

        Page<Subscription> followers = subscriptionRepository
                .findByFollowingIdAndStatus(userId, SubscriptionStatus.ACTIVE, pageable);

        log.info("Найдено {} подписчиков для пользователя с ID: {}", followers.getTotalElements(), userId);

        return subscriptionMapper.toDtoPage(followers);
    }

    @Override
    public boolean isSubscribed(Long followerId, Long followingId) {
        log.debug("Проверка подписки: followerId={} на followingId={}", followerId, followingId);

        boolean isSubscribed = subscriptionRepository
                .existsByFollowerIdAndFollowingIdAndStatus(followerId, followingId, SubscriptionStatus.ACTIVE);

        log.debug("Подписка существует: {}", isSubscribed);

        return isSubscribed;
    }

    @Override
    public Long getSubscriptionsCount(Long userId) {
        log.debug("Получение количества подписок пользователя с ID: {}", userId);

        Long count = subscriptionRepository.countSubscriptionsByUserId(userId);

        log.debug("Количество подписок: {}", count);

        return count;
    }

    @Override
    public Long getFollowersCount(Long userId) {
        log.debug("Получение количества подписчиков пользователя с ID: {}", userId);

        Long count = subscriptionRepository.countFollowersByUserId(userId);

        log.debug("Количество подписчиков: {}", count);

        return count;
    }
}
