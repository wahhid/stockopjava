/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author root
 */
public class StockService {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("StockOpPU");
    
    public StockService(){
        
    }
    
    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }
    
    
}
