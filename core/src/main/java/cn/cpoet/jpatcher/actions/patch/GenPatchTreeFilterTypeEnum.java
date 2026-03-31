package cn.cpoet.jpatcher.actions.patch;

import cn.cpoet.jpatcher.util.EnumUtil;
import cn.cpoet.jpatcher.util.I18nUtil;

/**
 * 生成补丁包过滤类型枚举
 *
 * @author CPoet
 */
public enum GenPatchTreeFilterTypeEnum {
    /** 项目文件 */
    PROJECT("project", "项目文件"),

    /** 变更文件 */
    CHANGE("change", "变更文件"),

    /** 当前编译的文件 */
    EDITOR("editor", "编辑文件"),

    /** 已选择文件 */
    SELECTED("selected", "已选文件"),
    ;

    private final String code;

    private final String desc;

    GenPatchTreeFilterTypeEnum(final String code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getTitle() {
        return I18nUtil.t("actions.patch.GenPatchPackageAction.treeFilterType." + code, desc);
    }

    public static GenPatchTreeFilterTypeEnum ofCode(String code) {
        return EnumUtil.find(values(), GenPatchTreeFilterTypeEnum::getCode, code, GenPatchTreeFilterTypeEnum.PROJECT);
    }
}
