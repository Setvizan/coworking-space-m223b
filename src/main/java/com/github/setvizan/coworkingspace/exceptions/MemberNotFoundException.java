package com.github.setvizan.coworkingspace.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus()
public class MemberNotFoundException extends RuntimeException{
    public MemberNotFoundException(String message){
        super(message);
    }
}
