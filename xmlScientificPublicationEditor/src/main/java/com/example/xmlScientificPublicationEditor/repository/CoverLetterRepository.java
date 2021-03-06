package com.example.xmlScientificPublicationEditor.repository;

import static com.example.xmlScientificPublicationEditor.util.template.XUpdateTemplate.TARGET_NAMESPACE;

import java.io.StringWriter;
import java.util.HashMap;

import org.exist.xmldb.EXistResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import com.example.xmlScientificPublicationEditor.exception.ResourceNotDeleted;
import com.example.xmlScientificPublicationEditor.exception.ResourceNotFoundException;
import com.example.xmlScientificPublicationEditor.service.IdGeneratorService;
import com.example.xmlScientificPublicationEditor.service.PersonService;
import com.example.xmlScientificPublicationEditor.serviceImpl.IdGeneratorServiceImpl;
import com.example.xmlScientificPublicationEditor.util.MyFile;
import com.example.xmlScientificPublicationEditor.util.DOMParser.DOMParser;
import com.example.xmlScientificPublicationEditor.util.RDF.MetadataExtractor;
import com.example.xmlScientificPublicationEditor.util.RDF.StoreToRDF;
import com.example.xmlScientificPublicationEditor.util.RDF.UpdateRDF;
import com.example.xmlScientificPublicationEditor.util.XSLFOTransformer.XSLFOTransformer;
import com.example.xmlScientificPublicationEditor.util.existAPI.RetriveFromDB;
import com.example.xmlScientificPublicationEditor.util.existAPI.StoreToDB;
import com.example.xmlScientificPublicationEditor.util.existAPI.UpdateDB;

@Repository
public class CoverLetterRepository {

	@Autowired
	private IdGeneratorService idGeneratorService;
	
	@Autowired
	private MetadataExtractor metadataExtractor;
	
	@Autowired
	private XSLFOTransformer xslFoTransformer;
	
	@Autowired
	private PersonService personService;

    public static final String ScientificPublicationID = "href";

	public static String coverLetterCollectionId = "/db/sample/coverLetter";
    public static String coverLetterSchemaPath = "src/main/resources/data/schemas/coverLetter.xsd";
    public static String CoverLetterXSLPath = "src/main/resources/data/xslt/coverLetter.xsl";
    public static String CoverLetterXSL_FO_PATH = "src/main/resources/data/xsl-fo/coverLetter_fo.xsl";
	public static String CoverLetterRDFPath = "src/main/resources/data/xmlToRDFa/coverLettertToRDFa.xsl";
    public static String CV_NAMED_GRAPH_URI_PREFIX = "/example/coverLetter/";

	public String findOne(String id) throws Exception {
		String retVal = null;
		String xpathExp = "//ns1:coverLetter[@id=\"" + id + "\"]";
		ResourceSet resultSet = RetriveFromDB.executeXPathExpression(coverLetterCollectionId, xpathExp,
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

	public String save(String coverLetter, String processId) throws Exception {
		Document document = DOMParser.buildDocument(coverLetter, coverLetterSchemaPath);
		String id = "cl" + idGeneratorService.getId("coverLetter");

		generateIDs(document, id);
		
		document.getDocumentElement().getAttributes().getNamedItem("id").setTextContent(id);
		document.getDocumentElement().setAttribute("processId", processId);
		
		String toSave = DOMParser.parseDocument(document, CoverLetterRepository.coverLetterSchemaPath);
		StoreToDB.store(coverLetterCollectionId, id, toSave);
		

		toSave = xslFoTransformer.generateHTML(toSave, CoverLetterRepository.CoverLetterRDFPath);
		saveMetadata(metadataExtractor.extractMetadataXML(toSave), id);
		
		return id;
	}

	private void generateIDs(Document document, String id) throws Exception {
		Node author = document.getElementsByTagName(IdGeneratorServiceImpl.AUTHOR).item(0);
		idGeneratorService.generateElementId(author, id + "_author", IdGeneratorServiceImpl.AUTHOR);
		
		Node cp = document.getElementsByTagName(IdGeneratorServiceImpl.CP).item(0);
		idGeneratorService.generateElementId(cp, id + "_cp", IdGeneratorServiceImpl.CP);

		NodeList institutions = document.getElementsByTagName(IdGeneratorServiceImpl.INSTITUTION);
		for (int i = 0; i < institutions.getLength(); ++i) {
			idGeneratorService.generateElementId(institutions.item(i), id + "_institution" + (i + 1),
					IdGeneratorServiceImpl.INSTITUTION);
		}

		NodeList address = document.getElementsByTagName(IdGeneratorServiceImpl.ADDRESS);
		for (int i = 0; i < address.getLength(); ++i) {
			idGeneratorService.generateElementId(address.item(i), id + "_address" + (i + 1),
					IdGeneratorServiceImpl.ADDRESS);
		}

		NodeList content = document.getElementsByTagName(IdGeneratorServiceImpl.CONTENT);
		for (int i = 0; i < content.getLength(); ++i) {
			idGeneratorService.generateParagraphId(content.item(i), id + "_content" + (i + 1));
		}
	}

	// metadata
	public String update(String coverLetter) throws Exception {
		Document document = DOMParser.buildDocument(coverLetter, coverLetterSchemaPath);
		String id = document.getDocumentElement().getAttribute("id");

		String oldCoverLetterData = this.findOne(id);
		if (oldCoverLetterData == null) {
			throw new ResourceNotFoundException("Cover letter with id: " + id);
		}
		this.delete(id);
		StoreToDB.store(coverLetterCollectionId, id, coverLetter);

		String toSave = xslFoTransformer.generateHTML(coverLetter, CoverLetterRepository.CoverLetterRDFPath);
		updateMetadata(metadataExtractor.extractMetadataXML(toSave), id);
		return id;
	}

	public void delete(String id) throws Exception {
		String xpathExp = "/ns1:coverLetter";
		long mods = UpdateDB.delete(coverLetterCollectionId, id, xpathExp);
		if (mods == 0) {
			throw new ResourceNotDeleted(String.format("Cover letter with documentId %s", id));
		}
		deleteMetadata(id);
	}

	public void saveMetadata(StringWriter metadata, String cvId) throws Exception {
		StoreToRDF.store(metadata, CV_NAMED_GRAPH_URI_PREFIX + cvId);
	}

	public void deleteMetadata(String cvId) throws Exception {
		UpdateRDF.delete(CV_NAMED_GRAPH_URI_PREFIX + cvId);
	}

	public void updateMetadata(StringWriter metadata, String cvId) throws Exception {
		String url = CV_NAMED_GRAPH_URI_PREFIX + cvId;
		deleteMetadata(cvId);
		StoreToRDF.store(metadata, url);
	}

	public String findOneByProcess(String id, String email) throws Exception {
		String xQueryPath = "src/main/resources/data/xQuery/findOneCLByProcessId.txt";
		
		HashMap<String, String> params = new HashMap<>();
		params.put("ID", id);
		params.put("AUTH", email);
		params.put("REDACTOR", personService.findOneAuth(email).getId());

		ResourceSet resultSet = RetriveFromDB.executeXQuery(
			coverLetterCollectionId, xQueryPath, params, TARGET_NAMESPACE);
		
		return MyFile.resourceSetToString(resultSet);
	}

}
