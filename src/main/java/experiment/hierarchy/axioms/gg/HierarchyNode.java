package experiment.hierarchy.axioms.gg;

import experiment.ont.*;
import experiment.util.*;
import experiment.structure.*;
import experiment.structure.nodes.*;
import experiment.iso.gg.*;
import experiment.subIso.*;

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


public class HierarchyNode {
    private int ID;
    private SyntaxTree tree;
    private SyntaxTree internalTree; 
    private Set<OWLAxiom> instances;
    private Set<HierarchyNode> children;
    private Set<HierarchyNode> parents;
    private int depth;


    public Set<HierarchyNode> visitedDuringInsertion;

    public HierarchyNode(SyntaxTree t, SyntaxTree i, int id){
        this.ID = id;
        this.tree = t;
        this.internalTree = i;
        this.instances = new HashSet<>();

        this.instances.add(((AxiomNode) this.tree.getRoot()).getAxiom());
        this.children = new HashSet<>();
        this.parents = new HashSet<>();

        this.visitedDuringInsertion = new HashSet<>(); 
    }

    public SyntaxTree getTree(){
        return this.tree;
    }

    public boolean represents(SyntaxTree t){ 
        return GroundGeneralisation.exists(this.tree, t);
    }

    public int getDepth(){
        return this.depth;
    }

    public void setDepth(int d){
        this.depth = d;
    }

    public SyntaxTree getInternalTree(){
        return this.internalTree;
    }

    public Set<OWLAxiom> getInstances(){
        return this.instances;
    }

    public Set<HierarchyNode> getChildren(){
        return this.children;
    }

    public Set<HierarchyNode> getParents(){
        return this.parents;
    }

    public void setID(int id){
        this.ID = id;
    }

    public int getID(){
        return this.ID;
    }

    public void addInstance(OWLAxiom a){
        this.instances.add(a);
    }

    public void addInstances(Set<OWLAxiom> as){
        this.instances.addAll(as);
    }

    public void addChild(HierarchyNode node){
        this.children.add(node);
    }

    public void addChildren(Set<HierarchyNode> nodes){
        this.children.addAll(nodes);
    }

    public void addParent(HierarchyNode node){
        this.parents.add(node);
    }

    public void addParents(Set<HierarchyNode> nodes){
        this.parents.addAll(nodes);
    }

    public boolean insertFromAbove(HierarchyNode n){
        n.visitedDuringInsertion.add(this);
        if(this.sameAs(n)){
           n.addInstance(((AxiomNode) this.tree.getRoot()).getAxiom());
           return true;
        }
        if(this.coveredBy(n)){
            boolean insertionPointFound = false;
            for(HierarchyNode c : this.children){
                if(!n.visitedDuringInsertion.contains(c)){
                    if(c.insertFromAbove(n)){
                        insertionPointFound = true;
                    } 
                } else {
                    if(c.coveredBy(n)){
                        insertionPointFound = true;
                    } 
                } 
            }
            if(!insertionPointFound){
                this.children.add(n);
            }
            return true; 
        }
        return false; 
    }

    public boolean coveredBy(HierarchyNode n){

        if(GroundGeneralisation.exists(n.getInternalTree(), this.internalTree)){
            if(Subisomorphism.exists(n.getTree(), this.tree)){
                return true;
            } else {
                return false; 
            } 
        } else {
            return Subisomorphism.exists(n.getInternalTree(), this.internalTree); 
        }


    } 

    public boolean sameAs(HierarchyNode n){
        return GroundGeneralisation.exists(this.tree, n.getTree());
    }
}
