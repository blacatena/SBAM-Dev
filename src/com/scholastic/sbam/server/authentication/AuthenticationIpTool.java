package com.scholastic.sbam.server.authentication;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.shared.objects.IpAddressInstance;

public class AuthenticationIpTool {
	public long	ipLo;
	public long	ipHi;
	
	public AuthenticationIpTool(long ipLo, long ipHi) {
		this.ipLo = ipLo;
		this.ipHi = ipHi;
	}
	
	public static List<Long []> getSegmented(long ipLo, long ipHi) {
		
		List<Long []> segments = new ArrayList<Long []>();
		
		if (ipLo == 0 && ipHi == 0) {
			System.out.println("Invalid ip range " + ipLo + " / " + ipHi);
			return segments;
		}
		
		if (ipLo == ipHi) {
			segments.add(new Long [] {ipLo, ipHi});
//			segments.add(IpAddressInstance.getBriefIpDisplay(ipLo, ipHi));
			return segments;
		}
		
		int [] loOctets = IpAddressInstance.getIpOctets(ipLo);
		int [] hiOctets = IpAddressInstance.getIpOctets(ipHi);
		
		//	Find break point
		
		int breakpoint = 0;
		for (breakpoint = 0; breakpoint < 4; breakpoint++) {
			if (loOctets [breakpoint] != hiOctets [breakpoint]) {
				break;
			}
		}
		
//		System.out.println("Break at " + breakpoint + " : " + IpAddressInstance.getIpDisplay(ipLo, ipHi));
		

		if (breakpoint > 3) {
			System.out.println("Should have been simple!!!");
		}
		
//		Detect the simplest, most common situations, 
		if (breakpoint == 3)
			if (loOctets [3] == 0 && hiOctets [3] == 255) {
				//	a simple wildcard
				segments.add(new Long [] {ipLo, ipHi});
				return segments;
			} else {
				//	a highest level range -- just cycle through the range
				for (long ip = ipLo; ip <= ipHi; ip++)
					segments.add(new Long [] {ip, ip});
//				for (loOctets [3] = loOctets [3]; loOctets [3] <= hiOctets [3]; loOctets [3]++)
//					segments.add(IpAddressInstance.getOctetForm(loOctets));
				return segments;
			}

		
//		Detect another common situation, a higher level wildcard
		int fullrange;
		for (fullrange = breakpoint; fullrange < 4; fullrange++) {
			if (loOctets [fullrange] != 0 && hiOctets [fullrange] != 255)
				break;
			if (fullrange >= 3) {
				//	Okay, this is a wildcard
				segments.add(new Long [] {ipLo, ipHi});
//				segments.add(IpAddressInstance.getBriefIpDisplay(ipLo, ipHi));
				return segments;
			}
		}

		System.out.println("Something complicated:" + IpAddressInstance.getBriefIpDisplay(ipLo, ipHi) + " from " + ipLo + " to " + ipHi);
		
//		If we got this far, we must have some mixed wildcards and ranges
		
		int [] newOctets = new int [4];
		for (int i = 0; i < breakpoint; i++)
			newOctets [i] = loOctets [i];
		
		//	Do low octet
		
		
		
		//	Do in-between octets
		
		
		//	Do high octet
		for (int octet = loOctets [breakpoint]; octet <= hiOctets [breakpoint]; octet++) {
			if (breakpoint == 4) {
				//	For last octet, just write this IP
				newOctets [4] = octet;
//				segments.add(IpAddressInstance.getOctetForm(newOctets));
			} else {
				
			}
		}
		
		return segments;
	}
}
