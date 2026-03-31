package cn.cpoet.jpatcher.impl223.setting;

import cn.cpoet.jpatcher.setting.SettingComponent9;
import cn.cpoet.jpatcher.util.I18nUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

/**
 * @author CPoet
 */
public class SettingComponent9Impl implements SettingComponent9 {

    @Override
    public void cpbPatchAssistant2JTextFieldWithBtn(TextFieldWithBrowseButton btn) {
        btn.addBrowseFolderListener(I18nUtil.t("settings.PatchAssistant2J.path")
                , null, null, FileChooserDescriptorFactory.createSingleFileDescriptor("exe"));
    }
}
