package cn.cpoet.jpatcher.actions.file;

import cn.cpoet.jpatcher.util.FileUtil;
import cn.cpoet.jpatcher.util.I18nUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * 在资源管理器中打开编码输出文件
 *
 * @author CPoet
 */
public class OpenOutputFileInExplorerAction extends OpenOutputFileAction {

    public OpenOutputFileInExplorerAction() {
        super(I18nUtil.td("actions.file.OpenOutputFileInExplorerAction.title"),
                I18nUtil.td("actions.file.OpenOutputFileInExplorerAction.description"));
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        FileUtil.selectFile(outputFile);
    }
}
