package cn.cpoet.jpatcher.component;

import com.intellij.ui.components.panels.HorizontalLayout;

import javax.swing.*;

/**
 * 水平面板
 *
 * @author CPoet
 */
public class SimpleHPanel extends JPanel {
    public SimpleHPanel() {
        super(new HorizontalLayout(-1, -1));
    }
}
