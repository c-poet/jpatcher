package cn.cpoet.jpatcher.impl243.setting;

import cn.cpoet.jpatcher.setting.SettingComponent9;
import cn.cpoet.jpatcher.util.I18nUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

/**
 * @author CPoet
 */
public class SettingComponent9Impl implements SettingComponent9 {

    @Override
    public void cpbPatchAssistant2JTextFieldWithBtn(TextFieldWithBrowseButton textFieldWithBrowseButton) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("exe").withTitle(I18nUtil.t("settings.PatchAssistant2J.path"));
        textFieldWithBrowseButton.addBrowseFolderListener(null, descriptor);
    }
}
