package experiment.iso.gg;

import experiment.iso.*;
import experiment.ont.*;
import experiment.util.*;
import experiment.structure.*;
import experiment.structure.nodes.*;

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

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.*;
import org.jgrapht.alg.isomorphism.*;


public class SetGroundGeneralisation {

    public static boolean exists(Set<SyntaxTree> s1, Set<SyntaxTree> s2){

        if(s1.size() != s2.size()){
            return false;
        }

        Set<SyntaxTree> matched = new HashSet<>();

        for(SyntaxTree t : s1){
            boolean found = false;
            for(SyntaxTree s : s2){
                if(!matched.contains(s)){
                    if(GroundGeneralisation.exists(t,s)){
                        found = true;
                        matched.add(s); 
                        break; 
                    }
                }
            }
            if(!found){
                return false;
            }
        }
        return true; 
    } 

}
