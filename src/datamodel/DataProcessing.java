package datamodel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import common.SimpleTools;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import weka.core.Instances;

public class DataProcessing {

    String[][] tempRatingMatrix= new String[24983][100];
    double[][] ratingMatrixs = new double[24983][100];
    double[][] ratingMatrixStan = new double[24983][100];
    int[][] ratingMatrixInt = new int[24983][100];
    int[][] testingFormalContext;
    int[][] trainingFormalContext;
    int[][] orFormalContext;
    int[][] newFormalContext;
    int[][] orfc;
    int[][] nefc;
    int[] random;
    int[] random1;
    int[] random2;
    RatingMatrix ratingMatrix = new RatingMatrix();
    SimpleTools simpleTools;

    /******************************************
     * Read the formal context from file.[1, 1, 1]
     ******************************************
     */
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
                tempFormalContext[tempI] = obtainFCRowArray(str1,paraItems) ;
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

    /******************************************
     * obtain formal context from file row by row.
     * @param paraRow
     * @param paraColumn
     * @return
     ******************************************
     */

    int[] obtainFCRowArray(String paraRow,int paraColumn){
        int[] tempRow = new int [paraColumn];
//		System.out.println("paraRow.lengh :" + paraRow.length());
        String tempSubString = paraRow.substring(1,paraRow.length() - 1);
        String[] tempAllElement = tempSubString.split(",");
        for (int i = 0; i < tempAllElement.length; i++) {
            int tempElement = Integer.parseInt(tempAllElement[i]);
            tempRow[i] = tempElement;
        } // Of for i
        return tempRow;
    }// Of function obtainFCRow

    /******************************************
     * obtain formal context from file row by row.
     * @param paraRow
     * @param paraColumn
     * @return
     ******************************************
     */

    int[] obtainFCRowArff(String paraRow,int paraColumn){
        int[] tempRow = new int [paraColumn];
//		System.out.println("paraRow.lengh :" + paraRow.length());
        String tempSubString = paraRow.substring(1,paraRow.length() - 1);
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
            String str1 = br.readLine();
            int row = Integer.parseInt(str1);
            str1 = br.readLine();
            int column = Integer.parseInt(str1);
            int tempI = 0;
            int[][] tempFormalContext = new int[row][];
            while (str1 != null) {
                str1 = br.readLine();
                if (str1 == null) {
                    break;
                } // Of if
                tempFormalContext[tempI] = obtainFCRowArray(str1,column) ;
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

    /**
     * read txt file like arff file.1, 1, 1
     *
     * @param
     * @return
     *
     */
    public double[][] readFile(String paraFileURL) {
        File f1 = new File(paraFileURL);
        String[][] rows = new String[25714][3];
        double[][] tempDis = new double[25714][3];
        int index = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f1));
            String str = null;
            // read by row.
            while ((str = br.readLine()) != null) {
                String str1 = str.substring(1, str.length() - 1);
                rows[index] = str1.split(", ");
//				rows[index] = str.split("( )+");
                for (int j = 0; j < 3; j++) {
                    tempDis[index][j] = Double.parseDouble(rows[index][j]);
                }
                index++;
            }
            br.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        double[][] tempMatrix = new double[index][3];
        for (int i = 0; i < tempMatrix.length; i++) {
            for (int j = 0; j < tempMatrix[0].length; j++) {
                tempMatrix[i][j] = tempDis[i][j];
            }
        }
        int[][] a = new int[300][100];
        for (int i = 0; i < tempMatrix.length; i++) {
            a[(int)tempMatrix[i][0]][(int)tempMatrix[i][1]] = (int)tempMatrix[i][2];
        }
        for (int i = 0; i < a.length; i++) {
            System.out.println(Arrays.toString(a[i]));
        }
        return null;
    }//of readFile

    /**
     * read data from excel file.
     *
     * @param
     * @return
     *
     */
    public String[][] readDataFromExcel() {
        // TODO Auto-generated method stub
        //read file
        try {
            File file = new File("src/data/jester-data-1.xls");
            Workbook wb = Workbook.getWorkbook(file);
            Sheet sheet = wb.getSheet(0);
            for (int i = 0; i < sheet.getRows(); i++) {
                for (int j = 1; j < sheet.getColumns(); j++) {
                    Cell cell = sheet.getCell(j-1, i);
                    tempRatingMatrix[i][j-1] = cell.getContents();
//                    System.out.printf(cell.getContents()+" ");
                } //of for j
//                System.out.println();
            } //of for i
        } catch (Exception e) {
            e.printStackTrace();
        }//of try
        return tempRatingMatrix;
    }//of readDataFromExcel

    /**
     * read file like arff file.
     *
     * @param
     * @return
     *
     */
    public double[][] readFileForJester(String paraFileURL) {
        File f1 = new File(paraFileURL);
        String[][] rows = new String[616912][3];
        double[][] tempDis = new double[616912][3];
        int index = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f1));
            String str = null;
            while ((str = br.readLine()) != null) {
//				char[] ch = str.toCharArray();
                rows[index] = str.split("   |\t|  | ");
                for (int j = 0; j < 3; j++) {
                    tempDis[index][j] = Double.parseDouble(rows[index][j]);
                }
                index++;
            }
            br.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
//		for (int i = 0; i < dis.length; i++) {
//			System.out.println(Arrays.toString(dis[i]));
//		}
        double[][] dis = new double[index][3];
        for (int i = 0; i < dis.length; i++) {
            for (int j = 0; j < dis[0].length; j++) {
                dis[i][j] = tempDis[i][j];
            }//of for j
        }//of for i
        double[][] fc = new double[24938][100];
        for (int i = 0; i < dis.length; i++) {
            fc[(int)dis[i][0]][(int)(dis[i][1])] = dis[i][2];
        }//of for i
        return fc;
    }//of readFile

    public void sampling (String paraFileURL,int paraSampleNumberOfUser,int paraSampleNumberOfItem) {
//		int[][] fc = readFile(paraFileURL);
//		int[][] fc = new int[tempfc.length][tempfc[0].length];
//		for (int i = 0; i < tempfc.length; i++) {
//			for (int j = 0; j < tempfc[0].length; j++) {
//				fc[i][j] = (int)(tempfc[i][j]);
//			}
//		}
        int[] randomOfUser = new int[paraSampleNumberOfUser];
        int[] randomOfItem = new int[paraSampleNumberOfItem];
        boolean tf1 = true;
        boolean tf2 = true;
        for (int i = 0; i < randomOfUser.length; i++) {
            randomOfUser[i] = (int)(Math.random()*6040);
            for (int j = 0; j < i; j++) {
                if (randomOfUser[i] == randomOfUser[j]) {
                    tf1 = false;
                    break;
                }//of if
            }//of for j
            if (!tf1) {
                tf1 = true;
                i = i - 1;
            }//of if
        }//of for i

        for (int i = 0; i < randomOfItem.length; i++) {
            randomOfItem[i] = (int)(Math.random()*3952);
            for (int j = 0; j < i; j++) {
                if (randomOfItem[i] == randomOfItem[j]) {
                    tf2 = false;
                    break;
                }//of if
            }//of for j
            if (!tf2) {
                tf2 = true;
                i = i - 1;
            }//of if
        }//of for i

        int[][] a = new int[paraSampleNumberOfUser][paraSampleNumberOfItem];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
//				a[i][j] = fc[randomOfUser[i]][randomOfItem[j]];
            }//of for j
        }//of for i

        int[][] b = new int[paraSampleNumberOfUser * paraSampleNumberOfItem][3];
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if (a[i][j] != 0) {
                    b[count][0] = i;
                    b[count][1] = j;
                    b[count][2] = a[i][j];
                    count++;
                }
            }
        }//of for i
        int[][] c = new int[count][3];
        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < c[0].length; j++) {
                c[i][j] = b[i][j];
            }
            System.out.println(Arrays.toString(c[i]));
        }//of for i

    }//of sampling

    public int[][] read(String paraFileURL) {
        File f1 = new File(paraFileURL);
        String[][] rows = new String[2636][3];
        int[][] tempDis = new int[2636][3];
        int index = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f1));
            String str = null;
            while ((str = br.readLine()) != null) {
                String str1 = str.substring(1, str.length() - 1);
                rows[index] = str1.split(", ");
                for (int j = 0; j < 3; j++) {
                    tempDis[index][j] = Integer.parseInt(rows[index][j]);
                }
                index++;
            }
            br.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
//		for (int i = 0; i < dis.length; i++) {
//			System.out.println(Arrays.toString(dis[i]));
//		}
//		int[][] dis = new int[index][3];
//		for (int i = 0; i < dis.length; i++) {
//			for (int j = 0; j < dis[0].length; j++) {
//				dis[i][j] = tempDis[i][j];
//			}//of for j
//		}//of for i
        int[][] fc = new int[300][200];
        for (int i = 0; i < tempDis.length; i++) {
            fc[tempDis[i][0]][tempDis[i][1]] = tempDis[i][2];
        }//of for i
        return fc;
    }//of readFile

    public void dividTrainAndTest () {
        //paraFc is a complete formal context.
        int[][] paraFc = read("C:/Users/zl/Desktop/ml-1m-sd2-300X200.txt");
        //paraArray is a matrix.
        int[][] paraArray = new int[60000][3];
        int count = 0;
        for (int i = 0; i < paraFc.length; i++) {
            for (int j = 0; j < paraFc[0].length; j++) {
                paraArray[count][0] = i;
                paraArray[count][1] = j;
                paraArray[count][2] = paraFc[i][j];
                count++;
            }
        }//of for i
        int[][] tempTrain = new int[48000][3];
        int[][] tempTest = new int[12000][3];

        int[] random = new int[48000];
        boolean tf = true;
        for (int i = 0; i < random.length; i++) {
            random[i] = (int)(Math.random()*60000);
            for (int j = 0; j < i; j++) {
                if (random[i] == random[j]) {
                    tf = false;
                    break;
                }//of if
            }//of for j
            if (!tf) {
                tf = true;
                i = i - 1;
            }//of if
        }//of for i
        boolean[] a = new boolean[paraArray.length];
        Arrays.fill(a, false);
        int count1 = 0;
        int count2 = 0;
        for (int i = 0; i < random.length; i++) {
            a[random[i]] = true;
        }//of for i
        for (int i = 0; i < a.length; i++) {
            if (a[i] && paraArray[i][2] != 0) {
                tempTrain[count1] = paraArray[i];
                count1++;
            }
            if (!a[i] && paraArray[i][2] != 0) {
                tempTest[count2] = paraArray[i];
                count2++;
            }//of if
        }//of for i
        System.out.println("The train set:");
        for (int i = 0; i < count1; i++) {
            System.out.println(Arrays.toString(tempTrain[i]));
        }
        System.out.println("The test set:");
        for (int i = 0; i < count2; i++) {
            System.out.println(Arrays.toString(tempTest[i]));
        }
    }

    /**
     * exchange the String matrix to double matrix.
     *
     * @param paraStringMatrix
     *            The String matrix.
     * @return
     *
     */
    public double[][] exchangeStringToDouble(String[][] paraStringMatrix) {
        double[][] tempInformation = new double[paraStringMatrix.length][paraStringMatrix[0].length];
        for (int i = 0; i < paraStringMatrix.length; i++) {
            for (int j = 0; j < paraStringMatrix[0].length; j++) {
                tempInformation[i][j] = Double.parseDouble(String.valueOf(paraStringMatrix[i][j]));
            }//of for j
        }//of for i
        return tempInformation;
    }//of for exchangeStringToInt

    /**
     * Find the max and min data.
     *
     * @param paraRatingMatrix
     * @return
     *
     */
    public double[] findTheMaxAndMin(double[] paraRatingMatrix) {
        double max = -100;
        double min = 100;
        double[] result = { 0, 0 };
        for (int i = 0; i < paraRatingMatrix.length; i++) {
            if (paraRatingMatrix[i] > max) {
                max = paraRatingMatrix[i];
            } // of if
            if (paraRatingMatrix[i] < min) {
                min = paraRatingMatrix[i];
            } // of if
        } // of for i
        result[0] = max;
        result[1] = min;
        return result;
    }// of findTheMaxAndMin

    /**
     * normalzie.
     *
     * @param paraRatingMatrix
     * @return
     *
     */
    public int[][] normalizeByColumn(double[][] paraRatingMatrix) {
        for (int i = 0; i < paraRatingMatrix[0].length; i++) {
            double max = findTheMaxAndMin(paraRatingMatrix[0])[0];
            double min = findTheMaxAndMin(paraRatingMatrix[0])[1];
            for (int j = 0; j < paraRatingMatrix.length; j++) {
                ratingMatrixStan[j][i] = (paraRatingMatrix[j][i] - min) / (max - min);
                ratingMatrixInt[j][i] = (int) (ratingMatrixStan[j][i] * 5);
            }//of for j
        }//of for i
        return ratingMatrixInt;
    }//normalizeByColumn

    /**********************************************
     * Divide the original rating matrix into training and testing rating matrix.
     *
     * @param paraFormalContext
     * @param paraRatio
     **********************************************
     */
    public int[][] obtainTrainingAndTestingFormalContext(int[][] paraFormalContext, double paraRatio) {
        testingFormalContext = new int[paraFormalContext.length][];
        trainingFormalContext = new int[paraFormalContext.length][];
        // The ration is usually set from 0.000 to 0.999
        int tempModel = 1000 / (int) (paraRatio * 1000);
        int temCount = 1;
        for (int i = 0; i < paraFormalContext.length; i++) {
            testingFormalContext[i] = new int[paraFormalContext[i].length];
            trainingFormalContext[i] = new int[paraFormalContext[i].length];
            for (int j = 0; j < paraFormalContext[i].length; j++) {
                if (paraFormalContext[i][j] > 0) {
                    temCount++;
                    if (temCount % tempModel == 1) {
                        testingFormalContext[i][j] = paraFormalContext[i][j];
                    } // End if
                    else {
                        trainingFormalContext[i][j] = paraFormalContext[i][j];
                    } // End else
                }
            } // End for j
        } // End for i
        return testingFormalContext;
    }// End function obtainTrainingFormalContext

    /****************************************
     * obtain string result of formal context.
     ****************************************
     */

    public String stringFormalContext(int[][] paraFormalContext) {
        String tempFormalContext = "";
        for (int i = 0; i < paraFormalContext.length; i++) {
            tempFormalContext += Arrays.toString(paraFormalContext[i]) + "\r\n";
        }
        return tempFormalContext;
    }// End function

    /****************************************
     * obtain string result of formal context.
     ****************************************
     */

    public String stringCompressedFormalContext(int[][] paraFormalContext) {
        String tempFormalContext = "";
        String formalContext = "";
        for (int i = 0; i < paraFormalContext.length; i++) {
            for (int j = 0; j < paraFormalContext[0].length; j++) {
                if (paraFormalContext[i][j] > 0) {
                    tempFormalContext = (i + 1) + " " + (j + 1) + " " + paraFormalContext[i][j];
                    formalContext += tempFormalContext + "\r\n";
                } // Of if
            } // Of for j
        } // Of for i
        return formalContext;
    }// End function

    /********************************
     * write formal context to file.
     ********************************
     */

    public void writeToFile(int[][] paraFormalContext, String paraWriteURL, int paraI) {
        String str1 = paraWriteURL;
        String tempFormalContext = "";
        switch (paraI) {
            case 1:
                tempFormalContext = stringFormalContext(paraFormalContext);
                break;
            case 2:
                tempFormalContext = stringCompressedFormalContext(paraFormalContext);
                break;
            default:
                break;
        }// Of switch
        // String tempFormalContext = stringFormalContext(paraFormalContext);
        // String tempFormalContext = stringCompressedFormalContext(paraFormalContext);
        File myFilePath = new File(str1);
        try {
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            } // End if
            FileWriter resultFile = new FileWriter(myFilePath);
            resultFile.write(tempFormalContext);
            resultFile.flush();
            resultFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        } // End try
    }// End function

    /**
     * Read arff file
     * @param paraFilename
     * @param paraNumUsers
     * @param paraNumItems
     * @return
     */
    public int[][] readFCFromArffFile (String paraFilename, int paraNumUsers, int paraNumItems) {
        int[][] formalContext = new int[paraNumUsers][paraNumItems];
        int[][] ratingMatrix = new int[paraNumUsers][paraNumItems];
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

        int tempUser, tempItem;
        for (int i = 0; i < data.numInstances(); i++) {
            tempUser = (int) data.instance(i).value(0);
            tempItem = (int) data.instance(i).value(1);
            formalContext[tempUser][tempItem] = 1;
            ratingMatrix[tempUser][tempItem] = (int) data.instance(i).value(2);
        }// Of for i
        return formalContext;
    }

    /**
     * Divide formal context to two formal context
     */
    public void divideTwoFC() {
        //paraFC is the original formal context.
//        int[][] paraFc = readFCFromArffFile("src/data/training.arff", 943, 1682);
        int[][] paraFc = ratingMatrix.readFile("src/data/eachMovie-2k-CompressedTrain0.3.txt");
//        int[][] paraFc = readFCFromArrayFile("src/data/created.txt", 6, 5);
//		RatingMatrix tempTrainRatingMatrix = new RatingMatrix("src/data/training.arff", 943, 1682);
//		System.out.println("*********");
//		for (int i = 0; i < paraFc.length; i++) {
//			System.out.println(Arrays.toString(paraFc[i]));
//		}//of for i
//		System.out.println("*********");
        orFormalContext = new int[(int) (paraFc.length * 0.9)][paraFc[0].length];
        newFormalContext = new int[paraFc.length - (int) (paraFc.length * 0.9)][paraFc[0].length];

        //Generate a random array.
        SimpleTools sim = new SimpleTools();
        random = sim.generateRandomArray(paraFc.length);
        //orFormalContext
        random1 = new int[orFormalContext.length];
        for (int i = 0; i < orFormalContext.length; i++) {
            random1[i] = random[i];
        }//of for i
        //sort random1
        Arrays.sort(random1);
        for (int i = 0; i < orFormalContext.length; i++) {
            for (int j = 0; j < orFormalContext[0].length; j++) {
                orFormalContext[i][j] = paraFc[random1[i]][j];
            }//of for j
        }//of for i
        //newFormalContext
        random2 = new int[newFormalContext.length];
        int count = 0;
        for (int i = orFormalContext.length; i < paraFc.length; i++) {
            random2[count] = random[i];
            count++;
        }//of for i
        //sort random2
        Arrays.sort(random2);
        for (int i = 0; i < newFormalContext.length; i++) {
            for (int j = 0; j < newFormalContext[0].length; j++) {
                newFormalContext[i][j] = paraFc[random2[i]][j];
            } //of for j
        }//of for i
        //show
//		System.out.println("random1 is: " + Arrays.toString(random1));
//		for (int i = 0; i < orFormalContext.length; i++) {
//			System.out.println(Arrays.toString(orFormalContext[i]));
//		}//of for i
//		System.out.println("***************");
//		System.out.println("random2 is: " + Arrays.toString(random2));
//		for (int i = 0; i < newFormalContext.length; i++) {
//			System.out.println(Arrays.toString(newFormalContext[i]));
//		}//of for i
//		System.out.println("***************");
    }//of devideTwoFC

    /**
     * Get the intersection of two array.
     * @param nums1
     * @param nums2
     * @return
     */
    public int[] intersection(int[] nums1, int[] nums2) {
        Set<Integer> set1 = new HashSet<>();
        for (int i=0;i<nums1.length;i++){
            set1.add(nums1[i]);
        }
        Set<Integer> resSet = new HashSet<>();

        for (int j=0;j<nums2.length;j++){
            if (set1.contains(nums2[j])){
                resSet.add(nums2[j]);
            }
        }
        int[] res = new int[resSet.size()];
        int k = 0;
        for (Integer v : resSet){
            res[k] = v;
            k++;
        }
        return res;
    }//of intersection

    /**
     * Get the union set of two array.
     * @param arr1
     * @param arr2
     * @return
     */
    public int[] mergeArrays(int[] arr1, int[] arr2) {
        int[] temp = new int[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, temp, 0, arr1.length);
        System.arraycopy(arr2, 0, temp, arr1.length, arr2.length);
        for (int i = 0; i < arr1.length; i++) {
            for (int j = arr1.length; j < temp.length; j++) {
                if (temp[i] == temp[j]) {
                    temp[j] = temp[temp.length - 1];
                    temp = Arrays.copyOf(temp, temp.length - 1);
                    break;
                } // of if
            } // of for j
        } // of for i
        Arrays.sort(temp);
        return temp;
    }// of mergeArrays
    /**
     * main.
     *
     * @param args
     * @return
     *
     */
    public static void main(String[] args) {
        DataProcessing d = new DataProcessing();
//    	d.sampling("C:/Users/zl/Desktop/wzy/ml-1m-ratings.txt", 300, 200);
//    	d.dividTrainAndTest();
//    	d.readFile("src/data/jester-sd1-300X100-test.txt");
        d.readFCFromArrayFile("src/data/created.txt", 6, 5);
        d.divideTwoFC();
    }//of main

}