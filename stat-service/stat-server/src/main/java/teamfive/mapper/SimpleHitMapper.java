package teamfive.mapper;

import dto.InputHitDto;
import dto.OutHitDto;
import dto.StatDto;
import org.mapstruct.Mapper;
import teamfive.model.Hit;
import teamfive.model.StatHit;

@Mapper(componentModel = "spring")
public interface SimpleHitMapper {
    Hit dtoToHit(InputHitDto inputHitDto);

    OutHitDto hitToDto(Hit hit);

    StatDto statHitToStatDto(StatHit statHit);
}
