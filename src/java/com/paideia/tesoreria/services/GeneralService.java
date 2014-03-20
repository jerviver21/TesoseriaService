/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.paideia.tesoreria.services;

import com.paideia.tesoreria.dominio.EstadoPago;
import com.paideia.tesoreria.dominio.Notificacion;
import com.paideia.tesoreria.dominio.Servicios;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class GeneralService {

    @PersistenceContext(unitName = "TesoseriaServicePU")
    private EntityManager em;

    public EstadoPago findEstado(String nombre){
        List<EstadoPago> estado = em.createNamedQuery("EstadoPago.findByNombre").setParameter("nombre", nombre).getResultList();
        if(estado.isEmpty()){
            return null;
        }
        return estado.get(0);
    }
    
    public EstadoPago saveEstado(EstadoPago estado){
        int id = em.createNamedQuery("EstadoPago.findAll").getResultList().size()+1;
        estado.setId(id);
        return em.merge(estado);
    }
    
    public Notificacion findNotificacion(String mensaje){
        List<Notificacion> msj = em.createNamedQuery("Notificacion.findByMensaje").setParameter("mensaje", mensaje).getResultList();
        if(msj.isEmpty()){
            return null;
        }
        return msj.get(0);
        
    }

    public Map getMapServiciosXNombre() {
        Map<String, String> mapa = new HashMap();
        List<Servicios> servicios = em.createNamedQuery("Servicios.findAll").getResultList();
        for(Servicios servicio: servicios){
            mapa.put(servicio.getNombre(), servicio.getCodigo());
        }
        return mapa;
    }

    public void cargarServicio(String codigo, String descripcion) {
        Servicios servicio = new Servicios();
        servicio.setCodigo(codigo.trim());
        servicio.setNombre(descripcion.trim().toUpperCase());
        em.merge(servicio);
    }

}
