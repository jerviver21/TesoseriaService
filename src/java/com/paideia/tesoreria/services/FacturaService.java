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
import com.paideia.tesoreria.utils.EstadosPago;
import com.vi.comun.exceptions.ParametroException;
import com.vi.comun.locator.ParameterLocator;
import com.vi.comun.util.AWSUtils;
import com.vi.comun.util.FilesUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
        InputStream soporte = factura.getImg();
        String ext = factura.getExtension();
        factura = em.merge(factura);
        
        //Carga del soporte
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

        return em.merge(factura);
    }
    
    
}
