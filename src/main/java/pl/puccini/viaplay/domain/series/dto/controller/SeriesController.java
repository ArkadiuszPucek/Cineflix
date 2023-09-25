package pl.puccini.viaplay.domain.series.dto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import pl.puccini.viaplay.domain.series.service.SeriesService;

@Controller
public class SeriesController {

    private final SeriesService seriesService;

    public SeriesController(SeriesService seriesService) {
        this.seriesService = seriesService;
    }
    @GetMapping("/series")
    String series(){
        return "series";
    }
}
