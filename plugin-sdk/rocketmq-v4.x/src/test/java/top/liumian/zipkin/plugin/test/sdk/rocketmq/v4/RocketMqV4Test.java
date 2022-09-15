package top.liumian.zipkin.plugin.test.sdk.rocketmq.v4;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import top.liumian.zipkin.core.tracing.TracingUtil;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author liumian  2022/8/22 12:52
 */
@FixMethodOrder
public class RocketMqV4Test {

    @Test
    public void aProducerTest() throws MQClientException {
        //Instantiate with a producer group name.
        DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
        // Specify name server addresses.
        producer.setNamesrvAddr("localhost:9876");
        //Launch the instance.
        producer.start();
        for (int i = 0; i < 2; i++) {
            //Create a message instance, specifying topic, tag and message body.
            Message msg;
            try {
                msg = new Message("TopicTest" /* Topic */,
                        "TagA" /* Tag */,
                        ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
                );
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            //Call send message to deliver message to one of brokers.
            SendResult sendResult = null;
            try {
                sendResult = producer.send(msg);
            } catch (MQClientException | RemotingException | InterruptedException | MQBrokerException e) {
                throw new RuntimeException(e);
            }
            System.out.printf("%s - %s%n", sendResult,msg.getProperties().get("X-B3-TraceId"));
            Assert.assertNotNull(msg.getProperties().get("X-B3-TraceId"));
        }
        //Shut down once the producer instance is not longer in use.

        producer.shutdown();
    }

    @Test
    public void bConsumerTest() throws MQClientException, InterruptedException {

        // Instantiate with specified consumer group name.
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("123");

        // Specify name server addresses.
        consumer.setNamesrvAddr("localhost:9876");

        // Subscribe one more more topics to consume.
        consumer.subscribe("TopicTest", "*");

        // Register callback to execute on arrival of messages fetched from brokers.
        MessageListenerConcurrently messageListenerConcurrently = new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {
                System.out.printf("%s - Receive New Messages: %s %n", TracingUtil.getTraceId(), msgs.get(0).getMsgId());
                Assert.assertNotNull(TracingUtil.getTraceId());
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        };
        consumer.registerMessageListener(messageListenerConcurrently);

        //Launch the consumer instance.
        consumer.start();

        System.out.println("Consumer Started");

        Thread.sleep(5000L);
    }




}
