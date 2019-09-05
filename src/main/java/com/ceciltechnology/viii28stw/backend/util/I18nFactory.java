package com.ceciltechnology.viii28stw.backend.util;

import com.ceciltechnology.viii28stw.backend.enumeration.LanguagesSetting;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.ResourceBundle;

@NoArgsConstructor
public class I18nFactory {
    @Getter
    private Locale locale;
    @Getter
    private ResourceBundle resourceBundle;
    private final static String RESOURCE_BUNDLE_BASE_NAME = "messages";

    private static I18nFactory uniqueInstance;

    public static synchronized I18nFactory getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new I18nFactory();
        }
        return uniqueInstance;
    }

    public void setSystemLanguage(LanguagesSetting languagesSetting) {
        setLocale(languagesSetting.getLanguageCode(), languagesSetting.getCountryCode());
        setResourceBundle();
    }

    private void setLocale(String language, String country) {
        locale = new Locale(language, country);
    }

    private void setResourceBundle() {
        resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, locale);
    }

}
