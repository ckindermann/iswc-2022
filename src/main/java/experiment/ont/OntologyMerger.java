package experiment.ont;

import experiment.util.*;
import experiment.ont.*;

import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.model.OWLOntology; 
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import java.util.*;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import java.io.FileWriter;
import org.semanticweb.owlapi.model.parameters.*;
import java.io.File;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import java.nio.file.*;



public class OntologyMerger {

    public static void main(String[] args) throws  Exception {

        String ontFile1 = args[0];
        String ontFile2 = args[1];
        String outputPath = args[2];

        String ontologyName = Paths.get(ontFile1).getFileName().toString();
        OntologyLoader l1 = new OntologyLoader(new File(ontFile1), false);
        OntologyLoader l2 = new OntologyLoader(new File(ontFile2), false);

        OWLOntology o1 = l1.getOntology();
        OWLOntology o2 = l2.getOntology();

        Set<OWLLogicalAxiom> o1Axioms = o1.getLogicalAxioms(Imports.EXCLUDED);
        Set<OWLLogicalAxiom> o2Axioms = o2.getLogicalAxioms(Imports.EXCLUDED);

        Set<OWLAxiom> merge = new HashSet<>(o1Axioms);
        merge.addAll(o2Axioms);

        int unionSize = o1Axioms.size() + o2Axioms.size();

        if(unionSize != merge.size()){
            System.out.println("There are overlaps");
        }

        OntologySaver.saveAxioms(merge,outputPath); 
    }
}


