/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sistemaits.pipeline.cqqueries.sql.mappers.impl.simple;

import com.googlecode.cqengine.query.simple.Between;
import com.sistemaits.pipeline.cqqueries.sql.mappers.Mapper;
import static com.sistemaits.pipeline.cqqueries.QueryTypeAdapter.*;

/**
 *
 * @author guglielmo.deconcini
 */
public class BetweenMapper implements Mapper<Between> {

    @Override
    public String map(Between q) {
        StringBuilder b = new StringBuilder();
        
        b.append(q.getAttributeName());
        b.append(" ");
        
        b.append(">");
        if(q.isLowerInclusive())
            b.append("=");
        b.append(" ");
        b.append(adapt(q.getLowerValue()));
        
        b.append(" AND ");
        b.append(q.getAttributeName());
        b.append(" ");
        
        b.append("<");
        if(q.isUpperInclusive())
            b.append("=");
        b.append(" ");
        b.append(adapt(q.getUpperValue()));

        return b.toString();
    }
    
}
