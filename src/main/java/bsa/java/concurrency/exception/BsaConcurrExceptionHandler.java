package bsa.java.concurrency.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class BsaConcurrExceptionHandler {

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        log.error("BsaConcurrExceptionHandler intercepted NullPointerException: " + ex.getMessage());
        return new ResponseEntity<>(Map.of("reson", "Received id is incorrect!"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HorizontalHashCalculatingException.class)
    public ResponseEntity<?> handleCalculatingHashException(HorizontalHashCalculatingException ex) {
        log.error("BsaConcurrExceptionHandler intercepted HorizontalHashCalculatingException: " + ex.getMessage());
        return new ResponseEntity<>(Map.of("reason", "Error of hash calculating "), HttpStatus.FORBIDDEN);
    }
}
