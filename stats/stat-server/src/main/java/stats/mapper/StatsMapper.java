package stats.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.StatDto;
import stats.model.Stats;

@Mapper(componentModel = "spring")
public interface StatsMapper {

    StatDto toDto(Stats stats);

    Stats toEntity(StatDto statDto);
}
