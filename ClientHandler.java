import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    Socket s;
    AccountManager a;

    public ClientHandler(Socket s, AccountManager a) {
        this.s = s;
        this.a = a;
    }

    @Override
    public void run() {
        try {
            Scanner from = new Scanner(s.getInputStream()); 
            //LEGGE QUELLO CHE MANDA "Sender" OVVERO LE ISTRUZIONI DEL CLIENT
            PrintWriter to = new PrintWriter(s.getOutputStream(), true);
            // MANDA MESSAGGI AL "Receiver" RESPONSE

            System.out.println("Thread " + Thread.currentThread() + " listening...");

            while (!Thread.interrupted()) {
                try{
                    String request = from.nextLine(); 
                    // LEGGE IL MESSAGGIO DEL SENDER (quindi il terminale del Client)
                    request=request.toLowerCase();
                    String[] parts = request.split(" ", 4);
                    //divido l'input da tastiera sulle celle di un array
                    String p = parts[0].trim();
                    if (!Thread.interrupted()) {
                        System.out.println("Request: " + request);
                        try {
                            switch (p) {
                                // controllo il primo elemento dell'array
                                case "quit":
                                    a.writeDataBase();
                                    to.println("quit");
                                    break;

                                case "open":
                                    if (parts.length > 2) {
                                        String name = parts[1];
                                        double money = Double.parseDouble(parts[2]);
                                        Account newAccount = new Account(name, money);
                                        a.addAccount(newAccount.getName(),newAccount);
                                        to.println("made account called: " + newAccount.getName());
                                    } else {
                                        to.println("error not specify details");
                                    }
                                    break;

                                case "list":
                                    if(parts.length >= 0)
                                        to.println(a.extractAll());
                                    else
                                        to.println("error not specify details");
                                    break;

                                case "transfer":
                                    if (parts.length > 3) {
                                        double M = Double.parseDouble(parts[1]);
                                        String aSender = parts[2];
                                        String aReceiver = parts[3];
                                        
                                        String mess = a.transfer(M, aSender, aReceiver, to);
                                        to.println(mess);
                                        
                                    }else{
                                        to.println("error not specify details");
                                    }
                                    break;

                                case "transfer_i":
                                    if (parts.length > 2) {
                                        String aSender = parts[1];
                                        String aReceiver = parts[2]; 
                                        
                                        a.interactive(aSender,aReceiver,to,from);
                                    } else {
                                        to.println("error not specify details");
                                    }
                                    break;
                                
                                default:
                                to.println("Unknown service");
                            }
                        } catch(IllegalArgumentException e){
                            
                            to.println(e); 
                            //Cattura l'eccezione che gli lancio dal metodo add di AccountMAnager. 
                                //"Account 'nome' already exist"

                        }catch (InterruptedException e) {
                            
                            /*
                                * se riceviamo un Thread.interrupt() mentre siamo in attesa di add() o
                                * extract(), interrompiamo il ciclo come richiesto, e passiamo alla chiusura
                                * del socket
                                */
                            to.println("Unexpected Interruption");
                            System.out.println("Unexpected Interruption");
                            to.println("quit");
                            break;
                        }
                    } else {
                            break;
                    }
                }catch(NoSuchElementException e){
                    System.out.println("Client terminated");
                    break;
                }
            }
            s.close();
            System.out.println("Client Close");
        } catch (IOException e) {
            System.err.println("ClientHandler: IOException caught: " + e);
        }
    }
    
}
