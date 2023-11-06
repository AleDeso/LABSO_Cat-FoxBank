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
        this.setSerchAccount(new HashMap<>()); // usa nome account come chiave e account come parametro
    }

    /*
     * Inserimento di un elemento nella mappa.
     * 
     * Se la chiave esiste già, rimaniamo in attesa, rilasciando il lock
     * sull'oggetto.
     * 
     * Una volta sbloccati e inserito il nuovo elemento, svegliamo eventuali thread
     * in attesa.
     */

    public HashMap<String, Account> getSerchAccount() {
        return serchAccount;
    }

    public void setSerchAccount(HashMap<String, Account> serchAccount) {
        this.serchAccount = serchAccount;
    }

    public synchronized void add(String key, Account bankAccount) throws InterruptedException {
        if (this.getSerchAccount().containsKey(key) == false) {
             // Non esiste un account con la stessa chiave, quindi possiamo aggiungerlo
            this.getSerchAccount().put(key,bankAccount );
            System.out.println("Account inserito con successo");
        }else{
            // Esiste già un account con la stessa chiave
            throw new IllegalArgumentException("Account: " + key + " gia esistente.");
        }
        notifyAll();
    }

    /*
     * Estrazione di una valore dalla mappa; una volta ottenuto il valore, la chiave
     * viene rimossa.
     * 
     * Duale di add(), rimaniamo in attesa finché la chiave non ha un valore
     * associato.
     */
    public synchronized Account extract(String key) throws InterruptedException {
        Account found = this.getSerchAccount().get(key);
        if(found == null) {
            // L'account con la chiave specificata non esiste
            throw new IllegalArgumentException("Account " + key + " non trovato");
        }
        notifyAll();
        return found;
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
        StringBuilder listAccountBuilder = new StringBuilder(); // Crea un oggetto StringBuilder
    
        int c = 0;
        for (Map.Entry<String, Account> entry : getSerchAccount().entrySet()) {
            //String indice = entry.getKey();
            Account valueAccount = entry.getValue();
            c++;
            // Utilizza append() per aggiungere le informazioni dell'account al StringBuilder
            listAccountBuilder.append(c + ") ");
 ////           listAccountBuilder.append("Codice: ").append(indice).append("\t");
            listAccountBuilder.append(valueAccount).append("\n");
        }
    
        notifyAll();
        
        // Converti il contenuto di StringBuilder in una stringa
        String listAccount = listAccountBuilder.toString();
        
        return listAccount;
    }
    
///////////////leggere e scrivere da database
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
        } finally{
            //scan.close();
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
            line.append(",");
            line.append(valueAccount.getTransation());
            line.append("\n");
            
        }
        
        writer.print(line);
        writer.close();
        System.out.println("DataBaseAccount update.");

        } catch (FileNotFoundException e) {
            System.out.println("QUINDI????");
        }finally{
            //writer.close();
        }
    }
}
