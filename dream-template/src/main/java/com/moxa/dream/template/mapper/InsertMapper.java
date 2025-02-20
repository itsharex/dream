package com.moxa.dream.template.mapper;

import com.moxa.dream.antlr.invoker.Invoker;
import com.moxa.dream.antlr.util.AntlrUtil;
import com.moxa.dream.system.antlr.invoker.MarkInvoker;
import com.moxa.dream.system.config.Command;
import com.moxa.dream.system.config.Configuration;
import com.moxa.dream.system.config.MappedStatement;
import com.moxa.dream.system.config.MethodInfo;
import com.moxa.dream.system.core.action.Action;
import com.moxa.dream.system.core.session.Session;
import com.moxa.dream.system.table.ColumnInfo;
import com.moxa.dream.system.table.TableInfo;
import com.moxa.dream.system.typehandler.TypeHandlerNotFoundException;
import com.moxa.dream.system.typehandler.factory.TypeHandlerFactory;
import com.moxa.dream.system.typehandler.handler.TypeHandler;
import com.moxa.dream.template.annotation.WrapType;
import com.moxa.dream.template.sequence.Sequence;
import com.moxa.dream.util.common.NonCollection;
import com.moxa.dream.util.common.ObjectUtil;
import com.moxa.dream.util.exception.DreamRunTimeException;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class InsertMapper extends WrapMapper {
    private final int CODE = 1;
    protected Sequence sequence;

    public InsertMapper(Session session, Sequence sequence) {
        super(session);
        this.sequence = sequence;
    }

    @Override
    protected MethodInfo getWrapMethodInfo(Configuration configuration, TableInfo tableInfo, List<Field> fieldList, Object arg) {
        this.sequence.init(tableInfo);
        String table = tableInfo.getTable();
        List<String> columnList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();
        if (!ObjectUtil.isNull(fieldList)) {
            for (Field field : fieldList) {
                String name = field.getName();
                ColumnInfo columnInfo = tableInfo.getColumnInfo(name);
                if (columnInfo != null) {
                    String column = columnInfo.getColumn();
                    String invokerSQL = AntlrUtil.invokerSQL(MarkInvoker.FUNCTION, Invoker.DEFAULT_NAMESPACE, DREAM_TEMPLATE_PARAM + "." + columnInfo.getName());
                    if (columnInfo.isPrimary()) {
                        columnList.add(0, column);
                        valueList.add(0, invokerSQL);
                    } else {
                        columnList.add(column);
                        valueList.add(invokerSQL);
                    }
                }
            }
        }
        String sql = "insert into " + table + "(" + String.join(",", columnList) + ")values(" + String.join(",", valueList) + ")";
        return getMethodInfo(configuration, tableInfo, sql);
    }

    protected MethodInfo getMethodInfo(Configuration configuration, TableInfo tableInfo, String sql) {
        List<Action> initActionList = new ArrayList<>();
        List<Action> destroyActionList = new ArrayList<>();
        String[] columnNames = sequence.columnNames(tableInfo);
        TypeHandler[] typeHandlers = null;
        SequenceAction sequenceAction = new SequenceAction(tableInfo, sequence);
        if (sequence.before()) {
            initActionList.add(sequenceAction);
        } else {
            destroyActionList.add(sequenceAction);
        }
        if (columnNames != null && columnNames.length > 0) {
            typeHandlers = new TypeHandler[columnNames.length];
            TypeHandlerFactory typeHandlerFactory = configuration.getTypeHandlerFactory();
            for (int i = 0; i < columnNames.length; i++) {
                String column = columnNames[i];
                String fieldName = tableInfo.getFieldName(column);
                if (fieldName == null) {
                    throw new DreamRunTimeException("表" + tableInfo.getTable() + "不存在字段" + column);
                }
                Class<?> type = tableInfo.getColumnInfo(fieldName).getField().getType();
                TypeHandler typeHandler;
                try {
                    typeHandler = typeHandlerFactory.getTypeHandler(type, Types.NULL);
                } catch (TypeHandlerNotFoundException e) {
                    throw new DreamRunTimeException(fieldName + "获取类型转换器失败，" + e.getMessage(), e);
                }
                typeHandlers[i] = typeHandler;
            }
        }
        return new MethodInfo()
                .setConfiguration(configuration)
                .setRowType(NonCollection.class)
                .setColType(Integer.class)
                .setColumnNames(columnNames)
                .setColumnTypeHandlers(typeHandlers)
                .addInitAction(initActionList.toArray(new Action[0]))
                .addDestroyAction(destroyActionList.toArray(new Action[0]))
                .setSql(sql);
    }

    @Override
    protected boolean accept(WrapType wrapType) {
        return (CODE & wrapType.getCode()) > 0;
    }

    @Override
    protected Command getCommand() {
        return Command.INSERT;
    }

    protected class SequenceAction implements Action {
        private TableInfo tableInfo;
        private Sequence sequence;

        public SequenceAction(TableInfo tableInfo, Sequence sequence) {
            this.tableInfo = tableInfo;
            this.sequence = sequence;
        }

        @Override
        public void doAction(Session session, MappedStatement mappedStatement, Object arg) {
            sequence.sequence(tableInfo, mappedStatement, arg);
        }
    }
}
