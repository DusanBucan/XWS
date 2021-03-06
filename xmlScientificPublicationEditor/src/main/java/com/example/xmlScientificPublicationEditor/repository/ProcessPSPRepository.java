package com.example.xmlScientificPublicationEditor.repository;

import static com.example.xmlScientificPublicationEditor.util.template.XUpdateTemplate.TARGET_NAMESPACE;

import java.util.ArrayList;
import java.util.HashMap;

import org.exist.xmldb.EXistResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import com.example.xmlScientificPublicationEditor.exception.ResourceNotDeleted;
import com.example.xmlScientificPublicationEditor.model.ProcessState;
import com.example.xmlScientificPublicationEditor.model.person.TPerson;
import com.example.xmlScientificPublicationEditor.model.person.TPersons;
import com.example.xmlScientificPublicationEditor.service.IdGeneratorService;
import com.example.xmlScientificPublicationEditor.service.NotificationService;
import com.example.xmlScientificPublicationEditor.service.PersonService;
import com.example.xmlScientificPublicationEditor.util.DOMParser.DOMParser;
import com.example.xmlScientificPublicationEditor.util.existAPI.RetriveFromDB;
import com.example.xmlScientificPublicationEditor.util.existAPI.StoreToDB;
import com.example.xmlScientificPublicationEditor.util.existAPI.UpdateDB;

@Repository
public class ProcessPSPRepository {

	

	@Autowired
	private IdGeneratorService idGeneratorService;

	@Autowired
	private PersonService personService;
	
	@Autowired
	private NotificationService notificationService;
	
	public static String ScientificPublicationFiled = "ns:scientificPublication";
	public static String ScientificPublicationNameFiled = "ns:scientificPublicationName";
	public static String RedactorFiled = "ns:idRedactor";
	public static String CoverLetterFiled = "ns:coverLetter";
	public static String PROCESS_ID = "id";
	public static String AUTHOR_EMAIL = "authorEmail";
	public static String PROCESS_ROOT = "ns:processPSP";
	public static String ReviewAssignments = "ns:reviewAssignments";
	public static String ReviewAssigment = "ns:reviewAssigment";
	public static String Review = "ns:review";
	public static String IdReviewer = "ns:idReviewer";
	public static String VERSIONS = "ns:versions";
	public static String VERSION = "ns:version";
	private static final String VERSION_VERSION = "version";
	public static final String PROCESS_LAST_VERSION = "lastVersion";
	public static final String PROCESS_STATE = "state";
	public static final String PROCESS_AUTHOR_SP = "authorEmail";
	public static String IN_PROGRESS = "inProgress";
	public static String PENDING = "pending";
	public static String ACCEPTED = "accepted";
	

	public static final String ProcessPSPXSLSPId = "src/main/resources/data/xslt/processPSPgetLastSCId.xsl";


    public static String ProcessPSPTemplatePath = "src/main/resources/data/xml/processPSPTemplate.xml";
	public static String ProcessPSPCollectionId = "/db/sample/processPSP";
	public static String ProcessPSPSchemaPath = "src/main/resources/data/schemas/processPSP.xsd";
	public static String ReviewAssigmentSchemaPath = "src/main/resources/data/schemas/reviewAssignment.xsd";

	public static String ProcessPSPXSLForReview = "src/main/resources/data/xslt/processforRevision.xsl";

	public static String Questionnaire_Save_to_ProcessPSP = "src/main/resources/data/xQuery/reviewUpdateProcessPSP.txt";
	public static String Questionnaire_Save_to_State_ProcessPSP
			= "src/main/resources/data/xQuery/reviewStateUpdateProcessPSP.txt";

	public static String Change_toScored_State_ProcessPSP =
			"src/main/resources/data/xQuery/changeProcessPSPstateToScored.txt";

	public static  String getSCName_forVersion_ProcessPSP =
			"src/main/resources/data/xQuery/findSCNameFromLastVersionOfPSP.txt";

	public static String Comments_Save_to_ProcessPSP = "src/main/resources/data/xQuery/reviewAddComments.txt";


	public String findOneByScientificPublicationID(String id) throws Exception {
		String retVal = null;
		String xpathExp = "//ns:processPSP[ns:scientificPublication=\"" + id + "\"]";
		ResourceSet resultSet = RetriveFromDB.executeXPathExpression(ProcessPSPCollectionId, xpathExp,
				TARGET_NAMESPACE);
		if (resultSet == null) {
			return retVal;
		}
		ResourceIterator i = resultSet.getIterator();
		XMLResource res = null;
		while (i.hasMoreResources()) {
			try {
				res = (XMLResource) i.nextResource();
				retVal = res.getContent().toString();
				return retVal;
			} finally {
				// don't forget to cleanup resources
				try {
					((EXistResource) res).freeResources();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}
		return null;
	}

	public Document findOneById(String processId) throws Exception {
		String xpathExp = "//processPSP[@id=\"" + processId + "\"]";
		ResourceSet resultSet = RetriveFromDB.executeXPathExpression(ProcessPSPCollectionId, xpathExp,
				TARGET_NAMESPACE);
		if (resultSet == null) {
			return null;
		}
		ResourceIterator i = resultSet.getIterator();
		XMLResource res = null;
		while (i.hasMoreResources()) {
			try {
				res = (XMLResource) i.nextResource();
				return DOMParser.buildDocument(res.getContent().toString(), ProcessPSPSchemaPath);
			} finally {
				// don't forget to cleanup resources
				try {
					((EXistResource) res).freeResources();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}
		return null;
	}

    public Document setRedactor(Document process, String redactorId) {
		process.getElementsByTagName(RedactorFiled).
			item(0).setTextContent(redactorId);
		return process;
	}
	
	public String getProceesId(Document process)
	{
		Element root = process.getDocumentElement();
		return root.getAttribute(PROCESS_ID);
	}

	public void setProceesId(Document process, String id)
	{
		process.getElementsByTagName(PROCESS_ROOT).item(0).getAttributes().getNamedItem(PROCESS_ID).setTextContent(id);
	}

	public Document save(Document process) throws Exception {
		String id = idGeneratorService.getId("process");
		this.setProceesId(process, id);
		String processS = DOMParser.parseDocument(process,ProcessPSPSchemaPath);
		StoreToDB.store(ProcessPSPCollectionId, id, processS);
		return process;
	}

	public Document update(Document process) throws Exception
	{
		String processId = this.getProceesId(process);
		this.delete(processId);
		String processS = DOMParser.parseDocument(process, ProcessPSPSchemaPath);
		StoreToDB.store(ProcessPSPCollectionId, processId, processS);
		return process;
	}

	public void delete(String id) throws Exception {
		String xpathExp = "/ns:processPSP";
		long mods = UpdateDB.delete(ProcessPSPCollectionId, id, xpathExp);
		if (mods == 0) {
			throw new ResourceNotDeleted(String.format("processPSP with documentId %s", id));
		}
	}


	// SET METHODS
    public Document setScientificPublicationId(Document process, String scientificPublciationID) throws Exception
    {
		Node lastVersion = this.getLastVersion(process);
		NodeList list = lastVersion.getChildNodes();
		for(int i=0; i < list.getLength(); i++){
			Node n = list.item(i);
			if(n.getNodeName().equals(ScientificPublicationFiled)){
				n.setTextContent(scientificPublciationID);
			}
		}
        return process;
	}


	public Document setScientificPublicationName(Document process, String scientificPublciationName) throws Exception
    {
		Node lastVersion = this.getLastVersion(process);
		NodeList list = lastVersion.getChildNodes();
		for(int i=0; i < list.getLength(); i++){
			Node n = list.item(i);
			if(n.getNodeName().equals(ScientificPublicationNameFiled)){
				n.setTextContent(scientificPublciationName);
			}
		}
        return process;
	}




	
	public Document setCoverLetter(Document process, String coverLetterId) {
		process.getElementsByTagName(CoverLetterFiled).
				item(0).setTextContent(coverLetterId);
        return process;
	}

	// vrati deo dokumenta process tj vrati deo koji cini poslednja verzija
	public Node getLastVersion(Document process) {
		
		String lastVersionNumber = getLastVersionNumber(process);
		
		NodeList l = process.getElementsByTagName(PROCESS_ROOT).item(0).getChildNodes();
		for(int i = 0; i < l.getLength(); i++){
			Node n = l.item(i);
			if( n.getNodeName().equals("ns:versions")) {
				NodeList versions =  n.getChildNodes();
				if(versions.getLength() == 1)
				{
					return versions.item(0);
				}
				for(int j=0; j < versions.getLength();j++){
					Node version = versions.item(j);
					if( version.getNodeName().equals("ns:version")) {
						String versionNumber = version.getAttributes().getNamedItem("version").getTextContent();
						if(versionNumber.equals(lastVersionNumber)) {
							return version;
						}
					}
				}
			}
		}
		return null;
	}

	public ArrayList<String> findProcessByState(ProcessState state) throws Exception
	{
		ArrayList<String> retVal = new ArrayList<>();
		String xpathExp = "//processPSP[@state=\"" + state.getAction() + "\"]";
		ResourceSet resultSet = RetriveFromDB.executeXPathExpression(ProcessPSPCollectionId, xpathExp,
				TARGET_NAMESPACE);
		if (resultSet == null) {
			return retVal;
		}
		ResourceIterator i = resultSet.getIterator();
		XMLResource res = null;
		while (i.hasMoreResources()) {
			try {
				res = (XMLResource) i.nextResource();
				retVal.add(res.getContent().toString());
			} finally {
				// don't forget to cleanup resources
				try {
					((EXistResource) res).freeResources();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}
		return retVal;
	}

	public ArrayList<String> findForPublishing()  throws Exception {
		ArrayList<String> retVal = findProcessByState(ProcessState.FOR_REVIEW);
		findProcessByState(ProcessState.SCORED)
			.forEach(p -> { retVal.add(p);} );
		return retVal;
	}

	public ArrayList<String> findByOwnerEmail(String email) throws Exception{
		ArrayList<String> retVal = new ArrayList<>();
		String xpathExp = "//processPSP[@authorEmail=\"" + email + "\"]";
		ResourceSet resultSet = RetriveFromDB.executeXPathExpression(ProcessPSPCollectionId, xpathExp,
				TARGET_NAMESPACE);
		if (resultSet == null) {
			return retVal;
		}
		ResourceIterator i = resultSet.getIterator();
		XMLResource res = null;
		while (i.hasMoreResources()) {
			try {
				res = (XMLResource) i.nextResource();
				retVal.add(res.getContent().toString());
			} finally {
				// don't forget to cleanup resources
				try {
					((EXistResource) res).freeResources();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}
		return retVal;
	}
	

	public ArrayList<String> findMyReviewAssigments(String email) throws Exception{
		ArrayList<String> retVal = new ArrayList<>();
		String xQueryPath = "src/main/resources/data/xQuery/findMyReviewAssigments.txt";

		HashMap<String, String> params = new HashMap<>();
		params.put("REVIWER_ID", email);  // params for XQuery

		ResourceSet resultSet = RetriveFromDB.executeXQuery(
			ProcessPSPCollectionId, xQueryPath, params, TARGET_NAMESPACE);
		if (resultSet == null || (resultSet.getSize() == 0 )) {
			return retVal;
		}
		ResourceIterator i = resultSet.getIterator();
		XMLResource res = null;
		while (i.hasMoreResources()) {
			try {
				res = (XMLResource) i.nextResource();
				retVal.add(res.getContent().toString());
			} finally {
				// don't forget to cleanup resources
				try {
					if(res != null) {
						((EXistResource) res).freeResources();
					}
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}
		return retVal;
    }
	
	public void addReviewAssigment(Document document, TPersons persons) throws Exception {
		Node lastVersion = getLastVersion(document);
		Element lv = (Element) lastVersion;
		NodeList ra = lv.getElementsByTagName(ReviewAssignments);
		if (ra.getLength() > 0) {
			lv.removeChild(ra.item(0));
		}
		String[] emails = new String[persons.getPersons().size()];
		int i = 0;
		Element reviewAssignments = document.createElement(ReviewAssignments);
		for (TPerson p : persons.getPersons()) {
			Element reviewAssigment = document.createElement(ReviewAssigment);
			reviewAssigment.setAttribute(PROCESS_STATE, PENDING);

			Element review = document.createElement(Review);
			review.setAttribute(PROCESS_STATE, IN_PROGRESS);

			Element idReviewer = document.createElement(IdReviewer);
			String email = personService.findOneAuthByPersonId(p.getId()).getEmail();
			idReviewer.appendChild(document.createTextNode(email));

			review.appendChild(idReviewer);
			reviewAssigment.appendChild(review);
			reviewAssignments.appendChild(reviewAssigment);
			
			emails[i] = email;
			i += 1;
		}
		lv.appendChild(reviewAssignments);
		update(document);
		notificationService.questionnaireReviewers(emails, getScientificPublicationNameByNode(lv));
	}

	public String findMySPProcess(String id, String authId) throws Exception {
		String retVal = "";
		String xQueryPath = "src/main/resources/data/xQuery/showMySPProcess.txt";

		HashMap<String, String> params = new HashMap<>();
		params.put("AUTH_ID", authId); 
		params.put("PROCESS_ID", id);

		ResourceSet resultSet = RetriveFromDB.executeXQuery(
			ProcessPSPCollectionId, xQueryPath, params, TARGET_NAMESPACE);
		if (resultSet == null) {
			return retVal;
		}
		ResourceIterator i = resultSet.getIterator();
		XMLResource res = null;
		while (i.hasMoreResources()) {
			try {
				res = (XMLResource) i.nextResource();
				retVal = res.getContent().toString();
			} finally {
				// don't forget to cleanup resources
				try {
					((EXistResource) res).freeResources();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}
		return retVal;
	}

	public String getLastVersionNumber(Document process) {
		return process.getElementsByTagName(PROCESS_ROOT).item(0).getAttributes().getNamedItem(PROCESS_LAST_VERSION)
				.getTextContent();
	}

	public String addNewVersion(Document process, String scId, String scName) {
		String lastVersionNum = getLastVersionNumber(process);
		String newVersion = (Integer.parseInt(lastVersionNum) + 1) + "";
		process.getDocumentElement().getAttributes().getNamedItem(PROCESS_LAST_VERSION)
				.setTextContent(newVersion);

		Element versions = (Element) process.getDocumentElement().getElementsByTagName(VERSIONS).item(0);
		
		Element version = process.createElement(VERSION);
		version.setAttribute(VERSION_VERSION, newVersion);
		
		Element spId = process.createElement(ScientificPublicationFiled);
		spId.appendChild(process.createTextNode(scId));
		
		Element spName = process.createElement(ScientificPublicationNameFiled);
		spName.appendChild(process.createTextNode(scName));
		
		Element ra = process.createElement(ReviewAssignments);
		
		version.appendChild(spId);
		version.appendChild(spName);
		version.appendChild(ra);
		versions.appendChild(version);
		return newVersion;
	}


	public boolean updateProcessReviewAssigment
			(String processId, String version, String decision, String email) throws Exception {
		String xQueryPath = "src/main/resources/data/xQuery/updateReviewAssigment.txt";

		HashMap<String, String> params = new HashMap<>();
		params.put("PROCESS_ID", processId);
		params.put("SC_VERSION", version);
		params.put("DECISION", decision);
		params.put("REVIEWER_EMAIL", email);

		ResourceSet resultSet = RetriveFromDB.executeXQuery(
				ProcessPSPCollectionId, xQueryPath, params, TARGET_NAMESPACE);
		if (resultSet == null) {
			return false;
		}
		return  true;
	}

	public String saveQuestionnaireToProcessPSP
			(String processId, String reviewEmail, String qId, boolean willComment) throws  Exception {
		HashMap<String, String> params = new HashMap<>();
		params.put("PROCESS_ID", processId);
		params.put("Q_ID", qId);
		params.put("REVIEWER_EMAIL", reviewEmail);
		if(willComment) {
			params.put("PROCESS_STATE", "waitingComments");
		} else {
			params.put("PROCESS_STATE", "Done");
		}
		ResourceSet resultSet = RetriveFromDB.executeXQuery(
				ProcessPSPCollectionId, Questionnaire_Save_to_ProcessPSP, params, TARGET_NAMESPACE);
		if (resultSet == null) {
			return null;
		}
		// change Review state to DONE or waitingForComments
		resultSet = RetriveFromDB.executeXQuery(
				ProcessPSPCollectionId, Questionnaire_Save_to_State_ProcessPSP, params, TARGET_NAMESPACE);
		if (resultSet == null) {
			return null;
		}
		// change processPSPState to SCORED if all accepted reviewAssigments are Done..
		resultSet = RetriveFromDB.executeXQuery(
				ProcessPSPCollectionId, Change_toScored_State_ProcessPSP, params, TARGET_NAMESPACE);
		if (resultSet == null) {
			return null;
		}
		resultSet = RetriveFromDB.executeXQuery(
				ProcessPSPCollectionId, getSCName_forVersion_ProcessPSP, params, TARGET_NAMESPACE);
		if (resultSet == null) {
			return null;
		}
		ResourceIterator i = resultSet.getIterator();
		if(i.hasMoreResources()) {
			return  i.nextResource().getContent().toString();
		}
		return null;
	}

	public void saveCommentsToProcessPSP(String processId, String reviewEmail, String spId) throws Exception{
		HashMap<String, String> params = new HashMap<>();
		params.put("PROCESS_ID", processId);
		params.put("SP_ID", spId);
		params.put("REVIEWER_EMAIL", reviewEmail);
		params.put("PROCESS_STATE", "Done");

		RetriveFromDB.executeXQuery(
				ProcessPSPCollectionId, Comments_Save_to_ProcessPSP, params, TARGET_NAMESPACE);
		// change Review state to DONE
		RetriveFromDB.executeXQuery(
				ProcessPSPCollectionId, Questionnaire_Save_to_State_ProcessPSP, params, TARGET_NAMESPACE);
		// change processPSPState to SCORED if all accepted reviewAssigments are Done..
		RetriveFromDB.executeXQuery(
				ProcessPSPCollectionId, Change_toScored_State_ProcessPSP, params, TARGET_NAMESPACE);
	}
	
	public String getScientificPublicationNameByNode(Element lastVersion) {
		return lastVersion.getElementsByTagName(ScientificPublicationNameFiled).item(0).getTextContent();
	}
}