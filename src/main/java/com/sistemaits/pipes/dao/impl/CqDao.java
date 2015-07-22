/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sistemaits.pipes.dao.impl;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.query.Query;
import com.sistemaits.pipes.dao.ReadOnlyPipeline;
import com.sistemaits.pipeline.cqqueries.QueryMatcher;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author guglielmo.deconcini
 */
public class CqDao<T> extends ConcurrentIndexedCollection<T> implements ReadOnlyPipeline<T>{
    
    private final Class<T> clazz;
    private final Query<T> setBounds;
    private final QueryMatcher<T> queryMatcher;
    
    public CqDao(Class<T> clazz){
        this(clazz,null);
    }
        
    public CqDao(Class<T> clazz,Query<T> setBounds){
        super();
        this.clazz = clazz;
        this.setBounds = setBounds;
        this.queryMatcher = setBounds != null ? new QueryMatcher<>(setBounds) : null;
    }
    
    @Override
    public Stream<T> select(Query<T> query) {
        return  queryMatcher.overlapsWith(query) ?  StreamSupport.stream(retrieve(query).spliterator(), false) : Stream.empty();
    }

    @Override
    public Stream<T> select() {
        return stream();
    }

}
