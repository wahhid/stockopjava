/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.jakc.stockop.datamodel.LazyPeriodeDataModel;
import org.jakc.stockop.entity.Periode;
import org.jakc.stockop.entity.Roles;
import org.jakc.stockop.entity.Site;
import org.primefaces.model.LazyDataModel;


/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class PeriodeBean {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("StockOpPU");
    
    private List<Periode> all;
    
    private LazyDataModel<Periode> lazyModel;
    
    private Periode object = new Periode();
    private Periode selectedObject = new Periode();
    private Date periodeDate;
    
    
    //Security        
    private boolean allowcreate=false;
    private boolean allowread=false;
    private boolean allowupdate=false;
    private boolean allowdelete=false;
    private boolean allowdownload=false;
    private boolean allowupload=false;
    
    private String hiddennik;
    private String hiddenemployeetype;
    private String formid="F0002";
    
    
    //filter
    private boolean filterPeriodeid=false;
    private String periodeid;
    
    /** Creates a new instance of PeriodeBean */
    
    public PeriodeBean() {               
           
    }
    
    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }
    

    public String add(){
        this.object = new Periode();
        return "periodeadd?faces-redirect=true";
    }
    
    public String edit(){
        return "periodeedit?faces-redirect=true";
    }
    
    public String cancel(){
        this.loadData();
        return "periodelist?faces-redirect=true";
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
        AuthBean authbean = this.getAuthBean();        
        Periode periode = this.findPeriode(authbean.getSiteid() + new SimpleDateFormat("yyyyMMdd").format(this.periodeDate));
        if(periode != null){
            if(periode.getDeleted()){
                FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Periode was deleted and already activated","");
                FacesContext.getCurrentInstance().addMessage(null, error);                           
                periode.setDescription(this.object.getDescription());
                periode.setDeleted(false);
                this.selectedObject = periode;
                this.editPeriode();
            }else{
                FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Periode Already Exist","");
                FacesContext.getCurrentInstance().addMessage(null, error);                           
            }
            return null;
        }else{
            return this.addPeriode();
        }
    }
    
    public String saveEdit(){
        return this.editPeriode();
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
    
    @PostConstruct
    public void init() {
        this.loadData();
    }

    public void loadData(){        
        System.out.println("Load Data...");        
        List<Periode> periodes = this.generateList();               
        this.lazyModel =  new LazyPeriodeDataModel(periodes,this.emf);
    }
    
    private List<Periode> generateList(){
        AuthBean authbean = this.getAuthBean();
        EntityManager em = getEntityManager();          
        CriteriaBuilder builder = em.getCriteriaBuilder();        
        CriteriaQuery<Periode> query = builder.createQuery(Periode.class);
        Root<Periode> oRoot = query.from(Periode.class);              
        Predicate criteria = builder.conjunction();        
        //Start Criteria
        criteria = builder.and(criteria,builder.equal(oRoot.get("site").<String>get("siteid"), authbean.getSiteid()));  
        criteria = builder.and(criteria,builder.equal(oRoot.get("deleted"), false));  
        if(this.filterPeriodeid){         
            System.out.println("Filter by Periodeid");            
            criteria = builder.and(criteria,builder.equal(oRoot.get("periodeid"),authbean.getSiteid()+this.periodeid));            
        }               
        query.where(criteria);                
        Query q = em.createQuery(query);
        q.setHint("eclipselink.refresh", "true");   
        return q.getResultList();
    }     
    
    
    public List<Periode> getAll(){
        AuthBean authbean = this.getAuthBean();
        EntityManager em = this.getEntityManager();
        try{
            return em.createNamedQuery("Periode.findBySiteid").setParameter("siteid", authbean.getSiteid()).getResultList();
        }finally{
            em.close();
        }
    }
        
    public SelectItem[] getPeriodeOption(){
        AuthBean authbean = this.getAuthBean();
        EntityManager em = this.getEntityManager();
        try{            
                Query query = em.createNamedQuery("Periode.findBySiteid")
                        .setParameter("siteid", authbean.getSiteid());                
                query.setHint("eclipselink.refresh", "true");
                List<Periode> periodes = query.getResultList();
                if(periodes.size()>0){
                    SelectItem[] options = new SelectItem[periodes.size() + 1];
                    options[0] = new SelectItem("", "Select");                   
                    int i = 0;
                    for(Periode o : periodes){
                         options[i + 1] = new SelectItem(o.getPeriodename(), o.getPeriodename()); 
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
    
    public Periode findPeriode(String periodeid){
        AuthBean authbean = this.getAuthBean();
        EntityManager em = this.getEntityManager();        
        try{
            Query query = em.createNamedQuery("Periode.findByPeriodeid").setParameter("periodeid", periodeid);
            query.setHint("eclipselink.refresh", "true");
            return (Periode) query.getSingleResult();
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
    }
    
    
    
    public String addPeriode(){
        AuthBean authbean = this.getAuthBean();
        EntityManager em = this.getEntityManager();                
        em.getTransaction().begin();
        try{
            this.object.setPeriodeid(authbean.getSiteid() +  new SimpleDateFormat("yyyyMMdd").format(periodeDate));
            System.out.println(authbean.getSiteid() +  new SimpleDateFormat("yyyyMMdd").format(periodeDate));
            this.object.setPeriodename(new SimpleDateFormat("yyyyMMdd").format(periodeDate));
            this.object.setSite(new Site());
            this.object.getSite().setSiteid(authbean.getSiteid());
            this.object.setPeriodesession(1);
            this.object.setStatus(true);
            this.object.setCreateddate(new Date(System.currentTimeMillis()));
            this.object.setCreatedby(authbean.getUsername());
            this.object.setUpdateddate(new Date(System.currentTimeMillis()));
            this.object.setUpdatedby(authbean.getUsername());
            em.persist(this.object);
            em.getTransaction().commit();
            this.loadData();
            return "periodelist?faces-redirect=true";
        }catch(Exception ex){
            em.getTransaction().rollback();            
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_INFO,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, error);               
            return null;
        }finally{
            em.close();
        }                  
    }
    
    public String editPeriode(){
        AuthBean authbean = this.getAuthBean();
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            this.selectedObject.setUpdateddate(new Date(System.currentTimeMillis()));
            this.selectedObject.setUpdatedby(authbean.getUsername());            
            em.merge(this.selectedObject);
            em.getTransaction().commit();
            this.loadData();
            return "periodelist?faces-redirect=true";
        }catch(Exception ex){
            em.getTransaction().rollback();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, error);               
            return null;
        }finally{
            em.close();
        }                  
    }
    
    public String closeSession(){
        AuthBean authbean = this.getAuthBean();
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            if(this.selectedObject.getPeriodesession() == 2){
                this.selectedObject.setStatus(false);
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Close Session 2 and Close Periode " + this.selectedObject.getPeriodename() + " successfully","");
                FacesContext.getCurrentInstance().addMessage(null, msg);                              
            }            
            if(this.selectedObject.getPeriodesession() == 1){
                this.selectedObject.setPeriodesession(2);
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Close Session 1 successfully","");
                FacesContext.getCurrentInstance().addMessage(null, msg);                              
                
            }
            this.selectedObject.setUpdateddate(new Date(System.currentTimeMillis()));
            this.selectedObject.setUpdatedby(authbean.getUsername());            
            em.merge(this.selectedObject);
            em.getTransaction().commit();
            this.loadData();
            return null;
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
    
    public Periode getObject() {
        return object;
    }

    public void setObject(Periode object) {
        this.object = object;
    }

    public Periode getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Periode selectedObject) {
        this.selectedObject = selectedObject;
    }

    public String getHiddenemployeetype() {
        return hiddenemployeetype;
    }

    public void setHiddenemployeetype(String hiddenemployeetype) {
        this.hiddenemployeetype = hiddenemployeetype;
    }

    public String getHiddennik() {
        return hiddennik;
    }

    public void setHiddennik(String hiddennik) {
        this.hiddennik = hiddennik;
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
        this.getallowDelete();
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

    public LazyDataModel<Periode> getLazyModel() {        
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Periode> lazyModel) {
        this.lazyModel = lazyModel;
    }


    public boolean isFilterPeriodeid() {
        return filterPeriodeid;
    }

    public void setFilterPeriodeid(boolean filterPeriodeid) {
        this.filterPeriodeid = filterPeriodeid;
    }

    public String getPeriodeid() {
        return periodeid;
    }

    public void setPeriodeid(String periodeid) {
        this.periodeid = periodeid;
    }

    public Date getPeriodeDate() {
        return periodeDate;
    }

    public void setPeriodeDate(Date periodeDate) {
        this.periodeDate = periodeDate;
    }


    
        
}
