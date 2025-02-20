package com.moxa.dream.system.config;

import com.moxa.dream.system.typehandler.handler.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MappedParam {
    private int jdbcType;
    private Object paramValue;
    private TypeHandler typeHandler;

    public int getJdbcType() {
        return jdbcType;
    }

    public MappedParam setJdbcType(int jdbcType) {
        this.jdbcType = jdbcType;
        return this;
    }

    public Object getParamValue() {
        return paramValue;
    }

    public MappedParam setParamValue(Object paramValue) {
        this.paramValue = paramValue;
        return this;
    }

    public TypeHandler getTypeHandler() {
        return typeHandler;
    }

    public MappedParam setTypeHandler(TypeHandler typeHandler) {
        this.typeHandler = typeHandler;
        return this;
    }

    public void setParam(PreparedStatement statement, int index) throws SQLException {
        typeHandler.setParam(statement, index, paramValue, jdbcType);
    }
}
