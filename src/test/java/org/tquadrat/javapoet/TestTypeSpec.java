/*
 * ============================================================================
 *  Copyright © 2002-2021 by Thomas Thrien.
 *  All Rights Reserved.
 * ============================================================================
 *  Licensed to the public under the agreements of the GNU Lesser General Public
 *  License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *       http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 */

package org.tquadrat.javapoet;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;
import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventListener;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

import org.junit.Rule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.javacomposer.AnnotationSpec;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.Primitives;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.javacomposer.TypeVariableName;
import org.tquadrat.foundation.javacomposer.WildcardTypeName;
import org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl;
import org.tquadrat.foundation.testutil.TestBaseClass;
import org.tquadrat.javapoet.helper.CompilationRule;
import com.google.common.collect.ImmutableMap;

@SuppressWarnings( {"javadoc", "OverlyCoupledClass", "ClassWithTooManyMethods", "OverlyComplexClass"} )
@DisplayName( "TestTypeSpec" )
public final class TestTypeSpec extends TestBaseClass
{
        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    private static final String DONUTS_PACKAGE = "com.squareup.donuts";

    private static final String TACOS_PACKAGE = "com.squareup.tacos";

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    @SuppressWarnings( "InstanceVariableOfConcreteClass" )
    @Rule
    public final CompilationRule m_Compilation = new CompilationRule();

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    @Test
    public void addAnnotationDisallowsNull()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = NullArgumentException.class;
        try
        {
            composer.classBuilder( "Foo" ).addAnnotation( (AnnotationSpec) null );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        try
        {
            composer.classBuilder( "Foo" ).addAnnotation( (ClassName) null );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        try
        {
            composer.classBuilder( "Foo" ).addAnnotation( (Class<?>) null );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }

    @Test
    public void annotatedClass() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var someType = ClassName.from( TACOS_PACKAGE, "SomeType" );
        final var taco = composer.classBuilder( "Foo" )
            .addAnnotation( composer.annotationBuilder( ClassName.from( TACOS_PACKAGE, "Something" ) )
                .addMember( "hi", "$T.$N", someType, "FIELD" )
                .addMember( "hey", "$L", 12 )
                .addMember( "hello", "$S", "goodbye" )
                .build() )
            .addModifiers( Modifier.PUBLIC )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            @Something(
                hi = SomeType.FIELD,
                hey = 12,
                hello = "goodbye"
            )
            public class Foo {
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void annotatedField() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addField( composer.fieldBuilder( String.class, "thing", Modifier.PRIVATE, Modifier.FINAL )
                .addAnnotation( composer.annotationBuilder( ClassName.from( TACOS_PACKAGE, "JsonAdapter" ) )
                    .addMember( "value", "$T.class", ClassName.from( TACOS_PACKAGE, "Foo" ) )
                    .build() )
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.String;

            class Taco {
              @JsonAdapter(Foo.class)
              private final String thing;
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void annotatedParameters() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var service = composer.classBuilder( "Foo" )
            .addMethod( composer.constructorBuilder()
                .addModifiers( Modifier.PUBLIC )
                .addParameter( long.class, "id" )
                .addParameter( composer.parameterBuilder( String.class, "one" )
                    .addAnnotation( ClassName.from( TACOS_PACKAGE, "Ping" ) )
                    .build() )
                .addParameter( composer.parameterBuilder( String.class, "two" )
                    .addAnnotation( ClassName.from( TACOS_PACKAGE, "Ping" ) )
                    .build() )
                .addParameter( composer.parameterBuilder( String.class, "three" )
                    .addAnnotation( composer.annotationBuilder( ClassName.from( TACOS_PACKAGE, "Pong" ) )
                        .addMember( "value", "$S", "pong" )
                        .build() )
                    .build() )
                .addParameter( composer.parameterBuilder( String.class, "four" )
                    .addAnnotation( ClassName.from( TACOS_PACKAGE, "Ping" ) )
                    .build() )
                .addCode( "/* code snippets */\n" )
                .build() )
            .build();

        final var actual = toString( service );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.String;

            class Foo {
              public Foo(long id, @Ping String one, @Ping String two, @Pong("pong") String three,
                  @Ping String four) {
                /* code snippets */
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void annotation() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var annotation = composer.annotationTypeBuilder( "MyAnnotation" )
            .addModifiers( Modifier.PUBLIC )
            .addMethod( composer.methodBuilder( "test" )
                .addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT )
                .defaultValue( "$L", 0 )
                .returns( int.class )
                .build() )
            .build();

        final var actual = toString( annotation );
        final var expected =
            """
            package com.squareup.tacos;

            public @interface MyAnnotation {
              int test() default 0;
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void annotationDeclarationToString() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var type = composer.annotationTypeBuilder( "Taco" ).build();
        assertThat( type.toString() ).isEqualTo(
            """
            @interface Taco {
            }
            """ );
    }

    /**
     *  JavaPoet had a bug where annotations were preventing them from doing
     *  the right thing when resolving imports.
     *  https://github.com/square/javapoet/issues/422
     */
    @Test
    public void annotationsAndJavaLangTypes() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var freeRange = ClassName.from( "javax.annotation", "FreeRange" );
        final var taco = composer.classBuilder( "EthicalTaco" )
            .addField( ClassName.from( String.class )
                .annotated( composer.annotationBuilder( freeRange )
                    .build() ), "meat" )
            .build();

        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.String;
            import javax.annotation.FreeRange;

            class EthicalTaco {
              @FreeRange String meat;
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void annotationsInAnnotations() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var beef = ClassName.from( TACOS_PACKAGE, "Beef" );
        final var chicken = ClassName.from( TACOS_PACKAGE, "Chicken" );
        final var option = ClassName.from( TACOS_PACKAGE, "Option" );
        final var mealDeal = ClassName.from( TACOS_PACKAGE, "MealDeal" );
        final var menu = composer.classBuilder( "Menu" )
            .addAnnotation( composer.annotationBuilder( mealDeal )
                .addMember( "price", "$L", 500 )
                .addMember( "options", "$L", composer.annotationBuilder( option )
                    .addMember( "name", "$S", "taco" )
                    .addMember( "meat", "$T.class", beef )
                    .build() )
                .addMember( "options", "$L", composer.annotationBuilder( option )
                    .addMember( "name", "$S", "quesadilla" )
                    .addMember( "meat", "$T.class", chicken )
                    .build() )
                .build() )
            .build();
        final var actual = toString( menu );
        final var expected =
            """
            package com.squareup.tacos;

            @MealDeal(
                price = 500,
                options = {
                    @Option(name = "taco", meat = Beef.class),
                    @Option(name = "quesadilla", meat = Chicken.class)
                }
            )
            class Menu {
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void annotationToString() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var annotation = composer.annotationBuilder( SuppressWarnings.class )
            .addMember( "value", "$S", "unused" )
            .build();
        assertThat( annotation.toString() ).isEqualTo(
            """
            @java.lang.SuppressWarnings("unused")\
            """ );
    }

    @Test
    public void annotationWithFields()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var field = composer.fieldBuilder( int.class, "FOO" )
            .addModifiers( Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL ).initializer( "$L", 101 )
            .build();

        final var anno = composer.annotationTypeBuilder( "Anno" )
            .addField( field ).build();

        final var actual = toString( anno );
        final var expected =
            """
            package com.squareup.tacos;

            @interface Anno {
              int FOO = 101;
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void anonymousClassToString() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var type = composer.anonymousClassBuilder( EMPTY_STRING )
            .addSuperinterface( Runnable.class )
            .addMethod( composer.methodBuilder( "run" )
                .addAnnotation( Override.class )
                .addModifiers( Modifier.PUBLIC )
                .build() )
            .build();
        final var actual = type.toString();
        final var expected =
            """
            new java.lang.Runnable() {
              @java.lang.Override
              public void run() {
              }
            }""";
        assertEquals( expected, actual );
    }

    @Test
    public void anonymousInnerClass() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var foo = ClassName.from( TACOS_PACKAGE, "Foo" );
        final var bar = ClassName.from( TACOS_PACKAGE, "Bar" );
        final var thingThang = ClassName.from( TACOS_PACKAGE, "Thing", "Thang" );
        final TypeName thingThangOfFooBar = ParameterizedTypeName.from( thingThang, foo, bar );
        final var thung = ClassName.from( TACOS_PACKAGE, "Thung" );
        final var simpleThung = ClassName.from( TACOS_PACKAGE, "SimpleThung" );
        final TypeName thungOfSuperBar = ParameterizedTypeName.from( thung, WildcardTypeName.supertypeOf( bar ) );
        final TypeName thungOfSuperFoo = ParameterizedTypeName.from( thung, WildcardTypeName.supertypeOf( foo ) );
        final TypeName simpleThungOfBar = ParameterizedTypeName.from( simpleThung, bar );

        final var thungParameter = composer.parameterBuilder( thungOfSuperFoo, "thung" )
            .addModifiers( Modifier.FINAL )
            .build();
        final var aSimpleThung = composer.anonymousClassBuilder( composer.codeBlockOf( "$N", thungParameter ) )
            .superclass( simpleThungOfBar )
            .addMethod( composer.methodBuilder( "doSomething" )
                .addAnnotation( Override.class )
                .addModifiers( Modifier.PUBLIC )
                .addParameter( bar, "bar" )
                .addCode( "/* code snippets */\n" )
                .build() )
            .build();
        final var aThingThang = composer.anonymousClassBuilder( EMPTY_STRING )
            .superclass( thingThangOfFooBar )
            .addMethod( composer.methodBuilder( "call" )
                .addAnnotation( Override.class )
                .addModifiers( Modifier.PUBLIC )
                .returns( thungOfSuperBar )
                .addParameter( thungParameter )
                .addCode( "return $L;\n", aSimpleThung )
                .build() )
            .build();
        final var taco = composer.classBuilder( "Taco" )
            .addField( composer.fieldBuilder( thingThangOfFooBar, "NAME" )
                .addModifiers( Modifier.STATIC, Modifier.FINAL, Modifier.FINAL )
                .initializer( "$L", aThingThang )
                .build() )
            .build();

        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Override;

            class Taco {
              static final Thing.Thang<Foo, Bar> NAME = new Thing.Thang<Foo, Bar>() {
                @Override
                public Thung<? super Bar> call(final Thung<? super Foo> thung) {
                  return new SimpleThung<Bar>(thung) {
                    @Override
                    public void doSomething(Bar bar) {
                      /* code snippets */
                    }
                  };
                }
              };
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void arrayType()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addField( int [].class, "ints" )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            class Taco {
              int[] ints;
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void basic() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addMethod( composer.methodBuilder( "toString" )
                .addAnnotation( Override.class )
                .addModifiers( Modifier.PUBLIC, Modifier.FINAL )
                .returns( String.class )
                .addCode( "return $S;\n", "taco" )
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Override;
            import java.lang.String;

            class Taco {
              @Override
              public final String toString() {
                return "taco";
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void classCannotHaveDefaultMethods() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = IllegalStateException.class;
        try
        {
            composer.classBuilder( "Tacos" )
                .addMethod( composer.methodBuilder( "test" )
                    .addModifiers( Modifier.PUBLIC, Modifier.DEFAULT )
                    .returns( int.class )
                    .addCode( composer.codeBlockBuilder()
                        .addStatement( "return 0" )
                        .build() )
                    .build() )
                .build();
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }

    @Test
    public void classCannotHaveDefaultValueForMethod() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = IllegalStateException.class;
        try
        {
            composer.classBuilder( "Tacos" )
                .addMethod( composer.methodBuilder( "test" )
                    .addModifiers( Modifier.PUBLIC )
                    .defaultValue( "0" )
                    .returns( int.class )
                    .build() )
                .build();
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }

    @Test
    public void classImplementsExtends() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = ClassName.from( TACOS_PACKAGE, "Taco" );
        final var food = ClassName.from( "com.squareup.tacos", "Food" );
        final var typeSpec = composer.classBuilder( "Taco" )
            .addModifiers( Modifier.ABSTRACT )
            .superclass( ParameterizedTypeName.from( ClassName.from( AbstractSet.class ), food ) )
            .addSuperinterface( Serializable.class )
            .addSuperinterface( ParameterizedTypeName.from( ClassName.from( Comparable.class ), taco ) )
            .build();
        assertThat( toString( typeSpec ) ).isEqualTo(
            """
            package com.squareup.tacos;

            import java.io.Serializable;
            import java.lang.Comparable;
            import java.util.AbstractSet;

            abstract class Taco extends AbstractSet<Food> implements Serializable, Comparable<Taco> {
            }
            """ );
    }

    @Test
    public void classImplementsNestedClass() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var outer = ClassName.from( TACOS_PACKAGE, "Outer" );
        final var inner = outer.nestedClass( "Inner" );
        final var callable = ClassName.from( Callable.class );
        final var typeSpec = composer.classBuilder( "Outer" )
            .superclass( ParameterizedTypeName.from( callable, inner ) )
            .addType( composer.classBuilder( "Inner" )
                .addModifiers( Modifier.STATIC )
                .build() )
            .build();

        final var actual = toString( typeSpec );
        final var expected =
            """
            package com.squareup.tacos;

            import java.util.concurrent.Callable;

            class Outer extends Callable<Outer.Inner> {
              static class Inner {
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "OptionalGetWithoutIsPresent" )
    @Test
    public void classNameFactories()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var className = ClassName.from( "com.example", "Example" );
        assertThat( composer.classBuilder( className ).build().name().get() ).isEqualTo( "Example" );
        assertThat( composer.interfaceBuilder( className ).build().name().get() ).isEqualTo( "Example" );
        assertThat( composer.enumBuilder( className ).addEnumConstant( "A" ).build().name().get() ).isEqualTo( "Example" );
        assertThat( composer.annotationTypeBuilder( className ).build().name().get() ).isEqualTo( "Example" );
    }

    @Test
    public void classToString() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var type = composer.classBuilder( "Taco" ).build();
        assertThat( type.toString() ).isEqualTo(
            """
            class Taco {
            }
            """ );
    }

    @Test
    @Deprecated
    public void codeBlockAddStatementOfCodeBlockToString() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var contents = composer.codeBlockOf( "$T $N = $S.substring(0, 3)", String.class, "s", "taco" );
        final var statement = composer.codeBlockBuilder()
            .addStatement( contents )
            .build();
        assertThat( statement.toString() ).isEqualTo(
            """
             java.lang.String s = "taco".substring(0, 3);
             """ );
    }

    @Test
    public void codeBlocks() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var ifBlock = composer.codeBlockBuilder()
            .beginControlFlow( "if (!a.equals(b))" )
            .addStatement( "return i" )
            .endControlFlow()
            .build();
        final var methodBody = composer.codeBlockBuilder()
            .addStatement( "$T size = $T.min(listA.size(), listB.size())", int.class, Math.class )
            .beginControlFlow( "for ($T i = 0; i < size; i++)", int.class )
            .addStatement( "$T $N = $N.get(i)", String.class, "a", "listA" )
            .addStatement( "$T $N = $N.get(i)", String.class, "b", "listB" )
            .add( "$L", ifBlock )
            .endControlFlow()
            .addStatement( "return size" )
            .build();
        final var fieldBlock = composer.codeBlockBuilder()
            .add( "$>$>" )
            .add( "\n$T.<$T, $T>builder()$>$>", ImmutableMap.class, String.class, String.class )
            .add( "\n.add($S, $S)", '\'', "&#39;" )
            .add( "\n.add($S, $S)", '&', "&amp;" )
            .add( "\n.add($S, $S)", '<', "&lt;" )
            .add( "\n.add($S, $S)", '>', "&gt;" )
            .add( "\n.build()$<$<" )
            .add( "$<$<" )
            .build();
        final var escapeHtml = composer.fieldBuilder( ParameterizedTypeName.from( Map.class, String.class, String.class ), "ESCAPE_HTML" )
            .addModifiers( Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL )
            .initializer( fieldBlock )
            .build();
        final var util = composer.classBuilder( "Util" )
            .addField( escapeHtml )
            .addMethod( composer.methodBuilder( "commonPrefixLength" )
                .returns( int.class )
                .addParameter( ParameterizedTypeName.from( List.class, String.class ), "listA" )
                .addParameter( ParameterizedTypeName.from( List.class, String.class ), "listB" )
                .addCode( methodBody )
                .build() )
            .build();
        final var actual = toString( util );
        final var expected =
            """
            package com.squareup.tacos;

            import com.google.common.collect.ImmutableMap;
            import java.lang.Math;
            import java.lang.String;
            import java.util.List;
            import java.util.Map;

            class Util {
              private static final Map<String, String> ESCAPE_HTML =\s
                  ImmutableMap.<String, String>builder()
                      .add("'", "&#39;")
                      .add("&", "&amp;")
                      .add("<", "&lt;")
                      .add(">", "&gt;")
                      .build();

              int commonPrefixLength(List<String> listA, List<String> listB) {
                int size = Math.min(listA.size(), listB.size());
                for (int i = 0; i < size; i++) {
                  String a = listA.get(i);
                  String b = listB.get(i);
                  if (!a.equals(b)) {
                    return i;
                  }
                }
                return size;
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void codeBlockToString() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var codeBlock = composer.codeBlockBuilder()
            .addStatement( "$T $N = $S.substring(0, 3)", String.class, "s", "taco" )
            .build();
        assertThat( codeBlock.toString() ).isEqualTo( "java.lang.String s = \"taco\".substring(0, 3);\n" );
    }

    @Test
    public void constructorToString() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var constructor = composer.constructorBuilder()
            .addModifiers( Modifier.PUBLIC )
            .addParameter( ClassName.from( TACOS_PACKAGE, "Taco" ), "taco" )
            .addStatement( "this.$N = $N", "taco", "taco" )
            .build();
        final var actual = constructor.toString();
        final var expected =
            """
            public Constructor(com.squareup.tacos.Taco taco) {
              this.taco = taco;
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void defaultModifiersForInterfaceMembers() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.interfaceBuilder( "Taco" )
            .addField( composer.fieldBuilder( String.class, "SHELL" )
                .addModifiers( Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL )
                .initializer( "$S", "crunchy corn" )
                .build() )
            .addMethod( composer.methodBuilder( "fold" )
                .addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT )
                .build() ).addType( composer.classBuilder( "Topping" )
                .addModifiers( Modifier.PUBLIC, Modifier.STATIC )
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.String;

            interface Taco {
              String SHELL = "crunchy corn";

              void fold();

              class Topping {
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void defaultModifiersForMemberInterfacesAndEnums() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addType( composer.classBuilder( "Meat" )
                .addModifiers( Modifier.STATIC )
                .build() )
            .addType( composer.interfaceBuilder( "Tortilla" )
                .addModifiers( Modifier.STATIC )
                .build() )
            .addType( composer.enumBuilder( "Topping" )
                .addModifiers( Modifier.STATIC )
                .addEnumConstant( "SALSA" )
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            class Taco {
              static class Meat {
              }

              interface Tortilla {
              }

              enum Topping {
                SALSA
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void doubleFieldInitialization()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = IllegalStateException.class;
        try
        {
            composer.fieldBuilder( String.class, "listA" )
                .initializer( "foo" )
                .initializer( "bar" )
                .build();
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        try
        {
            composer.fieldBuilder( String.class, "listA" )
                .initializer( composer.codeBlockBuilder()
                    .add( "foo" )
                    .build() )
                .initializer( composer.codeBlockBuilder()
                    .add( "bar" )
                    .build() )
                .build();
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }

    @Test
    public void doWhile() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addMethod( composer.methodBuilder( "loopForever" )
                .beginControlFlow( "do" )
                .addStatement( "$T.out.println($S)", System.class, "hello" )
                .endControlFlow( "while (5 < 6)" )
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.System;

            class Taco {
              void loopForever() {
                do {
                  System.out.println("hello");
                } while (5 < 6);
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void elseIf() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addMethod( composer.methodBuilder( "choices" )
                .beginControlFlow( "if (5 < 4) " )
                .addStatement( "$T.out.println($S)", System.class, "wat" )
                .nextControlFlow( "else if (5 < 6)" )
                .addStatement( "$T.out.println($S)", System.class, "hello" )
                .endControlFlow()
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.System;

            class Taco {
              void choices() {
                if (5 < 4)  {
                  System.out.println("wat");
                } else if (5 < 6) {
                  System.out.println("hello");
                }
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void enumConstantsRequired() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = IllegalArgumentException.class;
        try
        {
            composer.enumBuilder( "Roshambo" ).build();
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }

    @Test
    public void enumImplements() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var typeSpec = composer.enumBuilder( "Food" )
            .addSuperinterface( Serializable.class )
            .addSuperinterface( Cloneable.class )
            .addEnumConstant( "LEAN_GROUND_BEEF" )
            .addEnumConstant( "SHREDDED_CHEESE" ).build();
        final var actual = toString( typeSpec );
        final var expected =
            """
            package com.squareup.tacos;

            import java.io.Serializable;
            import java.lang.Cloneable;

            enum Food implements Serializable, Cloneable {
              LEAN_GROUND_BEEF,

              SHREDDED_CHEESE
            }
            """;
        assertEquals( expected, actual );
    }

    /**
     *  https://github.com/square/javapoet/issues/193
     */
    @Test
    public void enumsMayDefineAbstractMethods() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var roshambo = composer.enumBuilder( "Tortilla" )
            .addModifiers( Modifier.PUBLIC )
            .addEnumConstant( "CORN", composer.anonymousClassBuilder( "" )
                .addMethod( composer.methodBuilder( "fold" )
                    .addAnnotation( Override.class )
                    .addModifiers( Modifier.PUBLIC )
                    .build() )
                .build() )
            .addMethod( composer.methodBuilder( "fold" )
                .addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT )
                .build() )
            .build();
        final var actual = toString( roshambo );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Override;

            public enum Tortilla {
              CORN {
                @Override
                public void fold() {
                }
              };

              public abstract void fold();
            }
            """;
        assertEquals( expected, actual );
    }

    /**
     *  https://github.com/square/javapoet/issues/253
     */
    @Test
    public void enumWithAnnotatedValues() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var roshambo = composer.enumBuilder( "Roshambo" )
            .addModifiers( Modifier.PUBLIC )
            .addEnumConstant( "ROCK", composer.anonymousClassBuilder( "" )
                .addAnnotation( Deprecated.class )
                .build() )
            .addEnumConstant( "PAPER" )
            .addEnumConstant( "SCISSORS" )
            .build();
        final var actual = toString( roshambo );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Deprecated;

            public enum Roshambo {
              PAPER,

              @Deprecated
              ROCK,

              SCISSORS
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void enumWithMembersButNoConstructorCall() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var roshambo = composer.enumBuilder( "Roshambo" )
            .addEnumConstant( "SPOCK", composer.anonymousClassBuilder( EMPTY_STRING )
                .addMethod( composer.methodBuilder( "toString" )
                    .addAnnotation( Override.class )
                    .addModifiers( Modifier.PUBLIC )
                    .returns( String.class )
                    .addCode( "return $S;\n", "west side" )
                    .build() )
                .build() )
            .build();
        final var actual = toString( roshambo );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Override;
            import java.lang.String;

            enum Roshambo {
              SPOCK {
                @Override
                public String toString() {
                  return "west side";
                }
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void enumWithSubclassing() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var roshambo = composer.enumBuilder( "Roshambo" )
            .addModifiers( Modifier.PUBLIC )
            .addEnumConstant( "ROCK", composer.anonymousClassBuilder( EMPTY_STRING )
                .addJavadoc( "Avalanche!\n" )
                .build() )
            .addEnumConstant( "PAPER", composer.anonymousClassBuilder( "$S", "flat" )
                .addMethod( composer.methodBuilder( "toString" )
                    .addAnnotation( Override.class )
                    .addModifiers( Modifier.PUBLIC )
                    .returns( String.class )
                    .addCode( "return $S;\n", "paper airplane!" )
                    .build() )
                .build() )
            .addEnumConstant( "SCISSORS", composer.anonymousClassBuilder( "$S", "peace sign" )
                .build() )
            .addField( String.class, "handPosition", Modifier.PRIVATE, Modifier.FINAL )
            .addMethod( composer.constructorBuilder()
                .addParameter( String.class, "handPosition" )
                .addCode( "this.handPosition = handPosition;\n" )
                .build() )
            .addMethod( composer.constructorBuilder()
                .addCode( "this($S);\n", "fist" )
                .build() )
            .build();
        final var actual = toString( roshambo );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Override;
            import java.lang.String;

            public enum Roshambo {
              PAPER("flat") {
                @Override
                public String toString() {
                  return "paper airplane!";
                }
              },

              /**
               * Avalanche!
               */
              ROCK,

              SCISSORS("peace sign");

              private final String handPosition;

              Roshambo(String handPosition) {
                this.handPosition = handPosition;
              }

              Roshambo() {
                this("fist");
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void equalsAndHashCode()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        var a = composer.interfaceBuilder( "taco" )
            .build();
        var b = composer.interfaceBuilder( "taco" )
            .build();
        assertThat( a.equals( b ) ).isTrue();
        assertThat( a.hashCode() ).isEqualTo( b.hashCode() );
        a = composer.classBuilder( "taco" )
            .build();
        b = composer.classBuilder( "taco" )
            .build();
        assertThat( a.equals( b ) ).isTrue();
        assertThat( a.hashCode() ).isEqualTo( b.hashCode() );
        a = composer.enumBuilder( "taco" )
            .addEnumConstant( "SALSA" )
            .build();
        b = composer.enumBuilder( "taco" )
            .addEnumConstant( "SALSA" )
            .build();
        assertThat( a.equals( b ) ).isTrue();
        assertThat( a.hashCode() ).isEqualTo( b.hashCode() );
        a = composer.annotationTypeBuilder( "taco" )
            .build();
        b = composer.annotationTypeBuilder( "taco" )
            .build();
        assertThat( a.equals( b ) ).isTrue();
        assertThat( a.hashCode() ).isEqualTo( b.hashCode() );
    }

    @Test
    public void fieldToString() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var field = composer.fieldBuilder( String.class, "s", Modifier.FINAL )
            .initializer( "$S.substring(0, 3)", "taco" )
            .build();
        assertThat( field.toString() ).isEqualTo( "final java.lang.String s = \"taco\".substring(0, 3);\n" );
    }

    @SuppressWarnings( {"SameParameterValue", "unused"} )
    private final TypeElement getElement( final Class<?> clazz )
    {
        Elements elements = null;
        try
        {
            elements = m_Compilation.getElements();
        }
        catch( @SuppressWarnings( "unused" ) final IllegalStateException e ) { /* Deliberately ignored */ }
        assumeFalse( elements == null );
        final var retValue = elements.getTypeElement( requireNonNullArgument( clazz, "clazz" ).getCanonicalName() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getElement()

    @Test
    public void ifElse()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addMethod( composer.methodBuilder( "isDelicious" )
                .addParameter( Primitives.INT, "count" )
                .returns( Primitives.BOOLEAN )
                .beginControlFlow( "if (count > 0)" )
                .addStatement( "return true" )
                .nextControlFlow( "else" )
                .addStatement( "return false" )
                .endControlFlow()
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            class Taco {
              boolean isDelicious(int count) {
                if (count > 0) {
                  return true;
                } else {
                  return false;
                }
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void indexedElseIf() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addMethod( composer.methodBuilder( "choices" )
                .beginControlFlow( "if ($1L != null || $1L == $2L)", "taco", "otherTaco" )
                .addStatement( "$T.out.println($S)", System.class, "only one taco? NOO!" )
                .nextControlFlow( "else if ($1L.$3L && $2L.$3L)", "taco", "otherTaco", "isSupreme()" )
                .addStatement( "$T.out.println($S)", System.class, "taco heaven" )
                .endControlFlow()
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.System;

            class Taco {
              void choices() {
                if (taco != null || taco == otherTaco) {
                  System.out.println("only one taco? NOO!");
                } else if (taco.isSupreme() && otherTaco.isSupreme()) {
                  System.out.println("taco heaven");
                }
              }
            }
            """;
        assertEquals( expected, actual );
}

    @Test
    public void initializerBlockInRightPlace()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addField( String.class, "foo", Modifier.PRIVATE )
            .addField( String.class, "FOO", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL )
            .addStaticBlock( composer.codeBlockBuilder()
                .addStatement( "FOO = $S", "FOO" )
                .build() )
            .addMethod( composer.constructorBuilder()
                .build() )
            .addMethod( composer.methodBuilder( "toString" )
                .addAnnotation( Override.class )
                .addModifiers( Modifier.PUBLIC )
                .returns( String.class )
                .addCode( "return FOO;\n" )
                .build() )
            .addInitializerBlock( composer.codeBlockBuilder()
                .addStatement( "foo = $S", "FOO" )
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Override;
            import java.lang.String;

            class Taco {
              private static final String FOO;

              static {
                FOO = "FOO";
              }

              private String foo;

              {
                foo = "FOO";
              }

              Taco() {
              }

              @Override
              public String toString() {
                return FOO;
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void initializerBlockUnsupportedExceptionOnAnnotation()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var annotationBuilder = composer.annotationTypeBuilder( "Taco" );

        final Class<? extends Throwable> expectedException = UnsupportedOperationException.class;
        try
        {
            annotationBuilder.addInitializerBlock( composer.codeBlockBuilder().build() );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }

    @Test
    public void initializerBlockUnsupportedExceptionOnInterface()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var interfaceBuilder = composer.interfaceBuilder( "Taco" );

        final Class<? extends Throwable> expectedException = UnsupportedOperationException.class;
        try
        {
            interfaceBuilder.addInitializerBlock( composer.codeBlockBuilder().build() );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }

    @Test
    public void initializersToBuilder()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        // Tests if toBuilder() contains correct static and instance
        // initializers
        final var taco = composer.classBuilder( "Taco" )
            .addField( String.class, "foo", Modifier.PRIVATE )
            .addField( String.class, "FOO", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL )
            .addStaticBlock( composer.codeBlockBuilder()
                .addStatement( "FOO = $S", "FOO" )
                .build() )
            .addMethod( composer.constructorBuilder()
                .build() )
            .addMethod( composer.methodBuilder( "toString" )
                .addAnnotation( Override.class )
                .addModifiers( Modifier.PUBLIC )
                .returns( String.class )
                .addCode( "return FOO;\n" )
                .build() )
            .addInitializerBlock( composer.codeBlockBuilder()
                .addStatement( "foo = $S", "FOO" )
                .build() )
            .build();

        final var recreatedTaco = taco.toBuilder().build();
        assertThat( toString( taco ) ).isEqualTo( toString( recreatedTaco ) );

        final var initializersAdded = taco.toBuilder()
            .addInitializerBlock( composer.codeBlockBuilder()
                .addStatement( "foo = $S", "instanceFoo" )
                .build() )
            .addStaticBlock( composer.codeBlockBuilder()
                .addStatement( "FOO = $S", "staticFoo" )
                .build() )
            .build();

        final var actual = toString( initializersAdded );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Override;
            import java.lang.String;

            class Taco {
              private static final String FOO;

              static {
                FOO = "FOO";
              }
              static {
                FOO = "staticFoo";
              }

              private String foo;

              {
                foo = "FOO";
              }
              {
                foo = "instanceFoo";
              }

              Taco() {
              }

              @Override
              public String toString() {
                return FOO;
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void inlineIndent() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addMethod( composer.methodBuilder( "inlineIndent" )
                .addCode( "if (3 < 4) {\n$>$T.out.println($S);\n$<}\n", System.class, "hello" )
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.System;

            class Taco {
              void inlineIndent() {
                if (3 < 4) {
                  System.out.println("hello");
                }
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void innerAnnotationInAnnotationDeclaration() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var bar = composer.annotationTypeBuilder( "Bar" )
            .addMethod( composer.methodBuilder( "value" )
                .addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT )
                .defaultValue( "@$T", Deprecated.class )
                .returns( Deprecated.class )
                .build() )
            .build();

        final var actual = toString( bar );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Deprecated;

            @interface Bar {
              Deprecated value() default @Deprecated;
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void interestingTypes() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final TypeName listOfAny = ParameterizedTypeName.from( ClassName.from( List.class ), WildcardTypeName.subtypeOf( Object.class ) );
        final TypeName listOfExtends = ParameterizedTypeName.from( ClassName.from( List.class ), WildcardTypeName.subtypeOf( Serializable.class ) );
        final TypeName listOfSuper = ParameterizedTypeName.from( ClassName.from( List.class ), WildcardTypeName.supertypeOf( String.class ) );
        final var taco = composer.classBuilder( "Taco" )
            .addField( listOfAny, "extendsObject" )
            .addField( listOfExtends, "extendsSerializable" )
            .addField( listOfSuper, "superString" )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.io.Serializable;
            import java.lang.String;
            import java.util.List;

            class Taco {
              List<?> extendsObject;

              List<? extends Serializable> extendsSerializable;

              List<? super String> superString;
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void interfaceClassToString() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var type = composer.interfaceBuilder( "Taco" ).build();
        assertThat( type.toString() ).isEqualTo(
            """
            interface Taco {
            }
            """ );
    }

    @Test
    public void interfaceDefaultMethods() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var bar = composer.interfaceBuilder( "Tacos" )
            .addMethod( composer.methodBuilder( "test" )
                .addModifiers( Modifier.PUBLIC, Modifier.DEFAULT )
                .returns( int.class )
                .addCode( composer.codeBlockBuilder()
                    .addStatement( "return 0" )
                    .build() )
                .build() )
            .build();

        final var actual = toString( bar );
        final var expected =
            """
            package com.squareup.tacos;

            interface Tacos {
              default int test() {
                return 0;
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void interfaceExtends() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = ClassName.from( TACOS_PACKAGE, "Taco" );
        final var typeSpec = composer.interfaceBuilder( "Taco" )
            .addSuperinterface( Serializable.class )
            .addSuperinterface( ParameterizedTypeName.from( ClassName.from( Comparable.class ), taco ) )
            .build();
        assertThat( toString( typeSpec ) ).isEqualTo(
            """
            package com.squareup.tacos;

            import java.io.Serializable;
            import java.lang.Comparable;

            interface Taco extends Serializable, Comparable<Taco> {
            }
            """ );
    }

    @Test
    public void interfaceStaticMethods() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var bar = composer.interfaceBuilder( "Tacos" )
            .addMethod( composer.methodBuilder( "test" )
                .addModifiers( Modifier.PUBLIC, Modifier.STATIC )
                .returns( int.class )
                .addCode( composer.codeBlockBuilder()
                    .addStatement( "return 0" )
                    .build() )
                .build() )
            .build();

        final var actual = toString( bar );
        final var expected =
            """
            package com.squareup.tacos;

            interface Tacos {
              static int test() {
                return 0;
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void intersectionType()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var typeVariable = TypeVariableName.from( "T", Comparator.class, Serializable.class );
        final var taco = composer.classBuilder( "Taco" )
            .addMethod( composer.methodBuilder( "getComparator" )
                .addTypeVariable( typeVariable )
                .returns( typeVariable )
                .addCode( "return null;\n" )
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.io.Serializable;
            import java.util.Comparator;

            class Taco {
              <T extends Comparator & Serializable> T getComparator() {
                return null;
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void invalidSuperClass()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        {
            final Class<? extends Throwable> expectedException = IllegalStateException.class;
            try
            {
                composer.classBuilder( "foo" )
                    .superclass( ClassName.from( List.class ) )
                    .superclass( ClassName.from( Map.class ) );
                fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
            }
            catch( final AssertionError e ) { throw e; }
            catch( final Throwable t )
            {
                final var isExpectedException = expectedException.isInstance( t );
                assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
            }
        }

        {
            final Class<? extends Throwable> expectedException = IllegalArgumentException.class;
            try
            {
                composer.classBuilder( "foo" )
                    .superclass( Primitives.INT );
                fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
            }
            catch( final AssertionError e ) { throw e; }
            catch( final Throwable t )
            {
                final var isExpectedException = expectedException.isInstance( t );
                assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
            }
        }
    }

    @Test
    public void javadoc()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addJavadoc( "A hard or soft tortilla, loosely folded and filled with whatever\n" )
            .addJavadoc( "{@link $T random} tex-mex stuff we could find in the pantry\n", Random.class )
            .addJavadoc( composer.codeBlockOf( "and some {@link $T} cheese.\n", String.class ) )
            .addField( composer.fieldBuilder( boolean.class, "soft" )
                .addJavadoc( "True for a soft flour tortilla; false for a crunchy corn tortilla.\n" )
                .build() )
            .addMethod( composer.methodBuilder( "refold" )
                .addJavadoc(
                    """
                    Folds the back of this taco to reduce sauce leakage.

                    <p>For {@link $T#KOREAN}, the front may also be folded.
                    """, Locale.class )
                .addParameter( Locale.class, "locale" )
                .build() )
            .build();
        /*
         * Mentioning a type in Javadoc will not cause an import to be added
         * (java.util.Random here), but the short name will be used if it's
         * already imported (java.util.Locale here).
         */
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.util.Locale;

            /**
             * A hard or soft tortilla, loosely folded and filled with whatever
             * {@link java.util.Random random} tex-mex stuff we could find in the pantry
             * and some {@link java.lang.String} cheese.
             */
            class Taco {
              /**
               * True for a soft flour tortilla; false for a crunchy corn tortilla.
               */
              boolean soft;

              /**
               * Folds the back of this taco to reduce sauce leakage.
               *
               * <p>For {@link Locale#KOREAN}, the front may also be folded.
               */
              void refold(Locale locale) {
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void lineWrapping()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var methodBuilder = composer.methodBuilder( "call" );
        methodBuilder.addCode( "$[call(" );
        for( var i = 0; i < 32; i++ )
        {
            //noinspection StringConcatenationMissingWhitespace
            methodBuilder.addParameter( String.class, "s" + i );
            methodBuilder.addCode( i > 0 ? ",$W$S" : "$S", i );
        }
        methodBuilder.addCode( ");$]\n" );

        final var taco = composer.classBuilder( "Taco" )
            .addMethod( methodBuilder.build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
                package com.squareup.tacos;

                import java.lang.String;

                class Taco {
                  void call(String s0, String s1, String s2, String s3, String s4, String s5, String s6, String s7,
                      String s8, String s9, String s10, String s11, String s12, String s13, String s14, String s15,
                      String s16, String s17, String s18, String s19, String s20, String s21, String s22,
                      String s23, String s24, String s25, String s26, String s27, String s28, String s29,
                      String s30, String s31) {
                    call("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
                        "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31");
                  }
                }
                """;
        assertEquals( expected, actual );
    }

    @Test
    public void lineWrappingWithZeroWidthSpace()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var method = composer.methodBuilder( "call" )
            .addCode( "$[iAmSickOfWaitingInLine($Z" )
            .addCode( "it, has, been, far, too, long, of, a, wait, and, i, would, like, to, eat, " )
            .addCode( "this, is, a, run, on, sentence" )
            .addCode( ");$]\n" )
            .build();
        final var taco = composer.classBuilder( "Taco" )
            .addMethod( method )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            class Taco {
              void call() {
                iAmSickOfWaitingInLine(
                    it, has, been, far, too, long, of, a, wait, and, i, would, like, to, eat, this, is, a, run, on, sentence);
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void literalFromAnything()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var value = new Object()
        {
            @Override
            public String toString()
            {
                return "foo";
            }
        };
        assertThat( composer.codeBlockOf( "$L", value ).toString() ).isEqualTo( "foo" );
    }

    @Test
    public void membersOrdering() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        // Hand out names in reverse-alphabetical in order to defend against
        // unexpected sorting.
        final var taco = composer.classBuilder( "Members" )
            .addType( composer.classBuilder( "Z" )
                .build() )
            .addType( composer.classBuilder( "Y" )
                .build() )
            .addField( String.class, "X", Modifier.STATIC )
            .addField( String.class, "W" )
            .addField( String.class, "V", Modifier.STATIC )
            .addField( String.class, "U" )
            .addMethod( composer.methodBuilder( "T" )
                .addModifiers( Modifier.STATIC )
                .build() )
            .addMethod( composer.methodBuilder( "S" )
                .build() )
            .addMethod( composer.methodBuilder( "R" )
                .addModifiers( Modifier.STATIC )
                .build() )
            .addMethod( composer.methodBuilder( "Q" )
                .build() )
            .addMethod( composer.constructorBuilder()
                .addParameter( int.class, "p" )
                .build() )
            .addMethod( composer.constructorBuilder()
                .addParameter( long.class, "o" )
                .build() )
            .build();
        // Static fields, instance fields, constructors, methods, classes.
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.String;

            class Members {
              static String X;

              static String V;

              String W;

              String U;

              Members(int p) {
              }

              Members(long o) {
              }

              static void T() {
              }

              void S() {
              }

              static void R() {
              }

              void Q() {
              }

              class Z {
              }

              class Y {
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void methodThrows() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addModifiers( Modifier.ABSTRACT )
            .addMethod( composer.methodBuilder( "throwOne" )
                .addException( IOException.class )
                .build() )
            .addMethod( composer.methodBuilder( "throwTwo" )
                .addException( IOException.class )
                .addException( ClassName.from( TACOS_PACKAGE, "SourCreamException" ) )
                .build() )
            .addMethod( composer.methodBuilder( "abstractThrow" )
                .addModifiers( Modifier.ABSTRACT )
                .addException( IOException.class )
                .build() )
            .addMethod( composer.methodBuilder( "nativeThrow" )
                .addModifiers( Modifier.NATIVE )
                .addException( IOException.class )
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.io.IOException;

            abstract class Taco {
              void throwOne() throws IOException {
              }

              void throwTwo() throws IOException, SourCreamException {
              }

              abstract void abstractThrow() throws IOException;

              native void nativeThrow() throws IOException;
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void methodToString() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var method = composer.methodBuilder( "toString" )
            .addAnnotation( Override.class )
            .addModifiers( Modifier.PUBLIC )
            .returns( String.class )
            .addStatement( "return $S", "taco" )
            .build();
        final var actual = method.toString();
        final var expected =
            """
            @java.lang.Override
            public java.lang.String toString() {
              return "taco";
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void multilineStatement() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addMethod( composer.methodBuilder( "toString" )
                .addAnnotation( Override.class )
                .addModifiers( Modifier.PUBLIC )
                .returns( String.class )
                .addStatement( "return $S\n+ $S\n+ $S\n+ $S\n+ $S", "Taco(", "beef,", "lettuce,", "cheese", ")" )
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Override;
            import java.lang.String;

            class Taco {
              @Override
              public String toString() {
                return "Taco("
                    + "beef,"
                    + "lettuce,"
                    + "cheese"
                    + ")";
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void multilineStatementWithAnonymousClass() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final TypeName stringComparator = ParameterizedTypeName.from( Comparator.class, String.class );
        final TypeName listOfString = ParameterizedTypeName.from( List.class, String.class );
        final var prefixComparator = composer.anonymousClassBuilder( "" )
            .addSuperinterface( stringComparator )
            .addMethod( composer.methodBuilder( "compare" )
                .addAnnotation( Override.class )
                .addModifiers( Modifier.PUBLIC )
                .returns( int.class )
                .addParameter( String.class, "a" )
                .addParameter( String.class, "b" )
                .addStatement( "return a.substring(0, length)\n" + ".compareTo(b.substring(0, length))" )
                .build() )
            .build();
        final var taco = composer.classBuilder( "Taco" )
            .addMethod( composer.methodBuilder( "comparePrefix" )
                .returns( stringComparator )
                .addParameter( int.class, "length", Modifier.FINAL )
                .addStatement( "return $L", prefixComparator )
                .build() )
            .addMethod( composer.methodBuilder( "sortPrefix" )
                .addParameter( listOfString, "list" )
                .addParameter( int.class, "length", Modifier.FINAL )
                .addStatement( "$T.sort(\nlist,\n$L)", Collections.class, prefixComparator )
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Override;
            import java.lang.String;
            import java.util.Collections;
            import java.util.Comparator;
            import java.util.List;

            class Taco {
              Comparator<String> comparePrefix(final int length) {
                return new Comparator<String>() {
                  @Override
                  public int compare(String a, String b) {
                    return a.substring(0, length)
                        .compareTo(b.substring(0, length));
                  }
                };
              }

              void sortPrefix(List<String> list, final int length) {
                Collections.sort(
                    list,
                    new Comparator<String>() {
                      @Override
                      public int compare(String a, String b) {
                        return a.substring(0, length)
                            .compareTo(b.substring(0, length));
                      }
                    });
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void multilineStrings() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addField( composer.fieldBuilder( String.class, "toppings" )
                .initializer( "$S", "shell\nbeef\nlettuce\ncheese\n" )
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.String;

            class Taco {
              String toppings = "shell\\n"
                + "beef\\n"
                + "lettuce\\n"
                + "cheese\\n";
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void multipleAnnotationAddition()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addAnnotations( Arrays.asList( composer.annotationBuilder( SuppressWarnings.class )
                .addMember( "value", "$S", "unchecked" )
                .build(), composer.annotationBuilder( Deprecated.class )
                .build() ) )
            .build();
        assertThat( toString( taco ) ).isEqualTo(
            """
            package com.squareup.tacos;

            import java.lang.Deprecated;
            import java.lang.SuppressWarnings;

            @SuppressWarnings("unchecked")
            @Deprecated
            class Taco {
            }
            """ );
    }

    @Test
    public void multipleFieldAddition()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addFields( Arrays.asList( composer.fieldBuilder( int.class, "ANSWER", Modifier.STATIC, Modifier.FINAL ).build(),
                composer.fieldBuilder( BigDecimal.class, "price", Modifier.PRIVATE ).build() ) )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.math.BigDecimal;

            class Taco {
              static final int ANSWER;

              private BigDecimal price;
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void multipleMethodAddition()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addMethods( Arrays.asList( composer.methodBuilder( "getAnswer" )
                .addModifiers( Modifier.PUBLIC, Modifier.STATIC )
                .returns( int.class )
                .addStatement( "return $L", 42 )
                .build(),
                composer.methodBuilder( "getRandomQuantity" )
                    .addModifiers( Modifier.PUBLIC )
                    .returns( int.class )
                    .addJavadoc( "chosen by fair dice roll ;)" )
                    .addStatement( "return $L", 4 )
                    .build() ) )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            class Taco {
              public static int getAnswer() {
                return 42;
              }

              /**
               * chosen by fair dice roll ;) */
              public int getRandomQuantity() {
                return 4;
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void multipleSuperinterfaceAddition()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" ).
            addSuperinterfaces( Arrays.asList( TypeName.from( Serializable.class ), TypeName.from( EventListener.class ) ) )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.io.Serializable;
            import java.util.EventListener;

            class Taco implements Serializable, EventListener {
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void multipleTypeAddition()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addTypes( Arrays.asList( composer.classBuilder( "Topping" ).build(), composer.classBuilder( "Sauce" ).build() ) )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            class Taco {
              class Topping {
              }

              class Sauce {
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void multipleTypeVariableAddition()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var location = composer.classBuilder( "Location" )
            .addTypeVariables( Arrays.asList( TypeVariableName.from( "T" ), TypeVariableName.from( "P", Number.class ) ) )
            .build();
        final var actual = toString( location );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Number;

            class Location<T, P extends Number> {
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void nameFromCharSequence()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        assertThat( composer.codeBlockOf( "$N", "text" ).toString() ).isEqualTo( "text" );
    }

    @Test
    public void nameFromField()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var field = composer.fieldBuilder( String.class, "field" ).build();
        assertThat( composer.codeBlockOf( "$N", field ).toString() ).isEqualTo( "field" );
    }

    @Test
    public void nameFromMethod()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var method = composer.methodBuilder( "method" ).addModifiers( Modifier.ABSTRACT ).returns( String.class ).build();
        assertThat( composer.codeBlockOf( "$N", method ).toString() ).isEqualTo( "method" );
    }

    @Test
    public void nameFromParameter()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var parameter = composer.parameterBuilder( String.class, "parameter" ).build();
        assertThat( composer.codeBlockOf( "$N", parameter ).toString() ).isEqualTo( "parameter" );
    }

    @Test
    public void nameFromType()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var type = composer.classBuilder( "Type" ).build();
        assertThat( composer.codeBlockOf( "$N", type ).toString() ).isEqualTo( "Type" );
    }

    @Test
    public void nameFromUnsupportedType()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = IllegalArgumentException.class;
        try
        {
            composer.codeBlockBuilder().add( "$N", String.class );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }

    @Test
    public void nativeMethods() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addMethod( composer.methodBuilder( "nativeInt" )
                .addModifiers( Modifier.NATIVE )
                .returns( int.class )
                .build() )
                // GWT JSNI
                .addMethod( composer.methodBuilder( "alert" )
                    .addModifiers( Modifier.PUBLIC, Modifier.STATIC, Modifier.NATIVE )
                    .addParameter( String.class, "msg" )
                    .addCode( composer.codeBlockBuilder()
                        .add( " /*-{\n" )
                        .indent()
                        .addStatement( "$$wnd.alert(msg)" )
                        .unindent()
                        .add( "}-*/" )
                        .build() )
                    .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.String;

            class Taco {
              native int nativeInt();

              public static native void alert(String msg) /*-{
                $wnd.alert(msg);
              }-*/;
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void nestedClasses() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = ClassName.from( TACOS_PACKAGE, "Combo", "Taco" );
        final var topping = ClassName.from( TACOS_PACKAGE, "Combo", "Taco", "Topping" );
        final var chips = ClassName.from( TACOS_PACKAGE, "Combo", "Chips" );
        final var sauce = ClassName.from( TACOS_PACKAGE, "Combo", "Sauce" );
        final var typeSpec = composer.classBuilder( "Combo" )
            .addField( taco, "taco" )
            .addField( chips, "chips" )
            .addType( composer.classBuilder( taco.simpleName() )
                .addModifiers( Modifier.STATIC )
                .addField( ParameterizedTypeName.from( ClassName.from( List.class ), topping ), "toppings" )
                .addField( sauce, "sauce" )
                .addType( composer.enumBuilder( topping.simpleName() )
                    .addEnumConstant( "SHREDDED_CHEESE" )
                    .addEnumConstant( "LEAN_GROUND_BEEF" )
                    .build() )
                .build() )
            .addType( composer.classBuilder( chips.simpleName() ).addModifiers( Modifier.STATIC ).
                addField( topping, "topping" )
                .addField( sauce, "dippingSauce" )
                .build() )
            .addType( composer.enumBuilder( sauce.simpleName() )
                .addEnumConstant( "SOUR_CREAM" )
                .addEnumConstant( "SALSA" )
                .addEnumConstant( "QUESO" )
                .addEnumConstant( "MILD" )
                .addEnumConstant( "FIRE" )
                .build() )
            .build();

        final var actual = toString( typeSpec );
        final var expected =
            """
            package com.squareup.tacos;

            import java.util.List;

            class Combo {
              Taco taco;

              Chips chips;

              static class Taco {
                List<Topping> toppings;

                Sauce sauce;

                enum Topping {
                  LEAN_GROUND_BEEF,

                  SHREDDED_CHEESE
                }
              }

              static class Chips {
                Taco.Topping topping;

                Sauce dippingSauce;
              }

              enum Sauce {
                FIRE,

                MILD,

                QUESO,

                SALSA,

                SOUR_CREAM
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void nullAnnotationsAddition()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = IllegalArgumentException.class;
        try
        {
            composer.classBuilder( "Taco" )
                .addAnnotations( null );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }

    @Test
    public void nullFieldsAddition()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = IllegalArgumentException.class;
        try
        {
            composer.classBuilder( "Taco" )
                .addFields( null );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }

    @Test
    public void nullInSuperinterfaceIterableAddition()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Collection<TypeName> superinterfaces = new ArrayList<>();
        superinterfaces.add( TypeName.from( List.class ) );
        superinterfaces.add( null );

        final Class<? extends Throwable> expectedException = IllegalArgumentException.class;
        try
        {
            composer.classBuilder( "Taco" )
                .addSuperinterfaces( superinterfaces );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }

    @Test
    public void nullMethodsAddition()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = IllegalArgumentException.class;
        try
        {
            composer.classBuilder( "Taco" )
                .addMethods( null );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }

    @Test
    public void nullModifiersAddition()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = IllegalArgumentException.class;
        try
        {
            composer.classBuilder( "Taco" )
                .addModifiers( (Modifier) null );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }

    @Test
    public void nullSingleSuperinterfaceAddition()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = IllegalArgumentException.class;
        try
        {
            composer.classBuilder( "Taco" )
                .addSuperinterface( (TypeName) null );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }

    @Test
    public void nullStringLiteral() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addField( composer.fieldBuilder( String.class, "NULL" )
                .initializer( "$L", (Object) null )
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.String;

            class Taco {
              String NULL = null;
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void nullSuperinterfacesAddition()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = NullArgumentException.class;
        try
        {
            composer.classBuilder( "Taco" )
                .addSuperinterfaces( null );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final NullArgumentException expected )
        {
            assertThat( expected.getMessage() ).isEqualTo( "Argument 'superinterfaces' must not be null" );
        }
    }

    @Test
    public void nullTypesAddition()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = NullArgumentException.class;
        try
        {
            composer.classBuilder( "Taco" )
                .addTypes( null );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final NullArgumentException expected )
        {
            assertThat( expected.getMessage() ).isEqualTo( "Argument 'typeSpecs' must not be null" );
        }
    }

    @Test
    public void nullTypeVariablesAddition()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = NullArgumentException.class;
        try
        {
            composer.classBuilder( "Taco" )
                .addTypeVariables( null );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final NullArgumentException expected )
        {
            assertThat( expected.getMessage() ).isEqualTo( "Argument 'typeVariables' must not be null" );
        }
    }

    @Test
    public void onlyEnumsMayHaveEnumConstants() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = IllegalStateException.class;
        try
        {
            composer.classBuilder( "Roshambo" )
                .addEnumConstant( "ROCK" )
                .build();
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }

    @Test
    public void originatingElementsIncludesThoseOfNestedTypes()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var outerElement = Mockito.mock( Element.class );
        final var innerElement = Mockito.mock( Element.class );
        final var outer = composer.classBuilder( "Outer" )
            .addOriginatingElement( outerElement )
            .addType( composer.classBuilder( "Inner" )
                .addOriginatingElement( innerElement )
                .build() )
            .build();
        assertThat( outer.originatingElements() ).containsExactly( outerElement, innerElement );
    }

    @Test
    public void parameterToString() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var parameter = composer.parameterBuilder( ClassName.from( TACOS_PACKAGE, "Taco" ), "taco" )
            .addModifiers( Modifier.FINAL )
            .addAnnotation( ClassName.from( "javax.annotation", "Nullable" ) )
            .build();
        assertThat( parameter.toString() ).isEqualTo( "@javax.annotation.Nullable final com.squareup.tacos.Taco taco" );
    }

    @Test
    public void referencedAndDeclaredSimpleNamesConflict() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var internalTop = composer.fieldBuilder( ClassName.from( TACOS_PACKAGE, "Top" ), "internalTop" )
            .build();
        final var internalBottom = composer.fieldBuilder( ClassName.from( TACOS_PACKAGE, "Top", "Middle", "Bottom" ), "internalBottom" )
            .build();
        final var externalTop = composer.fieldBuilder( ClassName.from( DONUTS_PACKAGE, "Top" ), "externalTop" )
            .build();
        final var externalBottom = composer.fieldBuilder( ClassName.from( DONUTS_PACKAGE, "Bottom" ), "externalBottom" )
            .build();
        final var top = composer.classBuilder( "Top" )
            .addField( internalTop )
            .addField( internalBottom )
            .addField( externalTop )
            .addField( externalBottom )
            .addType( composer.classBuilder( "Middle" )
                .addField( internalTop )
                .addField( internalBottom )
                .addField( externalTop )
                .addField( externalBottom )
                .addType( composer.classBuilder( "Bottom" )
                    .addField( internalTop )
                    .addField( internalBottom )
                    .addField( externalTop )
                    .addField( externalBottom )
                    .build() )
                .build() )
            .build();
        final var actual = toString( top );
        final var expected =
            """
            package com.squareup.tacos;

            import com.squareup.donuts.Bottom;

            class Top {
              Top internalTop;

              Middle.Bottom internalBottom;

              com.squareup.donuts.Top externalTop;

              Bottom externalBottom;

              class Middle {
                Top internalTop;

                Bottom internalBottom;

                com.squareup.donuts.Top externalTop;

                com.squareup.donuts.Bottom externalBottom;

                class Bottom {
                  Top internalTop;

                  Bottom internalBottom;

                  com.squareup.donuts.Top externalTop;

                  com.squareup.donuts.Bottom externalBottom;
                }
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void retrofitStyleInterface() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var observable = ClassName.from( TACOS_PACKAGE, "Observable" );
        final var fooBar = ClassName.from( TACOS_PACKAGE, "FooBar" );
        final var thing = ClassName.from( TACOS_PACKAGE, "Thing" );
        final var things = ClassName.from( TACOS_PACKAGE, "Things" );
        final var map = ClassName.from( "java.util", "Map" );
        final var string = ClassName.from( "java.lang", "String" );
        final var headers = ClassName.from( TACOS_PACKAGE, "Headers" );
        final var post = ClassName.from( TACOS_PACKAGE, "POST" );
        final var body = ClassName.from( TACOS_PACKAGE, "Body" );
        final var queryMap = ClassName.from( TACOS_PACKAGE, "QueryMap" );
        final var header = ClassName.from( TACOS_PACKAGE, "Header" );
        final var service = composer.interfaceBuilder( "Service" )
            .addMethod( composer.methodBuilder( "fooBar" )
                .addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT )
                .addAnnotation( composer.annotationBuilder( headers )
                    .addMember( "value", "$S", "Accept: application/json" )
                    .addMember( "value", "$S", "User-Agent: foobar" )
                    .build() )
                .addAnnotation( composer.annotationBuilder( post )
                    .addMember( "value", "$S", "/foo/bar" )
                    .build() )
                .returns( ParameterizedTypeName.from( observable, fooBar ) )
                .addParameter( composer.parameterBuilder( ParameterizedTypeName.from( things, thing ), "things" )
                    .addAnnotation( body )
                    .build() )
                .addParameter( composer.parameterBuilder( ParameterizedTypeName.from( map, string, string ), "query" )
                    .addAnnotation( composer.annotationBuilder( queryMap )
                        .addMember( "encodeValues", "false" )
                        .build() )
                    .build() )
                .addParameter( composer.parameterBuilder( string, "authorization" )
                    .addAnnotation( composer.annotationBuilder( header )
                        .addMember( "value", "$S", "Authorization" )
                        .build() )
                    .build() )
                .build() )
            .build();

        final var actual = toString( service );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.String;
            import java.util.Map;

            interface Service {
              @Headers({
                  "Accept: application/json",
                  "User-Agent: foobar"
              })
              @POST("/foo/bar")
              Observable<FooBar> fooBar(@Body Things<Thing> things,
                  @QueryMap(encodeValues = false) Map<String, String> query,
                  @Header("Authorization") String authorization);
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void simpleNamesConflictInThisAndOtherPackage() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var internalOther = composer.fieldBuilder( ClassName.from( TACOS_PACKAGE, "Other" ), "internalOther" )
            .build();
        final var externalOther = composer.fieldBuilder( ClassName.from( DONUTS_PACKAGE, "Other" ), "externalOther" )
            .build();
        final var gen = composer.classBuilder( "Gen" )
            .addField( internalOther )
            .addField( externalOther )
            .build();
        final var actual = toString( gen );
        final var expected =
            """
                package com.squareup.tacos;

                class Gen {
                  Other internalOther;

                  com.squareup.donuts.Other externalOther;
                }
                """;
        assertEquals( expected, actual );
    }

    @Test
    public void staticCodeBlock()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addField( String.class, "foo", Modifier.PRIVATE )
            .addField( String.class, "FOO", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL )
            .addStaticBlock( composer.codeBlockBuilder()
                .addStatement( "FOO = $S", "FOO" )
                .build() )
            .addMethod( composer.methodBuilder( "toString" )
                .addAnnotation( Override.class )
                .addModifiers( Modifier.PUBLIC )
                .returns( String.class )
                .addCode( "return FOO;\n" )
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Override;
            import java.lang.String;

            class Taco {
              private static final String FOO;

              static {
                FOO = "FOO";
              }

              private String foo;

              @Override
              public String toString() {
                return FOO;
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void stringFromAnything()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var value = new Object()
        {
            @Override
            public String toString()
            {
                return "foo";
            }
        };
        assertThat( composer.codeBlockOf( "$S", value ).toString() ).isEqualTo( "\"foo\"" );
    }

    @Test
    public void stringFromNull()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        assertThat( composer.codeBlockOf( "$S", new Object [] {null} ).toString() ).isEqualTo( "null" );
        assertThat( composer.codeBlockOf( "$L", new Object [] {null} ).toString() ).isEqualTo( "null" );
    }

    @Test
    public void superClassOnlyValidForClasses()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = IllegalStateException.class;
        try
        {
            composer.annotationTypeBuilder( "A" ).superclass( ClassName.from( Object.class ) );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
        try
        {
            composer.enumBuilder( "E" ).superclass( ClassName.from( Object.class ) );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
        try
        {
            composer.interfaceBuilder( "I" ).superclass( ClassName.from( Object.class ) );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }

    @Test
    public void tooFewArguments()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = IllegalArgumentException.class;
        try
        {
            composer.codeBlockBuilder().add( "$S" );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "index 1 for '$S' not in range (received 0 arguments)" );
        }
    }

    @SuppressWarnings( "CastToConcreteClass" )
    public static final String toString( final TypeSpec typeSpec )
    {
        final var typeSpecImpl = (TypeSpecImpl) typeSpec;
        final var composer = typeSpecImpl.getFactory();
        final var retValue = composer.javaFileBuilder( TACOS_PACKAGE, typeSpec )
            .build()
            .toString();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toString()

    @Test
    public void tryCatch()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taco = composer.classBuilder( "Taco" )
            .addMethod( composer.methodBuilder( "addTopping" )
                .addParameter( ClassName.from( "com.squareup.tacos", "Topping" ), "topping" )
                .beginControlFlow( "try" )
                .addCode( "/* do something tricky with the topping */\n" )
                .nextControlFlow( "catch ($T e)", ClassName.from( "com.squareup.tacos", "IllegalToppingException" ) )
                .endControlFlow()
                .build() )
            .build();
        final var actual = toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            class Taco {
              void addTopping(Topping topping) {
                try {
                  /* do something tricky with the topping */
                } catch (IllegalToppingException e) {
                }
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void typeFromReflectType()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        assertThat( composer.codeBlockOf( "$T", String.class ).toString() ).isEqualTo( "java.lang.String" );
    }

    @Test
    public void typeFromTypeName()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var typeName = TypeName.from( String.class );
        assertThat( composer.codeBlockOf( "$T", typeName ).toString() ).isEqualTo( "java.lang.String" );
    }

    @Test
    public void typeFromUnsupportedType()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = IllegalArgumentException.class;
        try
        {
            composer.codeBlockBuilder().add( "$T", "java.lang.String" );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "expected type but was java.lang.String" );
        }
    }

    @Test
    public void typeVariables() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var t = TypeVariableName.from( "T" );
        final var p = TypeVariableName.from( "P", Number.class );
        final var location = ClassName.from( TACOS_PACKAGE, "Location" );
        final var typeSpec = composer.classBuilder( "Location" )
            .addTypeVariable( t )
            .addTypeVariable( p )
            .addSuperinterface( ParameterizedTypeName.from( ClassName.from( Comparable.class ), p ) )
            .addField( t, "label" )
            .addField( p, "x" )
            .addField( p, "y" )
            .addMethod( composer.methodBuilder( "compareTo" )
                .addAnnotation( Override.class )
                .addModifiers( Modifier.PUBLIC )
                .returns( int.class )
                .addParameter( p, "p" )
                .addCode( "return 0;\n" )
                .build() )
            .addMethod( composer.methodBuilder( "of" )
                .addModifiers( Modifier.PUBLIC, Modifier.STATIC )
                .addTypeVariable( t )
                .addTypeVariable( p )
                .returns( ParameterizedTypeName.from( location, t, p ) )
                .addParameter( t, "label" )
                .addParameter( p, "x" )
                .addParameter( p, "y" )
                .addCode( "throw new $T($S);\n", UnsupportedOperationException.class, "TODO" )
                .build() )
            .build();
        final var actual = toString( typeSpec );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Comparable;
            import java.lang.Number;
            import java.lang.Override;
            import java.lang.UnsupportedOperationException;

            class Location<T, P extends Number> implements Comparable<P> {
              T label;

              P x;

              P y;

              @Override
              public int compareTo(P p) {
                return 0;
              }

              public static <T, P extends Number> Location<T, P> of(T label, P x, P y) {
                throw new UnsupportedOperationException("TODO");
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void typeVariableWithBounds()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var a = composer.annotationBuilder( ClassName.from( "com.squareup.tacos", "A" ) )
            .build();
        final var p = TypeVariableName.from( "P", Number.class );
        final var q = (TypeVariableName) TypeVariableName.from( "Q", Number.class )
            .annotated( a );
        final var typeSpec = composer.classBuilder( "Location" )
            .addTypeVariable( p.withBounds( Comparable.class ) )
            .addTypeVariable( q.withBounds( Comparable.class ) )
            .addField( p, "x" )
            .addField( q, "y" ).build();
        final var actual = toString( typeSpec );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Comparable;
            import java.lang.Number;

            class Location<P extends Number & Comparable, @A Q extends Number & Comparable> {
              P x;

              @A Q y;
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void unusedArgumentsIndexed()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = IllegalArgumentException.class;
        try
        {
            composer.codeBlockBuilder().add( "$1L $2L", "a", "b", "c" );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "unused argument: $3" );
        }
        try
        {
            composer.codeBlockBuilder().add( "$1L $1L $1L", "a", "b", "c" );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "unused arguments: $2, $3" );
        }
        try
        {
            composer.codeBlockBuilder().add( "$3L $1L $3L $1L $3L", "a", "b", "c", "d" );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "unused arguments: $2, $4" );
        }
    }

    @Test
    public void unusedArgumentsRelative()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = IllegalArgumentException.class;
        try
        {
            composer.codeBlockBuilder().add( "$L $L", "a", "b", "c" );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "unused arguments: expected 2, received 3" );
        }
    }

    @Test
    public void varargs() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var taqueria = composer.classBuilder( "Taqueria" )
            .addMethod( composer.methodBuilder( "prepare" )
                .addParameter( int.class, "workers" )
                .addParameter( Runnable [].class, "jobs" )
                .varargs()
                .build() )
            .build();
        final var actual = toString( taqueria );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Runnable;

            class Taqueria {
              void prepare(int workers, Runnable... jobs) {
              }
            }
            """;
        assertEquals( expected, actual );
    }
}
//  class TestTypeSpec

/*
 *  End of File
 */