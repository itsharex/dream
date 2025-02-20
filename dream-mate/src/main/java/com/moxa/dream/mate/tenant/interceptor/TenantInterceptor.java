package com.moxa.dream.mate.tenant.interceptor;

import com.moxa.dream.mate.tenant.inject.TenantHandler;
import com.moxa.dream.system.config.MethodInfo;
import com.moxa.dream.system.dialect.DialectFactory;
import com.moxa.dream.system.plugin.interceptor.Interceptor;
import com.moxa.dream.system.plugin.invocation.Invocation;
import com.moxa.dream.util.exception.DreamRunTimeException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TenantInterceptor implements Interceptor {
    private TenantHandler tenantHandler;

    public TenantInterceptor(TenantHandler tenantHandler) {
        this.tenantHandler = tenantHandler;
    }

    @Override
    public Object interceptor(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        Object arg = args[1];
        if (arg == null) {
            arg = new HashMap<>(4);
            args[1] = arg;
        }
        if (!(arg instanceof Map)) {
            throw new DreamRunTimeException("多租户模式，参数类型必须是Map");
        }
        Map paramMap = (Map) arg;
        paramMap.put(tenantHandler.getTenantColumn(), tenantHandler.getTenantObject());
        return invocation.proceed();
    }

    @Override
    public Set<Method> methods() throws Exception {
        Set<Method> methods = new HashSet<>();
        Method method = DialectFactory.class.getMethod("compile", MethodInfo.class, Object.class);
        methods.add(method);
        return methods;
    }
}
