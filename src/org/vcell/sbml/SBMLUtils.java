package org.vcell.sbml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public abstract class SBMLUtils {

	public static final String SBML_NS_1 = "http://www.sbml.org/sbml/level1";
	public static final String SBML_NS_2 = "http://www.sbml.org/sbml/level2";
	public static final String SBML_VCML_NS = "http://www.sbml.org/2001/ns/vcell";
	public static void writeStringToFile(String xmlString, String filename) throws IOException {
		File outputFile = new File(filename);
		java.io.FileWriter fileWriter = new java.io.FileWriter(outputFile);
		fileWriter.write(xmlString);
		fileWriter.flush();
		fileWriter.close();
	}

	public static String readStringFromFile(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String temp;
		StringBuffer buf = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			buf.append(temp);
			buf.append("\n");
		}
		return buf.toString();
	}

	public static String xmlToString(Element root,boolean bTrimAllWhiteSpace) {
		XMLOutputter xmlOut = new XMLOutputter("   ");
	    xmlOut.setNewlines(true);
		xmlOut.setTrimAllWhite(bTrimAllWhiteSpace);		
		return xmlOut.outputString(root);		        
	}

	public static Element readXML(Reader reader) throws IOException, SbmlException {
		try {
			SAXBuilder builder = new SAXBuilder(false);
			Document sDoc = builder.build(reader);
			Element root = sDoc.getRootElement();
			return root;
		}catch (JDOMException e){
			e.printStackTrace(System.out);
			throw new SbmlException(e.getMessage());
		}
	}

	/**
	 * This method should ensure to return a string compliant to:
	 *  letter ::= 'a'..'z','A'..'Z'
	 *  digit  ::= '0'..'9'
	 *  SName  ::= { '_' } letter { letter | '_' | digit
	 *
	 * this is for SBML level 1
	 *
	 *
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public static String mangleToSName(String name) {
		//Check is not null or empty
		if (name==null || name.length()==0){ 
			return "";
		}
		//remove extra spaces
		String string = name.trim();
		//
		StringBuffer newString = new StringBuffer(string);
		//Replace any character which is not a {letter, number, '_'} with an '_'.
		for (int i=0;i<newString.length();i++){
			if (!Character.isLetterOrDigit(newString.charAt(i)) && newString.charAt(i)!='_'){
				newString.setCharAt(i,'_');
			}
		}
		//If the first character is a letter just return
		if (Character.isLetter(newString.charAt(0))) {
			return (newString.toString());
		}
		//feed map
		String[] map = {"_zero_","_one_", "_two_", "_three_", "_four_", "_five_", "_six_", "_seven_","_eight_", "_nine_", "_underscore_"};
		
		//At this point the string should start with a series of '_' or a number
		int index =0;
		while (index<newString.length() && newString.charAt(index)=='_') {
			index++;		
		}
		//Mangle strings made only of '_'
		if (index>=newString.length()) {
			//replace the last underscore
			newString.replace(index-1,index, map[10]);
			//return this string
			return newString.toString();
		}
		//make sure the index points to a number
		if (Character.isDigit(newString.charAt(index))) {
			//mangle the first number to its text version
			newString.replace(index,index+1, map[Character.getNumericValue(newString.charAt(index))]);
		}
	
		return newString.toString();
	}

	/**
	 * Read the *.test file corresponding to each SBML file to get the end time, num TimeSteps and variables
			Format of a .test file : (same format as the SBML validation test suite .test file)
	
			TIME 30
			STEPS 300
			SPECIES c1 c2
			URL Bindschadler2001_coupled_Ca_oscillators_Test
			REM tests Bindschadler2001_coupled_Ca_oscillators
	
	 * @param fileName
	 * @return
	 */
	static SimSpec readTestSpecsFile(String fileName) throws FileNotFoundException, IOException {
		File testFile = new File(fileName);
		long fileLength = testFile.length();
		// Check if file exists
		if (!testFile.exists()){
			throw new FileNotFoundException("File "+testFile.getPath()+" not found");
		}
		// Read characters from file into character array and transfer into string buffer.
		StringBuffer stringBuffer = new StringBuffer();
		FileInputStream is = null;
		try {
			is = new FileInputStream(fileName);
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(reader);
			char charArray[] = new char[10000];
			while (true) {
				int numRead = br.read(charArray, 0, charArray.length);
				if (numRead > 0) {
					stringBuffer.append(charArray,0,numRead);
				} else if (numRead == -1) {
					break;
				}
			}
		}finally{
			if (is != null){
				is.close();
			}
		}

		if (stringBuffer.length() != fileLength){
			System.out.println("<<<SYSOUT ALERT>>>testFile.read(), read "+stringBuffer.length()+" of "+fileLength+" bytes of input file");
		}

		String newLineDelimiters = "\n\r";
		StringTokenizer lineTokenizer = new StringTokenizer(stringBuffer.toString(),newLineDelimiters);
		
		String token1 = new String("");
		String token2 = new String("");
		String blankDelimiters = " \t";
		double time = 0.0;
		int steps = 0;
		Vector<String> varsVector = new Vector<String>();
		String[] varsStr = null;

		while (lineTokenizer.hasMoreTokens()) {
			token1 = lineTokenizer.nextToken();
			java.util.StringTokenizer nextLine = new java.util.StringTokenizer(token1, blankDelimiters);
			while (nextLine.hasMoreTokens()) {
				token2 = nextLine.nextToken();
				if (token2.equals("TIME")) {
					token2 = nextLine.nextToken().trim();
					time = Double.parseDouble(token2);
				} else if (token2.equals("STEPS")) {
					token2 = nextLine.nextToken().trim();
					steps = Integer.parseInt(token2);
				} else if (token2.equals("SPECIES")){
					while (nextLine.hasMoreTokens()) {
						token2 = nextLine.nextToken().trim();
						varsVector.addElement(token2);
					}
					varsStr = varsVector.toArray(new String[varsVector.size()]);
				}
			}
		}
		SimSpec ts = new SimSpec(varsStr, time, steps);
		return ts;
	}
	public static double taylorInterpolation(
	    double reqdTimePt,
	    double[] neighboringTimePts,
	    double[] neighboringValues) {
	    //
	    // This method applies a Taylor's series approximation to interpolate the function value at a required time point.
	    // 'reqdTimePt' is the point at which the value of the function is required.
	    // The 'neighboringTimePts' array contains the time points before and after the 'reqdTimePt' at which the value
	    // of the function is known, using which the value of fn at 'reqdTimePt' has to be interpolated.
	    // The 'neighboringValues' array contains the values of function at the time points provided in 'neighboringTimePts'.
	    //

	    if (neighboringTimePts.length != neighboringValues.length) {
	        throw new RuntimeException("Number of values provided in the 2 arrays are not equal, cannot proceed!");
	    }

	    // 
	    // Create a matrix (A_matrix) with the neighboring time points. The matrix is of the form :
	    //
	    //		1	del_t1	(del_t1^2)/2!	(del_t1^3)/3!
	    //		1	del_t2  (del_t2^2)/2!	(del_t2^3)/3!
	    //		1	del_t3  (del_t3^2)/2!	(del_t3^3)/3!
	    //		1	del_t4  (del_t4^2)/2!	(del_t4^3)/3!
	    //
	    // if interpolation is done using 4 points; 
	    // where del_ti is the difference between reqdTimePt and time points used for interpolation.
	    // 
	    int dim = neighboringTimePts.length;
	    Jama.Matrix A_matrix = new Jama.Matrix(dim, dim);
	    for (int i = 0; i < dim; i++) {
	        for (int j = 0; j < dim; j++) {
	            double val = neighboringTimePts[i] - reqdTimePt;
	            val = Math.pow(val, j);
	            val = val / factorial(j);
	            A_matrix.set(i, j, val);
	        }
	    }

	    // B_matrix is a column matrix containing the values of functions at the known time points (neighboringTimePts).
	    Jama.Matrix B_matrix = new Jama.Matrix(neighboringValues, dim);

	    // Solve A_matrix * F = B_matrix to obtain F, which is the transpose of [ f(t)	f'(t)	f''(t)	f'''(t) ]
	    Jama.Matrix solutionMatrix = A_matrix.solve(B_matrix);

	    // The required interpolated value at 'reqdTimePt' is the first value in solutionMatrix (F)
	    double reqdValue = solutionMatrix.get(0, 0);

	    return reqdValue;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/28/2004 12:21:16 PM)
	 * @return int
	 * @param n int
	 */
	public static int factorial(int n) {
	    if (n < 0) {
	        throw new RuntimeException("Cannot evaluate factorial of negative number");
	    }

	    int factorial = 1;
	    int index = 1;
	    while (index <= n) {
	        factorial *= index;
	        index++;
	    }

	    return factorial;
	}


}
