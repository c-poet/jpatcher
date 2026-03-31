package cn.cpoet.jpatcher.constant;

/**
 * 常用文件编译类型后缀枚举
 *
 * @author CPoet
 */
public enum FileBuildTypeExtEnum {

    JAVA("java", "class"),

    KOTLIN("kt", "class"),
    ;

    /** 源文件后缀 */
    private final String sourceExt;

    /** 输出文件后缀 */
    private final String buildExt;

    public String getSourceExt() {
        return sourceExt;
    }

    public String getBuildExt() {
        return buildExt;
    }

    FileBuildTypeExtEnum(final String sourceExt, final String buildExt) {
        this.sourceExt = sourceExt;
        this.buildExt = buildExt;
    }

    public static FileBuildTypeExtEnum ofSourceExt(String sourceExt) {
        for (FileBuildTypeExtEnum item : values()) {
            if (item.sourceExt.equals(sourceExt)) {
                return item;
            }
        }
        return null;
    }

    public static String findBuildExt(String sourceExt) {
        FileBuildTypeExtEnum fileBuildTypeExtEnum = ofSourceExt(sourceExt);
        return fileBuildTypeExtEnum == null ? null : fileBuildTypeExtEnum.buildExt;
    }

    public static String findSourceExt(String buildExt) {
        for (FileBuildTypeExtEnum item : values()) {
            if (item.buildExt.equals(buildExt)) {
                return item.sourceExt;
            }
        }
        return null;
    }
}