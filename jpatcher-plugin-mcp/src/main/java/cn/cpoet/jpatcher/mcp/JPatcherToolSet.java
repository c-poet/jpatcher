package cn.cpoet.jpatcher.mcp;

import cn.cpoet.jpatcher.actions.patch.GenPatchBuildTypeEnum;
import cn.cpoet.jpatcher.actions.patch.GenPatchSetting;
import com.intellij.mcpserver.McpCallInfoKt;
import com.intellij.mcpserver.annotations.McpDescription;
import com.intellij.mcpserver.annotations.McpTool;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import kotlin.coroutines.Continuation;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * JPatcher MCP tool set
 *
 * @author CPoet
 */
public class JPatcherToolSet implements McpToolsProvider {

    @McpTool(name = "jp_get_gen_patch_conf")
    @McpDescription(description = "获取项目应用增量补丁包生成配置")
    public Object getGenPatchConf(@NotNull Continuation<?> continuation) {
        Project project = McpCallInfoKt.getProject(continuation.getContext());
        return GenPatchSetting.getInstance(project).getState();
    }

    @McpTool(name = "jp_gen_patch")
    @McpDescription(description = "生成应用增量补丁包，传入需要打补丁的文件列表，自动生成补丁包。")
    public @Nullable Object genPatch(
            @McpDescription(description = "项目名称") String projectName,
            @McpDescription(description = "需要打包补丁的文件路径列表") @NotNull List<String> filePaths,
            @McpDescription(description = "生成的补丁保存目录") String outputFolder,
            @McpDescription(description = "生成补丁文件名") String fileName,
            @McpDescription(description = "编译类型：default(默认)、file(文件)、module(模块)、project(项目)") GenPatchBuildTypeEnum buildType,
            @McpDescription(description = "是否包含路径") Boolean isIncludePath,
            @McpDescription(description = "是否添加修改标识") Boolean isAddModLabel,
            @McpDescription(description = "是否压缩成zip文件") Boolean isCompress,
            @McpDescription(description = "是否覆盖已存在的文件或目录") Boolean isCover,
            @NotNull Continuation<?> continuation
    ) {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        Project project = null;
        if (StringUtils.isNotBlank(projectName)) {
            project = findProjectByName(projects, projectName);
        }
        if (project == null) {
            project = findProjectByPath(projects, filePaths);
        }
        if (project == null) {
            return StringUtils.isBlank(projectName) ? "Unknown project" : "Unknown project:" + projectName;
        }
        System.out.println(project);
        return "功能未实现";
    }

    private Project findProjectByName(Project[] projects, String projectName) {
        for (Project project : projects) {
            if (projectName.equals(project.getName())) {
                return project;
            }
        }
        return null;
    }

    private Project findProjectByPath(Project[] projects, List<String> filePaths) {
        for (Project project : projects) {
            String basePath = project.getBasePath();
            if (StringUtils.isNotBlank(basePath)) {
                for (String filePath : filePaths) {
                    if (filePath.startsWith(basePath)) {
                        return project;
                    }
                }
            }
        }
        return null;
    }
}
