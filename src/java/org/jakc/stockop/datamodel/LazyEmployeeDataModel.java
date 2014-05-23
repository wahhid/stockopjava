/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.jakc.stockop.datasorter.LazyEmployeeSorter;
import org.jakc.stockop.entity.Employee;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author root
 */
public class LazyEmployeeDataModel extends LazyDataModel<Employee> {

    private List<Employee> dataSource;
    private EntityManagerFactory emf;
    
    public LazyEmployeeDataModel(List<Employee> dataSource, EntityManagerFactory emf){
        this.dataSource = dataSource;
        this.emf = emf;
    }
    
    
    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }
    
    
    @Override
    public Employee getRowData(String rowKey){
        if(!rowKey.equals("null")){
            EntityManager em = this.getEntityManager();
            try{
                return em.find(Employee.class, rowKey);            
            }finally{
                em.close();
            }            
        }else{
            return null;
        }
    }
    
    @Override
    public Object getRowKey(Employee o) {
        return o.getNik();
    }            

    
    @Override
    public List<Employee> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        List<Employee> data = new ArrayList<Employee>();
        for(Employee o : dataSource){
            data.add(o);
        }                        
        
        int dataSize = data.size();
        this.setRowCount(dataSize);
        
        //sort
        if(sortField != null) {
            Collections.sort(data, new LazyEmployeeSorter(sortField, sortOrder));
        }
        
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
