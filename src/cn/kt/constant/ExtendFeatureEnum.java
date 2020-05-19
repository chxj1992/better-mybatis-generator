package cn.kt.constant;

/**
 * @author chenxiaojing
 */
public enum ExtendFeatureEnum {

    /**
     * 扩展配置选项
     */
    ADD_OFFSET_LIMIT("分页支持", false),
    ADD_COMMENT("生成注释", true),
    OVERRIDE_XML("XML覆盖", true),
    ADD_TO_STRING_AND_HASH_CODE_EQUALS("toString/hashCode/equals", true),
    USE_SCHEMA_PREFIX("使用Schema前缀", true),
    ADD_SELECT_FOR_UPDATE("ForUpdate支持", false),
    ADD_REPOSITORY_ANNOTATION("Repository注解", false),
    USE_EXTEND_STYLE("继承模式", false),
    USE_JSR310("JSR310: Date and Time API", true),
    ADD_JPA_ANNOTATION("JPA注解", false),
    USE_ACTUAL_NAME("使用真实字段名", false),
    USE_TABLE_ALIAS("别名模式", false),
    ADD_EXAMPLE("Example支持", false),
    USE_MYSQL8("MySQL8支持", false),
    ADD_MAPPER_ANNOTATION("@Mapper注解", true),
    USE_SHORT_METHOD_NAME("简洁命名", true),
    USE_LOMBOK("Lombok支持", true);


    /**
     * @param desc           描述
     * @param defaultChecked 默认是否开启
     */
    ExtendFeatureEnum(String desc, boolean defaultChecked) {
        this.desc = desc;
        this.defaultChecked = defaultChecked;
    }

    private String desc;

    private boolean defaultChecked;


    public String getDesc() {
        return desc;
    }

    public boolean isDefaultChecked() {
        return defaultChecked;
    }
}