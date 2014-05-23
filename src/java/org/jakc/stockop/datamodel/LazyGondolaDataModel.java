/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.jakc.stockop.backing.BackingModel;
import org.jakc.stockop.bean.GondolaBean;
import org.jakc.stockop.entity.Gondola;
import org.jakc.stockop.model.GondolaModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author root
 */
public class LazyGondolaDataModel extends LazyDataModel<Gondola>{
        
    private EntityManagerFactory emf;
    private GondolaBean bean;    
    private BackingModel bm;
    
    public LazyGondolaDataModel(GondolaBean bean,BackingModel bm){
        this.bean = bean;        
        this.bm = bm;
    }

    @Override
    public Gondola getRowData(String rowKey){
        if(!rowKey.equals("null")){                        
            return (Gondola) this.bm.getGondolaModel().find(rowKey);                               
        }else{
            return null;
        }

    }
    
    @Override
    public Object getRowKey(Gondola o) {
        return o.getGondolaid();
    }            

    @Override
    public List<Gondola> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        
        HashMap sortList = new HashMap();           
        if(sortField != null){
            sortList.put("sortfieldname", sortField);
            if(SortOrder.ASCENDING.equals(sortOrder)){
                sortList.put("sorttype", "asc");
            }else{
                sortList.put("sorttype", "desc");
            }           
        }else{
            sortList = null;
        }             
        int[] range = {first, first + pageSize};        
        this.bm.getGondolaModel().setFilterList(this.bean.getFilterList());
        this.bm.getGondolaModel().setSortList(sortList);        
        List<Gondola> os = this.bm.getGondolaModel().gridList(range);
        System.out.println("Row Count : " + os.size());        
        this.setPageSize(pageSize);
        this.setRowCount(this.bm.getGondolaModel().getRowCount());       
        return os;  
    }
    
}
