package org.ia.televisionspecs;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class TelevisionNotFoundAdvice {

    @ExceptionHandler(TelevisionException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String productNotFound(TelevisionException exception) {
        return exception.s;
    }

}
