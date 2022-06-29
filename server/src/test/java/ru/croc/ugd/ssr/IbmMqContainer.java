package ru.croc.ugd.ssr;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

public class IbmMqContainer extends DockerComposeContainer {
    private static IbmMqContainer container;

    public IbmMqContainer() {
        super(new File("src/test/resources/docker-compose.yml"));
        withExposedService("ibmmq", 1414);
    }

    /**
     * Возвращает контейнер.
     *
     * @return контейнер
     */
    @SuppressFBWarnings("LI_LAZY_INIT_STATIC") // Параллельных вызовов не будет.
    public static IbmMqContainer getInstance() {
        if (container == null) {
            container = new IbmMqContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("MQ_URL", container.getServiceHost("ibmmq", 1414));
    }
}
