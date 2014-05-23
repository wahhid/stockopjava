/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.util;

import java.util.Date;
import java.util.HashMap;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author wahhid
 */
public class FilterCriteria {    
    
    public Predicate result(HashMap filterList, String[] field, String[] fieldtype, CriteriaBuilder cb, Root root, Predicate criteria ){
        for(int i=0;i<field.length;i++){
            if(filterList.containsKey(field[i])){                
                if(fieldtype[i].toLowerCase().equals("string")){
                    if(field[i].contains(".")){
                        String[] arrField = field[i].split("\\.");
                        criteria = cb.and(criteria,cb.equal(root.get(arrField[0]).get(arrField[1]),(String) filterList.get(field[i])));
                    }else{
                        criteria = cb.and(criteria,cb.equal(root.get(field[i]),(String) filterList.get(field[i])));
                    }                   
                }
                if(fieldtype[i].toLowerCase().equals("int")){
                    if(field[i].contains(".")){
                        String[] arrField = field[i].split("\\.");
                        criteria = cb.and(criteria,cb.equal(root.get(arrField[0]).get(arrField[1]),(Integer) filterList.get(field[i])));    
                    }else{
                        criteria = cb.and(criteria,cb.equal(root.get(field[i]),(Integer) filterList.get(field[i])));    
                    }
                    
                }
                if(fieldtype[i].toLowerCase().equals("double")){
                    if(field[i].contains(".")){
                        String[] arrField = field[i].split("\\.");
                        criteria = cb.and(criteria,cb.equal(root.get(arrField[0]).get(arrField[1]),(Double) filterList.get(field[i])));
                    }else{
                        criteria = cb.and(criteria,cb.equal(root.get(field[i]),(Double) filterList.get(field[i])));
                    }
                }
                if(fieldtype[i].toLowerCase().equals("boolean")){
                    if(field[i].contains(".")){
                        String[] arrField = field[i].split("\\.");
                        criteria = cb.and(criteria,cb.equal(root.get(arrField[0]).get(arrField[1]),(Boolean) filterList.get(field[i])));
                    }else{
                        criteria = cb.and(criteria,cb.equal(root.get(field[i]),(Boolean) filterList.get(field[i])));
                    }
                    
                }                   
                if(fieldtype[i].toLowerCase().equals("date")){
                    if(field[i].contains(".")){
                        String[] arrField = field[i].split("\\.");
                        criteria = cb.and(criteria,cb.equal(root.get(arrField[0]).get(arrField[1]),(Date) filterList.get(field[i])));
                    }else{
                        criteria = cb.and(criteria,cb.equal(root.get(field[i]),(Date) filterList.get(field[i])));
                    }                    
                }                   
            }
        }        
        return criteria;
    }
}
