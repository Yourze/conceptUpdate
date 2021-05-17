package common;

import java.util.Arrays;

public class SimpleTools {
    /**
     ***************************
     * Convert an integer matrix into a string. Integers are separated by
     * separators. Author Xiangju Li.
     *
     * @param paraMatrix
     *            The given matrix.
     * @param paraSeparator
     *            The separator of data, blank and commas are most commonly uses
     *            ones.
     * @return The constructed String.
     ***************************
     */
    public static String intMatrixToString(int[][] paraMatrix,
                                           char paraSeparator) {
        String resultString = "[]";
        if ((paraMatrix == null) || (paraMatrix.length < 1))
            return resultString;
        resultString = "[";
        for (int i = 0; i < paraMatrix.length; i++) {
            resultString += "[";
            for (int j = 0; j < paraMatrix[i].length - 1; j++) {
                resultString += "" + paraMatrix[i][j] + paraSeparator + " ";
            }// Of for j
            resultString += paraMatrix[i][paraMatrix.length - 1];
            if (i == paraMatrix.length - 1) {
                resultString += "]]";
            } else {
                resultString += "]\r\n";
            }// Of if
        }// Of for i

        return resultString;
    }// Of intMatrixToString

    /**
     ***************************
     * Convert an integer matrix into a string. Integers are separated by
     * separators.
     *
     * @param paraMatrix
     *            The given matrix.
     * @param paraSeparator
     *            The separator of data, blank and commas are most commonly uses
     *            ones.
     * @return The constructed String.
     ***************************
     */
    public static String booleanMatrixToString(boolean[][] paraMatrix,
                                               char paraSeparator) {
        String resultString = "[]";
        if ((paraMatrix == null) || (paraMatrix.length < 1))
            return resultString;
        resultString = "[";
        for (int i = 0; i < paraMatrix.length; i++) {
            System.out.println("line " + i);
            resultString += "[";
            for (int j = 0; j < paraMatrix[i].length - 1; j++) {
                if (paraMatrix[i][j]) {
                    resultString += "1" + paraSeparator + " ";
                } else {
                    resultString += "0" + paraSeparator + " ";
                }//Of if
            }// Of for j

            //The last one in this row.
            if (paraMatrix[i][paraMatrix.length - 1]) {
                resultString += "1";
            } else {
                resultString += "0";
            }//Of if

            if (i == paraMatrix.length - 1) {
                resultString += "]]";
            } else {
                resultString += "]\r\n";
            }// Of if
        }// Of for i

        return resultString;
    }// Of booleanMatrixToString

    /**
     ***************************
     * Convert an integer matrix into a string. Integers are separated by
     * separators.
     *
     * @param paraMatrix
     *            The given matrix.
     * @param paraSeparator
     *            The separator of data, blank and commas are most commonly uses
     *            ones.
     * @return The constructed String.
     ***************************
     */
    public static String booleanMatrixToString(boolean[][] paraMatrix,
                                               char paraSeparator, int paraRows, int paraColumns) {
        String resultString = "[]";
        if ((paraMatrix == null) || (paraMatrix.length < 1))
            return resultString;
        resultString = "[";
        for (int i = 0; i < paraRows; i++) {
            System.out.println("line " + i);
            resultString += "[";
            for (int j = 0; j < paraColumns - 1; j++) {
                if (paraMatrix[i][j]) {
                    resultString += "1" + paraSeparator + " ";
                } else {
                    resultString += "0" + paraSeparator + " ";
                }//Of if
            }// Of for j

            //The last one in this row.
            if (paraMatrix[i][paraColumns - 1]) {
                resultString += "1";
            } else {
                resultString += "0";
            }//Of if

            if (i == paraColumns - 1) {
                resultString += "]]";
            } else {
                resultString += "]\r\n";
            }// Of if
        }// Of for i

        return resultString;
    }// Of booleanMatrixToString

    /**
     ***************************
     * Fill the matrix with the given value.
     * @param paraMatrix The given matrix.
     * @param paraInitialValue The given initialValue.
     ***************************
     */
    public static void matrixFill(int[][] paraMatrix,
                                  int paraInitialValue) {
        for (int i = 0; i < paraMatrix.length; i++) {
            for (int j = 0; j < paraMatrix[0].length; j++) {
                paraMatrix[i][j] = paraInitialValue;
            }//Of for j
        }//Of for i
    }//Of matrixFill

    /**
     * Union in order
     * @param paraThisArray
     * @param paraArray
     * @return
     */
    public static int[] unionInOrder(int[] paraThisArray, int[] paraArray){
        int[] tempArray = new int [paraThisArray.length+paraArray.length];
        int tempCount = 0  ;
        for (int i = 0; i < paraThisArray.length; i++) {
            tempArray[i] = paraThisArray[i];
            tempCount++;
        }
        boolean tempMark = false;
        int tempJ = 0;
        for(int i = 0 ; i < paraArray.length; i++){
            for (int j = tempJ; j < paraThisArray.length; j++) {
                if(paraArray[i]==paraThisArray[j]){
                    tempMark = true;
                    tempJ= i+1;
                    break;
                }else if (paraArray[i]<paraThisArray[j]){
                    tempJ = i;
                    break;
                }// Of if
            }// Of for j
            if(!tempMark){
                tempArray[tempCount] = paraArray[i];
                tempCount++;
            }// Of if
        }// Of for i
        int[] resultArray = new int [tempCount];
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = tempArray[i];
        }// Of for i
        return resultArray;
    }

    /**
     * Union
     * @param paraThisArray
     * @param paraArray
     * @return
     */
    public static int[] union(int[] paraThisArray, int[] paraArray){
        int[] tempArray = new int [paraThisArray.length+paraArray.length];
        int tempCount = 0  ;
        for (int i = 0; i < paraThisArray.length; i++) {
            tempArray[i] = paraThisArray[i];
            tempCount++;
        }
        boolean tempMark = false;
        @SuppressWarnings("unused")
        int tempJ = 0;
        for(int i = 0 ; i < paraArray.length; i++){
            for (int j = 0; j < paraThisArray.length; j++) {
                if(paraArray[i]==paraThisArray[j]){
                    tempMark = true;
                    break;
                }
            }// Of for j
            if(!tempMark){
                tempArray[tempCount] = paraArray[i];
                tempCount++;
            }// Of if
        }// Of for i
        int[] resultArray = new int [tempCount];
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = tempArray[i];
        }// Of for i
        return resultArray;
    }

    /**
     * Generate a random array.
     * @param paraLength The array's length.
     * @return
     */
    public int[] generateRandomArray(int paraLength) {
        int[] random = new int[paraLength];
        // Generate a random array.
        boolean tf = true;
        for (int i = 0; i < random.length; i++) {
            random[i] = (int) (Math.random() * paraLength);
            for (int j = 0; j < i; j++) {
                if (random[i] == random[j]) {
                    tf = false;
                    break;
                } // of if
            } // of for j
            if (!tf) {
                tf = true;
                i = i - 1;
            } // of if
        } // of for i
//		System.out.println(Arrays.toString(random));
        return random;
    }//of generateRandomArray

    /**
     * test
     */
    public static void test () {
        SimpleTools sim = new SimpleTools();
        sim.generateRandomArray(5);
    }//of test

    public static void main(String[] args) {
        SimpleTools sim = new SimpleTools();
        sim.test();
    }
}//Of class SimpleTools
