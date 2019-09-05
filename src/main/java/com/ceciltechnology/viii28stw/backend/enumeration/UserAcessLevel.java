package com.ceciltechnology.viii28stw.backend.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Plamedi L. Lusembo
 */

@AllArgsConstructor
@Getter
public enum UserAcessLevel {
    ADMINISTRATOR(1, "Administrator"),
    COMMON_USER(2, "Common user");

    private final int id;
    private final String description;
}
