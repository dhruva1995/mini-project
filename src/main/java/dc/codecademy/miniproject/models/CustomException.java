package dc.codecademy.miniproject.models;

import org.springframework.http.HttpStatus;

import lombok.Getter;

public class CustomException extends Exception {

    @Getter
    private final HttpStatus statusCode;

    public CustomException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

}
