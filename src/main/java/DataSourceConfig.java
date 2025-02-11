import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Configuration
public class DataSourceConfig {
    private final SecretClient secretClient;
    private final Environment environment;
    private final boolean isLocal;

    public DataSourceConfig(Environment environment) {
        this.environment = environment;
        if (Arrays.stream(environment.getActiveProfiles()).noneMatch(env -> (env.equalsIgnoreCase("local")))) {
            this.secretClient = new SecretClientBuilder()
                    .vaultUrl(environment.getProperty("spring.cloud.azure.keyvault.secret.endpoint"))
                    .credential(new DefaultAzureCredentialBuilder().build())
                    .buildClient();
            this.isLocal = false;
        } else {
            this.secretClient = null;
            this.isLocal = true;
        }
    }

    @Bean
    public HikariDataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        if (isLocal) {
            dataSource.setJdbcUrl(environment.getProperty("spring.datasource.url"));
            dataSource.setUsername(environment.getProperty("spring.datasource.username"));
            dataSource.setPassword(environment.getProperty("spring.datasource.password"));
        } else {
            dataSource.setJdbcUrl(secretClient.getSecret("ConnectionString").getValue());
        }

        return dataSource;
    }
}
