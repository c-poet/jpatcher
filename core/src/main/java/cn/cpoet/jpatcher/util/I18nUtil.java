package cn.cpoet.jpatcher.util;

import cn.cpoet.jpatcher.setting.Setting;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Supplier;

/**
 * 国际化工具
 *
 * @author CPoet
 */
public abstract class I18nUtil {

    private static final String I18N_FILE_PREFIX = "messages/jpatcher";

    /** I18n资源 */
    private static volatile ResourceBundle resourceBundle;

    private I18nUtil() {
    }

    /**
     * 获取国际化内容
     *
     * @param name 名称
     * @return 值
     */
    @NotNull
    public static String t(String name) {
        return t(name, "");
    }

    /**
     * 获取国际化内容
     *
     * @param name           名称
     * @param defaultMessage 默认值
     * @return 值
     */
    @NotNull
    public static String t(String name, @NotNull String defaultMessage) {
        try {
            return bundle().getString(name);
        } catch (Exception ignored) {
        }
        return defaultMessage;
    }

    /**
     * 获取国际化内容
     *
     * @param name   名称
     * @param params 参数列表
     * @return 值
     */
    @NotNull
    public static String tr(String name, Object... params) {
        String message = t(name);
        return StringUtils.isBlank(message) ? message : String.format(message, params);
    }

    /**
     * 获取国际化内容
     *
     * @param name 名称
     * @return 值
     */
    public static Supplier<String> td(String name) {
        return () -> t(name);
    }

    /**
     * 获取国际化内容
     *
     * @param name           名称
     * @param defaultMessage 默认值
     * @return 值
     */
    public static Supplier<String> td(String name, @NotNull String defaultMessage) {
        return () -> t(name, defaultMessage);
    }

    /**
     * 获取国际化内容
     *
     * @param name   名称
     * @param params 参数列表
     * @return 值
     */
    public static Supplier<String> trd(String name, Object... params) {
        return () -> tr(name, params);
    }

    public static String getLanguage() {
        Setting.State state = Setting.getInstance().getState();
        return state.language;
    }

    public static ResourceBundle bundle() {
        if (resourceBundle == null) {
            synchronized (I18nUtil.class) {
                if (resourceBundle == null) {
                    updateLocale();
                }
            }
        }
        return resourceBundle;
    }

    public static void updateLocale() {
        Locale locale = Locale.forLanguageTag(getLanguage());
        if (resourceBundle != null && Objects.equals(resourceBundle.getLocale(), locale)) {
            return;
        }
        synchronized (I18nUtil.class) {
            if (resourceBundle != null && Objects.equals(resourceBundle.getLocale(), locale)) {
                return;
            }
            resourceBundle = ResourceBundle.getBundle(I18N_FILE_PREFIX, locale);
        }
    }
}
