/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.paideia.tesoreria.dominio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author jerviver21
 */
@Entity
@Table(name = "proveedor")
@NamedQueries({
    @NamedQuery(name = "Proveedor.findAll", query = "SELECT p FROM Proveedor p"),
    @NamedQuery(name = "Proveedor.findByRuc", query = "SELECT p FROM Proveedor p WHERE p.ruc =:ruc")
})
public class Proveedor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 500)
    @Column(name = "razon_social")
    private String razonSocial;
    @Size(max = 255)
    @Column(name = "ruc")
    private String ruc;
    @Size(max = 25)
    @Column(name = "no_cuenta_detraccion")
    private String noCuentaDetraccion;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 255)
    @Column(name = "email")
    private String email;
    @Size(max = 255)
    @Column(name = "persona_contacto")
    private String personaContacto;
    @Size(max = 255)
    @Column(name = "telefono_celular")
    private String telefonoCelular;
    @Size(max = 255)
    @Column(name = "telefono_fijo")
    private String telefonoFijo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "proveedor", fetch = FetchType.EAGER)
    private List<CuentasProveedor> cuentas;
    
    @Column(name = "informacion_actualizada")
    private Boolean infoActualizada;

    public Proveedor() {
        cuentas = new ArrayList<CuentasProveedor>();
    }

    public Proveedor(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getNoCuentaDetraccion() {
        return noCuentaDetraccion;
    }

    public void setNoCuentaDetraccion(String noCuentaDetraccion) {
        this.noCuentaDetraccion = noCuentaDetraccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersonaContacto() {
        return personaContacto;
    }

    public void setPersonaContacto(String personaContacto) {
        this.personaContacto = personaContacto;
    }

    public String getTelefonoCelular() {
        return telefonoCelular;
    }

    public void setTelefonoCelular(String telefonoCelular) {
        this.telefonoCelular = telefonoCelular;
    }

    public String getTelefonoFijo() {
        return telefonoFijo;
    }

    public void setTelefonoFijo(String telefonoFijo) {
        this.telefonoFijo = telefonoFijo;
    }

    public List<CuentasProveedor> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<CuentasProveedor> cuentasProveedorList) {
        this.cuentas = cuentasProveedorList;
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
        if (!(object instanceof Proveedor)) {
            return false;
        }
        Proveedor other = (Proveedor) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.paideia.tesoreria.dominio.Proveedor[ id=" + id + " ]";
    }

    /**
     * @return the infoActualizada
     */
    public Boolean getInfoActualizada() {
        return infoActualizada;
    }

    /**
     * @param infoActualizada the infoActualizada to set
     */
    public void setInfoActualizada(Boolean infoActualizada) {
        this.infoActualizada = infoActualizada;
    }
    
}
