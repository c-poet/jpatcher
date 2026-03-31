package cn.cpoet.jpatcher.compatible;

import cn.cpoet.jpatcher.exception.JPatcherException;
import cn.cpoet.jpatcher.util.ClassUtil;
import cn.cpoet.jpatcher.util.JsonUtil;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.util.BuildNumber;
import com.intellij.openapi.util.io.StreamUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * @author CPoet
 */
@Service(Service.Level.APP)
public final class CompatibleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompatibleService.class);

    public static final String COMPATIBILITY_FILE = "compatibility.json";

    private final Map<Class<?>, Class<?>> compatibleTable = new HashMap<>();
    private final Map<Class<?>, Object> compatibleSingleTable = new HashMap<>();

    /**
     * 获取兼容服务实例
     *
     * @return 兼容服务实例
     */
    public static CompatibleService getInstance() {
        return ApplicationManager.getApplication().getService(CompatibleService.class);
    }

    public CompatibleService() {
        List<CompatibleInfo> compatibleInfos = loadCompatibleInfos();
        BuildNumber curBuildNumber = ApplicationInfo.getInstance().getBuild();
        compatibleInfos
                .stream()
                .filter(info -> curBuildNumber.compareTo(info.getBuildNumber()) >= 0)
                .sorted(Comparator.comparing(CompatibleInfo::getBuildNumber).reversed())
                .forEach(this::registerCompatible);
    }

    private void registerCompatible(CompatibleInfo compatibleInfo) {
        Compatible compatible = compatibleInfo.getCompatible();
        List<CompatibleItem> compatibleItems = compatible.getItems();
        if (CollectionUtils.isEmpty(compatibleItems)) {
            return;
        }
        for (CompatibleItem compatibleItem : compatibleItems) {
            String sourceClassName = compatibleItem.getSource().trim();
            Class<?> sourceClass = ClassUtil.tryGetClass(sourceClassName);
            if (sourceClass == null) {
                LOGGER.warn("Compatible source class " + sourceClassName + " does not exist");
            } else if (StringUtils.isNotBlank(compatibleItem.getImpl())) {
                registerCompatible(sourceClass, compatibleItem.getImpl().trim());
            } else {
                registerCompatible(compatible, sourceClass);
            }
        }
    }

    private String getImplName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        return name + "Impl";
    }

    private void registerCompatible(Compatible compatible, @NotNull Class<?> clazz) {
        Class<?> compatibleClass = getCompatibleClass(clazz);
        if (compatibleClass != null) {
            return;
        }
        String basePackageName = StringUtils.isBlank(compatible.getPackageName()) ? "" : compatible.getPackageName();
        String implName = getImplName(clazz);
        Class<?> targetClass = ClassUtil.tryGetClass(basePackageName + "." + implName);
        if (targetClass == null) {
            List<String> packagePaths = Arrays.asList(clazz.getPackageName().split("\\."));
            int i = packagePaths.size();
            while (targetClass == null && --i >= 0) {
                targetClass = ClassUtil.tryGetClass(basePackageName + "." + String.join(".", packagePaths.subList(i, packagePaths.size())) + "." + implName);
            }
        }
        if (targetClass == null) {
            throw new JPatcherException("Class " + clazz.getName() + " compatibility implementation class loading failed");
        }
        compatibleTable.put(clazz, targetClass);
    }

    private void registerCompatible(@NotNull Class<?> clazz, @NotNull String implName) {
        Class<?> compatibleClass = getCompatibleClass(clazz);
        if (compatibleClass != null) {
            return;
        }
        Class<?> targetClass = ClassUtil.tryGetClass(implName);
        if (targetClass == null) {
            throw new JPatcherException("Compatible implementation of class " + clazz.getName() + " cannot be loaded class " + implName);
        }
        compatibleTable.put(clazz, targetClass);
    }

    private List<CompatibleInfo> loadCompatibleInfos() {
        List<CompatibleInfo> compatibleInfos = null;
        try {
            Enumeration<URL> compatibilityXmlEnum = CompatibleService.class.getClassLoader().getResources(COMPATIBILITY_FILE);
            while (compatibilityXmlEnum.hasMoreElements()) {
                Compatible compatible = readCompatible(compatibilityXmlEnum.nextElement());
                CompatibleInfo compatibleInfo = new CompatibleInfo();
                compatibleInfo.setCompatible(compatible);
                compatibleInfo.setBuildNumber(BuildNumber.fromString(compatible.getSince()));
                if (compatibleInfos == null) {
                    compatibleInfos = new LinkedList<>();
                }
                compatibleInfos.add(compatibleInfo);
            }
        } catch (Exception e) {
            LOGGER.warn("Load compatible failed: {}", e.getMessage(), e);
        }
        return compatibleInfos == null ? Collections.emptyList() : compatibleInfos;
    }

    private Compatible readCompatible(URL url) throws IOException {
        try (InputStream in = url.openStream();
             InputStreamReader reader = new InputStreamReader(in)) {
            String compatibleJson = StreamUtil.readText(reader);
            return JsonUtil.read(compatibleJson, Compatible.class);
        }
    }

    public @NotNull <T> T instance(@NotNull Class<T> clazz) {
        return instance(clazz, false);
    }

    public @NotNull <T> T instance(@NotNull Class<T> clazz, boolean isSingle) {
        if (!isSingle) {
            return instance0(clazz);
        }
        T obj = getSingleInstance(clazz);
        if (obj == null) {
            synchronized (this) {
                obj = getSingleInstance(clazz);
                if (obj == null) {
                    obj = instance0(clazz);
                    compatibleSingleTable.put(clazz, obj);
                }
            }
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    private <T> T getSingleInstance(Class<T> clazz) {
        Object obj = compatibleSingleTable.get(clazz);
        if (obj != null) {
            return (T) obj;
        }
        for (Map.Entry<Class<?>, Object> entry : compatibleSingleTable.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                return (T) entry.getValue();
            }
        }
        return null;
    }

    private <T> T instance0(Class<T> clazz) {
        Class<T> compatibleClass = getCompatibleClass(clazz);
        if (compatibleClass == null) {
            throw new JPatcherException("No compatibility implementation class for class " + clazz.getName() + " found");
        }
        try {
            return ConstructorUtils.invokeConstructor(compatibleClass);
        } catch (Exception e) {
            throw new JPatcherException("Class " + compatibleClass.getName() + " instantiation failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getCompatibleClass(Class<? super T> clazz) {
        Class<?> implClass = compatibleTable.get(clazz);
        if (implClass != null) {
            return (Class<T>) implClass;
        }
        for (Map.Entry<Class<?>, Class<?>> entry : compatibleTable.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                return (Class<T>) entry.getValue();
            }
        }
        return null;
    }
}
