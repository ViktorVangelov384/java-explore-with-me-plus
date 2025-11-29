package teamfive.subscription.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
public class SubscriptionResponseDto {

    private List<SubscriptionDto> subscriptions;
    private Long totalCount;
}
