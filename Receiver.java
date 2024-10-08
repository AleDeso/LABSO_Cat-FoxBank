import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Receiver implements Runnable {

    Socket s;
    Thread sender;

    public Receiver(Socket s, Thread sender) {
        this.s = s;
        this.sender = sender;
    }

    @Override
    public void run() {
        try {
            Scanner from = new Scanner(this.s.getInputStream()); 
            // GRAZIE A ".getInputStream" LEGGIAMO I DATI MANDATI CHE ARRIVANO DA "s" 
            // CHE E' UNA CONNESSIONE SOCKET; QUINDI DAL SERVER
            while (true) {
                String response = from.nextLine(); // LEGGIAMO QUELLO CHE CI MANDA IL SERVER 
                
                if (response.equalsIgnoreCase("quit")) {
                    break;
                }
                System.out.println("Received: " + response);
            }
        }catch (IOException e) {
            System.err.println("IOException caught: " + e);
        } catch(NoSuchElementException e){
            System.out.println("Server not available");
        }finally {
            this.sender.interrupt();
            System.out.println("Receiver closed.");
        }
    }
}
