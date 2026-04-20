package cn.cpoet.jpatcher.mcp;

import cn.cpoet.jpatcher.actions.patch.GenPatchSetting;
import cn.cpoet.jpatcher.mcp.tool.GenPatchTool;
import cn.cpoet.jpatcher.mcp.tool.GetPatchConfTool;
import cn.cpoet.jpatcher.mcp.tool.GetTargetPathTool;
import cn.cpoet.jpatcher.mcp.tool.OpenOutputTool;
import com.intellij.mcpserver.McpTool;
import com.intellij.mcpserver.McpToolsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * JPatcher MCP tool provider
 *
 * @author CPoet
 */
public class JPatcherMcpToolsProvider implements McpToolsProvider {

    @NotNull
    @Override
    public List<McpTool> getTools() {
        return List.of(
            new GetPatchConfTool(),
            new GenPatchTool(),
            new GetTargetPathTool(),
            new OpenOutputTool()
        );
    }
}
