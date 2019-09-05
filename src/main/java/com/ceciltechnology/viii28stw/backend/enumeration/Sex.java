package com.ceciltechnology.viii28stw.backend.enumeration;

import com.ceciltechnology.viii28stw.backend.util.I18nFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Plamedi L. Lusembo
 */

@AllArgsConstructor
@Getter
public enum Sex {
    MALE('M', I18nFactory.getInstance().getResourceBundle().getString("text.sex.male")),
    FEMALE('F', I18nFactory.getInstance().getResourceBundle().getString("text.sex.female"));

    private final char id;
    private final String description;

    public static List<Sex> getList() {
        List<Sex> sexList = new ArrayList<Sex>
                (Arrays.asList(Sex.values()));
        return sexList;
    }

    @Override
    public String toString() {
        return this.description;
    }



}
