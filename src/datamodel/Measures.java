package datamodel;

import java.util.Arrays;

public class Measures {

    /**
     * The first rating vector.
     */
    int[] firstRatingVector;
    /**
     * The first number.
     */
    int firstNumber;
    /**
     * The second rating vector.
     */
    int[] secondRatingVector;

    /**
     * The first constructor.
     * @param para1stRatingVector
     * @param para2ndRatingVector
     */
    public Measures(int[] para1stRatingVector, int[] para2ndRatingVector) {
        // TODO Auto-generated constructor stub
//		System.out.println("" + Arrays.toString(para1stRatingVector));
//		System.out.println("" + Arrays.toString(para2ndRatingVector));

        firstRatingVector = para1stRatingVector;
        secondRatingVector = para2ndRatingVector;
    }// End constructor.


    public Measures(int para1stRatingNumber, int[] para2ndRatingVector) {
        firstNumber = para1stRatingNumber;
        secondRatingVector = para2ndRatingVector;
    }//End constructor.
    /**************************************
     * Obtain the intersection of two sets.
     *
     * @return
     **************************************
     */
    int intersect() {

        int tempCount = 0;
        for (int i = 0; i < firstRatingVector.length; i++) {
            if (firstRatingVector[i] > 0 && secondRatingVector[i] > 0) {
                tempCount++;
            } // End if
        } // End for i

        return tempCount;
    }// End function intersect

    /**************************************
     * Obtain the union of two sets.
     *
     * @return
     **************************************
     */
    int unions() {
        int tempCountInterset = intersect();
        int tempCountFirst = 0;
        int tempCountSecond = 0;
        for(int i = 0; i < firstRatingVector.length;i++) {
            if(firstRatingVector[i] > 0) {
                tempCountFirst++;
            }// End if
            if(secondRatingVector[i] > 0) {
                tempCountSecond ++;
            }// End if
        }// End for i
        return tempCountFirst + tempCountSecond - tempCountInterset;
    }// End function intersect

    /**************************************
     * Compute the Jaccard similarity of two rating vector.
     *
     * @return
     **************************************
     */
    double jaccardSimilarity() {
        int tempIntersetion = intersect();
        int tempUnion = unions();
        double tempJaccardSim = (tempIntersetion + 0.0) / tempUnion;
        return tempJaccardSim;
    }// End function jaccardSimilarity

    /**************************************
     * Compute the Cosine similarity of two rating vector.
     *
     * @return
     **************************************
     */
    double cosineSimilarity() {
        int tempInnerProduct = 0;
        for (int i = 0; i < firstRatingVector.length; i++) {
            tempInnerProduct += firstRatingVector[i] * secondRatingVector[i];
        } // End for i

        int tempFirstBase = 0;
        int tempSecondBase = 0;
        for (int i = 0; i < firstRatingVector.length; i++) {
            tempFirstBase += firstRatingVector[i] * firstRatingVector[i];
            tempSecondBase += secondRatingVector[i] * secondRatingVector[i];
        } // End for i

        double tempSqrtFirstBase = Math.sqrt(tempFirstBase);
        double tempSqrtSecondBase = Math.sqrt(tempSecondBase);
        double tempCosineSim = (tempInnerProduct + 0.0) / (tempSqrtFirstBase * tempSqrtSecondBase);

        return tempCosineSim;
    }// End function cosineSimilarity

    /**************************************
     * Compute the Triangle similarity of two rating vector.
     *
     * @return
     **************************************
     */
    double triangleSimilarity() {
        // The bases of two rating vectors.
        int tempFirstBase = 0;
        int tempSecondBase = 0;
        for (int i = 0; i < firstRatingVector.length; i++) {
            tempFirstBase += firstRatingVector[i] * firstRatingVector[i];
            tempSecondBase += secondRatingVector[i] * secondRatingVector[i];
        } // End for i

        double tempSqrtFirstBase = Math.sqrt(tempFirstBase);
        double tempSqrtSecondBase = Math.sqrt(tempSecondBase);

        // The Euclid distance between two vectors.
        int tempDifference = 0;

        for (int i = 0; i < firstRatingVector.length; i++) {
            tempDifference += (firstRatingVector[i] - secondRatingVector[i])
                    * (firstRatingVector[i] - secondRatingVector[i]);
        } // End for i
        double tempSqrtDifference = Math.sqrt(tempDifference);
        double tempTriangleSim = 1.0 - tempSqrtDifference / (tempSqrtFirstBase + tempSqrtSecondBase);
        return tempTriangleSim;
    }// End function triangleSimilarity

    /**************************************
     * Compute the Manhattan similarity of two rating vector.
     *
     * @return
     **************************************
     */
    double manhattanSimilarity() {
        // The bases of two rating vectors.
        int tempFirstBase = 0;
        int tempSecondBase = 0;
        for (int i = 0; i < firstRatingVector.length; i++) {
            tempFirstBase += firstRatingVector[i] * firstRatingVector[i];
            tempSecondBase += secondRatingVector[i] * secondRatingVector[i];
        } // End for i

        double tempSqrtFirstBase = Math.sqrt(tempFirstBase);
        double tempSqrtSecondBase = Math.sqrt(tempSecondBase);

        // The Euclid distance between two vectors.
        int tempDifference = 0;

        for (int i = 0; i < firstRatingVector.length; i++) {
            tempDifference += (firstRatingVector[i] - secondRatingVector[i])
                    * (firstRatingVector[i] - secondRatingVector[i]);
        } // End for i
        double tempSqrtDifference = Math.sqrt(tempDifference);
        double tempTriangleSim = 1.0 - tempSqrtDifference / (tempSqrtFirstBase + tempSqrtSecondBase);
        return tempTriangleSim;
    }// End function triangleSimilarity

    /**************************************
     * Compute the integrated similarity of two rating vector with Triangle and
     * Jaccard.
     *
     * @return
     **************************************
     */
    double integratedTriangleandJaccard() {
        double tempJaccardSim = jaccardSimilarity();
        double tempTriangleSim = triangleSimilarity();
        double tempIntegratedTriangleandJaccardSim = tempTriangleSim * tempJaccardSim;

        return tempIntegratedTriangleandJaccardSim;
    }// End function integratedTriangleandJaccard

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        int[] temp1stRatingVector = new int[] { 1, 2, 3, 4 };
        int[] temp2ndRatingVector = new int[] { 2, 4, 6, 8 };
        int[] temp3rdRatingVector = new int[] { 1, 2, 3, 4 };
        int[] temp4thRatingVector = new int[] { 4, 3, 2, 1 };

        Measures tempMeasureTest = new Measures(temp1stRatingVector, temp2ndRatingVector);
        System.out.println("Jaccard: " + tempMeasureTest.jaccardSimilarity());
        System.out.println("Cosine: " + tempMeasureTest.cosineSimilarity());
        System.out.println("Triangle: " + tempMeasureTest.triangleSimilarity());
        System.out.println("Integration: " + tempMeasureTest.integratedTriangleandJaccard());
        System.out.println("---------------------------------------");

        tempMeasureTest = new Measures(temp1stRatingVector, temp3rdRatingVector);
        System.out.println("Jaccard: " + tempMeasureTest.jaccardSimilarity());
        System.out.println("Cosine: " + tempMeasureTest.cosineSimilarity());
        System.out.println("Triangle: " + tempMeasureTest.triangleSimilarity());
        System.out.println("Integration: " + tempMeasureTest.integratedTriangleandJaccard());
        System.out.println("---------------------------------------");

        tempMeasureTest = new Measures(temp1stRatingVector, temp4thRatingVector);
        System.out.println("Jaccard: " + tempMeasureTest.jaccardSimilarity());
        System.out.println("Cosine: " + tempMeasureTest.cosineSimilarity());
        System.out.println("Triangle: " + tempMeasureTest.triangleSimilarity());
        System.out.println("Integration: " + tempMeasureTest.integratedTriangleandJaccard());

    }// End main

}// End Class Measures
