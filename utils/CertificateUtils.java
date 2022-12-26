package com.learning.phisingdetection.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class CertificateUtils {


    public static X509Certificate getCertificate(String urlStr) {
        URL url = null;
        HttpsURLConnection con = null;
        try {
            url = new URL(urlStr);
            con = (HttpsURLConnection) url.openConnection();
            con.setConnectTimeout(30 * 1000);
            con.connect();
            Certificate[] certs = con.getServerCertificates();
            if (certs.length == 0) {
                return null;
            }

            Certificate cert = certs[0];
            if (cert.getType().equals("X.509") == false) {
                return null;
            }

            return (X509Certificate) cert;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SSLPeerUnverifiedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
