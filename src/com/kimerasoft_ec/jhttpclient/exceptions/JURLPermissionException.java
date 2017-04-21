package com.kimerasoft_ec.jhttpclient.exceptions;
public class JURLPermissionException extends Exception {
    public JURLPermissionException() {
        super("You do not have permissions to access to this content.");
    }
}
