package com.lihao.lisa.model.util.config;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static volatile Config sInstance = null;
    private Properties mProperties;
    private static final String CONFIG_FILE = "Config.properties";

    private Config(Context context) {
        mProperties = load(context);
    }

    public static Config getInstance(Context context) {
        if (sInstance == null) {
            synchronized (Config.class) {
                sInstance = new Config(context);
            }
        }
        return sInstance;
    }

    public String getConfigItem(String key) {
        String value = mProperties.getProperty(key);
        if (value == null) {
            throw new ConfigException("No setting in assets/" + CONFIG_FILE + key);
        }
        return value.trim();
    }

    private Properties load(Context context) {
        try {
            InputStream is = context.getAssets().open(CONFIG_FILE);
            Properties prop = new Properties();
            prop.load(is);
            is.close();
            return prop;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConfigException(e);
        }
    }

    public static class ConfigException extends RuntimeException {
        public ConfigException(String message) {
            super(message);
        }

        public ConfigException(Throwable cause) {
            super(cause);
        }
    }
}
