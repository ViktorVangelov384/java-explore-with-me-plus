package teamfive.service;

import dto.InputHitDto;
import dto.OutHitDto;
import dto.StatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamfive.mapper.SimpleHitMapper;
import teamfive.model.Hit;
import teamfive.model.StatHit;
import teamfive.storage.StatRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;
    private final SimpleHitMapper mapper;

    @Override
    public List<StatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (uris != null && uris.isEmpty()) {
            return Collections.emptyList();
        }
        List<StatHit> statHitList;
        if (unique) {
            statHitList = statRepository.getUniqueStat(start, end, uris);
        } else {
            statHitList = statRepository.getNonUniqueStat(start, end, uris);
        }
        return statHitList.stream().map(mapper::statHitToStatDto)
                .collect(Collectors.toList());
    }

    @Override
    public OutHitDto createHit(InputHitDto inputHitDto) {
        Hit hit = mapper.dtoToHit(inputHitDto);
        return mapper.hitToDto(statRepository.save(hit));
    }
}
