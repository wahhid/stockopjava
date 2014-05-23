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
@Table(name = "refreshlog", catalog = "opnam", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Refreshlog.findAll", query = "SELECT r FROM Refreshlog r"),
    @NamedQuery(name = "Refreshlog.findById", query = "SELECT r FROM Refreshlog r WHERE r.id = :id"),
    @NamedQuery(name = "Refreshlog.findBySiteid", query = "SELECT r FROM Refreshlog r WHERE r.siteid = :siteid"),
    @NamedQuery(name = "Refreshlog.findByPeriodeid", query = "SELECT r FROM Refreshlog r WHERE r.periodeid = :periodeid"),
    @NamedQuery(name = "Refreshlog.findByRefreshdate", query = "SELECT r FROM Refreshlog r WHERE r.refreshdate = :refreshdate")})
public class Refreshlog implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "siteid")
    private String siteid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "periodeid")
    private int periodeid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "refreshdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date refreshdate;

    public Refreshlog() {
    }

    public Refreshlog(Integer id) {
        this.id = id;
    }

    public Refreshlog(Integer id, String siteid, int periodeid, Date refreshdate) {
        this.id = id;
        this.siteid = siteid;
        this.periodeid = periodeid;
        this.refreshdate = refreshdate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSiteid() {
        return siteid;
    }

    public void setSiteid(String siteid) {
        this.siteid = siteid;
    }

    public int getPeriodeid() {
        return periodeid;
    }

    public void setPeriodeid(int periodeid) {
        this.periodeid = periodeid;
    }

    public Date getRefreshdate() {
        return refreshdate;
    }

    public void setRefreshdate(Date refreshdate) {
        this.refreshdate = refreshdate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Refreshlog)) {
            return false;
        }
        Refreshlog other = (Refreshlog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.jakc.stockop.entity.Refreshlog[ id=" + id + " ]";
    }
    
}
