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
@Table(name = "periode", catalog = "opnam", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Periode.findAll", query = "SELECT p FROM Periode p"),
    @NamedQuery(name = "Periode.findByPeriodeid", query = "SELECT p FROM Periode p WHERE p.periodeid = :periodeid"),
    @NamedQuery(name = "Periode.findByPeriodename", query = "SELECT p FROM Periode p WHERE p.periodename = :periodename"),
    @NamedQuery(name = "Periode.findBySiteid", query = "SELECT p FROM Periode p WHERE p.site.siteid = :siteid"),
    @NamedQuery(name = "Periode.findByDeleted", query = "SELECT p FROM Periode p WHERE p.deleted = :deleted"),
    @NamedQuery(name = "Periode.findByCreateddate", query = "SELECT p FROM Periode p WHERE p.createddate = :createddate"),
    @NamedQuery(name = "Periode.findByCreatedby", query = "SELECT p FROM Periode p WHERE p.createdby = :createdby"),
    @NamedQuery(name = "Periode.findByUpdateddate", query = "SELECT p FROM Periode p WHERE p.updateddate = :updateddate"),
    @NamedQuery(name = "Periode.findByUpdatedby", query = "SELECT p FROM Periode p WHERE p.updatedby = :updatedby")})
public class Periode implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "periodeid")
    private String periodeid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "periodename")
    private String periodename;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "description")
    private String description;    
    @ManyToOne
    @JoinColumn(name="siteid")
    private Site site;    
    @Basic(optional = false)
    @Column(name = "periodesession")
    private int periodesession;
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

    public Periode() {
    }

    public Periode(String periodeid) {
        this.periodeid = periodeid;
    }

    public Periode(String periodeid, String periodename, Site site, boolean deleted, Date createddate, String createdby, Date updateddate, String updatedby) {
        this.periodeid = periodeid;
        this.periodename = periodename;
        this.site = site;
        this.deleted = deleted;
        this.createddate = createddate;
        this.createdby = createdby;
        this.updateddate = updateddate;
        this.updatedby = updatedby;
    }

    public String getPeriodeid() {
        return periodeid;
    }

    public void setPeriodeid(String periodeid) {
        this.periodeid = periodeid;
    }

    public String getPeriodename() {
        return periodename;
    }

    public void setPeriodename(String periodename) {
        this.periodename = periodename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public int getPeriodesession() {
        return periodesession;
    }

    public void setPeriodesession(int periodesession) {
        this.periodesession = periodesession;
    }

    
    public boolean isStatus() {
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
        hash += (periodeid != null ? periodeid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Periode)) {
            return false;
        }
        Periode other = (Periode) object;
        if ((this.periodeid == null && other.periodeid != null) || (this.periodeid != null && !this.periodeid.equals(other.periodeid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.jakc.stockop.entity.Periode[ periodeid=" + periodeid + " ]";
    }
    
}
