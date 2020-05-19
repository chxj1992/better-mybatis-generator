package cn.kt.ui;

import cn.kt.constant.ExtendFeatureEnum;
import cn.kt.generate.Generate;
import cn.kt.model.Config;
import cn.kt.model.TableInfo;
import cn.kt.setting.PersistentConfig;
import cn.kt.util.JTextFieldHintListener;
import cn.kt.util.StringUtils;
import com.intellij.database.psi.DbTable;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.components.*;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

import static cn.kt.constant.Constant.INIT_CONFIG_KEY;
import static cn.kt.constant.Defaults.*;

/**
 * 插件主界面
 * Created by kangtian on 2018/8/1.
 */
public class MainUI extends JFrame implements Configurable {

    private final String DEFAULT_MAPPER_POSTFIX = "Mapper";

    private AnActionEvent anActionEvent;
    private Project project;
    private PsiElement[] psiElements;
    private Config config;

    private JTextField tableNameField = new JTextField(10);
    private JBTextField modelPackageField = new JBTextField(12);
    private JBTextField daoPackageField = new JBTextField(12);
    private JBTextField xmlPackageField = new JBTextField(12);
    private JTextField mapperNameField = new JTextField(10);
    private JTextField modelNameField = new JTextField(10);
    private JTextField keyField = new JTextField(10);


    public MainUI(AnActionEvent anActionEvent) throws HeadlessException {
        this.anActionEvent = anActionEvent;
        this.project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        PersistentConfig persistentConfig = PersistentConfig.getInstance(project);
        this.psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);

        Map<String, Config> initConfigMap = persistentConfig.getInitConfig();
        Map<String, Config> historyConfigList = persistentConfig.getHistoryConfigList();

        config = new Config();
        setTitle("mybatis generate tool");
        // 设置大小
        setPreferredSize(new Dimension(1200, 700));
        // 设置位置
        setLocation(120, 100);
        pack();
        setVisible(true);
        JButton buttonOk = new JButton("OK");
        getRootPane().setDefaultButton(buttonOk);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        PsiElement psiElement = psiElements[0];
        TableInfo tableInfo = new TableInfo((DbTable) psiElement);
        String tableName = tableInfo.getTableName();
        String modelName = StringUtils.dbStringToCamelStyle(tableName);
        String primaryKey = "";
        if (tableInfo.getPrimaryKeys().size() > 0) {
            primaryKey = tableInfo.getPrimaryKeys().get(0);
        }
        String projectFolder = project.getBasePath();


        if (psiElements.length > 1) {
            //多表时，只使用默认配置
            if (initConfigMap != null) {
                config = initConfigMap.get(INIT_CONFIG_KEY);
            }
        } else {
            //单表时，优先使用已经存在的配置
            if (historyConfigList == null) {
                historyConfigList = new HashMap<>();
            }
            if (historyConfigList.containsKey(tableName)) {
                config = historyConfigList.get(tableName);
            } else if (initConfigMap != null) {
                config = initConfigMap.get(INIT_CONFIG_KEY);
            }
        }

        JPanel contentPanel = new JBPanel<>();
        contentPanel.setBorder(JBUI.Borders.empty(5));
        contentPanel.setLayout(new BorderLayout());

        //主要设置显示在这里
        JPanel paneMain = new JPanel(new GridLayout(2, 1, 3, 3));
        JPanel paneMainTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paneMainTop.setBorder(JBUI.Borders.empty(10, 30, 5, 40));

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
        JLabel tablejLabel = new JLabel("table name :");
        tablejLabel.setSize(new Dimension(20, 30));
        paneLeft1.add(tablejLabel);
        if (psiElements.length > 1) {
            tableNameField.addFocusListener(new JTextFieldHintListener(tableNameField, "eg. db_table"));
        } else {
            tableNameField.setText(tableName);
        }
        paneLeft1.add(tableNameField);

        JPanel paneLeft2 = new JPanel();
        paneLeft2.setLayout(new FlowLayout(FlowLayout.LEFT));
        paneLeft2.add(new JLabel("主键(选填) :"));
        if (psiElements.length > 1) {
            keyField.addFocusListener(new JTextFieldHintListener(keyField, "eg. primary key"));
        } else {
            keyField.setText(primaryKey);
        }
        paneLeft2.add(keyField);

        JPanel paneRight1 = new JPanel();
        paneRight1.setLayout(new FlowLayout(FlowLayout.LEFT));
        paneRight1.add(new JLabel("model :"));
        if (psiElements.length > 1) {
            modelNameField.addFocusListener(new JTextFieldHintListener(modelNameField, "eg. DbTable"));
        } else {
            modelNameField.setText(modelName);
        }
        paneRight1.add(modelNameField);

        JPanel paneRight2 = new JPanel();
        paneRight2.setLayout(new FlowLayout(FlowLayout.LEFT));
        paneRight2.add(new JLabel("mapper name : "));
        if (psiElements.length > 1) {
            if (config != null && !StringUtils.isEmpty(config.getMapperPostfix())) {
                mapperNameField.addFocusListener(new JTextFieldHintListener(mapperNameField, "eg. DbTable" + config.getMapperPostfix()));
            } else {
                mapperNameField.addFocusListener(new JTextFieldHintListener(mapperNameField, "eg. DbTable" + DEFAULT_MAPPER_POSTFIX));
            }
        } else {
            if (config != null && !StringUtils.isEmpty(config.getMapperPostfix())) {
                mapperNameField.setText(modelName + config.getMapperPostfix());
            } else {
                mapperNameField.setText(modelName + DEFAULT_MAPPER_POSTFIX);
            }
        }

        paneRight2.add(mapperNameField);

        paneMainTop1.add(paneLeft1);
        paneMainTop1.add(paneLeft2);
        paneMainTop2.add(paneRight1);
        paneMainTop2.add(paneRight2);

        JPanel modelPackagePanel = new JPanel();
        modelPackagePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JBLabel labelLeft4 = new JBLabel("model package : ");
        modelPackagePanel.add(labelLeft4);
        if (config != null && !StringUtils.isEmpty(config.getModelPackage())) {
            modelPackageField.setText(config.getModelPackage());
        } else {
            modelPackageField.setText(DEFAULT_PACKAGE_NAME);
        }
        modelPackagePanel.add(modelPackageField);
        JButton packageBtn1 = new JButton("...");
        packageBtn1.addActionListener(actionEvent -> {
            final PackageChooserDialog chooser = new PackageChooserDialog("choose model package", project);
            chooser.selectPackage(modelPackageField.getText());
            chooser.show();
            final PsiPackage psiPackage = chooser.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            modelPackageField.setText(packageName);
            MainUI.this.toFront();
        });
        modelPackagePanel.add(packageBtn1);


        JPanel daoPackagePanel = new JPanel();
        daoPackagePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel labelLeft5 = new JLabel("mapper package : ");
        daoPackagePanel.add(labelLeft5);


        if (config != null && !StringUtils.isEmpty(config.getMapperPackage())) {
            daoPackageField.setText(config.getMapperPackage());
        } else {
            daoPackageField.setText(DEFAULT_PACKAGE_NAME);
        }
        daoPackagePanel.add(daoPackageField);

        JButton packageBtn2 = new JButton("...");
        packageBtn2.addActionListener(actionEvent -> {
            final PackageChooserDialog chooser = new PackageChooserDialog("choose mapper package", project);
            chooser.selectPackage(daoPackageField.getText());
            chooser.show();
            final PsiPackage psiPackage = chooser.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            daoPackageField.setText(packageName);
            MainUI.this.toFront();
        });
        daoPackagePanel.add(packageBtn2);

        JPanel xmlPackagePanel = new JPanel();
        xmlPackagePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel labelLeft6 = new JLabel("xml package : ");
        xmlPackagePanel.add(labelLeft6);
        if (config != null && !StringUtils.isEmpty(config.getXmlPackage())) {
            xmlPackageField.setText(config.getXmlPackage());
        } else {
            xmlPackageField.setText(DEFAULT_PACKAGE_NAME);
        }
        xmlPackagePanel.add(xmlPackageField);

        paneMainTop3.add(modelPackagePanel);
        paneMainTop3.add(daoPackagePanel);
        paneMainTop3.add(xmlPackagePanel);

        JPanel projectFolderPanel = new JPanel();
        projectFolderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel projectLabel = new JLabel("project folder : ");
        projectFolderPanel.add(projectLabel);
        projectFolderBtn.setTextFieldPreferredWidth(45);
        if (config != null && !StringUtils.isEmpty(config.getProjectFolder())) {
            projectFolderBtn.setText(config.getProjectFolder());
        } else {
            projectFolderBtn.setText(projectFolder);
        }
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
        if (config != null && !StringUtils.isEmpty(config.getModelTargetFolder())) {
            modelFolderBtn.setText(config.getModelTargetFolder());
        } else {
            modelFolderBtn.setText(projectFolder);
        }
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
        daoFolderPanel.add(new JLabel("mapper folder : "));
        daoFolderBtn.setTextFieldPreferredWidth(45);
        if (config != null && !StringUtils.isEmpty(config.getMapperTargetFolder())) {
            daoFolderBtn.setText(config.getMapperTargetFolder());
        } else {
            daoFolderBtn.setText(projectFolder);
        }
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
        if (config != null && !StringUtils.isEmpty(config.getXmlTargetFolder())) {
            xmlFolderBtn.setText(config.getXmlTargetFolder());
        } else {
            xmlFolderBtn.setText(projectFolder);
        }

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

        paneMain.add(paneMainTop);

        JBPanel paneMainDown = buildPanelDown(config);
        paneMain.add(paneMainDown);

        //确认和取消按钮
        JPanel paneBottom = new JPanel();
        paneBottom.setLayout(new FlowLayout(2));
        paneBottom.add(buttonOk);
        JButton buttonCancel = new JButton("CANCEL");
        paneBottom.add(buttonCancel);


        JPanel panelLeft = new JPanel();
        panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
        //采用x布局时，添加固定宽度组件隔开
        this.getContentPane().add(Box.createVerticalStrut(10));
        final DefaultListModel defaultListModel = new DefaultListModel();

        Border historyBorder = BorderFactory.createTitledBorder("history config : ");
        panelLeft.setBorder(historyBorder);


        if (historyConfigList == null) {
            historyConfigList = new HashMap<>();
        }
        for (String historyConfigName : historyConfigList.keySet()) {
            defaultListModel.addElement(historyConfigName);
        }
        Map<String, Config> finalHistoryConfigList = historyConfigList;

        final JBList fruitList = new JBList(defaultListModel);
        fruitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fruitList.setSelectedIndex(0);
        fruitList.setVisibleRowCount(25);
        JBScrollPane scrollPanel = new JBScrollPane(fruitList);
        panelLeft.add(scrollPanel);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

        JButton selectConfigBtn = new JButton("SELECT");
        btnPanel.add(selectConfigBtn);
        JButton deleteConfigBtn = new JButton("DELETE");
        btnPanel.add(deleteConfigBtn);
        selectConfigBtn.addActionListener(e -> {
            String configName = (String) fruitList.getSelectedValue();
            Config selectedConfig = finalHistoryConfigList.get(configName);

            modelPackageField.setText(selectedConfig.getModelPackage());
            daoPackageField.setText(selectedConfig.getMapperPackage());
            xmlPackageField.setText(selectedConfig.getXmlPackage());
            projectFolderBtn.setText(selectedConfig.getProjectFolder());
            modelFolderBtn.setText(selectedConfig.getModelTargetFolder());
            daoFolderBtn.setText(selectedConfig.getMapperTargetFolder());
            xmlFolderBtn.setText(selectedConfig.getXmlTargetFolder());

        });
        deleteConfigBtn.addActionListener(e -> {
            finalHistoryConfigList.remove(fruitList.getSelectedValue());
            defaultListModel.removeAllElements();
            for (String historyConfigName : finalHistoryConfigList.keySet()) {
                defaultListModel.addElement(historyConfigName);
            }
        });
        panelLeft.add(btnPanel);


        contentPanel.add(paneMain, BorderLayout.CENTER);
        contentPanel.add(paneBottom, BorderLayout.SOUTH);
        contentPanel.add(panelLeft, BorderLayout.WEST);

        setContentPane(contentPanel);

        setProjectBtn.addActionListener(e -> {
            modelFolderBtn.setText(projectFolderBtn.getText());
            daoFolderBtn.setText(projectFolderBtn.getText());
            xmlFolderBtn.setText(projectFolderBtn.getText());
        });

        buttonOk.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPanel.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        try {
            dispose();

            if (psiElements.length == 1) {
                Config generatorConfig = new Config();
                generatorConfig.setName(tableNameField.getText());
                generatorConfig.setTableName(tableNameField.getText());
                generatorConfig.setMapperName(mapperNameField.getText());
                generatorConfig.setModelName(modelNameField.getText());
                generatorConfig.setPrimaryKey(keyField.getText());

                fillBasicConfig(generatorConfig);
                fillOtherConfig(generatorConfig);
                generatorConfig.setFeatureMap(config.getFeatureMap());

                new Generate(generatorConfig).execute(anActionEvent);

            } else {
                for (PsiElement psiElement : psiElements) {
                    TableInfo tableInfo = new TableInfo((DbTable) psiElement);
                    String tableName = tableInfo.getTableName();
                    String modelName = StringUtils.dbStringToCamelStyle(tableName);
                    String primaryKey = "";
                    if (tableInfo.getPrimaryKeys() != null && tableInfo.getPrimaryKeys().size() != 0) {
                        primaryKey = tableInfo.getPrimaryKeys().get(0);
                    }

                    Config generatorConfig = new Config();
                    generatorConfig.setName(tableName);
                    generatorConfig.setTableName(tableName);
                    if (this.config != null) {
                        generatorConfig.setMapperName(modelName + this.config.getMapperPostfix());
                    } else {
                        generatorConfig.setMapperName(modelName + DEFAULT_MAPPER_POSTFIX);
                    }
                    generatorConfig.setModelName(modelName);
                    generatorConfig.setPrimaryKey(primaryKey);

                    fillBasicConfig(generatorConfig);
                    fillOtherConfig(generatorConfig);
                    generatorConfig.setFeatureMap(config.getFeatureMap());

                    new Generate(generatorConfig).execute(anActionEvent);
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            dispose();
        }
    }

    private void fillBasicConfig(Config config) {
        config.setProjectFolder(projectFolderBtn.getText());
        config.setModelPackage(modelPackageField.getText());
        config.setModelTargetFolder(modelFolderBtn.getText());
        config.setMapperPackage(daoPackageField.getText());
        config.setMapperTargetFolder(daoFolderBtn.getText());
        config.setXmlPackage(xmlPackageField.getText());
        config.setXmlTargetFolder(xmlFolderBtn.getText());
    }

    private void fillOtherConfig(Config config) {
        config.setModelPath(modelPathField.getText());
        config.setMapperPath(mapperPathField.getText());
        config.setXmlPath(xmlPathField.getText());
    }


    private void onCancel() {
        dispose();
    }
}
