/*
 * ============================================================================
 * Copyright © 2002-2021 by Thomas Thrien.
 * All Rights Reserved.
 * ============================================================================
 * Licensed to the public under the agreements of the GNU Lesser General Public
 * License, version 3.0 (the "License"). You may obtain a copy of the License at
 * http://www.gnu.org/licenses/lgpl.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

/**
 *  <p><i>JavaComposer</i> is a Java toolkit for the generation of Java source
 *  code. Basically, this is a fork of version&nbsp;1.11.1 of <i>JavaPoet</i>
 *  that was originally developed by Square, Inc. (see
 *  {@href #section_License below}.
 *  Refer to
 *  {@href https://github.com/square/javapoet}
 *  for the original software.</p>
 *  <p>The interfaces in this package are not meant to be implemented by the
 *  user; instead they provide several static methods that allow to obtain an
 *  instance of the respective implementation. According to this, the behaviour
 *  of the adapted version is not different from the original.</p>
 *  <p>Unless otherwise stated, {@code null} argument values will cause
 *  methods and constructors of all classes in this package to throw an
 *  {@link java.lang.Exception Exception},
 *  usually a
 *  {@link org.tquadrat.foundation.exception.NullArgumentException},
 *  but in some rare cases, it could be also a
 *  {@link java.lang.NullPointerException}.</p>
 *  <hr>
 *  <p>Source code generation can be useful when doing things such as
 *  annotation processing or interacting with metadata files (e.g., database
 *  schemas, protocol formats). By generating the code, you eliminate the need
 *  to write boilerplate while also keeping a single source of truth for the
 *  metadata.</p>
 *  <h2>Example</h2>
 *  <p>Here's a (boring) HelloWorld class:</p>
 *  <div class="source-container"><pre>  package com.example.helloworld;
 *
 *  public final class HelloWorld {
 *      public static void main(String[] args) {
 *          System.out.println("Hello, JavaComposer!");
 *      }
 *  }</pre></div>
 *  <p>And this is the (exciting) code to generate it with JavaComposer:</p>
 *  <div class="source-container"><pre>  MethodSpec main = MethodSpec.methodBuilder( "main" )
 *      .addModifiers( Modifier.PUBLIC, Modifier.STATIC )
 *      .returns( void.class )
 *      .addParameter( String[].class, "args" )
 *      .addStatement( "$T.out.println($S)", System.class, "Hello, JavaComposer!" )
 *      .build();
 *
 *  TypeSpec helloWorld = TypeSpec.classBuilder( "HelloWorld" )
 *      .addModifiers( Modifier.PUBLIC, Modifier.FINAL )
 *      .addMethod( main )
 *      .build();
 *
 *  JavaFile javaFile = JavaFile.builder( "com.example.helloworld", helloWorld )
 *      .layout( JavaFile.Layout.LAYOUT_JAVAPOET )
 *      .build();
 *
 *  javaFile.writeTo( System.out );</pre></div>
 *  <p>To declare the main method, we've created a
 *  {@link org.tquadrat.foundation.javacomposer.MethodSpec}
 *  &quot;main&quot; and configured it with <i>modifiers</i>,
 *  <i>return type</i>, <i>parameters</i> and <i>code statements</i>. We add
 *  that main method to a <code>HelloWorld</code> class, and then add that to a
 *  <code>HelloWorld.java</code> file.</p>
 *  <p>In this case we write the file to
 *  {@link java.lang.System#out System.out},
 *  but we could also get it as a string
 *  ({@link org.tquadrat.foundation.javacomposer.JavaFile#toString() JavaFile.toString()})
 *  or write it to the file system
 *  ({@link org.tquadrat.foundation.javacomposer.JavaFile#writeTo(java.io.File) JavaFile.writeTo()}).
 *  It can be used also directly as input to an instance of
 *  {@link javax.tools.JavaCompiler}
 *  ({@link org.tquadrat.foundation.javacomposer.JavaFile#toJavaFileObject}).
 *  The JavaDoc lists the complete JavaComposer API, which we explore
 *  below.</p>
 *
 *  <h2>Code &amp; Control Flow</h2>
 *  <p>Most of JavaComposer's API uses plain old immutable Java objects. There
 *  are also builders, method chaining and varargs to make the API friendly to
 *  use. JavaComposer offers models for classes &amp; interfaces
 *  ({@link org.tquadrat.foundation.javacomposer.TypeSpec}),
 *  fields
 *  ({@link org.tquadrat.foundation.javacomposer.FieldSpec}),
 *  methods &amp; constructors
 *  ({@link org.tquadrat.foundation.javacomposer.MethodSpec}),
 *  parameters
 *  ({@link org.tquadrat.foundation.javacomposer.ParameterSpec}),
 *  annotations
 *  ({@link org.tquadrat.foundation.javacomposer.AnnotationSpec})
 *  and lambdas
 *  ({@link org.tquadrat.foundation.javacomposer.LambdaSpec}).</p>
 *  <p>But the body of methods and constructors is not modelled. There's no
 *  expression class, no statement class or syntax tree nodes. Instead,
 *  JavaComposer uses strings for code blocks:</p>
 *  <div class="source-container"><pre>  MethodSpec main = MethodSpec.methodBuilder( "main" )
 *      .addCode( """
 *                int total = 0;
 *                for (int i = 0; i &lt; 10; i++) {
 *                    total += i;
 *                }
 *                """ )
 *      .build();</pre></div>
 *  <p>Which generates this:</p>
 *  <div class="source-container"><pre>  void main() {
 *      int total = 0;
 *      for (int i = 0; i &lt; 10; i++) {
 *          total += i;
 *      }
 *  }</pre></div>
 *  <p>The manual semicolons, line wrapping, and indentation are tedious and so
 *  JavaComposer offers APIs to make it easier. There's
 *  {@link org.tquadrat.foundation.javacomposer.MethodSpec.Builder#addStatement(String,Object[]) addStatement()}
 *  which takes care of semicolons and newline, and
 *  {@link org.tquadrat.foundation.javacomposer.MethodSpec.Builder#beginControlFlow(String,Object[]) beginControlFlow()}
 *  and
 *  {@link org.tquadrat.foundation.javacomposer.MethodSpec.Builder#endControlFlow() endControlFlow()}
 *  which are used together for braces, newlines, and indentation:</p>
 *  <div class="source-container"><pre>  MethodSpec main = MethodSpec.methodBuilder( "main" )
 *      .addStatement( "int total = 0" )
 *      .beginControlFlow( "for (int i = 0; i &lt; 10; i++)" )
 *      .addStatement( "total += i" )
 *      .endControlFlow()
 *      .build();</pre></div>
 *  <p>This example is lame because the generated code is constant! Suppose
 *  instead of just adding 0 to 10, we want to make the operation and range
 *  configurable. Here's a method that generates a method:</p>
 *  <div class="source-container"><pre>  private MethodSpec computeRange( String name, int from, int to, String op )
 *  {
 *      var retValue = MethodSpec.methodBuilder( name )
 *          .returns( int.class )
 *          .addStatement( "int result = 1" )
 *          .beginControlFlow( "for (int i = " + from + "; i &lt; " + to + "; i++)" )
 *          .addStatement( "result = result " + op + " i" )
 *          .endControlFlow()
 *          .addStatement( "return result" )
 *          .build();
 *
 *     //---* Done *----------------------------------------------------------
 *     return retValue;
 *  }   //  computeRange()</pre></div>
 *  <p>And here's what we get when we call
 *  <code>computeRange( "multiply10to20", 10, 20, "*" )</code>:</p>
 *  <div class="source-container"><pre>  int multiply10to20() {
 *      int result = 1;
 *      for (int i = 10; i &lt; 20; i++) {
 *          result = result * i;
 *      }
 *      return result;
 *  }</pre></div>
 *  <p>Methods generating methods! And since JavaComposer generates source
 *  instead of byte code, you can read through it to make sure it's right.</p>
 *
 *  <h2><code>$L</code> for Literals</h2>
 *  <p>The string-concatenation in calls to <code>beginControlFlow()</code> and
 *  <code>addStatement()</code> is distracting. Too many operators. To address
 *  this, JavaComposer offers a syntax inspired-by but incompatible with
 *  {@link java.lang.String#format(String,Object[])}.
 *  It accepts <code>$L</code> to emit a literal value in the output. This
 *  works just like
 *  {@link java.util.Formatter}'s
 *  <code>%s</code>:</p>
 *  <div class="source-container"><pre>  private MethodSpec computeRange( String name, int from, int to, String op )
 *  {
 *      var retValue = MethodSpec.methodBuilder( name )
 *          .returns( int.class )
 *          .addStatement( "int result = 0" )
 *          .beginControlFlow( "for (int i = $L; i &lt; $L; i++)", from, to )
 *          .addStatement( "result = result $L i", op )
 *          .endControlFlow()
 *          .addStatement( "return result" )
 *          .build();
 *
 *      //---* Done *----------------------------------------------------------
 *      return retValue;
 *  }   //  computeRange()</pre></div>
 *  <p>Literals are emitted directly to the output code with no escaping.
 *  Arguments for literals may be Strings, primitives, and a few JavaComposer
 *  types described below.</p>
 *
 *  <h2><code>$S</code> for Strings</h2>
 *  <p>When emitting code that includes string literals, we can use
 *  <code>$S</code> to emit a string, complete with wrapping quotation marks
 *  and escaping. Here's a program that emits three methods, each of which
 *  returns its own name:</p>
 *  <div class="source-container"><pre>  public static void main( String[] args ) throws Exception
 *  {
 *      TypeSpec helloWorld = TypeSpec.classBuilder( "HelloWorld" )
 *          .addModifiers( Modifier.PUBLIC, Modifier.FINAL )
 *          .addMethod( whatsMyName( "slimShady" ) )
 *          .addMethod( whatsMyName( "eminem" ) )
 *          .addMethod( whatsMyName( "marshallMathers" ) )
 *          .build();
 *
 *      JavaFile javaFile = JavaFile.builder( "com.example.helloworld", helloWorld )
 *          .layout( JavaFile.Layout.LAYOUT_JAVAPOET )
 *          .build();
 *
 *      javaFile.writeTo(System.out);
 *  }   //  main()
 *
 *  private static MethodSpec whatsMyName( String name )
 *  {
 *      var retValue MethodSpec.methodBuilder( name )
 *          .returns( String.class )
 *          .addStatement( "return $S", name )
 *          .build();
 *
 *      //---* Done *----------------------------------------------------------
 *      return retValue;
 *  }   //  whatsMyName()</pre></div>
 *  <p>In this case, using <code>$S</code> gives us quotation marks:</p>
 *  <div class="source-container"><pre>  public final class HelloWorld {
 *      String slimShady() {
 *          return "slimShady";
 *      }
 *
 *      String eminem() {
 *          return "eminem";
 *      }
 *
 *      String marshallMathers() {
 *          return "marshallMathers";
 *      }
 *  }</pre></div>
 *
 *  <h2><code>$T</code> for Types</h2>
 *  <p>We Java programmers love our types: they make our code easier to
 *  understand. And JavaComposer is on board. It has rich built-in support for
 *  types, including automatic generation of import statements. Just use
 *  <code>$T</code> to reference types:</p>
 *  <div class="source-container"><pre>  MethodSpec today = MethodSpec.methodBuilder( "today" )
 *      .returns( Date.class )
 *      .addStatement( "return new $T()", Date.class )
 *      .build();
 *
 *  TypeSpec helloWorld = TypeSpec.classBuilder( "HelloWorld" )
 *      .addModifiers( Modifier.PUBLIC, Modifier.FINAL )
 *      .addMethod( today )
 *      .build();
 *
 *  JavaFile javaFile = JavaFile.builder( "com.example.helloworld", helloWorld )
 *      .layout( JavaFile.Layout.LAYOUT_JAVAPOET )
 *      .build();
 *
 *  javaFile.writeTo( System.out );</pre></div>
 *  <p>That generates the following <code>*.java</code> file, complete with the
 *  necessary import:</p>
 *  <div class="source-container"><pre>  package com.example.helloworld;
 *
 *  import java.util.Date;
 *
 *  public final class HelloWorld {
 *      Date today() {
 *          return new Date();
 *      }
 *  }</pre></div>
 *  <p>We passed <code>Date.class</code> to reference a class that
 *  just-so-happens to be available when we're generating code. This doesn't
 *  need to be the case. Here's a similar example, but this one references a
 *  class that doesn't exist (yet):</p>
 *  <div class="source-container"><pre>  ClassName hoverboard = ClassName.get( "com.mattel", "Hoverboard" );
 *
 *  MethodSpec today = MethodSpec.methodBuilder( "tomorrow" )
 *      .returns( hoverboard )
 *      .addStatement( "return new $T()", hoverboard )
 *      .build();</pre></div>
 *  <p>And that not-yet-existent class is imported as well:</p>
 *  <div class="source-container"><pre>  package com.example.helloworld;
 *
 *  import com.mattel.Hoverboard;
 *
 *  public final class HelloWorld {
 *      Hoverboard tomorrow() {
 *          return new Hoverboard();
 *      }
 *  }</pre></div>
 *  <p>The <code>ClassName</code> type is very important, and you'll need it
 *  frequently when you're using JavaComposer. It can identify any declared
 *  class. Declared types are just the beginning of Java's rich type system: we
 *  also have arrays, parameterised types, wildcard types, and type variables.
 *  JavaComposer has classes for building each of these:</p>
 *  <div class="source-container"><pre>  ClassName hoverboard = ClassName.get( "com.mattel", "Hoverboard" );
 *  ClassName list = ClassName.get( "java.util", "List" );
 *  ClassName arrayList = ClassName.get( "java.util", "ArrayList" );
 *  TypeName listOfHoverboards = ParameterizedTypeName.get( list, hoverboard );
 *
 *  MethodSpec beyond = MethodSpec.methodBuilder( "beyond" )
 *      .returns( listOfHoverboards )
 *      .addStatement( "$T result = new $T&lt;&gt;()", listOfHoverboards, arrayList )
 *      .addStatement( "result.add(new $T())", hoverboard )
 *      .addStatement( "result.add(new $T())", hoverboard )
 *      .addStatement( "result.add(new $T())", hoverboard )
 *      .addStatement( "return result" )
 *      .build();</pre></div>
 *  <p>JavaComposer will decompose each type and import its components where
 *  possible:</p>
 *  <div class="source-container"><pre>  package com.example.helloworld;
 *
 *  import com.mattel.Hoverboard;
 *  import java.util.ArrayList;
 *  import java.util.List;
 *
 *  public final class HelloWorld {
 *      List&lt;Hoverboard&gt; beyond() {
 *          List&lt;Hoverboard&gt; result = new ArrayList &lt;&gt;();
 *          result.add(new Hoverboard());
 *          result.add(new Hoverboard());
 *          result.add(new Hoverboard());
 *          return result;
 *      }
 *  }</pre></div>
 *
 *  <h3>Import <code>static</code></h3>
 *  <p>JavaComposer also supports <code>import static</code>. It does it via
 *  explicitly collecting type member names. Let's enhance the previous example
 *  with some static sugar:</p>
 *  <div class="source-container"><pre>  &hellip;
 *
 *  ClassName namedBoards = ClassName.get( "com.mattel", "Hoverboard", "Boards" );
 *
 *  MethodSpec beyond = MethodSpec.methodBuilder( "beyond" )
 *      .returns( listOfHoverboards )
 *      .addStatement( "$T result = new $T&lt;&gt;()", listOfHoverboards, arrayList )
 *      .addStatement( "result.add($T.createNimbus(2000))", hoverboard )
 *      .addStatement( "result.add($T.createNimbus(\"2001\"))", hoverboard )
 *      .addStatement( "result.add($T.createNimbus($T.THUNDERBOLT))", hoverboard, namedBoards )
 *      .addStatement( "$T.sort(result)", Collections.class )
 *      .addStatement( "return result.isEmpty() ? $T.emptyList() : result", Collections.class )
 *      .build();
 *
 *  TypeSpec hello = TypeSpec.classBuilder( "HelloWorld" )
 *      .addMethod( beyond )
 *      .build();
 *
 *  JavaFile.builder( "com.example.helloworld", hello )
 *      .layout( JavaFile.Layout.LAYOUT_JAVAPOET )
 *      .addStaticImport( hoverboard, "createNimbus" )
 *      .addStaticImport( namedBoards, "*" )
 *      .addStaticImport( Collections.class, "*" )
 *      .build();</pre></div>
 *  <p>JavaComposer will first add your import static block to the file as
 *  configured, match and mangle all calls accordingly and also import all
 *  other types as needed:</p>
 *  <div class="source-container"><pre>  package com.example.helloworld;
 *
 *  import static com.mattel.Hoverboard.Boards.*;
 *  import static com.mattel.Hoverboard.createNimbus;
 *  import static java.util.Collections.*;
 *
 *  import com.mattel.Hoverboard;
 *  import java.util.ArrayList;
 *  import java.util.List;
 *
 *  class HelloWorld {
 *      List&lt;Hoverboard&gt; beyond() {
 *          List&lt;Hoverboard&gt; result = new ArrayList&lt;&gt;();
 *          result.add(createNimbus(2000));
 *          result.add(createNimbus("2001"));
 *          result.add(createNimbus(THUNDERBOLT));
 *          sort(result);
 *          return result.isEmpty() ? emptyList() : result;
 *      }
 *  }</pre></div>
 *
 *  <h3><code>$N</code> for Names</h3>
 *  <p>Generated code is often self-referential. Use <code>$N</code> to refer
 *  to another generated declaration by its name. Here's a method that calls
 *  another:</p>
 *  <div class="source-container"><pre>  public String byteToHex(int b) {
 *  char[] result = new char[2];
 *  result[0] = hexDigit((b &gt;&gt;&gt; 4) &amp; 0xf);
 *  result[1] = hexDigit(b &amp; 0xf);
 *  return new String(result);
 *  }
 *
 *  public char hexDigit(int i) {
 *  return (char) (i &lt; 10 ? i + '0' : i - 10 + 'a');
 *  }</pre></div>
 *  <p>When generating the code above, we pass the <code>hexDigit()</code>
 *  method as an argument to the <code>byteToHex()</code> method using
 *  <code>$N</code>:</p>
 *  <div class="source-container"><pre>  MethodSpec hexDigit = MethodSpec.methodBuilder( "hexDigit" )
 *      .addParameter( int.class, "i" )
 *      .returns( char.class )
 *      .addStatement( "return (char) (i &lt; 10 ? i + '0' : i - 10 + 'a')" )
 *      .build();
 *
 *  MethodSpec byteToHex = MethodSpec.methodBuilder( "byteToHex" )
 *      .addParameter( int.class, "b" )
 *      .returns( String.class )
 *      .addStatement( "char[] result = new char[2]" )
 *      .addStatement( "result[0] = $N((b &gt;&gt;&gt; 4) &amp; 0xf)", hexDigit )
 *      .addStatement( "result[1] = $N(b &amp; 0xf)", hexDigit )
 *      .addStatement( "return new String(result)" )
 *      .build();</pre></div>
 *
 *  <h2>Code block format strings</h2>
 *  <p>Code blocks may specify the values for their placeholders in a few ways.
 *  Only one style may be used for each operation on a code block.</p>
 *  <p>In each example, we generate code to say &quot;I ate 3 tacos&quot;.</p>
 *
 *  <h3>Relative Arguments</h3>
 *  <p>Pass an argument value for each placeholder in the format string to
 *  {@link org.tquadrat.foundation.javacomposer.CodeBlock.Builder#add(String,Object[]) CodeBlock.add()}.</p>
 *  <div class="source-container"><pre>  CodeBlock.builder().add( "I ate $L $L", 3, "tacos" );</pre></div>
 *
 *  <h3>Positional Arguments</h3>
 *  <p>Place an integer index (1-based) before the placeholder in the format
 *  string to specify which argument to use.</p>
 *  <div class="sourceContainer"><pre>  CodeBlock.builder().add( "I ate $2L $1L", "tacos", 3 );</pre></div>
 *
 *  <h3>Named Arguments</h3>
 *  <p>Use the syntax</p>
 *  <pre><code><b>$</b>&lt;<i>argumentName</i>&gt;<b>:</b>&lt;<i>X</i>&gt;</code></pre>
 *  <p>where <code><i>X</i></code> is the format character, and call
 *  {@link org.tquadrat.foundation.javacomposer.CodeBlock.Builder#addNamed(String,java.util.Map) CodeBlock.addNamed()}
 *  with a map containing all argument keys in the format string. Argument
 *  names use characters in a-z, A-Z, 0-9, and _, and must start with a
 *  lowercase character.</p>
 *  <div class="source-container"><pre>  Map&lt;String,Object&gt; map = Map.of( "food", "tacos", "count", 3 );
 *  CodeBlock.builder().addNamed( "I ate $count:L $food:L", map );</pre></div>
 *
 *  <h2>Methods</h2>
 *  <p>All of the above methods have a code body. Use
 *  {@link javax.lang.model.element.Modifier#ABSTRACT Modifier.ABSTRACT}
 *  to get a method without any body. This is only legal if the enclosing class
 *  is either abstract or an interface.
 *  <div class="source-container"><pre>  MethodSpec flux = MethodSpec.methodBuilder( "flux" )
 *      .addModifiers( Modifier.ABSTRACT, Modifier.PROTECTED )
 *      .build();
 *
 *  TypeSpec helloWorld = TypeSpec.classBuilder( "HelloWorld" )
 *      .addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT )
 *      .addMethod( flux )
 *      .build();</pre></div>
 *  <p>Which generates this:</p>
 *  <div class="source-container"><pre>public abstract class HelloWorld {
 *      protected abstract void flux();
 *  }</pre></div>
 *  <p>The other modifiers work where permitted. Note that when specifying
 *  modifiers, JavaComposer uses
 *  {@link javax.lang.model.element.Modifier},
 *  a class that is not available on Android. This limitation applies to
 *  code-generating-code only; the output code runs everywhere: JVMs, Android,
 *  and GWT.</p>
 *  <p>Methods also have parameters, exceptions, varargs, Javadoc comments,
 *  annotations, type variables, and a return type. All of these are configured
 *  with
 *  {@link org.tquadrat.foundation.javacomposer.MethodSpec.Builder MethodSpec.Builder}.</p>
 *
 *  <h2>Constructors</h2>
 *  <p><code><i>MethodSpec</i></code> is a slight misnomer; it can also be used
 *  for constructors:</p>
 *  <div class="source-container"><pre>  MethodSpec flux = MethodSpec.constructorBuilder()
 *      .addModifiers( Modifier.PUBLIC )
 *      .addParameter( String.class, "greeting" )
 *      .addStatement( "this.$N = $N", "greeting", "greeting" )
 *      .build();
 *
 *  TypeSpec helloWorld = TypeSpec.classBuilder( "HelloWorld" )
 *      .addModifiers( Modifier.PUBLIC )
 *      .addField( String.class, "greeting", Modifier.PRIVATE, Modifier.FINAL )
 *      .addMethod( flux )
 *      .build();</pre></div>
 *  <p>Which generates this:</p>
 *  <div class="source-container"><pre>  public class HelloWorld {
 *      private final String greeting;
 *
 *      public HelloWorld(String greeting) {
 *          this.greeting = greeting;
 *      }
 *  }</pre></div>
 *  <p>For the most part, constructors work just like methods. When emitting
 *  code, JavaComposer will place constructors before methods in the output
 *  file.</p>
 *
 *  <h2>Parameters</h2>
 *  <p>Declare parameters on methods and constructors with either
 *  {@link org.tquadrat.foundation.javacomposer.ParameterSpec#builder(org.tquadrat.foundation.javacomposer.TypeName,java.lang.CharSequence,javax.lang.model.element.Modifier[])}
 *  or
 *  {@link org.tquadrat.foundation.javacomposer.MethodSpec.Builder MethodSpec}'s
 *  convenience
 *  {@link org.tquadrat.foundation.javacomposer.MethodSpec.Builder#addParameter(org.tquadrat.foundation.javacomposer.TypeName,java.lang.String,javax.lang.model.element.Modifier[]) addParameter()}
 *  API:</p>
 *  <div class="source-container"><pre>  ParameterSpec android = ParameterSpec.builder( String.class, "android" )
 *      .addModifiers( Modifier.FINAL )
 *      .build();
 *
 *  MethodSpec welcomeOverlords = MethodSpec.methodBuilder( "welcomeOverlords" )
 *      .addParameter( android )
 *      .addParameter( String.class, "robot", Modifier.FINAL )
 *      .build();</pre></div>
 *  <p>Though the code above to generate android and robot parameters is
 *  different, the output is the same:</p>
 *  <div class="source-container"><pre>  void welcomeOverlords(final String android, final String robot) {
 *  }</pre></div>
 *  <p>The extended Builder form is mandatory when the parameter has
 *  annotations (such as <code>@Nullable</code>).</p>
 *
 *  <h2>Fields</h2>
 *  <p>Like parameters, fields can be created either with builders or by using
 *  convenient helper methods:</p>
 *  <div class="source-container"><pre>  FieldSpec android = FieldSpec.builder( String.class, "android" )
 *      .addModifiers( Modifier.PRIVATE, Modifier.FINAL )
 *      .build();
 *
 *  TypeSpec helloWorld = TypeSpec.classBuilder( "HelloWorld" )
 *      .addModifiers( Modifier.PUBLIC )
 *      .addField( android )
 *      .addField( String.class, "robot", Modifier.PRIVATE, Modifier.FINAL )
 *      .build();</pre></div>
 *  <p>Which generates:</p>
 *  <div class="source-container"><pre>  public class HelloWorld {
 *      private final String android;
 *
 *      private final String robot;
 *  }</pre></div>
 *  <p>The extended Builder form is necessary when a field has Javadoc
 *  comments, annotations, or a field initializer. Field initializers use the
 *  same <code>String.format()</code>-like syntax as the code blocks above:</p>
 *  <div class="source-container"><pre>  FieldSpec android = FieldSpec.builder( String.class, "android" )
 *      .addModifiers( Modifier.PRIVATE, Modifier.FINAL )
 *      .initializer( "$S + $L", "Lollipop v.", 5.0d )
 *      .build();</pre></div>
 *  <p>Which generates:</p>
 *  <div class="source-container"><pre>  private final String android = "Lollipop v." + 5.0;</pre></div>
 *  <p>The method
 *  {@link org.tquadrat.foundation.javacomposer.TypeSpec.Builder#addProperty(org.tquadrat.foundation.javacomposer.FieldSpec,boolean) TypeSpec.addProperty()}
 *  can be used to add getter and setter together with the field:</p>
 *  <div class="source-container"><pre> FieldSpec android = FieldSpec.builder( String.class, "android" )
 *      .addModifiers( Modifier.PRIVATE )
 *      .build();
 *
 *  TypeSpec helloWorld = TypeSpec.classBuilder( "HelloWorld" )
 *      .addModifiers( Modifier.PUBLIC )
 *      .addProperty( android, false )
 *      .build();</pre></div>
 *  <p>Which generates:</p>
 *  <div class="source-container"><pre>  public class HelloWorld {
 *      private String android;
 *
 *      public final String getAndroid() {
 *          return android;
 *      }
 *
 *      public final void setAndroid(String value) {
 *          android = value;
 *      }
 *  }</pre></div>
 *
 *  <h2>Interfaces</h2>
 *  <p>JavaComposer has no trouble with interfaces. Note that interface methods
 *  must always be <code>public abstract</code> and interface fields must
 *  always be <code>public static final</code>. These modifiers are necessary
 *  when defining the interface:</p>
 *  <div class="source-container"><pre>  TypeSpec helloWorld = TypeSpec.interfaceBuilder( "HelloWorld" )
 *      .addModifiers( Modifier.PUBLIC )
 *      .addField( FieldSpec.builder( String.class, "ONLY_THING_THAT_IS_CONSTANT" )
 *          .addModifiers( Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL )
 *          .initializer( "$S", "change" )
 *          .build() )
 *      .addMethod( MethodSpec.methodBuilder( "beep" )
 *          .addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT )
 *          .build() )
 *       .build();</pre></div>
 *  <p>But these modifiers are omitted when the code is generated. These are
 *  the defaults so we don't need to include them for <code>javac</code>'s
 *  benefit!</p>
 *  <div class="source-container"><pre>  public interface HelloWorld {
 *      String ONLY_THING_THAT_IS_CONSTANT = "change";
 *
 *      void beep();
 *  }</pre></div>
 *
 *  <h2>Enums</h2>
 *  <p>Use
 *  {@link org.tquadrat.foundation.javacomposer.TypeSpec#enumBuilder(java.lang.CharSequence) enumBuilder}
 *  to create the <code>enum</code> type, and
 *  {@link org.tquadrat.foundation.javacomposer.TypeSpec.Builder#addEnumConstant(java.lang.CharSequence) addEnumConstant()}
 *  for each value:</p>
 *  <div class="source-container"><pre>  TypeSpec helloWorld = TypeSpec.enumBuilder( "Roshambo" )
 *      .addModifiers( Modifier.PUBLIC )
 *      .addEnumConstant( "ROCK" )
 *      .addEnumConstant( "SCISSORS" )
 *      .addEnumConstant( "PAPER" )
 *      .build();</pre></div>
 *  <p>To generate this:</p>
 *  <div class="source-container"><pre>  public enum Roshambo {
 *      ROCK,
 *
 *      SCISSORS,
 *
 *      PAPER
 *  }</pre></div>
 *  <p>Fancy enums are supported, where the <code>enum</code> values override
 *  methods or call a superclass constructor. Here's a comprehensive
 *  example:</p>
 *  <div class="source-container"><pre>  TypeSpec helloWorld = TypeSpec.enumBuilder( "Roshambo" )
 *      .addModifiers( Modifier.PUBLIC )
 *      .addEnumConstant( "ROCK", TypeSpec.anonymousClassBuilder( "$S", "fist" )
 *          .addMethod( MethodSpec.methodBuilder( "toString" )
 *              .addAnnotation( Override.class )
 *              .addModifiers( Modifier.PUBLIC )
 *              .addStatement( "return $S", "avalanche!" )
 *              .returns( String.class )
 *              .build() )
 *          .build() )
 *      .addEnumConstant( "SCISSORS", TypeSpec.anonymousClassBuilder( "$S", "peace" )
 *          .build() )
 *      .addEnumConstant( "PAPER", TypeSpec.anonymousClassBuilder( "$S", "flat" )
 *          .build() )
 *      .addField( String.class, "handsign", Modifier.PRIVATE, Modifier.FINAL )
 *      .addMethod( MethodSpec.constructorBuilder()
 *          .addParameter( String.class, "handsign" )
 *          .addStatement( "this.$N = $N", "handsign", "handsign" )
 *          .build() )
 *      .build();</pre></div>
 *  <p>Which generates this:</p>
 *  <div class="sourceContainer"><pre>  public enum Roshambo {
 *      ROCK("fist") {
 *          &#64;Override
 *          public String toString() {
 *              return "avalanche!";
 *          }
 *      },
 *
 *      SCISSORS("peace"),
 *
 *      PAPER("flat");
 *
 *      private final String handsign;
 *
 *      Roshambo(String handsign) {
 *          this.handsign = handsign;
 *      }
 *  }</pre></div>
 *
 *  <h2>Anonymous Inner Classes</h2>
 *  <p>In the enum code above, we used
 *  {@link org.tquadrat.foundation.javacomposer.TypeSpec#anonymousClassBuilder(java.lang.String,java.lang.Object[]) TypeSpec.anonymousClassBuilder()}
 *  to create an anonymous inner class. This can also be used in code blocks.
 *  They are values that can be referenced with <code>$L</code>:</p>
 *  <div class="source-container"><pre>  TypeSpec comparator = TypeSpec.anonymousClassBuilder( "" )
 *      .addSuperinterface( ParameterizedTypeName.get( Comparator.class, String.class ) )
 *      .addMethod( MethodSpec.methodBuilder( "compare" )
 *          .addAnnotation( Override.class )#
 *          .addModifiers( Modifier.PUBLIC )#
 *          .addParameter( String.class, "a" )
 *          .addParameter( String.class, "b" )
 *          .returns( int.class )
 *          .addStatement( "return $N.length() - $N.length()", "a", "b" )
 *          .build() )
 *      .build();
 *
 *  TypeSpec helloWorld = TypeSpec.classBuilder( "HelloWorld" )
 *      .addMethod( MethodSpec.methodBuilder( "sortByLength" )
 *          .addParameter( ParameterizedTypeName.get( List.class, String.class ), "strings" )
 *          .addStatement( "$T.sort($N, $L)", Collections.class, "strings", comparator )
 *          .build() )
 *      .build();</pre></div>
 *  <p>This generates a method that contains a class that contains a
 *  method:</p>
 *  <div class="source-container"><pre>  void sortByLength(List&lt;String&gt; strings) {
 *      Collections.sort(strings, new Comparator&lt;String&gt;() {
 *          &#64;Override
 *          public int compare(String a, String b) {
 *              return a.length() - b.length();
 *          }
 *      });
 *  }</pre></div>
 *  <p>One particularly tricky part of defining anonymous inner classes is the
 *  arguments to the superclass constructor. In the above code we're passing
 *  the empty string for no arguments:
 *  <code>TypeSpec.anonymousClassBuilder(&nbsp;&quot;&quot;&nbsp;)</code>. To
 *  pass different parameters use JavaComposer's code block syntax with commas
 *  to separate arguments.</p>
 *
 *  <h2>Annotations</h2>
 *  <p>Simple annotations are easy:</p>
 *  <div class="source-container"><pre>  MethodSpec toString = MethodSpec.methodBuilder( "toString" )
 *      .addAnnotation( Override.class )
 *      .returns( String.class )
 *      .addModifiers( Modifier.PUBLIC )
 *      .addStatement( "return $S", "Hoverboard" )
 *      .build();</pre></div>
 *  <p>Which generates this method with an <code>&#64;Override</code>
 *  annotation:</p>
 *  <div class="source-container"><pre>  &#64;Override
 *  public String toString() {
 *      return "Hoverboard";
 *  }</pre></div>
 *  <p>Use
 *  {@link org.tquadrat.foundation.javacomposer.AnnotationSpec#builder(java.lang.Class) AnnotationSpec.builder()}
 *  to set properties on annotations:</p>
 *  <div class="source-container"><pre>  MethodSpec logRecord = MethodSpec.methodBuilder( "recordEvent" )
 *      .addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT )
 *      .addAnnotation( AnnotationSpec.builder( Headers.class )
 *          .addMember( "accept", "$S", "application/json; charset=utf-8" )
 *          .addMember( "userAgent", "$S", "Square Cash" )
 *          .build() )
 *      .addParameter( LogRecord.class, "logRecord" )
 *      .returns( LogReceipt.class )
 *      .build();</pre></div>
 *  <p>Which generates this annotation with <code>accept</code> and
 *  <code>userAgent</code> properties:</p>
 *  <div class="source-container"><pre>  &#64;Headers(
 *      accept = "application/json; charset=utf-8",
 *      userAgent = "Square Cash"
 *  )
 *  LogReceipt recordEvent(LogRecord logRecord);</pre></div>
 *  <p>When you get fancy, annotation values can be annotations themselves. Use
 *  <code>$L</code> for embedded annotations:</p>
 *  <div class="sourceContainer"><pre>MethodSpec logRecord = MethodSpec.methodBuilder( "recordEvent" )
 *      .addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT )
 *      .addAnnotation( AnnotationSpec.builder( HeaderList.class )
 *          .addMember( "value", "$L", AnnotationSpec.builder( Header.class )
 *              .addMember( "name", "$S", "Accept" )
 *              .addMember( "value", "$S", "application/json; charset=utf-8" )
 *              .build() )
 *          .addMember( "value", "$L", AnnotationSpec.builder( Header.class )
 *              .addMember( "name", "$S", "User-Agent" )
 *              .addMember( "value", "$S", "Square Cash" )
 *              .build() )
 *          .build() )
 *      .addParameter( LogRecord.class, "logRecord" )
 *      .returns( LogReceipt.class )
 *      .build();</pre></div>
 *  <p>Which generates this:</p>
 *  <div class="source-container"><pre> &#64;HeaderList({
 *      &#64;Header(name = "Accept", value = "application/json; charset=utf-8"),
 *      &#64;Header(name = "User-Agent", value = "Square Cash")
 *  })
 *  LogReceipt recordEvent(LogRecord logRecord);</pre></div>
 *  <p>Note that you can call
 *  {@link org.tquadrat.foundation.javacomposer.AnnotationSpec.Builder#addMember(java.lang.CharSequence,java.lang.String,java.lang.Object[]) addMember()}
 *  multiple times with the same property name to populate a list of values for
 *  that property.</p>
 *
 *  <h2>Javadoc</h2>
 *  <p>Fields, methods and types can be documented with Javadoc (in fact, they
 *  should be documented, even for generated code):</p>
 *  <div class="source-container"><pre>  MethodSpec dismiss = MethodSpec.methodBuilder( "dismiss" )
 *      .addJavadoc( """
 *                   Hides {&#64;code message} from the caller's history. Other
 *                   participants in the conversation will continue to see the
 *                   message in their own history unless they also delete it.
 *                   """ )
 *      .addJavadoc( "\n" )
 *      .addJavadoc( """
 *                   &lt;p&gt;Use {&#64;link #delete($T)} to delete the entire
 *                   conversation for all participants.
 *                   """, Conversation.class )
 *      .addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT )
 *      .addParameter( Message.class, "message" )
 *      .build();</pre></div>
 *  <p>Which generates this:</p>
 *  <div class="source-container"><pre>  &#47;**
 *   * Hides {&#64;code message} from the caller's history. Other
 *   * participants in the conversation will continue to see the
 *   * message in their own history unless they also delete it.
 *   *
 *   * &lt;p&gt;Use {&#64;link #delete(Conversation)} to delete the entire
 *   * conversation for all participants. *&#47;
 *   void dismiss(Message message);</pre></div>
 *   <p>Use <code>$T</code> when referencing types in JavaDoc to get automatic
 *   imports.</p>
 *
 *   <h2>Lambda</h2>
 *   <p>{@link org.tquadrat.foundation.javacomposer.LambdaSpec} provides a
 *   basic API for the generation of lambda expressions.</p>
 *   <p>The code</p>
 *   <div class="source-container"><pre>  ParameterSpec parameter = ParameterSpec.of( VOID, "s" );
 *   LambdaSpec lambda = LambdaSpec.builder()
 *      .addParameter( parameter )
 *      .addCode( "upperCase( $N )" , parameter )
 *      .build();
 *
 *      CodeBlock codeBlock = CodeBlock.of( "$T&lt;$T&gt; function = $L;", UnaryOperator.class, String.class, candidate );</pre></div>
 *  <p>generates</p>
 *  <div class="source-container"><pre>  java.util.function.UnaryOperator&lt;java.lang.String&gt; function = s -&gt; upperCase( s );</pre></div>.
 *
 *  <h2>Layout</h2>
 *  <p>You may have noticed that the layout for the samples of
 *  <i>generating</i> code looks different from that of <i>generated</i> code:
 *  the first was formatted like the code for the Foundation Library, while the
 *  layout for the second was that used by the original JavaPoet code.</p>
 *  <p>When generating a <code>*.java</code> file with
 *  {@link org.tquadrat.foundation.javacomposer.JavaFile},
 *  you can specify a layout by calling
 *  {@link org.tquadrat.foundation.javacomposer.JavaFile.Builder#layout(Layout) JavaFile.layout()}
 *  with the name of the desired layout.</p>
 *  <p>Currently, this implementation supports the following layouts:</p>
 *  <dl>
 *      <dt>{@link org.tquadrat.foundation.javacomposer.Layout#LAYOUT_JAVAPOET LAYOUT_JAVAPOET}</dt>
 *      <dd>The format as used by the original JavaPoet software from
 *      Square,Inc.</dd>
 *      <dt>{@link org.tquadrat.foundation.javacomposer.Layout#LAYOUT_FOUNDATION LAYOUT_FOUNDATION}</dt>
 *      <dd>The format as used by the Foundation Library</dd>
 *  </dl>
 *
 *  <h2>Debug Output</h2>
 *  <p>In particular when generating complex code, it is not immediately
 *  obvious which statement of the generator was responsible for which part of
 *  the output.</p>
 *  <p>To help with the debugging of generated code, several builder methods
 *  (see the list
 *  {@href #listofmethod below})
 *  will have an additional {@code boolean} argument {@code addDebugOutput}; if
 *  that is {@code true}, the respective output from that method is prepended
 *  with the name of the class and the line number where that method is
 *  called.</p>
 *
 *  <h3>{@anchor #listofmethods Methods with Debug Output}</h3>
 *  <ul>
 *      <li>{@link org.tquadrat.foundation.javacomposer.AnnotationSpec.Builder#addMember(CharSequence,boolean,String,Object[])}</li>
 *      <li>{@link org.tquadrat.foundation.javacomposer.CodeBlock#of(boolean,String,Object[])}</li>
 *      <li>{@link org.tquadrat.foundation.javacomposer.CodeBlock.Builder#add(boolean,String,Object[])}</li>
 *      <li>{@link org.tquadrat.foundation.javacomposer.CodeBlock.Builder#addNamed(boolean,String,java.util.Map)}</li>
 *      <li>{@link org.tquadrat.foundation.javacomposer.CodeBlock.Builder#addStatement(boolean,String,Object[])}</li>
 *      <li>{@link org.tquadrat.foundation.javacomposer.CodeBlock.Builder#beginControlFlow(boolean,String,Object[])}</li>
 *      <li>{@link org.tquadrat.foundation.javacomposer.CodeBlock.Builder#endControlFlow(boolean,String,Object[])}</li>
 *      <li>{@link org.tquadrat.foundation.javacomposer.CodeBlock.Builder#nextControlFlow(boolean,String,Object[])}</li>
 *      <li>{@link org.tquadrat.foundation.javacomposer.MethodSpec.Builder#addCode(boolean,String,Object[])}</li>
 *      <li>{@link org.tquadrat.foundation.javacomposer.MethodSpec.Builder#addNamedCode(boolean,String,java.util.Map)}</li>
 *      <li>{@link org.tquadrat.foundation.javacomposer.MethodSpec.Builder#addStatement(boolean,String,Object[])}</li>
 *      <li>{@link org.tquadrat.foundation.javacomposer.MethodSpec.Builder#beginControlFlow(boolean,String,Object[])}</li>
 *      <li>{@link org.tquadrat.foundation.javacomposer.MethodSpec.Builder#endControlFlow(boolean,String,Object[])}</li>
 *      <li>{@link org.tquadrat.foundation.javacomposer.MethodSpec.Builder#nextControlFlow(boolean,String,Object[])}</li>
 *  </ul>
 *
 *  <h2>{@anchor #section_License License}</h2>
 *  <p>The original code is
 *  Copyright&nbsp;&copy;&nbsp;2015&nbsp;Square,&nbsp;Inc.</p>
 *  <p>Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  not use this file except in compliance with the License. You may obtain a
 *  copy of the License at</p>
 *  <p style="text-align:center;">{@href http://www.apache.org/licenses/LICENSE-2.0}</p>
 *  <p>Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.</p>
 *  <p>The modification for the Foundation version and the amendments are
 *  Copyright&nbsp;&copy;&nbsp;2018&nbsp;by&nbsp;Thomas&nbsp;Thrien,&nbsp;tquadrat.org.</p>
 *
 *  @todo   task.list
 */

package org.tquadrat.foundation.javacomposer;

/*
 *  End of File
 */