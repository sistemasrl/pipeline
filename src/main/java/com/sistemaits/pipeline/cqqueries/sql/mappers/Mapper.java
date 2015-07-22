/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sistemaits.pipeline.cqqueries.sql.mappers;


import com.googlecode.cqengine.query.Query;

/**
 *
 * @author guglielmo.deconcini
 */
public interface Mapper<T extends Query> {
    
    public String map(T q);
}
