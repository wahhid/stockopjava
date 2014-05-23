/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.bean;

import java.util.Date;
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
import org.jakc.stockop.datamodel.LazyArticleDataModel;
import org.jakc.stockop.entity.Product;
import org.jakc.stockop.entity.Roles;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class ArticleBean {
        
    private BackingModel bm;
    private Product object = new Product();
    private Product selectedObject = new Product();
    private LazyDataModel<Product> lazyModel;
            
    private List<Product> all;
    private String siteid;
    private int periodeid;
    
    //Security        
    private String formid="F0001";
    private boolean allowcreate=false;
    private boolean allowread=false;
    private boolean allowupdate=false;
    private boolean allowdelete=false;
    private boolean allowdownload=false;
    private boolean allowupload=false;       
    
    //Filter
    private HashMap filterList;             
    private boolean filterEan=false;
    private String ean;
    private boolean filterArticleid=false;
    private String articleid;
    private boolean filterCategoryid=false;
    private String categoryid;
    private boolean filterProductname=false;
    private String productname;
            
    
    /** Creates a new instance of ArticleBean */
    public ArticleBean() {
        this.bm = new BackingModel();
        this.loadData();
    }
    
    private EntityManager getEntityManager(){
        return this.bm.getEmf().createEntityManager();
    }
    
    public String add(){
        this.object = new Product();
        return "productadd?faces-redirect=true";
    }
    
    public String edit(){
        return "productedit?faces-redirect=true";
    }
    
    public String cancel(){
        return "productlist?faces-redirect=true";
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
       this.filterList = new HashMap();
       try{
            System.out.println("Load Data...");            
            //Define Filter List            
            if(filterEan){
                this.filterList.put("ean", this.ean);
            }
            if(filterArticleid){
                this.filterList.put("articleid", this.articleid);
            }
            if(filterCategoryid){
                this.filterList.put("categoryid", this.categoryid);
            }            
            if(filterProductname){
                this.filterList.put("productname", this.productname);
            }
            
            this.lazyModel =  new LazyArticleDataModel(this,this.bm);                 
        }catch(Exception ex){            
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, msg);                      
        }        
    }
    
//    private List<Product> generateArticles(){
//        EntityManager em = getEntityManager();                 
//        CriteriaBuilder builder = em.getCriteriaBuilder();        
//        CriteriaQuery<Product> query = builder.createQuery(Product.class);
//        Root<Product> oRoot = query.from(Product.class);        
//        
//        Predicate criteria = builder.conjunction();                
//        if(filterEan){
//            criteria = builder.and(criteria,builder.equal(oRoot.get("ean"), this.ean));
//        }
//        
//        if(filterArticleid){           
//            criteria = builder.and(criteria,builder.equal(oRoot.get("articleid"), this.articleid));
//        }
//        
//        if(filterCategoryid){                        
//            criteria = builder.and(criteria,builder.like(oRoot.<String>get("categoryid"), this.categoryid + "%"));
//        }
//        
//        if(filterProductname){
//            criteria = builder.and(criteria,builder.like(oRoot.<String>get("productname"), "%" + this.productname + "%"));
//        }
//        
//        query.where(criteria);
//        Query q = em.createQuery(query);
//        q.setHint("eclipselink.refresh", "true");
//        List<Product> products = q.getResultList();
//        return products;        
//        
//    }    

    public String saveAdd(){
        Product product = this.findProduct(this.object.getEan(),this.object.getArticleid());
        if(product != null){
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Article Already Exist","");
            FacesContext.getCurrentInstance().addMessage(null, error);                           
            return null;
        }else{
            return this.addProduct();
        }
    }
    
    public String saveEdit(){
        return this.editProduct();
    }
    
    public void delete(){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            this.selectedObject.setUpdateddate(new Date(System.currentTimeMillis()));
            this.selectedObject.setUpdatedby("007290");            
            this.selectedObject.setDeleted(true);
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
    
    public List<Product> getAll(){
        EntityManager em = this.getEntityManager();
        try{
            //if(this.all == null){
                Query query = em.createNamedQuery("Product.findByDeleted").setParameter("deleted", false);
                query.setHint("eclipselink.refresh", "true");
                this.all = query.getResultList();
            //}           
        }catch(NoResultException ex){
            this.all = null;
        }finally{
            em.close();
        }
        return this.all;
    }

    private Product findProduct(String ean,String articleid){
        EntityManager em = this.getEntityManager();
        String strSQL = "SELECT p FROM Product p WHERE p.ean=:ean AND p.articleid=:articleid";
        try{
            Query query = em.createQuery(strSQL)
                    .setParameter("ean", ean)
                    .setParameter("articleid",articleid);
            query.setHint("eclipselink.refresh", "true");
            return (Product) query.getSingleResult();
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
    }
    
    private String addProduct(){
        EntityManager em = this.getEntityManager();                
        em.getTransaction().begin();
        try{
            this.object.setCreateddate(new Date(System.currentTimeMillis()));
            this.object.setCreatedby("007290");
            this.object.setUpdateddate(new Date(System.currentTimeMillis()));
            this.object.setUpdatedby("007290");
            em.persist(this.object);
            em.getTransaction().commit();
            this.loadData();
            return "productlist?faces-redirect=true";
        }catch(Exception ex){
            em.getTransaction().rollback();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, error);               
            return null;
        }finally{
            em.close();
        }                  
    }
    
    private String editProduct(){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            this.selectedObject.setUpdateddate(new Date(System.currentTimeMillis()));
            this.selectedObject.setUpdatedby("007290");            
            em.merge(this.selectedObject);
            em.getTransaction().commit();
            this.loadData();
            return "productlist?faces-redirect=true";
        }catch(Exception ex){
            em.getTransaction().rollback();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, error);               
            return null;
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
    
    
    public Product getObject() {
        return object;
    }

    public void setObject(Product object) {
        this.object = object;
    }

    public Product getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Product selectedObject) {
        this.selectedObject = selectedObject;
    }

    public int getPeriodeid() {
        return periodeid;
    }

    public void setPeriodeid(int periodeid) {
        this.periodeid = periodeid;
    }

    public String getSiteid() {
        return siteid;
    }

    public void setSiteid(String siteid) {
        this.siteid = siteid;
    }

    public boolean isAllowcreate() {
        this.getallowCreate();
        return allowcreate;
    }

    public void setAllowcreate(boolean allowcreate) {
        this.allowcreate = allowcreate;
    }

    public boolean isAllowdelete() {
        this.getallowDelete();
        return allowdelete;
    }

    public void setAllowdelete(boolean allowdelete) {
        this.allowdelete = allowdelete;
    }

    public boolean isAllowdownload() {
        this.getallowDownload();
        return allowdownload;
    }

    public void setAllowdownload(boolean allowdownload) {
        this.allowdownload = allowdownload;
    }

    public boolean isAllowread() {
        this.getallowRead();
        return allowread;
    }

    public void setAllowread(boolean allowread) {
        this.allowread = allowread;
    }

    public boolean isAllowupdate() {
        this.getallowUpdate();
        return allowupdate;
    }

    public void setAllowupdate(boolean allowupdate) {
        this.allowupdate = allowupdate;
    }

    public boolean isAllowupload() {
        this.getallowUpload();
        return allowupload;
    }

    public void setAllowupload(boolean allowupload) {
        this.allowupload = allowupload;
    }

    public LazyDataModel<Product> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Product> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public String getArticleid() {
        return articleid;
    }

    public void setArticleid(String articleid) {
        this.articleid = articleid;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public boolean isFilterArticleid() {
        return filterArticleid;
    }

    public void setFilterArticleid(boolean filterArticleid) {
        this.filterArticleid = filterArticleid;
    }

    public boolean isFilterCategoryid() {
        return filterCategoryid;
    }

    public void setFilterCategoryid(boolean filterCategoryid) {
        this.filterCategoryid = filterCategoryid;
    }

    public boolean isFilterEan() {
        return filterEan;
    }

    public void setFilterEan(boolean filterEan) {
        this.filterEan = filterEan;
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

    public HashMap getFilterList() {
        return filterList;
    }

    public void setFilterList(HashMap filterList) {
        this.filterList = filterList;
    }

    

    
}
