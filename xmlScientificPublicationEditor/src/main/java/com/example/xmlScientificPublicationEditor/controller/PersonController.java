package com.example.xmlScientificPublicationEditor.controller;

import com.example.xmlScientificPublicationEditor.model.authPerson.TAuthPerson;
import com.example.xmlScientificPublicationEditor.model.person.TPerson;
import com.example.xmlScientificPublicationEditor.security.TokenUtils;
import com.example.xmlScientificPublicationEditor.service.PersonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/api")
public class PersonController extends BaseController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private PersonService personService;

	@GetMapping(value = "/person/getPersonTemplate", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> getPersonTemplate() throws Exception {
		String person = personService.generatePersonXMLTemplate();
        return new ResponseEntity<>(person, HttpStatus.OK);
	}

	@GetMapping(value = "/person/getAuthTemplate", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> getAuthTemplate() throws Exception {
		String auth = personService.generateAuthXMLTemplate();
        return new ResponseEntity<>(auth, HttpStatus.OK);
	}

	@GetMapping(value = "/person/{id}", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<TPerson> getPersonById(@PathVariable("id") String email ) throws Exception {
		TPerson person = personService.findOne(email);
        return new ResponseEntity<>(person, HttpStatus.OK);
	}

	@PostMapping(value="/login", 
				consumes = MediaType.APPLICATION_XML_VALUE,
				produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String>login(@RequestBody TAuthPerson person) throws Exception {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(person.getEmail(), person.getPassword());
		authenticationManager.authenticate(token);
		UserDetails userDetails = userDetailsService.loadUserByUsername(person.getEmail());
		return new ResponseEntity<>(tokenUtils.generateToken(userDetails), HttpStatus.OK);
	}

	@PostMapping(value="/registration", 
				consumes = MediaType.APPLICATION_XML_VALUE,
				produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String>registration(@RequestBody TAuthPerson person) throws Exception {
		personService.registration(person);
		return new ResponseEntity<>("You are registred, now you need to verify your email", HttpStatus.OK);
	}

	@PutMapping(value="/person", consumes = MediaType.APPLICATION_XML_VALUE,produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<TPerson> updatePerson(@RequestBody TPerson person) throws Exception {
		TPerson p = personService.update(person);
		return new ResponseEntity<>(p, HttpStatus.OK);
	}

	@DeleteMapping(value="/person/{email}", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> delete(@PathVariable("email")String email) throws Exception{
		personService.delete(email);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

}



