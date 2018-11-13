# RDF/XML checker

Checks some properties, including RDF vocabulary accesibility etc.

## Getting started

First, **make sure `tools-common` is installed.**

**NB!** Some tests are failing as of Nov'18.

```
mvn clean package -DskipTests
java -jar target/checker.jar %FILENAME%
```

For example:

```
java -jar target/checker.jar src/test/resources/testcase6.rdf
0 [main] INFO org.eclipse.lyo.tools.common.util.OSLCToolLogger  -
0 [main] INFO org.eclipse.lyo.tools.common.util.OSLCToolLogger  - Input example:        /Users/andrew/kth/lyo/docs/tools/examplechecker/src/test/resources/testcase6.rdf format:RDF/XML
301 [main] INFO org.eclipse.lyo.tools.common.util.OSLCToolLogger  - Checking for unterminated and invalid namespace prefix URIs in prefix mappings
302 [main] ERROR org.eclipse.lyo.tools.common.util.OSLCToolLogger  - Suspicious namespace URI: http://open-services.net/ns/servicemanagement/1.0/
302 [main] INFO org.eclipse.lyo.tools.common.util.OSLCToolLogger  - Checked 8 prefix mappings
302 [main] INFO org.eclipse.lyo.tools.common.util.OSLCToolLogger  - Checking for non-HTTP/HTTPS resource URIs as objects of RDF triples
309 [main] INFO org.eclipse.lyo.tools.common.util.OSLCToolLogger  - Checked 2 objects of RDF triples
310 [main] INFO org.eclipse.lyo.tools.common.util.OSLCToolLogger  - Checking predicates and objects in the statements...
377 [main] INFO org.eclipse.lyo.tools.common.util.OSLCToolLogger  - Loading predicate type reference...
428 [main] INFO org.eclipse.lyo.tools.common.util.OSLCToolLogger  - Loading resource shapes...
492 [main] ERROR org.eclipse.lyo.tools.common.util.OSLCToolLogger  - oslc:totalCount            Suggestion:     add rdf:datatype="http://www.w3.org/2001/XMLSchema#integer";
492 [main] INFO org.eclipse.lyo.tools.common.util.OSLCToolLogger  - Checked 2 statements.
505 [main] INFO org.eclipse.lyo.tools.common.util.OSLCToolLogger  - Checking 1 URIs, it may take a while...
1030 [main] ERROR org.eclipse.lyo.tools.common.util.OSLCToolLogger  - Error:            "http://open-services.net/ns/core#ResponseInfo" is not accessible via GET, or its content does not contain the URI as would be expected in a RDFS vocabulary document.
```
