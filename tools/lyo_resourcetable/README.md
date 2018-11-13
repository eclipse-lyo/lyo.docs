A utility to generate an OSLC shape document from a tWiki or mediawiki format
resource table.

## Getting started

First, **make sure `tools-common` is installed.**

    mvn clean package
    java -jar target/table.jar -f %FILENAME%

## Usage

The usage is simple. This tool consumes a text file (passed to the tool using the `-f` arg) containing a block of wiki table text that decribes a resource.
The tool would then generate a Resource shape document in `text/turtle` format.

The tool has known namespaces mapped to known prefixes. However, your resource might have some namespaces that are not known to this too. A simple way to make the tool aware of those ns/prefix bindings is to use the `-m` arg.
The tool allows 0..* of those `-m` args. A sample usage is provided below.

    java -f<Wiki File to process> [-m<ns-prefix mapping>] wiki.WikiTest2ResourceShape

    java -fcm_wiki.txt sandbox.WikiTest2ResourceShape

    java -fcmwiki.txt -mex|http://example.org# -mabc|http://a.b# sandbox.WikiTest2ResourceShape