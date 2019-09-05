package com.ceciltechnology.viii28stw.backend.util;

public class UrlPrefixFactory {

    private static final String URL_PREFIX;

    static {
        URL_PREFIX = "http://localhost:9000/pensiltik";
    }

    private UrlPrefixFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static String getUrlPrefix() {
        return URL_PREFIX;
    }
}
