import java.util.List;



public class ClienteMulticastCheckList extends Thread{

    private final database db;
    

    public ClienteMulticastCheckList(database db){
        this.db = db;
        System.out.print( "Cliente Multicast CheckList Creado. ");
    }
    
    public void run(){
        System.out.print("Cliente Multicast CheckList Iniciado");
        while(true){
            try{
                List<serverData> ServersList = db.getServersList();
                if(ServersList.size() != 0){
                    for(int i=0 ; i < ServersList.size() ; i++){
                        if(ServersList.get(i).getTemp() == 0){
                            //Cuando llega a 0 el servidor no se reporto, asi que lo eliminamos
                            ServersList.remove(i);
                            System.out.println( "Servidor inactivo, se eliminó de la lista");
                        }else{
                            ServersList.get(i).setTemp(ServersList.get(i).getTemp()-1);
                        }
                        
                    }
                }
                Thread.sleep(1000);
            } 
            catch(InterruptedException ex) 
            {
                Thread.currentThread().interrupt();
            }
        }
    }
   
    
}
