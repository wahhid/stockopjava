/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.backing;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.jakc.stockop.model.GondolaModel;
import org.jakc.stockop.model.ProductModel;
import org.jakc.stockop.model.StockModel;


/**
 *
 * @author wahhid
 */
public class BackingModel {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("StockOpPU");
    private GondolaModel gondolaModel;
    private StockModel stockModel;
    private ProductModel productModel;

    public BackingModel(){
        this.gondolaModel = new GondolaModel(emf);
        this.stockModel = new StockModel(emf);
        this.productModel = new ProductModel(emf);
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }

    public GondolaModel getGondolaModel() {
        return gondolaModel;
    }

    public StockModel getStockModel() {
        return stockModel;
    }

    public ProductModel getProductModel() {
        return productModel;
    }
    
    
    
    

    
    
    
    
}
