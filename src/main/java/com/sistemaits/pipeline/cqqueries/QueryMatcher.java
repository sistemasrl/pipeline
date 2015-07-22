/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sistemaits.pipeline.cqqueries;

import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.logical.And;
import com.googlecode.cqengine.query.logical.Not;
import com.googlecode.cqengine.query.logical.Or;
import com.googlecode.cqengine.query.logical.LogicalQuery;
import com.googlecode.cqengine.query.simple.Between;
import com.googlecode.cqengine.query.simple.Equal;
import com.googlecode.cqengine.query.simple.GreaterThan;
import com.googlecode.cqengine.query.simple.LessThan;
import com.googlecode.cqengine.query.simple.SimpleQuery;

import java.util.HashMap;
import java.util.function.Predicate;

/**
 *
 * @author guglielmo.deconcini
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class QueryMatcher<O> {
    
    private HashMap<String,QueryBound<?>> baseBounds = new HashMap<>();
    
    public QueryMatcher(Query<O> baseQuery){
    	baseBounds = getBoundsFromQuery(baseQuery, e-> true);
    }   

    private HashMap<String, QueryBound<?>> getBoundsFromQuery(Query<O> query, Predicate<SimpleQuery<O, ?>> predicate){
        if(query instanceof LogicalQuery)
            return extractBoundsFromLogicalQuery((LogicalQuery<O>) query,predicate);
        else if(query instanceof SimpleQuery){
        	SimpleQuery sq = (SimpleQuery)query;
        	HashMap<String, QueryBound<?>> bounds = new HashMap<>();
        	if(predicate.test(sq))
        		bounds.put(sq.getAttributeName(), new QueryBound(sq));
        	return bounds;
        }
        else
            throw new UnsupportedOperationException("Unknown class query type " + query.getClass().getName());      
    }
    
    private HashMap<String, QueryBound<?>> extractBoundsFromLogicalQuery(LogicalQuery<O> query, Predicate<SimpleQuery<O, ?>> predicate){
    	HashMap<String, QueryBound<?>> bounds = new HashMap<>();
    	
        if(query instanceof And){      	
        	if(query.hasLogicalQueries())
        		query.getLogicalQueries()
        		.forEach( lq -> extractBoundsFromLogicalQuery(lq, predicate)
        				.forEach((k,v) -> {
                			QueryBound<?> qb;
                			if(  (qb = bounds.get(k)) != null)
                				qb.andMerge(v);
                			else
                				bounds.put(k, v);
        				}));

            if(query.hasSimpleQueries())        	
        		query.getSimpleQueries()
        		.stream()
        		.filter(predicate)
        		.forEach(sq -> {
        			QueryBound<?> qb;
        			if(  (qb = bounds.get(sq.getAttributeName())) != null)
        				qb.andMerge(new QueryBound(sq));
        			else
        				bounds.put(sq.getAttributeName(), new QueryBound(sq));
        		});
                            
        }
        else if(query instanceof Or){      	
        	if(query.hasLogicalQueries())
        		query.getLogicalQueries()
        		.forEach( lq -> extractBoundsFromLogicalQuery(lq, predicate)
        				.forEach((k,v) -> {
                			QueryBound<?> qb;
                			if(  (qb = bounds.get(k)) != null)
                				qb.orMerge(v);
                			else
                				bounds.put(k, v);
        				}));

            if(query.hasSimpleQueries())        	
        		query.getSimpleQueries()
        		.stream()
        		.filter(predicate)
        		.forEach(sq -> {
        			QueryBound<?> qb;
        			if(  (qb = bounds.get(sq.getAttributeName())) != null)
        				qb.orMerge(new QueryBound(sq));
        			else
        				bounds.put(sq.getAttributeName(), new QueryBound(sq));
        		});
        }
        else if(query instanceof Not){
            Not<O> lq = (Not<O>) query;
            final HashMap<String,QueryBound<?>> boundsToNegate = getBoundsFromQuery(lq.getNegatedQuery(), predicate);
            
            boundsToNegate.forEach( (k,v) -> bounds.put(k, v.not()));
        }
        else
            throw new UnsupportedOperationException("Unknown class query type " +query.getClass().getName());
        
        return bounds;
    }
        
    public boolean overlapsWith(Query<O> otherQuery){
    	
    	return getBoundsFromQuery(otherQuery, sq -> baseBounds.containsKey(sq.getAttributeName()))
    			.entrySet()
    			.stream()
    			.allMatch(e -> baseBounds.get(e.getKey()).overlaps(e.getValue()));
    }
    
    private class QueryBound <C extends Comparable<C>>{
        //Bounds are assumed always inclusive
        //Comparison is loose
        C lowerBound = null;
        C upperBound = null;

        public QueryBound(SimpleQuery<?,C> q){
            if(q instanceof Between){
                Between<?,C> sq = (Between<?, C>)q;
                this.lowerBound = sq.getLowerValue();
                this.upperBound = sq.getUpperValue();
            }
            else if(q instanceof LessThan)
                this.upperBound = ((LessThan<?,C>)q).getValue();
            else if(q instanceof GreaterThan)
                this.lowerBound = ((GreaterThan<?,C>)q).getValue();
            else if(q instanceof Equal){
                Equal<?,C> sq = (Equal<?, C>)q;
                this.lowerBound = sq.getValue();
                this.upperBound = sq.getValue();
            }
            else
                throw new UnsupportedOperationException("Unknown class query type " + q.getClass().getName());
        }
        
        QueryBound<C> not(){
            C newLowerBound = this.lowerBound == null ? this.upperBound : null;
            C newUpperBound = this.upperBound == null ? this.lowerBound : null;
            
            this.lowerBound = newLowerBound;
            this.upperBound = newUpperBound;
            
            return this;
        }
        
        QueryBound<C> andMerge(QueryBound<?> qb){
        	QueryBound<C> qbc = (QueryBound<C>)qb; //Generics Madness
        	
        	if(this.lowerBound == null || qbc.lowerBound != null && this.lowerBound.compareTo(qbc.lowerBound) < 0 )
                this.lowerBound = qbc.lowerBound;
            
            if(this.upperBound == null || qbc.upperBound != null && this.upperBound.compareTo(qbc.upperBound) > 0)
                this.upperBound = qbc.upperBound;
                   
            if(this.upperBound != null && this.lowerBound !=null)
            	return this.upperBound.compareTo(this.lowerBound) >  0 ? this : null;
           
            return  this ;
        }
        
        QueryBound<C> orMerge(QueryBound<?> qb){
        	QueryBound<C> qbc = (QueryBound<C>)qb; //Generics Madness
        	
            if(qbc.lowerBound == null || this.lowerBound != null && this.lowerBound.compareTo(qbc.lowerBound) > 0 )
                this.lowerBound = qbc.lowerBound;
            
            if(qbc.upperBound == null || this.upperBound != null && this.upperBound.compareTo(qbc.upperBound) < 0)
                this.upperBound = qbc.upperBound;
                        
            return this;

        }
        
        boolean overlaps(QueryBound<?> qb){
        	QueryBound<C> qbc = (QueryBound<C>)qb; //Generics Madness
        	      	
        	return  ((this.upperBound == null || qbc.lowerBound == null)  || this.upperBound.compareTo(qbc.lowerBound) >= 0) &&
        			((this.lowerBound == null || qbc.upperBound == null)  || qbc.upperBound.compareTo(this.lowerBound) >= 0);
        }
    } 
      
}
