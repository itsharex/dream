package com.moxa.dream.antlr.smt;

public class DropTableStatement extends Statement {
    private Statement table;

    public Statement getTable() {
        return table;
    }

    public void setTable(Statement table) {
        this.table = table;
        if (table != null) {
            table.parentStatement = this;
        }
    }

    @Override
    protected Boolean isNeedInnerCache() {
        return isNeedInnerCache(table);
    }
}
