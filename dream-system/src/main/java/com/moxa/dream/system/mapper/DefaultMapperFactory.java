package com.moxa.dream.system.mapper;

import com.moxa.dream.system.annotation.Mapper;
import com.moxa.dream.system.annotation.Param;
import com.moxa.dream.system.annotation.Sql;
import com.moxa.dream.system.config.Configuration;
import com.moxa.dream.system.config.MethodInfo;
import com.moxa.dream.system.config.MethodParam;
import com.moxa.dream.system.core.action.Action;
import com.moxa.dream.system.core.resultsethandler.ResultSetHandler;
import com.moxa.dream.system.core.statementhandler.StatementHandler;
import com.moxa.dream.system.provider.ActionProvider;
import com.moxa.dream.util.common.NonCollection;
import com.moxa.dream.util.common.NullObject;
import com.moxa.dream.util.common.ObjectMap;
import com.moxa.dream.util.common.ObjectUtil;
import com.moxa.dream.util.exception.DreamRunTimeException;
import com.moxa.dream.util.reflect.ReflectUtil;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;


public class DefaultMapperFactory implements MapperFactory {
    protected Map<Method, MethodInfo> methodInfoMap = new HashMap<>(1024);
    protected Map<Class, Class[]> mapperTypeMap = new HashMap<>(512);

    @Override
    public boolean addMapper(Configuration configuration, Class mapperClass) {
        if (isMapper(mapperClass)) {
            Map<String, MethodInfo> methodInfoMap = new HashMap<>(4);
            List<Method> methodList = ReflectUtil.findMethod(mapperClass);
            if (!ObjectUtil.isNull(methodList)) {
                for (Method method : methodList) {
                    if (!method.isDefault()) {
                        String name = method.getName();
                        if (methodInfoMap.containsKey(name)) {
                            throw new DreamRunTimeException("方法名'" + name + "'重复定义，请检查" + mapperClass.getName());
                        }
                        MethodInfo methodInfo = createMethodInfo(configuration, mapperClass, method);
                        methodInfoMap.put(name, methodInfo);
                    }
                }
            }
            padMethodInfo(configuration, mapperClass, methodInfoMap);
            for (String name : methodInfoMap.keySet()) {
                MethodInfo methodInfo = methodInfoMap.get(name);
                if (ObjectUtil.isNull(methodInfo.getSql())) {
                    throw new DreamRunTimeException(methodInfo.getId() + "未绑定SQL");
                }
                this.methodInfoMap.put(methodInfo.getMethod(), methodInfo);
            }
            this.mapperTypeMap.put(mapperClass, getAllInterface(mapperClass));
            return true;
        } else {
            return false;
        }
    }

    protected Class<?> getActionType(Class<?> type) {
        Mapper mapperAnnotation = type.getAnnotation(Mapper.class);
        if (mapperAnnotation == null) {
            return null;
        }
        Class<?> value = mapperAnnotation.value();
        if (value == NullObject.class) {
            return null;
        }
        return value;
    }

    protected void padMethodInfo(Configuration configuration, Class type, Map<String, MethodInfo> methodInfoMap) {
        Class<?> actionType = getActionType(type);
        if (actionType != null) {
            List<Method> methodList = ReflectUtil.findMethod(actionType)
                    .stream()
                    .filter(method -> Modifier.isPublic(method.getModifiers()) && method.getParameters().length == 0)
                    .collect(Collectors.toList());
            if (!ObjectUtil.isNull(methodList)) {
                Object actionObject = ReflectUtil.create(actionType);
                for (Method method : methodList) {
                    String methodName = method.getName();
                    MethodInfo methodInfo = methodInfoMap.get(methodName);
                    if (methodInfo == null) {
                        throw new DreamRunTimeException("类" + type.getName() + "不存在方法" + methodName);
                    }
                    ActionProvider actionProvider = null;
                    try {
                        Class<?> returnType = method.getReturnType();
                        Object value = method.invoke(actionObject);
                        if (ActionProvider.class.isAssignableFrom(returnType)) {
                            actionProvider = (ActionProvider) value;
                        } else if (String.class == returnType) {
                            actionProvider = () -> (String) value;
                        }
                    } catch (Exception e) {
                        throw new DreamRunTimeException("调用方法" + actionType.getName() + "." + methodName + "失败，" + e.getMessage(), e);
                    }
                    if (actionProvider != null) {
                        Action[] initActionList = actionProvider.initActionList();
                        String sql = actionProvider.sql();
                        Action[] destroyActionList = actionProvider.destroyActionList();
                        Class<? extends Collection> rowType = actionProvider.rowType();
                        Class<?> colType = actionProvider.colType();
                        Boolean cache = actionProvider.cache();
                        Integer timeOut = actionProvider.timeOut();
                        StatementHandler statementHandler = actionProvider.statementHandler();
                        ResultSetHandler resultSetHandler = actionProvider.resultSetHandler();
                        if (initActionList != null) {
                            methodInfo.addInitAction(initActionList);
                        }
                        if (sql != null) {
                            methodInfo.setSql(sql);
                        }
                        if (destroyActionList != null) {
                            methodInfo.addDestroyAction(destroyActionList);
                        }
                        if (rowType != null) {
                            methodInfo.setRowType(rowType);
                        }
                        if (colType != null) {
                            methodInfo.setColType(colType);
                        }
                        if (cache != null) {
                            methodInfo.setCache(cache);
                        }
                        if (timeOut != null) {
                            methodInfo.setTimeOut(timeOut);
                        }
                        if (statementHandler != null) {
                            methodInfo.setStatementHandler(statementHandler);
                        }
                        if (resultSetHandler != null) {
                            methodInfo.setResultSetHandler(resultSetHandler);
                        }
                    }
                }
            }
        }
    }

    protected MethodInfo createMethodInfo(Configuration configuration, Class mapperClass, Method method) {
        Class<? extends Collection> rowType = getRowType(mapperClass, method);
        Class colType = getColType(mapperClass, method);
        boolean cache = isCache(mapperClass, method);
        MethodParam[] methodParamList = getMethodParamList(method);
        String sql = getSql(configuration, method);
        int timeOut = getTimeOut(mapperClass, method);
        return new MethodInfo()
                .setConfiguration(configuration)
                .setId(method.getDeclaringClass().getName() + "." + method.getName())
                .setRowType(rowType)
                .setColType(colType)
                .setCache(cache)
                .setMethodParamList(methodParamList)
                .setSql(sql)
                .setTimeOut(timeOut)
                .setMethod(method);
    }

    protected boolean isMapper(Class mapperClass) {
        return mapperClass.isAnnotationPresent(Mapper.class);
    }

    protected String getSql(Configuration configuration, Method method) {
        String sql = null;
        Sql sqlAnnotation = method.getDeclaredAnnotation(Sql.class);
        if (sqlAnnotation != null) {
            sql = sqlAnnotation.value();
        }
        return sql;
    }

    protected int getTimeOut(Class mapperClass, Method method) {
        Sql sqlAnnotation = method.getDeclaredAnnotation(Sql.class);
        if (sqlAnnotation != null) {
            return sqlAnnotation.timeOut();
        }
        return 0;
    }

    protected String getParamName(Parameter parameter) {
        Param paramAnnotation = parameter.getDeclaredAnnotation(Param.class);
        if (paramAnnotation != null) {
            return paramAnnotation.value();
        } else {
            return null;
        }
    }

    protected Class<? extends Collection> getRowType(Class mapperClass, Method method) {
        Class<? extends Collection> rowType = ReflectUtil.getRowType(mapperClass, method);
        if (rowType == null) {
            rowType = NonCollection.class;
        }
        return rowType;
    }

    protected Class getColType(Class mapperClass, Method method) {
        return ReflectUtil.getColType(mapperClass, method);
    }

    protected boolean isCache(Class mapperClass, Method method) {
        Sql sqlAnnotation = method.getDeclaredAnnotation(Sql.class);
        if (sqlAnnotation != null) {
            return sqlAnnotation.cache();
        }
        return true;
    }

    protected MethodParam[] getMethodParamList(Method method) {
        Parameter[] parameters = method.getParameters();
        MethodParam[] methodParamList = null;
        if (!ObjectUtil.isNull(parameters)) {
            methodParamList = new MethodParam[parameters.length];
            if (parameters.length > 1) {
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    String paramName = getParamName(parameter);
                    if (paramName == null) {
                        paramName = parameter.getName();
                    }
                    methodParamList[i] = new MethodParam(paramName, parameter.getType());
                }
            } else {
                String name = getParamName(parameters[0]);
                Class<?> type = parameters[0].getType();
                methodParamList[0] = new MethodParam(name, type);
            }
        }
        return methodParamList;
    }

    protected Class[] getAllInterface(Class type) {
        return ReflectUtil.find(type, classType -> {
            if (classType.isInterface()) {
                return Arrays.asList(classType);
            } else {
                return null;
            }
        }).toArray(new Class[0]);
    }

    @Override
    public <T> T getMapper(Class<T> type, MapperInvoke mapperInvoke) {
        Class[] typeList = mapperTypeMap.get(type);
        if (typeList == null) {
            return null;
        }
        return (T) Proxy.newProxyInstance(type.getClassLoader(), typeList, (proxy, method, args) -> {
            MethodInfo methodInfo = methodInfoMap.get(method);
            if (methodInfo != null) {
                Map<String, Object> argMap = getArg(methodInfo, args);
                return mapperInvoke.invoke(methodInfo, argMap, type);
            } else {
                return invoke(type, proxy, method, args);
            }
        });
    }

    protected Object invoke(Class type, Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isDefault()) {
            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                    .getDeclaredConstructor(Class.class);
            constructor.setAccessible(true);
            return constructor.newInstance(type)
                    .in(type)
                    .unreflectSpecial(method, type)
                    .bindTo(proxy)
                    .invokeWithArguments(args);
        } else {
            throw new DreamRunTimeException("接口方法不支持调用，方法名：" + type.getName() + "." + method.getName());
        }
    }

    protected Map<String, Object> getArg(MethodInfo methodInfo, Object[] args) {
        Map<String, Object> arg = null;
        if (!ObjectUtil.isNull(args)) {
            if (args.length == 1) {
                MethodParam methodParam = methodInfo.getMethodParamList()[0];
                String paramName = methodParam.getParamName();
                if (ObjectUtil.isNull(paramName)) {
                    arg = new ObjectMap(args[0]);
                } else {
                    Map<String, Object> paramMap = new HashMap<>(4);
                    paramMap.put(paramName, args[0]);
                    arg = paramMap;
                }
            } else {
                MethodParam[] methodParamList = methodInfo.getMethodParamList();
                Map<String, Object> paramMap = new HashMap<>(4);
                for (int i = 0; i < methodParamList.length; i++) {
                    paramMap.put(methodParamList[i].getParamName(), args[i]);
                }
                arg = paramMap;
            }
        }
        return arg;
    }

    @Override
    public Collection<Class> getMapperTypeList() {
        return mapperTypeMap.keySet();
    }
}
