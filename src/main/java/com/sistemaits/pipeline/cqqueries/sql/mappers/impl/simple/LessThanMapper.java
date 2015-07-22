/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sistemaits.pipeline.cqqueries.sql.mappers.impl.simple;

import com.googlecode.cqengine.query.simple.LessThan;
import com.sistemaits.pipeline.cqqueries.sql.mappers.Mapper;
import static com.sistemaits.pipeline.cqqueries.QueryTypeAdapter.*;

/**
 *
 * @author guglielmo.deconcini
 */
public class LessThanMapper implements Mapper<LessThan>{

    @Override
    public String map(LessThan q) {
        StringBuilder b = new StringBuilder();
        
        b.append(q.getAttributeName());
        b.append(" ");
        
        b.append("<");
        if(q.isValueInclusive())
            b.append("=");
        b.append(" ");
        b.append(adapt(q.getValue()));
        
        return b.toString();
    }
    
}
