/**
 * Copyright 2011-2015 DEIB - Politecnico di Milano
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Acknowledgements:
 * We would like to thank Davide Barbieri, Emanuele Della Valle,
 * Marco Balduini, Soheila Dehghanzadeh, Shen Gao, and
 * Daniele Dell'Aglio for the effort in the development of the software.
 *
 * This work is partially supported by
 * - the European LarKC project (FP7-215535) of DEIB, Politecnico di
 * Milano
 * - the ERC grant “Search Computing” awarded to prof. Stefano Ceri
 * - the European ModaClouds project (FP7-ICT-2011-8-318484) of DEIB,
 * Politecnico di Milano
 * - the IBM Faculty Award 2013 grated to prof. Emanuele Della Valle;
 * - the City Data Fusion for Event Management 2013 project funded
 * by EIT Digital of DEIB, Politecnico di Milano
 * - the Dynamic and Distributed Information Systems Group of the
 * University of Zurich;
 * - INSIGHT NUIG and Science Foundation Ireland (SFI) under grant
 * No. SFI/12/RC/2289
 */
package eu.larkc.csparql.core.new_parser;

import eu.larkc.csparql.core.engine.CsparqlEngine;
import eu.larkc.csparql.core.streams.formats.CSparqlQuery;


import java.util.ListIterator;
import java.util.Properties;
import java.util.Scanner;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.InfGraph;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;
import org.apache.jena.sparql.util.NodeUtils;
import org.apache.commons.io.FileUtils;
import org.apache.jena.ext.com.google.common.base.Predicate;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.sparql.modify.request.UpdateDataDelete;
import org.apache.jena.sparql.modify.request.UpdateDataInsert;
import org.apache.jena.sparql.modify.request.UpdateModify;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.update.Update;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.query.*;

public class FactTranslator {

	static String subject = null;
	static String predicate = null;
	static String object = null;
	static String _obsMolecule = null;
	static String _measMolecule = null;
	static String _prefix_ = "PREFIX  ";
	static String _prefix_uri = " <http://linkeddata.com/ontology#>\n";
	static String _prefix="lld:";
	static String transformedQuery = _prefix_+_prefix+_prefix_uri;
	static Boolean _flag = false;
	

	public static String transformMeasurementsAndObservations(String x){
		final String queryString =x;
		final Query query = QueryFactory.create(queryString);
		// transforming
		String final_query = transformQuery(query);
		return final_query;
	}

	private static String transformQuery(Query query) {

		ElementWalker.walk(query.getQueryPattern(), new ElementVisitorBase() {
			@Override
			public void visit(ElementPathBlock el) {
				ListIterator<TriplePath> it = el.getPattern().iterator();
				Set<TriplePath> hasObsList = new HashSet<TriplePath>();
				while (it.hasNext()) {

					final TriplePath tp = it.next();
					if (!(tp.getPredicate().toString().contains("result"))) {
						it.remove();
					}

					if (tp.getSubject().toString().contains("observation") &&  tp.getObject().toString().contains("??")){
						it.remove();
					}
					
					subject = tp.getSubject().toString();
					predicate = tp.getPredicate().toString();
					object = tp.getObject().toString();
					
					//System.out.println(subject);
					if (predicate.contains("result") || predicate.contains("procedure")
							|| predicate.contains("observedProperty") || predicate.contains("type")) {
						TriplePath tp_hasObs = new TriplePath(new Triple((Node) NodeFactory.createVariable(subject.replace("?", "") + "M"),	NodeFactory.createVariable(":hasObservation"), tp.getSubject()));

						if (!hasObsList.contains(tp_hasObs))
							hasObsList.add(tp_hasObs);
								
						if (predicate.contains("result")) {
							it.add(new TriplePath(new Triple((Node) NodeFactory.createVariable(subject.replace("?", "") + "M"),	tp.getPredicate(), (Node) NodeFactory.createVariable(object.replace("?", "") + "M"))));
						} else {
							it.add(new TriplePath(new Triple((Node) NodeFactory.createVariable(subject.replace("?", "") + "M"),	tp.getPredicate(), tp.getObject())));
						}
					} else if (predicate.contains("floatValue") || predicate.contains("uom")) {
						it.add(new TriplePath(new Triple((Node) NodeFactory.createVariable(subject.replace("?", "") + "M"),	tp.getPredicate(), tp.getObject())));
					} else {
						if (predicate.contains("generatedObservation")){
							it.add(new TriplePath(new Triple(tp.getSubject(), tp.getPredicate(), (Node) NodeFactory.createVariable(object.replace("?", "") + "M"))));
						}else{
						it.add(new TriplePath(new Triple(tp.getSubject(), tp.getPredicate(), tp.getObject())));
						}
					}
					
				}
				for (TriplePath t : hasObsList)
					it.add(t);
			}
		});

		ElementWalker.walk(query.getQueryPattern(), new ElementVisitorBase() {
			@Override
			public void visit(ElementPathBlock el) {
				List<TriplePath> it = el.getPattern().getList();
				final Set<TriplePath> subjects = new HashSet<TriplePath>(it);
				el.getPattern().getList().clear();
				el.getPattern().getList().addAll(subjects);
			}
		});
		
		if(query.toString().contains("hasObservation")){
			transformedQuery+=query.toString().replace("?:",_prefix);
		}else{
			transformedQuery=query.toString();
		}
		
		return transformedQuery;
	}

	public static void main(String[] args) {
		//String output = transformMeasurementsAndObservations(queryString);
		} 
	}