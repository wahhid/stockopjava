/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.model;

import javax.persistence.EntityManagerFactory;
import org.jakc.stockop.abstracts.AbstractFacade;
import org.jakc.stockop.entity.Stock;

/**
 *
 * @author wahhid
 */
public class StockModel extends AbstractFacade{
        
    
    public StockModel(EntityManagerFactory emf){
        super(Stock.class);
        this.emf = emf;
        this.field = new String[]{"id",
                                  "site.siteid",
                                  "pid",
                                  "item",
                                  "periode.periodeid",
                                  "articleid",
                                  "qty",
                                  "pendingqty",
                                  "opnameqty",
                                  "unitcost",
                                  "difqty",
                                  "difunitcost",
                                  "printed",
                                  "refreshdate",
                                  "createddate",
                                  "createdby",
                                  "updateddate",
                                  "updatedby"                
        };
        this.fieldtype = new String[]{"int",
                                  "string",
                                  "string",
                                  "int",
                                  "string",
                                  "string",
                                  "double",
                                  "double",
                                  "double",
                                  "double",
                                  "double",
                                  "double",
                                  "boolean",
                                  "date",
                                  "date",
                                  "string",
                                  "date",
                                  "string"                
        };

        
    }
}
