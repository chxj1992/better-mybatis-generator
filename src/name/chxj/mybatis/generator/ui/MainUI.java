package name.chxj.mybatis.generator.ui;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import name.chxj.mybatis.generator.generate.Generate;
import name.chxj.mybatis.generator.model.Config;
import name.chxj.mybatis.generator.model.TableInfo;
import name.chxj.mybatis.generator.util.StringUtils;
import name.chxj.mybatis.generator.setting.PersistentService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

/**
 * 插件主界面
 * Created by kangtian on 2018/8/1.
 */
public class MainUI extends JFrame implements Configurable {

    private AnActionEvent anActionEvent;
    private Project project;
    private PsiElement[] psiElements;
    private Config config;

    public MainUI(AnActionEvent anActionEvent) throws HeadlessException {
        this.anActionEvent = anActionEvent;
        this.project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        PersistentService persistentService = PersistentService.getInstance(project);
        this.psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);

        setTitle("Mybatis Generator");
        // 设置大小
        setPreferredSize(new Dimension(1200, 800));
        // 设置位置
        setLocation(120, 100);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        PsiElement psiElement = psiElements[0];
        TableInfo tableInfo = new TableInfo((DbTable) psiElement);
        String projectFolder = project.getBasePath();

        Map<String, Config> historyConfigMap = persistentService.getHistoryConfigMap();

        if (psiElements.length > 1) {
            //多表时，只使用默认配置
            config = persistentService.getInitConfig(tableInfo, projectFolder);
        } else {
            config = historyConfigMap.getOrDefault(tableInfo.getTableName(), persistentService.getInitConfig(tableInfo, projectFolder));
        }

        JPanel contentPanel = new JBPanel<>();
        contentPanel.setBorder(JBUI.Borders.empty(5));
        contentPanel.setLayout(new BorderLayout());

        //主要设置显示在这里
        JPanel paneMain = new JPanel(new GridLayout(2, 1, 3, 3));

        JPanel basicPanel = createBasicPanel(project, false);
        fillBasicPanel(config, false);

        JBPanel featurePanel = createFeaturePanel(config);

        paneMain.add(basicPanel);
        paneMain.add(featurePanel);

        JPanel btnPanel = createBtnPanel();

        JPanel historySelectorPanel = createHistorySelectorPanel(historyConfigMap);

        contentPanel.add(paneMain, BorderLayout.CENTER);
        contentPanel.add(historySelectorPanel, BorderLayout.WEST);
        contentPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });

        contentPanel.registerKeyboardAction(e -> onClose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    /**
     * 底部按钮栏
     *
     * @return
     */
    @NotNull
    private JPanel createBtnPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton generateBtn = new JButton("GENERATE!");
        getRootPane().setDefaultButton(generateBtn);
        bottomPanel.add(generateBtn);
        JButton closeBtn = new JButton("CLOSE");
        bottomPanel.add(closeBtn);

        generateBtn.addActionListener(e -> onGenerate());
        closeBtn.addActionListener(e -> onClose());
        return bottomPanel;
    }

    /**
     * 左侧历史配置栏
     *
     * @param historyConfigMap
     * @return
     */
    @NotNull
    private JPanel createHistorySelectorPanel(Map<String, Config> historyConfigMap) {
        JPanel historySelectorPanel = new JPanel();
        historySelectorPanel.setLayout(new BoxLayout(historySelectorPanel, BoxLayout.Y_AXIS));
        //采用x布局时，添加固定宽度组件隔开
        this.getContentPane().add(Box.createVerticalStrut(10));
        final DefaultListModel<String> defaultListModel = new DefaultListModel<>();

        Border historyBorder = BorderFactory.createTitledBorder("history config : ");
        historySelectorPanel.setBorder(historyBorder);
        historySelectorPanel.setPreferredSize(new Dimension(250, 650));

        for (String historyConfigName : historyConfigMap.keySet()) {
            defaultListModel.addElement(historyConfigName);
        }

        final JBList<String> historyJBList = new JBList<>(defaultListModel);
        historyJBList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyJBList.setSelectedIndex(0);
        historyJBList.setVisibleRowCount(25);
        JBScrollPane scrollPanel = new JBScrollPane(historyJBList);
        historySelectorPanel.add(scrollPanel);

        historyJBList.addListSelectionListener(e -> {
            String configName = historyJBList.getSelectedValue();
            Config selectedConfig = historyConfigMap.get(configName);
            if (selectedConfig != null) {
                fillView(selectedConfig);
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

        JButton deleteConfigBtn = new JButton("DELETE");
        btnPanel.add(deleteConfigBtn);

        deleteConfigBtn.addActionListener(e -> {
            historyConfigMap.remove(historyJBList.getSelectedValue());
            defaultListModel.removeElement(historyJBList.getSelectedValue());
            scrollPanel.updateUI();
        });

        historySelectorPanel.add(btnPanel);
        return historySelectorPanel;
    }

    /**
     * 填充视图
     *
     * @param selectedConfig
     */
    private void fillView(Config selectedConfig) {
        AUTHOR_FIELD.setText(selectedConfig.getAuthor());
        TABLE_NAME_FIELD.setText(selectedConfig.getTableName());
        PRIMARY_KEY_FIELD.setText(selectedConfig.getPrimaryKey());
        MODEL_NAME_FIELD.setText(selectedConfig.getModelName());
        MAPPER_NAME_FIELD.setText(selectedConfig.getMapperName());
        MODEL_PACKAGE_FIELD.setText(selectedConfig.getModelPackage());
        MAPPER_PACKAGE_FIELD.setText(selectedConfig.getMapperPackage());
        PROJECT_FOLDER_BTN.setText(selectedConfig.getProjectFolder());
        MODEL_FOLDER_BTN.setText(selectedConfig.getModelTargetFolder());
        MAPPER_FOLDER_BTN.setText(selectedConfig.getMapperTargetFolder());
        XML_FOLDER_BTN.setText(selectedConfig.getXmlTargetFolder());
    }

    private void onGenerate() {
        try {
            dispose();

            if (psiElements.length == 1) {
                Config generatedConfig = generateConfig();
                new Generate(generatedConfig).execute(anActionEvent);
            } else {
                for (PsiElement psiElement : psiElements) {
                    TableInfo tableInfo = new TableInfo((DbTable) psiElement);
                    String tableName = tableInfo.getTableName();
                    String modelName = StringUtils.dbStringToCamelStyle(tableName);
                    String primaryKey = "";
                    if (tableInfo.getPrimaryKeys() != null && tableInfo.getPrimaryKeys().size() != 0) {
                        primaryKey = tableInfo.getPrimaryKeys().get(0);
                    }

                    Config generatedConfig = new Config();

                    generatedConfig.setName(tableName);
                    generatedConfig.setAuthor(config.getAuthor());
                    generatedConfig.setTableName(tableName);
                    generatedConfig.setPrimaryKey(primaryKey);

                    generatedConfig.setModelName(modelName);
                    generatedConfig.setMapperName(modelName + config.getMapperPostfix());
                    generatedConfig.setMapperPostfix(config.getMapperPostfix());

                    generatedConfig.setModelPackage(config.getModelPackage());
                    generatedConfig.setMapperPackage(config.getMapperPackage());

                    generatedConfig.setModelPath(config.getModelPath());
                    generatedConfig.setMapperPath(config.getMapperPath());
                    generatedConfig.setXmlPath(config.getXmlPath());

                    generatedConfig.setProjectFolder(config.getProjectFolder());
                    generatedConfig.setModelTargetFolder(config.getModelTargetFolder());
                    generatedConfig.setMapperTargetFolder(config.getMapperTargetFolder());
                    generatedConfig.setXmlTargetFolder(config.getXmlTargetFolder());

                    generatedConfig.setFeatureMap(config.getFeatureMap());

                    new Generate(generatedConfig).execute(anActionEvent);
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            dispose();
        }
    }

    private Config generateConfig() {
        Config generatedConfig = new Config();

        generatedConfig.setName(TABLE_NAME_FIELD.getText());
        generatedConfig.setAuthor(AUTHOR_FIELD.getText());
        generatedConfig.setTableName(TABLE_NAME_FIELD.getText());
        generatedConfig.setPrimaryKey(PRIMARY_KEY_FIELD.getText());

        generatedConfig.setModelName(MODEL_NAME_FIELD.getText());
        generatedConfig.setMapperName(MAPPER_NAME_FIELD.getText());

        String mapper = generatedConfig.getMapperName();
        String modelName = generatedConfig.getModelName();
        String mapperPostfix = mapper.replace(modelName, "");
        generatedConfig.setMapperPostfix(mapperPostfix);

        generatedConfig.setModelPackage(MODEL_PACKAGE_FIELD.getText());
        generatedConfig.setMapperPackage(MAPPER_PACKAGE_FIELD.getText());

        generatedConfig.setModelPath(MODEL_PATH_FIELD.getText());
        generatedConfig.setMapperPath(MAPPER_PATH_FIELD.getText());
        generatedConfig.setXmlPath(XML_PATH_FIELD.getText());

        generatedConfig.setProjectFolder(PROJECT_FOLDER_BTN.getText());
        generatedConfig.setModelTargetFolder(MODEL_FOLDER_BTN.getText());
        generatedConfig.setMapperTargetFolder(MAPPER_FOLDER_BTN.getText());
        generatedConfig.setXmlTargetFolder(XML_FOLDER_BTN.getText());

        generatedConfig.setFeatureMap(config.getFeatureMap());

        return generatedConfig;
    }

    private void onClose() {
        dispose();
    }
}
