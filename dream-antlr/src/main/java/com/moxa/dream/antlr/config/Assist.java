package com.moxa.dream.antlr.config;

import com.moxa.dream.antlr.exception.AntlrException;
import com.moxa.dream.antlr.factory.InvokerFactory;
import com.moxa.dream.antlr.invoker.Invoker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Assist {
    private Map<Class, Object> customObjMap;
    private Map<String, InvokerFactory> invokerFactoryMap;
    private Map<String, Invoker> sqlInvokerMap;

    public Assist(Collection<InvokerFactory> invokerFactoryList, Map<Class, Object> customObjMap) {
        setInvokerFactoryList(invokerFactoryList);
        this.customObjMap = customObjMap;
    }

    public void setInvokerFactoryList(Collection<InvokerFactory> invokerFactoryList) {
        if (invokerFactoryList != null && !invokerFactoryList.isEmpty()) {
            invokerFactoryMap = new HashMap<>();
            sqlInvokerMap = new HashMap<>();
            for (InvokerFactory invokerFactory : invokerFactoryList) {
                String namespace = invokerFactory.namespace();
                invokerFactoryMap.put(namespace, invokerFactory);
            }
        }
    }

    public Invoker getInvoker(String namespace, String function) throws AntlrException {
        String invokerKey;
        Invoker invoker;
        if (namespace == null) {
            invokerKey = function;
            invoker = sqlInvokerMap.get(invokerKey);
            if (invoker == null) {
                for (InvokerFactory invokerFactory : invokerFactoryMap.values()) {
                    invoker = invokerFactory.create(function);
                    if (invoker != null) {
                        namespace = invokerFactory.namespace();
                        if (namespace == null) {
                            invokerKey = function;
                        } else {
                            invokerKey = function + ":" + namespace;
                        }
                        sqlInvokerMap.put(function, invoker);
                        break;
                    }
                }
            } else
                return invoker;
        } else {
            invokerKey = function + ":" + namespace;
            invoker = sqlInvokerMap.get(invokerKey);
            if (invoker == null) {
                InvokerFactory invokerFactory = invokerFactoryMap.get(namespace);
                if (invokerFactory == null) {
                    throw new AntlrException("命名空间'" + namespace + "'尚未注册");
                }
                invoker = invokerFactory.create(function);
                if (!sqlInvokerMap.containsKey(function)) {
                    sqlInvokerMap.put(function, invoker);
                }
            } else
                return invoker;
        }
        if (invoker == null) {
            throw new AntlrException("函数@" + invokerKey + "尚未注册");
        }
        sqlInvokerMap.put(invokerKey, invoker);
        invoker.init(this);
        return invoker;
    }

    public <T> T getCustom(Class<T> type) {
        return (T) customObjMap.get(type);
    }

    public <T> void setCustom(Class<T> type, T value) {
        if (customObjMap == null) {
            customObjMap = new HashMap<>();
        }
        customObjMap.put(type, value);
    }

    public Map<String, Invoker> getSqlInvokerMap() {
        return sqlInvokerMap;
    }
}
