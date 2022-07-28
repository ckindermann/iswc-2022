# ISWC 2022 - Submission

## Basic Usage

Download the latest release and compute regularities for a given ontology with

`java -jar release.jar ontology category output`

The input expects an OWL ontology, a regularity category (either `axiom` or `classframe`), and an output destination (a path to a folder).

## Output Description

All output is written to a new folder `output/ontology` where `output` is the given output destination and `ontology` is the input ontology.
The folder contains the following contents:

1. a folder `statistics` containing CSV files with data about regularities (`regularityStatistics`) and the Hasse diagram w.r.t. to the substructure relation (`hierarchyStatistics`)
2. a folder `instances` containing instances of regularities serialised as OWL ontologies
 - in the case of regularities for axioms, instances of a regularity are saved in a single ontology that is named after the ID of the regularity (note that an instance of a regularity is an axiom - so all instaces can be serialised in a single ontology)
 - in the case of regularities fof axioms, instances of a regularity are saved as ontologies in a folder that is named after the ID of the regularity (note that an instance of a regularity is a set of axioms - so all instances are are serialised in separate ontologies)
3. a folder `structures` that provides (abridged) example instances to quickly inspect modelling structures associated with a regularity (note that there is no custom serialisation format for modelling structures yet. Instead, we use example instances written in OWL functional style syntax with the implicit understanding that atomic entities would need to be replaced with a star. In the case of regularities for axioms, we indicate the repetition of an axiom structure with a number in square brackets.) 
4. a file `graph` that describes the Hasse diagram of the partially ordered set of regularities w.r.t. the respective substructure relation. A visualisation of the Hasse Diagram for interrelations of syntactic regularities can be generated with `dot -Tpng graph -o hasse.png`. The picture displays a graph in which nodes are labelled with two numbers. The number at the top is an ID for a regularity and the bottom number is the number of its elements. 
5. a file `roots` that lists regularity IDs that appear as root nodes in the aforementioned Hasse diagram  

## Materials

The release indcludes

- a jar file that can be executed from the command line.

- the list of ontologies indexed at BioPortal (created on February 3rd 2022) on which our empirical survey is based.
This file includes (amongst other things) ontology submission IDs, version information, release specification, and name abbreviations.

## Reproducibility

To reproduce the results presented in the paper, proceed as follows:

1. Acquire a new list of ontologies indexed at BioPortal (or use the exact same we provide as part of release `v0.0.1-alpha`)
2. Use the [BioPortal REST API](http://data.bioontology.org/documentation) to download a snapshot of BioPortal as described [here](https://zenodo.org/record/439510#.YnUWiFzMJhE)
3. Check which ontologies can be [loaded](https://github.com/supmaterial/iswc2022/blob/main/src/main/java/experiment/exp/corpus/LoadCheck.java#L50) with the OWLAPI
4. Exclude ontologies [without](https://github.com/supmaterial/iswc2022/blob/main/src/main/java/experiment/exp/ontology/Profiling.java#L28) class expression axioms
5. Run the regularity discovery executable (of release `v.0.0.1-alpha`) on the remaining ontologies
6. Analyse the data 
