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
                /*
                 * se il thread è stato interrotto mentre leggevamo l'input da tastiera, inviamo
                 * "quit" al server e usciamo
                 */
                if (Thread.interrupted()) {
                    to.println("quit");
                    break;
                }
                /* in caso contrario proseguiamo e analizziamo l'input inserito */
                
                to.println(requestClient); // MANDA LA RICHIESTA AL SERVER 

                if (requestClient.equals("quit")) {
                    to.println("quitC");
                    break;
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
