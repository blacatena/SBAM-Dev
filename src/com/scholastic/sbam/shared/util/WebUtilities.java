package com.scholastic.sbam.shared.util;

public class WebUtilities {
	
	/**
	 * Convert a string for presentation in HTML by converting end of lines/returns to <br/>
	 * @param value
	 * @return
	 */
	public static String getAsHtml(String value) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < value.length(); i++) {
			if (value.charAt(i) == '\r' || value.charAt(i) == '\n') {
				sb.append("<br/>");
				sb.append('\n');
			} else {
				sb.append(value.charAt(i));
			}
		}
		return sb.toString();
	}
}
