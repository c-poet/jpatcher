package cn.cpoet.jpatcher.util;

import cn.cpoet.jpatcher.constant.CommonConst;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 模板替换工具
 * <p>{@link org.apache.velocity.app.Velocity}</p>
 *
 * @author CPoet
 */
public class VelocityUtil {

    private static class ENGINE_HOLDER {
        public final static VelocityEngine ENGINE = new VelocityEngine();
    }

    public static String render(String template, Map<String, Object> properties) {
        VelocityContext context = new VelocityContext(properties);
        context.put("TOOL_NAME", CommonConst.TOOL_NAME);
        return evaluate(template, context);
    }

    public static String render(String template, Project project, Map<String, Object> properties) {
        Map<String, Object> newProperties = new HashMap<>(properties);
        Properties defaultProperties = FileTemplateManager.getInstance(project).getDefaultProperties();
        defaultProperties.forEach((k, v) -> newProperties.put(String.valueOf(k), v));
        return render(template, newProperties);
    }

    public static String evaluate(String template, VelocityContext context) {
        StringWriter writer = new StringWriter();
        boolean isOk = ENGINE_HOLDER.ENGINE.evaluate(context, writer, VelocityUtil.class.getName(), template);
        return isOk ? writer.toString() : template;
    }
}
