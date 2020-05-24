package cn.kt.setting;

import cn.kt.model.Config;
import cn.kt.model.User;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;


/**
 * 配置持久化
 * @author chenxiaojing
 */
@State(name = "PersistentService", storages = {@Storage("mybatis-generator-config.xml")})
public class PersistentService implements PersistentStateComponent<PersistentService> {

    private Map<String, Config> initConfig;
    private Map<String, User> users;
    private Map<String, Config> historyConfigList = new HashMap<>();

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


    public Map<String, Config> getInitConfig() {
        return initConfig;
    }

    public void setInitConfig(Map<String, Config> initConfig) {
        this.initConfig = initConfig;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public void setUsers(Map<String, User> users) {
        this.users = users;
    }

    public Map<String, Config> getHistoryConfigList() {
        return historyConfigList;
    }

    public void setHistoryConfigList(Map<String, Config> historyConfigList) {
        this.historyConfigList = historyConfigList;
    }
}
