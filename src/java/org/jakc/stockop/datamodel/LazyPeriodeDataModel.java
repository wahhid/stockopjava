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
import org.jakc.stockop.entity.Periode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author root
 */
public class LazyPeriodeDataModel extends LazyDataModel<Periode>{

    private List<Periode> dataSource;
    private EntityManagerFactory emf;
    
    public LazyPeriodeDataModel(List<Periode> dataSource,EntityManagerFactory emf){
        this.dataSource = dataSource;
        this.emf = emf;
    }
    
    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }
    
    @Override
    public Periode getRowData(String rowKey){
        if(!rowKey.equals("null")){
            EntityManager em = this.getEntityManager();
            try{
                return em.find(Periode.class, rowKey);            
            }finally{
                em.close();
            }            
        }else{
            return null;
        }

    }
    
    @Override
    public Object getRowKey(Periode o) {
        return o.getPeriodeid();
    }        
        
    @Override
    public List<Periode> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        List<Periode> data = new ArrayList<Periode>();
        for(Periode o : dataSource){
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
