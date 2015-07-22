/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sistemaits.pipeline.cqqueries;

import java.time.LocalDateTime;
/**
 *
 * @author guglielmo.deconcini
 */
public class QueryTypeAdapter {
 
    /**
     * Properly fills a query field based on its type,
     * e.g. a String, a timestamp, etc.
     * @param o
     * @return 
     */
    public static String adapt(Object o){
        
        if(o instanceof String)
            return adapt((String)o);
        if(o instanceof LocalDateTime)
            return adapt((LocalDateTime) o);
 
        return o.toString();
    }
    
    public static String adapt(String o){
        return "'"+o+"'";
    }
    
    public static String adapt(LocalDateTime o){
        return "'"+o.toString()+"'";
    }
    
}
