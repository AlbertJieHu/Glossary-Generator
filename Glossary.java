import java.util.Comparator;

import components.queue.Queue;
import components.queue.Queue1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Glossary Creator that takes a file input and creates a group of html files.
 *
 * @author Albert Hu
 *
 */
public final class Glossary {

	/**
	 * Private constructor so this utility class cannot be instantiated.
	 */
	private Glossary() {
	}

	/**
	 * Comparator to sort the terms alphabetically.
	 *
	 * @author Albert Hu
	 *
	 */
	private static class StringLT implements Comparator<String> {
		@Override
		public int compare(String o1, String o2) {
			return o1.compareTo(o2);
		}
	}

	/**
	 * Gathers all the terms from the input file and put them into the Queue terms.
	 *
	 * @param inFile the file where terms for the list are stored
	 * @param terms  the Queue where terms are to be stored
	 */
	private static void getTerms(SimpleReader inFile, Queue<String> terms) {
		String line = "";
		/*
		 * While input file isn't at End of Stream, Every term will be added to the
		 * Queue terms.
		 */
		while (!inFile.atEOS()) {
			line = inFile.nextLine();
			terms.enqueue(line);
			/*
			 * Keeps on going to the next line until it reaches an empty line. An empty line
			 * means the next line will be a term.
			 */
			while (!line.equals("")) {
				line = inFile.nextLine();
			}
		}
	}

	/**
	 * Prints html depending on whether the word is a term or not
	 *
	 * @param word    the str to check if it is a term
	 * @param terms   the queue of terms to check for
	 * @param outFile the file where the html will be printed
	 */
	private static void printIsTerm(String word, Queue<String> terms, SimpleWriter outFile) {
		// Checks if the word is a term
		boolean isTerm = false;
		for (String str : terms) {
			if (str.equals(word)) {
				isTerm = true;
			}
		}
		// If it is a term, it will link to itself
		if (isTerm) {
			outFile.print("<a href=\"" + word + ".html\">");
			outFile.print(word);
			outFile.print("</a> ");
		} else {
			outFile.print(word + " ");
		}
	}

	/**
	 * Outputs html code for the Header and sets the title.
	 *
	 * @param title   the string used for the title and header
	 * @param outFile the file where the html will be printed
	 * @param isIndex boolean on whether the header is for the Index
	 *
	 */
	private static void printHeader(String title, SimpleWriter outFile, boolean isIndex) {
		if (isIndex) {
			outFile.println("<html>");
			outFile.println("<head><title>" + title + "</title></head>");
			outFile.println("<body>");
			outFile.println("<h1>" + title + "</h1>");
			outFile.println("<hr/>");
			outFile.println("<h2>Index</h2>");
		} else {
			outFile.println("<html>");
			outFile.println("<head><title>" + title + "</title></head>");
			outFile.println("<body>");
			outFile.println("<h1><FONT COLOR=\"#FF0000\"><b><i>" + title + "</i></b></FONT></h1>");
		}
	}

	/**
	 * Outputs html code for the Footer.
	 *
	 * @param outFile the file where the html will be printed
	 *
	 */
	private static void printFooter(SimpleWriter outFile) {
		outFile.println("</body></html>");
	}

	/**
	 * Outputs html code for a list
	 *
	 * @param inFile  the file where terms for the list are stored
	 * @param outFile the file where the html will be printed
	 *
	 */
	private static void printList(SimpleReader inFile, SimpleWriter outFile) {
		Queue<String> terms = new Queue1L<>();
		Comparator<String> alpha = new StringLT();
		outFile.println("<ul>");

		// Gets the terms
		getTerms(inFile, terms);

		// Sorts the Queue Alphabetically
		terms.sort(alpha);

		// For Each loop that prints each term
		for (String str : terms) {
			outFile.print("<li>");
			outFile.print("<a href=\"" + str + ".html\">");
			outFile.print(str);
			outFile.println("</a></li>");
		}

		outFile.println("</ul>");

	}

	/**
	 * Generates the top level index from a txt file.
	 *
	 * @param fileName   the str name of the file containing terms and descriptions
	 * @param folderName the str name of the folder where the html files will be
	 *                   stored
	 * @param in         SimpleReader for console
	 * @param out        SimplerWriter for console
	 *
	 */
	private static void generateIndex(String fileName, String folderName, SimpleReader in, SimpleWriter out) {
		// Text file to generate Index from
		SimpleReader inFile = new SimpleReader1L(fileName);

		// Output for Index html File
		SimpleWriter outFile = new SimpleWriter1L(folderName + "/index.html");

		// Asks User for Glossary Title
		out.println("Glossary Title: ");
		String title = in.nextLine();

		// Generates the Header & Title
		printHeader(title, outFile, true);

		// Generates the list of terms
		printList(inFile, outFile);

		// Closes html tags
		printFooter(outFile);

		inFile.close();
		outFile.close();

	}

	/**
	 * Generates the html files for pages of descriptions.
	 *
	 * @param fileName   the str name of the file containing terms and descriptions
	 * @param folderName the str name of the folder where the html files will be
	 *                   stored
	 *
	 */
	private static void generateDescriptions(String fileName, String folderName) {
		Queue<String> terms = new Queue1L<>();

		// Text file to generate Descriptions from
		SimpleReader inFile = new SimpleReader1L(fileName);

		// Gets the terms
		getTerms(inFile, terms);
		inFile = new SimpleReader1L(fileName);

		// Folder path
		String path = "";

		// String variables
		String line = "";
		String term = "";
		String desc = "";
		String word = "";

		// Goes through the input file
		while (!inFile.atEOS()) {
			// Sets the term
			line = inFile.nextLine();
			term = line;
			// Sets the description
			desc = "";
			while (!line.equals("")) {
				line = inFile.nextLine();
				desc = desc + line;
			}

			// Changes the output path for each term
			path = folderName + "/" + term + ".html";

			// Output for Description html File
			SimpleWriter outFile = new SimpleWriter1L(path);

			// Prints html code
			printHeader(term, outFile, false);
			outFile.print("<p>");
			// Traverses through the description and prints each word
			for (int i = 0; i < desc.length(); i++) {
				if (desc.charAt(i) == (' ')) {
					printIsTerm(word, terms, outFile);
					word = "";
				} else {
					word = word + desc.charAt(i);
				}
			}
			printIsTerm(word, terms, outFile);
			word = "";
			outFile.println("</p>");
			outFile.println("<hr/>");
			outFile.print("<p>Return to ");
			outFile.print("<a href=\"index.html\"> Index </a>");
			outFile.print("</p>");
			printFooter(outFile);
			outFile.close();
		}
		inFile.close();
	}

	/**
	 * Main method.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		SimpleReader in = new SimpleReader1L();
		SimpleWriter out = new SimpleWriter1L();

		// Asks User for Input File's Name
		out.println("Input File: ");
		String inputFile = in.nextLine();

		// Asks User for Output Folder's Name
		out.println("Output Folder: ");
		String outputFolder = in.nextLine();

		// Generates the Index Page
		generateIndex(inputFile, outputFolder, in, out);

		// Generate the Descriptions Pages
		generateDescriptions(inputFile, outputFolder);

		/*
		 * Close input and output streams
		 */
		in.close();
		out.close();
	}

}
