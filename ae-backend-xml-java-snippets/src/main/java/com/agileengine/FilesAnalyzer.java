package com.agileengine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FilesAnalyzer {
    public static final String WILDCARD = "{IGNORE}";
    private static Logger LOGGER = LoggerFactory.getLogger(FilesAnalyzer.class);
    FilesAnalyzer(String xml1 , String xml2,String optional) throws SAXException, IOException {
        final Diff documentDiff = DiffBuilder
                .compare(xml1)
                .ignoreComments()
                .ignoreWhitespace()
                .withTest(xml2)
                .withDifferenceEvaluator(DifferenceEvaluators.chain(DifferenceEvaluators.Default, new DifferenceEvaluator() {
                    @Override
                    public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
                        if (outcome == ComparisonResult.EQUAL)
                            return outcome; // only evaluate differences.

                        Object test = comparison.getTestDetails().getValue();
                        if (WILDCARD.equals(test)) {
                            return ComparisonResult.SIMILAR;
                        }


                        return outcome;
                    }
                }))
                .withNodeFilter(node -> shouldEvaluateNode(node, new HashSet<String>()))
                .checkForSimilar()
                .build();

        for (Difference diff : documentDiff.getDifferences()) {
            //if(diff.getComparison())
            if(diff.getComparison().getType() != ComparisonType.CHILD_NODELIST_LENGTH ){
                if( diff.getComparison().getType() == ComparisonType.ATTR_NAME_LOOKUP){
                     QName qname = (QName) diff.getComparison().getControlDetails().getValue();
                    if("a".equals(diff.getComparison().getControlDetails().getTarget().getNodeName()) && "id".equals(qname.getLocalPart()) ){
                        NamedNodeMap node = diff.getComparison().getControlDetails().getTarget().getAttributes();
                        String toCompare = optional !=null? optional:"make-everything-ok-button";
                        if(toCompare.equals(node.getNamedItem("id").getNodeValue())){
                            LOGGER.info("------- OUTPUT GENERATED X PATH -------------- ");
                            LOGGER.info(diff.getComparison().getTestDetails().getXPath());
                            System.in.read();
                        }
                    }
                }
            }

        }

    }

    private static boolean shouldEvaluateNode(Node node, Set<String> nodeFilter) {
        if (nodeFilter.isEmpty()) {
            return true;
        }
        return !nodeFilter.contains(getXPath(node));

    }

    public static String getXPath(Node node) {
        Node parent = node.getParentNode();
        if (parent == null) {
            return "";
        }
        return getXPath(parent) + "/" + node.getLocalName() ;
    }

}
