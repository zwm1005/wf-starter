package fun.werfamily.starter.mq.rocket.base;

import fun.werfamily.starter.mq.rocket.MQException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;

/**
 * RocketMQ的事务生产者的抽象基类
 *
 * @Author Mr.WenMing
 * Created on 2022/3/1.
 */
@SuppressWarnings("ALL")
@Slf4j
public abstract class AbstractMQTransactionProducer implements TransactionListener {

    private TransactionMQProducer transactionProducer;

    public void setProducer(TransactionMQProducer transactionProducer) {
        this.transactionProducer = transactionProducer;
    }

    public SendResult sendMessageInTransaction(Message msg, Object arg) throws MQException {
        try {
            SendResult sendResult = transactionProducer.sendMessageInTransaction(msg, arg);
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                log.error("事务消息发送失败，topic : {}, msgObj {}", msg.getTopic(), msg);
                throw new MQException("事务消息发送失败，topic :" + msg.getTopic() + ", status :" + sendResult.getSendStatus());
            }
            log.info("发送事务消息成功，事务id: {}", msg.getTransactionId());
            return sendResult;
        } catch (Exception e) {
            log.error("事务消息发送失败，topic : {}, msgObj {}", msg.getTopic(), msg);
            throw new MQException("事务消息发送失败，topic :" + msg.getTopic() + ",e:" + e.getMessage());
        }
    }


}
