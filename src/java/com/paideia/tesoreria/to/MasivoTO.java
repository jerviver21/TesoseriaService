/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.paideia.tesoreria.to;

/**
 *
 * @author jerviver21
 */
public class MasivoTO {
    private String ruta;
    private int numUsuariosCargados = 0;

    /**
     * @return the ruta
     */
    public String getRuta() {
        return ruta;
    }

    /**
     * @param ruta the ruta to set
     */
    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    /**
     * @return the numUsuariosCargados
     */
    public int getNumUsuariosCargados() {
        return numUsuariosCargados;
    }

    /**
     * @param numUsuariosCargados the numUsuariosCargados to set
     */
    public void setNumUsuariosCargados(int numUsuariosCargados) {
        this.numUsuariosCargados = numUsuariosCargados;
    }
    
}
