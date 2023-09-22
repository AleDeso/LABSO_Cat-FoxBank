# LABSO_Cat-FoxBank
 Progetto
2.1 Funzionalità
Il progetto richiede di implementare un semplice servizio per la gestione di
trasferimenti di denaro tra conti bancari.
Il servizio permette a pi`u client di connettersi a un singolo server, e di
spostare somme di denaro arbitrarie tra due conti a scelta; nello specifico, gli
utenti possono:
• Aprire nuovi conti
• Effettuare transazioni di denaro tra un conto e l’altro
• Aprire sessioni interattive in cui spostare denaro tra un conto e l’altro
• Richiedere la lista dei conti aperti, incluso il nome e l’ultima transazione
eseguita
Ogni conto ha un nome univoco, definito alla sua creazione e utilizzato
per indicarlo nelle transazioni.
Un conto pu`o essere parte solo di una transazione alla volta. Quando un
utente sta trasferendo denaro da un conto all’altro, nessuno pu`o effettuare un
trasferimento che coinvolga uno dei due conti. Una sessione interattiva viene
considerata come una transazione in atto, finch´e non viene esplicitamente
chiusa.
Nel caso in cui un utente provi ad effettuate una transazione su uno (o
due) conti gi`a occupati, la richiesta non termina con un errore, ma rimane
in attesa finch´e entrambi i conti coinvolti non si sono liberati.


2.2 Requisiti implementativi
2.2.1 Linguaggio
Il progetto deve essere implementato in Java (ultima versione LTS: Java SE
17). La comunicazione di rete `e implementata attraverso i socket. Il progetto
deve essere diviso in un’applicazione Client e un’applicazione Server, che
implementino le funzionalit`a descritte nella sezione 2.1.
2.2.2 Client
Il client viene avviato da linea di comando e richiede come parametri l’indirizzo
IP e la porta del server a cui connettersi, ad esempio:
                      java Client 127.0.0.1 900
Se il server non `e raggiungibile all’indirizzo e alla porta specificati, il
comando restituisce un messaggio di errore. Se la connessione va a buon
fine, il client rimane in attesa delle istruzioni dell’utente.
I comandi richiesti per il client sono i seguenti:
• Il comando list restituisce un elenco di tutti i conti presenti. Ogni
voce dell’elenco deve contenere:
– Il nome del conto
– Il bilancio del conto
– I dati dell’ultima transazione, cio`e: data, ammontare di denaro, e
mittente (o destinatario) della transazione
• Il comando open crea un nuovo conto sul server. Il comando richiede
due argomenti: <Account> e <Amount>, rispettivamente il nome del
conto che sta venendo creato, e il bilancio iniziale del conto. Ad esempio:
open ExampleAccount 1000
Se un conto con lo stesso nome esiste gi`a, il comando restituisce un
messaggio di errore.
• Il comando transfer effettua una transazione di denaro da un conto
all’altro. Il comando richiede tre argomenti: <Amount>, <Account1>
e <Account2>, rispettivamente l’ammontare di denaro da trasferire, il
conto mittente e il conto destinatario. Ad esempio:
transfer 500 ExampleAccount1 ExampleAccount2
Per semplicit`a, si pu`o imporre che Amount sia sempre positivo. Se la
transazione `e effettuabile (ovvero se entrambi i conti esistono e se il
mittente contiene denaro a sufficienza) allora l’ammontare specificato
viene prelevato da Account1 e depositato in Account2; in caso contrario,
il comando restituisce un error
• Il comando transfer i `e un modo alternativo per trasferire denaro
tra due conti. Il comando richiede due argomenti: <Account1> e
<Account2>, rispettivamente il conto mittente e destinatario della transazione.
Se entrambi i conti esistono, il comando avvia una sessione di transazione
tra i due conti; quando il client si trova in questa sessione, sono disponibili i seguenti comandi:
– Il comando :move trasferisce denaro da Account1 ad Account2;
richiede un argomento, <Amount>, che rappresenta la quantit`a di
denaro spostata. Si pu`o di nuovo assumere che Amount sia positivo. Se il bilancio di Account1 non `e sufficiente a sostenere la
transazione, il comando restituisce un errore, ma la sessione non
viene chiusa.
– Il comando :end termina la sessione di transazione interattiva,
riportando il client in modalit`a normale e liberando i due conti.
Quando il client `e in sessione di transazione, i comandi standard sono
disabilitati fino al suo termine.
Un esempio di utilizzo di questo comando e della sessione di transazione:
transfer_i ExampleAccount1 ExampleAccount2
> :move 500
...
> :end
• Il comando quit arresta il client.


2.2.3 Server
Il server viene avviato da linea di comando, e accetta come unico parametro
la porta su cui restare in ascolto, ad esempio:
                                java Server 9000
Il server accetta solo il seguente comando:
• quit, disconnette tutti i client ancora connessi e successivamente termina il server.

2.2.4 Requisito opzionale (1 Punto)
Implementare il comando close <Account>, che rimuove un conto corrente
dal server, o restituisce un errore se il conto `e inesistente. L’implementazione
di questo comando non `e necessaria per ottenere il voto massimo (30L).
