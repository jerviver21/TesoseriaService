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
import java.util.Map;
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
public class DetraService {

    @PersistenceContext(unitName = "TesoseriaServicePU")
    private EntityManager em;
    
    ParameterLocator locator;
    SimpleDateFormat formatFecha = new SimpleDateFormat("yyyy-MM-dd");
    
    @EJB
    EmpresaService eService;
    
    @EJB
    TeleceService pService;
    
    @EJB
    ProveedorService prService;
    
    @EJB
    GeneralService gService;
    
    
    public DetraService(){
        locator = ParameterLocator.getInstance();
    }
    

    public void cargarCuentas(InputStream inputstream, String nombreArchivo, String noLicencia)throws Exception{
        
        if(nombreArchivo.matches("D\\d{17}.(txt|TXT)")){
            int consecutivo = Integer.parseInt(nombreArchivo.replaceAll("D\\d{13}(\\d{4}).*", "$1"));
            Empresa empresa = eService.findEmpresaByLicencia(noLicencia);
            if(empresa.getConsecutivo() == null || (empresa.getConsecutivo()!= null && empresa.getConsecutivo() <= consecutivo) ){
                empresa.setConsecutivo(consecutivo+1);
                em.merge(empresa);
            }
        }
        
        String rutaDescarga = locator.getParameter("rutaDescarga");
        if(rutaDescarga == null){
            throw new ParametroException("No existe el parámetro rutaDescarga" );
        }
        String ruta = FilesUtils.crearArchivo(rutaDescarga, Utils.getNumAleatorio()+nombreArchivo , inputstream);
        File archivo = new File(ruta);
        RandomAccessFile lector = new RandomAccessFile(archivo, "rw");
        
        String linea = lector.readLine();
        
        while(linea != null){
            String ruc = linea.replaceAll("(\\d{11})\\d{9}\\d{3}\\d{11}\\d{15}\\d{2}\\d{6}", "$1");
            String cuenta = linea.replaceAll("\\d{11}\\d{9}\\d{3}(\\d{11})\\d{15}\\d{2}\\d{6}", "$1");
            em.createNativeQuery("UPDATE proveedor SET no_cuenta_detraccion = '"+cuenta+"' WHERE ruc ='"+ruc+"'").executeUpdate();
            linea = lector.readLine();
        }
        lector.close();
        archivo.delete();
    }

    public List<Detraccion> cargarComprobantes(InputStream inputstream, String nombreArchivo)throws Exception{
        List<Detraccion> detracciones = new ArrayList<Detraccion>();
        String rutaDescarga = locator.getParameter("rutaDescarga");
        if(rutaDescarga == null){
            throw new ParametroException("No existe el parámetro rutaDescarga" );
        }
        String ruta = FilesUtils.crearArchivo(rutaDescarga, Utils.getNumAleatorio()+nombreArchivo , inputstream);
        File archivo = new File(ruta);
        RandomAccessFile lector = new RandomAccessFile(archivo, "rw");
        
        String linea = lector.readLine();
        
        String nombreDetraccion = "";
        Date fechaPago = new Date();
        
        while(linea != null){
            if(linea.matches("Archivo.*")){
                nombreDetraccion = linea.replaceAll("Archivo.*(D.*[Tt][Xx][Tt]).*", "$1");
            }
            if(linea.matches("Fecha y hora de pago.*")){
                fechaPago = formatFecha.parse(linea.replaceAll(".*(\\d{2})/(\\d{2})/(\\d{4}).*", "$3-$2-$1"));
            }
            
            String ruc = null;
            String monto = null;
            String periodo = null;
            String opr = null;
            String servicio = null;
            String comprobante = null;
            
            if(linea.matches("Numero de constancia.*")){
                comprobante = linea.replaceAll("Numero de constancia.*(\\d+).*", "$1");
            }
            if(linea.matches("RUC Proveedor.*")){
                ruc = linea.replaceAll("RUC Proveedor.*(\\d+).*", "$1");
            }
            if(linea.matches("Codigo operacion.*")){
                opr = linea.replaceAll("Codigo operacion.*(\\d+).*", "$1");
            }
            if(linea.matches("Codigo bien o servicio.*")){
                servicio = linea.replaceAll("Codigo bien o servicio.*(\\d+).*", "$1");
            }
            if(linea.matches("Monto deposito.*")){
                monto = linea.replaceAll("Monto deposito.*(\\d+\\.\\d+).*", "$1");
            }
            if(linea.matches("Periodo Tributario.*")){
                periodo = linea.replaceAll("Periodo Tributario.*(\\d+).*", "$1");
                
                List<Detraccion> concidencias = em.createNamedQuery("Detraccion.findForComprobante")
                        .setParameter("servicio", servicio).setParameter("opr", opr)
                        .setParameter("archivo", nombreDetraccion).setParameter("periodo", periodo)
                        .setParameter("importe", new BigDecimal(monto)).setParameter("ruc", ruc).getResultList();
                if(concidencias.isEmpty()){
                    Detraccion detra = new Detraccion();
                    detra.setPeriodoTributario(periodo);
                    detra.setImporte(new BigDecimal(monto));
                    detra.getProveedor().setRuc(ruc);
                    detra.setCargadoComprobante(false);
                    detracciones.add(detra);
                }else{
                    for(Detraccion detra: concidencias){
                        if(detra.getComprobante() == null){
                            detra.setCargadoComprobante(true);
                            detra.setFechaPago(fechaPago);
                            detra.setComprobante(comprobante);
                            em.merge(detra);
                            break;
                        }
                    }
                }
            }
            
            
            
            linea = lector.readLine();
        }
        lector.close();
        archivo.delete();
        return detracciones;
    }

    public String generarDetracciones(InputStream inputstream, String nombreEntrada, Empresa empresa)throws Exception{
        Map codsServicioXNombre = gService.getMapServiciosXNombre();
        
        List<Detraccion> anteriores = em.createNamedQuery("Detraccion.findDetraccion").setParameter("fecha", new Date()).setParameter("in", nombreEntrada).getResultList();
        if(!anteriores.isEmpty()){
            em.createNativeQuery("DELETE FROM detracciones WHERE fecha_carga = '"+formatFecha.format(new Date())+"' AND archivo_in = '"+nombreEntrada+"'").executeUpdate();
        }
        
        List<Detraccion> detracciones = new ArrayList<Detraccion>();
        String rutaDescarga = locator.getParameter("rutaDescarga");
        if(rutaDescarga == null){
            throw new ParametroException("No existe el parámetro rutaDescarga" );
        }
        
        String ano = (""+FechaUtils.getAnoActual()).substring(2);
        String nombreSalida = "D"+empresa.getRuc()+ano+String.format("%04d",empresa.getConsecutivo())+".txt";
        String rutaSalida = rutaDescarga+File.separator+nombreSalida;
        File archivoSalida = new File(rutaSalida);
        if(archivoSalida.exists()){
            archivoSalida.delete();
        }
        RandomAccessFile salida = new RandomAccessFile(rutaSalida, "rw");
        
        
        
        
        String rutaEntrada = FilesUtils.crearArchivo(rutaDescarga, Utils.getNumAleatorio()+nombreEntrada , inputstream);
        File archivoEntrada = new File(rutaEntrada);
        RandomAccessFile entrada = new RandomAccessFile(archivoEntrada, "rw");
        
        String linea = entrada.readLine();
        double valor = 0;
        while(linea != null){
            if(linea.matches("\\d{11};.*") && linea.split(";").length > 10){
                Detraccion detraccion = new Detraccion();
                String[] datos = linea.split(";");
                String razonSocial = datos[1].trim();
                String ruc = datos[0].trim();
                String noFactura = datos[2].trim();
                Date fechaEmision = formatFecha.parse(datos[3].replaceAll("(\\d{2})/(\\d{2})/(\\d{4})", "$3-$2-$1"));
                BigDecimal base = new BigDecimal(datos[4].trim().replace(",","."));
                Integer porcentaje = Integer.parseInt(datos[5].replace("%", "").trim());
                BigDecimal importe = new BigDecimal(datos[6].trim().replace(",","."));
                String periodo = datos[3].replaceAll("(\\d{2})/(\\d{2})/(\\d{4})", "$3$2");
                String codOperacion = "01";
                String codServicio = "001";
                Proveedor proveedor = prService.findProveedorByRuc(ruc);
                if(proveedor == null){
                    proveedor = new Proveedor();
                    proveedor.setRazonSocial(razonSocial);
                    proveedor.setRuc(ruc);
                    em.merge(proveedor);
                }
                detraccion.setCodOperacion(codOperacion);
                detraccion.setCodServicio(codServicio);
                detraccion.setFechaCarga(new Date());
                detraccion.setProveedor(proveedor);
                detraccion.setPeriodoTributario(periodo);
                detraccion.setImporte(importe);
                detraccion.setBase(base);
                detraccion.setFechaEmision(fechaEmision);
                detraccion.setPorcentaje(porcentaje);
                detraccion.setNoFactura(noFactura);
                detraccion.setArchivoIn(nombreEntrada);
                detraccion.setArchivoOut(nombreSalida);
                detraccion.setEmpresa(empresa);
                
                em.merge(detraccion);
                
                valor += detraccion.getImporte().doubleValue();
                detracciones.add(detraccion);
                
                pService.asociarDetraccionPago(detraccion);
            }
            linea = entrada.readLine();
        }
        String svalor = ""+valor;
        String[] partes = svalor.split("[,\\.]");
        String reg1 = "*"+empresa.getRuc()+Utils.rellenarDerecha(empresa.getNombre(), " ", 35)+ano+String.format("%04d",empresa.getConsecutivo())+String.format("%013d",Long.parseLong(partes[0]))+Utils.rellenarDerecha(partes[1], "0",2);
        salida.writeBytes(reg1+"\r\n");
        int i = 1;
        for(Detraccion detraccion : detracciones){
            svalor = ""+detraccion.getImporte().doubleValue();
            partes = svalor.split("[,\\.]");
            String importe = String.format("%013d",Long.parseLong(partes[0]))+Utils.rellenarDerecha(partes[1], "0", 2);
            String reg2 = detraccion.getProveedor().getRuc()+detraccion.getNoFactura()+detraccion.getCodServicio()
                    +detraccion.getProveedor().getNoCuentaDetraccion()+importe+detraccion.getCodOperacion()+detraccion.getPeriodoTributario();
            if(i == detracciones.size()){
                salida.writeBytes(reg2);
            }else{
                salida.writeBytes(reg2+"\r\n");
            }
            i++;
            
            detraccion.setArchivoOut(nombreSalida);
            em.merge(detraccion);
        }
        salida.close();
        entrada.close();
        archivoEntrada.delete();
        return rutaSalida;
    }

}
