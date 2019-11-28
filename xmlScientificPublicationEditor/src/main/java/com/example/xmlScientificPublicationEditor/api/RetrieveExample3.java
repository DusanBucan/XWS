package com.example.xmlScientificPublicationEditor.api;

import javax.xml.transform.OutputKeys;

import org.exist.xmldb.EXistResource;
import org.springframework.stereotype.Service;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import com.example.xmlScientificPublicationEditor.util.AuthenticationUtilities;
import com.example.xmlScientificPublicationEditor.util.AuthenticationUtilities.ConnectionProperties;

@Service
public class RetrieveExample3 {
    
	// public static void main(String[] args) throws Exception {
	// 	RetrieveExample3.run(AuthenticationUtilities.loadProperties(), args);
	// }
    
    /**
     * conn XML DB connection properties
     * args[0] Should be the collection ID to access
     * args[1] Should be the document ID to store in the collection
     */
    public String run() throws Exception {
       
        ConnectionProperties conn = AuthenticationUtilities.loadProperties();

    	System.out.println("[INFO] " + RetrieveExample3.class.getSimpleName());
    	
    	// initialize collection and document identifiers
        String collectionId = null;
		String documentId = null; 
        
        if (false) {
        } else {
        	
        	System.out.println("[INFO] Using defaults.");
        	
        	collectionId = "/db/sample/library";
        	documentId = "books.xml";
        }

        System.out.println("\t- collection ID: " + collectionId);
    	System.out.println("\t- document ID: " + documentId + "\n");
        
        // initialize database driver
    	System.out.println("[INFO] Loading driver class: " + conn.driver);
        Class<?> cl = Class.forName(conn.driver);
        
        Database database = (Database) cl.newInstance();
        database.setProperty("create-database", "true");
        
        DatabaseManager.registerDatabase(database);
        
        Collection col = null;
        XMLResource res = null;
        
        try {    
            // get the collection
        	System.out.println("[INFO] Retrieving the collection: " + collectionId);
            col = DatabaseManager.getCollection(conn.uri + collectionId);
            col.setProperty(OutputKeys.INDENT, "yes");
            
            System.out.println("[INFO] Retrieving the document: " + documentId);
            res = (XMLResource)col.getResource(documentId);
            
            if(res == null) {
                System.out.println("[WARNING] Document '" + documentId + "' can not be found!");

                return "nesto se sjebalo";
            } else {
            	
            	System.out.println("[INFO] Showing the document as XML resource: ");
                System.out.println(res.getContent());
                
                return res.getContent().toString();
            }
        } finally {
            //don't forget to clean up!
            
            if(res != null) {
                try { 
                	((EXistResource)res).freeResources(); 
                } catch (XMLDBException xe) {
                	xe.printStackTrace();
                }
            }
            
            if(col != null) {
                try { 
                	col.close(); 
                } catch (XMLDBException xe) {
                	xe.printStackTrace();
                }
            }
        }
    }
}