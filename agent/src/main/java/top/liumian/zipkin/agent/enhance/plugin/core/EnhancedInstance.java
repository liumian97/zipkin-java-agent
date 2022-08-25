package top.liumian.zipkin.agent.enhance.plugin.core;

/**
 * 被增强的实例将继承本接口，作用有两个：
 * 1. 标识实例是否已经被增强
 * 2. 提供动态的get、set方法，可以设置自定义的数据到目标实例
 * 在{@link PluginEnhancer}对目标实例进行增强
 *
 * @author liumian  2022/8/25 22:50
 */
public interface EnhancedInstance {

    /**
     * 动态生成的属性名
     */
    String CONTEXT_ATTR_NAME = "_$EnhancedClassField_zk";

    /**
     * 获取动态设置进来的值，如当时的链路上下文信息
     *
     * @return 动态设置进来的值
     */
    Object getZkDynamicField();


    /**
     * 对enhanced Instance设置自定义的值
     *
     * @param value 自定义的值
     */
    void setZkDynamicField(Object value);


}
