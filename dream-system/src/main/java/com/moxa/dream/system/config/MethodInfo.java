package com.moxa.dream.system.config;

import com.moxa.dream.antlr.smt.PackageStatement;
import com.moxa.dream.system.cache.CacheKey;
import com.moxa.dream.system.core.action.Action;
import com.moxa.dream.system.core.resultsethandler.ResultSetHandler;
import com.moxa.dream.system.core.statementhandler.StatementHandler;
import com.moxa.dream.system.typehandler.handler.TypeHandler;
import com.moxa.dream.util.common.ObjectUtil;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MethodInfo {
    protected final Map<Class, Object> builtMap = new HashMap<>(8);
    protected Configuration configuration;
    protected String id;
    protected Class<? extends Collection> rowType;
    protected Class colType;
    protected String[] columnNames;
    protected TypeHandler[] columnTypeHandlers;
    protected boolean cache = true;
    protected Compile compile = Compile.ANTLR_COMPILE;
    protected String sql;
    protected int timeOut;
    protected PackageStatement statement;
    protected CacheKey methodKey;
    protected Method method;
    protected Action[] initActionList;
    protected Action[] destroyActionList;
    protected MethodParam[] methodParamList;
    protected StatementHandler statementHandler;
    protected ResultSetHandler resultSetHandler;

    public Configuration getConfiguration() {
        return configuration;
    }

    public MethodInfo setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        return this;
    }

    public String getId() {
        return id;
    }

    public MethodInfo setId(String id) {
        this.id = id;
        return this;
    }

    public Class<? extends Collection> getRowType() {
        return rowType;
    }

    public MethodInfo setRowType(Class<? extends Collection> rowType) {
        this.rowType = rowType;
        return this;
    }

    public Class getColType() {
        return colType;
    }

    public MethodInfo setColType(Class colType) {
        this.colType = colType;
        return this;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public MethodInfo setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
        return this;
    }

    public TypeHandler[] getColumnTypeHandlers() {
        return columnTypeHandlers;
    }

    public MethodInfo setColumnTypeHandlers(TypeHandler[] columnTypeHandlers) {
        this.columnTypeHandlers = columnTypeHandlers;
        return this;
    }

    public boolean isCache() {
        return cache;
    }

    public MethodInfo setCache(boolean cache) {
        this.cache = cache;
        return this;
    }

    public Compile getCompile() {
        return compile;
    }

    public MethodInfo setCompile(Compile compile) {
        this.compile = compile;
        return this;
    }

    public String getSql() {
        return sql;
    }

    public MethodInfo setSql(String sql) {
        this.sql = sql;
        return this;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public MethodInfo setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public PackageStatement getStatement() {
        return statement;
    }

    public MethodInfo setStatement(PackageStatement statement) {
        this.statement = statement;
        return this;
    }

    public CacheKey getMethodKey() {
        return methodKey.clone();
    }

    public MethodInfo setMethodKey(CacheKey methodKey) {
        this.methodKey = methodKey;
        return this;
    }

    public Method getMethod() {
        return method;
    }

    public MethodInfo setMethod(Method method) {
        this.method = method;
        return this;
    }

    public Action[] getInitActionList() {
        return initActionList;
    }

    public MethodInfo addInitAction(Action... initActions) {
        this.initActionList = ObjectUtil.merge(this.initActionList, initActions);
        return this;
    }

    public Action[] getDestroyActionList() {
        return destroyActionList;
    }

    public MethodInfo addDestroyAction(Action... destroyActionList) {
        this.destroyActionList = ObjectUtil.merge(this.destroyActionList, destroyActionList);
        return this;
    }

    public MethodParam[] getMethodParamList() {
        return methodParamList;
    }

    public MethodInfo setMethodParamList(MethodParam[] methodParamList) {
        this.methodParamList = methodParamList;
        return this;
    }

    public StatementHandler getStatementHandler() {
        return statementHandler;
    }

    public MethodInfo setStatementHandler(StatementHandler statementHandler) {
        this.statementHandler = statementHandler;
        return this;
    }

    public ResultSetHandler getResultSetHandler() {
        return resultSetHandler;
    }

    public MethodInfo setResultSetHandler(ResultSetHandler resultSetHandler) {
        this.resultSetHandler = resultSetHandler;
        return this;
    }

    public <T> MethodInfo set(Class<T> type, T value) {
        builtMap.put(type, value);
        return this;
    }

    public <T> T get(Class<T> type) {
        return (T) builtMap.get(type);
    }
}
