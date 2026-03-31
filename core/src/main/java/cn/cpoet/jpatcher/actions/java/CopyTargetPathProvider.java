package cn.cpoet.jpatcher.actions.java;

import cn.cpoet.jpatcher.util.FileUtil;
import cn.cpoet.jpatcher.util.I18nUtil;
import cn.cpoet.jpatcher.util.SpringUtil;
import com.intellij.ide.actions.DumbAwareCopyPathProvider;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author CPoet
 */
public class CopyTargetPathProvider extends DumbAwareCopyPathProvider {

    public CopyTargetPathProvider() {
        Presentation presentation = getTemplatePresentation();
        presentation.setText(I18nUtil.td("actions.java.CopyTargetPathProvider.title"));
    }

    @Nullable
    @Override
    public String getPathToElement(@NotNull Project project, @Nullable VirtualFile virtualFile, @Nullable Editor editor) {
        String outputFilePath = getOutputFilePath(project, virtualFile);
        if (StringUtils.isBlank(outputFilePath)) {
            return outputFilePath;
        }
        // 判断是否是Spring应用
        if (!SpringUtil.hasSpringLibrary(project)) {
            return outputFilePath;
        }
        Module module = ModuleUtil.findModuleForFile(virtualFile, project);
        if (module == null) {
            return outputFilePath;
        }
        // 判断是否服务所在模块
        if (SpringUtil.isSpringAppModule(project, module)) {
            return SpringUtil.SB_LIB_PATH + FileUtil.UNIX_SEPARATOR + module.getName() + FileUtil.UNIX_SEPARATOR + outputFilePath;
        }
        return SpringUtil.SB_CLASSES_PATH + FileUtil.UNIX_SEPARATOR + outputFilePath;
    }

    private String getOutputFilePath(Project project, VirtualFile virtualFile) {
        if (virtualFile == null) {
            return null;
        }
        VirtualFile sourceRootForFile = ProjectFileIndex.getInstance(project).getSourceRootForFile(virtualFile);
        if (sourceRootForFile == null) {
            return null;
        }
        String relativePath = VfsUtil.getRelativePath(virtualFile, sourceRootForFile);
        if (relativePath == null) {
            return null;
        }
        String ext = FileUtil.getBuildExt(relativePath);
        if (ext != null) {
            relativePath = FilenameUtils.removeExtension(relativePath) + FilenameUtils.EXTENSION_SEPARATOR + ext;
        }
        return relativePath;
    }
}
