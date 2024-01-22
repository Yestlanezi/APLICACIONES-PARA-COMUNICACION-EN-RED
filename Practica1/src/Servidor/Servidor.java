package Servidor;

// SERVIDOR
import java.net.*;
import java.io.*;
import static java.nio.file.Files.delete;
import java.util.zip.*;
import javax.swing.JOptionPane;


public class Servidor {

    public static String sep = System.getProperty("file.separator");
    //separa los nombres de archivo y las rutas de directorio.
    private static String rutaServer = "." + sep + "serverP1" + sep;
    //el servidor almacenará los archivos que serán compartidos con los clientes.
    private static File[] list;
    // representa un archivo o directorio en el sistema de archivos.
    private static String rutaActual = "";
    // almacena la ruta actual que el servidor está explorando en el directorio "rutaServer" 
    //mientras busca archivos.
    private static int numVeces = 0;
    //realiza un seguimiento del número de veces que se ha explorado una ruta específica 
    //en el directorio "rutaServer".

    /**
     * *******************************************************************************************
     * 0. RECIBIR ARCHIVOS
     * *******************************************************************************************
     */
    // Valor de la bandera = 0
    public static void RecibirArchivos(DataInputStream dis, String nombre) throws IOException { 
        //DataInputStream te permite leer datos del socket conectado al Cliente 
        long tam = dis.readLong();
        //Lee el tamaño del archivo que se envia desde el cliente 
        String pathDestino = dis.readUTF();
        //e lee la ruta de destino en el servidor donde se almacenará el archivo (dis.readUTF())
        nombre = rutaServer + pathDestino;   
        System.out.println("\nSe recibe el archivo " + nombre + " con " + tam + "bytes");
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre)); // OutputStream
        //DataOutputStream que permite escribir datos en el archivo que se ha recibido.
        long recibidos = 0;
        //es la cantidad de bytes recibidos hasta el momento.
        int n = 0, porciento = 0;
        //es el número de bytes leídos en cada iteración.
        byte[] b = new byte[2000];
        //leer y escribir el archivo

        while (recibidos < tam) {
            //se repetirá mientras no se hayan recibido todos los bytes del archivo.
            n = dis.read(b);
            // n: bytes del socket
            // dis: se escriben en el archivo 
            dos.write(b, 0, n);
            //dos: actualiza la cantidad de bytes
            dos.flush();
            recibidos += n;
            porciento = (int) ((recibidos * 100) / tam);
            System.out.println("\r Recibiendo el " + porciento + "% --- " + recibidos + "/" + tam + " bytes");
            // imprime el porcentaje del archivo recibido hasta el momento.
        } // while

        System.out.println("\nArchivo " + nombre + " de tamanio: " + tam + " recibido.");
        dos.close();
        dis.close();
    } // RecibirArchivos

    
    /**
     * *******************************************************************************************
     * 1. ACTUALIZAR CLIENTES
     * *******************************************************************************************
     */
    //Valor de la bandera = 1
    public static void ActualizarCliente(Socket cl, DataInputStream dis, String path, int bandera) throws IOException {
        //DataInputStream: te permite leer datos del socket conectado al Cliente 
        //string path: la ruta donde se encuentran los archivos del servidor.
        //bandera: una bandera para saber si es la primera vez que se llama a la función o no.
        File archivosRuta = new File(path);
          //Crea un objeto File con la ruta del servidor.
        if (!archivosRuta.exists()) {
            //Verifica si la ruta del servidor existe. Si no existe, se crea una nueva carpeta en esa ruta.
            archivosRuta.mkdir();
        }//if

        if (bandera == 1) {
            //concatena la ruta actual con el nombre de la carpeta actual. 
            rutaActual = rutaActual + sep + archivosRuta.getName();
            System.out.println("Ubicacion: " + rutaActual);
        }

        list = archivosRuta.listFiles();
        //Obtiene la lista de archivos y carpetas de la ruta del servidor.

        DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); // OutputStream
        //envia información al cliente.
        dos.writeInt(list.length);
        dos.flush();
        //Envía al cliente la cantidad de archivos y carpetas que hay en la carpeta actual del servidor.
        String info = "";
        int tipo = 0;

        for (File f : list) {
            if (f.isDirectory()) {
                tipo = 1;
                if (bandera == 0) {//Ruta raiz - Inicio
                    info = "." + sep + f.getName();
                } else {//Abrir ruta y concatenar
                    info = "." + rutaActual + sep + f.getName();
                }
            }//if
            else {
                tipo = 2;
                if (bandera == 0) {//Ruta raiz - Inicio
                    info = f.getName();
                    //info = f.getName() + "  -------  " + f.length() + " bytes";
                } else {//Abrir ruta y concatenar
                    info = "." + rutaActual + sep + f.getName();
                    //info = "." + rutaActual + sep + f.getName() + "  -------  " + f.length() + " bytes";
                }
            }//else
            dos.writeUTF(info);
            dos.flush();
            dos.writeInt(tipo);
            dos.flush();

            tipo = 0;
        }//for
        dos.close();
        System.out.println("Informacion enviada al cliente: Carpeta actualizada.");
        //Recorre la lista de archivos y carpetas y envía la información al cliente. 
        //Si el archivo es una carpeta, el tipo será igual a 1 y se envía el nombre de la carpeta con la ruta actual.
        //Si el archivo es un archivo, el tipo será igual a 2 y se envía el nombre del archivo con la ruta actual. 
    }//Actualizar

    
    /**
     * *******************************************************************************************
     * CREAR ARCHIVO .ZIP
     * *******************************************************************************************
     */
    public static void crearZIP(DataInputStream dis, int tam) {
        //recibe un objeto DataInputStream y un número tam que representa la cantidad de archivos a
        //incluir en el archivo ZIP.
        try {
            //Enviamos los indices de los archivos seleccionados
            String[] nombreArchivos = new String[tam];
            //almacena los nombres de los archivos a incluir en el archivo ZIP.
            String aux = "";
            int i, j;
            for (i = 0; i < tam; i++) {
                nombreArchivos[i] = dis.readUTF();
                System.out.println("\nArchivo: " + nombreArchivos[i]);
                //mprime en consola el nombre del archivo que se acaba de leer.
            }

            // Quito ./ al nombre del directorio
            char aux1 = ' ', aux2 = ' ';
            String nombre = "";
            for (i = 0; i < tam; i++) {
                aux1 = nombreArchivos[i].charAt(0);
                if (aux1 == '.') {
                    for (j = 2; j < nombreArchivos[i].length(); j++) {
                        nombre = nombre + Character.toString(nombreArchivos[i].charAt(j));
                    }
                    nombreArchivos[i] = nombre;
                    nombre = "";
                }
            }
            String destino = rutaServer + "Download" + numVeces + ".zip";
            FileOutputStream fos = new FileOutputStream(destino);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            String sourceFile = "";
            for (i = 0; i < tam; i++) {
                // Le doy la ruta de mi archivo o directorio
                sourceFile = rutaServer + nombreArchivos[i];
                File fileToZip = new File(sourceFile);
                zipFile(fileToZip, fileToZip.getName(), zipOut);
                sourceFile = " ";
            }
            zipOut.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * *******************************************************************************************
     * ZIP
     * *******************************************************************************************
     */
    public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }

        if (fileToZip.isDirectory()) {
            if (fileName.endsWith(sep)) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + sep));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + sep + childFile.getName(), zipOut);
            }
            return;
        }

        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;

        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }

        fis.close();
        System.out.println("Archivos comprimidos en un ZIP. Listo para enviar...");
    }

    /**
     * *******************************************************************************************
     * EnviarArchivo
     * *******************************************************************************************
     */
    public static void EnviarArchivo(DataOutputStream dos, File f) {
        //enviar el archivo especificado por el objeto File a través del objeto DataOutputStream.
        try {
            String nombre = f.getName();
            // contiene el nombre del archivo
            long tam = f.length();
            //ontiene el tamaño del archivo en bytes
            String path = f.getAbsolutePath();
            //contiene la ruta absoluta del archivo en el sistema de archivos.
            System.out.println("\nSe envia el archivo " + nombre + " con " + tam + " bytes");
            //Se imprime en la consola un mensaje que indica el nombre y tamaño del archivo que se va a enviar.
            DataInputStream disArchivo = new DataInputStream(new FileInputStream(path)); 
            // InputStream: leerá los datos del archivo. Se inicializa con un objeto FileInputStream que utiliza la ruta absoluta del archivo.
            //Se envia info de los archivos
            dos.writeUTF(nombre);
            //Se escriben los metadatos del archivo en el objeto.
            //Primero se envía el nombre del archivo.      
            dos.flush();         
            //asegurarse de que los datos se envíen de inmediato y no se almacenen en un búfer. 
            dos.writeLong(tam);
            //su tamaño   
            dos.flush();
            long enviados = 0;
            //antidad de bytes que se han enviado hasta el momento
            int n = 0, porciento = 0;
            //cantidad de bytes que se van a leer del archivo en cada iteración
            byte[] b = new byte[2000];
            //porcentaje de bytes enviados en relación con el tamaño total del archivo
            while (enviados < tam) {
                //se ejecutará mientras la cantidad de bytes enviados sea menor que el tamaño total del archivo.
                n = disArchivo.read(b);
                //bytes del archivo en el búfer 
                dos.write(b, 0, n);
                dos.flush();
                enviados += n;
                porciento = (int) ((enviados * 100) / tam);
                System.out.println("\r Enviando el " + porciento + "% --- " + enviados + "/" + tam + " bytes");
            } //while

            System.out.println("\nArchivo " + nombre + " de tamanio: " + tam + " enviado.");

            disArchivo.close();
            dos.close();
        } // try
        catch (Exception e) {
            e.printStackTrace();
        }
    } // Enviar archivo

    /**
     * *******************************************************************************************
     * EnviarArchivo
     * *******************************************************************************************
     */
    
    public static void deleteDir(File file) {
        //File: contiene los archivos y subdirectorios contenidos en el directorio especificado por el objeto 
        File [] contents = file.listFiles();
        if (contents != null) {
            //Verifica que la variable contents no sea =NULL, lo que indicaría que el directorio está vacío.
            for (File f : contents) {
                deleteDir(f);
                //Llama recursivamente a la función deleteDir pasando como argumento el archivo o subdirectorio actual.
            }
        }
        file.delete();
        //Elimina el directorio especificado por el objeto file. 
        //Si el directorio contiene archivos o subdirectorios, estos habrán sido eliminados en el paso anterior mediante las llamadas recursivas a deleteDir.
    }

    
    public static void EliminarArchivo(DataInputStream dis, int tam, DataOutputStream dos) {
        
        try {
            String[] nombreArchivos = new String[tam];
            String aux = "";
            int i, j;
            
            for (i = 0; i < tam; i++) {
    String nombre = nombreArchivos[i];
    File archivo = new File(nombre);
    boolean esCarpeta = archivo.isDirectory();

    // enviar el nombre del archivo o carpeta a eliminar
    dos.writeUTF(nombre);
    dos.flush();

    // si es una carpeta, eliminar recursivamente sus contenidos antes de eliminarla
    if (esCarpeta) {
        eliminarCarpeta(nombre, dos, dis);
    }

    // recibir la respuesta del servidor
    boolean eliminado = dis.readBoolean();
    if (eliminado) {
        JOptionPane.showMessageDialog(null, "Se ha eliminado " + (esCarpeta ? "la carpeta " : "el archivo ") + nombre);
    } else {
        JOptionPane.showMessageDialog(null, "No se pudo eliminar " + (esCarpeta ? "la carpeta " : "el archivo ") + nombre);
    }
}
            
            
            /*
            for (i = 0; i < tam; i++) {
                nombreArchivos[i] = dis.readUTF();
                String nombre = nombreArchivos[i];
                boolean bandera = false;
                if (nombre.indexOf(".") == 0) {
                    nombre = nombre.substring(2,nombre.length());
                    bandera = true;
                }
                System.out.println("\nArchivo: " + nombre);
                File f = new File(rutaServer + nombre);
                if (bandera) {
                    deleteDir(f);
                    //Llama al método deleteDir para eliminar la carpeta representada por el objeto f.
                    System.out.println("Carpeta eliminada");
                } else {
                    if (f.delete()) {
                        System.out.println("\nArchivo " + nombre + " de tamanio: " + tam + " Eliminado.");
                    } else {
                        System.out.println("\nArchivo " + nombre + " de tamanio: " + tam + " no eliminado.");

                    }
                }
                dos.writeUTF(nombre);
                dos.flush();
                dos.writeLong(tam);
                dos.flush();
            }*/

        } // try
        catch (Exception e) {
            e.printStackTrace();
        }
    } // Enviar archivo

    
    
 
    
    
    
    /**
     * *******************************************************************************************
     * MAIN
     * *******************************************************************************************
     */
    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(4444);
            s.setReuseAddress(true);
            System.out.println("Servidor de archivos iniciado, esperando cliente...");

            // Espera clientes
            for (;;) {
                Socket cl = s.accept();
                System.out.println("\n\nCliente conectado desde " + cl.getInetAddress() + " " + cl.getPort());
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); //OutputStream
                DataInputStream dis = new DataInputStream(cl.getInputStream()); // InputStream

                int bandera = dis.readInt();

                if (bandera == 0) {
                    //Subir un archivo -> El servidor recibe
                    String nombre = dis.readUTF();
                    RecibirArchivos(dis, nombre);
                } else if (bandera == 1) {
                    //Ver archivos / Actualizar -> El servidor envia los nombres de los archivos
                    //Bandera = 0. Se actualiza la carpeta raiz
                    rutaActual = "";
                    ActualizarCliente(cl, dis, rutaServer, 0);

                } else if (bandera == 2) {
                    //Descargar archivos -> El servidor prepara y envia archivos
                    //Subir archivos -> El servidor recibe
                    int tam = dis.readInt();
                    String path = "Download" + numVeces + ".zip";
                    path = rutaServer + path;
                    System.out.println("" + path);
                    File archivoZip = new File(path);
                    System.out.println("" + archivoZip.getAbsoluteFile());

                    crearZIP(dis, tam);

                    if (archivoZip.exists()) {
                        //System.out.println("Si existeee");
                        System.out.println("La path del archivo esta en: " + path + " Con nombre: " + archivoZip.getName());
                        EnviarArchivo(dos, archivoZip);
                        // Lo elimino porque no debe estar en el servidor, solo lo hice temporalmente
                        if (archivoZip.delete()) {
                            System.out.println("Archivo temporal Download" + numVeces + ".zip eliminado");
                        }
                    }

                    numVeces++;

                } else if (bandera == 3) {
                    //Abrir carpeta -> El servidor envia los nombres de los contenidos de la carpeta seleccionada
                    int ubicacionRuta = dis.readInt();
                    //Bandera = 1. Se navega dentro de una carpeta
                    String nuevaRuta = "" + list[ubicacionRuta].getAbsoluteFile();
                    ActualizarCliente(cl, dis, nuevaRuta, 1);
                } else if (bandera == 4) {
                    //Subir archivos -> El servidor recibe
                    String rutaDirectorio = dis.readUTF();
                    String path = rutaServer + rutaDirectorio;
                    File archivosRuta = new File(path);
                    if (!archivosRuta.exists()) {
                        archivosRuta.mkdir();
                    }
                } else if (bandera == 5) {
                    int tam = dis.readInt();
                    EliminarArchivo(dis, tam, dos);
                } else {
                    System.out.println("Error al atender la solicitud del cliente.");
                }
                dis.close();
                cl.close();
            }//for
        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }//main

    private static void eliminarCarpeta(String nombre, DataOutputStream dos, DataInputStream dis) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
