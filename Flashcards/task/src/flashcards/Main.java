package flashcards;


import java.io.*;
import java.util.*;


public class Main {

    static String importPath;
    static String exportPath;
    static File file = null;
    static String card;
    static String definition;
    static int  mistakes;
    static Map<String, String> cardToDefinition = new HashMap<>();
    static Map<String, String> definitionToCard = new HashMap<>();
    static List<String> log = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);
    static Map<String ,Integer> hardest=new HashMap<>();

    public static void println(String s){
        System.out.println(s);
        log.add(s+"\n");
    }
    public static void print(String s){
        System.out.println(s);
        log.add(s);
    }

    public static String readln(){
        String tmp = sc.nextLine();
        log.add(tmp+"\n");
        return tmp;
    }

    public static void log(){
        println("File name:");
        file = new File(readln());
        try {
            file.createNewFile();

        } catch (IOException e) {
            println("Cannot create the file: " + file.getPath());
        }
        try {
            PrintWriter printWriter = new PrintWriter(file);
            for (String p : log) {
                printWriter.println(p);
            }

        } catch (FileNotFoundException e) {
            println("File cant be created.");
        }
        println("The log has been saved.");
    }

    public static void hardestCard(){
        boolean isEmpty = true;
        int tmp=0;
        List<String> cards= new ArrayList<>();
        for(String i: hardest.keySet()){
            if(hardest.get(i)>0){
                isEmpty=false;
                break;
            }
        }
        if(isEmpty){
            println("There are no cards with errors.");
        } else{
            for(String i: hardest.keySet()){
                if(hardest.get(i)>tmp){
                    cards.clear();
                    cards.add(i);
                    tmp=hardest.get(i);
                }else if(hardest.get(i)==tmp){
                    cards.add(i);
                }
            }
            if(cards.size()>1) {
                StringBuilder tmpString = new StringBuilder();
                tmpString.append("The hardest cards are ");
                for (int i = 0; i < cards.size() - 1; i++) {
                    tmpString.append("\""+cards.get(i) + "\", ");
                }
                tmpString.append("\""+cards.get(cards.size()-1) + "\". You have "+tmp+" errors answering them.");
                println(tmpString.toString());
            } else{
                println("The hardest card is \""+cards.get(0)+"\". You have "+tmp+" errors answering it.\n");
            }
        }


    }



    public static void add() {
        String card;
        String definition;
        println("The card:");
        card = readln();
        if (cardToDefinition.containsKey(card)) {
            println("The card \"" + card + "\" already exists.");
            return;
        }
        println("The definition of the card:");
        definition = readln();
        //Check definition duplicate
        if (definitionToCard.containsKey(definition)) {
            println("The definition \"" + definition + "\" already exists.");
            return;
        }
        //input to maps
        cardToDefinition.put(card, definition);
        definitionToCard.put(definition, card);
        hardest.put(card,0);
        println("The pair (\"" + card + "\":\"" + definition + "\") has been added.\n");

    }

    public static void remove(){
        println("The card:");
        String s = readln();
        if (cardToDefinition.containsKey(s)) {
            definitionToCard.remove(cardToDefinition.get(s));
            cardToDefinition.remove(s);
            hardest.remove(s);
            println("The card has been removed.");
            return;
        }
        println("Can't remove \"" + s + "\": there is no such card.");
    }
    public static void saveImportCards(){
        println("File name:");
        file = new File(readln());
        importCards();
    }
    public static void importStart(){
        file = new File(importPath);
        importCards();
    }


    public static void importCards(){

        int cardCounter = 0;
        try {
            Scanner scFile = new Scanner(file);

            while (scFile.hasNextLine()) {
                card = scFile.nextLine();
                definition = scFile.nextLine();
                mistakes=Integer.parseInt(scFile.nextLine());
                if (cardToDefinition.containsKey(card)) {
                    //println("The card \"" + card + "\" already exists.");
                    cardToDefinition.replace(card, definition);
                    hardest.replace(card,mistakes);
                    //sprawdz
                    cardCounter++;
                    continue;
                }


                cardToDefinition.put(card, definition);
                definitionToCard.put(definition, card);
                hardest.put(card,mistakes);
                cardCounter++;
            }
            println(cardCounter + " cards have been loaded.");

        } catch (FileNotFoundException e) {
            println("File not found.");
        }
    }

    public static void exportCards(){
        println("File name:");
        file = new File(readln());
        try {
            file.createNewFile();

        } catch (IOException e) {
            println("Cannot create the file: " + file.getPath());
        }
        saveExportCards();
    }
    public static void exportExit(){

        file = new File(exportPath);
        try {
            file.createNewFile();

        } catch (IOException e) {
            println("Cannot create the file: " + file.getPath());
        }
        saveExportCards();
    }
        public static void saveExportCards(){
            int cardCounter = 0;
        try {
            PrintWriter printWriter = new PrintWriter(file);
            for (String p : cardToDefinition.keySet()) {
                printWriter.println(p);
                printWriter.println(cardToDefinition.get(p));
                printWriter.println(hardest.get(p));
                cardCounter++;
            }
            printWriter.close();

        } catch (FileNotFoundException e) {
            println("File cant be created.");
        }

        println(cardCounter + " cards have been saved.");
    }

    public static void ask(){
        Random random = new Random();

        Object[] randCard = cardToDefinition.keySet().toArray();


        println("How many times to ask?");
        int f = Integer.parseInt(readln());
        for (int i = 0; i < f; i++) {
            int rand = random.nextInt(cardToDefinition.size());
            String s = (String) randCard[rand];


            println("Print the definition of \"" + s + "\":");
            definition = readln();
            if (definition.equals(cardToDefinition.get(s))) {
                print("Correct answer. ");
            } else if (definitionToCard.containsKey(definition)) {
                hardest.replace(s,hardest.get(s)+1);
                print("Wrong answer (the correct one is \"" + cardToDefinition.get(s) + "\", you've just written the definition of \"" + definitionToCard.get(definition) + "\".)");
            } else {
                hardest.replace(s,hardest.get(s)+1);
                print("Wrong answer (the correct one is \"" + cardToDefinition.get(s) + "\").");
            }
        }
    }

    public static void resetStats(){
        for(String s:hardest.keySet()){
            hardest.replace(s,0);
        }
        println("Card statistics has been reset.");
    }

    public static void main(String[] args) {
        boolean saveExit=false;
        boolean loeadStart=false;
        for(int i =0;i<args.length;i++){
            if(args[i].equals("-import")){
                importPath=args[i+1];
                importStart();
                loeadStart=true;
            }
            if(args[i].equals("-export")){
                exportPath=args[i+1];
                saveExit=true;
            }
        }

     //   if(!loeadStart){
     //       System.out.println("0 cards have been loaded.");
     //   }



        boolean t = true;
        String tmp = "";
        while (t) {
            println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            tmp = readln();
            switch (tmp) {
                default:
                    println("wrong option");
                    break;
                case "add":
                    add();
                    break;
                case "remove":
                    remove();
                    break;
                case "import":
                    importCards();
                    break;
                case "export":
                    exportCards();
                    break;
                case "ask":
                    ask();
                    break;
                case "log":
                    log();
                    break;
                case"hardest card":
                    hardestCard();
                    break;
                case "reset stats":
                    resetStats();
                    break;
                case "exit":
                    t = false;
                    break;

            }
        }



        println("Bye bye!");
        if(saveExit){
            exportExit();
        }
    }
}
