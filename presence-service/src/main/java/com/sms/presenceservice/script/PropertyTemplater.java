package com.sms.presenceservice.script;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;

public class PropertyTemplater {

    private final static String VALUES = "values.properties";
    private final static String APP_PROPERTIES_TEMPLATE = "application.template.properties";
    private final static String APP_PROPERTIES = "application.properties";

    public static void main(String[] args) throws IOException, URISyntaxException {
        Properties values = new Properties();
        values.load(PropertyTemplater.class.getClassLoader().getResourceAsStream(VALUES));

        Path templatePath = Paths.get(PropertyTemplater.class
                .getClassLoader()
                .getResource(APP_PROPERTIES_TEMPLATE)
                .toURI());
        String template = new String(Files.readAllBytes(templatePath));

        for (String value : values.stringPropertyNames()) {
            template = template.replaceAll("\\{" + value + "\\}", values.getProperty(value));
        }

        Path outputPath = Paths.get("/", templatePath.subpath(0, templatePath.getNameCount() - 1).toString(), APP_PROPERTIES);
        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows")) {
            outputPath = Paths.get(outputPath.toString().substring(1)).toAbsolutePath();
        }

        if (!Files.exists(outputPath)) {
            Files.createFile(outputPath.toAbsolutePath());
        }
        Files.write(outputPath, template.getBytes());
    }
}