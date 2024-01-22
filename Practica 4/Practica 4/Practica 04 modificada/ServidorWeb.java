import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorWeb {
	
    public static void main(String[] args) {
    	int pto, tamPool;

        try {
        	Scanner sc = new Scanner(System.in);
        	System.out.print("Puerto: ");
			pto = sc.nextInt();
			System.out.print("Indica el numero de conexiones (Grupo de subprocesos): ");
			tamPool = sc.nextInt();

        	// Pool de Conexiones
        	ExecutorService pool = Executors.newFixedThreadPool(tamPool);
	        System.out.println("\n\nIniciando Servidor.... ");
	        System.out.println("Pool de Conexiones = " + tamPool);

	        ServerSocket s = new ServerSocket(pto);
	        System.out.println("Servidor: http://localhost:" + pto + "/ --- OK");
	        System.out.println("Esperando a Cliente....");

	        for( ; ; ) { 
	            Socket cl = s.accept();
	            Manejador manejador = new Manejador(cl);
	            pool.execute(manejador);
	        }
        }
        catch(Exception e){
        	e.printStackTrace();
        }
    }
}