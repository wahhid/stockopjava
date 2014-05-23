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
import org.jakc.stockop.entity.Employeetype;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author root
 */
public class LazyEmployeetypeDataModel extends LazyDataModel<Employeetype>{

    private List<Employeetype> dataSource;
    private EntityManagerFactory emf;
    
    public LazyEmployeetypeDataModel(List<Employeetype> dataSource, EntityManagerFactory emf){
        this.dataSource = dataSource;
        this.emf = emf;
    }
    
    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }
        
    @Override
    public Employeetype getRowData(String rowKey){
        if(!rowKey.equals("null")){
            EntityManager em = this.getEntityManager();
            try{
                return em.find(Employeetype.class, rowKey);            
            }finally{
                em.close();
            }            
        }else{
            return null;
        }

    }

    @Override
    public Object getRowKey(Employeetype o) {
        return o.getEmployeetypeid();
    }
    
    @Override
    public List<Employeetype> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        List<Employeetype> data = new ArrayList<Employeetype>();
        for(Employeetype o : dataSource){
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
    
}
