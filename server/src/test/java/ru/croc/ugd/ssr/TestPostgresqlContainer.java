package ru.croc.ugd.ssr;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Контейнер БД для запуска тестов.
 */
public class TestPostgresqlContainer extends PostgreSQLContainer<TestPostgresqlContainer> {

    /**
     * Контейнер.
     */
    private static TestPostgresqlContainer container;

    /**
     * Constructor.
     */
    public TestPostgresqlContainer() {
        super("postgres:10.5");
        withDatabaseName("integration-tests-db").withUsername("sa").withPassword("test");
    }

    /**
     * Возвращает контейнер.
     * 
     * @return контейнер
     */
    @SuppressFBWarnings("LI_LAZY_INIT_STATIC") // Параллельных вызовов не будет.
    public static TestPostgresqlContainer getInstance() {
        if (container == null) {
            container = new TestPostgresqlContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        // do nothing, JVM handles shut down
    }
}
