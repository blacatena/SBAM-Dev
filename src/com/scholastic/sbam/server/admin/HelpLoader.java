package com.scholastic.sbam.server.admin;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

public class HelpLoader {
	
	public static class HelpPage {
		public static final String idChars = "[({:";
		public static final String idEnds  = "])}:";
		int				level	=	-1;
		String			id		=	"";
		String			parent	=	"";
		String			next	=	"";
		String			prev	=	"";
		StringBuffer	header  =	new StringBuffer();
		StringBuffer	content	=	new StringBuffer();
		
		public HelpPage(HelpPage parentPage, int level) {
			this.level = level;
			if (parentPage == null)
				parent = "";
			else
				parent = parentPage.getId();
		}
		
		public HelpPage(String parent, int level) {
			this.level = level;
			this.parent = parent;
		}
		
		public String getId() {
			if (id == null || id.length() == 0) {
				if (header.length() > 0) {
					char start = 0;
					char end = 0;
					StringBuffer idSb = new StringBuffer();
					int i;
					for (i = 0; i < header.length(); i++) {
						if (idChars.indexOf(header.charAt(i)) >= 0) {
							start = header.charAt(i);
							end   = idEnds.charAt(idChars.indexOf(header.charAt(i)));
							break;
						}
					}
					if (start > 0) {
						for (i++; i < header.length(); i++) {
							if (header.charAt(i) == end)
								break;
							idSb.append(header.charAt(i));
						}
						id = idSb.toString();
					} else
						id = header.toString();
				}
			}
			return id;
		}
		
		public void dump() {
			System.out.println("_______________________________________________");
			System.out.println("ID : " + id);
			System.out.println("Parent : " + parent);
			System.out.println("Prev : " + prev);
			System.out.println("Next : " + next);
			System.out.println("Header : " + header);
			System.out.println("Content : " + content);
		}
	}
	
	protected String	helpHtmlFileName;
	
	Stack<HelpPage> pageStack	=	new Stack<HelpPage>();
	Stack<HTML.Tag> tagStack	=	new Stack<HTML.Tag>();
	
	List<HelpPage> allPages = new ArrayList<HelpPage>();
	
	HelpPage		currentPage;
	int				currentLevel;
	HTML.Tag		currentTag;
	
	StringBuffer	currentSb;
	
	public HelpLoader(String filename) {
		helpHtmlFileName = filename;
	}
	
	public void parseHelpHtml() {
		
		try {
			
			FileInputStream fin =new FileInputStream(helpHtmlFileName);
			DataInputStream in = new DataInputStream(fin);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            HTMLEditorKit.Parser parser;
            System.out.println("About to parse " + helpHtmlFileName);
            parser = new ParserDelegator();
            parser.parse(br, new HTMLParseLister(), true);
            br.close();
            
            System.out.println();
            System.out.println();
            System.out.println();
            
            fixAllNext();
            
            for (HelpPage page : allPages)
            	page.dump();
            
		} catch (Exception exc) {
			System.out.println("Error encountered parsing HTML Help file.");
		}
	}
	

	/**
	* HTML parsing proceeds by calling a callback for
	* each and every piece of the HTML document. This
	* simple callback class simply prints an indented
	* structural listing of the HTML data.
	*/
	class HTMLParseLister extends HTMLEditorKit.ParserCallback {

		public void handleText(char[] data, int pos) {
			text(data, pos);
		}

		public void handleComment(char[] data, int pos) {
		}

		public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
			start(t, a, pos);
		}

		public void handleEndTag(HTML.Tag t, int pos) {
			end(t, pos);
		}

		public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
			simple(t, a, pos);
		}

		public void handleError(String errorMsg, int pos){
		}
	}
	
	public void pageChange(int level) {
		if (level > currentLevel) {
			HelpPage parent = currentPage;
			
			currentPage = new HelpPage(parent, level);
			currentSb = currentPage.header;
			
			allPages.add(currentPage);
			
			currentLevel = level;
			pageStack.push(currentPage);
		} else if (level == currentLevel) {
			HelpPage prev = currentPage;
			
			currentPage = new HelpPage(prev.parent, level);
			currentPage.prev = prev.getId();
			currentSb = currentPage.header;
			
			allPages.add(currentPage);
			
			pageStack.pop();
			pageStack.push(currentPage);
			
		} else if (level < currentLevel) {
			while (!pageStack.empty() && pageStack.peek().level > currentLevel)
				pageStack.pop();
			
			currentPage = pageStack.peek();
			currentSb = currentPage.content;
		}
	}
	
	public void endHeader(int level) {
		if (currentPage != null && currentPage.level == level) {
			currentSb = currentPage.content;
		} else {
			System.out.println("Error -- no current page or wrong current page for level " + level);
		}
	}
	
	public void printAttributes(MutableAttributeSet a) {
		if (a.getAttributeCount() > 0) {
			Enumeration<?> enu = a.getAttributeNames();
			while (enu.hasMoreElements()) {
				Object name = enu.nextElement();
				Object o = a.getAttribute(name);
				currentSb.append(" ");
				currentSb.append(name);
				currentSb.append("=\"");
				currentSb.append(o);
				currentSb.append("\"");
			}
		}
	}
	
	public void printTag(HTML.Tag t, MutableAttributeSet a, boolean simple) {
		if (currentSb == null) {
			System.out.println("No current StringBuffer for " + t.toString());
			return;
		}
		currentSb.append("<");
		currentSb.append(t);
		printAttributes(a);
		if (simple)
			currentSb.append("/>");
		else
			currentSb.append(">");
	}
	
	public void simple(HTML.Tag t, MutableAttributeSet a, int pos) {
		if (t.equals(HTML.Tag.IMG)) {
			printTag(t, a, true);
		} else {
			if (!t.toString().equals("o")) {
				printTag(t, a, true);
			}
		}
	}
	
	public int getPageLevel(HTML.Tag t) {
		if (t == null)
			return 0;
		if (t.equals(HTML.Tag.H1)) {
			return 1;
		} else if (t.equals(HTML.Tag.H2)) {
			return 2;
		} else if (t.equals(HTML.Tag.H3)) {
			return 3;
		} else if (t.equals(HTML.Tag.H4)) {
			return 4;
		} else if (t.equals(HTML.Tag.H5)) {
			return 5;
		} else if (t.equals(HTML.Tag.H6)) {
			return 6;
		} else {
			return 0;
		}
	}
	
	public void start(HTML.Tag t, MutableAttributeSet a, int pos) {
		tagStack.push(t);
		
		int level = getPageLevel(t);
		if (level > 0) {
			pageChange(level);
//		} else if (t.equals(HTML.Tag.DIV)) {
//			
//		} else if (t.equals(HTML.Tag.P)) {
//			
//		} else if (t.equals(HTML.Tag.B)) {
//			
//		} else if (t.equals(HTML.Tag.I)) {
//			
//		} else if (t.equals(HTML.Tag.EM)) {
//			
//		} else if (t.equals(HTML.Tag.U)) {
//			
//		} else if (t.equals(HTML.Tag.TABLE)) {
//				
//		} else if (t.equals(HTML.Tag.TR)) {
//				
//		} else if (t.equals(HTML.Tag.TD)) {
//			
		} else {
			if (!t.toString().equals("o")) {
				printTag(t, a, false);
			}
		}
	}
	
	public void end(HTML.Tag t, int pos) {
		tagStack.pop();
		
		int level = getPageLevel(t);
		if (level > 0) {
			endHeader(level);
		} else {
			if (currentSb == null) {
				System.out.println("No current StringBuffer to end tag " + t.toString());
			} else {
				currentSb.append("</");
				currentSb.append(t);
				currentSb.append(">");
			}
		}
	}
	
	public void text(char[] data, int pos) {
		if (currentSb != null) {
			currentSb.append(data);
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(data);
			System.out.println("No current buffer for " + sb);
		}
	}
	
	public void fixAllNext() {
		HashMap<String, HelpPage> map = new HashMap<String, HelpPage>();
		
		for (HelpPage page : allPages)
			map.put(page.id, page);
		
		for (HelpPage page : allPages) {
			if (page.prev != null && page.prev.length() > 0) {
				HelpPage prevPage = map.get(page.prev);
				prevPage.next = page.id;
			}
		}
	}
	
	public static void main(String [] args) {
		if (args == null || args.length == 0) {
			System.out.println("A file name is required.");
			System.exit(1);
		}
		
		HelpLoader loader = new HelpLoader(args [0]);
		
		loader.parseHelpHtml();
	}
}
