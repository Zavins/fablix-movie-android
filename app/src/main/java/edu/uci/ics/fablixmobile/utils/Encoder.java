package edu.uci.ics.fablixmobile.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Encoder {
    public static String urlEncode(String text){
        try {
            return URLEncoder.encode(text, StandardCharsets.UTF_8.name());
        }catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to URL encode parameters", e);
        }
    }
}
