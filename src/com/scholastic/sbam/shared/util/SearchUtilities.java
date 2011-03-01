package com.scholastic.sbam.shared.util;

import java.util.ArrayList;
import java.util.List;

public class SearchUtilities {
	public static final int 	FULLTEXT_MIN_LEN		= 3;
	public static final boolean	DEFAULT_LOOSE_SEARCH	= true;
	public static final boolean	DEFAULT_BOOLEAN_SEARCH	= true;
	public static final int		DEFAULT_FRAG_SIZE		= 30;
	public static final String	CUT_MARKER				= "...";

	/**
	 * Convert a MySQL boolean search string to wildcard any words not in quotes or already wildcarded
	 * @param pattern
	 * 		The original search string.
	 * @return
	 * 		The wildcarded search string.
	 */
	public static String getLooseBoolean(String pattern) {
		StringBuffer sb 	= new StringBuffer(pattern);
		StringBuffer bool	= new StringBuffer();
		boolean      lastAlphaNum = false;
		boolean		 quotes = false;
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == '"') {
				bool.append(sb.charAt(i));
				quotes = !quotes;
				lastAlphaNum = false;
			} else if (quotes) {
				bool.append(sb.charAt(i));
			} else {
				if (isAlphaNumeric(sb.charAt(i))) {
					bool.append(sb.charAt(i));
					lastAlphaNum = true;
				} else {
					if (lastAlphaNum && sb.charAt(i) != '*' && sb.charAt(i) != '.')
						bool.append('*');
					if (!lastAlphaNum || sb.charAt(i) != '.')
						bool.append(sb.charAt(i));
					lastAlphaNum = false;
				}
			}
		}
		if (bool.length() > 0 && quotes)
			bool.append('"');
		if (bool.length() > 0 && !quotes && lastAlphaNum)
			bool.append('*');
		return bool.toString();
	}

	/**
	 * Get any wildcard terms from the string of terms.
	 *
	 *
	 * @param terms
	 * 		The string of search terms.
	 *
	 * @param doLoose
	 * 		Use loose searches?
	 *
	 * @param doBoolean
	 * 		Use boolean searches?
	 *
	 * @return
	 * 		A List<String> of individual search terms that had wildcards at the end.
	 */
	private static List<String> getWildcardTerms(String terms, boolean doLoose, boolean doBoolean) {
		List<String> wildcards = new ArrayList<String>();
		terms = terms.toLowerCase();
		boolean inQuotes = false;
		int ptr = 0;
		StringBuffer word = new StringBuffer();
		while (ptr < terms.length()) {
			if (terms.charAt(ptr) == '"') {
				inQuotes = !inQuotes;
				word.setLength(0);
			} else if (!inQuotes) {
				if (terms.charAt(ptr) == '*') {
					if (word.length() > 0)
						wildcards.add(word.toString());
				} else if (isAlphaNumeric(terms.charAt(ptr))) {
					word.append(terms.charAt(ptr));
				} else	// If we didn't get a wildcard character, toss the word
					word.setLength(0);
			}
			ptr++;
		}
		//	Anything left in word didn't end with a wildcard character... so who cares?
		return wildcards;
	}

	/**
	 * Get any exact match search terms from the string of terms.
	 *
	 *
	 * @param terms
	 * 		The string of search terms.
	 *
	 * @param doLoose
	 * 		Use loose searches?
	 *
	 * @param doBoolean
	 * 		Use boolean searches?
	 *
	 * @return
	 * 		A List<String> of individual search terms that did not have wildcards at the end.
	 */
	private static List<String> getExactMatchTerms(String terms, boolean doLoose, boolean doBoolean) {
		List<String> exactWords = new ArrayList<String>();
		terms = terms.toLowerCase();
		boolean inQuotes = false;
		int ptr = 0;
		StringBuffer word = new StringBuffer();
		while (ptr < terms.length()) {
			if (terms.charAt(ptr) == '"') {
				inQuotes = !inQuotes;
				if (!inQuotes && word.length() > 0)
					exactWords.add(word.toString());
				word.setLength(0);
			} else if (inQuotes) {
				word.append(terms.charAt(ptr));
			} else if (!inQuotes) {
				if (isAlphaNumeric(terms.charAt(ptr))) {
					word.append(terms.charAt(ptr));
				} else if (word.length() > 0) {
					exactWords.add(word.toString());
					word.setLength(0);
				}
			}
			ptr++;
		}
		if (word.length() > 0)
			exactWords.add(word.toString());
		return exactWords;
	}

	/**
	 * Determine if a character is alphanumeric (i.e. a-z, A-Z, 0-9).
	 * @param chr
	 * 		The character to be tested.
	 * @return
	 * 		True if the character is a letter or digit.
	 */
	private static boolean isAlphaNumeric(char chr) {
		return (chr >= 'a' && chr <= 'z') || (chr >= 'A' && chr <= 'Z') || (chr >= '0' && chr <= '9');
	}

	/**
	 * Insert &lt;STRONG&gt; and &lt;/STRONG&gt; tags before and after the occurance of search terms in text.
	 *
	 * @param text
	 *	 	The text to be modified.
	 * @param terms
	 * 		The terms to search for.
	 * @return
	 * 		A <code>String</code> containing the modified text.
	 */
	public static String markTerms(String text, String terms) {
		return markTerms(text, terms, "<STRONG>", "</STRONG>", DEFAULT_LOOSE_SEARCH, DEFAULT_BOOLEAN_SEARCH, DEFAULT_FRAG_SIZE);
	}

	/**
	 * Insert &lt;STRONG&gt; and &lt;/STRONG&gt; tags before and after the occurance of search terms in text.
	 *
	 * @param text
	 *	 	The text to be modified.
	 * @param terms
	 * 		The terms to search for.
	 * @param fragSize
	 * 		The size of emphasis fragments to retain, -1 to retain everything
	 * @return
	 * 		A <code>String</code> containing the modified text.
	 */
	public static String markTerms(String text, String terms, int fragSize) {
		return markTerms(text, terms, "<STRONG>", "</STRONG>", DEFAULT_LOOSE_SEARCH, DEFAULT_BOOLEAN_SEARCH, fragSize);
	}

	/**
	 * Insert &lt;STRONG&gt; and &lt;/STRONG&gt; tags before and after the occurance of search terms in text.
	 *
	 * @param text
	 *	 	The text to be modified.
	 * @param terms
	 * 		The terms to search for.
	 * @param fragSize
	 * 		The size of emphasis fragments to retain, -1 to retain everything
	 * @return
	 * 		A <code>String</code> containing the modified text.
	 */
	public static String markTerms(String text, String terms, boolean doLoose, boolean doBoolean) {
		return markTerms(text, terms, "<STRONG>", "</STRONG>", doLoose, doBoolean, DEFAULT_FRAG_SIZE);
	}

	/**
	 * Insert &lt;STRONG&gt; and &lt;/STRONG&gt; tags before and after the occurance of search terms in text.
	 *
	 * @param text
	 *	 	The text to be modified.
	 * @param terms
	 * 		The terms to search for.
	 * @param fragSize
	 * 		The size of emphasis fragments to retain, -1 to retain everything
	 * @return
	 * 		A <code>String</code> containing the modified text.
	 */
	public static String markTerms(String text, String terms, boolean doLoose, boolean doBoolean, int fragSize) {
		return markTerms(text, terms, "<STRONG>", "</STRONG>", doLoose, doBoolean, fragSize);
	}

	/**
	 * Insert Strings before and after the occurance of search terms in text, using the default application settings for loose and boolean searches.
	 *
	 * @param text
	 *	 	The text to be modified.
	 * @param terms
	 * 		The terms to search for.
	 * @param before
	 * 		The string to insert before a term in the text.
	 * @param after
	 * 		The string to insert after a term in the text.
	 * @return
	 * 		A <code>String</code> containing the modified text.
	 */
	public static String markTerms(String text, String terms, String before, String after) {
		return markTerms(text, terms, before, after, DEFAULT_LOOSE_SEARCH, DEFAULT_BOOLEAN_SEARCH, DEFAULT_FRAG_SIZE);
	}

	/**
	 * Insert Strings before and after the occurance of search terms in text.
	 *
	 * @param text
	 *	 	The text to be modified.
	 * @param terms
	 * 		The terms to search for.
	 * @param before
	 * 		The string to insert before a term in the text.
	 * @param after
	 * 		The string to insert after a term in the text.
	 * @param doLoose
	 * 		Interpet the search terms loosely.
	 * @param doBoolean
	 * 		Interpet the search terms using boolean operators.
	 * @param fragSize
	 * 		The size of emphasis fragments to retain, -1 to retain everything
	 * @return
	 * 		A <code>String</code> containing the modified text.
	 */
	public static String markTerms(String text, String terms, String before, String after, boolean doLoose, boolean doBoolean, int fragSize) {
		final int START = 0;
		final int LENGTH = 1;

		//	Convert the terms, as they would be for the database search
		terms = terms.trim();
		if (doBoolean && terms.length() > FULLTEXT_MIN_LEN && doLoose)
			terms = getLooseBoolean(terms);

		List<String> wildcards = getWildcardTerms(terms, doLoose, doBoolean);
		List<String> exactMatch = getExactMatchTerms(terms, doLoose, doBoolean);
		List<int []> emphasize = new ArrayList<int []>();

		//	Find the points in the text that match wildcards or exact words

		String testtext = text.toLowerCase();

		//	Add wildcards to emphasize
		for (int i = 0; i < wildcards.size(); i++) {
			int ptr = 0;
			String wildcard = (String) wildcards.get(i);
			while (testtext.indexOf(wildcard, ptr) >= 0) {
				ptr = testtext.indexOf(wildcard, ptr);
				if (ptr == 0 || !isAlphaNumeric(testtext.charAt(ptr-1)))
					emphasize.add(new int [] {ptr, wildcard.length()});
				ptr++;
			}
		}

		//	Add exact match to emphasize
		for (int i = 0; i < exactMatch.size(); i++) {
			int ptr = 0;
			String exactWord = (String) exactMatch.get(i);
			while (testtext.indexOf(exactWord, ptr) >= 0) {
				ptr = testtext.indexOf(exactWord, ptr);
				if (ptr == 0 || !isAlphaNumeric(testtext.charAt(ptr-1)))
					if (ptr + exactWord.length() >= testtext.length() || !isAlphaNumeric(text.charAt(ptr + exactWord.length())))
						emphasize.add(new int [] {ptr, exactWord.length()});
				ptr++;
			}
		}

		//	Merge emphasize overlaps
		for (int i = 0; i < emphasize.size(); i++) {
			int [] marker1 = emphasize.get(i);
			for (int j = i + 1; j < emphasize.size(); j++) {
				int [] marker2 = emphasize.get(j);
				if (marker1 [START] == marker2 [START]) {
					if (marker1 [LENGTH] < marker2 [LENGTH]) {
						// 2 is bigger than one, so make 1 like 2, remove 2, and redo everything for 1
						marker1 [LENGTH] = marker2 [LENGTH];
						emphasize.remove(j);
						i = i -1;
						break;
					} else // 2 is the same as or smaller than 1, so just get rid of it
						emphasize.remove(j);
				} else if (marker1 [START] < marker2 [START]) {
					if (marker1 [START] + marker1 [LENGTH] > marker2 [START]) {
						int end1 = marker1 [START] + marker1 [LENGTH];
						int end2 = marker2 [START] + marker2 [LENGTH];
						marker1 [LENGTH] = ((end1 > end2)?end1:end2) - marker1 [START];
						emphasize.remove(j);
						i = i - 1;
						break;
					}
				} else {
					if (marker2 [START] + marker2 [LENGTH] > marker1 [START]) {
						int end1 = marker1 [START] + marker1 [LENGTH];
						int end2 = marker2 [START] + marker2 [LENGTH];
						marker1 [LENGTH] = ((end1 > end2)?end1:end2) - marker2 [START];
						marker1 [START] = marker2 [START];
						emphasize.remove(j);
						i = i - 1;
						break;
					}
				}
			}
		}

		//	Sort the emphasis, descending, by start marker
		for (int i = 0; i < emphasize.size(); i++) {
			int [] marker1 = emphasize.get(i);
			for (int j = i + 1; j < emphasize.size(); j++) {
				int [] marker2 = emphasize.get(j);
				if (marker1 [START] < marker2 [START]) {
					int holdStart = marker1 [START];
					int holdLength = marker1 [LENGTH];
					marker1 [START]		= marker2 [START];
					marker1 [LENGTH]	= marker2 [LENGTH];
					marker2 [START]		= holdStart;
					marker2 [LENGTH]	= holdLength;
				}
			}
		}

		//	Insert emphasis marks, and strip unwanted text if needed
		int lastStart = text.length();
		StringBuffer sb = new StringBuffer(text);
		for (int i = 0; i < emphasize.size(); i++) {
			int [] marker = emphasize.get(i);
			// Delete unwanted text
			//	Delete between this end and previous start (remember, we're working backwards, so the previous comes after this one in the string)
			if (fragSize > 0 && lastStart > 0) {
				if (marker [START] + marker [LENGTH] + fragSize < lastStart) {
					sb.delete(marker [START] + marker [LENGTH] + fragSize, lastStart);
					if (marker [START] + marker [LENGTH] + fragSize > sb.length())
						sb.append(CUT_MARKER);
					else
						sb.insert(marker [START] + marker [LENGTH] + fragSize, CUT_MARKER);
				}
			}
			// Insert markers
			if (marker [START] + marker [LENGTH] > sb.length())
				sb.append(after);
			else
				sb.insert(marker [START] + marker [LENGTH], after);
			sb.insert(marker [START], before);
			lastStart = marker [START] - fragSize;
		}
		// Also delete unwanted text at the beginning
		if (emphasize.size() == 0) {
			if (fragSize > 0 && fragSize + CUT_MARKER.length() < text.length()) {
				sb.delete(fragSize, sb.length());
				sb.append(CUT_MARKER);
			}
		} else {
			if (fragSize > 0 && lastStart > fragSize) {
				sb.delete(0, lastStart - fragSize);
				sb.insert(0, CUT_MARKER);
			}
		}
		

		return sb.toString();
	}
}
