package cn.cpoet.jpatcher.actions.patch;


import cn.cpoet.jpatcher.util.I18nUtil;

/**
 * @author CPoet
 */
public enum GenPatchBuildTypeEnum {
    /** 默认 */
    DEFAULT("default", "默认"),

    /** 文件 */
    FILE("file", "文件"),

    /** 模块 */
    MODULE("module", "模块"),

    /** 项目 */
    PROJECT("project", "项目");

    private final String code;
    private final String desc;

    GenPatchBuildTypeEnum(final String code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getName() {
        return I18nUtil.t("actions.patch.GenPatchPackageAction.config.buildType." + code, desc);
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static GenPatchBuildTypeEnum ofCode(String code) {
        for (GenPatchBuildTypeEnum item : values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return DEFAULT;
    }
}