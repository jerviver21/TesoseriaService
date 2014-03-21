/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paideia.tesoreria.services;

import com.paideia.tesoreria.dominio.Detraccion;
import com.paideia.tesoreria.dominio.Empresa;
import com.paideia.tesoreria.dominio.Proveedor;
import com.paideia.tesoreria.dominio.Service;
import com.vi.comun.exceptions.ParametroException;
import com.vi.comun.exceptions.ValidacionException;
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
    

    public void cargarArchivoAprobado(InputStream inputstream, String nombreArchivo, String noLicencia)throws ValidacionException, Exception{
        
        if(nombreArchivo.matches("D\\d{17}.(txt|TXT)")){
            int consecutivo = Integer.parseInt(nombreArchivo.replaceAll("D\\d{13}(\\d{4}).*", "$1"));
            Empresa empresa = eService.findEmpresaByLicencia(noLicencia);
            if(empresa.getConsecutivo() == null || (empresa.getConsecutivo()!= null && empresa.getConsecutivo() <= consecutivo) ){
                empresa.setConsecutivo(consecutivo+1);
                em.merge(empresa);
            }
        }else{
            throw new ValidacionException("El archivo no tiene el formato de nombre valido de SUNAT");
        }
        
        String rutaDescarga = locator.getParameter("rutaDescarga");
        if(rutaDescarga == null){
            throw new ParametroException("No existe el parámetro rutaDescarga" );
        }
        String ruta = FilesUtils.crearArchivo(rutaDescarga, Utils.getNumAleatorio()+nombreArchivo , inputstream);
        File archivo = new File(ruta);
        RandomAccessFile lector = new RandomAccessFile(archivo, "rw");
        
        String linea = lector.readLine();
        
        
        int numRegs = 0;
        while(linea != null){
            if(linea.matches("\\d{11}\\d{9}\\d{3}\\d{11}\\d{15}\\d{2}\\d{6}")){
                String ruc = linea.replaceAll("(\\d{11})\\d{9}\\d{3}\\d{11}\\d{15}\\d{2}\\d{6}", "$1");
                String cuenta = linea.replaceAll("\\d{11}\\d{9}\\d{3}(\\d{11})\\d{15}\\d{2}\\d{6}", "$1");
                String parteEntera = linea.replaceAll("\\d{11}\\d{9}\\d{3}\\d{11}(\\d{13})\\d{2}\\d{2}\\d{6}", "$1");
                String parteDecimal = linea.replaceAll("\\d{11}\\d{9}\\d{3}\\d{11}\\d{13}(\\d{2})\\d{2}\\d{6}", "$1");
                BigDecimal importe = new BigDecimal(Integer.parseInt(parteEntera)+"."+Integer.parseInt(parteDecimal));
                String servicio = linea.replaceAll("\\d{11}\\d{9}(\\d{3})\\d{11}\\d{13}\\d{2}\\d{2}\\d{6}", "$1");
                String periodo = linea.replaceAll("\\d{11}\\d{9}\\d{3}\\d{11}\\d{13}\\d{2}\\d{2}(\\d{6})", "$1");
                String opr = linea.replaceAll("\\d{11}\\d{9}\\d{3}\\d{11}\\d{13}\\d{2}(\\d{2})\\d{6}", "$1");
                
                List<Detraccion> detraccion = em.createNamedQuery("Detraccion.findForComprobante")
                        .setParameter("servicio", servicio).setParameter("opr", opr)
                        .setParameter("archivo", nombreArchivo).setParameter("periodo", periodo)
                        .setParameter("importe", importe).setParameter("ruc", ruc).getResultList();
                if(!detraccion.isEmpty()){
                    for(Detraccion detra : detraccion){
                        detra.setAprobadaSunat(Boolean.TRUE);
                        em.merge(detra);
                    }
                }else{
                    Detraccion detra = new Detraccion();
                    Proveedor proveedor = prService.findProveedorByRuc(ruc);
                    if(proveedor == null){
                        proveedor = new Proveedor();
                        proveedor.setRazonSocial("CARGADA POR ARCHIVO DETRACCIONES - ACTUALIZAR");
                        proveedor.setRuc(ruc);
                        proveedor.setNoCuentaDetraccion(cuenta);
                        proveedor.setInfoActualizada(Boolean.FALSE);
                        proveedor = em.merge(proveedor);
                    }
                    detra.setProveedor(proveedor);
                    detra.setCodOperacion(opr);
                    detra.setCodServicio(servicio);
                    detra.setImporte(importe);
                    detra.setPeriodoTributario(periodo);
                    detra.setArchivoOut(nombreArchivo);
                    detra.setAprobadaSunat(Boolean.TRUE);
                    em.merge(detra);
                }
                em.createNativeQuery("UPDATE proveedor SET no_cuenta_detraccion = '"+cuenta+"' WHERE ruc ='"+ruc+"'").executeUpdate(); 
                numRegs++;
            }
            linea = lector.readLine();
        }
        lector.close();
        archivo.delete();
        if(numRegs == 0){
            throw new ValidacionException("El archivo no contiene registros con formato de SUNAT");
        }
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
        
        String ruc = null;
        String monto = null;
        String periodo = null;
        String opr = null;
        String codServicio = null;
        String comprobante = null;
        String nservicio=null;
        
        
        while(linea != null){

            if(linea.matches("Archivo.*")){
                nombreDetraccion = linea.replaceAll("Archivo.*(D.*[Tt][Xx][Tt]).*", "$1").trim();
            }
            if(linea.matches("Fecha y hora de pago.*")){
                fechaPago = formatFecha.parse(linea.replaceAll(".*?(\\d{2})/(\\d{2})/(\\d{4}).*", "$3-$2-$1"));
            }
            
            if(linea.matches(".*Numero de constancia.*")){
                comprobante = linea.replaceAll(".*Numero de constancia.*?(\\d+).*", "$1").trim();
            }
            if(linea.matches(".*RUC Proveedor.*")){
                ruc = linea.replaceAll(".*RUC Proveedor.*?(\\d+).*", "$1").trim();
            }
            if(linea.matches(".*Codigo operacion.*")){
                opr = linea.replaceAll(".*Codigo operacion.*?(\\d+).*", "$1").trim();
            }
            if(linea.matches(".*Codigo bien o servicio.*")){
                codServicio = linea.replaceAll(".*Codigo bien o servicio.*?(\\d+).*", "$1").trim();
            }
            if(linea.matches(".*Nombre bien o servicio.*")){
                nservicio = linea.replaceAll(".*Nombre bien o servicio\\s+(.*)", "$1").trim().toUpperCase();

                List<Service> servicios = em.createNamedQuery("Service.findByNombre")
                        .setParameter("nombre", nservicio.trim()).getResultList();
                if(servicios.isEmpty() && codServicio.matches("\\d{3}")){
                    Service servicio = new Service();
                    servicio.setCod(codServicio);
                    servicio.setDescripcion(nservicio.trim());
                    em.merge(servicio);
                }else{
                    Service servicio = servicios.get(0);
                    System.out.println(nservicio.trim()+" - "+servicio.getId()+" - "+servicio.getDescripcion()+" - "+servicio.getCod());
                    if(!servicio.getCod().trim().matches(codServicio)){
                        throw new ValidacionException("El servicio "+nservicio.trim()+" tiene el código: "+servicio.getCod()+" y en el archivo es: "+codServicio+" ; Por favor Revise esta información y actualice el sistema si es necesario. Sin embargo por lo menos una detracción se habría pagado con el código errado!");
                    }
                }                                                                                 
                
            }
            if(linea.matches(".*Monto deposito.*")){
                monto = linea.replaceAll(".*Monto deposito.*?(\\d+\\.\\d+).*", "$1");
            }
            if(linea.matches("Periodo Tributario.*")){
                periodo = linea.replaceAll("Periodo Tributario.*?(\\d+).*", "$1");
                                List<Detraccion> concidencias = em.createNamedQuery("Detraccion.findForComprobante")
                        .setParameter("servicio", codServicio).setParameter("opr", opr)
                        .setParameter("archivo", nombreDetraccion).setParameter("periodo", periodo)
                        .setParameter("importe", new BigDecimal(monto.trim())).setParameter("ruc", ruc).getResultList();
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
                            detra.setArchivoComprobante(nombreArchivo);
                            em.merge(detra);
                            detracciones.add(detra);
                            break;
                        }
                    }
                }
                
                ruc = null;
                monto = null;
                periodo = null;
                opr = null;
                codServicio = null;
                comprobante = null;
                nservicio = null;
            }
            
            
            
            linea = lector.readLine();
        }
        lector.close();
        archivo.delete();
        return detracciones;
    }

    public String generarDetracciones(InputStream inputstream, String nombreEntrada, Empresa empresa)throws ValidacionException, Exception{
        Map<String, String> codsServicioXNombre = gService.getMapServiciosXNombre();
        
        List<Detraccion> anteriores1 = em.createNamedQuery("Detraccion.findDetraccionIn").setParameter("in", nombreEntrada).getResultList();
        if(!anteriores1.isEmpty()){
            for(Detraccion detra:anteriores1){
                if(detra.getAprobadaSunat()){
                    throw new ValidacionException("Ya se cargaron detracciones que fueron aprobadas por SUNAT para el nombre de archivo: "+nombreEntrada+" Si son otras detracciones de otro mes, por favor cambie el nombre del archivo y pongale el periodo al nombre, para que no se confunda");
                }
            }
            em.createNativeQuery("DELETE FROM detraccion WHERE archivo_in = '"+nombreEntrada+"'").executeUpdate();
        }
        
        em.merge(empresa);
        
        
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
        int fila = 1;
        while(linea != null){
            if(linea.split(";").length >= 10 && !linea.matches(".*;;;;.*")){
                Detraccion detraccion = new Detraccion();
                String[] datos = linea.split(";");
                String razonSocial = datos[1].trim();
                String ruc = datos[0].trim();
                if(!ruc.matches("\\d{11}")){
                    throw new ValidacionException("RUC invalido "+ruc+". Fila no: "+fila);
                }
                
                String noFactura = null;
                if(datos[2].trim().matches(".*\\d+.*")){
                    noFactura = datos[2].trim();
                }
                
                Date fechaEmision = null;
                if(datos[3].trim().matches(".*\\d{2}/\\d{2}/\\d{4}.*")){
                    fechaEmision = formatFecha.parse(datos[3].replaceAll("(\\d{2})/(\\d{2})/(\\d{4})", "$3-$2-$1"));
                }

                String bases = "0.0";
                if(datos[4].trim().matches(".*\\d,\\d\\d$")){
                    bases = datos[4].trim().replace(".", "").replace(",",".");
                }else  if(datos[4].trim().matches(".*\\d\\.\\d\\d$")){
                    bases = datos[4].trim().replace(",", "");
                }
                BigDecimal base = new BigDecimal(bases);
                
                String importes = "0.0";
                if(datos[6].trim().matches(".*\\d,\\d\\d$")){
                    importes = datos[6].trim().replace(".", "").replace(",",".");
                }else  if(datos[6].trim().matches(".*\\d\\.\\d\\d$")){
                    importes = datos[6].trim().replace(",", "");
                }else{
                    throw new ValidacionException("Importe invalido "+datos[6].trim()+". Fila no: "+fila);
                }
                BigDecimal importe = new BigDecimal(importes);
                
                
                
                Integer porcentaje = 0;
                if(datos[5].trim().matches(".*?\\d{1,2}%.*")){
                   porcentaje = Integer.parseInt(datos[5].replace(".*?(\\d{1,2}).*", "").trim().replace("%", "")); 
                }
                
                String periodo = "";
                if(datos[3].trim().matches(".*\\d{2}/\\d{2}/\\d{4}.*")){
                    periodo = datos[3].replaceAll(".*(\\d{2})/(\\d{2})/(\\d{4}).*", "$3$2");
                }else{
                    throw new ValidacionException("No se pudo obtener el periodo "+datos[3]+", columna 3 deberia ser fecha de emisión . Fila no: "+fila);
                }
                
                String codOperacion = "01";
                String codServicio = datos[9].trim().matches("\\d{3}")?datos[9].trim():codsServicioXNombre.get(datos[9].trim().toUpperCase());
                if(codServicio == null){
                    throw new ValidacionException("No existe un código de servicio para:  "+datos[9]+"  - Guardelo a través del Menú: Detracciones - Cargar Servicio");
                }
                
                Proveedor proveedor = prService.findProveedorByRuc(ruc);
                if(proveedor == null){
                    proveedor = new Proveedor();
                    proveedor.setRazonSocial(razonSocial);
                    proveedor.setRuc(ruc);
                    proveedor.setInfoActualizada(Boolean.FALSE);
                    proveedor = em.merge(proveedor);
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
                detraccion.setNombreServicio(datos[9].trim());
                detraccion.setArchivoOut(nombreSalida);
                detraccion.setAprobadaSunat(Boolean.FALSE);
                
                detraccion = em.merge(detraccion);
                
                valor += detraccion.getImporte().doubleValue();
                detracciones.add(detraccion);
                
                pService.asociarDetraccionPago(detraccion);
                fila++;
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
            String reg2 = detraccion.getProveedor().getRuc()+String.format("%09d", 0)+detraccion.getCodServicio()
                    +(detraccion.getProveedor().getNoCuentaDetraccion()==null?
                    "99999999999":detraccion.getProveedor().getNoCuentaDetraccion())+
                    importe+detraccion.getCodOperacion()+detraccion.getPeriodoTributario();
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
