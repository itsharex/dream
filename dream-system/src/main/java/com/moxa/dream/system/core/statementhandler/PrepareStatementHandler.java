package com.moxa.dream.system.core.statementhandler;

import com.moxa.dream.system.config.BatchMappedStatement;
import com.moxa.dream.system.config.MappedParam;
import com.moxa.dream.system.config.MappedStatement;
import com.moxa.dream.util.common.ObjectUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PrepareStatementHandler implements StatementHandler<PreparedStatement> {

    @Override
    public PreparedStatement prepare(Connection connection, MappedStatement mappedStatement) throws SQLException {
        String[] columnNames = mappedStatement.getColumnNames();
        if (columnNames != null && columnNames.length > 0) {
            return connection.prepareStatement(mappedStatement.getSql(), columnNames);
        } else {
            return connection.prepareStatement(mappedStatement.getSql());
        }
    }

    protected void doParameter(PreparedStatement statement, MappedStatement mappedStatement) throws SQLException {
        List<MappedParam> mappedParamList = mappedStatement.getMappedParamList();
        if (!ObjectUtil.isNull(mappedParamList)) {
            for (int i = 0; i < mappedParamList.size(); i++) {
                MappedParam mappedParam = mappedParamList.get(i);
                mappedParam.setParam(statement, i + 1);
            }
        }
    }

    protected void doTimeOut(PreparedStatement statement, MappedStatement mappedStatement) throws SQLException {
        int timeOut = mappedStatement.getTimeOut();
        if (timeOut != 0) {
            statement.setQueryTimeout(timeOut);
        }
    }

    @Override
    public ResultSet query(PreparedStatement statement, MappedStatement mappedStatement) throws SQLException {
        doParameter(statement, mappedStatement);
        doTimeOut(statement, mappedStatement);
        ResultSet resultSet = statement.executeQuery();
        return resultSet;
    }

    @Override
    public Object update(PreparedStatement statement, MappedStatement mappedStatement) throws SQLException {
        doParameter(statement, mappedStatement);
        return statement.executeUpdate();
    }

    @Override
    public Object batch(PreparedStatement statement, BatchMappedStatement batchMappedStatement) throws SQLException {
        for (MappedStatement mappedStatement : batchMappedStatement.getMappedStatementList()) {
            doParameter(statement, mappedStatement);
            statement.addBatch();
        }
        int[] result = statement.executeBatch();
        statement.clearBatch();
        return result;
    }
}
