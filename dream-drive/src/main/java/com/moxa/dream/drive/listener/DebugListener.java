package com.moxa.dream.drive.listener;

import com.moxa.dream.system.config.MappedParam;
import com.moxa.dream.system.config.MappedStatement;
import com.moxa.dream.system.core.listener.Listener;
import com.moxa.dream.util.common.ObjectUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DebugListener implements Listener {
    final String START_DATE = "startDate";

    @Override
    public void before(MappedStatement mappedStatement) {
        mappedStatement.put(START_DATE, System.currentTimeMillis());
        List<MappedParam> mappedParamList = mappedStatement.getMappedParamList();
        List<Object> paramList;
        if (!ObjectUtil.isNull(mappedParamList)) {
            paramList = mappedParamList.stream().map(MappedParam::getParamValue).collect(Collectors.toList());
        } else {
            paramList = new ArrayList<>();
        }
        System.out.println("方法：" + mappedStatement.getId());
        System.out.println("语句：" + mappedStatement.getSql());
        System.out.println("参数：" + paramList);
    }

    @Override
    public void afterReturn(Object result, MappedStatement mappedStatement) {
        after(mappedStatement);
    }

    @Override
    public void exception(SQLException e, MappedStatement mappedStatement) {
        System.err.println("异常：" + e.getMessage());
        after(mappedStatement);
    }

    public void after(MappedStatement mappedStatement) {
        System.out.println("用时：" + (System.currentTimeMillis() - (long) mappedStatement.get(START_DATE)) + "ms");
    }
}
