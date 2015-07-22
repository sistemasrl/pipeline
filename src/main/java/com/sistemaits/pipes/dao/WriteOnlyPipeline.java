/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sistemaits.pipes.dao;

import java.util.stream.Stream;

/**
 *
 * @author guglielmo.deconcini
 */
public interface WriteOnlyPipeline <T>{
    
    public void save(Stream<T> elems);
}
