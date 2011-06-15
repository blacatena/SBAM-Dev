package com.scholastic.sbam.server.authentication;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.shared.objects.IpAddressInstance;

public class AuthenticationIpTool {
	
	/**
	 * This method segments a range of IP addresses in the simple way as is expected in the export output, that is either as a single IP or as a full wildcard.
	 * @param ipLo
	 * @param ipHi
	 * @return
	 */
	public static List<Long []> getSegmented(long ipLo, long ipHi) {
		
		List<Long []> segments = new ArrayList<Long []>();

		//	Optimization for simplest case of a single IP
		if (ipLo == ipHi) {
			segments.add(new Long [] {ipLo, ipHi});
			return segments;
		}
		
		//	More complex... go through ranges, looking for valid wildcards when possible
		long ip = ipLo;
		while (ip <= ipHi) {
			long next = ip;
			if (ip % 256 == 0) {
				long next1 = ip + 255;
				if (next1 <= ipHi) {
					next = next1;
					if (ip % (256 * 256) == 0) {
						long next2 = ip + 256 * 256 - 1;
						if (next2 <= ipHi) {
							next = next2;
							if (ip % (256 * 256 * 256) == 0) {
								long next3 = ip + 256 * 256 * 256 - 1;
								 if (next3 <= ipHi) {
									 next = next3;
								 }
							}
						}
					}
				}
			}
			segments.add(new Long [] {ip, next});
			ip = next + 1;
		}
		
		return segments;
	}
	
	/**
	 * Just some a main method to do some simple testing/debugging.
	 * @param args
	 */
	public static void main(String [] args) {
		System.out.println(IpAddressInstance.getBriefIpDisplay(3460290040L, 3460290305L));
		List<Long []> test = getSegmented(3460290040L, 3460290305L);
		for (Long [] range : test)
			System.out.println(IpAddressInstance.getBriefIpDisplay(range));
		System.out.println("---------------------");
		
		System.out.println(IpAddressInstance.getBriefIpDisplay(3460290050L, 3460290302L));
		test = getSegmented(3460290050L, 3460290302L);
		for (Long [] range : test)
			System.out.println(IpAddressInstance.getBriefIpDisplay(range));
		System.out.println("---------------------");
		
		System.out.println(IpAddressInstance.getBriefIpDisplay(2251096064L, 2251161599L));
		test = getSegmented(2251096064L, 2251161599L);
		for (Long [] range : test)
			System.out.println(IpAddressInstance.getBriefIpDisplay(range));
		
	}
}
