package cn.cpoet.jpatcher.mcp.tool;

import cn.cpoet.jpatcher.actions.patch.GenPatchSetting;
import com.intellij.mcpserver.McpCallInfoKt;
import com.intellij.mcpserver.McpTool;
import com.intellij.mcpserver.McpToolCallResult;
import com.intellij.mcpserver.McpToolDescriptor;
import com.intellij.mcpserver.McpToolSchema;
import com.intellij.openapi.project.Project;
import kotlin.coroutines.Continuation;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * @author CPoet
 */
public class GetPatchConfTool implements McpTool {
    @NotNull
    @Override
    public McpToolDescriptor getDescriptor() {
        String propertiesSchema = """
                {
                  "type": "object",
                  "properties": {}
                }
                """;
        return new McpToolDescriptor(
                "jp_get_gen_patch_conf",
                "获取项目应用增量补丁包生成配置",
                new McpToolSchema(
                        (JsonObject) Json.Default.parseToJsonElement(propertiesSchema),
                        Set.of(),
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
        return GenPatchSetting.getInstance(project).getState();
    }
}
