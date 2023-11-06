import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
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

            AccountManager.readDataBase(a); //Leggo gli account memorizzati sul file csv

            while (!Thread.interrupted()) {
                try{
                    String request = from.nextLine(); // LEGGE IL MESSAGGIO DEL SENDER (quindi il terminale del Client)**********************
                    request = request.toLowerCase();
                    String[] parts = request.split(" ", 4);
                    String p = parts[0].trim();
                    if (!Thread.interrupted()) {
                        System.out.println("Request: " + request);
                        try {
                            switch (p) {
                                case "quit":
                                    //closed = true;
                                    a.writeDataBase();
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
                                        Account S = a.extract(aSender);
                                        Account R = a.extract(aReceiver);
                                        while(S.isLocked()){
                                                S.wait();
                                        }
                                        while(R.isLocked()){
                                                R.wait();
                                        }
                                        S.lock();
                                        R.lock();
                                        String mess = transfer(M, S, R);
                                        to.println(mess);
                                        S.unlock();
                                        R.unlock();
                                        
                                    }else{
                                        to.println("error not specify details");
                                    }
                                    break;

                                case "transfer_i":
                                    if (parts.length > 2) {
                                        String aSender = parts[1];
                                        String aReceiver = parts[2];
                                        to.println("Start interattive transation: \n" + "\t Commands: 1.move <money> \t 2.end"); 
                                        Account S = a.extract(aSender);
                                        Account R = a.extract(aReceiver);
                                        while(S.isLocked()){
                                                S.wait();
                                        }
                                        while(R.isLocked()){
                                                R.wait();
                                        }
                                        S.lock();
                                        R.lock();
                                        interattive(S,R);
                                        S.unlock();
                                        R.unlock();
                                    } else {
                                        to.println("error not specify details");
                                    }
                                    break;
                                default:
                                    to.println("Unknown cmd");
                            }
                        } catch(IllegalArgumentException e){
                            /*
                            * mando il messaggio che la chiave (quindi l'account che si vuole inserire)
                            * esiste già, ovvero un'altro account ha quel nome id.
                            */
                            to.println(e); //LASCIARE SOLO 'e' ! Perchè gli lancio l'eccezione dal metodo add di AccountMAnager. 
                            //"Account 'nome' already exist"

                        }catch (InterruptedException e) {
                            /*
                            * se riceviamo un Thread.interrupt() mentre siamo in attesa di add() o
                            * extract(), interrompiamo il ciclo come richiesto, e passiamo alla chiusura
                            * del socket
                            */
                            to.println("quit");
                            break;
                        }
                    }
                     else {
                        break;
                    }
                } catch (NoSuchElementException e) {
                    System.out.println(" Client terminated");
                    break;
                }
            }

            s.close();
            //System.out.println("one Client is Closed");
        } catch (IOException e) {
            System.err.println("ClientHandler: IOException caught: " + e);
            e.printStackTrace();
        }
    }
//////////////////////////IMPLEMENTAZIONE COMANDO TRANSFER
    public String transfer(double M, Account a_S, Account a_R){
        String message = "";
        try {
            // Decremento del conto mittente
            if(a_S.getMoney()>=M){
                a_S.OutFlow(M);
                a_S.setTransation(-M, a_S.getName());
            }else{
                throw new Exception("transaction interrupt! -- insufficient balance :(");
            }

/////////////////a.add(negativeT.getTKey(), negativeT);//Memorizzo in una HasMap

            // Incremento del conto ricevente
            a_R.lock();
            a_R.InFlow(M);
            a_R.setTransation(+M, a_R.getName());
////////////////////a.add(positiveT.getTKey(), positiveT);//Memorizzo in una HasMap
            
            message = "successful transation";

        }catch (Exception e) {   
                    
             message = " insufficient balance"; //ON DEL TUTTO CORRETTO PERCHE CATTURA ANCHE ALTRE ECCEZIONI
             System.err.println(e);
        }
        return message;
    }

        
 //////////////////////////IMPLEMENTAZIONE COMANDO INTERATTIVE
    public void interattive(Account aSender_i, Account aReceiver_i){
            
        while (true) {
            try{
                Scanner from_i = new Scanner(s.getInputStream());
                PrintWriter to_i = new PrintWriter(s.getOutputStream(), true); 
                String request_i = from_i.nextLine();
                String[] parts_i = request_i.split(" ", 2);
                String p = parts_i[0].toLowerCase().trim();
                switch (p) {
                    case "move":
                        if (parts_i.length > 1) {
                                double money = Double.parseDouble(parts_i[1]);
                                String m = transfer(money, aSender_i, aReceiver_i);
                                to_i.println(m);
                            } else {
                                to_i.println("error not specify details");
                            }
                        break;
                    case "end":
                        to_i.println("Finish interattive transation.");
                        return; //per uscire dal metodo
                
                    default:
                        to_i.println("unknown cmd.. try again");
                }
            }catch(IOException e){
                e.printStackTrace();
            }  
        }
    }
    
}
