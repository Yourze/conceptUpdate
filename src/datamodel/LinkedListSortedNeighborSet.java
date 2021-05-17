package datamodel;

import java.text.DecimalFormat;

public class LinkedListSortedNeighborSet {

    /**
     * The actual number of neighbors.
     */
    int actualNeighborCount;

    /**
     * The threshold of neighbors.
     */
    int neighborThreshold;

    /**
     * The first neighbor.
     */
    NeighborNode headNeighbor;

    /**
     * The current neighbor.
     */
    NeighborNode currentNeighbor;

    /**
     * The last neighbor.
     */
    NeighborNode tailNeighbor;

    public LinkedListSortedNeighborSet() {
        // TODO Auto-generated constructor stub
        actualNeighborCount = 0;
        headNeighbor = new NeighborNode();
        currentNeighbor = headNeighbor;
        tailNeighbor = headNeighbor;
    }// End constructor 1

    public LinkedListSortedNeighborSet(int paraObject, double paraSimilarity) {
        // TODO Auto-generated constructor stub
        actualNeighborCount = 1;
        headNeighbor = new NeighborNode(paraObject, paraSimilarity);
        currentNeighbor = headNeighbor;
        tailNeighbor = headNeighbor;
    }// End constructor 2

    public LinkedListSortedNeighborSet(int[] paraObjectSet, double[] paraSimilaritySet) {
        // TODO Auto-generated constructor stub
        for (int i = 0; i < paraObjectSet.length; i++) {
            append(paraObjectSet[i], paraSimilaritySet[i]);
        } // End for i
    }// End constructor 3

    /****************************************
     * Set the threshold of the neighbor number.
     *
     * @param neighborThreshold
     ****************************************
     */
    public void setNeighborThreshold(int neighborThreshold) {
        this.neighborThreshold = neighborThreshold;
    }// End setter setNeighborThreshold

    /****************************************
     * Reset the position of the current neighbor node.
     ****************************************
     */
    void resetCurrentNode() {
        currentNeighbor = headNeighbor;
    }// End function resetCurrentNode

    /****************************************
     * Append the given object into tail.
     *
     * @return
     ****************************************
     */
    void append(int paraObject, double paraSimilarity) {
        // Is headNeighbor null?
        if (headNeighbor == null) {
            headNeighbor = new NeighborNode(paraObject, paraSimilarity);
            currentNeighbor = headNeighbor;
            tailNeighbor = headNeighbor;
        } else {
            // Not null.
            NeighborNode tempNewNode = new NeighborNode(paraObject, paraSimilarity);
            tailNeighbor.nextNeighbor = tempNewNode;
            tailNeighbor = tailNeighbor.nextNeighbor;
        } // End if

        actualNeighborCount++;
    }// End function append

    /****************************************
     * Insert the new neighbor into the current set.
     *
     * @param paraObject
     * @param paraSimilarity
     ****************************************
     */
    void sortedInsert(int paraObject, double paraSimilarity) {
        NeighborNode tempNewNode = new NeighborNode(paraObject, paraSimilarity);
        resetCurrentNode();
        // Is the new head node?
        if (tempNewNode.similarity > currentNeighbor.similarity) {
            tempNewNode.nextNeighbor = currentNeighbor;
            headNeighbor = tempNewNode;
            currentNeighbor = headNeighbor;
            actualNeighborCount++;
            return;
        } else {
            while (currentNeighbor.nextNeighbor != null) {

                if (tempNewNode.similarity > currentNeighbor.nextNeighbor.similarity) {
                    // Insert the new node after the current node.
                    tempNewNode.nextNeighbor = currentNeighbor.nextNeighbor;
                    currentNeighbor.nextNeighbor = tempNewNode;
                    actualNeighborCount++;
                    return;
                } // End if

                currentNeighbor = currentNeighbor.nextNeighbor;
            } // End while

            // Insert it into the tail.
            tailNeighbor.nextNeighbor = tempNewNode;
            tailNeighbor = tailNeighbor.nextNeighbor;
            actualNeighborCount++;
        } // End if
    }// End function sortedInsert

    /****************************************
     * Sorted insert the given neighbor and keep neighbor number within threshold k.
     *
     * @param paraObject
     * @param paraSimilarity
     ****************************************
     */
    void topKSortedInsert(int paraObject, double paraSimilarity) {
        if (actualNeighborCount < neighborThreshold) {
            // Insert straightforward.
            sortedInsert(paraObject, paraSimilarity);
        } else {
            if (tailNeighbor.similarity > paraSimilarity) {
                return;
            } else {
                // Insert it and delete the tail node.
                // Insert.
                sortedInsert(paraObject, paraSimilarity);
                // Delete.
                retainTopKNeighbors();
            } // End if
        } // End if
    }// End function topKSortedInsert

    /****************************************
     * Keep the top-k similar neighbors.
     *
     * @param paraK
     ****************************************
     */
    void retainTopKNeighbors() {
        if (actualNeighborCount <= neighborThreshold) {
            return;
        } // End if

        resetCurrentNode();
        for (int i = 0; i < neighborThreshold - 1; i++) {
            currentNeighbor = currentNeighbor.nextNeighbor;
        } // End for i
        tailNeighbor = currentNeighbor;
        tailNeighbor.nextNeighbor = null;
        actualNeighborCount = neighborThreshold;
    }// End function retainTopKNeighbors

    /****************************************
     * Return the string result of the neighbor set.
     ****************************************
     */
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.000");
        String tempResult = actualNeighborCount + ": {";
        resetCurrentNode();
        while (currentNeighbor.nextNeighbor != null) {
            tempResult += "(" + currentNeighbor.neighborIndex + ", ";
            tempResult += df.format(currentNeighbor.similarity) + "), ";
            currentNeighbor = currentNeighbor.nextNeighbor;
        } // End while
        tempResult += "(" + currentNeighbor.neighborIndex + ", ";
        tempResult += df.format(currentNeighbor.similarity) + ")";
        tempResult += "}" + "\r\n";
        return tempResult;
    }// End function toString

    /****************************************
     * The neighbor node.
     *
     * @author Administrator
     ****************************************
     */
    class NeighborNode {
        int neighborIndex;
        double similarity;
        NeighborNode nextNeighbor;

        NeighborNode() {
            neighborIndex = -1;
            similarity = 0.0;
            nextNeighbor = null;
        }// End constructor

        NeighborNode(int paraNeighborIndex, double paraSimilarity) {
            neighborIndex = paraNeighborIndex;
            similarity = paraSimilarity;
            nextNeighbor = null;
        }// End constructor
    }// End class

    /****************************************
     * The test of testClass
     ****************************************
     */
    public static void testClass() {
        int tempK = 2;

        int n1 = 1;
        double s1 = 0.5;
        LinkedListSortedNeighborSet tempNode1 = new LinkedListSortedNeighborSet(n1, s1);
        tempNode1.setNeighborThreshold(tempK);
        System.out.println(tempNode1.toString());
        System.out.println("Tail Node: " + tempNode1.tailNeighbor.neighborIndex);

        int n2 = 2;
        double s2 = 0.6;
        tempNode1.sortedInsert(n2, s2);
        System.out.println(tempNode1.toString());
        System.out.println("Tail Node: " + tempNode1.tailNeighbor.neighborIndex);

        int n3 = 3;
        double s3 = 0.55;
        tempNode1.sortedInsert(n3, s3);
        System.out.println(tempNode1.toString());
        System.out.println("Tail Node: " + tempNode1.tailNeighbor.neighborIndex);

        int n4 = 4;
        double s4 = 0.7;
        tempNode1.sortedInsert(n4, s4);
        System.out.println(tempNode1.toString());
        System.out.println("Tail Node: " + tempNode1.tailNeighbor.neighborIndex);

        tempNode1.retainTopKNeighbors();
        System.out.println(tempNode1.toString());
        System.out.println("Tail Node: " + tempNode1.tailNeighbor.neighborIndex);
        System.out.println("---------------------------");

        LinkedListSortedNeighborSet tempNode2 = new LinkedListSortedNeighborSet(n1, s1);
        tempNode2.setNeighborThreshold(tempK);
        tempNode2.topKSortedInsert(n2, s2);
        System.out.println(tempNode2.toString());
        System.out.println("Tail Node: " + tempNode2.tailNeighbor.neighborIndex);
        tempNode2.topKSortedInsert(n3, s3);
        System.out.println(tempNode2.toString());
        System.out.println("Tail Node: " + tempNode2.tailNeighbor.neighborIndex);
        tempNode2.topKSortedInsert(n4, s4);
        System.out.println(tempNode2.toString());
        System.out.println("Tail Node: " + tempNode2.tailNeighbor.neighborIndex);

    }// End function testClass

    /****************************************
     * The main function of all tests.
     *
     * @param args
     ****************************************
     */
    public static void main(String[] args) {
        testClass();
    }// End main

}// End class
