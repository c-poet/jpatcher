package cn.cpoet.jpatcher.setting;

import cn.cpoet.jpatcher.compatible.CompatibleService;
import cn.cpoet.jpatcher.component.CustomComboBox;
import cn.cpoet.jpatcher.component.TitledPanel;
import cn.cpoet.jpatcher.constant.LanguageEnum;
import cn.cpoet.jpatcher.util.I18nUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.awt.*;

/**
 * 插件配置组件
 *
 * @author CPoet
 */
public class SettingComponent {

    private final JComponent mainPanel;
    private JBTextField readmeFileNameField;
    private JBTextField patchNameTemplateField;
    private JBTextArea readmeContentTemplateArea;
    private ComboBox<LanguageEnum> selectLanguageComboBox;
    private TextFieldWithBrowseButton patchAssistant2JTextFieldWithBtn;

    public SettingComponent() {
        JBTabbedPane tabbedPane = new JBTabbedPane();
        tabbedPane.addTab(I18nUtil.t("settings.tab.general"), createGeneralCompoenent());
        tabbedPane.addTab(I18nUtil.t("settings.tab.patch"), createPathcComponent());
        mainPanel = tabbedPane;
    }

    private Component createGeneralCompoenent() {
        selectLanguageComboBox = buildSelectLanguageComboBox();
        return FormBuilder.createFormBuilder()
                .addLabeledComponent(I18nUtil.t("settings.SelectLanguage.label"), selectLanguageComboBox)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    private Component createPathcComponent() {
        readmeFileNameField = new JBTextField();
        patchNameTemplateField = new JBTextField();
        readmeContentTemplateArea = new JBTextArea();
        readmeContentTemplateArea.setRows(8);
        JBScrollPane readmeContentTemplatePane = new JBScrollPane(readmeContentTemplateArea);
        readmeContentTemplatePane.setPreferredSize(readmeContentTemplateArea.getPreferredSize());
        readmeContentTemplatePane.setVerticalScrollBarPolicy(JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel readmeFormPanel = FormBuilder.createFormBuilder()
                .setFormLeftIndent(20)
                .addLabeledComponent(I18nUtil.t("settings.patch.readme.fileName"), readmeFileNameField)
                .addLabeledComponent(I18nUtil.t("settings.patch.readme.contentTemplate"), readmeContentTemplatePane, true)
                .getPanel();
        TitledPanel readmePanel = new TitledPanel(I18nUtil.t("settings.patch.readme.label"));
        readmePanel.add(readmeFormPanel);

        patchAssistant2JTextFieldWithBtn = new TextFieldWithBrowseButton();
        cpbPatchAssistant2JTextFieldWithBtn(patchAssistant2JTextFieldWithBtn);
        JPanel extFormPanel = FormBuilder.createFormBuilder()
                .setFormLeftIndent(20)
                .addLabeledComponent(I18nUtil.t("settings.PatchAssistant2J.label"), patchAssistant2JTextFieldWithBtn)
                .getPanel();
        JPanel extPanel = new TitledPanel(I18nUtil.t("settings.patch.extConfig.label"));
        extPanel.add(extFormPanel);

        return FormBuilder.createFormBuilder()
                .setFormLeftIndent(20)
                .addLabeledComponent(I18nUtil.t("settings.patch.nameTemplate"), patchNameTemplateField)
                .setFormLeftIndent(0)
                .addComponent(readmePanel)
                .addComponent(extPanel)
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

    public String getPatchNameTemplate() {
        return patchNameTemplateField.getText();
    }

    public void setPatchNameTemplate(String patchNameTemplate) {
        patchNameTemplateField.setText(patchNameTemplate);
    }

    public String getReadmeFileName() {
        return readmeFileNameField.getText();
    }

    public void setReadmeFileName(String readmeNameTemplate) {
        readmeFileNameField.setText(readmeNameTemplate);
    }

    public String getReadmeContentTemplate() {
        return readmeContentTemplateArea.getText();
    }

    public void setReadmeContentTemplate(String readmeContentTemplate) {
        readmeContentTemplateArea.setText(readmeContentTemplate);
    }
}
