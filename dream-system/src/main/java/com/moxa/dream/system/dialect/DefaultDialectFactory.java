package com.moxa.dream.system.dialect;

import com.moxa.dream.antlr.sql.ToSQL;
import com.moxa.dream.system.config.MappedStatement;
import com.moxa.dream.system.config.MethodInfo;

public class DefaultDialectFactory extends AbstractRouteDialectFactory {
    private AntlrDialectFactory antlrDialectFactory = new AntlrDialectFactory();
    private UnAntlrDialectFactory unAntlrDialectFactory = new UnAntlrDialectFactory();


    @Override
    protected MappedStatement compileUnAntlr(MethodInfo methodInfo, Object arg) throws Exception {
        return unAntlrDialectFactory.compile(methodInfo, arg);
    }

    @Override
    protected MappedStatement compileAntlr(MethodInfo methodInfo, Object arg) throws Exception {
        return antlrDialectFactory.compile(methodInfo, arg);
    }

    public void setToSQL(ToSQL toSQL) {
        antlrDialectFactory.setToSQL(toSQL);
    }
}
