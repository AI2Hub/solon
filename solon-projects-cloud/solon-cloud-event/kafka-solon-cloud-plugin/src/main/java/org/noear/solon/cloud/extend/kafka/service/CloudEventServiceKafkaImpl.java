package org.noear.solon.cloud.extend.kafka.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.extend.kafka.impl.KafkaConfig;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.model.EventTransaction;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.cloud.service.CloudEventServicePlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author noear
 * @since 1.3
 */
public class CloudEventServiceKafkaImpl implements CloudEventServicePlus, Closeable {
    private static final Logger log = LoggerFactory.getLogger(CloudEventServiceKafkaImpl.class);

    private final KafkaConfig config;
    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;

    public CloudEventServiceKafkaImpl(CloudProps cloudProps) {
        this.config = new KafkaConfig(cloudProps);
    }

    private void initProducer() {
        if (producer != null) {
            return;
        }

        Utils.locker().lock();

        try {
            if (producer != null) {
                return;
            }

            Properties properties = config.getProducerProperties();
            producer = new KafkaProducer<>(properties);
        } finally {
            Utils.locker().unlock();
        }
    }

    private void initConsumer() {
        if (consumer != null) {
            return;
        }

        Utils.locker().lock();

        try {
            if (consumer != null) {
                return;
            }

            Properties properties = config.getConsumerProperties();
            consumer = new KafkaConsumer<>(properties);
        } finally {
            Utils.locker().unlock();
        }
    }

    private void beginTransaction(EventTransaction transaction) throws CloudEventException {
        //不支持事务消息
        log.warn("Message transactions are not supported!");
    }

    @Override
    public boolean publish(Event event) throws CloudEventException {
        initProducer();

        if (Utils.isEmpty(event.key())) {
            event.key(Utils.guid());
        }

        if(event.transaction() != null){
            beginTransaction(event.transaction());
        }

        Future<RecordMetadata> future = producer.send(new ProducerRecord<>(event.topic(), event.key(), event.content()));
        if (config.getPublishTimeout() > 0 && event.qos() > 0) {
            try {
                future.get(config.getPublishTimeout(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                throw new CloudEventException(e);
            }
        }

        return true;
    }

    CloudEventObserverManger observerManger = new CloudEventObserverManger();

    @Override
    public void attention(EventLevel level, String channel, String group, String topic, String tag, int qos, CloudEventHandler observer) {
        observerManger.add(topic, level, group, topic, tag, qos, observer);
    }

    public void subscribe() {
        //订阅
        if (observerManger.topicSize() > 0) {
            try {
                initConsumer();
                consumer.subscribe(observerManger.topicAll());

                //开始拉取
                new Thread(this::subscribePull).start();
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void subscribePull() {
        while (true) {
            try {
                subscribePullDo();
            } catch (EOFException e) {
                break;
            } catch (Throwable e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    private void subscribePullDo() throws Throwable {
        //拉取
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

        //如果没有小休息下
        if (records.isEmpty()) {
            Thread.sleep(100);
            return;
        }

        Map<TopicPartition, OffsetAndMetadata> topicOffsets = new LinkedHashMap<>();

        for (ConsumerRecord<String, String> record : records) {
            Event event = new Event(record.topic(), record.value())
                    .key(record.key())
                    .channel(config.getEventChannel());

            try {
                //接收并处理事件
                if (onReceive(event)) {
                    //接收需要提交的偏移量
                    topicOffsets.put(new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1));
                }
            } catch (Throwable e) {
                log.warn(e.getMessage(), e);
            }
        }

        if (topicOffsets.size() > 0) {
            consumer.commitAsync(topicOffsets, null);
        }
    }

    /**
     * 处理接收事件
     */
    public boolean onReceive(Event event) throws Throwable {
        boolean isOk = true;
        CloudEventHandler handler = null;

        handler = observerManger.getByTopic(event.topic());
        if (handler != null) {
            isOk = handler.handle(event);
        } else {
            //只需要记录一下
            log.warn("There is no observer for this event topic[{}]", event.topic());
        }

        return isOk;
    }

    @Override
    public String getChannel() {
        return config.getEventChannel();
    }

    @Override
    public String getGroup() {
        return config.getEventGroup();
    }

    @Override
    public void close() throws IOException {
        if (producer != null) {
            producer.close();
        }

        if (consumer != null) {
            consumer.close();
        }
    }
}
