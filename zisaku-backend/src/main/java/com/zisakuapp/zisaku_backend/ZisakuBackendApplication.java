package com.zisakuapp.zisaku_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

import java.util.Properties;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class ZisakuBackendApplication {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("spring.datasource.url", "jdbc:sqlite:database.db");
        props.setProperty("spring.datasource.driver-class-name", "org.sqlite.JDBC");
        props.setProperty("spring.jpa.database-platform", "org.hibernate.community.dialect.SQLiteDialect");
        props.setProperty("spring.jpa.hibernate.ddl-auto", "none");

        SpringApplication app = new SpringApplication(ZisakuBackendApplication.class);
        app.setDefaultProperties(props);
        
        // 💡 起動コードはこれ「1行だけ」にします！
        app.run(args);
    }
}