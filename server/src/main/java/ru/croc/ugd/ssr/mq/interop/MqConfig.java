package ru.croc.ugd.ssr.mq.interop;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsUtils;
import ru.croc.ugd.ssr.integration.service.EtpService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.mq.listener.StatusListener;
import ru.croc.ugd.ssr.mq.validation.XmlValidation;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

/**
 * Конфиг mq.
 */
@Configuration
@EnableConfigurationProperties({QueueProperties.class})
@RequiredArgsConstructor
public class MqConfig {

    @Value("${ibm.mq.url}")
    private String commonUrl;

    @Value("${ibm.mq.queue-manager}")
    private String commonQueueManager;

    @Value("${ibm.mq.user}")
    private String commonUser;

    @Value("${ibm.mq.password}")
    private String commonPassword;

    @Value("${ibm.mq.channel}")
    private String commonChannel;

    @Value("${ibm.mq.etp.url:}")
    private String etpUrl;

    @Value("${ibm.mq.etp.queue-manager}")
    private String etpQueueManager;

    @Value("${ibm.mq.etp.user}")
    private String etpUser;

    @Value("${ibm.mq.etp.password}")
    private String etpPassword;

    @Value("${ibm.mq.etp.channel}")
    private String etpChannel;

    @Value("${ibm.mq.guo.queue-manager}")
    private String guoQueueManager;

    @Value("${ibm.mq.guo.user}")
    private String guoUser;

    @Value("${ibm.mq.guo.password}")
    private String guoPassword;

    @Value("${ibm.mq.guo.channel}")
    private String guoChannel;

    @Value("${ibm.mq.disableAppQueueListener}")
    private Boolean disableListeners;

    private final ApplicationContext applicationContext;

    @Bean
    @Primary
    public ConnectionFactory jmsConnectionFactory() {
        return buildMqConnectionFactory(commonQueueManager, commonChannel, commonUser, commonPassword);
    }

    @Bean
    public ConnectionFactory jmsConnectionFactory2() {
        final String nonEmptyEtpUrl = etpUrl.isEmpty() ? commonUrl : etpUrl;
        return buildMqConnectionFactory(nonEmptyEtpUrl, etpQueueManager, etpChannel, etpUser, etpPassword);
    }

    @Bean
    @ConditionalOnProperty(name = "ibm.mq.guo.enabled")
    public ConnectionFactory guoConnectionFactory() {
        return buildMqConnectionFactory(guoQueueManager, guoChannel, guoUser, guoPassword);
    }

    private MQConnectionFactory buildMqConnectionFactory(
        final String manager, final String channel, final String user, final String password
    ) {
        return buildMqConnectionFactory(commonUrl, manager, channel, user, password);
    }

    private MQConnectionFactory buildMqConnectionFactory(
        final String url, final String manager, final String channel, final String user, final String password
    ) {
        try {
            MQConnectionFactory cf = new MQConnectionFactory();
            cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, manager);

            cf.setConnectionNameList(url);
            cf.setChannel(channel);
            cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
            cf.setStringProperty(WMQConstants.USERID, user);
            cf.setStringProperty(WMQConstants.PASSWORD, password);
            cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, false);

            cf.setTransportType(1);
            cf.setCCSID(1208);

            return cf;
        } catch (JMSException j) {
            throw JmsUtils.convertJmsAccessException(j);
        }
    }

    @Bean
    @Primary
    public JmsTemplate jmsTemplateCommon(final ConnectionFactory connectionFactory) {
        return buildJmsTemplate(connectionFactory);
    }

    @Bean
    public JmsTemplate jmsTemplateShipping(
        @Qualifier("jmsConnectionFactory2") final ConnectionFactory connectionFactory
    ) {
        return buildJmsTemplate(connectionFactory);
    }

    @Bean
    @ConditionalOnProperty(name = "ibm.mq.guo.enabled")
    public JmsTemplate jmsTemplateGuo(
        @Qualifier("guoConnectionFactory") final ConnectionFactory connectionFactory
    ) {
        return buildJmsTemplate(connectionFactory);
    }

    private JmsTemplate buildJmsTemplate(final ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        return jmsTemplate;
    }

    @Bean(name = "etpmvFactory")
    @Primary
    public JmsListenerContainerFactory<?> queueFactoryConfig(
        ConnectionFactory connectionFactory,
        DefaultJmsListenerContainerFactoryConfigurer configurer
    ) {
        return createJmsListenerContainerFactory(connectionFactory, configurer);
    }

    @Bean(name = "etpListenerFactory")
    public JmsListenerContainerFactory<?> queueFactoryConfig2(
        @Qualifier("jmsConnectionFactory2") ConnectionFactory connectionFactory,
        DefaultJmsListenerContainerFactoryConfigurer configurer
    ) {
        return createJmsListenerContainerFactory(connectionFactory, configurer);
    }

    @Bean(name = "guoListenerFactory")
    @ConditionalOnProperty(name = "ibm.mq.guo.enabled")
    public JmsListenerContainerFactory<?> guoQueueFactoryConfig(
        @Qualifier("guoConnectionFactory") ConnectionFactory connectionFactory,
        DefaultJmsListenerContainerFactoryConfigurer configurer
    ) {
        return createJmsListenerContainerFactory(connectionFactory, configurer);
    }

    private JmsListenerContainerFactory<?> createJmsListenerContainerFactory(
        ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer
    ) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(false);
        factory.setSessionTransacted(true);
        return factory;
    }

    @Bean(name = "senderShipping")
    public MqSender senderShipping(@Qualifier("jmsTemplateShipping") JmsTemplate jmsTemplate) {
        return new MqSender(jmsTemplate);
    }

    @Bean(name = "senderGuo")
    @ConditionalOnProperty(name = "ibm.mq.guo.enabled")
    public MqSender senderGuo(@Qualifier("jmsTemplateGuo") JmsTemplate jmsTemplate) {
        return new MqSender(jmsTemplate);
    }

    @Bean(name = "etpServiceShipping")
    public EtpService etpService(
        Environment environment,
        @Qualifier("senderShipping") MqSender mqSender,
        XmlUtils xmlUtils,
        XmlValidation xmlValidation
    ) {
        return new EtpService(environment, mqSender, xmlUtils, xmlValidation);
    }

    @Bean(name = "etpServiceGuo")
    @ConditionalOnProperty(name = "ibm.mq.guo.enabled")
    public EtpService etpServiceGuo(
        Environment environment,
        @Qualifier("senderGuo") MqSender mqSender,
        XmlUtils xmlUtils,
        XmlValidation xmlValidation
    ) {
        return new EtpService(environment, mqSender, xmlUtils, xmlValidation);
    }

    /**
     * Создание бина StatusListener.
     *
     * @return StatusListener
     */
    @Bean
    public StatusListener statusListener() {
        if (disableListeners) {
            return null;
        }
        return new StatusListener();
    }
}
