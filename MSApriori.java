

import java.io.*;
import static java.lang.Integer.parseInt;
import java.util.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MSApriori {

 
    
//        AprioriCalculation ap = new AprioriCalculation();

  //      ap.aprioriProcess();
    //}

/******************************************************************************
 * Class Name   : AprioriCalculation
 * Purpose      : generate Apriori itemsets
 *****************************************************************************/
/*class AprioriCalculation
{*/
    Vector<String> candidates=new Vector<String>(); //the current candidates
    static String inputFile; //configuration file
    static String inputParamFile; //transaction file
    static String outputFile;//output file
    static int numItemIDs=0; //number of unique items
    static int numTransactions=0; //number of transactions
    public static LinkedHashMap<Integer, Double> minSupports;
    static double supportDifferenceConstraint = 0.0;
    static int[][] cannotBeTogether;
    static int mustContain[];
    static PrintWriter pr;
    double minSup; //minimum support for a frequent itemset
    static int transactions[];

    /************************************************************************
     * Method Name  : aprioriProcess
     * Purpose      : Generate the apriori itemsets
     * Parameters   : None
     * Return       : None
     *************************************************************************/
 /*   public void aprioriProcess()
    {
       
        int itemsetNumber=0; //the current itemset being looked at
        

        System.out.println("MSApriori algorithm has started.\n");

        

        //while not complete
        do
        {
            //increase the itemset that is being looked at
            itemsetNumber++;

            //generate the candidates
            generateCandidates(itemsetNumber);

            //determine and display frequent itemsets
            calculateFrequentItemsets(itemsetNumber);
            if(candidates.size()!=0)
            {
                System.out.println("Frequent " + itemsetNumber + "-itemsets");
                System.out.println(candidates);
            }
        //if there are <=1 frequent items, then its the end. This prevents reading through the database again. When there is only one frequent itemset.
        }while(candidates.size()>1);

        

 }

    /************************************************************************
     * Method Name  : getInput
     * Purpose      : get user input from System.in
     * Parameters   : None
     * Return       : String value of the users input
     *************************************************************************/
    public static ArrayList<Integer> arrayStringToIntegerArray(String arrayString){
    String removedBrackets = arrayString.substring(1, arrayString.length() - 1);
    String[] individualNumbers = removedBrackets.split(",");
    ArrayList<Integer> integerArrayList = new ArrayList<>();
    for(String numberString : individualNumbers){
        integerArrayList.add(Integer.parseInt(numberString.trim()));
    }
    return integerArrayList;
}
     static void getinput() throws FileNotFoundException, NumberFormatException
     {
         File file = new File("C:\\Users\\parbhakar loke\\Documents\\NetBeansProjects\\JavaApplication3\\src\\input-data.txt");
        inputParamFile="C:\\Users\\parbhakar loke\\Documents\\NetBeansProjects\\JavaApplication3\\src\\parameter-file.txt"; //transaction file
        outputFile="C:\\Users\\parbhakar loke\\Documents\\NetBeansProjects\\JavaApplication3\\src\\output.txt";//
        
        ArrayList<String> Transactions = new ArrayList<String>();
        String input="";
        List<Integer> newList ;
        
       // FileInputStream file_in = new FileInputStream(inputFile);
         BufferedReader reader = new BufferedReader(new FileReader(file));
        String parts[] = null;
        int intParts[] = null;
        //try to get users input, if there is an error print the message
       try{
            while((input = reader.readLine()) != null) {
                input = input.substring(1, input.length() - 1); // removed '{' and '}' chars]
                input = input.replace(",", "").trim();
                parts = input.split(" ");

                intParts = new int[parts.length];
                for(int i = 0; i < intParts.length; i++) {
                    intParts[i] = Integer.parseInt(parts[i]);
                }
                Transactions.add(Arrays.toString(intParts));
                numTransactions++;
            }
           
            for (int i = 0; i < Transactions.size(); i++)
            {
                System.out.println(Transactions.get(i));
                System.out.println(arrayStringToIntegerArray(Transactions.get(0)));
            }
            
    Iterator<Integer> iterator = arrayStringToIntegerArray(Transactions.get(0)).iterator();
    int[] ret = new int[Transactions.size()];
   
    for (int i = 0; i < ret.length; i++)
    {
        ret[i] = iterator.next().intValue();//output in array of 1 transaction
    }
    
    
            System.out.println("ret= "+Arrays.toString(ret));
        }catch (IOException e) {
            System.out.println("Could not load transaction set.");
            e.printStackTrace();
            
        }
     }
     private static void loadParameters(String parameterPath) throws FileNotFoundException {
        minSupports = new LinkedHashMap<>();

               ArrayList<Integer> mustContainList = new ArrayList<>();

        try {
            BufferedReader bb = new BufferedReader(new FileReader(parameterPath));
            String line = "";

            while((line = bb.readLine()) != null) {
                if (line.contains("MIS")) {
                    addMinimumItemSupport(line);
                }
                else if (line.contains("SDC")) {
                    setSupportDifferenceConstraint(line);
                }
                else if (line.contains("cannot_be_together")) {
                    addCannotBeTogether(line);
                }
                else if (line.contains("must-have")) {
                    addMustHave(line, mustContainList);
                }
            }
           // Set<Integer> keys = minSupports.keySet();
      //  for(Integer k:keys){
            System.out.println(10+" -- "+minSupports.get(10));   
           

             } catch (IOException e) {
            System.out.println("Could not load parameters.");
            e.printStackTrace();
        }
    }
     private static void addMinimumItemSupport(String line) {
        line = line.substring(4); // removes MIS( from line
        String parts[] = line.split("=");

        // Item ID cleanup
        int itemID = Integer.parseInt(parts[0].replace(")", "").replace(" ", ""));

        // MIS of Item
        double mis = Double.parseDouble(parts[1].replace(" ", ""));

        minSupports.put(itemID, mis);
        int itemIDCount;

        numItemIDs++;
    }
      private static void setSupportDifferenceConstraint(String line) {
        line = line.substring(4);
        line = line.replace("=", "").replace(" ", "");

        supportDifferenceConstraint = Double.parseDouble(line);
        
    }
       private static void addCannotBeTogether(String line) {
        line = line.replace("cannot_be_together: {", "");
        String parts[] = line.split("},");
        
         char[] ch= null;
         String s=new String();
        String s1=new String();
        
        int intPartIDs[] = null;
                cannotBeTogether=new int[parts.length][];
                for(int i = 0; i < parts.length; i++) {
                  s1=parts[i].replace(" ","").replace("{", "").replace("}", "");
                   System.out.println("s1="+s1);
                 String[] partIDs=s1.split(",");
                 int j=0;
                 
                 for (j=0;j<partIDs.length;j++){
                     cannotBeTogether[i]=new int[partIDs.length];
                    
                    cannotBeTogether[i][j]=Integer.parseInt(partIDs[j]);
                    
                 }
                }                
            }
        
        private static void addMustHave(String line, ArrayList<Integer> mustHaveList) {
        line = line.replace("must-have: ", "").trim();
        String parts[] = line.split("or");

        for (String part : parts) {
            part = part.trim();
            mustHaveList.add(Integer.parseInt(part));
           
        }
    }

        public static void main(String[] args) throws FileNotFoundException {
            getinput();
            loadParameters(inputParamFile);
        }
}

        
              



    /************************************************************************
     * Method Name  : generateCandidates
     * Purpose      : Generate all possible candidates for the n-th itemsets
     *              : these candidates are stored in the candidates class vector
     * Parameters   : n - integer value representing the current itemsets to be created
     * Return       : None
     *************************************************************************/
  /*  private void generateCandidates(int n)
    {
        Vector<String> tempCandidates = new Vector<String>(); //temporary candidate string vector
        String str1, str2; //strings that will be used for comparisons
        StringTokenizer st1, st2; //string tokenizers for the two itemsets being compared

        //if its the first set, candidates are just the numbers
        if(n==1)
        {
            for(int i=1; i<=numItems; i++)
            {
                tempCandidates.add(Integer.toString(i));
            }
        }
        else if(n==2) //second itemset is just all combinations of itemset 1
        {
            //add each itemset from the previous frequent itemsets together
            for(int i=0; i<candidates.size(); i++)
            {
                st1 = new StringTokenizer(candidates.get(i));
                str1 = st1.nextToken();
                for(int j=i+1; j<candidates.size(); j++)
                {
                    st2 = new StringTokenizer(candidates.elementAt(j));
                    str2 = st2.nextToken();
                    tempCandidates.add(str1 + " " + str2);
                }
            }
        }
        else
        {
            //for each itemset
            for(int i=0; i<candidates.size(); i++)
            {
                //compare to the next itemset
                for(int j=i+1; j<candidates.size(); j++)
                {
                    //create the strigns
                    str1 = new String();
                    str2 = new String();
                    //create the tokenizers
                    st1 = new StringTokenizer(candidates.get(i));
                    st2 = new StringTokenizer(candidates.get(j));

                    //make a string of the first n-2 tokens of the strings
                    for(int s=0; s<n-2; s++)
                    {
                        str1 = str1 + " " + st1.nextToken();
                        str2 = str2 + " " + st2.nextToken();
                    }

                    //if they have the same n-2 tokens, add them together
                    if(str2.compareToIgnoreCase(str1)==0)
                        tempCandidates.add((str1 + " " + st1.nextToken() + " " + st2.nextToken()).trim());
                }
            }
        }
        //clear the old candidates
        candidates.clear();
        //set the new ones
        candidates = new Vector<String>(tempCandidates);
        tempCandidates.clear();
    }

    /************************************************************************
     * Method Name  : calculateFrequentItemsets
     * Purpose      : Determine which candidates are frequent in the n-th itemsets
     *              : from all possible candidates
     * Parameters   : n - iteger representing the current itemsets being evaluated
     * Return       : None
     *************************************************************************/
   /* private void calculateFrequentItemsets(int n)
    {
        Vector<String> frequentCandidates = new Vector<String>(); //the frequent candidates for the current itemset
        FileInputStream file_in; //file input stream
        BufferedReader data_in; //data input stream
        FileWriter fw;
        BufferedWriter file_out;

        StringTokenizer st, stFile; //tokenizer for candidate and transaction
        boolean match; //whether the transaction has all the items in an itemset
        boolean trans[] = new boolean[numItems]; //array to hold a transaction so that can be checked
        int count[] = new int[candidates.size()]; //the number of successful matches

        try
        {
                //output file
                fw= new FileWriter(outputFile, true);
                file_out = new BufferedWriter(fw);
                //load the transaction file
                file_in = new FileInputStream(transaFile);
                data_in = new BufferedReader(new InputStreamReader(file_in));

                //for each transaction
                for(int i=0; i<numTransactions; i++)
                {
                    //System.out.println("Got here " + i + " times"); //useful to debug files that you are unsure of the number of line
                    stFile = new StringTokenizer(data_in.readLine(), itemSep); //read a line from the file to the tokenizer
                    //put the contents of that line into the transaction array
                    for(int j=0; j<numItems; j++)
                    {
                        trans[j]=(stFile.nextToken().compareToIgnoreCase(oneVal[j])==0); //if it is not a 0, assign the value to true
                    }

                    //check each candidate
                    for(int c=0; c<candidates.size(); c++)
                    {
                        match = false; //reset match to false
                        //tokenize the candidate so that we know what items need to be present for a match
                        st = new StringTokenizer(candidates.get(c));
                        //check each item in the itemset to see if it is present in the transaction
                        while(st.hasMoreTokens())
                        {
                            match = (trans[Integer.valueOf(st.nextToken())-1]);
                            if(!match) //if it is not present in the transaction stop checking
                                break;
                        }
                        if(match) //if at this point it is a match, increase the count
                            count[c]++;
                    }

                }
                for(int i=0; i<candidates.size(); i++)
                {
                    //  System.out.println("Candidate: " + candidates.get(c) + " with count: " + count + " % is: " + (count/(double)numItems));
                    //if the count% is larger than the minSup%, add to the candidate to the frequent candidates
                    if((count[i]/(double)numTransactions)>=minSup)
                    {
                        frequentCandidates.add(candidates.get(i));
                        //put the frequent itemset into the output file
                        file_out.write(candidates.get(i) + "," + count[i]/(double)numTransactions + "\n");
                    }
                }
                file_out.write("-\n");
                file_out.close();
        }
        //if error at all in this process, catch it and print the error messate
        catch(IOException e)
        {
            System.out.println(e);
        }
        //clear old candidates
        candidates.clear();
        //new candidates are the old frequent candidates
        candidates = new Vector<String>(frequentCandidates);
        frequentCandidates.clear();
    }
}*/