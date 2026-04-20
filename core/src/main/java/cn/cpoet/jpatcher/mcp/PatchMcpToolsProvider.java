package cn.cpoet.jpatcher.mcp;

import cn.cpoet.jpatcher.mcp.tool.GeneratePatchTool;
import com.intellij.mcpserver.McpTool;
import com.intellij.mcpserver.McpToolsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author CPoet
 */
public class PatchMcpToolsProvider implements McpToolsProvider {

    @NotNull
    @Override
    public List<McpTool> getTools() {
        return List.of(new GeneratePatchTool());
    }
}
