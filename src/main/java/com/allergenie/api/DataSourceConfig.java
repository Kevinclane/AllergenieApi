package com.allergenie.api;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Configuration
public class DataSourceConfig {
    private final SecretClient secretClient;
    private final Environment environment;
    private final boolean isLive;

    public DataSourceConfig(Environment environment) {
        this.environment = environment;
        this.isLive = Arrays.stream(environment.getActiveProfiles()).anyMatch(env -> (env.equalsIgnoreCase("dev") || env.equalsIgnoreCase("test") || env.equalsIgnoreCase("prod")));
        if (isLive) {
            this.secretClient = new SecretClientBuilder()
                    .vaultUrl(environment.getProperty("spring.cloud.azure.keyvault.secret.endpoint"))
                    .credential(new DefaultAzureCredentialBuilder().build())
                    .buildClient();
        } else {
            this.secretClient = null;
        }
    }

    @Bean
    public HikariDataSource dataSource() {

        HikariConfig config = new HikariConfig();
        if (isLive) {
            config.setJdbcUrl(secretClient.getSecret("ConnectionString").getValue());
            config.setUsername(secretClient.getSecret("Username").getValue());
            config.setPassword(secretClient.getSecret("Password").getValue());
        } else {
            config.setJdbcUrl(environment.getProperty("spring.datasource.url"));
            config.setUsername(environment.getProperty("spring.datasource.username"));
            config.setPassword(environment.getProperty("spring.datasource.password"));
        }

        return new HikariDataSource(config);
    }
}
