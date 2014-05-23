/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.abstracts;

import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.jakc.stockop.util.FilterCriteria;
import org.jakc.stockop.util.ProcessCallBack;

/**
 *
 * @author wahhid
 */
public abstract class AbstractFacade<T> {
   
    protected EntityManagerFactory emf;
    protected Class<T> entityClass;    
    protected ProcessCallBack pcb;
    
    protected CriteriaBuilder cb;
    protected CriteriaQuery<T> cq;
    protected Root<T> root;
    protected Predicate criteria;
    
    protected String[] field;
    protected String[] fieldtype;
    
    protected int rowCount = 0;
    protected HashMap filterList;
    protected HashMap sortList;
    
    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;   
       
    }       
    
    protected void filterGenerated(){
        cb = this.emf.createEntityManager().getCriteriaBuilder();
        cq = cb.createQuery(entityClass);
        root = cq.from(entityClass);
        criteria = cb.conjunction(); 
        if(this.filterList != null){
            criteria = new FilterCriteria().result(filterList, field, fieldtype, cb, root, criteria);
        }        
        if(this.sortList != null && sortList.size() > 0){
            if(sortList.get("sorttype").equals("asc")){
                cq.orderBy(cb.asc(root.get((String)sortList.get("sortfieldname"))));
            }else{
                cq.orderBy(cb.desc(root.get((String)sortList.get("sortfieldname"))));
            }           
        }                       
        cq.where(criteria);        
    }
            
    public EntityManager getEntityManager(){
        return this.emf.createEntityManager();                
    }
    
    private void filterFinal(){        
        this.filterGenerated();        
    }
        
    public void create(T entity) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();        
        try{
            em.persist(entity);
            em.getTransaction().commit();
        }catch(Exception ex){
            ex.printStackTrace();
            em.getTransaction().rollback();
        }finally{
            em.close();
        }                                
    }

    public void edit(T entity) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();        
        try{
            em.merge(entity);
            em.getTransaction().commit();
        }catch(Exception ex){
            ex.printStackTrace();
            em.getTransaction().rollback();
        }finally{
            em.close();
        }
    }

    public void remove(T entity) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();        
        try{
            em.remove(em.merge(entity));
            em.getTransaction().commit();
        }catch(Exception ex){
            em.getTransaction().rollback();
        }finally{
            em.close();
        }               
    }
    
    public void delete(T entity) {
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            em.merge(entity);
            em.getTransaction().commit();
        }catch(Exception ex){
            ex.printStackTrace();
            em.getTransaction().rollback();
        }finally{
            em.close();
        }
    }    

    public T find(Object id) {
        EntityManager em = this.getEntityManager();
        try{
            return em.find(entityClass, id);
        }catch(NoResultException ex){
            ex.printStackTrace();
            return null;
        }finally{
            em.close();
        }        
    }        
            
    public List<T> forList() {
        this.filterList = new HashMap();
        this.filterList.put("deleted", false);
        this.sortList = null;
//        this.sortList.put("sortfieldname", "name");
//        this.sortList.put("sorttype","asc");
        return this.findList();
    }
    
    public Object findObject() {
        try{
            this.filterFinal();
            Query query = getEntityManager().createQuery(this.cq);   
            return query.getSingleResult();            
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    
    public List findList() {
        try{
            this.filterFinal();
            Query query = getEntityManager().createQuery(this.cq);   
            this.rowCount = query.getResultList().size();
            return query.getResultList();           
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
    
    public List gridList(int[] range) {
        try{
            this.filterFinal();
            Query query = getEntityManager().createQuery(cq);           
            this.rowCount = query.getResultList().size();
            if(range != null){
                query.setMaxResults(range[1] - range[0]);
                query.setFirstResult(range[0]);                
            }
            return query.getResultList();                     
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }    
               
    public int getRowCount() {
        return rowCount;
    }
  
    public void setFilterList(HashMap filterList) {
        this.filterList = filterList;
    }
    
    public void setSortList(HashMap sortList) {
        this.sortList = sortList;
    }
       
    
}
