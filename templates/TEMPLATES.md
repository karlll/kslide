# On using templates in your project

The kslide application can use predefined powerpoint templates to create presentations.

## Template definition (`templates.json`)

The file `templates.json` defines the templates available in the application. Each template is defined by a name, a
description of the template, the filename of the template, and the filename (`layouts-filename`) of the layout
definitions.

The layout definitions are used to define the different slide layouts available in the template. The layout definitions
are stored in a separate file, which is referenced in the template definition.

### Example template definition

```json
[
  {
    "name": "Default",
    "description": "A simple template with 17 different slide layouts.",
    "filename": "template-simple.pptx",
    "layouts-filename": "template-simple-layout.json"
  }
]
```

## Layout definitions

The layout definitions refer to the different slide layouts available in the template. Each layout is defined by an
index, a name, and a description of the layout. The index is used to identify the layout in the template, and the name
and description are used to provide information about the layout.
The index of the layout should correspond to the index of the slide layout in the PowerPoint template file. The name and
description are used to provide information about the layout; the description should contain a textual description of
the layout, that could be read by an LLM. It must also include the corresponding placeholder id for each placeholder
described.
The placeholder ids are used to identify the placeholders in the layout, and they should be unique within the layout.
The placeholder ids are used to reference the placeholders in the PowerPoint template file.

### Example layout definition

```json
[
  {
    "index": 0,
    "name": "Photo - 3 Up",
    "description": "Three overlapping picture frames occupy the slide: a large left-side image (id 146), a full-height right-side image (id 145) and a smaller top-right image (id 144). A slide-number placeholder (id 147) is centered at the footer."
  }
]
```