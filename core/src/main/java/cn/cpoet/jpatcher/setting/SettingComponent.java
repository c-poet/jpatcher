package cn.cpoet.jpatcher.setting;

import cn.cpoet.jpatcher.compatible.CompatibleService;
import cn.cpoet.jpatcher.component.CustomComboBox;
import cn.cpoet.jpatcher.constant.LanguageEnum;
import cn.cpoet.jpatcher.util.I18nUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

/**
 * 插件配置组件
 *
 * @author CPoet
 */
public class SettingComponent {

    private final JPanel mainPanel;

    private final ComboBox<LanguageEnum> selectLanguageComboBox;
    private final TextFieldWithBrowseButton patchAssistant2JTextFieldWithBtn;

    public SettingComponent() {
        selectLanguageComboBox = buildSelectLanguageComboBox();
        patchAssistant2JTextFieldWithBtn = new TextFieldWithBrowseButton();
        cpbPatchAssistant2JTextFieldWithBtn(patchAssistant2JTextFieldWithBtn);
        mainPanel = FormBuilder.createFormBuilder().setFormLeftIndent(20)
                .addLabeledComponent(I18nUtil.t("settings.SelectLanguage.label"), selectLanguageComboBox)
                .addLabeledComponent(I18nUtil.t("settings.PatchAssistant2J.label"), patchAssistant2JTextFieldWithBtn)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    protected void cpbPatchAssistant2JTextFieldWithBtn(TextFieldWithBrowseButton btn) {
        CompatibleService.getInstance()
                .instance(SettingComponent9.class)
                .cpbPatchAssistant2JTextFieldWithBtn(btn);
    }

    private ComboBox<LanguageEnum> buildSelectLanguageComboBox() {
        CustomComboBox<LanguageEnum> comboBox = new CustomComboBox<>();
        comboBox.customText(LanguageEnum::getName);
        for (LanguageEnum value : LanguageEnum.values()) {
            comboBox.addItem(value);
        }
        return comboBox;
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public LanguageEnum getLanguage() {
        return (LanguageEnum) selectLanguageComboBox.getSelectedItem();
    }

    public void setLanguage(LanguageEnum language) {
        selectLanguageComboBox.setSelectedItem(language);
    }

    public String getPatchAssistant2JPath() {
        return patchAssistant2JTextFieldWithBtn.getText();
    }

    public void setPatchAssistant2JPath(String patchAssistant2JPath) {
        patchAssistant2JTextFieldWithBtn.setText(patchAssistant2JPath);
    }
}
