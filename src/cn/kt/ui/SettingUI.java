package cn.kt.ui;

import cn.kt.model.Config;
import cn.kt.setting.PersistentService;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;

/**
 * 设置界面
 * Created by kangtian on 2018/8/3.
 */
public class SettingUI extends JDialog implements Configurable {

    private JPanel mainPanel = new JBPanel<>(new GridLayout(2, 1));

    private PersistentService persistentService;
    private Config config;

    public SettingUI() {
        setContentPane(mainPanel);
    }

    public void createUI(Project project) {

        String projectFolder = project.getBasePath();
        mainPanel.setPreferredSize(new Dimension(0, 0));

        persistentService = PersistentService.getInstance(project);
        config = persistentService.getInitConfig(null, projectFolder);

        JPanel basicPanel = createBasicPanel(project, true);
        fillBasicPanel(config, true);

        JBPanel featurePanel = createFeaturePanel(config);

        mainPanel.add(basicPanel);
        mainPanel.add(featurePanel);
    }


    public boolean isModified() {
        return true;
    }

    public void apply() {
        config.setAuthor(AUTHOR_FIELD.getText());
        config.setMapperPostfix(MAPPER_NAME_FIELD.getText());
        config.setModelPackage(MODEL_PACKAGE_FIELD.getText());
        config.setMapperPackage(MAPPER_PACKAGE_FIELD.getText());
        config.setProjectFolder(PROJECT_FOLDER_BTN.getText());
        config.setModelTargetFolder(MODEL_FOLDER_BTN.getText());
        config.setMapperTargetFolder(MAPPER_FOLDER_BTN.getText());
        config.setXmlTargetFolder(XML_FOLDER_BTN.getText());

        this.persistentService.setInitConfig(config);
    }

    @Override
    public JPanel getContentPane() {
        return mainPanel;
    }

}
