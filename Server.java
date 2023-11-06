import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Server <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);  //.parseInt() trasforma da string a int
        Scanner scan = new Scanner(System.in);
        // VOLEVO PROVARE A MANDARE DA QUA UN QUIT PrintWriter to = new PrintWriter(s.getOutputStream(), true);
        try {
            ServerSocket server = new ServerSocket(port);
            /*
             * deleghiamo a un altro thread la gestione di tutte le connessioni; nel thread
             * principale ascoltiamo solo l'input da tastiera dell'utente (in caso voglia
             * chiudere il programma)
             */
            Thread serverThread = new Thread(new SocketListener(server)); // Collega il thread a socketListener in modo che si occupi della connessione
            serverThread.start();

            String command = "";

            while (!command.equalsIgnoreCase("quit")) {
                command = scan.nextLine();
            }

            try {
                serverThread.interrupt(); // va a uscire dal ciclo SocketListener
                /* attendi la terminazione del thread */
                serverThread.join();
                
            } catch (InterruptedException e) {
                /*
                 * se qualcuno interrompe questo thread nel frattempo, terminiamo
                 */
                return;
            }
            System.out.println("Main thread terminated.");
        } catch (IOException e) {
            System.err.println("(SERVER)IOException caught: " + e); // QUANDO SI VERIFICA QUESTA ECCEZIONE?
            e.printStackTrace();
        } finally {
            scan.close();
        }
    }
}
