/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.jakc.stockop.entity.Transstock;
import java.util.List;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.jakc.stockop.datamodel.LazyTransstockDataModel;
import org.jakc.stockop.datamodel.LazyTranstockdetailDataModel;
import org.jakc.stockop.entity.Roles;
import org.jakc.stockop.entity.Transtockdetail;
import org.jakc.stockop.entity.Transtockdetailtemp;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class TransstockBean {

    private EntityManagerFactory emf  = Persistence.createEntityManagerFactory("StockOpPU");
    
    private List<Transstock> all;
    private List<Transtockdetail> alldetail;
    private LazyDataModel<Transstock> lazyModel;
    private LazyDataModel<Transtockdetail> lazyModelDetail;
    
    private Transstock object = new Transstock();
    private Transstock selectedObject = new Transstock();
    
    private Transtockdetail tdetail = new Transtockdetail();
    private Transtockdetail selectedTdetail = new Transtockdetail();               
    
    //Security        
    private String formid="F0008";
    private boolean allowcreate=false;
    private boolean allowread=false;
    private boolean allowupdate=false;
    private boolean allowdelete=false;
    private boolean allowdownload=false;
    private boolean allowupload=false;       
    
    //Filter
    
    private boolean filterPeriodeid=false;
    private String periodeid;
    private boolean filterGondolaid=false;
    private String gondolaid;
    
    //Filter transtokdetail
    
    
    private boolean filterProductname=false;
    private String productname;    
    private boolean filterProductid=false;
    private String productid;   
    
    //Render
   
    private boolean allowclosesession=false;
    
    
    /** Creates a new instance of TransstockBean */
    public TransstockBean() {        
        this.loadData();
    }

    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }

    public String back(){
        return "/faces/transstock/transstocklist?faces-redirect=true";
    }

    public String detail(){
        this.loadDataDetail();
        return "/faces/transstock/transtockdetaillist?faces-redirect=true";
    }
    
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
            this.allowupdate = false;
        }
    }
    
    
    public void loadData(){        
        System.out.println("Load Data...");
        List<Transstock> transstocks = this.generateList();       
        System.out.println("Row Number : " + transstocks.size());
        this.lazyModel =  new LazyTransstockDataModel(transstocks,this.emf);
    }
    
    private List<Transstock> generateList(){
        AuthBean authbean = this.getAuthBean();
        EntityManager em = getEntityManager();                 
        CriteriaBuilder builder = em.getCriteriaBuilder();        
        CriteriaQuery<Transstock> query = builder.createQuery(Transstock.class);
        Root<Transstock> oRoot = query.from(Transstock.class);              
        Predicate criteria = builder.conjunction();        
        criteria = builder.and(criteria,builder.equal(oRoot.get("site").get("siteid"), authbean.getSiteid()));                    
        if(filterPeriodeid){                                    
            criteria = builder.and(criteria,builder.equal(oRoot.get("periode").get("periodeid"), this.periodeid));            
        }
        if(filterGondolaid){
            criteria = builder.and(criteria,builder.equal(oRoot.get("gondola").get("gondolaid"), authbean.getSiteid()+this.gondolaid));            
        }        
        
        query.where(criteria);        
        System.out.println(query);
        Query q = em.createQuery(query);
        q.setHint("eclipselink.refresh", "true");   
        List<Transstock> transstocks = q.getResultList();
        return transstocks;        
    }     
    
    public void loadDataDetail(){        
        System.out.println("Load Data...");
        List<Transtockdetail> transtockdetails = this.generateListDetail();       
        System.out.println("Row Number : " + transtockdetails.size());
        this.lazyModelDetail =  new LazyTranstockdetailDataModel(transtockdetails,this.emf);
    }
    
    private List<Transtockdetail> generateListDetail(){        
        EntityManager em = getEntityManager();                 
        CriteriaBuilder builder = em.getCriteriaBuilder();        
        CriteriaQuery<Transtockdetail> query = builder.createQuery(Transtockdetail.class);
        Root<Transtockdetail> oRoot = query.from(Transtockdetail.class);              
        Predicate criteria = builder.conjunction();                
        criteria = builder.and(criteria,builder.equal(oRoot.get("transstock").<String>get("transid"), this.selectedObject.getTransid()));                                                           
        if(this.filterProductid){
            criteria = builder.and(criteria,builder.equal(oRoot.<String>get("productid"), this.productid));  
        }
        query.where(criteria);                        
        query.orderBy(builder.asc(oRoot.get("sequence")));        
        Query q = em.createQuery(query);
        q.setHint("eclipselink.refresh", "true");   
        List<Transtockdetail> transtockdetails = q.getResultList();
        return transtockdetails;        
    }         
    
    public List<Transtockdetail> getAlldetail() {
        EntityManager em = this.getEntityManager();
        this.alldetail = null;
        try{
            if(this.alldetail == null){
                this.alldetail = em.createNamedQuery("Transtockdetail.findByTransid").setParameter("transid", this.selectedObject.getTransid()).getResultList();
            }
        }catch(NoResultException ex){
            this.alldetail = null;
        }finally{
            em.close();
        }        
        return alldetail;
    }
    
        
    public void setAll(List<Transstock> all) {
        this.all = all;
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
    
    public Transstock getObject() {
        return object;
    }

    public void setObject(Transstock object) {
        this.object = object;
    }

    public Transstock getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Transstock selectedObject) {
        this.selectedObject = selectedObject;
    }

    public boolean isAllowcreate() {
        return allowcreate;
    }

    public void setAllowcreate(boolean allowcreate) {
        this.allowcreate = allowcreate;
    }

    public boolean isAllowdelete() {
        return allowdelete;
    }

    public void setAllowdelete(boolean allowdelete) {
        this.allowdelete = allowdelete;
    }

    public boolean isAllowdownload() {
        return allowdownload;
    }

    public void setAllowdownload(boolean allowdownload) {
        this.allowdownload = allowdownload;
    }

    public boolean isAllowread() {
        return allowread;
    }

    public void setAllowread(boolean allowread) {
        this.allowread = allowread;
    }

    public boolean isAllowupdate() {
        return allowupdate;
    }

    public void setAllowupdate(boolean allowupdate) {
        this.allowupdate = allowupdate;
    }

    public boolean isAllowupload() {
        return allowupload;
    }

    public void setAllowupload(boolean allowupload) {
        this.allowupload = allowupload;
    }

    public String getGondolaid() {
        return gondolaid;
    }

    public void setGondolaid(String gondolaid) {
        this.gondolaid = gondolaid;
    }

    public String getPeriodeid() {
        return periodeid;
    }

    public void setPeriodeid(String periodeid) {
        this.periodeid = periodeid;
    }

    public LazyDataModel<Transstock> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Transstock> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public boolean isFilterGondolaid() {
        return filterGondolaid;
    }

    public void setFilterGondolaid(boolean filterGondolaid) {
        this.filterGondolaid = filterGondolaid;
    }

    public boolean isFilterPeriodeid() {
        return filterPeriodeid;
    }

    public void setFilterPeriodeid(boolean filterPeriodeid) {
        this.filterPeriodeid = filterPeriodeid;
    }

    public LazyDataModel<Transtockdetail> getLazyModelDetail() {
        return lazyModelDetail;
    }

    public void setLazyModelDetail(LazyDataModel<Transtockdetail> lazyModelDetail) {
        this.lazyModelDetail = lazyModelDetail;
    }

    public boolean isAllowclosesession() {
        return allowclosesession;
    }

    public void setAllowclosesession(boolean allowclosesession) {
        this.allowclosesession = allowclosesession;
    }

    public boolean isFilterProductname() {
        return filterProductname;
    }

    public void setFilterProductname(boolean filterProductname) {
        this.filterProductname = filterProductname;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public Transtockdetail getSelectedTdetail() {
        return selectedTdetail;
    }

    public void setSelectedTdetail(Transtockdetail selectedTdetail) {
        this.selectedTdetail = selectedTdetail;
    }

    public Transtockdetail getTdetail() {
        return tdetail;
    }

    public void setTdetail(Transtockdetail tdetail) {
        this.tdetail = tdetail;
    }

    public boolean isFilterProductid() {
        return filterProductid;
    }

    public void setFilterProductid(boolean filterProductid) {
        this.filterProductid = filterProductid;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    
    
    
    

    
    
    
}
