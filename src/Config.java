package indi.goldenwater.waterfalldisconnect.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Logger;

public class Config {
    private final Gson gson = new Gson();
    private final File dataFolder;
    private final String fileName;
    private final Logger logger;
    private final String defaultConfig;

    private Map<String, String> config;
    private boolean successInit;

    public Config(File dataFolder, String fileName, Logger logger, String defaultConfig) {
        this.dataFolder = dataFolder;
        this.fileName = fileName;
        this.logger = logger;
        this.defaultConfig = defaultConfig;
        initialize();
    }

    public void initialize() {
        if (!dataFolder.exists()) {
            if (!dataFolder.mkdirs()) { // 如果目录不存在则创建目录
                logger.warning("Fail to create data folder.");
                return;
            }
        }

        File configFile = new File(dataFolder, fileName);
        try {
            if (configFile.createNewFile()) {
                logger.info("Config file doesn't exists, created.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!(configFile.canWrite() && configFile.canRead())) {
            logger.warning("Target file cannot write/read.");
            return;
        }

        try {
            Path path = Paths.get(configFile.getPath());
            byte[] data = Files.readAllBytes(path);
            String jsonStr = new String(data, StandardCharsets.UTF_8); // 读取内容

            if (jsonStr.equals("")) {
                jsonStr = defaultConfig;
                Files.write(configFile.toPath(), defaultConfig.getBytes(StandardCharsets.UTF_8));
            }

            config = gson.fromJson(jsonStr, new TypeToken<Map<String, String>>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }

        successInit = true;
    }

    public void save() {
        if (!successInit) return;

        if (!dataFolder.exists()) {
            if (!dataFolder.mkdirs()) { // 如果目录不存在则创建目录
                logger.warning("Fail to create data folder.");
                return;
            }
        }

        File configFile = new File(dataFolder, fileName);
        try {
            if (configFile.createNewFile()) {
                logger.info("Config file doesn't exists, created.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!(configFile.canWrite() && configFile.canRead())) {
            logger.warning("Target file cannot write/read.");
            return;
        }

        try {
            Path path = Paths.get(configFile.getPath());
            Files.write(path, gson.toJson(config).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        initialize();
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }
}
