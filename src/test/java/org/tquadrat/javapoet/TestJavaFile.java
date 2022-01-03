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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;

import javax.lang.model.element.Modifier;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.JavaFile;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.javacomposer.TypeVariableName;

/**
 *  The tests for the class
 *  {@link JavaFile}
 *  that came with the original library.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestJavaFile.java 943 2021-12-21 01:34:32Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestJavaFile.java 943 2021-12-21 01:34:32Z tquadrat $" )
@SuppressWarnings( {"javadoc", "MisorderedAssertEqualsArguments", "ClassWithTooManyMethods"} )
@RunWith( JUnit4.class )
@DisplayName( "TestJavaFile" )
public final class TestJavaFile
{
    @SuppressWarnings( "static-method" )
    @Test
    public final void annotatedTypeParam() throws Exception
    {
        final var composer = new JavaComposer();
        final var actual = composer
            .javaFileBuilder( "com.squareup.tacos",
                composer.classBuilder( "Taco" )
                    .addField( ParameterizedTypeName.from( ClassName.from( List.class ), ClassName.from( "com.squareup.meat", "Chorizo" ).annotated( composer.annotationBuilder( ClassName.from( "com.squareup.tacos", "Spicy" ) ).build() ) ), "chorizo" )
                    .build() )
            .build().toString();
        final var expected =
            """
            package com.squareup.tacos;

            import com.squareup.meat.Chorizo;
            import java.util.List;

            class Taco {
              List<@Spicy Chorizo> chorizo;
            }
            """;
        assertEquals( expected, actual );
    }

    /** https://github.com/square/javapoet/issues/366 */
    @SuppressWarnings( "static-method" )
    @Test
    public final void annotationIsNestedClass() throws Exception
    {
        final var composer = new JavaComposer();
        final var actual = composer.javaFileBuilder( "com.squareup.tacos", composer.classBuilder( "TestComponent" ).addAnnotation( ClassName.from( "dagger", "Component" ) ).addType( composer.classBuilder( "Builder" ).addAnnotation( ClassName.from( "dagger", "Component", "Builder" ) ).build() ).build() ).build().toString();
        final var expected =
            """
            package com.squareup.tacos;

            import dagger.Component;

            @Component
            class TestComponent {
              @Component.Builder
              class Builder {
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void classAndSuperclassShareName() throws Exception
    {
        final var composer = new JavaComposer();
        final var actual = composer.javaFileBuilder( "com.squareup.tacos", composer.classBuilder( "Taco" ).superclass( ClassName.from( "com.taco.bell", "Taco" ) ).build() ).build().toString();
        final var expected =
            """
            package com.squareup.tacos;

            import com.taco.bell.Taco;

            class Taco extends Taco {
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void conflictingAnnotation() throws Exception
    {
        final var composer = new JavaComposer();
        final var actual = composer.javaFileBuilder( "com.squareup.tacos", composer.classBuilder( "Taco" ).addAnnotation( ClassName.from( "com.taco.bell", "Taco" ) ).build() ).build().toString();
        final var expected =
            """
            package com.squareup.tacos;

            import com.taco.bell.Taco;

            @Taco
            class Taco {
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void conflictingAnnotationReferencedClass() throws Exception
    {
        final var composer = new JavaComposer();
        final var actual = composer.javaFileBuilder( "com.squareup.tacos", composer.classBuilder( "Taco" ).addAnnotation( composer.annotationBuilder( ClassName.from( "com.squareup.tacos", "MyAnno" ) ).addMember( "value", "$T.class", ClassName.from( "com.taco.bell", "Taco" ) ).build() ).build() ).build().toString();
        final var expected =
            """
            package com.squareup.tacos;

            import com.taco.bell.Taco;

            @MyAnno(Taco.class)
            class Taco {
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void conflictingChildName() throws Exception
    {
        final var composer = new JavaComposer();

        final var actual =
            composer.javaFileBuilder( "com.squareup.tacos",
                composer.classBuilder( "A" )
                    .addType( composer.classBuilder( "B" )
                        .addType( composer.classBuilder( "C" )
                            .addField( ClassName.from( "com.squareup.tacos", "A", "Twin", "D" ), "d" )
                                .addType( composer.classBuilder( "Twin" )
                                    .build() )
                            .build() )
                        .build() )
                    .addType( composer.classBuilder( "Twin" )
                        .addType( composer.classBuilder( "D" )
                            .build() )
                        .build() )
                    .build() )
                .build()
                .toString();
        final var expected =
            """
            package com.squareup.tacos;

            class A {
              class B {
                class C {
                  A.Twin.D d;

                  class Twin {
                  }
                }
              }

              class Twin {
                class D {
                }
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( {"static-method", "UseOfObsoleteDateTimeApi"} )
    @Test
    public final void conflictingImports() throws Exception
    {
        final var composer = new JavaComposer();
        final var actual = composer.javaFileBuilder( "com.squareup.tacos", composer.classBuilder( "Taco" )
            .addField( Date.class, "madeFreshDate" )
            .addField( ClassName.from( "java.sql", "Date" ), "madeFreshDatabaseDate" )
            .build() )
            .build()
            .toString();
        final var expected =
            """
            package com.squareup.tacos;

            import java.util.Date;

            class Taco {
              Date madeFreshDate;

              java.sql.Date madeFreshDatabaseDate;
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void conflictingNameOutOfScope() throws Exception
    {
        final var composer = new JavaComposer();

        final var actual = composer.javaFileBuilder( "com.squareup.tacos", composer.classBuilder( "A" )
            .addType( composer.classBuilder( "B" )
                .addType( composer.classBuilder( "C" )
                    .addField( ClassName.from( "com.squareup.tacos", "A", "Twin", "D" ), "d" )
                    .addType( composer.classBuilder( "Nested" )
                        .addType( composer.classBuilder( "Twin" )
                            .build() )
                        .build() )
                    .build() )
                .build() )
            .addType( composer.classBuilder( "Twin" )
                .addType( composer.classBuilder( "D" )
                    .build() )
                .build() )
            .build() )
            .build()
            .toString();
        final var expected =
            """
            package com.squareup.tacos;

            class A {
              class B {
                class C {
                  Twin.D d;

                  class Nested {
                    class Twin {
                    }
                  }
                }
              }

              class Twin {
                class D {
                }
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void conflictingParentName() throws Exception
    {
        final var composer = new JavaComposer();

        final var actual = composer.javaFileBuilder( "com.squareup.tacos",
            composer.classBuilder( "A" )
                .addType( composer.classBuilder( "B" )
                    .addType( composer.classBuilder( "Twin" )
                        .build() )
                    .addType( composer.classBuilder( "C" )
                        .addField( ClassName.from( "com.squareup.tacos", "A", "Twin", "D" ), "d" )
                        .build() )
                    .build() )
                .addType( composer.classBuilder( "Twin" )
                    .addType( composer.classBuilder( "D" )
                        .build() )
                    .build() )
                .build() )
            .build()
            .toString();
        final var expected =
            """
            package com.squareup.tacos;

            class A {
              class B {
                class Twin {
                }

                class C {
                  A.Twin.D d;
                }
              }

              class Twin {
                class D {
                }
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void conflictingTypeVariableBound() throws Exception
    {
        final var composer = new JavaComposer();

        final var actual = composer.javaFileBuilder( "com.squareup.tacos",
            composer.classBuilder( "Taco" )
                .addTypeVariable( TypeVariableName.from( "T", ClassName.from( "com.taco.bell", "Taco" ) ) )
                .build() )
            .build()
            .toString();
        final var expected =
            """
            package com.squareup.tacos;

            import com.taco.bell.Taco;

            class Taco<T extends Taco> {
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void defaultPackage() throws Exception
    {
        final var composer = new JavaComposer();

        final var actual = composer.javaFileBuilder( EMPTY_STRING,
            composer.classBuilder( "HelloWorld" )
                .addMethod( composer.methodBuilder( "main" )
                    .addModifiers( Modifier.PUBLIC, Modifier.STATIC )
                    .addParameter( String [].class, "args" )
                    .addCode( "$T.out.println($S);\n", System.class, "Hello World!" )
                    .build() )
                .build() )
            .build()
            .toString();
        final var expected =
            """
            import java.lang.String;
            import java.lang.System;

            class HelloWorld {
              public static void main(String[] args) {
                System.out.println("Hello World!");
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void defaultPackageTypesAreNotImported() throws Exception
    {
        final var composer = new JavaComposer();

        final var actual = composer.javaFileBuilder( "hello",
            composer.classBuilder( "World" )
                .addSuperinterface( ClassName.from( "", "Test" ) )
                .build() )
            .build()
            .toString();
        final var expected =
            """
            package hello;

            class World implements Test {
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void emptyLinesInTopOfFileComment() throws Exception
    {
        final var composer = new JavaComposer();

        final var actual = composer.javaFileBuilder( "com.squareup.tacos",
            composer.classBuilder( "Taco" )
                .build() )
            .addFileComment( "\nGENERATED FILE:\n\nDO NOT EDIT!\n" )
            .build()
            .toString();
        final var expected =
            """
            /*
             *
             * GENERATED FILE:
             *
             * DO NOT EDIT!
             *
             */
 
            package com.squareup.tacos;

            class Taco {
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
//    @Ignore( "addStaticImport doesn't support members with $L" )
    @Test
    public final void importStaticDynamic()
    {
        final var composer = new JavaComposer();

        var method = composer.methodBuilder( "main" )
            .addStatement( "$T.$L.println($S)", System.class, "out", "hello" )
            .build();

        var candidateClass = composer.classBuilder( "Taco" )
            .addMethod( method )
            .build();

        var actual = composer.javaFileBuilder( "com.squareup.tacos", candidateClass )
            .addStaticImport( System.class, "out" )
            .build()
            .toString();
        var expected =
            """
            package com.squareup.tacos;

            import static java.lang.System.out;
            
            import java.lang.System;

            class Taco {
              void main() {
                System.out.println("hello");
              }
            }
            """;
        assertEquals( expected, actual );

        method = composer.methodBuilder( "main" )
            .addStatement( "$L.println($S)", "out", "hello" )
            .build();

        candidateClass = composer.classBuilder( "Taco" )
            .addMethod( method )
            .build();

        actual = composer.javaFileBuilder( "com.squareup.tacos", candidateClass )
            .addStaticImport( System.class, "out" )
            .build()
            .toString();
        expected =
            """
            package com.squareup.tacos;

            import static java.lang.System.out;

            class Taco {
              void main() {
                out.println("hello");
              }
            }
            """;
        assertEquals( expected, actual );
    }   //  importStaticDynamic()

    @SuppressWarnings( "static-method" )
    @Test
    public final void importStaticForCrazyFormatsWorks()
    {
        final var composer = new JavaComposer();

        //--- * Don't look at the generated code... *--------------------------
        final var method = composer.methodBuilder( "method" ).build();
        final var actual = composer.javaFileBuilder( "com.squareup.tacos",
            composer.classBuilder( "Taco" )
                .addStaticBlock(
                    composer.codeBlockBuilder()
                        .addStatement( "$T", Runtime.class )
                        .addStatement( "$T.a()", Runtime.class )
                        .addStatement( "$T.X", Runtime.class )
                        .addStatement( "$T$T", Runtime.class, Runtime.class )
                        .addStatement( "$T.$T", Runtime.class, Runtime.class )
                        .addStatement( "$1T$1T", Runtime.class )
                        .addStatement( "$1T$2L$1T", Runtime.class, "?" )
                        .addStatement( "$1T$2L$2S$1T", Runtime.class, "?" )
                        .addStatement( "$1T$2L$2S$1T$3N$1T", Runtime.class, "?", method )
                        .addStatement( "$T$L", Runtime.class, "?" )
                        .addStatement( "$T$S", Runtime.class, "?" )
                        .addStatement( "$T$N", Runtime.class, method )
                        .build() )
                .build() )
            .addStaticImport( Runtime.class, "*" )
            .build()
            .toString();
        final var expected =
            """
            package com.squareup.tacos;

            import static java.lang.Runtime.*;

            import java.lang.Runtime;

            class Taco {
              static {
                Runtime;
                a();
                X;
                RuntimeRuntime;
                Runtime.Runtime;
                RuntimeRuntime;
                Runtime?Runtime;
                Runtime?"?"Runtime;
                Runtime?"?"RuntimemethodRuntime;
                Runtime?;
                Runtime"?";
                Runtimemethod;
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void importStaticMixed()
    {
        final var composer = new JavaComposer();

        final var source = composer.javaFileBuilder( "com.squareup.tacos",
            composer.classBuilder( "Taco" )
                .addStaticBlock(
                    composer.codeBlockBuilder()
                        .addStatement( "assert $1T.valueOf(\"BLOCKED\") == $1T.BLOCKED", Thread.State.class )
                        .addStatement( "$T.gc()", System.class )
                        .addStatement( "$1T.out.println($1T.nanoTime())", System.class )
                        .build() )
                .addMethod(
                    composer.constructorBuilder()
                        .addParameter( Thread.State [].class, "states" )
                        .varargs( true )
                        .build() )
                .build() )
            .addStaticImport( Thread.State.BLOCKED )
            .addStaticImport( System.class, "*" )
            .addStaticImport( Thread.State.class, "valueOf" )
            .build();
        final var actual = source.toString();
        final var expected =
            """
            package com.squareup.tacos;

            import static java.lang.System.*;
            import static java.lang.Thread.State.BLOCKED;
            import static java.lang.Thread.State.valueOf;

            import java.lang.Thread;

            class Taco {
              static {
                assert valueOf("BLOCKED") == BLOCKED;
                gc();
                out.println(nanoTime());
              }

              Taco(Thread.State... states) {
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void importStaticNone()
    {
        final var composer = new JavaComposer();

        final var actual = composer.javaFileBuilder( "readme", importStaticTypeSpec( composer, "Util" ) )
            .build()
            .toString();
        final var expected =
            """
            package readme;

            import java.lang.System;
            import java.util.concurrent.TimeUnit;

            class Util {
              public static long minutesToSeconds(long minutes) {
                System.gc();
                return TimeUnit.SECONDS.convert(minutes, TimeUnit.MINUTES);
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void importStaticOnce()
    {
        final var composer = new JavaComposer();

        final var actual = composer.javaFileBuilder( "readme", importStaticTypeSpec( composer, "Util" ) )
            .addStaticImport( TimeUnit.SECONDS )
            .build()
            .toString();
        final var expected =
            """
            package readme;

            import static java.util.concurrent.TimeUnit.SECONDS;

            import java.lang.System;
            import java.util.concurrent.TimeUnit;

            class Util {
              public static long minutesToSeconds(long minutes) {
                System.gc();
                return SECONDS.convert(minutes, TimeUnit.MINUTES);
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void importStaticReadmeExample()
    {
        final var composer = new JavaComposer();

        final var hoverboard = ClassName.from( "com.mattel", "Hoverboard" );
        final var namedBoards = ClassName.from( "com.mattel", "Hoverboard", "Boards" );
        final var list = ClassName.from( "java.util", "List" );
        final var arrayList = ClassName.from( "java.util", "ArrayList" );
        final TypeName listOfHoverboards = ParameterizedTypeName.from( list, hoverboard );
        final var beyond = composer.methodBuilder( "beyond" )
            .returns( listOfHoverboards )
            .addStatement( "$T result = new $T<>()", listOfHoverboards, arrayList )
            .addStatement( "result.add($T.createNimbus(2000))", hoverboard )
            .addStatement( "result.add($T.createNimbus(\"2001\"))", hoverboard )
            .addStatement( "result.add($T.createNimbus($T.THUNDERBOLT))", hoverboard, namedBoards )
            .addStatement( "$T.sort(result)", Collections.class )
            .addStatement( "return result.isEmpty() ? $T.emptyList() : result", Collections.class )
            .build();
        final var hello = composer.classBuilder( "HelloWorld" )
            .addMethod( beyond )
            .build();
        final var example = composer.javaFileBuilder( "com.example.helloworld", hello )
            .addStaticImport( hoverboard, "createNimbus" )
            .addStaticImport( namedBoards, "*" )
            .addStaticImport( Collections.class, "*" )
            .build();
        final var actual = example.toString();
        final var expected =
            """
            package com.example.helloworld;

            import static com.mattel.Hoverboard.Boards.*;
            import static com.mattel.Hoverboard.createNimbus;
            import static java.util.Collections.*;

            import com.mattel.Hoverboard;
            import java.util.ArrayList;
            import java.util.List;

            class HelloWorld {
              List<Hoverboard> beyond() {
                List<Hoverboard> result = new ArrayList<>();
                result.add(createNimbus(2000));
                result.add(createNimbus("2001"));
                result.add(createNimbus(THUNDERBOLT));
                sort(result);
                return result.isEmpty() ? emptyList() : result;
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void importStaticTwice()
    {
        final var composer = new JavaComposer();

        final var actual = composer.javaFileBuilder( "readme", importStaticTypeSpec( composer, "Util" ) )
            .addStaticImport( TimeUnit.SECONDS )
            .addStaticImport( TimeUnit.MINUTES )
            .build()
            .toString();
        final var expected =
            """
            package readme;

            import static java.util.concurrent.TimeUnit.MINUTES;
            import static java.util.concurrent.TimeUnit.SECONDS;

            import java.lang.System;

            class Util {
              public static long minutesToSeconds(long minutes) {
                System.gc();
                return SECONDS.convert(minutes, MINUTES);
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( {"SameParameterValue", "UseOfConcreteClass"} )
    private static TypeSpec importStaticTypeSpec( final JavaComposer composer, final String name )
    {
        final var method = composer.methodBuilder( "minutesToSeconds" )
            .addModifiers( Modifier.PUBLIC, Modifier.STATIC )
            .returns( long.class )
            .addParameter( long.class, "minutes" )
            .addStatement( "$T.gc()", System.class )
            .addStatement( "return $1T.SECONDS.convert(minutes, $1T.MINUTES)", TimeUnit.class )
            .build();
        return composer.classBuilder( name )
            .addMethod( method )
            .build();
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void importStaticUsingWildcards()
    {
        final var composer = new JavaComposer();

        final var actual = composer.javaFileBuilder( "readme", importStaticTypeSpec( composer, "Util" ) )
            .addStaticImport( TimeUnit.class, "*" )
            .addStaticImport( System.class, "*" )
            .build()
            .toString();
        final var expected =
            """
            package readme;

            import static java.lang.System.*;
            import static java.util.concurrent.TimeUnit.*;

            class Util {
              public static long minutesToSeconds(long minutes) {
                gc();
                return SECONDS.convert(minutes, MINUTES);
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void nestedClassAndSuperclassShareName() throws Exception
    {
        final var composer = new JavaComposer();

        final var actual = composer.javaFileBuilder( "com.squareup.tacos",
            composer.classBuilder( "Taco" )
                .superclass( ClassName.from( "com.squareup.wire", "Message" ) )
                .addType( composer.classBuilder( "Builder" )
                    .superclass( ClassName.from( "com.squareup.wire", "Message", "Builder" ) )
                    .build() )
                .build() )
            .build()
            .toString();
        final var expected =
            """
            package com.squareup.tacos;

            import com.squareup.wire.Message;

            class Taco extends Message {
              class Builder extends Message.Builder {
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void noImports() throws Exception
    {
        final var composer = new JavaComposer();

        final var actual = composer.javaFileBuilder( "com.squareup.tacos",
            composer.classBuilder( "Taco" )
                .build() )
            .build()
            .toString();
        final var expected =
            """
            package com.squareup.tacos;

            class Taco {
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void packageClassConflictsWithNestedClass() throws Exception
    {
        final var composer = new JavaComposer();

        final var actual = composer.javaFileBuilder( "com.squareup.tacos",
            composer.classBuilder( "Taco" )
                .addField( ClassName.from( "com.squareup.tacos", "A" ), "a" )
                .addType( composer.classBuilder( "A" )
                    .build() )
                .build() )
            .build()
            .toString();
        final var expected =
            """
            package com.squareup.tacos;

            class Taco {
              com.squareup.tacos.A a;

              class A {
              }
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void packageClassConflictsWithSuperlass() throws Exception
    {
        final var composer = new JavaComposer();

        final var actual = composer.javaFileBuilder( "com.squareup.tacos",
            composer.classBuilder( "Taco" )
                .superclass( ClassName.from( "com.taco.bell", "A" ) )
                .addField( ClassName.from( "com.squareup.tacos", "A" ), "a" )
                .build() )
            .build()
            .toString();
        final var expected =
            """
            package com.squareup.tacos;

            class Taco extends com.taco.bell.A {
              A a;
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( {"static-method", "UseOfObsoleteDateTimeApi"} )
    @Test
    public final void singleImport() throws Exception
    {
        final var composer = new JavaComposer();

        final var actual = composer.javaFileBuilder( "com.squareup.tacos",
            composer.classBuilder( "Taco" )
                .addField( Date.class, "madeFreshDate" )
                .build() )
            .build()
            .toString();
        final var expected =
            """
            package com.squareup.tacos;

            import java.util.Date;

            class Taco {
              Date madeFreshDate;
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void skipJavaLangImportsWithConflictingClassFirst() throws Exception
    {
        final var composer = new JavaComposer();

        /*
         * Whatever is used first wins! In this case the Float in
         * com.squareup.soda is imported.
         */
        final var actual = composer.javaFileBuilder( "com.squareup.tacos",
            composer.classBuilder( "Taco" )
                .addField( ClassName.from( "com.squareup.soda", "Float" ), "beverage" )
                .addField( ClassName.from( "java.lang", "Float" ), "litres" )
                .build() )
            .skipJavaLangImports( true )
            .build()
            .toString();

        /*
         *  Second 'Float' is fully qualified, because it would cause a name
         *  clash otherwise.
         */
        final var expected =
            """
            package com.squareup.tacos;

            import com.squareup.soda.Float;

            class Taco {
              Float beverage;

              java.lang.Float litres;
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void skipJavaLangImportsWithConflictingClassLast() throws Exception
    {
        final var composer = new JavaComposer();

        /*
         * Whatever is used first wins! In this case the Float in java.lang is
         * imported.
         */
        final var actual = composer.javaFileBuilder( "com.squareup.tacos",
            composer.classBuilder( "Taco" )
                .addField( ClassName.from( "java.lang", "Float" ), "litres" )
                .addField( ClassName.from( "com.squareup.soda", "Float" ), "beverage" )
                .build() )
            .skipJavaLangImports( true )
            .build()
            .toString();

        /*
         *  Second 'Float' is fully qualified, because it would cause a name
         *  clash otherwise.
         */
        final var expected =
            """
            package com.squareup.tacos;

            class Taco {
              Float litres;

              com.squareup.soda.Float beverage;
            }
            """;
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public final void superclassReferencesSelf() throws Exception
    {
        final var composer = new JavaComposer();

        final var actual = composer.javaFileBuilder( "com.squareup.tacos",
            composer.classBuilder( "Taco" )
                .superclass( ParameterizedTypeName.from( ClassName.from( Comparable.class ), ClassName.from( "com.squareup.tacos", "Taco" ) ) )
                .build() )
            .build()
            .toString();
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Comparable;

            class Taco extends Comparable<Taco> {
            }
            """;
        assertEquals( expected, actual );
   }

    @SuppressWarnings( "static-method" )
    @Test
    public final void topOfFileComment() throws Exception
    {
        final var composer = new JavaComposer();

        final var actual = composer.javaFileBuilder( "com.squareup.tacos",
            composer.classBuilder( "Taco" )
                .build() )
            .addFileComment( "Generated $L by JavaPoet. DO NOT EDIT!", "2015-01-13" )
            .build()
            .toString();
        final var expected =
            """
            /*
             * Generated 2015-01-13 by JavaPoet. DO NOT EDIT!
             */
             
            package com.squareup.tacos;

            class Taco {
            }
            """;
        assertEquals( expected, actual );
    }
}
//  class TestJavaFile

/*
 *  End of File
 */