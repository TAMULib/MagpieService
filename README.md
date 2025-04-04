[![Build Status][build-badge]][build-status]
[![Coverage Status][coverage-badge]][coverage-status]

# Metadata Assignment GUI Providing Ingest and Export Service

<a name="readme-top"></a>

A *Spring* backend for the *Metadata Assignment GUI Providing Ingest and Export (MAGPIE) Service* developed and maintained by [Texas A&M University Libraries][tamu-library].

<details>
<summary>Table of contents</summary>

  - [Deployment](#deployment)
  - [Developer Documentation](#developer-documentation)
  - [Additional Resources](#additional-resources)

</details>


## Deployment

For a quick and easy deployment using `docker-compose` consider using the [MAGPIE App Repo][app-repo].

For _advanced use cases_, or when `docker-compose` is unavailable, the UI may be either started using `docker` directly or even manually started.
This process is further described in the [Deployment Guide][deployment-guide].

<div align="right">(<a href="#readme-top">back to top</a>)</div>

<details>
<summary>Sample for metadata json file</summary>

```json
{
   "dissertation":{
      "repositories":[
         {
            "name":"DSpace for Dissertations",
            "type":"DSPACE",
            "settings":[
               {
                  "key":"repoUrl",
                  "values":[
                     "https://dspacepre1.library.tamu.edu"
                  ]
               },
               {
                  "key":"repoUIPath",
                  "values":[]
               },
               {
                  "key":"collectionId",
                  "values":[
                     "138"
                  ]
               },
               {
                  "key":"groupId",
                  "values":[
                     "246"
                  ]
               },
               {
                  "key":"userName",
                  "values":[
                     "magpie@oaktrust.library.tamu.edu"
                  ]
               },
               {
                  "key":"password",
                  "values":[
                     "************"
                  ]
               }
            ]
         }
      ],
      "authorities":[
         {
            "name":"Voyager for Dissertations Authority",
            "type":"VOYAGER",
            "settings":[
               {
                  "key":"host",
                  "values":[
                     "surprise.tamu.edu"
                  ]
               },
               {
                  "key":"port",
                  "values":[
                     "7014"
                  ]
               },
               {
                  "key":"app",
                  "values":[
                     "vxws"
                  ]
               }
            ]
         }
      ],
      "suggestors":[],
      "metadata":[
         {
            "label":"dc.title",
            "gloss":"Title",
            "repeatable":false,
            "readOnly":false,
            "required":true,
            "inputType":"TEXT"
         },
         {
            "label":"dc.creator",
            "gloss":"Creator",
            "repeatable":false,
            "readOnly":false,
            "required":true,
            "inputType":"TEXT"
         },
         {
            "label":"dc.contributor.advisor",
            "gloss":"Committee Chair",
            "repeatable":true,
            "inputType":"TEXT"
         },
         {
            "label":"dc.contributor.committeeMember",
            "gloss":"Committee Member",
            "repeatable":true,
            "inputType":"TEXT"
         },
         {
            "label":"dc.date.created",
            "gloss":"Date Created",
            "inputType":"TEXT"
         },
         {
            "label":"dc.date.issued",
            "gloss":"Date Issued",
            "inputType":"TEXT"
         },
         {
            "label":"dc.subject.lcsh",
            "gloss":"LoC Subject Terms",
            "repeatable":true,
            "inputType":"TEXT"
         },
         {
            "label":"dc.subject",
            "gloss":"Subject",
            "repeatable":true,
            "inputType":"TEXT"
         },
         {
            "label":"dc.description",
            "gloss":"Description",
            "repeatable":false,
            "readOnly":false,
            "required":true,
            "inputType":"TEXTAREA"
         },
         {
            "label":"dc.description.abstract",
            "gloss":"Abstract",
            "inputType":"TEXTAREA"
         },
         {
            "label":"thesis.degree.grantor",
            "gloss":"Grantor",
            "inputType":"TEXT"
         },
         {
            "label":"dc.language.iso",
            "gloss":"Language",
            "hidden":true,
            "inputType":"TEXT",
            "default":"en_US"
         },
         {
            "label":"thesis.degree.name",
            "gloss":"Degree Name",
            "inputType":"TEXT"
         },
         {
            "label":"thesis.degree.level",
            "gloss":"Degree Level",
            "inputType":"TEXT",
            "default":"Doctoral"
         },
         {
            "label":"dc.publisher",
            "gloss":"Publisher",
            "inputType":"TEXT",
            "default":"Texas A&M University"
         },
         {
            "label":"dc.type.material",
            "gloss":"Material",
            "inputType":"TEXT",
            "default":"text"
         },
         {
            "label":"dc.type.genre",
            "gloss":"Genre",
            "inputType":"TEXT",
            "default":"Dissertation"
         },
         {
            "label":"dc.type",
            "gloss":"Type",
            "inputType":"TEXT",
            "default":"Thesis"
         },
         {
            "label":"dc.format.digitalOrigin",
            "gloss":"Digital Origin",
            "inputType":"TEXT",
            "default":"reformatted digital"
         },
         {
            "label":"dc.format.medium",
            "gloss":"Medium",
            "readOnly":true,
            "inputType":"TEXT",
            "default":"electronic"
         },
         {
            "label":"dc.rights",
            "gloss":"Rights",
            "inputType":"TEXTAREA",
            "default":"This student work was part of a retrospective digitization project authorized by the Texas A&M University Libraries. This item is in the public domain."
         }
      ]
   },
   "taes_misc_publication":{
      "repositories":[
        {
            "name":"DSpace for TAES Misc Publications",
            "type":"DSPACE",
            "settings":[
               {
                  "key":"repoUrl",
                  "values":[
                     "https://dspacepre1.library.tamu.edu"
                  ]
               },
               {
                  "key":"repoUIPath",
                  "values":[

                  ]
               },
               {
                  "key":"collectionId",
                  "values":[
                     "176"
                  ]
               },
               {
                  "key":"groupId",
                  "values":[
                     "0"
                  ]
               },
               {
                  "key":"userName",
                  "values":[
                     "magpie@oaktrust.library.tamu.edu"
                  ]
               },
               {
                  "key":"password",
                  "values":[
                     "************"
                  ]
               }
            ]
         }
      ],
      "authorities":[
         {
            "name":"NALT CSV Authority",
            "type":"CSV",
            "settings":[
               {
                  "key":"paths",
                  "values":[
                     "config/csv/TAES-Misc-Publication-Collection-Metadata.csv"
                  ]
               },
               {
                  "key":"delimeter",
                  "values":[
                     "||"
                  ]
               }
            ]
         }
      ],
      "suggestors":[
         {
            "name":"NALT Pelican Suggestor",
            "type":"NALT",
            "settings":[
               {
                  "key":"pelicanUrl",
                  "values":[
                     "http://localhost:9000/nalt/suggestions"
                  ]
               },
               {
                  "key":"subjectLabel",
                  "values":[
                     "dc.subject.nalt"
                  ]
               }
            ]
         }
      ],
      "metadata":[
         {
            "label":"dc.creator",
            "gloss":"Author",
            "repeatable":true,
            "inputType":"TEXT"
         },
         {
            "label":"dc.title",
            "gloss":"Title",
            "repeatable":false,
            "inputType":"TEXT"
         },
         {
            "label":"dc.relation.ispartof",
            "gloss":"Is Part Of",
            "repeatable":false,
            "inputType":"TEXT"
         },
         {
            "label":"dc.publisher",
            "gloss":"Publisher",
            "repeatable":false,
            "inputType":"TEXT",
            "default":"Texas Agricultural Experiment Station"
         },
         {
            "label":"dc.date.issued",
            "gloss":"Issue Date",
            "repeatable":false,
            "inputType":"TEXT"
         },
         {
            "label":"dc.description",
            "gloss":"Description (Number of Pages)",
            "repeatable":false,
            "inputType":"TEXT"
         },
         {
            "label":"dc.description.tableofcontents",
            "gloss":"Table of Contents",
            "repeatable":false,
            "inputType":"TEXTAREA"
         },
         {
            "label":"dc.subject",
            "gloss":"Subject",
            "repeatable":true,
            "inputType":"TEXT"
         },
         {
            "label":"dc.subject.nalt",
            "gloss":"NALT Term",
            "repeatable":true,
            "inputType":"SUGGESTION"
         },
         {
            "label":"dc.subject.lcsh",
            "gloss":"Library of Congress Subject Headings",
            "repeatable":true,
            "inputType":"TEXT"
         },
         {
            "label":"dc.language",
            "gloss":"Language",
            "repeatable":false,
            "inputType":"TEXT",
            "default":"en_US"
         },
         {
            "label":"dc.type",
            "gloss":"Type",
            "repeatable":false,
            "inputType":"TEXT"
         },
         {
            "label":"dc.format",
            "gloss":"Format",
            "repeatable":false,
            "inputType":"TEXT"
         },
         {
            "label":"dc.coverage.spatial",
            "gloss":"Spatial Coverage",
            "repeatable":true,
            "inputType":"TEXT"
         },
         {
            "label":"dc.audience",
            "gloss":"Audience",
            "repeatable":true,
            "inputType":"TEXT"
         }
      ]
   },
   "thesis":{
      "repositories":[],
      "authorities":[],
      "suggestors":[],
      "metadata":[
         {
            "label":"dc.author",
            "gloss":"Author",
            "repeatable":true,
            "inputType":"TEXT"
         },
         {
            "label":"dc.advisor",
            "gloss":"Advisor",
            "repeatable":true,
            "inputType":"TEXT"
         }
      ]
   },
   "default":{
      "repositories":[],
      "authorities":[],
      "suggestors":[],
      "metadata":[
         {
            "label":"dc.title",
            "gloss":"Title",
            "inputType":"TEXTAREA"
         },
         {
            "label":"dc.description",
            "gloss":"Description",
            "inputType":"TEXTAREA"
         }
      ]
   }
}
```

</details>


## Developer Documentation

- [Contributors Documentation][contribute-guide]
- [Deployment Documentation][deployment-guide]

<div align="right">(<a href="#readme-top">back to top</a>)</div>


## Additional Resources

Please feel free to file any issues concerning MAGPIE Service to the issues section of the repository.

Any questions concerning MAGPIE Service can be directed to helpdesk@library.tamu.edu.

Copyright © 2022-2025 Texas A&M University Libraries under the [MIT license][LICENSE].

<div align="right">(<a href="#readme-top">back to top</a>)</div>


<!-- LINKS -->
[app-repo]: https://github.com/TAMULib/MAGPIE
[build-badge]: https://github.com/TAMULib/MetadataAssignmentToolService/workflows/Build/badge.svg
[build-status]: https://github.com/TAMULib/MetadataAssignmentToolService/actions?query=workflow%3ABuild
[coverage-badge]: https://coveralls.io/repos/github/TAMULib/MetadataAssignmentToolService/badge.svg
[coverage-status]: https://coveralls.io/github/TAMULib/MetadataAssignmentToolService

[tamu-library]: http://library.tamu.edu
[deployment-guide]: DEPLOYING.md
[contribute-guide]: CONTRIBUTING.md
[license]: LICENSE
