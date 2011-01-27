package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.ProductServiceListService;
import com.scholastic.sbam.server.database.codegen.ProductService;
import com.scholastic.sbam.server.database.codegen.Service;
import com.scholastic.sbam.server.database.objects.DbProductService;
import com.scholastic.sbam.server.database.objects.DbService;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.ProductServiceTreeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service to list product service assignments.
 * 
 * Note that this class can be generalized for other implementations by making ProductServiceTreeInstance an implementation that requires the
 * getters and setters for Description, Type, and Children.
 */
@SuppressWarnings("serial")
public class ProductServiceListServiceImpl extends AuthenticatedServiceServlet implements ProductServiceListService {

	public static final char PATH_DELIMITER = '/';
	public static final char PATH_ESCAPE	= '\\';
	
	@Override
	public List<ProductServiceTreeInstance> getProductServices(String productCode, LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list product services", SecurityManager.ROLE_CONFIG);
		
		if (productCode == null || productCode.length() == 0)
			throw new IllegalArgumentException("Product code is a required argument.");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		ProductServiceTreeInstance root = null;

		List<ProductServiceTreeInstance> list = new ArrayList<ProductServiceTreeInstance>();
		try {
			
			//	Find only undeleted product services
			List<ProductService> productServices = DbProductService.findByProduct(productCode, 'X');
			
			//	Create a hash set of the selected product services
			HashSet<String> selectedServices = new HashSet<String>();
			for (ProductService productService : productServices) {
				selectedServices.add(productService.getId().getServiceCode());
			}
			
			List<Service> services = DbService.findUndeleted();

			for (Service service : services) {
				ProductServiceTreeInstance instance = new ProductServiceTreeInstance();
				
				instance.setProductCode(productCode);
				instance.setServiceCode(service.getServiceCode());
				instance.setDescription(service.getDescription());
				instance.setType(ProductServiceTreeInstance.SERVICE);
				instance.setSelected(selectedServices.contains(service.getServiceCode()));
				
				root = addFolderTree(list, root, instance, service.getPresentationPath());
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
	
	/**
	 * Return an instance to add to the response list which incorporates the previous tree structure.
	 * 
	 * This must be done by parsing the path strings for each individual service, and creating "folder" elements and child relationships where needed.
	 * 
	 * Note that names are not necessarily unique, so sequence matters.  That is, the path /a/b/c may refer to different folders than the same path encountered later.
	 * 
	 * For example:
	 * 
	 * 		product A, path a/b/c
	 * 		product B, path a
	 * 		product C, path a/b/c
	 * 
	 * This results in one a directory, but two branches of b/c directories, because product B lies between A and C.  However, if A and C came one after the other, without B
	 * in between, then they would share the same a/b/c directory structure.
	 */
	protected ProductServiceTreeInstance addFolderTree(List<ProductServiceTreeInstance> list, ProductServiceTreeInstance root, ProductServiceTreeInstance instance, String path) {
		
		//	If there's no path, put this service at the root
		if (path == null || path.trim().length() == 0) {
//			System.out.println("No path: add " + instance.getDescription());
			list.add(instance);
			return null;
		}
		
		//	Parse the path into consecutive folders
		String [] folders = parsePath(path);
		
		//	If the path has no true contents, put this service at the root
		if (folders.length == 0) {
//			System.out.println("No folders: add " + instance.getDescription());
			list.add(instance);
			return null;
		}
		
		//	If there's no previous root or the base root folder doesn't math, create a completely new tree from this path
		if (root == null || !root.getDescription().equals(folders [0])) {
//			System.out.println("New root: create for " + instance.getDescription() + " ... " + path);
			root = getNewFolder(folders [0]);
			addChild(addFolders(root, folders, 1), instance);
			list.add(root);
			return root;
		}
		
		//	If the path is non-zero in length but part of the previous root at some point, find the point of commonality in the current tree, then branch
		addToRoot(root, instance, folders, 1);
		
		//	Important!!  There's no need to add this root to the list, because it should already have been put there when it was created.
		
		return root;
	}
	
	/**
	 * Create and return a new folder instance.
	 * @param name
	 * @return
	 */
	private ProductServiceTreeInstance getNewFolder(String name) {
		ProductServiceTreeInstance folder = new ProductServiceTreeInstance();
		folder.setDescription(name);
		folder.setType(ProductServiceTreeInstance.FOLDER);
		return folder;
	}
	
	/**
	 * Follow the paths in a root instance, and add a new instance as a child, finding or creating any intermediate folders as necessary.
	 * @param parent
	 * 	The instance to act as the parent.
	 * @param instance
	 * 	The instance to be added as a child.
	 * @param folders
	 * 	The list of folders.
	 * @param i
	 *  The starting point in the list of folders corresponding to the current parent element.
	 */
	protected void addToRoot(ProductServiceTreeInstance parent, ProductServiceTreeInstance instance, String [] folders, int i) {
//		System.out.println("addToRoot: for " + instance.getDescription() + " ::: " + parent.getDescription());
		if (i >= folders.length)	// No more folders, so append here
			addChild(parent, instance);
		else {
			ProductServiceTreeInstance lastChild = getLastChild(parent);
			if (lastChild == null)	//	If there's nothing below this, add from here
				addChild(addFolders(parent, folders, i), instance);
			else { // If there is a child, test it for a match
				 if (ProductServiceTreeInstance.FOLDER.equals(lastChild.getType()) && lastChild.getDescription().equals(folders [i])) // Folders match, so go to the next
					 addToRoot(lastChild, instance, folders, i+1);
				 else // Folders don't match, or it's not a folder, so add remaining folders here, than add to that
					 addChild(addFolders(parent, folders, i), instance);
			}
		}
	}
	
	/**
	 * Add a sequence of nested folders, starting from a specified point in an array of folder names, to a particular instance.
	 * @param parent
	 *  The instance to which the folders will be added.
	 * @param folders
	 *  The array of folder names.
	 * @param start
	 *  The point at which to start using names from the array.
	 * @return
	 */
	protected ProductServiceTreeInstance addFolders(ProductServiceTreeInstance parent, String [] folders, int start) {
		for (int i = start; i < folders.length; i++) {
			ProductServiceTreeInstance child = getNewFolder(folders [i]);
			addChild(parent, child);
			parent = child;
		}
		return parent;
	}
	
	/**
	 * Add an instance as the next child of a parent instance.
	 * @param parent
	 * @param child
	 */
	protected void addChild(ProductServiceTreeInstance parent, ProductServiceTreeInstance child) {
		//	Copy the children to a new array, one larger, and put the new child in that last spot
		ProductServiceTreeInstance [] children = new ProductServiceTreeInstance [parent.getChildren().length + 1];
		for (int i = 0; i < parent.getChildren().length; i++)
			children [i] = parent.getChildren() [i];
		children [children.length - 1] = child;
		parent.setChildren(children);
	}
	
	/**
	 * Get the last folder item (not necessarily the last item) in an instance.
	 * 
	 * This method is not currently used.
	 * 
	 * @param instance
	 * @return
	 */
	protected ProductServiceTreeInstance getLastFolder(ProductServiceTreeInstance instance) {
		if (instance.getChildren() == null || instance.getChildren().length == 0)
			return null;
		for (int i = 0; i < instance.getChildren().length; i++)
			if (instance.getChildren() [i].getType() == ProductServiceTreeInstance.FOLDER)
				return instance.getChildren() [i];
		return null;
	}
	
	/**
	 * Get the last child of an instance, regardless of type.
	 * @param instance
	 * @return
	 */
	protected ProductServiceTreeInstance getLastChild(ProductServiceTreeInstance instance) {
		if (instance.getChildren() == null || instance.getChildren().length == 0)
			return null;
		return instance.getChildren() [ instance.getChildren().length - 1];
	}
	
	/**
	 * Parse a string of folder names delimited by the default delimiter and using the default escape character.
	 * 
	 * @param path
	 * @return
	 */
	protected String [] parsePath(String path) {
		return parsePath(path, PATH_DELIMITER, PATH_ESCAPE);
	}
	
	/**
	 * Parse a string of folder names where the very first character is the escape character and the next character is the delimiter character.
	 * 
	 * This method is not currently used, but is available for use by a subclass that overrides parsePath(String) to do so.
	 * 
	 * @param path
	 * @return
	 */
	protected String [] parsePathVariable(String path) {
		if (path.length() < 2)
			return parsePath(path);
		char escapeChar = path.charAt(0);
		char delimiter  = path.charAt(1);
		return parsePath(path, delimiter, escapeChar);
	}
	
	/**
	 * Parse a string of folder names.
	 * 
	 * Names will be delimited by a specified character, which may be escaped with a specified escape character.
	 * 
	 * The escape character itself may also be escaped.
	 * 
	 * @param path
	 * @param delimiter
	 * @param escapeChar
	 * @return
	 */
	protected String [] parsePath(String path, char delimiter, char escapeChar) {
		path = path.trim();
		List<String> folders = new ArrayList<String>();
		int begin=0;
		boolean escaped = false;
		for (int i = 0; i < path.length(); i++) {
			if (path.charAt(i) == delimiter) {
				if (!escaped) {
					if (i > begin) {
						folders.add(path.substring(begin, i));
						begin = i + 1;
					}
				}
			} else if (path.charAt(i) == escapeChar)
				escaped = !escaped;
			else
				escaped = false;
		}
		
		if (begin < path.length()) {
			folders.add(path.substring(begin));
		}
		
		return folders.toArray(new String [] {});
	}
}
