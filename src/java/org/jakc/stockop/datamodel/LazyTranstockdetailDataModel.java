/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.jakc.stockop.entity.Product;
import org.jakc.stockop.entity.Transtockdetail;
import org.jakc.stockop.entity.Transtockdetailtemp;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author root
 */

public class LazyTranstockdetailDataModel extends LazyDataModel<Transtockdetail>{

    private List<Transtockdetail> dataSource;
    private EntityManagerFactory emf;
    
    public LazyTranstockdetailDataModel(List<Transtockdetail> dataSource, EntityManagerFactory emf){
        this.dataSource = dataSource;
        this.emf = emf;
    }
    
    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }
    
    
    @Override
    public Transtockdetail getRowData(String rowKey){
        if(!rowKey.equals("null")){
            EntityManager em = this.getEntityManager();
            try{
                return em.find(Transtockdetail.class, Integer.parseInt(rowKey));            
            }finally{
                em.close();
            }            
        }else{
            return new Transtockdetail();
        }
    }
    
    @Override
    public Object getRowKey(Transtockdetail o) {
        return o.getDetailid();
    }    
    
    @Override
    public List<Transtockdetail> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {        
        List<Transtockdetail> data = new ArrayList<Transtockdetail>();
        for(Transtockdetail o : dataSource){
            if(o.getProduct() == null){                               
                Product product = new Product();
                product.setEan(this.findTranstokdetail(o.getDetailid()));
                product.setProductname("");
                o.setProduct(product);
            }
            data.add(o);
        }                        
        int dataSize = data.size();
        this.setRowCount(dataSize);
        
        //paginate
        if(dataSize > pageSize) {
            try {
                return data.subList(first, first + pageSize);
            }
            catch(IndexOutOfBoundsException e) {
                return data.subList(first, first + (dataSize % pageSize));
            }
        }
        else {
            return data;
        }        
    }    
    
    
    private Product findProduct(String ean){
        EntityManager em = this.getEntityManager();
        try{
            return (Product) em.createNamedQuery("Product.findByEan").setParameter("ean", ean).getSingleResult();
        }catch(Exception ex){
            return null;
        }finally{
            em.close();
        }
    }
    
    private String findTranstokdetail(int detailid){
        EntityManager em = this.getEntityManager();
        String strSQL = "SELECT t.detailid, t.productid FROM Transtockdetailtemp t WHERE t.detailid=:detailid";
        try{
            Query query = em.createQuery(strSQL).setParameter("detailid", detailid);
            Object[] o =  (Object[]) query.getSingleResult();
            return (String) o[1];
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
            
    }
        
    
}
