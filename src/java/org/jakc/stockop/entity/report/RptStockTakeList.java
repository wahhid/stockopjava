/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.entity.report;

/**
 *
 * @author root
 */
public class RptStockTakeList {
    
    private String flagdifference;
    private int detailid;
    private int sequence;
    private String productid;
    private String articleid;
    private String productname;
    private String gondolaid;
    private String gondolaname;
    private double qty1;
    private double qty2;
    private String uom;
    private String flagexist;
    private String flagstock;
    
    public RptStockTakeList() {
        
    }

    public int getDetailid() {
        return detailid;
    }

    public void setDetailid(int detailid) {
        this.detailid = detailid;
    }

    public String getFlagdifference() {
        return flagdifference;
    }

    public void setFlagdifference(String flagdifference) {
        this.flagdifference = flagdifference;
    }

    public String getFlagexist() {
        return flagexist;
    }

    public void setFlagexist(String flagexist) {
        this.flagexist = flagexist;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public double getQty1() {
        return qty1;
    }

    public void setQty1(double qty1) {
        this.qty1 = qty1;
    }

    public double getQty2() {
        return qty2;
    }

    public void setQty2(double qty2) {
        this.qty2 = qty2;
    }
    
    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getFlagstock() {
        return flagstock;
    }

    public void setFlagstock(String flagstock) {
        this.flagstock = flagstock;
    }

    public String getGondolaid() {
        return gondolaid;
    }

    public void setGondolaid(String gondolaid) {
        this.gondolaid = gondolaid;
    }

    public String getGondolaname() {
        return gondolaname;
    }

    public void setGondolaname(String gondolaname) {
        this.gondolaname = gondolaname;
    }

    public String getArticleid() {
        return articleid;
    }

    public void setArticleid(String articleid) {
        this.articleid = articleid;
    }
    
    
    
}
