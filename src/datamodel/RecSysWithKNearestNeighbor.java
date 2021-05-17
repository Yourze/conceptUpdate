package datamodel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Calendar;

public class RecSysWithKNearestNeighbor {

    /**
     * The formal context from data file.
     */
    RatingMatrix formalContext;

    /**
     * The number of neighbors.
     */
    int k;

    /**
     * The voting threshold for the neighbors.
     */
    double votingThreshold;

    public RecSysWithKNearestNeighbor() {
        // TODO Auto-generated constructor stub
        formalContext = null;
        k = 0;
    }// End constructor

    public RecSysWithKNearestNeighbor(RatingMatrix paraFormalContext) {
        // TODO Auto-generated constructor stub
        formalContext = paraFormalContext;
    }// End constructor

    public RecSysWithKNearestNeighbor(int paraK) {
        // TODO Auto-generated constructor stub
        k = paraK;
    }// End constructor

    public RecSysWithKNearestNeighbor(RatingMatrix paraFormalContext, int paraK) {
        // TODO Auto-generated constructor stub
        formalContext = paraFormalContext;
        k = paraK;
    }// End constructor

    /***************************************
     * Set the formal context.
     *
     * @param formalContext
     ***************************************
     */
    public void setFormalContext(RatingMatrix formalContext) {
        this.formalContext = formalContext;
    }// End setter

    /***************************************
     * Set the number of neighbor.
     *
     * @param k
     ***************************************
     */
    public void setK(int k) {
        this.k = k;
    }// End setter

    /***************************************
     * Set the voting thresold.
     *
     * @param votingThreshold
     ***************************************
     */
    public void setVotingThreshold(double votingThreshold) {
        this.votingThreshold = votingThreshold;
    }// End setter

    /***************************************
     * Obtain the k nearest neighbors for the given object.
     *
     * @param paraObjectIndex
     * @return
     ***************************************
     */
    LinkedListSortedNeighborSet obtainKNeighbors(int paraObjectIndex) {
        LinkedListSortedNeighborSet tempNeighborSet = new LinkedListSortedNeighborSet();
        tempNeighborSet.setNeighborThreshold(k);

        for (int i = 0; i < formalContext.trainingFormalContext.length; i++) {
//			System.out.println("u: " + paraObjectIndex);
            if (i != paraObjectIndex) {
//				System.out.println("v: " + i);
                Measures tempSimilarityMetric = new Measures(formalContext.trainingFormalContext[paraObjectIndex],
                        formalContext.trainingFormalContext[i]);
                double tempSimilarity = tempSimilarityMetric.jaccardSimilarity();
//				System.out.println("Similarity: " + tempSimilarity);
                tempNeighborSet.topKSortedInsert(i, tempSimilarity);
//				System.out.println("" + tempNeighborSet.toString());
            } // End if
        } // End for
        return tempNeighborSet;
    }// End function

    /***************************************
     * Predict for the given user with the given item index.
     *
     * @param paraObjectIndex
     * @return
     ***************************************
     */
    int[] predictForAllItems(int paraObjectIndex) {
//		System.out.println("1.2");
        LinkedListSortedNeighborSet tempNeighborSet = obtainKNeighbors(paraObjectIndex);
        int[] tempPredictionArray = new int[formalContext.trainingFormalContext[0].length];
        for (int i = 0; i < formalContext.trainingFormalContext[0].length; i++) {

            if (formalContext.trainingFormalContext[paraObjectIndex][i] > 0) {
                continue;
            } // End if
//			System.out.println("i: " + i);
            int tempRatedCount = 0;
            tempNeighborSet.resetCurrentNode();
            while ((tempNeighborSet.currentNeighbor != null) && (tempNeighborSet.currentNeighbor.neighborIndex != -1)) {
//				System.out.println("neighbor: " + tempNeighborSet.currentNeighbor.neighborIndex);
                if (formalContext.trainingFormalContext[tempNeighborSet.currentNeighbor.neighborIndex][i] > 0) {
                    tempRatedCount++;
                } // End if
                tempNeighborSet.currentNeighbor = tempNeighborSet.currentNeighbor.nextNeighbor;
            } // End while

            double tempVotingRatio = (tempRatedCount + 0.0) / tempNeighborSet.actualNeighborCount;
            if (tempVotingRatio >= votingThreshold) {
                tempPredictionArray[i] = 1;
            } // End if

        } // End for i

        return tempPredictionArray;
    }// End function predictForOneItem

    /***************************************
     * Obtain the prediction matrix.
     *
     * @return
     ***************************************
     */
    int[][] predictionForAllUsers() {
        int[][] tempPredictionMatrix = new int[formalContext.trainingFormalContext.length][];
        for (int i = 0; i < formalContext.trainingFormalContext.length; i++) {
//			System.out.println("1.1");
            tempPredictionMatrix[i] = predictForAllItems(i);
        } // End for i
        return tempPredictionMatrix;
    }// End function predictionForAllUsers

    /*************************************
     * Compute the precision of prediction matrix.
     *
     * @param paraPredicionMatrix
     * @return
     *************************************
     */
    double computePrecision(int[][] paraPredicionMatrix) {
        double tempPrecision = 0.0;
        int tempTotal = 0;
        int temptempPrecisionCount = 0;
        for (int i = 0; i < paraPredicionMatrix.length; i++) {
            for (int j = 0; j < paraPredicionMatrix[i].length; j++) {
                if (paraPredicionMatrix[i][j] == 1) {
                    tempTotal++;
                    if (formalContext.testingFormalContext[i][j] > 0) {
                        temptempPrecisionCount++;
                    } // End if
                } // End if
            } // End for j
        } // End for i
        System.out.println("temptempPrecisionCount: " + temptempPrecisionCount);
        System.out.println("tempTotal: " + tempTotal);
        if (temptempPrecisionCount == 0) {
            return 0.0;
        } // End if
        tempPrecision = (temptempPrecisionCount + 0.0) / tempTotal;
        return tempPrecision;
    }// End function computePrecision

    /*************************************
     * Compute the recall of prediction matrix.
     *
     * @param paraPredicionMatrix
     * @return
     *************************************
     */
    double computeRecall(int[][] paraPredicionMatrix) {
        double tempRecall = 0.0;
        int tempTotal = 0;
        int tempAccurate = 0;
        for (int i = 0; i < paraPredicionMatrix.length; i++) {
            for (int j = 0; j < paraPredicionMatrix[i].length; j++) {
                if (formalContext.testingFormalContext[i][j] > 0) {
                    tempTotal++;
                    if (paraPredicionMatrix[i][j] == 1) {
                        tempAccurate++;
                    } // End if
                } // End if
            } // End for j
        } // End for i
        System.out.println("tempAccurate: " + tempAccurate);
        System.out.println("tempTotal: " + tempTotal);
        if (tempAccurate == 0) {
            return 0.0;
        } // End if
        tempRecall = (tempAccurate + 0.0) / tempTotal;
        return tempRecall;
    }// End function computePrecision

    /***************************************
     * The test function of KNN.
     ***************************************
     */
//	public static void test() {
//		for (int i = 100; i < 101; i += 4) {
//			System.out.println("k : " + i);
//			String arffFilename = "src/data/ConceptLatticeData/ratings.arff";
//			FileReader fileReader;
//			try {
//				fileReader = new FileReader(arffFilename);
//				RatingMatrix tempFormalContext = new RatingMatrix(fileReader, 0);
////				int paraUserNumber = 230;
////				int paraItemNumber = 420;
////				int paraMaxSize = 4;
////				tempFormalContext.obtainSamplingFormalContext(paraUserNumber, paraItemNumber, paraMaxSize);
////				System.out.println("Formal Context Sampling Done.");
//
//				double tempRatio = 0.2;
//				tempFormalContext.obtainTrainingAndTestingFormalContext(tempFormalContext.originalFormalContext,
//						tempRatio);
//
//				RecSysWithKNearestNeighbor tempKNN = new RecSysWithKNearestNeighbor(tempFormalContext);
//				tempKNN.setK(i);
//				tempKNN.setVotingThreshold(0.5);
//				int[][] predictionMatrix = tempKNN.predictionForAllUsers();
//
//				double precision = tempKNN.computePrecision(predictionMatrix);
//				double recall = tempKNN.computeRecall(predictionMatrix);
//				System.out.println("Precision: " + precision);
//				System.out.println("Recall: " + recall);
//				System.out.println("F1: " + 2 * precision * recall / (precision + recall));
//
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} // End try
//		}
//
//	}// End test

    /***************************************
     * The test function of KNN.
     ***************************************
     */
    public static void test2() {
//		String tempTrainingURL = "src/knndata/jester-sd2-300X100-train.txt";
//		String tempTestingURL = "src/knndata/jester-sd2-300X100-test.txt";
//		RatingMatrix tempFormalContext = new RatingMatrix(tempTrainingURL, tempTestingURL);
        RatingMatrix tempTrainRatingMatrix = new RatingMatrix("src/data/training.arff", 943, 1682);
        RatingMatrix tempTestRatingMatrix = new RatingMatrix("src/data/testing.arff", 943, 1682);
        RatingMatrix tempFormalContext = new RatingMatrix(tempTrainRatingMatrix.formalContext, tempTestRatingMatrix.formalContext);
        System.out.println("Step 1.0");
        RecSysWithKNearestNeighbor tempKNN = new RecSysWithKNearestNeighbor(tempFormalContext);
        tempKNN.setK(3);
        tempKNN.setVotingThreshold(0.5);
        int[][] predictionMatrix = tempKNN.predictionForAllUsers();

        System.out.println("Step 2.0");
        double precision = tempKNN.computePrecision(predictionMatrix);
        double recall = tempKNN.computeRecall(predictionMatrix);
        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("F1: " + 2 * precision * recall / (precision + recall));
    }// End test2

    /***************************************
     * The main function for all tests.
     *
     * @param args
     ***************************************
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        test2();
    }// End main

}// End class
