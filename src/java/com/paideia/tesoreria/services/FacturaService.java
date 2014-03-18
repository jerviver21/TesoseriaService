/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.paideia.tesoreria.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.paideia.tesoreria.dominio.Empresa;
import com.paideia.tesoreria.dominio.RegistroFactura;
import com.paideia.tesoreria.dominio.RegistroPago;
import com.paideia.tesoreria.utils.EstadosPago;
import com.vi.comun.exceptions.ParametroException;
import com.vi.comun.locator.ParameterLocator;
import com.vi.comun.util.AWSUtils;
import com.vi.comun.util.FechaUtils;
import com.vi.comun.util.FilesUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
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
public class FacturaService {
    
    @PersistenceContext(unitName = "TesoseriaServicePU")
    private EntityManager em;
    
    @EJB
    EmpresaService eService;
    
    ParameterLocator locator;
    
    public FacturaService(){
        locator = ParameterLocator.getInstance();
    }

    
    public RegistroFactura guardar(RegistroFactura factura, String noLicencia)throws ParametroException, FileNotFoundException, IOException{
        Empresa empresa = eService.findEmpresaByLicencia(noLicencia);
        factura.setEmpresa(empresa);
        factura.setEstado(EstadosPago.PENDIENTE);
        factura.setSerieFactura(Integer.parseInt(factura.getSerieNoFactura().split("-")[0]));
        factura.setNumFactura(Long.parseLong(factura.getSerieNoFactura().split("-")[1]));
        
        
        if(factura.getAcuerdoPago().getId()<= 3){
            factura.setFechaVencimiento(FechaUtils.getFechaMasPeriodo(factura.getFechaEmisionFact(), 
                    factura.getAcuerdoPago().getNumDias(), Calendar.DATE));
        }else{
            factura.setFechaVencimiento(FechaUtils.getFechaMasPeriodo(factura.getFechaEntregaFact(), 
                    factura.getAcuerdoPago().getNumDias(), Calendar.DATE));
        }
        
        
        InputStream soporte = factura.getImg();
        String ext = factura.getExtension();
        factura = em.merge(factura);
        
        //Carga del soporte
        if(soporte != null){
            String nombreBucket = locator.getParameter("nombre_bucket");
            String urlImgs = locator.getParameter("url_imagenes");
            String rutaDescarga = locator.getParameter("rutaDescarga");
            if(nombreBucket == null || urlImgs == null){
                throw new ParametroException("Los parametros de almacenamiento de imagenes no existen " );
            }

            AmazonS3 s3client = AWSUtils.getAmazonS3();
            String nombreSoporte = factura.getId()+File.separator+"SOPORTE."+ext;
            String rutaSoporte = FilesUtils.crearArchivo(rutaDescarga, factura.getId()+(int)(Math.random()*1000)+"SOPORTE."+ext , soporte);
            File archivo = new File(rutaSoporte);
            s3client.putObject(new PutObjectRequest(nombreBucket, nombreSoporte, archivo));
            soporte.close();
            archivo.delete();
            factura.setUrlSoporte(urlImgs+File.separator+nombreBucket+File.separator+nombreSoporte);
            factura = em.merge(factura);
        }

        return factura;
    }
    
    public List<RegistroFactura> findFacturaByNo(String no){
        List<RegistroFactura> facturas = new ArrayList<RegistroFactura>();
        if(no.matches("\\d+")){
            facturas = em.createNamedQuery("RegistroFactura.findByNo").setParameter("no", Long.parseLong(no)).getResultList();
            for(RegistroFactura factura : facturas){
                factura.getProveedor().getRazonSocial();
                factura.getEstado().getNombre();
                if(factura.getRegistroPago() != null){
                    factura.getRegistroPago().getNoPlanilla();
                }
            }
        }
        return facturas;
    }
    
    public void pagarFactura(String no, RegistroPago pago){
        if(no.matches("\\d+")){
            List<RegistroFactura> lista = em.createNamedQuery("RegistroFactura.findByNo").setParameter("no", Long.parseLong(no)).getResultList();
            for(RegistroFactura r : lista){
                if(pago.getEstado().equals(EstadosPago.PROCESADA)){
                    r.setEstado(EstadosPago.PROCESADA);
                    r.setRegistroPago(pago);
                    em.merge(r);
                }
            }
        }
    }

    public List<RegistroFactura> findFacturasByRUC(String ruc) {
        List<RegistroFactura> facturas = em.createNamedQuery("RegistroFactura.findByRuc").setParameter("ruc", ruc).getResultList();
        for(RegistroFactura factura : facturas){
            factura.getProveedor().getRazonSocial();
            factura.getEstado().getNombre();
            if(factura.getRegistroPago() != null){
                factura.getRegistroPago().getNoPlanilla();
            }
        }
        return facturas;
    }

    public List<RegistroFactura> findFacturasXVencer(int noDias) {
        Date fecha = FechaUtils.getFechaMasPeriodo(new Date(), noDias, Calendar.DATE);
        List<RegistroFactura> facturas = em.createNamedQuery("RegistroFactura.findXVencer")
                .setParameter("fecha", fecha).getResultList();
        for(RegistroFactura factura : facturas){
            factura.getProveedor().getRazonSocial();
            factura.getEstado().getNombre();
            if(factura.getRegistroPago() != null){
                factura.getRegistroPago().getNoPlanilla();
            }
            
        }
        return facturas;

    }
    
    
}
