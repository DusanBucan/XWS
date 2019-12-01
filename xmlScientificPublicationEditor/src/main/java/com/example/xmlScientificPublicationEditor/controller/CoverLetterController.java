package com.example.xmlScientificPublicationEditor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xmldb.api.modules.XMLResource;

import com.example.xmlScientificPublicationEditor.exception.ResourceNotFoundException;
import com.example.xmlScientificPublicationEditor.service.CoverLetterService;

@RestController
@RequestMapping("/api")
public class CoverLetterController extends BaseController {

	@Autowired
	private CoverLetterService coverLetterService;
	
	@GetMapping(value="/coverLetter/{id}", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<XMLResource> getCoverLetterById(@PathVariable("id") String id) throws ResourceNotFoundException{
		XMLResource coverLetter = coverLetterService.findOne(id);
		return new ResponseEntity<>(coverLetter, HttpStatus.OK);
	}
	
	@PostMapping(value="/coverLetter", 
			consumes = MediaType.APPLICATION_XML_VALUE,
			produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> addCoverLetter(@RequestBody String cl) throws Exception {
		coverLetterService.save(cl);
		return new ResponseEntity<>("You succesfully add cover letter", HttpStatus.OK);
	}
	
	@PutMapping(value="/coverLetter", consumes = MediaType.APPLICATION_XML_VALUE,produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<XMLResource> updatePerson(@RequestBody XMLResource coverLetter) throws Exception {
		XMLResource cl = coverLetterService.update(coverLetter);
		return new ResponseEntity<>(cl, HttpStatus.OK);
	}
	
	@DeleteMapping(value="/coverLetter/{id}", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> delete(@PathVariable("id")String id) throws Exception{
		coverLetterService.delete(id);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

}
