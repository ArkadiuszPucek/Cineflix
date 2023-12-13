package pl.puccini.cineflix.domain.imdb;

import org.springframework.stereotype.Component;

@Component
public class IMDbApiUrlBuilder {

    public String buildMovieDataBaseListAPIUrl(String imdbId) {
        return "https://mdblist.p.rapidapi.com/?i=" + imdbId;
    }
    public String buildMovieDatabaseAlternativeAPIUrl(String imdbId) {
        return "https://movie-database-alternative.p.rapidapi.com/?r=json&i=" + imdbId;
    }
    public String buildOverDetailsIMDbAPIUrl(String imdbId) {
        return "https://imdb8.p.rapidapi.com/title/get-overview-details?tconst=" + imdbId + "&currentCountry=US";
    }
    public String buildGetSeasonsIMDbAPIUrl(String imdbId) {
        return "https://imdb8.p.rapidapi.com/title/get-seasons?tconst=" + imdbId;
    }
    public String buildAutoCompleteIMDbAPIUrl(String imdbId) {
        return "https://imdb8.p.rapidapi.com/auto-complete?q=" + imdbId;
    }
    public String buildGetDetailsIMDbAPIUrl(String episodeImdbId) {
        return "https://imdb8.p.rapidapi.com/title/get-details?tconst=" + episodeImdbId;
    }
    public String getSeasonsIMDbAPIUrl(String imdbId) {
        return "https://imdb8.p.rapidapi.com/title/get-seasons?tconst=" + imdbId;

    }
}
