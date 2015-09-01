Metadata Assignment Service
===========================

Spring MVC applications which provides an interface to annotate documents.


Below is a sample for metadata json file
```json
{
	"dissertation":  [
		{
			"label": "dc.format.medium",
			"gloss": "Medium",
			"readOnly": true,
			"inputType": "TEXT",
			"default": "electronic"
		},
		{
			"label": "dc.rights",
			"gloss": "Rights",
			"inputType": "TEXTAREA",
			"default": "This student work was part of a retrospective digitization project authorized by the Texas A&M University Libraries. This item is in the public domain."
		}
	],
	
	"thesis":  [
		{
			"label": "dc.author",
			"gloss": "Author",
			"repeatable": true,
			"inputType": "TEXT"
		},
		{
			"label": "dc.advisor",
			"gloss": "Advisor",
			"repeatable": true,
			"inputType": "TEXT"
		}
	],

	"default":  [
		{
			"label": "dc.title",
			"gloss": "Title",
			"inputType": "TEXTAREA"
		},
		{
			"label": "dc.description",
			"gloss": "Description",
			"inputType": "TEXTAREA"
		}
	]
}
```
