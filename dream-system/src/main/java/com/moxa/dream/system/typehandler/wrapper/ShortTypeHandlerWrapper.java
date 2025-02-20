package com.moxa.dream.system.typehandler.wrapper;

import com.moxa.dream.system.typehandler.handler.ShortTypeHandler;
import com.moxa.dream.system.typehandler.handler.TypeHandler;
import com.moxa.dream.system.typehandler.util.TypeUtil;

import java.sql.Types;

public class ShortTypeHandlerWrapper implements TypeHandlerWrapper {
    @Override
    public TypeHandler<Short> getTypeHandler() {
        return new ShortTypeHandler();
    }

    @Override
    public Integer[] typeCode() {
        return new Integer[]{
                TypeUtil.hash(Object.class, Types.SMALLINT),
                TypeUtil.hash(short.class, Types.SMALLINT),
                TypeUtil.hash(Short.class, Types.SMALLINT),
                TypeUtil.hash(short.class, Types.NULL),
                TypeUtil.hash(Short.class, Types.NULL),
        };
    }

}
