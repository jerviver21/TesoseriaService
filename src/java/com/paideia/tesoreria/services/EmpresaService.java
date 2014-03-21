
package com.paideia.tesoreria.services;

import com.paideia.tesoreria.dominio.CuentasEmpresa;
import com.paideia.tesoreria.dominio.Empresa;
import com.vi.comun.util.FechaUtils;
import com.vi.usuarios.dominio.Groups;
import com.vi.usuarios.dominio.Licencia;
import com.vi.usuarios.dominio.Users;
import com.vi.usuarios.services.GruposServicesLocal;
import com.vi.usuarios.services.LicenciaService;
import com.vi.usuarios.services.UsuariosServicesLocal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
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
    
    @EJB
    private UsuariosServicesLocal uService;
    
    @EJB
    private LicenciaService lService;
    
    @EJB
    GruposServicesLocal gService;

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
    
    public Empresa saveEmpresa(Empresa empresa){
        return em.merge(empresa);
    }
    
    public CuentasEmpresa saveCuenta(CuentasEmpresa cuenta){
        return em.merge(cuenta);
    }
    
    
    public void registrarEmpresa(Empresa empresa, Users usr)throws Exception{
        Licencia licencia = new Licencia();
        licencia.setFechaInicio(new Date());
        licencia.setFechaFin(FechaUtils.getFechaMasPeriodo(new Date(), 31, Calendar.DATE));
        licencia.setOwner(empresa.getNombre());
        
        licencia = lService.create(licencia);
        
        usr.setLicencia(licencia);
        usr.setUsr(empresa.getRuc().trim());
        usr.setNumId(empresa.getRuc().trim());
        usr.setEstado(0);
        usr.setNombre(empresa.getNombre());
        usr.setMail(empresa.getEmail());
        
        Groups grupo = gService.findByCodigo("TESOREROS");
        List<Groups> grupos = new ArrayList<Groups>();
        grupos.add(grupo);
        usr.setGrupos(grupos);
        
        uService.create(usr);
        
        empresa.setNoLicencia(licencia.getNoLicencia());
        em.merge(empresa);
        
    }
}
