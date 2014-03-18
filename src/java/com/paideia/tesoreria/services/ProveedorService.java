/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.paideia.tesoreria.services;

import com.paideia.tesoreria.dominio.CuentasEmpresa;
import com.paideia.tesoreria.dominio.CuentasProveedor;
import com.paideia.tesoreria.dominio.Proveedor;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author jerviver21
 */
@Stateless
@LocalBean
public class ProveedorService {
    @PersistenceContext(unitName = "TesoseriaServicePU")
    private EntityManager em;
    
    public Proveedor findProveedorByRuc(String ruc){
        List<Proveedor> proveedor = em.createNamedQuery("Proveedor.findByRuc").setParameter("ruc", ruc).getResultList();
        if(proveedor.isEmpty()){
            return null;
        }
        return proveedor.get(0);
    }
    
    public CuentasProveedor findCuentaByNum(String no){
        List<CuentasProveedor> cuenta = em.createNamedQuery("CuentasProveedor.findByNo").setParameter("no", no).getResultList();
        if(cuenta.isEmpty()){
            return null;
        }
        return cuenta.get(0);
    }
    
    public Proveedor saveProveedor(Proveedor proveedor){
        proveedor.setInfoActualizada(Boolean.TRUE);
        return  em.merge(proveedor);
    }
    
    public CuentasProveedor saveCuenta(CuentasProveedor cuenta){
        return em.merge(cuenta);
    }

    public void borrarCuenta(CuentasProveedor cb) {
        em.remove(em.merge(cb));
    }

}
