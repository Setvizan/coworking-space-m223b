package com.github.setvizan.coworkingspace.exceptions;

public class MemberNotFoundException extends RuntimeException{
    public MemberNotFoundException(String message){
        super(message);
    }
}
