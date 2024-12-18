package br.com.blitech.authorization.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import javax.sql.DataSource;

@Configuration
@Profile("!test")
public class DataSourceConfig {

    @Autowired
    private Environment environment;

    @Autowired
    private SecretsManagerClient secretsManagerClient;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(getRequiredProperty("spring.datasource.driver-class-name"));
        dataSource.setSchema(environment.getProperty("spring.jpa.properties.hibernate.default_schema"));
        dataSource.setUrl(environment.getProperty("spring.datasource.url"));
        dataSource.setUsername(getSecretValue(getRequiredProperty("spring.datasource.username")));
        dataSource.setPassword(getSecretValue(getRequiredProperty("spring.datasource.password")));
        return dataSource;
    }

    private String getRequiredProperty(String key) {
        String value = environment.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing required property: " + key);
        }
        return value;
    }

    private String getSecretValue(String secretName) {
        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();
        return secretsManagerClient.getSecretValue(valueRequest).secretString();
    }
}
