package cn.cpoet.jpatcher.actions.patch;

import org.apache.commons.io.FilenameUtils;

/**
 * 补丁包常量
 *
 * @author CPoet
 */
public interface GenPatchConst {
    /** 生成的补丁包后缀名 */
    String PATCH_FILE_EXT = "zip";

    /** 生成的补丁包后缀名 */
    String PATCH_FULL_FILE_EXT = FilenameUtils.EXTENSION_SEPARATOR + PATCH_FILE_EXT;

    /** 补丁说明文件名称 */
    String PATCH_DESC_FILE_NAME = "README.txt";

    /** 补丁说明文件描述 */
    String PATCH_DESC_FILE_COMMENT = "The Patch description file";

    /** PatchAssistant Modify Change Type */
    String CHANGE_TYPE_MOD = "!";
}
