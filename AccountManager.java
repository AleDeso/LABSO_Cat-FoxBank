import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class AccountManager {
    private HashMap<String, Account> serchAccount;
    private HashMap<String, Account> busyAccount;

    public AccountManager() {
        this.serchAccount = new HashMap<>(); // usa nome account come chiave e account come parametro
        this.busyAccount = new HashMap<>();
    }

 //????????????????????????????????????????????????????????????????????????????????????   
 /*****public HashMap<String, Account> getSerchAccount() {
        return serchAccount;
    }

    public void setSerchAccount(HashMap<String, Account> serchAccount) {
        this.serchAccount = serchAccount;
    }
*****/
//?????????????????????????????????????????????????????????????????????????????
    public synchronized void addAccount(String key, Account bankAccount) throws InterruptedException {
        if (this.serchAccount.get(key) == null) {
             // Non esiste un account con la stessa chiave, quindi possiamo aggiungerlo
            this.serchAccount.put(key,bankAccount);
        }else{
            // Esiste già un account con la stessa chiave
            throw new IllegalArgumentException("Account: " + key + " already exist."); // Decidere se passare direttamente il messaggio sul client con un to.print oppure fare cosi(con eccezione)
        }
        notifyAll();
    }

    /*
     * Estrazione di una valore dalla mappa; una volta ottenuto il valore, la chiave viene rimossa.
     */
    public synchronized Account extract(String key) throws InterruptedException {
        Account found = this.serchAccount.get(key);
        if(found == null) {
            // L'account con la chiave specificata non esiste
            throw new IllegalArgumentException("Account " + key + " not found");
        }
        return found;
    }

    public synchronized String extractAll() throws InterruptedException {
        StringBuilder listAccountBuilder = new StringBuilder(); // Crea un oggetto StringBuilder
        int c = 0;
        for (Map.Entry<String, Account> entry : serchAccount.entrySet()) {
            //String indice = entry.getKey();
            Account valueAccount = entry.getValue();
            c++;
            // Utilizza append() per aggiungere le informazioni dell'account al StringBuilder
            listAccountBuilder.append(c + ". ");
 ////           listAccountBuilder.append("Codice: ").append(indice).append("\t");
            listAccountBuilder.append(valueAccount).append("\n");
        }
        
        // Converti il contenuto di StringBuilder in una stringa
        return  listAccountBuilder.toString();
    }

/**************************************************************************************************************************************/
//                                          leggere e scrivere da database
    public synchronized static void readDataBase(AccountManager r_a){
        Scanner scan = null;
        try {
            scan = new Scanner(new File("DataBaseAccount.csv"));
            while (scan.hasNextLine()) {
                String read = scan.nextLine();
                String[] parts = read.split(";", 4);
                Account readAccount = new Account(parts[0],Double.parseDouble(parts[1]), parts[2],Double.parseDouble(parts[3]));
                r_a.addAccount(readAccount.getName(),readAccount);
            }
            System.out.println("DataBase loaded.");
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }catch (Exception e){
            System.out.println(e);
        } finally{
            scan.close();
        }
    }

    public synchronized void writeDataBase(){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new File("DataBaseAccount.csv"));
        StringBuilder line = new StringBuilder();
        for (Map.Entry<String, Account> entry : serchAccount.entrySet()) {
            Account valueAccount = entry.getValue();
            // Utilizza append() per aggiungere le informazioni dell'account al StringBuilder
            line.append(valueAccount.getName());
            line.append(";");
            line.append(valueAccount.getMoney());
            line.append(";");
            line.append(valueAccount.getTransfer());
            line.append("\n");
            
        }
        
        writer.print(line);
        System.out.println("DataBaseAccount update.");

        } catch (FileNotFoundException e) {
            System.out.println("QUINDI????");
        }finally{
            writer.close();
        }
    }
/**************************************************************************************************************************************/
//                                          IMPLEMENTAZIONE COMANDO TRANSFER
    public synchronized String transfer(double M, String a_S, String a_R, PrintWriter to) throws InterruptedException{
        String m = "";
        Account S = extract(a_S);
        Account R = extract(a_R);
        checkBusy(S, R,to);
            busyAccount.put(S.getName(),S);
            busyAccount.put(R.getName(),R);
        m = sectionMove(S,R,M);
            busyEnd(S,R);
        return m;
    }
    //                                  Transazioni tra gli account
    public synchronized String sectionMove(Account S, Account R, double M){
        String message = "";
        try {
            // Decremento del conto mittente
            if(S.getMoney()>=M){
                S.OutFlow(M);
                S.setTransation(-M, S.getName());
            }else{
                throw new Exception("transaction interrupt! -- insufficient balance :(");
            }


            // Incremento del conto ricevente
            R.InFlow(M);
            R.setTransation(+M, R.getName());
            
            message = "successful transation";
            

        }catch (Exception e) {   
                    
             message = " insufficient balance";
             System.err.println(e);
        }
        return message;
    }
        
 //////////////////////////IMPLEMENTAZIONE COMANDO INTERATTIVE
 // no synchronized perhè voglio eseguire piu sessioni interattive se uso coppie di account differenti.
    public void interactive(String aSender_i, String aReceiver_i, PrintWriter to_i, Scanner from_i)throws InterruptedException{
            Account S = extract(aSender_i);
            Account R = extract(aReceiver_i);
            checkBusy(S, R, to_i);
            to_i.println("Start interattive transation: \nCommands:");
        while (true) {
            try{ 
                to_i.println(" 1.:move <money>  2.:end");
                String request_i = from_i.nextLine();
                String[] parts_i = request_i.split(" ", 2);
                String p = parts_i[0].toLowerCase().trim();
                switch (p) {
                    case ":move":
                        if (parts_i.length > 1) {
                                double money = Double.parseDouble(parts_i[1]);
                                String m = sectionMove(S, R,money);
                                to_i.println(m);
                            } else {
                                to_i.println("error not specify details");
                            }
                        break;
                    case ":end":
                        to_i.println("Finish interattive transation.");
                        busyEnd(S,R);
                        return; //per uscire dal metodo
                    case "quit":
                        writeDataBase();
                        busyEnd(S,R);
                        to_i.println("quit");
                        return;
                    default:
                        to_i.println("unknown cmd.. try again");
                }
            }catch(NoSuchElementException e){
                //e.printStackTrace();
                System.err.println("esco da un client");
            }  
        }
    }
    public synchronized void checkBusy(Account cSender,Account cReceiver, PrintWriter to_i) throws InterruptedException{
        while(busyAccount.get(cSender.getName()) != null || busyAccount.get(cReceiver.getName()) != null)
            {
                to_i.println("One Account is Busy.. waiting..");
                wait();
            }
            busyAccount.put(cSender.getName(),cSender);
            busyAccount.put(cReceiver.getName(),cReceiver);
            notifyAll();
    }
    public synchronized void busyEnd(Account eSender,Account eReceiver){
        busyAccount.remove(eSender.getName());
        busyAccount.remove(eReceiver.getName());
        notifyAll();
    }
}
