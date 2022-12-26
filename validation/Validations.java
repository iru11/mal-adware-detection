package com.learning.phisingdetection.validation;

import com.learning.phisingdetection.service.URLInfo;

import java.util.regex.Pattern;

public class Validations {

    public static boolean validURL(String url) {
        try {
            new URLInfo(url);
        } catch (Exception ex) {
            return false;
        }

        return true;
    }
}