package ru.croc.ugd.ssr.report;

import org.junit.ClassRule;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.croc.ugd.ssr.BaseRestTest;
import ru.croc.ugd.ssr.IbmMqContainer;
import ru.croc.ugd.ssr.TestPostgresqlContainer;

/**
 * Родительский класс для интеграционных тестов потоков.
 * Поднимает постгре и ibmmq.
 */
public abstract class AbstractReportTest extends BaseRestTest {

    /**
     * Контейнер postgres.
     */
    @ClassRule
    public static PostgreSQLContainer<?> postgreSQLContainer = TestPostgresqlContainer.getInstance();

}
