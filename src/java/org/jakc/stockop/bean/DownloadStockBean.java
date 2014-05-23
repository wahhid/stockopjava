/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.bean;

import com.csvreader.CsvReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.jakc.stockop.entity.Periode;
import org.jakc.stockop.entity.Site;
import org.jakc.stockop.entity.Stock;
import org.jakc.stockop.service.ProductService;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class DownloadStockBean {
    
    private EntityManagerFactory emf  = Persistence.createEntityManagerFactory("StockOpPU");
    private ProductService productService = new ProductService();
    private String siteid;
    private String periodeid;    
    private static final int BUFFER_SIZE = 6124;
    private int rowInsert=0;
    private int rowUpdate=0;
    private int rowSkip=0;
    private int rowTotal=0;
    /** Creates a new instance of DownloadStockBean */
    public DownloadStockBean() {
        
    }
    
    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }
    
    public void processFileUpload(FileUploadEvent event){
        AuthBean authbean = this.getAuthBean();
        StockBean stockbean = this.getStockBean();
        this.rowInsert=0;
        this.rowUpdate=0;
        this.rowSkip=0;
        this.rowTotal=0;        
        ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();                 
        String fullPath = extContext.getRealPath("//upload") + "//" + event.getFile().getFileName();        
        File result;        
        result = new File(fullPath);
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(result);
            byte[] buffer = new byte[this.BUFFER_SIZE];
            int bulk;
            
            InputStream inputStream = event.getFile().getInputstream();
            while(true){
                bulk = inputStream.read(buffer);
                if(bulk<0){
                    break;
                }
                fileOutputStream.write(buffer,0,bulk);
                fileOutputStream.flush();                
            }                        
            fileOutputStream.close();
            inputStream.close();                        
            //Process Stock CSV
            CsvReader article = new CsvReader(fullPath);
            article.setDelimiter(';');
            while(article.readRecord()){                
                if(article.getColumnCount() > 7){
                    //if(!article.get(0).equals(authbean.getSiteid())){                   
                    if(!article.get(1).equals(authbean.getSiteid())){
                        rowSkip++;                                                                                     
                    }else{               
                        Stock stock = new Stock();                            
                        stock.setSite(new Site());
                        stock.setPeriode(new Periode());                
                        
                        //stock.getSite().setSiteid(article.get(0));               
                        stock.getSite().setSiteid(article.get(1));
                        
                        stock.getPeriode().setPeriodeid(periodeid);
                        
                        //stock.setPid(article.get(1));           
                        stock.setPid(article.get(3));           
                        
                        //stock.setItem(Integer.parseInt(article.get(2)));
                        stock.setItem(Integer.parseInt(article.get(0)));
                        
                        //stock.setArticleid(article.get(3));
                        stock.setArticleid(article.get(2).substring(1,article.get(2).length()));                        
                        
                        //stock.setQty(Double.parseDouble(article.get(4)));
                        stock.setQty(Double.parseDouble(article.get(7)));
                        
                        //stock.setUnitcost(Double.parseDouble(article.get(6)));
                        stock.setUnitcost(Double.parseDouble(article.get(10)));
                        
                        Stock findstock = this.findStock(stock.getSite().getSiteid(), periodeid, stock.getArticleid());
                        
                        if(findstock == null){
                            rowInsert++;
                            System.out.println("Insert Stock");
                            System.out.println("Periode ID : " + periodeid);
                            System.out.println("Qty : " + stock.getQty());
                            stock.setCreateddate(new Date(System.currentTimeMillis()));
                            stock.setCreatedby(authbean.getUsername());
                            stock.setUpdateddate(new Date(System.currentTimeMillis()));
                            stock.setUpdatedby(authbean.getUsername());                                    
                            this.insertStock(stock);                    
                        }else{
                            rowUpdate++;
                            System.out.println("Update Stock");   
                            System.out.println("Periode ID : " + periodeid);        
                            //findstock.setItem(Integer.parseInt(article.get(2)));
                            findstock.setItem(Integer.parseInt(article.get(0)));
                            
                            //findstock.setQty(Double.parseDouble(article.get(4)));   
                            findstock.setQty(Double.parseDouble(article.get(7)));   
                            
                            //findstock.setUnitcost(Double.parseDouble(article.get(6)));                        
                            findstock.setUnitcost(Double.parseDouble(article.get(10)));                        
                            
                            System.out.println("Qty : " + stock.getQty());
                            findstock.setUpdateddate(new Date(System.currentTimeMillis()));
                            findstock.setUpdatedby(authbean.getUsername());                
                            this.updateStock(findstock);
                        }                   
                       
                    }                           
                }else{
                    rowSkip++;
                }
                rowTotal++;                             
            }
            stockbean.loadData();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Total Upload : " + rowTotal + " (Insert:" + rowInsert + ",Update:" + rowUpdate + ",Skip: " + rowSkip + ")","");
            FacesContext.getCurrentInstance().addMessage(null, msg);                
        }catch(IOException ex){
            System.out.println("Created File Error!");
            FacesMessage error = new FacesMessage("The files were not uploaded");
            FacesContext.getCurrentInstance().addMessage(null, error);                                
        }                   
    }

    private Stock findStock(String siteid,String periodeid,String articleid){        
        EntityManager em = this.getEntityManager();
        String strSQL = "SELECT s FROM Stock s WHERE s.site.siteid=:siteid AND s.periode.periodeid=:periodeid AND s.articleid=:articleid";                
        try{
            Query query = em.createQuery(strSQL)
                    .setParameter("siteid", siteid)
                    .setParameter("periodeid", periodeid)
                    .setParameter("articleid", articleid);
            query.setHint("eclipselink.refresh", "true");
            return (Stock) query.getSingleResult();
        }catch(NoResultException ex){
            System.out.println("Article Not Found");
            return null;
        }finally{
            em.close();
        }        
    }
    
    private void insertStock(Stock o){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            em.persist(o);
            em.getTransaction().commit();
        }catch(Exception ex){
            ex.printStackTrace();
            em.getTransaction().rollback();            
        }finally{
            em.close();
        }
    }
    
    private void updateStock(Stock o){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{            
            em.merge(o);
            em.getTransaction().commit();
        }catch(Exception ex){
            ex.printStackTrace();
            em.getTransaction().rollback();            
        }finally{
            em.close();
        }        
    }
    
    private AuthBean getAuthBean(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application application = facesContext.getApplication();
        ValueBinding binding = application.createValueBinding("#{authBean}");
        AuthBean authbean =  (AuthBean) binding.getValue(facesContext);
        return authbean;
    }
    
    private StockBean getStockBean(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application application = facesContext.getApplication();
        ValueBinding binding = application.createValueBinding("#{stockBean}");
        StockBean stockbean =  (StockBean) binding.getValue(facesContext);
        return stockbean;
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
    
}
