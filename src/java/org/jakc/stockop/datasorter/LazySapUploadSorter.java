/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.datasorter;

import java.util.Comparator;
import org.jakc.stockop.entity.Employee;
import org.jakc.stockop.entity.Stock;
import org.primefaces.model.SortOrder;

public class LazySapUploadSorter implements Comparator<Stock> {

    private String sortField;
    
    private SortOrder sortOrder;
    
    public LazySapUploadSorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    @Override
    public int compare(Stock stock1, Stock stock2) {
        try {
            Object value1 = Employee.class.getField(sortField).get(stock1);
            Object value2 = Employee.class.getField(sortField).get(stock2);
            int value = ((Comparable)value1).compareTo(value2);            
            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        }
        catch(Exception e) {
            throw new RuntimeException();
        }
    }


}
