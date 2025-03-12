package com.zhaoyss.exchange.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhaoyss
 * @date 6/3/2025 下午 5:53
 * @description:
 */
@Service
public class GenericDbService {

    @Autowired
    private MapperRegistry mapperRegistry;

    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    public <T> void insertIgnore(List<T> beans) {
        for (T bean : beans) {
            doInsert(bean);
        }
    }

    @SuppressWarnings("unchecked")
    <T> void doInsert(T bean) {
        BaseMapper<T> mapper = (BaseMapper<T>) mapperRegistry.getMapper(bean.getClass());
        mapper.insertOrUpdate(bean);
    }
}
