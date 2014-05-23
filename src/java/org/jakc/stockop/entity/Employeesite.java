/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jakc.stockop.entity;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author root
 */
@Entity
@Table(name = "employeesite", catalog = "opnam", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Employeesite.findAll", query = "SELECT e FROM Employeesite e"),
    @NamedQuery(name = "Employeesite.findById", query = "SELECT e FROM Employeesite e WHERE e.id = :id"),
    @NamedQuery(name = "Employeesite.findByNik", query = "SELECT e FROM Employeesite e WHERE e.nik = :nik"),
    @NamedQuery(name = "Employeesite.findBySiteid", query = "SELECT e FROM Employeesite e WHERE e.site.siteid = :siteid"),
    @NamedQuery(name = "Employeesite.findByDeleted", query = "SELECT e FROM Employeesite e WHERE e.deleted = :deleted")})
public class Employeesite implements Serializable {
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
    @Column(name = "nik")
    private String nik;
    @ManyToOne
    @JoinColumn(name="siteid")
    private Site site;    
    @Basic(optional = false)
    @NotNull
    @Column(name = "deleted")
    private boolean deleted;

    public Employeesite() {
    }

    public Employeesite(Integer id) {
        this.id = id;
    }

    public Employeesite(Integer id, String nik, Site site, boolean deleted) {
        this.id = id;
        this.nik = nik;
        this.site= site;
        this.deleted = deleted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }


    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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
        if (!(object instanceof Employeesite)) {
            return false;
        }
        Employeesite other = (Employeesite) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.jakc.stockop.entity.Employeesite[ id=" + id + " ]";
    }
    
}
