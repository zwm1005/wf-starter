package fun.werfamily.starter.mq.rocket.base;

import com.alibaba.fastjson.JSON;
import fun.werfamily.starter.mq.rocket.MQException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * RocketMQ的生产者的抽象基类
 *
 * @Author Mr.WenMing
 * Created on 2022/3/1.
 */
@SuppressWarnings("ALL")
@Slf4j
public abstract class AbstractMQProducer {

    private static MessageQueueSelector messageQueueSelector = new SelectMessageQueueByHash();

    public AbstractMQProducer() {
    }

    @Autowired(required = false)
    private DefaultMQProducer producer;

    public void shutdown() {
        if(Objects.nonNull(producer)) {
            producer.shutdown();
        }
    }

    /**
     * 同步发送消息
     *
     * @param topic
     * @param tag
     * @param key
     * @param o
     * @return
     */
    public SendResult syncSend(String topic, String tag, String key, Object o) {
        try {
            Message message = new Message(topic, tag, key, JSON.toJSONString(o).getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = producer.send(message);
            if (sendResult == null || sendResult.getSendStatus() != SendStatus.SEND_OK) {
                log.error("消息发送异常，topic : {}, tag : {}, key : {} , msg {} sendResult = {} ", topic, tag, key, JSON.toJSONString(o), JSON.toJSONString(sendResult));
            }
            return sendResult;
        } catch (Exception e) {
            log.error("消息发送失败，topic : {}, tag : {}, key : {} , msg {}", topic, tag, key, JSON.toJSONString(o), e);
            throw new MQException("消息发送失败，topic :" + topic + ",e:" + e.getMessage());
        }

    }

    /**
     * 同步延迟发送消息
     *
     * @param topic
     * @param tag
     * @param key
     * @param o
     * @param delayTimeLevel "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h"
     * @return
     */
    public SendResult syncDelaySend(String topic, String tag, String key, Object o, int delayTimeLevel) {
        try {
            Message message = new Message(topic, tag, key, JSON.toJSONString(o).getBytes(RemotingHelper.DEFAULT_CHARSET));
            message.setDelayTimeLevel(delayTimeLevel);
            SendResult sendResult = producer.send(message);
            if (sendResult == null || sendResult.getSendStatus() != SendStatus.SEND_OK) {
                log.error("消息发送异常，topic : {}, tag : {}, key : {} , msg {} sendResult = {} ", topic, tag, key, JSON.toJSONString(o), JSON.toJSONString(sendResult));
            }
            return sendResult;
        } catch (Exception e) {
            log.error("消息发送失败，topic : {}, tag : {}, key : {} , msg {}", topic, tag, key, JSON.toJSONString(o), e);
            throw new MQException("消息发送失败，topic :" + topic + ",e:" + e.getMessage());
        }

    }

    /**
     * 同步发送消息
     *
     * @param message 消息体
     * @throws MQException 消息异常
     */
    public SendResult syncSend(Message message) throws MQException {
        try {
            SendResult sendResult = producer.send(message);
            log.info("send rocketmq message ,messageId : {}", sendResult.getMsgId());
            this.doAfterSyncSend(message, sendResult);
            if (sendResult == null || sendResult.getSendStatus() != SendStatus.SEND_OK) {
                log.error("send rocketmq fail");
            }
            return sendResult;
        } catch (Exception e) {
            log.error("消息发送失败，topic : {}, msgObj {}", message.getTopic(), message, e);
            throw new MQException("消息发送失败，topic :" + message.getTopic() + ",e:" + e.getMessage());
        }
    }


    /**
     * 同步发送消息
     *
     * @param message 消息体
     * @param hashKey 用于hash后选择queue的key
     * @throws MQException 消息异常
     */
    public void syncSendOrderly(Message message, String hashKey) throws MQException {
        if (StringUtils.isEmpty(hashKey)) {
            // fall back to normal
            syncSend(message);
        }
        try {
            SendResult sendResult = producer.send(message, messageQueueSelector, hashKey);
            log.debug("send rocketmq message orderly ,messageId : {}", sendResult.getMsgId());
            this.doAfterSyncSend(message, sendResult);
        } catch (Exception e) {
            log.error("顺序消息发送失败，topic : {}, msgObj {}", message.getTopic(), message, e);
            throw new MQException("顺序消息发送失败，topic :" + message.getTopic() + ",e:" + e.getMessage());
        }
    }

    /**
     * 重写此方法处理发送后的逻辑
     *
     * @param message    发送消息体
     * @param sendResult 发送结果
     */
    public void doAfterSyncSend(Message message, SendResult sendResult) {
    }

    /**
     * 异步发送消息
     *
     * @param message      msgObj
     * @param sendCallback 回调
     * @throws MQException 消息异常
     */
    public void asyncSend(Message message, SendCallback sendCallback) throws MQException {
        try {
            producer.send(message, sendCallback);
            log.debug("send rocketmq message async");
        } catch (Exception e) {
            log.error("消息发送失败，topic : {}, msgObj {}", message.getTopic(), message, e);
            throw new MQException("消息发送失败，topic :" + message.getTopic() + ",e:" + e.getMessage());
        }
    }
}
