import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/*
 * Resource implementa una semplice HashMap con associazioni String->String. Ogni chiave ha una sola stringa associata.
 * 
 * La risorsa è accessibile da diversi thread, che possono inserire elementi o estrarli dalla mappa.
 * Se viene richiesto di inserire una chiave già esistente, il thread viene messo in attesa;
 * se viene richiesto di estrarre una chiave non esistente, il thread viene messo in attesa.
 * 
 */
public class AccountManager {
    private HashMap<String, Account> serchAccount;

    public AccountManager() {
        this.serchAccount = new HashMap<>(); // usa nome account come chiave e account come parametro
    }

    public synchronized void add(String key, Account bankAccount) throws InterruptedException {
        if (this.serchAccount.get(key) == null) {
             // Non esiste un account con la stessa chiave, quindi possiamo aggiungerlo
            this.serchAccount.put(key,bankAccount );
        }else{
            // Esiste già un account con la stessa chiave
            throw new IllegalArgumentException("Account: " + key + " already exist.");
        }
        notifyAll();
    }

    public synchronized Account extract(String key) throws InterruptedException {
        Account found = this.serchAccount.get(key);
        if(found == null) {
            // L'account con la chiave specificata non esiste
            throw new IllegalArgumentException("Account " + key + " not found");
        }
        notifyAll();
        return found;
    }

    public static void readDataBase(AccountManager r_a){
        try (Scanner scan = new Scanner(new File("DataBaseAccount.csv"));) {
            while (scan.hasNextLine()) {
                String read = scan.nextLine();
                String[] parts = read.split(",", 2);
                Account readAccount = new Account(parts[0],Double.parseDouble(parts[1]));
                r_a.add(readAccount.getName(),readAccount);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public synchronized void writeDataBase(){
        try (PrintWriter writer = new PrintWriter(new File("DataBaseAccount.csv"))) {
        StringBuilder line = new StringBuilder();
        for (Map.Entry<String, Account> entry : serchAccount.entrySet()) {
            Account valueAccount = entry.getValue();
            // Utilizza append() per aggiungere le informazioni dell'account al StringBuilder
            line.append(valueAccount.getName());
            line.append(",");
            line.append(valueAccount.getMoney());
        }
        writer.close();
        System.out.println("DataBaseAccount update.");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }




    /*public synchronized String extractAll() throws InterruptedException {
        String listAccount = "";
        for (Map.Entry<String, Account> entry : serchAccount.entrySet()) {
            String indice = entry.getKey();
            Account valueAccount = entry.getValue();

            listAccount = listAccount + "Codice: " + indice + " " +  valueAccount + "\n";
        }
        notifyAll();
        return listAccount; 
    }*/
    
    public synchronized String extractAll() throws InterruptedException {
        // Crea un oggetto StringBuilder
        StringBuilder listAccountBuilder = new StringBuilder(); 
        int c = 0;
        for (Map.Entry<String, Account> entry : serchAccount.entrySet()) {
            Account valueAccount = entry.getValue();
            c++;
            // Utilizza append() per aggiungere le informazioni dell'account al StringBuilder
            listAccountBuilder.append(c + ". ");
            listAccountBuilder.append(valueAccount).append("\n");
        }
    
        notifyAll();
        
        // Converti il contenuto di StringBuilder in una stringa
        String listAccount = listAccountBuilder.toString();
        
        return listAccount;
    }
    
}
