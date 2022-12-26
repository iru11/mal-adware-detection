package com.learning.phisingdetection.utils;

import android.net.Uri;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ApiUtils {

    private static final String DOMAIN_API_KEY = "6C4F20D6D242FB7E94134673F5046797";

    public static int getDomainInformation(String urlStr) {
        String reqURL = String.format("https://api.ip2whois.com/v2?key=%s&domain=%s",
                DOMAIN_API_KEY,
                Uri.encode(urlStr));

        Map<String, Object> infoMap =  new HashMap<>();
        URL url = null;
        HttpsURLConnection con = null;
        try {
            url = new URL(reqURL);
            con = (HttpsURLConnection) url.openConnection();
            int code = con.getResponseCode();
            if (code != 200) {
                return -1;
            }

            InputStream is = con.getInputStream();
            int count = 0;
            byte arr[] = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while ((count = is.read(arr)) != -1) {
                out.write(arr, 0, count);
            }

            String output = out.toString();
            JSONObject obj = new JSONObject(output);
            return obj.getInt("domain_age");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        return -1;
    }
}
