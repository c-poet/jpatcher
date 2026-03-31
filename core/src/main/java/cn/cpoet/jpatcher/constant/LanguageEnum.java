package cn.cpoet.jpatcher.constant;

import cn.cpoet.jpatcher.util.I18nUtil;

/**
 * 支持的国际化语言枚举
 *
 * @author CPoet
 */
public enum LanguageEnum {
    /** 简体中文 */
    ZH("zh", "简体中文"),

    /** 繁体中文 */
    ZH_TW("zh-tw", "繁体中文"),

    /** 英文 */
    EN("en", "英文");

    private final String code;
    private final String desc;

    LanguageEnum(final String code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getName() {
        return I18nUtil.t("language." + code, desc);
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static LanguageEnum ofCode(String code) {
        for (LanguageEnum item : values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return ZH;
    }
}