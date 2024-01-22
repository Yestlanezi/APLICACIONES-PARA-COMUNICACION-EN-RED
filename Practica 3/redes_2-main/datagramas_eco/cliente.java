import java.io.*;
import java.net.*;
import java.util.Arrays;

public class PruebaC //Se define la clase pública CecoD.
{
    public static void main(String[] args) //Se define el método main, que es el punto de entrada del programa.
    {
      try
      {  
          int pto=1234; //Se establece el número de puerto pto donde se encuentra el servidor.
          String dir="127.0.0.1"; //Se establece la dirección IP del servidor como "127.0.0.1" (localhost).
          InetAddress dst= InetAddress.getByName(dir); //direccion IP
          
          int tam = 10; //Se define el tamaño máximo de cada datagrama tam como 10 bytes.
          BufferedReader br= new BufferedReader(new InputStreamReader(System.in)); //Se crea un objeto BufferedReader br para recibir la entrada del usuario desde la consola.
          DatagramSocket cl = new DatagramSocket(); //Se crea un objeto DatagramSocket cl para enviar y recibir datagramas.


          while(true) //Se inicia un bucle while(true) donde el cliente espera por la entrada del usuario y envía y recibe datagramas según corresponda.
          {
              System.out.println("Escribe un mensaje, <Enter> para enviar, \"salir\" para terminar");
              String msj = br.readLine();

              if(msj.compareToIgnoreCase("salir")==0) //Si el usuario escribe "salir", el programa termina cerrando el BufferedReader y el DatagramSocket.
              {

                  System.out.println("termina programa");
                  br.close();
                  cl.close();
                  System.exit(0);

              }
              else
              {

                  byte[]b = msj.getBytes();

                  if(b.length>tam) //Si el mensaje es más grande que tam bytes, se divide en varios fragmentos más pequeños.
                  {
                      byte[]b_eco = new byte[b.length]; //se crea un arreglo de bytes b_eco del mismo tamaño que el mensaje original. 
                      System.out.println("b_eco: "+b_eco.length+" bytes");
                      
                      int tp = (int)(b.length/tam); //se calcula el número de fragmentos tp que serán necesarios para enviar todo el mensaje

                      for(int j=0;j<tp;j++) //se inicia un bucle for(int j=0;j<tp;j++) que enviará cada fragmento por separado.
                      {
                        if(j==tp-1)
                        {
                          if(b.length%tam>0)
                          {
                            byte []tmp=Arrays.copyOfRange(b, j*tam, ((j*tam)+(tam))); //se corta el arreglo de bytes b en un fragmento más pequeño utilizando la función copyOfRange() de Arrays.
                            System.out.println("Fragmento de tamano "+tmp.length);

                            //modificacion
                            String numeroSecuencia = Integer.toString(j+1)+" ";
                            byte[]numeroSecuenciaByte = numeroSecuencia.getBytes();

                            //unimos el numro de secuencia con el mensaje 
                            byte[] combined = new byte[numeroSecuenciaByte.length + tmp.length];

                            System.arraycopy(numeroSecuenciaByte, 0, combined, 0, numeroSecuenciaByte.length);
                            System.arraycopy(tmp, 0, combined, numeroSecuenciaByte.length, tmp.length);
                            //unimos el numro de secuencia con el mensaje 
                            //modificacion

                            DatagramPacket p= new DatagramPacket(combined,combined.length,dst,pto); //Se crea un objeto DatagramPacket p con el fragmento recién creado.
                            cl.send(p); //se envía al servidor con el método send() del objeto DatagramSocket cl.

                            System.out.println("Enviando fragmento "+(j+1)+" de "+tp+"\ndesde:"+(j*tam)+" hasta "+((j*tam)+(tam))); //Se muestra en la consola información acerca del fragmento que se está enviando, indicando el número de fragmento actual y el número total de fragmentos.
                              
                            DatagramPacket p1= new DatagramPacket(new byte[tam],tam); //Se espera a recibir un eco del servidor con el método receive() del objeto DatagramSocket cl. Se crea un nuevo DatagramPacket p1 para almacenar el datagrama entrante.
                            cl.receive(p1);

                            byte[]bp1 = p1.getData(); //Se copian los datos recibidos en el DatagramPacket p1 en un nuevo arreglo de bytes bp1.

                            for(int i=0; i<tam;i++) //Los datos recibidos bp1 se copian en el arreglo de bytes b_eco en la posición correspondiente según el fragmento actual que se esté enviando.
                            {
                                System.out.println((j*tam)+i+"->"+i);
                                b_eco[(j*tam)+i]=bp1[i];
                            }//for
                          }
                          else
                          {
                            byte []tmp=Arrays.copyOfRange(b, j*tam, ((j*tam)+(tam))); //se corta el arreglo de bytes b en un fragmento más pequeño utilizando la función copyOfRange() de Arrays.
                            System.out.println("Fragmento de tamano "+tmp.length);

                            //modificacion
                            String numeroSecuencia = "-1 ";
                            byte[]numeroSecuenciaByte = numeroSecuencia.getBytes();

                            //unimos el numro de secuencia con el mensaje 
                            byte[] combined = new byte[numeroSecuenciaByte.length + tmp.length];

                            System.arraycopy(numeroSecuenciaByte, 0, combined, 0, numeroSecuenciaByte.length);
                            System.arraycopy(tmp, 0, combined, numeroSecuenciaByte.length, tmp.length);
                            //unimos el numro de secuencia con el mensaje 
                            //modificacion

                            DatagramPacket p= new DatagramPacket(combined,combined.length,dst,pto); //Se crea un objeto DatagramPacket p con el fragmento recién creado.
                            cl.send(p); //se envía al servidor con el método send() del objeto DatagramSocket cl.

                            System.out.println("Enviando fragmento "+(j+1)+" de "+tp+"\ndesde:"+(j*tam)+" hasta "+((j*tam)+(tam))); //Se muestra en la consola información acerca del fragmento que se está enviando, indicando el número de fragmento actual y el número total de fragmentos.
                              
                            DatagramPacket p1= new DatagramPacket(new byte[tam],tam); //Se espera a recibir un eco del servidor con el método receive() del objeto DatagramSocket cl. Se crea un nuevo DatagramPacket p1 para almacenar el datagrama entrante.
                            cl.receive(p1);

                            byte[]bp1 = p1.getData(); //Se copian los datos recibidos en el DatagramPacket p1 en un nuevo arreglo de bytes bp1.

                            for(int i=0; i<tam;i++) //Los datos recibidos bp1 se copian en el arreglo de bytes b_eco en la posición correspondiente según el fragmento actual que se esté enviando.
                            {
                                System.out.println((j*tam)+i+"->"+i);
                                b_eco[(j*tam)+i]=bp1[i];
                            }//for
                          }
                        // }
                        // else if(j==1)
                        // {
                        //   continue;
                        // }
                        // else
                        // {
                          byte []tmp=Arrays.copyOfRange(b, j*tam, ((j*tam)+(tam))); //se corta el arreglo de bytes b en un fragmento más pequeño utilizando la función copyOfRange() de Arrays.
                          System.out.println("Fragmento de tamano "+tmp.length);

                          //modificacion
                          String numeroSecuencia = Integer.toString(j+1)+" ";
                          byte[]numeroSecuenciaByte = numeroSecuencia.getBytes();

                          //unimos el numro de secuencia con el mensaje 
                          byte[] combined = new byte[numeroSecuenciaByte.length + tmp.length];

                          System.arraycopy(numeroSecuenciaByte, 0, combined, 0, numeroSecuenciaByte.length);
                          System.arraycopy(tmp, 0, combined, numeroSecuenciaByte.length, tmp.length);
                          //unimos el numro de secuencia con el mensaje 
                          //modificacion

                          DatagramPacket p= new DatagramPacket(combined,combined.length,dst,pto); //Se crea un objeto DatagramPacket p con el fragmento recién creado.
                          cl.send(p); //se envía al servidor con el método send() del objeto DatagramSocket cl.

                          System.out.println("Enviando fragmento "+(j+1)+" de "+tp+"\ndesde:"+(j*tam)+" hasta "+((j*tam)+(tam))); //Se muestra en la consola información acerca del fragmento que se está enviando, indicando el número de fragmento actual y el número total de fragmentos.
                            
                          DatagramPacket p1= new DatagramPacket(new byte[tam],tam); //Se espera a recibir un eco del servidor con el método receive() del objeto DatagramSocket cl. Se crea un nuevo DatagramPacket p1 para almacenar el datagrama entrante.
                          cl.receive(p1);

                          byte[]bp1 = p1.getData(); //Se copian los datos recibidos en el DatagramPacket p1 en un nuevo arreglo de bytes bp1.

                          for(int i=0; i<tam;i++) //Los datos recibidos bp1 se copian en el arreglo de bytes b_eco en la posición correspondiente según el fragmento actual que se esté enviando.
                          {
                              System.out.println((j*tam)+i+"->"+i);
                              b_eco[(j*tam)+i]=bp1[i];
                          }//for
                        }

                      }//for

                      if(b.length%tam>0) //Si hay bytes sobrantes después de haber dividido el mensaje original en fragmentos más pequeños, se envían estos bytes sobrantes en un último fragmento utilizando el mismo procedimiento descrito anteriormente.
                      {  

                          int sobrantes = b.length%tam;
                          System.out.println("sobrantes:"+sobrantes);
                          System.out.println("b:"+b.length+" ultimo pedazo desde "+tp*tam+" hasta "+((tp*tam)+sobrantes));

                          byte[] tmp = Arrays.copyOfRange(b, tp*tam, ((tp*tam)+sobrantes));
                          System.out.println("Fragmento de tamano "+tmp.length);

                          //modificacion
                          String numeroSecuencia = "-1 ";
                          byte[]numeroSecuenciaByte = numeroSecuencia.getBytes();

                          //unimos el numro de secuencia con el mensaje 
                          byte[] combined = new byte[numeroSecuenciaByte.length + tmp.length];

                          System.arraycopy(numeroSecuenciaByte, 0, combined, 0, numeroSecuenciaByte.length);
                          System.arraycopy(tmp, 0, combined, numeroSecuenciaByte.length, tmp.length);
                          //unimos el numro de secuencia con el mensaje 
                          //modificacion



                          DatagramPacket p = new DatagramPacket(combined,combined.length,dst,pto);
                          cl.send(p);

                          DatagramPacket p1= new DatagramPacket(new byte[tam],tam);
                          cl.receive(p1);
                          byte[]bp1 = p1.getData();

                          for(int i=0; i<sobrantes;i++)
                          {
                              System.out.println((tp*tam)+i+"->"+i);
                              b_eco[(tp*tam)+i]=bp1[i];
                          }//for

                      }//if
                      
                      //Finalmente, se muestra en la consola el eco completo del mensaje enviado.
                      String eco = new String(b_eco);
                      System.out.println("Eco recibido: "+eco);

                  }
                  else
                  {
                      //modificacion
                      String numeroSecuencia = "0 ";
                      byte[]numeroSecuenciaByte = numeroSecuencia.getBytes();


                      //unimos el numro de secuencia con el mensaje 
                      byte[] combined = new byte[numeroSecuenciaByte.length + b.length];

                      System.arraycopy(numeroSecuenciaByte, 0, combined, 0, numeroSecuenciaByte.length);
                      System.arraycopy(b, 0, combined, numeroSecuenciaByte.length, b.length);
                      //unimos el numro de secuencia con el mensaje 

                      //modificacion

                      //Si el mensaje es más pequeño o igual que tam bytes, se envía el mensaje completo en un solo datagrama utilizando el método send() del objeto DatagramSocket cl.
                      DatagramPacket p=new DatagramPacket(combined,combined.length,dst,pto);
                      cl.send(p);
                      //Se espera a recibir un eco del servidor con el método receive() del objeto DatagramSocket cl. Se crea un nuevo DatagramPacket p1 para almacenar el datagrama entrante.
                      DatagramPacket p1 = new DatagramPacket(new byte[65535],65535);
                      cl.receive(p1);
                      //Se convierte los datos recibidos en el DatagramPacket p1 en una cadena de caracteres utilizando el constructor
                      String eco = new String(p1.getData(),0,p1.getLength());
                      System.out.println("Eco recibido: "+eco);

                  }//else

              }//else

          }//while

      }
      catch(Exception e)
      {
          e.printStackTrace();
      }//catch

    }//main
}
