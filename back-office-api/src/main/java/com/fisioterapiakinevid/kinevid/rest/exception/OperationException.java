package com.fisioterapiakinevid.kinevid.rest.exception;

public class OperationException extends RuntimeException
{

    private static final long serialVersionUID = 10316048604864L;


    public OperationException(String mensaje)
    {
        super(mensaje);
    }


    public OperationException(String mensaje, Throwable e)
    {
        super(mensaje, e);
    }

}
