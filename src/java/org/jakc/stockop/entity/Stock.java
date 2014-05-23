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
 * @author wahhid
 */
@Entity
@Table(name = "stock", catalog = "opnam", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Stock.findAll", query = "SELECT s FROM Stock s"),
    @NamedQuery(name = "Stock.findById", query = "SELECT s FROM Stock s WHERE s.id = :id"),
    @NamedQuery(name = "Stock.findBySiteid", query = "SELECT s FROM Stock s WHERE s.site.siteid = :siteid"),
    @NamedQuery(name = "Stock.findByPid", query = "SELECT s FROM Stock s WHERE s.pid = :pid"),
    @NamedQuery(name = "Stock.findByItem", query = "SELECT s FROM Stock s WHERE s.item = :item"),
    @NamedQuery(name = "Stock.findByPeriodeid", query = "SELECT s FROM Stock s WHERE s.periode.periodeid = :periodeid"),
    @NamedQuery(name = "Stock.findByArticleid", query = "SELECT s FROM Stock s WHERE s.articleid = :articleid"),
    @NamedQuery(name = "Stock.findByQty", query = "SELECT s FROM Stock s WHERE s.qty = :qty"),
    @NamedQuery(name = "Stock.findByPendingqty", query = "SELECT s FROM Stock s WHERE s.pendingqty = :pendingqty"),
    @NamedQuery(name = "Stock.findByOpnamqty", query = "SELECT s FROM Stock s WHERE s.opnamqty = :opnamqty"),
    @NamedQuery(name = "Stock.findByUnitcost", query = "SELECT s FROM Stock s WHERE s.unitcost = :unitcost"),
    @NamedQuery(name = "Stock.findByDifqty", query = "SELECT s FROM Stock s WHERE s.difqty = :difqty"),
    @NamedQuery(name = "Stock.findByDifunitcost", query = "SELECT s FROM Stock s WHERE s.difunitcost = :difunitcost"),
    @NamedQuery(name = "Stock.findByPrinted", query = "SELECT s FROM Stock s WHERE s.printed = :printed"),
    @NamedQuery(name = "Stock.findByRefreshdate", query = "SELECT s FROM Stock s WHERE s.refreshdate = :refreshdate"),
    @NamedQuery(name = "Stock.findByCreateddate", query = "SELECT s FROM Stock s WHERE s.createddate = :createddate"),
    @NamedQuery(name = "Stock.findByCreatedby", query = "SELECT s FROM Stock s WHERE s.createdby = :createdby"),
    @NamedQuery(name = "Stock.findByUpdateddate", query = "SELECT s FROM Stock s WHERE s.updateddate = :updateddate"),
    @NamedQuery(name = "Stock.findByUpdatedby", query = "SELECT s FROM Stock s WHERE s.updatedby = :updatedby")})
public class Stock implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "siteid")
    private Site site;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "pid")
    private String pid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "item")
    private int item;    
    @ManyToOne
    @JoinColumn(name = "periodeid")
    private Periode periode;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "articleid")
    private String articleid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "qty")
    private double qty;
    @Basic(optional = false)
    @NotNull
    @Column(name = "pendingqty")
    private double pendingqty;
    @Basic(optional = false)
    @NotNull
    @Column(name = "opnamqty")
    private double opnamqty;
    @Basic(optional = false)
    @NotNull
    @Column(name = "unitcost")
    private double unitcost;
    @Basic(optional = false)
    @NotNull
    @Column(name = "difqty")
    private double difqty;
    @Basic(optional = false)
    @NotNull
    @Column(name = "difunitcost")
    private double difunitcost;
    @Basic(optional = false)
    @NotNull
    @Column(name = "printed")
    private boolean printed;
    @Column(name = "refreshdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date refreshdate;
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

    public Stock() {
        
    }

    public Stock(Integer id) {
        this.id = id;
    }

    public Stock(Integer id, Site site, String pid, int item, Periode periode, String articleid, double qty, double pendingqty, double opnamqty, double unitcost, double difqty, double difunitcost, boolean printed, Date createddate, String createdby, Date updateddate, String updatedby) {
        this.id = id;
        this.site = site;
        this.pid = pid;
        this.item = item;
        this.periode = periode;
        this.articleid = articleid;
        this.qty = qty;
        this.pendingqty = pendingqty;
        this.opnamqty = opnamqty;
        this.unitcost = unitcost;
        this.difqty = difqty;
        this.difunitcost = difunitcost;
        this.printed = printed;
        this.createddate = createddate;
        this.createdby = createdby;
        this.updateddate = updateddate;
        this.updatedby = updatedby;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }



    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public Periode getPeriode() {
        return periode;
    }

    public void setPeriode(Periode periode) {
        this.periode = periode;
    }



    public String getArticleid() {
        return articleid;
    }

    public void setArticleid(String articleid) {
        this.articleid = articleid;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getPendingqty() {
        return pendingqty;
    }

    public void setPendingqty(double pendingqty) {
        this.pendingqty = pendingqty;
    }

    public double getOpnamqty() {
        return opnamqty;
    }

    public void setOpnamqty(double opnamqty) {
        this.opnamqty = opnamqty;
    }

    public double getUnitcost() {
        return unitcost;
    }

    public void setUnitcost(double unitcost) {
        this.unitcost = unitcost;
    }

    public double getDifqty() {
        return difqty;
    }

    public void setDifqty(double difqty) {
        this.difqty = difqty;
    }

    public double getDifunitcost() {
        return difunitcost;
    }

    public void setDifunitcost(double difunitcost) {
        this.difunitcost = difunitcost;
    }

    public boolean getPrinted() {
        return printed;
    }

    public void setPrinted(boolean printed) {
        this.printed = printed;
    }

    public Date getRefreshdate() {
        return refreshdate;
    }

    public void setRefreshdate(Date refreshdate) {
        this.refreshdate = refreshdate;
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Stock)) {
            return false;
        }
        Stock other = (Stock) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.jakc.stockop.entity.Stock[ id=" + id + " ]";
    }
    
}
