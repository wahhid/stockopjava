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
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.jakc.stockop.backing.BackingModel;
import org.jakc.stockop.datamodel.LazyGondolaDataModel;
import org.jakc.stockop.entity.Gondola;
import org.jakc.stockop.entity.Roles;
import org.jakc.stockop.entity.Site;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class GondolaBean {

    private BackingModel bm;
    private Gondola object = new Gondola();
    private Gondola selectedObject;
    private LazyDataModel<Gondola> lazyModel;
    
        
    //Security        
    private String formid="F0003";
    private boolean allowcreate=false;
    private boolean allowread=false;
    private boolean allowupdate=false;
    private boolean allowdelete=false;
    private boolean allowdownload=false;
    private boolean allowupload=false;    
       
    //Filter
    private HashMap filterList;
    
    
    private boolean filterGondolaid;
    private String gondolaid;    
  
    
    /** Creates a new instance of GondolaBean */
    public GondolaBean() {
        this.bm = new BackingModel();
        this.loadData();
    }

    private EntityManager getEntityManager(){
        return this.bm.getEmf().createEntityManager();
    }
    
    public String add(){
        this.object = new Gondola();
        this.object.setSite(new Site());
        return "gondolaadd?faces-redirect=true";
    }
    public String edit(){
        return "gondolaedit?faces-redirect=true";
    }
    
    public String cancel(){
        return "gondolalist?faces-redirect=true";
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
    
    public void getallowUpload(){
        AuthBean ab = this.getAuthBean();
        String employeetypeid = ab.getEmployeetype();
        Roles roles = this.getSecurity(formid, employeetypeid);
        if(roles != null){
            this.allowupload = roles.getUpload();
        }else{
            this.allowupload = false;
        }
    }
            
    
    public String saveAdd(){
        AuthBean authbean = this.getAuthBean();
        Gondola gondola = this.findGondola(authbean.getSiteid() + this.object.getGondolaname());
        if(gondola != null){
            if(gondola.getDeleted() == true){
                FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Gondola was deleted and already activated","");
                FacesContext.getCurrentInstance().addMessage(null, error);        
                gondola.setDeleted(false);
                this.selectedObject = gondola;
                this.editGondola();
            }else{
                FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Gondola already exist","");
                FacesContext.getCurrentInstance().addMessage(null, error);                     
            }
            return null;
        }else{
            return this.addGondola();
        }
        
    }
    
    public String saveEdit(){
        return this.editGondola();
    }
    
    public void delete(){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            this.selectedObject.setUpdateddate(new Date(System.currentTimeMillis()));
            this.selectedObject.setUpdatedby(this.getAuthBean().getUsername());            
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
        
    public void loadData(){
        this.filterList = new HashMap();
        try{
            System.out.println("Load Data...");            
            //Define Filter List
            if(this.filterGondolaid){
                this.filterList.put("gondolaid",this.getAuthBean().getSiteid()+this.gondolaid);               
            }                                                
            this.filterList.put("site.siteid",this.getAuthBean().getSiteid());
            this.lazyModel =  new LazyGondolaDataModel(this,this.bm);                 
        }catch(Exception ex){            
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, msg);                      
        }        
    }
    
//    public void loadData(){        
//        System.out.println("Load Data...");
//        List<Gondola> gondolas = this.generateList();          
//        System.out.println("Row Count : " + gondolas.size());
//        this.lazyModel =  new LazyGondolaDataModel(gondolas,this.emf);
//    }
    
//    private List<Gondola> generateList(){
//        AuthBean authbean = this.getAuthBean();
//        EntityManager em = getEntityManager();                 
//        CriteriaBuilder builder = em.getCriteriaBuilder();        
//        CriteriaQuery<Gondola> query = builder.createQuery(Gondola.class);
//        Root<Gondola> oRoot = query.from(Gondola.class);              
//        Predicate criteria = builder.conjunction();        
//        criteria = builder.and(criteria,builder.equal(oRoot.get("site").get("siteid"), authbean.getSiteid()));      
//        criteria = builder.and(criteria,builder.equal(oRoot.get("deleted"), false));      
//        System.out.println(authbean.getSiteid()+this.gondolaid);
//        if(this.filterGondolaid){            
//            criteria = builder.and(criteria,builder.equal(oRoot.<String>get("gondolaid"), authbean.getSiteid()+this.gondolaid));      
//        }
//        query.where(criteria);                
//        Query q = em.createQuery(query);
//        q.setHint("eclipselink.refresh", "true");   
//        List<Gondola> gondolas = q.getResultList();
//        return gondolas;
//    }
    
    
    
    public List<Gondola> getAll(){
        AuthBean authbean = this.getAuthBean();        
        EntityManager em = this.getEntityManager();
        String strSQL = "SELECT g FROM Gondola g WHERE g.site.siteid=:siteid AND g.deleted=:deleted";
        try{
            Query query = em.createQuery(strSQL)
                    .setParameter("siteid", authbean.getSiteid())
                    .setParameter("deleted", false);
            query.setHint("eclipselink.refresh", "true");
            return query.getResultList();
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
    }

    public SelectItem[] getGondolaOption(){
        AuthBean authbean = this.getAuthBean();        
        EntityManager em = this.getEntityManager();
        String strSQL = "SELECT g FROM Gondola g WHERE g.site.siteid=:siteid AND g.deleted=:deleted";
        try{
            Query query = em.createQuery(strSQL)
                    .setParameter("siteid", authbean.getSiteid())
                    .setParameter("deleted", false);
            query.setHint("eclipselink.refresh", "true");
            List<Gondola> gondolas = query.getResultList();
            if(gondolas.size()>0){
                    SelectItem[] options = new SelectItem[gondolas.size() + 1];
                    options[0] = new SelectItem("", "Select");                   
                    int i = 0;
                    for(Gondola o : gondolas){
                         options[i + 1] = new SelectItem(o.getGondolaname(), o.getGondolaname()); 
                         i++;
                    }
                    return options;
                }else{
                    return null;
                }            
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }        
        
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
    
    private Gondola findGondola(String gondolaid){
        AuthBean authbean = this.getAuthBean();
        EntityManager em = this.getEntityManager();        
        String strSQL = "SELECT g FROM Gondola g WHERE g.gondolaid=:gondolaid AND g.site.siteid=:siteid";
        try{
            Query query = em.createQuery(strSQL)
                    .setParameter("gondolaid", gondolaid)
                    .setParameter("siteid", authbean.getSiteid());
            query.setHint("eclipselink.refresh", "true");
            return (Gondola) query.getSingleResult();
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
    }
    
    private String addGondola(){
        AuthBean authbean = this.getAuthBean();
        EntityManager em = this.getEntityManager();                
        em.getTransaction().begin();
        try{
            this.object.setGondolaid(authbean.getSiteid() + this.object.getGondolaname());
            this.object.getSite().setSiteid(authbean.getSiteid());
            this.object.setCreateddate(new Date(System.currentTimeMillis()));
            this.object.setCreatedby(authbean.getUsername());
            this.object.setUpdateddate(new Date(System.currentTimeMillis()));
            this.object.setUpdatedby(authbean.getUsername());
            em.persist(this.object);
            em.getTransaction().commit();
            this.loadData();
            return "gondolalist?faces-redirect=true";
        }catch(Exception ex){
            em.getTransaction().rollback();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, error);               
            return null;
        }finally{
            em.close();
        }          
    }
    
    private String editGondola(){
        AuthBean authbean = this.getAuthBean();        
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();        
        try{
            this.selectedObject.setUpdateddate(new Date(System.currentTimeMillis()));
            this.selectedObject.setUpdatedby(authbean.getUsername());            
            em.merge(this.selectedObject);
            em.getTransaction().commit();
            this.loadData();
            return "gondolalist?faces-redirect=true";
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
    
    public Gondola getObject() {
        return object;
    }

    public void setObject(Gondola object) {
        this.object = object;
    }

    public Gondola getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Gondola selectedObject) {                
        this.selectedObject = selectedObject;
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

    public LazyDataModel<Gondola> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Gondola> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public boolean isFilterGondolaid() {
        return filterGondolaid;
    }

    public void setFilterGondolaid(boolean filterGondolaid) {
        this.filterGondolaid = filterGondolaid;
    }

    public String getGondolaid() {
        return gondolaid;
    }

    public void setGondolaid(String gondolaid) {
        this.gondolaid = gondolaid;
    }

    public HashMap getFilterList() {
        return filterList;
    }

    public void setFilterList(HashMap filterList) {
        this.filterList = filterList;
    }
                       
    
    
}
