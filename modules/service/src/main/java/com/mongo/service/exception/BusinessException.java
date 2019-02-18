package com.up.service.exception;

/**
 * 
 * @author Marcos Rachid
 *
 */
public class BusinessException extends Exception {

    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }

}