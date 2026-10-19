package com.pie.demo.exceptions;

public class IsbnAlreadyExistsException extends RuntimeException {

    public IsbnAlreadyExistsException(String isbn) {
        super(String.format("ISBN già esistente: %s", isbn));
    }

}
