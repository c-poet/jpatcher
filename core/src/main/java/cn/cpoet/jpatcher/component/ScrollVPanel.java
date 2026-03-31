package cn.cpoet.jpatcher.component;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;

/**
 * 垂直滚动面板
 *
 * @author CPoet
 */
public class ScrollVPanel extends JBScrollPane {

    private final SimpleVPanel viewPanel;

    public ScrollVPanel() {
        viewPanel = new SimpleVPanel();
        setViewportView(viewPanel);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
    }

    public SimpleVPanel getView() {
        return viewPanel;
    }

    public void add2View(JComponent component) {
        viewPanel.add(component);
    }

    public void remove4View(int index) {
        viewPanel.remove(index);
    }
}
