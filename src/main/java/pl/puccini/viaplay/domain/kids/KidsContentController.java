package pl.puccini.viaplay.domain.kids;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/kids")
public class KidsContentController {
    private final KidsContentService kidsContentService;

    public KidsContentController(KidsContentService kidsContentService) {
        this.kidsContentService = kidsContentService;
    }

    @GetMapping
    public String getAllKidsContent(Model model){
        model.addAttribute("movies", kidsContentService.getAllKidsMovies());
        model.addAttribute("series", kidsContentService.getAllKidsSeries());
        return "kids";
    }


}
