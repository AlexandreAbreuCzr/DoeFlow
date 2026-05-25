package com.alexandre.doeflow.infra.exceptions;

import com.alexandre.doeflow.dto.exception.ApiErrorDTO;
import com.alexandre.doeflow.infra.exceptions.campaign.CampaignAlreadyExistsException;
import com.alexandre.doeflow.infra.exceptions.campaign.CampaignNotFoundException;
import com.alexandre.doeflow.infra.exceptions.campaign.InvalidCampaignOperationException;
import com.alexandre.doeflow.infra.exceptions.donation.DonationNotFoundException;
import com.alexandre.doeflow.infra.exceptions.donation.InvalidDonationException;
import com.alexandre.doeflow.infra.exceptions.user.UserNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Locale;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CampaignNotFoundException.class)
    private ResponseEntity<ApiErrorDTO> campaignNotFoundException(CampaignNotFoundException exception){
        return buildError(HttpStatus.NOT_FOUND, "NOT FOUND", exception.getMessage());
    }

    @ExceptionHandler(CampaignAlreadyExistsException.class)
    private ResponseEntity<ApiErrorDTO> campaignAlreadyExistsException(CampaignAlreadyExistsException exception){
        return buildError(HttpStatus.CONFLICT, "CONFLICT", exception.getMessage());
    }

    @ExceptionHandler(InvalidCampaignOperationException.class)
    private ResponseEntity<ApiErrorDTO> invalidCampaignOperationException(InvalidCampaignOperationException exception){
        return buildError(HttpStatus.CONFLICT, "CONFLICT", exception.getMessage());
    }

    @ExceptionHandler(DonationNotFoundException.class)
    private ResponseEntity<ApiErrorDTO> donationNotFoundException(DonationNotFoundException exception){
        return buildError(HttpStatus.NOT_FOUND, "NOT FOUND", exception.getMessage());
    }

    @ExceptionHandler(InvalidDonationException.class)
    private ResponseEntity<ApiErrorDTO> invalidDonationException(InvalidDonationException exception){
        return buildError(HttpStatus.BAD_REQUEST, "BAD REQUEST", exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<ApiErrorDTO> userNotFoundException(UserNotFoundException exception){
        return buildError(HttpStatus.NOT_FOUND, "NOT FOUND", exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<ApiErrorDTO> illegalArgumentException(IllegalArgumentException exception){
        return buildError(HttpStatus.BAD_REQUEST, "BAD REQUEST", exception.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    private ResponseEntity<ApiErrorDTO> responseStatusException(ResponseStatusException exception) {
        HttpStatusCode status = exception.getStatusCode();
        String message = exception.getReason() == null ? exception.getMessage() : exception.getReason();
        return buildError(status, resolveError(status), message);
    }

    @ExceptionHandler(BadCredentialsException.class)
    private ResponseEntity<ApiErrorDTO> badCredentialsException() {
        return buildError(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "E-mail ou senha invalidos");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Invalid request body");

        ApiErrorDTO error = new ApiErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                message
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private ResponseEntity<ApiErrorDTO> buildError(HttpStatusCode status, String error, String message) {
        ApiErrorDTO apiError = new ApiErrorDTO(
                status.value(),
                error,
                message
        );

        return ResponseEntity.status(status).body(apiError);
    }

    private String resolveError(HttpStatusCode status) {
        HttpStatus httpStatus = HttpStatus.resolve(status.value());

        if (httpStatus == null) {
            return "ERROR";
        }

        return httpStatus.getReasonPhrase().toUpperCase(Locale.ROOT);
    }
}
