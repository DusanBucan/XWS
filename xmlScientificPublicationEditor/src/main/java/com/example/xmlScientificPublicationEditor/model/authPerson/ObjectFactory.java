//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.01.27 at 07:19:29 PM CET 
//

package com.example.xmlScientificPublicationEditor.model.authPerson; 

import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the rs.ac.uns.tim1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Auth_QNAME = new QName("http://www.uns.ac.rs/Tim1", "auth");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: rs.ac.uns.tim1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TAuthPerson }
     * 
     */
    public TAuthPerson createTAuthPerson() {
        return new TAuthPerson();
    }

    /**
     * Create an instance of {@link TAuthPerson.Roles }
     * 
     */
    public TAuthPerson.Roles createTAuthPersonRoles() {
        return new TAuthPerson.Roles();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TAuthPerson }{@code >}}
     * 
     */
    // @XmlElementDecl(namespace = "http://www.uns.ac.rs/Tim1", name = "auth")
    // public JAXBElement<TAuthPerson> createAuth(TAuthPerson value) {
    //     return new JAXBElement<TAuthPerson>(_Auth_QNAME, TAuthPerson.class, null, value);
    // }

}
