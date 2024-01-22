package servidorbuscaminas;

import usuario.Usuario;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class ServidorBuscaminas {
    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(1234);
            System.out.println("Esperando cliente...");
            //El servidor espera la solicitud de conexión de un cliente
            for (;;) {
                Socket cl = s.accept();
                System.out.println("Conexión establecida desde: "
                        + cl.getInetAddress() + ":" + cl.getPort());
                //Se define un flujo de entrada ligado al socket
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                //se reciben los datos para generar el juego
                int nivel = dis.readInt();

                ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
                int matriz[][];
                Random rand = new Random(System.currentTimeMillis());
                switch (nivel) {
                    case 1:
                        matriz = new int[9][9];
                        for (int i = 0; i < matriz.length; i++) {
                            for (int j = 0; j < matriz[0].length; j++) {
                                matriz[i][j] = 0;
                            }
                        }
                        for (int i = 0; i <= 10; i++) {
                            int y = rand.nextInt(matriz.length);
                            int x = rand.nextInt(matriz[0].length);
                            System.out.println("Posicion Mina: " + y + "," + x);
                            matriz[y][x] = 1;
                            rand.setSeed(System.currentTimeMillis());
                            Thread.sleep(5L);
                        }
                        oos.writeObject(matriz);
                        oos.flush();
                        break;
                    case 2:
                        matriz = new int[16][16];
                        for (int i = 0; i < matriz.length; i++) {
                            for (int j = 0; j < matriz[0].length; j++) {
                                matriz[i][j] = 0;
                            }
                        }
                        
                        for (int i = 0; i <= 40; i++) {
                            rand.nextInt();
                            int y = rand.nextInt(matriz.length);
                            int x = rand.nextInt(matriz[0].length);
                            System.out.println("Posicion Mina: " + y + "," + x);
                            matriz[y][x] = 1;
                            Thread.sleep(17L);
                            rand.setSeed(System.currentTimeMillis());
                        }
                        oos.writeObject(matriz);
                        oos.flush();
                        break;
                    case 3:
                        matriz = new int[16][30];
                        for (int i = 0; i < matriz.length; i++) {
                            for (int j = 0; j < matriz[0].length; j++) {
                                matriz[i][j] = 0;
                            }
                        }
                        for (int i = 0; i <= 99; i++) {
                            rand.nextInt();
                            int y = rand.nextInt(matriz.length);
                            int x = rand.nextInt(matriz[0].length);
                            System.out.println("Posicion Mina: " + y + "," + x);
                            matriz[y][x] = 1;
                            Thread.sleep(22L);
                            rand.setSeed(System.currentTimeMillis());
                        }
                        oos.writeObject(matriz);
                        oos.flush();
                        break;
                }
                                
                //Leer puntuación
                int puntuacion = dis.readInt();
                System.out.println("Puntos: " + puntuacion);              
                oos.close();
                dis.close();
                cl.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public static Usuario searchUsuario(String u) throws IOException, ClassNotFoundException{
        ObjectInputStream ois = null;
        try{
            ois = new ObjectInputStream(new FileInputStream("Records.dat"));             
            while(true){
                Usuario usuario = (Usuario)ois.readObject();                
                if(usuario.getNombre().equals(u))
                    return usuario;                
            }
        }catch(EOFException e){
            return null;
        }finally{
            ois.close();
        }
    }
    
    public static ArrayList<Usuario> getUsuarios() throws IOException, ClassNotFoundException{
        ObjectInputStream ois = null;
        ArrayList <Usuario> usuarios = new ArrayList<>(); 
        try{
            ois = new ObjectInputStream(new FileInputStream("Records.dat"));
            while(true){
                Usuario usuario = (Usuario)ois.readObject();
                usuarios.add(usuario);
                //System.out.println(usuario.getNombre());
            }
        }catch(EOFException e){
            return usuarios;
        }finally{
            ois.close();
        }
    }    
    
    public static void actualizarUsuario(String u, int p) throws IOException, ClassNotFoundException{
        ArrayList<Usuario> usuarios = getUsuarios();      
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Records.dat"));
        for(int i = 0; i < usuarios.size(); i++){
            if(usuarios.get(i).getNombre().equals(u)){
                int partidas = usuarios.get(i).getPartidas() + 1;
                usuarios.get(i).setPartidas(partidas);
                if(usuarios.get(i).getPuntuacion() < p)
                    usuarios.get(i).setPuntuacion(p);
                oos.writeObject(usuarios.get(i));
            }
            else
                 oos.writeObject(usuarios.get(i));
        }              
        oos.close();
    }
    
}
