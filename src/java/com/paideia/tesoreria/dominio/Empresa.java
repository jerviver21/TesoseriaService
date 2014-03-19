/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.paideia.tesoreria.dominio;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
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
@Table(name = "empresa")
@NamedQueries({
    @NamedQuery(name = "Empresa.findAll", query = "SELECT e FROM Empresa e"),
    @NamedQuery(name = "Empresa.findByRuc", query = "SELECT e FROM Empresa e WHERE e.ruc =:ruc"),
    @NamedQuery(name = "Empresa.findByLicencia", query = "SELECT e FROM Empresa e WHERE e.noLicencia =:licencia")
})
public class Empresa implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 50)
    @Column(name = "ruc")
    private String ruc;
    @Size(max = 500)
    @Column(name = "nombre")
    private String nombre;
    
    
    @Size(max = 500)
    @Column(name = "telefono_fijo")
    private String fijo;
    @Size(max = 500)
    @Column(name = "telefono_celular")
    private String celular;
    @Size(max = 500)
    @Column(name = "persona_contacto")
    private String persona;
    @Size(max = 500)
    @Column(name = "email")
    private String email;
    
    @Column(name = "consecutivo")
    private Integer consecutivo;
    @Size(max = 500)
    @Column(name = "no_licencia")
    private String noLicencia;
    @OneToMany(mappedBy = "empresa", fetch = FetchType.EAGER)
    private List<CuentasEmpresa> cuentas;

    public Empresa() {
    }

    public Empresa(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(Integer consecutivo) {
        this.consecutivo = consecutivo;
    }

    public String getNoLicencia() {
        return noLicencia;
    }

    public void setNoLicencia(String noLicencia) {
        this.noLicencia = noLicencia;
    }

    public List<CuentasEmpresa> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<CuentasEmpresa> cuentasEmpresaList) {
        this.cuentas = cuentasEmpresaList;
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
        if (!(object instanceof Empresa)) {
            return false;
        }
        Empresa other = (Empresa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.paideia.tesoreria.dominio.Empresa[ id=" + id + " ]";
    }

    /**
     * @return the fijo
     */
    public String getFijo() {
        return fijo;
    }

    /**
     * @param fijo the fijo to set
     */
    public void setFijo(String fijo) {
        this.fijo = fijo;
    }

    /**
     * @return the celular
     */
    public String getCelular() {
        return celular;
    }

    /**
     * @param celular the celular to set
     */
    public void setCelular(String celular) {
        this.celular = celular;
    }

    /**
     * @return the persona
     */
    public String getPersona() {
        return persona;
    }

    /**
     * @param persona the persona to set
     */
    public void setPersona(String persona) {
        this.persona = persona;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
}
