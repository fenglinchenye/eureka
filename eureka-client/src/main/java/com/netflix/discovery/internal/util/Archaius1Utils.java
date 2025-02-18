package com.netflix.discovery.internal.util;

import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This is an INTERNAL class not for public use.
 *
 * @author David Liu
 */
public final class Archaius1Utils {

    private static final Logger logger = LoggerFactory.getLogger(Archaius1Utils.class);

    private static final String ARCHAIUS_DEPLOYMENT_ENVIRONMENT = "archaius.deployment.environment";
    private static final String EUREKA_ENVIRONMENT = "eureka.environment";

    public static DynamicPropertyFactory initConfig(String configName) {
        // 配置文件对象
        DynamicPropertyFactory configInstance = DynamicPropertyFactory.getInstance();
        // 配置文件名
        // 从环境变量 eureka.client.props 获取配置文件名。如果未配置，使用参数configName.
        // 即CommonConstants.CONFIG_FILE_NAME("eureka-client")
        // DynamicPropertyFactory 对应Property 处理
        DynamicStringProperty EUREKA_PROPS_FILE = configInstance.getStringProperty("eureka.client.props", configName);
        // 配置文件环境
        //从环境变量读取文件配置信息，获取配置文件环境
        String env = ConfigurationManager.getConfigInstance().getString(EUREKA_ENVIRONMENT, "test");
        ConfigurationManager.getConfigInstance().setProperty(ARCHAIUS_DEPLOYMENT_ENVIRONMENT, env);
        // 将配置文件加载到环境变量
        String eurekaPropsFile = EUREKA_PROPS_FILE.get();
        try {
            //读取配置文件到环境变量，首先读取${eureka.client.props}对应的配置文件;
            //然后读取${eureka.client.props}-${eureka.environment} 对应的配置文件。若有相同属性,进行覆盖
            ConfigurationManager.loadCascadedPropertiesFromResources(eurekaPropsFile);
        } catch (IOException e) {
            logger.warn(
                    "Cannot find the properties specified : {}. This may be okay if there are other environment "
                            + "specific properties or the configuration is installed with a different mechanism.",
                    eurekaPropsFile);

        }

        return configInstance;
    }
}
