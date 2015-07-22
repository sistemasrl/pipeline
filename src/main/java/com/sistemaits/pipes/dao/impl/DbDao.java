/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sistemaits.pipes.dao.impl;

import com.googlecode.cqengine.query.Query;
import static com.googlecode.cqengine.query.QueryFactory.*;
import com.sistemaits.annotation.Column;
import com.sistemaits.pipeline.cqqueries.sql.CQE2SQL;
import com.sistemaits.pipes.dao.ReadOnlyPipeline;
import com.sistemaits.pipeline.cqqueries.QueryMatcher;
import java.util.Arrays;
import java.util.stream.Stream;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

/**
 *
 * @author guglielmo.deconcini
 */
public class DbDao <T> implements ReadOnlyPipeline<T>{

    private final Class<T> clazz;
    private final QueryMatcher<T> queryMatcher;
    private final String table;
    private final Query<T> setBounds;
    private final Sql2o sql2o;
    private final String fields;
    
    private static final String SELECT = " SELECT ";
    private static final String FROM = " FROM ";
    private static final String WHERE = " WHERE ";
    private static final Logger log = Logger.getLogger(DbDao.class);
    
    public DbDao(Class<T> clazz,Sql2o sql2o){
        this(clazz,null,clazz.getSimpleName().toLowerCase(),sql2o);
    }
        
    public DbDao(Class<T> clazz,String table,Sql2o sql2o){
        this(clazz,null,table,sql2o);
    }
    
    public DbDao(Class<T> clazz,Query<T> setBounds,Sql2o sql2o){
        this(clazz,setBounds,clazz.getSimpleName(),sql2o);
    }
        
    public DbDao(Class<T> clazz, Query<T> setBounds, String table,Sql2o sql2o){
        this.table = table;
        this.clazz = clazz;
        this.setBounds = setBounds;
        this.sql2o = sql2o;
        
        queryMatcher = setBounds != null ? new QueryMatcher<>(setBounds) : null;
        
        this.fields = Arrays.asList(clazz.getDeclaredFields())
                .stream()
                .filter(f -> f.isAnnotationPresent(Column.class))
                .map(f -> f.getName())
                .reduce((a,b) -> a.concat(", ").concat(b)).get();
    }
    
    
    @Override
    public Stream<T> select(Query<T> query) {
        StringBuilder queryBuilder = new StringBuilder();
        
        queryBuilder
                .append(SELECT)
                .append(fields)
                .append(FROM)
                .append(table);
        
        try(Connection conn =  sql2o.open()){

            if(setBounds != null ){
                if(query != null && !queryMatcher.overlapsWith(query))
                    return Stream.empty();
                
                queryBuilder
                        .append(WHERE)
                        .append(CQE2SQL.convertCqQuery(query != null ? and(query,setBounds) : setBounds));
            }
            else if(query != null){
                queryBuilder
                        .append(WHERE)
                        .append(CQE2SQL.convertCqQuery(query));
            }
            
            log.debug(queryBuilder.toString());
            
            return conn.createQuery(queryBuilder.toString())
                    .executeAndFetch(clazz).stream();
            
        }

    }

    @Override
    public Stream<T> select() {
      return select(null);
    }
    
}
