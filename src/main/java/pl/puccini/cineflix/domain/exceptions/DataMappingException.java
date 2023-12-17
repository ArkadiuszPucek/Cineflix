package pl.puccini.cineflix.domain.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;

public class DataMappingException extends RuntimeException {
    public DataMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
