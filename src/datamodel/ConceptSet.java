package datamodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import common.SimpleTools;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;;

/**
 * @author wzy
 *
 */
public class ConceptSet {

    static int[][] recommendations;
    double[] similarityOfEachConcept;
    double[] similarityOfEachUser;
    int tempActualConcepts = 0;
    int tempActualConceptsForUC = 0;
    int tempActualOrConcepts = 0;
    int tempNumNewConcept = 0;
    int[] numUserConcept;
    int[][] userInclusion;
    /**
     * The minimal number of relevant concepts for each users. Attention: the name
     * is inappropriate.
     */
    public static final int MIN_CONCEPTS_PER_USER = 0;

    /**
     * The minimal value of similarity of two users;
     */
    public static final double MIN_SIMILARITY_OF_TWO_USER = 100;

    /**
     * The rating matrix object.
     */
    RatingMatrix ratingMatrix;

    DataProcessing dataProcessing = new DataProcessing();
    SimpleTools simpleTools = new SimpleTools();
    Measures measures = new Measures();

    /**
     * The number of users.
     */
    int numUsers;

    /**
     * The number of items.
     */
    int numItems;

    /**
     * An array storing all concepts.
     */
    Concept[] userOrientedConcepts;
    Concept[] conceptsArray;
    Concept[] conceptsArrayAfterFilter;
    Concept[] newConceptArray;
    /**
     * User-concept inclusion relationships. -1 stands for no more relevant concepts
     * for this user.
     */
    int[][] userConceptInclusionMatrix;// Two-dimensional relationship, matrix

    /**
     * How many concepts are relevant to this user.
     */
    int[] userConceptCounts;

    /**
     * User Sorted
     */
    int[] ascendantFever;

    /**
     * User Sorted
     */
    int[] decendantFever;
    int[] decendantFeverUC;
    /**
     * Represent
     */
    int[] represent;

    /**
     * Used to collect information about concept. Each concept recommend time and
     * correct time.
     */
    int[][] informationConcept;

    /**
     * Record the data from jester data excel file.
     */
    static double[][] information;

    /**
     * Record the ratings after standarlize.
     */
    double[][] informationAfterStan;

    /**
     * record which user generate new concept while cross and mutate.
     */
    static boolean[] recordUsers;

    /**
     * Only support relevant number of concepts for one user with this size.
     */
    public static final int USER_MAXIMAL_RELEVANT_CONCEPTS = 1000;

    /**
     * The first constructor
     *
     * @param paraMatrix
     */
    public ConceptSet(RatingMatrix paraMatrix) {
        ratingMatrix = paraMatrix;

        numUsers = ratingMatrix.numUsers();
        numItems = ratingMatrix.numItems();

        userOrientedConcepts = new Concept[numUsers];
        conceptsArray = new Concept[numUsers];
        conceptsArrayAfterFilter = new Concept[numUsers];
        newConceptArray = new Concept[20000];
        similarityOfEachConcept = new double[numUsers];
        similarityOfEachUser = new double[numUsers];
        userConceptInclusionMatrix = new int[numUsers][USER_MAXIMAL_RELEVANT_CONCEPTS];
        userConceptCounts = new int[numUsers];
        SimpleTools.matrixFill(userConceptInclusionMatrix, -1);
        information = new double[24983][100];
        informationAfterStan = new double[24983][100];

        recommendations = new int[numUsers][numItems];
        recordUsers = new boolean[numUsers];

        numUserConcept = new int[numUsers];
        userInclusion = new int[numUsers][USER_MAXIMAL_RELEVANT_CONCEPTS];
    }// Of the first constructor

    /**
     * Read data from excel file.
     *
     * @param
     * @return
     *
     */
    public void readDataFromExcel() {
        // TODO Auto-generated method stub
        String[][] tempInformation = new String[24983][100];
        try {
            File file = new File("src/data/jester-standarlize.xls");
            Workbook wb = Workbook.getWorkbook(file);
            Sheet sheet = wb.getSheet(0);
            for (int i = 0; i < sheet.getRows(); i++) {
                for (int j = 0; j < sheet.getColumns(); j++) {
                    Cell cell = sheet.getCell(j, i);
//					System.out.printf(cell.getContents() + " ");
                    tempInformation[i][j] = cell.getContents();
//					System.out.println(information[i][j]);
                } // of for j
//				System.out.println();
            } // of for i
        } catch (Exception e) {
            e.printStackTrace();
        } // of try
        information = exchangeStringToDouble(tempInformation);
        informationAfterStan = standarlize();
        System.out.println(Arrays.toString(information[0]));
        System.out.println(Arrays.toString(informationAfterStan[0]));
    }// of readDataFromExcel

    /**
     * exchange the String Array to double Array.
     *
     * @param paraInformation The String Array.
     * @return The int Array.
     */
    public double[][] exchangeStringToDouble(String[][] paraInformation) {
        double[][] tempInformation = new double[paraInformation.length][paraInformation[0].length];
        for (int i = 0; i < paraInformation.length; i++) {
            for (int j = 0; j < paraInformation[0].length; j++) {
                tempInformation[i][j] = Double.parseDouble(String.valueOf(paraInformation[i][j]));
            } // of for j
        } // of for i
        return tempInformation;
    }// of for exchangeStringToInt

    /**
     * compute S2
     *
     * @param @return
     * @return
     *
     */
    public double[][] standarlize() {
        double s2;
        double s;
        double sum = 0;
        double ave = 0;
        double up = 0;
        double count = 0;
        double[][] tempInformationAfterStan = new double[24983][100];
        // Step 1.compute average
        for (int i = 0; i < information.length; i++) {
            for (int j = 0; j < information[0].length; j++) {
                sum += information[i][j];
                count++;
            } // of for j
        } // of for i
        ave = sum / count;
        // Step 2.compute up
        for (int i = 0; i < information.length; i++) {
            for (int j = 0; j < information[0].length; j++) {
                up += (information[i][j] - ave) * (information[i][j] - ave);
            } // of for j
        } // of for i
        // Step 3.compute s2
        s = Math.sqrt(up / count);
        // standarlize
        for (int i = 0; i < information.length; i++) {
            for (int j = 0; j < information[0].length; j++) {
                tempInformationAfterStan[i][j] = (information[i][j] - ave) / s;
            } // of for j
        } // of for i
        return tempInformationAfterStan;
    }// of computeS2

    /**
     **************
     * Sort the user according to the number of items they have rated in descendant
     * order.
     *
     * @return the sorted indices.
     **************
     */
    public int[] computeDecendantFever() {
        // Step 1. Count the number of items for each user.
        int[] tempFever = ratingMatrix.getUserFever();

        // Step 2. Select the current least enthusiast
        boolean[] tempSelected = new boolean[numUsers];
        int[] resultArray = new int[numUsers];

        int tempMaximal;
        int tempUserIndex = -1;
        for (int i = 0; i < numUsers; i++) {
            tempMaximal = 0;
            for (int j = 0; j < numUsers; j++) {
                if (tempSelected[j]) {
                    continue;
                } // Of if

                if (tempFever[j] >= tempMaximal) {
                    tempMaximal = tempFever[j];
                    tempUserIndex = j;
                } // Of if
            } // Of for j

            resultArray[i] = tempUserIndex;
            tempSelected[tempUserIndex] = true;
        } // Of for i
        return resultArray;
    }// Of computeDecendantFever

    int[] getUserFever(int[][] paraFormalContext) {
        int[] tempFever = new int[paraFormalContext.length];
        for (int i = 0; i < paraFormalContext.length; i++) {
            for (int j = 0; j < paraFormalContext[0].length; j++) {
                if (paraFormalContext[i][j] == 1) {
                    tempFever[i] ++;
                }//Of if
            }//Of for j
        }//Of for i

        return tempFever;
    }//end getUserFever
    /**
     **************
     * Sort the user according to the number of items they have rated in descendant
     * order.
     *
     * @return the sorted indices.
     **************
     */
    public int[] computeDecendantFever(int[][] paraFormalContext) {
        // Step 1. Count the number of items for each user.
        int[] tempFever = getUserFever(paraFormalContext);

        // Step 2. Select the current least enthusiast
        boolean[] tempSelected = new boolean[paraFormalContext.length];
        int[] resultArray = new int[paraFormalContext.length];

        int tempMaximal;
        int tempUserIndex = -1;
        for (int i = 0; i < paraFormalContext.length; i++) {
            tempMaximal = 0;
            for (int j = 0; j < paraFormalContext.length; j++) {
                if (tempSelected[j]) {
                    continue;
                } // Of if

                if (tempFever[j] >= tempMaximal) {
                    tempMaximal = tempFever[j];
                    tempUserIndex = j;
                } // Of if
            } // Of for j

            resultArray[i] = tempUserIndex;
            tempSelected[tempUserIndex] = true;
        } // Of for i
        return resultArray;
    }// Of computeDecendantFever

    public boolean[] findUserHaveNoFilm() {
        boolean[] recordUser = new boolean[numUsers];
        Arrays.fill(recordUser, false);
        for (int i = 0; i < ratingMatrix.formalContext.length; i++) {
            int count = 0;
            for (int j = 0; j < ratingMatrix.formalContext[0].length; j++) {
                if (ratingMatrix.formalContext[i][j] == 1) {
                    count++;
                }
            }
            if (count != 0) {
                recordUser[i] = true;
            }
        }
        return recordUser;
    }

    /**
     **************
     * Generate all the concept for the formal context.
     **************
     */
    public int generateConcept(int paraUsersThreshold, int paraItemsThreshold) {
        Concept resultConcept = null;
        int count = 0;
        for (int i = 0; i < ratingMatrix.formalContext.length; i++) {
            Concept[] tempNewConcept = ratingMatrix.computeRepresentativeAllOrientedConcept(i, paraItemsThreshold);
            for (int j = 0; j < tempNewConcept.length; j++) {
                count++;
                System.out.println(
                        Arrays.toString(tempNewConcept[j].users) + "," + Arrays.toString(tempNewConcept[j].items));
            }
        } // of for i
        System.out.println(count);
        return count;
    }// of generateConcept

    /**
     **************
     * Generate the concept using the user fever in descending order.
     *
     * @param paraItemsThreshold The threshold for common items.
     * @param paraUsersThreshold The threshold for common users.
     **************
     */
    public int generateDeConceptSet(int paraUsersThreshold, int paraItemsThreshold) {
//		boolean[] recordUser = new boolean[numUsers];
//		recordUser = findUserHaveNoFilm();
        // Step 1. Sort the users according to their fever.
        decendantFever = computeDecendantFever();
        // System.out.println("decent: " + Arrays.toString(decendantFever));
        // Step 2. Generate concepts according to users.
        for (int i = 0; i < decendantFever.length; i++) {
//			if (recordUser[decendantFever[i]]) {
            // Step 2.1 Is this user covered?
//            if (userConceptCounts[decendantFever[i]] > MIN_CONCEPTS_PER_USER) {
//                continue;
//            } // Of if
            // Step 2.2 Construct a concept using this user.
//			System.out.println("for user " + decendantFever[i] + ":");
            Concept tempNewConcept = ratingMatrix.computeRepresentativeOrientedConcept(decendantFever[i],
                    paraItemsThreshold);
            // Step 2.3 Record the concept.
            userOrientedConcepts[decendantFever[i]] = tempNewConcept;
//			System.out.println(Arrays.toString(tempNewConcept.users) + Arrays.toString(tempNewConcept.items));
            // Step 2.4 Update the user-concept relationships.
            int[] tempUsers = tempNewConcept.getUsers();
            for (int j = 0; j < tempUsers.length; j++) {
                userConceptInclusionMatrix[tempUsers[j]][userConceptCounts[tempUsers[j]]] = decendantFever[i];
                userConceptCounts[tempUsers[j]]++;
            } // Of for j
//			}
        } // Of for i

        for (int i = 0; i < numUsers; i++) {
            if (userOrientedConcepts[i] != null) {
                tempActualConcepts++;
            } // Of if
        } // Of for i
		System.out.println("There are " + tempActualConcepts + " concepts.");
//		@SuppressWarnings("unused")
        int tempCount = 0;
        for (int i = 0; i < userConceptCounts.length; i++) {
            tempCount += userConceptCounts[i];
        } // of for i
        return tempActualConcepts;

    }// Of generateDeConceptSet

    /**
     **************
     * Generate the concept for concept update.
     *
     * @param paraItemsThreshold The threshold for common items.
     * @param paraUsersThreshold The threshold for common users.
     **************
     */
    public int generateConceptSet(int paraUsersThreshold, int paraItemsThreshold, int[][] paraFormalContext, int[] paraRandom) {
        // Step 1. Generate concepts according to users.
        for (int i = 0; i < paraFormalContext.length; i++) {
            // Step 1.1 Construct a concept using this user.
            Concept tempNewConcept = ratingMatrix.computeRepresentativeOrientedConcept(i, paraItemsThreshold,
                    paraRandom, paraFormalContext);
            // Step 1.2 Check is the concept repeat?
            if (tempActualConceptsForUC == 0) {
                conceptsArray[tempActualConceptsForUC] = tempNewConcept;
				System.out.println(Arrays.toString(tempNewConcept.users) + "," + Arrays.toString(tempNewConcept.items));
                tempActualConceptsForUC++;
            } else {
                int count = 0;
                for (int j = 0; j < tempActualConceptsForUC; j++) {
                    boolean isEqual = Arrays.equals(conceptsArray[j].users, tempNewConcept.users);
                    if (!isEqual) {
                        count++;
                    } else {
                        break;
                    } // of if
                    if (count == tempActualConceptsForUC) {
                        conceptsArray[tempActualConceptsForUC] = tempNewConcept;
						System.out.println(Arrays.toString(tempNewConcept.users) + "," + Arrays.toString(tempNewConcept.items));
                        tempActualConceptsForUC++;
                        break;
                    } // of if
                } // of for j
            } // of if
        } // of for i
//		System.out.println("There are " + tempActualConcepts + "concepts");
        return tempActualConceptsForUC;
    }// Of generateDeConceptSet
    /**
     **************
     * Generate the concept using the user fever in descending order.
     *
     * @param paraItemsThreshold The threshold for common items.
     * @param paraUsersThreshold The threshold for common users.
     **************
     */
    public int generateDeConceptSet(int paraUsersThreshold, int paraItemsThreshold, int[][] paraFormalContext, int[] random) {
        // Step 1. Sort the users according to their fever.
        decendantFeverUC = computeDecendantFever(paraFormalContext);
        // System.out.println("decent: " + Arrays.toString(decendantFever));
        // Step 2. Generate concepts according to users.
        for (int i = 0; i < decendantFeverUC.length; i++) {
//			if (recordUser[decendantFever[i]]) {
            // Step 2.1 Is this user covered?
//            if (userConceptCounts[decendantFeverUC[i]] > MIN_CONCEPTS_PER_USER) {
//                continue;
//            } // Of if
            // Step 2.2 Construct a concept using this user.
//			System.out.println("for user " + decendantFever[i] + ":");
            Concept tempNewConcept = ratingMatrix.computeRepresentativeOrientedConcept(decendantFeverUC[i],
                    paraItemsThreshold, random, paraFormalContext);
            if (tempNewConcept.users.length > 1) {
                System.out.println(Arrays.toString(tempNewConcept.users) + Arrays.toString(tempNewConcept.items));
                // Step 2.3 Record the concept.
                conceptsArray[tempActualConceptsForUC] = tempNewConcept;
                tempActualConceptsForUC++;
            }//of if
        }//of for i
        return tempActualConcepts;
    }//end generateDeConceptSet
    /**
     **************
     * Generate the concept using the user fever in descending order.
     *
     * @param paraOrConcept The threshold for common items.
     * @param paraNewConcept The threshold for common users.
     **************
     */
    public void updateConceptSet(Concept[] paraConceptsArray, int paraOrConcept, int paraNewConcept) {
        // for every concept in orConceptArray.
        for (int i = 0; i < paraOrConcept; i++) {
//			int count = 0;
            // for every concept in newConceptArray.
            for (int j = paraOrConcept; j < paraOrConcept + paraNewConcept; j++) {
                // generate intension by intersection.
                int[] intension = dataProcessing.intersection(conceptsArray[i].items, conceptsArray[j].items);
                int[] extension = null;
                if (intension.length == 0) {
                    continue;
//					count++;
                } else if (Arrays.equals(intension, conceptsArray[i].items)) {// b1&b2=b1,update c1,retain c2
                    // update c1
                    extension = dataProcessing.mergeArrays(conceptsArray[i].users, conceptsArray[j].users);
                    if (extension.length > 1) {
                        Concept tempConcept = new Concept(extension, intension);
                        if (isRepeat(tempConcept)) {
                            newConceptArray[tempNumNewConcept] = tempConcept;
//							System.out.println(Arrays.toString(newConceptArray[tempNumNewConcept].users)
//									+ Arrays.toString(newConceptArray[tempNumNewConcept].items));
                            tempNumNewConcept++;
                        } // of if
                        // retain c2
                        if (isRepeat(conceptsArray[j])) {
                            newConceptArray[tempNumNewConcept] = conceptsArray[j];
//							System.out.println(
//									Arrays.toString(conceptsArray[j].users) + Arrays.toString(conceptsArray[j].items));
                            tempNumNewConcept++;
                        } // of if
                    } else {
                        continue;
                    }//of if
                } else if (Arrays.equals(intension, conceptsArray[j].items)) {// b1&b2=b2,update c2,retain c1
                    // update c2
                    extension = dataProcessing.mergeArrays(conceptsArray[i].users, conceptsArray[j].users);
                    if (extension.length > 1) {
                        Concept tempConcept = new Concept(extension, intension);
                        if (isRepeat(tempConcept)) {
                            newConceptArray[tempNumNewConcept] = tempConcept;
//						System.out.println(Arrays.toString(newConceptArray[tempNumNewConcept].users)
//								+ Arrays.toString(newConceptArray[tempNumNewConcept].items));
                            tempNumNewConcept++;
                        } // of if
                        // retain c1
                        if (isRepeat(conceptsArray[i])) {
                            newConceptArray[tempNumNewConcept] = conceptsArray[i];
//						System.out.println(
//								Arrays.toString(conceptsArray[i].users) + Arrays.toString(conceptsArray[i].items));
                            tempNumNewConcept++;
                        } // of if
                    } else {
                        continue;
                    } // of if
                } else {
                    // generate c3
                    extension = dataProcessing.mergeArrays(conceptsArray[i].users, conceptsArray[j].users);
                    if (extension.length > 1) {
                        Concept tempConcept = new Concept(extension, intension);
                        if (isRepeat(tempConcept)) {
                            newConceptArray[tempNumNewConcept] = tempConcept;
//						System.out.println(Arrays.toString(newConceptArray[tempNumNewConcept].users)
//								+ Arrays.toString(newConceptArray[tempNumNewConcept].items));
                            tempNumNewConcept++;
                        } // of if
                        // retain c1 and c2
                        if (isRepeat(conceptsArray[i])) {
                            newConceptArray[tempNumNewConcept] = conceptsArray[i];
//						System.out.println(
//								Arrays.toString(conceptsArray[i].users) + Arrays.toString(conceptsArray[i].items));
                            tempNumNewConcept++;
                        } // of if
                        if (isRepeat(conceptsArray[j])) {
                            newConceptArray[tempNumNewConcept] = conceptsArray[j];
//						System.out.println(
//								Arrays.toString(conceptsArray[j].users) + Arrays.toString(conceptsArray[j].items));
                            tempNumNewConcept++;
                        } // of if
                    } else {
                        continue;
                    } // of if
                } // of if
//				if (count == paraNewConcept) {
//					newConceptArray[tempNumNewConcept] = conceptsArray[i];
//					System.out.println(Arrays.toString(newConceptArray[tempNumNewConcept].users) + Arrays.toString(newConceptArray[tempNumNewConcept].items));
//					tempNumNewConcept ++;
//				}//of if
            } // of for j
        } // of for i
        System.out.println("tempNunNewConcepts: " + tempNumNewConcept);
    }// of updateConceptSet

    /**
     * is concept repeat true means not repeat false means repeat
     */
    public boolean isRepeat(Concept paraConcept) {
        int count = 0;
        boolean f = false;
        if (tempNumNewConcept == 0) {
            f = true;
        } else {
            for (int i = 0; i < tempNumNewConcept; i++) {
                if (!Arrays.equals(paraConcept.users, newConceptArray[i].users)) {
                    if (!Arrays.equals(paraConcept.items, newConceptArray[i].items)) {
                        count++;
                    } // of if
                } // of if
                if (count == tempNumNewConcept) {
                    f = true;
                } else {
                    f = false;
                } // of if
            } // of for i
        } // of if
        return f;
    }// of isRepeat

    /**
     * replace a user with a more similar user
     *
     * @param paraUser
     * @param paraConcept
     * @param paraInsteadUser
     * @param paraInsteadUserIndex
     * @return The new concept after replace
     */
    public Concept replace(int paraUser, Concept paraConcept, int paraInsteadUser, int paraInsteadUserIndex) {
        Concept resultConcept = null;
        for (int i = 0; i < paraConcept.users.length; i++) {
            resultConcept.users[i] = paraConcept.users[i];
        } // of for i
        // replace
        // step 1.compute similarity with each user
        double[] sim = ratingMatrix.computeSimilarityOfEachUser(paraUser);
        boolean[] temp = new boolean[numUsers];
        for (int i = 0; i < paraConcept.users.length; i++) {
            temp[paraConcept.users[i]] = true;
        } // of for i
        // step 2.find the most similar user to replace the user
        double maxSim = -1;
        int maxSimUserIndex = -1;
        for (int i = 0; i < temp.length; i++) {
            if (sim[i] > maxSim && !temp[i]) {
                maxSimUserIndex = i;
                maxSim = sim[i];
            } // of if
        } // of for i
        // step 3.replace
        paraConcept.users[paraInsteadUserIndex] = maxSimUserIndex;
        return resultConcept;
    }// of for replace

    /**
     * Exchange the two concept at the cross site.
     *
     * @param paraConcept1
     * @param paraConcept2
     * @param paraCrossSite
     * @param paraUser
     * @return New Concept after exchange.
     */
    public Concept[] exchange(Concept paraConcept1, Concept paraConcept2, int paraCrossSite, int paraUser) {
        Concept[] result = new Concept[2];
        double sim1 = ratingMatrix.computeSimilarityOfConcepts(paraConcept1);
        double sim2 = ratingMatrix.computeSimilarityOfConcepts(paraConcept2);

        int[] extension1 = new int[paraConcept2.users.length];
        int[] extension2 = new int[paraConcept1.users.length];

        for (int i = 0; i <= paraCrossSite; i++) {
            extension1[i] = paraConcept1.users[i];
            extension2[i] = paraConcept2.users[i];
        } // of for i
        for (int i = paraCrossSite + 1; i < paraConcept2.users.length; i++) {
            extension1[i] = paraConcept2.users[i];
        } // of for i
        for (int i = paraCrossSite + 1; i < paraConcept1.users.length; i++) {
            extension2[i] = paraConcept1.users[i];
        } // of for i
        int a = isHave(extension1, paraUser);
        if (a == 0) {
            // the new extension not contain the object user,it is a invalid extension
            result[0] = null;
        } else if (a == 1) {
            // the new extension contain the object user,generate a new concept and compute
            // similarity value
            int[] intension1 = ratingMatrix.getSuperItems(extension1);
            if (intension1.length > 1) {
                Concept tempConcept1 = new Concept(extension1, intension1);
                double sim = ratingMatrix.computeSimilarityOfConcepts(tempConcept1);
                if (sim >= sim1 || sim >= sim2) {
                    result[0] = tempConcept1;
                } // of if
            } // of if
        } else {
            // the new extension contain more than one object user
            extension1 = isSame(extension1, paraUser);
            // generate new concept
            int[] intension1 = ratingMatrix.getSuperItems(extension1);
            if (intension1.length > 1) {
                Concept tempConcept1 = new Concept(extension1, intension1);
                double sim = ratingMatrix.computeSimilarityOfConcepts(tempConcept1);
                if (sim >= sim1 || sim >= sim2) {
                    result[0] = tempConcept1;
                } // of if
            } // of if
        } // of if
        int b = isHave(extension2, paraUser);
        if (b == 0) {
            // the new extension not contain the object user,it is a invalid extension
            result[1] = null;
        } else if (b == 1) {
            // the new extension contain the object user,generate a new concept and compute
            // similarity value
            int[] intension2 = ratingMatrix.getSuperItems(extension2);
            if (intension2.length > 1) {
                Concept tempConcept2 = new Concept(extension2, intension2);
                double sim = ratingMatrix.computeSimilarityOfConcepts(tempConcept2);
                if (sim >= sim1 || sim >= sim2) {
                    result[1] = tempConcept2;
                } // of if
            } // of if
        } else {
            // the new extension contain more than one object user
            extension2 = isSame(extension2, paraUser);
            // generate new concept
            int[] intension2 = ratingMatrix.getSuperItems(extension2);
            if (intension2.length > 1) {
                Concept tempConcept2 = new Concept(extension2, intension2);
                double sim = ratingMatrix.computeSimilarityOfConcepts(tempConcept2);

                if (sim >= sim1 || sim >= sim2) {
                    result[1] = tempConcept2;
                } // of if
            } // of if
        } // of if
        return result;
    }// of exchange

    /**
     * Is the given user set include the given user?
     *
     * @param paraUserSet
     * @param paraUser
     * @return The result.0 means not have,1 means have one,2 means the given user
     *         is duplicate.
     */
    public int isHave(int[] paraUserSet, int paraUser) {
        int count = 0;
        for (int i = 0; i < paraUserSet.length; i++) {
            if (paraUserSet[i] == paraUser) {
                count++;
            } // of for i
        } // of for i
        if (count == 0) {
            return 0;
        } else if (count == 1) {
            return 1;
        } else {
            return 2;
        } // of if
    }// of isHave

    /**
     ********************
     * Whether there are duplicate elements in the given user set?
     *
     * @param paraUserSet The given user set.
     * @param paraUser    The given user.
     * @return The result user set without duplicate.
     ********************
     */
    public int[] isSame(int[] paraUserSet, int paraUser) {
        List list = new ArrayList();
        for (int i = 0; i < paraUserSet.length; i++) {
            if (!list.contains(paraUserSet[i])) {
                list.add(paraUserSet[i]);
            } // of if
        } // of for i
        int[] resultUserSet = new int[list.size()];
        for (int i = 0; i < resultUserSet.length; i++) {
            resultUserSet[i] = (int) list.get(i);
        } // of for i
        return resultUserSet;
    }// of isSame

    /**
     ********************
     * Is the given two concepts same?
     *
     * @param paraConcept1 The first given concept.
     * @param paraConcept2 The second given concept.
     * @return The result.true means same,false means not same
     ********************
     */
    public static boolean isConceptSame(Concept paraConcept1, Concept paraConcept2) {
        if (paraConcept1.items.length != paraConcept2.items.length
                || paraConcept1.users.length != paraConcept2.users.length) {
            return false;
        } // Of if
        for (int i = 0; i < paraConcept1.items.length; i++) {
            if (paraConcept1.items[i] != paraConcept2.items[i]) {
                return false;
            } // Of if
        } // Of for i
        return true;
    }// Of isConceptSame

    /**
     * GCGA
     *
     * @param paraUser
     * @param paraLowUserThreshold
     * @param paraItemThreshold
     */
//	public void GCGA(int paraUser, int paraLowUserThreshold, int paraItemThreshold) {
//		// Cross
//		Concept[] newConceptOfCross = new Concept[200];
//		Concept[] newConcept = new Concept[200];
//		int count1 = 0;
//		for (int i = 0; i < userConceptCounts[paraUser] - 1; i++) {
//			for (int j = i + 1; j < userConceptCounts[paraUser]; j++) {
//				// generate new concept by cross
//				Concept tempNewConcept = ratingMatrix.getNewConceptByCrossUsers(
//						userOrientedConcepts[userConceptInclusionMatrix[paraUser][i]],
//						userOrientedConcepts[userConceptInclusionMatrix[paraUser][j]]);
//				// is the new concept effective?
//				if ((tempNewConcept.users.length > 1) && (tempNewConcept.items.length > 0)) {
//					// is the new concept better than the old concept?
//					if (ratingMatrix.computeSimilarityOfConcepts(tempNewConcept) > ratingMatrix
//							.computeSimilarityOfConcepts(userOrientedConcepts[userConceptInclusionMatrix[paraUser][i]])
//							|| ratingMatrix.computeSimilarityOfConcepts(tempNewConcept) > ratingMatrix
//									.computeSimilarityOfConcepts(
//											userOrientedConcepts[userConceptInclusionMatrix[paraUser][j]])) {
//						// is the concept same as before
//						// true means same,false means not same
//						if (count1 == 0) {
//							// record the concept
//							newConceptOfCross[count1] = tempNewConcept;
//							System.out.println("after cross(0): " + Arrays.toString(newConceptOfCross[count1].users)
//									+ Arrays.toString(newConceptOfCross[count1].items));
//							count1++;
//						} else {
//							boolean isSame;
//							for (int k = 0; k < count1; k++) {
//								isSame = isConceptSame(tempNewConcept, newConceptOfCross[k]);
//								if (isSame) {
//									break;
//								} else {
//									if (k == count1 - 1) {
//										// record the new concept
//										newConceptOfCross[count1] = tempNewConcept;
//										System.out.println(
//												"after cross: " + Arrays.toString(newConceptOfCross[count1].users)
//														+ Arrays.toString(newConceptOfCross[count1].items));
//										count1++;
//									} // of if
//								} // of if
//							} // of for k
//						} // of if
//					} // of if
//				} // of if
//			} // of for j
//		} // of for i
//
//		// mutate
//		int count2 = 0;
//		for (int i = 0; i < count1; i++) {
//			if (newConceptOfCross[i].users.length <= paraLowUserThreshold) {
//
//				// generate a concept by mutate
//				Concept tempNewConcept = ratingMatrix.mutateOrientedConcepts(paraUser, paraItemThreshold,
//						newConceptOfCross[i]);
//				if (count2 == 0) {
//					// record the concept
//					newConcept[count2] = tempNewConcept;
//
//					System.out.println("after mutate(0): " + Arrays.toString(newConcept[count2].users)
//							+ Arrays.toString(newConcept[count2].items));
//					count2++;
//				} else {
//					// is the concept same
//					// true means same,false means not same
//					boolean isSame;
//					for (int k = 0; k < count2; k++) {
//						isSame = isConceptSame(tempNewConcept, newConcept[k]);
//						if (isSame) {
//							break;
//						} else {
//							if (k == count2 - 1) {
//								// record the new concept
//								newConcept[count2] = tempNewConcept;
//								System.out.println("after mutate:  " + Arrays.toString(newConcept[count2].users)
//										+ Arrays.toString(newConcept[count2].items));
//								count2++;
//							} // of if
//						} // of if
//					} // of for k
//				} // of if
//			} else {
//				newConcept[count2] = newConceptOfCross[i];
//				count2++;
//			} // of if
//		} // of for i
//
//		// final
////		if (count2 != 0) {
////			recordUsers[paraUser] = true;
////		} // of if
//		// use old concept to recommend
////		Concept[] userConcepts = new Concept[userConceptCounts[paraUser]];
////		for (int i = 0; i < userConceptCounts[paraUser]; i++) {
////			userConcepts[i] = userOrientedConcepts[userConceptInclusionMatrix[paraUser][i]];
////		} // of for i
////		int[] tempRecommendation;
////		tempRecommendation = recommendationTest(paraUser, 0.5, userConcepts);
////		for (int j = 0; j < tempRecommendation.length; j++) {
////			recommendations[paraUser][j] = tempRecommendation[j];
////		} // of for j
//		// use new concept to recommend
//		if (count2 != 0) {
//			Concept[] finalConcept = new Concept[count2];
//			for (int i = 0; i < finalConcept.length; i++) {
//				finalConcept[i] = newConcept[i];
//			} // of for i
//
////			int[] tempRecommendations;
////			tempRecommendations = recommendationTest(paraUser, 0.5, finalConcept);
////			for (int i = 0; i < tempRecommendations.length; i++) {
////				recommendations[paraUser][i] = tempRecommendations[i];
////			} // of for i
//
//			int[][] tempRecommendations;
//			tempRecommendations = recommendationToAllUsers(0.5, finalConcept);
//			for (int i = 0; i < tempRecommendations.length; i++) {
//				for (int j = 0; j < tempRecommendations[0].length; j++) {
//					recommendations[i][j] = tempRecommendations[i][j];
//				} // of for j
//			} // of for i
//		} // of if
//	}// of GCGAv

//	public void GCGAv6(int paraUser, int paraLowUserThreshold, int paraItemThreshold) {
//		// Cross
//		Concept[] newConceptOfCross = new Concept[2000];
//		Concept[] newConcept = new Concept[2000];
//		int count1 = 0;
//		for (int i = 0; i < userConceptCounts[paraUser] - 1; i++) {
//			for (int j = i + 1; j < userConceptCounts[paraUser]; j++) {
//				// generate new concept by cross
//				Concept tempNewConcept = ratingMatrix.getNewConceptByCrossUsers(
//						userOrientedConcepts[userConceptInclusionMatrix[paraUser][i]],
//						userOrientedConcepts[userConceptInclusionMatrix[paraUser][j]]);
//				// is the new concept effective锟斤拷
//				if ((tempNewConcept.users.length > 1) && (tempNewConcept.items.length > 0)) {
//					// is the new concept better than the old concept?
//					if (ratingMatrix.computeSimilarityOfConcepts(tempNewConcept) > ratingMatrix
//							.computeSimilarityOfConcepts(userOrientedConcepts[userConceptInclusionMatrix[paraUser][i]])
//							|| ratingMatrix.computeSimilarityOfConcepts(tempNewConcept) > ratingMatrix
//									.computeSimilarityOfConcepts(
//											userOrientedConcepts[userConceptInclusionMatrix[paraUser][j]])) {
//						// is the concept same as before
//						// true means same,false means not same
//						if (count1 == 0) {
//							// record the concept
//							newConceptOfCross[count1] = tempNewConcept;
////							System.out.println("after cross(0): " + Arrays.toString(newConceptOfCross[count1].users)
////									+ Arrays.toString(newConceptOfCross[count1].items));
//							count1++;
//						} else {
//							boolean isSame;
//							for (int k = 0; k < count1; k++) {
//								isSame = isConceptSame(tempNewConcept, newConceptOfCross[k]);
//								if (isSame) {
//									break;
//								} else {
//									if (k == count1 - 1) {
//										// record the new concept
//										newConceptOfCross[count1] = tempNewConcept;
////										System.out.println("after cross: " + Arrays.toString(newConceptOfCross[count1].users)
////												+ Arrays.toString(newConceptOfCross[count1].items));
//										count1++;
//									} // of if
//								} // of if
//							} // of for k
//						} // of if
//					} // of if
//				} // of if
//			} // of for j
//		} // of for i
//
//		// mutate
//		int count2 = 0;
//		for (int i = 0; i < count1; i++) {
//			if (newConceptOfCross[i].users.length <= paraLowUserThreshold) {
//
//				// generate a concept by mutate
//				Concept tempNewConcept = ratingMatrix.mutateOrientedConcepts(paraUser, paraItemThreshold,
//						newConceptOfCross[i]);
//				if (count2 == 0) {
//					// record the concept
//					newConcept[count2] = tempNewConcept;
//
////					System.out.println("after mutate(0): " + Arrays.toString(newConcept[count2].users)
////							+ Arrays.toString(newConcept[count2].items));
//					count2++;
//				} else {
//					// is the concept same
//					// true means same,false means not same
//					boolean isSame;
//					for (int k = 0; k < count2; k++) {
//						isSame = isConceptSame(tempNewConcept, newConcept[k]);
//						if (isSame) {
//							break;
//						} else {
//							if (k == count2 - 1) {
//								// record the new concept
//								newConcept[count2] = tempNewConcept;
////								System.out.println("after mutate: " + Arrays.toString(newConcept[count2].users)
////										+ Arrays.toString(newConcept[count2].items));
//								count2++;
//							} // of if
//						} // of if
//					} // of for k
//				} // of if
//			} else {
//				newConcept[count2] = newConceptOfCross[i];
//				count2++;
//			} // of if
//		} // of for i
//
//		// final
//		if (count2 != 0) {
//			recordUsers[paraUser] = true;
//		} // of if
//			// use old concept to recommend
////		Concept[] userConcepts = new Concept[userConceptCounts[paraUser]];
////		for (int i = 0; i < userConceptCounts[paraUser]; i++) {
////			userConcepts[i] = userOrientedConcepts[userConceptInclusionMatrix[paraUser][i]];
////		}//of for i
////		int[] tempRecommendation;
////		tempRecommendation = recommendationTest(paraUser,0.5,userConcepts);
////		for (int j = 0; j < tempRecommendation.length; j++) {
////			recommendations[paraUser][j] = tempRecommendation[j];
////		} // of for j
//		// use new concept to recommend
//		if (count2 != 0) {
//			Concept[] finalConcept = new Concept[count2];
//			for (int i = 0; i < finalConcept.length; i++) {
//				finalConcept[i] = newConcept[i];
//			} // of for i
//			int[][] tempRecommendations;
//			tempRecommendations = recommendationToAllUsers(0.5, finalConcept);
//			for (int i = 0; i < tempRecommendations.length; i++) {
//				for (int j = 0; j < tempRecommendations[0].length; j++) {
////					if (recommendations[i][j] == 0) {
//					recommendations[i][j] += tempRecommendations[i][j];
////					}//of if
//				} // of for j
//			} // of for i
//		} // of if
//
//	}// of GCGAv6

    /**
     **************
     * Validate the recommendation while this rating matrix serves as the test set
     * Record the contribution of each concept
     *
     * @param paraRecommendations The recommendation in matrix, storing recommended
     *                            concept index above -1 indicates recommend, -1
     *                            indicates not recommend.
     **************
     */
    public double[] validateRecommendationOfGCGA(boolean[][] paraTestMatrix, boolean[] paraRecordUsers,
                                                 int[][] paraRecommendations) {
        double tempRecommendation = 0;
        double tempCorrect = 0;
        double tempRated = 0;
        double tempTP = 0;
        double tempFP = 0;
        double tempTN = 0;
        double tempFN = 0;
        double[] tempResult = new double[3];
        for (int i = 0; i < paraRecordUsers.length; i++) {
            if (paraRecordUsers[i]) {
                for (int j = 0; j < paraRecommendations[i].length; j++) {
                    if ((paraRecommendations[i][j] == 0) && (!paraTestMatrix[i][j])) {
                        tempTN++;
                    } else if ((paraRecommendations[i][j] == 0) && (paraTestMatrix[i][j])) {
                        tempFP++;
                        tempRated++;
                    } else if ((paraRecommendations[i][j] > 0) && (!paraTestMatrix[i][j])) {
                        tempFN++;
                        tempRecommendation++;
                    } else if ((paraRecommendations[i][j] > 0) && (paraTestMatrix[i][j])) {
                        tempTP++;
                        tempRecommendation++;
                        tempRated++;
                        tempCorrect++;
                    } // Of if
                } // of for j
            } // of if
        } // of for i
        System.out.println("tempCorrect: " + tempCorrect);
        System.out.println("tempRecommendation: " + tempRecommendation);
        System.out.println("tempRated: " + tempRated);
        double tempPrecision = tempCorrect / tempRecommendation;
        double tempRecall = tempCorrect / tempRated;
        tempResult[0] = tempPrecision;
        tempResult[1] = tempRecall;
        tempResult[2] = (2 * tempPrecision * tempRecall) / (tempPrecision + tempRecall);
        System.out.println("Precision = " + tempPrecision + " recall = " + tempRecall + " F1 = "
                + (2 * tempPrecision * tempRecall) / (tempPrecision + tempRecall));
        double TPR = tempTP / (tempTP + tempFN);
        double FPR = tempFP / (tempFP + tempTN);
        System.out.println("TPR: " + TPR);
        System.out.println("FPR: " + FPR);
        return tempResult;
    }// Of validateRecommendationOfGCGAv6

    /**
     **************
     * Concept set based recommendation.
     *
     * @param paraThreshold The threshold for recommendation.
     **************
     */
    public int[][] recommendation(double paraThreshold) {
        int[][] resultRecommendations = new int[numUsers][numItems];
        boolean[] tempRecommendations;
        System.out.println("userConceptCounts：" + Arrays.toString(userConceptCounts));
        for (int i = 0; i < numUsers; i++) {
            for (int j = 0; j < userConceptCounts[i]; j++) {
                tempRecommendations = ratingMatrix.userConceptBasedRecommendation(i,
                        userOrientedConcepts[userConceptInclusionMatrix[i][j]], paraThreshold);
                for (int k = 0; k < numItems; k++) {
                    if (tempRecommendations[k]) {
                        resultRecommendations[i][k]++;
                    } // of if
                } // of for k
            } // of for j
        } // of for i
//		System.out.println("recommendations: " + Arrays.toString(resultRecommendations[0]));
        for (int i = 0; i < resultRecommendations.length; i++) {
            for (int j = 0; j < resultRecommendations[0].length; j++) {
                if (resultRecommendations[i][j] < 2) {
                    resultRecommendations[i][j] = 0;
                }//of if
            }//of for j
        }//of for i
        return resultRecommendations;
    }// Of recommendation

    /**
     * Get userConceptsForUC
     */
    public void getUserConceptsForUC () {
        for (int i = 0; i < numUsers; i++) {
            for (int j = 0; j < tempActualConceptsForUC; j++) {
                if (isContain(i, conceptsArray[j].users)) {
                    userInclusion[i][numUserConcept[i]] = j;
                    numUserConcept[i] ++;
                }
            }//of for j
        }//of for i
//		System.out.println(Arrays.toString(numUserConcept));
    }//of getUserConceptForUC

    /**
     * recommendation for update concepts.
     */
    public int[][] recommendationForUC(double paraThreshold) {
        int[][] resultRecommendations = new int[numUsers][numItems];
        boolean[] tempRecommendations;
        getUserConceptsForUC();

        System.out.println("numUserConcept: " + Arrays.toString(numUserConcept));
        for (int i = 0; i < numUsers; i++) {
            if (numUserConcept[i] == 0) {
                for (int j = 0; j < userConceptCounts[i]; j++) {
                    tempRecommendations = ratingMatrix.userConceptBasedRecommendation(i,
                            userOrientedConcepts[userConceptInclusionMatrix[i][j]], paraThreshold);
                    for (int k = 0; k < numItems; k++) {
                        if (tempRecommendations[k]) {
                            resultRecommendations[i][k]++;
                        } // of if
                    } // of for k
                } // of for j
            } else {
                for (int j = 0; j < numUserConcept[i]; j++) {
                    tempRecommendations = ratingMatrix.userConceptBasedRecommendation(i,
                            conceptsArray[userInclusion[i][j]], paraThreshold);
                    for (int k = 0; k < numItems; k++) {
                        if (tempRecommendations[k]) {
                            resultRecommendations[i][k]++;
                        } // of if
                    } // of for k
                }//of for j
            }//of for i
            for (int j = 0; j < resultRecommendations.length; j++) {
                for (int k = 0; k < resultRecommendations[0].length; k++) {
                    if (resultRecommendations[j][k] < 2) {
                        resultRecommendations[j][k] = 0;
                    }//of if
                }//of for j
            }//of for i
        }
        return resultRecommendations;
    }//of recommendationForUC

    /**
     * Judge an array contain object user or not.
     * @param paraUser
     * @param paraUserSet
     * @return
     */
    public boolean isContain (int paraUser, int[] paraUserSet) {
        boolean f = false;
        for (int i = 0; i < paraUserSet.length; i++) {
            if (paraUserSet[i] == paraUser) {
                f = true;
                break;
            } else {
                f = false;
            }
        }//of for i
        return f;
    }//of isContain

    /**
     * *************
     * Validate the recommendation while this rating matrix serves as the test set
     * Record the contribution of each concept
     *
     * @param paraRecommendations The recommendation in matrix, storing recommended
     *                            concept index above -1 indicates recommend, -1
     *                            indicates not recommend.
     *                            *************
     */
    public double[] validateRecommendation(int[][] paraTestMatrix, int[][] paraRecommendations) {

        double tempRecommendation = 0;
        double tempCorrect = 0;
        double tempRated = 0;
        double tempTP = 0;
        double tempFP = 0;
        double tempTN = 0;
        double tempFN = 0;
        double[] tempResult = new double[3];
        for (int i = 0; i < paraRecommendations.length; i++) {

            for (int j = 0; j < paraRecommendations[0].length; j++) {
                if ((paraRecommendations[i][j] == 0) && (paraTestMatrix[i][j]) == 0) {
                    tempTN++;
                } else if ((paraRecommendations[i][j] == 0) && (paraTestMatrix[i][j]) == 1) {
                    tempFP++;
                    tempRated++;
                } else if ((paraRecommendations[i][j] > 0) && (paraTestMatrix[i][j]) == 0) {
                    tempFN++;
                    tempRecommendation++;
                } else if ((paraRecommendations[i][j] > 0) && (paraTestMatrix[i][j]) == 1) {
                    tempTP++;
                    tempRecommendation++;
                    tempRated++;
                    tempCorrect++;
                } // Of if
            } // Of for j

        } // Of for i
        System.out.println("tempCorrect: " + tempCorrect);
        System.out.println("tempRecommendation: " + tempRecommendation);
        System.out.println("tempRated: " + tempRated);
        double tempPrecision = tempCorrect / tempRecommendation;
        double tempRecall = tempCorrect / tempRated;
        tempResult[0] = tempPrecision;
        tempResult[1] = tempRecall;
        tempResult[2] = (2 * tempPrecision * tempRecall) / (tempPrecision + tempRecall);
        System.out.println("Precision = " + tempPrecision + " recall = " + tempRecall + " F1 = "
                + (2 * tempPrecision * tempRecall) / (tempPrecision + tempRecall));
//		double TPR = tempTP / (tempTP + tempFN);
//		double FPR = tempFP / (tempFP + tempTN);
//		System.out.println("TPR: " + TPR);
//		System.out.println("FPR: " + FPR);
        return tempResult;
    }// Of validateRecommendation

    /**
     **************
     * Validate the recommendation while this rating matrix serves as the test set
     * Record the contribution of each concept
     *
     * @param paraRecommendations The recommendation in matrix, storing recommended
     *                            concept index above -1 indicates recommend, -1
     *                            indicates not recommend.
     **************
     */
    public double[] validateRecommendationForUC(int[][] paraTestMatrix, int[][] paraRecommendations) {
        boolean[] isHave = new boolean[numUsers];
        Arrays.fill(isHave, false);
        for (int i = 0; i < numUserConcept.length; i++) {
            if (numUserConcept[i] == 0) {
                isHave[i] = true;
            }//of if
        }//of for i
        double tempRecommendation = 0;
        double tempCorrect = 0;
        double tempRated = 0;
        double tempTP = 0;
        double tempFP = 0;
        double tempTN = 0;
        double tempFN = 0;
        double[] tempResult = new double[3];
        for (int i = 0; i < paraRecommendations.length; i++) {
            if (!isHave[i]) {
                for (int j = 0; j < paraRecommendations[0].length; j++) {
                    if ((paraRecommendations[i][j] == 0) && (paraTestMatrix[i][j]) == 0) {
                        tempTN++;
                    } else if ((paraRecommendations[i][j] == 0) && (paraTestMatrix[i][j]) == 1) {
                        tempFP++;
                        tempRated++;
                    } else if ((paraRecommendations[i][j] > 0) && (paraTestMatrix[i][j]) == 0) {
                        tempFN++;
                        tempRecommendation++;
                    } else if ((paraRecommendations[i][j] > 0) && (paraTestMatrix[i][j]) == 1) {
                        tempTP++;
                        tempRecommendation++;
                        tempRated++;
                        tempCorrect++;
                    } // Of if
                } // Of for j
            } // of if
        } // Of for i
        System.out.println("tempCorrect: " + tempCorrect);
        System.out.println("tempRecommendation: " + tempRecommendation);
        System.out.println("tempRated: " + tempRated);
        double tempPrecision = tempCorrect / tempRecommendation;
        double tempRecall = tempCorrect / tempRated;
        tempResult[0] = tempPrecision;
        tempResult[1] = tempRecall;
        tempResult[2] = (2 * tempPrecision * tempRecall) / (tempPrecision + tempRecall);
        System.out.println("Precision = " + tempPrecision + " recall = " + tempRecall + " F1 = "
                + (2 * tempPrecision * tempRecall) / (tempPrecision + tempRecall));
//		double TPR = tempTP / (tempTP + tempFN);
//		double FPR = tempFP / (tempFP + tempTN);
//		System.out.println("TPR: " + TPR);
//		System.out.println("FPR: " + FPR);
        return tempResult;
    }// Of validateRecommendation

    /**
     **************
     * Validate the recommendation while this rating matrix serves as the test set
     * Record the contribution of each concept
     *
     * @param paraRecommendations The recommendation in matrix, storing recommended
     *                            concept index above -1 indicates recommend, -1
     *                            indicates not recommend.
     **************
     */
    public double[] validateRecommendationForUser(int[][] paraTestMatrix, int[][] paraRecommendations) {
        double tempRecommendation = 0;
        double tempCorrect = 0;
        double tempRated = 0;
        double[] tempResult = new double[4];

        double tempSumPrecision = 0;
        double tempSumRecall = 0;
        double tempSumF1 = 0;
        for (int i = 0; i < paraRecommendations.length; i++) {
            double tempPrecision = 0;
            double tempRecall = 0;
            double tempTP = 0;
            double tempFP = 0;
            double tempTN = 0;
            double tempFN = 0;
            double tempF1 = 0;
            for (int j = 0; j < paraRecommendations[0].length; j++) {
                if ((paraRecommendations[i][j] == 0) && (paraTestMatrix[i][j]) == 0) {
                    tempTN++;
                } else if ((paraRecommendations[i][j] == 0) && (paraTestMatrix[i][j]) == 1) {
                    tempFN++;
                    tempRated++;
                } else if ((paraRecommendations[i][j] > 0) && (paraTestMatrix[i][j]) == 0) {
                    tempFP++;
                    tempRecommendation++;
                } else if ((paraRecommendations[i][j] > 0) && (paraTestMatrix[i][j]) == 1) {
                    tempTP++;
                    tempRecommendation++;
                    tempRated++;
                    tempCorrect++;
                } // Of if
            }//of for j
            if (tempTP + tempFP == 0.0) {
                tempPrecision = 0.0;
                tempSumPrecision += tempPrecision;
            } else if (tempTP + tempFN == 0.0) {
                tempRecall = 0.0;
                tempSumRecall += tempRecall;
            } else {
                if (tempPrecision + tempRecall == 0.0) {
                    tempF1 = 0.0;
                    tempSumF1 += tempF1;
                } else {
                    tempPrecision = tempTP / (tempTP + tempFP);
                    System.out.println("tempPrecision: " + tempPrecision);
                    tempRecall = tempTP / (tempTP + tempFN);
                    tempF1 = (2 * tempPrecision * tempRecall) / (tempPrecision + tempRecall);
                    tempSumPrecision += tempPrecision;
                    System.out.println("tempSumPrecision: " + tempSumPrecision);
                    tempSumRecall += tempRecall;
                    tempSumF1 += tempF1;
                }//of if
            } // of if
        }//of for i
        tempResult[0] = tempSumPrecision / paraRecommendations.length;
//		System.out.println("tempResult[0]: " + tempResult[0]);
        tempResult[1] = tempSumRecall / paraRecommendations.length;
        tempResult[2] = (2 * tempResult[0] * tempResult[1]) / (tempResult[0] + tempResult[1]);
        tempResult[3] = tempSumF1 / paraRecommendations.length;
        System.out.println("Precision = " + tempResult[0] + " recall = " + tempResult[1] + " F1 = "
                + tempResult[2] + " F1 = "+ tempResult[3]);
        return tempResult;
    }//of validateRecommendationForUser

    /**
     **************
     * Validate the recommendation while this rating matrix serves as the test set
     * Record the contribution of each concept
     *
     * @param paraRecommendations The recommendation in matrix, storing recommended
     *                            concept index above -1 indicates recommend, -1
     *                            indicates not recommend.
     **************
     */
    public double[] validateRecommendationTest(boolean[][] paraTestMatrix, int[] paraRecommendations) {
        double tempRecommendation = 0;
        double tempCorrect = 0;
        double tempRated = 0;
        double[] tempResult = new double[4];

        for (int i = 0; i < paraRecommendations.length; i++) {
            if ((paraRecommendations[i] == 0) && (!paraTestMatrix[0][i])) {
            } else if ((paraRecommendations[i] == 0) && (paraTestMatrix[0][i])) {
                tempRated++;
            } else if ((paraRecommendations[i] > 0) && (!paraTestMatrix[0][i])) {
                tempRecommendation++;
            } else if ((paraRecommendations[i] > 0) && (paraTestMatrix[0][i])) {
                tempRecommendation++;
                tempRated++;
                tempCorrect++;
            } // Of if
        } // Of for i
        System.out.println("tempCorrect: " + tempCorrect);
        System.out.println("tempRecommendation: " + tempRecommendation);
        System.out.println("tempRated: " + tempRated);
        double tempPrecision = tempCorrect / tempRecommendation;
        double tempRecall = tempCorrect / tempRated;
        tempResult[0] = tempPrecision;
        tempResult[1] = tempRecall;
        tempResult[2] = (2 * tempPrecision * tempRecall) / (tempPrecision + tempRecall);
        System.out.println("Precision = " + tempPrecision + " recall = " + tempRecall + " F1 = "
                + (2 * tempPrecision * tempRecall) / (tempPrecision + tempRecall));
        return tempResult;
    }// Of validateRecommendationTest

    /**
     * test
     *
     * @param paraThreshold
     * @param paraConceptsSet
     * @return
     */
    public int[] recommendationTest(int paraUser, double paraThreshold, Concept[] paraConceptsSet) {
        int[] resultRecommendations = new int[numItems];
        boolean[] tempRecommendations;
        for (int i = 0; i < paraConceptsSet.length; i++) {
            tempRecommendations = ratingMatrix.userConceptBasedRecommendation(paraUser, paraConceptsSet[i],
                    paraThreshold);
            // System.out.println("conceptsSet " + i + ":" +
            // Arrays.toString(tempRecommendations));
            for (int j = 0; j < numItems; j++) {
                if (tempRecommendations[j]) {
                    resultRecommendations[j]++;
                } // of if
            } // of for k
        } // of for j2
        return resultRecommendations;
    }// of recommendationTest

    /**
     * test
     *
     * @param paraThreshold
     * @param paraConceptsSet
     * @return
     */
    public int[][] recommendationToAllUsers(double paraThreshold, Concept[] paraConceptsSet) {
        int[][] resultRecommendations = new int[numUsers][numItems];
        boolean[] tempRecommendations;
        for (int i = 0; i < paraConceptsSet.length; i++) {
            for (int j = 0; j < paraConceptsSet[i].users.length; j++) {
                tempRecommendations = ratingMatrix.userConceptBasedRecommendation(paraConceptsSet[i].users[j],
                        paraConceptsSet[i], paraThreshold);
                for (int j2 = 0; j2 < tempRecommendations.length; j2++) {
                    if (tempRecommendations[j2]) {
                        resultRecommendations[paraConceptsSet[i].users[j]][j2]++;
                    } // of if
                } // of for j2
            } // of for j
        } // of for i
        return resultRecommendations;
    }// of recommendationTest

    public void computeSparsity() {
        double sparsity = 0;
        double count1 = 0;
        double count2 = 0;
        for (int i = 0; i < ratingMatrix.formalContext.length; i++) {
            for (int j = 0; j < ratingMatrix.formalContext[0].length; j++) {
                if (ratingMatrix.formalContext[i][j] == 1) {
                    count1++;
                } // of if
                count2++;
            } // of for j
        } // of for i
        sparsity = count1 / count2;
        System.out.println("The sparsity is: " + sparsity);
    }// of computeSparsity

    /**
     * Update concept by increase user.
     */
    public void updateByIncreaseUser() {
        // Generate concept by concept update.
        dataProcessing.divideTwoFC();

        int[] ran1 = dataProcessing.random1;
        int[] ran2 = dataProcessing.random2;
        int[][] orfc = dataProcessing.orFormalContext;

        int[][] nefc = dataProcessing.newFormalContext;
        System.out.println("%%%%%%%%%%%%%%%%%%%%");
        generateDeConceptSet(2, 4, orfc, ran1);

        int orConcept = tempActualConceptsForUC;
        tempActualOrConcepts = orConcept;
        generateDeConceptSet(2, 4, nefc, ran2);

        long startTime2 = System.currentTimeMillis();
        updateConceptSet(conceptsArray, orConcept, tempActualConceptsForUC - orConcept);
        long endTime2 = System.currentTimeMillis();
        System.out.println("Generate concept by concept update took " + (endTime2 - startTime2) + "s");

    }// of updateByIncreaseUser

    /**
     * Update concept set for increase user by knn.
     */
    public void updateByIncreaseUserKNN() {
        // Generate concept by concept update.
        dataProcessing.divideTwoFC();

        int[] ran1 = dataProcessing.random1;
        int[] ran2 = dataProcessing.random2;
        int[][] orfc = dataProcessing.orFormalContext;
        int[][] nefc = dataProcessing.newFormalContext;
        generateConceptSet(2, 5, orfc, ran1);

        int orConcept = tempActualConceptsForUC;
        tempActualOrConcepts = orConcept;
        double[] distanceWithConcepts = new double[orConcept];
        for (int i = 0; i < nefc.length; i++) {
            int count = 0;
            for (int j = 0; j < orConcept; j++) {
                double tempDistanceSum = 0;
                for (int k = 0; k < conceptsArray[j].users.length; k++) {
                    tempDistanceSum += measures.manhattanSimilarity(nefc[i], ratingMatrix.ratingMatrix[conceptsArray[j].users[k]]);
                }//of for k
                double tempDistance = tempDistanceSum / (conceptsArray[j].users.length);
                distanceWithConcepts[count] = tempDistance;
                count++;
            }//of for j
            //Find the top-k distance in distanceWithConcepts.
            double[] tempSort = distanceWithConcepts;
            Arrays.sort(tempSort);
            int[] topK = new int[3];
            for (int j = 0; j < distanceWithConcepts.length; j++) {
                if (distanceWithConcepts[j] == tempSort[tempSort.length - 1]) {
                    topK[0] = j;
                }//of if
                if (distanceWithConcepts[j] == tempSort[tempSort.length - 2]) {
                    topK[1] = j;
                }//of if
                if (distanceWithConcepts[j] == tempSort[tempSort.length - 3]) {
                    topK[2] = j;
                }//of if
            }//of for i
            //Insert current user in concepts.
            for (int j = 0; j < topK.length; j++) {
                int[] tempUserSet = new int[conceptsArray[topK[j]].users.length + 1];
                for (int k = 0; k < conceptsArray[topK[j]].users.length; k++) {
                    tempUserSet[k] = conceptsArray[topK[j]].users[k];
                }//of for k
                tempUserSet[conceptsArray[topK[j]].users.length] = ran2[i];
                //update itemset
                int[] tempItemSet = ratingMatrix.getSuperItems(tempUserSet);
                int[] tempItem = dataProcessing.intersection(tempItemSet, conceptsArray[topK[j]].items);
                //1.new intension is same as old/new intension belong to old
                if (Arrays.equals(tempItemSet, conceptsArray[topK[j]].items) || tempItemSet.length > conceptsArray[topK[j]].items.length) {
                    Concept tempNewConcept = new Concept(tempUserSet, tempItemSet);
                    conceptsArray[topK[j]] = tempNewConcept;
                    System.out.println("update");
                }//of if
            }//of for j
        }//of for i
    }// of updateByIncreaseUser

    /**
     * Concept update by data change(0 -> 1)
     */
    public void updateByDataChange () {
        //Step 1. Generate an array randomly(represent which user's data change)
        int[] random = simpleTools.generateRandomArray((int)(numUsers * 0.05));
        //Step 2. Change 0 to 1
        double paraRatio = 0.1;
        int tempModel = 1000 / (int) (paraRatio * 1000);
        int temCount = 1;
        for (int i = 0; i < random.length; i++) {
            for (int j = 0; j < ratingMatrix.formalContext[0].length; j++) {
                if (ratingMatrix.formalContext[i][j] == 0) {
                    temCount++;
                    if (temCount % tempModel == 1) {
                        ratingMatrix.formalContext[i][j] = 1;
                    }//of if
                }//of if
            }//of for j
        }//of for i
        boolean[] temp = new boolean[ratingMatrix.formalContext.length];
        for (int i = 0; i < random.length; i++) {
            temp[random[i]] = true;
        }//of for i
        //Step 3. Use user that not change to generate concept.
        //ran1 represent the user not change data.
        int[] ran1 = new int[ratingMatrix.formalContext.length - random.length];
        int count = 0;
        for (int i = 0; i < temp.length; i++) {
            if (!temp[i]) {
                ran1[count] = i;
                count++;
            }//of if
        }//of for i
        int[][] orFC = new int[ratingMatrix.formalContext.length - random.length][ratingMatrix.formalContext[0].length];
        for (int i = 0; i < orFC.length; i++) {
            orFC[i] = ratingMatrix.formalContext[ran1[i]];
        } // of for i
        //Step 4. The user which change
        int[][] newFC = new int[random.length][ratingMatrix.formalContext[0].length];
        Arrays.sort(random);
        for (int i = 0; i < random.length; i++) {
            newFC[i] = ratingMatrix.formalContext[random[i]];
        }//of for i

        //generate concept
        long startTime1 = System.currentTimeMillis();
        generateDeConceptSet(2, 5, orFC, ran1);
        int orConcept = tempActualConceptsForUC;
        tempActualOrConcepts = orConcept;

        generateDeConceptSet(2, 5, newFC, random);

//        long startTime1 = System.currentTimeMillis();
        updateConceptSet(conceptsArray, orConcept, tempActualConceptsForUC - orConcept);
        long endTime1 = System.currentTimeMillis();
        System.out.println("generate concepts by update use: " + (endTime1 - startTime1));

        //use all data
		long startTime2 = System.currentTimeMillis();
		generateDeConceptSet(2, 5);
		long endTime2 = System.currentTimeMillis();
		System.out.println("generate concepts by all data use: " + (endTime2 - startTime2));
    }//of updateByDataChange

    /**
     * filter
     */
    public void filter() {
        int c = 0;
        for (int i = 0; i < tempActualConceptsForUC; i++) {
            if (conceptsArray[i].users.length > 1) {
                conceptsArrayAfterFilter[c] = conceptsArray[i];
                c++;
            } // of if
        } // of for i
    }// of filter

    /**
     * Train and test 1. Use ML-100k.
     *
     * @param:
     * @return
     */
    public static void trainAndTest1() {
        RatingMatrix tempTrainRatingMatrix = new RatingMatrix("src/data/training.arff", 943, 1682);
        RatingMatrix tempTestRatingMatrix = new RatingMatrix("src/data/testing.arff", 943, 1682);

        ConceptSet tempSet = new ConceptSet(tempTrainRatingMatrix);
        tempSet.generateDeConceptSet(2, 8);
//		tempSet.showInformation();
        // long startTime = System.currentTimeMillis();
//		for (int i = 0; i < tempSet.numUsers; i++) {
//			System.out.println("for user " + i + " ");
//			tempSet.GCGAv6(i, 2, 1);
//		} // of for i
        int[][] tempRecommendations = tempSet.recommendation(0.5);
        tempSet.validateRecommendation(tempTestRatingMatrix.formalContext, tempRecommendations);
        // long endTime = System.currentTimeMillis();
//		 tempSet.validateRecommendation(tempTestRatingMatrix.formalContext, recommendations);
//		tempSet.validateRecommendationOfGCGAv6(tempTestRatingMatrix.formalContext, recordUsers, recommendations);
        // System.out.println("The run time is: " + (endTime - startTime) + "ms");
    }// Of trainAndTest

    /**
     * Train and test 2. use ML-100k-sd1.
     *
     * @param:
     * @return
     */
    public static void trainAndTest2() {
        String tempTrainFormalContextURL = "src/data/smalltest.txt";
        String tempTestFormalContextURL = "src/data/smalltest.txt";

        RatingMatrix tempTrainRatingMatrix = new RatingMatrix(10, 7, tempTrainFormalContextURL);
        RatingMatrix tempTestRatingMatrix = new RatingMatrix(10, 7, tempTestFormalContextURL);

        ConceptSet tempSet = new ConceptSet(tempTrainRatingMatrix);
        for (int i = 0; i < tempTestRatingMatrix.formalContext.length; i++) {
            int sum = 0;
            int count = 0;
            double sim = 0;
            for (int j = 0; j < tempTestRatingMatrix.formalContext[0].length; j++) {
//				if (tempTestRatingMatrix.formalContext[2][j] && tempTestRatingMatrix.formalContext[i][j]) {
//					sum += Math.abs(tempTestRatingMatrix.ratingMatrix[2][j] - tempTestRatingMatrix.ratingMatrix[i][j]);
//					count++;
//				}
            } // of for j
            sim = sum / count;
            System.out.println("2" + "," + i + ":" + sim);
        } // of for i
//		tempSet.generateConcept(2, 2);
//		tempSet.computeSparsity();

//		long startTime1 = System.currentTimeMillis();
        tempSet.generateDeConceptSet(2, 2);
//		System.out.println("for old concepts");
//		int[][] tempRecommendation;
//		tempRecommendation = tempSet.recommendation(0.5);
//		tempSet.validateRecommendation(tempTestRatingMatrix.formalContext, tempRecommendation);
//		long endTime1 = System.currentTimeMillis();
//		System.out.println("The run time is: " + (endTime1 - startTime1) + "ms");
//
//		long startTime2 = System.currentTimeMillis();
////		System.out.println("for new concepts");
//		for (int i = 0; i < tempTrainRatingMatrix.formalContext.length; i++) {
//			tempSet.GCGAv6(i, 2, 1);
//		} // of for i
//
//		tempSet.validateRecommendation(tempTestRatingMatrix.formalContext, recommendations);
//		long endTime2 = System.currentTimeMillis();
//		System.out.println("The run time is: " + (endTime2 - startTime2) + "ms");
//		tempSet.validateRecommendationOfGCGA(tempTestRatingMatrix.formalContext, recordUsers, recommendations);

    }// Of trainAndTest2

    /**
     * Train and test 3. eachmovie
     *
     * @param:
     * @return
     */
    public static void trainAndTest3() {
        String tempTrainFormalContextURL = "src/data/jester-data-1-train.txt";
        String tempTestFormalContextURL = "src/data/jester-data-1-test.txt";

        RatingMatrix tempTrainRatingMatrix = new RatingMatrix(24983, 100, tempTrainFormalContextURL);
        RatingMatrix tempTestRatingMatrix = new RatingMatrix(24983, 100, tempTestFormalContextURL);

        ConceptSet tempSet = new ConceptSet(tempTrainRatingMatrix);

        tempSet.computeSparsity();

        long startTime1 = System.currentTimeMillis();
        tempSet.generateDeConceptSet(2, 2);
        System.out.println("for old concepts");
        int[][] tempRecommendation;
        tempRecommendation = tempSet.recommendation(0.5);
        tempSet.validateRecommendation(tempTestRatingMatrix.formalContext, tempRecommendation);
        long endTime1 = System.currentTimeMillis();
        System.out.println("The run time is: " + (endTime1 - startTime1) + "ms");

        long startTime2 = System.currentTimeMillis();
        System.out.println("for new concepts");
//		for (int i = 0; i < tempTrainRatingMatrix.formalContext.length; i++) {
////			System.out.println("for user " + i  + ":");
//			tempSet.GCGAv6(i, 2, 1);
//		} // of for i

        tempSet.validateRecommendation(tempTestRatingMatrix.formalContext, recommendations);
        long endTime2 = System.currentTimeMillis();
        System.out.println("The run time is: " + (endTime2 - startTime2) + "ms");
//		tempSet.validateRecommendationOfGCGA(tempTestRatingMatrix.formalContext, recordUsers, recommendations);
    }// Of trainAndTest3

    /**
     * Train and test 4. ml-1m
     *
     * @param:
     * @return
     */
    public static void trainAndTest4() {
        String tempTrainFormalContextURL = "src/data/movielens1m-Train0.2.txt";
        String tempTestFormalContextURL = "src/data/movielens1m-Test0.2.txt";

        RatingMatrix tempTrainRatingMatrix = new RatingMatrix(6040, 3952, tempTrainFormalContextURL);
        RatingMatrix tempTestRatingMatrix = new RatingMatrix(6040, 3952, tempTestFormalContextURL);

        ConceptSet tempSet = new ConceptSet(tempTrainRatingMatrix);
//		tempSet.sampling();
//		tempSet.dividTrainAndTest();
        tempSet.computeSparsity();

        long startTime1 = System.currentTimeMillis();
        tempSet.generateDeConceptSet(2, 2);
//		int[][] tempRecommendations = tempSet.recommendation(0.5);
//		tempSet.validateRecommendation(tempTestRatingMatrix.formalContext, tempRecommendations);
//		System.out.println("for old concepts");
//		int[][] tempRecommendation;
//		tempRecommendation = tempSet.recommendation(0.5);
//		tempSet.validateRecommendation(tempTestRatingMatrix.formalContext, tempRecommendation);
//		long endTime1 = System.currentTimeMillis();
//		System.out.println("The run time is: " + (endTime1 - startTime1) + "ms");
//
        long startTime2 = System.currentTimeMillis();
//  	System.out.println("for new concepts");
//		for (int i = 0; i < tempTrainRatingMatrix.formalContext.length; i++) {
//			System.out.println("for user " + i + ":");
//			tempSet.GCGAv6(i, 2, 1);
//		} // of for i
//
        tempSet.validateRecommendation(tempTestRatingMatrix.formalContext, recommendations);
        long endTime2 = System.currentTimeMillis();
        System.out.println("The run time is: " + (endTime2 - startTime2) + "ms");
    }

    /**
     * Test 5(test for concept update)
     */
    public static void test5() {
        // Read data file
        String tempFormalContextURL = "src/data/created.txt";
        RatingMatrix tempTestFormalContext = new RatingMatrix(6, 5, tempFormalContextURL);
        ConceptSet tempSet = new ConceptSet(tempTestFormalContext);

//		long startTime1 = System.currentTimeMillis();
        tempSet.generateDeConceptSet(2, 2);
//		long endTime1 = System.currentTimeMillis();
//		System.out.println("Generate concept by all data took " + (endTime1 - startTime1) + "s");
        System.out.println("***");
        DataProcessing dataProcessing = new DataProcessing();
        dataProcessing.divideTwoFC();

        int[][] orformalcontext = dataProcessing.orFormalContext;
        int[] ran1 = dataProcessing.random1;
        int[] ran2 = dataProcessing.random2;
//		boolean[][] orfc = dataProcessing.orfc;

        int[][] newformalcontext = dataProcessing.newFormalContext;
//		boolean[][] nefc = dataProcessing.nefc;

        tempSet.generateConceptSet(2, 2, orformalcontext, ran1);
        System.out.println("***");
        int orConcept = tempSet.tempActualConceptsForUC;
        tempSet.tempActualOrConcepts = orConcept;
        tempSet.generateConceptSet(2, 2, newformalcontext, ran2);
        System.out.println("***");
//		long startTime2 = System.currentTimeMillis();
//		System.out.println("tempActualConceptsForUC: " + tempSet.tempActualConceptsForUC + " orConcept: " + orConcept);
        tempSet.updateConceptSet(tempSet.conceptsArray, orConcept, tempSet.tempActualConceptsForUC - orConcept);
        System.out.println("tempActualConcept: " + tempSet.tempActualConcepts);
        System.out.println("tempActualConceptForUC: " + tempSet.tempActualConceptsForUC);
//		long endTime2 = System.currentTimeMillis();
//		System.out.println("Generate concept by concept update took " + (endTime2 - startTime2) + "s");
    }// of test5

    /**
     * Test 6(test for concept update/for increase user)
     */
    public static void test6() {
        // Read data file.
//        RatingMatrix tempTrainRatingMatrix = new RatingMatrix("src/data/training.arff", 943, 1682);
//        RatingMatrix tempTestRatingMatrix = new RatingMatrix("src/data/testing.arff", 943, 1682);
        String tempTrainRatingMatrixURL = "src/data/200X420-1-train.txt";
        String tempTestRatingMatrixURL = "src/data/200X420-1-test.txt";
        RatingMatrix tempTrainRatingMatrix = new RatingMatrix(200, 420, tempTrainRatingMatrixURL);
        RatingMatrix tempTestRatingMatrix = new RatingMatrix(200, 420, tempTestRatingMatrixURL);
        ConceptSet tempSet = new ConceptSet(tempTrainRatingMatrix);

        //Generate concept by all data, record the runtime.
//        long startTime1 = System.currentTimeMillis();
//        tempSet.generateDeConceptSet(2, 4);
//        long endTime1 = System.currentTimeMillis();
//        System.out.println("Generate concept by all data took " + (endTime1 - startTime1) + "s");
//        int[][] tempRecommendation;
//        tempRecommendation = tempSet.recommendation(0.5);
//        tempSet.validateRecommendation(tempTestRatingMatrix.formalContext, tempRecommendation);

        //Generate concept by concept update.
        tempSet.updateByIncreaseUser();

        int[][] tempRecommendationForUC;
        tempRecommendationForUC = tempSet.recommendationForUC(0.5);
        tempSet.validateRecommendation(tempTestRatingMatrix.formalContext, tempRecommendationForUC);
    }// of test6

    /**
     * Test 7(test for concept update/for data change 0->1)
     */
    public static void test7() {
        // Read data file.
        RatingMatrix tempTrainRatingMatrix = new RatingMatrix("src/data/training.arff", 943, 1682);
        RatingMatrix tempTestRatingMatrix = new RatingMatrix("src/data/testing.arff", 943, 1682);
        ConceptSet tempSet = new ConceptSet(tempTrainRatingMatrix);

        //Generate concept by all data, record the runtime.
//        long startTime1 = System.currentTimeMillis();
//        tempSet.generateDeConceptSet(2, 5);
//        long endTime1 = System.currentTimeMillis();
//        System.out.println("Generate concept by all data took " + (endTime1 - startTime1) + "s");

        //Generate concept by concept update.
//		long startTime3 = System.currentTimeMillis();
        tempSet.updateByDataChange();
//		long endTime3 = System.currentTimeMillis();
//		System.out.println("time3: " + (endTime3 - startTime3));

        int[][] tempRecommendation;
        tempRecommendation = tempSet.recommendation(0.5);
        tempSet.validateRecommendation(tempTestRatingMatrix.formalContext, tempRecommendation);

        int[][] tempRecommendationForUC;
        tempRecommendationForUC = tempSet.recommendationForUC(0.5);
        tempSet.validateRecommendation(tempTestRatingMatrix.formalContext, tempRecommendationForUC);
//		System.out.println("tempActualConcept: " + tempSet.tempActualConcepts);
//		System.out.println("tempActualConceptForUC: " + tempSet.tempActualConceptsForUC);

    }// of test7

    /**
     * Test8
     */
    public static void test8() {
//        String tempFormalContextURL = "src/data/created.txt";
//        RatingMatrix tempTestFormalContext = new RatingMatrix(6, 5, tempFormalContextURL);
        RatingMatrix tempTrainRatingMatrix = new RatingMatrix("src/data/training.arff", 943, 1682);
        RatingMatrix tempTestRatingMatrix = new RatingMatrix("src/data/testing.arff", 943, 1682);
        ConceptSet tempSet = new ConceptSet(tempTrainRatingMatrix);
        tempSet.generateDeConceptSet(2, 5);
        int[][] tempRecommendation;
        tempRecommendation = tempSet.recommendation(0.5);
        tempSet.validateRecommendation(tempTestRatingMatrix.formalContext, tempRecommendation);

        long startTime = System.currentTimeMillis();
        tempSet.updateByIncreaseUserKNN();
        long endTime = System.currentTimeMillis();
        System.out.println("time: " + (endTime - startTime));
        System.out.println("concept number: " + tempSet.tempActualConceptsForUC);
        int[][] tempRecommendationForUC;
        tempRecommendationForUC = tempSet.recommendationForUC(0.5);
        tempSet.validateRecommendation(tempTestRatingMatrix.formalContext, tempRecommendationForUC);
    }//of for test8
    /**
     **************
     * Test the class.
     *
     * @throws FileNotFoundException
     **************
     */
    public static void main(String args[]) throws FileNotFoundException {
//		trainAndTest1();
//		trainAndTest2();
//		trainAndTest3();
//		trainAndTest4();
//		test5();
		test6();
//        test7();
//        test8();
    }// Of main
}// Of ConceptSet