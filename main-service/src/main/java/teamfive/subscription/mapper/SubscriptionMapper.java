package teamfive.subscription.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;
import teamfive.subscription.dto.SubscriptionDto;
import teamfive.subscription.model.Subscription;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubscriptionMapper {

    @Mapping(source = "follower", target = "follower")
    @Mapping(source = "following", target = "following")
    @Mapping(source = "status", target = "status")
    SubscriptionDto toDto(Subscription subscription);

    List<SubscriptionDto> toDtoList(List<Subscription> subscriptions);

    default Page<SubscriptionDto> toDtoPage(Page<Subscription> subscriptions) {
        return subscriptions.map(this::toDto);
    }
}
