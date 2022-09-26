//Guarda este archivo con el nombre Main.java
//package ....libros;

import java.io.*;
import java.util.*;

public class Main {

    public static Scanner teclado = new Scanner(System.in);
    public static PrintStream out = System.out;

    public static void pausar(String mensage) {
        out.print(mensage + "\nPresione <ENTER> para continuar . . . ");
        teclado.nextLine();
        out.println();
    }

    public static String leer_cadena(String mensaje) {
        out.print(mensaje + ": ");
        return teclado.nextLine();
    }

    public static int leer_entero(String mensaje) {
        try {
            return Integer.parseInt(leer_cadena(mensaje));
        } catch (NumberFormatException e) {
            out.print("N\u00FAmero incorrecto.");
            return leer_entero(mensaje);
        }
    }

    public static String ruta = "libros.tsv";

    // agregada 1
    // valida si es Linux para configurar el printstream y el scanner
    public static void validarSystemProperties() {

        if (!System.getProperties().get("os.name").equals("Linux") && System.console() != null)
            try {
                out = new PrintStream(System.out, true, "CP850");
                teclado = new Scanner(System.in, "CP850");
            } catch (UnsupportedEncodingException e) {
            }
    }

    // agregada 2.
    // Trae los libros de "libros.tsv"
    public static Vector<Libro> getLibrosFromFile() {
        String[] campos;
        Libro libro;
        Vector<Libro> vector = new Vector<Libro>();

        try {
            Scanner entrada = new Scanner(new FileReader(ruta));
            while (entrada.hasNextLine()) {
                campos = entrada.nextLine().split("\t");
                libro = new Libro();
                libro.setISBN(campos[0]);
                libro.setTitulo(campos[1]);
                libro.setAutor(campos[2]);
                libro.setEditorial(campos[3]);
                libro.setEdicion(Integer.parseInt(campos[4]));
                libro.setAnno_de_publicacion(Integer.parseInt(campos[5]));
                vector.add(libro);
            }
            entrada.close();

        } catch (FileNotFoundException e) {
        }
        return vector;
    }

    // agregada 3
    // guarda en archivo de ruta seteada
    public static void printEnArchivo(Vector<Libro> vector) {
        int i, n;

        Funcion<Libro> imprimirEnArchivo = new Funcion<Libro>() {
            @Override
            public void funcion(Libro libro, Object parametros) {
                PrintStream archivo = (PrintStream) parametros;
                archivo.print(libro.getISBN() + "\t");
                archivo.print(libro.getTitulo() + "\t");
                archivo.print(libro.getAutor() + "\t");
                archivo.print(libro.getEditorial() + "\t");
                archivo.print(libro.getEdicion() + "\t");
                archivo.print(libro.getAnno_de_publicacion() + "\n");
            }
        };
        try {
            PrintStream salida = new PrintStream(ruta);
            n = vector.size();
            for (i = 0; i < n; i++)
                imprimirEnArchivo.funcion(vector.get(i), salida);
            salida.close();
        } catch (FileNotFoundException e) {
        }

    }

    // agregada 4
    public static void imprimirOpciones() {
        out.println("MEN\u00DA");
        out.println("1.- Altas");
        out.println("2.- Consultas");
        out.println("3.- Actualizaciones");
        out.println("4.- Bajas");
        out.println("5.- Ordenar registros");
        out.println("6.- Listar registros");
        out.println("7.- Salir");
    }

    // agregada 5
    public static int getOpcionValida() {
        int opcion;

        do {
            opcion = leer_entero("Seleccione una opci\u00F3n");
            if (opcion < 1 || opcion > 7)
                out.println("Opci\u00F3nn no v\u00E1lida.");
        } while (opcion < 1 || opcion > 7);
        return opcion;
    }

    // agregada 6
    public static boolean hayRegistros(Vector<Libro> vector, int opcion) {
        if (vector.isEmpty() && opcion != 1 && opcion != 7) {
            pausar("No hay registros.\n");
            return false;
        }
        return true;
    }

    // agregada 7
    public static void procesarOpcion(int opcion, Vector<Libro> vector) {

        Funcion<Libro> imprimir = new Funcion<Libro>() {
            @Override
            public void funcion(Libro libro, Object parametros) {
                out.println(libro);
                int[] contador = (int[]) parametros;
                contador[0]++;
            }
        };

        int i, n;
        Libro dato = null, libro;
        int[] contador = { 0 };
        //int subopcion;
        libro = new Libro();

        // cargar isbn
        if (opcion < 5) {

            libro.setISBN(leer_cadena("Ingrese el ISBN del libro"));
            i = vector.indexOf(libro);
            dato = i < 0 ? null : vector.get(i);
            if (dato != null) {
                out.println();
                imprimir.funcion(dato, contador);
            }
        }

        if (esOpcionValida(opcion, dato)) {
            switch (opcion) {
                case 1: // dar de ALTA
                    libro = darDeAltaLibro(libro.getISBN());
                    vector.add(libro);
                    break;
                case 3: // ACTUALIZAR
                    i = vector.indexOf(libro);                    
                    dato = actualizarLibro(vector.get(i));
                    vector.set(vector.indexOf(libro), dato);
                                        
                    break;
                case 4: //BAJA
                    vector.remove(dato);
                    out.println("Registro borrado correctamente.");
                    break;
                case 5: //ORDENAR
                    Collections.sort(vector);
                    out.println("Registros ordenados correctamente.");
                    break;
                case 6: 
                    n = vector.size();
                    contador[0] = 0;
                    for (i = 0; i < n; i++)
                        imprimir.funcion(vector.get(i), contador);
                    out.println("Total de registros: " + contador[0] + ".");
                    break;

            }
        }

        if (opcion < 7 && opcion >= 1)
            pausar("");
    }

    // agregada 8
    public static boolean esOpcionValida(int opcion, Libro dato) {
        // si es dar de ALTA y ya existe isbn
        if (opcion == 1 && dato != null) {
            out.println("El registro ya existe.");
            return false;

        }
        // si es CONSULTA, ACTUALIZACION O BAJA y no existe libro
        else if (opcion >= 2 && opcion <= 4 && dato == null) {
            out.println("\nRegistro no encontrado.");
            return false;
        }
        return true;
    }

    // agregada 9
    public static Libro darDeAltaLibro (String ISBN) {

        Libro libro = new Libro();
        libro.setISBN(ISBN);
        libro.setTitulo(leer_cadena("Ingrese el titulo"));
        libro.setAutor(leer_cadena("Ingrese el autor"));
        libro.setEditorial(leer_cadena("Ingrese el editorial"));
        libro.setEdicion(leer_entero("Ingrese el edicion"));
        libro.setAnno_de_publicacion(leer_entero("Ingrese el anno de publicacion"));

        out.println("\nRegistro agregado correctamente.");

        return libro;
    }

    // agregada 10
    public static Libro actualizarLibro(Libro libro) {
        
        Libro dato = libro;
        int subopcion;

        out.println("Men\u00FA de modificaci\u00F3n de campos");
        out.println("1.- titulo");
        out.println("2.- autor");
        out.println("3.- editorial");
        out.println("4.- edicion");
        out.println("5.- anno de publicacion");
        do {
            subopcion = leer_entero("Seleccione un n\u00FAmero de campo a modificar");
            if (subopcion < 1 || subopcion > 5)
                out.println("Opci\u00F3n no v\u00E1lida.");
        } while (subopcion < 1 || subopcion > 5);
        switch (subopcion) {
            case 1:
                dato.setTitulo(leer_cadena("Ingrese el nuevo titulo"));
                break;
            case 2:
                dato.setAutor(leer_cadena("Ingrese el nuevo autor"));
                break;
            case 3:
                dato.setEditorial(leer_cadena("Ingrese el nuevo editorial"));
                break;
            case 4:
                dato.setEdicion(leer_entero("Ingrese el nuevo edicion"));
                break;
            case 5:
                dato.setAnno_de_publicacion(leer_entero("Ingrese el nuevo anno de publicacion"));
                break;
        }
        out.println("\nRegistro actualizado correctamente.");
        return dato;

    }

    public static void main(String[] args) {

        validarSystemProperties();
        Vector<Libro> vector = getLibrosFromFile();

        int opcion;

        do {
            imprimirOpciones();

            opcion = getOpcionValida();

            out.println();

            if (!hayRegistros(vector, opcion)) {
                continue;
            }

            procesarOpcion(opcion, vector);

        } while (opcion != 7);

        printEnArchivo(vector);
    }

}

interface Funcion<T extends Comparable<T>> {
    void funcion(T dato, Object parametros);
}

class Libro implements Comparable<Libro> {

    private String ISBN;
    private String titulo;
    private String autor;
    private String editorial;
    private int edicion;
    private int anno_de_publicacion;

    @Override
    public boolean equals(Object libro) {
        return this == libro || (libro instanceof Libro && ISBN.equals(((Libro) libro).ISBN));
    }

    @Override
    public int compareTo(Libro libro) {
        return ISBN.compareTo(libro.ISBN);
    }

    @Override
    public String toString() {
        return "ISBN               : " + ISBN + "\n" +
                "titulo             : " + titulo + "\n" +
                "autor              : " + autor + "\n" +
                "editorial          : " + editorial + "\n" +
                "edicion            : " + edicion + "\n" +
                "anno de publicacion: " + anno_de_publicacion + "\n";
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public int getEdicion() {
        return edicion;
    }

    public void setEdicion(int edicion) {
        this.edicion = edicion;
    }

    public int getAnno_de_publicacion() {
        return anno_de_publicacion;
    }

    public void setAnno_de_publicacion(int anno_de_publicacion) {
        this.anno_de_publicacion = anno_de_publicacion;
    }
    public void funcionAlPedo(){
        if(true){
            System.out.println("hola");
        }
        if(true){
            System.out.println("chau");
        }
    }

}