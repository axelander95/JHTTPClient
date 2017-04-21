package com.kimerasoft_ec.jhttpclient;

import com.kimerasoft_ec.jhttpclient.exceptions.JURLErrorException;
import com.kimerasoft_ec.jhttpclient.exceptions.JURLNotFoundException;
import com.kimerasoft_ec.jhttpclient.exceptions.JURLPermissionException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;

/*
 * @author Andres Leon
 * JHttpRequest is a class which can send a HTTP request to any server
 */
public class JHttpRequest {
    private String URLRequest;
    private URL url;
    private HttpURLConnection httpCon;
    private HttpsURLConnection httpsCon;
    private URLConnection con;
    private final Logger log;
    public JHttpRequest(String URLRequest)
    {
        this.URLRequest = URLRequest;
        log = Logger.getLogger(JHttpRequest.class.getName());
        log.log(Level.INFO, "JHttpRequest was initialized");
    }
    
    private boolean isHTTPS()
    {
        return URLRequest.contains("https");
    }
    public String getURLRequest() {
        return URLRequest;
    }

    public void setURLRequest(String URLRequest) {
        this.URLRequest = URLRequest;
    }
    
    private String readFromStream(InputStream stream) throws IOException
    {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while((line = reader.readLine()) != null)
            builder.append(line);
        return builder.toString();
    }
    
    public String sendRawRequest(String method, ArrayList<JHttpParameter> headers, String raw) throws MalformedURLException, ProtocolException, IOException, JURLNotFoundException, JURLPermissionException, JURLErrorException
    {
        url = new URL(URLRequest);
        if (isHTTPS())
        {
            httpsCon = (HttpsURLConnection) url.openConnection();
            httpsCon.setRequestMethod(method);
            con = httpsCon;
            log.log(Level.INFO, "The connection has been determined as HTTP");
        }
        else 
        {
            httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setRequestMethod(method);
            con = httpCon;
            log.log(Level.INFO, "The connection has been determined as HTTPS");
        }
        setHeaders(headers);
        log.log(Level.INFO, "The headers have been set");
        if (raw != null)
        {
            con.setDoOutput(true);
            try (DataOutputStream output = new DataOutputStream(con.getOutputStream())) {
                output.writeUTF(raw);
                output.flush();
            }
            log.log(Level.INFO, "The params have been written");
        }
        int responseCode = (isHTTPS())?httpsCon.getResponseCode():httpCon.getResponseCode();
        switch (responseCode)
        {
            case 200:
            case 201:
                log.log(Level.FINEST, "There was success in the request");
                return readFromStream(con.getInputStream());
            case 404:
                log.log(Level.WARNING, "Response code was 404 Not found");
                throw(new JURLNotFoundException());
            case 403:
                log.log(Level.WARNING, "Response code was 403, you are not allowed to access");
                throw(new JURLPermissionException());
            case 500:
                log.log(Level.SEVERE, "Response code was 500, there was an error in server");
                throw(new JURLErrorException(readFromStream((isHTTPS())?httpsCon.getErrorStream():httpCon.getErrorStream())));
        }
        return null;
    }
    
    public String send(String method, ArrayList<JHttpParameter> headers, 
            ArrayList<JHttpParameter> parameters) throws MalformedURLException, IOException, JURLNotFoundException, JURLPermissionException, JURLErrorException
    {
        return sendRawRequest(method, headers, getParametersAsString(parameters));
    }
    
    private void setHeaders(ArrayList<JHttpParameter> headers)
    {
        if (con != null)
        {
            headers.forEach((header) -> {
                con.addRequestProperty(header.getKey(), header.getValue());
            });
        }
    }
    
    private String getParametersAsString(ArrayList<JHttpParameter> parameters)
    {
        if (parameters != null && parameters.size() > 0)
        {
            StringBuilder builder = new StringBuilder();
            parameters.forEach((parameter) -> {
                builder.append((builder.toString().length() == 0)?"?":"&").append(parameter.getKey()).append("=").append(parameter.getValue());
            });
            return builder.toString();
        }
        return null;
    }
}
