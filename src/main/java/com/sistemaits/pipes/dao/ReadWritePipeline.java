/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sistemaits.pipes.dao;

import com.googlecode.cqengine.query.Query;
import java.util.stream.Stream;

/**
 *
 * @author guglielmo.deconcini
 */
public interface ReadWritePipeline <T>{
    
    public Stream<T> select(Query<T> query);
    
    public Stream<T> select();
    
    public void save(Stream<T> elems);
    
}
