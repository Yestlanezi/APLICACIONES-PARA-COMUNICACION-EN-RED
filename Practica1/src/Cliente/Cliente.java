package Cliente;

import javax.swing.JFileChooser;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;

/*Funciones del cliente que haran las peticiones que se requieran al servidor*/
public class Cliente {

    private static int pto = 4444;
    //establecer el puerto de conexión que se utiliza para conectarse al servidor.
    private static String host = "127.0.0.1";
    //especifica la dirección IP del servidor al que se conectará el cliente.
    private static String rutaDirectorios = "";
    //almacenar la ruta de directorios que se utiliza para almacenar los archivos que el cliente descarga del servidor.
    public static String sep = System.getProperty("file.separator");
    //separa los nombres de archivo y las rutas de directorio.
    public static int[] tipoFile;
    //almacena los tipos de archivos que el cliente puede descargar del servidor.

    /**
     * *******************************************************************************************
     * ABRIR CARPETA
     * *******************************************************************************************
     */
    //acepta un índice como parámetro y se encarga de enviar una solicitud al servidor para abrir una carpeta en particular, 
    //recibir información sobre los archivos que se encuentran en ella y agregarlos al modelo 
    //de un componente gráfico en la interfaz de usuario.
    // Funcion abrir carpetas del servidor en el cliente
    public static void AbrirCarpeta(int indice) {
        //Toma un parámetro "índice" que representa la posición de la carpeta dentro del arreglo de archivos.
        try {
            Socket cl = new Socket(host, pto);
            //Se crea un nuevo objeto "Socket" que se conecta 
            //al host y puerto específico en el que se está ejecutando el servidor.
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); //OutputStream
            //"DataOutputStream" que utiliza la instancia de Socket para obtener un flujo de salida.
            //Esto se utilizará para enviar datos al servidor.
            //La bandera tiene el valor de 3 = AbrirCarpeta
            dos.writeInt(3);
            //indica al servidor que se está haciendo una solicitud para abrir una carpeta. 
            dos.flush();
            //flush()" para asegurarse de que los datos se envíen al servidor de inmediato.
            //Enviamos el indice en donde se encuentra la carpeta dentro del arreglo de Files[]
            dos.writeInt(indice);
            dos.flush();
            //Se escribe el índice de la carpeta en el flujo de salida y se llama a "flush()" para enviar los datos al servidor.

            DataInputStream dis = new DataInputStream(cl.getInputStream()); // InputStream
            //DataInputStream" que utiliza la instancia de Socket para obtener un flujo de entrada. 
            //Esto se utilizará para recibir datos del servidor.
            int numArchivos = dis.readInt();
            // cantidad de archivos que se encuentran en la carpeta.
            tipoFile = new int[numArchivos];

            for (int i = 0; i < numArchivos; i++) {
                String archivoRecibido = dis.readUTF();
                DropBox.modelo.addElement(archivoRecibido);
                tipoFile[i] = dis.readInt();
            }//for

            dis.close();
            dos.close();
            cl.close();
            System.out.println("Nueva carpeta abierta: Request recibido.");

        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }

    /**
     * *******************************************************************************************
     * ENVIAR ARCHIVO
     * *******************************************************************************************
     */
    /*
		Descripción: La función permite enviar un archivo o directorio.
		Parametros: Archivo a enviar, Ruta de dónde se encuentra ese archivo
		Regresa: Nada, solo envía el archivo.
                envia archivos o carpetas desde una ubicación de origen a una ubicación 
                de destino a través de un socket TCP.
     */
    public static void EnviarArchivo(File f, String pathOrigen, String pathDestino) {
        //f: es el archivo o carpeta que se va a enviar,
        //pathOrigen: es la ruta de la ubicación de origen del archivo o carpeta 
        //pathDestino: es la ruta de la ubicación de destino del archivo o carpeta.
        try {
            if (f.isFile()) {
                //comprueba si el objeto f es un archivo. Si es un archivo, el código dentro de este if se ejecutará. 
                //Si es una carpeta, se ejecutará el código dentro del else.
                Socket cl = new Socket(host, pto);
                //Socket que se conecta a un servidor en el host y puerto especificados. 
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); //OutputStream
                //DataOutputStream que envía datos al servidor a través del objeto Socket.
                String nombre = f.getName();
                //nombre: almacena el nombre del archivo o carpeta que se va a enviar.              
                long tam = f.length();
                //tam: almacena el tamaño del archivo o carpeta en bytes.

                System.out.println("\nSe envia el archivo " + pathOrigen + " con " + tam + " bytes");
                //muestra en la consola que se está enviando un archivo con la ruta de origen y el tamaño en bytes.
                DataInputStream dis = new DataInputStream(new FileInputStream(pathOrigen)); 
                // InputStream: recibe datos del archivo de origen.
    
                //La bandera tiene el valor de 0 = Subir archivo
                dos.writeInt(0);
                dos.flush();

                //Se envia info de los archivos
                //metadatos
                dos.writeUTF(nombre);
                dos.flush();
                dos.writeLong(tam);
                dos.flush();
                dos.writeUTF(pathDestino);
                dos.flush();

                long enviados = 0;
                //enviados: almacena el número de bytes que se han enviado al servidor. 
                int pb = 0;
                int n = 0, porciento = 0;
                //pb y porciento: variables calcular el porcentaje de bytes que se han enviado
                byte[] b = new byte[2000];
                //n: almacena el número de bytes que se lee del archivo. 
                //b: almacena temporalmente los datos que se leen del archivo.
                
                // mientras la cantidad de bytes enviados sea menor que el tamaño del archivo.
                //Dentro del ciclo se realiza la lectura de los bytes del archivo en un arreglo de bytes llamado "b"
                while (enviados < tam) {
                    n = dis.read(b);
                    dos.write(b, 0, n);
                    dos.flush();
                    enviados += n;
                    porciento = (int) ((enviados * 100) / tam);
                    System.out.println("\r Enviando el " + porciento + "% --- " + enviados + "/" + tam + " bytes");
                } //while

                JOptionPane.showMessageDialog(null, "Se ha subido el archivo " + nombre + " con tamanio: " + tam);
                dis.close();
                dos.close();
                cl.close();
            } // If
            //En caso de que no sea un archivo, crea un nuevo socket para conectarse 
            //al servidor y envía la carpeta con sus archivos.
            else {
                Socket cl = new Socket(host, pto);
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());

                String nombre = f.getName();
                String ruta = f.getAbsolutePath();
                System.out.println("Nombre: " + nombre + " Ruta: " + ruta);

                String aux = rutaDirectorios;
                rutaDirectorios = rutaDirectorios + sep + nombre;

                //La bandera tiene el valor de 4 = Subir Carpeta
                dos.writeInt(4);
                dos.flush();

                //Se envia info de los archivos
                dos.writeUTF(rutaDirectorios);
                dos.flush();

                // Envio los archivos que pertenecen al directorio creado
                File folder = new File(ruta);
                File[] files = folder.listFiles();

                for (File file : files) {
                    String path = rutaDirectorios + sep + file.getName();
                    System.out.println("Ruta destino en el servidor:" + path);
                    EnviarArchivo(file, file.getAbsolutePath(), path);
                }// for

                rutaDirectorios = aux;
                dos.close();
                cl.close();
            } // Else		
        } // try
        catch (Exception e) {
            e.printStackTrace();
            //Captura cualquier excepción que pueda surgir durante 
            //la ejecución del código y la muestra en la consola de depuración.
        }
    } // Enviar archivo

    
    /**
     * *******************************************************************************************
     * SELECCIONAR ARCHIVOS
     * *******************************************************************************************
     */
    // Envia muchos archivos al servidor
    public static void SeleccionarArchivos() {
        try {
            JFileChooser jf = new JFileChooser();
            //JFileChooser, la cual es un diálogo que permite al usuario seleccionar archivos y carpetas.
            jf.setMultiSelectionEnabled(true);
            //el usuario pueda seleccionar varios archivos y carpetas.
            jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            //permite seleccionar tanto archivos como carpetas.
            int r = jf.showOpenDialog(null);
            //r:  almacena la respuesta que el usuario proporciona.
            if (r == JFileChooser.APPROVE_OPTION) {
                //Si el usuario ha seleccionado al menos un archivo o carpeta, se obtiene una referencia 
                //a cada archivo o carpeta seleccionada y se almacenan en un arreglo de objetos File.
                rutaDirectorios = "";
                File[] files = jf.getSelectedFiles();
                for (File file : files) {
                    //se obtiene la ruta absoluta de cada archivo o carpeta.
                    String rutaOrigen = file.getAbsolutePath();
                    // Tipo caso base: La primera vez que mandemos un archivo
                    // Siempre estará en la raíz del servidor
                    EnviarArchivo(file, rutaOrigen, file.getName());
                }//for
                DropBox.modelo.clear();
                //elimina el contenido de un modelo de datos
                Actualizar();
                //actualiza los datos
            }//if   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * *******************************************************************************************
     * ACTUALIZAR
     * *******************************************************************************************
     */
    public static void Actualizar() {
        try {
            Socket cl = new Socket(host, pto);
            // Socket para conectarse a un servidor en un determinado host
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); //OutputStream
            // cl: conexion establecida
            //La bandera tiene el valor de 1 = Actualizar 
            dos.writeInt(1);
            //Se envía un entero (1) al servidor a través del objeto DataOutputStream 
            //para indicar que se solicita una actualización.
            dos.flush();
            //lush()" se utiliza para asegurarse de que los datos se envíen 
            //inmediatamente y no se queden en el búfer del objeto DataOutputStream.
            DataInputStream dis = new DataInputStream(cl.getInputStream()); // InputStream
            //recibi datos del servidor a través del objeto Socket
            //dis: almacena esta conexión de entrada.
            int numArchivos = dis.readInt();
            //indica cuántos archivos están siendo enviados a través de la conexión.
            tipoFile = new int[numArchivos];
            //tamaño del número de archivos recibidos desde el servidor.

            for (int i = 0; i < numArchivos; i++) {
                //Se lee primero la ruta absoluta del archivo en formato UTF-8
                String archivoRecibido = dis.readUTF();
                DropBox.modelo.addElement(archivoRecibido);
                tipoFile[i] = dis.readInt();
            }//for

            dis.close();
            dos.close();
            cl.close();
            System.out.println("Carpeta del cliente actualizada.");

        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }//Actualizar

    /**
     * *******************************************************************************************
     * RECIBIR ARCHIVOS
     * *******************************************************************************************
     */
    public static void RecibirArchivos(String[] nombresArchivos, int tama) {
        try {
            //Se crea un objeto Socket con el nombre cl, que se utiliza para establecer una conexión con un servidor. 
            //l primer parámetro host y el segundo parámetro pto representan el host y el puerto al que se desea conectar.
            Socket cl = new Socket(host, pto);
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); //OutputStream: enviar datos al servidor
            DataInputStream dis = new DataInputStream(cl.getInputStream()); // InputStream: recibir datos del servidor 

            //La bandera tiene el valor de 2 = Descargar seleccion
            dos.writeInt(2);
            dos.flush();
            //2: Este valor representa una bandera que indica que se ha seleccionado descargar archivos. 
            //Luego se llama al método flush() para asegurarse de que los datos se hayan enviado correctamente.

            dos.writeInt(tama);
            //tamaño de los archivos que se descargaran
          
            dos.flush();
            //asegurarse de que los datos se enviaron correctamente 
            //Enviamos los indices de los archivos seleccionados
            String aux = "";

            for (int i = 0; i < tama; i++) {
                aux = nombresArchivos[i];
                dos.writeUTF(aux);
                dos.flush();
            }

            String nombre = System.getProperty("user.home") + "/Escritorio/";

            nombre = nombre + dis.readUTF();

            long tam = dis.readLong();
            System.out.println("\nSe recibe el archivo " + nombre + " con " + tam + "bytes");

            DataOutputStream dosArchivo = new DataOutputStream(new FileOutputStream(nombre)); // OutputStream

            long recibidos = 0;
            int n = 0, porciento = 0;
            byte[] b = new byte[2000];

            while (recibidos < tam) {
                //recibe el archivo desde el servidor y guardarlo en el sistema de archivos local.
                n = dis.read(b);
                dosArchivo.write(b, 0, n);
                dosArchivo.flush();
                recibidos += n;
                porciento = (int) ((recibidos * 100) / tam);
                System.out.println("\r Recibiendo el " + porciento + "% --- " + recibidos + "/" + tam + " bytes");
            } // while
            //mientras el número de bytes recibidos (recibidos) sea menor que el tamaño total del archivo (tam).

            JOptionPane.showMessageDialog(null, "Se ha descargado el archivo " + nombre + " con tamanio: " + tam);
            dos.close();
            dis.close();
            dosArchivo.close();
            cl.close();

        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }

    /**
     * *******************************************************************************************
     * ELIMINAR ARCHIVO
     * *******************************************************************************************
     */
    /*
		Descripción: La función permite eliminar un archivo o directorio.
		Parametros: Archivo a eliminar
		Regresa: Nada, solo elimina el archivo.
     */
    
    /*
    public static void EliminarCarpeta(String[] nombresArchivos, int tama) {
        try {
            //se crea un objeto Socket, que establece una conexión con un servidor remoto 
            //en una dirección IP y un puerto especificados por las variables "host" y "pto"
            Socket cl = new Socket(host, pto);
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); //OutputStream
            DataInputStream dis = new DataInputStream(cl.getInputStream()); // InputStream

            //La bandera tiene el valor de 2 = Descargar seleccion
            dos.writeInt(5);
            dos.flush();

            dos.writeInt(tama);
            dos.flush();

            //Enviamos los indices de los archivos seleccionados
            /*String aux = "";

            for (int i = 0; i < tama; i++) {
                //envia los nombres de los archivos que se van a eliminar al servidor.
                aux = nombresArchivos[i];
                dos.writeUTF(aux);
                dos.flush();
            }

            String nombre = dis.readUTF();           
            long tam = dis.readLong();
            ////se lee el nombre y el tamaño del archivo eliminado del servidor 
            JOptionPane.showMessageDialog(null, "Se ha eliminado el archivo o carptea " + nombre + " con tamanio: " + tam);
            
            
            for (int i = 0; i < tama; i++) {
    String nombre = nombresArchivos[i];
    File archivo = new File(nombre);
    boolean esCarpeta = archivo.isDirectory();

    // enviar el nombre del archivo o carpeta a eliminar
    dos.writeUTF(nombre);
    dos.flush();

    // si es una carpeta, eliminar recursivamente sus contenidos antes de eliminarla
    if (esCarpeta) {
        EliminarCarpeta(nombresArchivos, dos, dis);
    }

    // recibir la respuesta del servidor
    boolean eliminado = dis.readBoolean();
    if (eliminado) {
        JOptionPane.showMessageDialog(null, "Se ha eliminado " + (esCarpeta ? "la carpeta " : "el archivo ") + nombre);
    } else {
        JOptionPane.showMessageDialog(null, "No se pudo eliminar " + (esCarpeta ? "la carpeta " : "el archivo ") + nombre);
    }
}
            dos.close();
            dis.close();
            cl.close();

        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    } // Eliminar archivo

   */
  
    
    private static void eliminarCarpeta(String nombre, DataOutputStream dos, DataInputStream dis) throws IOException {
    File carpeta = new File(nombre);
    File[] archivos = carpeta.listFiles();

    for (File archivo : archivos) {
        String nombreArchivo = archivo.getPath();
        boolean esCarpeta = archivo.isDirectory();

        if (esCarpeta) {
            eliminarCarpeta(nombreArchivo, dos, dis);
        }

        // enviar el nombre del archivo o carpeta a eliminar
        dos.writeUTF(nombreArchivo);
        dos.flush();

        // recibir la respuesta del servidor
        boolean eliminado = dis.readBoolean();
        if (!eliminado) {
            JOptionPane.showMessageDialog(null, "No se pudo eliminar " + (esCarpeta ? "la carpeta " : "el archivo ") + nombreArchivo);
            return;
        }
    }

    // eliminar la carpeta vacía
    dos.writeUTF(nombre);
    dos.flush();

    boolean eliminado = dis.readBoolean();
    if (!eliminado) {
        JOptionPane.showMessageDialog(null, "No se pudo eliminar la carpeta " + nombre);
    }
}

    static void eliminarCarpeta(String[] nombreSeleccion, int length) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
