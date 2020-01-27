import { Component, OnInit } from '@angular/core';
import { PublicationService } from 'src/app/services/publication-service/publication.service';
declare const Xonomy: any;

@Component({
  selector: 'app-add-publication',
  templateUrl: './add-publication.component.html',
  styleUrls: ['./add-publication.component.scss']
})
export class AddPublicationComponent implements OnInit {

  publicationXml = '';

  constructor(private publicationService: PublicationService) { }

  ngOnInit() {
    const docSpec = {
      onchange: function () {
        console.log('I been changed now!')
      },
      validate: function (obj) {
        console.log('I be validatin\' now!')
      },
      elements: {
        'ns:scientificPublication': {
          menu: [
            {
              caption: 'Append an <ns:chapter>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:chapter id="chapter" title="some title" xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:paragraph id="paragraph"/></ns:chapter>'
            },
            {
              caption: 'Append an <ns:references>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:references xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:reference id="reference"/></ns:references>',
              hideIf: function (jsElement) {
                return jsElement.hasChildElement('references');
              }
            },
            {
              caption: 'Append an <ns:comments>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:comments xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:comment id="comment" ref=""><ns:content id="content"/></ns:comment></ns:comments>',
              hideIf: function (jsElement) {
                return jsElement.hasChildElement('comments');
              }
            }],
          attributes: {
            'tableOfContent': {
              asker: Xonomy.askPicklist,
              askerParameter: [
                'true', 'false'
              ]
            }
          }
        },
        'ns:caption': {
          mustBeBefore: ['ns:authors', 'ns:abstract', 'ns:chapter', 'ns:references', 'ns:comments'],
          canDropTo: ['ns:scientificPublication'],
        },
        'ns:value': {
          oneliner: true,
          canDropTo: ['ns:caption'],
          menu: [
            {
              caption: 'Edit',
              action: Xonomy.editRaw,
              actionParameter: {
                fromJs: function (jsElement) {
                  return jsElement.getText();
                },
                toJs: function (txt, origElement) {
                  origElement.addText(txt);
                  return origElement;
                }
              }
            }
          ]
        },
        'ns:authors': {
          mustBeAfter: ['ns:caption'],
          mustBeBefore: ['ns:abstract', 'ns:chapter', 'ns:references', 'ns:comments'],
          canDropTo: ['ns:scientificPublication'],
          menu: [
            {
              caption: 'Append an <ns:author>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:author id="author" xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:name/><ns:surname/><ns:email/><ns:phone/><ns:institution id="institution"><ns:name/><ns:address><ns:city/><ns:street/><ns:country/></ns:address></ns:institution></ns:author>'
            }]
        },
        'ns:abstract': {
          mustBeAfter: ['ns:caption', 'ns:authors'],
          mustBeBefore: ['ns:chapter', 'ns:references', 'ns:comments'],
          canDropTo: ['ns:scientificPublication'],
          menu: [{
            caption: 'Append an <ns:paragraph>',
            action: Xonomy.newElementChild,
            actionParameter: '<ns:paragraph id="paragraph" xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
          },
          ]
        },
        'ns:chapter': {
          mustBeAfter: ['ns:caption', 'ns:authors', 'ns:abstract'],
          mustBeBefore: ['ns:references', 'ns:comments'],
          canDropTo: ['ns:scientificPublication'],
          menu: [{
            caption: 'Append an <ns:paragraph>',
            action: Xonomy.newElementChild,
            actionParameter: '<ns:paragraph id="paragraph" xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
          }, {
            caption: 'Append an <ns:subchapter>',
            action: Xonomy.newElementChild,
            actionParameter: '<ns:subchapter id="subchapter" title="some title" xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:paragraph id="paragraph"/></ns:subchapter>'
          }],
          attributes: {
            'title': {
              asker: Xonomy.askString,
            }
          }
        },
        'ns:references': {
          mustBeAfter: ['ns:caption', 'ns:authors', 'ns:abstract', 'ns:chapter'],
          mustBeBefore: ['ns:comments'],
          canDropTo: ['ns:scientificPublication'],
          menu: [
            {
              caption: 'Append an <ns:reference>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:reference id="reference" xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
            },
          ]
        },
        'ns:comments': {
          mustBeAfter: ['ns:caption', 'ns:authors', 'ns:abstract', 'ns:chapter', 'ns:references'],
          canDropTo: ['ns:scientificPublication'],
          menu: [{
            caption: 'Append an <ns:comment>',
            action: Xonomy.newElementChild,
            actionParameter: '<ns:comment id="comment" ref="" xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:content id="content"/></ns:comment>'
          },]
        },
        'ns:author': {
          canDropTo: ['ns:authors'],
          menu: [
            {
              caption: 'Delete this author',
              action: Xonomy.deleteElement,
              hideIf: function (jsElement) {
                return jsElement.parent().getChildElements('author').length < 2;
              }
            }]
        },
        'ns:name': {
          oneliner: true,
          mustBeBefore: ['ns:surname', 'ns:email', 'ns:phone', 'ns:institution', 'ns:address'],
          canDropTo: ['ns:author', 'ns:institution'],
          menu: [
            {
              caption: 'Edit',
              action: Xonomy.editRaw,
              actionParameter: {
                fromJs: function (jsElement) {
                  return jsElement.getText();
                },
                toJs: function (txt, origElement) {
                  origElement.addText(txt);
                  return origElement;
                }
              }
            }
          ]
        },
        'ns:surname': {
          oneliner: true,
          mustBeAfter: ['ns:name'],
          mustBeBefore: ['ns:email', 'ns:phone', 'ns:institution'],
          canDropTo: ['ns:author'],
          menu: [
            {
              caption: 'Edit',
              action: Xonomy.editRaw,
              actionParameter: {
                fromJs: function (jsElement) {
                  return jsElement.getText();
                },
                toJs: function (txt, origElement) {
                  origElement.addText(txt);
                  return origElement;
                }
              }
            }
          ]
        },
        'ns:email': {
          oneliner: true,
          mustBeAfter: ['ns:name', 'ns:surname'],
          mustBeBefore: ['ns:phone', 'ns:institution'],
          canDropTo: ['ns:author'], menu: [
            {
              caption: 'Edit',
              action: Xonomy.editRaw,
              actionParameter: {
                fromJs: function (jsElement) {
                  return jsElement.getText();
                },
                toJs: function (txt, origElement) {
                  origElement.addText(txt);
                  return origElement;
                }
              }
            }
          ]
        },
        'ns:phone': {
          oneliner: true,
          mustBeAfter: ['ns:name', 'ns:surname', 'ns:email'],
          mustBeBefore: ['ns:institution'],
          canDropTo: ['ns:author'],
          menu: [
            {
              caption: 'Edit',
              action: Xonomy.editRaw,
              actionParameter: {
                fromJs: function (jsElement) {
                  return jsElement.getText();
                },
                toJs: function (txt, origElement) {
                  origElement.addText(txt);
                  return origElement;
                }
              }
            }
          ]
        },
        'ns:institution': {
          mustBeAfter: ['ns:name', 'ns:surname', 'ns:email', 'ns:phone'],
          canDropTo: ['ns:author'],
        },
        'ns:address': {
          mustBeAfter: ['ns:name'],
          canDropTo: ['ns:institution'],
          menu: [
            {
              caption: 'Append an <ns:streetNumber>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:streetNumber xmlns:ns="http://www.uns.ac.rs/Tim1"> </ns:streetNumber>',
              hideIf: function (jsElement) {
                return jsElement.hasChildElement('streetNumber');
              }
            }, {
              caption: 'Append an <ns:floorNumber>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:floorNumber xmlns:ns="http://www.uns.ac.rs/Tim1"> </ns:floorNumber>',
              hideIf: function (jsElement) {
                return jsElement.hasChildElement('floorNumber');
              }
            }
          ]
        },
        'ns:city': {
          oneliner: true,
          mustBeBefore: ['ns:streetNumber', 'ns:floorNumber', 'ns:street', 'ns:country'],
          canDropTo: ['ns:address'],
          menu: [
            {
              caption: 'Edit',
              action: Xonomy.editRaw,
              actionParameter: {
                fromJs: function (jsElement) {
                  return jsElement.getText();
                },
                toJs: function (txt, origElement) {
                  origElement.addText(txt);
                  return origElement;
                }
              }
            }
          ]
        },
        'ns:streetNumber': {
          oneliner: true,
          mustBeAfter: ['ns:city'],
          mustBeBefore: ['ns:floorNumber', 'ns:street', 'ns:country'],
          canDropTo: ['ns:address'],
          menu: [
            {
              caption: 'Edit',
              action: Xonomy.editRaw,
              actionParameter: {
                fromJs: function (jsElement) {
                  return jsElement.getText();
                },
                toJs: function (txt, origElement) {
                  origElement.addText(txt);
                  return origElement;
                }
              }
            },
            {
              caption: 'Delete this streetNumber',
              action: Xonomy.deleteElement
            }
          ],
        },
        'ns:floorNumber': {
          oneliner: true,
          mustBeAfter: ['ns:city', 'ns:streetNumber'],
          mustBeBefore: ['ns:street', 'ns:country'],
          canDropTo: ['ns:address'],
          menu: [{
            caption: 'Delete this floorNumber',
            action: Xonomy.deleteElement
          },
          {
            caption: 'Edit',
            action: Xonomy.editRaw,
            actionParameter: {
              fromJs: function (jsElement) {
                return jsElement.getText();
              },
              toJs: function (txt, origElement) {
                origElement.addText(txt);
                return origElement;
              }
            }
          }
          ]
        },
        'ns:street': {
          oneliner: true,
          mustBeAfter: ['ns:city', 'ns:streetNumber', 'ns:floorNumber'],
          mustBeBefore: ['ns:country'],
          canDropTo: ['ns:address'], menu: [
            {
              caption: 'Edit',
              action: Xonomy.editRaw,
              actionParameter: {
                fromJs: function (jsElement) {
                  return jsElement.getText();
                },
                toJs: function (txt, origElement) {
                  origElement.addText(txt);
                  return origElement;
                }
              }
            }
          ]
        },
        'ns:country': {
          oneliner: true,
          mustBeAfter: ['ns:city', 'ns:streetNumber', 'ns:floorNumber', 'ns:street'],
          canDropTo: ['ns:address'],
          menu: [
            {
              caption: 'Edit',
              action: Xonomy.editRaw,
              actionParameter: {
                fromJs: function (jsElement) {
                  return jsElement.getText();
                },
                toJs: function (txt, origElement) {
                  origElement.addText(txt);
                  return origElement;
                }
              }
            }
          ]
        },
        'ns:keywords': {
          mustBeBefore: ['ns:paragraph'],
          canDropTo: ['ns:abstract'],
          menu: [
            {
              caption: 'Append an <ns:keyword>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:keyword xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
            }
          ]
        },
        'ns:keyword': {
          canDropTo: ['ns:keywords'],
          menu: [{
            caption: 'Edit',
            action: Xonomy.editRaw,
            actionParameter: {
              fromJs: function (jsElement) {
                return jsElement.getText();
              },
              toJs: function (txt, origElement) {
                origElement.addText(txt);
                return origElement;
              }
            }
          },
          {
            caption: 'Delete this keyword',
            action: Xonomy.deleteElement,
            hideIf: function (jsElement) {
              return jsElement.parent().getChildElements('keyword').length < 2;
            }
          }]
        },
        'ns:subchapter': {
          mustBeAfter: ['ns:paragraph'],
          canDropTo: ['ns:chapter', 'ns:subchapter'],
          menu: [
            {
              caption: 'Append an <ns:paragraph>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:paragraph id="paragraph" xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
            }, {
              caption: 'Append an <ns:subchapter>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:subchapter id="subchapter" title="some title" xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:paragraph id="paragraph"/></ns:subchapter>'
            }
          ],
          attributes: {
            'title': {
              asker: Xonomy.askString,
            }
          }
        },
        'ns:paragraph': {
          mustBeAfter: ['ns:keywords'],
          mustBeBefore: ['ns:subchapter'],
          canDropTo: ['ns:abstract'],
          menu: [
            {
              caption: 'Append an <ns:text>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:text id="text" xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:cursive/></ns:text>'
            },
            {
              caption: 'Append an <ns:quote>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:quote id="quote" ref="" xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:cursive/></ns:quote>'
            },
            {
              caption: 'Append an <ns:formula>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:formula xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
            },
            {
              caption: 'Append an <ns:list>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:list id="list" type="ordered" xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:listitem><ns:cursive/></ns:listitem></ns:list>'
            },
            {
              caption: 'Append an <ns:image>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:image id="image" xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:description/><ns:source/></ns:image>'
            }, {
              caption: 'Append an <ns:table>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:table id="table" border="true" xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:table_row><ns:table_cell/></ns:table_row></ns:table>'
            },
            {
              caption: 'Delete this paragraph',
              action: Xonomy.deleteElement,
              hideIf: function (jsElement) {
                return jsElement.parent().getChildElements('paragraph').length < 2;
              }
            }
          ]
        },
        'ns:text': {
          canDropTo: ['ns:paragraph', 'ns:content'],
          menu: [
            {
              caption: 'Delete this text',
              action: Xonomy.deleteElement
            }
          ]
        },
        'ns:cursive': {
          menu: [
            {
              caption: 'Append an <ns:bold>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:bold xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
            },
            {
              caption: 'Append an <ns:italic>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:italic xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
            },
            {
              caption: 'Append an <ns:underline>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:underline xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
            },
            {
              caption: 'Edit',
              action: Xonomy.editRaw,
              actionParameter: {
                fromJs: function (jsElement) {
                  return jsElement.getText();
                },
                toJs: function (txt, origElement) {
                  origElement.addText(txt);
                  return origElement;
                }
              }
            }
          ]
        },
        'ns:bold': {
          canDropTo: ['ns:cursive', 'ns:italic', 'ns:underline'],
          menu: [
            {
              caption: 'Append an <ns:italic>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:italic xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
            },
            {
              caption: 'Append an <ns:underline>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:underline xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
            },
            {
              caption: 'Delete this bold',
              action: Xonomy.deleteElement
            },
            {
              caption: 'Edit',
              action: Xonomy.editRaw,
              actionParameter: {
                fromJs: function (jsElement) {
                  return jsElement.getText();
                },
                toJs: function (txt, origElement) {
                  origElement.addText(txt);
                  return origElement;
                }
              }
            }
          ]
        },
        'ns:italic': {
          canDropTo: ['ns:cursive', 'ns:bold', 'ns:underline'],
          menu: [
            {
              caption: 'Append an <ns:bold>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:bold xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
            },
            {
              caption: 'Append an <ns:underline>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:underline xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
            },
            {
              caption: 'Delete this italic',
              action: Xonomy.deleteElement
            },
            {
              caption: 'Edit',
              action: Xonomy.editRaw,
              actionParameter: {
                fromJs: function (jsElement) {
                  return jsElement.getText();
                },
                toJs: function (txt, origElement) {
                  origElement.addText(txt);
                  return origElement;
                }
              }
            }
          ]

        },
        'ns:underline': {
          canDropTo: ['ns:cursive', 'ns:italic', 'ns:bold'],
          menu: [
            {
              caption: 'Append an <ns:bold>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:bold xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
            },
            {
              caption: 'Append an <ns:italic>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:italic xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
            },
            {
              caption: 'Delete this underline',
              action: Xonomy.deleteElement
            }, {
              caption: 'Edit',
              action: Xonomy.editRaw,
              actionParameter: {
                fromJs: function (jsElement) {
                  return jsElement.getText();
                },
                toJs: function (txt, origElement) {
                  origElement.addText(txt);
                  return origElement;
                }
              }
            }
          ]
        },
        'ns:quote': {
          canDropTo: ['ns:paragraph', 'ns:content'],
          menu: [
            {
              caption: 'Delete this quote',
              action: Xonomy.deleteElement
            }
          ],
          attributes: {
            'ref': {
              asker: Xonomy.askString,
            }
          }
        },
        'ns:formula': {
          canDropTo: ['ns:paragraph', 'ns:content'],
        },
        'ns:list': {
          canDropTo: ['ns:paragraph', 'ns:content'],
          menu: [
            {
              caption: 'Append an <ns:listitem>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:listitem xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:cursive/></ns:listitem>'
            },
            {
              caption: 'Delete this list',
              action: Xonomy.deleteElement
            }
          ],
          attributes: {
            'type': {
              asker: Xonomy.askPicklist,
              askerParameter: [
                'ordered', 'unordered'
              ]
            }
          }
        },
        'ns:listitem': {
          canDropTo: ['ns:list'],
          menu: [
            {
              caption: 'Delete this listitem',
              action: Xonomy.deleteElement,
              hideIf: function (jsElement) {
                return jsElement.parent().getChildElements('listitem').length < 2;
              }
            }
          ]
        },
        'ns:image': {
          canDropTo: ['ns:paragraph'],
          menu: [
            {
              caption: 'Delete this image',
              action: Xonomy.deleteElement,
            },
            {
              caption: 'Add @height="100"',
              action: Xonomy.newAttribute,
              actionParameter: { name: 'height', value: '100' },
              hideIf: function (jsElement) {
                return jsElement.hasAttribute('height');
              }
            }, {
              caption: 'Add @width="100"',
              action: Xonomy.newAttribute,
              actionParameter: { name: 'width', value: '100' },
              hideIf: function (jsElement) {
                return jsElement.hasAttribute('width');
              }
            },
          ],
          attributes: {
            'height': {
              asker: Xonomy.askString,
            },
            'width': {
              asker: Xonomy.askString,
            }
          }
        },
        'ns:description': {
          oneliner: true,
          menu: [
            {
              caption: 'Edit',
              action: Xonomy.editRaw,
              actionParameter: {
                fromJs: function (jsElement) {
                  return jsElement.getText();
                },
                toJs: function (txt, origElement) {
                  origElement.addText(txt);
                  return origElement;
                }
              }
            }
          ]
        },
        'ns:source': {
          oneliner: true,
          menu: [
            {
              caption: 'Edit',
              action: Xonomy.editRaw,
              actionParameter: {
                fromJs: function (jsElement) {
                  return jsElement.getText();
                },
                toJs: function (txt, origElement) {
                  origElement.addText(txt);
                  return origElement;
                }
              }
            }
          ]
        },
        'ns:table': {
          canDropTo: ['ns:paragraph'],
          menu: [
            {
              caption: 'Append an <ns:table_row>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:table_row xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:table_cell/></ns:table_row>'
            },
            {
              caption: 'Delete this table',
              action: Xonomy.deleteElement
            }
          ],
          attributes: {
            'border': {
              asker: Xonomy.askPicklist,
              askerParameter: [
                'true', 'false'
              ]
            }
          }
        },
        'ns:table_row': {
          canDropTo: ['ns:table'],
          localDropOnly: function (jsElement) {
            return jsElement.parent().getChildElements('table_row').length < 2;
          },
          menu: [
            {
              caption: 'Append an <ns:table_cell>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:table_cell xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
            },
            {
              caption: 'Delete this table_row',
              action: Xonomy.deleteElement,
              hideIf: function (jsElement) {
                return jsElement.parent().getChildElements('table_row').length < 2;
              }
            }
          ]
        },
        'ns:table_cell': {
          canDropTo: ['ns:table_row'],
          localDropOnly: function (jsElement) {
            return jsElement.parent().getChildElements('table_cell').length < 2;
          },
          menu: [
            {
              caption: 'Append an <ns:cursive>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:cursive xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
            },
            {
              caption: 'Append an <ns:image>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:image id="image" xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:description/><ns:source/></ns:image>'
            },
            {
              caption: 'Delete this table_cell',
              action: Xonomy.deleteElement,
              hideIf: function (jsElement) {
                return jsElement.parent().getChildElements('table_cell').length < 2;
              }
            }
          ]
        },
        'ns:reference': {
          canDropTo: ['ns:references'],
          menu: [
            {
              caption: 'Append an <ns:book>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:book xmlns:ns="http://www.uns.ac.rs/Tim1"/>',
              hideIf: function (jsElement) {
                return jsElement.hasElements();
              }
            },
            {
              caption: 'Append an <ns:link>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:link xmlns:ns="http://www.uns.ac.rs/Tim1"/>',
              hideIf: function (jsElement) {
                return jsElement.hasElements();
              }
            },
            {
              caption: 'Delete this reference',
              action: Xonomy.deleteElement,
              hideIf: function (jsElement) {
                return jsElement.parent().getChildElements('reference').length < 2;
              }
            }
          ]
        },
        'ns:book': {
          oneliner: true,
          menu: [
            {
              caption: 'Edit',
              action: Xonomy.editRaw,
              actionParameter: {
                fromJs: function (jsElement) {
                  return jsElement.getText();
                },
                toJs: function (txt, origElement) {
                  origElement.addText(txt);
                  return origElement;
                }
              }
            }, {
              caption: 'Delete this book',
              action: Xonomy.deleteElement
            }
          ]

        },
        'ns:link': {
          oneliner: true,
          menu: [
            {
              caption: 'Edit',
              action: Xonomy.editRaw,
              actionParameter: {
                fromJs: function (jsElement) {
                  return jsElement.getText();
                },
                toJs: function (txt, origElement) {
                  origElement.addText(txt);
                  return origElement;
                }
              }
            }, {
              caption: 'Delete this link',
              action: Xonomy.deleteElement
            }
          ]
        },
        'ns:comment': {
          canDropTo: ['ns:comments'],
          menu: [
            {
              caption: 'Delete this comment',
              action: Xonomy.deleteElement,
              hideIf: function (jsElement) {
                return jsElement.parent().getChildElements('comment').length < 2;
              }
            }],
          attributes: {
            'ref': {
              asker: Xonomy.askString,
            }
          }
        },
        'ns:content': {
          canDropTo: ['ns:comment'],
          menu: [
            {
              caption: 'Append an <ns:text>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:text id="text" xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:cursive/></ns:text>'
            },
            {
              caption: 'Append an <ns:quote>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:quote id="quote" ref="" xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:cursive/></ns:quote>'
            },
            {
              caption: 'Append an <ns:formula>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:formula xmlns:ns="http://www.uns.ac.rs/Tim1"/>'
            },
            {
              caption: 'Append an <ns:list>',
              action: Xonomy.newElementChild,
              actionParameter: '<ns:list id="list" type="ordered" xmlns:ns="http://www.uns.ac.rs/Tim1"><ns:listitem><ns:cursive/></ns:listitem></ns:list>'
            },
            {
              caption: 'Delete this content',
              action: Xonomy.deleteElement,
              hideIf: function (jsElement) {
                return jsElement.parent().getChildElements('content').length < 2;
              }
            }]
        }
      }
    };
    this.publicationService.getPublicationTeplate().subscribe(
      (data: string) => {
        this.publicationXml = data;
        const editor = document.getElementById('editor');
        // tslint:disable-next-line: no-unused-expression
        new Xonomy.render(this.publicationXml, editor, docSpec);
      }, () => { },
      () => {
      }
    );
  }

  addPublication() {
    this.publicationXml = Xonomy.harvest() as string;
    console.log(this.publicationXml);
    this.publicationService.addPublication(this.publicationXml)
      .subscribe(res => {
        console.log(res);
      });
  }
}
