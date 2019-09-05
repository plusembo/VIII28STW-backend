package com.ceciltechnology.viii28stw.backend.enumeration;

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
    MALE('M', "Male"),
    FEMALE('F', "Female");

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
