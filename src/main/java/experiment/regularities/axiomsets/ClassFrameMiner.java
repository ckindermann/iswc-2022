package experiment.regularities.axiomsets;

import experiment.ont.*;
import experiment.parser.*;
import experiment.util.*;
import experiment.structure.*;
import experiment.structure.nodes.*;
import experiment.parser.*;

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


import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

public class ClassFrameMiner {

    private OWLOntology ontology; 
    private Map<OWLClassExpression, ClassFrame> class2frame;

    public ClassFrameMiner(OWLOntology o){
        this.ontology = o; 
        this.mineFrames();
    }

    public Map<OWLClassExpression,ClassFrame> getFrames(){
        return this.class2frame;
    }

    private void mineFrames(){
        this.class2frame = new HashMap<>();
        this.mineSubclassFrames();
        this.mineEquivalenceFrames();
        this.mineDisjointUnionFrames();
        this.mineDisjointness(); 
    }

    private void mineSubclassFrames(){ 
        for(OWLSubClassOfAxiom axiom : this.ontology.getAxioms(AxiomType.SUBCLASS_OF, Imports.EXCLUDED)){
            OWLClassExpression lhs = axiom.getSubClass(); 
            this.class2frame.putIfAbsent(lhs, new ClassFrame(lhs));
            this.class2frame.get(lhs).addSuperclass(axiom);
        }
    }

    private void mineEquivalenceFrames(){
        for(OWLEquivalentClassesAxiom axiom : this.ontology.getAxioms(AxiomType.EQUIVALENT_CLASSES, Imports.EXCLUDED)){
            Set<OWLClassExpression> equivs = axiom.getClassExpressions();
            for(OWLClassExpression exp : equivs){
                this.class2frame.putIfAbsent(exp, new ClassFrame(exp));
                this.class2frame.get(exp).addEquivalence(axiom);
            }
        } 
    }

    private void mineDisjointUnionFrames(){
        for(OWLDisjointUnionAxiom axiom : this.ontology.getAxioms(AxiomType.DISJOINT_UNION, Imports.EXCLUDED)){
            OWLClassExpression target = axiom.getOWLClass();
            this.class2frame.putIfAbsent(target, new ClassFrame(target));
            this.class2frame.get(target).addDisjointUnion(axiom);
        } 
    }

    private void mineDisjointness(){
        for(OWLDisjointClassesAxiom axiom : this.ontology.getAxioms(AxiomType.DISJOINT_CLASSES, Imports.EXCLUDED)){
            Set<OWLClassExpression> operands = axiom.getClassExpressions();
            for(OWLClassExpression exp : operands){
                this.class2frame.putIfAbsent(exp, new ClassFrame(exp));
                this.class2frame.get(exp).addDisjointClass(axiom);
            }
        } 
    } 
}
