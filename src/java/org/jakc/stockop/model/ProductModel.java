/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.model;

import javax.persistence.EntityManagerFactory;
import org.jakc.stockop.abstracts.AbstractFacade;
import org.jakc.stockop.entity.Product;

/**
 *
 * @author wahhid
 */
public class ProductModel extends AbstractFacade{
    
    public ProductModel(EntityManagerFactory emf){
        super(Product.class);
        this.emf = emf;
        this.field = new String[]{"ean",
                                  "articleid",
                                  "categoryid",
                                  "productname",
                                  "uom",
                                  "deleted",
                                  "createddate",
                                  "createdby",
                                  "updateddate",
                                  "updatedby"
        };
        this.fieldtype = new String[]{"sgtring",
                                  "string",
                                  "string",
                                  "string",
                                  "string",
                                  "boolean",
                                  "date",
                                  "string",
                                  "date",
                                  "string"
        };
        
    }
}
