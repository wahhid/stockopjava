/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author root
 */
@Entity
@Table(name = "employee", catalog = "opnam", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Employee.findAll", query = "SELECT e FROM Employee e"),
    @NamedQuery(name = "Employee.findByNik", query = "SELECT e FROM Employee e WHERE e.nik = :nik"),
    @NamedQuery(name = "Employee.findByFullname", query = "SELECT e FROM Employee e WHERE e.fullname = :fullname"),
    @NamedQuery(name = "Employee.findBySiteid", query = "SELECT e FROM Employee e WHERE e.site.siteid = :siteid"),
    @NamedQuery(name = "Employee.findByPassword", query = "SELECT e FROM Employee e WHERE e.password = :password"),
    @NamedQuery(name = "Employee.findByEmployeetype", query = "SELECT e FROM Employee e WHERE e.employeetype = :employeetype"),
    @NamedQuery(name = "Employee.findByStatus", query = "SELECT e FROM Employee e WHERE e.status = :status"),
    @NamedQuery(name = "Employee.findByDeleted", query = "SELECT e FROM Employee e WHERE e.deleted = :deleted"),
    @NamedQuery(name = "Employee.findByCreateddate", query = "SELECT e FROM Employee e WHERE e.createddate = :createddate"),
    @NamedQuery(name = "Employee.findByCreatedby", query = "SELECT e FROM Employee e WHERE e.createdby = :createdby"),
    @NamedQuery(name = "Employee.findByUpdateddate", query = "SELECT e FROM Employee e WHERE e.updateddate = :updateddate"),
    @NamedQuery(name = "Employee.findByUpdatedby", query = "SELECT e FROM Employee e WHERE e.updatedby = :updatedby")})
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull    
    @Size(min = 1, max = 10)
    @Column(name = "nik")
    private String nik;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "fullname")
    private String fullname;
    @ManyToOne
    @JoinColumn(name="siteid")
    private Site site;
    @Size(max = 10)
    @Column(name = "password")
    private String password;
//    @Basic(optional = false)
//    @NotNull
//    @Size(min = 1, max = 2)
//    @Column(name = "employeetype")
//    private String employeetype;
    @ManyToOne
    @JoinColumn(name="employeetype")
    private Employeetype employeetype;
    @Basic(optional = false)
    @NotNull
    @Column(name = "status")
    private boolean status;
    @Basic(optional = false)
    @NotNull
    @Column(name = "deleted")
    private boolean deleted;
    @Basic(optional = false)
    @NotNull
    @Column(name = "createddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createddate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "createdby")
    private String createdby;
    @Column(name = "updateddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateddate;
    @Size(max = 10)
    @Column(name = "updatedby")
    private String updatedby;

    public Employee() {
    }

    public Employee(String nik) {
        this.nik = nik;
    }

    public Employee(String nik, String fullname, Site site, Employeetype employeetype, boolean status, boolean deleted, Date createddate, String createdby) {
        this.nik = nik;
        this.fullname = fullname;
        this.site = site;
        this.employeetype = employeetype;
        this.status = status;
        this.deleted = deleted;
        this.createddate = createddate;
        this.createdby = createdby;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Employeetype getEmployeetype() {
        return employeetype;
    }

    public void setEmployeetype(Employeetype employeetype) {
        this.employeetype = employeetype;
    }



    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getCreateddate() {
        return createddate;
    }

    public void setCreateddate(Date createddate) {
        this.createddate = createddate;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public Date getUpdateddate() {
        return updateddate;
    }

    public void setUpdateddate(Date updateddate) {
        this.updateddate = updateddate;
    }

    public String getUpdatedby() {
        return updatedby;
    }

    public void setUpdatedby(String updatedby) {
        this.updatedby = updatedby;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nik != null ? nik.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee other = (Employee) object;
        if ((this.nik == null && other.nik != null) || (this.nik != null && !this.nik.equals(other.nik))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.jakc.stockop.entity.Employee[ nik=" + nik + " ]";
    }
    
}
