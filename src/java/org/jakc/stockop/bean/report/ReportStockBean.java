/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.bean.report;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.jakc.stockop.bean.AuthBean;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class ReportStockBean {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("StockOpPU");
    private String siteid;
    private String periodeid;
    private String gondolaid;
    private String nik;
    private String variant;    
    private String marchcode;    
    private String productinfo="00";   
    private String filterGondola="00";
    private String from;
    private String to;
    
    /** Creates a new instance of ReportStockBean */
    public ReportStockBean() {
        
    }

    public void rptStockTakingList() throws IOException{      
        AuthBean authbean = this.getAuthBean();
        siteid = authbean.getSiteid();
        String url;
        if(this.filterGondola.equals("00")){
            url = "/StockOp/ReportTransactionAll?siteid=" + siteid + "&periodeid=" + periodeid  + "&productinfo=" + productinfo + "&variant=" + variant + "&printedbynik=" + authbean.getUsername() + "&printedbyname=" + authbean.getFullname();
        }else{
            url = "/StockOp/ReportTransaction?siteid=" + siteid + "&periodeid=" + periodeid + "&from=" + this.from + "&to=" + this.to + "&productinfo=" + productinfo + "&variant=" + variant + "&printedbynik=" + authbean.getUsername() + "&printedbyname=" + authbean.getFullname();
        }
        
        FacesContext context = FacesContext.getCurrentInstance();
        try {           
           context.getExternalContext().redirect(url);
        }catch (Exception ex) {
           System.out.println(ex.getMessage());
        }
        finally{
           context.responseComplete();
        }        
    }
    
    
    public void rptStockTakingMarchList() throws IOException{      
        AuthBean authbean = this.getAuthBean();
        siteid = authbean.getSiteid();
        String url;
        if(this.filterGondola.equals("00")){
            url = "/StockOp/ReportTransactionMarchAll?siteid=" + siteid + "&periodeid=" + periodeid  + "&productinfo=" + productinfo + "&variant=" + variant + "&marchcode=" + marchcode + "&printedbynik=" + authbean.getUsername() + "&printedbyname=" + authbean.getFullname();
        }else{
            url = "/StockOp/ReportTransactionMarch?siteid=" + siteid + "&periodeid=" + periodeid + "&from=" + this.from + "&to=" + this.to + "&productinfo=" + productinfo + "&variant=" + variant + "&marchcode=" + marchcode + "&printedbynik=" + authbean.getUsername() + "&printedbyname=" + authbean.getFullname();
        }
        
        FacesContext context = FacesContext.getCurrentInstance();
        try {           
           context.getExternalContext().redirect(url);
        }catch (Exception ex) {
           System.out.println(ex.getMessage());
        }
        finally{
           context.responseComplete();
        }        
    }    
    
    
    
    public void rptRequestPrinted() throws IOException{   
        AuthBean authbean = this.getAuthBean();
        siteid = authbean.getSiteid();
        String url;
        url = "/StockOp/ReportArticle?siteid=" + siteid + "&periodeid=" + periodeid  + "&printedbynik=" + authbean.getUsername() + "&printedbyname=" + authbean.getFullname();                
        
        FacesContext context = FacesContext.getCurrentInstance();
        try {           
           context.getExternalContext().redirect(url);
        }catch (Exception ex) {
           System.out.println(ex.getMessage());
        }
        finally{
           context.responseComplete();
        }             
    }
    
    private AuthBean getAuthBean(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application application = facesContext.getApplication();
        ValueBinding binding = application.createValueBinding("#{authBean}");
        AuthBean authbean =  (AuthBean) binding.getValue(facesContext);
        return authbean;
    }
    
    public String getGondolaid() {
        return gondolaid;
    }

    public void setGondolaid(String gondolaid) {
        this.gondolaid = gondolaid;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getPeriodeid() {
        return periodeid;
    }

    public void setPeriodeid(String periodeid) {
        this.periodeid = periodeid;
    }



    public String getSiteid() {
        return siteid;
    }

    public void setSiteid(String siteid) {
        this.siteid = siteid;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getMarchcode() {
        return marchcode;
    }

    public void setMarchcode(String marchcode) {
        this.marchcode = marchcode;
    }

    public String getFilterGondola() {
        return filterGondola;
    }

    public void setFilterGondola(String filterGondola) {
        this.filterGondola = filterGondola;
    }

    public String getProductinfo() {
        return productinfo;
    }

    public void setProductinfo(String productinfo) {
        this.productinfo = productinfo;
    }

    

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
    
    
    
    
    
}
