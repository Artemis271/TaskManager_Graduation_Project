package com.artemis.projectservice.util.exception;

public class AccessDeniedException extends RuntimeException
{
    public AccessDeniedException(String message)
    {
        super(message);
    }
}
