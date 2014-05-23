/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.bean;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import org.jakc.stockop.datamodel.LazyEmployeeDataModel;
import org.jakc.stockop.entity.Employee;
import org.jakc.stockop.entity.Employeesite;
import org.jakc.stockop.entity.Employeetype;
import org.jakc.stockop.entity.Form;
import org.jakc.stockop.entity.Roles;
import org.jakc.stockop.entity.Site;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class UserBean {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("StockOpPU");
    private LazyDataModel<Employee> lazyModel;
    private Employee object = new Employee();
    private Employee selectedObject = new Employee();
    private Employeesite selectedEmployeesite = new Employeesite();
    
    private String siteid;
    private String password;
    private String newpassword;
    private String employeetypeid;
    
    //filter
    private boolean filterNik;
    private String nik;
    
    //sort
    private String sortField="nik";
    private String sortType="asc";
            
    /** Creates a new instance of UserBean */
    public UserBean() {
        this.loadData();
    }
    
    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }
    
    public String add(){        
        this.object = new Employee();
        this.object.setEmployeetype(new Employeetype());
        return "useradd?faces-redirect=true";
    }
    
    public String edit(){
        return "useredit?faces-redirect=true";
    }
    
    public String cancel(){
        return "userlist?faces-redirect=true";
    }
    
    public String changepassword(){
        return "/faces/user/changepassword?faces-redirect=true";
    }

    public String usersite(){
        return "usersitelist?faces-redirect=true";
    }
    
    public String backtoUsersite(){
        return "userlist?faces-redirect=true";
    }
    
    
    public String saveAdd(){
         Employee employee = this.findUser(this.object.getNik());
         if(employee != null){
            if(employee.getDeleted() == true){
                FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,"User was deleted and already activated","");
                FacesContext.getCurrentInstance().addMessage(null, error);      
                employee.setFullname(this.object.getFullname());
                employee.setPassword(this.generateHash(this.object.getPassword()));
                employee.setStatus(true);
                employee.setDeleted(false);
                this.selectedObject = employee;
                this.editUser();                    
            }else{
                FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,"User already exist","");
                FacesContext.getCurrentInstance().addMessage(null, error);               
            }
            return null;
         }else{
             return this.addUser();
         }
    }
    
    public String saveEdit(){
        return this.editUser();
    }
    
    public String saveChangePassword(){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        AuthBean authbean = this.getAuthBean();
        Employee employee = em.find(Employee.class, authbean.getUsername());
        if(employee != null){
            try{
                String hashpassword = this.generateHash(password);
                employee.setPassword(hashpassword);
                em.merge(employee);
                em.getTransaction().commit();
                return "/faces/mainpage?faces-redirect=true";
            }catch(Exception ex){
                em.getTransaction().rollback();                
                FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
                FacesContext.getCurrentInstance().addMessage(null, error);                               
                return null;
            }finally{
                em.close();
            }                                            
        }else{
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,"User not found","");
            FacesContext.getCurrentInstance().addMessage(null, error);                           
            return null;
        }
        
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
        System.out.println("Load Data...");
        List<Employee> os = this.generateList();               
        System.out.println("User List Row Count : " + os.size());
        this.lazyModel =  new LazyEmployeeDataModel(os,this.emf);
    }
    
    private List<Employee> generateList(){
        AuthBean authbean = this.getAuthBean();
        EntityManager em = getEntityManager();                 
        CriteriaBuilder builder = em.getCriteriaBuilder();        
        CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
        Root<Employee> oRoot = query.from(Employee.class);              
        Predicate criteria = builder.conjunction();               
        criteria = builder.and(criteria,builder.equal(oRoot.get("deleted"), false));      
        
        if(this.filterNik){         
            System.out.println("Filter by Nik");
            criteria = builder.and(criteria,builder.equal(oRoot.get("nik"), this.nik));            
        }               
        
        query.where(criteria);                
        if(this.sortType.equals("asc")){
            query.orderBy(builder.asc(oRoot.get(this.sortField)));
        }
        if(this.sortType.equals("desc")){
            query.orderBy(builder.desc(oRoot.get(this.sortField)));
        }
        
        Query q = em.createQuery(query);
        q.setHint("eclipselink.refresh", "true");   
        List<Employee> periodes = q.getResultList();
        return periodes;        
    }     
    
    
    public List<Employee> getAll(){
        EntityManager em = this.getEntityManager();
        try{
            Query query = em.createNamedQuery("Employee.findByDeleted").setParameter("deleted", false);
            query.setHint("eclipselink.refresh", "true");
            return query.getResultList();
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
    }

    public List<Employeesite> getEmployeesites(){
        EntityManager em = this.getEntityManager();
        
        try{
            Query query = em.createNamedQuery("Employeesite.findByNik").setParameter("nik", this.selectedObject.getNik());
            return query.getResultList();
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
    }
    
    public void createEmployeesite(){
        Employeesite employeesite = this.findEmployeesite();
        if(employeesite == null){
            this.addEmployeesite();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,"Employee site created succesfully","");
            FacesContext.getCurrentInstance().addMessage(null, msg);            
        }else{
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,"Employee site already exist","");
            FacesContext.getCurrentInstance().addMessage(null, msg);                 
        }
    }
    
    private void addEmployeesite(){
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            Employeesite o = new Employeesite();
            o.setSite(new Site());
            o.setNik(this.selectedObject.getNik());
            o.getSite().setSiteid(this.siteid);
            em.persist(o);
            em.getTransaction().commit();
        }catch(Exception ex){
            ex.printStackTrace();
            em.getTransaction().rollback();
        }finally{
            em.close();
        }        
    }
    private Employeesite findEmployeesite(){
        EntityManager em = this.getEntityManager();
        String strSQL = "SELECT e FROM Employeesite e WHERE e.nik=:nik AND e.site.siteid=:siteid";
        try{
            Query query = em.createQuery(strSQL)
                    .setParameter("nik", this.selectedObject.getNik())
                    .setParameter("siteid", this.siteid);
            query.setHint("eclipselink.refresh", "true");
            return (Employeesite) query.getSingleResult();                    
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }finally{
            em.close();
        }
    }
    
    public void changeUserPassword(){
        AuthBean authbean = this.getAuthBean();
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{
            this.selectedObject.setPassword(this.generateHash(this.newpassword));            
            this.selectedObject.setUpdateddate(new Date(System.currentTimeMillis()));
            this.selectedObject.setUpdatedby(authbean.getUsername());
            em.merge(this.selectedObject);
            em.getTransaction().commit();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_INFO,"Password Change Succesfully","");
            FacesContext.getCurrentInstance().addMessage(null, error);                               
            this.newpassword = "";
        }catch(Exception ex){
            this.newpassword = "";
            em.getTransaction().rollback();                    
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Password Change Failed","");
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
    
    private Employee findUser(String nik){
        EntityManager em = this.getEntityManager();        
        try{
            Query query = em.createNamedQuery("Employee.findByNik").setParameter("nik", nik);
            query.setHint("eclipselink.refresh", true);
            return (Employee) query.getSingleResult();
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
    }
    
    private String addUser(){
        AuthBean authbean = this.getAuthBean();
        EntityManager em = this.getEntityManager();                
        em.getTransaction().begin();
        try{            
            //this.object.setEmployeetype(this.findEmployeetype(employeetypeid));
            this.object.setPassword(this.generateHash(this.object.getPassword()));
            this.object.setCreateddate(new Date(System.currentTimeMillis()));
            this.object.setCreatedby(authbean.getUsername());
            this.object.setUpdateddate(new Date(System.currentTimeMillis()));
            this.object.setUpdatedby(authbean.getUsername());
            em.persist(this.object);
            em.getTransaction().commit();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_INFO,"User Added Successfully","");
            FacesContext.getCurrentInstance().addMessage(null, error);                           
            this.loadData();
            return "userlist?faces-redirect=true";
        }catch(Exception ex){
            ex.printStackTrace();
            em.getTransaction().rollback();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, error);               
            return null;
        }finally{
            em.close();
        }         
    }
    
    private String editUser(){        
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        try{                 
            //this.selectedObject.setEmployeetype(this.findEmployeetype(employeetypeid));            
            this.selectedObject.setUpdateddate(new Date(System.currentTimeMillis()));
            this.selectedObject.setUpdatedby(this.getAuthBean().getUsername());            
            em.merge(this.selectedObject);
            em.getTransaction().commit();
            this.loadData();
            return "userlist?faces-redirect=true";                
        }catch(Exception ex){
            em.getTransaction().rollback();
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.getMessage(),"");
            FacesContext.getCurrentInstance().addMessage(null, error);               
            return null;
        }finally{
            em.close();
        }                  
    }
    
    public void deleteFromSite(){
        System.out.println("Delete User");
        EntityManager em = this.getEntityManager();        
        em.getTransaction().begin();
        try{
            String strSQL = "DELETE FROM Employeesite e WHERE e.id=" + this.selectedEmployeesite.getId();
            Query query=em.createQuery(strSQL);
            query.executeUpdate();
            em.getTransaction().commit();
            this.loadData();
        }catch(Exception ex){
            em.getTransaction().rollback();
        }finally{
            em.close();
        }
    }
    
    private String generateHash(String input){
        StringBuilder hash = new StringBuilder();
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] hashedBytes = sha.digest(input.getBytes());
            char[] digits = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
            for(int idx=0; idx < hashedBytes.length; idx++){                
                byte b = hashedBytes[idx];
                hash.append(digits[(b & 0xf0) >> 4]);
                hash.append(digits[(b & 0x0f)]);
            }
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        }
        return hash.toString();
    }    
    
    private Employeetype findEmployeetype(String employeetypeid){
        EntityManager em = this.getEntityManager();
        try{
            Query query = em.createNamedQuery("Employeetype.findByEmployeetypeid").setParameter("employeetypeid", employeetypeid);
            query.setHint("eclipselink.refresh", true);
            return (Employeetype) query.getSingleResult();
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
    }
    
    public Employee getObject() {
        return object;
    }

    public void setObject(Employee object) {
        this.object = object;
    }

    public Employee getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Employee selectedObject) {
        this.selectedObject = selectedObject;
        
       
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSiteid() {
        return siteid;
    }

    public void setSiteid(String siteid) {
        this.siteid = siteid;
    }

    public boolean isFilterNik() {
        return filterNik;
    }

    public void setFilterNik(boolean filterNik) {
        this.filterNik = filterNik;
    }

    public LazyDataModel<Employee> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Employee> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public String getEmployeetypeid() {
        return employeetypeid;
    }

    public void setEmployeetypeid(String employeetypeid) {
        this.employeetypeid = employeetypeid;
    }

    public Employeesite getSelectedEmployeesite() {
        return selectedEmployeesite;
    }

    public void setSelectedEmployeesite(Employeesite selectedEmployeesite) {
        this.selectedEmployeesite = selectedEmployeesite;
    }
    
    
    
    
    
}
