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

package test.org.tquadrat.javacomposer.annotationspec;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.AnnotationSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.TypeSpec;

/**
 *  A tests for the class
 *  {@link AnnotationSpec}
 *  that was created to validate the migration.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ReflectAnnotation.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( {"MisorderedAssertEqualsArguments", "NewClassNamingConvention"} )
@ClassVersion( sourceVersion = "$Id: ReflectAnnotation.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "test.org.tquadrat.javacomposer.annotationspec.ReflectAnnotation" )
public final class ReflectAnnotation
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    @SuppressWarnings( "javadoc" )
    @Inherited
    @Retention( RetentionPolicy.RUNTIME )
    public @interface AnnotationB
    { /* Empty Annotation */ }
    // annotation AnnotationB

    @SuppressWarnings( "javadoc" )
    @Retention( RetentionPolicy.RUNTIME )
    public @interface HasDefaultsAnnotation
    {
            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        Class<? extends Annotation> l() default AnnotationB.class;

        Class<? extends Number> [] r() default {Byte.class, Short.class, Integer.class, Long.class};
    }
    //  annotation HasDefaultsAnnotation

    @SuppressWarnings( "javadoc" )
    @HasDefaultsAnnotation( l = Override.class, r = {Float.class, Double.class} )
    public static class IsAnnotated
    { /* Empty Class */ }
    //  class IsAnnotated

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    @SuppressWarnings( {"static-method", "javadoc"} )
    @Test
    final void reflectAnnotation()
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
            import test.org.tquadrat.javacomposer.annotationspec.ReflectAnnotation;

            @ReflectAnnotation.HasDefaultsAnnotation(
                l = Override.class,
                r = {
                    Float.class,
                    Double.class
                }
            )
            class Taco {
            }
            """;
        final var actual = toString( composer, taco );
        assertEquals( expected, actual );
    }   //  reflectAnnotation()

    @SuppressWarnings( {"javadoc", "UseOfConcreteClass"} )
    private static String toString( final JavaComposer composer, final TypeSpec typeSpec )
    {
        return composer.javaFileBuilder( "com.squareup.tacos", typeSpec )
            .build()
            .toString();
    }
}
//  ReflectAnnotation

/*
 *  End of File
 */