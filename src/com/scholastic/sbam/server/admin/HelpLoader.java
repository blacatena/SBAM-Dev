package com.scholastic.sbam.server.admin;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import com.scholastic.sbam.server.database.codegen.HelpText;
import com.scholastic.sbam.server.database.objects.DbHelpText;
import com.scholastic.sbam.server.database.util.HibernateUtil;

/**
 * This class will read a Word generated HTML file and reload the HELP_TEXT table contents from it.
 * 
 * The Word file should use headers 1 to 6 to nest entries.  Headers beyond 6 are not supported.
 * 
 * The Header entry may contain an image.  If so it will be stripped, 
 * @author Bob Lacatena
 *
 */
public class HelpLoader {
	
	public static final String DEFAULT_ICON_PATH		= "resources/images/icons/colorful/";
	public static final String DEFAULT_HELP_IMAGE_PATH	= "resources/images/help/";
	public static final String ID_START_CHARS			= "[({|";
	public static final String ID_END_CHARS 			= "])}|";
	public static final String PAGE_REFERENCES_START	=	"<p class=\"PageReferences\">";
	
	public static class HelpPage {
		
		int				level			=	-1;
		String			id				=	"";
		
		HelpPage		parentPage		=	null;
		HelpPage		firstChildPage	=	null;
		HelpPage		prevPage		=	null;
		HelpPage		nextPage		=	null;
		
		String			relatedIds		=	"";
		
		String			iconName		=	"";
		StringBuffer	title  			=	new StringBuffer();
		StringBuffer	content			=	new StringBuffer();
		
		public HelpPage(HelpPage parentPage, int level) {
			this.level = level;
			this.parentPage = parentPage;
			if (parentPage != null && parentPage.firstChildPage == null)
				parentPage.firstChildPage = this;
		}
		
		public void setPrevPage(HelpPage prevPage) {
			if (prevPage != null) {
				prevPage.nextPage = null;
			}
			this.prevPage = prevPage;
			if (prevPage != null) {
				prevPage.nextPage = this;
			}
		}
		
		public String getId() {
			if (id == null || id.length() == 0) {
				if (title.length() > 0) {
					int	 startPos = 0;
					int  endPos   = 0;
					char start = 0;
					char end = 0;
					StringBuffer idSb = new StringBuffer();
					int i;
					for (i = 0; i < title.length(); i++) {
						if (ID_START_CHARS.indexOf(title.charAt(i)) >= 0) {
							startPos = i;
							start = title.charAt(i);
							end   = ID_END_CHARS.charAt(ID_START_CHARS.indexOf(title.charAt(i)));
							break;
						}
					}
					if (start > 0) {
						for (i++; i < title.length(); i++) {
							if (title.charAt(i) == end) {
								endPos = i;
								break;
							}
							idSb.append(title.charAt(i));
						}
						id = idSb.toString().trim();
						
						if (endPos > 0 && endPos < title.length()) {
							title.delete(startPos, endPos + 1);
						} else
							title.delete(startPos, title.length());
					} else {
						id = title.toString().trim();
					}
				}
			}
			if (id == null)
				id = "";
			return id;
		}
		
		public String toString() {
			return getId();
		}
		
		public void dump() {
			System.out.println("_______________________________________________");
			System.out.println("ID : " + getId());
			System.out.println("Parent : " + parentPage);
			System.out.println("Prev : " + prevPage);
			System.out.println("Next : " + nextPage);
			System.out.println("Header : " + title);
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
            
            fillInRelated();
            
            extractIconNames();
            
            stripHeaderTags();
            
            System.out.println();
            System.out.println();
            System.out.println();
            
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
	
	protected void pageChange(int level) {
		 if (level < currentLevel) {
			while (!pageStack.empty() && pageStack.peek().level > level) {
				pageStack.pop();
				currentLevel--;
			}
			
			currentPage = pageStack.peek();
			if (currentPage != null) {
				currentLevel = currentPage.level;
				currentSb = currentPage.content;
			}
		 }
		 
		if (level > currentLevel) {
			HelpPage parent = currentPage;
			
			currentPage = new HelpPage(parent, level);
			currentSb = currentPage.title;
			
			allPages.add(currentPage);
			
			currentLevel = level;
			pageStack.push(currentPage);
		} else if (level == currentLevel) {
			HelpPage prevPage = currentPage;
			
			currentPage = new HelpPage(prevPage.parentPage, level);
			currentPage.setPrevPage(prevPage);
			currentSb = currentPage.title;
			
			allPages.add(currentPage);
			
			pageStack.pop();
			pageStack.push(currentPage);
			
		}
	}
	
	protected void endHeader(int level) {
		if (currentPage != null && currentPage.level == level) {
			currentSb = currentPage.content;
		} else {
			System.out.println("Error -- no current page or wrong current page for level " + level);
		}
	}
	
	protected void printAttributes(HTML.Tag t, MutableAttributeSet a) {
		boolean largeImage	= false;
		String	imageName	= null;
		
		if (a.getAttributeCount() > 0) {
			Enumeration<?> enu = a.getAttributeNames();
			while (enu.hasMoreElements()) {
				Object name = enu.nextElement();
				Object o = a.getAttribute(name);
//				if ("class".equals(name.toString())) {
//					System.out.println("=============Class " + o);
//					if ("MsoNormal".equals(o.toString()))
//						continue;
//				}
				if ("style".equals(name.toString())) {
//					o = cleanStyleAttribute(o);
					continue;	//	Eliminate all style tags... use CSS with Mso Styles
				}
				//	For images
				if (t.equals(HTML.Tag.IMG)) {
					String nameStr = name.toString();
					//	For images, skip any "width" or "height" attributes
					if ("width".equals(nameStr) || "height".equals(nameStr)) {
						if (o.toString().length() > 0) {
							try {
								if (Integer.parseInt(o.toString()) > 20) {
									largeImage = true;
									System.out.println("large image");
								}
							} catch (NumberFormatException exc) {
								System.out.println("width/height exception " + exc);
							}
						}
						continue;
					}
					//	For images, skip any "v" or "shapes" attributes
					if ("v".equals(nameStr) || "shapes".equals(nameStr))
						continue;
					//	For images, skip the src tag, and change the alt attribute to the src attribute
					if ("src".equals(nameStr))
						continue;
					//	Also use the alt to generate the proper src attribute from the image name
					if ("alt".equals(nameStr)) {
						imageName = o.toString();
						//	Write the src last
//						currentSb.append(" src=\"");
//						currentSb.append(alterImagePathAttribute(o, largeImage));
//						currentSb.append("\"");
					}
				}
				currentSb.append(" ");
				currentSb.append(name);
				currentSb.append("=\"");
				currentSb.append(o);
				currentSb.append("\"");
			}
		}
		
		if (imageName != null) {
			currentSb.append(" src=\"");
			currentSb.append(alterImagePathAttribute(imageName, largeImage));
			currentSb.append("\"");
		}
	}
	
	protected Object cleanStyleAttribute(Object attr) {
		if (attr instanceof String) {
			StringBuffer sb = new StringBuffer((String) attr);
			while (true) {
				int start = sb.indexOf("mso-");
				if (start < 0)
					break;
				
				int end = sb.indexOf(";", start);
				if (end >= 0)
					sb.delete(start, end + 1);
				else
					sb.delete(start, sb.length());
			}
			
			return sb.toString();
		}
		
		return attr;
	}
	
	protected Object alterImagePathAttribute(Object attr, boolean largeImage) {
		if (attr instanceof String) {
			String imageFull = (String) attr;
			for (int i = imageFull.length() - 1; i >= 0; i--) {
				if (imageFull.charAt(i) == '/') {
					String imageName = imageFull.substring(i + 1);
					//	PNG images are assumed to be icons.  All others (JPG, GIF) are assumed to be explicit help images and to reside in the help image directory.
					return getImageWithPath(imageName, largeImage);
				}
			}
			return getImageWithPath(imageFull, largeImage);
		}
		
		return attr;
	}
	
	protected String getImageWithPath(String imageName, boolean largeImage) {
		if (imageName.endsWith(".png") && !largeImage)
			return DEFAULT_ICON_PATH + imageName;
		else
			return DEFAULT_HELP_IMAGE_PATH + imageName;
	}
	
	protected void printTag(HTML.Tag t, MutableAttributeSet a, boolean simple) {
		if (currentSb == null) {
			System.out.println("No current StringBuffer for " + t.toString());
			return;
		}
		currentSb.append("<");
		currentSb.append(t);
		printAttributes(t, a);
		if (simple)
			currentSb.append("/>");
		else
			currentSb.append(">");
	}
	
	protected void simple(HTML.Tag t, MutableAttributeSet a, int pos) {
		if (t.equals(HTML.Tag.IMG)) {
			printTag(t, a, true);
		} else {
			if (!t.toString().equals("o")) {
				printTag(t, a, true);
			}
		}
	}
	
	protected int getPageLevel(HTML.Tag t) {
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
	
	protected void start(HTML.Tag t, MutableAttributeSet a, int pos) {
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
	
	protected void end(HTML.Tag t, int pos) {
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
	
	protected void text(char[] data, int pos) {
		if (currentSb != null) {
			currentSb.append(data);
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(data);
			System.out.println("No current buffer for " + sb);
		}
	}
	
	//	For all pages, find the related entries in the content and extract them
	protected void fillInRelated() {
		for (HelpPage page : allPages) {

			if (page.content != null && page.content.length() > 0) {
				int pageRefStart = page.content.indexOf(PAGE_REFERENCES_START);
				if (pageRefStart < 0)
					continue;
				
				int pageRefEnd = page.content.indexOf("</p>", pageRefStart) + "</p>".length();
				
				int listStart = pageRefStart + PAGE_REFERENCES_START.length();
				
				//	Skip blanks and useless tags like <span>
				while (true) {
					while (listStart < page.content.length() && page.content.charAt(listStart) == ' ')
						listStart++;
					if (page.content.charAt(listStart) != '<')
						break;
					while (listStart < page.content.length() && page.content.charAt(listStart) != '>')
						listStart++;
					listStart++;
				}
				
				//	Skip to next tag, and everything in between is used as content
				if (listStart < page.content.length()) {
					int listEnd = listStart;
					while (listEnd < page.content.length() && page.content.charAt(listEnd) != '<')
						listEnd++;
					
					page.relatedIds = page.content.substring(listStart, listEnd);
					page.content.delete(pageRefStart, pageRefEnd);
				}
			}
			
		}
	}
	
	//	For all pages, find the icon name in the header and extract and adjust it
	protected void extractIconNames() {
		final String ALT_START = "alt=\"";
		for (HelpPage page : allPages) {
			
			if (page.title != null && page.title.length() > 0) {
				System.out.println(page.title);
				int pageImgStart = page.title.indexOf("<img");
				if (pageImgStart >= 0) {
					int pageImgEnd = page.title.indexOf(">", pageImgStart) + 1;
					
					int imageNameStart = page.title.indexOf(ALT_START, pageImgStart);
					if (imageNameStart >= 0) {
						int imageNameEnd = page.title.indexOf("\"", imageNameStart + ALT_START.length());
						imageNameStart = imageNameEnd - 1;
						while (imageNameStart < imageNameEnd && page.title.charAt(imageNameStart) != '/' && page.title.charAt(imageNameStart) != '"') {
							imageNameStart--;
						}
						imageNameStart++;
						page.iconName = DEFAULT_ICON_PATH + page.title.substring(imageNameStart, imageNameEnd);
					}
					
					page.title.delete(pageImgStart, pageImgEnd);
				}
				System.out.println(page.title);
				System.out.println("-------------------------------");
			}
			
		}
	}
	
	protected void stripHeaderTags() {
		for (HelpPage page : allPages) {
			if(page.title != null) {
				while (true) {
					int tagStart = page.title.indexOf("<");
					if (tagStart < 0)
						break;
					int tagEnd = page.title.indexOf(">", tagStart);
					if (tagEnd < 0)
						break;
					page.title.delete(tagStart, tagEnd + 1);
				}
			}
		}
	} 
	
	protected String trimmed(StringBuffer sb) {
		while (sb.length() > 0 && (sb.charAt(0) == ' ' || sb.charAt(0) == 160) ) {
			sb.delete(0, 1);
		}
		while (sb.length() > 0 && (sb.charAt(sb.length() - 1) == ' ' || sb.charAt(sb.length() - 1) == 160) ) {
			sb.delete(sb.length() - 1, sb.length());
		}
		
		return sb.toString();
	}
	
	protected void loadDatabase() {

		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		List<HelpText> oldHelp = DbHelpText.findAll();
		for (HelpText ht : oldHelp)
			DbHelpText.delete(ht);
		
		for (HelpPage page : allPages) {
			
			HelpText ht = new HelpText();
			
			ht.setId(page.getId());
			ht.setIconName(page.iconName);
			ht.setTitle(trimmed(page.title));
			
			String content;
			if (page.iconName != null && page.iconName.length() > 0) {
				content = "<h1>" + 
							"<img src=\"" + page.iconName + "\"/> " +
							trimmed(page.title) + "</h1><br/>" + trimmed(page.content);
			} else {
				content = "<h1>" + 
							trimmed(page.title) + "</h1><br/>" + trimmed(page.content);
			}
			
			ht.setText(content);
			ht.setRelatedIds(page.relatedIds);
			
			if (page.parentPage == null)
				ht.setParentId("");
			else
				ht.setParentId(page.parentPage.getId());
			
			if (page.firstChildPage == null)
				ht.setFirstChildId("");
			else
				ht.setFirstChildId(page.firstChildPage.getId());
			
			if (page.prevPage == null)
				ht.setPrevSiblingId("");
			else
				ht.setPrevSiblingId(page.prevPage.getId());
			
			if (page.nextPage == null)
				ht.setNextSiblingId("");
			else
				ht.setNextSiblingId(page.nextPage.getId());
			
			DbHelpText.persist(ht);
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}
	
	public void doLoad() {
		parseHelpHtml();
		
		if (allPages.size() > 0)
			loadDatabase();
	}
	
	public List<HelpPage> getAllPages() {
		return allPages;
	}

	public static void main(String [] args) {
		if (args == null || args.length == 0) {
			System.out.println("A file name is required.");
			System.exit(1);
		}
		
		HelpLoader loader = new HelpLoader(args [0]);
		
		loader.doLoad();
	}
}
