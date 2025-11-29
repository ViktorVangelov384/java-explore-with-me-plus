package teamfive.subscription.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import teamfive.user.dto.UserShortDto;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
public class SubscriptionDto {
    private long id;
    private UserShortDto follower;
    private UserShortDto following;
    private String status;
    private LocalDateTime createdOn;
}
