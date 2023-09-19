package pl.puccini.viaplay;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class homeController {
    @GetMapping("/")
    String home(){
        return "movie-listing";
    }
}
