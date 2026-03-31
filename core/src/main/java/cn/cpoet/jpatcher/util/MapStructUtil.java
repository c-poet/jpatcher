package cn.cpoet.jpatcher.util;

import cn.cpoet.jpatcher.constant.FileBuildTypeExtEnum;
import cn.cpoet.jpatcher.model.MapStructMapperAnno;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.lang.jvm.annotation.JvmAnnotationConstantValue;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import org.apache.commons.lang3.ClassUtils;

import java.util.List;
import java.util.Objects;

/**
 * MapStruct Util
 *
 * @author CPoet
 */
public class MapStructUtil {
    private MapStructUtil() {
    }

    /**
     * 获取MapStruct支持的扩展
     *
     * @param file 文件
     * @return 受支持的扩展|Null
     */
    public static FileBuildTypeExtEnum getSupportBuildTypeExt(VirtualFile file) {
        if (FileBuildTypeExtEnum.JAVA.getSourceExt().equals(file.getExtension())) {
            return FileBuildTypeExtEnum.JAVA;
        }
        if (FileBuildTypeExtEnum.KOTLIN.getSourceExt().equals(file.getExtension())) {
            return FileBuildTypeExtEnum.KOTLIN;
        }
        return null;
    }

    /**
     * 查找MapStruct Mapper注解
     *
     * @param psiClass 类
     * @return MapStruct Mapper注解|Null
     */
    public static MapStructMapperAnno getMapperAnno(PsiClass psiClass) {
        PsiAnnotation anno = psiClass.getAnnotation("org.mapstruct.Mapper");
        if (anno == null) {
            return null;
        }
        MapStructMapperAnno mapperAnno = new MapStructMapperAnno();
        List<JvmAnnotationAttribute> attributes = anno.getAttributes();
        for (JvmAnnotationAttribute attribute : attributes) {
            if ("implementationName".equals(attribute.getAttributeName())) {
                JvmAnnotationConstantValue attributeValue = (JvmAnnotationConstantValue) attribute.getAttributeValue();
                if (attributeValue != null) {
                    mapperAnno.setImplementationName(Objects.toString(attributeValue.getConstantValue(), null));
                }
            } else if ("implementationPackage".equals(attribute.getAttributeName())) {
                JvmAnnotationConstantValue attributeValue = (JvmAnnotationConstantValue) attribute.getAttributeValue();
                if (attributeValue != null) {
                    mapperAnno.setImplementationPackage(Objects.toString(attributeValue.getConstantValue(), null));
                }
            }
        }
        return mapperAnno;
    }

    /**
     * 获取Mapper实现类名
     *
     * @param psiClass   类信息
     * @param mapperAnno Mapper注解信息
     * @return 实现类名
     */
    public static String getMapperImplName(PsiClass psiClass, MapStructMapperAnno mapperAnno) {
        String implementationPackage = mapperAnno.getImplementationPackage().replace("<PACKAGE_NAME>", ClassUtils.getPackageCanonicalName(psiClass.getQualifiedName()));
        String implementationName = mapperAnno.getImplementationName().replace("<CLASS_NAME>", Objects.requireNonNull(psiClass.getName()));
        return implementationPackage + ClassUtils.PACKAGE_SEPARATOR + implementationName;
    }

    /**
     * 获取Mapper实现类名
     *
     * @param psiClass 类信息
     * @return 实现类名|Null
     */
    public static String getMapperImplName(PsiClass psiClass) {
        MapStructMapperAnno mapperAnno = getMapperAnno(psiClass);
        if (mapperAnno == null) {
            return null;
        }
        return getMapperImplName(psiClass, mapperAnno);
    }
}
