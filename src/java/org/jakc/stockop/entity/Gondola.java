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
@Table(name = "gondola", catalog = "opnam", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Gondola.findAll", query = "SELECT g FROM Gondola g"),
    @NamedQuery(name = "Gondola.findByGondolaid", query = "SELECT g FROM Gondola g WHERE g.gondolaid = :gondolaid"),
    @NamedQuery(name = "Gondola.findByGondolaname", query = "SELECT g FROM Gondola g WHERE g.gondolaname = :gondolaname"),
    @NamedQuery(name = "Gondola.findByDescription", query = "SELECT g FROM Gondola g WHERE g.description = :description"),
    @NamedQuery(name = "Gondola.findBySiteid", query = "SELECT g FROM Gondola g WHERE g.site.siteid = :siteid"),
    @NamedQuery(name = "Gondola.findByStatus", query = "SELECT g FROM Gondola g WHERE g.status = :status"),
    @NamedQuery(name = "Gondola.findByDeleted", query = "SELECT g FROM Gondola g WHERE g.deleted = :deleted"),
    @NamedQuery(name = "Gondola.findByCreateddate", query = "SELECT g FROM Gondola g WHERE g.createddate = :createddate"),
    @NamedQuery(name = "Gondola.findByCreatedby", query = "SELECT g FROM Gondola g WHERE g.createdby = :createdby"),
    @NamedQuery(name = "Gondola.findByUpdateddate", query = "SELECT g FROM Gondola g WHERE g.updateddate = :updateddate"),
    @NamedQuery(name = "Gondola.findByUpdatedby", query = "SELECT g FROM Gondola g WHERE g.updatedby = :updatedby")})
public class Gondola implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "gondolaid")
    private String gondolaid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "gondolaname")
    private String gondolaname;    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "description")
    private String description;       
    @ManyToOne
    @JoinColumn(name="siteid")
    private Site site;    
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

    public Gondola() {
    }

    public Gondola(String gondolaid) {
        this.gondolaid = gondolaid;
    }

    public Gondola(String gondolaid, String gondolaname, Site site, boolean status, boolean deleted, Date createddate, String createdby, Date updateddate, String updatedby) {
        this.gondolaid = gondolaid;
        this.gondolaname = gondolaname;
        this.site = site;
        this.status = status;
        this.deleted = deleted;
        this.createddate = createddate;
        this.createdby = createdby;
        this.updateddate = updateddate;
        this.updatedby = updatedby;
    }

    public String getGondolaid() {
        return gondolaid;
    }

    public void setGondolaid(String gondolaid) {
        this.gondolaid = gondolaid;
    }

    public String getGondolaname() {
        return gondolaname;
    }

    public void setGondolaname(String gondolaname) {
        this.gondolaname = gondolaname;
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
        hash += (gondolaid != null ? gondolaid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Gondola)) {
            return false;
        }
        Gondola other = (Gondola) object;
        if ((this.gondolaid == null && other.gondolaid != null) || (this.gondolaid != null && !this.gondolaid.equals(other.gondolaid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.jakc.stockop.entity.Gondola[ gondolaid=" + gondolaid + " ]";
    }
    
}
