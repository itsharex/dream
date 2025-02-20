package com.moxa.dream.template.mapper;

import com.moxa.dream.system.core.session.Session;
import com.moxa.dream.system.table.factory.TableFactory;
import com.moxa.dream.util.common.NonCollection;

import java.util.Collection;
import java.util.Set;

public class ExistMapper extends SelectListMapper {
    public ExistMapper(Session session) {
        super(session);
    }

    @Override
    protected Class<? extends Collection> getRowType() {
        return NonCollection.class;
    }

    @Override
    protected Class getColType(Class type) {
        return Integer.class;
    }

    @Override
    protected String getSelectColumn(Class<?> type) {
        return "1";
    }

    @Override
    protected String getOrderSql(Class type, Set<String> tableSet, TableFactory tableFactory) {
        return " limit 1";
    }
}
