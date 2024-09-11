package com.Unicor_Ads_2.Unicor_Ads_2.demo.service.exception;

public class ResourceAlreadyExistsException extends  RuntimeException{
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
