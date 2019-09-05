package com.ceciltechnology.viii28stw.backend.enumeration;

import com.ceciltechnology.viii28stw.backend.util.I18nFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Plamedi L. Lusembo
 */

@AllArgsConstructor
@Getter
public enum UserAcessLevel {
    ADMINISTRATOR(1, I18nFactory.getInstance().getResourceBundle().getString("text.user.acess.level.administrator")),
    COMMON_USER(2, I18nFactory.getInstance().getResourceBundle().getString("text.user.acess.level.common.user"));

    private final int id;
    private final String description;
}
