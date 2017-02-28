/*
 * Project: Project 2 - Disneyland Dining Rewards
 * Author: Jimmy Nguyen
 * Contact me: Jimmy@JimmyWorks.net
 */
package cs2336_project2.Customer;

/**
 *
 * @author Jimmy
 */
public class Customer {
    protected int guestID; //stores unique ID number for customer
    protected String firstName; //stores customer's first name
    protected String lastName; //stores customer's last name
    protected double totalSpent; //running total spent by customer
    
    //=== Overloaded Constructors for Customers ===
    //Orders received without a valid entry in both lists will be created with
    //a new account and default name "New Account", allowing customers to keep
    //track of purchases and account details updated at a later date.
    public Customer(int guestID){
        this.guestID = guestID;
        this.firstName = "New";
        this.lastName = "Account";
        this.totalSpent = 0;
    }
    //Same as above constructor, allowing guest ID to pass as a double
    public Customer(double guestID){
        this.guestID = (int)guestID;
        this.firstName = "New";
        this.lastName = "Account";
        this.totalSpent = 0;
    }
    //Fully loaded constructor for Customer (Preferred method of creating Customer)
    public Customer(int guestID, String firstName, String lastName, double totalSpent){
        this.guestID = guestID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.totalSpent = totalSpent;
    }
    //Constructor without the running total spent specified (defaulted to zero)
    public Customer(int guestID, String firstName, String lastName){
        this.guestID = guestID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.totalSpent = 0;
    }
    //Accessor for first name
    public String getFirstName(){
        return firstName;
    }
    //Accessor for last name
    public String getLastName(){
        return lastName;
    }
    //Accessor for Guest ID
    public int getGuestID(){
        return guestID;
    }  
    //Accessor for Running Total Spent
    public double getLifetime(){
        return totalSpent;
    }
    //Mutator for first name
    public void setFirstName(String newName){
        firstName = newName;
    }
    //Mutator for last name
    public void setLastName(String newName){
        lastName = newName;
    }
    //Mutator for Guest ID
    public void setGuestID(int newID){
        guestID = newID;
    }  
    //Mutator for Running Total Spent
    public void setLifetime(double fixTotal){
        totalSpent = fixTotal;
    }   
    //Overridden printDetails function returning a string to either print to console or print to file
    public String printDetails(){
        String details = String.format("%d %s %s %.2f", guestID, firstName, lastName, totalSpent);
        return details;
    }

}
