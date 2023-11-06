import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Client <host> <port>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            Socket s = new Socket(host, port);
            System.out.println("Connected to server");

            System.out.println("Usage:\t 1.Open <name> <money> \n\t 2.list \n\t 3.transfer <cash> <SenderAccount> <ReciverAccount>" +
            "\n\t 4.transfer_i <SenderAccount> <ReciverAccount> \n\t 5.quit" );
// SCRIVERE MENU DEL CLIENT 
// OPEN - LIST - TRANSFER - ecc...

            /*
             * Delega la gestione di input/output a due thread separati, 
             * uno per inviare messaggi e uno per leggerli
             * 
             */
            Thread sender = new Thread(new Sender(s));
            Thread receiver = new Thread(new Receiver(s, sender));

            sender.start();
            receiver.start();

            try {
                /* rimane in attesa che sender e receiver terminino la loro esecuzione */
                sender.join();
                receiver.join();
                s.close();
                System.out.println("Socket closed.");
            } catch (InterruptedException e) { 
                System.out.println("Client interrupted");
                /*
                 * se qualcuno interrompe questo thread nel frattempo, terminiamo
                 */
                return;
            }

        } catch (IOException e) {
            System.err.println("Connection refused");
            //e.printStackTrace();
        }
    }
}
