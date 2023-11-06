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
            this.server.setSoTimeout(10000); // attende 10 sec prima di sollevare una SocketTimeoutException
            AccountManager r = new AccountManager(); 
            // Risorsa che controlla HashMap degli account

            while (!Thread.interrupted()) {
                try {
                                        System.out.println("Waiting for a new client...");
                    /*
                     * Questa istruzione è bloccante, a prescindere da Thread.interrupt(). Occorre
                     * quindi controllare, una volta accettata la connessione, che il server non sia
                     * stato interrotto.
                     * 
                     * In caso venga raggiunto il timeout, viene sollevata una
                     * SocketTimeoutException, dopo la quale potremo ricontrollare lo stato del
                     * Thread nella condizione del while().
                     */
                    Socket s = this.server.accept();
                    //PrintWriter to = new PrintWriter(s.getOutputStream(), true);
                    if (!Thread.interrupted()) {
                        System.out.println("Client connected");

                        /* crea un nuovo thread per lo specifico socket */
                        Thread handlerThread = new Thread(new ClientHandler(s, r)); // Thread che va ad ascoltare richieste del Client in CLIENTHANDLER
                        handlerThread.start();
                        this.worker.add(handlerThread);
                        /*
                         * una volta creato e avviato il thread, torna in ascolto per il prossimo client
                         */
                    } else {
                        
                        s.close(); // CHIUDO LA CONNESSIONE CON IL THREAD ASSOCIATO A UN CLIENT
                        break;
                    }
                } catch (SocketTimeoutException e) {
                    /* in caso di timeout procediamo semplicemente con l'esecuzione */
                        System.out.println("Timeout, continuing...");
                    continue;
                } catch (IOException e) {
                    /*
                     * s.close() potrebbe sollevare un'eccezione; in questo caso non vogliamo finire
                     * nel "catch" esterno, perché non abbiamo ancora chiamato this.server.close()
                     */
                    break;
                }
            }
            //r.writeDataBase();
            this.server.close();
        } catch (IOException e) {
            System.err.println("SocketListener: IOException caught: " + e);
            e.printStackTrace();
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