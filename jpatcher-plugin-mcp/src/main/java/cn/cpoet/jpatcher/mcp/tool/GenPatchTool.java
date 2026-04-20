package cn.cpoet.jpatcher.mcp.tool;

import cn.cpoet.jpatcher.actions.patch.GenPatchBuildTypeEnum;
import cn.cpoet.jpatcher.actions.patch.GenPatchSetting;
import com.intellij.mcpserver.McpCallInfoKt;
import com.intellij.mcpserver.McpTool;
import com.intellij.mcpserver.McpToolCallResult;
import com.intellij.mcpserver.McpToolDescriptor;
import com.intellij.mcpserver.McpToolSchema;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import kotlin.coroutines.Continuation;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author CPoet
 */
public class GenPatchTool implements McpTool {
    @NotNull
    @Override
    public McpToolDescriptor getDescriptor() {
        String propertiesSchema = """
                {
                  "type": "object",
                  "properties": {
                    "projectName": {
                      "type": "string",
                      "description": "Project name. If not specified, will try to find project by file paths."
                    },
                    "filePaths": {
                      "type": "array",
                      "items": {
                        "type": "string"
                      },
                      "description": "List of source file paths to include in the patch package. Can be absolute paths to Java source files or class files."
                    },
                    "outputFolder": {
                      "type": "string",
                      "description": "Directory path where the patch package will be generated. If not specified, uses the configured default output folder."
                    },
                    "fileName": {
                      "type": "string",
                      "description": "Name of the patch package (without extension). Defaults to patch-yyyyMMdd-HHmmss format if not specified."
                    },
                    "buildType": {
                      "type": "string",
                      "enum": ["default", "file", "module", "project"],
                      "description": "Build type before generating patch: default (default), file (compile selected files), module (rebuild affected modules), project (rebuild all modules). Default is default."
                    },
                    "isIncludePath": {
                      "type": "boolean",
                      "description": "Whether to preserve the directory structure in the patch package. Default is false."
                    },
                    "isAddModLabel": {
                      "type": "boolean",
                      "description": "Whether to add modification type labels (ADD/MOD/DEL) in the readme file. Default is false."
                    },
                    "isCompress": {
                      "type": "boolean",
                      "description": "Whether to compress the patch package into a ZIP file. Default is true."
                    },
                    "isCover": {
                      "type": "boolean",
                      "description": "Whether to overwrite existing patch package with the same name. Default is true."
                    }
                  }
                }
                """;
        return new McpToolDescriptor(
                "jp_gen_patch",
                "生成应用增量补丁包，传入需要打补丁的文件列表，自动生成补丁包。",
                new McpToolSchema(
                        (JsonObject) Json.Default.parseToJsonElement(propertiesSchema),
                        Set.of("filePaths"),
                        Map.of(),
                        "#/definitions/"
                ),
                null
        );
    }

    @Nullable
    @Override
    public Object call(@NotNull JsonObject jsonObject, @NotNull Continuation<? super McpToolCallResult> continuation) {
        Project project = McpCallInfoKt.getProject(continuation.getContext());
        
        // 解析参数
        String projectName = extractString(jsonObject, "projectName");
        List<String> filePaths = extractStringList(jsonObject, "filePaths");
        String outputFolder = extractString(jsonObject, "outputFolder");
        String fileName = extractString(jsonObject, "fileName");
        String buildTypeStr = extractString(jsonObject, "buildType");
        Boolean isIncludePath = extractBoolean(jsonObject, "isIncludePath");
        Boolean isAddModLabel = extractBoolean(jsonObject, "isAddModLabel");
        Boolean isCompress = extractBoolean(jsonObject, "isCompress");
        Boolean isCover = extractBoolean(jsonObject, "isCover");
        
        // 查找项目
        Project targetProject = findProject(project, projectName, filePaths);
        if (targetProject == null) {
            return StringUtils.isBlank(projectName) ? "Unknown project" : "Unknown project: " + projectName;
        }
        
        // 更新配置
        GenPatchSetting setting = GenPatchSetting.getInstance(targetProject);
        GenPatchSetting.State state = setting.getState();
        
        if (StringUtils.isNotBlank(outputFolder)) {
            state.outputFolder = outputFolder;
        }
        if (StringUtils.isNotBlank(fileName)) {
            state.lastFileName = fileName;
        }
        if (buildTypeStr != null) {
            state.buildType = buildTypeStr;
        }
        if (isIncludePath != null) {
            state.includePath = isIncludePath;
        }
        if (isAddModLabel != null) {
            state.addModLabel = isAddModLabel;
        }
        if (isCompress != null) {
            state.compress = isCompress;
        }
        if (isCover != null) {
            state.cover = isCover;
        }
        
        // TODO: 实现实际的补丁生成逻辑
        // 这里需要调用 GenPatchPanel 的生成逻辑，但由于 MCP 工具是无界面的，
        // 需要重构生成逻辑为独立的服务类
        
        return "Patch generation functionality needs to be implemented with file paths: " + filePaths;
    }
    
    private Project findProject(Project currentProject, String projectName, List<String> filePaths) {
        if (currentProject != null && StringUtils.isBlank(projectName)) {
            return currentProject;
        }
        
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        Project project = null;
        
        if (StringUtils.isNotBlank(projectName)) {
            project = findProjectByName(projects, projectName);
        }
        if (project == null && filePaths != null && !filePaths.isEmpty()) {
            project = findProjectByPath(projects, filePaths);
        }
        
        return project != null ? project : currentProject;
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
    
    private String extractString(JsonObject jsonObject, String key) {
        if (jsonObject.containsKey(key)) {
            return jsonObject.get(key).toString().replace("\"", "");
        }
        return null;
    }
    
    private List<String> extractStringList(JsonObject jsonObject, String key) {
        if (jsonObject.containsKey(key)) {
            // 简化实现，实际需要根据 JsonObject 结构解析
            return List.of();
        }
        return List.of();
    }
    
    private Boolean extractBoolean(JsonObject jsonObject, String key) {
        if (jsonObject.containsKey(key)) {
            String value = jsonObject.get(key).toString();
            return Boolean.parseBoolean(value);
        }
        return null;
    }
}
