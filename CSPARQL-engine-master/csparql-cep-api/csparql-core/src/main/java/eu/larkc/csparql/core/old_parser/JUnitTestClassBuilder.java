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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.larkc.csparql.core.old_parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * @author Marco
 */
public class JUnitTestClassBuilder {

   // Variables
   private static final String negativePath = "src/negativeTests/";
   private static final String positivePath = "src/positiveTests/";
   private static final String headerPathTemplate = "src/templateJUnitTests/headerJUnitTemplate.txt";
   private static final String footerPathTemplate = "src/templateJUnitTests/footerJUnitTemplate.txt";
   private static final String negativePathTemplate = "src/templateJUnitTests/negativeJUnitTemplate.txt";
   private static final String positivePathTemplate = "src/templateJUnitTests/positiveJUnitTemplate.txt";
   private static final String classPathWrite = "test/querytester/JUnitTesterTest.java";

   public static void createJunitTestClass() {
      try {
         String temp;
         BufferedReader in = new BufferedReader(new FileReader(
               JUnitTestClassBuilder.headerPathTemplate));
         final BufferedWriter write = new BufferedWriter(new FileWriter(
               JUnitTestClassBuilder.classPathWrite));
         // **************** WRITE HEADER
         while ((temp = in.readLine()) != null) {
            write.append(temp);
            write.newLine();
            write.flush();
         }
         in.close();

         String[] list = null;
         String className = null;
         String fileName = null;
         File path = null;
         // **************** WRITE NEGATIVE TESTS
         write.append("//*************** NEGATIVE TESTS");
         write.newLine();
         write.flush();

         // Go throught every query file
         path = new File(JUnitTestClassBuilder.negativePath);
         list = path.list();
         for (int i = 0; i < list.length; i++) {
            fileName = list[i];

            if (fileName.contains(".DS_Store") || fileName.contains(".svn")) {
               continue;
            }

            className = list[i].replaceAll("-", "_");
            className = className.replaceAll("\\.rq", "");
            // Get template for negative test
            in = new BufferedReader(new FileReader(
                  JUnitTestClassBuilder.negativePathTemplate));
            while ((temp = in.readLine()) != null) {
               if (temp.contains("@nameFile@")) {
                  write.append(temp.replaceAll("@nameFile@", fileName));
               } else if (temp.contains("@nameClass@")) {
                  write.append(temp.replaceAll("@nameClass@", className));
               } else {
                  write.append(temp);
               }
               write.newLine();
               write.flush();
            }
            in.close();
         }
         // **************** WRITE POSITIVE TESTS
         write.append("//*************** POSITIVE TESTS");
         write.newLine();
         write.flush();

         // Go throught every query file
         path = new File(JUnitTestClassBuilder.positivePath);
         list = path.list();
         for (int i = 0; i < list.length; i++) {
            fileName = list[i];

            if (fileName.contains(".DS_Store") || fileName.contains(".svn")) {
               continue;
            }

            className = list[i].replaceAll("-", "_");
            className = className.replaceAll("\\.rq", "");
            // Get template for negative test
            in = new BufferedReader(new FileReader(
                  JUnitTestClassBuilder.positivePathTemplate));
            while ((temp = in.readLine()) != null) {
               if (temp.contains("@nameFile@")) {
                  write.append(temp.replaceAll("@nameFile@", fileName));
               } else if (temp.contains("@nameClass@")) {
                  write.append(temp.replaceAll("@nameClass@", className));
               } else {
                  write.append(temp);
               }
               write.newLine();
               write.flush();
            }
            in.close();
         }
         // **************** WRITE FOOTER
         in = new BufferedReader(new FileReader(JUnitTestClassBuilder.footerPathTemplate));
         while ((temp = in.readLine()) != null) {
            write.append(temp);
            write.newLine();
            write.flush();
         }
         write.close();
      } catch (final Exception e) {
         e.printStackTrace();
      }
   }
}
