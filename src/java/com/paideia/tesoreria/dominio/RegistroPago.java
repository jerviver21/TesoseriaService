/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.paideia.tesoreria.dominio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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
import javax.persistence.OneToMany;
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
@Table(name = "registro_pago")
@NamedQueries({
    @NamedQuery(name = "RegistroPago.findAll", query = "SELECT r FROM RegistroPago r"),
    @NamedQuery(name = "RegistroPago.findRegistro", query = "SELECT r FROM RegistroPago r WHERE r.noPlanilla =:planilla AND r.consecutivo =:cons AND r.fechaPago =:fecha")
})
public class RegistroPago implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 255)
    @Column(name = "tipo_operacion")
    private String tipoOperacion;
    @Size(max = 255)
    @Column(name = "subtipo_operacion")
    private String subtipoOperacion;
    @Size(max = 255)
    @Column(name = "no_planilla")
    private String noPlanilla;
    @Column(name = "fecha_pago")
    @Temporal(TemporalType.DATE)
    private Date fechaPago;
    @Size(max = 255)
    @Column(name = "rp")
    private String rp;
    @Column(name = "consecutivo")
    private Integer consecutivo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "monto")
    private BigDecimal monto;
    @Column(name = "monto_abonado")
    private BigDecimal montoAbonado;
    @Size(max = 255)
    @Column(name = "observacion")
    private String observacion;
    @Size(max = 255)
    @Column(name = "otra_informacion")
    private String otraInformacion;
    @Size(max = 255)
    @Column(name = "no_factura")
    private String noFactura;
    @Size(max = 255)
    @Column(name = "no_detraccion")
    private String noDetraccion;
    @Size(max = 1000)
    @Column(name = "sede_empresa")
    private String sedeEmpresa;
    @Size(max = 1000)
    @Column(name = "nombre")
    private String nombre;
    
    @Size(max = 10)
    @Column(name = "mon_org")
    private String monOrg;
    @Size(max = 10)
    @Column(name = "mon_des")
    private String monDes;
    
    @Size(max = 255)
    @Column(name = "desc_destino")
    private String descDestino;
    @JoinColumn(name = "id_proveedor", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Proveedor proveedor;
    @JoinColumn(name = "id_estado", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private EstadoPago estado;
    @JoinColumn(name = "id_empresa", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Empresa empresa;
    @JoinColumn(name = "id_cuenta_destino", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentasProveedor cuentaDestino;
    @JoinColumn(name = "id_cuenta_origen", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuentasEmpresa cuentaOrigen;
    
    
    @Transient
    private boolean cargado = false;

    public RegistroPago() {
    }

    public RegistroPago(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public String getSubtipoOperacion() {
        return subtipoOperacion;
    }

    public void setSubtipoOperacion(String subtipoOperacion) {
        this.subtipoOperacion = subtipoOperacion;
    }

    public String getNoPlanilla() {
        return noPlanilla;
    }

    public void setNoPlanilla(String noPlanilla) {
        this.noPlanilla = noPlanilla;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getRp() {
        return rp;
    }

    public void setRp(String rp) {
        this.rp = rp;
    }

    public Integer getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(Integer consecutivo) {
        this.consecutivo = consecutivo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public BigDecimal getMontoAbonado() {
        return montoAbonado;
    }

    public void setMontoAbonado(BigDecimal montoAbonado) {
        this.montoAbonado = montoAbonado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getOtraInformacion() {
        return otraInformacion;
    }

    public void setOtraInformacion(String otraInformacion) {
        this.otraInformacion = otraInformacion;
    }

    public String getNoFactura() {
        return noFactura;
    }

    public void setNoFactura(String noFactura) {
        this.noFactura = noFactura;
    }

    public String getNoDetraccion() {
        return noDetraccion;
    }

    public void setNoDetraccion(String noDetraccion) {
        this.noDetraccion = noDetraccion;
    }

    public String getDescDestino() {
        return descDestino;
    }

    public void setDescDestino(String descDestino) {
        this.descDestino = descDestino;
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

    public CuentasProveedor getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(CuentasProveedor idCuentaDestino) {
        this.cuentaDestino = idCuentaDestino;
    }

    public CuentasEmpresa getCuentaOrigen() {
        return cuentaOrigen;
    }

    public void setCuentaOrigen(CuentasEmpresa idCuentaOrigen) {
        this.cuentaOrigen = idCuentaOrigen;
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
        if (!(object instanceof RegistroPago)) {
            return false;
        }
        RegistroPago other = (RegistroPago) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.paideia.tesoreria.dominio.RegistroPago[ id=" + id + " ]";
    }

    /**
     * @return the cargado
     */
    public boolean isCargado() {
        return cargado;
    }

    /**
     * @param cargado the cargado to set
     */
    public void setCargado(boolean cargado) {
        this.cargado = cargado;
    }

    /**
     * @return the sedeEmpresa
     */
    public String getSedeEmpresa() {
        return sedeEmpresa;
    }

    /**
     * @param sedeEmpresa the sedeEmpresa to set
     */
    public void setSedeEmpresa(String sedeEmpresa) {
        this.sedeEmpresa = sedeEmpresa;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the monOrg
     */
    public String getMonOrg() {
        return monOrg;
    }

    /**
     * @param monOrg the monOrg to set
     */
    public void setMonOrg(String monOrg) {
        this.monOrg = monOrg;
    }

    /**
     * @return the monDes
     */
    public String getMonDes() {
        return monDes;
    }

    /**
     * @param monDes the monDes to set
     */
    public void setMonDes(String monDes) {
        this.monDes = monDes;
    }
    
}
