package br.com.blitech.authorization.core.config;

import br.com.blitech.authorization.core.properties.DataSourceProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceConfig {

    @Autowired
    private Environment environment;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(getRequiredProperty("spring.datasource.driver-class-name"));
        dataSource.setSchema(getRequiredProperty("spring.jpa.properties.hibernate.default_schema"));
        dataSource.setUrl(getRequiredProperty("spring.datasource.url"));
        dataSource.setUsername(dataSourceProperties.getUsername());
        dataSource.setPassword(dataSourceProperties.getPassword());

        return dataSource;
    }

    @NotNull
    private String getRequiredProperty(String key) {
        String value = environment.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing required property: " + key);
        }
        return value;
    }
}
