package cn.cpoet.jpatcher.util;

import java.util.Objects;
import java.util.function.Function;

/**
 * 枚举工具
 *
 * @author CPoet
 */
public abstract class EnumUtil {
    private EnumUtil() {
    }

    public static <T extends Enum<T>, R> T find(T[] values, Function<T, R> func, R val) {
        return find(values, func, val, null);
    }

    public static <T extends Enum<T>, R> T find(T[] values, Function<T, R> func, R val, T defaultVal) {
        for (T item : values) {
            if (Objects.equals(func.apply(item), val)) {
                return item;
            }
        }
        return defaultVal;
    }
}
