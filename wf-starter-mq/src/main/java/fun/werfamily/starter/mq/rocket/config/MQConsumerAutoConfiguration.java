package fun.werfamily.starter.mq.rocket.config;

import fun.werfamily.starter.mq.rocket.annotation.MQConsumer;
import fun.werfamily.starter.mq.rocket.base.AbstractMQPushConsumer;
import fun.werfamily.starter.mq.rocket.base.MessageExtConst;
import fun.werfamily.starter.mq.rocket.trace.common.OnsTraceConstants;
import fun.werfamily.starter.mq.rocket.trace.dispatch.impl.AbstractAsyncTraceAppender;
import fun.werfamily.starter.mq.rocket.trace.dispatch.impl.AbstractAsyncTraceDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Description: 自动装配消息消费者
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/3/1.
 */
@SuppressWarnings("ALL")
@Slf4j
@Configuration
@ConditionalOnBean(MQBaseAutoConfiguration.class)
public class MQConsumerAutoConfiguration extends MQBaseAutoConfiguration {

    private AbstractAsyncTraceDispatcher asyncTraceDispatcher;
    /**
     * 维护一份map用于检测是否用同样的consumerGroup订阅了不同的topic+tag
     */
    private Map<String, String> validConsumerMap;

    @PostConstruct
    public void init() throws Exception {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(MQConsumer.class);
        validConsumerMap = new HashMap<>(12);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            publishConsumer(entry.getKey(), entry.getValue());
        }
        // 清空map，等待回收
        validConsumerMap = null;
    }

    private AbstractAsyncTraceDispatcher initAsyncAppender() {
        if (asyncTraceDispatcher != null) {
            return asyncTraceDispatcher;
        }
        try {
            Properties tempProperties = new Properties();
            tempProperties.put(OnsTraceConstants.MAX_MSG_SIZE, "128000");
            tempProperties.put(OnsTraceConstants.ASYNC_BUFFER_SIZE, "2048");
            tempProperties.put(OnsTraceConstants.MAX_BATCH_NUM, "1");
            tempProperties.put(OnsTraceConstants.WAKE_UP_NUM, "1");
            tempProperties.put(OnsTraceConstants.NAMESRV_ADDR, mqProperties.getNameServerAddress());
            tempProperties.put(OnsTraceConstants.INSTANCE_NAME, UUID.randomUUID().toString());
            AbstractAsyncTraceAppender asyncAppender = new AbstractAsyncTraceAppender(tempProperties);
            asyncTraceDispatcher = new AbstractAsyncTraceDispatcher(tempProperties);
            asyncTraceDispatcher.start(asyncAppender, "DEFAULT_WORKER_NAME");
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        return asyncTraceDispatcher;
    }

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    private void publishConsumer(String beanName, Object bean) throws Exception {
        MQConsumer mqConsumer = applicationContext.findAnnotationOnBean(beanName, MQConsumer.class);
        if (StringUtils.isEmpty(mqProperties.getNameServerAddress())) {
            throw new RuntimeException("name server address must be defined");
        }
        Assert.notNull(mqConsumer.consumerGroup(), "consumer's consumerGroup must be defined");
        Assert.notNull(mqConsumer.topic(), "consumer's topic must be defined");
        if (!AbstractMQPushConsumer.class.isAssignableFrom(bean.getClass())) {
            throw new RuntimeException(bean.getClass().getName() + " - consumer未实现Consumer抽象类");
        }
        Environment environment = applicationContext.getEnvironment();

        String consumerGroup = environment.resolvePlaceholders(mqConsumer.consumerGroup());
        String topic = environment.resolvePlaceholders(mqConsumer.topic());
        String tags = "*";
        if (mqConsumer.tag().length == 1) {
            tags = environment.resolvePlaceholders(mqConsumer.tag()[0]);
        } else if (mqConsumer.tag().length > 1) {
            tags = StringUtils.join(mqConsumer.tag(), "||");
        }

        // 检查consumerGroup
        if (!StringUtils.isEmpty(validConsumerMap.get(consumerGroup))) {
            String exist = validConsumerMap.get(consumerGroup);
            throw new RuntimeException("消费组重复订阅，请新增消费组用于新的topic和tag组合: " + consumerGroup + "已经订阅了" + exist);
        } else {
            validConsumerMap.put(consumerGroup, topic + "-" + tags);
        }

        // 配置push consumer
        if (AbstractMQPushConsumer.class.isAssignableFrom(bean.getClass())) {

            DefaultMQPushConsumer consumer = null;
            if (mqProperties.getTraceEnabled()) {
                consumer = new DefaultMQPushConsumer(consumerGroup, true);
            } else {
                consumer = new DefaultMQPushConsumer(consumerGroup);
            }

            consumer.setNamesrvAddr(mqProperties.getNameServerAddress());
            consumer.setMessageModel(MessageModel.valueOf(mqConsumer.messageMode()));
            consumer.subscribe(topic, tags);
            consumer.setInstanceName(UUID.randomUUID().toString());
            consumer.setVipChannelEnabled(mqProperties.getVipChannelEnabled());
            AbstractMQPushConsumer abstractMQPushConsumer = (AbstractMQPushConsumer) bean;
            if (MessageExtConst.CONSUME_MODE_CONCURRENTLY.equals(mqConsumer.consumeMode())) {
                consumer.registerMessageListener((List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) ->
                        abstractMQPushConsumer.dealMessage(list, consumeConcurrentlyContext));
            } else if (MessageExtConst.CONSUME_MODE_ORDERLY.equals(mqConsumer.consumeMode())) {
                consumer.registerMessageListener((List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) ->
                        abstractMQPushConsumer.dealMessage(list, consumeOrderlyContext));
            } else {
                throw new RuntimeException("unknown consume mode ! only support CONCURRENTLY and ORDERLY");
            }
            log.debug("DefaultMQPushConsumer namesrvAddr:{}, messageModel:{}, topic:{}, tag:{}, instanceName:{}, vipChannelEnabled:{}, traceDispatcher:{}", consumer.getNamesrvAddr(), consumer.getMessageModel(),
                    topic, tags, consumer.getInstanceName(), consumer.isVipChannelEnabled(), consumer.getTraceDispatcher());
            abstractMQPushConsumer.setConsumer(consumer);

            consumer.start();
        }

        log.info(String.format("%s is ready to subscribe message", bean.getClass().getName()));
    }

}