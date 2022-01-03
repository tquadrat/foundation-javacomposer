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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.javacomposer.AnnotationSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.javapoet.helper.CompilationRule;

/**
 *  The tests for the class
 *  {@link AnnotationSpec}
 *  that came with the original library.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestAnnotationSpec.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestAnnotationSpec.java 937 2021-12-14 21:59:00Z tquadrat $" )
@SuppressWarnings( {"javadoc", "MisorderedAssertEqualsArguments"} )
@RunWith( JUnit4.class )
@DisplayName( "TestAnnotationSpec" )
public final class TestAnnotationSpec
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    @Retention( RetentionPolicy.RUNTIME )
    public @interface AnnotationA
    { /* Empty Annotation */ }
    // annotation AnnotationA

    @Inherited
    @Retention( RetentionPolicy.RUNTIME )
    public @interface AnnotationB
    { /* Empty Annotation */ }
    // annotation AnnotationB

    @Retention( RetentionPolicy.RUNTIME )
    public @interface AnnotationC
    {
            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        String value();
    }
    // annotation AnnotationC

    public enum Breakfast
    {
            /*------------------*\
        ====** Enum Declaration **=============================================
            \*------------------*/
        WAFFLES, PANCAKES;

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         */
        @Override
        public String toString()
        {
            return name() + " with cherries!";
        }   //  toString()
    }
    //  enum Breakfast

    @Retention( RetentionPolicy.RUNTIME )
    public @interface HasDefaultsAnnotation
    {
            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        byte a() default 5;

        short b() default 6;

        int c() default 7;

        long d() default 8;

        float e() default 9.0f;

        double f() default 10.0;

        char [] g() default {0, 0xCAFE, 'z', '€', 'ℕ', '"', '\'', '\t', '\n'};

        boolean h() default true;

        Breakfast i() default Breakfast.WAFFLES;

        AnnotationA j() default @AnnotationA;

        String k() default "maple";

        Class<? extends Annotation> l() default AnnotationB.class;

        int [] m() default {1, 2, 3};

        Breakfast [] n() default {Breakfast.WAFFLES, Breakfast.PANCAKES};

        Breakfast o();

        int p();

        AnnotationC q() default @AnnotationC( "foo" );

        Class<? extends Number> [] r() default {Byte.class, Short.class, Integer.class, Long.class};
    }
    //  annotation HasDefaultsAnnotation

    @HasDefaultsAnnotation( o = Breakfast.PANCAKES, p = 1701, f = 11.1, m = {9, 8, 1}, l = Override.class, j = @AnnotationA, q = @AnnotationC( "bar" ), r = {Float.class, Double.class} )
    public static class IsAnnotated
    { /* Empty Class */ }
    //  class IsAnnotated

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    @SuppressWarnings( "InstanceVariableOfConcreteClass" )
    @Rule
    public final CompilationRule m_Compilation = new CompilationRule();

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    @SuppressWarnings( "static-method" )
    @Test
    public void equalsAndHashCode()
    {
        final var composer = new JavaComposer();

        var a = composer.annotationBuilder( AnnotationC.class )
            .build();
        var b = composer.annotationBuilder( AnnotationC.class )
            .build();
        assertThat( a.equals( b ) ).isTrue();
        assertThat( a.hashCode() ).isEqualTo( b.hashCode() );
        a = composer.annotationBuilder( AnnotationC.class )
            .addMember( "value", "$S", "123" )
            .build();
        b = composer.annotationBuilder( AnnotationC.class )
            .addMember( "value", "$S", "123" )
            .build();
        assertThat( a.equals( b ) ).isTrue();
        assertThat( a.hashCode() ).isEqualTo( b.hashCode() );
    }

    @Test
    public void defaultAnnotation()
    {
        final var composer = new JavaComposer();

        final var name = IsAnnotated.class.getCanonicalName();
        final var element = m_Compilation.getElements().getTypeElement( name );
        final var annotation = composer.createAnnotation( element.getAnnotationMirrors().get( 0 ) );

        final var taco = composer.classBuilder( "Taco" ).addAnnotation( annotation ).build();
        final var expected =
            """
            package com.squareup.tacos;

            import com.squareup.javapoet.AnnotationSpecTest;
            import java.lang.Double;
            import java.lang.Float;
            import java.lang.Override;

            @AnnotationSpecTest.HasDefaultsAnnotation(
                o = AnnotationSpecTest.Breakfast.PANCAKES,
                p = 1701,
                f = 11.1,
                m = {
                    9,
                    8,
                    1
                },
                l = Override.class,
                j = @AnnotationSpecTest.AnnotationA,
                q = @AnnotationSpecTest.AnnotationC("bar"),
                r = {
                    Float.class,
                    Double.class
                }
            )
            class Taco {
            }
            """;
        final var actual = toString( taco );
        assertEquals( expected, actual );
    }

    @Test
    public void defaultAnnotationWithImport()
    {
        final var composer = new JavaComposer();

        final var name = IsAnnotated.class.getCanonicalName();
        final var element = m_Compilation.getElements().getTypeElement( name );
        final var annotation = composer.createAnnotation( element.getAnnotationMirrors().get( 0 ) );
        final var typeBuilder = composer.classBuilder( IsAnnotated.class.getSimpleName() );
        typeBuilder.addAnnotation( annotation );
        final var file = composer.javaFileBuilder( "com.squareup.javapoet", typeBuilder.build() )
            .build();
        assertThat( file.toString() ).isEqualTo(
            """
            package com.squareup.javapoet;

            import java.lang.Double;
            import java.lang.Float;
            import java.lang.Override;

            @AnnotationSpecTest.HasDefaultsAnnotation(
                o = AnnotationSpecTest.Breakfast.PANCAKES,
                p = 1701,
                f = 11.1,
                m = {
                    9,
                    8,
                    1
                },
                l = Override.class,
                j = @AnnotationSpecTest.AnnotationA,
                q = @AnnotationSpecTest.AnnotationC("bar"),
                r = {
                    Float.class,
                    Double.class
                }
            )
            class IsAnnotated {
            }
            """ );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void emptyArray()
    {
        final var composer = new JavaComposer();

        final var builder = composer.annotationBuilder( HasDefaultsAnnotation.class );
        builder.addMember( "n", "$L", "{}" );
        assertThat( builder.build().toString() ).isEqualTo( "@TestAnnotationSpec.HasDefaultsAnnotation(" + "n = {}" + ")" );
        builder.addMember( "m", "$L", "{}" );
        assertThat( builder.build().toString() ).isEqualTo( "@TestAnnotationSpec.HasDefaultsAnnotation(" + "n = {}, m = {}" + ")" );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void dynamicArrayOfEnumConstants()
    {
        final var composer = new JavaComposer();

        var builder = composer.annotationBuilder( HasDefaultsAnnotation.class );
        builder.addMember( "n", "$T.$L", Breakfast.class, Breakfast.PANCAKES.name() );
        assertThat( builder.build().toString() ).isEqualTo(
            """
            @TestAnnotationSpec.HasDefaultsAnnotation\
            (\
            n = TestAnnotationSpec.Breakfast.PANCAKES\
            )""" );

        // builder = AnnotationSpec.builder(HasDefaultsAnnotation.class);
        builder.addMember( "n", "$T.$L", Breakfast.class, Breakfast.WAFFLES.name() );
        builder.addMember( "n", "$T.$L", Breakfast.class, Breakfast.PANCAKES.name() );
        assertThat( builder.build().toString() ).isEqualTo( "@TestAnnotationSpec.HasDefaultsAnnotation(n = {TestAnnotationSpec.Breakfast.PANCAKES, TestAnnotationSpec.Breakfast.WAFFLES, TestAnnotationSpec.Breakfast.PANCAKES})" );

        builder = builder.build().toBuilder(); // idempotent
        assertThat( builder.build().toString() ).isEqualTo( "@TestAnnotationSpec.HasDefaultsAnnotation(n = {TestAnnotationSpec.Breakfast.PANCAKES, TestAnnotationSpec.Breakfast.WAFFLES, TestAnnotationSpec.Breakfast.PANCAKES})" );

        builder.addMember( "n", "$T.$L", Breakfast.class, Breakfast.WAFFLES.name() );
        assertThat( builder.build().toString() ).isEqualTo(
            """
            @TestAnnotationSpec.HasDefaultsAnnotation\
            (\
            n = {TestAnnotationSpec.Breakfast.PANCAKES, \
            TestAnnotationSpec.Breakfast.WAFFLES, \
            TestAnnotationSpec.Breakfast.PANCAKES, \
            TestAnnotationSpec.Breakfast.WAFFLES}\
            )""" );
    }

    @Test
    public void defaultAnnotationToBuilder()
    {
        var composer = new JavaComposer();

        final var name = IsAnnotated.class.getCanonicalName();
        final var element = m_Compilation.getElements()
            .getTypeElement( name );
        final var builder = composer.createAnnotation( element.getAnnotationMirrors()
            .get( 0 ) )
            .toBuilder();
        builder.addMember( "m", "$L", 123 );
        assertThat( builder.build().toString() ).isEqualTo(
            """
             @TestAnnotationSpec.HasDefaultsAnnotation\
             (\
             o = com.squareup.javapoet.AnnotationSpecTest.Breakfast.PANCAKES, \
             p = 1701, \
             f = 11.1, \
             m = {9, 8, 1, 123}, \
             l = java.lang.Override.class, \
             j = @com.squareup.javapoet.AnnotationSpecTest.AnnotationA, \
             q = @com.squareup.javapoet.AnnotationSpecTest.AnnotationC("bar"), \
             r = {java.lang.Float.class, java.lang.Double.class}\
             )""" );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void reflectAnnotation()
    {
        final var composer = new JavaComposer();

        final var annotation = IsAnnotated.class.getAnnotation( HasDefaultsAnnotation.class );
        final var spec = composer.createAnnotation( annotation );
        final var taco = composer.classBuilder( "Taco" )
            .addAnnotation( spec )
            .build();
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Double;
            import java.lang.Float;
            import java.lang.Override;
            import TestAnnotationSpec;

            @TestAnnotationSpec.HasDefaultsAnnotation(
                f = 11.1,
                l = Override.class,
                m = {
                    9,
                    8,
                    1
                },
                o = TestAnnotationSpec.Breakfast.PANCAKES,
                p = 1701,
                q = @TestAnnotationSpec.AnnotationC("bar"),
                r = {
                    Float.class,
                    Double.class
                }
            )
            class Taco {
            }
            """;
        assertThat( toString( taco ) ).isEqualTo( expected );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void reflectAnnotationWithDefaults()
    {
        final var composer = new JavaComposer();

        final var annotation = IsAnnotated.class.getAnnotation( HasDefaultsAnnotation.class );
        final var spec = composer.createAnnotation( annotation, true );
        final var taco = composer.classBuilder( "Taco" ).addAnnotation( spec ).build();
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.Double;
            import java.lang.Float;
            import java.lang.Override;
            import TestAnnotationSpec;

            @TestAnnotationSpec.HasDefaultsAnnotation(
                a = 5,
                b = 6,
                c = 7,
                d = 8,
                e = 9.0f,
                f = 11.1,
                g = {
                    '\\u0000',
                    '쫾',
                    'z',
                    '€',
                    'ℕ',
                    '"',
                    '\\'',
                    '\\t',
                    '\\n'
                },
                h = true,
                i = TestAnnotationSpec.Breakfast.WAFFLES,
                j = @TestAnnotationSpec.AnnotationA,
                k = "maple",
                l = Override.class,
                m = {
                    9,
                    8,
                    1
                },
                n = {
                    TestAnnotationSpec.Breakfast.WAFFLES,
                    TestAnnotationSpec.Breakfast.PANCAKES
                },
                o = TestAnnotationSpec.Breakfast.PANCAKES,
                p = 1701,
                q = @TestAnnotationSpec.AnnotationC("bar"),
                r = {
                    Float.class,
                    Double.class
                }
            )
            class Taco {
            }
            """;
        assertEquals( expected, toString( taco ) );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void disallowsNullMemberName()
    {
        var composer = new JavaComposer();

        final var builder = composer.annotationBuilder( HasDefaultsAnnotation.class );
        try
        {
            final var $L = builder.addMember( null, "$L", "" );
            fail( $L.build().toString() );
        }
        catch( final NullArgumentException e )
        {
            assertThat( e ).hasMessageThat().isEqualTo( "Argument 'name' must not be null" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void requiresValidMemberName()
    {
        var composer = new JavaComposer();

        final var builder = composer.annotationBuilder( HasDefaultsAnnotation.class );
        try
        {
            final var $L = builder.addMember( "@", "$L", "" );
            fail( $L.build().toString() );
        }
        catch( final IllegalArgumentException e )
        {
            assertThat( e ).hasMessageThat().isEqualTo( "not a valid name: @" );
        }
    }

    private static String toString( final TypeSpec typeSpec )
    {
        return new JavaComposer().javaFileBuilder( "com.squareup.tacos", typeSpec ).build().toString();
    }
}
//  TestAnnotationSpec

/*
 *  End of File
 */