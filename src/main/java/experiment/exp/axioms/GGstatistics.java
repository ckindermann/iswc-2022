package experiment.exp.axioms;


import experiment.ont.*;
import experiment.util.*;
import experiment.regularities.axiom.*;
import experiment.parser.*;
import experiment.structure.*;
import experiment.structure.nodes.*;
import experiment.iso.gg.*;
import experiment.hierarchy.axioms.gg.*;

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
public class GGstatistics {

    private static final Logger log = Logger.getLogger(String.valueOf(GGstatistics.class));

    public static void run(String ontFilePath, String outputPath) throws IOException , Exception {


        File ontFile = new File(ontFilePath);
        OntologyLoader ontLoader = new OntologyLoader(ontFile, false);
        OWLOntology ont = ontLoader.getOntology(); 

        String ontologyName = Paths.get(ontFilePath).getFileName().toString();
        IOHelper.createFolder(outputPath + "/" + ontologyName);


        SyntaxTreeBuilder treeBuilder = new SyntaxTreeBuilder(); 

        Set<OWLAxiom> toTest = new HashSet<>();
        toTest.addAll(ont.getAxioms(AxiomType.SUBCLASS_OF, Imports.EXCLUDED));
        toTest.addAll(ont.getAxioms(AxiomType.EQUIVALENT_CLASSES, Imports.EXCLUDED));
        toTest.addAll(ont.getAxioms(AxiomType.DISJOINT_UNION, Imports.EXCLUDED));
        toTest.addAll(ont.getAxioms(AxiomType.DISJOINT_CLASSES, Imports.EXCLUDED));

        Set<SyntaxTree> syntrees = new HashSet<>();
        for(OWLAxiom a : toTest){
            syntrees.add(treeBuilder.build(a));
        } 

        AxiomRegularityHierarchy hImp = new AxiomRegularityHierarchy(syntrees); 

        hImp.writeGraphWithInstances(outputPath + "/" + ontologyName);

        Set<HierarchyNode> nodes = hImp.getNodes();
        Set<HierarchyNode> roots = hImp.getRoots();

        writeStructures(nodes, outputPath + "/" + ontologyName); 
        writeInstances(nodes, outputPath + "/" + ontologyName); 
        writeRoots(roots, outputPath + "/" + ontologyName); 
        String statisticsPath = outputPath + "/" + ontologyName + "/statistics";
        IOHelper.createFolder(statisticsPath);

        writeRegularityStatistics(nodes, statisticsPath);
        writeHierarchyStatistics(roots, nodes, statisticsPath);
    }

    public static void writeHierarchyStatistics(Set<HierarchyNode> roots, Set<HierarchyNode> nodes, String output) throws Exception {
        String basePath = output + "/hierarchyStatistics";

        int numberOfRoots = roots.size();
        int numberOfNodes = nodes.size(); 
        int numberOfLeafs = 0; 
        int depth = 0; 
        int maxBranching = 0; 
        HashMap<Integer,Integer> level2nodes = new HashMap<>();
        int numberOfEdges = 0;

        Set<HierarchyNode> level = new HashSet<>(); 
        level.addAll(roots);
        Set<HierarchyNode> nextLevel = new HashSet<>();

        while(!level.isEmpty()){
            depth++;
            level2nodes.put(depth, level.size());
            for(HierarchyNode n : level){ 
                for(HierarchyNode c : n.getChildren()){
                    nextLevel.add(c); 
                }
            }
            level.clear();
            level.addAll(nextLevel);
            nextLevel.clear();
        }

        for(HierarchyNode n : nodes){
            int numberOfChildren = n.getChildren().size();
            numberOfEdges += numberOfChildren;
            if(numberOfChildren == 0){
                numberOfLeafs++;
            }
            if(numberOfChildren > maxBranching){
                maxBranching = numberOfChildren;
            }
        }

        IOHelper.writeAppend("NumberOfRoots,NumberOfNodes,NumberOfLeafs,Depth,MaxBranching",basePath);
        IOHelper.writeAppend(numberOfRoots + "," +
                             numberOfNodes + "," +
                             numberOfLeafs + "," +
                             depth + "," +
                             maxBranching,basePath); 
    }

    public static void writeStructures(Set<HierarchyNode> nodes, String output) throws Exception {
        String basePath = output + "/structures";
        IOHelper.createFolder(basePath);
        for(HierarchyNode node : nodes){ 
            IOHelper.writeAppend(node.getTree().getRoot().toString(),basePath + "/" + node.getID()); 
        }
    }

    public static void writeRoots(Set<HierarchyNode> nodes, String output) throws Exception {
        String basePath = output + "/roots";
        for(HierarchyNode r : nodes){ 
            IOHelper.writeAppend("" + r.getID(), basePath); 
        }
    }

    public static void writeInstances(Set<HierarchyNode> nodes, String output) throws Exception {
        String basePath = output + "/instances";
        IOHelper.createFolder(basePath);
        for(HierarchyNode node : nodes){
            Set<OWLAxiom> instances = node.getInstances();
            OntologySaver.saveAxioms(instances, basePath + "/" + node.getID() + ".owl"); 
        }
    }


    public static HashMap<String,Integer> getConstructorUsage(HierarchyNode node) throws Exception {

        HashMap<String, Integer> constructor2occurrence = new HashMap<String, Integer>();

        SyntaxTree t = node.getTree();

        SimpleDirectedGraph<SyntaxNode,DefaultEdge> tree = t.getTree();


        SyntaxNode root = t.getRoot();
        Set<SyntaxNode> level = new HashSet<>(); 
        level.add(root);
        Set<SyntaxNode> nextLevel = new HashSet<>();

        while(!level.isEmpty()){
            for(SyntaxNode n : level){ 

                if(tree.outgoingEdgesOf(n).size() > 0){
                    if(n instanceof AxiomNode){
                        OWLAxiom axiom = (OWLAxiom) n.getObject();
                        String type = axiom.getAxiomType().getName();
                        constructor2occurrence.put(type, constructor2occurrence.getOrDefault(type, 0) + 1); 
                    }
                    if(n instanceof ClassNode){
                        OWLClassExpression expression = (OWLClassExpression) n.getObject();
                        String type = expression.getClassExpressionType().getName();
                        constructor2occurrence.put(type, constructor2occurrence.getOrDefault(type, 0) + 1);
                    }
                    if(n instanceof PropertyNode){

                        String type = "ObjectInverseOf";
                        constructor2occurrence.put(type, constructor2occurrence.getOrDefault(type, 0) + 1);
                    }
                    if(n instanceof DataRangeNode){
                        OWLDataRange range = (OWLDataRange) n.getObject();
                        String type = range.getDataRangeType().getName();
                        constructor2occurrence.put(type, constructor2occurrence.getOrDefault(type, 0) + 1);
                    }
                }

                Set<DefaultEdge> edges = tree.outgoingEdgesOf(n); 
                for(DefaultEdge e : edges){
                    nextLevel.add(tree.getEdgeTarget(e)); 
                }
            }
            level.clear();
            level.addAll(nextLevel);
            nextLevel.clear();
        } 
        return constructor2occurrence;
    }

    public static void writeRegularityStatistics(Set<HierarchyNode> nodes, String output) throws Exception {
        String basePath = output + "/regularityStatistics";
        String header = "Regularity ID," +
                        "Number Of Instances," +
                        "Size of Regularity Structure," +
                        "Depth," +
                        "Number of Leafs," +
                        "Number of Non-Leafs," +
                        "Max Branching"; 

        IOHelper.writeAppend(header, basePath); 
        for(HierarchyNode node : nodes){
            SyntaxTree synTree = node.getTree();
            int regularitySize = node.getInstances().size();
            int structureSize = synTree.getTree().vertexSet().size();
            int depth = getDepth(synTree);
            int leafs = getLeafs(synTree);
            int nonLeafs = structureSize - leafs;
            int maxBranching = getMaxmialBranchingFactor(synTree);

            String sum = node.getID() + "," +
                regularitySize + "," +
                structureSize + "," +
                depth + "," +
                leafs + "," +
                nonLeafs + "," +
                maxBranching; 

            IOHelper.writeAppend(sum, basePath); 
        } 
    }

    public static int getDepth(SyntaxTree t){
        SimpleDirectedGraph<SyntaxNode,DefaultEdge> tree = t.getTree();
        int depth = -1;
        SyntaxNode root = t.getRoot();
        Set<SyntaxNode> level = new HashSet<>(); 
        level.add(root);
        Set<SyntaxNode> nextLevel = new HashSet<>();

        while(!level.isEmpty()){
            depth++;
            for(SyntaxNode n : level){ 

                Set<DefaultEdge> edges = tree.outgoingEdgesOf(n); 
                for(DefaultEdge e : edges){
                    nextLevel.add(tree.getEdgeTarget(e)); 
                }
            }
            level.clear();
            level.addAll(nextLevel);
            nextLevel.clear();
        } 
        return depth;
    }

    public static int getLeafs(SyntaxTree t){
        SimpleDirectedGraph<SyntaxNode,DefaultEdge> tree = t.getTree();
        int leafs = 0;
        for (SyntaxNode n : tree.vertexSet()) { 
            if(tree.outgoingEdgesOf(n).size() == 0){
                leafs++;
            }
        } 
        return leafs;
    }

    public static int getMaxmialBranchingFactor(SyntaxTree t){
        SimpleDirectedGraph<SyntaxNode,DefaultEdge> tree = t.getTree();
        int maxBranching = 0;
        for (SyntaxNode n : tree.vertexSet()) { 
            int branching = tree.outgoingEdgesOf(n).size();
            if(branching > maxBranching){
                maxBranching = branching;
            }
        } 
        return maxBranching; 
    } 

}
