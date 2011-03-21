package com.scholastic.sbam.client.util;

public class AddressFormatter {
	public static String plusIfNotEmpty(String value, String prefix) {
		if (value == null || value.length() == 0)
			return "";
		return prefix + value;
	}
	
	public static String brIfNotEmpty(String value) {
		return plusIfNotEmpty(value, "<br/>");
	}
	
	public static String commaIfNotEmpty(String value) {
		return plusIfNotEmpty(value, ", ");
	}
	
	public static String spaceIfNotEmpty(String value) {
		return plusIfNotEmpty(value, "&nbsp;&nbsp;&nbsp;");
	}
	
	public static String brIfNotUsa(String value) {
		if (value == null || value.length() == 0)
			return "";
		if (value.equalsIgnoreCase("USA"))
			return "";
		return "<br/>" + value;
	}
	
	public static String getMultiLineAddress(String address1, String address2, String address3, String city, String state, String zip, String country) {
		return getAddress("<br/>", address1, address2, address3, city, state, zip, country);
	}
	
	public static String getOneLineAddress(String address1, String address2, String address3, String city, String state, String zip, String country) {
		return getAddress(", ", address1, address2, address3, city, state, zip, country);
	}
	
	public static String getAddress(String separator, String address1, String address2, String address3, String city, String state, String zip, String country) {
		StringBuffer sb = new StringBuffer();

		if (address1 != null && address1.length() > 0) {
			sb.append(address1);
		}

		if (address2 != null && address2.length() > 0) {
			if (sb.length() > 0)
				sb.append(separator);
			sb.append(address2);
		}
		
		if (address3 != null && address3.length() > 0) {
			if (sb.length() > 0)
				sb.append(separator);
			sb.append(address3);
			return sb.toString();
		}
		
		if ( (city != null && city.length() > 0) || (state != null && state.length() > 0) || ( zip != null && zip.length() > 0) ) {
			if (sb.length() > 0)
				sb.append(separator);
			if (city != null && city.length() > 0) {
				sb.append(city);
				if (state != null && state.length() > 0)
					sb.append(", ");
			}
			if (state != null && state.length() > 0) {
				sb.append(state);
				if (zip != null && zip.length() > 0)
					sb.append(" ");
			}
			if (zip != null && zip.length() > 0)
				sb.append(zip);
		}
		
		if (country != null && country.length() > 0 && !country.equalsIgnoreCase("usa")) {
			if (sb.length() > 0)
				sb.append(separator);
			sb.append(country);
		}
		
		return sb.toString();
	}
}
