# Spring Boot Multiple Datasouce H2 Initialization using Liquibase

This is an example project of how to use [Liquibase](http://www.liquibase.org/index.html) to initialize multiple H2 databases for local development in a Spring Boot application.

The impetus for this came about as I was working on a project for a client where we were re-writing several applications that used the same database. Some of the data that the application managed consisted of PHI (personal health information). For this reason the client did not want us to copy that data onto our local laptops which lead to us using H2 in memory databases for local development with dummy data.
 
As we were re-writing these multiple thick client applications into a single web application we ran into an issue that we needed to connect to multiple datasources (be they new databases for the application or importing data from databases managed by other systems). We also wanted to use SQL extracted from those existing databases (as much as possible) to verify that what we were developing and mapping would work when deployed.
 
At this point we could no long rely on Spring Boot's [built in database initialization](https://docs.spring.io/spring-boot/docs/current/reference/html/howto-database-initialization.html) of supplying a schema.sql statement in your application properties as Spring Boot only supports the autoconfiguation of one datasource.

```
spring:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    show-sql: true
    generate-ddl: true
    hibernate.ddl-auto: validate

  datasource:
    url: jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driverClassName: org.h2.Driver
    username: sa
    password:

    h2:
      console:
        path: /myconsole
        enabled: true

    initialize: true
    schema: classpath:schema-h2.sql
```

## Solution

After some research and finding [documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto-two-datasources) and examples of how to configure multiple datasources with Spring Boot, I did not really find anything for initializing H2 databases. I did however find information about using Liquibase with Spring Boot and [multiple datasources](https://github.com/jeffxor/spring-multiple-datasources). This lead me to the solution that we ended up using on our project and it has worked pretty well.

1. We have to disable the database autoconfiguartion.

    ```
    @SpringBootApplication
    @EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration.class])
    class ExampleApplication extends SpringBootServletInitializer {
    ```

2. We created a database `@Configuration` for each datasource along with an entry in the application properties that the configuration will read in and apply:

    Here is the primary database configuration. Most of this configuration should be fairly self explanatory. I will walk through it briefly.
    ```
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
    
    ...
    
    
      primary:
        datasource:
          url: jdbc:h2:mem:modern;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;
          username: sa
          password:
          driver-class-name: org.h2.Driver
        jpa:
          properties:
            hibernate:
              default_schema: admin_records
              hbm2ddl:
                ddl-auto: none
              naming:
                  physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    ```
    
   Here we set up the datasource:
    
    ```
    DatabaseConfig
    
    @Primary
    @Bean(name = 'dataSource')
    @ConfigurationProperties(prefix = 'spring.primary.datasource')
    DataSource dataSource() {
        return DataSourceBuilder.create().build()
    }
    
    ...
    
    application.yml
    
    primary:
        datasource:
          url: jdbc:h2:mem:modern;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;
          username: sa
          password:
          driver-class-name: org.h2.Driver
    
    ```
    
    Here we set hibernate properties that were needed due to the existing table designs / naming strategies.
    
    ```
    DatabaseConfig
    
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

    application.yml
    
    jpa:
      properties:
        hibernate:
          default_schema: admin_records
          hbm2ddl:
            ddl-auto: none
          naming:
              physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    ```
    
    Next we had to setup the liquibase beans for initializing the database.
    
    ```
    DatabaseConfig
    
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
    
    application.yml
    
    liquibase-changelogs:
      primary:
        liquibase:
          change-log: classpath:/db.changelog/db.changelog-master.xml
          default-schema: admin_records
          drop-first: true
    ```
    
    As we only wanted to use Liquibase to initialize the local development H2 databases, we put `@Profile(value = 'Local')` on all the liquibase beans to ensure they were only instantiated with the "Local" profile.