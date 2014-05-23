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
import org.jakc.stockop.entity.Product;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class DownloadArticleBean {
    private EntityManagerFactory emf  = Persistence.createEntityManagerFactory("StockOpPU");    
    private static final int BUFFER_SIZE = 6124;
    private int rowInsert=0;
    private int rowUpdate=0;
    private int rowSkip=0;
    private int rowTotal=0;
    /** Creates a new instance of DownloadArticleBean */
    public DownloadArticleBean() {
    
    }
    
    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }
    
    public void processFileUpload(FileUploadEvent event){        
        AuthBean authbean = this.getAuthBean();
        ArticleBean articlebean = this.getArticleBean();
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
            
            CsvReader article = new CsvReader(fullPath);
            article.setDelimiter(';');
            while(article.readRecord()){           
                if(article.getColumnCount() > 5){
                    Product product = this.findArticle(article.get(0));
                    if(product != null){
                        rowUpdate++;
                        Product o = product;                               
                        o.setEan(article.get(0));
                        o.setArticleid(article.get(1));
                        o.setCategoryid(article.get(2));
                        o.setProductname(article.get(3));
                        o.setUom(article.get(4));
                        o.setDeleted(false);
                        o.setUpdateddate(new Date(System.currentTimeMillis()));
                        o.setUpdatedby(authbean.getUsername());             
                        System.out.println("Update : " + article.get(0));      
                        this.updateArticle(o);
                    }else{
                        rowInsert++;
                        Product o = new Product();                               
                        o.setEan(article.get(0));
                        o.setArticleid(article.get(1));
                        o.setCategoryid(article.get(2));
                        o.setProductname(article.get(3));
                        o.setUom(article.get(4));
                        o.setDeleted(false);
                        o.setCreateddate(new Date(System.currentTimeMillis()));
                        o.setCreatedby(authbean.getUsername());
                        o.setUpdateddate(new Date(System.currentTimeMillis()));
                        o.setUpdatedby(authbean.getUsername());             
                        System.out.println("Create : " + article.get(0));                              
                        this.insertArticle(o);
                    }                                
                }else{
                    rowSkip++;
                }                   
                rowTotal++;
            }            
            articlebean.loadData();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Total Upload : " + rowTotal + " (Insert:" + rowInsert + ",Update:" + rowUpdate + ",Skip:" + rowSkip  + ")","");
            FacesContext.getCurrentInstance().addMessage(null, msg);                       
        }catch(IOException ex){
            ex.printStackTrace();
            System.out.println("IOException : Created File Error!");
            FacesMessage error = new FacesMessage("The files were not uploaded");
            FacesContext.getCurrentInstance().addMessage(null, error);                                
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println("Exception : Created File Error!");
            FacesMessage error = new FacesMessage("The files were not uploaded");
            FacesContext.getCurrentInstance().addMessage(null, error);                  
        }                   
    }
    
    private Product findArticle(String ean){
        EntityManager em = this.getEntityManager();
        String strSQL = "SELECT p FROM Product p WHERE p.ean=:ean";                
        try{
            Query query = em.createQuery(strSQL)
                    .setParameter("ean", ean);
            query.setHint("eclipselink.refresh", "true");
            return (Product) query.getSingleResult();
        }catch(NoResultException ex){
            System.out.println("Article Not Found");
            return null;
        }finally{
            em.close();
        }        
    }
    
    private void insertArticle(Product o){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            em.persist(o);
            em.getTransaction().commit();
        }catch(Exception ex){
            em.getTransaction().rollback();
            
        }finally{
            em.close();
        }
    }
    
    private void updateArticle(Product o){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            em.merge(o);
            em.getTransaction().commit();
        }catch(Exception ex){
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
    
    private ArticleBean getArticleBean(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application application = facesContext.getApplication();
        ValueBinding binding = application.createValueBinding("#{articleBean}");
        ArticleBean articlebean =  (ArticleBean) binding.getValue(facesContext);
        return articlebean;
    }    
}
