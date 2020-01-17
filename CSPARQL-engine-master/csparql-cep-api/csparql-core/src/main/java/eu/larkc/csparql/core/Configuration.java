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
package eu.larkc.csparql.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import eu.larkc.csparql.cep.api.CepEngine;
import eu.larkc.csparql.cep.api.CepQuery;
import eu.larkc.csparql.core.engine.CsparqlEngine;
import eu.larkc.csparql.core.engine.Reasoner;
import eu.larkc.csparql.core.new_parser.utility_files.Translator;
import eu.larkc.csparql.sparql.api.SparqlEngine;
import eu.larkc.csparql.sparql.api.SparqlQuery;

public class Configuration {

   // Singleton pattern implementation
   private static Configuration instance;
   
   private final String cepEngineName = "eu.larkc.csparql.cep.esper.EsperEngine";
   private final String reasonerName = "eu.larkc.csparql.core.engine.TransparentReasoner";
   private final String sparqlEngineName = "eu.larkc.csparql.sparql.jena.JenaEngine";
   private final String cepQueryName = "eu.larkc.csparql.cep.esper.EsperQuery";
   private final String sparqlQueryName = "eu.larkc.csparql.sparql.sesame.SesameQuery";
   private final String translatorName = "eu.larkc.csparql.core.new_parser.utility_files.CSparqlTranslator";

   public static Configuration getCurrentConfiguration() {
      if (Configuration.instance == null) {
         Configuration.instance = new Configuration();
      }

      return Configuration.instance;
   }

   public Reasoner createReasoner() {

      Class< ? > c = null;

      try {
         c = Class.forName(this.reasonerName);

         return (Reasoner) c.newInstance();

      } catch (final IllegalAccessException ex) {
         ex.printStackTrace();
         return null;
      } catch (final ClassNotFoundException ex) {
         ex.printStackTrace();
         return null;
      } catch (final InstantiationException ex) {
         ex.printStackTrace();
         return null;
      } catch (final IllegalArgumentException ex) {
         // TODO Auto-generated catch block
         ex.printStackTrace();
      }

      return null;
   }

   public Translator createTranslator(final CsparqlEngine engine) {

      Class< ? > c = null;

      try {
         c = Class.forName(this.translatorName);

         final Translator t = (Translator) c.newInstance();
         t.setEngine(engine);
         return t;

      } catch (final IllegalAccessException ex) {
         ex.printStackTrace();
         return null;
      } catch (final ClassNotFoundException ex) {
         ex.printStackTrace();
         return null;
      } catch (final InstantiationException ex) {
         ex.printStackTrace();
         return null;
      } catch (final IllegalArgumentException ex) {
         // TODO Auto-generated catch block
         ex.printStackTrace();
      }

      return null;
   }

   public CepQuery createCepQuery(final String command) {
      // TODO: Correct it, it should NEVER return null!
      Class< ? > c = null;
      CepQuery e = null;

      try {
         c = Class.forName(this.cepQueryName);

         final Constructor< ? >[] ctors = c.getConstructors();
         e = (CepQuery) ctors[0].newInstance(command);
         return e;

      } catch (final IllegalAccessException ex) {
         ex.printStackTrace();
         return null;
      } catch (final ClassNotFoundException ex) {
         ex.printStackTrace();
         return null;
      } catch (final InstantiationException ex) {
         ex.printStackTrace();
         return null;
      } catch (final IllegalArgumentException ex) {
         // TODO Auto-generated catch block
         ex.printStackTrace();
      } catch (final InvocationTargetException ex) {
         // TODO Auto-generated catch block
         ex.printStackTrace();
      }

      return null;
   }

   public SparqlQuery createSparqlQuery(final String command) {
      // TODO: Correct it, it should NEVER return null!
      Class< ? > c = null;
      SparqlQuery e = null;

      try {
         c = Class.forName(this.sparqlQueryName);

         final Constructor< ? >[] ctors = c.getConstructors();

         for (final Constructor< ? > cc : ctors) {

            if (cc.getParameterTypes().length == 1) {
               e = (SparqlQuery) cc.newInstance(command);
               return e;
            }
         }

      } catch (final IllegalAccessException ex) {
         ex.printStackTrace();
         return null;
      } catch (final ClassNotFoundException ex) {
         ex.printStackTrace();
         return null;
      } catch (final InstantiationException ex) {
         ex.printStackTrace();
         return null;
      } catch (final IllegalArgumentException ex) {
         // TODO Auto-generated catch block
         ex.printStackTrace();
      } catch (final InvocationTargetException ex) {
         // TODO Auto-generated catch block
         ex.printStackTrace();
      }

      return null;
   }

   public CepEngine createCepEngine() {
      // TODO: Correct it, it should NEVER return null!
      Class< ? > c = null;
      CepEngine e = null;

      try {
         c = Class.forName(this.cepEngineName);
         e = (CepEngine) c.newInstance();
         return e;
      } catch (final IllegalAccessException ex) {
         ex.printStackTrace();
         return null;
      } catch (final ClassNotFoundException ex) {
         ex.printStackTrace();
         return null;
      } catch (final InstantiationException ex) {
         ex.printStackTrace();
         return null;
      }
   }

   public SparqlEngine createSparqlEngine() {
      // TODO: Correct it, it should NEVER return null!
      Class< ? > c = null;
      SparqlEngine e = null;

      try {
         c = Class.forName(this.sparqlEngineName);
         e = (SparqlEngine) c.newInstance();
         return e;
      } catch (final IllegalAccessException ex) {
         ex.printStackTrace();
         return null;
      } catch (final ClassNotFoundException ex) {
         ex.printStackTrace();
         return null;
      } catch (final InstantiationException ex) {
         ex.printStackTrace();
         return null;
      }
   }

}
