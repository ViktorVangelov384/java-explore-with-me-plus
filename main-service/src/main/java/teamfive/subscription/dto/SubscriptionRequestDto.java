package teamfive.subscription.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class SubscriptionRequestDto {
    @NotNull
    @Positive
    private Long followingId;
}
