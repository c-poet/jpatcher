package cn.cpoet.jpatcher.model;

/**
 * MapStructMapper注解信息
 *
 * @author CPoet
 */
public class MapStructMapperAnno {
    /**
     * 实现类名
     */
    private String implementationName = "<CLASS_NAME>Impl";

    /**
     * 实现包名
     */
    private String implementationPackage = "<PACKAGE_NAME>";

    public String getImplementationName() {
        return implementationName;
    }

    public void setImplementationName(String implementationName) {
        this.implementationName = implementationName;
    }

    public String getImplementationPackage() {
        return implementationPackage;
    }

    public void setImplementationPackage(String implementationPackage) {
        this.implementationPackage = implementationPackage;
    }
}
