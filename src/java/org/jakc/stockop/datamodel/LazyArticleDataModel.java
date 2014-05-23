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
import org.jakc.stockop.bean.ArticleBean;
import org.jakc.stockop.entity.Product;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author root
 */
public class LazyArticleDataModel extends LazyDataModel<Product>{

    private ArticleBean bean;
    private BackingModel bm;
    
    public LazyArticleDataModel(ArticleBean bean, BackingModel bm){
        this.bean = bean;
        this.bm = bm;
    }
    
    private EntityManager getEntityManager(){
        return this.bm.getEmf().createEntityManager();
    }
        
    @Override
    public Product getRowData(String rowKey){
        if(!rowKey.equals("null")){
            return (Product) bm.getProductModel().find(rowKey);            
        }else{
            return null;
        }

    }

    @Override
    public Object getRowKey(Product product) {
        return product.getEan();
    }
    
    @Override
    public List<Product> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
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
        this.bm.getProductModel().setFilterList(this.bean.getFilterList());
        this.bm.getProductModel().setSortList(sortList);        
        List<Product> os = this.bm.getProductModel().gridList(range);
        //System.out.println("Row Count : " + os.size());        
        this.setPageSize(pageSize);
        this.setRowCount(this.bm.getProductModel().getRowCount());       
        return os;         
    }
    
}
