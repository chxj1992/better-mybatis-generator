package cn.kt.ui;

import cn.kt.model.Config;
import cn.kt.setting.PersistentConfig;
import cn.kt.util.JTextFieldHintListener;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import static cn.kt.constant.Constant.INIT_CONFIG_KEY;
import static cn.kt.constant.Defaults.*;

/**
 * 设置界面
 * Created by kangtian on 2018/8/3.
 */
public class SettingUI extends JDialog implements Configurable {

    private JPanel mainPanel = new JBPanel<>(new GridLayout(2, 1));

    private JTextField tableNameField = new JTextField(10);
    private JBTextField modelPackageField = new JBTextField(12);
    private JBTextField daoPackageField = new JBTextField(12);
    private JBTextField xmlPackageField = new JBTextField(12);
    private JTextField mapperNameField = new JTextField(10);
    private JTextField domainNameField = new JTextField(10);
    private JTextField keyField = new JTextField(12);


    private PersistentConfig persistentConfig;
    private Config config;

    public SettingUI() {
        setContentPane(mainPanel);
    }


    public void createUI(Project project) {
        String projectFolder = project.getBasePath();
        mainPanel.setPreferredSize(new Dimension(0, 0));
        JPanel paneMainTop = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JPanel paneMainTop1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel paneMainTop2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel paneMainTop3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel paneMainTop4 = new JPanel(new GridLayout(4, 1, 3, 3));
        paneMainTop.add(paneMainTop1);
        paneMainTop.add(paneMainTop2);
        paneMainTop.add(paneMainTop3);
        paneMainTop.add(paneMainTop4);

        JPanel paneLeft1 = new JPanel();
        paneLeft1.setLayout(new FlowLayout(FlowLayout.LEFT));
        paneLeft1.add(new JLabel("table name : "));
        tableNameField.setText("eg. db_table");
        tableNameField.setEnabled(false);
        paneLeft1.add(tableNameField);

        JPanel paneLeft2 = new JPanel();
        paneLeft2.setLayout(new FlowLayout(FlowLayout.LEFT));
        paneLeft2.add(new JLabel("primary key : "));
        keyField.setText("default primary key");
        keyField.setEnabled(false);
        paneLeft2.add(keyField);

        JPanel paneRight1 = new JPanel();
        paneRight1.setLayout(new FlowLayout(FlowLayout.LEFT));
        paneRight1.add(new JLabel("model name : "));
        domainNameField.setText("eg. dbTable");
        domainNameField.setEnabled(false);
        paneRight1.add(domainNameField);
        JPanel paneRight2 = new JPanel();
        paneRight2.setLayout(new FlowLayout(FlowLayout.LEFT));
        paneRight2.add(new JLabel("dao postfix : "));
        mapperNameField.setText("Dao");
        paneRight2.add(mapperNameField);

        paneMainTop1.add(paneLeft1);
        paneMainTop1.add(paneLeft2);
        paneMainTop2.add(paneRight1);
        paneMainTop2.add(paneRight2);

        JPanel modelPackagePanel = new JPanel();
        modelPackagePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JBLabel labelLeft4 = new JBLabel("model package : ");
        modelPackagePanel.add(labelLeft4);
        modelPackagePanel.add(modelPackageField);
        JButton packageBtn1 = new JButton("...");
        packageBtn1.addActionListener(actionEvent -> {
            final PackageChooserDialog chooser = new PackageChooserDialog("choose model package", project);
            chooser.selectPackage(modelPackageField.getText());
            chooser.show();
            final PsiPackage psiPackage = chooser.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            modelPackageField.setText(packageName);
        });
        modelPackagePanel.add(packageBtn1);


        JPanel daoPackagePanel = new JPanel();
        daoPackagePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel labelLeft5 = new JLabel("dao package : ");
        daoPackagePanel.add(labelLeft5);
        daoPackagePanel.add(daoPackageField);

        JButton packageBtn2 = new JButton("...");
        packageBtn2.addActionListener(actionEvent -> {
            final PackageChooserDialog chooser = new PackageChooserDialog("choose dao package", project);
            chooser.selectPackage(daoPackageField.getText());
            chooser.show();
            final PsiPackage psiPackage = chooser.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            daoPackageField.setText(packageName);
        });
        daoPackagePanel.add(packageBtn2);

        JPanel xmlPackagePanel = new JPanel();
        xmlPackagePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel labelLeft6 = new JLabel("xml package : ");
        xmlPackagePanel.add(labelLeft6);
        xmlPackagePanel.add(xmlPackageField);

        paneMainTop3.add(modelPackagePanel);
        paneMainTop3.add(daoPackagePanel);
        paneMainTop3.add(xmlPackagePanel);


        JPanel projectFolderPanel = new JPanel();
        projectFolderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel projectLabel = new JLabel("project folder : ");
        projectFolderPanel.add(projectLabel);
        projectFolderBtn.setTextFieldPreferredWidth(45);
        projectFolderBtn.setText(projectFolder);
        projectFolderBtn.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                projectFolderBtn.setText(projectFolderBtn.getText().replaceAll("\\\\", "/"));
            }
        });
        projectFolderPanel.add(projectFolderBtn);
        projectFolderPanel.add(setProjectBtn);


        JPanel modelFolderPanel = new JPanel();
        modelFolderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        modelFolderPanel.add(new JLabel("model  folder : "));

        modelFolderBtn.setTextFieldPreferredWidth(45);
        modelFolderBtn.setText(projectFolder);
        modelFolderBtn.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                modelFolderBtn.setText(modelFolderBtn.getText().replaceAll("\\\\", "/"));
            }
        });
        modelFolderPanel.add(modelFolderBtn);
        modelFolderPanel.add(new JLabel("package path : "));
        modelPathField.setText(DEFAULT_JAVA_PATH);
        modelFolderPanel.add(modelPathField);


        JPanel daoFolderPanel = new JPanel();
        daoFolderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        daoFolderPanel.add(new JLabel("dao folder : "));
        daoFolderBtn.setTextFieldPreferredWidth(45);
        daoFolderBtn.setText(projectFolder);
        daoFolderBtn.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                daoFolderBtn.setText(daoFolderBtn.getText().replaceAll("\\\\", "/"));
            }
        });
        daoFolderPanel.add(daoFolderBtn);
        daoFolderPanel.add(new JLabel("package path : "));
        mapperPathField.setText(DEFAULT_JAVA_PATH);
        daoFolderPanel.add(mapperPathField);


        JPanel xmlFolderPanel = new JPanel();
        xmlFolderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        xmlFolderPanel.add(new JLabel("xml folder : "));


        xmlFolderBtn.setTextFieldPreferredWidth(45);
        xmlFolderBtn.setText(projectFolder);
        xmlFolderBtn.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()) {
        });
        xmlFolderPanel.add(xmlFolderBtn);
        xmlFolderPanel.add(new JLabel("package path : "));
        xmlPathField.setText(DEFAULT_XML_PATH);
        xmlFolderPanel.add(xmlPathField);

        paneMainTop4.add(projectFolderPanel);
        paneMainTop4.add(modelFolderPanel);
        paneMainTop4.add(daoFolderPanel);
        paneMainTop4.add(xmlFolderPanel);

        persistentConfig = PersistentConfig.getInstance(project);
        Map<String, Config> initConfig = persistentConfig.getInitConfig();

        if (initConfig != null) {
            config = initConfig.get(INIT_CONFIG_KEY);
        } else {
            config = new Config();
            config.setName(INIT_CONFIG_KEY);
        }

        JBPanel paneMainDown = buildPanelDown(config);

        if (initConfig != null) {
            mapperNameField.setText(config.getMapperPostfix());
            modelPackageField.setText(config.getModelPackage());
            daoPackageField.setText(config.getMapperPackage());
            xmlPackageField.setText(config.getXmlPackage());

            projectFolderBtn.setText(config.getProjectFolder());
            modelFolderBtn.setText(config.getModelTargetFolder());
            daoFolderBtn.setText(config.getMapperTargetFolder());
            xmlFolderBtn.setText(config.getXmlTargetFolder());
        } else {
            modelPackageField.addFocusListener(new JTextFieldHintListener(modelPackageField, DEFAULT_PACKAGE_NAME));
            daoPackageField.addFocusListener(new JTextFieldHintListener(daoPackageField, DEFAULT_PACKAGE_NAME));
            xmlPackageField.addFocusListener(new JTextFieldHintListener(xmlPackageField, DEFAULT_PACKAGE_NAME));
        }

        mainPanel.add(paneMainTop);
        mainPanel.add(paneMainDown);
    }

    public boolean isModified() {
        return true;
    }

    public void apply() {
        HashMap<String, Config> initConfig = new HashMap<>(1);

        config.setMapperPostfix(mapperNameField.getText());
        config.setModelPackage(modelPackageField.getText());
        config.setMapperPackage(daoPackageField.getText());
        config.setXmlPackage(xmlPackageField.getText());
        config.setProjectFolder(projectFolderBtn.getText());
        config.setModelTargetFolder(modelFolderBtn.getText());
        config.setMapperTargetFolder(daoFolderBtn.getText());
        config.setXmlTargetFolder(xmlFolderBtn.getText());

        initConfig.put(config.getName(), config);
        this.persistentConfig.setInitConfig(initConfig);
    }

    public void reset() {

    }

    @Override
    public JPanel getContentPane() {
        return mainPanel;
    }

}
