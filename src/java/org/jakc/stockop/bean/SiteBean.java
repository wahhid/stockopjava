/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.bean;

import java.util.Date;
import java.util.List;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.jakc.stockop.datamodel.LazySiteDataModel;
import org.jakc.stockop.entity.Roles;
import org.jakc.stockop.entity.Site;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class SiteBean {
        
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("StockOpPU");
    
    private List<Site> all;
    private LazyDataModel<Site> lazyModel;
    private Site site = new Site();
    private Site selectedSite = new Site();

    //Security        
    private String formid="F0001";
    private boolean allowcreate=false;
    private boolean allowread=false;
    private boolean allowupdate=false;
    private boolean allowdelete=false;
    private boolean allowdownload=false;
    private boolean allowupload=false;      
    
    //filter
    private boolean filterSiteid=false;
    private String siteid;
    
    /** Creates a new instance of SiteBean */
    public SiteBean() {
        this.loadData();
    }

    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }
    

    public String add(){
        this.site = new Site();
        return "siteadd?faces-redirect=true";
    } 
    
    public String edit(){
        if(this.selectedSite == null){
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please select one row!","");
            FacesContext.getCurrentInstance().addMessage(null, error);  
            return null;
        }
        return "siteedit?faces-redirect=true";
    }
    
    public String cancel(){
        return "sitelist?faces-redirect=true";
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
    
    public String saveAdd(){
        Site site = this.findSite(this.site.getSiteid());
        if(site != null){
            if(site.getDeleted()){
                FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Site was deleted and already activated","");
                FacesContext.getCurrentInstance().addMessage(null, error);        
                site.setDeleted(false);
                this.selectedSite = site;
                this.editSite();                
            }else{
                FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Site already exist","");
                FacesContext.getCurrentInstance().addMessage(null, error);                           
            }
            return null;
        }else{
            return this.addSite();
        }

    }
    
    public String saveEdit(){
        return this.editSite();
    }
    
    public void delete(){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            this.selectedSite.setUpdateddate(new Date(System.currentTimeMillis()));
            this.selectedSite.setUpdatedby(this.getAuthBean().getUsername());            
            this.selectedSite.setDeleted(true);
            em.merge(this.selectedSite);
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
    
    public void loadData(){
        System.out.println("Load Data...");
        List<Site> sites = this.generateList();       
        this.lazyModel =  new LazySiteDataModel(sites,this.emf);        
    }
    
    public List<Site> generateList(){
        EntityManager em = getEntityManager();                 
        CriteriaBuilder builder = em.getCriteriaBuilder();        
        CriteriaQuery<Site> query = builder.createQuery(Site.class);
        Root<Site> oRoot = query.from(Site.class);    
        Predicate criteria = builder.conjunction();   
        criteria = builder.and(criteria,builder.equal(oRoot.get("deleted"), false)); 
        if(filterSiteid){
            criteria = builder.and(criteria,builder.equal(oRoot.get("siteid"), this.siteid));                   
        }
        query.where(criteria);        
        Query q = em.createQuery(query);
        q.setHint("eclipselink.refresh", "true");
        List<Site> sites = q.getResultList();
        return sites;        
    }
    
    public List<Site> getAll(){
        EntityManager em = this.getEntityManager();
        try{
            return em.createNamedQuery("Site.findByDeleted").setParameter("deleted", false).getResultList();
        }finally{
            em.close();
        }
    }
    
    public Site findSite(String siteid){
        EntityManager em = this.getEntityManager();        
        try{
            Query query = em.createNamedQuery("Site.findBySiteid").setParameter("siteid", siteid);
            query.setHint("eclipselink.refresh", "true");
            return (Site) query.getSingleResult();
        }catch(NoResultException ex){
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, error);                                
            return null;
        }finally{
            em.close();
        }
            
    }
    
    public String addSite(){
        EntityManager em = this.getEntityManager();                
        em.getTransaction().begin();
        try{
            this.site.setCreateddate(new Date(System.currentTimeMillis()));
            this.site.setCreatedby(this.getAuthBean().getUsername());
            this.site.setUpdateddate(new Date(System.currentTimeMillis()));
            this.site.setUpdatedby(this.getAuthBean().getUsername());
            em.persist(this.site);
            em.getTransaction().commit();
            this.loadData();
            return "sitelist?faces-redirect=true";
        }catch(Exception ex){
            em.getTransaction().rollback();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, error);               
            return null;
        }finally{
            em.close();
        }         
    }
    
    public String editSite(){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            this.selectedSite.setUpdateddate(new Date(System.currentTimeMillis()));
            this.selectedSite.setUpdatedby(this.getAuthBean().getUsername());            
            em.merge(this.selectedSite);
            em.getTransaction().commit();
            this.loadData();
            return "sitelist?faces-redirect=true";
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
    
    public Site getSelectedSite() {
        return selectedSite;
    }

    public void setSelectedSite(Site selectedSite) {
        this.selectedSite = selectedSite;
    }

    public Site getSite() {        
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
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

    public boolean isFilterSiteid() {
        return filterSiteid;
    }

    public void setFilterSiteid(boolean filterSiteid) {
        this.filterSiteid = filterSiteid;
    }

    public LazyDataModel<Site> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Site> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public String getSiteid() {
        return siteid;
    }

    public void setSiteid(String siteid) {
        this.siteid = siteid;
    }
    
    
    
}
