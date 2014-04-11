/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.paideia.tesoreria.services;

import com.paideia.tesoreria.dominio.CuentasProveedor;
import com.paideia.tesoreria.dominio.InconsistenciaPlanilla;
import com.paideia.tesoreria.dominio.Semt;
import com.paideia.tesoreria.to.PlanillaTO;
import com.vi.comun.exceptions.ParametroException;
import com.vi.comun.exceptions.ValidacionException;
import com.vi.comun.locator.ParameterLocator;
import com.vi.comun.util.FilesUtils;
import com.vi.comun.util.Utils;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
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
public class PlanillaService {

    @PersistenceContext(unitName = "TesoseriaServicePU")
    private EntityManager em;
    
    @EJB
    ProveedorService pService;
    
    ParameterLocator locator;
    
    public PlanillaService(){
        locator = ParameterLocator.getInstance();
    }

    public PlanillaTO cargar(InputStream inputstream, String nombreEntrada, double tasaCambio)throws ValidacionException, ParametroException, Exception{
        Map<String, BigDecimal> valores = new HashMap();
        PlanillaTO to = new PlanillaTO();
        
        String rutaDescarga = locator.getParameter("rutaDescarga");
        if(rutaDescarga == null){
            throw new ParametroException("No existe el parámetro rutaDescarga" );
        }
        
        String rutaEntrada = FilesUtils.crearArchivo(rutaDescarga, Utils.getNumAleatorio()+nombreEntrada , inputstream);
        File archivoEntrada = new File(rutaEntrada);
        RandomAccessFile entrada = new RandomAccessFile(archivoEntrada, "rw");
        
        String linea = entrada.readLine();
        int fila = 0;
        int registrosLeidos = 0;
        while(linea != null){
            fila++;
            if(linea.length() == 290){
                String tipo = linea.substring(1, 3).trim();
                String cuenta = linea.substring(3, 23).trim();
                String tipoId = linea.substring(80, 83).trim();
                String numId = linea.substring(83, 94).trim();
                String moneda = linea.substring(63, 65).trim();
                String monto = linea.substring(65, 78).trim();
                String decimal = linea.substring(78, 80).trim();
                
                String valString = monto+"."+decimal;
                if(!valString.matches("\\d+\\.\\d+")){
                    throw new ValidacionException("En fila "+fila+" El valor no tiene el formato correcto, deben ser digitos desde el caracter 65 hasta el caracter 80");
                }
                BigDecimal val = new BigDecimal(valString);
                if(moneda.equalsIgnoreCase("US")){
                    val = val.multiply(new BigDecimal(""+tasaCambio));
                }
                
                BigDecimal valor = valores.get(numId);
                if(valor != null){
                    valor.add(val);
                    valores.put(numId, valor);
                }else{
                    valores.put(numId, val);
                }
                
                InconsistenciaPlanilla inconsistencia = verificarInconsistencia(tipo, cuenta, numId, fila, nombreEntrada);
                if(inconsistencia != null){
                    to.getPlanillas().add(inconsistencia);
                }
                
                //System.out.println(tipo+" - "+cuenta+" - "+tipoId+" - "+numId+" - "+moneda+" - "+monto+"."+decimal);
                registrosLeidos++;
            }
            if(linea.length() == 195){
                String tipo = linea.substring(0, 2).trim();
                String cuenta = linea.substring(2, 22).trim();
                String numId = linea.substring(23, 38).trim();
                String monto = linea.substring(177, 194).trim();
                
                if(!monto.matches("\\d+\\.\\d+")){
                    throw new ValidacionException("En fila "+fila+" El valor no tiene el formato correcto, deben ser digitos desde el caracter 65 hasta el caracter 80");
                }
                BigDecimal val = new BigDecimal(monto);
                
                BigDecimal valor = valores.get(numId);
                if(valor != null){
                    valor.add(val);
                    valores.put(numId, valor);
                }else{
                    valores.put(numId, val);
                }
                
                
                InconsistenciaPlanilla inconsistencia = verificarInconsistencia(tipo, cuenta, numId, fila, nombreEntrada);
                if(inconsistencia != null){
                    to.getPlanillas().add(inconsistencia);
                }
                
                //System.out.println(tipo+" - "+cuenta+" - "+numId+" - "+monto);
                registrosLeidos++;
            }
            linea = entrada.readLine();
        }
        
        
        Set<String> keys = valores.keySet();
        for(String key:keys){
            Semt semt = new Semt();
            semt.setRuc(key);
            semt.setMonto(valores.get(key));
            
            to.getSemts().add(semt);
            
        }
        
        if(registrosLeidos == 0){
            throw new ValidacionException("No se valido ninguna cuenta, ninguna línea tiene el formato valido");
        }
        
        
        entrada.close();
        archivoEntrada.delete();

        return to;
    }
    
    private InconsistenciaPlanilla verificarInconsistencia(String tipo, String cuenta, String numId, int fila, String nombreEntrada){
        if(tipo.equals("2C")){
            cuenta = cuenta.substring(0,3)+cuenta.substring(4);
        }
        System.out.print("Tipo: "+tipo+" ");
        if(tipo.equals("2C") || tipo.equals("2A") || tipo.equals("2B")){
            System.out.println("Validando cuenta... "+cuenta+" para ruc "+numId);
            List<CuentasProveedor> cuentas = pService.findCuentasByProveedor(numId);
            
            if(cuentas.isEmpty()){
               InconsistenciaPlanilla inconsistencia = new InconsistenciaPlanilla();
               inconsistencia.setNoFila(fila);
               inconsistencia.setNombreArchivo(nombreEntrada);
               inconsistencia.setDescripcionInconsistencia("No existen cuentas para el proveedor: "+numId);
               return inconsistencia;
            }
            boolean cuentaValida = false;
            for(CuentasProveedor no : cuentas){
                if(no.getNoCuenta().replace("-", "").replace(" ", "").equals(cuenta)){
                    cuentaValida = true;
                }
            }
            if(!cuentaValida){
               InconsistenciaPlanilla inconsistencia = new InconsistenciaPlanilla();
               inconsistencia.setNoFila(fila);
               inconsistencia.setNombreArchivo(nombreEntrada);
               inconsistencia.setDescripcionInconsistencia("La cuenta:"+cuenta+", no se encuentra registrada para el proveedor con ruc: "+numId);
               return inconsistencia;
            }

        }
        return null;
    }

    public String generarArchivoSEMT(List<Semt> semts)throws ParametroException, Exception{
        Map<String, BigDecimal> valores = new HashMap();
        String rutaDescarga = locator.getParameter("rutaDescarga");
        if(rutaDescarga == null){
            throw new ParametroException("No existe el parámetro rutaDescarga" );
        }
        
        String rutaSalida = rutaDescarga+Utils.getNumAleatorio()+"SEMTS.txt";
        RandomAccessFile salida = new RandomAccessFile( rutaSalida, "rw");
        
        for(Semt semt : semts){
            BigDecimal valor = valores.get(semt.getRuc());
            if(valor != null){
                valor.add(semt.getMonto());
                valores.put(semt.getRuc(), valor);
            }else{
                valores.put(semt.getRuc(), semt.getMonto());
            }
        }
        
        Set<String> keys = valores.keySet();
        for(String key:keys){
            Semt semt = new Semt();
            semt.setRuc(key);
            semt.setMonto(valores.get(key));
            
            salida.writeBytes(key+"|"+valores.get(key)+"\r\n");
            
        }
        
        salida.close();
        
        
        return rutaSalida;
        
    }
    
    
}
