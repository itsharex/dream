package com.moxa.dream.mate.block.invoker;

import com.moxa.dream.antlr.config.Assist;
import com.moxa.dream.antlr.exception.AntlrException;
import com.moxa.dream.antlr.handler.Handler;
import com.moxa.dream.antlr.invoker.AbstractInvoker;
import com.moxa.dream.antlr.invoker.Invoker;
import com.moxa.dream.antlr.smt.InvokerStatement;
import com.moxa.dream.antlr.smt.ListColumnStatement;
import com.moxa.dream.antlr.smt.Statement;
import com.moxa.dream.antlr.sql.ToSQL;
import com.moxa.dream.mate.block.handler.BlockHandler;
import com.moxa.dream.util.common.LowHashSet;
import com.moxa.dream.util.exception.DreamRunTimeException;
import com.moxa.dream.util.resource.ResourceUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class BlockInvoker extends AbstractInvoker {
    public static final String FUNCTION = "dream_mate_block";
    private Set<String> filterSet;

    public BlockInvoker(String resource) {
        InputStream inputStream = ResourceUtil.getResourceAsStream(resource);
        Set<String> filterSet = new LowHashSet();
        if (inputStream != null) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            try {
                while ((length = inputStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                String keyword = result.toString();
                StringTokenizer tokenizer = new StringTokenizer(keyword);
                while (tokenizer.hasMoreTokens()) {
                    filterSet.add(tokenizer.nextToken());
                }
            } catch (Exception e) {
                throw new DreamRunTimeException("读取" + resource + "失败", e);
            }
        } else {
            throw new DreamRunTimeException(resource + "不存在");
        }
        this.filterSet = filterSet;
    }

    public BlockInvoker(Set<String> filterSet) {
        this.filterSet = filterSet;
    }

    @Override
    protected String invoker(InvokerStatement invokerStatement, Assist assist, ToSQL toSQL, List<Invoker> invokerList) throws AntlrException {
        Statement statement = ((ListColumnStatement) invokerStatement.getParamStatement()).getColumnList()[0];
        String sql = toSQL.toStr(invokerStatement.getParamStatement(), assist, invokerList);
        invokerStatement.replaceWith(statement);
        return sql;
    }

    @Override
    protected Handler[] handler() {
        return new Handler[]{new BlockHandler(this)};
    }

    public boolean filter(String column) {
        return filterSet.contains(column);
    }

    @Override
    public Invoker newInstance() {
        return this;
    }

    @Override
    public String function() {
        return FUNCTION;
    }

    public Set<String> getFilterSet() {
        return filterSet;
    }
}

