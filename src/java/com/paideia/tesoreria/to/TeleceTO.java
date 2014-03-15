
package com.paideia.tesoreria.to;

import com.paideia.tesoreria.dominio.Notificacion;
import com.paideia.tesoreria.dominio.RegistroPago;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerson Viveros Aguirre
 */
public class TeleceTO {
    
    private List<RegistroPago> pagos = new ArrayList();
    private List<Notificacion> notificacion = new ArrayList();
    
    private int noInconsistencias = 0;
    
    public void incrementInconsistencias(){
        noInconsistencias++;
    }

    /**
     * @return the pagos
     */
    public List<RegistroPago> getPagos() {
        return pagos;
    }

    /**
     * @param pagos the pagos to set
     */
    public void setPagos(List<RegistroPago> pagos) {
        this.pagos = pagos;
    }

    /**
     * @return the notificacion
     */
    public List<Notificacion> getNotificaciones() {
        return notificacion;
    }

    /**
     * @param notificacion the notificacion to set
     */
    public void setNotificacion(List<Notificacion> notificacion) {
        this.notificacion = notificacion;
    }

    /**
     * @return the noInconsistencias
     */
    public int getNoInconsistencias() {
        return noInconsistencias;
    }

    /**
     * @param noInconsistencias the noInconsistencias to set
     */
    public void setNoInconsistencias(int noInconsistencias) {
        this.noInconsistencias = noInconsistencias;
    }
    
}
