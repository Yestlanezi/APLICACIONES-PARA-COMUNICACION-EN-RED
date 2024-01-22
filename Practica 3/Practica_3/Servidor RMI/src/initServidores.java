

public class initServidores extends Thread{

    ServidorMulticast ServidorMulticast = new ServidorMulticast();
    ServidorRMI ServidorRMI = new ServidorRMI();
    ServidorUnicast ServidorUnicast = new ServidorUnicast();
    
    public initServidores() {
        System.out.println( "Servidor Principal Iniciando...");
        System.out.print( "Servidor Multicast Iniciando... ");
        System.out.print( "Servidor RMI Iniciando... ");
        System.out.println( "Servidor Unicast Iniciando... ");
        ServidorMulticast.start();
        ServidorRMI.start();
        ServidorUnicast.start();
    }
    
    public static void main(String[] args) {
        try{
	    initServidores servidores = new initServidores();
	    servidores.start();
	}catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
}
