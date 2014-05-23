/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.jakc.stockop.entity.Product;

/**
 *
 * @author root
 */
public class ProductService {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("StockOpPU");
    
    public ProductService(){
        
    }
    
    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }
    
    public Product find(String ean,String articleid){        
        EntityManager em = this.getEntityManager();        
        String strSQL = "SELECT p FROM Product p WHERE p.ean=:ean AND p.articleid=:articleid";
        try{
            Query query = em.createQuery(strSQL)
                    .setParameter("ean", ean)
                    .setParameter("articleid", articleid);
            return (Product) query.getSingleResult();            
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }

    }
}
