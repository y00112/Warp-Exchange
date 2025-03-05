package com.zhaoyss.exchange.messaging;

import com.zhaoyss.exchange.message.AbstractMessage;
import com.zhaoyss.exchange.util.JsonUtil;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author zhaoyss
 * @date 5/3/2025 下午 1:23
 * @description: 保存消息类型
 */
@Component
public class MessageTypes {

    final Logger logger = LoggerFactory.getLogger(getClass());

    final String messagePackage = AbstractMessage.class.getPackageName();

    final Map<String, Class<? extends AbstractMessage>> messageTypes = new HashMap<>();

    @SuppressWarnings("unchecked")
    @PostMapping
    public void init() {
        logger.info("find message classes...");
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new TypeFilter() {
            @Override
            public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                return AbstractMessage.class.isAssignableFrom(clazz);
            }
        });

        Set<BeanDefinition> beans = provider.findCandidateComponents(messagePackage);
        for (BeanDefinition bean : beans) {
            try {
                Class<?> clazz = Class.forName(bean.getBeanClassName());
                logger.info("found message class: {}", clazz.getName());
                if (this.messageTypes.put(clazz.getName(), (Class<? extends AbstractMessage>) clazz) != null) {
                    throw new RuntimeException("duplicate message type name: " + clazz.getName());
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String serialize(AbstractMessage message) {
        String type = message.getClass().getName();
        String json = JsonUtil.writeJson(message);
        return type + SEP + json;
    }

    public AbstractMessage deserialize(String data) {
        int pos = data.indexOf(SEP);
        if (pos == -1) {
            throw new RuntimeException("Unable to handle message with data: " + data);
        }
        String type = data.substring(0, pos);
        Class<? extends AbstractMessage> clazz = messageTypes.get(type);
        if (clazz == null) {
            throw new RuntimeException("Unable to handle message with type: " + type);
        }
        String json = data.substring(pos + 1);
        return JsonUtil.readJson(json, clazz);

    }

    private static final char SEP = '#';
}
