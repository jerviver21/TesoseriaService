/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.paideia.tesoreria.utils;

import com.paideia.tesoreria.dominio.EstadoPago;

/**
 *
 * @author jerviver21
 */
public class EstadosPago {
    public static EstadoPago CANCELADO = new EstadoPago(0);
    public static EstadoPago PENDIENTE = new EstadoPago(1);
    public static EstadoPago RECHAZADA = new EstadoPago(3);
    public static EstadoPago PROCESADA = new EstadoPago(2);
    
}
