package name.chxj.mybatis.generator.model;

import name.chxj.mybatis.generator.constant.ExtendFeatureEnum;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 界面配置
 *
 * @author chenxiaojing
 */
public class Config implements Serializable {


    /**
     * 配置名称
     */
    private String name;

    /**
     * 作者
     */
    private String author;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 主键
     */
    private String primaryKey;

    /**
     * 实体名
     */
    private String modelName;

    /**
     * dao名称
     */
    private String mapperName;

    /**
     * dao后缀
     */
    private String mapperPostfix;

    /**
     * 工程目录
     */
    private String projectFolder;

    private String modelPackage;
    private String modelTargetFolder;
    private String modelPath;

    private String mapperPackage;
    private String mapperTargetFolder;
    private String mapperPath;

    private String xmlTargetFolder;
    private String xmlPath;

    private String encoding;
    private String connectorJarPath;

    private Map<ExtendFeatureEnum, Boolean> featureMap = new HashMap<>();

    public Boolean isSelected(ExtendFeatureEnum featureEnum) {
        Boolean hasFeature = featureMap.get(featureEnum);
        if (hasFeature != null) {
            return hasFeature;
        }
        featureMap.put(featureEnum, featureEnum.isDefaultChecked());
        return featureEnum.isDefaultChecked();
    }

    public Map<ExtendFeatureEnum, Boolean> getFeatureMap() {
        return featureMap;
    }

    public void setFeatureMap(Map<ExtendFeatureEnum, Boolean> featureMap) {
        this.featureMap = featureMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getConnectorJarPath() {
        return connectorJarPath;
    }

    public void setConnectorJarPath(String connectorJarPath) {
        this.connectorJarPath = connectorJarPath;
    }

    public String getProjectFolder() {
        return projectFolder;
    }

    public void setProjectFolder(String projectFolder) {
        this.projectFolder = projectFolder;
    }

    public String getModelPackage() {
        return modelPackage;
    }

    public void setModelPackage(String modelPackage) {
        this.modelPackage = modelPackage;
    }

    public String getModelTargetFolder() {
        return modelTargetFolder;
    }

    public void setModelTargetFolder(String modelTargetFolder) {
        this.modelTargetFolder = modelTargetFolder;
    }

    public String getMapperPackage() {
        return mapperPackage;
    }

    public void setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
    }

    public String getMapperTargetFolder() {
        return mapperTargetFolder;
    }

    public void setMapperTargetFolder(String mapperTargetFolder) {
        this.mapperTargetFolder = mapperTargetFolder;
    }

    public String getXmlTargetFolder() {
        return xmlTargetFolder;
    }

    public void setXmlTargetFolder(String xmlTargetFolder) {
        this.xmlTargetFolder = xmlTargetFolder;
    }

    public String getMapperName() {
        return mapperName;
    }

    public void setMapperName(String mapperName) {
        this.mapperName = mapperName;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public String getMapperPath() {
        return mapperPath;
    }

    public void setMapperPath(String mapperPath) {
        this.mapperPath = mapperPath;
    }

    public String getXmlPath() {
        return xmlPath;
    }

    public void setXmlPath(String xmlPath) {
        this.xmlPath = xmlPath;
    }

    public String getMapperPostfix() {
        return mapperPostfix;
    }

    public void setMapperPostfix(String mapperPostfix) {
        this.mapperPostfix = mapperPostfix;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
