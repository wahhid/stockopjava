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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "transstock", catalog = "opnam", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Transstock.findAll", query = "SELECT t FROM Transstock t"),
    @NamedQuery(name = "Transstock.findByTransid", query = "SELECT t FROM Transstock t WHERE t.transid = :transid"),
    @NamedQuery(name = "Transstock.findByPeriodeid", query = "SELECT t FROM Transstock t WHERE t.periode.periodeid = :periodeid"),
    @NamedQuery(name = "Transstock.findBySiteid", query = "SELECT t FROM Transstock t WHERE t.site.siteid = :siteid"),
    @NamedQuery(name = "Transstock.findByGondolaid", query = "SELECT t FROM Transstock t WHERE t.gondola.gondolaid = :gondolaid"),
    @NamedQuery(name = "Transstock.findByNik", query = "SELECT t FROM Transstock t WHERE t.employee.nik = :nik"),
    @NamedQuery(name = "Transstock.findByStatus", query = "SELECT t FROM Transstock t WHERE t.status = :status"),
    @NamedQuery(name = "Transstock.findByCreateddate", query = "SELECT t FROM Transstock t WHERE t.createddate = :createddate"),
    @NamedQuery(name = "Transstock.findByCreatedby", query = "SELECT t FROM Transstock t WHERE t.createdby = :createdby"),
    @NamedQuery(name = "Transstock.findByUpdateddate", query = "SELECT t FROM Transstock t WHERE t.updateddate = :updateddate"),
    @NamedQuery(name = "Transstock.findByUpdatedby", query = "SELECT t FROM Transstock t WHERE t.updatedby = :updatedby")})
public class Transstock implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "transid")
    private Integer transid;
    @ManyToOne
    @JoinColumn(name="periodeid")
    private Periode periode;
    @ManyToOne
    @JoinColumn(name="siteid")
    private Site site;            
    @ManyToOne
    @JoinColumn(name="gondolaid")
    private Gondola gondola;            
    @ManyToOne
    @JoinColumn(name="nik")
    private Employee employee;            
    @Basic(optional = false)    
    @Column(name = "transcount")
    private Integer transcount;    
    @Basic(optional = false)
    @NotNull
    @Column(name = "status")
    private boolean status;
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
    @Basic(optional = false)
    @NotNull
    @Column(name = "updateddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateddate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "updatedby")
    private String updatedby;

    public Transstock() {
    }

    public Transstock(Integer transid) {
        this.transid = transid;
    }

    public Transstock(Integer transid, Periode periode, Site site, Gondola gondola, Employee employee, boolean status, Date createddate, String createdby, Date updateddate, String updatedby) {
        this.transid = transid;
        this.periode = periode;
        this.site = site;
        this.gondola = gondola;
        this.employee = employee;
        this.status = status;
        this.createddate = createddate;
        this.createdby = createdby;
        this.updateddate = updateddate;
        this.updatedby = updatedby;
    }

    public Integer getTransid() {
        return transid;
    }

    public void setTransid(Integer transid) {
        this.transid = transid;
    }

    public Gondola getGondola() {
        return gondola;
    }

    public void setGondola(Gondola gondola) {
        this.gondola = gondola;
    }

    public Periode getPeriode() {
        return periode;
    }

    public void setPeriode(Periode periode) {
        this.periode = periode;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Integer getTranscount() {
        return transcount;
    }

    public void setTranscount(Integer transcount) {
        this.transcount = transcount;
    }
        
    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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
        hash += (transid != null ? transid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Transstock)) {
            return false;
        }
        Transstock other = (Transstock) object;
        if ((this.transid == null && other.transid != null) || (this.transid != null && !this.transid.equals(other.transid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.jakc.stockop.entity.Transstock[ transid=" + transid + " ]";
    }
    
}
