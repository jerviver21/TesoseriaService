/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paideia.tesoreria.services;

import com.paideia.tesoreria.dominio.Detraccion;
import com.paideia.tesoreria.dominio.Empresa;
import com.paideia.tesoreria.dominio.Proveedor;
import com.vi.comun.exceptions.ParametroException;
import com.vi.comun.locator.ParameterLocator;
import com.vi.comun.util.FechaUtils;
import com.vi.comun.util.FilesUtils;
import com.vi.comun.util.Utils;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class DetraService {

    @PersistenceContext(unitName = "TesoseriaServicePU")
    private EntityManager em;
    
    ParameterLocator locator;
    SimpleDateFormat formatFecha = new SimpleDateFormat("yyyy-MM-dd");
    
    public DetraService(){
        locator = ParameterLocator.getInstance();
    }
    
    public Proveedor findProveedor(String ruc){
        List<Proveedor> registro = em.createNamedQuery("Proveedor.findByRuc")
                .setParameter("ruc", ruc).getResultList();
        if(registro.isEmpty()){
            return null;
        }
        return registro.get(0);
    }
    

    public List<Detraccion> cargar(InputStream inputstream, String nombreArchivo, Empresa adq, Integer periodo)throws Exception{
        List<Detraccion> registros = new ArrayList<Detraccion>();
        String rutaDescarga = locator.getParameter("rutaDescarga");
        if(rutaDescarga == null){
            throw new ParametroException("No existe el parámetro rutaDescarga" );
        }
        String ruta = FilesUtils.crearArchivo(rutaDescarga, Utils.getNumAleatorio()+nombreArchivo , inputstream);
        File archivo = new File(ruta);
        RandomAccessFile lector = new RandomAccessFile(archivo, "rw");
        
        String linea = lector.readLine();
        
        while(linea != null){
            if(linea.matches(".*PAGO\\s+PPI.*")){
                Detraccion detracciones = new Detraccion();
                String[] datos = linea.split(";");
                String razonSocial = datos[25].replaceAll(".*/\\d(.*)-.*", "$1").replaceAll(",\\s*R\\s*U\\s*C", " ");
                String ruc = datos[25].replaceAll(".*-(.*)", "$1").replaceAll("\\s+", "").replaceAll("-", "");
                String importe = datos[32].replace(",", "").trim();
                Proveedor proveedor = findProveedor(ruc);
                if(proveedor == null){
                    proveedor = new Proveedor();
                    proveedor.setRazonSocial(razonSocial);
                    proveedor.setRuc(ruc);
                }
                detracciones.setCodOperacion("X");
                detracciones.setCodServicio("X");
                detracciones.setFechaCarga(new Date());
                detracciones.setProveedor(proveedor);
                detracciones.setPeriodoTributario(periodo+"");
                detracciones.setImporte(new BigDecimal(importe));
                detracciones.setArchivoIn(nombreArchivo);
                registros.add(detracciones);
            }
            linea = lector.readLine();
        }
        lector.close();
        archivo.delete();
        return registros;
        
    }

    public void guardarInfoBD(List<Detraccion> registros, Empresa adq, String nombreArchivo) {
        Empresa adquiriente = (Empresa)em.find(Empresa.class, adq.getId());
        List<Detraccion> anteriores = em.createNamedQuery("Detraccion.findDetraccion").setParameter("fecha", new Date()).setParameter("in", nombreArchivo).getResultList();
        if(!anteriores.isEmpty()){
            em.createNativeQuery("DELETE FROM detracciones WHERE fecha_carga = '"+formatFecha.format(new Date())+"' AND archivo_in = '"+nombreArchivo+"'").executeUpdate();
            List<Detraccion> detraFecha = em.createNamedQuery("Detraccion.findDetFecha").setParameter("fecha", new Date()).getResultList();
            if(detraFecha.isEmpty()){
                adquiriente.setConsecutivo(adquiriente.getConsecutivo()-1);
            }
        }
        
        List<Detraccion> detraFecha = em.createNamedQuery("Detraccion.findDetFecha").setParameter("fecha", new Date()).getResultList();
        if(detraFecha.isEmpty()){
            adquiriente.setConsecutivo(adquiriente.getConsecutivo()+1);
        }
        
        for(Detraccion registro:registros){
            Proveedor pro1 = registro.getProveedor();
            Proveedor provedor = findProveedor(registro.getProveedor().getRuc());
            registro.setEmpresa(adquiriente);
            if(provedor != null){
                registro.setProveedor(provedor);
            }
            registro.setNoFactura(String.format("%09d", Integer.parseInt(registro.getNoFactura())) );
            em.merge(registro);
        }
    }

    public String generarArchivo(Date fecha)throws Exception{
        List<Detraccion> detracciones = em.createNamedQuery("Detraccion.findDetFecha")
                .setParameter("fecha", fecha).getResultList();
        String rutaDescarga = locator.getParameter("rutaDescarga");
        if(rutaDescarga == null){
            throw new ParametroException("No existe el parámetro rutaDescarga" );
        }
        
        Empresa adq = null;
        if(!detracciones.isEmpty()){
            adq = detracciones.get(0).getEmpresa();
        }else{
            return null;
        }
        String ano = (""+FechaUtils.getAnoActual()).substring(2);
        String nombreArchivo = "D"+adq.getRuc()+ano+String.format("%04d",adq.getConsecutivo())+".txt";
        String rutaArchivo = rutaDescarga+File.separator+nombreArchivo;
        File archivo = new File(rutaArchivo);
        if(archivo.exists()){
            archivo.delete();
        }
        RandomAccessFile escritor = new RandomAccessFile(rutaArchivo, "rw");
        
        double valor = 0;
        for(Detraccion detraccion : detracciones){
            valor += detraccion.getImporte().doubleValue();
        }
        String svalor = ""+valor;
        String[] partes = svalor.split("[,\\.]");
        
        
        
        String reg1 = "*"+adq.getRuc()+Utils.rellenarDerecha(adq.getNombre(), " ", 35)+ano+String.format("%04d",adq.getConsecutivo())+String.format("%013d",Long.parseLong(partes[0]))+Utils.rellenarDerecha(partes[1], "0",2);
        
        escritor.writeBytes(reg1+"\n");
        
        
        int i = 1;
        for(Detraccion detraccion : detracciones){
            
            svalor = ""+detraccion.getImporte().doubleValue();
            partes = svalor.split("[,\\.]");
            String importe = String.format("%013d",Long.parseLong(partes[0]))+Utils.rellenarDerecha(partes[1], "0", 2);
            
            String reg2 = detraccion.getProveedor().getRuc()+detraccion.getNoFactura()+detraccion.getCodServicio()
                    +detraccion.getProveedor().getNoCuentaDetraccion()+importe+detraccion.getCodOperacion()+detraccion.getPeriodoTributario();
            
            if(i == detracciones.size()){
                escritor.writeBytes(reg2);
            }else{
                escritor.writeBytes(reg2+"\n");
            }
            i++;
            
            detraccion.setArchivoOut(rutaArchivo.replaceAll(".*/(.*)", "$1"));
            em.merge(detraccion);
        }
        
        escritor.close();
        
        return rutaArchivo;
    }

}
