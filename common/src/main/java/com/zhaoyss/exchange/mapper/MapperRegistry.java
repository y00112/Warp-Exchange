package com.zhaoyss.exchange.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhaoyss
 * @date 6/3/2025 下午 5:53
 * @description:
 */
@Component
public class MapperRegistry implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private static final Map<Class<?>, BaseMapper<?>> mapperMap = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        // 获取所有BeanMapper类型的Bean
        String[] beanNames = applicationContext.getBeanNamesForType(BaseMapper.class);
        for (String beanName : beanNames) {
            BaseMapper<?> mapper = (BaseMapper<?>) applicationContext.getBean(beanName);
            Class<?> entityClass = resolveEntityClass(mapper);
            if (entityClass != null) {
                mapperMap.put(entityClass, mapper);
            }
        }
    }

    private Class<?> resolveEntityClass(BaseMapper<?> mapper) {
        // 处理代理类获取原始接口
        Class<?> mapperInterface = getMapperInterface(mapper.getClass());
        if (mapperInterface != null) return null;

        // 解析泛型参数类型
        Type[] types = mapperInterface.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) type;
                if (pt.getRawType().equals(BaseMapper.class)) {
                    Type[] actualArgs = pt.getActualTypeArguments();
                    if (actualArgs.length > 0 && actualArgs[0] instanceof Class) {
                        return (Class<?>) actualArgs[0];
                    }
                }
            }
        }
        return null;
    }

    private Class<?> getMapperInterface(Class<?> mapperClass) {
        if (Proxy.isProxyClass(mapperClass)) {
            // JDK 动态代理，获取实现接口中的BeanMapper子接口
            for (Class<?> iface : mapperClass.getInterfaces()) {
                if (BaseMapper.class.isAssignableFrom(iface)) {
                    return iface;
                }
            }
        } else {
            // 非代理类直接返回 n
            return mapperClass;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> BaseMapper<T> getMapper(Class<T> entityClass) {
        return (BaseMapper<T>) mapperMap.get(entityClass);
    }
}
