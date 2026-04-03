package cn.cpoet.jpatcher.setting;

import cn.cpoet.jpatcher.constant.CommonConst;
import cn.cpoet.jpatcher.constant.LanguageEnum;
import cn.cpoet.jpatcher.util.I18nUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * 插件配置
 *
 * @author CPoet
 */
public class Configurable implements com.intellij.openapi.options.Configurable {

    private SettingComponent settingComponent;

    @Override
    public String getDisplayName() {
        return CommonConst.TOOL_NAME;
    }

    @Override
    public @Nullable JComponent createComponent() {
        settingComponent = new SettingComponent();
        return settingComponent.getComponent();
    }

    @Override
    public boolean isModified() {
        Setting.State state = Setting.getInstance().getState();
        return !settingComponent.getLanguage().getCode().equals(state.language)
                || !Objects.equals(settingComponent.getPatchAssistant2JPath(), state.patchAssistant2JPath)
                || !Objects.equals(settingComponent.getPatchNameTemplate(), state.patchNameTemplate)
                || !Objects.equals(settingComponent.getReadmeFileName(), state.readmeFileName)
                || !Objects.equals(settingComponent.getReadmeContentTemplate(), state.readmeContentTemplate);
    }

    @Override
    public void apply() {
        Setting.State state = Setting.getInstance().getState();
        // 判断是否需要重新加载语言
        String oldLanguage = state.language;
        state.language = settingComponent.getLanguage().getCode();
        if (!oldLanguage.equals(state.language)) {
            I18nUtil.updateLocale();
        }
        state.patchAssistant2JPath = settingComponent.getPatchAssistant2JPath();
        state.patchNameTemplate = settingComponent.getPatchNameTemplate();
        state.readmeFileName = settingComponent.getReadmeFileName();
        state.readmeContentTemplate = settingComponent.getReadmeContentTemplate();
    }

    @Override
    public void reset() {
        Setting.State state = Setting.getInstance().getState();
        settingComponent.setLanguage(LanguageEnum.ofCode(state.language));
        settingComponent.setPatchAssistant2JPath(state.patchAssistant2JPath);
        settingComponent.setPatchNameTemplate(state.patchNameTemplate);
        settingComponent.setReadmeFileName(state.readmeFileName);
        settingComponent.setReadmeContentTemplate(state.readmeContentTemplate);
    }

    @Override
    public void disposeUIResources() {
        settingComponent = null;
    }
}
