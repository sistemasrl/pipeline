/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sistemaits.pipeline.cqqueries.sql.mappers.impl.simple;

import com.googlecode.cqengine.query.simple.Equal;
import com.sistemaits.pipeline.cqqueries.sql.mappers.Mapper;
import static com.sistemaits.pipeline.cqqueries.QueryTypeAdapter.*;
/**
 *
 * @author guglielmo.deconcini
 */
public class EqualMapper implements Mapper<Equal>{

    @Override
    public String map(Equal q) {
        
        return String.format("%s = %s",q.getAttributeName(),adapt(q.getValue()));
    }
    
}
