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
import org.jakc.stockop.entity.Transstock;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author root
 */
public class LazyTransstockDataModel extends LazyDataModel<Transstock>{

    private List<Transstock> dataSource;
    private EntityManagerFactory emf;
    
    public LazyTransstockDataModel(List<Transstock> dataSource,EntityManagerFactory emf){
        this.dataSource = dataSource;
        this.emf = emf;
    }
    
    
    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }
    
    @Override
    public Transstock getRowData(String rowKey){
        if(!rowKey.equals("null")){
            EntityManager em = this.getEntityManager();
            try{
                return em.find(Transstock.class, Integer.parseInt(rowKey));            
            }finally{
                em.close();
            }                        
        }else{
            return null;
        }
        

    }
    
    @Override
    public Object getRowKey(Transstock o) {
        return o.getTransid();
    }    
        
    @Override
    public List<Transstock> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        List<Transstock> data = new ArrayList<Transstock>();
        for(Transstock o : dataSource){
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
