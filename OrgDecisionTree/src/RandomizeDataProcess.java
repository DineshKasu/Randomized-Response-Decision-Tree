
import java.io.*;
import java.util.*;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dineshkasu
 */
public class RandomizeDataProcess
{
 
    public void randomizefile(String inFile, String outFile,Double theta) throws Exception
    {
        BufferedReader ReadDataFile = new BufferedReader(new FileReader(inFile));
        PrintStream RandDataFile = new PrintStream(new FileOutputStream(outFile));
       
         String DataFileString;
         int ColumnCount;
         double Randval;
         boolean reverse = true;
         String Male="1",Ridden="1",Tall="1",Near="1",Comma =",";
         
      
         while((DataFileString = ReadDataFile.readLine())!= null )
         {
             StringTokenizer DataFileContent = new StringTokenizer(DataFileString, ",");
             ColumnCount = 0;    
             Randval = Math.random();

             while(DataFileContent.hasMoreTokens())
             {
                  String Content = DataFileContent.nextToken();
                  ColumnCount = ColumnCount + 1; 
                  
                  if (Randval > theta)
                  { 
                      
                       switch(Content)
                        {
                           case "1":  Content = "0";  break;  
                           case "0":  Content = "1";   break; 
                        }
                       
                  }
                  
                          if(ColumnCount == 1)
                          {
                            Male = Content;
                          }                        
                          if(ColumnCount == 2)
                          {
                             Tall = Content;
                          }
                          if(ColumnCount == 3)
                          {
                             Near = Content;
                          }  
                          if(ColumnCount == 4)
                          {
                             Ridden = Content;
                          }     
                              
               }//while each line end
                      RandDataFile.println(Male + Comma + Tall + Comma + Near + Comma + Ridden);
         }// while out end
                 
              
    
   } // fun end
    
}    
