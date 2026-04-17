package cn.cpoet.jpatcher.actions.patch;

import cn.cpoet.jpatcher.constant.CommonConst;
import cn.cpoet.jpatcher.util.OSUtil;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * 生成补丁配置
 *
 * @author CPoet
 */
@Service(Service.Level.PROJECT)
@State(name = "cn.cpoet.jpatcher.actions.patch.GenPatchSetting", storages = @Storage(CommonConst.SETTING_FILE_NAME))
public final class GenPatchSetting implements PersistentStateComponent<GenPatchSetting.State> {

    public static class State {
        /**
         * 面板宽度
         */
        public int width = 720;

        /**
         * 面板高度
         */
        public int height = 400;

        /**
         * 面板宽度
         */
        public int previewReadmeWidth = 720;

        /**
         * 面板高度
         */
        public int previewReadmeHeight = 400;

        /**
         * 记录最后一次操作的文件名
         */
        public String lastFileName;

        /** 文件树过滤类型 */
        public String treeFilterType = GenPatchTreeFilterTypeEnum.PROJECT.getCode();

        /**
         * 输出目录
         */
        public String outputFolder = OSUtil.getDesktopPath();

        /**
         * 包含路径
         */
        public boolean includePath;

        /**
         * 添加修改标识符
         */
        public boolean addModLabel;

        /**
         * 是否压缩
         */
        public boolean compress = true;

        /**
         * 存在文件或者目录的情况下是否覆盖
         */
        public boolean cover = true;

        /**
         * 编译类型
         */
        public String buildType = GenPatchBuildTypeEnum.DEFAULT.getCode();

        /**
         * 生成后打开输出的目录
         */
        public boolean openOutputFolder = true;

        /**
         * 生成后打开替换工具
         */
        public boolean openReplacePatch = false;
    }

    private State state = new State();

    public static GenPatchSetting getInstance(Project project) {
        return project.getService(GenPatchSetting.class);
    }

    @NotNull
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }
}
