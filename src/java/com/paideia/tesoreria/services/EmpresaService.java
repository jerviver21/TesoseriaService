
package com.paideia.tesoreria.services;

import com.paideia.tesoreria.dominio.CuentasEmpresa;
import com.paideia.tesoreria.dominio.Empresa;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author jerviver21
 */
@Stateless
@LocalBean
public class EmpresaService {
    @PersistenceContext(unitName = "TesoseriaServicePU")
    private EntityManager em;

    public Empresa findEmpresaByRuc(String ruc){
        List<Empresa> empresa = em.createNamedQuery("Empresa.findByRuc").setParameter("ruc", ruc).getResultList();
        if(empresa.isEmpty()){
            return null;
        }
        return empresa.get(0);
    }
    
     public Empresa findEmpresaByLicencia(String licencia){
        List<Empresa> empresa = em.createNamedQuery("Empresa.findByLicencia").setParameter("licencia", licencia).getResultList();
        if(empresa.isEmpty()){
            return null;
        }
        return empresa.get(0);
    }
    
    public CuentasEmpresa findCuentaByNum(String no){
        List<CuentasEmpresa> cuenta = em.createNamedQuery("CuentasEmpresa.findByNo").setParameter("no", no).getResultList();
        if(cuenta.isEmpty()){
            return null;
        }
        return cuenta.get(0);
    }
    
    public Empresa saveEmpresa(Empresa proveedor){
        return em.merge(proveedor);
    }
    
    public CuentasEmpresa saveCuenta(CuentasEmpresa cuenta){
        return em.merge(cuenta);
    }
}
