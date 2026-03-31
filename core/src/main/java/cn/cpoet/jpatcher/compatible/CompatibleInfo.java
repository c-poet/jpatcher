package cn.cpoet.jpatcher.compatible;

import com.intellij.openapi.util.BuildNumber;

/**
 * 兼容信息
 *
 * @author CPoet
 */
public class CompatibleInfo {

    private BuildNumber buildNumber;

    private Compatible compatible;

    public BuildNumber getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(BuildNumber buildNumber) {
        this.buildNumber = buildNumber;
    }

    public Compatible getCompatible() {
        return compatible;
    }

    public void setCompatible(Compatible compatible) {
        this.compatible = compatible;
    }
}
