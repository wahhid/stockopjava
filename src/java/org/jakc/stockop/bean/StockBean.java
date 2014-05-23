/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.bean;

import java.util.HashMap;
import java.util.List;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.jakc.stockop.backing.BackingModel;
import org.jakc.stockop.datamodel.LazyStockDataModel;
import org.jakc.stockop.entity.Roles;
import org.jakc.stockop.entity.Stock;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class StockBean {

    private BackingModel bm;   
    private List<Stock> uploads;
    private LazyDataModel<Stock> lazyModel;       
 
    private Stock object;
    private Stock selectedObject;
    private Stock selectedUpload;                
    //Filter
    private HashMap filterList;
    
    //Security        
    private String formid="F0007";
    private boolean allowcreate=false;
    private boolean allowread=false;
    private boolean allowupdate=false;
    private boolean allowdelete=false;
    private boolean allowdownload=false;
    private boolean allowupload=false;      
   
    //Pagination
    
    private int rowCount=0;
    private int pages=0;
    
    
    //filter
    private boolean filterPeriodeid = false;
    private String periodeid;
    private boolean filterArticleid = false;
    private String articleid;
    
    
    
    
    /** Creates a new instance of StockBean */
    public StockBean() {
        this.bm = new BackingModel();
        this.loadData();
    }
    
    private EntityManager getEntityManager(){
        return this.bm.getEmf().createEntityManager();
    }
    
    public void loadData(){
        this.filterList = new HashMap();
        try{
            System.out.println("Load Data...");            
            //Define Filter List
            
            if(this.filterPeriodeid){
                this.filterList.put("periode.periodeid",this.getAuthBean().getSiteid()+this.periodeid);               
                //this.filterList.put("periode.periodeid",this.periodeid);               
            }                      
            
            if(this.filterArticleid){
                this.filterList.put("articleid",this.articleid); 
            }
            
            this.filterList.put("site.siteid",this.getAuthBean().getSiteid());            
            this.lazyModel =  new LazyStockDataModel(this,this.bm);                 
        }catch(Exception ex){            
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, msg);                      
        }        
    }
    

    
//    private List<Stock> generateStocks(){
//        AuthBean authbean = this.getAuthBean();
//        EntityManager em = getEntityManager();   
//        CriteriaBuilder builder = em.getCriteriaBuilder();
//        CriteriaQuery<Stock> query = builder.createQuery(Stock.class);
//        Root<Stock> oRoot = query.from(Stock.class);        
//        Predicate criteria = builder.conjunction();        
//        criteria = builder.and(criteria,builder.equal(oRoot.get("site").get("siteid"), authbean.getSiteid()));                        
//        
//        if(filterPeriodeid){
//            criteria = builder.and(criteria,builder.equal(oRoot.get("periode").get("periodeid"), this.periodeid));                    
//        }
//        
//        if(filterArticleid){
//            criteria = builder.and(criteria,builder.equal(oRoot.get("articleid"), this.articleid));
//        }           
//        query.where(criteria);
//        List<Stock> stocks = em.createQuery(query).getResultList();
//        return stocks;                                
//    }
//    
    
      
        
    private void getallowCreate(){
        AuthBean ab = this.getAuthBean();
        String employeetypeid = ab.getEmployeetype();
        Roles roles = this.getSecurity(formid, employeetypeid);
        if(roles != null){
            this.allowcreate = roles.getCreaterecord();
        }else{
            this.allowcreate = false;
        }
    }
    
    private void getallowRead(){
        AuthBean ab = this.getAuthBean();
        String employeetypeid = ab.getEmployeetype();
        Roles roles = this.getSecurity(formid, employeetypeid);
        if(roles != null){
            this.allowread = roles.getReadrecord();
        }else{
            this.allowread = false;
        }
    }
    
    private void getallowUpdate(){
        AuthBean ab = this.getAuthBean();
        String employeetypeid = ab.getEmployeetype();
        Roles roles = this.getSecurity(formid, employeetypeid);
        if(roles != null){
            this.allowupdate = roles.getUpdaterecord();
        }else{
            this.allowupdate = false;
        }
    }
    
    private void getallowDelete(){
        AuthBean ab = this.getAuthBean();
        String employeetypeid = ab.getEmployeetype();
        Roles roles = this.getSecurity(formid, employeetypeid);
        if(roles != null){
            this.allowdelete = roles.getDeleterecord();
        }else{
            this.allowdelete = false;
        }
    }
       
    private void getallowDownload(){
        AuthBean ab = this.getAuthBean();
        String employeetypeid = ab.getEmployeetype();
        Roles roles = this.getSecurity(formid, employeetypeid);
        if(roles != null){
            this.allowdownload = roles.getDownload();
        }else{
            this.allowdownload = false;
        }
    }
    
    private void getallowUpload(){
        AuthBean ab = this.getAuthBean();
        String employeetypeid = ab.getEmployeetype();
        Roles roles = this.getSecurity(formid, employeetypeid);
        if(roles != null){
            this.allowupload = roles.getUpload();
        }else{
            this.allowupload = false;
        }
    }    

    
    public void saveAdd(){
        EntityManager em = this.getEntityManager();                
        em.getTransaction().begin();
        try{
            em.persist(this.object);
            em.getTransaction().commit();
            this.loadData();
        }catch(Exception ex){
            em.getTransaction().rollback();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, error);               
        }finally{
            em.close();
        }          
    }
    
    public void saveEdit(){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{       
            em.merge(this.selectedObject);
            em.getTransaction().commit();
            this.loadData();
        }catch(Exception ex){
            em.getTransaction().rollback();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, error);               
        }finally{
            em.close();
        }          
    }
    
    public void delete(){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            em.merge(this.selectedObject);
            em.getTransaction().commit();
        }catch(Exception ex){
            em.getTransaction().rollback();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, error);               
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
    
    private Roles getSecurity(String formid, String employeetypeid){
        EntityManager em = this.getEntityManager();
        String strSQL = "SELECT r FROM Roles r WHERE r.formid=:formid AND r.employeetypeid=:employeetypeid";
        try{
            Query query = em.createQuery(strSQL)
                    .setParameter("formid", formid)
                    .setParameter("employeetypeid", employeetypeid);
            query.setHint("eclipselink.refresh", "true");
            return (Roles) query.getSingleResult();
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
    }
    
    public Stock getObject() {
        return object;
    }

    public void setObject(Stock object) {
        this.object = object;
    }

    public Stock getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Stock selectedObject) {
        this.selectedObject = selectedObject;
    }

    public String getPeriodeid() {
        return periodeid;
    }

    public void setPeriodeid(String periodeid) {
        this.periodeid = periodeid;
    }
  
    public Stock getSelectedUpload() {
        return selectedUpload;
    }

    public void setSelectedUpload(Stock selectedUpload) {
        this.selectedUpload = selectedUpload;
    }

    public boolean getAllowcreate() {
        this.getallowCreate();
        return allowcreate;
    }

    public void setAllowcreate(boolean allowcreate) {
        this.allowcreate = allowcreate;
    }

    public boolean getAllowdelete() {
        this.getallowDelete();
        return allowdelete;
    }

    public void setAllowdelete(boolean allowdelete) {
        this.allowdelete = allowdelete;
    }

    public boolean getAllowdownload() {
        this.getallowDownload();
        return allowdownload;
    }

    public void setAllowdownload(boolean allowdownload) {
        this.allowdownload = allowdownload;
    }

    public boolean getAllowread() {
        this.getallowRead();
        return allowread;
    }

    public void setAllowread(boolean allowread) {
        this.allowread = allowread;
    }

    public boolean getAllowupdate() {
        this.getallowUpdate();
        return allowupdate;
    }

    public void setAllowupdate(boolean allowupdate) {
        this.allowupdate = allowupdate;
    }

    public boolean getAllowupload() {
        this.getallowUpload();
        return allowupload;
    }

    public void setAllowupload(boolean allowupload) {
        this.allowupload = allowupload;
    }
  
    public LazyDataModel<Stock> getLazyModel() {        
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Stock> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public boolean getFilterArticleid() {
        return filterArticleid;
    }

    public void setFilterArticleid(boolean filterArticleid) {
        this.filterArticleid = filterArticleid;
    }

    public boolean getFilterPeriodeid() {
        return filterPeriodeid;
    }

    public void setFilterPeriodeid(boolean filterPeriodeid) {
        this.filterPeriodeid = filterPeriodeid;
    }

    public String getArticleid() {
        return articleid;
    }

    public void setArticleid(String articleid) {
        this.articleid = articleid;
    }

    public HashMap getFilterList() {
        return filterList;
    }

    public void setFilterList(HashMap filterList) {
        this.filterList = filterList;
    }

                    
}
