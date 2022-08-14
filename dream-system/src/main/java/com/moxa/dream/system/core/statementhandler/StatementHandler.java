package com.moxa.dream.system.core.statementhandler;

import com.moxa.dream.system.mapped.MappedStatement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface StatementHandler {

    ResultSet executeQuery(MappedStatement mappedStatement) throws SQLException;

    int executeUpdate(MappedStatement mappedStatement) throws SQLException;

    void addBatch(MappedStatement mappedStatement) throws SQLException;

    void prepare(Connection connection, MappedStatement mappedStatement) throws SQLException;

    void flushStatement(boolean rollback);

    Statement getStatement();

    void close();


}
