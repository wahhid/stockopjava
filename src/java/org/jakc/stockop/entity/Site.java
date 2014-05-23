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
@Table(name = "site", catalog = "opnam", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Site.findAll", query = "SELECT s FROM Site s"),
    @NamedQuery(name = "Site.findBySiteid", query = "SELECT s FROM Site s WHERE s.siteid = :siteid"),
    @NamedQuery(name = "Site.findByBranchname", query = "SELECT s FROM Site s WHERE s.branchname = :branchname"),
    @NamedQuery(name = "Site.findByDeleted", query = "SELECT s FROM Site s WHERE s.deleted = :deleted"),
    @NamedQuery(name = "Site.findByCreateddate", query = "SELECT s FROM Site s WHERE s.createddate = :createddate"),
    @NamedQuery(name = "Site.findByCreatedby", query = "SELECT s FROM Site s WHERE s.createdby = :createdby"),
    @NamedQuery(name = "Site.findByUpdateddate", query = "SELECT s FROM Site s WHERE s.updateddate = :updateddate"),
    @NamedQuery(name = "Site.findByUpdatedby", query = "SELECT s FROM Site s WHERE s.updatedby = :updatedby")})
public class Site implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "siteid")
    private String siteid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "branchname")
    private String branchname;
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

    public Site() {
    }

    public Site(String siteid) {
        this.siteid = siteid;
    }

    public Site(String siteid, String branchname, boolean deleted, Date createddate, String createdby, Date updateddate, String updatedby) {
        this.siteid = siteid;
        this.branchname = branchname;
        this.deleted = deleted;
        this.createddate = createddate;
        this.createdby = createdby;
        this.updateddate = updateddate;
        this.updatedby = updatedby;
    }

    public String getSiteid() {
        return siteid;
    }

    public void setSiteid(String siteid) {
        this.siteid = siteid;
    }

    public String getBranchname() {
        return branchname;
    }

    public void setBranchname(String branchname) {
        this.branchname = branchname;
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
        hash += (siteid != null ? siteid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Site)) {
            return false;
        }
        Site other = (Site) object;
        if ((this.siteid == null && other.siteid != null) || (this.siteid != null && !this.siteid.equals(other.siteid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.jakc.stockop.entity.Site[ siteid=" + siteid + " ]";
    }
    
}
