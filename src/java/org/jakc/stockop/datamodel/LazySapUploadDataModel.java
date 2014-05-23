/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.datamodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.jakc.stockop.backing.BackingModel;
import org.jakc.stockop.bean.SapUploadBean;
import org.jakc.stockop.bean.StockBean;
import org.jakc.stockop.entity.Stock;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author root
 */
public class LazySapUploadDataModel extends LazyDataModel<Stock>{

    private SapUploadBean bean;
    private BackingModel bm;
    
    public LazySapUploadDataModel(SapUploadBean bean,BackingModel bm){
        this.bean = bean;
        this.bm = bm;
    }
    
    
    private EntityManager getEntityManager(){
        return this.bm.getEmf().createEntityManager();
    }
    
    @Override
    public Stock getRowData(String rowKey){
        if(!rowKey.equals("null")){
            return (Stock) this.bm.getStockModel().find(rowKey);            
        }else{
            return null;
        }
    }
    
    @Override
    public Object getRowKey(Stock stock) {
        return stock.getId();
    }    
    
    @Override
    public List<Stock> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
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
        this.bm.getStockModel().setFilterList(this.bean.getFilterList());
        this.bm.getStockModel().setSortList(sortList);        
        List<Stock> os = this.bm.getStockModel().gridList(range);
        //System.out.println("Row Count : " + os.size());        
        this.setPageSize(pageSize);
        this.setRowCount(this.bm.getStockModel().getRowCount());       
        return os;         
    }    
}
