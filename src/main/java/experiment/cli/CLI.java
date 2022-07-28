package experiment.cli;

import experiment.exp.axioms.*;
import experiment.exp.classframes.*;
//import experiment.ont.*;
//import experiment.util.*;
//import experiment.regularities.axiom.*;
//import experiment.parser.*;
//import experiment.structure.*;
//import experiment.structure.nodes.*;
//import experiment.iso.gg.*;
//import experiment.hierarchy.axioms.gg.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.model.parameters.*;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.HermiT.ReasonerFactory;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.*;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.change.AxiomChangeData;
import org.semanticweb.owlapi.search.EntitySearcher; 

import java.nio.file.*;

import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.*;

/**
 * A class to demonstrate the functionality of the library.
 */
public class CLI {

    private static final Logger log = Logger.getLogger(String.valueOf(CLI.class));

    public static void main(String[] args) throws IOException , Exception {

        if(args.length != 3){
            System.out.println("Argument missing. Required input: ontology category output"); 
            System.exit(0);
        }

        String ontFilePath = args[0]; 
        String category = args[1]; 
        String outputPath = args[2]; 

        File ontFile = new File(ontFilePath);
        String ontologyName = Paths.get(ontFilePath).getFileName().toString();
        File outputFile = new File(outputPath + "/" + ontologyName);
        if(outputFile.exists()){
            System.out.println("Output destination at '" + outputPath  + "' for '" + ontologyName + "' already exists."); 
            System.exit(0);
        }

        if(category.equals("axiom")){ 
            System.out.println("Running Regularity Discovery for Axioms"); 
            experiment.exp.axioms.GGstatistics.run(ontFilePath, outputPath);
        }

        if(category.equals("classframe")){ 
            System.out.println("Running Regularity Discovery for Class frames"); 
            experiment.exp.classframes.GGstatistics.run(ontFilePath, outputPath);
        }

        if(!category.equals("axiom") && !category.equals("classframe")){
            System.out.println("Please provide one of the two categories: axiom | classframe");
            System.exit(0);
        } 
    }


}
