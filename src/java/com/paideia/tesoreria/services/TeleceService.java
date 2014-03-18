/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paideia.tesoreria.services;


import com.paideia.tesoreria.dominio.CuentasEmpresa;
import com.paideia.tesoreria.dominio.CuentasProveedor;
import com.paideia.tesoreria.dominio.Empresa;
import com.paideia.tesoreria.dominio.EstadoPago;
import com.paideia.tesoreria.dominio.Notificacion;
import com.paideia.tesoreria.dominio.Proveedor;
import com.paideia.tesoreria.dominio.RegistroPago;
import com.paideia.tesoreria.to.TeleceTO;
import com.vi.comun.exceptions.ParametroException;
import com.vi.comun.locator.ParameterLocator;
import com.vi.comun.util.FilesUtils;
import com.vi.comun.util.LectorHTMLUtils;
import com.vi.comun.util.Utils;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
public class TeleceService {

    @PersistenceContext(unitName = "TesoseriaServicePU")
    private EntityManager em;
    
    ParameterLocator locator;
    SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
    
    
    @EJB
    EmpresaService empresaService;
    
    @EJB
    ProveedorService provedorService;
    
    @EJB
    GeneralService generalService;
    
    @EJB
    FacturaService fService;
    
    public TeleceService(){
        locator = ParameterLocator.getInstance();
    }
    
    public RegistroPago findRegistro(String noPlanilla, Date fecha, Integer consecutivo){
        List<RegistroPago> registro = em.createNamedQuery("RegistroPago.findRegistro")
                .setParameter("planilla", noPlanilla)
                .setParameter("cons", consecutivo)
                .setParameter("fecha", fecha).getResultList();
        if(registro.isEmpty()){
            return null;
        }
        return registro.get(0);
    }
    
    public List<RegistroPago> findPagosByRuc(String ruc){
        List<RegistroPago> registros = em.createNamedQuery("RegistroPago.findByRuc")
                .setParameter("ruc", ruc).getResultList();
        return registros;
    }
    
    public List<RegistroPago> findPagosByFact(String no){
        List<RegistroPago> registros = em.createNamedQuery("RegistroPago.findByFact")
                .setParameter("no", no).getResultList();
        return registros;
    }
    

    public TeleceTO cargar(InputStream inputstream, String nombreArchivo, String noLicencia)throws Exception{
        TeleceTO to = new TeleceTO();
        String rutaDescarga = locator.getParameter("rutaDescarga");
        if(rutaDescarga == null){
            throw new ParametroException("No existe el parámetro rutaDescarga" );
        }
        String ruta = FilesUtils.crearArchivo(rutaDescarga, Utils.getNumAleatorio()+nombreArchivo , inputstream);
        File archivo = new File(ruta);
        RandomAccessFile lector = new RandomAccessFile(archivo, "rw");
        String linea = lector.readLine();
        
        String tipo = "";
        String subtipo = "";
        String noPlanilla = "";
        String monedaOrigen = "";
        String fecha = "";
        String referencia = "";
        String origen = "";
        String sede = "";
        
        Empresa empresa = empresaService.findEmpresaByLicencia(noLicencia);
        if(empresa == null){
            throw new Exception("No existe ninguna empresa asociada a la licencia del usuario");
        }
        
        
        RegistroPago registro = null;
        while(linea != null){
            if(linea.matches(".*<[tT][rR].*>.*")){
                String fila = LectorHTMLUtils.leerFila(lector, linea);
                if(fila.matches(".*Tipo de ope.*;;.*Subtipo.*")){
                    String[] datos = fila.split(";;");
                    tipo=datos[1];
                    subtipo=datos[3];
                }
                if(fila.matches(".*Tipo de ope.*;;.*") && fila.split(";;").length == 2){
                    String[] datos = fila.split(";;");
                    tipo=datos[1];
                    subtipo = "";
                }
                if(fila.matches(".*planilla.*;;.*Fecha de pr.*")){
                    String[] datos = fila.split(";;");
                    noPlanilla=datos[1];
                    fecha=datos[3];
                }
                if(fila.matches(".*Referencia de planilla.*;;.*Cuenta de origen.*")){
                    String[] datos = fila.split(";;");
                    referencia=datos[1];
                    origen=datos[3].replaceAll(".*(\\d{3}-\\d{7}-\\d-\\d{2}).*", "$1").trim();
                    monedaOrigen = datos[3].replaceAll(".*\\d{3}-\\d{7}-\\d-\\d{2}.*?-(.*?)-.*", "$1").trim();
                    sede = datos[3].replaceAll(".*\\d{3}-\\d{7}-\\d-\\d{2}.*?-.*?-(.*)", "$1").trim();
                }
                
                if(fila.matches(".*Referencia de planilla.*;;.*Planilla.*")){
                    String[] datos = fila.split(";;");
                    referencia=datos[1];
                }
                
                if(fila.matches(".*Cuenta de origen.*;;.*Empresa pagadora.*")){
                    String[] datos = fila.split(";;");
                    origen=datos[1].replaceAll(".*(\\d{3}-\\d{7}-\\d-\\d{2}).*", "$1").trim();
                    monedaOrigen = datos[1].replaceAll(".*\\d{3}-\\d{7}-\\d-\\d{2}.*?-(.*?)-.*", "$1").trim();
                }
                
                
                String[] datosreg = fila.split(";;");
                if(datosreg.length == 13){
                    registro = new RegistroPago();
                    registro.setFechaPago(formatFecha.parse(fecha));
                    
                    CuentasEmpresa cuentaEmp = empresaService.findCuentaByNum(origen.trim());
                    if(cuentaEmp == null){
                        cuentaEmp = new CuentasEmpresa();
                        cuentaEmp.setEmpresa(empresa);
                        cuentaEmp.setNoCuenta(origen.trim());
                        cuentaEmp.setMoneda(monedaOrigen.trim());
                        cuentaEmp = empresaService.saveCuenta(cuentaEmp);
                        to.getNotificaciones().add(crearNotificacion(nombreArchivo, "CuentaOrigen", origen
                                , "Cuenta de Empresa nueva: -"+origen+"-"+monedaOrigen, "NOVEDAD", noLicencia));
                }else{
                        if(!cuentaEmp.getEmpresa().equals(empresa)){
                            to.getNotificaciones().add(crearNotificacion(nombreArchivo, "CuentaOrigen", origen
                                , "La cuenta "+origen+" está "
                                    + "registrada para la empresa "+cuentaEmp.getEmpresa().getNombre()+" "
                                    + "- No para la empresa "+empresa.getNombre(), "INCONSISTENCIA", noLicencia));
                            to.incrementInconsistencias();
                        }
                        if(!cuentaEmp.getMoneda().equals(monedaOrigen.trim())){
                            to.getNotificaciones().add(crearNotificacion(nombreArchivo, "CuentaOrigen", origen
                                , "La cuenta "+origen+" está "
                                    + "registrada en "+cuentaEmp.getMoneda()+" - En esta planilla está en "+
                                        monedaOrigen+" ", "INCONSISTENCIA", noLicencia));
                            to.incrementInconsistencias();
                        }
                    }
                    
                    
                    registro.setCuentaOrigen(cuentaEmp);
                    registro.setMonOrg(monedaOrigen);
                    registro.setNoPlanilla(noPlanilla);
                    registro.setRp(referencia);
                    registro.setTipoOperacion(tipo);
                    registro.setSubtipoOperacion(subtipo);
                    registro.setConsecutivo(Integer.parseInt(datosreg[0].trim()));
                    
                    
                    String ruc = datosreg[2].replaceAll(".*;(.*)", "$1").trim();
                    Proveedor provedor = provedorService.findProveedorByRuc(ruc);
                    if(provedor == null){
                        provedor = new Proveedor();
                        provedor.setRazonSocial(datosreg[1].trim());
                        provedor.setRuc(ruc);
                        provedor = provedorService.saveProveedor(provedor);
                        to.getNotificaciones().add(crearNotificacion(nombreArchivo, "RUC", ruc
                                , "Nuevo Proveedor: -"+datosreg[1].trim()+"-"+ruc, "NOVEDAD", noLicencia));
                    }else{
                        if(!provedor.getRazonSocial().trim().equals(datosreg[1].trim())){
                            to.getNotificaciones().add(crearNotificacion(nombreArchivo, "RUC", ruc
                                , "La razon social del ruc "+ruc+" está "
                                    + "registrada como -"+provedor.getRazonSocial()+"-  "
                                        + "y en la planilla como -"+datosreg[1].trim()+"-", "INCONSISTENCIA", noLicencia));
                            to.incrementInconsistencias();
                        }
                    }
                    
                    
                    registro.setNombre(datosreg[1].trim());
                    registro.setProveedor(provedor);
                    registro.setMonto(new BigDecimal(datosreg[4].trim().replace(",", "")));
                    registro.setMontoAbonado(datosreg[7].equals("")?BigDecimal.ZERO:new BigDecimal(datosreg[7].trim().replace(",", "")));
                    registro.setOtraInformacion(datosreg[5]+"/"+datosreg[6]+" - "+datosreg[8].trim());
                    
                    
                    
                    if(datosreg[10].trim().matches(".*\\d.*")){
                        CuentasProveedor cuentaPro = provedorService.findCuentaByNum(datosreg[10].trim());
                        if(cuentaPro == null){
                            cuentaPro = new CuentasProveedor();
                            cuentaPro.setProveedor(provedor);
                            cuentaPro.setNoCuenta(datosreg[10].trim());
                            cuentaPro.setMoneda(datosreg[9].trim());
                            cuentaPro = provedorService.saveCuenta(cuentaPro);
                            to.getNotificaciones().add(crearNotificacion(nombreArchivo, "Cuenta Provedor", datosreg[10].trim()
                                , "Cuenta de Proveedor nueva: -"+datosreg[10].trim()+"-"+datosreg[9].trim(),
                                "NOVEDAD", noLicencia));
                        }else{
                            if(!cuentaPro.getProveedor().equals(provedor)){
                                to.getNotificaciones().add(crearNotificacion(nombreArchivo, "Cuenta Provedor", datosreg[10].trim()
                                , "La cuenta "+datosreg[10].trim()+" está "
                                        + "registrada para el proveedor "+cuentaPro.getProveedor().getRazonSocial()+" "
                                        + "- No para el proveedor "+provedor.getRazonSocial(), "INCONSISTENCIA", noLicencia));
                                to.incrementInconsistencias();
                            }
                            if(!cuentaPro.getMoneda().equals(datosreg[9].trim())){
                                to.getNotificaciones().add(crearNotificacion(nombreArchivo, "Cuenta Provedor", datosreg[10].trim()
                                , "La cuenta "+datosreg[10].trim()+" está "
                                        + "registrada en "+cuentaPro.getMoneda()+" - "
                                        + "En esta planilla está en "+datosreg[9].trim()+" ", "INCONSISTENCIA", noLicencia));
                                to.incrementInconsistencias();
                            }
                        }
                        registro.setCuentaDestino(cuentaPro);
                    }
                    
                    
                    registro.setDescDestino(datosreg[10].trim());
                    registro.setMonDes(datosreg[9].trim());
                    
                    EstadoPago estado = generalService.findEstado(datosreg[11].trim());
                    if(estado == null){
                        estado = new EstadoPago();
                        estado.setNombre(datosreg[11].trim());
                        estado = generalService.saveEstado(estado);
                        
                        to.getNotificaciones().add(crearNotificacion(nombreArchivo, "Estado", datosreg[11].trim()
                                , "Nuevo Estado: -"+datosreg[11].trim(), "NOVEDAD", noLicencia));
                    }
                    registro.setEstado(estado);
                    registro.setObservacion(datosreg[12].trim());
                    registro.setCargado(findRegistro(noPlanilla, registro.getFechaPago(), registro.getConsecutivo())!=null);
                    to.getPagos().add(registro);
                }
                if(datosreg.length == 5 && registro != null && datosreg[2].trim().matches("\\d+")){
                    registro.setNoFactura(datosreg[2]);
                    registro = null;
                }

            }
            linea = lector.readLine();
        }
        lector.close();
        archivo.delete();
        return to;
        
    }

    public void guardarInfoBD(List<RegistroPago> pagos, List<Notificacion> notificaciones) {

        for(RegistroPago registro:pagos){
            if(!registro.isCargado()){
                registro = em.merge(registro);
                if(registro.getNoFactura() != null){
                    fService.pagarFactura(registro.getNoFactura(), registro);
                }
            }
        }
        for(Notificacion registro:notificaciones){
            if(!registro.isCargado()){
                em.merge(registro);
            }
        }
    }
    
    
    public Notificacion crearNotificacion(String archivo, String campo, String criterio, String mensaje, String tipo, String lic){
        Notificacion noti = new Notificacion();
        noti.setArchivo(archivo);
        noti.setCampo(campo);
        noti.setCriterio(criterio);
        noti.setMensaje(mensaje);
        noti.setTipo(tipo);
        noti.setLicencia(lic);
        noti.setCargado(generalService.findNotificacion(mensaje)!=null);
        
        
        return noti;
    }

    public RegistroPago findPagos(Long id) {
        return em.find(RegistroPago.class, id);
    }

}
