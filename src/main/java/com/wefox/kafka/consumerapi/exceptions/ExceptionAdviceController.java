package com.wefox.kafka.consumerapi.exceptions;



        import com.fasterxml.jackson.core.JsonParseException;
        import com.wefox.kafka.consumerapi.model.response.TransactionMicroserviceResponse;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.http.HttpStatus;
        import org.springframework.web.bind.MethodArgumentNotValidException;
        import org.springframework.web.bind.annotation.*;
        import org.springframework.web.client.HttpClientErrorException;

        import javax.persistence.PersistenceException;
        import java.net.SocketTimeoutException;
        import static com.wefox.kafka.consumerapi.enums.ResponseCodes.*;



@Slf4j
@ResponseBody
@ControllerAdvice(annotations = RestController.class)
public class ExceptionAdviceController {

    @ExceptionHandler(NullPointerException.class)
    public TransactionMicroserviceResponse noAccessException(NullPointerException e) {
        log.error("Null Pointer exception", e);
        return new TransactionMicroserviceResponse(INTERNAL_SERVER_ERROR.getCanonicalCode(), INTERNAL_SERVER_ERROR.getDescription());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public TransactionMicroserviceResponse handleLockedException(HttpClientErrorException e) {
        log.error("Error", e);
        return new TransactionMicroserviceResponse(String.valueOf(e.getRawStatusCode()), e.getStatusText() + " " + e.getResponseBodyAsString());
    }

    @ExceptionHandler(PersistenceException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public TransactionMicroserviceResponse handleLoginException(PersistenceException e) {
        log.error("PersistenceException ", e);
        return new TransactionMicroserviceResponse(BAD_REQUEST.getCanonicalCode(), BAD_REQUEST.getDescription());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public TransactionMicroserviceResponse handleLoginException(IllegalArgumentException e) {
        log.error("IllegalArgumentException ", e);
        return new TransactionMicroserviceResponse(BAD_REQUEST.getCanonicalCode(), BAD_REQUEST.getDescription());
    }

    @ExceptionHandler(TransactionServiceException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public TransactionMicroserviceResponse jsonException(TransactionServiceException e) {
        return new TransactionMicroserviceResponse(e.getStatusCode(), e.getMessage());
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public TransactionMicroserviceResponse noAccessException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException ", e);
        return new TransactionMicroserviceResponse( BAD_REQUEST.getCanonicalCode(), "Wrong message was sent or some required fields were not provided");
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public TransactionMicroserviceResponse timeOutException(SocketTimeoutException e) {
        log.error("SocketTimeoutException ", e);
        return new TransactionMicroserviceResponse("408", "Sorry...request is taking longer than expected.. please trying again later");
    }



    @ExceptionHandler(Exception.class)
    public TransactionMicroserviceResponse noAccessException(Exception e) {
        log.error("Unknown Exception", e);
        return new TransactionMicroserviceResponse(INTERNAL_SERVER_ERROR.getCanonicalCode(), INTERNAL_SERVER_ERROR.getDescription());
    }

    @ExceptionHandler(JsonParseException.class)
    public TransactionMicroserviceResponse noAccessException(JsonParseException e) {
        log.error("Unknown Exception", e);
        return new TransactionMicroserviceResponse(UNMARSHALL_EXCEPTION.getCanonicalCode(), UNMARSHALL_EXCEPTION.getDescription());
    }
}
