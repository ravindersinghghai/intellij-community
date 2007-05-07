/*
 * Copyright 2000-2007 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.groovy.lang.completion;


import com.intellij.codeInsight.completion.CompletionData;
import com.intellij.codeInsight.completion.CompletionVariant;
import com.intellij.codeInsight.completion.WordCompletionData;
import com.intellij.psi.PsiElement;
import com.intellij.psi.filters.*;
import com.intellij.psi.filters.position.LeftNeighbour;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.plugins.groovy.lang.completion.filters.toplevel.PackageFilter;
import org.jetbrains.plugins.groovy.lang.completion.filters.toplevel.ImportFilter;
import org.jetbrains.plugins.groovy.lang.completion.filters.toplevel.ClassInterfaceEnumFilter;
import org.jetbrains.plugins.groovy.lang.completion.filters.control.ControlStructureFilter;
import org.jetbrains.plugins.groovy.lang.completion.filters.control.additional.CaseDefaultFilter;
import org.jetbrains.plugins.groovy.lang.completion.filters.control.additional.CatchFinallyFilter;
import org.jetbrains.plugins.groovy.lang.completion.filters.control.additional.ElseFilter;
import org.jetbrains.plugins.groovy.lang.completion.filters.types.BuiltInTypeFilter;
import org.jetbrains.plugins.groovy.lang.completion.filters.classdef.ExtendsFilter;
import org.jetbrains.plugins.groovy.lang.completion.filters.classdef.ImplementsFilter;
import org.jetbrains.plugins.groovy.lang.completion.filters.exprs.SimpleExpressionFilter;

/**
 * @author ilyas
 */
public class GroovyCompletionData extends CompletionData {

  public GroovyCompletionData() {
    registerAllCompletions();

  }

  private void registerAllCompletions1() {
    LeftNeighbour afterDotFilter = new LeftNeighbour(new TextFilter("."));
    CompletionVariant variant = new CompletionVariant(new NotFilter(afterDotFilter));

    variant.includeScopeClass(LeafPsiElement.class);
    variant.addCompletionFilterOnElement(TrueFilter.INSTANCE);
    String[] keywords = new String[]{
        "class", "interface", "enum",   // Types

        "extends", "implements",  // Other

        "true", "false", "null", "super", "new", "this", // Expressions

        "try", "while", "with", "switch", "for", "return", "break", "continue", "throw", "assert", // Control

        "finally", "catch", // Additional 1
        "case", "default", // Additional 2
        "else", // Additional 3

        "boolean", "byte", "char", "short", "int", "float", "long", "double", "any", // Built-in Types

        "static", "def", "void", "as",
        "private", "public", "protected", "transient", "native", "threadsafe", "synchronized", "volatile",
        "throws", "in", "instanceof",


    };


    variant.addCompletion(keywords);
    registerVariant(variant);
  }

  /**
   * Registers completions on top level of Groovy script file
   */
  private void registerAllCompletions() {
    registerPackageCompletion();
    registerImportCompletion();

    registerClassInterfaceEnumAnnotationCompletion();
    registerControlCompletion();
    registerSimpleExprsCompletion();
    registerBuiltInTypeCompletion();

  }


  private void registerPackageCompletion() {
    registerStandardCompletion(new PackageFilter(), "package");
  }

  private void registerClassInterfaceEnumAnnotationCompletion() {
    registerStandardCompletion(new ClassInterfaceEnumFilter(), "class", "interface", "enum");

    registerStandardCompletion(new ExtendsFilter(), "extends");
    registerStandardCompletion(new ImplementsFilter(), "implements");
}

  private void registerControlCompletion() {
    String[] controlKeywords = {"try", "while", "with", "switch", "for",
        "return", "break", "continue", "throw", "assert"};


    registerStandardCompletion(new ControlStructureFilter(), controlKeywords);
    registerStandardCompletion(new CaseDefaultFilter(), "case", "default");
    registerStandardCompletion(new CatchFinallyFilter(), "catch", "finally");
    registerStandardCompletion(new ElseFilter(), "else");


  }

  private void registerBuiltInTypeCompletion() {
    String[] builtInTypes = {"boolean", "byte", "char", "short", "int", "float", "long", "double", "any"};
    registerStandardCompletion(new BuiltInTypeFilter(), builtInTypes);
  }

  private void registerSimpleExprsCompletion(){
    String[] exprs = {"true", "false", "null", "super", "new", "this"};
    registerStandardCompletion(new SimpleExpressionFilter(), exprs);
  }


  private void registerImportCompletion() {
    registerStandardCompletion(new ImportFilter(), "import");
  }

  /**
   * Template to add all standard keywords complettions
   *
   * @param filter   - Semantic filter for given keywords
   * @param keywords - Keyword to be completed
   */
  private void registerStandardCompletion(ElementFilter filter, String... keywords) {
    LeftNeighbour afterDotFilter = new LeftNeighbour(new TextFilter("."));
    CompletionVariant variant = new CompletionVariant(new AndFilter(new NotFilter(afterDotFilter), filter));
    variant.includeScopeClass(LeafPsiElement.class);
    variant.addCompletionFilterOnElement(TrueFilter.INSTANCE);
    addCompletions(variant, keywords);
    registerVariant(variant);
  }


  public String findPrefix(PsiElement insertedElement, int offset) {
    return WordCompletionData.findPrefixSimple(insertedElement, offset);

  }

  /**
   * Adds all completion variants in sequence
   *
   * @param comps   Given completions
   * @param variant Variant for completions
   */
  private void addCompletions(CompletionVariant variant, String... comps) {
    for (String completion : comps) {
      variant.addCompletion(completion);
    }
  }


}
