package com.learning.phisingdetection.service;

import android.text.TextUtils;

import com.learning.phisingdetection.utils.ApiUtils;
import com.learning.phisingdetection.utils.CertificateUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class URLInfo {
    private String url;
    private String scheme;
    private String domain;
    private int port;
    private String ip;

    private void extractScheme() {
        int index = url.indexOf("://");
        if (index == -1) {
            throw new InvalidURLException();
        }

        this.scheme = url.substring(0, index);
        if (TextUtils.isEmpty(this.scheme) == true) {
            throw new InvalidURLException();
        }

        if (this.scheme.equals("http") == false && this.scheme.equals("https") == false) {
            throw new InvalidURLException();
        }
    }

    private void parseURL() {
        if (TextUtils.isEmpty(url) == true) {
            throw new InvalidURLException();
        }

        this.url = this.url.toLowerCase();
        extractScheme();
        extractDomain();
        extractPort();
        extractIP();
    }

    private void extractPort() {
        if (this.scheme.equals("http") == true) {
            this.port = 80;
        } else if (this.scheme.equals("https") == true) {
            this.port = 443;
        }

        String tmpURL = this.url.replace(this.scheme + "://", "");
        int index = tmpURL.indexOf('/');
        int colonIndex = tmpURL.indexOf(':');
        String port = null;
        if (colonIndex != -1 && index != -1) {
            port = tmpURL.substring(colonIndex + 1, index);
        }

        if (TextUtils.isEmpty(port) == false) {
            try {
                this.port = Integer.parseInt(port);
            } catch (NumberFormatException ex) {
                throw new InvalidURLException();
            }
        }

    }

    public URLInfo(String url) {
        this.url = url;
        parseURL();
    }

    private void extractDomain() {
        String chkDomain = "";
        chkDomain = this.url.replace(this.scheme + "://", "");
        chkDomain = chkDomain.split("/")[0];
        chkDomain = chkDomain.split(":")[0];
        this.domain = chkDomain;
    }
    public void extractIP() {
        try {
            InetAddress address =  Inet4Address.getByName(this.domain);
            this.ip = address.getHostAddress();
        } catch (Exception e) {
            throw new InvalidURLException();
        }
    }

    public float getDotCount() {
        String[] domains = this.domain.split("\\.");
        float point = 0f;
        if (domains.length > 3) {
            point = 0.5f;
        } else if (domains.length == 3) {
            point = 0.25f;
        }

        return point;
    }

    public float getPointUrlLength(){
        float point;
        if (url.length() < 54){
            point = 0;
        }else if (url.length() < 75){
            point = 0.025f;
        }else{
            point = 0.05f;
        }
        return point;
    }

    public String getStatus() {
        float total = getPointUrlLength()
                + getDotCount()
                + getCertificateCount()
                + getDomainAge()
                + getPointSuffix();

        String result = "unsolved";
        if (total < 0.1){
            result = "TrustWorthy";
        }else if (total < 0.3){
            result = "Fairly Legitimate";
        }else if (total < 0.5){
            result = "Unsolved";
        }else if (total < 0.75){
            result = "Suspicious";
        }else{
            result = "Phishy";
        }


        return result + " Score: "+ total;
    }

    private float getDomainAge() {
        float point = 0;

        String url = this.scheme + "://" + this.domain + ":" + this.port;
        int age =  ApiUtils.getDomainInformation(url);
        if (age > 365) {
            point = 0f;
        } else if (age > 90) {
            point = 0.25f;
        } else {
            point = 0.05f;
        }

        return point;
    }

    private float getPointSuffix(){
        float point;
        if (url.contains("-")){
            point = 0.05f;
        }else{
            point = 0;
        }
        return point;
    }

    private float getCertificateCount() {
        float point = 0;

        if (this.scheme.equals("https") == false) {
            return 0.8f;
        }

        String url = this.scheme + "://" + this.domain + ":" + this.port;
        X509Certificate x509Cert = CertificateUtils.getCertificate(url);
        if (x509Cert == null) {
            return 0.8f;
        }

        try {
            x509Cert.checkValidity();
        } catch (CertificateException ex) {
            return 0.5f;
        }

        return point;
    }
}
