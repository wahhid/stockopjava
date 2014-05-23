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
import javax.persistence.OneToMany;
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
@Table(name = "transtockdetail", catalog = "opnam", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Transtockdetail.findAll", query = "SELECT t FROM Transtockdetail t"),
    @NamedQuery(name = "Transtockdetail.findByDetailid", query = "SELECT t FROM Transtockdetail t WHERE t.detailid = :detailid"),
    @NamedQuery(name = "Transtockdetail.findBySequence", query = "SELECT t FROM Transtockdetail t WHERE t.sequence = :sequence"),
    @NamedQuery(name = "Transtockdetail.findByTransid", query = "SELECT t FROM Transtockdetail t WHERE t.transstock.transid = :transid"),
    @NamedQuery(name = "Transtockdetail.findByProductid", query = "SELECT t FROM Transtockdetail t WHERE t.product.ean= :productid"),
    @NamedQuery(name = "Transtockdetail.findByTransdate", query = "SELECT t FROM Transtockdetail t WHERE t.transdate = :transdate"),
    @NamedQuery(name = "Transtockdetail.findByQty1", query = "SELECT t FROM Transtockdetail t WHERE t.qty1 = :qty1"),
    @NamedQuery(name = "Transtockdetail.findByQty2", query = "SELECT t FROM Transtockdetail t WHERE t.qty2 = :qty2"),
    @NamedQuery(name = "Transtockdetail.findByStatus", query = "SELECT t FROM Transtockdetail t WHERE t.status = :status"),
    @NamedQuery(name = "Transtockdetail.findByCreateddate", query = "SELECT t FROM Transtockdetail t WHERE t.createddate = :createddate"),
    @NamedQuery(name = "Transtockdetail.findByCreatedby", query = "SELECT t FROM Transtockdetail t WHERE t.createdby = :createdby"),
    @NamedQuery(name = "Transtockdetail.findByUpdateddate", query = "SELECT t FROM Transtockdetail t WHERE t.updateddate = :updateddate"),
    @NamedQuery(name = "Transtockdetail.findByUpdatedby", query = "SELECT t FROM Transtockdetail t WHERE t.updatedby = :updatedby")})
public class Transtockdetail implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "detailid")
    private Integer detailid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "sequence")
    private int sequence;    
    @ManyToOne
    @JoinColumn(name = "transid")
    private Transstock transstock;
    @ManyToOne
    @JoinColumn(name = "productid")
    private Product product;
    @Basic(optional = false)
    @NotNull
    @Column(name = "transdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date transdate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "qty1")
    private double qty1;
    @Basic(optional = false)
    @NotNull
    @Column(name = "qty2")
    private double qty2;
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

    public Transtockdetail() {
    }

    public Transtockdetail(Integer detailid) {
        this.detailid = detailid;
    }

    public Transtockdetail(Integer detailid, int sequence, Transstock transstock, Product product, Date transdate, double qty1, double qty2, boolean status, Date createddate, String createdby, Date updateddate, String updatedby) {
        this.detailid = detailid;
        this.sequence = sequence;
        this.transstock = transstock;
        this.product = product;
        this.transdate = transdate;
        this.qty1 = qty1;
        this.qty2 = qty2;
        this.status = status;
        this.createddate = createddate;
        this.createdby = createdby;
        this.updateddate = updateddate;
        this.updatedby = updatedby;
    }

    public Integer getDetailid() {
        return detailid;
    }

    public void setDetailid(Integer detailid) {
        this.detailid = detailid;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public Transstock getTransstock() {
        return transstock;
    }

    public void setTransstock(Transstock transstock) {
        this.transstock = transstock;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }



    public Date getTransdate() {
        return transdate;
    }

    public void setTransdate(Date transdate) {
        this.transdate = transdate;
    }

    public double getQty1() {
        return qty1;
    }

    public void setQty1(double qty1) {
        this.qty1 = qty1;
    }

    public double getQty2() {
        return qty2;
    }

    public void setQty2(double qty2) {
        this.qty2 = qty2;
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
        hash += (detailid != null ? detailid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Transtockdetail)) {
            return false;
        }
        Transtockdetail other = (Transtockdetail) object;
        if ((this.detailid == null && other.detailid != null) || (this.detailid != null && !this.detailid.equals(other.detailid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.jakc.stockop.entity.Transtockdetail[ detailid=" + detailid + " ]";
    }
    
}
