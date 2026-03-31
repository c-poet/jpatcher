package cn.cpoet.jpatcher.component;

import com.intellij.ui.components.panels.VerticalLayout;

import javax.swing.*;

/**
 * 垂直面板
 *
 * @author CPoet
 */
public class SimpleVPanel extends JPanel {
    public SimpleVPanel() {
        super(new VerticalLayout(-1, -1));
    }
}
