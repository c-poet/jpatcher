package cn.cpoet.jpatcher.actions.patch;

import cn.cpoet.jpatcher.util.EnumUtil;

/**
 * 项目类型
 *
 * @author CPoet
 */
public enum GenPatchProjectTypeEnum {

    NONE("none", "普通项目"),

    SPRING("spring", "Spring项目");

    private final String code;

    private final String desc;

    GenPatchProjectTypeEnum(final String code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static GenPatchProjectTypeEnum ofCode(String code) {
        return EnumUtil.find(GenPatchProjectTypeEnum.values(), GenPatchProjectTypeEnum::getCode, code);
    }
}
