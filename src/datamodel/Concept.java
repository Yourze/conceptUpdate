package datamodel;

import java.util.Arrays;

/**
 * Concept. With a set of users and a set of items.
 *
 * @author minfanphd 2018/11/27.
 *
 */
public class Concept {
    /**
     * Users in this concept. A compressed int array. It is in ascendant order.
     */
    int[] users;

    /**
     * Items in this concept. A compressed int array.  It is in ascendant order.
     */
    int[] items;

    /**
     **************
     * The first constructor
     *
     * @param paraUsers
     *            The given users. The reference is copied directly without
     *            allocating new space.
     * @param paraItems
     *            The given items.
     **************
     */
    public Concept(int[] paraUsers, int[] paraItems) {
        users = paraUsers;
        items = paraItems;
    }// Of the first constructor

    /**
     **************
     * The second constructor. Copy an existing concept and allocate new space.
     *
     * @param paraConcept
     *            An existing concept.
     **************
     */
    public Concept(Concept paraConcept) {
        users = new int[paraConcept.users.length];
        for (int i = 0; i < users.length; i++) {
            users[i] = paraConcept.users[i];
        }// Of for i

        items = new int[paraConcept.items.length];
        for (int i = 0; i < items.length; i++) {
            items[i] = paraConcept.items[i];
        }// Of for i
    }// Of the first constructor

    /**
     **************
     * The third constructor
     *
     * @param paraUsers
     *            The given users. The reference is copied directly without
     *            allocating new space.
     * @param paraItems
     *            The given items.
     **************
     */
    public Concept(int paraUser, boolean[] paraItems) {
        users = new int [1];
        users[0] = paraUser;
        int[] tempItems = new int [paraItems.length ];
        int tempCount = 0 ;
        for(int i = 0 ; i < paraItems.length ; i++){
            if(paraItems[i]){
                tempItems[tempCount] = i;
                tempCount++;
            }// Of if
        }// Of for i
        items = new int[tempCount];
        for(int i = 0 ; i < tempCount ; i++){
            items[i] = tempItems[i];
        }// Of for i
    }// Of the first constructor

    /**
     **************
     * Get the users of this concept.
     **************
     */
    public int[] getUsers() {
        return users;
    }//Of getUsers

    /**
     **************
     * Does it contain the specified user.
     * @param paraUser The specified user.
     **************
     */
    public boolean containsUser(int paraUser) {
        System.out.println("Does " + this + " contains " + paraUser + "?");
        for (int i = 0; i < users.length; i++) {
            if (users[i] == paraUser) {
                //System.out.println("Yes.");
                return true;
            } else if (users[i] > paraUser) {
                //System.out.println("No 1.");
                return false;
            }//Of if
        }//Of for i

        //System.out.println("No 2.");
        return false;
    }//Of containsUser



    /**
     **************
     * toString.
     **************
     */
    public String toString() {
        String resultString = "(";
        resultString += Arrays.toString(users);
        resultString += ",";
        resultString += Arrays.toString(items);
        resultString += ")";

        return resultString;
    }// Of toString

    /**
     **************
     * Test the class.
     **************
     */
    public static void main(String args[]) {
        int[] tempUsers = { 1, 5 };
        int[] tempItems = { 3, 4 };

        Concept tempConcept = new Concept(tempUsers, tempItems);

        System.out.println("The concept is: " + tempConcept);
    }// Of main

}// Of class Concept
