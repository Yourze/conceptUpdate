package datamodel;

import java.io.BufferedReader;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import common.*;

import weka.core.Instances;
import datamodel.DataProcessing.*;

/**
 * The rating matrix.
 *
 * @author minfanphd 2018/11/27.
 */
public class RatingMatrix {
    public RatingMatrix() {

    }
//    boolean[] isHaveFilm;
    /**
     * The sampling formal context.
     */
    int[][] samplingFormalContext;
    /**
     * The formal context.形式背景 二值 0/1
     */
    int[][] formalContext;

    /**
     * The rating matrix.
     */
    int[][] ratingMatrix;

    /**
     * Can the instance be a representative.
     */
    int[] candidateRepresentatives;

    /**
     * The training formal context.用于训练的形式背景
     */
    int[][] trainingFormalContext;

    /**
     * The testing formal context.用于测试的形式背景
     */
    int[][] testingFormalContext;

    /**
     * The fever of movie.
     */
    int[] movieFever;

    /**
     * The dataProcessing object.
     */
    DataProcessing dp;

    /**
     * *************
     * The first constructor
     *
     * @param paraFilename The arff filename.
     *                     *************
     */
    public RatingMatrix(String paraFilename, int paraNumUsers, int paraNumItems) {
        // Read the arff file.
        Instances data = null;
        try {
            FileReader fileReader = new FileReader(paraFilename);
            data = new Instances(fileReader);
            fileReader.close();
        } catch (Exception ee) {
            System.out.println("Cannot read the file: " + paraFilename + "\r\n"
                    + ee);
            System.exit(0);
        } // Of try

        // obtain formal context and rating matrix.获取形式背景和评分矩阵
        formalContext = new int[paraNumUsers][paraNumItems];
        ratingMatrix = new int[paraNumUsers][paraNumItems];
        int tempUser, tempItem;
        for (int i = 0; i < data.numInstances(); i++) {
            tempUser = (int) data.instance(i).value(0);
            tempItem = (int) data.instance(i).value(1);
            formalContext[tempUser][tempItem] = 1;
            ratingMatrix[tempUser][tempItem] = (int) data.instance(i).value(2);
        }// Of for i

        //Initialize
        candidateRepresentatives = new int[paraNumUsers];
        Arrays.fill(candidateRepresentatives, 1);
    }// Of the constructor

    /**
     * *************
     * The second constructor
     *
     * @param paraRatingMatrixFilename The arff filename.
     *                                 *************
     */
    public RatingMatrix(String paraRatingMatrixFilename) {
        int[][] tempRatingMatrix = dp.readFCFromFile(paraRatingMatrixFilename);
        formalContext = new int[tempRatingMatrix.length][tempRatingMatrix[0].length];
        ratingMatrix = new int[tempRatingMatrix.length][tempRatingMatrix[0].length];
        for (int i = 0; i < formalContext.length; i++) {
            for (int j = 0; j < formalContext[0].length; j++) {
                ratingMatrix[i][j] = tempRatingMatrix[i][j];
                if (tempRatingMatrix[i][j] > 0) {
                    formalContext[i][j] = 1;
                } else {
                    formalContext[i][j] = 0;
                }// Of if
            }// Of for j
        }// Of for i
        candidateRepresentatives = new int[tempRatingMatrix.length];
        Arrays.fill(candidateRepresentatives, 1);
    }

    public RatingMatrix(String paraTrainingFilename, String paraTestingFilename) {
        // TODO Auto-generated constructor stub
        samplingFormalContext = null;
        trainingFormalContext = readFCFromFile(paraTrainingFilename);
        testingFormalContext = readFCFromFile(paraTestingFilename);
    }// End constructor 3

    public RatingMatrix(int[][] paraTrainMatrix, int[][] paraTestMatrix) {
        trainingFormalContext = paraTrainMatrix;
        testingFormalContext = paraTestMatrix;
    }
    /**
     * The second constructor
     * @param paraUsers
     * @param paraItems
     * @param paraTxtFilename
     */
//	public RatingMatrix( int paraUsers, int paraItems,String paraTxtFilename){
//		int[][] tempRatingMatrix = readFCFromArrayFile(paraTxtFilename, paraUsers, paraItems);
////		System.out.println("tempRatingMatrix.length :" + tempRatingMatrix.length + " tempRatingMatrix[0].length : " + tempRatingMatrix[0].length);
//		formalContext = new boolean[tempRatingMatrix.length][tempRatingMatrix[0].length];
//		ratingMatrix = new int[tempRatingMatrix.length][tempRatingMatrix[0].length];
//		for (int i = 0; i < formalContext.length; i++) {
//			for(int j = 0 ; j < formalContext[0].length ; j++){
//				ratingMatrix[i][j] = tempRatingMatrix[i][j];
//				if(tempRatingMatrix[i][j] > 0 ){
//					formalContext[i][j] = true;
//				}else{
//					formalContext[i][j] = false;
//				}// Of if
//			}// Of for j
//		}// Of for i
//		candidateRepresentatives = new boolean[tempRatingMatrix.length];
//		Arrays.fill(candidateRepresentatives, true);
//		movieFever = getItemFever();
//	}

    /******************************************
     * obtain formal context from file row by row.
     * @param paraRow
     * @param paraColumn
     * @return
     ******************************************
     */

    int[] obtainFCRow(String paraRow, int paraColumn) {
        int[] tempRow = new int[paraColumn];
        String tempSubString = paraRow.substring(1, paraRow.length() - 1);
        String[] tempAllElement = tempSubString.split(", ");
        for (int i = 0; i < tempAllElement.length; i++) {
            int tempElement = Integer.parseInt(tempAllElement[i]);
            tempRow[i] = tempElement;
        } // Of for i
        return tempRow;
    }// Of function obtainFCRow

    /******************************************
     * Read the formal context from file.
     ******************************************
     */
    public int[][] readFCFromFile(String paraFormalContextURL) {
        try {
            FileReader fr = new FileReader(paraFormalContextURL);
            BufferedReader br = new BufferedReader(fr);
            String str1 = "";
            int row = 200;
            int column = 420;
            int tempI = 0;
            int[][] tempFormalContext = new int[row][];
            while (str1 != null) {
                str1 = br.readLine();
                if (str1 == null) {
                    break;
                } // Of if
                tempFormalContext[tempI] = obtainFCRow(str1, column);
                tempI++;
            } // Of while
            br.close();
            fr.close();
//			boolean[][] tempBooleanFormalContext = new boolean[row][];
//			for (int i = 0; i < tempBooleanFormalContext.length; i++) {
//				for (int j = 0; j < tempBooleanFormalContext[0].length; j++) {
//					if (tempFormalContext[i][j] == 1) {
//						tempBooleanFormalContext[i][j] = true;
//					}
//				}//of for j
//			}//of for i
            return tempFormalContext;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // Of try
        return null;
    }// Of function readCLFromFile

    /**
     * read txt file like arff file.
     *
     * @param
     * @return
     */
    public int[][] readFile(String paraFileURL) {
        File f1 = new File(paraFileURL);
        String[][] rows = new String[4939][3];
        int[][] tempDis = new int[4939][3];
        int index = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f1));
            String str = null;
            // 按行读取
            while ((str = br.readLine()) != null) {
                String str1 = str.substring(0, str.length());
                rows[index] = str1.split(" ");
//				rows[index] = str.split("( )+");
                for (int j = 0; j < 3; j++) {
                    tempDis[index][j] = Integer.parseInt(rows[index][j]);
                }
                // index标记行数,逐行读取和转换成int型
                index++;
            }
            // 关闭流
            br.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        int[][] tempRatingMatrix = new int[200][420];
//        isHaveFilm = new boolean[tempRatingMatrix.length];
//        System.out.println("tempDis.length: " + tempDis.length);
        for (int i = 0; i < index; i++) {
//            System.out.println("for " + i);
//            System.out.println(Arrays.toString(tempDis[i]));
            tempRatingMatrix[tempDis[i][0]][tempDis[i][1]] = tempDis[i][2];
//            isHaveFilm[tempDis[i][0]] = true;
        }//of for i
        int[][] tempFormalContext = new int[200][420];
        for (int i = 0; i < tempRatingMatrix.length; i++) {
            for (int j = 0; j < tempRatingMatrix[0].length; j++) {
                if (tempRatingMatrix[i][j] > 0) {
                    tempFormalContext[i][j] = 1;
                }
            }//of for j
        }//of for i
//		for (int i = 0; i < tempMatrix.length; i++) {
//			for (int j = 0; j < tempMatrix[0].length; j++) {
//				tempMatrix[i][j] = tempDis[i][j];
//			}
////			System.out.println(Arrays.toString(tempMatrix[i]));
//		}
//		int[][] a = new int[300][200];
//		for (int i = 0; i < tempMatrix.length; i++) {
//			a[tempMatrix[i][0]][tempMatrix[i][1]] = tempMatrix[i][2];
//		}
//		for (int i = 0; i < tempDis.length; i++) {
//			System.out.println(Arrays.toString(tempDis[i]));
//		}
        return tempFormalContext;
    }//of readFile

    public int[][] readFCFromArrayFile(String paraFormalContextURL, int paraUsers, int paraItems) {
        try {
            FileReader fr = new FileReader(paraFormalContextURL);
            BufferedReader br = new BufferedReader(fr);
            String str1 = "";
            int tempI = 0;
            int[][] tempFormalContext = new int[paraUsers][paraItems];
            while (str1 != null) {
                str1 = br.readLine();
                if (str1 == null) {
                    break;
                } // Of if
//				System.out.println("第 " + tempI +"行");
                tempFormalContext[tempI] = obtainFCRowArray(str1, paraItems);
                tempI++;
            } // Of while
            br.close();
            fr.close();
            return tempFormalContext;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // Of try
        return null;
    }// Of function readCLFromFile

    int[] obtainFCRowArray(String paraRow, int paraColumn) {
        int[] tempRow = new int[paraColumn];
//		System.out.println("paraRow.lengh :" + paraRow.length());
        String tempSubString = paraRow.substring(0, paraRow.length());
        String[] tempAllElement = tempSubString.split(" ");
        for (int i = 0; i < tempAllElement.length; i++) {
            int tempElement = Integer.parseInt(tempAllElement[i]);
            tempRow[i] = tempElement;
        } // Of for i
        return tempRow;
    }// Of function obtainFCRow

    /**
     * The second constructor
     *
     * @param paraUsers
     * @param paraItems
     * @param paraTxtFilename
     */
    public RatingMatrix(int paraUsers, int paraItems, String paraTxtFilename) {
//		int[][] tempArray = readFile(paraTxtFilename);
        int[][] tempArray = readFile(paraTxtFilename);
//		for (int i = 0; i < tempArray.length; i++) {
//			System.out.println(Arrays.toString(tempArray[i]));
//		}
//		System.out.println("tempRatingMatrix.length :" + tempRatingMatrix.length + " tempRatingMatrix[0].length : " + tempRatingMatrix[0].length);
        formalContext = new int[paraUsers][paraItems];
        ratingMatrix = new int[paraUsers][paraItems];
        for (int i = 0; i < tempArray.length; i++) {
            for (int j = 0; j < tempArray[0].length; j++) {
                ratingMatrix[i][j] = tempArray[i][j];
            }
        }//of for i
        for (int i = 0; i < ratingMatrix.length; i++) {
            for (int j = 0; j < ratingMatrix[0].length; j++) {
                if (ratingMatrix[i][j] > 0) {
                    formalContext[i][j] = 1;
                }//of if
            }//of for j
        }//of for i
        candidateRepresentatives = new int[tempArray.length];
        Arrays.fill(candidateRepresentatives, 1);
        movieFever = getItemFever();
    }

    /**
     * *************
     * The third constructor
     *
     * @param paraMatrix The arff filename.
     *                   *************
     */
    public RatingMatrix(int[][] paraMatrix) {
        // Convert to the boolean matrix.
        formalContext = new int[paraMatrix.length][paraMatrix[0].length];
        ratingMatrix = new int[paraMatrix.length][paraMatrix[0].length];
        for (int i = 0; i < paraMatrix.length; i++) {
            for (int j = 0; j < paraMatrix[0].length; j++) {
                ratingMatrix[i][j] = paraMatrix[i][j];
                if (paraMatrix[i][j] > 0) {
                    formalContext[i][j] = 1;
                }// End if
            }// End for j
        }// Of for i
        candidateRepresentatives = new int[paraMatrix.length];
        Arrays.fill(candidateRepresentatives, 1);
    }// Of the constructor

    /**
     * *************
     * The third constructor
     *
     * @param paraMatrix The arff filename.
     *                   *************
     */
    public RatingMatrix(double[][] paraMatrix) {
        // Convert to the boolean matrix.
        formalContext = new int[paraMatrix.length][paraMatrix[0].length];
        ratingMatrix = new int[paraMatrix.length][paraMatrix[0].length];
        for (int i = 0; i < paraMatrix.length; i++) {
            for (int j = 0; j < paraMatrix[0].length; j++) {
                ratingMatrix[i][j] = (int) paraMatrix[i][j];
                if (paraMatrix[i][j] > 0) {
                    formalContext[i][j] = 1;
                }// End if
            }// End for j
        }// Of for i
        candidateRepresentatives = new int[paraMatrix.length];
        Arrays.fill(candidateRepresentatives, 1);
    }// Of the constructor

    public RatingMatrix(FileReader paraReader) {
        // TODO Auto-generated constructor stub
        Instances paraWekaFormalContext;
        try {
            paraWekaFormalContext = new Instances(paraReader);
            transformInstancesToTestingAndTrainingFC(paraWekaFormalContext);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // End try
    }// End constructor 3

    /**************************************************
     * Transform weka instances to a formal context.
     *
     * @param paraWekaFormalContext
     **************************************************
     */
    void transformInstancesToTestingAndTrainingFC(
            Instances paraWekaFormalContext) {
        trainingFormalContext = new int[paraWekaFormalContext.attribute(0)
                .numValues()][paraWekaFormalContext.attribute(1).numValues()];
        testingFormalContext = new int[paraWekaFormalContext.attribute(0)
                .numValues()][paraWekaFormalContext.attribute(1).numValues()];
        int tempTestCount = 0;
        int tempTrainCount = 0;

        for (int i = 0; i < paraWekaFormalContext.numInstances(); i++) {
            int tempRow = (int) paraWekaFormalContext.instance(i).value(0);
            int tempColumn = (int) paraWekaFormalContext.instance(i).value(1);

            if (tempTrainCount < 80000 && tempTestCount < 20000) {
                int tempRandom = new Random().nextInt(100);
//				System.out.println(tempRandom);
                if (0 <= tempRandom && tempRandom <= 79) {
                    tempTrainCount++;
                    trainingFormalContext[tempRow][tempColumn] = 1;
                } else {
                    tempTestCount++;
                    testingFormalContext[tempRow][tempColumn] = 1;
                }// Of if
            } else if (tempTestCount >= 20000) {
                trainingFormalContext[tempRow][tempColumn] = 1;

            } else if (tempTrainCount >= 80000) {
                testingFormalContext[tempRow][tempColumn] = 1;
            }
        } // End for i
        formalContext = trainingFormalContext;
    }// End function transformInstancesToFomalContext

    /**
     * get the specified matrix.
     *
     * @param paraRow The vector store the row indices
     */
    int[][] getSpecifiedMatrix(int paraRow) {
        int[][] resultMatrix = new int[1][];
        for (int j = 0; j < formalContext[0].length; j++) {
            resultMatrix[0][j] = formalContext[paraRow][j];
        }// End for j
        return resultMatrix;
    }// End getSpecifiedMatrix

    /**
     * *************
     * Compute representative oriented concept.
     * *************
     */
    Concept[] computeRepresentativeAllOrientedConcept(int paraRepresentative, int paraItemsThreshold) {
        // Step 1.Initialize.
        Concept resultConcept = null;
        Concept[] conceptSet = new Concept[10];
        int tempCounter = 0;
        // How many items have I rated?
        for (int i = 0; i < formalContext[0].length; i++) {
            if (formalContext[paraRepresentative][i] == 1) {
                tempCounter++;
            } // Of if
        } // Of for i
        int[] tempItemSet = new int[tempCounter];// 当前用户看过的电影集合
        // Scan one more time to fill.
        tempCounter = 0;
        for (int i = 0; i < formalContext[0].length; i++) {
            if (formalContext[paraRepresentative][i] == 1) {
                tempItemSet[tempCounter] = i;
                tempCounter++;
            } // Of if
        } // Of for i

        // Step 2. Which items are available for deciding neighbors
        boolean[] tempItemAvailable = new boolean[tempItemSet.length];

        // Step 3. Add the most popular item rated by the user.
        tempItemAvailable[0] = true;

        // Step 4. Add item one by one until the number of users smaller than a threshold.
        int tempItemsCounter = 1;

        int[] tempUserArray;
        int[] tempItemArray;
        int count = 0;
        while (true) {
            // Step 4.1 Select item one by one.
            for (int i = 0; i < tempItemSet.length; i++) {
                if (tempItemAvailable[i]) {
                    continue;
                } // Of if

                // Try to add this item
                tempItemAvailable[i] = true;
                tempUserArray = getSuperUsers(tempItemSet, tempItemAvailable);
                tempItemArray = getSuperItems(tempUserArray);

                resultConcept = new Concept(tempUserArray, tempItemArray);
                // Reset
                tempItemAvailable[i] = false;
                conceptSet[count] = resultConcept;
                count++;
            } // Of for i

            if (tempItemsCounter < paraItemsThreshold) {
                // the size of attribute of representative < item threshold
                tempItemsCounter++;
            } else {
                break;
            } // Of if
        } // Of while
//		tempUserArray = getSuperUsers(tempItemSet, tempItemAvailable);
//		int[] tempItemsArray = getSuperItems(tempUserArray);
//
//		resultConcept = new Concept(tempUserArray, tempItemsArray);
        Concept[] resultConceptSet = new Concept[count];
        for (int i = 0; i < resultConceptSet.length; i++) {
            resultConceptSet[i] = conceptSet[i];
        }
        return resultConceptSet;
    }// of computeRepresentativeAllOrientedConcept

    /**
     * *************
     * Compute representative oriented concept.
     *
     * @param paraRepresentative The representative user.
     * @param paraItemsThreshold The threshold for common items.
     *                           *************
     */
    Concept computeRepresentativeOrientedConcept(int paraRepresentative, int paraItemsThreshold) {
//		System.out.println("representative: " + paraRepresentative);
//        System.out.println(Arrays.toString(formalContext[paraRepresentative]));
        // Step 1. Initialize
        Concept resultConcept = null;
        int tempCounter = 0;
        // How many items have I rated?
        for (int i = 0; i < formalContext[0].length; i++) {
            if (formalContext[paraRepresentative][i] == 1) {
                tempCounter++;
            } // Of if
        } // Of for i

        int[] tempItemSet = new int[tempCounter];// 当前用户看过的电影集合
        // Scan one more time to fill.
        tempCounter = 0;
        for (int i = 0; i < formalContext[0].length; i++) {
            if (formalContext[paraRepresentative][i] == 1) {
                tempItemSet[tempCounter] = i;
                tempCounter++;
            } // Of if
        } // Of for i
//		System.out.println("tempItemSet: " + Arrays.toString(tempItemSet));
        // Step 2. Statistics on each item rated by the representative.
        int[] tempCommonRates = new int[tempItemSet.length];
        for (int i = 0; i < formalContext.length; i++) {
            for (int j = 0; j < tempItemSet.length; j++) {
                if (formalContext[i][tempItemSet[j]] == 1) {
                    tempCommonRates[j]++;
                } // Of if
            } // Of for j
        } // Of for i
//		System.out.println("tempCommonrates: " + Arrays.toString(tempCommonRates));
        // Step 3. Which items are available for deciding neighbors
        boolean[] tempItemAvailable = new boolean[tempItemSet.length];

        // Step 4. Add the most popular item rated by the user.
        int tempMostPopular = -1;
        int tempMaximalPopularity = -1;
        for (int i = 0; i < tempCommonRates.length; i++) {
            if (tempMaximalPopularity < tempCommonRates[i]) {
                tempMaximalPopularity = tempCommonRates[i];
                tempMostPopular = i;
            } // Of if
        } // Of for i
//		System.out.println("tempMostPopular is: " + tempMostPopular);
        tempItemAvailable[tempMostPopular] = true;
        int[] tempItemsFever = getItemFever();
        // sort the paraRatedItemsSet
        int temp;
        int[] tempSortedItemsSet = new int[tempItemSet.length];
        for (int i = 0; i < tempItemSet.length; i++) {
            tempSortedItemsSet[i] = tempItemSet[i];
        } // of for i
        for (int i = 0; i < tempSortedItemsSet.length; i++) {
            for (int j = 0; j < tempSortedItemsSet.length - i - 1; j++) {
                if (tempItemsFever[tempSortedItemsSet[j]] < tempItemsFever[tempSortedItemsSet[j + 1]]) {
                    temp = tempSortedItemsSet[j];
                    tempSortedItemsSet[j] = tempSortedItemsSet[j + 1];
                    tempSortedItemsSet[j + 1] = temp;
                } // of if
            } // of for j
        } // of for i

        // Step 5. Add item one by one until the number of users smaller than a threshold, or the area decrease.
        int tempLastRoundArea, tempCurrentArea, tempItemsCounter;
        tempItemsCounter = 1;
        tempLastRoundArea = tempCommonRates[tempMostPopular];
        tempCurrentArea = tempLastRoundArea;
        int tempBestItemIndex;
        int tempBestUnionSize;

        int[] tempUserArray;
        while (true) {
            // Step 4.1 Select the best item
            tempBestItemIndex = -1;
            tempBestUnionSize = -1;
            for (int i = 0; i < tempSortedItemsSet.length; i++) {
                if (tempItemAvailable[i]) {
                    continue;
                } // Of if

                // Try to add this item
                tempItemAvailable[i] = true;
                tempUserArray = getSuperUsers(tempSortedItemsSet, tempItemAvailable);
                if (tempBestUnionSize < tempUserArray.length) {
                    tempBestUnionSize = tempUserArray.length;
                    tempBestItemIndex = i;
                } // Of if
                // Reset
                tempItemAvailable[i] = false;
            } // Of for i

            tempCurrentArea = tempBestUnionSize * (tempItemsCounter + 1);
            if ((tempCurrentArea >= tempLastRoundArea) || (tempItemsCounter < paraItemsThreshold)) {
                //the size of attribute of representative < item threshold
                if (tempBestItemIndex < 0) {
                    break;//如果tempBestItemIndex<0,说明tempBestItemIndex=-1,说明这一轮没有添加项目.
                }
                tempItemsCounter++;//反之，项目数+1.
                tempItemAvailable[tempBestItemIndex] = true;//这一轮添加过的项目标为true.
                tempLastRoundArea = tempCurrentArea;
            } else {
                break;
            } // Of if
        } // Of while

        tempUserArray = getSuperUsers(tempSortedItemsSet, tempItemAvailable);
        int[] tempItemsArray = getSuperItems(tempUserArray);

        resultConcept = new Concept(tempUserArray, tempItemsArray);

        return resultConcept;
    }// Of computeRepresentativeOrientedConcept

    /**
     * *************
     * Compute representative oriented concept.
     *
     * @param paraRepresentative The representative user.
     * @param paraItemsThreshold The threshold for common items.
     *                           *************
     */
    Concept computeRepresentativeOrientedConcept(int paraRepresentative, int paraItemsThreshold, int[] paraRandom,
                                                 int[][] paraFormalContext) {
        // Step 1. Initialize
        Concept resultConcept = null;
        int tempCounter = 0;
        // How many items have I rated?
        for (int i = 0; i < paraFormalContext[0].length; i++) {
            if (paraFormalContext[paraRepresentative][i] == 1) {
                tempCounter++;
            } // Of if
        } // Of for i

        int[] tempItemSet = new int[tempCounter];// 当前用户看过的电影集合
        // Scan one more time to fill.
        tempCounter = 0;
        for (int i = 0; i < paraFormalContext[0].length; i++) {
            if (paraFormalContext[paraRepresentative][i] == 1) {
                tempItemSet[tempCounter] = i;
                tempCounter++;
            } // Of if
        } // Of for i
//        System.out.println("for user " + paraRepresentative + ": ");
//        System.out.println(Arrays.toString(paraFormalContext[paraRepresentative]));
//        System.out.println("tempItemSet: " + Arrays.toString(tempItemSet));
        // Step 2. Statistics on each item rated by the representative.
        int[] tempCommonRates = new int[tempItemSet.length];
        for (int i = 0; i < paraFormalContext.length; i++) {
            for (int j = 0; j < tempItemSet.length; j++) {
                if (paraFormalContext[i][tempItemSet[j]] == 1) {
                    tempCommonRates[j]++;
                } // Of if
            } // Of for j
        } // Of for i

        // Step 3. Which items are available for deciding neighbors
        boolean[] tempItemAvailable = new boolean[tempItemSet.length];
        // Step 4. Add the most popular item rated by the user.
        int tempMostPopular = -1;
        int tempMaximalPopularity = -1;
        for (int i = 0; i < tempCommonRates.length; i++) {
            if (tempMaximalPopularity < tempCommonRates[i]) {
                tempMaximalPopularity = tempCommonRates[i];
                tempMostPopular = i;
            } // Of if
        } // Of for i
//        System.out.println("tempCommonRates: " + Arrays.toString(tempCommonRates));
//		System.out.println("tempMostPopular is: " + tempMostPopular);
        tempItemAvailable[tempMostPopular] = true;
        int[] tempItemsFever = getItemFever();
        // sort the paraRatedItemsSet
        int temp;
        int[] tempSortedItemsSet = new int[tempItemSet.length];
        for (int i = 0; i < tempItemSet.length; i++) {
            tempSortedItemsSet[i] = tempItemSet[i];
        } // of for i
        for (int i = 0; i < tempSortedItemsSet.length; i++) {
            for (int j = 0; j < tempSortedItemsSet.length - i - 1; j++) {
                if (tempItemsFever[tempSortedItemsSet[j]] < tempItemsFever[tempSortedItemsSet[j + 1]]) {
                    temp = tempSortedItemsSet[j];
                    tempSortedItemsSet[j] = tempSortedItemsSet[j + 1];
                    tempSortedItemsSet[j + 1] = temp;
                } // of if
            } // of for j
        } // of for i

        // Step 5. Add item one by one until the number of users smaller than a threshold, or the area decrease.
        int tempLastRoundArea, tempCurrentArea, tempItemsCounter;
        tempItemsCounter = 1;
        tempLastRoundArea = tempCommonRates[tempMostPopular];
        tempCurrentArea = tempLastRoundArea;
        int tempBestItemIndex;
        int tempBestUnionSize;

        int[] tempUserArray;
        while (true) {
            // Step 4.1 Select the best item
            tempBestItemIndex = -1;
            tempBestUnionSize = -1;
            for (int i = 0; i < tempSortedItemsSet.length; i++) {
                if (tempItemAvailable[i]) {
                    continue;
                } // Of if

                // Try to add this item
                tempItemAvailable[i] = true;
                tempUserArray = getSuperUsers(tempSortedItemsSet, tempItemAvailable);
                if (tempBestUnionSize < tempUserArray.length) {
                    tempBestUnionSize = tempUserArray.length;
                    tempBestItemIndex = i;
                } // Of if
                // Reset
                tempItemAvailable[i] = false;
            } // Of for i

            tempCurrentArea = tempBestUnionSize * (tempItemsCounter + 1);
            if ((tempCurrentArea >= tempLastRoundArea) || (tempItemsCounter < paraItemsThreshold)) {
                //the size of attribute of representative < item threshold
                if (tempBestItemIndex < 0) {
                    break;//如果tempBestItemIndex<0,说明tempBestItemIndex=-1,说明这一轮没有添加项目.
                }
                tempItemsCounter++;//反之，项目数+1.
                tempItemAvailable[tempBestItemIndex] = true;//这一轮添加过的项目标为true.
                tempLastRoundArea = tempCurrentArea;
            } else {
                break;
            } // Of if
        } // Of while
//        // Step 4. Add an item rated by the user.
//        tempItemAvailable[0] = true;
//        // Step 5. Add item one by one until the number of users smaller than a
//        // threshold, or the area decrease.
//        int tempLastRoundArea, tempCurrentArea, tempItemsCounter;
//        tempItemsCounter = 1;
//        tempLastRoundArea = tempCommonRates[0];
//        tempCurrentArea = tempLastRoundArea;
//        int tempBestItemIndex;
//        int tempBestUnionSize;
//
//        int[] tempUserArray;
//        while (true) {
//            // Step 4.1 Select the best item
//            tempBestItemIndex = -1;
//            tempBestUnionSize = -1;
//            for (int i = 0; i < tempItemSet.length; i++) {
//                if (tempItemAvailable[i]) {
//                    continue;
//                } // Of if
//
//                // Try to add this item
//                tempItemAvailable[i] = true;
//                tempUserArray = getSuperUsers(tempItemSet, tempItemAvailable, paraFormalContext);
//                if (tempBestUnionSize < tempUserArray.length) {
//                    tempBestUnionSize = tempUserArray.length;
//                    tempBestItemIndex = i;
//                } // Of if
//                // Reset
//                tempItemAvailable[i] = false;
//            } // Of for i
//
//            tempCurrentArea = tempBestUnionSize * (tempItemsCounter + 1);
//            if ((tempCurrentArea >= tempLastRoundArea) || (tempItemsCounter < paraItemsThreshold)) {
//                // the size of attribute of representative < item threshold
//                if (tempBestItemIndex < 0) {
//                    break;
//                }
//                tempItemsCounter++;
//                tempItemAvailable[tempBestItemIndex] = true;
//                tempLastRoundArea = tempCurrentArea;
//            } else {
//                break;
//            } // Of if
//        } // Of while

        tempUserArray = getSuperUsers(tempItemSet, tempItemAvailable, paraFormalContext);
        int[] tempItemsArray = getSuperItems(tempUserArray, paraFormalContext);
        int[] finalUser = new int[tempUserArray.length];
        for (int i = 0; i < finalUser.length; i++) {
            finalUser[i] = paraRandom[tempUserArray[i]];
        }//of for i
        resultConcept = new Concept(finalUser, tempItemsArray);

        return resultConcept;
    }// Of computeRepresentativeOrientedConcept

    /**
     * compute similarity of each user
     */
    public double[] computeSimilarityOfEachUser(int paraUser) {
        double[] similarityOfEachUser = new double[formalContext.length];
        for (int i = 0; i < formalContext.length; i++) {
            if (i != paraUser) {
                similarityOfEachUser[i] = computeSimilarityOfUsers(paraUser, i);
            } else {
                similarityOfEachUser[i] = 0;
            }
        } // of for i
        return similarityOfEachUser;
    }// of computeSimilarityOfEachUser

    /**
     * compute mutate oriented concepts
     */
    public Concept computeMutateOrientedConcepts(int paraUser, int paraUserThreshold, Concept paraConcept) {
        Concept resultConcept = null;
        double[] similarity = computeSimilarityOfEachUser(paraUser);
        boolean[] tempUserAvailable = new boolean[formalContext.length];

        // sort the similarity
        double tempSim;
        int tempIndex;
        double[] tempSortedSimilarity = new double[similarity.length];
        int[] tempIndexOfUser = new int[similarity.length];
        for (int i = 0; i < similarity.length; i++) {
            tempSortedSimilarity[i] = similarity[i];
            tempIndexOfUser[i] = i;
        } // of for i
        for (int i = 0; i < tempSortedSimilarity.length; i++) {
            for (int j = 0; j < tempSortedSimilarity.length - i - 1; j++) {
                if (tempSortedSimilarity[j] < tempSortedSimilarity[j + 1]) {
                    tempSim = tempSortedSimilarity[j];
                    tempSortedSimilarity[j] = tempSortedSimilarity[j + 1];
                    tempSortedSimilarity[j + 1] = tempSim;
                    tempIndex = tempIndexOfUser[j + 1];
                    tempIndexOfUser[j + 1] = tempIndexOfUser[j];
                    tempIndexOfUser[j] = tempIndex;
                } // of if
            } // of for j
        } // of for i

        for (int i = 0; i < tempUserAvailable.length; i++) {
            tempUserAvailable[tempIndexOfUser[i]] = true;
            int[] userArray = getUsers(paraConcept.users, tempUserAvailable);
            int[] itemArray = getSuperItems(userArray);
            if (itemArray.length < 0) {//不满足条件，不再继续添加，输出当前的概念作为最终的概念
                resultConcept = new Concept(userArray, itemArray);
            }//of if
        }//of for i
//		int gap = paraUserThreshold - paraConcept.users.length;
//		for (int i = 0; i < gap; i++) {
//			tempUserAvailable[tempIndexOfUser[i]] = true;
//		} // of for i
//		int[] userArray = getUsers(paraConcept.users, tempUserAvailable);
//		int[] itemArray = getSuperItems(userArray);
//		resultConcept = new Concept(userArray, itemArray);
        return resultConcept;
    }// of computeMutateOrientedConcepts

    /**
     * compute mutate oriented concepts
     */
//	public Concept mutateOrientedConcepts(int paraUser, int paraItemThreshold, Concept paraConcept) {
//		Concept resultConcept = null;
//
//		double[] similarity = computeSimilarityOfEachUser(paraUser);
//		boolean[] tempAvailable = new boolean[formalContext.length];
//		// sort the similarity
//		double tempSim;
//		int tempIndex;
//		double[] tempSortedSimilarity = new double[similarity.length];
//		int[] tempIndexOfUser = new int[similarity.length];
//		for (int i = 0; i < similarity.length; i++) {
//			tempSortedSimilarity[i] = similarity[i];
//			tempIndexOfUser[i] = i;
//		} // of for i
//		for (int i = 0; i < tempSortedSimilarity.length; i++) {
//			for (int j = 0; j < tempSortedSimilarity.length - i - 1; j++) {
//				if (tempSortedSimilarity[j] < tempSortedSimilarity[j + 1]) {
//					tempSim = tempSortedSimilarity[j];
//					tempSortedSimilarity[j] = tempSortedSimilarity[j + 1];
//					tempSortedSimilarity[j + 1] = tempSim;
//					tempIndex = tempIndexOfUser[j + 1];
//					tempIndexOfUser[j + 1] = tempIndexOfUser[j];
//					tempIndexOfUser[j] = tempIndex;
//				} // of if
//			} // of for j
//		} // of for i
//			// add users
//		double maxSim = computeSimilarityOfConcepts(paraConcept);
//
//		for (int i = 0; i < tempIndexOfUser.length; i++) {
//			tempAvailable[tempIndexOfUser[i]] = true;
//			int[] tempItemArray = getSuperItems(paraConcept.users, tempAvailable);
//			if (tempItemArray.length > paraItemThreshold) {
//				continue;
//			} else {
//				tempAvailable[tempIndexOfUser[i]] = false;
//				break;
//			} // of if
//		} // of for i
//		int[] resultItemArray = getSuperItems(paraConcept.users, tempAvailable);
//		int[] resultUserArray = getUsers(resultItemArray);
//		resultConcept = new Concept(resultUserArray, resultItemArray);
//		return resultConcept;
//	}//of mutateOrientedConcepts

    /**
     * get users
     *
     * @param paraConcpet
     * @param paraAvailable
     * @return
     */
    public int[] addUsers(Concept paraConcpet, boolean[] paraAvailable) {
        int[] users = paraConcpet.users;
        int[] addUsers = new int[formalContext.length];
        int count = paraConcpet.users.length;
        for (int i = 0; i < users.length; i++) {
            addUsers[i] = users[i];
        }//of for i
        for (int i = 0; i < paraAvailable.length; i++) {
            if (paraAvailable[i]) {
                addUsers[count] = i;
                count++;
            }//of if
        }//of for i
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = addUsers[i];
        }//of for i
        return result;
    }//of addUsers

    /**
     * get users
     *
     * @param paraUserSet
     * @param paraAvailable
     * @return
     */
    public int[] getUsers(int[] paraUserSet, boolean[] paraAvailable) {
        int[] tempUserset = new int[paraAvailable.length];
        for (int i = 0; i < paraUserSet.length; i++) {
            tempUserset[i] = paraUserSet[i];
        }//of for i
        int count = paraUserSet.length;
        for (int i = 0; i < paraAvailable.length; i++) {
            if (paraAvailable[i]) {
                tempUserset[count] = i;
                count++;
            }//of if
        }//of for i
        int[] resultUserArray = new int[count];
        for (int i = 0; i < count; i++) {
            resultUserArray[i] = tempUserset[i];
        }//of for i
        return resultUserArray;
    }//of getUsers

    /**
     * *************
     * Get the number of users.
     *
     * @return the number of users.
     * *************
     */
    public int numUsers() {
        return formalContext.length;
    }//Of numUsers

    /**
     * *************
     * Get the number of items.
     *
     * @return the number of items.
     * *************
     */
    public int numItems() {
        return formalContext[0].length;
    }//Of numItems

    /**
     * *************
     * Which users have rated the given item set.
     *
     * @param paraItemSet   The given item set.
     * @param paraAvailable Is the respective item available.
     *                      *************
     */
    int[] getSuperUsers(int[] paraItemSet, boolean[] paraAvailable) {
        int[] tempUsers = new int[formalContext.length];
        int tempActualUsers = 0;
        boolean tempIsSuperUser;

        for (int i = 0; i < formalContext.length; i++) {
            tempIsSuperUser = true;
            for (int j = 0; j < paraItemSet.length; j++) {
                if (paraAvailable[j] && formalContext[i][paraItemSet[j]] == 0) {
                    tempIsSuperUser = false;
                    break;
                }//Of if
            }//Of for j

            if (tempIsSuperUser) {
                tempUsers[tempActualUsers] = i;
                tempActualUsers++;
            }//Of if
        }//Of for i

        //Now compress.
        int[] resultUsers = new int[tempActualUsers];
        for (int i = 0; i < tempActualUsers; i++) {
            resultUsers[i] = tempUsers[i];
        }//Of for i

        return resultUsers;
    }//Of getSuperUsers

    /**
     * *************
     * Which users have rated the given item set.
     *
     * @param paraItemSet   The given item set.
     * @param paraAvailable Is the respective item available.
     *                      *************
     */
    int[] getSuperUsers(int[] paraItemSet, boolean[] paraAvailable, int[][] paraFormalContext) {
        int[] tempUsers = new int[paraFormalContext.length];
        int tempActualUsers = 0;
        boolean tempIsSuperUser;

        for (int i = 0; i < paraFormalContext.length; i++) {
            tempIsSuperUser = true;
            for (int j = 0; j < paraItemSet.length; j++) {
                if (paraAvailable[j] && paraFormalContext[i][paraItemSet[j]] == 0) {
                    tempIsSuperUser = false;
                    break;
                }//Of if
            }//Of for j

            if (tempIsSuperUser) {
                tempUsers[tempActualUsers] = i;
                tempActualUsers++;
            }//Of if
        }//Of for i

        //Now compress.
        int[] resultUsers = new int[tempActualUsers];
        for (int i = 0; i < tempActualUsers; i++) {
            resultUsers[i] = tempUsers[i];
        }//Of for i

        return resultUsers;
    }//Of getSuperUsers

    /**
     * *************
     * Which items have been rated by the given user set.
     *
     * @param paraUserSet       The given user set.
     * @param paraUserAvailable Is the respective item available.
     *                          *************
     */
    int[] getSuperItems(int[] paraUserSet, boolean[] paraUserAvailable) {

        int[] tempUser = new int[formalContext.length];

        for (int i = 0; i < paraUserSet.length; i++) {
            tempUser[i] = paraUserSet[i];
        }//of for i

        int count = paraUserSet.length;
        for (int i = 0; i < paraUserAvailable.length; i++) {
            if (paraUserAvailable[i]) {
                tempUser[count] = i;
                count++;
            }//of if
        }//of for i

        //compress user set
        int[] userSet = new int[count];
        for (int i = 0; i < userSet.length; i++) {
            userSet[i] = tempUser[i];
        }//of for i

        int[] itemSet = getSuperItems(userSet);
//		boolean tempIsSuperItem;
//		for (int i = 0; i < formalContext.length; i++) {
//			tempIsSuperItem = true;
//			for (int j = 0; j < userSet.length; j++) {
//				if (!formalContext[i][paraUserSet[j]]) {
//					tempIsSuperItem = false;
//					break;
//				}//Of if
//			}//Of for j
//
//			if (tempIsSuperItem) {
//				tempItems[tempActualItems] = i;
//				tempActualItems ++;
//			}//Of if
//		}//Of for i
//
//		//Now compress.
//		int[] resultItems = new int[tempActualItems];
//		for (int i = 0; i < tempActualItems; i++) {
//			resultItems[i] = tempItems[i];
//		}//Of for i

        return itemSet;
    }//of getSuperItems

//    /**
//     * *************
//     * Which items have been rated by the given user set.
//     *
//     * @param paraUserSet       The given user set.
//     * @param paraUserAvailable Is the respective item available.
//     *                          *************
//     */
//    int[] getSuperItems(int[] paraUserSet, boolean[] paraUserAvailable, boolean[][] paraFormalContext) {
//
//        int[] tempUser = new int[paraFormalContext.length];
//
//        for (int i = 0; i < paraUserSet.length; i++) {
//            tempUser[i] = paraUserSet[i];
//        }//of for i
//
//        int count = paraUserSet.length;
//        for (int i = 0; i < paraUserAvailable.length; i++) {
//            if (paraUserAvailable[i]) {
//                tempUser[count] = i;
//                count++;
//            }//of if
//        }//of for i
//
//        //compress user set
//        int[] userSet = new int[count];
//        for (int i = 0; i < userSet.length; i++) {
//            userSet[i] = tempUser[i];
//        }//of for i
//
//        int[] itemSet = getSuperItems(userSet, paraFormalContext);
////		boolean tempIsSuperItem;
////		for (int i = 0; i < formalContext.length; i++) {
////			tempIsSuperItem = true;
////			for (int j = 0; j < userSet.length; j++) {
////				if (!formalContext[i][paraUserSet[j]]) {
////					tempIsSuperItem = false;
////					break;
////				}//Of if
////			}//Of for j
////
////			if (tempIsSuperItem) {
////				tempItems[tempActualItems] = i;
////				tempActualItems ++;
////			}//Of if
////		}//Of for i
////
////		//Now compress.
////		int[] resultItems = new int[tempActualItems];
////		for (int i = 0; i < tempActualItems; i++) {
////			resultItems[i] = tempItems[i];
////		}//Of for i
//
//        return itemSet;
//    }//of getSuperItems

    /**
     * *************
     * Which users have rated the given item set.
     *
     * @param paraItemSet The given item set.
     *                    *************
     */
    int[] getUsers(int[] paraItemSet) {
        int[] tempUsers = new int[formalContext.length];
        int tempActualUsers = 0;
        boolean tempIsSuperUser;

        for (int i = 0; i < formalContext.length; i++) {
            tempIsSuperUser = true;
            for (int j = 0; j < paraItemSet.length; j++) {
                if (formalContext[i][paraItemSet[j]] == 0) {
                    tempIsSuperUser = false;
                    break;
                }//Of if
            }//Of for j

            if (tempIsSuperUser) {
                tempUsers[tempActualUsers] = i;
                tempActualUsers++;
            }//Of if
        }//Of for i

        //Now compress.
        int[] resultUsers = new int[tempActualUsers];
        for (int i = 0; i < tempActualUsers; i++) {
            resultUsers[i] = tempUsers[i];
        }//Of for i

        return resultUsers;
    }

    /**
     * *************
     * Which users have rated the given item set.
     *
     * @param paraItemSet The given item set.
     *                    *************
     */
    int[] getSuperUsers(int[] paraItemSet) {
        int[] tempUsers = new int[formalContext.length];
        int tempActualUsers = 0;
        boolean tempIsSuperUser;

        for (int i = 0; i < formalContext[0].length; i++) {
            tempIsSuperUser = true;
            for (int j = 0; j < paraItemSet.length; j++) {
                if (formalContext[paraItemSet[j]][i] == 0) {
                    tempIsSuperUser = false;
                    break;
                }//Of if
            }//Of for j

            if (tempIsSuperUser) {
                tempUsers[tempActualUsers] = i;
                tempActualUsers++;
            }//Of if
        }//Of for i

        //Now compress.
        int[] resultUsers = new int[tempActualUsers];
        for (int i = 0; i < tempActualUsers; i++) {
            resultUsers[i] = tempUsers[i];
        }//Of for i

        return resultUsers;
    }//of getSuperUsers

    /**
     * *************
     * Which items have been rated by the given user set.
     *
     * @param paraUserSet The given user set.
     *                    *************
     */
    int[] getSuperItems(int[] paraUserSet) {
        int[] tempItems = new int[formalContext[0].length];
        int tempActualItems = 0;
        boolean tempIsSuperItem;

        for (int i = 0; i < formalContext[0].length; i++) {
            tempIsSuperItem = true;
            for (int j = 0; j < paraUserSet.length; j++) {
                if (formalContext[paraUserSet[j]][i] == 0) {
                    tempIsSuperItem = false;
                    break;
                }//Of if
            }//Of for j

            if (tempIsSuperItem) {
                tempItems[tempActualItems] = i;
                tempActualItems++;
            }//Of if
        }//Of for i

        //Now compress.
        int[] resultItems = new int[tempActualItems];
        for (int i = 0; i < tempActualItems; i++) {
            resultItems[i] = tempItems[i];
        }//Of for i

        return resultItems;
    }//Of getSuperItems

    /**
     * *************
     * Which items have been rated by the given user set.
     *
     * @param paraUserSet The given user set.
     *                    *************
     */
    int[] getSuperItems(int[] paraUserSet, int[][] paraFormalContext) {
        int[] tempItems = new int[paraFormalContext[0].length];
        int tempActualItems = 0;
        boolean tempIsSuperItem;

        for (int i = 0; i < paraFormalContext[0].length; i++) {
            tempIsSuperItem = true;
            for (int j = 0; j < paraUserSet.length; j++) {
                if (paraFormalContext[paraUserSet[j]][i] == 0) {
                    tempIsSuperItem = false;
                    break;
                }//Of if
            }//Of for j

            if (tempIsSuperItem) {
                tempItems[tempActualItems] = i;
                tempActualItems++;
            }//Of if
        }//Of for i

        //Now compress.
        int[] resultItems = new int[tempActualItems];
        for (int i = 0; i < tempActualItems; i++) {
            resultItems[i] = tempItems[i];
        }//Of for i

        return resultItems;
    }//Of getSuperItems


    /**
     * *************
     * Compute the number of items rated by each user.
     * *************
     */
    public int[] getUserFever() {//方法getUserFever()返回一个int类型的数组
        int[] tempFever = new int[formalContext.length];
        for (int i = 0; i < formalContext.length; i++) {
            for (int j = 0; j < formalContext[0].length; j++) {
                if (formalContext[i][j] == 1) {
                    tempFever[i]++;
                }//Of if
            }//Of for j
        }//Of for i

        return tempFever;
    }//Of getUserFever

    /**
     * *************
     * Compute the number of items rated by each user.
     * *************
     */
    public int getUserFever(int paraUser) {//方法getUserFever()返回一个int类型的数组
        int tempFever = 0;
        for (int i = 0; i < formalContext[0].length; i++) {
            if (formalContext[paraUser][i] == 1) {
                tempFever++;
            }//Of if
        }//Of for i
        return tempFever;
    }//Of getUserFever

    /**
     * *************
     * Compute the number of items rated by each user.
     * *************
     */
    public int getUserFever(int paraUser, int[][] paraFormalContext) {//方法getUserFever()返回一个int类型的数组
        int tempFever = 0;
        for (int i = 0; i < paraFormalContext[0].length; i++) {
            if (paraFormalContext[paraUser][i] == 1) {
                tempFever++;
            }//Of if
        }//Of for i
        return tempFever;
    }//Of getUserFever

    /**
     * *************
     * Compute the number of items rated by each item.
     * *************
     */
    public int[] getItemFever() {
        int[] tempFever = new int[formalContext[0].length];
        for (int i = 0; i < formalContext[0].length; i++) {
            for (int j = 0; j < formalContext.length; j++) {
                if (formalContext[j][i] == 1) {
                    tempFever[i]++;
                }//Of if
            }//Of for j
        }//Of for i

        return tempFever;
    }//Of getItemFever

//	/**
//	 **************
//	 * toString.
//	 **************
//	 */
//	public String toString() {
//		String resultString = "The rating matrix is:\r\n";
//
//		resultString += SimpleTools.booleanMatrixToString(formalContext, ',', 10, 10);
//
//		return resultString;
//	}// Of toString

    /**
     * Calculate concept similarity.
     *
     * @param paraSourConcept
     * @param paraConcept
     * @return
     */
    double conceptSimilarity(Concept paraSourConcept, Concept paraConcept) {
        double tempSimilarity = 0.0;
        int tempItemSame = 0;
        int tempIndex = 0;
        // Calculate extension similarity.
        double tempExtensionSimilarity = (0.0) * (1 / paraConcept.users.length);
        // Calculate intension similarity.
        for (int i = 0; i < paraConcept.items.length; i++) {
            for (int j = tempIndex; j < paraSourConcept.items.length; j++) {
                if (paraConcept.items[i] < paraSourConcept.items[j]) {
                    break;
                }// Of if
                if (paraConcept.items[i] == paraSourConcept.items[j]) {
                    tempItemSame++;
                    tempIndex = j + 1;
                    break;
                }// Of if
            }// Of for j
        }// Of for i
        double tempIntensionSimilarity = (1.0) * tempItemSame / (paraSourConcept.items.length);

        tempSimilarity = tempExtensionSimilarity + tempIntensionSimilarity;
        return tempSimilarity;
    }// Of conceptSimilarity

    /**
     * *************
     * Recommend to the given user with the given concept.
     *
     * @param paraUser      The given user.
     * @param paraConcept   The given concept.
     * @param paraThreshold The threshold for recommendation.
     *                      *************
     */
    public boolean[] userConceptBasedRecommendation(int paraUser, Concept paraConcept, double paraThreshold) {
        boolean[] resultRecommendation = new boolean[formalContext[0].length];
        //Scan all the items.
        double tempRatedNeighbors;
        for (int i = 0; i < formalContext[0].length; i++) {
            //I have rated it already.
            if (formalContext[paraUser][i] == 1) {
                continue;
            }//Of if

            //How many neighbors have rated it?
            tempRatedNeighbors = 0;

            for (int j = 0; j < paraConcept.users.length; j++) {
                if (formalContext[paraConcept.users[j]][i] == 1) {
                    tempRatedNeighbors++;
                }//Of if
            }//Of for j

            if (tempRatedNeighbors / paraConcept.users.length > paraThreshold) {
                resultRecommendation[i] = true;
            }//Of if
        }//Of for i

        return resultRecommendation;
    }//Of recommendForUser

    /**
     * *************
     * Recommend to the given user with the given concept.
     *
     * @param paraUser      The given user.
     * @param paraConcept   The given concept.
     * @param paraThreshold The threshold for recommendation.
     *                      *************
     */
    public int[] userConceptBasedRecommendationInt(int paraUser, Concept paraConcept, double paraThreshold) {
        int[] resultRecommendation = new int[formalContext[0].length];
        //Scan all the items.
        double tempRatedNeighbors;
        for (int i = 0; i < formalContext[0].length; i++) {
            //I have rated it already.
            if (formalContext[paraUser][i] == 1) {
                continue;
            }//Of if

            //How many neighbors have rated it?
            tempRatedNeighbors = 0;

            for (int j = 0; j < paraConcept.users.length; j++) {
                if (formalContext[paraConcept.users[j]][i] == 1) {
                    tempRatedNeighbors++;
                }//Of if
            }//Of for j

            if (tempRatedNeighbors / paraConcept.users.length > paraThreshold) {
                resultRecommendation[i]++;
            }//Of if
        }//Of for i

        return resultRecommendation;
    }//Of recommendForUser

    /**
     * *************
     * Recommend to the given user with the given concept.
     *
     * @param paraUser    The given user.
     * @param paraConcept The given concept.
     *                    *************
     */
    public double[] conceptsBasedRatingRecommendation(int paraUser, Concept paraConcept) {
        double[] resultRecommendation = new double[formalContext[0].length];
        int[] tempMovieFever = getItemFever();
        //Scan all the items.
        int tempRatedNeighbors;
        for (int i = 0; i < formalContext[0].length; i++) {
            //I have rated it already.
            if (formalContext[paraUser][i] == 1) {
                continue;
            }//Of if

            //Statistic how many neighbors have rated it.
            //And sum the ratings of item i.
            tempRatedNeighbors = 0;
            int tempRatingSum = 0;
            for (int j = 0; j < paraConcept.users.length; j++) {
                if (formalContext[paraConcept.users[j]][i] == 1) {
                    tempRatedNeighbors++;
                    tempRatingSum += ratingMatrix[paraConcept.users[j]][i];
                }//Of if
            }//Of for j

            //Here given a rating prediction
            //Unless the voting number satisfy the threshold.

//			if(tempMovieFever[i]>50 && tempMovieFever[i]< 200){
            if (1.0 * tempRatedNeighbors / paraConcept.users.length >= 0.4) {
                resultRecommendation[i] = (double) tempRatingSum / tempRatedNeighbors;
            } else {
                resultRecommendation[i] = 0.0;
            }// Of if
//			}
//			else{
//				if(1.0*tempRatedNeighbors/paraConcept.users.length >= 0.5){
//					resultRecommendation[i] = (double)tempRatingSum/tempRatedNeighbors;
//				}else{
//					resultRecommendation[i] = 0.0;
//				}// Of if
//			}


        }//Of for i

        return resultRecommendation;
    }//Of recommendForUser

    /**
     * *************
     * Recommend to the given user with the given user group.
     *
     * @param paraUser The given user.
     *                 *************
     */
    public double[] groupBasedRatingRecommendation(int paraUser, int[] paraUserGroup) {
        double[] resultRecommendation = new double[formalContext[0].length];
        //Scan all the items.
        int tempRatedNeighbors;
        for (int i = 0; i < formalContext[0].length; i++) {
            //I have rated it already.
            if (formalContext[paraUser][i] == 1) {
                continue;
            }//Of if

            //How many neighbors have rated it?
            tempRatedNeighbors = 0;
            int tempRatingSum = 0;

            for (int j = 0; j < paraUserGroup.length; j++) {
                if (formalContext[paraUserGroup[j]][i] == 1) {
                    tempRatedNeighbors++;
                    tempRatingSum += ratingMatrix[paraUserGroup[j]][i];
                }//Of if
            }//Of for j
            //resultRecommendation[i] = (double)tempRatingSum/tempRatedNeighbors;
            resultRecommendation[i] = (double) tempRatingSum / tempRatedNeighbors;
        }//Of for i

        return resultRecommendation;
    }//Of recommendForUser

    /**
     *
     */
    public int getRating(int paraUserIndex, int paraItemIndex) {
        return ratingMatrix[paraUserIndex][paraItemIndex];
    }

    /**
     * compute the similarity of users.
     */
    public double computeSimilarityOfUsers(int paraUserIndex1, int paraUserIndex2) {
        double similarity = 0;
        //Step 1.Find the common rated movie of two users and the difference value.
        int[] tempCommonRated = new int[formalContext[0].length];
//		int[] tempDifferenceValue = new int[formalContext[0].length];
        int count = 0;
        for (int i = 0; i < formalContext[0].length; i++) {
            if (ratingMatrix[paraUserIndex1][i] != 0 && ratingMatrix[paraUserIndex2][i] != 0) {
                tempCommonRated[count] = i;
//				tempDifferenceValue[count] =Math.abs(ratingMatrix[paraUserIndex1][i] - ratingMatrix[paraUserIndex2][i]);
                count++;
            }//of if
        }//of for i
//		int[] commonRated = new int[count];
//		int[] differenceValue = new int[count];
//		for (int i = 0; i < count; i++) {
//			commonRated[i] = tempCommonRated[i];
////			differenceValue[i] = tempDifferenceValue[i];
//		}//of for i
//		int sumOfDifferenceValue = 0;
//		for (int i = 0; i < differenceValue.length; i++) {
//			sumOfDifferenceValue += differenceValue[i];
//		}//of for i
//		if (commonRated.length == 0) {
//			similarity = 0;
//		} else {
//			similarity = (double)(sumOfDifferenceValue / commonRated.length);
//		}
//		return similarity;
        return count;
    }//of computeSimilarityOfUsers

    /**
     * compute the similarity of users.
     */
    public double computeSimilarityOfConcepts(Concept paraConcept) {
        double similarity;
        int sumOfSimilarity = 0;
        for (int i = 0; i < paraConcept.users.length - 1; i++) {
            for (int j = i + 1; j < paraConcept.users.length; j++) {
                sumOfSimilarity += computeSimilarityOfUsers(paraConcept.users[i], paraConcept.users[j]);
            }//of for j
        }//of for i
        similarity = sumOfSimilarity / (paraConcept.users.length - 1);
        return similarity;
    }//of computeSimilarityOfConcepts

    /**
     * compute the Pearson coefficient of two users.
     *
     * @param paraUser1
     * @param paraUser2
     * @return The Pearson coefficient.
     */
    public double computePearsonCoefficientOfTwoUsers(int paraUser1, int paraUser2) {
        int[] tempCommonRated = new int[formalContext[0].length];
        int[] commonRated;
        int count = 0;
        double pearSon = 0;
        double aveOfUser1 = 0;
        double aveOfUser2 = 0;

        double sumOfUser1 = 0;
        double sumOfUser2 = 0;

        //find the common rated movie of the two users.
        for (int i = 0; i < formalContext[0].length; i++) {
            if (formalContext[paraUser1][i] == 1 && formalContext[paraUser2][i] == 1) {
                tempCommonRated[count] = i;
                count++;
            }//of if
        }//of for i
        commonRated = new int[count];
        for (int i = 0; i < count; i++) {
            commonRated[i] = tempCommonRated[i];
            sumOfUser1 += ratingMatrix[paraUser1][commonRated[i]];
            sumOfUser2 += ratingMatrix[paraUser2][commonRated[i]];
        }//of for i
        aveOfUser1 = sumOfUser1 / count;
        aveOfUser2 = sumOfUser2 / count;
        //compute pearson coefficient
        double up = 0;
        double down = 0;
        double d1 = 0;
        double d2 = 0;
        for (int i = 0; i < commonRated.length; i++) {
            up += (ratingMatrix[paraUser1][commonRated[i]] - aveOfUser1) * (ratingMatrix[paraUser2][commonRated[i]] - aveOfUser2);
            d1 += (ratingMatrix[paraUser1][commonRated[i]] - aveOfUser1) * (ratingMatrix[paraUser1][commonRated[i]] - aveOfUser1);
            d2 += (ratingMatrix[paraUser2][commonRated[i]] - aveOfUser2) * (ratingMatrix[paraUser2][commonRated[i]] - aveOfUser2);
        }//of for i
        pearSon = up / Math.sqrt(d1 * d2);
        return pearSon;
    }//of computePearsonCoefficientOfTwoUsers

    /**
     * get cross set
     */
    public int[] getCrossSet(int[] paraSet1, int[] paraSet2) {
        int[] tempNewSet = new int[paraSet1.length < paraSet2.length ? paraSet1.length
                : paraSet2.length];
        int count = 0;
        for (int i = 0; i < paraSet1.length; i++) {
            for (int j = 0; j < paraSet2.length; j++) {
                if (paraSet1[i] == paraSet2[j]) {
                    tempNewSet[count] = paraSet1[i];
                    count++;
                    break;
                }//of if
            }//of for j
        }//of for i
        int[] newSet = new int[count];
        for (int i = 0; i < count; i++) {
            newSet[i] = tempNewSet[i];
        }//of for i
        return newSet;
    }//of getCrossSet

    /**
     * get cross set
     */
    public int[] getMutationSet(int[] paraSet1, int[] paraSet2) {
        int[] tempNewSet = new int[paraSet1.length + paraSet2.length];
        for (int i = 0; i < paraSet1.length; i++) {
            tempNewSet[i] = paraSet1[i];
        }//of for i
        int flag = 0;
        for (int i = 0; i < paraSet2.length; i++) {
            int count = 0;
            for (int j = 0; j < paraSet1.length; j++) {
                if (paraSet2[i] == paraSet1[j]) {
                    break;
                } else {
                    count++;
                }//of if
                if (count == paraSet1.length) {
                    tempNewSet[paraSet1.length + flag] = paraSet2[i];
                    flag++;
                }//of if
            }//of for j
        }//of for i
        int[] newSet = new int[paraSet1.length + flag];
        for (int i = 0; i < newSet.length; i++) {
            newSet[i] = tempNewSet[i];
        }//of for i
        return newSet;
    }//of getMutationSet

    /**
     *
     */
    public Concept getNewConceptByCross(Concept paraConcept1, Concept paraConcept2) {
        int[] newUserSet = getCrossSet(paraConcept1.users, paraConcept2.users);
        int[] newItemSet = getCrossSet(paraConcept1.items, paraConcept2.items);
        Concept newConcept = new Concept(newUserSet, newItemSet);
        return newConcept;
    }//of getNewConceptByCross

    /**
     *
     */
    public Concept getNewConceptByCrossUsers(Concept paraConcept1, Concept paraConcept2) {
        int[] newUserSet = getCrossSet(paraConcept1.users, paraConcept2.users);
        int[] newItemSet = getSuperItems(newUserSet);
        Concept newConcept = new Concept(newUserSet, newItemSet);
        return newConcept;
    }//of getNewConceptByCrossUsers

    /**
     * generateNewConceptByMutate
     */
    public Concept generateNewConceptByMutate(Concept paraConcept, int paraUser, int paraItemThreshold) {
        //compute similarity with each user
        double currentSim = computeSimilarityOfConcepts(paraConcept);
        double[] similarityWithEachUser = new double[formalContext.length];
        for (int i = 0; i < formalContext.length; i++) {
            similarityWithEachUser[i] = computeSimilarityOfUsers(paraUser, i);
        }//of for i
        //sort
        int[] tempUserAvaliable = new int[formalContext.length];
        for (int i = 0; i < tempUserAvaliable.length; i++) {
            tempUserAvaliable[i] = i;
        }//of for i
        double temp1;
        int temp2;
        for (int i = 0; i < tempUserAvaliable.length; i++) {
            for (int j = 0; j < tempUserAvaliable.length - i - 1; j++) {
                if (similarityWithEachUser[j] < similarityWithEachUser[j + 1]) {
                    temp1 = similarityWithEachUser[j];
                    similarityWithEachUser[j] = similarityWithEachUser[j + 1];
                    similarityWithEachUser[j + 1] = temp1;
                    temp2 = tempUserAvaliable[j];
                    tempUserAvaliable[j] = tempUserAvaliable[j + 1];
                    tempUserAvaliable[j] = temp2;
                }//of if
            }//of for j
        }//of for i
        //insert user into extension
        int[] tempUserSet = new int[formalContext.length];
        int count1 = paraConcept.users.length;
        int count2 = 0;
        double tempCurrentSim = 0;
        double tempMaxSim = currentSim;
        for (int i = 0; i < count1; i++) {
            tempUserSet[i] = paraConcept.users[i];
        }//of for i
        int[] itemSet;
        do {
            tempUserSet[count1] = tempUserAvaliable[count2];
            count1++;
            count2++;
            int[] tempNewUserSet = new int[count1];
            for (int i = 0; i < count1; i++) {
                tempNewUserSet[i] = tempUserSet[i];
            }//of for i
            int[] tempNewItemSet = getSuperItems(tempNewUserSet);
            itemSet = tempNewItemSet;
            Concept tempNewConcept = new Concept(tempNewUserSet, tempNewItemSet);
            tempCurrentSim = computeSimilarityOfConcepts(tempNewConcept);
        } while ((tempCurrentSim < tempMaxSim) && itemSet.length > paraItemThreshold);
        int[] resultUserSet = new int[count1];
        for (int i = 0; i < paraConcept.users.length; i++) {
            resultUserSet[i] = paraConcept.users[i];
        }//of for i
        for (int i = paraConcept.users.length; i < count2; i++) {
            resultUserSet[i] = tempUserAvaliable[i];
        }//of for i
        int[] resultItemSet = getSuperItems(resultUserSet);
        Concept resultConcept = new Concept(resultUserSet, resultItemSet);
        return resultConcept;
    }//of generateNewConceptByMutate

    /**
     * *************
     * Test the class.
     * *************
     */
    public static void main(String args[]) {
        RatingMatrix tempRatingMatrix = new RatingMatrix("src/data/training.arff", 943, 1682);
//		tempRatingMatrix.dp.readFile("src/data/200X420-1-train.txt");
//		System.out.println("The data is: " + tempRatingMatrix);
//		tempRatingMatrix.computeRepresentativeOrientedConcept(880, 2, 10);
//		int[] tempItemSet = {0, 3, 7, 18, 55};
//		boolean[] tempAvailable = {true, true, false, true, true};
//		int[] tempSet = tempRatingMatrix.getSuperUsers(tempItemSet, tempAvailable);
//		System.out.println("The neighbors are: " + Arrays.toString(tempSet));
//		System.out.println("Done");
//		int[] a = {1,2,3,4,5};
//		int[] b = {3,4,5,6,7};
//		int[] set = tempRatingMatrix.getMutationSet(a, b);
//		System.out.println(Arrays.toString(set));
        int[] userSet = {33, 112, 172, 591};
        int[] itemSet = tempRatingMatrix.getSuperItems(userSet);
        System.out.println(Arrays.toString(itemSet));
    }// Of main
}// Of class RatingMatrix