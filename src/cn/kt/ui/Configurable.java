package cn.kt.ui;

import cn.kt.constant.ExtendFeatureEnum;
import cn.kt.model.Config;
import cn.kt.setting.CheckBoxHolder;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author chenxiaojing
 */
public interface Configurable {

    JTextField AUTHOR_FIELD = new JTextField(12);
    JTextField TABLE_NAME_FIELD = new JTextField(12);
    JTextField PRIMARY_KEY_FIELD = new JTextField(12);
    JTextField MODEL_NAME_FIELD = new JTextField(20);
    JTextField MAPPER_NAME_FIELD = new JTextField(20);

    JBTextField MODEL_PACKAGE_FIELD = new JBTextField(12);
    JBTextField MAPPER_PACKAGE_FIELD = new JBTextField(12);

    TextFieldWithBrowseButton PROJECT_FOLDER_BTN = new TextFieldWithBrowseButton();
    TextFieldWithBrowseButton MODEL_FOLDER_BTN = new TextFieldWithBrowseButton();
    TextFieldWithBrowseButton MAPPER_FOLDER_BTN = new TextFieldWithBrowseButton();
    TextFieldWithBrowseButton XML_FOLDER_BTN = new TextFieldWithBrowseButton();

    JTextField MODEL_PATH_FIELD = new JBTextField(15);
    JTextField MAPPER_PATH_FIELD = new JBTextField(15);
    JTextField XML_PATH_FIELD = new JBTextField(15);

    JButton SET_PROJECT_BTN = new JButton("Set Project Path");


    /**
     * 构造头部基本配置
     *
     * @param project 工程信息
     * @param init 是否为默认配置
     * @return JBPanel
     */
    @NotNull
    default JPanel createBasicPanel(Project project, boolean init) {
        JPanel basicPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JPanel panelRow1 = createPanelRow1(init);
        JPanel panelRow2 = createPanelRow2(init);
        JPanel panelRow3 = createPanelRow3(project);
        JPanel panelRow4 = createPanelRow4();

        basicPanel.add(panelRow1);
        basicPanel.add(panelRow2);
        basicPanel.add(panelRow3);
        basicPanel.add(panelRow4);
        return basicPanel;
    }


    /**
     * 构造头部基本配置 第一行
     *
     * @param init 是否为默认配置
     * @return 基本配置第一行
     */
    @NotNull
    default JPanel createPanelRow1(boolean init) {
        JPanel panelRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JPanel authorPanel = new JPanel();
        authorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        authorPanel.add(new JLabel("author : "));
        authorPanel.add(AUTHOR_FIELD);

        JPanel tableNamePanel = new JPanel();
        tableNamePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        tableNamePanel.add(new JLabel("table name : "));
        TABLE_NAME_FIELD.setEnabled(!init);
        tableNamePanel.add(TABLE_NAME_FIELD);

        JPanel primaryKeyPanel = new JPanel();
        primaryKeyPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        primaryKeyPanel.add(new JLabel("primary key : "));
        PRIMARY_KEY_FIELD.setEnabled(!init);
        primaryKeyPanel.add(PRIMARY_KEY_FIELD);

        panelRow1.add(authorPanel);
        panelRow1.add(tableNamePanel);
        panelRow1.add(primaryKeyPanel);
        return panelRow1;
    }


    /**
     * 构造头部基本配置 第二行
     *
     * @param init 是否为默认配置
     * @return 基本配置第二行
     */
    @NotNull
    default JPanel createPanelRow2(boolean init) {
        JPanel panelRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JPanel modelNamePanel = new JPanel();
        modelNamePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        modelNamePanel.add(new JLabel("model name : "));
        MODEL_NAME_FIELD.setEnabled(!init);
        modelNamePanel.add(MODEL_NAME_FIELD);

        JPanel mapperNamePanel = new JPanel();
        mapperNamePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        if (init) {
            mapperNamePanel.add(new JLabel("mapper postfix : "));
        } else {
            mapperNamePanel.add(new JLabel("mapper name : "));
        }
        mapperNamePanel.add(MAPPER_NAME_FIELD);

        panelRow2.add(modelNamePanel);
        panelRow2.add(mapperNamePanel);

        return panelRow2;
    }


    /**
     * 构造头部基本配置 第三行
     *
     * @param project 工程信息
     * @return 基本配置第三行
     */
    @NotNull
    default JPanel createPanelRow3(Project project) {
        JPanel panelRow3 = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JPanel modelPackagePanel = new JPanel();
        modelPackagePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        modelPackagePanel.add(new JBLabel("model package : "));
        modelPackagePanel.add(MODEL_PACKAGE_FIELD);

        JButton modelPackageBtn = new JButton("...");
        modelPackageBtn.addActionListener(actionEvent -> {
            final PackageChooserDialog chooser = new PackageChooserDialog("choose model package", project);
            chooser.selectPackage(MODEL_PACKAGE_FIELD.getText());
            chooser.show();
            final PsiPackage psiPackage = chooser.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            MODEL_PACKAGE_FIELD.setText(packageName);
        });
        modelPackagePanel.add(modelPackageBtn);


        JPanel mapperPackagePanel = new JPanel();
        mapperPackagePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        mapperPackagePanel.add(new JLabel("mapper package : "));
        mapperPackagePanel.add(MAPPER_PACKAGE_FIELD);

        JButton mapperPackageBtn = new JButton("...");
        mapperPackageBtn.addActionListener(actionEvent -> {
            final PackageChooserDialog chooser = new PackageChooserDialog("choose mapper package", project);
            chooser.selectPackage(MAPPER_PACKAGE_FIELD.getText());
            chooser.show();
            final PsiPackage psiPackage = chooser.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            MAPPER_PACKAGE_FIELD.setText(packageName);
        });
        mapperPackagePanel.add(mapperPackageBtn);

        panelRow3.add(modelPackagePanel);
        panelRow3.add(mapperPackagePanel);

        return panelRow3;
    }


    /**
     * 构造头部基本配置 第四行
     *
     * @return 基本配置第四行
     */
    default JPanel createPanelRow4() {
        JPanel panelRow4 = new JPanel(new GridLayout(4, 1, 3, 3));

        JPanel projectFolderPanel = new JPanel();
        projectFolderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        projectFolderPanel.add(new JLabel("project folder : "));
        PROJECT_FOLDER_BTN.setTextFieldPreferredWidth(45);
        PROJECT_FOLDER_BTN.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                PROJECT_FOLDER_BTN.setText(PROJECT_FOLDER_BTN.getText().replaceAll("\\\\", "/"));
            }
        });
        projectFolderPanel.add(PROJECT_FOLDER_BTN);


        SET_PROJECT_BTN.addActionListener(e -> {
            MODEL_FOLDER_BTN.setText(PROJECT_FOLDER_BTN.getText());
            MAPPER_FOLDER_BTN.setText(PROJECT_FOLDER_BTN.getText());
            XML_FOLDER_BTN.setText(PROJECT_FOLDER_BTN.getText());
        });
        projectFolderPanel.add(SET_PROJECT_BTN);

        JPanel modelFolderPanel = new JPanel();
        modelFolderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        modelFolderPanel.add(new JLabel("model folder : "));
        MODEL_FOLDER_BTN.setTextFieldPreferredWidth(45);
        MODEL_FOLDER_BTN.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                MODEL_FOLDER_BTN.setText(MODEL_FOLDER_BTN.getText().replaceAll("\\\\", "/"));
            }
        });
        modelFolderPanel.add(MODEL_FOLDER_BTN);
        modelFolderPanel.add(new JLabel("package path : "));
        modelFolderPanel.add(MODEL_PATH_FIELD);

        JPanel mapperFolderPanel = new JPanel();
        mapperFolderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        mapperFolderPanel.add(new JLabel("mapper folder : "));
        MAPPER_FOLDER_BTN.setTextFieldPreferredWidth(45);
        MAPPER_FOLDER_BTN.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                MAPPER_FOLDER_BTN.setText(MAPPER_FOLDER_BTN.getText().replaceAll("\\\\", "/"));
            }
        });
        mapperFolderPanel.add(MAPPER_FOLDER_BTN);
        mapperFolderPanel.add(new JLabel("package path : "));
        mapperFolderPanel.add(MAPPER_PATH_FIELD);

        JPanel xmlFolderPanel = new JPanel();
        xmlFolderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        xmlFolderPanel.add(new JLabel("xml folder : "));
        XML_FOLDER_BTN.setTextFieldPreferredWidth(45);
        XML_FOLDER_BTN.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()) {
        });
        xmlFolderPanel.add(XML_FOLDER_BTN);
        xmlFolderPanel.add(new JLabel("package path : "));
        xmlFolderPanel.add(XML_PATH_FIELD);

        panelRow4.add(projectFolderPanel);
        panelRow4.add(modelFolderPanel);
        panelRow4.add(mapperFolderPanel);
        panelRow4.add(xmlFolderPanel);

        return panelRow4;
    }


    /**
     * 构造底部配置表单
     *
     * @param config 当前配置
     * @return JBPanel
     */
    default JBPanel createFeaturePanel(Config config) {
        JBPanel featurePanel = new JBPanel(new GridLayout(5, 5, 5, 5));
        featurePanel.setBorder(JBUI.Borders.empty(20, 80, 50, 40));

        for (ExtendFeatureEnum feature : ExtendFeatureEnum.values()) {
            JCheckBox featureCheckBox = CheckBoxHolder.fetchFeatureCheckBox(config, feature);
            featurePanel.add(featureCheckBox);
        }
        return featurePanel;
    }

    /**
     * 填充顶部配置表单
     *
     * @param config 当前配置
     * @param init 是否为默认配置
     */
    default void fillBasicPanel(Config config, boolean init) {
        AUTHOR_FIELD.setText(config.getAuthor());
        TABLE_NAME_FIELD.setText(config.getTableName());
        PRIMARY_KEY_FIELD.setText(config.getPrimaryKey());
        MODEL_NAME_FIELD.setText(config.getModelName());
        if (init) {
            MAPPER_NAME_FIELD.setText(config.getMapperPostfix());
        } else {
            MAPPER_NAME_FIELD.setText(config.getMapperName());
        }

        MODEL_PACKAGE_FIELD.setText(config.getModelPackage());
        MAPPER_PACKAGE_FIELD.setText(config.getMapperPackage());

        PROJECT_FOLDER_BTN.setText(config.getProjectFolder());
        MODEL_FOLDER_BTN.setText(config.getModelTargetFolder());
        MAPPER_FOLDER_BTN.setText(config.getMapperTargetFolder());
        XML_FOLDER_BTN.setText(config.getXmlTargetFolder());

        MODEL_PATH_FIELD.setText(config.getModelPath());
        MAPPER_PATH_FIELD.setText(config.getMapperPath());
        XML_PATH_FIELD.setText(config.getXmlPath());
    }

}
