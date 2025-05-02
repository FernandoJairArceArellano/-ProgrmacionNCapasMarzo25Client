package com.Digis01.FArceProgramacionNCapas.Controller;

import com.Digis01.FArceProgramacionNCapas.ML.Result;
import com.Digis01.FArceProgramacionNCapas.ML.Colonia;
import com.Digis01.FArceProgramacionNCapas.ML.Direccion;
import com.Digis01.FArceProgramacionNCapas.ML.Estado;
import com.Digis01.FArceProgramacionNCapas.ML.Municipio;
import com.Digis01.FArceProgramacionNCapas.ML.Pais;
import com.Digis01.FArceProgramacionNCapas.ML.ResultFile;
import com.Digis01.FArceProgramacionNCapas.ML.Rol;
import com.Digis01.FArceProgramacionNCapas.ML.Usuario;
import com.Digis01.FArceProgramacionNCapas.ML.UsuarioDireccion;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/Usuario")
public class UsuarioController {

    @GetMapping
    public String Index(Model model) {

        RestTemplate restTemplate = new RestTemplate();

                ResponseEntity<Result<UsuarioDireccion>> responseEntity = restTemplate.exchange("http://localhost:8081/alumnoapi", 
                HttpMethod.GET, 
                HttpEntity.EMPTY, 
                new ParameterizedTypeReference<Result<UsuarioDireccion>>(){});
        
        Result response = responseEntity.getBody();

        Usuario usuarioBusqueda = new Usuario();
        usuarioBusqueda.Rol = new Rol();

        model.addAttribute("usuarioBusqueda", usuarioBusqueda);
        //model.addAttribute("roles", resultRol.object);
        //model.addAttribute("roles", resultRol.objects);
        model.addAttribute("listaUsuarios", response.objects);

        return "UsuarioIndex";
    }

    /*@GetMapping("Form/{IdUsuario}")
    public String Form(@PathVariable int IdUsuario, Model model) {
        if (IdUsuario == 0) { //Agregar
            UsuarioDireccion usuarioDireccion = new UsuarioDireccion();
            usuarioDireccion.Usuario = new Usuario();
            usuarioDireccion.Usuario.Rol = new Rol();
            usuarioDireccion.Direccion = new Direccion();
            usuarioDireccion.Direccion.Colonia = new Colonia();

            usuarioDireccion.Direccion.Colonia.Municipio = new Municipio();
            usuarioDireccion.Direccion.Colonia.Municipio.Estado = new Estado();
            usuarioDireccion.Direccion.Colonia.Municipio.Estado.Pais = new Pais();

            //model.addAttribute("roles", RolDAOImplementation.GetAll().object);
            model.addAttribute("roles", RolDAOImplementation.GetAllJPA().objects);
            model.addAttribute("usuarioDireccion", usuarioDireccion);
            //model.addAttribute("paises", PaisDAOImplementation.GetAll().correct ? PaisDAOImplementation.GetAll().objects : null);
            model.addAttribute("paises", PaisDAOImplementation.GetAllJPA().correct ? PaisDAOImplementation.GetAllJPA().objects : null);
            return "UsuarioForm";
        } else { // Edicion
            System.out.println("Voy a editar");
            //Result result = usuarioDAOImplementation.direccionesByIdUsuario(IdUsuario);
            Result result = usuarioDAOImplementation.DireccionesByIdUsuarioJPA(IdUsuario);
            model.addAttribute("usuarioDirecciones", result.object);
            return "UsuarioDetail";
        }
    }

    @GetMapping("Delete/{IdUsuario}")
    public String deleteUsuario(@PathVariable int IdUsuario, Model model) {
        Result result = usuarioDAOImplementation.DeleteJPA(IdUsuario);

        if (result.correct) {
            // Redirigir a la lista de usuarios si todo salió bien
            return "redirect:/Usuario";
        } else {
            model.addAttribute("error", result.errorMessage);
            return "error";
        }
    }

    @GetMapping("/DeleteDireccion/{IdDireccion}")
    public String deleteDireccionById(@PathVariable int IdDireccion, Model model) {
        System.out.println("Voy a eliminar una direccion");

        Result result = direccionDAOImplementation.DeleteDireccionJPA(IdDireccion);

        if (result.correct) {
            // Redirigir a usuario detail si todo sale bien
            return "redirect:/Usuario";
        } else {
            model.addAttribute("error", result.errorMessage);
            return "error";
        }
    }

    // Muestra de formulario Carga Masiva
    @GetMapping("/CargaMasiva")
    public String CargaMasiva() {
        return "CargaMasiva"; // Renderizado de pagina HTML
    }

    // Procesa el archivo subido por el usuario
    @PostMapping("/CargaMasiva")
    public String CargaMasiva(@RequestParam MultipartFile archivo, Model model, HttpSession session) {

        // Validación inicial: si no se seleccionó archivo
        if (archivo == null || archivo.isEmpty()) {
            model.addAttribute("mensaje", "No se seleciono archivo.");
            return "cargaMasiva";
        }

        try {
            // Guardsarlo en un punto del sistema
            if (archivo != null && !archivo.isEmpty()) {
                // Definir ruta donde se guardará el archivo

                String tipoArchivo = archivo.getOriginalFilename().split("\\.")[1];
                String root = System.getProperty("user.dir"); // Obtener direccion del proyecto en el equipo
                String path = "src/main/resources/static/archivos"; // Path relativo dentro del proyecto
                String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmSS")); // Formato de dia y hora de subida
                String absolutePath = root + "/" + path + "/" + fecha + archivo.getOriginalFilename(); // Construccion de la ruta total del archivo

                // Guardar el archivo físicamente
                archivo.transferTo(new File(absolutePath)); // Creacion de un nuevo archivo

                // Leer el contenido del archivo y convertirlo a objetos
                List<UsuarioDireccion> listaUsuarios = new ArrayList();

                if (tipoArchivo.equals("txt")) {
                    listaUsuarios = LecturaArchivoTXT(new File(absolutePath)); // Metodo para leer la lista
                } else {
                    listaUsuarios = LecturaArchivoExcel(new File(absolutePath));
                }

                // Validar los datos leídos del archivo
                List<ResultFile> listaErrores = ValidarArchivo(listaUsuarios);

                if (listaErrores.isEmpty()) {
                    // Si no hay errores, se guarda la ruta en la sesión y se puede procesar más adelante
                    session.setAttribute("urlFile", absolutePath); // Guarda ruta en sesión si no hay errores
                    model.addAttribute("listaErrores", listaErrores); // Mostrar la lista de errores "ya procesados"
                    model.addAttribute("archivoNombre", archivo.getOriginalFilename());
                    model.addAttribute("exito", true);
                } else {
                    // Si hay errores, se envían al frontend
                    model.addAttribute("listaErrores", listaErrores); // Mostrar errores en el frontend
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(); // Log del error
            model.addAttribute("mensaje", "Error al procesar el archivo.");
            return "redirect:/Usuario/CargaMasiva";
        }
        return "CargaMasiva"; // regresar a la vista
    }

    // Método para leer el archivo y convertir líneas en objetos UsuarioDireccion
    private List<UsuarioDireccion> LecturaArchivoTXT(File archivo) {
        //Logica para leer el archivo
        List<UsuarioDireccion> listaUsuarios = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split("\\|");

                // Validación rápida del número de campos esperados
                if (campos.length != 18) {
                    System.out.println("ERROR: Línea con número de campos incorrecto:");
                    System.out.println("Esperados: 18, Recibidos: " + campos.length);
                    System.out.println(linea);
                    continue;
                }

                UsuarioDireccion usuarioDireccion = new UsuarioDireccion();
                usuarioDireccion.Usuario = new Usuario();

                usuarioDireccion.Usuario.setNombre(campos[0]);               // 1
                usuarioDireccion.Usuario.setApellidoPaterno(campos[1]);      // 2
                usuarioDireccion.Usuario.setApellidoMaterno(campos[2]);      // 3
                usuarioDireccion.Usuario.setImagen(null);               // 4

                // Fecha de nacimiento
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                usuarioDireccion.Usuario.setFNacimiento(formatter.parse(campos[4]));  // 5

                usuarioDireccion.Usuario.setNCelular(campos[5]);             // 6

                usuarioDireccion.Usuario.Rol = new Rol();
                usuarioDireccion.Usuario.Rol.setIdRol(Integer.parseInt(campos[6]));  // 7

                usuarioDireccion.Usuario.setCURP(campos[7]);                 // 8
                usuarioDireccion.Usuario.setUsername(campos[8]);            // 9
                usuarioDireccion.Usuario.setEmail(campos[9]);               // 10
                usuarioDireccion.Usuario.setPassword(campos[10]);           // 11

                String sexoStr = campos[11];                                 // 12
                usuarioDireccion.Usuario.setSexo(sexoStr != null && !sexoStr.isEmpty() ? sexoStr.charAt(0) : null);

                usuarioDireccion.Usuario.setTelefono(campos[12]);           // 13

                usuarioDireccion.Direccion = new Direccion();
                usuarioDireccion.Direccion.setCalle(campos[13]);            // 14
                usuarioDireccion.Direccion.setNumeroExterior(campos[14]);   // 15
                usuarioDireccion.Direccion.setNumeroInterior(campos[15]);   // 16

                usuarioDireccion.Direccion.Colonia = new Colonia();
                usuarioDireccion.Direccion.Colonia.setIdColonia(Integer.parseInt(campos[16])); // 17

                // El último campo puede ser ignorado 
                usuarioDireccion.Usuario.setStatus(Integer.parseInt(campos[17]));  // 18
                listaUsuarios.add(usuarioDireccion);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            listaUsuarios = null;
        }

        return listaUsuarios;
    }

    // Método para leer el archivo y convertir líneas en objetos UsuarioDireccion
    private List<UsuarioDireccion> LecturaArchivoExcel(File archivo) {
        List<UsuarioDireccion> listaUsuarios = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(archivo);) {
            for (Sheet sheet : workbook) {
                for (Row row : sheet) {
                    UsuarioDireccion usuarioDireccion = new UsuarioDireccion();
                    usuarioDireccion.Usuario = new Usuario();
                    usuarioDireccion.Usuario.setNombre(row.getCell(0).toString());
                    usuarioDireccion.Usuario.setApellidoPaterno(row.getCell(1).toString());
                    usuarioDireccion.Usuario.setApellidoMaterno(row.getCell(2).toString());
                    usuarioDireccion.Usuario.setImagen(null);

                    if (DateUtil.isCellDateFormatted(row.getCell(4))) {
                        usuarioDireccion.Usuario.setFNacimiento(row.getCell(4).getDateCellValue());
                    } else {
                        double fechaTexto = row.getCell(4).getNumericCellValue();
                        Date formato = DateUtil.getJavaDate(fechaTexto);
                        usuarioDireccion.Usuario.setFNacimiento(formato);
                    }

                    usuarioDireccion.Usuario.setNCelular(row.getCell(5).toString());

                    usuarioDireccion.Usuario.Rol = new Rol();
                    usuarioDireccion.Usuario.Rol.setIdRol(Integer.parseInt(row.getCell(6).toString()));
                    usuarioDireccion.Usuario.setCURP(row.getCell(7).toString());
                    usuarioDireccion.Usuario.setUsername(row.getCell(8).toString());
                    usuarioDireccion.Usuario.setEmail(row.getCell(9).toString());
                    usuarioDireccion.Usuario.setPassword(row.getCell(10).toString());
                    usuarioDireccion.Usuario.setSexo(row.getCell(11).toString().charAt(0));
                    usuarioDireccion.Usuario.setTelefono(row.getCell(12).toString());
                    usuarioDireccion.Direccion.setCalle(row.getCell(13).toString());
                    usuarioDireccion.Direccion.setNumeroExterior(row.getCell(14).toString());
                    usuarioDireccion.Direccion.setNumeroInterior(row.getCell(15).toString());
                    usuarioDireccion.Direccion.Colonia = new Colonia();
                    usuarioDireccion.Direccion.Colonia.setIdColonia(Integer.parseInt(row.getCell(16).toString()));
                    usuarioDireccion.Usuario.setStatus(Integer.parseInt(row.getCell(17).toString()));

                }
            }

        } catch (Exception ex) {
            System.out.println("Error al abrir el archivo");
            listaUsuarios = null;
        }

        return listaUsuarios;
    }

    // Validación del contenido del archivo leído
    private List<ResultFile> ValidarArchivo(List<UsuarioDireccion> listaUsuarios) {
        List<ResultFile> listaErrores = new ArrayList<>();

        // Validar si la lista está vacía o nula
        if (listaUsuarios == null) {
            listaErrores.add(new ResultFile(0, "Lista nula", "No se pudo leer el archivo"));
        } else if (listaUsuarios.isEmpty()) {
            listaErrores.add(new ResultFile(0, "Lista vacía", "No hay registros en el archivo"));
        } else {
            int fila = 1;
            for (UsuarioDireccion usuarioDireccion : listaUsuarios) {
                String nombre = usuarioDireccion.Usuario.getNombre();
                String apellidoPaterno = usuarioDireccion.Usuario.getApellidoPaterno();
                String apellidoMaterno = usuarioDireccion.Usuario.getApellidoMaterno();
                Date fNacimiento = usuarioDireccion.Usuario.getFNacimiento();
                String nCelular = usuarioDireccion.Usuario.getNCelular();
                int idRol = usuarioDireccion.Usuario.Rol.getIdRol();
                String curp = usuarioDireccion.Usuario.getCURP();
                String userName = usuarioDireccion.Usuario.getUsername();
                String email = usuarioDireccion.Usuario.getEmail();
                String password = usuarioDireccion.Usuario.getPassword();
                char sexo = usuarioDireccion.Usuario.getSexo();
                String telefono = usuarioDireccion.Usuario.getTelefono();
                int status = usuarioDireccion.Usuario.getStatus();
                //String imagen = usuarioDireccion.Usuario.getImagen();

                if (nombre == null || nombre.trim().isEmpty()) {
                    listaErrores.add(new ResultFile(fila, nombre, "El nombre es obligatorio"));
                }

                if (apellidoPaterno == null || apellidoPaterno.trim().isEmpty()) {
                    listaErrores.add(new ResultFile(fila, apellidoPaterno, "El apellido paterno es obligatorio"));
                }

                if (apellidoMaterno == null || apellidoMaterno.trim().isEmpty()) {
                    listaErrores.add(new ResultFile(fila, apellidoMaterno, "El apellido materno es obligatorio"));
                }

                if (fNacimiento == null) {
                    listaErrores.add(new ResultFile(fila, "", "La fecha de nacimiento es obligatoria"));
                }

                if (nCelular == null || nCelular.trim().isEmpty()) {
                    listaErrores.add(new ResultFile(fila, nCelular, "El número de celular es obligatorio"));
                } else if (!nCelular.matches("^\\d{10}$")) {
                    listaErrores.add(new ResultFile(fila, nCelular, "El número de celular debe tener 10 dígitos"));
                }

                if (curp == null || curp.trim().isEmpty()) {
                    listaErrores.add(new ResultFile(fila, curp, "El CURP es obligatorio"));
                }

                if (userName == null || userName.trim().isEmpty()) {
                    listaErrores.add(new ResultFile(fila, userName, "El nombre de usuario es obligatorio"));
                }

                if (email == null || email.trim().isEmpty()) {
                    listaErrores.add(new ResultFile(fila, email, "El correo electrónico es obligatorio"));
                }

                if (password == null || password.trim().isEmpty()) {
                    listaErrores.add(new ResultFile(fila, password, "La contraseña es obligatoria"));
                }

                if (sexo != 'M' && sexo != 'F') {
                    listaErrores.add(new ResultFile(fila, String.valueOf(sexo), "El sexo debe ser 'M' o 'F'"));
                }

                if (telefono != null && !telefono.trim().isEmpty() && !telefono.matches("^\\d{10}$")) {
                    listaErrores.add(new ResultFile(fila, telefono, "El teléfono debe tener 10 dígitos si se proporciona"));
                }

                if (idRol <= 0) {
                    listaErrores.add(new ResultFile(fila, String.valueOf(idRol), "El rol debe ser un ID válido"));
                }

                if (status != 0 && status != 1) {
                    listaErrores.add(new ResultFile(fila, String.valueOf(status), "El status debe ser 0 (inactivo) o 1 (activo)"));
                }
                
                fila++;
            }
        }

        return listaErrores;
    }

    // Procesar archivo
    @GetMapping("/CargaMasiva/Procesar")
    public String ProcesarArchivo(HttpSession session) {
        String absolutePath = session.getAttribute("urlFile").toString();
        List<UsuarioDireccion> listaUsuarios = LecturaArchivoTXT(new File(absolutePath));

        for (UsuarioDireccion usuarioDireccion : listaUsuarios) {
            System.out.println("Estoy agregando un nuevo usuario y direccion");
            //usuarioDAOImplementation.Add(usuarioDireccion);
            usuarioDAOImplementation.AddJPA(usuarioDireccion);
        }

        return "CargaMasiva";
    }

    @GetMapping("/formEditable")
    public String FormEditable(Model model, @RequestParam int IdUsuario, @RequestParam(required = false) Integer IdDireccion) {
        if (IdDireccion == null) {//Editar Usuario
            System.out.println("Voy a editar datos del usuario");
            UsuarioDireccion usuarioDireccion = new UsuarioDireccion();

            //usuarioDireccion = (UsuarioDireccion) usuarioDAOImplementation.GetById(IdUsuario).object;
            usuarioDireccion = (UsuarioDireccion) usuarioDAOImplementation.GetByIdJPA(IdUsuario).object;
            usuarioDireccion.Direccion = new Direccion();   //Error de linea java.lang.NullPointerException: Cannot assign field "Direccion" because "usuarioDireccion" is null
            usuarioDireccion.Direccion.setIdDireccion(-1);
            model.addAttribute("usuarioDireccion", usuarioDireccion);

            //model.addAttribute("roles", RolDAOImplementation.GetAll().object);
            model.addAttribute("roles", RolDAOImplementation.GetAllJPA().objects);
        } else if (IdDireccion == 0) {//Agregar Direccion
            System.out.println("Voy a agregar una direccion");
            UsuarioDireccion usuarioDireccion = new UsuarioDireccion();
            usuarioDireccion.Usuario = new Usuario();
            usuarioDireccion.Usuario.setIdUsuario(1);
            usuarioDireccion.Direccion = new Direccion();
            usuarioDireccion.Direccion.setIdDireccion(0);
            model.addAttribute("usuarioDireccion", usuarioDireccion);
            //model.addAttribute("paises", PaisDAOImplementation.GetAll().correct ? PaisDAOImplementation.GetAll().objects : null);
            model.addAttribute("paises", PaisDAOImplementation.GetAllJPA().correct ? PaisDAOImplementation.GetAllJPA().objects : null);
        } else { //Editar direccion
            UsuarioDireccion usuarioDireccion = new UsuarioDireccion();
            usuarioDireccion.Usuario = new Usuario();
            usuarioDireccion.Usuario.setIdUsuario(IdUsuario);
            usuarioDireccion.Direccion = new Direccion();
            usuarioDireccion.Direccion.setIdDireccion(IdDireccion);

            //usuarioDireccion.Direccion = (Direccion) direccionDAOImplementation.GetById(IdDireccion).object;
            usuarioDireccion.Direccion = (Direccion) direccionDAOImplementation.GetByIdJPA(IdDireccion).object;

            model.addAttribute("usuarioDireccion", usuarioDireccion);
            //model.addAttribute("paises", PaisDAOImplementation.GetAll().correct ? PaisDAOImplementation.GetAll().objects : null);
            model.addAttribute("paises", PaisDAOImplementation.GetAllJPA().correct ? PaisDAOImplementation.GetAllJPA().objects : null);
        }
        return "UsuarioForm";
    }

    @PostMapping("/GetAllDinamico")
    public String BusquedaDinamica(@ModelAttribute Usuario usuario, Model model) {
        //Result result = usuarioDAOImplementation.GetAllDinamico(usuario);
        Result result = usuarioDAOImplementation.GetAllDinamicoJPA(usuario);
        //Result resultRol = RolDAOImplementation.GetAll();
        Result resultRol = RolDAOImplementation.GetAllJPA();
        Usuario usuarioBusqueda = new Usuario();
        usuarioBusqueda.setStatus(-1);
        usuarioBusqueda.Rol = new Rol();

        model.addAttribute("roles", resultRol.object);
        model.addAttribute("listaUsuarios", result.objects);
        model.addAttribute("usuarioBusqueda", usuarioBusqueda);

        return "UsuarioIndex";
    }

    @PostMapping("/ActualizarStatus")
    @ResponseBody
    public String actualizarStatus(@RequestParam int IdUsuario, @RequestParam int Status) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(IdUsuario);
        usuario.setStatus(Status);

        //Result result = usuarioDAOImplementation.UpdateStatus(usuario);
        Result result = usuarioDAOImplementation.UpdateStatusJPA(usuario);

        return result.correct ? "OK" : "ERROR";
    }

    @PostMapping("Form")
    public String Form(@Valid @ModelAttribute UsuarioDireccion usuarioDireccion, BindingResult BindingResult, @RequestParam(required = false) MultipartFile imagenFile, Model model) {

//        if (BindingResult.hasErrors()) {
//            // Si hay errores, regresar al formunlario con los errores visibles
//            model.addAttribute("usuarioDireccion", usuarioDireccion);
//            return "UsuarioForm";
//        }
        try {
            if (!imagenFile.isEmpty()) {
                byte[] bytes = imagenFile.getBytes();
                String imgBase64 = Base64.getEncoder().encodeToString(bytes);
                usuarioDireccion.Usuario.setImagen(imgBase64);
            }
        } catch (Exception ex) {
            System.out.println("Error al procesar informacion");
        }

        if (usuarioDireccion.Usuario.getIdUsuario() == 0) {
            System.out.println("Estoy agregando un nuevo usuario y direccion");
            //usuarioDAOImplementation.Add(usuarioDireccion);
            usuarioDAOImplementation.AddJPA(usuarioDireccion);

        } else {
            if (usuarioDireccion.Direccion.getIdDireccion() == -1) { // Editar Usuario
                System.out.println("Estoy actualizando un usuario");
                //usuarioDAOImplementation.Update(usuarioDireccion.Usuario);
                usuarioDAOImplementation.UpdateJPA(usuarioDireccion.Usuario);
            } else if (usuarioDireccion.Direccion.getIdDireccion() == 0) { // Agregar direccion
                System.out.println("Estoy agregando direccion");
                //direccionDAOImplementation.DireccionAdd(usuarioDireccion);
                direccionDAOImplementation.DireccionAddJPA(usuarioDireccion);
            } else { // Editar direccion
                System.out.println("Estoy actualizando direccion");
                //direccionDAOImplementation.UpdateById(usuarioDireccion);
                direccionDAOImplementation.UpdateByIdJPA(usuarioDireccion);
            }
        }
        // Si no hay errores en la BD guardar los datos
//        usuarioDAOImplementation.Add(usuarioDireccion);

        return "redirect:/Usuario";
    }

    @GetMapping("EstadoByIdPais/{IdPais}")
    @ResponseBody
    public Result EstadoByIdPais(@PathVariable int IdPais) {
        //Result result = estadoDAOImplementation.EstadoByIdPais(IdPais);
        Result result = estadoDAOImplementation.EstadoByIdPaisJPA(IdPais);

        return result;
    }

    @GetMapping("MunicipioByIdEstado/{IdEstado}")
    @ResponseBody
    public Result MunicipioByIdEstado(@PathVariable int IdEstado) {
        //Result result = municipioDAOImplementation.MunicipioByIdEstado(IdEstado);
        Result result = municipioDAOImplementation.MunicipioByIdEstadoJPA(IdEstado);

        return result;
    }

    @GetMapping("ColoniaByIdMunicipio/{IdMunicipio}")
    @ResponseBody
    public Result ColoniaByIdMunicipio(@PathVariable int IdMunicipio) {
        //Result result = coloniaDAOImplementation.ColoniaByIdMunicipio(IdMunicipio);
        Result result = coloniaDAOImplementation.ColoniaByIdMunicipioJPA(IdMunicipio);

        return result;
    }*/
}
