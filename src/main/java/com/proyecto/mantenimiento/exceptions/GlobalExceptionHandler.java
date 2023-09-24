package com.proyecto.mantenimiento.exceptions;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.proyecto.mantenimiento.exceptions.customs.CredencialesErroneas;
import com.proyecto.mantenimiento.exceptions.customs.TokenNoValidoException;
import com.proyecto.mantenimiento.exceptions.customs.UsuarioEnUsoException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse("Problemas con el metodo de la request", ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsuarioEnUsoException.class)
    @ResponseBody
    protected ResponseEntity<Object> handleUsuarioEnUsoException(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse("El usuario ya esta en uso. Intente otro.", ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CredencialesErroneas.class)
    @ResponseBody
    protected ResponseEntity<Object> handleBadCredentials(CredencialesErroneas ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse("El usuario o contrasenia son incorrectos.", ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseBody
    protected ResponseEntity<ExceptionResponse> handleExpiredExcetion(TokenExpiredException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse("El Token ya ha expirado.", ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenNoValidoException.class)
    @ResponseBody
    protected ResponseEntity<ExceptionResponse> handleTokenNoValidoException(TokenNoValidoException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse("El Token no es valido o ha sido modificado.", ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JWTDecodeException.class)
    @ResponseBody
    protected ResponseEntity<ExceptionResponse> handleJWTDecodeException(JWTDecodeException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse("El Token no es valido o ha sido modificado.", ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    protected ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse("Campos no validos.", ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }


}
