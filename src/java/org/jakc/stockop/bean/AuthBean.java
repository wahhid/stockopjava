/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor. */
package org.jakc.stockop.bean;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.jakc.stockop.entity.Employee;
import org.jakc.stockop.entity.Employeesite;
/**
 *
 * @author root
 */
@ManagedBean(name="authBean")
@SessionScoped
public class AuthBean{
            
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("StockOpPU");
    private String username;
    private String password;
    private String fullname;
    private String employeetype;
    private String siteid;
    private boolean allowadmin=false;    
    private boolean loginStatus=false;
    
    public AuthBean(){
        
    }

    
    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }
    
    public String processLogin(){
        if(this.allowSite()){
            return this.allowLogin();
        }else{
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Login to this site access denied!","");
            FacesContext.getCurrentInstance().addMessage(null, error);        
            this.loginStatus = false;
            return null;            
        }
    }    
    
    public void notLoggedIn(ComponentSystemEvent cse){
        String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        if(!this.loginStatus){
            this.username = "";
            this.fullname = "";        
            this.employeetype = "";        
            this.loginStatus = false;               
            FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "/faces/index?faces-redirect=true");            
        }
    }
    
    public String Logout(){
        this.username = "";
        this.fullname = "";        
        this.employeetype = "";        
        this.loginStatus = false;
        return "/faces/index?faces-redirect=true";
    }

    private String allowLogin(){
        EntityManager em = this.getEntityManager();        
        try{
            Query query = em.createNamedQuery("Employee.findByNik").setParameter("nik", this.username);
            query.setHint("eclipselink.refresh", "true");
            
            Employee employee = (Employee) query.getSingleResult();
            if(employee.getPassword().equals(this.generateHash(this.password))                   
                    && employee.getStatus()){
                this.username = employee.getNik();
                this.fullname = employee.getFullname();
                this.loginStatus = true;
                this.employeetype = employee.getEmployeetype().getEmployeetypeid();                
                if(this.employeetype.equals("01")){
                    this.allowadmin = true;
                }else{
                    this.allowadmin = false;
                }
                return "mainpage";                
            }else{
                FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,"User or Password was Wrong!","");
                FacesContext.getCurrentInstance().addMessage(null, error);        
                this.loginStatus = false;
                return null;                
            }
        }catch(Exception ex){
            FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR,"User or Password was Wrong!","");
            FacesContext.getCurrentInstance().addMessage(null, error);        
            this.loginStatus = false;
            return null;
        }finally{
            em.close();
        }              
    }
    
    private boolean allowSite(){
        EntityManager em = this.getEntityManager();        
        String strSQL = "SELECT e FROM Employeesite e WHERE e.nik=:nik AND e.site.siteid=:siteid";
        try{
            Query query = em.createQuery(strSQL)
                    .setParameter("nik", this.username)
                    .setParameter("siteid", this.siteid);
            query.setHint("eclipselink.refresh", "true");
            
            Employeesite o = (Employeesite) query.getSingleResult();            
            if(o != null){
               System.out.println("Employee Site Found");
                return true;
            }else{
                System.out.println("Employee Site Not Found");
                return false;
            }
        }catch(NoResultException ex){
            ex.printStackTrace();
            return false;
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

    public String getEmployeetype() {
        return employeetype;
    }

    public void setEmployeetype(String employeetype) {
        this.employeetype = employeetype;
    }

    public String getSiteid() {
        return siteid;
    }

    public void setSiteid(String siteid) {
        this.siteid = siteid;
    }



    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(boolean loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAllowadmin() {
        return allowadmin;
    }

    public void setAllowadmin(boolean allowadmin) {
        this.allowadmin = allowadmin;
    }
    
    
    
    
    
    
    
}
