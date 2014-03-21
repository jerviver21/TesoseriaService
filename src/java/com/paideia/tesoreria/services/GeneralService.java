/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.paideia.tesoreria.services;

import com.paideia.tesoreria.dominio.EstadoPago;
import com.paideia.tesoreria.dominio.Notificacion;
import com.paideia.tesoreria.dominio.Service;
import com.vi.comun.exceptions.LlaveDuplicadaException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.exception.ConstraintViolationException;

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
        List<Service> servicios = em.createNamedQuery("Service.findAll").getResultList();
        for(Service servicio: servicios){
            mapa.put(servicio.getDescripcion().trim(), servicio.getCod().trim());
        }
        return mapa;
    }

    public void cargarServicio(String codigo, String descripcion)throws LlaveDuplicadaException, Exception{
        Service servicio = new Service();
        servicio.setCod(codigo.trim());
        servicio.setDescripcion(descripcion.trim().toUpperCase());
        try {
            em.merge(servicio);
        } catch (Exception e) {
            if(e instanceof ConstraintViolationException || (e.getCause() != null && e.getCause() instanceof ConstraintViolationException)){
                throw new LlaveDuplicadaException("El servicio "+descripcion+" ya existe");
            }
            throw e;
        }
    }

}
