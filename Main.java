/*
 * Project: Project 2 - Disneyland Dining Rewards
 * Author: Jimmy Nguyen
 * Contact me: Jimmy@JimmyWorks.net
 */
package cs2336_project2;

import java.io.*;
import java.util.*;
import cs2336_project2.Customer.*;

/*

This project simulates how a company may handle transactions and customer database
for a reward program.  Strictly using arrays and dynamically expanding and contracting,
as needed, customer and preferred customer database files are uploaded to the program.
The Customer class is used as objects to store customer ID numbers, first and last names,
and total spending history.  The Preferred class is a derived class from Customer specifically
for customers who have spent enough to be promoted to a reward member.  These members have
access to special discount benefits depending on spending level.  Each transaction that
comes in calculates the total transaction amount and is added to the total spending history
for that customer.  Upon reaching reward member status, or upon reaching higher reward levels,
the customer is removed from the Customer array and added to the Preferred array or his/her 
reward/discount is increased, as needed.  Polymorphism is demonstrated, as needed, in the program.
All global constants are labeled for easy reading and easy future updates.  

*/

public class Main {
    
//==============================================================================
//                          Global Constants
//==============================================================================
    
//Filenames:  Update, as needed, if changed.
static final String CUSTOMER_FILE = "customers.dat";
static final String PREFERRED_FILE = "preferred.dat";
static final String TRANSACTION_FILE = "orders.dat";
//==Reward Member Discounts==//
//Additional discount reward levels can be added to this table, but they must
//be sorted with the lowest level and discount in the top rows down to the 
//highest reward level and discount at the bottom rows.
//Column 1: Spending Requirement, Column 2: Discount Applied
static final double [][] DISCOUNT_TABLE =   {
                                            {150, 0.05}, //bronze level
                                            {200, 0.07}, //silver level
                                            {350, 0.10}  //gold   level
                                            };
//For ease reading code, the following is declared for accessing transaction variables
    static final int ID = 0;
    static final int CUP_RADIUS = 1;
    static final int CUP_HEIGHT = 2;
    static final int DRINK_OZ = 3;
    static final int OZ_PRICE = 4;
    static final int SQIN_PRICE = 5;
    static final int QUANTITY = 6;
    //Sum of the items in each transaction
    static final int ORDER_ITEMS_COUNT = 7;

//For ease reading code, the following is declared for determining member status    
    static final int SEARCHING = -1;
    static final int CUSTOMER = 0;
    static final int PREFERRED = 1;

//==============================================================================
//                              Main
//==============================================================================   
    public static void main(String[] args) throws Exception{
        
        
        int tempArraySize;  //declaration for sizing customer and preferred customer arrays
        Scanner input = new Scanner(System.in); //scanner in case files cannot be located automatically
        String customerFilename = CUSTOMER_FILE; //default filenames
        String preferredFilename = PREFERRED_FILE;
        
        //Locating Customer Database File
        //Create file and check if the file exists.  If the file does not exist,
        //prompt the user through the console to specify its location
        File customerFile = new File(customerFilename);
        while(!customerFile.exists() || customerFile.isDirectory()){ //while loop to locate file
                System.out.println("Your customer database file was not found.");
                System.out.println("Please specify the name of your input file:");
                customerFilename = input.nextLine();
                customerFile = new File(customerFilename);
        }
        //Read number of lines in customer file
        BufferedReader readCustomer = new BufferedReader(new FileReader(customerFilename));
        tempArraySize = getArrayRows(readCustomer);
        //Create customer array and fill the array from the file
        Customer[] customerList = new Customer[tempArraySize];
        readCustomer = new BufferedReader(new FileReader(customerFilename));
        fillCustomerArray(customerList, readCustomer);        

        //Locating Preferred Customer Database File
        //Create file and check if the file exists.  If the file does not exist,
        //prompt the user through the console to specify its location
        File preferredFile = new File(preferredFilename);
        while(!preferredFile.exists() || preferredFile.isDirectory()){ //while loop to locate file
                System.out.println("Your preferred customer database file was not found.");
                System.out.println("Please specify the name of your input file:");
                preferredFilename = input.nextLine();
                preferredFile = new File(preferredFilename);
        }
        //Read number of lines in preferred file    
        BufferedReader readPreferred = new BufferedReader(new FileReader(preferredFilename));
        tempArraySize = getArrayRows(readPreferred);
        //Create preferred array and fill the array from the file
        Preferred[] preferredList = new Preferred[tempArraySize];
        readPreferred = new BufferedReader(new FileReader(preferredFilename));
        fillPreferredArray(preferredList, readPreferred);

//==============================================================================
//                      Begin Processing Orders
//==============================================================================
        
        //Create scanner to scan the transaction file
        Scanner scanOrders = new Scanner(new File(TRANSACTION_FILE));
        String buffer;  //buffer for storing the next line
        
        //=== PROCESSING LOOP ===
        //Each iteration of this while loop goes through a line of a single 
        //transaction, processes the transaction, finds the respective customer's
        //account, either in customer or preferred customer array, and update that
        //account with the new running total.  Any promotion to reward member or 
        //higher reward level is performed as well.
        while(scanOrders.hasNext()){
            buffer = scanOrders.nextLine(); //take the next transaction from file
            Scanner scanLine = new Scanner(buffer); //scans that line
            double[] newOrder = new double[ORDER_ITEMS_COUNT]; //creates temp array to hold elements
            for(int i = 0; scanLine.hasNext(); i++){
                newOrder[i] = scanLine.nextDouble();    //stores all elements in temp array
            }
            //=== BEGIN PROCESSING NEW ORDER ===
            int customerType = SEARCHING;   //while searching for the customer ID,
            int foundIndex = SEARCHING;     //the current customer type and index in
            double price, area;             //the array is -1 (sentinel for not found)
            
            //Look up the customer ID in the preferred list
            foundIndex = lookupCustomer(newOrder[ID], preferredList); //polymorphic call
            //If found, the they are a preferred customer
            if(foundIndex >= 0)
                customerType = PREFERRED;
            else{   //if not found
                //=== Customer or New Customer Loop ===
                while(foundIndex == -1){    //loop to find customer or create new customer account
                    //Loop up the customer ID in the customer list
                    foundIndex = lookupCustomer(newOrder[ID], customerList);          
                    //If found, they are a customer
                    if(foundIndex >= 0)
                        customerType = CUSTOMER;
                    //Else, create a customer account and loop back to try again
                    else{
                        customerList = createCustomer(newOrder[ID], customerList);
                    }
                }
            }

            //Calculations for Surface Area of the Cup 
            //This is to customize the cup and get the total price of the transaction
            //Area = 2*Surface Area of the top and bottom + Surface Area of the side
            //Price = (Total Surface Area * price per sq in + drink ounces * price per ounce) * number of drinks
            area = 2*Math.pow(newOrder[CUP_RADIUS], 2)*Math.PI + 2*Math.PI*newOrder[CUP_RADIUS]*newOrder[CUP_HEIGHT];       
            price = ( area * newOrder[SQIN_PRICE] + newOrder[DRINK_OZ]*newOrder[OZ_PRICE] ) * newOrder[QUANTITY];
            
            //=== For Preferred Customers ===
            if(customerType == PREFERRED){
                price = price - price*preferredList[foundIndex].getDiscount(); //apply discount by subtracting the discount from the price
                price += preferredList[foundIndex].getLifetime(); //add the purchase to the lifetime total
                preferredList[foundIndex].setLifetime(price); //save the new lifetime total to customer's account
                
                //Check if the current discount is less than the maximum discount available in the discount table,
                //i.e. checking the last row's index and column index 1, which holds the discount.
                //If it's less than the maximum reward, check if they qualify to promote to a higher reward level.
                if(preferredList[foundIndex].getDiscount() < DISCOUNT_TABLE[DISCOUNT_TABLE.length - 1][1]){
                    for(int i = 0; i < DISCOUNT_TABLE.length; i++){ //if they are not the max reward, cycle the reward table
                        if(preferredList[foundIndex].getLifetime() >= DISCOUNT_TABLE[i][0])
                            preferredList[foundIndex].setDiscount(DISCOUNT_TABLE[i][1]);
                    }
                }
                
            } 
            //=== For Regular Customers ===
            else if(customerType == CUSTOMER){
                //no discount applied
                price += customerList[foundIndex].getLifetime(); //add the purchase to the lifetime total
                customerList[foundIndex].setLifetime(price); //save the new lifetime total to the customer's account
                
                //Checking if they qualify to become reward member
                double newDiscount = 0;
                for(int i = 0; i < DISCOUNT_TABLE.length; i++){ //cycle the reward table to check discount eligibility
                    if(customerList[foundIndex].getLifetime() >= DISCOUNT_TABLE[i][0])
                            newDiscount = DISCOUNT_TABLE[i][1];
                    }
                if(newDiscount > 0){ //if they qualified for a discount, they are a new reward member
                    //promote the customer to preferred status, adding them to the array
                    preferredList = promoteCustomer((int)foundIndex, newDiscount, customerList, preferredList);
                    //remove the customer from regular customer array and shrink array
                    customerList = removeCustomer((int)foundIndex, customerList);
                }
            }
            
        } //bottom of Processing Orders loop
        scanOrders.close(); //close scanner
//==============================================================================
//                      Writing Back to Database Files
//==============================================================================
        //Create printwriter objects to record changes back to respective files
        PrintWriter writeCustomers = new PrintWriter(new File(CUSTOMER_FILE));
        PrintWriter writePreferred = new PrintWriter(new File(PREFERRED_FILE));
        
        //Call polymorphic export methods to write back to files
        exportCustomers(customerList, writeCustomers);
        exportCustomers(preferredList, writePreferred);
        //Close all printwriters
        writeCustomers.close();
        writePreferred.close();
        //end program
        endProgram();
    }
    
//==============================================================================
//                              Methods
//==============================================================================    
    
    //=== Print Method ===
    //Not used in this program, but useful when debugging issues
    //Prints any Customer list and lists with derived classes with printDetails overridden
    static void printList(Customer[] myList){
        for(Customer i: myList){
            System.out.printf("%s\n", i.printDetails());
        }
    }
    
    //=== Export Customer Files ===
    //Exports any Customer list and lists with derived classes with printDetails overridden
    static void exportCustomers(Customer[] mylist, PrintWriter printer){
        for(int i = 0; i < mylist.length; i++)
            printer.println(mylist[i].printDetails());
    }
    
    //=== Count Number or Lines in File ===
    //Count file lines to determine to large Customer arrays need to be
    //Returns size of array needed
    static int getArrayRows(BufferedReader br) throws IOException{
        
        int counter = 0;
        //reads each line to count number of lines
        while(br.readLine()!= null){
            counter++;
        }
        br.close();
        return counter;
    }
    
    //=== Fill Regular Customer Array ===
    //Because of varying size and elements, this function is only used for filling
    //regular customer arrays
    static void fillCustomerArray(Customer[] array, BufferedReader br) throws IOException{
        for(int i = 0; i < array.length; i++){
            String[] tokens = br.readLine().split(" ");
            array[i] = new Customer(Integer.parseInt(tokens[0]), tokens[1], tokens[2], Double.parseDouble(tokens[3]));
        }
        br.close();
    }
    
    //=== Fill Preferred Customer Array ===
    //Because of varying size and elements, this function is only used for filling
    //preferred cusotmer arrays
    static void fillPreferredArray(Preferred[] array, BufferedReader br) throws IOException{
        for(int i = 0; i < array.length; i++){
            String[] tokens = br.readLine().split(" ");
            tokens[4] = tokens[4].replace("%", "");
            array[i] = new Preferred(Integer.parseInt(tokens[0]), tokens[1], tokens[2], Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4])/100.0);
        }
        br.close();
    }
    
    //=== Lookup Customer ID ===
    //Another polymorphic call.  Call check Customer ID in any Customer or 
    //Customer-derived class array
    static int lookupCustomer(double guestID, Customer[] myList){
        int index = -1;
        for(int i = 0; i < myList.length && index == -1; i++){
            if(guestID == myList[i].getGuestID())
                index = i;
        }
        return index;    
    }
    
    //=== Create New Default Customer ===
    //In cases where the transaction calls an ID that does not exist in the 
    //system, this method will create a default name and store the transaction
    //with the current ID for future customer update to the customer details.
    static Customer[] createCustomer(double guestID, Customer[] customerList){
        //Create new customer list, one size larger
        Customer[] newCustomerList = new Customer[customerList.length+1];
        for(int i = 0; i < customerList.length; i++) //copy database to new array
            newCustomerList[i] = customerList[i];
        newCustomerList[newCustomerList.length-1] = new Customer(guestID); //add new customer to the end
        
        return newCustomerList; //return reference to the new array
    }
    
    //=== Promote Customer from Customer to Preferred Customer Status ===
    //This method copies a Customer over to Preferred member status.  Note that
    //this method must always be used followed with removeCustomer to take the
    //specific customer out of the regular customer array
    static Preferred[] promoteCustomer(int index, double discount, Customer[] customerList, Preferred[] preferredList){
        //Create new preferred list, one size larger
        Preferred[] newPreferredList = new Preferred[preferredList.length+1];
        for(int i = 0; i < preferredList.length; i++) //copy database to new array
            newPreferredList[i] = preferredList[i];
        //add new preferred member to the end with new discount applied to future purchases
        newPreferredList[newPreferredList.length-1] = new Preferred(customerList[index], discount);
        
        return newPreferredList; //return reference to the new array
    }
    
    //=== Remove Customer from Customer Array and Shrink Array Size ===
    //This method removes a Customer from the Customer list. The index is required.
    static Customer[] removeCustomer(int index, Customer[] customerList){
        //Create new Customer list, one size smaller
        Customer[] newCustomerList = new Customer[customerList.length-1];
        int j = 0;
        //Loop through both arrays, i for indexing the old array, j for indexing the new one
        for(int i = 0; i < customerList.length && j < newCustomerList.length; i++){
            //if i is not the index of the removed customer
            if(i != index){
                newCustomerList[j] = customerList[i]; //copy the customer over
                j++; //increment the new array index
            }
        }   //else, the new array is not incremented and only i is incremented, effectively skipping that slot
        return newCustomerList;  //return the reference to the new array
    }

    //=== End Program ===
    //Prints final messages to the console and closes the program successfully
    public static void endProgram()
    {        
        System.out.println("\n\n====================================================\n\n");
        System.out.println("All transactions completed!\n");        
        System.out.println("Thank you for visiting!\n\n");
        System.out.println("For all questions, please contact me:");
        System.out.println("Jimmy Nguyen");
        System.out.println("Jimmy@JimmyWorks.net\n\n");
        System.out.println("====================================================");
        
        System.exit(0);  //closes program... all io streams closed prior to 
                         //executing this function
    } 
    
}
