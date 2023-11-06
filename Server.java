import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class Server {
   
    public static void main(String[] args) {
         if (args.length < 1) {
            System.out.println("Usage: java Server <port>");
            return;
        }
             int port = Integer.parseInt(args[0]);
                  
        Scanner scan = new Scanner(System.in);
        
        try {
            ServerSocket server = new ServerSocket(port);
            /*
             * deleghiamo a un altro thread la gestione di tutte le connessioni; nel thread
             * principale ascoltiamo solo l'input da tastiera dell'utente (in caso voglia
             * chiudere il programma)
             */
            System.out.println("Apro la connessione");
            Thread serverThread = new Thread(new SocketListener(server)); // Collega il thread a socketListener in modo che si occupi della connessione
            serverThread.start();
            System.out.println("Funziona");

            String command = "";

            while (!command.equalsIgnoreCase("quit")) {
                command = scan.nextLine();
            }

            try {
                serverThread.interrupt(); // va a uscire dal ciclo SocketListener
                /* attendi la terminazione del thread */
                serverThread.join();
                
            } catch (InterruptedException e) {
                System.out.println("Server interrotto");
                /*
                 * se qualcuno interrompe questo thread nel frattempo, terminiamo
                 */
                return;
            }
            System.out.println("Main thread terminated.");
        } catch (IOException e) {
            System.err.println("(SERVER)IOException caught: " + e);//CHIEDI come e quando si verificano
            e.printStackTrace();
        } finally {
            scan.close();
        }
    }
}
