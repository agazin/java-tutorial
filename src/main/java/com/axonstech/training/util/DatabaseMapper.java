package com.axonstech.training.util;


import com.axonstech.training.exception.BusinessException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseMapper {
    private static DatabaseMapper instance = null;
    private final ObjectMapper objectMapper;

    private DatabaseMapper() {
        objectMapper = new ObjectMapper();
        MapperFeature[] features = {MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES};
        objectMapper.setConfig(objectMapper.getSerializationConfig().with(features));
        MapperFeature[] features1 = {MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES};
        objectMapper.setConfig(objectMapper.getDeserializationConfig().with(features1));
    }

    public static DatabaseMapper getInstance() {
        if (instance == null) instance = new DatabaseMapper();
        return instance;
    }

    private List<Map<String, Object>> convertTuplesToMap(List<?> tuples) {
        List<Map<String, Object>> result = new ArrayList<>();
        tuples.forEach(object -> {
            if (object instanceof Tuple) {
                Tuple single = (Tuple) object;
                Map<String, Object> tempMap = new HashMap<>();
                for (TupleElement<?> key : single.getElements()) {
                    tempMap.put(key.getAlias(), single.get(key));
                }
                result.add(tempMap);
            } else throw new BusinessException("Query should return instance of Tuple");
        });

        return result;
    }

    private Map<String, Object> convertTuplesToMap(Tuple tuple) {
        Tuple single = (Tuple) tuple;
        Map<String, Object> tempMap = new HashMap<>();
        for (TupleElement<?> key : single.getElements()) {
            tempMap.put(key.getAlias(), single.get(key));
        }
        return tempMap;
    }

    public <T> T parseResult(Tuple tuple, Class<T> clz) {
        return objectMapper.convertValue(convertTuplesToMap(tuple), clz);
    }

    public <T> List<T> parseResult(List<?> list, Class<T> clz) {
        List<T> result = new ArrayList<>();
        convertTuplesToMap(list)
                .forEach(map -> result.add(objectMapper.convertValue(map, clz))
                );
        return result;
    }

}

