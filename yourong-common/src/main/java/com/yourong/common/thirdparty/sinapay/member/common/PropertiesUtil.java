package com.yourong.common.thirdparty.sinapay.member.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>加载properties工具类</p>
 * @author Wallis Wang
 * @version $Id: PropertyUtil.java, v 0.1 2014年6月17日 下午5:10:10 wangqiang Exp $
 */
public class PropertiesUtil {

    private static Log              logger                       = LogFactory
                                                                     .getLog(PropertiesUtil.class);

    private static final String     PROPERTIES_RESOURCE_LOCATION = "mgf.properties";

    private static final Properties localProperties              = new Properties();

    static {
        try {
            ClassLoader cl = PropertiesUtil.class.getClassLoader();
            URL url = cl.getResource(PROPERTIES_RESOURCE_LOCATION);
            if (url != null) {
                logger.info("Found 'mgf.properties' file in local classpath");
                InputStream is = url.openStream();
                try {
                    localProperties.load(is);
                } finally {
                    is.close();
                }
            }
        } catch (IOException ex) {
            if (logger.isInfoEnabled()) {
                logger.info("Could not load 'mgf.properties' file from local classpath: " + ex);
            }
        }
    }

    /**
     * Retrieve the property value for the given key, checking local payfront
     * properties first and falling back to JVM-level system properties.
     * @param key the property key
     * @return the associated property value, or {@code null} if none found
     */
    public static String getProperty(String key) {
        String value = localProperties.getProperty(key);
        if (value == null) {
            try {
                value = System.getProperty(key);
            } catch (Throwable ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not retrieve system property '" + key + "': " + ex);
                }
            }
        }
        return value;
    }
}
