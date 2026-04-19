package cn.cpoet.jpatcher.mcp.tool;

import com.intellij.mcpserver.McpTool;
import com.intellij.mcpserver.McpToolCallResult;
import com.intellij.mcpserver.McpToolDescriptor;
import kotlin.coroutines.Continuation;
import kotlinx.serialization.json.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author CPoet
 */
public class GetPatchConfTool implements McpTool {
    @Override
    public @NotNull McpToolDescriptor getDescriptor() {
        return null;
    }

    @Override
    public @Nullable Object call(@NotNull JsonObject jsonObject, @NotNull Continuation<? super McpToolCallResult> continuation) {
        return null;
    }
}
