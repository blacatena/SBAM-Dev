package com.scholastic.sbam.shared.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.user.datepicker.client.CalendarUtil;

public class AppConstants {
	
	public static final String VERSION = "0.0.7dev";
	public static final String VERSION_DESCRIPTION = "In Development.";
	
	public static 		boolean USER_PORTLET_CACHE_ACTIVE = true;	//	Use this to turn on/off the portlet cache service
	public static 		boolean USER_ACCESS_CACHE_ACTIVE  = true;	//	Use this to turn on/off the access cache service
	
	public static final char STATUS_ACTIVE		= 'A';
	public static final char STATUS_INACTIVE	= 'I';
	public static final char STATUS_DELETED		= 'X';
	public static final char STATUS_ERROR		= '!';
	public static final char STATUS_EXPIRED		= 'E';
	public static final char STATUS_NEW			= 'N';
	public static final char STATUS_NULL		= '0';
	public static final char STATUS_ALL			= '*';
	public static final char STATUS_ANY_NONE	= (char) 0;
	
	public static final char PATH_DELIMITER		= '/';
	public static final char PATH_ESCAPE		= '\\';
	
	public static final String REPLACEMENT_START = "[$";
	public static final String REPLACEMENT_END   = "$]";
	public static final String CHUNK_START		= "[[";
	public static final String CHUNK_END		= "]]";
	public static final String CHUNK_PART		= "::";
    
    public static final String DIGITS			= "0123456789";
    public static final String LETTERS			= "abcdefghijklmnopqrstuvwxyz";
    public static final String LETTERS_ALL		= "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static final String []	NUMBER_WORDS_0_19		=	{ 
																	"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
																	"Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen" 
																};
	public static final String []	NUMBER_WORDS_0_90		=	{ 
																	"Zero", "Ten", "Twenty", "Thirty", "Fourty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
																};
	
	public static final char	STRICT_BOOLEAN_SEARCH_FLAG		= '!';
	public static final char	LOOSE_BOOLEAN_SEARCH_FLAG		= '?';
	public static final char	OR_MODE_BOOLEAN_SEARCH_FLAG		= '|';
	public static final char	QUERY_EXPANSION_SEARCH_FLAG		= '~';
	
	public static final int[][] CC_SUM_TABLE = {{0,1,2,3,4,5,6,7,8,9},{0,2,4,6,8,1,3,5,7,9}};
	public static final double	MAX_DOLLAR_VALUE = 99000000.00;		//	Ninety nine million
	
	public static final int		STANDARD_LOAD_LIMIT				=	500;
    
	public static class TypedTerms {
		private List<String>		words;
		private List<String>		numbers;
		private List<String>		ipStrings;
		private List<Long []>		ips;
		private List<String>		messages;
		
		public TypedTerms(List<String> words, List<String> numbers, List<String> ipStrings, List<Long []> ips, List<String> messages) {
			this.words		= words;
			this.numbers	= numbers;
			this.ips		= ips;
			this.ipStrings	= ipStrings;
			this.messages	= messages;
		}
		
		public List<String> getWords() {
			return words;
		}
		public void setWords(List<String> words) {
			this.words = words;
		}
		public List<String> getNumbers() {
			return numbers;
		}
		public void setNumbers(List<String> numbers) {
			this.numbers = numbers;
		}
		public List<Long []> getIps() {
			return ips;
		}
		public void setIps(List<Long []> ips) {
			this.ips = ips;
		}
		public List<String> getIpStrings() {
			return ipStrings;
		}
		public void setIpStrings(List<String> ipStrings) {
			this.ipStrings = ipStrings;
		}
		public List<String> getMessages() {
			return messages;
		}
		public void setMessages(List<String> messages) {
			this.messages = messages;
		}
	}
	
	
    public static boolean isLetter(char chr) {
    	return LETTERS.indexOf(Character.toLowerCase(chr)) >= 0;
    }
    
    public static boolean isDigit(char chr) {
    	return DIGITS.indexOf(chr) >= 0;
    }
    
    public static boolean isLetter(String chr) {
    	if (chr == null)
    		return false;
    	if (chr.length() > 1)
    		chr = chr.substring(0, 1);
    	return LETTERS.indexOf(chr.toLowerCase()) >= 0;
    }
    
    public static boolean isDigit(String chr) {
    	if (chr.length() > 1)
    		chr = chr.substring(0, 1);
    	return DIGITS.indexOf(chr) >= 0;
    }
    
    public static boolean isNumeric(String term) {
    	if (term == null || term.length() == 0)
    		return false;
    	for (int i = 0; i < term.length(); i++)
    		if (DIGITS.indexOf(term.charAt(i)) < 0)
    			return false;
    	return true;
    }
    
    public static boolean isDate(String term) {
    	if (term.length() != 10)
    		return false;
    	if (! term.matches("[0-9][0-9][0-9][0-9]\\-[0-9][0-9]\\-[0-9][0-9]"))
    		return false;

//    	try {
//			new SimpleDateFormat("yyyy-MM-dd").parse(term);
//		} catch (ParseException e) {
//			return false;
//		}
		
		return true;
    }
    
    public static boolean isEmpty(String str) {
    	return str == null || str.length() == 0 || str.trim().length() == 0;
    }
	
	public static String getNumberWord(int number) {
		String prefix = (number < 0) ? "Negative " : "";
		if (number < 20)
			return prefix + NUMBER_WORDS_0_19 [number];
		if (number >= 10000)
			return number + "";
		if (number >= 1000) {
			prefix += NUMBER_WORDS_0_19 [number / 100] + " Thousand"; 
			if (number % 1000 == 0)
				return prefix;
			prefix += ", ";
			number = number % 1000;
		}
		if (number >= 100) {
			prefix += NUMBER_WORDS_0_19 [number / 10] + " Hundred";
			if (number % 100 == 0)
				return prefix;
			prefix += ", ";
			number = number % 100;
		}
		if (number >= 20) {
			prefix += NUMBER_WORDS_0_90 [number / 10];
			if (number % 10 == 0)
				return prefix;
			prefix += " ";
			number = number % 10;
		}
		return prefix + NUMBER_WORDS_0_19 [number];
	}
	
	public static String getNumberWordLower(int number) {
		return getNumberWord(number).toLowerCase();
	}
	
	public static boolean isValidCheckDigit(int num) {
		return isValidCheckDigit(num + "");
	}
	
	public static boolean isValidCheckDigit(String num) {
	    int sum = 0, flip = 0;
	 
	    for (int i = num.length() - 1; i >= 0; i--)
	    	sum += CC_SUM_TABLE[flip++ & 0x1][Character.digit(num.charAt(i), 10)];
	    return sum % 10 == 0;
	}
	
	public static int appendCheckDigit(int num) {
		return (num * 10) + getCheckDigit(num);
	}
	
	public static int getCheckDigit(int num) {
		return getCheckDigit(num + "");
	}
	
	public static int getCheckDigit(String num) {
	    int sum = 0, flip = 1;
	 
	    for (int i = num.length() - 1; i >= 0; i--)
	    	sum += CC_SUM_TABLE[flip++ & 0x1][Character.digit(num.charAt(i), 10)];
	    if (sum % 10 == 0)
	    	return 0;
	    return (10 - (sum % 10));
	}
	
	public static String [] parseFilterTerms(String filter) {
		HashSet<String> terms = new HashSet<String>();
		if (filter != null) {
			filter = filter.trim().toUpperCase();
			StringBuffer word = new StringBuffer();
			for (int i = 0; i < filter.length(); i++) {
				if ( (filter.charAt(i) >= 'a' && filter.charAt(i) <= 'z')
				|| 	 (filter.charAt(i) >= 'A' && filter.charAt(i) <= 'Z')
				||	 (filter.charAt(i) >= '0' && filter.charAt(i) <= '9')) {
					word.append(filter.charAt(i));
				} else if (word.length() > 0) {
					if (!terms.contains(word)) {
						terms.add(word.toString());
					}
					word = new StringBuffer();
				}
			}
			if (word.length() > 0)
				if (!terms.contains(word.toString())) {
					terms.add(word.toString());
				}
		}
		return terms.toArray(new String [] {});
	}
	
	public static String getStatusDescription(char status) {
		if (status == STATUS_ACTIVE)		return "Active";
		if (status == STATUS_INACTIVE)		return "Inactive";
		if (status == STATUS_DELETED)		return "Deleted";
		if (status == STATUS_EXPIRED)		return "Expired";
		if (status == STATUS_ANY_NONE)		return "Unknown";
		return "Bad Status " + status;
	}
	
	public static String addAsIp(StringBuffer ip, List<Long []> ipRanges) {
		if (ip.indexOf(".") < 0)
			return null;
		
		int ipCount = 0;
		int octetCount = 0;
		StringBuffer octet = new StringBuffer();
		String [] [] octets = new String [2] [4];
		
		for (int i = 0; i < ip.length(); i++) {
			char it = ip.charAt(i);
			
			if (it == '*') {
				if (octet.length() > 0)	// We've already accumulated digits, so this can't be right
					return "Invalid octet '" + octet + it + "' in '" + ip + "'.";
				if (octetCount == 0) // Can't wildcard right away
					return "First octet can't be a wildcard in '" + ip + "'.";
				if (octetCount >= 4) // Can't have more than 4 octets 
					return "Too many octets in '" + ip + "'.";
				octet.append(it);
			} else if (AppConstants.DIGITS.indexOf(it) >= 0) {
				if (octetCount >= 4) // Already have 4 octets, so no place to add the left over octet, so that can't be right
					return "Too many octets in '" + ip + "'.";
				if (octet.length() > 0 && octet.charAt(0) == '*')	// Can't wildcard and have digits, too
					return "Invalid octet '" + octet + it + "' in '" + ip + "'.";
				octet.append(it);
			} else if (it == '.') {
				octets [ipCount] [octetCount] = octet.toString();
				octetCount++;
				octet.setLength(0);
			} else if (it == '-' || it == ':') {
				if (octetCount == 0) // Nothing before this, so that can't be right
					return "Invalid range (nothing before '" + it + "' in '" + ip + "'.";
				if (ipCount >= 2) // Already got two IPs, so that can't be right
					return "Too many IP addresses in a range in '" + ip + "'.";
				if (octet.length() == 0) // Don't have an octet before this, so that can't be right
					return "First IP didn't end with an octet in '" + ip + "'.";
				
				octets [ipCount] [octetCount] = octet.toString();
				octet.setLength(0);
				
				ipCount++;
				octetCount = 0;
			}
		}
		
		if (octet.length() > 0) {
			// One last octet
			if (octetCount > 3)	// 	Already 4 octets, so this is one too many
				return "Too many octets in '" + ip + "'.";

			octets [ipCount] [octetCount] = octet.toString();
			octetCount++;
		}
		
		if (octetCount > 0)
			ipCount++;
		
		Long [] ips = new Long [2];
		
		ips [0] = new Long(0);
		ips [1] = new Long(0);
		
		for (int i = 0; i < ipCount; i++) {
			for (int j = 0; j < 4; j++) {
				ips [i] *= 256;
				if (ipCount == 1) ips [1] *= 256; 	// If we don't have a range, count in the high, too , in case of wildcards
				
				if (octets [i] [j] == null && j < 2)
					return "Too few octets to use in '" + ip + "'.";
				if (octets [i] [j] == null || octets [i] [j].charAt(0) == '*') {
					// Treat this as a wildcard
					if (ipCount > 1)	// 	Can't use wildcards in a range
						return "Can't use wildcards in a range in '" + ip + "'.";
					ips [1] += 255; 
				} else {
					try {
						long octetValue = Long.parseLong(octets [i] [j]);
						if (octetValue > 255)
							return "Invalid octet value " + octetValue + " in '" + ip + "'.";
						ips [i] += octetValue;
						if (ipCount == 1)
							ips [1] += octetValue;	// If only one IP, count it in the high value, too 
					} catch (NumberFormatException e) {
						return "Invalid octet value '" + octets [i] [j] + "' in '" + ip + "'.";
					}
				}
			}
		}
		
		if (ips [1] < ips [0])
			return "First IP is greater than the second in '" + ip + "'.";	
		
		ipRanges.add(ips);
		
		return null;
	}
	
	public static TypedTerms parseTypedFilterTerms(String filter) {
		return parseTypedFilterTerms(filter, false);
	}
	
	/**
	 * Parse a filter string into individual terms, separated into words, numbers and optionally ip address ranges, for 
	 * using in search logic.
	 * @param filter
	 * The string to be parsed.
	 * @param includeIps
	 * True to recognize and include IP address ranges.  False to treat IP address ranges as just more numbers.
	 * @return
	 * A TypedTerms object with lists of words, numbers, IP address ranges and any error messages generated when recognizing IP address ranges.
	 * The messages are really only relevant if you are expecting IP address ranges... otherise, they'll simply be pointing out things like the fact
	 * that 23B in "Apt. 23B" is not a valid IP address.
	 */
	public static TypedTerms parseTypedFilterTerms(String filter, boolean includeIps) {
		if (filter == null || filter.length() == 0)
			return null;
		
//		Create a list of numeric and alphanumeric components
		List<Long []>		ipRanges	=	new ArrayList<Long []>();
    	List<String>		numbers		=	new ArrayList<String>();
    	List<String>		words   	=	new ArrayList<String>();
    	List<String>		ipStrings   =	new ArrayList<String>();
    	List<String>		messages   	=	new ArrayList<String>();
    	
    	filter = filter.trim().toUpperCase();
    	
    	StringBuffer term = new StringBuffer();
    	
    	// First, look for valid IP addresses and extract them
    	if (includeIps) {
    		boolean letters = false;
    		StringBuffer ip	  		=	new StringBuffer();
    		StringBuffer remnant	=	new StringBuffer();
    		
    		for (int i = 0; i <= filter.length(); i++) {
    			char it = (i < filter.length()) ? filter.charAt(i) : 0;
    			if (it == '.' || it == '*' || it == '-' || it == ':' || AppConstants.DIGITS.indexOf(it) >= 0) {
    				ip.append(it);
    			} else if (AppConstants.LETTERS_ALL.indexOf(it) >= 0) {
    				ip.append(it);
    				letters = true;
    			} else if (it == 0 || it == ' ' || AppConstants.LETTERS_ALL.indexOf(it) < 0) {	// Anything but a letter triggers the ip test... look for 0 or blank explicitly just as an optimization (to avoid scanning the LETTERS_ALL string)
    				if (ip.length() > 0) {
    					if (letters) {
	    					remnant.append(ip);
	    					ip.setLength(0);
	    				} else {
		    				String message = addAsIp(ip, ipRanges);
		    				if (message != null) {	// If addAsIp returns a non-zero length message, then this wasn't a valid IP range
		    					remnant.append(ip);
		    					messages.add(message);
		    				} else
		    					ipStrings.add(ip.toString());
		    				ip.setLength(0);
	    				}
    				}
    				
    				if (it > 0)
    					remnant.append(it);
    				letters = false;
    			} else {
    				remnant.append(ip);
    				remnant.append(it);
    				ip.setLength(0);
    			}
    		}
    		
    		filter = remnant.toString();
    	}
    	
    	// Now look for numbers and words with what's left
    	boolean numeric = true;
    	boolean quotes = false;
    	for (int i = 0; i < filter.length(); i++) {
    		char it = filter.charAt(i);
    		if (it == '"') {
    			if (quotes) {
    				quotes = false;
    				if (term.length() > 0) {
            			if (numeric)
            				numbers.add(term.toString());
            			else
            				words.add(term.toString());
            			term = new StringBuffer();
            		}
    			} else {
    				quotes = true;
    			}
    		} else if (AppConstants.LETTERS_ALL.indexOf(it) >= 0) {
    			numeric = false;
    			term.append(it);
    		} else if (AppConstants.DIGITS.indexOf(it) >= 0) {
    			term.append(it);
    		} else if (quotes) {
    			term.append(it);
    			numeric = false;
    		} else if (term.length() > 0) {
    			if (numeric)
    				numbers.add(term.toString());
    			else
    				words.add(term.toString());
    			term = new StringBuffer();
    			numeric = true;
    		}
    	}
    	if (term.length() > 0) {
			if (numeric)
				numbers.add(term.toString());
			else
				words.add(term.toString());
		}
    	
    	return new TypedTerms(words, numbers, ipStrings, ipRanges, messages);
	}
	
	/**
	 * Return a date given a start date and a number of days.
	 * @param date
	 * @param days
	 * @return
	 */
	public Date getDatePlusDays(Date date, int days) {
//		long newTime = date.getTime() + (days * (24 * 60 * 60 * 1000));
		Date newDate = new Date();
		newDate.setTime(date.getTime());
		CalendarUtil.addDaysToDate(newDate, days);
		return newDate;
	}
}
