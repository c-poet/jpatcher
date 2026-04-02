package cn.cpoet.jpatcher.setting;

import cn.cpoet.jpatcher.compatible.CompatibleService;
import cn.cpoet.jpatcher.component.CustomComboBox;
import cn.cpoet.jpatcher.component.TitledPanel;
import cn.cpoet.jpatcher.constant.LanguageEnum;
import cn.cpoet.jpatcher.util.I18nUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

/**
 * 插件配置组件
 *
 * @author CPoet
 */
public class SettingComponent {

    private final JComponent mainPanel;

    private final JBTextField readmeNameTemplateField;
    private final EditorTextField readmeContentTemplateEditor;
    private final ComboBox<LanguageEnum> selectLanguageComboBox;
    private final TextFieldWithBrowseButton patchAssistant2JTextFieldWithBtn;

    public SettingComponent() {
        selectLanguageComboBox = buildSelectLanguageComboBox();
        JPanel generalPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(I18nUtil.t("settings.SelectLanguage.label"), selectLanguageComboBox)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        readmeNameTemplateField = new JBTextField();
        readmeContentTemplateEditor = new EditorTextField();
        readmeContentTemplateEditor.setOneLineMode(false);
        patchAssistant2JTextFieldWithBtn = new TextFieldWithBrowseButton();
        cpbPatchAssistant2JTextFieldWithBtn(patchAssistant2JTextFieldWithBtn);

        JPanel readmeFormPanel = FormBuilder.createFormBuilder()
                .setFormLeftIndent(20)
                .addLabeledComponent("名称模板", readmeNameTemplateField)
                .addLabeledComponent("内容模板", readmeContentTemplateEditor)
                .getPanel();
        TitledPanel readmePanel = new TitledPanel("说明文件");
        readmePanel.add(readmeFormPanel);
        JPanel extFormPanel = FormBuilder.createFormBuilder()
                .setFormLeftIndent(20)
                .addLabeledComponent(I18nUtil.t("settings.PatchAssistant2J.label"), patchAssistant2JTextFieldWithBtn)
                .getPanel();
        JPanel extPanel = new TitledPanel("扩展配置");
        extPanel.add(extFormPanel);
        JPanel patchPanel = FormBuilder.createFormBuilder()
                .addComponent(readmePanel)
                .addComponent(extPanel)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        JBTabbedPane tabbedPane = new JBTabbedPane();
        tabbedPane.addTab("常规配置", generalPanel);
        tabbedPane.addTab("补丁配置", patchPanel);
        mainPanel = tabbedPane;
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

    public JComponent getComponent() {
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

    public String getReadmeNameTemplate() {
        return readmeNameTemplateField.getText();
    }

    public void setReadmeNameTemplate(String readmeNameTemplate) {
        readmeNameTemplateField.setText(readmeNameTemplate);
    }

    public String getReadmeContentTemplate() {
        return readmeContentTemplateEditor.getText();
    }

    public void setReadmeContentTemplate(String readmeContentTemplate) {
        readmeContentTemplateEditor.setText(readmeContentTemplate);
    }
}
