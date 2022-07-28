package experiment.parser;

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


import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

/**
 * Class Expression Visitor 
 */
public class ConstructorPreservanceBuilder extends TreeBuilder
    implements OWLAxiomVisitor,
               OWLClassExpressionVisitor,
               OWLPropertyExpressionVisitor,
               OWLIndividualVisitor,
               OWLDataRangeVisitor,
               OWLDataVisitor {


    private SimpleDirectedGraph<SyntaxNode, DefaultEdge> syntaxTree;
    private SyntaxNode root;
    private SyntaxNode previousCall;

    private AxiomVisitor axiomVisitor;

    public ConstructorPreservanceBuilder(){
        this.axiomVisitor = new AxiomVisitor();
    }

    public SyntaxTree build(OWLAxiom axiom){
        this.syntaxTree = new SimpleDirectedGraph<>(DefaultEdge.class);
        axiom.accept(this);
        return new SyntaxTree(this.syntaxTree, this.root);
    }

    public SyntaxTree build(OWLClassExpression ce){
        this.syntaxTree = new SimpleDirectedGraph<>(DefaultEdge.class);
        this.parseRoot(ce); 
        return new SyntaxTree(this.syntaxTree, this.root);
    }

//===============================================================
//===================AXIOM VISITOR===========================
//===============================================================

    private void parseRoot(OWLAxiom axiom){
        axiom.accept(this.axiomVisitor);
        SyntaxNode node = this.axiomVisitor.getSyntaxNode();
        this.root = node;
        this.syntaxTree.addVertex(node);
        this.previousCall = node; 
    }

    public void visit(OWLAsymmetricObjectPropertyAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLClassAssertionAxiom axiom){
        this.parseRoot(axiom); 
    }
    public void visit(OWLDataPropertyAssertionAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLDataPropertyDomainAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLDataPropertyRangeAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLDifferentIndividualsAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLDisjointClassesAxiom axiom){
        this.parseRoot(axiom);

        Stream<OWLClassExpression> ops = axiom.operands();
        Set<OWLClassExpression> classes = ops.collect(Collectors.toSet()); 
        SyntaxNode parent = this.previousCall;
        for(OWLClassExpression c : classes){
            this.previousCall = parent;
            this.addNode(c);
            c.accept(this);
        }
    }
    public void visit(OWLDisjointDataPropertiesAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLDisjointObjectPropertiesAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLDisjointUnionAxiom axiom){
        this.parseRoot(axiom);

        SyntaxNode parent = this.previousCall;
        OWLClassExpression union = axiom.getOWLClass();

        if(!union.isOWLClass()){
            UnionNode node = new UnionNode(union); 
            this.syntaxTree.addVertex(node);
            this.syntaxTree.addEdge(this.previousCall,node); 
            this.previousCall = node; 
            union.accept(this);
        }

        this.previousCall = parent;
        Stream<OWLClassExpression> ops = axiom.operands();
        Set<OWLClassExpression> operands = ops.collect(Collectors.toSet()); 

        for(OWLClassExpression o : operands){
            this.previousCall = parent;
            this.addNode(o);
            o.accept(this); 
        }

    }
    public void visit(OWLEquivalentClassesAxiom axiom){
        this.parseRoot(axiom);
        Stream<OWLClassExpression> ops = axiom.operands();
        Set<OWLClassExpression> classes = ops.collect(Collectors.toSet());

        SyntaxNode parent = this.previousCall;
        for(OWLClassExpression c : classes){
            this.previousCall = parent;

            this.addNode(c);

            c.accept(this);
        }
    }
    public void visit(OWLEquivalentDataPropertiesAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLEquivalentObjectPropertiesAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLFunctionalDataPropertyAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLFunctionalObjectPropertyAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLHasKeyAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLInverseObjectPropertiesAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLIrreflexiveObjectPropertyAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLObjectPropertyAssertionAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLObjectPropertyDomainAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLObjectPropertyRangeAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLReflexiveObjectPropertyAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLSameIndividualAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLSubClassOfAxiom axiom){
        this.parseRoot(axiom);
        SyntaxNode parent = this.previousCall;

        OWLClassExpression subClass = axiom.getSubClass(); 
        if(!subClass.isOWLClass()){
            SubClassOfNode subnode = new SubClassOfNode(subClass); 
            this.syntaxTree.addVertex(subnode);
            this.syntaxTree.addEdge(this.previousCall,subnode); 
            this.previousCall = subnode; 
            subClass.accept(this);
        }

        this.previousCall = parent;
        OWLClassExpression superClass = axiom.getSuperClass();
        if(!superClass.isOWLClass()){
            SuperClassOfNode node = new SuperClassOfNode(superClass); 
            this.syntaxTree.addVertex(node);
            this.syntaxTree.addEdge(this.previousCall,node); 
            this.previousCall = node; 
            superClass.accept(this); 
        }
    }
    public void visit(OWLSubDataPropertyOfAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLSubObjectPropertyOfAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLSubPropertyChainOfAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLSymmetricObjectPropertyAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
        this.parseRoot(axiom);
    }


    public void visit(SWRLRule node){
        System.out.println("Parsed a SWRLRule - cannot be handled");
    }
    public void doDefault(Object object){
        System.out.println("Parsed an Object in 'doDefault' - cannot be handled"); 
    }
    public void getDefaultReturnValue(Object object){
        System.out.println("Parsed an Object in 'getDefaultReturnValue' - cannot be handled"); 
    }
    public void handleDefault(Object c){
        System.out.println("Parsed an Object in 'handleDefault' - cannot be handled"); 
    }
    //-----------------
    public void visit(OWLAnnotationAssertionAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLAnnotationPropertyDomainAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLAnnotationPropertyRangeAxiom axiom){
        this.parseRoot(axiom);
    }
    public void visit(OWLSubAnnotationPropertyOfAxiom axiom){
        this.parseRoot(axiom);
    }
//===============================================================
//============CLASS EXPRESSIONS VISITOR==========================
//===============================================================
//
    private void parseRoot(OWLClassExpression ce){
        SyntaxNode node = new ClassNode(ce);
        this.root = node;
        this.syntaxTree.addVertex(node);
        this.previousCall = node; 
        ce.accept(this);
    }

    private void addNode(OWLClassExpression expr){ 
        if(expr.isOWLClass()){
            return;
        }
        ClassNode node = new ClassNode(expr); 
        this.syntaxTree.addVertex(node);
        if(this.previousCall != null){
            this.syntaxTree.addEdge(this.previousCall,node); 
        }
        this.previousCall = node; 
    }

    public void visit(OWLClass ce){ 
        this.previousCall = null; 
    }

    public void visit(OWLDataAllValuesFrom ce){ 

        SyntaxNode parent = this.previousCall;
        OWLPropertyExpression property = ce.getProperty();
        this.addNode(property);

        this.previousCall = parent; 
        OWLDataRange range = (OWLDataRange) ce.getFiller(); 
        this.addNode(range);
        range.accept(this);
    }
    public void visit(OWLDataExactCardinality ce){ 
        int cardinality = ce.getCardinality();
        CardinalityNode cardNode = new CardinalityNode(cardinality);
        this.syntaxTree.addVertex(cardNode); 
        this.syntaxTree.addEdge(this.previousCall,cardNode); 

        SyntaxNode parent = this.previousCall;

        OWLDataPropertyExpression pe = ce.getProperty(); 
        this.addNode(pe);
        pe.accept(this);

        this.previousCall = parent;

        OWLDataRange dataRange = ce.getFiller();
        if(dataRange != null){
            this.addNode(dataRange);
            dataRange.accept(this);
        }
    }
    public void visit(OWLDataHasValue ce){ 
        SyntaxNode parent = this.previousCall;
        OWLDataPropertyExpression pe = ce.getProperty();
        this.addNode(pe);
        pe.accept(this);

        this.previousCall = parent;

        OWLLiteral filler = ce.getFiller();
        this.addNode(filler);
        filler.accept(this); 
    }
    public void visit(OWLDataMaxCardinality ce){ 
        int cardinality = ce.getCardinality();
        CardinalityNode cardNode = new CardinalityNode(cardinality);
        this.syntaxTree.addVertex(cardNode); 
        this.syntaxTree.addEdge(this.previousCall,cardNode); 

        SyntaxNode parent = this.previousCall;

        OWLDataPropertyExpression pe = ce.getProperty(); 
        this.addNode(pe);
        pe.accept(this);

        this.previousCall = parent;

        OWLDataRange dataRange = ce.getFiller();
        if(dataRange != null){
            this.addNode(dataRange);
            dataRange.accept(this);
        }
    }
    public void visit(OWLDataMinCardinality ce){ 
        int cardinality = ce.getCardinality();
        CardinalityNode cardNode = new CardinalityNode(cardinality);
        this.syntaxTree.addVertex(cardNode); 
        this.syntaxTree.addEdge(this.previousCall,cardNode); 

        SyntaxNode parent = this.previousCall;

        OWLDataPropertyExpression pe = ce.getProperty(); 
        this.addNode(pe);
        pe.accept(this);

        this.previousCall = parent;

        OWLDataRange dataRange = ce.getFiller();
        if(dataRange != null){
            this.addNode(dataRange);
            dataRange.accept(this);
        }
    }
    public void visit(OWLDataSomeValuesFrom ce){ 
        SyntaxNode parent = this.previousCall;
        OWLPropertyExpression property = ce.getProperty();
        this.addNode(property);

        this.previousCall = parent; 
        OWLDataRange range = (OWLDataRange) ce.getFiller(); 
        this.addNode(range);
        range.accept(this);
    }

    public void visit(OWLObjectOneOf ce){ 
        Set<OWLIndividual> individuals = ce.getIndividuals();
        SyntaxNode parent = this.previousCall;
        for(OWLIndividual i : individuals){
            this.previousCall = parent;
            this.addNode(i);
        }
        this.previousCall = null;
    }

    public void visit(OWLObjectHasSelf ce){ 
        OWLObjectPropertyExpression pe = ce.getProperty();
        this.addNode(pe);
        pe.accept(this);
    }

    public void visit(OWLObjectHasValue ce){ 
        OWLIndividual individual = (OWLIndividual) ce.getFiller();
        SyntaxNode parent = this.previousCall;
        this.addNode(individual);

        this.previousCall = parent; 
        OWLPropertyExpression property = ce.getProperty();
        this.addNode(property);
        property.accept(this); 
    }

    public void visit(OWLObjectAllValuesFrom ce){ 
        SyntaxNode parent = this.previousCall;

        OWLPropertyExpression property = ce.getProperty();
        this.addNode(property);
        property.accept(this);

        this.previousCall = parent;

        OWLClassExpression filler = ce.getFiller();
        this.addNode(filler);
        filler.accept(this); 
    }
    public void visit(OWLObjectComplementOf ce){ 
        OWLClassExpression complement = ce.getOperand();
        this.addNode(complement); 
        complement.accept(this);
    }
    public void visit(OWLObjectExactCardinality ce){
        SyntaxNode parent = this.previousCall;

        int cardinality = ce.getCardinality();
        CardinalityNode cardNode = new CardinalityNode(cardinality);
        this.syntaxTree.addVertex(cardNode); 
        this.syntaxTree.addEdge(this.previousCall,cardNode); 

        OWLPropertyExpression property = ce.getProperty();
        this.addNode(property);
        property.accept(this);

        this.previousCall = parent;

        OWLClassExpression filler = ce.getFiller();
        this.addNode(filler);
        filler.accept(this); 

    }
    public void visit(OWLObjectIntersectionOf ce){
        Set<OWLClassExpression> operands = ce.getOperands();
        SyntaxNode parent = this.previousCall;

        for(OWLClassExpression o : operands){
            this.previousCall = parent;
            this.addNode(o);
            o.accept(this);
        }
    }
    public void visit(OWLObjectMaxCardinality ce){
        SyntaxNode parent = this.previousCall;

        int cardinality = ce.getCardinality();
        CardinalityNode cardNode = new CardinalityNode(cardinality);
        this.syntaxTree.addVertex(cardNode); 
        this.syntaxTree.addEdge(this.previousCall,cardNode); 

        OWLPropertyExpression property = ce.getProperty();
        this.addNode(property);
        property.accept(this);

        this.previousCall = parent;

        OWLClassExpression filler = ce.getFiller();
        this.addNode(filler);
        filler.accept(this); 
    }
    public void visit(OWLObjectMinCardinality ce){
        SyntaxNode parent = this.previousCall;

        int cardinality = ce.getCardinality();
        CardinalityNode cardNode = new CardinalityNode(cardinality);
        this.syntaxTree.addVertex(cardNode); 
        this.syntaxTree.addEdge(this.previousCall,cardNode); 

        OWLPropertyExpression property = ce.getProperty();
        this.addNode(property);
        property.accept(this);

        this.previousCall = parent;

        OWLClassExpression filler = ce.getFiller();
        this.addNode(filler);
        filler.accept(this); 
    }
    public void visit(OWLObjectSomeValuesFrom ce){
        SyntaxNode parent = this.previousCall;
        OWLPropertyExpression property = ce.getProperty();
        this.addNode(property);
        property.accept(this);

        this.previousCall = parent;

        OWLClassExpression filler = ce.getFiller();
        this.addNode(filler);
        filler.accept(this); 
    }
    public void visit(OWLObjectUnionOf ce){
        Set<OWLClassExpression> operands = ce.getOperands();
        SyntaxNode parent = this.previousCall;
        for(OWLClassExpression o : operands){
            this.previousCall = parent;
            this.addNode(o);
            o.accept(this);
        }
    } 


    //===============================================================
    //============Property EXPRESSIONS VISITOR==========================
    //===============================================================
    private void addNode(OWLPropertyExpression expr){ 
        if(expr.isNamed()){
            return;
        }
        PropertyNode node = new PropertyNode(expr); 
        this.syntaxTree.addVertex(node);
        if(this.previousCall != null){
            this.syntaxTree.addEdge(this.previousCall,node); 
        }
        this.previousCall = node; 
    }

    public void visit(OWLObjectInverseOf property){
        OWLObjectPropertyExpression pe = property.getInverse();
        this.addNode(pe);
        pe.accept(this); 
    }

    public void visit(OWLObjectProperty property){
        this.previousCall = null; 
    }

    public void visit(OWLDataProperty property){
        this.previousCall = null;
    }

    public void visit(OWLAnnotationProperty property){
        ;
    }

    //===============================================================
    //============Individual VISITOR==========================
    //===============================================================
    private void addNode(OWLIndividual i){ 
        if(i.isNamed()){
            return;
        }
        IndividualNode node = new IndividualNode(i); 
        this.syntaxTree.addVertex(node);
        
        if(this.previousCall != null){
            this.syntaxTree.addEdge(this.previousCall,node); 
        }
        this.previousCall = node; 
    }

    public void visit(OWLAnonymousIndividual individual){
        this.previousCall = null; 
    }
    public void visit(OWLNamedIndividual individual){
        this.previousCall = null; 
    }

    //===============================================================
    //============Data Range VISITOR==========================
    //===============================================================
    private void addNode(OWLDataRange r){
        DataRangeNode node = new DataRangeNode(r);
        this.syntaxTree.addVertex(node);
        if(this.previousCall != null){
            this.syntaxTree.addEdge(this.previousCall,node); 
        }
        this.previousCall = node; 
    }

    private void addNode(OWLLiteral l){
        return;

    }

    private void addNode(OWLFacetRestriction r){
        FacetRestrictionNode node = new FacetRestrictionNode(r);
        this.syntaxTree.addVertex(node);
        if(this.previousCall != null){
            this.syntaxTree.addEdge(this.previousCall,node); 
        }
        this.previousCall = node; 
    }

    public void visit(OWLDataComplementOf node){
        OWLDataRange r = node.getDataRange();
        this.addNode(r);
        r.accept(this);
    }
    public void visit(OWLDataIntersectionOf node){
        Set<OWLDataRange> rs = node.getOperands();
        SyntaxNode parent = this.previousCall;
        for(OWLDataRange r : rs){
            this.previousCall = parent;
            this.addNode(r);
            r.accept(this); 
        } 
    }
    public void visit(OWLDataOneOf node){
        Set<OWLLiteral> ls = node.getValues();
        SyntaxNode parent = this.previousCall;
        for(OWLLiteral l : ls){
            this.previousCall = parent;
            this.addNode(l);
            l.accept(this); 
        }

    }
    public void visit(OWLDatatypeRestriction node){
        SyntaxNode parent = this.previousCall;

        OWLDatatype dt = node.getDatatype();
        this.addNode(dt);
        dt.accept(this);

        Set<OWLFacetRestriction> rs = node.getFacetRestrictions();
        for(OWLFacetRestriction r : rs){
            this.previousCall = parent;
            this.addNode(r);
            r.accept(this);
        } 
    }
    public void visit(OWLDataUnionOf node){
        Set<OWLDataRange> rs = node.getOperands();
        SyntaxNode parent = this.previousCall;
        for(OWLDataRange r : rs){
            this.previousCall = parent;
            this.addNode(r);
            r.accept(this); 
        } 
    } 

    public void visit(OWLDatatype node){
        this.previousCall = null;
    } 
    public void visit(OWLLiteral node){
        this.previousCall = null;
    }
    public void visit(OWLFacetRestriction node){
        OWLLiteral facetValue = node.getFacetValue();
        this.addNode(facetValue);
        facetValue.accept(this);
    }
}
