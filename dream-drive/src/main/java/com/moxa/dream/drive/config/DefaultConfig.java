package com.moxa.dream.drive.config;

import com.moxa.dream.system.cache.CacheFactory;
import com.moxa.dream.system.compile.CompileFactory;
import com.moxa.dream.system.config.Configuration;
import com.moxa.dream.system.core.listener.factory.ListenerFactory;
import com.moxa.dream.system.datasource.DataSourceFactory;
import com.moxa.dream.system.dialect.DialectFactory;
import com.moxa.dream.system.inject.factory.InjectFactory;
import com.moxa.dream.system.mapper.MapperFactory;
import com.moxa.dream.system.plugin.factory.PluginFactory;
import com.moxa.dream.system.table.factory.TableFactory;
import com.moxa.dream.system.transaction.factory.TransactionFactory;
import com.moxa.dream.system.typehandler.factory.TypeHandlerFactory;
import com.moxa.dream.util.common.ObjectUtil;
import com.moxa.dream.util.exception.DreamRunTimeException;
import com.moxa.dream.util.resource.ResourceUtil;

import java.util.List;


public class DefaultConfig {
    private Configuration configuration = new Configuration();
    private List<String> mapperPackages;
    private List<String> tablePackages;


    public TableFactory getTableFactory() {
        return configuration.getTableFactory();
    }

    public DefaultConfig setTableFactory(TableFactory tableFactory) {
        configuration.setTableFactory(tableFactory);
        return this;
    }

    public MapperFactory getMapperFactory() {
        return configuration.getMapperFactory();
    }

    public DefaultConfig setMapperFactory(MapperFactory mapperFactory) {
        configuration.setMapperFactory(mapperFactory);
        return this;
    }

    public CacheFactory getCacheFactory() {
        return configuration.getCacheFactory();
    }

    public DefaultConfig setCacheFactory(CacheFactory cacheFactory) {
        configuration.setCacheFactory(cacheFactory);
        return this;
    }


    public TypeHandlerFactory getTypeHandlerFactory() {
        return configuration.getTypeHandlerFactory();
    }

    public DefaultConfig setTypeHandlerFactory(TypeHandlerFactory typeHandlerFactory) {
        configuration.setTypeHandlerFactory(typeHandlerFactory);
        return this;
    }

    public CompileFactory getCompileFactory() {
        return configuration.getCompileFactory();
    }

    public DefaultConfig setCompileFactory(CompileFactory compileFactory) {
        configuration.setCompileFactory(compileFactory);
        return this;
    }

    public InjectFactory getInjectFactory() {
        return configuration.getInjectFactory();
    }

    public DefaultConfig setInjectFactory(InjectFactory injectFactory) {
        configuration.setInjectFactory(injectFactory);
        return this;
    }

    public DialectFactory getDialectFactory() {
        return configuration.getDialectFactory();
    }

    public DefaultConfig setDialectFactory(DialectFactory dialectFactory) {
        configuration.setDialectFactory(dialectFactory);
        return this;
    }

    public TransactionFactory getTransactionFactory() {
        return configuration.getTransactionFactory();
    }

    public DefaultConfig setTransactionFactory(TransactionFactory transactionFactory) {
        configuration.setTransactionFactory(transactionFactory);
        return this;
    }

    public DataSourceFactory getDataSourceFactory() {
        return configuration.getDataSourceFactory();
    }

    public DefaultConfig setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        configuration.setDataSourceFactory(dataSourceFactory);
        return this;
    }

    public PluginFactory getPluginFactory() {
        return configuration.getPluginFactory();
    }

    public DefaultConfig setPluginFactory(PluginFactory pluginFactory) {
        configuration.setPluginFactory(pluginFactory);
        return this;
    }

    public ListenerFactory getListenerFactory() {
        return configuration.getListenerFactory();
    }

    public DefaultConfig setListenerFactory(ListenerFactory listenerFactory) {
        configuration.setListenerFactory(listenerFactory);
        return this;
    }

    public List<String> getMapperPackages() {
        return mapperPackages;
    }

    public DefaultConfig setMapperPackages(List<String> mapperPackages) {
        this.mapperPackages = mapperPackages;
        return this;
    }

    public List<String> getTablePackages() {
        return tablePackages;
    }

    public DefaultConfig setTablePackages(List<String> tablePackages) {
        this.tablePackages = tablePackages;
        return this;
    }

    public Configuration toConfiguration() {
        return build();
    }

    private void tableMapping(String type) {
        if (!ObjectUtil.isNull(type)) {
            type = type.replace(".", "/");
            List<Class> resourceAsClass = ResourceUtil.getResourceAsClass(type);
            if (!ObjectUtil.isNull(resourceAsClass)) {
                TableFactory tableFactory = configuration.getTableFactory();
                if (tableFactory == null) {
                    throw new DreamRunTimeException("TableFactory未在Configuration注册");
                }
                for (Class classType : resourceAsClass) {
                    tableFactory.addTableInfo(classType);
                }
            }
        }
    }

    private void mapperMapping(String type) {
        if (!ObjectUtil.isNull(type)) {
            String resourcePath = type.replace(".", "/");
            List<Class> resourceAsClass = ResourceUtil.getResourceAsClass(resourcePath);
            if (!ObjectUtil.isNull(resourceAsClass)) {
                for (Class classType : resourceAsClass) {
                    configuration.addMapper(classType);
                }
            }
        }
    }

    private Configuration build() {
        if (!ObjectUtil.isNull(mapperPackages)) {
            for (String mapperPackage : mapperPackages) {
                mapperMapping(mapperPackage);
            }
        }
        if (!ObjectUtil.isNull(tablePackages)) {
            for (String tablePackage : tablePackages) {
                tableMapping(tablePackage);
            }
        }
        return configuration;
    }
}
