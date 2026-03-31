package cn.cpoet.jpatcher.impl223.actions.patch;

import cn.cpoet.jpatcher.actions.patch.GenPatchConfPanel9;
import cn.cpoet.jpatcher.util.I18nUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

/**
 * @author CPoet
 */
public class GenPatchConfPanel9Impl implements GenPatchConfPanel9 {

    @Override
    public void cpbOutputFolderTextField(Project project, TextFieldWithBrowseButton btn) {
        btn.addBrowseFolderListener(I18nUtil.t("actions.patch.GenPatchPackageAction.config.outputFolder"), null
                , project, FileChooserDescriptorFactory.createSingleFolderDescriptor());
    }
}
