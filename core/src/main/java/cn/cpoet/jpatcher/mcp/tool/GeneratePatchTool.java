package cn.cpoet.jpatcher.mcp.tool;

import com.intellij.mcpserver.McpTool;
import com.intellij.mcpserver.McpToolCallResult;
import com.intellij.mcpserver.McpToolDescriptor;
import com.intellij.mcpserver.McpToolSchema;
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
public class GeneratePatchTool implements McpTool {
    @NotNull
    @Override
    public McpToolDescriptor getDescriptor() {
        String propertiesSchema = """
                {
                  "type": "object",
                  "properties": {
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
                    "compress": {
                      "type": "boolean",
                      "description": "Whether to compress the patch package into a ZIP file. Default is true."
                    },
                    "includePath": {
                      "type": "boolean",
                      "description": "Whether to preserve the directory structure in the patch package. Default is true."
                    },
                    "buildType": {
                      "type": "string",
                      "enum": ["PROJECT", "MODULE", "FILE", "NONE"],
                      "description": "Build type before generating patch: PROJECT (rebuild all modules), MODULE (rebuild affected modules), FILE (compile selected files), NONE (skip build). Default is FILE."
                    },
                    "addModLabel": {
                      "type": "boolean",
                      "description": "Whether to add modification type labels (ADD/MOD/DEL) in the readme file. Default is false."
                    },
                    "cover": {
                      "type": "boolean",
                      "description": "Whether to overwrite existing patch package with the same name. Default is false."
                    }
                  }
                }
                """;
        return new McpToolDescriptor(
                "jpatcher_generate_patch",
                "Generate a patch package for incremental deployment. This tool analyzes selected files or VCS changes and creates a deployable patch package containing compiled outputs, supporting both Spring Boot and standard Java projects.",
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
        return "Function not implemented";
    }
}
