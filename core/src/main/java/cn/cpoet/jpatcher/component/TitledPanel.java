package cn.cpoet.jpatcher.component;

import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.TitledSeparator;

import javax.swing.*;

/**
 * 标题面板
 *
 * @author CPoet
 */
public class TitledPanel extends JPanel {

    /** 标题 */
    private final TitledSeparator titled;

    public TitledPanel() {
        super(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, true));
        this.titled = new TitledSeparator();
        add(this.titled);
    }

    public TitledPanel(String title) {
        this();
        setTitled(title);
    }

    public void setTitled(String title) {
        this.titled.setText(title);
    }
}
