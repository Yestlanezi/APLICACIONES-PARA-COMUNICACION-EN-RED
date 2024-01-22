import java.net.*;

public class PruebaS//Se define la clase pública SecoD.
{
    public static void main(String[] args) //Se define el método main, que es el punto de entrada del programa.
    {
      try
      {  
          int pto=1234; //Se establece el número de puerto pto donde el servidor estará escuchando.
          String msj=""; //Se inicializa una cadena de caracteres msj vacía.
          DatagramSocket s = new DatagramSocket(pto); //Se crea un objeto DatagramSocket s para recibir y enviar datagramas. Este objeto se asocia con el número de puerto especificado.
          s.setReuseAddress(true); //Se llama al método setReuseAddress() del objeto DatagramSocket para permitir la reutilización del puerto en caso de que el servidor se reinicie.
          System.out.println("Starting server... waiting for datagrams"); //Se muestra un mensaje en la consola para indicar que el servidor ha iniciado.
          //modificacion
          int numeroDeSecuenciaEsperado = 1; 
          //modificacion
          for(;;) //Se inicia un bucle infinito for(;;) donde el servidor se quedará esperando por datagramas entrantes.
          {
              //modificacion 
              boolean reinicio = false;
              //modificacion

              byte[] b = new byte[65535]; //se crea un arreglo de bytes b con tamaño máximo de 65535 (tamaño máximo permitido de un datagrama).
              DatagramPacket p = new DatagramPacket(b,b.length); //Se crea un objeto DatagramPacket p para almacenar el datagrama entrante.

              s.receive(p); //Se llama al método receive() del objeto DatagramSocket s para esperar a que llegue un datagrama. El datagrama recibido se almacena en el objeto DatagramPacket p.
              msj = new String(p.getData(),0,p.getLength()); //Se convierte el contenido del DatagramPacket en una cadena de caracteres utilizando el constructor de la clase String.
              
              String[] parts = msj.split("\\s+");
              int numeroSecuencia =  Integer.parseInt(parts[0]);

              int index = msj.indexOf(" ");
              String msjEnviar = msj.substring(index + 1);

              byte[] msjEnviarByte = msjEnviar.getBytes();

              DatagramPacket p1 = new DatagramPacket(msjEnviarByte,msjEnviarByte.length,p.getAddress(),p.getPort());

              if(numeroSecuencia==0)
              {
                System.out.println("Received from:  "+p.getAddress()+":"+p.getPort()+" sequence number: "+Integer.toString(numeroSecuencia)+" message: "+msjEnviar); //Se muestra un mensaje en la consola que indica la dirección y puerto del remitente del datagrama, así como el contenido del mensaje.
                s.send(p1); //Se llama al método send() del objeto DatagramSocket s para enviar una respuesta al remitente del datagrama. Como se está reutilizando el objeto DatagramPacket p, éste ya tiene la dirección y puerto del remitente almacenados y sólo es necesario enviar el mismo datagrama de vuelta.
              }
              else
              {
                  if(numeroSecuencia==numeroDeSecuenciaEsperado)
                  {
                    System.out.println("waiting datagram with sequence number "+numeroDeSecuenciaEsperado); 
                    System.out.println("Received from: "+p.getAddress()+":"+p.getPort()+" sequence number: "+Integer.toString(numeroSecuencia)+" message:"+msjEnviar); //Se muestra un mensaje en la consola que indica la dirección y puerto del remitente del datagrama, así como el contenido del mensaje.
                    s.send(p1); //Se llama al método send() del objeto DatagramSocket s para enviar una respuesta al remitente del datagrama. Como se está reutilizando el objeto DatagramPacket p, éste ya tiene la dirección y puerto del remitente almacenados y sólo es necesario enviar el mismo datagrama de vuelta.
                    numeroDeSecuenciaEsperado++;
                  }
                  else if(numeroSecuencia==-1)
                  {
                    System.out.println("Waiting datagram with sequence number -1"); 
                    System.out.println("Received from: "+p.getAddress()+":"+p.getPort()+" sequence number: "+Integer.toString(numeroSecuencia)+" message:"+msjEnviar); //Se muestra un mensaje en la consola que indica la dirección y puerto del remitente del datagrama, así como el contenido del mensaje.
                    numeroDeSecuenciaEsperado=1;
                    s.send(p1); //Se llama al método send() del objeto DatagramSocket s para enviar una respuesta al remitente del datagrama. Como se está reutilizando el objeto DatagramPacket p, éste ya tiene la dirección y puerto del remitente almacenados y sólo es necesario enviar el mismo datagrama de vuelta.
                  }
                  else
                  {
                    System.out.println("waiting datagram with sequence number "+numeroDeSecuenciaEsperado); 
                    System.out.println("Received from: "+p.getAddress()+":"+p.getPort()+" sequence number: "+Integer.toString(numeroSecuencia)+" message:"+msjEnviar); //Se muestra un mensaje en la consola que indica la dirección y puerto del remitente del datagrama, así como el contenido del mensaje.
                    System.out.println("Datagrams received in order");
                  }
              }
              
              
          }//for
          
      }
      catch(Exception e)
      {
          e.printStackTrace();
      }//catch
        
    }//main
}