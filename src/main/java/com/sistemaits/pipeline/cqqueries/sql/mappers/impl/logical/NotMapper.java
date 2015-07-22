/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sistemaits.pipeline.cqqueries.sql.mappers.impl.logical;

import com.googlecode.cqengine.query.logical.Not;
import com.sistemaits.pipeline.cqqueries.sql.CQE2SQL;
import com.sistemaits.pipeline.cqqueries.sql.mappers.Mapper;

/**
 *
 * @author guglielmo.deconcini
 */
public class NotMapper implements Mapper<Not>{
        private static final String NOT = " NOT ";
        private static final String TC = " ) ";
        private static final String SC = " ( ";

    @Override
    public String map(Not q) {

        return NOT + SC + CQE2SQL.convertCqQuery(q.getNegatedQuery()) + TC;
    }
    
}
