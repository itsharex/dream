package com.moxa.dream.system.typehandler.wrapper;

import com.moxa.dream.system.typehandler.handler.DoubleTypeHandler;
import com.moxa.dream.system.typehandler.handler.TypeHandler;
import com.moxa.dream.system.typehandler.util.TypeUtil;

import java.sql.Types;

public class DoubleTypeHandlerWrapper implements TypeHandlerWrapper {
    @Override
    public TypeHandler<Double> getTypeHandler() {
        return new DoubleTypeHandler();
    }

    @Override
    public Integer[] typeCode() {
        return new Integer[]{
                TypeUtil.hash(Object.class, Types.DOUBLE),
                TypeUtil.hash(double.class, Types.DOUBLE),
                TypeUtil.hash(Double.class, Types.DOUBLE),
                TypeUtil.hash(double.class, Types.NUMERIC),
                TypeUtil.hash(Double.class, Types.NUMERIC),
                TypeUtil.hash(double.class, Types.NULL),
                TypeUtil.hash(Double.class, Types.NULL),
        };
    }

}
