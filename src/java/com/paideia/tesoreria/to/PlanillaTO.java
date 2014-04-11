/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.paideia.tesoreria.to;

import com.paideia.tesoreria.dominio.InconsistenciaPlanilla;
import com.paideia.tesoreria.dominio.Semt;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jerviver21
 */
public class PlanillaTO {
    private List<Semt> semts;
    private List<InconsistenciaPlanilla> planillas;


    public PlanillaTO(){
        semts = new ArrayList();
        planillas = new ArrayList();
    }
    
    /**
     * @return the planillas
     */
    public List<InconsistenciaPlanilla> getPlanillas() {
        return planillas;
    }

    /**
     * @param planillas the planillas to set
     */
    public void setPlanillas(List<InconsistenciaPlanilla> planillas) {
        this.planillas = planillas;
    }

    /**
     * @return the semtsSoles
     */
    public List<Semt> getSemts() {
        return semts;
    }

    /**
     * @param semtsSoles the semtsSoles to set
     */
    public void setSemts(List<Semt> semtsSoles) {
        this.semts = semtsSoles;
    }


}
