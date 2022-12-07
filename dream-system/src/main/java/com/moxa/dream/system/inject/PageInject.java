package com.moxa.dream.system.inject;

import com.moxa.dream.antlr.smt.InvokerStatement;
import com.moxa.dream.antlr.smt.PackageStatement;
import com.moxa.dream.antlr.smt.SymbolStatement;
import com.moxa.dream.system.annotation.PageQuery;
import com.moxa.dream.system.antlr.factory.SystemInvokerFactory;
import com.moxa.dream.system.config.MethodInfo;
import com.moxa.dream.system.util.InvokerUtil;
import com.moxa.dream.util.common.ObjectUtil;

public class PageInject implements Inject {
    private final String START_ROW = "startRow";
    private final String PAGE_SIZE = "pageSize";

    @Override
    public void inject(MethodInfo methodInfo) {
        PageQuery pageQuery = methodInfo.get(PageQuery.class);
        if (pageQuery != null) {
            PackageStatement statement = methodInfo.getStatement();
            String value = pageQuery.value();
            String prefix = ObjectUtil.isNull(value) ? "" : (value + ".");
            String startRow = prefix + START_ROW;
            String pageSize = prefix + PAGE_SIZE;
            InvokerStatement pageStatement;
            if (pageQuery.offset()) {
                pageStatement = InvokerUtil.wrapperInvoker(SystemInvokerFactory.NAMESPACE,
                        SystemInvokerFactory.OFFSET, ",",
                        statement.getStatement(),
                        InvokerUtil.wrapperInvoker(SystemInvokerFactory.NAMESPACE,
                                SystemInvokerFactory.$, ",",
                                new SymbolStatement.LetterStatement(pageSize)),
                        InvokerUtil.wrapperInvoker(SystemInvokerFactory.NAMESPACE,
                                SystemInvokerFactory.$, ",",
                                new SymbolStatement.LetterStatement(startRow)));

            } else {
                pageStatement = InvokerUtil.wrapperInvoker(SystemInvokerFactory.NAMESPACE,
                        SystemInvokerFactory.LIMIT, ",",
                        statement.getStatement(),
                        InvokerUtil.wrapperInvoker(SystemInvokerFactory.NAMESPACE,
                                SystemInvokerFactory.$, ",",
                                new SymbolStatement.LetterStatement(startRow)),
                        InvokerUtil.wrapperInvoker(SystemInvokerFactory.NAMESPACE,
                                SystemInvokerFactory.$, ",",
                                new SymbolStatement.LetterStatement(pageSize)));
            }
            statement.setStatement(pageStatement);
        }
    }
}
