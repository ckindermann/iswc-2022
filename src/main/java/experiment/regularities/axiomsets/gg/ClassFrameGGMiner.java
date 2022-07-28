package experiment.regularities.axiomsets.gg;

import experiment.ont.*;
import experiment.parser.*;
import experiment.util.*;
import experiment.iso.gg.*;
import experiment.structure.*;
import experiment.structure.nodes.*;
import experiment.parser.*;
import experiment.regularities.axiomsets.*;

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


public class ClassFrameGGMiner {

    private OWLOntology ontology;
    private TreeMap<Integer,Set<ClassFrameGroundGeneralisation>> specificity2frames;
    private TreeMap<Integer,Set<ClassFrameGroundGeneralisation>> specificity2regularity;

    private Map<ClassFrameGroundGeneralisation,ClassFrameGroundGeneralisation> instance2regularity;
    private Map<ClassFrameGroundGeneralisation,Set<ClassFrameGroundGeneralisation>> regularity2instances;

    private Map<OWLClassExpression,ClassFrame> class2frame; 

    public ClassFrameGGMiner(OWLOntology o){
        this.ontology = o;
        ClassFrameMiner cfminer = new ClassFrameMiner(o);
        this.class2frame = cfminer.getFrames();
        this.stratifyBySize(new HashSet<>(this.class2frame.values()));
        this.mine(); 
    }

    public ClassFrameGGMiner(Set<ClassFrame> frames){
        this.stratifyBySize(frames);
        this.mine(); 
    }

    public Map<ClassFrameGroundGeneralisation,Set<ClassFrameGroundGeneralisation>> getRegularity2instance(){
        return this.regularity2instances;
    }

    public Map<OWLClassExpression,ClassFrame> getClass2Frame(){
        return this.class2frame;
    }

    private void mine() {
        this.specificity2regularity = new TreeMap<>();

        this.instance2regularity = new HashMap<>();
        this.regularity2instances = new HashMap<>();

        int i = 1;
        for(Map.Entry<Integer,Set<ClassFrameGroundGeneralisation>> entry : this.specificity2frames.entrySet()){


            int specificity = entry.getKey();
            Set<ClassFrameGroundGeneralisation> toPartition = entry.getValue();

            this.specificity2regularity.putIfAbsent(specificity,new HashSet<>());
            Set<ClassFrameGroundGeneralisation> regs = this.specificity2regularity.get(specificity);

            for(ClassFrameGroundGeneralisation frame : toPartition){
                boolean found = false;
                for(ClassFrameGroundGeneralisation regularity : regs){
                    if(frame.isIsomorphic(regularity)){
                        this.instance2regularity.put(frame,regularity);
                        this.regularity2instances.get(regularity).add(frame);
                        found = true; 
                        break;
                    } 
                } 
                if(!found){
                    this.regularity2instances.put(frame,new HashSet<>());
                    this.regularity2instances.get(frame).add(frame);
                    regs.add(frame);
                }
            } 
        }

    }

    private void stratifyBySize(Set<ClassFrame> framesToTest){
        this.specificity2frames = new TreeMap<>(); 
        for(ClassFrame f : framesToTest){
            ClassFrameGroundGeneralisation fr = new ClassFrameGroundGeneralisation(f);
            int specificity = this.getSpecificity(fr);
            this.specificity2frames.putIfAbsent(specificity, new HashSet<>());
            this.specificity2frames.get(specificity).add(fr);
        }
    }

    private int getSpecificity(ClassFrameGroundGeneralisation f){ 
        int specificity = 0;
        Map<SyntaxTree,Integer> tree2weight = f.getTrees();
        for(Map.Entry<SyntaxTree,Integer> entry : tree2weight.entrySet()){
            SyntaxTree t = entry.getKey();
            int w = entry.getValue();
            specificity += (w*getSpecificity(t));
        } 
        return specificity;
    }

    private int getSpecificity(SyntaxTree t){
        SimpleDirectedGraph<SyntaxNode,DefaultEdge> graph = t.getTree();
        return graph.vertexSet().size(); 
    } 
}

