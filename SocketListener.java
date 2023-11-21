import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class SocketListener implements Runnable {
    ServerSocket server;
    ArrayList<Thread> worker = new ArrayList<>();

    public SocketListener(ServerSocket server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            this.server.setSoTimeout(5000);
            AccountManager r = new AccountManager(); 
            // Risorsa che controlla HashMap degi account
            AccountManager.readDataBase(r);   
            //Leggo gli account memorizzati sul file
            while (!Thread.interrupted()) {
                try {
                     //System.out.println("Waiting for a new client...");
                    Socket s = this.server.accept();
                    if (!Thread.interrupted()) {
                        System.out.println("Client connected");

                        // crea un nuovo thread per lo specifico socket
                        Thread handlerThread = new Thread(new ClientHandler(s, r)); 
                        // Thread che va ad ascoltare richieste del Client
                        handlerThread.start();
                        this.worker.add(handlerThread);
                        // una volta creato e avviato il thread, torna in ascolto per il prossimo client
                    } else {
                        s.close();
                        break;
                    }
                } catch (SocketTimeoutException e) {
                    // in caso di timeout ricontrolliamo la condizione del while e proseguiamo con l'esecuzione
                    //System.out.println("Timeout, continuing...");
                    continue;
                } catch (IOException e) { //GESTISCIIIII
                    /*
                     * s.close() potrebbe sollevare un'eccezione; in questo caso non vogliamo finire
                     * nel "catch" esterno, perché non abbiamo ancora chiamato this.server.close()
                     */
                    break;
                }
            }
            this.server.close();
        } catch (IOException e) { 
            System.err.println("SocketListener: IOException caught: " + e);
        }

        System.out.println("Interrupting workers...");
        for (Thread w : this.worker) {
            System.out.println("Interrupting " + w + "...");
            /*
             * w.interrupt() non è bloccante; una volta inviato il segnale
             * di interruzione proseguiamo con l'esecuzione, senza aspettare che "w"
             * termini
             */
            w.interrupt();
        }

    }

}
