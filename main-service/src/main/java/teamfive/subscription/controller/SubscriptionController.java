package teamfive.subscription.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import teamfive.subscription.dto.SubscriptionDto;
import teamfive.subscription.dto.SubscriptionRequestDto;
import teamfive.subscription.service.SubscriptionService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/subscriptions")
@Validated
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto subscribe(@PathVariable @Positive Long userId,
                                     @Valid @RequestBody SubscriptionRequestDto requestDto,
                                     @RequestHeader("X-User-Id") Long currentUserId) {

        if (!userId.equals(currentUserId)) {
            throw new SecurityException("Недостаточно прав для создания подписки от чужого имени");
        }

        log.info("POST: Создание подписки пользователем ID: {} на пользователя ID: {}",
                userId, requestDto.getFollowingId());

        SubscriptionDto subscription = subscriptionService.subscribe(userId, requestDto);

        log.info("Подписка успешно создана с ID: {}", subscription.getId());

        return subscription;
    }

    @DeleteMapping("/{targetUserId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable @Positive Long userId,
                            @PathVariable @Positive Long targetUserId,
                            @RequestHeader("X-User-Id") Long currentUserId) {

        if (!userId.equals(currentUserId)) {
            throw new SecurityException("Недостаточно прав для отмены чужой подписки");
        }

        log.info("DELETE: Отмена подписки пользователем ID: {} на пользователя ID: {}",
                userId, targetUserId);

        subscriptionService.unsubscribe(userId, targetUserId);

        log.info("Подписка успешно отменена");
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SubscriptionDto> getSubscriptions(
            @PathVariable @Positive Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {

        log.info("GET: Получение подписок пользователя ID: {} с параметрами: from={}, size={}",
                userId, from, size);

        Sort sort = Sort.by(Sort.Direction.DESC, "createdOn");
        PageRequest pageable = PageRequest.of(from, size, sort);

        List<SubscriptionDto> subscriptions = subscriptionService
                .getUserSubscriptions(userId, pageable)
                .toList();

        log.info("Возвращено {} подписок", subscriptions.size());

        return subscriptions;
    }

    @GetMapping("/followers")
    @ResponseStatus(HttpStatus.OK)
    public List<SubscriptionDto> getFollowers(
            @PathVariable @Positive Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {

        log.info("GET: Получение подписчиков пользователя ID: {} с параметрами: from={}, size={}",
                userId, from, size);

        Sort sort = Sort.by(Sort.Direction.DESC, "createdOn");
        PageRequest pageable = PageRequest.of(from, size, sort);

        List<SubscriptionDto> followers = subscriptionService
                .getUserFollowers(userId, pageable)
                .toList();

        log.info("Возвращено {} подписчиков", followers.size());

        return followers;
    }

    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    public SubscriptionCountResponse getCounts(@PathVariable @Positive Long userId) {
        log.info("GET: Получение счетчиков подписок для пользователя ID: {}", userId);

        Long subscriptionsCount = subscriptionService.getSubscriptionsCount(userId);
        Long followersCount = subscriptionService.getFollowersCount(userId);

        log.info("Подписки: {}, Подписчики: {}", subscriptionsCount, followersCount);

        return new SubscriptionCountResponse(subscriptionsCount, followersCount);
    }

    @GetMapping("/check/{targetUserId}")
    @ResponseStatus(HttpStatus.OK)
    public SubscriptionCheckResponse checkSubscription(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long targetUserId) {

        log.info("GET: Проверка подписки пользователя ID: {} на пользователя ID: {}",
                userId, targetUserId);

        boolean isSubscribed = subscriptionService.isSubscribed(userId, targetUserId);

        log.info("Подписка существует: {}", isSubscribed);

        return new SubscriptionCheckResponse(isSubscribed);
    }

    public record SubscriptionCountResponse(Long subscriptions, Long followers) {}
    public record SubscriptionCheckResponse(Boolean isSubscribed) {}
}