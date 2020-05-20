package cn.kt.ui;

import cn.kt.constant.ExtendFeatureEnum;
import cn.kt.model.Config;
import cn.kt.setting.CheckBoxHolder;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

/**
 * @author chenxiaojing
 */
public interface Configurable {

    TextFieldWithBrowseButton projectFolderBtn = new TextFieldWithBrowseButton();
    TextFieldWithBrowseButton modelFolderBtn = new TextFieldWithBrowseButton();
    TextFieldWithBrowseButton daoFolderBtn = new TextFieldWithBrowseButton();
    TextFieldWithBrowseButton xmlFolderBtn = new TextFieldWithBrowseButton();

    JTextField modelPathField = new JBTextField(15);
    JTextField mapperPathField = new JBTextField(15);
    JTextField xmlPathField = new JBTextField(15);

    JButton setProjectBtn = new JButton("Set-Project-Path");

    /**
     * 构造底部菜单
     * @param config 当前配置
     * @return JBPanel
     */
    default JBPanel buildPanelDown(Config config) {
        JBPanel paneMainDown = new JBPanel(new GridLayout(5, 5, 5, 5));
        paneMainDown.setBorder(JBUI.Borders.empty(2, 80, 100, 40));

        for (ExtendFeatureEnum feature : ExtendFeatureEnum.values()) {
            JCheckBox featureCheckBox = CheckBoxHolder.fetchFeatureCheckBox(config, feature);
            paneMainDown.add(featureCheckBox);
        }
        return paneMainDown;
    }

}
