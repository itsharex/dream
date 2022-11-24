package com.moxa.dream.template.mapper;

import com.moxa.dream.system.config.Page;
import com.moxa.dream.system.core.session.Session;

import java.util.List;

public class DefaultTemplateMapper implements TemplateMapper {
    private Session session;
    private SelectByIdMapper selectByIdSqlMapper;
    private SelectByIdsMapper selectByIdsSqlMapper;
    private SelectOneMapper selectOneSqlMapper;
    private SelectListMapper selectListMapper;
    private SelectPageMapper selectPageSqlMapper;
    private DeleteByIdMapper deleteByIdSqlMapper;
    private DeleteByIdsMapper deleteByIdsSqlMapper;
    private UpdateByIdMapper updateByIdSqlMapper;
    private BatchUpdateByIdMapper batchUpdateByIdMapper;
    private UpdateNonByIdMapper updateNonByIdSqlMapper;
    private BatchUpdateNonByIdMapper batchUpdateNonByIdMapper;
    private InsertMapper insertSqlMapper;
    private BatchInsertMapper batchInsertMapper;
    private ExistByIdMapper existByIdMapper;
    private ExistMapper existMapper;

    public DefaultTemplateMapper(Session session) {
        this.session = session;
        selectByIdSqlMapper = new SelectByIdMapper(session);
        selectByIdsSqlMapper = new SelectByIdsMapper(session);
        selectOneSqlMapper = new SelectOneMapper(session);
        selectListMapper = new SelectListMapper(session);
        selectPageSqlMapper = new SelectPageMapper(session);
        deleteByIdSqlMapper = new DeleteByIdMapper(session);
        deleteByIdsSqlMapper = new DeleteByIdsMapper(session);
        updateByIdSqlMapper = new UpdateByIdMapper(session);
        batchUpdateByIdMapper = new BatchUpdateByIdMapper(session);
        updateNonByIdSqlMapper = new UpdateNonByIdMapper(session);
        batchUpdateNonByIdMapper = new BatchUpdateNonByIdMapper(session);
        insertSqlMapper = new InsertMapper(session);
        batchInsertMapper = new BatchInsertMapper(session);
        existByIdMapper = new ExistByIdMapper(session);
        existMapper = new ExistMapper(session);
    }

    @Override
    public <T> T selectById(Class<T> type, Object id) {
        return (T) selectByIdSqlMapper.execute(type, id);
    }

    @Override
    public <T> List<T> selectByIds(Class<T> type, List<Object> idList) {
        return (List<T>) selectByIdsSqlMapper.execute(type, idList);
    }

    @Override
    public <T> T selectOne(Class<T> type, Object conditionObject) {
        return (T) selectOneSqlMapper.execute(type, conditionObject);
    }

    @Override
    public <T> List<T> selectList(Class<T> type, Object conditionObject) {
        return (List<T>) selectListMapper.execute(type, conditionObject);
    }

    @Override
    public <T> Page<T> selectPage(Class<T> type, Object conditionObject, Page page) {
        return (Page<T>) selectPageSqlMapper.execute(type, conditionObject, page);
    }

    @Override
    public int updateById(Object view) {
        return (int) updateByIdSqlMapper.execute(view.getClass(), view);
    }

    @Override
    public int updateNonById(Object view) {
        return (int) updateNonByIdSqlMapper.execute(view.getClass(), view);
    }

    @Override
    public int insert(Object view) {
        Class<?> type = view.getClass();
        return (int) insertSqlMapper.execute(type, view);
    }

    @Override
    public int deleteById(Class<?> type, Object id) {
        return (int) deleteByIdSqlMapper.execute(type, id);
    }

    @Override
    public int deleteByIds(Class<?> type, List<?> idList) {
        return (int) deleteByIdsSqlMapper.execute(type, idList);
    }

    @Override
    public boolean existById(Class<?> type, Object id) {
        Integer result = (Integer) existByIdMapper.execute(type, id);
        return result != null;
    }

    @Override
    public boolean exist(Class<?> type, Object conditionObject) {
        Integer result = (Integer) existMapper.execute(type, conditionObject);
        return result != null;
    }

    @Override
    public void batchInsert(List<?> viewList, int batchSize) {
        if (viewList == null || viewList.isEmpty()) {
            return;
        }
        batchInsertMapper.execute(viewList.get(0).getClass(), viewList, batchSize);
    }

    @Override
    public void batchUpdateById(List<?> viewList, int batchSize) {
        batchUpdateByIdMapper.execute(viewList.get(0).getClass(), viewList, batchSize);
    }

    @Override
    public void batchUpdateNonById(List<?> viewList, int batchSize) {
        batchUpdateNonByIdMapper.execute(viewList.get(0).getClass(), viewList, batchSize);
    }
}
