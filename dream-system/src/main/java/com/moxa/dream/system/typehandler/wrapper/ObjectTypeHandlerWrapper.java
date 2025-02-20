package com.moxa.dream.system.typehandler.wrapper;

import com.moxa.dream.system.typehandler.handler.ObjectTypeHandler;
import com.moxa.dream.system.typehandler.handler.TypeHandler;
import com.moxa.dream.system.typehandler.util.TypeUtil;

import java.sql.Types;

public class ObjectTypeHandlerWrapper implements TypeHandlerWrapper {
    @Override
    public TypeHandler<Object> getTypeHandler() {
        return new ObjectTypeHandler();
    }

    @Override
    public Integer[] typeCode() {
        return new Integer[]{
                TypeUtil.hash(Object.class, Types.NULL),
        };
    }

}
