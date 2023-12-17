package pl.puccini.cineflix.domain;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MediaUtils {

    public String extractVideoId(String youtubeUrl) {
        Pattern pattern = Pattern.compile("https?://www\\.youtube\\.com/watch\\?v=([\\w-]+)");
        Matcher matcher = pattern.matcher(youtubeUrl);

        if (matcher.find()) {
            return "https://www.youtube.com/embed/" + matcher.group(1);
        }

        pattern = Pattern.compile("https?://youtu\\.be/([\\w-]+)");
        matcher = pattern.matcher(youtubeUrl);
        if (matcher.find()) {
            return "https://www.youtube.com/embed/" + matcher.group(1);
        }

        return null;
    }
}
