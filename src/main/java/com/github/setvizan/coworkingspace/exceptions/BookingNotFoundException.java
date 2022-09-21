package com.github.setvizan.coworkingspace.exceptions;

public class BookingNotFoundException extends RuntimeException{
    public BookingNotFoundException(String message) {
        super(message);
    }
}
