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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

    String urlBase = "http://localhost:8081/";
    String urlApi = "/usuarioapi/v1";
    String rolurlApi = "/rolapi/v1";
    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public String Index(Model model) {

        ResponseEntity<Result<UsuarioDireccion>> responseEntity = restTemplate.exchange(
                urlBase + urlApi,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result<UsuarioDireccion>>() {
        });
        Result response = responseEntity.getBody();

        ResponseEntity<Result<Rol>> responseRolEntity = restTemplate.exchange(
                urlBase + rolurlApi,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result<Rol>>() {
        });
        Result<Rol> resultRol = responseRolEntity.getBody();

        // Filtros de busqueda
        Usuario usuarioBusqueda = new Usuario();
        usuarioBusqueda.Rol = new Rol();

        model.addAttribute("usuarioBusqueda", usuarioBusqueda);
        //model.addAttribute("roles", resultRol.object);
        model.addAttribute("roles", resultRol.objects);
        model.addAttribute("listaUsuarios", response.objects);

        return "UsuarioIndex";
    }

    @GetMapping("Form/{IdUsuario}")
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

            // Obtener roles
            ResponseEntity<Result<Rol>> responseRolEntity = restTemplate.exchange(
                    urlBase + rolurlApi,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<Rol>>() {
            });
            Result<Rol> resultRol = responseRolEntity.getBody();

            // Obtener paises
            ResponseEntity<Result<Pais>> responsePaisEntity = restTemplate.exchange(
                    urlBase + "/paisapi/v1",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<Pais>>() {
            });
            Result<Pais> resultPais = responsePaisEntity.getBody();

            model.addAttribute("roles", resultRol.objects);
            model.addAttribute("usuarioDireccion", usuarioDireccion);
            model.addAttribute("paises", resultPais.correct ? resultPais.objects : null);
            return "UsuarioForm";
        } else { // Edicion
            System.out.println("Voy a mostrar el resumen de un usuario");

            // Obtener usuario por Id
            ResponseEntity<Result<UsuarioDireccion>> responseUsuarioDatos = restTemplate.exchange(
                    urlBase + urlApi + "/getById/" + IdUsuario,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Result<UsuarioDireccion>>() {
            }
            );

            Result<UsuarioDireccion> result = responseUsuarioDatos.getBody();

            if (result != null && result.correct && result.object != null) {
                UsuarioDireccion usuarioDireccion = result.object;
                model.addAttribute("usuarioDirecciones", usuarioDireccion);
            } else {
                model.addAttribute("usuarioDirecciones", new UsuarioDireccion());
                model.addAttribute("error", "No se pudo obtener el usuario");
            }
            return "UsuarioDetail";
        }
    }

    @GetMapping("Delete/{IdUsuario}")
    public String deleteUsuario(@PathVariable int IdUsuario) {

        try {

            ResponseEntity<Result> responseEntity = restTemplate.exchange(
                    urlBase + urlApi + "/usuario/delete/" + IdUsuario,
                    HttpMethod.DELETE,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result>() {
            });

            Result result = responseEntity.getBody();

            // Implementar mensaje de exito
        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
        }

        return "redirect:/Usuario";
    }

    @GetMapping("/DeleteDireccion/{IdDireccion}")
    public String deleteDireccionById(@PathVariable int IdDireccion) {
        try {
            ResponseEntity<Result> responseEntity = restTemplate.exchange(
                    urlBase + urlApi + "/deletedireccion/" + IdDireccion,
                    HttpMethod.DELETE,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result>() {
            });

            Result result = responseEntity.getBody();

        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
        }
        return "redirect:/Usuario";
    }

    // Muestra de formulario Carga Masiva
    @GetMapping("/CargaMasiva")
    public String CargaMasiva() {
        return "CargaMasiva"; // Renderizado de pagina HTML
    }

    // Procesa el archivo subido por el usuario
//    @PostMapping("/CargaMasiva")
//    public String CargaMasiva(@RequestParam MultipartFile archivo, Model model, HttpSession session) {
//
//        // Validación inicial: si no se seleccionó archivo
//        /*if (archivo == null || archivo.isEmpty()) {
//            model.addAttribute("mensaje", "No se seleciono archivo.");
//            return "cargaMasiva";
//            
//        }*/
//        try {
//            // Guardsarlo en un punto del sistema
//            if (archivo != null && !archivo.isEmpty()) { // Mientras el archivo no sea nulo ni esta vacio
//
//                //Body 
//                ByteArrayResource byteArrayResource = new ByteArrayResource(archivo.getBytes());
//                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//                body.add("archivo", byteArrayResource);
//
//                //Headers
//                HttpHeaders httpHeaders = new HttpHeaders();
//                httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//                //Entidad de la petición
//                HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(body, httpHeaders);
//
//                ResponseEntity<ResultFile> responseEntity = restTemplate.exchange(
//                        url,
//                        HttpMethod.GET,
//                        httpEntity,
//                        new ParameterizedTypeReference<ResultFile>() {
//                });
//
//                // Validar los datos leídos del archivo
//                List<ResultFile> listaErrores = ValidarArchivo(listaUsuarios);
//
//                if (listaErrores.isEmpty()) {
//                    // Si no hay errores, se guarda la ruta en la sesión y se puede procesar más adelante
//                    session.setAttribute("urlFile", absolutePath); // Guarda ruta en sesión si no hay errores
//                    model.addAttribute("listaErrores", listaErrores); // Mostrar la lista de errores "ya procesados"
//                    model.addAttribute("archivoNombre", archivo.getOriginalFilename());
//                    model.addAttribute("exito", true);
//                } else {
//                    // Si hay errores, se envían al frontend
//                    model.addAttribute("listaErrores", listaErrores); // Mostrar errores en el frontend
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace(); // Log del error
//            model.addAttribute("mensaje", "Error al procesar el archivo.");
//            return "redirect:/Usuario/CargaMasiva";
//        }
//        return "CargaMasiva"; // regresar a la vista
//    }
//
//    // Procesar archivo
//    @GetMapping("/CargaMasiva/Procesar")
//    public String ProcesarArchivo(HttpSession session) {
//        String absolutePath = session.getAttribute("urlFile").toString();
//        List<UsuarioDireccion> listaUsuarios = LecturaArchivoTXT(new File(absolutePath));
//
//        for (UsuarioDireccion usuarioDireccion : listaUsuarios) {
//            System.out.println("Estoy agregando un nuevo usuario y direccion");
//            //usuarioDAOImplementation.Add(usuarioDireccion);
//            usuarioDAOImplementation.AddJPA(usuarioDireccion);
//        }
//
//        return "CargaMasiva";
//    }
//
    @GetMapping("/formEditable")
    public String FormEditable(Model model, @RequestParam int IdUsuario, @RequestParam(required = false) Integer IdDireccion) {
        if (IdDireccion == null) {//Editar Usuario
            System.out.println("Voy a editar los datos del usuario");

            UsuarioDireccion usuarioDireccion = new UsuarioDireccion();

            // Obtener usuario por Id
            ResponseEntity<Result<UsuarioDireccion>> responseUsuario = restTemplate.exchange(
                    urlBase + urlApi + "/GetById/" + IdUsuario,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Result<UsuarioDireccion>>() {
            },
                    IdUsuario
            );

            Result<UsuarioDireccion> result = responseUsuario.getBody();

            if (result != null && result.correct && result.object != null) {

                // Preparar la dirección
                usuarioDireccion.Direccion = new Direccion();
                usuarioDireccion.Direccion.setIdDireccion(-1);
                model.addAttribute("usuarioDireccion", usuarioDireccion);

                // Obtener roles
                ResponseEntity<Result<Rol>> responseRolEntity = restTemplate.exchange(
                        urlBase + rolurlApi,
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<Result<Rol>>() {
                }
                );

                Result<Rol> resultRol = responseRolEntity.getBody();
                model.addAttribute("roles", resultRol.objects);
            }
        } else if (IdDireccion == 0) {//Agregar Direccion
            System.out.println("Voy a agregar una direccion al usuario");
            UsuarioDireccion usuarioDireccion = new UsuarioDireccion();
            usuarioDireccion.Usuario = new Usuario();
            usuarioDireccion.Usuario.setIdUsuario(1);
            usuarioDireccion.Direccion = new Direccion();
            usuarioDireccion.Direccion.setIdDireccion(0);
            model.addAttribute("usuarioDireccion", usuarioDireccion);

            // Obtener paises
            ResponseEntity<Result<Pais>> responsePaisEntity = restTemplate.exchange(
                    urlBase + "/paisapi/v1",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<Pais>>() {
            });
            Result<Pais> resultPais = responsePaisEntity.getBody();

            model.addAttribute("paises", resultPais.correct ? resultPais.objects : null);

            // Guardar la direccion
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<UsuarioDireccion> request = new HttpEntity<>(usuarioDireccion, headers);

            ResponseEntity<Result> response = restTemplate.exchange(
                    urlBase + urlApi + "/add",
                    HttpMethod.POST,
                    request,
                    Result.class
            );

            Result result = response.getBody();
            if (result != null && result.correct) {
                System.out.println("Dirección guardada correctamente");
                // Redireccionar o mostrar éxito
            } else {
                System.out.println("Error al guardar: " + result.errorMessage);
                model.addAttribute("error", result != null ? result.errorMessage : "Error desconocido");
            }

        } else { //Editar direccion
            System.out.println("Voy a editar la direccion de un usuario");

            ResponseEntity<Result<Direccion>> responseDireccion = restTemplate.exchange(
                    urlBase + urlApi + "/direccionById/" + IdDireccion,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<Direccion>>() {
            });

            Result<Direccion> resultDireccion = responseDireccion.getBody();

            UsuarioDireccion usuarioDireccion = new UsuarioDireccion();
            usuarioDireccion.Usuario = new Usuario();
            usuarioDireccion.Usuario.setIdUsuario(IdUsuario);

            if (resultDireccion != null && resultDireccion.correct && resultDireccion.object != null) {
                usuarioDireccion.Direccion = resultDireccion.object;
            } else {
                usuarioDireccion.Direccion = new Direccion(); // para evitar null
                model.addAttribute("error", "No se pudo obtener la dirección");
            }

            // Obtener paises
            ResponseEntity<Result<Pais>> responsePaisEntity = restTemplate.exchange(
                    urlBase + "/paisapi/v1",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<Pais>>() {
            });
            Result<Pais> resultPais = responsePaisEntity.getBody();

            model.addAttribute("usuarioDireccion", usuarioDireccion);

            model.addAttribute("paises", resultPais.correct ? resultPais.objects : null);

        }
        return "UsuarioForm";
    }

//    @PostMapping("/GetAllDinamico")
//    public String BusquedaDinamica(@ModelAttribute Usuario usuario, Model model) {
//        //Result result = usuarioDAOImplementation.GetAllDinamico(usuario);
//        Result result = usuarioDAOImplementation.GetAllDinamicoJPA(usuario);
//        //Result resultRol = RolDAOImplementation.GetAll();
//        Result resultRol = RolDAOImplementation.GetAllJPA();
//        Usuario usuarioBusqueda = new Usuario();
//        usuarioBusqueda.setStatus(-1);
//        usuarioBusqueda.Rol = new Rol();
//
//        model.addAttribute("roles", resultRol.object);
//        model.addAttribute("listaUsuarios", result.objects);
//        model.addAttribute("usuarioBusqueda", usuarioBusqueda);
//
//        return "UsuarioIndex";
//    }
    
    @PostMapping("Form")
    public String Form(@Valid @ModelAttribute UsuarioDireccion usuarioDireccion, BindingResult BindingResult, @RequestParam(required = false) MultipartFile imagenFile, Model model) {

//        if (BindingResult.hasErrors()) {
//            // Si hay errores, regresar al formunlario con los errores visibles
//            model.addAttribute("usuarioDireccion", usuarioDireccion);
//            return "UsuarioForm";
//        }
        // Validacion de imagen
        try {
            if (!imagenFile.isEmpty()) {
                byte[] bytes = imagenFile.getBytes();
                String imgBase64 = Base64.getEncoder().encodeToString(bytes);
                usuarioDireccion.Usuario.setImagen(imgBase64);
            }
        } catch (Exception ex) {
            // Regresar con la informacion que ya estaba faltante
            System.out.println("Error al procesar informacion");
        }

        if (usuarioDireccion.Usuario.getIdUsuario() == 0) {
            // Logica a consumir el DAO para agregar un nuevo Usuario
            System.out.println("Estoy agregando un nuevo usuario y direccion");

            usuarioDireccion.Usuario.setFNacimiento(new Date());

            HttpEntity<UsuarioDireccion> entity = new HttpEntity<>(usuarioDireccion);

            restTemplate.exchange(
                    urlBase + urlApi + "add",
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Result>() {
            });

        } else {
            /*if (usuarioDireccion.Direccion.getIdDireccion() == -1) { // Editar Usuario
                System.out.println("Estoy actualizando un usuario");
                //usuarioDAOImplementation.Update(usuarioDireccion.Usuario);
                usuarioDAOImplementation.UpdateJPA(usuarioDireccion.Usuario);
            } else if (usuarioDireccion.Direccion.getIdDireccion() == 0) { // Agregar direccion
                System.out.println("Estoy agregando direccion");
                //direccionDAOImplementation.DireccionAdd(usuarioDireccion);
                HttpEntity<UsuarioDireccion> entity = new HttpEntity<>(usuarioDireccion);

                restTemplate.exchange("endpointAdd",
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<Result>() {
                });

                direccionDAOImplementation.DireccionAddJPA(usuarioDireccion);
            } else { // Editar direccion
                System.out.println("Estoy actualizando direccion");
                //direccionDAOImplementation.UpdateById(usuarioDireccion);
                direccionDAOImplementation.UpdateByIdJPA(usuarioDireccion);
            }*/
        }
        // Si no hay errores en la BD guardar los datos
//        usuarioDAOImplementation.Add(usuarioDireccion);

        return "redirect:/Usuario";
    }

}
