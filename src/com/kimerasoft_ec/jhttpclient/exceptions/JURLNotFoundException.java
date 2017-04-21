package com.kimerasoft_ec.jhttpclient.exceptions;

/**
 *
 * @author Andres Leon
 */
public class JURLNotFoundException extends Exception{
    public JURLNotFoundException()
    {
        super("The given URL does not exist.");
    }
}
