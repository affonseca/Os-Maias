package com.mobilelearning.maias.serviceHandling;

/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 26-01-2015
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class RequestException extends Exception {

    public RequestException() {}

    //Constructor that accepts a message
    public RequestException(String message)
    {
        super(message);
    }

}
