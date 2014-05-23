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
import org.jakc.stockop.datamodel.LazyEmployeetypeDataModel;
import org.jakc.stockop.entity.Employeetype;
import org.jakc.stockop.entity.Form;
import org.jakc.stockop.entity.Roles;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class EmployeetypeBean {

    private EntityManagerFactory emf  = Persistence.createEntityManagerFactory("StockOpPU");
    private LazyDataModel<Employeetype> lazyModel;
    
    private Employeetype object;
    private Employeetype selectedObject;
    private Roles selectedRoles = new Roles();
    
    private boolean filterEmployeetypeid;
    private String employeetypeid;
    /** Creates a new instance of EmployeetypeBean */
    public EmployeetypeBean() {
        this.loadData();
    }
    
    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }
    
    public String add(){
        this.object = new Employeetype();
        return "employeetypeadd?faces-redirect=true";
    }
    
    public String edit(){
        return "employeetypeedit?faces-redirect=true";
    }
    
    public String cancel(){
        return "employeetypelist?faces-redirect=true";
    }
    
    public String listRoles(){
        return "roleslist?faces-redirect=true";
    }
    
    public String saveAdd(){
        Employeetype employeetype = this.findEmployeetype(this.object.getEmployeetypeid());
        if(employeetype != null){
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Employee Type Already Exist","");
            FacesContext.getCurrentInstance().addMessage(null, error);                           
            return null;
        }else{
            return this.addEmployeetype();
        }
    }
    
    public String saveEdit(){
        return this.editEmployeetype();
    }
   
    
    public void saveEditRoles(){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            em.merge(this.selectedRoles);
            em.getTransaction().commit();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_INFO,"Roles Updated Successfully","");
            FacesContext.getCurrentInstance().addMessage(null, error);                     
        }catch(Exception ex){
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Roles Updated Failed","");
            FacesContext.getCurrentInstance().addMessage(null, error);                
            em.getTransaction().rollback();
        }finally{
            em.close();
        }

    }
   
    public void refreshRoles(){
        
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
        List<Employeetype> os = this.generateList();               
        this.lazyModel =  new LazyEmployeetypeDataModel(os,this.emf);
    }
    
    private List<Employeetype> generateList(){
        AuthBean authbean = this.getAuthBean();
        EntityManager em = getEntityManager();                 
        CriteriaBuilder builder = em.getCriteriaBuilder();        
        CriteriaQuery<Employeetype> query = builder.createQuery(Employeetype.class);
        Root<Employeetype> oRoot = query.from(Employeetype.class);              
        Predicate criteria = builder.conjunction();        
        //Start Criteria        
        if(this.filterEmployeetypeid){         
            System.out.println("Filter by Employeetypeid");
            criteria = builder.and(criteria,builder.equal(oRoot.get("employeetypeid"), this.employeetypeid));            
        }               
        query.where(criteria);                
        Query q = em.createQuery(query);
        q.setHint("eclipselink.refresh", "true");   
        List<Employeetype> os = q.getResultList();
        return os;        
    }     
    
    public List<Employeetype> getAll(){
        EntityManager em = this.getEntityManager();
        try{
            Query query = em.createNamedQuery("Employeetype.findByDeleted").setParameter("deleted", false);
            query.setHint("eclipselink.refresh", "true");
            return query.getResultList();
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
    }
    
    public List<Roles> getRoles(){
        EntityManager em = this.getEntityManager();
        String strSQL = "SELECT r FROM Roles r WHERE r.employeetypeid=:employeetypeid";
        try{
            Query query = em.createQuery(strSQL).setParameter("employeetypeid", this.selectedObject.getEmployeetypeid());
            query.setHint("eclipselink.refresh", "true");
            return query.getResultList();
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
    }

    private Employeetype findEmployeetype(String employeetypeid){
        EntityManager em = this.getEntityManager();
        try{
            Query query = em.createNamedQuery("Employeetype.findByEmployeetypeid").setParameter("employeetypeid", employeetypeid);
            query.setHint("eclipselink.refresh", "true");
            return (Employeetype) query.getSingleResult();
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
    }
    
    private String addEmployeetype(){
        EntityManager em = this.getEntityManager();                
        em.getTransaction().begin();
        try{
            this.object.setCreateddate(new Date(System.currentTimeMillis()));
            this.object.setCreatedby("007290");
            this.object.setUpdateddate(new Date(System.currentTimeMillis()));
            this.object.setUpdatedby("007290");
            em.persist(this.object);
            em.getTransaction().commit();
            this.generateRoles();
            return "employeetypelist?faces-redirect=true";
        }catch(Exception ex){
            em.getTransaction().rollback();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, error);               
            return null;
        }finally{
            em.close();
        }        
    }
    
    private String editEmployeetype(){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            this.selectedObject.setUpdateddate(new Date(System.currentTimeMillis()));
            this.selectedObject.setUpdatedby("007290");            
            em.merge(this.selectedObject);
            em.getTransaction().commit();
            return "employeetypelist?faces-redirect=true";
        }catch(Exception ex){
            em.getTransaction().rollback();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, error);               
            return null;
        }finally{
            em.close();
        }            
    }
    
    
    private List<Form> getForm(){
        EntityManager em = this.getEntityManager();
        try{
            return em.createNamedQuery("Form.findAll").getResultList();
        }catch(NoResultException ex){
            ex.printStackTrace();
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
    
    private void generateRoles(){
        EntityManager em = this.getEntityManager();        
        em.getTransaction().begin();
        try{
            List<Form> forms = this.getForm();
            for(Form form : forms){
                Roles o = new Roles();                
                o.setEmployeetypeid(this.object.getEmployeetypeid());
                o.setFormid(form.getFormid());
                o.setFormname(form.getFormname());
                o.setCreateddate(new Date(System.currentTimeMillis()));
                o.setCreatedby("007290");
                o.setUpdateddate(new Date(System.currentTimeMillis()));
                o.setUpdatedby("007290");
                em.persist(o);                
            }
            em.getTransaction().commit();
        }catch(Exception ex){
            em.getTransaction().rollback();
        }finally{
            em.close();
        }
    }

    
    public Employeetype getObject() {             
        return object;
    }

    public void setObject(Employeetype object) {        
        this.object = object;
    }

    public Employeetype getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Employeetype selectedObject) {
        this.selectedObject = selectedObject;
    }

    public Roles getSelectedRoles() {
        return selectedRoles;
    }

    public void setSelectedRoles(Roles selectedRoles) {
        this.selectedRoles = selectedRoles;
    }

    public String getEmployeetypeid() {
        return employeetypeid;
    }

    public void setEmployeetypeid(String employeetypeid) {
        this.employeetypeid = employeetypeid;
    }

    public boolean isFilterEmployeetypeid() {
        return filterEmployeetypeid;
    }

    public void setFilterEmployeetypeid(boolean filterEmployeetypeid) {
        this.filterEmployeetypeid = filterEmployeetypeid;
    }

    public LazyDataModel<Employeetype> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Employeetype> lazyModel) {
        this.lazyModel = lazyModel;
    }
    
    
}
