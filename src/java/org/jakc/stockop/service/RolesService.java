/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.jakc.stockop.entity.Roles;

/**
 *
 * @author root
 */
public class RolesService {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("StockOpPU");
    
    public RolesService(){
        
    }
    
    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }
    
    public Roles getRoles(String formid, String employeetypeid){
        EntityManager em = this.getEntityManager();
        String strSQL = "SELECT r FROM Roles r WHERE r.formid=:formid AND r.employeetypeid=:employeetypeid";
        try{
            Query query = em.createQuery(strSQL)
                    .setParameter("formid", formid)
                    .setParameter("employeetypeid", employeetypeid);
            return (Roles) query.getSingleResult();
        }catch(Exception ex){
            return null;
        }finally{
            em.close();
        }
    }
}
