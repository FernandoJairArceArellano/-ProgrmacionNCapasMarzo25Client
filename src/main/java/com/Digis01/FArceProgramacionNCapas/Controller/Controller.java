
package com.Digis01.FArceProgramacionNCapas.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@org.springframework.stereotype.Controller
@RequestMapping("/demo")
public class Controller {
    
    @GetMapping("/saludo")
    @ResponseBody
    public String holamundo() {
        return "Hola Mundo";
    }

    @GetMapping("/saludoPath/{nombre}")
    @ResponseBody
    public String saludoPath(@PathVariable String nombre) {
        return "Hola " + nombre;
    }

    @GetMapping("/saludoRequest")
    @ResponseBody
    public String saludoRequest(@RequestParam String nombre) {
        return "Hola " + nombre;
    }

    @GetMapping("/operaciones/{op}/{numeroUno}")
    @ResponseBody
    public String operaciones(
            @PathVariable String op,
            @PathVariable int numeroUno,
            @RequestParam int numeroDos) {

        int resultado = 0;
        String operacion = "";

        switch (op.toLowerCase()) {
            case "suma":
                resultado = numeroUno + numeroDos;
                operacion = "Suma";
                //localhost:8080/demo/operaciones/suma/10?numeroDos=5
                break;
            case "resta":
                resultado = numeroUno - numeroDos;
                operacion = "Resta";
                //localhost:8080/demo/operaciones/resta/10?numeroDos=5
                break;
            case "multiplicacion":
                resultado = numeroUno * numeroDos;
                operacion = "Multiplicación";
                //localhost:8080/demo/operaciones/multiplicacion/10?numeroDos=5
                break;
            case "division":
                if (numeroDos != 0) {
                    resultado = numeroUno / numeroDos;
                    operacion = "División";
                    //localhost:8080/demo/operaciones/division/10?numeroDos=0
                    //localhost:8080/demo/operaciones/division/10?numeroDos=5
                } else {
                    return "Error: No se puede dividir por cero";
                }
                break;
            default:
                return "Operación no válida. Usa: suma, resta, multiplicacion, division.";
        }

        String mensaje = "Operación: " + operacion + " | Numero uno: " + numeroUno + " Numero dos: " + numeroDos +" | Resultado: " + resultado;
        System.out.println(mensaje);
        return mensaje;
    }
}
