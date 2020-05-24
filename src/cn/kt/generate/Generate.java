package cn.kt.generate;

import static cn.kt.constant.Defaults.DEFAULT_PACKAGE_NAME;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.JavaTypeResolverConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.DefaultShellCallback;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.database.model.RawConnectionConfig;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbTable;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;

import cn.kt.ModelCommentGenerator;
import cn.kt.constant.ExtendFeatureEnum;
import cn.kt.model.Config;
import cn.kt.model.DbType;
import cn.kt.model.User;
import cn.kt.setting.PersistentService;
import cn.kt.ui.UserUI;
import cn.kt.util.GeneratorCallback;
import cn.kt.util.StringUtils;

/**
 * 生成mybatis相关代码
 *
 * @author kangtian
 * @date 2018/7/28
 */
public class Generate {

    private AnActionEvent anActionEvent;
    private Project project;

    /**
     * 持久化的配置
     */
    private PersistentService persistentConfig;

    /**
     * 界面默认配置
     */
    private Config config;

    private String username;

    /**
     * 数据库类型
     */
    private String databaseType;

    /**
     * 数据库驱动
     */
    private String driverClass;

    /**
     * 数据库连接url
     */
    private String url;

    public Generate(Config config) {
        this.config = config;
    }

    /**
     * 自动生成的主逻辑
     *
     * @param anActionEvent
     * @throws Exception
     */
    public void execute(AnActionEvent anActionEvent) throws Exception {
        this.anActionEvent = anActionEvent;
        this.project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        this.persistentConfig = PersistentService.getInstance(project);

        //执行前 先保存一份当前配置
        persistentConfig.getHistoryConfigMap().put(config.getName(), config);

        PsiElement[] psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);

        if (psiElements == null || psiElements.length == 0) {
            return;
        }

        RawConnectionConfig connectionConfig = ((DbDataSource) psiElements[0].getParent().getParent()).getConnectionConfig();
        driverClass = connectionConfig.getDriverClass();
        url = connectionConfig.getUrl();
        if (driverClass.contains("mysql")) {
            databaseType = "MySQL";
        }
        else if (driverClass.contains("oracle")) {
            databaseType = "Oracle";
        }
        else if (driverClass.contains("postgresql")) {
            databaseType = "PostgreSQL";
        }
        else if (driverClass.contains("sqlserver")) {
            databaseType = "SqlServer";
        }
        else if (driverClass.contains("sqlite")) {
            databaseType = "Sqlite";
        }
        else if (driverClass.contains("mariadb")) {
            databaseType = "MariaDB";
        }


        //用后台任务执行代码生成
        ApplicationManager.getApplication().invokeLater(() -> ProgressManager.getInstance().run(new Task.Backgroundable(project, "Mybatis Generating ...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {

                for (PsiElement psiElement : psiElements) {
                    if (!(psiElement instanceof DbTable)) {
                        continue;
                    }
                    Configuration configuration = new Configuration();
                    Context context = new Context(ModelType.CONDITIONAL);
                    configuration.addContext(context);

                    context.setId("myid");
                    context.addProperty("autoDelimitKeywords", "true");
                    context.addProperty("beginningDelimiter", "`");
                    context.addProperty("endingDelimiter", "`");
                    context.addProperty("javaFileEncoding", "UTF-8");
                    context.addProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING, "UTF-8");
                    context.setTargetRuntime("MyBatis3");

                    JDBCConnectionConfiguration jdbcConfig = buildJdbcConfig(psiElement);
                    if (jdbcConfig == null) {
                        return;
                    }
                    TableConfiguration tableConfig = buildTableConfig(psiElement, context);
                    JavaModelGeneratorConfiguration modelConfig = buildModelConfig();
                    SqlMapGeneratorConfiguration mapperConfig = buildMapperXmlConfig();
                    JavaClientGeneratorConfiguration daoConfig = buildDaoConfig();
                    CommentGeneratorConfiguration commentConfig = buildCommentConfig();

                    context.addTableConfiguration(tableConfig);
                    context.setJdbcConnectionConfiguration(jdbcConfig);
                    context.setJavaModelGeneratorConfiguration(modelConfig);
                    context.setSqlMapGeneratorConfiguration(mapperConfig);
                    context.setJavaClientGeneratorConfiguration(daoConfig);
                    context.setCommentGeneratorConfiguration(commentConfig);
                    addPluginConfiguration(psiElement, context);

                    createFolderForNeed(config);
                    List<String> warnings = new ArrayList<>();
                    ShellCallback shellCallback = new DefaultShellCallback(true); // override=true
                    Set<String> fullyQualifiedTables = new HashSet<>();
                    Set<String> contexts = new HashSet<>();
                    try {
                        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration, shellCallback, warnings);
                        myBatisGenerator.generate(new GeneratorCallback(), contexts, fullyQualifiedTables);
                    }
                    catch (Exception e) {
                        // Messages.showMessageDialog(e.getMessage() + " if use mysql,check version8?", "Generate failure", Messages.getInformationIcon());
                        System.out.println("代码生成报错 : " + e.getMessage());
                    }
                    project.getBaseDir().refresh(false, true);
                }
            }
        }));


    }

    /**
     * 创建所需目录
     *
     * @param config
     */
    private void createFolderForNeed(Config config) {
        String modelTargetFolder = config.getModelTargetFolder();
        String daoTargetFolder = config.getMapperTargetFolder();
        String xmlTargetFolder = config.getXmlTargetFolder();

        String modelMvnPath = config.getModelPath();
        String daoMvnPath = config.getMapperPath();
        String xmlMvnPath = config.getXmlPath();

        String modelPath = modelTargetFolder + "/" + modelMvnPath + "/";
        String daoPath = daoTargetFolder + "/" + daoMvnPath + "/";
        String xmlPath = xmlTargetFolder + "/" + xmlMvnPath + "/";

        File modelFile = new File(modelPath);
        if (!modelFile.exists() && !modelFile.isDirectory()) {
            modelFile.mkdirs();
        }

        File daoFile = new File(daoPath);
        if (!daoFile.exists() && !daoFile.isDirectory()) {
            daoFile.mkdirs();
        }

        File xmlFile = new File(xmlPath);
        if (!xmlFile.exists() && !xmlFile.isDirectory()) {
            xmlFile.mkdirs();
        }

    }


    /**
     * 保存当前配置到历史记录
     */
    private void saveConfig() {

    }

    /**
     * 生成数据库连接配置
     *
     * @param psiElement
     * @return
     */
    private JDBCConnectionConfiguration buildJdbcConfig(PsiElement psiElement) {

        JDBCConnectionConfiguration jdbcConfig = new JDBCConnectionConfiguration();
        jdbcConfig.addProperty("nullCatalogMeansCurrent", "true");
        jdbcConfig.addProperty("useInformationSchema", "true");

        Map<String, User> users = persistentConfig.getUsers();
        if (users != null && users.containsKey(url)) {
            User user = users.get(url);

            username = user.getUsername();

            CredentialAttributes credentialAttributes = new CredentialAttributes("better-mybatis-generator-" + url, username, this.getClass(), false);
            String password = PasswordSafe.getInstance().getPassword(credentialAttributes);
            if (StringUtils.isEmpty(password)) {
                new UserUI(driverClass, url, anActionEvent, config);
                return null;
            }

            jdbcConfig.setUserId(username);
            jdbcConfig.setPassword(password);

            if (config.isSelected(ExtendFeatureEnum.USE_MYSQL8)) {
                driverClass = DbType.MySQL_8.getDriverClass();
            }

            jdbcConfig.setDriverClass(driverClass);
            jdbcConfig.setConnectionURL(url);
            return jdbcConfig;
        }
        else {
            new UserUI(driverClass, url, anActionEvent, config);
            return null;
        }

    }

    /**
     * 生成table配置
     *
     * @param psiElement
     * @param context
     * @return
     */
    private TableConfiguration buildTableConfig(PsiElement psiElement, Context context) {
        TableConfiguration tableConfig = new TableConfiguration(context);
        tableConfig.setTableName(config.getTableName());
        tableConfig.setDomainObjectName(config.getModelName());

        String schema;
        if (databaseType.equals(DbType.MySQL.name())) {
            String[] nameSplit = url.split("/");
            schema = nameSplit[nameSplit.length - 1];
            tableConfig.setSchema(schema);
        }
        else if (databaseType.equals(DbType.Oracle.name())) {
            String[] nameSplit = url.split(":");
            schema = nameSplit[nameSplit.length - 1];
            tableConfig.setCatalog(schema);
        }
        else {
            String[] nameSplit = url.split("/");
            schema = nameSplit[nameSplit.length - 1];
            tableConfig.setCatalog(schema);
        }

        if (!config.isSelected(ExtendFeatureEnum.ADD_EXAMPLE)) {
            tableConfig.setUpdateByExampleStatementEnabled(false);
            tableConfig.setCountByExampleStatementEnabled(false);
            tableConfig.setDeleteByExampleStatementEnabled(false);
            tableConfig.setSelectByExampleStatementEnabled(false);
        }
        if (config.isSelected(ExtendFeatureEnum.USE_SCHEMA_PREFIX)) {
            if (DbType.MySQL.name().equals(databaseType)) {
                tableConfig.setSchema(schema);
            }
            else if (DbType.Oracle.name().equals(databaseType)) {
                //Oracle的schema为用户名，如果连接用户拥有dba等高级权限，若不设schema，会导致把其他用户下同名的表也生成一遍导致mapper中代码重复
                tableConfig.setSchema(username);
            }
            else {
                tableConfig.setCatalog(schema);
            }
        }

        if ("org.postgresql.Driver".equals(driverClass)) {
            tableConfig.setDelimitIdentifiers(true);
        }

        if (!StringUtils.isEmpty(config.getPrimaryKey())) {
            String dbType = databaseType;
            if (DbType.MySQL.name().equals(databaseType)) {
                dbType = "JDBC";
                //dbType为JDBC，且配置中开启useGeneratedKeys时，Mybatis会使用Jdbc3KeyGenerator,
                //使用该KeyGenerator的好处就是直接在一次INSERT 语句内，通过resultSet获取得到 生成的主键值，
                //并很好的支持设置了读写分离代理的数据库
                //例如阿里云RDS + 读写分离代理 无需指定主库
                //当使用SelectKey时，Mybatis会使用SelectKeyGenerator，INSERT之后，多发送一次查询语句，获得主键值
                //在上述读写分离被代理的情况下，会得不到正确的主键
            }
            tableConfig.setGeneratedKey(new GeneratedKey(config.getPrimaryKey(), dbType, true, null));
        }

        if (config.isSelected(ExtendFeatureEnum.USE_ACTUAL_NAME)) {
            tableConfig.addProperty("useActualColumnNames", "true");
        }

        if (config.isSelected(ExtendFeatureEnum.USE_TABLE_ALIAS)) {
            tableConfig.setAlias(config.getTableName());
        }

        tableConfig.setMapperName(config.getMapperName());

        return tableConfig;
    }


    /**
     * 生成实体类配置
     *
     * @return
     */
    private JavaModelGeneratorConfiguration buildModelConfig() {
        String projectFolder = config.getProjectFolder();
        String modelPackage = config.getModelPackage();
        String modelTargetFolder = config.getModelTargetFolder();
        String modelMvnPath = config.getModelPath();

        JavaModelGeneratorConfiguration modelConfig = new JavaModelGeneratorConfiguration();

        modelConfig.setTargetPackage(getTargetPackage(modelPackage));
        modelConfig.setTargetProject(getTargetProject(modelTargetFolder, projectFolder, modelMvnPath));

        return modelConfig;
    }


    /**
     * 生成dao接口文件配置
     *
     * @return
     */
    private JavaClientGeneratorConfiguration buildDaoConfig() {

        String projectFolder = config.getProjectFolder();
        String mapperPackage = config.getMapperPackage();
        String mapperTargetFolder = config.getMapperTargetFolder();
        String mapperMvnPath = config.getMapperPath();

        JavaClientGeneratorConfiguration daoConfig = new JavaClientGeneratorConfiguration();
        daoConfig.setConfigurationType("XMLMAPPER");
        daoConfig.setTargetPackage(getTargetPackage(mapperPackage));
        daoConfig.setTargetProject(getTargetProject(mapperTargetFolder, projectFolder, mapperMvnPath));

        return daoConfig;
    }

    /**
     * 生成mapper.xml文件配置
     *
     * @return
     */
    private SqlMapGeneratorConfiguration buildMapperXmlConfig() {

        String projectFolder = config.getProjectFolder();
        String mapperPackage = config.getMapperPackage();
        String xmlTargetFolder = config.getXmlTargetFolder();
        String xmlMvnPath = config.getXmlPath();

        SqlMapGeneratorConfiguration mapperConfig = new SqlMapGeneratorConfiguration();

        mapperConfig.setTargetPackage(getTargetPackage(mapperPackage));
        mapperConfig.setTargetProject(getTargetProject(xmlTargetFolder, projectFolder, xmlMvnPath));

        // mybatis-generator 默认在已生成的xml后面追加内容, 当选择 OverrideXML 配置项时, 则直接覆盖
        if (config.isSelected(ExtendFeatureEnum.OVERRIDE_XML)) {
            String xmlFilePath = getMappingXmlFilePath(config);
            File xmlFile = new File(xmlFilePath);
            if (xmlFile.exists()) {
                xmlFile.delete();
            }
        }

        return mapperConfig;
    }


    private String getTargetPackage(String targetPackage) {
        if (!StringUtils.isEmpty(targetPackage)) {
            return targetPackage;
        }
        else {
            return DEFAULT_PACKAGE_NAME;
        }
    }

    private String getTargetProject(String targetFolder, String projectFolder, String mvnPath) {
        if (!StringUtils.isEmpty(targetFolder)) {
            return targetFolder + "/" + mvnPath + "/";
        }
        else {
            return projectFolder + "/" + mvnPath + "/";
        }
    }

    /**
     * 生成注释
     *
     * @return
     */
    private CommentGeneratorConfiguration buildCommentConfig() {
        CommentGeneratorConfiguration commentConfig = new CommentGeneratorConfiguration();
        commentConfig.setConfigurationType(ModelCommentGenerator.class.getName());

        if (config.isSelected(ExtendFeatureEnum.ADD_COMMENT)) {
            commentConfig.addProperty("author", config.getAuthor());
            commentConfig.addProperty("columnRemarks", "true");
        }
        if (config.isSelected(ExtendFeatureEnum.ADD_JPA_ANNOTATION)) {
            commentConfig.addProperty("annotations", "true");
        }

        return commentConfig;
    }

    /**
     * 添加相关插件（注意插件文件需要通过jar引入）
     *
     * @param context
     */
    private void addPluginConfiguration(PsiElement psiElement, Context context) {

        // 实体添加序列化
        if (config.isSelected(ExtendFeatureEnum.SERIALIZABLE)) {
            PluginConfiguration serializablePlugin = new PluginConfiguration();
            serializablePlugin.addProperty("type", "org.mybatis.generator.plugins.SerializablePlugin");
            serializablePlugin.setConfigurationType("org.mybatis.generator.plugins.SerializablePlugin");
            context.addPluginConfiguration(serializablePlugin);
        }

        // ToString/Hashcode/Equals 支持
        if (config.isSelected(ExtendFeatureEnum.ADD_TO_STRING_AND_HASH_CODE_EQUALS)) {
            PluginConfiguration equalsHashCodePlugin = new PluginConfiguration();
            equalsHashCodePlugin.addProperty("type", "org.mybatis.generator.plugins.EqualsHashCodePlugin");
            equalsHashCodePlugin.setConfigurationType("org.mybatis.generator.plugins.EqualsHashCodePlugin");
            context.addPluginConfiguration(equalsHashCodePlugin);
            PluginConfiguration toStringPluginPlugin = new PluginConfiguration();
            toStringPluginPlugin.addProperty("type", "org.mybatis.generator.plugins.ToStringPlugin");
            toStringPluginPlugin.setConfigurationType("org.mybatis.generator.plugins.ToStringPlugin");
            context.addPluginConfiguration(toStringPluginPlugin);
        }

        // limit/offset 支持
        if (config.isSelected(ExtendFeatureEnum.ADD_OFFSET_LIMIT)) {
            if (DbType.MySQL.name().equals(databaseType) || DbType.PostgreSQL.name().equals(databaseType)) {
                PluginConfiguration mysqlLimitPlugin = new PluginConfiguration();
                mysqlLimitPlugin.addProperty("type", "cn.kt.MySQLLimitPlugin");
                mysqlLimitPlugin.setConfigurationType("cn.kt.MySQLLimitPlugin");
                context.addPluginConfiguration(mysqlLimitPlugin);
            }
        }

        // JSR310 Date API 支持
        if (config.isSelected(ExtendFeatureEnum.USE_JSR310)) {
            JavaTypeResolverConfiguration javaTypeResolverPlugin = new JavaTypeResolverConfiguration();
            javaTypeResolverPlugin.setConfigurationType("cn.kt.JavaTypeResolverJsr310Impl");
            context.setJavaTypeResolverConfiguration(javaTypeResolverPlugin);
        }

        // SelectForUpdate 支持
        if (config.isSelected(ExtendFeatureEnum.ADD_SELECT_FOR_UPDATE)) {
            if (DbType.MySQL.name().equals(databaseType) || DbType.PostgreSQL.name().equals(databaseType)) {
                PluginConfiguration mysqlForUpdatePlugin = new PluginConfiguration();
                mysqlForUpdatePlugin.addProperty("type", "cn.kt.MySQLForUpdatePlugin");
                mysqlForUpdatePlugin.setConfigurationType("cn.kt.MySQLForUpdatePlugin");
                context.addPluginConfiguration(mysqlForUpdatePlugin);
            }
        }

        // @Repository 注解
        if (config.isSelected(ExtendFeatureEnum.ADD_REPOSITORY_ANNOTATION)) {
            if (DbType.MySQL.name().equals(databaseType) || DbType.PostgreSQL.name().equals(databaseType)) {
                PluginConfiguration repositoryPlugin = new PluginConfiguration();
                repositoryPlugin.addProperty("type", "cn.kt.RepositoryPlugin");
                repositoryPlugin.setConfigurationType("cn.kt.RepositoryPlugin");
                context.addPluginConfiguration(repositoryPlugin);
            }
        }

        // 继承模式
        if (config.isSelected(ExtendFeatureEnum.USE_EXTEND_STYLE)) {
            if (DbType.MySQL.name().equals(databaseType) || DbType.PostgreSQL.name().equals(databaseType)) {
                PluginConfiguration commonDaoInterfacePlugin = new PluginConfiguration();
                commonDaoInterfacePlugin.addProperty("type", "cn.kt.CommonDAOInterfacePlugin");
                commonDaoInterfacePlugin.setConfigurationType("cn.kt.CommonDAOInterfacePlugin");
                context.addPluginConfiguration(commonDaoInterfacePlugin);
            }
        }

        // @Mapper 注解
        if (config.isSelected(ExtendFeatureEnum.ADD_MAPPER_ANNOTATION)) {
            if (DbType.MySQL.name().equals(databaseType) || DbType.PostgreSQL.name().equals(databaseType)) {
                PluginConfiguration mapperAnnotationPlugin = new PluginConfiguration();
                mapperAnnotationPlugin.addProperty("type", "cn.kt.MapperAnnotationPlugin");
                mapperAnnotationPlugin.setConfigurationType("cn.kt.MapperAnnotationPlugin");
                context.addPluginConfiguration(mapperAnnotationPlugin);
            }
        }

        // Lombok 支持
        if (config.isSelected(ExtendFeatureEnum.USE_LOMBOK)) {
            if (DbType.MySQL.name().equals(databaseType) || DbType.PostgreSQL.name().equals(databaseType)) {
                PluginConfiguration lombokPlugin = new PluginConfiguration();
                lombokPlugin.addProperty("type", "cn.kt.LombokPlugin");
                lombokPlugin.setConfigurationType("cn.kt.LombokPlugin");
                context.addPluginConfiguration(lombokPlugin);
            }
        }

        // 简洁命名
        if (config.isSelected(ExtendFeatureEnum.USE_SHORT_METHOD_NAME)) {
            if (DbType.MySQL.name().equals(databaseType) || DbType.PostgreSQL.name().equals(databaseType)) {
                PluginConfiguration shortMethodNamePlugin = new PluginConfiguration();
                shortMethodNamePlugin.addProperty("type", "cn.kt.ShortMethodNamePlugin");
                shortMethodNamePlugin.setConfigurationType("cn.kt.ShortMethodNamePlugin");
                context.addPluginConfiguration(shortMethodNamePlugin);
            }
        }

        // 添加注解
        if (config.isSelected(ExtendFeatureEnum.ADD_COMMENT)) {
            if (DbType.MySQL.name().equals(databaseType) || DbType.PostgreSQL.name().equals(databaseType)) {
                PluginConfiguration mapperCommentPlugin = new PluginConfiguration();
                mapperCommentPlugin.addProperty("type", "cn.kt.MapperCommentPlugin");
                mapperCommentPlugin.addProperty("author", config.getAuthor());
                mapperCommentPlugin.setConfigurationType("cn.kt.MapperCommentPlugin");
                context.addPluginConfiguration(mapperCommentPlugin);
            }
        }

        // 支持批量插入
        if (config.isSelected(ExtendFeatureEnum.ADD_BATCH_INSERT)) {
            if (DbType.MySQL.name().equals(databaseType) || DbType.PostgreSQL.name().equals(databaseType)) {
                PluginConfiguration batchInsertPlugin = new PluginConfiguration();
                batchInsertPlugin.addProperty("type", "cn.kt.BatchInsertPlugin");
                batchInsertPlugin.setConfigurationType("cn.kt.BatchInsertPlugin");
                context.addPluginConfiguration(batchInsertPlugin);
            }
        }

        // 设置ctime/utime/valid默认值
        if (config.isSelected(ExtendFeatureEnum.SET_DEFAULT_TIME_AND_VALID)) {
            if (DbType.MySQL.name().equals(databaseType) || DbType.PostgreSQL.name().equals(databaseType)) {
                PluginConfiguration defaultValuePlugin = new PluginConfiguration();
                defaultValuePlugin.addProperty("type", "cn.kt.DefaultValuePlugin");
                defaultValuePlugin.setConfigurationType("cn.kt.DefaultValuePlugin");
                context.addPluginConfiguration(defaultValuePlugin);
            }
        }

    }


    /**
     * 获取xml文件路径 用以删除之前的xml
     *
     * @param config
     * @return
     */
    private String getMappingXmlFilePath(Config config) {
        StringBuilder sb = new StringBuilder();
        String mapperPackage = config.getMapperPackage();
        String xmlTargetFolder = config.getXmlTargetFolder();
        String xmlPath = config.getXmlPath();
        sb.append(xmlTargetFolder).append("/").append(xmlPath).append("/");

        if (!StringUtils.isEmpty(mapperPackage)) {
            sb.append(mapperPackage.replace(".", "/")).append("/");
        }
        if (!StringUtils.isEmpty(config.getMapperName())) {
            sb.append(config.getMapperName()).append(".xml");
        }
        else {
            sb.append(config.getModelName()).append("Dao.xml");
        }

        return sb.toString();
    }
}
