package org.example.config.database

import groovy.util.logging.Slf4j
import liquibase.integration.spring.SpringLiquibase
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = 'entityManagerFactory',
        basePackages = ['org.example.domain.repository']
)
class DatabaseConfig {

    @Primary
    @Bean(name = 'dataSource')
    @ConfigurationProperties(prefix = 'spring.primary.datasource')
    DataSource dataSource() {
        return DataSourceBuilder.create().build()
    }

    @Value('${spring.primary.jpa.properties.hibernate.hbm2ddl.ddl-auto:}')
    String hibernateHbm2DllAuto

    @Value('${spring.primary.jpa.properties.hibernate.default_schema:}')
    String hibernateDefaultSchema

    @Value('${spring.primary.jpa.properties.hibernate.naming.physical-strategy:}')
    String hibernateNamingPhysicalStrategy

    @Primary
    @Bean(name = 'entityManagerFactory')
    LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                @Qualifier('dataSource') DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>()
        if (hibernateHbm2DllAuto) {
            properties.put('hibernate.hbm2ddl.auto', hibernateHbm2DllAuto)
        }
        if (hibernateDefaultSchema) {
            properties.put('hibernate.default_schema', hibernateDefaultSchema)
        }
        if (hibernateNamingPhysicalStrategy) {
            properties.put('hibernate.naming.physical-strategy', hibernateNamingPhysicalStrategy)
        }

        log.debug("datasource specific entityManager properties: ${properties}")

        return builder
                .dataSource(dataSource)
                .packages('org.example.domain')
                .persistenceUnit('primary')
                .properties(properties)
                .build()
    }

    @Primary
    @Bean(name = 'transactionManager')
    PlatformTransactionManager transactionManager(
            @Qualifier('entityManagerFactory') EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory)
    }

    @Profile(value = 'Local')
    @Bean(name = 'primaryLiquibaseProperties')
    @ConfigurationProperties('liquibase-changelogs.primary.liquibase')
    LiquibaseProperties primaryLiquibaseProperties() {
        return new LiquibaseProperties()
    }

    @Profile(value = 'Local')
    @Bean(name = 'liquibase')
    SpringLiquibase primaryLiquibase(@Qualifier('primaryLiquibaseProperties') LiquibaseProperties liquibaseProperties) {
        SpringLiquibase primary = new SpringLiquibase()
        primary.setDataSource(dataSource())
        primary.setChangeLog(primaryLiquibaseProperties().getChangeLog())

        return primary
    }
}
