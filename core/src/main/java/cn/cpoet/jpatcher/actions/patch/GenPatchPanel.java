package cn.cpoet.jpatcher.actions.patch;

import cn.cpoet.jpatcher.constant.CommonConst;
import cn.cpoet.jpatcher.constant.FileBuildTypeExtEnum;
import cn.cpoet.jpatcher.exception.JPatcherException;
import cn.cpoet.jpatcher.model.FileInfo;
import cn.cpoet.jpatcher.model.TreeNodeInfo;
import cn.cpoet.jpatcher.setting.Setting;
import cn.cpoet.jpatcher.util.*;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.task.ProjectTaskManager;
import com.intellij.ui.CheckboxTreeListener;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.JBSplitter;
import com.intellij.util.ui.JBDimension;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 补丁包生成视图
 *
 * @author CPoet
 */
public class GenPatchPanel extends JBSplitter {

    private final static Logger LOGGER = LoggerFactory.getLogger(GenPatchPanel.class);

    private final Project project;
    private final GenPatchSetting setting;
    private final DialogWrapper dialogWrapper;
    private final AtomicInteger checkedCount;
    private final GenPatchConfPanel confPanel;
    private final GenPatchTreePanel treePanel;

    public GenPatchPanel(Project project, Object[] selectedItems, DialogWrapper dialogWrapper) {
        this.project = project;
        this.dialogWrapper = dialogWrapper;
        this.setting = GenPatchSetting.getInstance(project);
        setPreferredSize(new JBDimension(setting.getState().width, setting.getState().height));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setting.getState().width = getWidth();
                setting.getState().height = getHeight();
            }
        });
        treePanel = new GenPatchTreePanel(project, selectedItems);
        setFirstComponent(treePanel);
        confPanel = new GenPatchConfPanel(project, this);
        setSecondComponent(confPanel);
        checkedCount = new AtomicInteger(getTreeCheckedNodes().length);
        addCheckboxTreeListener();
    }

    private void addCheckboxTreeListener() {
        treePanel.getTree().addCheckboxTreeListener(new CheckboxTreeListener() {
            @Override
            public void nodeStateChanged(@NotNull CheckedTreeNode node) {
                checkedCount.getAndAdd(node.isChecked() ? 1 : -1);
                updateBtnStatus();
            }
        });
        updateBtnStatus();
    }

    public void generate() {
        UITaskUtil.runProgress(project, "Generating", this::generate);
    }

    private void generate(ProgressIndicator indicator) {
        try {
            GenPatchSetting.State state = setting.getState();
            indicator.setFraction(0.1);
            if (!buildGenPatchBefore(indicator)) {
                return;
            }
            indicator.setFraction(0.3);
            indicator.setText("Wait index refresh");
            // 等待文件索引完成（特别是重新编译的情况下）
            if (DumbService.isDumb(project)) {
                DumbService.getInstance(project).waitForSmartMode();
            }
            indicator.setFraction(0.35);
            GenPatchBean patch = getGenPatch(indicator);
            if (patch.isFailed()) {
                return;
            }
            indicator.setFraction(0.5);
            String path = doGenerate(patch, indicator);
            if (StringUtils.isBlank(path)) {
                return;
            }
            indicator.setText("Generate after");
            indicator.setFraction(0.8);
            if (state.openOutputFolder) {
                String patchPath = FilenameUtils.separatorsToSystem(path);
                if (state.compress) {
                    FileUtil.selectFile(patchPath);
                } else {
                    FileUtil.openFolder(patchPath);
                }
            }
            state.lastFileNamePrefix = confPanel.getFileNamePrefix();
            state.lastFileName = confPanel.getFileName();
            indicator.setFraction(0.98);
            doOpenReplacePatch(path);
            indicator.setFraction(1);
        } catch (Exception e) {
            LOGGER.error("Failed to generate the patch: {}", e.getMessage(), e);
            NotificationUtil.initBalloonError(e.getMessage()).notify(project);
        }
    }

    protected void doOpenReplacePatch(String path) {
        if (!setting.getState().openReplacePatch) {
            return;
        }
        String patchAssistant2JPath = Setting.getInstance().getState().patchAssistant2JPath;
        if (StringUtils.isBlank(patchAssistant2JPath)) {
            NotificationUtil.initBalloonError("The PatchAssistant2J path is not configured").notify(project);
            return;
        }
        try {
            Runtime.getRuntime().exec(new String[]{patchAssistant2JPath, "--patch=" + path});
        } catch (Exception e) {
            NotificationUtil.initBalloonError("Failed to launch PatchAssistant2J, please check if the path configuration is correct").notify(project);
        }
    }

    protected void updateBtnStatus() {
        GenPatchSetting.State state = setting.getState();
        dialogWrapper.setOKActionEnabled(checkedCount.get() > 0
                && StringUtils.isNotBlank(state.outputFolder)
                && StringUtils.isNotBlank(confPanel.getFileName()));
    }


    protected String doGenerate(GenPatchBean patch, ProgressIndicator indicator) {
        indicator.setText("Generate patch");
        GenPatchSetting.State state = setting.getState();
        if (state.compress) {
            return doGenerateCompress(patch);
        }
        String path = getWriteFilePath(patch);
        List<GenPatchItemBean> items = patch.getItems();
        for (GenPatchItemBean item : items) {
            String filePath = path;
            if (state.includePath) {
                if (GenPatchProjectTypeEnum.SPRING.equals(patch.getProjectType())) {
                    if (item.getPatchModule().isApp()) {
                        filePath = FilenameUtils.concat(filePath, SpringUtil.SB_CLASSES_PATH);
                    } else {
                        filePath = FilenameUtils.concat(filePath, SpringUtil.SB_LIB_PATH);
                        filePath = FilenameUtils.concat(filePath, item.getPatchModule().getModule().getName());
                    }
                }
                filePath = FilenameUtils.concat(filePath, item.getFullPath());
            }
            FileUtil.writeToFile(item.getOutputFile(), FilenameUtils.concat(filePath, item.getOutputFile().getName()));
            doWriteAttachOutputFilesToFile(item, filePath);
        }
        doWriteReadmeFileToFile(patch, path);
        return path;
    }

    protected String getWriteFilePath(GenPatchBean patch) {
        String path = FilenameUtils.concat(patch.getOutputFolder(), patch.getFileName());
        File file = new File(path);
        if (!file.exists()) {
            return path;
        }
        if (setting.getState().cover) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (Exception e) {
                LOGGER.error("File deletion failed: {}", file.getPath(), e);
            }
            return path;
        }
        int i = 0;
        do {
            file = new File(path + "(" + i++ + ")");
        } while (file.exists());
        return file.getPath();
    }

    protected void doWriteAttachOutputFilesToFile(GenPatchItemBean patchItem, String filePath) {
        if (CollectionUtils.isNotEmpty(patchItem.getAttachOutputFiles())) {
            for (VirtualFile attach : patchItem.getAttachOutputFiles()) {
                FileUtil.writeToFile(attach, FilenameUtils.concat(filePath, attach.getName()));
            }
        }
    }

    protected void doWriteReadmeFileToFile(GenPatchBean patch, String path) {
        String filePath = FilenameUtils.concat(path, "README.txt");
        FileUtil.writeToFile(patch.getDesc().toString().getBytes(), filePath);
    }

    protected String doGenerateCompress(GenPatchBean patch) {
        String filePath = getWriteFileName(patch);
        FileUtil.mkdirParent(filePath);
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
            List<GenPatchItemBean> items = patch.getItems();
            for (GenPatchItemBean item : items) {
                doWritePatchItemToZip(patch, zipOutputStream, item);
                doWriteAttachOutputFilesToZip(patch, zipOutputStream, item);
            }
            doWriteReadmeFileToZip(zipOutputStream, patch);
        } catch (IOException e) {
            throw new JPatcherException("Patch generate fail", e);
        }
        return filePath;
    }

    protected String getWriteFileName(GenPatchBean patch) {
        String filePath = FilenameUtils.concat(patch.getOutputFolder(), patch.getFileName());
        File file = new File(filePath + GenPatchConst.PATCH_FULL_FILE_EXT);
        if (!file.exists()) {
            return file.getPath();
        }
        if (setting.getState().cover) {
            if (!file.delete()) {
                LOGGER.warn("File deletion failed:{}", file.getPath());
            }
            return file.getPath();
        }
        int i = 0;
        do {
            file = new File(filePath + "(" + i++ + ")" + GenPatchConst.PATCH_FULL_FILE_EXT);
        } while (file.exists());
        return file.getPath();
    }

    protected void doWriteAttachOutputFilesToZip(GenPatchBean patch, ZipOutputStream zipOutputStream, GenPatchItemBean item) {
        if (CollectionUtils.isNotEmpty(item.getAttachOutputFiles())) {
            for (VirtualFile attach : item.getAttachOutputFiles()) {
                doWritePatchItemToZip(patch, zipOutputStream, item, attach);
            }
        }
    }

    protected void doWriteReadmeFileToZip(ZipOutputStream zipOutputStream, GenPatchBean patch) {
        ZipEntry zipEntry = new ZipEntry(GenPatchConst.PATCH_DESC_FILE_NAME);
        zipEntry.setComment(GenPatchConst.PATCH_DESC_FILE_COMMENT);
        ZipUtil.writeEntry(zipOutputStream, zipEntry, patch.getDesc().toString().getBytes());
    }

    protected void doWritePatchItemToZip(GenPatchBean patch, ZipOutputStream zipOutputStream, GenPatchItemBean patchItem) {
        VirtualFile outputFile = patchItem.getOutputFile();
        doWritePatchItemToZip(patch, zipOutputStream, patchItem, outputFile);
    }

    protected void doWritePatchItemToZip(GenPatchBean patch, ZipOutputStream zipOutputStream, GenPatchItemBean patchItem, VirtualFile file) {
        ZipEntry zipEntry = createZipEntry(patch, patchItem, file);
        ZipUtil.writeEntry(zipOutputStream, zipEntry, file);
    }

    protected ZipEntry createZipEntry(GenPatchBean patch, GenPatchItemBean patchItem, VirtualFile file) {
        GenPatchSetting.State state = setting.getState();
        ZipEntry zipEntry;
        if (state.includePath) {
            String filePath;
            if (GenPatchProjectTypeEnum.SPRING.equals(patch.getProjectType())) {
                if (patchItem.getPatchModule().isApp()) {
                    filePath = String.join(FileUtil.UNIX_SEPARATOR, SpringUtil.SB_CLASSES_PATH, patchItem.getFullPath(), file.getName());
                } else {
                    filePath = String.join(FileUtil.UNIX_SEPARATOR, SpringUtil.SB_LIB_PATH, patchItem.getFullPath(), file.getName());
                }
            } else {
                filePath = String.join(FileUtil.UNIX_SEPARATOR, patchItem.getFullPath(), file.getName());
            }
            zipEntry = new ZipEntry(filePath);
        } else {
            zipEntry = new ZipEntry(file.getName());
        }
        zipEntry.setComment(file.getPath());
        return zipEntry;
    }


    protected GenPatchBean getGenPatch(ProgressIndicator indicator) {
        TreeNodeInfo[] checkedNodes = getTreeCheckedNodes();
        indicator.setText("Generate patch info");
        return doGetGenPatch(checkedNodes);
    }

    protected boolean buildGenPatchBefore(ProgressIndicator indicator) {
        GenPatchBuildTypeEnum buildTypeEnum = GenPatchBuildTypeEnum.ofCode(setting.getState().buildType);
        indicator.setText("Patch build type: " + buildTypeEnum.getCode());
        boolean isOk = switch (buildTypeEnum) {
            case PROJECT -> {
                indicator.setText("Wait project build");
                yield buildProjectGenPatch();
            }
            case MODULE -> {
                indicator.setText("Wait module build");
                yield buildModuleGenPatch();
            }
            case FILE -> {
                indicator.setText("Wait file build");
                yield buildFileGenPatch();
            }
            default -> true;
        };
        if (!isOk) {
            UITaskUtil.runUI(() -> Messages.showWarningDialog(project, I18nUtil.t("actions.patch.GenPatchPackageAction.buildFailedWarnMsg"),
                    I18nUtil.t("message.warn.title")));
        }
        return isOk;
    }

    protected boolean buildProjectGenPatch() {
        try {
            return Optional.ofNullable(ProjectTaskManager.getInstance(project)
                            .rebuildAllModules()
                            .blockingGet(6, TimeUnit.MINUTES))
                    .map(result -> !result.hasErrors())
                    .orElse(true);
        } catch (Exception e) {
            LOGGER.info("Build project failed", e);
        }
        return false;
    }

    protected boolean buildModuleGenPatch() {
        TreeNodeInfo[] checkedNodes = getTreeCheckedNodes();
        Set<Module> modules = new HashSet<>();
        for (TreeNodeInfo checkedNode : checkedNodes) {
            Module module = TreeUtil.findModule(checkedNode);
            if (module != null) {
                modules.add(module);
            }
        }
        try {
            return Optional.ofNullable(ProjectTaskManager.getInstance(project)
                            .rebuild(modules.toArray(Module[]::new))
                            .blockingGet(6, TimeUnit.MINUTES))
                    .map(result -> !result.hasErrors())
                    .orElse(true);
        } catch (Exception e) {
            LOGGER.info("Build module failed", e);
        }
        return false;
    }

    protected boolean buildFileGenPatch() {
        TreeNodeInfo[] checkedNodes = getTreeCheckedNodes();
        VirtualFile[] files = Arrays.stream(checkedNodes)
                .map(nodeInfo -> (VirtualFile) nodeInfo.getObject())
                .toArray(VirtualFile[]::new);
        try {
            return Optional.ofNullable(ProjectTaskManager.getInstance(project)
                            .compile(files)
                            .blockingGet(6, TimeUnit.MINUTES))
                    .map(result -> !result.hasErrors())
                    .orElse(true);
        } catch (Exception e) {
            LOGGER.info("Build file failed", e);
        }
        return false;
    }

    protected GenPatchBean doGetGenPatch(TreeNodeInfo[] treeNodeInfos) {
        GenPatchBean patch = createGenPatch();
        patch.getDesc().append("File Name: ").append(patch.getFileName());
        String patchDesc = getPatchDesc();
        if (StringUtils.isNotBlank(patchDesc)) {
            patch.getDesc().append('\n').append("Patch Desc:\n").append(patchDesc);
        }
        patch.getDesc().append("\n\n").append("File Paths:");
        Map<GenPatchModuleBean, List<TreeNodeInfo>> moduleFilesMapping = getModuleFilesMapping(patch, treeNodeInfos);
        for (Map.Entry<GenPatchModuleBean, List<TreeNodeInfo>> entry : moduleFilesMapping.entrySet()) {
            for (TreeNodeInfo nodeInfo : entry.getValue()) {
                addPatchItem(patch, entry.getKey(), (VirtualFile) nodeInfo.getObject());
                if (patch.isFailed()) {
                    return patch;
                }
            }
        }
        return patch;
    }

    private void addPatchItem(GenPatchBean patch, GenPatchModuleBean patchModule, VirtualFile file) {
        addPatchItem(patch, patchModule, file, true);
    }

    private void addPatchItem(GenPatchBean patch, GenPatchModuleBean patchModule, VirtualFile sourceFile, boolean isMapStruct) {
        FileInfo fileInfo = patchModule.getModule() == null ? FileUtil.getFileInfo(sourceFile) : FileUtil.getFileInfo(patchModule.getModule(), sourceFile);
        if (fileInfo.getOutputFile() == null) {
            patch.setFailed(true);
            UITaskUtil.runUI(() -> Messages.showWarningDialog(project, I18nUtil.tr("actions.patch.GenPatchPackageAction.notFoundOutputFile", sourceFile.getName())
                    , I18nUtil.t("message.warn.title")));
            return;
        }
        GenPatchItemBean patchItem = new GenPatchItemBean();
        patchItem.setPatchModule(patchModule);
        patchItem.setSourceFile(fileInfo.getSourceFile());
        patchItem.setOutputFile(fileInfo.getOutputFile());
        String relativePath = FileUtil.removeStartSeparator(fileInfo.getOutputRelativePath());
        patchItem.setFullPath(FilenameUtils.getFullPathNoEndSeparator(relativePath));
        addPatchReplacePathInfo(patch, patchItem);
        addInner2AttachOutFiles(patchItem);
        patch.getItems().add(patchItem);
        if (isMapStruct) {
            addMapStructMapperImpl(patch, patchItem);
        }
    }

    private void addPatchReplacePathInfo(GenPatchBean patch, GenPatchItemBean patchItem) {
        patch.getDesc().append("\n");
        GenPatchModuleBean patchModule = patchItem.getPatchModule();
        if (setting.getState().addModLabel) {
            patch.getDesc().append(GenPatchConst.CHANGE_TYPE_MOD);
        }
        if (!setting.getState().includePath) {
            patch.getDesc().append(patchItem.getOutputFile().getName()).append("\t");
        }
        if (GenPatchProjectTypeEnum.SPRING.equals(patch.getProjectType())) {
            if (patchModule.isApp()) {
                patch.getDesc().append(SpringUtil.SB_CLASSES_PATH).append(FileUtil.UNIX_SEPARATOR);
            } else if (patchModule.getModule() == null) {
                // 依赖的外部文件没有所属的项目模块
                patch.getDesc().append(SpringUtil.SB_LIB_PATH).append(FileUtil.UNIX_SEPARATOR);
            } else {
                patch.getDesc().append(SpringUtil.SB_LIB_PATH).append(FileUtil.UNIX_SEPARATOR)
                        .append(patchModule.getModule().getName()).append(FileUtil.UNIX_SEPARATOR);
            }
        }
        if (StringUtils.isBlank(patchItem.getFullPath())) {
            patch.getDesc().append(patchItem.getOutputFile().getName());
        } else {
            patch.getDesc().append(patchItem.getFullPath()).append(FileUtil.UNIX_SEPARATOR).append(patchItem.getOutputFile().getName());
        }
    }

    private void addInner2AttachOutFiles(GenPatchItemBean patchItem) {
        VirtualFile[] innerOutputFiles = ClassUtil.getInnerOutputFiles(patchItem.getOutputFile());
        for (VirtualFile innerOutputFile : innerOutputFiles) {
            patchItem.getAndInitAttachOutputFiles().add(innerOutputFile);
        }
    }

    private void addMapStructMapperImpl(GenPatchBean patch, GenPatchItemBean patchItem) {
        VirtualFile sourceFile = patchItem.getSourceFile();
        FileBuildTypeExtEnum fileExt = MapStructUtil.getSupportBuildTypeExt(sourceFile);
        if (fileExt == null) {
            return;
        }
        PsiClass[] classes = ReadAction.compute(() -> {
            PsiJavaFile psiFile = Objects.requireNonNull((PsiJavaFile) PsiManager.getInstance(project).findFile(sourceFile));
            return psiFile.getClasses();
        });
        for (PsiClass psiClass : classes) {
            String mapperImplName = ReadAction.compute(() -> MapStructUtil.getMapperImplName(psiClass));
            if (StringUtils.isBlank(mapperImplName)) {
                continue;
            }
            String filePath = ClassUtil.convertNameToPath(mapperImplName) + FilenameUtils.EXTENSION_SEPARATOR + fileExt.getSourceExt();
            VirtualFile mapperImplFile = FileUtil.getSourceFile(patchItem.getPatchModule().getModule(), filePath);
            if (mapperImplFile == null) {
                addMapStructMapperImpl(patch, patchItem.getPatchModule(), filePath);
                continue;
            }
            addPatchItem(patch, patchItem.getPatchModule(), mapperImplFile, false);
        }
    }

    private void addMapStructMapperImpl(GenPatchBean patch, GenPatchModuleBean patchModule, String filePath) {
        String outputFilePath = FileUtil.getOutputFilePath(filePath);
        VirtualFile outputFile = FileUtil.getOutputFile(patchModule.getModule(), outputFilePath);
        if (outputFile != null) {
            GenPatchItemBean patchItem = new GenPatchItemBean();
            patchItem.setPatchModule(patchModule);
            patchItem.setSourceFile(null);
            patchItem.setOutputFile(outputFile);
            patchItem.setFullPath(FilenameUtils.getFullPathNoEndSeparator(FileUtil.removeStartSeparator(filePath)));
            addPatchReplacePathInfo(patch, patchItem);
            addInner2AttachOutFiles(patchItem);
            patch.getItems().add(patchItem);
        }
    }

    protected Map<GenPatchModuleBean, List<TreeNodeInfo>> getModuleFilesMapping(GenPatchBean patch, TreeNodeInfo[] treeNodeInfos) {
        Map<Module, GenPatchModuleBean> patchModuleCache = new HashMap<>();
        Map<GenPatchModuleBean, List<TreeNodeInfo>> moduleFilesMapping = new HashMap<>();
        for (TreeNodeInfo checkedNode : treeNodeInfos) {
            Module module = TreeUtil.findModule(checkedNode);
            GenPatchModuleBean patchModule = patchModuleCache.computeIfAbsent(module, k -> createGenPatchModule(k, patch));
            moduleFilesMapping.computeIfAbsent(patchModule, k -> new LinkedList<>()).add(checkedNode);
        }
        return moduleFilesMapping;
    }

    protected GenPatchModuleBean createGenPatchModule(Module module, GenPatchBean patch) {
        GenPatchModuleBean patchModule = new GenPatchModuleBean();
        if (module != null) {
            patchModule.setModule(module);
            if (GenPatchProjectTypeEnum.SPRING.equals(patch.getProjectType())) {
                ReadAction.run(() -> patchModule.setApp(SpringUtil.isSpringAppModule(project, module)));
            } else {
                patchModule.setApp(false);
            }
        }
        return patchModule;
    }

    protected GenPatchBean createGenPatch() {
        GenPatchSetting.State state = setting.getState();
        GenPatchBean patch = new GenPatchBean();
        patch.setOutputFolder(state.outputFolder);
        patch.setFileName(getFileName());
        ReadAction.run(() -> {
            if (SpringUtil.hasSpringLibrary(project)) {
                patch.setProjectType(GenPatchProjectTypeEnum.SPRING);
            } else {
                patch.setProjectType(GenPatchProjectTypeEnum.NONE);
            }
        });
        return patch;
    }

    protected String getPatchDesc() {
        return treePanel.getPatchDesc();
    }

    protected String getFileName() {
        return confPanel.getFileName();
    }

    protected TreeNodeInfo[] getTreeCheckedNodes() {
        GenPatchTree tree = treePanel.getTree();
        return tree.getCheckedNodes(TreeNodeInfo.class, (nodeInfo) -> {
            // 内部内后续统一处理，需要移除内部类
            if (nodeInfo.getName().endsWith(CommonConst.FILE_EXT_CLASS_FULL)) {
                return !nodeInfo.getName().contains(CommonConst.INNER_CLASS_NAME_SPE);
            }
            return true;
        });
    }
}
