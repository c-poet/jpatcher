package cn.cpoet.jpatcher.actions.patch;

import cn.cpoet.jpatcher.compatible.CompatibleService;
import cn.cpoet.jpatcher.component.CustomComboBox;
import cn.cpoet.jpatcher.component.ScrollVPanel;
import cn.cpoet.jpatcher.component.TitledPanel;
import cn.cpoet.jpatcher.setting.Setting;
import cn.cpoet.jpatcher.util.I18nUtil;
import cn.cpoet.jpatcher.util.VelocityUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.DocumentEvent;
import java.awt.event.ItemEvent;
import java.util.Map;

/**
 * 生成补丁配置面板
 *
 * @author CPoet
 */
public class GenPatchConfPanel extends ScrollVPanel {

    private final Project project;
    private JBTextField fileNameField;
    private final GenPatchPanel parent;

    public GenPatchConfPanel(Project project, GenPatchPanel parent) {
        this.parent = parent;
        this.project = project;
        setBorder(JBUI.Borders.empty());
        GenPatchSetting setting = GenPatchSetting.getInstance(project);
        buildGeneral(setting);
        buildBeforeGenerate(setting);
        buildAfterGenerate(setting);
    }

    protected void cpbOutputFolderTextField(TextFieldWithBrowseButton btn) {
        CompatibleService.getInstance().instance(GenPatchConfPanel9.class)
                .cpbOutputFolderTextField(project, btn);
    }

    public void buildGeneral(GenPatchSetting setting) {
        GenPatchSetting.State state = setting.getState();
        FormBuilder formBuilder = createFormBuilder();
        fileNameField = new JBTextField();
        if (StringUtils.isNotBlank(state.lastFileName)) {
            fileNameField.setText(state.lastFileName);
        } else {
            refreshFileName();
        }
        fileNameField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent event) {
                parent.updateBtnStatus();
            }
        });
        TextFieldWithBrowseButton fileNameFieldBtn = new TextFieldWithBrowseButton(fileNameField, e -> refreshFileName());
        fileNameFieldBtn.setButtonIcon(AllIcons.Actions.Refresh);
        formBuilder.addLabeledComponent(I18nUtil.t("actions.patch.GenPatchPackageAction.config.fileName"), fileNameFieldBtn);
        // 选择输出的目录
        TextFieldWithBrowseButton outputFolderTextField = new TextFieldWithBrowseButton();
        cpbOutputFolderTextField(outputFolderTextField);
        outputFolderTextField.setText(state.outputFolder);
        outputFolderTextField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent event) {
                setting.getState().outputFolder = outputFolderTextField.getText();
                parent.updateBtnStatus();
            }
        });
        formBuilder.addLabeledComponent(I18nUtil.t("actions.patch.GenPatchPackageAction.config.outputFolder"), outputFolderTextField);

        JBCheckBox coverCheckBox = new JBCheckBox(I18nUtil.t("actions.patch.GenPatchPackageAction.config.cover"), state.cover);
        coverCheckBox.addActionListener(e -> setting.getState().cover = !setting.getState().cover);
        formBuilder.addComponent(coverCheckBox);

        // 是否编译
        JBCheckBox compressCheckBox = new JBCheckBox(I18nUtil.t("actions.patch.GenPatchPackageAction.config.compress"), state.compress);
        compressCheckBox.addActionListener(e -> setting.getState().compress = !setting.getState().compress);
        formBuilder.addComponent(compressCheckBox);

        // 包含路径
        JBCheckBox includePathCheckBox = new JBCheckBox(I18nUtil.t("actions.patch.GenPatchPackageAction.config.includePath"), state.includePath);
        includePathCheckBox.addActionListener(e -> setting.getState().includePath = !setting.getState().includePath);
        formBuilder.addComponent(includePathCheckBox);

        // 添加修改标识符
        JBCheckBox addModCheckBox = new JBCheckBox(I18nUtil.t("actions.patch.GenPatchPackageAction.config.addModLabel"), state.addModLabel);
        addModCheckBox.addActionListener(e -> {
            setting.getState().addModLabel = !setting.getState().addModLabel;
            parent.updateBtnStatus();
        });
        formBuilder.addComponent(addModCheckBox);

        TitledPanel titledPanel = new TitledPanel(I18nUtil.t("actions.patch.GenPatchPackageAction.config.generalTitle"));
        titledPanel.add(formBuilder.getPanel());
        add2View(titledPanel);
    }

    private void refreshFileName() {
        String fileName = Setting.getInstance().getState().patchNameTemplate;
        if (StringUtils.isNotBlank(fileName)) {
            fileName = VelocityUtil.render(fileName, project, Map.of());
        }
        fileNameField.setText(fileName);
    }

    public void buildBeforeGenerate(GenPatchSetting setting) {
        GenPatchSetting.State state = setting.getState();
        FormBuilder formBuilder = createFormBuilder();
        CustomComboBox<GenPatchBuildTypeEnum> buildTypeComboBox = new CustomComboBox<>();
        for (GenPatchBuildTypeEnum genPatchBuildTypeEnum : GenPatchBuildTypeEnum.values()) {
            buildTypeComboBox.addItem(genPatchBuildTypeEnum);
        }
        buildTypeComboBox.customText(GenPatchBuildTypeEnum::getName);
        buildTypeComboBox.setSelectedItem(GenPatchBuildTypeEnum.ofCode(state.buildType));
        buildTypeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                GenPatchBuildTypeEnum buildTypeEnum = (GenPatchBuildTypeEnum) e.getItem();
                setting.getState().buildType = buildTypeEnum.getCode();
            }
        });
        formBuilder.addLabeledComponent(I18nUtil.t("actions.patch.GenPatchPackageAction.config.buildType.label"), buildTypeComboBox);
        TitledPanel configTitledPanel = new TitledPanel(I18nUtil.t("actions.patch.GenPatchPackageAction.config.beforeTitle"));
        configTitledPanel.add(formBuilder.getPanel());
        add2View(configTitledPanel);
    }

    public void buildAfterGenerate(GenPatchSetting setting) {
        FormBuilder formBuilder = createFormBuilder();
        JBCheckBox openOutputFolderCheckBox = new JBCheckBox(I18nUtil.t("actions.patch.GenPatchPackageAction.config.openOutputFolder"), setting.getState().openOutputFolder);
        openOutputFolderCheckBox.addActionListener((event) -> setting.getState().openOutputFolder = !setting.getState().openOutputFolder);
        formBuilder.addComponent(openOutputFolderCheckBox);

        JBCheckBox replacePatchCheckBox = new JBCheckBox(I18nUtil.t("actions.patch.GenPatchPackageAction.config.openPatchAssistant2J"), setting.getState().openReplacePatch);
        replacePatchCheckBox.addActionListener((event) -> setting.getState().openReplacePatch = !setting.getState().openReplacePatch);
        formBuilder.addComponent(replacePatchCheckBox);

        TitledPanel titledPanel = new TitledPanel(I18nUtil.t("actions.patch.GenPatchPackageAction.config.afterTitle"));
        titledPanel.add(formBuilder.getPanel());
        add2View(titledPanel);
    }

    protected FormBuilder createFormBuilder() {
        return FormBuilder.createFormBuilder().setFormLeftIndent(20);
    }

    public String getFileName() {
        return fileNameField == null ? null : fileNameField.getText();
    }
}
