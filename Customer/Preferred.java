/*
 * Project: Project 2 - Disneyland Dining Rewards
 * Author: Jimmy Nguyen
 * Contact me: Jimmy@JimmyWorks.net
 */
package cs2336_project2.Customer;


public class Preferred extends Customer{
    
    protected double discount; //discount applied towards new purchases
    
    //=== Overloaded Constructors ===
    //Constructor for Preferred customer with all variables passed in
    public Preferred(int guestID, String firstName, String lastName, double totalSpent, double discount){
        super(guestID, firstName, lastName, totalSpent); //calls the base class constructor
        this.discount = discount; //sets discount variable specific to preferred members
    }
    
    //Constructor specifically for promoting a Customer from regular status to Preferred status
    //Take the Customer object as an argument along with the new discount 
    public Preferred(Customer newMember, double discount){
        super(newMember.getGuestID(), newMember.getFirstName(), newMember.getLastName(), newMember.getLifetime());
        this.discount = discount;
    }
    //Accessor for discount
    public double getDiscount(){
        return discount;
    }
    //Mutator for discount
    public void setDiscount(double discount){
        this.discount = discount;
    }
    //Overridden printDetails function returning a string to either print to console or print to file
    @Override
    public String printDetails(){
        String details = String.format("%d %s %s %.2f %.0f%%", guestID, firstName, lastName, totalSpent, discount*100);
        return details;
    }
    
}
