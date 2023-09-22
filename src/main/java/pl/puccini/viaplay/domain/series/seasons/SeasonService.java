package pl.puccini.viaplay.domain.series.seasons;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeasonService {
    private final SeasonRepository seasonRepository;

    public SeasonService(SeasonRepository seasonRepository) {
        this.seasonRepository = seasonRepository;
    }

    public List<Season> getAllSeasons(){
        return seasonRepository.findAllBy();
    }
}
