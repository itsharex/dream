package com.moxa.dream.module.core.statementhandler;

import com.moxa.dream.antlr.bind.Command;
import com.moxa.dream.module.cache.CacheKey;
import com.moxa.dream.module.mapped.MappedStatement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BatchStatementHandler implements StatementHandler {
    private StatementHandler statementHandler;
    private List<BatchResult> batchResultList = new ArrayList<>();
    private BatchResult currentBatchResult;

    public BatchStatementHandler(StatementHandler statementHandler) {
        this.statementHandler = statementHandler;
    }


    @Override
    public void prepare(Connection connection, MappedStatement mappedStatement, int autoGeneratedKeys) throws SQLException {
        Command command = mappedStatement.getCommand();
        switch (command) {
            case QUERY:
                statementHandler.prepare(connection, mappedStatement, autoGeneratedKeys);
                currentBatchResult = null;
                break;
            default:
                if (currentBatchResult == null || !mappedStatement.getSqlKey().equals(currentBatchResult.sqlKey)) {
                    statementHandler.prepare(connection, mappedStatement, autoGeneratedKeys);
                    Statement statement = statementHandler.getStatement();
                    currentBatchResult = new BatchResult(mappedStatement.getSqlKey(), statement);
                    batchResultList.add(currentBatchResult);
                }
                break;
        }
    }

    @Override
    public ResultSet executeQuery(MappedStatement mappedStatement) throws SQLException {
        return statementHandler.executeQuery(mappedStatement);
    }

    @Override
    public int executeUpdate(MappedStatement mappedStatement) throws SQLException {
        statementHandler.addBatch(mappedStatement);
        return 0;
    }

    @Override
    public void addBatch(MappedStatement mappedStatement) throws SQLException {
        statementHandler.addBatch(mappedStatement);
    }

    @Override
    public void flushStatement(boolean rollback) {
        try {
            if (!rollback) {
                for (BatchResult batchResult : batchResultList) {
                    try {
                        batchResult.statement.executeBatch();
                    } finally {
                        batchResult.statement.close();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            currentBatchResult = null;
            batchResultList.clear();
        }
    }

    @Override
    public Statement getStatement() {
        return statementHandler.getStatement();
    }

    @Override
    public void close() {

    }

    public static class BatchResult {
        private CacheKey sqlKey;
        private Statement statement;

        public BatchResult(CacheKey sqlKey, Statement statement) {
            this.sqlKey = sqlKey;
            this.statement = statement;
        }
    }
}
