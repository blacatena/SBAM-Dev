package com.scholastic.sbam.server.authentication;

/**
 * This class initiates the creation of all authentication export data.
 * 
 * Note that a public static main(String []) method is implemented so that the task can be run as a stand alone java APP (not yet complete). 
 * @author Bob Lacatena
 *
 */
public class AuthenticationGenerator {
	public AuthenticationGenerator() {
		
	}
	
	public static void main(String [] args) {
		//	Establish database connections
		
		//	Generate Export
		new AuthenticationGenerator().generateExport();
	}
	
	protected void generateExport() {
		//	
	}
}
