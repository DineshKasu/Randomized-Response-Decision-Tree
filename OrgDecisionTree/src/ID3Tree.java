import java.io.*;
import java.util.*;

public class ID3Tree 
{
    private static double PEwithoutClass,PENotwithoutClass,PEwithClass,PENotwithClass;
    private static double PStarEwithoutClass,PStarENotwithoutClass,PStarEwithClass,PStarENotwithClass;
    private static double theta ;
    private static String inputFilename = "./training_rand.txt";
    
	public static ArrayList<Boolean[]> readFile(String filename,int PercTraining) throws Exception 
        {

		ArrayList<Boolean[]> data = new ArrayList<>();
		int count=0;
		Scanner input = new Scanner(new File(filename));
		
		while (input.hasNext()&& count < PercTraining) 
                {
			String line = input.nextLine();
			String[] tokens = line.split("[,]");
			
			Boolean[] record = new Boolean[tokens.length];
			for (int i=0; i<tokens.length; i++) 
                        {
				if (tokens[i].trim().equals("1")) {
					record[i] = true;
				} else {
					record[i] = false;
				}
			}
			 data.add(record);
                         count = count + 1;
                       // System.out.println("count is "+count);
		}
		
		input.close();
		return data;
	}
	
       
        //
	public static ArrayList<Boolean[]> getTrueRows(ArrayList<Boolean[]> data, int attribute) {
		ArrayList<Boolean[]> trueRows = new ArrayList<>();
                
                
		
		for (Boolean[] row: data) {
			if (row[attribute]) {
				trueRows.add(row);
			}
		}
		
		return trueRows;
	}
	
	public static ArrayList<Boolean[]> getFalseRows(ArrayList<Boolean[]> data, int attribute) {
		ArrayList<Boolean[]> falseRows = new ArrayList<>();
		
		for (Boolean[] row: data) {
			if (!row[attribute]) {
				falseRows.add(row);
			}
		}
		
		return falseRows;
	}
	
	public static boolean allOneClass(ArrayList<Boolean[]> data) {
		return (numTrue(data) == data.size() || numFalse(data) == data.size());
	}
	
	public static boolean majorityClassRandom(ArrayList<Boolean[]> data) throws Exception
        {
		//return numTrue(data) > numFalse(data);
                
                int numTrue = PStarEWithoutClass("./training_rand.txt","1",3);
                int numFalse = PStarENotWithoutClass("./training_rand.txt","0",3); 
                
                return numTrue > numFalse;   
	}
	
	public static int numTrue(ArrayList<Boolean[]> data) {
		int count = 0;
		for (Boolean[] row: data) {
			if (row[row.length-1]) {
				count++;
			}
		}
		return count;
	}
	
	public static int numFalse(ArrayList<Boolean[]> data) {
		int count = 0;
		for (Boolean[] row: data) {
			if (!row[row.length-1]) {
				count++;
			}
		}
		return count;
	}
	

	public static double entropyRandom(ArrayList<Boolean[]> data,int indicator) throws Exception
        {
		
		int numTrue = PStarEWithoutClass("./training_rand.txt","1",3);
                int numFalse = PStarENotWithoutClass("./training_rand.txt","0",3);
                
                 double SVtrue = (PEwithClass*data.size());
                 double Strue  = (PEwithoutClass*data.size()); 
                 double SVfalse = (PENotwithClass*data.size());
                 double Sfalse  = (PENotwithoutClass*data.size()); 
                
                double Qtrue;
		double Qfalse;
                double entropy = 0;
                
		
                if(indicator == 0)
                {
                     Qtrue = numTrue(data) / (double) data.size();
                     Qfalse = numFalse(data) / (double) data.size();
                  
                  	if (Qtrue != 0 && !Double.isNaN(Qtrue)) 
                        {
			entropy += Qtrue * (Math.log(Qtrue) / Math.log(2));
		         }
		 
		        if (Qfalse != 0 && !Double.isNaN(Qfalse)) 
                        {
			entropy += Qfalse * (Math.log(Qfalse) / Math.log(2));
		         }
		
                }
                else if (indicator == 1)
                {
                    Qtrue = SVtrue /Strue; 
                    
                    entropy += Qtrue * (Math.log(Qtrue) / Math.log(2));
                    
                }
                else
                {
                    Qfalse = SVfalse /Sfalse;
                    entropy += Qfalse * (Math.log(Qfalse) / Math.log(2));
                }

		return -entropy;
	}
	
	public static double findGainRandom(ArrayList<Boolean[]> data, int attribute) throws Exception
        {
                 EvaluateEandEnotValues(inputFilename,attribute);      
                 
                  double SVtrue = (PEwithClass*data.size());
                  double SVfalse = (PENotwithClass*data.size());
                  
                 double Strue  = (PEwithoutClass*data.size()); 
                 
                 double Sfalse  = (PENotwithoutClass*data.size()); 
                 
                 ArrayList<Boolean[]> trueRows = getTrueRows(data, attribute);
                 ArrayList<Boolean[]> falseRows = getFalseRows(data, attribute);
		
               /* double gain = entropy(data,0) - 
				(((SVtrue / Strue) * entropy(data,1)) + ((SVfalse/Sfalse) * entropy(data,2))); */
                
                 double gain = entropyRandom(data,0) - 
				(((trueRows.size() / SVtrue) * entropyRandom(data,1)) + ((falseRows.size()/SVfalse) * entropyRandom(data,2)));
	
               // System.out.println("gain"+gain);

		if (Double.isNaN(gain)) 
			return 0.0;
		else 
			return gain;
	}
	 
	public static int selectAttributeRandom(ArrayList<Boolean[]> data, HashSet<Integer> attributes) throws Exception
        {
		double maxGain = -Double.MIN_VALUE;
		int attribute = -1;
		
		for (Integer i: attributes) {
			double gain = findGainRandom(data, i);
			if (gain > maxGain) {
				maxGain = gain;
				attribute = i;
			}
		}
		
		return attribute;
	}
	

	public static Node buildTreeRandom(ArrayList<Boolean[]> data, HashSet<Integer> attributes) throws Exception 
        {
                Node node = new Node();
		
		/*
                if (allOneClass(data)) {
			node.label = majorityClass(data);
			return node;
		}
                */
		
		if (attributes.size() == 0) { // no more attributes to split on
			node.label = majorityClassRandom(data);
			return node;
		}
                 
                
		node.attribute = selectAttributeRandom(data, attributes);
		attributes.remove(node.attribute);
               
		ArrayList<Boolean[]> trueRows = getTrueRows(data, node.attribute);
		/*
                if (trueRows.size() == 0) {
			node.trueChild = new Node();
			node.trueChild.label = majorityClass(data);
		} else { */
			node.trueChild = buildTreeRandom(trueRows, attributes);
		//}
		
		ArrayList<Boolean[]> falseRows = getFalseRows(data, node.attribute);
		/*
                if (falseRows.size() == 0) {
			node.falseChild = new Node();
			node.falseChild.label = majorityClass(data);
		} else {
		*/	node.falseChild = buildTreeRandom(falseRows, attributes);
		//}
		
		return node;
	}

	public static boolean classify(Node node, Boolean[] row) {
		if (node.label != null) {
			return node.label;
		} else {
			boolean direction = row[node.attribute];
			if (direction) {
				return classify(node.trueChild, row);
			} else {
				return classify(node.falseChild, row);
			}
		}
	}
	
	public static void traverseTree(Node node, int tab, String dir) {
		if (node.label != null) {
			for (int i=0; i<tab; i++) {
				System.out.print("\t");
			}
			System.out.println(dir + "the final answer is " + node.label);
		} else {
			for (int i=0; i<tab; i++) {
				System.out.print("\t");
			}
			System.out.println(dir + "split on " + node.attribute);
			traverseTree(node.trueChild, tab+1, "if true: ");
			traverseTree(node.falseChild, tab+1, "if false: ");
		}
	}
	   
        public static int PStarEWithoutClass(String inputFilename,String TruthValue,int AttributePosition) throws Exception
        {
            int count=0;
            try (Scanner input = new Scanner(new File(inputFilename))) 
            {
                while (input.hasNext()) 
                {
                    String line = input.nextLine();
                    String[] tokens = line.split("[,]");
                        if (tokens[AttributePosition].trim().equals(TruthValue))
                        {
                            count = count + 1;
                        }    
                }
            }
            return count;
        }

        
        public static int PStarENotWithoutClass(String inputFilename,String TruthValue,int AttributePosition) throws Exception
        {
            int count=0;
          //  if (TruthValue.equals("1")){TruthValue = "0";} else {TruthValue = "1";}
            try (Scanner input = new Scanner(new File(inputFilename))) 
            {
                while (input.hasNext()) 
                {
                    String line = input.nextLine();
                    String[] tokens = line.split("[,]");
                        if (tokens[AttributePosition].trim().equals(TruthValue))
                        {
                            count = count + 1;
                        }    
                }
            }
            return count;
        }
        
        
        public static int PStarEWithClass(String inputFilename,String TruthValue,String ClassValue,int AttributePosition) throws Exception
        {
         
            int count=0;
            try (Scanner input = new Scanner(new File(inputFilename))) 
            {
                while (input.hasNext()) 
                {
                    String line = input.nextLine();
                    String[] tokens = line.split("[,]");
                        if ((tokens[AttributePosition].trim().equals(TruthValue))&&(tokens[3].trim().equals(ClassValue)))
                        {
                            count = count + 1;
                        }    
                }
            }
            return count;
        }
        
        
        public static int PStarENotWithClass(String inputFilename,String TruthValue,String ClassValue,int AttributePosition) throws Exception
        {
            int count=0;
           // if (TruthValue.equals("1")){TruthValue = "0";} else {TruthValue = "1";}
            try (Scanner input = new Scanner(new File(inputFilename))) 
            {
                while (input.hasNext()) 
                {
                    String line = input.nextLine();
                    String[] tokens = line.split("[,]");
                        if ((tokens[AttributePosition].trim().equals(TruthValue))&&(tokens[3].trim().equals(ClassValue)))
                        {
                            count = count + 1;
                        }    
                }
            }
            return count;
        }
        
        public static void EvaluateEandEnotValues(String inputFilename,int AttributePosition) throws Exception
        {
 
            PStarEwithoutClass = PStarEWithoutClass(inputFilename,"1",AttributePosition);
            PStarENotwithoutClass = PStarENotWithoutClass(inputFilename,"0",AttributePosition);
            PStarEwithClass = PStarEWithClass(inputFilename,"1","1",AttributePosition);
            PStarENotwithClass = PStarENotWithClass(inputFilename,"0","0",AttributePosition);

            PEwithoutClass = ((PStarENotwithoutClass*(1-theta))-(PStarEwithoutClass*theta))/(1-(2*theta));
            PENotwithoutClass = ((PStarEwithoutClass*(1-theta))-(PStarENotwithoutClass*theta))/(1-(2*theta));
            
            PEwithClass = ((PStarENotwithClass*(1-theta))-(PStarEwithClass*theta))/(1-(2*theta));
            PENotwithClass = ((PStarEwithClass*(1-theta))-(PStarENotwithClass*theta))/(1-(2*theta)); 
        }
        
	public static void main(String[] args) throws Exception 
        {
             boolean isRunning , StartBuildTree = false;
            // Double theta;
             BufferedReader UserInput = new BufferedReader(new InputStreamReader(System.in));
             int Count;
             int PercTraining [] ={1000,750,500,300,100,60,40,10,5};
             double PercTrained [] ={100,75,50,30,10,6,4,1,0.5};
             
             String DataFileString;
             int ColumnCount,AllTrue =0,AllFalse=0;
             Boolean Male,Tall,Near,Ridden = true,Varb; 
             Boolean[] record = {false, true, true};
             
             OriginalTree Org = new OriginalTree();
             System.out.println("---------------------------------------------------");
             System.out.println("Results for UnRandmozied data with unmodified code");
             Org.DisplayResults("./training.txt","./test.txt");

             do
              {
                      if(StartBuildTree)
                          {
                                RandomizeDataProcess RandomizeFileData = new RandomizeDataProcess();
                                System.out.println("Please Enter the Theta Value");
                                theta = Double.parseDouble(UserInput.readLine());
                                RandomizeFileData.randomizefile("./training.txt","./training_rand.txt",theta);
                                RandomizeFileData.randomizefile("./test.txt", "./test_rand.txt",theta);
                                System.out.println("Both Training Data and Test data are randomized "); 
                                System.out.println("-------------------------------------------------");
                                System.out.println("Results for Randmozied data with unmodified code");
                                Org.DisplayResults("./training_rand.txt", "./test_rand.txt");       
                                System.out.println("------------------------------------");
                                System.out.println("Results for Randmozied data with modified code");
                                System.out.println("--------------             ---------");                
                                System.out.println("%TRAINING DATA             %ACCURACY");
                                System.out.println("--------------             ---------");

                                for(Count = 0; Count < 9; Count++)
                               {
                                   AllTrue = 0;
                                  ArrayList<Boolean[]> data = readFile("./training_rand.txt",PercTraining[Count]);
                                  HashSet<Integer> attributes = new HashSet<>();
		                  for (int i=0; i<data.get(0).length-1; i++) {attributes.add(i);}

		                  Node root = buildTreeRandom(data, attributes);
                                  //traverseTree(root, 0, ""); 
                                 // System.out.println("---------------------------");
                                  BufferedReader ReadDataFile = new BufferedReader(new FileReader("./test_rand.txt"));
                                  
                                  while((DataFileString = ReadDataFile.readLine())!= null )
                                 {
                                     StringTokenizer DataFileContent = new StringTokenizer(DataFileString, ",");
                                     ColumnCount = 0;              
                                     while(DataFileContent.hasMoreTokens())
                                    {
                                         String CurrentContent = DataFileContent.nextToken();
                                         ColumnCount = ColumnCount + 1;  
                                         if ("1".equals(CurrentContent)){Varb = true;}else{Varb = false;}    
                                         switch(ColumnCount)
                                        {
                                          case 1:  record[0] = Varb;  break;  
                                          case 2:  record[1] = Varb;  break; 
                                          case 3:  record[2] = Varb;  break;
                                          case 4:  Ridden = Varb;     break;           
                                        }
                                    }       
                                     Arrays.toString(record);
                                     Boolean r = classify(root,record); 
                                   if ((Ridden == r)) 
                                   {
                                       AllTrue = AllTrue + 1; 
                                   } 
                                    else 
                                   {
                                          AllFalse = AllFalse + 1; 
                                   }
                                 }
                                  System.out.println(PercTrained[Count]+"                    "+AllTrue);
                                  AllTrue = 0;
                               }                    

                          }
                          
                          //It takes the input from the console as User enters
                           System.out.println("Do you want to check the Randmoziation of a file using Theta ?");
                           System.out.println("Please enter (Y/N)?");
                           Scanner scanner = new Scanner(System.in);
                           String input = scanner.next();
                           char Response = input.charAt(0); 
                           isRunning = (Response == 'y') || (Response == 'Y');
                           StartBuildTree = false;
                           if(isRunning){StartBuildTree = true;}
                       }
                       while(isRunning);

          }

}
