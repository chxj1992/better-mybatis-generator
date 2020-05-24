package name.chxj.mybatis.generator.setting;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import name.chxj.mybatis.generator.model.Config;
import name.chxj.mybatis.generator.model.TableInfo;
import name.chxj.mybatis.generator.model.User;
import name.chxj.mybatis.generator.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static name.chxj.mybatis.generator.constant.Defaults.*;


/**
 * 配置持久化
 *
 * @author chenxiaojing
 */
@State(name = "PersistentService", storages = {@Storage("mybatis-generator-config.xml")})
public class PersistentService implements PersistentStateComponent<PersistentService> {

    private final static String INIT_CONFIG_NAME = "_INIT_";

    private Config initConfig;
    private Map<String, User> users;
    private Map<String, Config> historyConfigMap;

    @Nullable
    public static PersistentService getInstance(Project project) {
        return ServiceManager.getService(project, PersistentService.class);
    }

    @Override
    @Nullable
    public PersistentService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PersistentService persistentConfig) {
        XmlSerializerUtil.copyBean(persistentConfig, this);
    }


    public Config getInitConfig(TableInfo tableInfo, String projectFolder) {
        if (initConfig == null) {
            initConfig = new Config();
            initConfig.setName(INIT_CONFIG_NAME);
            initConfig.setMapperPostfix(DEFAULT_MAPPER_POSTFIX);

            initConfig.setModelPackage(DEFAULT_PACKAGE_NAME);
            initConfig.setMapperPackage(DEFAULT_PACKAGE_NAME);

            initConfig.setProjectFolder(projectFolder);
            initConfig.setModelTargetFolder(projectFolder);
            initConfig.setMapperTargetFolder(projectFolder);
            initConfig.setXmlTargetFolder(projectFolder);

            initConfig.setModelPath(DEFAULT_JAVA_PATH);
            initConfig.setMapperPath(DEFAULT_JAVA_PATH);
            initConfig.setXmlPath(DEFAULT_XML_PATH);
        }
        if (tableInfo != null) {
            initConfig.setTableName(tableInfo.getTableName());
            initConfig.setModelName(StringUtils.dbStringToCamelStyle(tableInfo.getTableName()));
            initConfig.setMapperName(initConfig.getModelName() + DEFAULT_MAPPER_POSTFIX);
            if (tableInfo.getPrimaryKeys().size() > 0) {
                initConfig.setPrimaryKey(tableInfo.getPrimaryKeys().get(0));
            }
        }
        return initConfig;
    }

    public void setInitConfig(Config initConfig) {
        this.initConfig = initConfig;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public void setUsers(Map<String, User> users) {
        this.users = users;
    }

    public Map<String, Config> getHistoryConfigMap() {
        if (historyConfigMap == null) {
            historyConfigMap = new HashMap<>();
        }
        return historyConfigMap;
    }

}
