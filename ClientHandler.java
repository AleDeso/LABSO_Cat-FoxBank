import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    Socket s;
    /* la risorsa in questo caso è necessariamente condivisa tra tutti i thread */
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

            //boolean closed = false;
            while (!Thread.interrupted()) {
                String request = from.nextLine(); // LEGGE IL MESSAGGIO DEL SENDER (quindi il terminale del Client)**********************
                String[] parts = request.split(" ", 4);
                String p = parts[0].toLowerCase();
                if (!Thread.interrupted()) {
                    System.out.println("Request: " + request);
                    try {
                        switch (p) {
                            case "quit":
                                //closed = true;
                                to.println("quit");
                                break;

                            case "open":
                                if (parts.length > 2) {
                                    String name = parts[1];
                                    double money = Double.parseDouble(parts[2]);
                                    Account newAccount = new Account(name, money);
                                    a.add(newAccount.getName(),newAccount);
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

                                    String mess = transfer(M, aSender, aReceiver);

                                    to.println(mess);
                                    
                                }else{
                                     to.println("error not specify details");
                                }
                                break;

                            case "transfer_i":
                                
                                break;
                            default:
                                to.println("Unknown cmd");
                        }
                    } catch(IllegalArgumentException e){
                        /*
                         * mando il messaggio che la chiave (quindi l'account che si vuole inserire)
                         * esiste già, ovvero un'altro account ha quel nome id.
                         */
                        to.println(e);

                    }catch (InterruptedException e) {
                        /*
                         * se riceviamo un Thread.interrupt() mentre siamo in attesa di add() o
                         * extract(), interrompiamo il ciclo come richiesto, e passiamo alla chiusura
                         * del socket
                         */
                        to.println("quit");
                        break;
                    }
                } else {
                    break;
                }
            }

            //to.println("quit");

            s.close();
            System.out.println("one Client is Closed");
        } catch (IOException e) {
            System.err.println("ClientHandler: IOException caught: " + e);
            e.printStackTrace();
        }
    }

    public synchronized String transfer(double M,String aSender,String aReceiver){
        String message = "";
        try {
            // Decremento del conto mittente
            Account S = a.extract(aSender);
            while(S.isLocked()){
                wait();
            }
            S.lock();
            S.OutFlow(M);
            S.setTransation(-M, aSender);
   /////////////////a.add(negativeT.getTKey(), negativeT);//Memorizzo in una HasMap
            S.unlock();

            // Incremento del conto ricevente
            Account R = a.extract(aReceiver);
            while(R.isLocked()){
                wait();
            }
            R.lock();
            R.InFlow(M);
            R.setTransation(M, aReceiver);
  ////////////////////a.add(positiveT.getTKey(), positiveT);//Memorizzo in una HasMap
            R.unlock();

            message = "successful transation";

        }catch (Exception e) {
                    
             message = "transaction interrupt!";
        }
        return message;
    }

}
