/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.paideia.tesoreria.dominio;

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import javax.persistence.Transient;
import javax.validation.constraints.Size;

/**
 *
 * @author jerviver21
 */
@Entity
@Table(name = "registro_factura")
@NamedQueries({
    @NamedQuery(name = "RegistroFactura.findAll", query = "SELECT r FROM RegistroFactura r")})
public class RegistroFactura implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 255)
    @Column(name = "serie_fact")
    private String serieFact;
    @Size(max = 255)
    @Column(name = "no_factura")
    private String noFactura;
    @Size(max = 255)
    @Column(name = "area_entrega_fact")
    private String areaEntregaFact;
    @Size(max = 255)
    @Column(name = "persona_entrega_fact")
    private String personaEntregaFact;
    @Column(name = "fecha_emision_fact")
    @Temporal(TemporalType.DATE)
    private Date fechaEmisionFact;
    @Column(name = "fecha_entrega_fact")
    @Temporal(TemporalType.DATE)
    private Date fechaEntregaFact;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "monto_total")
    private BigDecimal montoTotal;
    @Size(max = 10)
    @Column(name = "moneda")
    private String moneda;
    @Column(name = "fecha_vencimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;
    @JoinColumn(name = "id_registro_pago", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RegistroPago registroPago;
    @JoinColumn(name = "id_proveedor", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Proveedor proveedor;
    @JoinColumn(name = "id_estado", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private EstadoPago estado;
    @JoinColumn(name = "id_empresa", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Empresa empresa;
    @JoinColumn(name = "id_cuenta_proveedor", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentasProveedor cuentaProveedor;
    @JoinColumn(name = "id_concepto", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ConceptoPago concepto;
    @JoinColumn(name = "id_acuerdo_pago", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AcuerdoPago acuerdoPago;
    
    @Transient
    private String extension;
    
    @Transient
    private InputStream img;

    public RegistroFactura() {
        proveedor = new Proveedor();
        acuerdoPago = new AcuerdoPago();
        concepto = new ConceptoPago();
        estado = new EstadoPago();
        cuentaProveedor = new CuentasProveedor();
        empresa = new Empresa();
    }

    public RegistroFactura(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerieFact() {
        return serieFact;
    }

    public void setSerieFact(String serieFact) {
        this.serieFact = serieFact;
    }

    public String getNoFactura() {
        return noFactura;
    }

    public void setNoFactura(String noFactura) {
        this.noFactura = noFactura;
    }

    public String getAreaEntregaFact() {
        return areaEntregaFact;
    }

    public void setAreaEntregaFact(String areaEntregaFact) {
        this.areaEntregaFact = areaEntregaFact;
    }

    public String getPersonaEntregaFact() {
        return personaEntregaFact;
    }

    public void setPersonaEntregaFact(String personaEntregaFact) {
        this.personaEntregaFact = personaEntregaFact;
    }

    public Date getFechaEmisionFact() {
        return fechaEmisionFact;
    }

    public void setFechaEmisionFact(Date fechaEmisionFact) {
        this.fechaEmisionFact = fechaEmisionFact;
    }

    public Date getFechaEntregaFact() {
        return fechaEntregaFact;
    }

    public void setFechaEntregaFact(Date fechaEntregaFact) {
        this.fechaEntregaFact = fechaEntregaFact;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public RegistroPago getRegistroPago() {
        return registroPago;
    }

    public void setRegistroPago(RegistroPago idRegistroPago) {
        this.registroPago = idRegistroPago;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor idProveedor) {
        this.proveedor = idProveedor;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago idEstado) {
        this.estado = idEstado;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa idEmpresa) {
        this.empresa = idEmpresa;
    }

    public CuentasProveedor getCuentaProveedor() {
        return cuentaProveedor;
    }

    public void setCuentaProveedor(CuentasProveedor idCuentaProveedor) {
        this.cuentaProveedor = idCuentaProveedor;
    }

    public ConceptoPago getConcepto() {
        return concepto;
    }

    public void setConcepto(ConceptoPago idConcepto) {
        this.concepto = idConcepto;
    }

    public AcuerdoPago getAcuerdoPago() {
        return acuerdoPago;
    }

    public void setAcuerdoPago(AcuerdoPago idAcuerdoPago) {
        this.acuerdoPago = idAcuerdoPago;
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
        if (!(object instanceof RegistroFactura)) {
            return false;
        }
        RegistroFactura other = (RegistroFactura) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.paideia.tesoreria.dominio.RegistroFactura[ id=" + id + " ]";
    }

    /**
     * @return the extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @param extension the extension to set
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * @return the img
     */
    public InputStream getImg() {
        return img;
    }

    /**
     * @param img the img to set
     */
    public void setImg(InputStream img) {
        this.img = img;
    }
    
}
