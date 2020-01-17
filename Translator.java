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

public class Translator {

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
	

	public static String transformMeasurementsAndObservations(String x) throws FileNotFoundException {
		final String queryString =x;

		final Query query = QueryFactory.create(queryString);
		// transforming
		String final_query = transformQuery(query);
		return final_query;
		
		// deprecated code
		
		//System.out.println("/=============================== transformed query 1===============================/\n\n" + final_query);
       // final Query query1 = QueryFactory.create(final_query);
       // String final_query1 = transformQuery(query1);
       // QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query1 );
        
        
        		
      //  ResultSet results = qExe.execSelect();
        
		//System.out.println("/=============================== transformed query2===============================/\n\n" + query1);
		//ResultSetFormatter.out(System.out, results, query1) ;
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
		String fileName = args[0];

		System.out.println("Ignore the exception that will be printed below");
		// taking the file of files line by line and transform them.

			    	String content = null;
					String output = null;
					File queryFile = new File(fileName);
					FileReader reader = null;
					try {
						reader = new FileReader(queryFile);
						char[] chars = new char[(int) queryFile.length()];
						reader.read(chars);
						content = new String(chars);
						reader.close();
						output = transformMeasurementsAndObservations(content);
						
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							if(reader !=null){try {
								reader.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}}
							}
					// create output files at desired location
					File f = new File(System.getProperty("user.dir")+"/transOutputFiles/"+queryFile.getName());
				    if(!f.getParentFile().exists()){
				        f.getParentFile().mkdirs();
				    }
				    try {
				        f.createNewFile();
				        } catch (Exception e) {
				        e.printStackTrace();
				    }
				    BufferedWriter writer = null;
				    try
				    {
				        writer = new BufferedWriter( new FileWriter( f));
				        writer.write( output);
				    }
				    catch ( IOException e)
				    {
				    }
				    finally
				    {
				        try
				        {
				            if ( writer != null)
				            writer.close( );
				        }
				        catch ( IOException e)
				        {
				        }
				    }

		} 
	}