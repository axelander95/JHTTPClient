package com.kimerasoft_ec.jhttpclient.exceptions;
public class JURLErrorException extends Exception {
    private final String contentError;
    public JURLErrorException(String contentError) {
        super("There was an error in the server side.");
        this.contentError = contentError;
    }

    public String getContentError() {
        return contentError;
    }
    
}
