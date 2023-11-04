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
                request = request.toLowerCase();
                String[] parts = request.split(" ", 4);
                String p = parts[0].trim();
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
                                if (parts.length > 2) {
                                    String aSender = parts[1];
                                    String aReceiver = parts[2];
                                    to.println("Start interattive transation: \n" + "\t Commands: 1.move <money> \t 2.end"); 
                                    interattive(aSender,aReceiver);
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
//////////////////////////IMPLEMENTAZIONE COMANDO TRANSFER
    public String transfer(double M,String aSender,String aReceiver){
        String message = "";
        try {
            // Decremento del conto mittente
            Account S = a.extract(aSender);
            while(S.isLocked()){
                S.wait();
            }
            S.lock();

            if(S.getMoney()>M){
                S.OutFlow(M);
                S.setTransation(-M, aSender);
            }else{
                throw new Exception("transaction interrupt! -- insufficient balance :(");
            }

/////////////////a.add(negativeT.getTKey(), negativeT);//Memorizzo in una HasMap
            

            // Incremento del conto ricevente
            Account R = a.extract(aReceiver);
            while(R.isLocked()){
                R.wait();
            }

            R.lock();
            R.InFlow(M);
            R.setTransation(+M, aReceiver);
////////////////////a.add(positiveT.getTKey(), positiveT);//Memorizzo in una HasMap
            
            message = "successful transation";
            S.unlock();
            R.unlock();

        }catch (Exception e) {   
                    
             message = " insufficient balance"; //ON DEL TUTTO CORRETTO PERCHE CATTURA ANCHE ALTRE ECCEZIONI
             System.err.println(e);
        }
        return message;
    }

        
 //////////////////////////IMPLEMENTAZIONE COMANDO INTERATTIVE
        public void interattive(String aSender_i, String aReceiver_i){
            try {
                Account A1 = a.extract(aSender_i);
                while(A1.isLocked()){
                    A1.wait();
                }
                A1.lock();
                Account A2 = a.extract(aReceiver_i);
                while(A2.isLocked()){
                    A2.wait();
                }
                A2.lock();
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
                                A1.unlock();
                                A2.unlock();
                                return; //per uscire dal metodo
                        
                            default:
                                to_i.println("unknown cmd.. try again");
                        }
                    }catch(IOException e){
                        e.printStackTrace();
                    }  
                }
            } catch (Exception e) {
                System.out.println("interattive try per il lock"+e);
            }
        }
}
