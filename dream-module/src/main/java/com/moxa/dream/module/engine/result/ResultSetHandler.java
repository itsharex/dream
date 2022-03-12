package com.moxa.dream.module.engine.result;

import com.moxa.dream.module.hold.mapped.MappedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetHandler {
    Object result(ResultSet resultSet, MappedStatement mappedStatement) throws SQLException;
}
