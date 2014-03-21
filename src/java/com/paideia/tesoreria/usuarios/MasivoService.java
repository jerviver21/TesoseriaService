/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.paideia.tesoreria.usuarios;

import com.paideia.tesoreria.dominio.Proveedor;
import com.paideia.tesoreria.services.ProveedorService;
import com.paideia.tesoreria.to.MasivoTO;
import com.vi.comun.exceptions.LlaveDuplicadaException;
import com.vi.comun.exceptions.ParametroException;
import com.vi.comun.exceptions.ValidacionException;
import com.vi.comun.locator.ParameterLocator;
import com.vi.comun.util.FilesUtils;
import com.vi.comun.util.Utils;
import com.vi.usuarios.dominio.Groups;
import com.vi.usuarios.dominio.Licencia;
import com.vi.usuarios.dominio.Users;
import com.vi.usuarios.services.GruposServicesLocal;
import com.vi.usuarios.services.LicenciaService;
import com.vi.usuarios.services.UsuariosServicesLocal;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.security.authentication.encoding.PasswordEncoder;

/**
 *
 * @author jerviver21
 */
@Stateless
@LocalBean
public class MasivoService {

    @PersistenceContext(unitName = "TesoseriaServicePU")
    private EntityManager em;
    
    @EJB
    UsuariosServicesLocal uService;
    
    @EJB
    LicenciaService lService;
    
    @EJB
    GruposServicesLocal gService;
    
    @EJB
    ProveedorService pService;
    
    ParameterLocator locator;
    
    public MasivoService(){
        locator = ParameterLocator.getInstance();
    }
    
    
    public MasivoTO generaUsuarios(InputStream input, String nombreArchivo, Licencia licencia, PasswordEncoder encoder)throws ValidacionException, LlaveDuplicadaException, Exception{
        MasivoTO dto = new MasivoTO();
        String rutaDescarga = locator.getParameter("rutaDescarga");
        if(rutaDescarga == null){
            throw new ParametroException("No existe el parámetro rutaDescarga" );
        }
        String rutaSalida = rutaDescarga+File.separator+Utils.getNumAleatorio()+"CLAVES.CSV";
        dto.setRuta(rutaSalida);
        RandomAccessFile claves = new RandomAccessFile(rutaSalida, "rw");
        claves.writeBytes("Nombre de Usuario;Contraseña\r\n");
        String ruta = FilesUtils.crearArchivo(rutaDescarga, Utils.getNumAleatorio()+nombreArchivo , input);
        File archivo = new File(ruta);
        BufferedReader lector = new BufferedReader(new FileReader(ruta));
        String ruc = lector.readLine();
        int i = 1;
        while(ruc != null){
            if(ruc.matches("(\\d{3,11}|^\\s*$)")){
                if(ruc.matches("^\\s*$")){
                    i++;
                    ruc = lector.readLine();
                    continue;
                }
                String claveAleatoria = "AF"+String.format("%06d", Utils.getNumAleatorio() );
                Groups grupo = gService.findByCodigo("PROVEEDORES");
                List<Groups> grupos = new ArrayList<Groups>();
                grupos.add(grupo);
                Users usr = new Users();
                usr.setLicencia(licencia);
                usr.setGrupos(grupos);
                usr.setUsr(ruc);
                usr.setEstado(1);
                usr.setPwd(encoder.encodePassword(claveAleatoria, null));
                
                Proveedor proveedor = pService.findProveedorByRuc(ruc);
                if(proveedor !=  null){
                    proveedor.setInfoActualizada(Boolean.FALSE);
                }
                
                uService.create(usr);
                claves.writeBytes(usr.getUsr()+";"+claveAleatoria+"\r\n");
                dto.setNumUsuariosCargados(dto.getNumUsuariosCargados()+1);
            }else{
                throw new ValidacionException("El RUC o numero de identificación no puede tener más de 11 digitos, línea: "+i+" - RUC: "+ruc);
            }
            i++;
            ruc = lector.readLine();
        }
        claves.close();
        lector.close();
        archivo.delete();
        
        return dto;
    }
}
