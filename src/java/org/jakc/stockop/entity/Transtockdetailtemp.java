/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author root
 */

@Entity
@Table(name = "transtockdetail", catalog = "opnam", schema = "")
@XmlRootElement
public class Transtockdetailtemp implements Serializable {
    
    @Id
    @Column(name="detailid")
    private Integer detailid;
    @Column(name="productid")
    private String productid;
    @Column(name = "qty2")
    private double qty2;    

    public Transtockdetailtemp(){
        
    }

    public Integer getDetailid() {
        return detailid;
    }

    public void setDetailid(Integer detailid) {
        this.detailid = detailid;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public double getQty2() {
        return qty2;
    }

    public void setQty2(double qty2) {
        this.qty2 = qty2;
    }

    

    
    
    
}
