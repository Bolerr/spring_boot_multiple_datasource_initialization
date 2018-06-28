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
        entityManagerFactoryRef = 'legacyEntityMangerFactory',
        transactionManagerRef = 'legacyTransactionManager',
        basePackages = ['org.example.legacy.repository']
)
class LegacyDatabaseConfig {

    @Bean(name = 'legacyDataSource')
    @ConfigurationProperties(prefix = 'spring.legacy.datasource')
    DataSource legacyDataSource() {
        return DataSourceBuilder.create().build()
    }

    @Value('${spring.legacy.jpa.properties.hibernate.hbm2ddl.ddl-auto}')
    String hibernateHbm2DllAuto

    @Value('${spring.legacy.jpa.properties.hibernate.default_schema}')
    String hibernateDefaultSchema

    @Value('${spring.legacy.jpa.properties.hibernate.naming.physical-strategy}')
    String hibernateNamingPhysicalStrategy

    @Bean(name = 'legacyEntityMangerFactory')
    LocalContainerEntityManagerFactoryBean legacyEntityManager(EntityManagerFactoryBuilder builder,
                                                               @Qualifier('legacyDataSource') DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>()
        properties.put('hibernate.hbm2ddl.auto', hibernateHbm2DllAuto)
        properties.put('hibernate.default_schema', hibernateDefaultSchema)
        properties.put('hibernate.naming.physical-strategy', hibernateNamingPhysicalStrategy)

        log.debug("datasource specific entityManager properties: ${properties}")

        return builder
                .dataSource(dataSource)
                .packages('org.example.legacy.domain')
                .persistenceUnit('legacy')
                .properties(properties)
                .build()
    }

    @Bean(name = 'legacyTransactionManager')
    PlatformTransactionManager legacyTransactionManager(
            @Qualifier('legacyEntityMangerFactory') EntityManagerFactory legacyEntityManager) {
        return new JpaTransactionManager(legacyEntityManager)
    }

    @Profile(value = 'Local')
    @Bean(name = 'legacyLiquibaseProperties')
    @ConfigurationProperties('liquibase-changelogs.legacy.liquibase')
    LiquibaseProperties legacyLiquibaseProperties() {
        return new LiquibaseProperties()
    }

    @Profile(value = 'Local')
    @Bean(name = 'legacy-liquibase')
    SpringLiquibase metadataLiquibase(@Qualifier('legacyLiquibaseProperties') LiquibaseProperties liquibaseProperties) {
        SpringLiquibase metadata = new SpringLiquibase()
        metadata.setDataSource(legacyDataSource())
        metadata.setChangeLog(legacyLiquibaseProperties().getChangeLog())

        return metadata
    }
}
