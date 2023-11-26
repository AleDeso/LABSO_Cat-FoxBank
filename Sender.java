import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Sender implements Runnable {

    Socket s;

    public Sender(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {
        Scanner scan = new Scanner(System.in);

        try {
            PrintWriter to = new PrintWriter(this.s.getOutputStream(), true);
            while (true) {
                
                String requestClient = scan.nextLine(); // LEGGE TERMINALE CLIENT   ***************************************
             
                
                 if (requestClient.equalsIgnoreCase("quit")) {
                    to.println("quit");
                    break;
                }else if (Thread.interrupted()) {
                    to.println("quit");
                    break;
                 /*
                 * se il thread è stato interrotto mentre leggevamo l'input da tastiera, inviamo
                 * "quit" al server e usciamo
                 */
                }else 
                {
                    to.println(requestClient); // MANDA LA RICHIESTA AL SERVER
                     /* in caso contrario proseguiamo e analizziamo l'input inserito */
                
                }
            }
            System.out.println("Sender closed.");
        } catch (IOException e) {
            System.err.println("IOException caught: " + e);
            e.printStackTrace();
        } finally {
            scan.close();
        }
    }

}
