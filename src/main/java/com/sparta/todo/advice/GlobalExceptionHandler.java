package com.sparta.todo.advice;

import com.sparta.todo.dto.response.ErrorResponse;
import com.sparta.todo.exception.ApiException;
import com.sparta.todo.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;

import static com.sparta.todo.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.sparta.todo.exception.ErrorCode.INVALID_VALUE;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     * [Exception] RuntimeException 반환하는 경우
     *
     * @param ex RuntimeException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeExceptionHandler(RuntimeException ex){
        log.error("Runtime exceptions:", ex);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR.getHttpStatus()).body(
                ErrorResponse.builder()
                        .status(INTERNAL_SERVER_ERROR.getHttpStatus().value())
                        .name(INTERNAL_SERVER_ERROR.name())
                        .message(INTERNAL_SERVER_ERROR.getDetail())
                        .build()
        );
    }


    /**
     * [Exception] API 요청 시 '객체' 혹은 '파라미터' 데이터 값이 유효하지 않은 경우
     *
     * @param ex MethodArgumentNotValidException,
     * @return ResponseEntity<ErrorResponse>
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("handleMethodArgumentNotValidException", ex);
        BindingResult bindingResult = ex.getBindingResult();
        HashMap<String, String> errors = new HashMap<>();
        bindingResult.getAllErrors().forEach(error -> errors.put(((FieldError) error).getField(), error.getDefaultMessage()));

        return ResponseEntity.status(INVALID_VALUE.getHttpStatus()).body(
                ErrorResponse.builder()
                        .status(INVALID_VALUE.getHttpStatus().value())
                        .name(INVALID_VALUE.name())
                        .message(INVALID_VALUE.getDetail())
                        .data(errors)
                        .build()
        );
    }

    /**
     * [Exception] API 요청에 맞는 파라미터를 받지 못한 경우
     *
     * @param ex MissingServletRequestParameterException,
     * @return ResponseEntity<ErrorResponse>
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .status(INVALID_VALUE.getHttpStatus().value())
                        .name(HttpStatus.BAD_REQUEST.name())
                        .message("잘못된 요청입니다." + "누락된 파라미터: " + ex.getParameterName())

                        .build()
        );
    }

    /**
     * [Exception] API 호출 시 CustomException 으로 정의한 예외가 반환되는 경우
     *
     * @param ex MethodArgumentNotValidException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(value = {ApiException.class})
    protected ResponseEntity<Object> handleCustomException(ApiException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        String message = ex.getMessage();
        if(!StringUtils.hasText(message)) {
            message = errorCode.getDetail();
        }
        log.error("handleCustomException throw CustomException : {}", errorCode);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(
                ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .name(errorCode.name())
                        .message(message)
                        .build()
        );
    }
}