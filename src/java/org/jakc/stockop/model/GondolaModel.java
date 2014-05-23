/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.model;

import javax.persistence.EntityManagerFactory;
import org.jakc.stockop.abstracts.AbstractFacade;
import org.jakc.stockop.entity.Gondola;

/**
 *
 * @author wahhid
 */
public class GondolaModel extends AbstractFacade {
    
    public GondolaModel(EntityManagerFactory emf){
        super(Gondola.class);
        this.emf = emf;
        this.field = new String[]{"gondolaid","gondolaname","description","site.siteid","progress","status","deleted","createddate","createdby","updateddate","updatedby"};
        this.fieldtype = new String[]{"string","string","string","string","int","boolean","boolean","date","string","date","boolean"};        
    }
}
