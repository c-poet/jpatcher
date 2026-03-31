package cn.cpoet.jpatcher.actions.patch;

import com.intellij.openapi.module.Module;

/**
 * @author CPoet
 */
public class GenPatchModuleBean {

    /**
     * 模块
     */
    private Module module;

    /**
     * 是否入口文件所在目录
     */
    private boolean isApp;

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public boolean isApp() {
        return isApp;
    }

    public void setApp(boolean app) {
        isApp = app;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
