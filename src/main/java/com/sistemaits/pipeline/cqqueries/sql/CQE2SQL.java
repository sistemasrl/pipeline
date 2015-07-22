/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sistemaits.pipeline.cqqueries.sql;

import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.logical.And;
import com.googlecode.cqengine.query.logical.LogicalQuery;
import com.googlecode.cqengine.query.logical.Not;
import com.googlecode.cqengine.query.logical.Or;
import com.googlecode.cqengine.query.simple.Between;
import com.googlecode.cqengine.query.simple.Equal;
import com.googlecode.cqengine.query.simple.GreaterThan;
import com.googlecode.cqengine.query.simple.LessThan;
import com.googlecode.cqengine.query.simple.SimpleQuery;
import com.sistemaits.pipeline.cqqueries.sql.mappers.Mapper;
import com.sistemaits.pipeline.cqqueries.sql.mappers.impl.logical.AndMapper;
import com.sistemaits.pipeline.cqqueries.sql.mappers.impl.simple.BetweenMapper;
import com.sistemaits.pipeline.cqqueries.sql.mappers.impl.simple.EqualMapper;
import com.sistemaits.pipeline.cqqueries.sql.mappers.impl.simple.GreaterThanMapper;
import com.sistemaits.pipeline.cqqueries.sql.mappers.impl.simple.LessThanMapper;
import com.sistemaits.pipeline.cqqueries.sql.mappers.impl.logical.NotMapper;
import com.sistemaits.pipeline.cqqueries.sql.mappers.impl.logical.OrMapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author guglielmo.deconcini
 */
public class CQE2SQL {
    
    public static final String SELECT = "SELECT %s from %s";
    public static final String WHERE = "WHERE %s";
    public static final String TC = ";";
    
    private static final Map<Class<? extends Query>,Mapper> mappers = getMappers();
    
    public static String convertCqQuery(Query<?> query){
        
        if( query instanceof SimpleQuery )
            return getMapper((Class<SimpleQuery<?,?>>)query.getClass()).map((SimpleQuery<?,?>) query);
        else if (query instanceof LogicalQuery)
            return getMapper((Class<LogicalQuery<?>>)query.getClass()).map((LogicalQuery<?>) query);
        
        throw new UnsupportedOperationException("Unknown query class " + query.getClass().getName());
    }

    private static Map<Class<? extends Query>, Mapper> getMappers(){
        HashMap<Class<? extends Query>,Mapper> map = new HashMap<>();
        
        //Simple
        map.put(Equal.class,new EqualMapper());
        map.put(GreaterThan.class,new GreaterThanMapper());
        map.put(LessThan.class,new LessThanMapper());
        map.put(Between.class,new BetweenMapper());
        
        //Logical
        map.put(And.class,new AndMapper());
        map.put(Or.class,new OrMapper());
        map.put(Not.class,new NotMapper());
        
        return Collections.unmodifiableMap(map);        
    }
    
    public static <T extends Query> Mapper<T> getMapper(Class<T> clazz){
       Mapper <T> mapper;
       
       if( (mapper = mappers.get(clazz)) != null )
           return mapper;
       
       throw new UnsupportedOperationException("Unknown query class " + clazz.getName());
    } 
    
}
