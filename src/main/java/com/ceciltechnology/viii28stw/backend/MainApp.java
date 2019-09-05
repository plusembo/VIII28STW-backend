package com.ceciltechnology.viii28stw.backend;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.time.LocalDateTime;
import java.util.TimeZone;

@SpringBootApplication
public class MainApp {
    public static void main(String[] args) {
        try {
            MainApp.setPropertiesConfiguration();
            SpringApplication.run(MainApp.class, args);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    private static void setPropertiesConfiguration() throws ConfigurationException {
        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(MainApp.class.getResource("/application.properties").toString()
                .replace("file:/", ""));
        propertiesConfiguration.setHeader(LocalDateTime.now().toString());

        int port = 9000;
        String ipAdress = "127.0.0.1";
        String antPattern = "/pensiltik";

        propertiesConfiguration.setProperty("server.port", port);
        propertiesConfiguration.setProperty("management.server.port", 9001);
        propertiesConfiguration.setProperty("management.server.address", ipAdress);

        propertiesConfiguration.setProperty("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
        propertiesConfiguration.setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/pensiltikdb?serverTimezone=".concat(TimeZone.getDefault().getID()));
        propertiesConfiguration.setProperty("spring.datasource.username", "root");
        propertiesConfiguration.setProperty("spring.datasource.password", "#Fp31314");
        propertiesConfiguration.setProperty("spring.datasource.tomcat.max-wait", "20000");
        propertiesConfiguration.setProperty("spring.datasource.tomcat.max-active", "50");
        propertiesConfiguration.setProperty("spring.datasource.tomcat.max-idle", "20");
        propertiesConfiguration.setProperty("spring.datasource.tomcat.min-idle", "15");

        propertiesConfiguration.setProperty("spring.jpa.properties.hibernate.generate_statistics","true");
        propertiesConfiguration.setProperty("spring.jpa.show-sql","true");
        propertiesConfiguration.setProperty("spring.jpa.properties.hibernate.dialect","org.hibernate.dialect.MySQL8Dialect");
        propertiesConfiguration.setProperty("spring.jpa.properties.hibernate.id.new_generator_mappings","false");
        propertiesConfiguration.setProperty("spring.jpa.properties.hibernate.format_sql","true");
        propertiesConfiguration.setProperty("spring.jpa.hibernate.ddl-auto","update");
        propertiesConfiguration.setProperty("spring.jpa.hibernate.naming.physical-strategy","org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        propertiesConfiguration.setProperty("spring.jpa.open-in-view","true");
        propertiesConfiguration.setProperty("spring.jpa.properties.jadira.usertype.autoRegisterUserTypes","true");

        propertiesConfiguration.setProperty("logging.level.org.hibernate.SQL","DEBUG");
        propertiesConfiguration.setProperty("logging.level.org.hibernate.type.descriptor.sql.BasicBinder","TRACE");

        propertiesConfiguration.setProperty("url.prefix", "http://".concat(ipAdress).concat(":") + port + antPattern);

        propertiesConfiguration.save();
    }

}
