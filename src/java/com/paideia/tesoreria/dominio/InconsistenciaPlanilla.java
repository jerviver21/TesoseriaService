/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.paideia.tesoreria.dominio;

/**
 *
 * @author jerviver21
 */
public class InconsistenciaPlanilla {
    private String nombreArchivo;
    private int noFila;
    private String descripcionInconsistencia;

    /**
     * @return the nombreArchivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * @param nombreArchivo the nombreArchivo to set
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * @return the noFila
     */
    public int getNoFila() {
        return noFila;
    }

    /**
     * @param noFila the noFila to set
     */
    public void setNoFila(int noFila) {
        this.noFila = noFila;
    }

    /**
     * @return the descripcionInconsistencia
     */
    public String getDescripcionInconsistencia() {
        return descripcionInconsistencia;
    }

    /**
     * @param descripcionInconsistencia the descripcionInconsistencia to set
     */
    public void setDescripcionInconsistencia(String descripcionInconsistencia) {
        this.descripcionInconsistencia = descripcionInconsistencia;
    }
    
    
    
}
