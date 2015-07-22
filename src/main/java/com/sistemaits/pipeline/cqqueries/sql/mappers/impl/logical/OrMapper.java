/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sistemaits.pipeline.cqqueries.sql.mappers.impl.logical;


import com.googlecode.cqengine.query.logical.LogicalQuery;
import com.googlecode.cqengine.query.logical.Or;
import com.googlecode.cqengine.query.simple.SimpleQuery;
import com.sistemaits.pipeline.cqqueries.sql.CQE2SQL;
import com.sistemaits.pipeline.cqqueries.sql.mappers.Mapper;

/**
 *
 * @author guglielmo.deconcini
 */
public class OrMapper implements Mapper<Or>{
    private static final String OR = " OR ";
    private static final String TC = " ) ";
    private static final String SC = " ( ";
    
    @Override
    public String map(Or q) {
        StringBuilder sb = new StringBuilder();
        //Open parenthesis
        sb.append(SC);
        
        //Prepare first block from logical child queries, if any
        if(q.hasLogicalQueries())
            sb.append(q.getLogicalQueries()
                    .stream()
                    .map(lq -> CQE2SQL.getMapper((Class<LogicalQuery>) lq.getClass()).map((LogicalQuery) lq))
                    .reduce( (a,b) -> a+OR+b).get());
        
        if(q.hasLogicalQueries() && q.hasSimpleQueries())
            sb.append(OR);//Connect the logical block and simple block of child queries
        
        //Prepare second block from simple child queries, if any
        if(q.hasSimpleQueries())
           sb.append(q.getSimpleQueries()
                    .stream()
                    .map(sq -> CQE2SQL.getMapper((Class<SimpleQuery<?,?>>)sq.getClass()).map((SimpleQuery<?, ?>) sq))
                    .reduce( (a,b) -> a+OR+b).get());
            
        //Close parenthesis
        sb.append(TC);
        
        return sb.toString();
    }
}
