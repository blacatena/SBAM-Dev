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
	
	public static String getCommonIpRangeCode(long ipLo, long ipHi) {
		String rangeLo = getIpRangeCode(ipLo);
		String rangeHi = getIpRangeCode(ipHi);
		int len;
		for (len = 0; len < rangeLo.length() && len < rangeHi.length(); len++) {
			if (rangeLo.charAt(len) != rangeLo.charAt(len))
				break;
		}
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
