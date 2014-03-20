/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.paideia.tesoreria.dominio;

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
@Table(name = "detraccion")
@NamedQueries({
    @NamedQuery(name = "Detraccion.findAll", query = "SELECT d FROM Detraccion d"),
    @NamedQuery(name = "Detraccion.findByFact", query = "SELECT d FROM Detraccion d WHERE d.noFactura =:no"),
    @NamedQuery(name = "Detraccion.findDetraccion", query = "SELECT d FROM Detraccion d WHERE d.fechaCarga =:fecha AND d.archivoIn =:in"),
    @NamedQuery(name = "Detraccion.findForComprobante", query = "SELECT d FROM Detraccion d "
            + "WHERE d.archivoOut =:archivo AND d.codServicio =:servicio AND d.codOperacion =:opr AND "
            + "d.periodoTributario =:periodo AND d.proveedor.ruc =:ruc AND d.importe =:importe ")
})
public class Detraccion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 10)
    @Column(name = "cod_servicio")
    private String codServicio;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "importe")
    private BigDecimal importe;
    @Size(max = 10)
    @Column(name = "cod_operacion")
    private String codOperacion;
    @Size(max = 6)
    @Column(name = "periodo_tributario")
    private String periodoTributario;
    @Column(name = "fecha_carga")
    @Temporal(TemporalType.DATE)
    private Date fechaCarga;
    
    @Column(name = "fecha_emision")
    @Temporal(TemporalType.DATE)
    private Date fechaEmision;
    
    @Column(name = "base_imponible")
    private BigDecimal base;
    @Column(name = "monto_total")
    private BigDecimal montoTotal;
    @Column(name = "porcentaje")
    private Integer porcentaje;
    
    @Column(name = "nombre_servicio")
    private String nombreServicio;
    
    
    
    
    @Size(max = 255)
    @Column(name = "comprobante")
    private String comprobante;
    @Size(max = 255)
    @Column(name = "no_factura")
    private String noFactura;
    @Column(name = "fecha_pago")
    @Temporal(TemporalType.DATE)
    private Date fechaPago;
    @Size(max = 255)
    @Column(name = "archivo_in")
    private String archivoIn;
    @Size(max = 255)
    @Column(name = "archivo_out")
    private String archivoOut;
    @JoinColumn(name = "id_proveedor", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Proveedor proveedor;
    @JoinColumn(name = "id_empresa", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Empresa empresa;
    
    @Transient
    private boolean cargadoComprobante = false;

    public Detraccion() {
        proveedor = new Proveedor();
    }

    public Detraccion(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodServicio() {
        return codServicio;
    }

    public void setCodServicio(String codServicio) {
        this.codServicio = codServicio;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getCodOperacion() {
        return codOperacion;
    }

    public void setCodOperacion(String codOperacion) {
        this.codOperacion = codOperacion;
    }

    public String getPeriodoTributario() {
        return periodoTributario;
    }

    public void setPeriodoTributario(String periodoTributario) {
        this.periodoTributario = periodoTributario;
    }

    public Date getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(Date fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public String getNoFactura() {
        return noFactura;
    }

    public void setNoFactura(String noFactura) {
        this.noFactura = noFactura;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getArchivoIn() {
        return archivoIn;
    }

    public void setArchivoIn(String archivoIn) {
        this.archivoIn = archivoIn;
    }

    public String getArchivoOut() {
        return archivoOut;
    }

    public void setArchivoOut(String archivoOut) {
        this.archivoOut = archivoOut;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor idProveedor) {
        this.proveedor = idProveedor;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa idEmpresa) {
        this.empresa = idEmpresa;
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
        if (!(object instanceof Detraccion)) {
            return false;
        }
        Detraccion other = (Detraccion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.paideia.tesoreria.dominio.Detraccion[ id=" + id + " ]";
    }

    /**
     * @return the cargadoComprobante
     */
    public boolean isCargadoComprobante() {
        return cargadoComprobante;
    }

    /**
     * @param cargadoComprobante the cargadoComprobante to set
     */
    public void setCargadoComprobante(boolean cargadoComprobante) {
        this.cargadoComprobante = cargadoComprobante;
    }

    /**
     * @return the fechaEmision
     */
    public Date getFechaEmision() {
        return fechaEmision;
    }

    /**
     * @param fechaEmision the fechaEmision to set
     */
    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    /**
     * @return the base
     */
    public BigDecimal getBase() {
        return base;
    }

    /**
     * @param base the base to set
     */
    public void setBase(BigDecimal base) {
        this.base = base;
    }

    /**
     * @return the montoTotal
     */
    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    /**
     * @param montoTotal the montoTotal to set
     */
    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    /**
     * @return the porcentaje
     */
    public Integer getPorcentaje() {
        return porcentaje;
    }

    /**
     * @param porcentaje the porcentaje to set
     */
    public void setPorcentaje(Integer porcentaje) {
        this.porcentaje = porcentaje;
    }

    /**
     * @return the nombreServicio
     */
    public String getNombreServicio() {
        return nombreServicio;
    }

    /**
     * @param nombreServicio the nombreServicio to set
     */
    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }
    
}
