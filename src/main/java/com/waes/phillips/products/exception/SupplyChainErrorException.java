package com.waes.phillips.products.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SupplyChainErrorException extends RuntimeException {

    private String errorMessageDetail;
    private HttpStatus httpStatus;
    private String id;

    SupplyChainErrorException(String errorMessageDetail, HttpStatus httpStatus) {
        super(errorMessageDetail);
        this.errorMessageDetail = errorMessageDetail;
        this.httpStatus = httpStatus;
    }

    public SupplyChainErrorException(String errorMessageDetail) {
        this(errorMessageDetail, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public SupplyChainErrorException(String errorMessageDetail, String id) {
        this(errorMessageDetail, HttpStatus.INTERNAL_SERVER_ERROR);
        this.id = id;
    }

}
