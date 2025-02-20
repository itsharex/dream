package com.moxa.dream.mate.tenant.invoker;

import com.moxa.dream.antlr.config.Assist;
import com.moxa.dream.antlr.exception.AntlrException;
import com.moxa.dream.antlr.handler.Handler;
import com.moxa.dream.antlr.invoker.AbstractInvoker;
import com.moxa.dream.antlr.invoker.Invoker;
import com.moxa.dream.antlr.smt.InvokerStatement;
import com.moxa.dream.antlr.sql.ToSQL;
import com.moxa.dream.mate.tenant.handler.TenantDeleteHandler;
import com.moxa.dream.mate.tenant.handler.TenantInsertHandler;
import com.moxa.dream.mate.tenant.handler.TenantQueryHandler;
import com.moxa.dream.mate.tenant.handler.TenantUpdateHandler;
import com.moxa.dream.mate.tenant.inject.TenantHandler;
import com.moxa.dream.mate.tenant.inject.TenantInject;
import com.moxa.dream.system.config.Configuration;
import com.moxa.dream.system.config.MethodInfo;
import com.moxa.dream.system.inject.factory.InjectFactory;
import com.moxa.dream.system.table.TableInfo;
import com.moxa.dream.system.table.factory.TableFactory;

import java.util.List;

public class TenantInvoker extends AbstractInvoker {
    public static final String FUNCTION = "dream_mate_tenant";
    private TableFactory tableFactory;
    private MethodInfo methodInfo;
    private TenantHandler tenantHandler;

    @Override
    public void init(Assist assist) {
        methodInfo = assist.getCustom(MethodInfo.class);
        Configuration configuration = methodInfo.getConfiguration();
        tableFactory = configuration.getTableFactory();
        InjectFactory injectFactory = configuration.getInjectFactory();
        TenantInject tenantInject = injectFactory.getInject(TenantInject.class);
        tenantHandler = tenantInject.getTenantHandler();

    }

    @Override
    public Invoker newInstance() {
        return new TenantInvoker();
    }

    @Override
    public String function() {
        return FUNCTION;
    }

    @Override
    protected String invoker(InvokerStatement invokerStatement, Assist assist, ToSQL toSQL, List<Invoker> invokerList) throws AntlrException {
        String sql = toSQL.toStr(invokerStatement.getParamStatement(), assist, invokerList);
        invokerStatement.replaceWith(invokerStatement.getParamStatement());
        return sql;
    }

    @Override
    protected Handler[] handler() {
        return new Handler[]{new TenantQueryHandler(this), new TenantInsertHandler(this), new TenantUpdateHandler(this), new TenantDeleteHandler(this)};
    }

    public boolean isTenant(String table) {
        TableInfo tableInfo = tableFactory.getTableInfo(table);
        if (tableInfo != null) {
            return tenantHandler.isTenant(methodInfo, tableInfo);
        }
        return false;
    }

    public String getTenantColumn() {
        return tenantHandler.getTenantColumn();
    }
}
