/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.datasorter;

import java.util.Comparator;
import org.jakc.stockop.entity.Employee;
import org.primefaces.model.SortOrder;

public class LazyEmployeeSorter implements Comparator<Employee> {

    private String sortField;
    
    private SortOrder sortOrder;
    
    public LazyEmployeeSorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    @Override
    public int compare(Employee employee1, Employee employee2) {
        try {
            Object value1 = Employee.class.getField(this.sortField).get(employee1);
            Object value2 = Employee.class.getField(this.sortField).get(employee2);

            int value = ((Comparable)value1).compareTo(value2);
            
            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        }
        catch(Exception e) {
            throw new RuntimeException();
        }
    }
}
