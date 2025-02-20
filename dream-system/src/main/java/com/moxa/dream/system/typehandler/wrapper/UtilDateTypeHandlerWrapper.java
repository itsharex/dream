package com.moxa.dream.system.typehandler.wrapper;

import com.moxa.dream.system.typehandler.handler.TypeHandler;
import com.moxa.dream.system.typehandler.handler.UtilDateTypeHandler;
import com.moxa.dream.system.typehandler.util.TypeUtil;

import java.sql.Types;
import java.util.Date;

public class UtilDateTypeHandlerWrapper implements TypeHandlerWrapper {
    @Override
    public TypeHandler<Date> getTypeHandler() {
        return new UtilDateTypeHandler();
    }

    @Override
    public Integer[] typeCode() {
        return new Integer[]{
                TypeUtil.hash(Object.class, Types.DATE),
                TypeUtil.hash(Date.class, Types.DATE),
                TypeUtil.hash(Date.class, Types.NULL),
                TypeUtil.hash(Date.class, Types.TIMESTAMP),
        };
    }

}
