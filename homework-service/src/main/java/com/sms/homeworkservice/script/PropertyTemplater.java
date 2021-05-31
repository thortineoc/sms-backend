package com.sms.homeworkservice.script;

import com.sms.config.scripts.Templater;

import java.io.IOException;
import java.net.URISyntaxException;

public class PropertyTemplater {

    public static void main(String[] args) throws IOException, URISyntaxException {
        Templater.template("application.properties");
    }
}