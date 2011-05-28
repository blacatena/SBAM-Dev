package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Base class containing helper methods for use with any class that has IP addresses or address ranges.
 * 
 * @author Bob Lacatena
 *
 */
public abstract class IpAddressInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable {
	
	public static String [] [] getIpOctetStrings(long ipLo, long ipHi) {
		int [] lo = getIpOctets(ipLo);
		int [] hi = getIpOctets(ipHi);
		String [] strLo = new String [4];
		String [] strHi = new String [4];
		for (int i = 0; i < 4; i++) {
			strLo [i] = lo [i] + "";
			strHi [i] = hi [i] + "";
		}
		if (lo [0] == hi [0]) {
			if (lo [1] == hi [1]) {
				if (lo [2] == hi [2]) {
					if (lo [3] == hi [3]) {
						blankOctets(strHi);
					} else if (lo [3] == 0 && hi [3] == 255) {
						blankOctets(strHi);
						strLo [3] = "*";
					}
				} else if (lo [2] == 0 && lo [3] == 0 && hi [2] == 255 && hi [3] == 255) {
					blankOctets(strHi);
					strLo [2] = "*";
					strLo [3] = "";
				}
			} else if (lo [1] == 0 && lo [2] == 0 && lo [3] == 0 && hi [1] == 255 && hi [2] == 255 && hi [3] == 255) {
				blankOctets(strHi);
				strLo [1] = "*";
				strLo [2] = "";
				strLo [3] = "";
			}
		}
		
		return new String [] [] {strLo, strHi};
	}
	
	public static void blankOctets(String [] octets) {
		for (int i = 0; i < octets.length; i++)
			octets [i] = "";
	}
	
	public static int [] getIpOctets(long ip) {
		int o1 = (int) (ip % 256);
		ip = ip / 256;
		int o2 = (int) (ip % 256);
		ip = ip / 256;
		int o3 = (int) (ip % 256);
		ip = ip / 256;
		int o4 = (int) (ip % 256);
		return new int [] {o4, o3, o2, o1};
	}
	
	public static String getOctetForm(int [] octets) {
		return octets [0] + "." + octets [1] + "." + octets [2] + "." + octets [3];
	}
	
	public static String getOctetForm(long ip) {
		return getOctetForm(getIpOctets(ip));
	}
	
	public static Long [] getIpRange(String [] loOctets, String [] hiOctets) {
		long loValue = 0;
		long hiValue = 0;
		if (hiOctets [0] == null || hiOctets [0].length() == 0) {
			//	Low IP is wildcarded or only IP
			for (int i = 0; i < 4; i++) {
				loValue = loValue * 256;
				hiValue = hiValue * 256;
				if (loOctets [i] == null || "*".equals(loOctets [i]) || loOctets [i].length() == 0) {
					// loValue doesn't change
					hiValue = hiValue + 255;
				} else {
					try {
						loValue += Integer.parseInt(loOctets [i]);
						hiValue = loValue;
					} catch (NumberFormatException e) {
						
					}
				}
			}
		} else {
			//	Both IPs have values
			for (int i = 0; i < 4; i++) {
				loValue = loValue * 256;
				hiValue = hiValue * 256;
				try {
					loValue += Integer.parseInt(loOctets [i]);
					hiValue += Integer.parseInt(hiOctets [i]);
				} catch (NumberFormatException e) {
					
				}
			}
		}
		return new Long [] { loValue, hiValue };
	}
	
	public static String getBriefIpDisplay(long ipLo, long ipHi) {
		if (ipLo == ipHi)
			return getOctetForm(ipLo);
		
		String [] [] octets = getIpOctetStrings(ipLo, ipHi);

		StringBuffer result = new StringBuffer();
		
		if (octets [1] [0].length() == 0) {	// High IP is blank, so it's been wildcarded
			for (int i = 0; i < 4; i++) {
				if (octets [0] [i].length() == 0)
					return result.toString();
				if (i > 0)
					result.append(".");
				result.append(octets [0] [i]);
			}
			return result.toString();
		}
		
		//	Okay, it's a complex range
		for (int i = 0; i < 4; i++) {
			if (i > 0)
				result.append(".");
			result.append(octets [0] [i]);
		}
		result.append(" - ");
		for (int i = 0; i < 4; i++) {
			if (i > 0)
				result.append(".");
			result.append(octets [1] [i]);
		}
		
		return result.toString();
	}
	
	public static String getIpDisplay(long ipLo, long ipHi) {
		if (ipHi == 0 || ipHi == ipLo)
			return getOctetForm(ipLo);
		if (ipLo == 0)
			return getOctetForm(ipHi);
		return getOctetForm(ipLo) + " - " + getOctetForm(ipHi);
	}
	
	public static String getCommonIpRangeCode(long ipLo, long ipHi) {
		String rangeLo = getIpRangeCode(ipLo);
		String rangeHi = getIpRangeCode(ipHi);
		int len;
		for (len = 0; len < rangeLo.length() && len < rangeHi.length(); len++) {
			if (rangeLo.charAt(len) != rangeHi.charAt(len)) {
				break;
			}
		}
		if (len <= 0)
			return "";
		return rangeLo.substring(0,len);
	}
	
	public static String getIpRangeCode(long ip) {
		char [] codes = new char [8];
		for (int i = 7; i >= 0; i--) {
			int  left = (int) (ip % 16);
			ip = ip / 16;
			codes [i] = "0123456789ABCDEF".charAt(left);
		}
		return String.copyValueOf(codes);
	}
}
