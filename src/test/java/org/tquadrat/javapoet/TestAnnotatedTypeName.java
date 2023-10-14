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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.javacomposer.internal.TypeNameImpl.BOOLEAN_PRIMITIVE;
import static org.tquadrat.foundation.javacomposer.internal.TypeNameImpl.DOUBLE_PRIMITIVE;
import static org.tquadrat.foundation.javacomposer.internal.TypeNameImpl.VOID_PRIMITIVE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.javacomposer.AnnotationSpec;
import org.tquadrat.foundation.javacomposer.ArrayTypeName;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.WildcardTypeName;

@SuppressWarnings( {"static-method", "javadoc", "ClassWithTooManyMethods"} )
@ClassVersion( sourceVersion = "$Id: TestAnnotatedTypeName.java 1076 2023-10-03 18:36:07Z tquadrat $" )
@DisplayName( "org.tquadrat.javapoet.TestAnnotatedTypeName" )
public class TestAnnotatedTypeName
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    @Target( ElementType.TYPE_USE )
    public @interface NeverNull
    {/* Empty Block */}
    //  annotation NeverNull

    @Target( ElementType.TYPE_USE )
    public @interface TypeUseAnnotation
    {/* Empty Block */}
    //  annotation TypeUseAnnotation

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The message for an exception that was not thrown: {@value}.
     */
    public static final String MSG_ExceptionNotThrown = "Expected Exception '%s' was not thrown";

    /**
     *  The message that another than the expected exception was thrown: {@value}.
     */
    public static final String MSG_WrongExceptionThrown = "Wrong Exception type; caught '%2$s' but '%1$s' was expected";

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/
    private static final JavaComposer m_Composer;

    private static final String NN = NeverNull.class.getCanonicalName();

    private static final String TUA = TypeUseAnnotation.class.getCanonicalName();

    private static final AnnotationSpec NEVER_NULL;

    private static final AnnotationSpec TYPE_USE_ANNOTATION;

    static
    {
        m_Composer = new JavaComposer();

        NEVER_NULL = m_Composer.annotationBuilder( NeverNull.class )
            .build();

        TYPE_USE_ANNOTATION = m_Composer.annotationBuilder( TypeUseAnnotation.class )
            .build();
    }

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    @Test
    public void annotated()
    {
        final var simpleString = TypeName.from( String.class );
        assertFalse( simpleString.isAnnotated() );
        assertEquals( simpleString, TypeName.from( String.class ) );

        final var annotated = simpleString.annotated( NEVER_NULL );
        assertTrue( annotated.isAnnotated() );
        assertEquals( annotated, annotated.annotated() );
    }

    @Test
    public void annotatedArgumentOfParameterizedType()
    {
        final var type = TypeName.from( String.class ).annotated( TYPE_USE_ANNOTATION );
        final TypeName actual = ParameterizedTypeName.from( ClassName.from( List.class ), type );
        assertThat( actual.toString() ).isEqualTo( "java.util.List<java.lang. @" + TUA + " String>" );
    }

    @Test
    public void annotatedArrayElementType()
    {
        final TypeName type = ArrayTypeName.of( ClassName.from( Object.class ).annotated( TYPE_USE_ANNOTATION ) );
        assertThat( type.toString() ).isEqualTo( "java.lang. @" + TUA + " Object[]" );
    }

    // https://github.com/square/javapoet/issues/614
    @Test
    public void annotatedArrayType()
    {
        final var type = ArrayTypeName.of( ClassName.from( Object.class ) ).annotated( TYPE_USE_ANNOTATION );
        assertThat( type.toString() ).isEqualTo( "java.lang.Object @" + TUA + " []" );
    }

    // https://github.com/square/javapoet/issues/614
    @Test
    public void annotatedArrayTypeInVarargsParameter()
    {
        final var composer = new JavaComposer();

        final TypeName type = ArrayTypeName.of( ArrayTypeName.of( ClassName.from( Object.class ) )
            .annotated( TYPE_USE_ANNOTATION ) );
        final var varargsMethod = composer.methodBuilder( "m" )
            .addParameter( composer.parameterBuilder( type, "p" )
                .build() )
            .varargs()
            .build();
        final var actual = varargsMethod.toString();
        final var expected =
            """
            void m(java.lang.Object[] @org.tquadrat.javapoet.TestAnnotatedTypeName.TypeUseAnnotation ... p) {
            }
            """;
        assertEquals( expected, actual );
    }

    // https://github.com/square/javapoet/issues/614
    @Test
    public void annotatedArrayTypeVarargsParameter()
    {
        final var composer = new JavaComposer();

        final var type = ArrayTypeName.of( ArrayTypeName.of( ClassName.from( Object.class ) ) )
            .annotated( TYPE_USE_ANNOTATION );
        final var varargsMethod = composer.methodBuilder( "m" )
            .addParameter( composer.parameterBuilder( type, "p" )
                .build() )
            .varargs()
            .build();
        final var actual = varargsMethod.toString();
        final var expected =
            """
            void m(java.lang.Object @org.tquadrat.javapoet.TestAnnotatedTypeName.TypeUseAnnotation []... p) {
            }
            """;
        assertEquals( expected, actual );
    }

    @Test
    public void annotatedEnclosingAndNestedType()
    {
        final var type = ((ClassName) TypeName.from( Map.class ).annotated( TYPE_USE_ANNOTATION )).nestedClass( "Entry" ).annotated( TYPE_USE_ANNOTATION );
        assertThat( type.toString() ).isEqualTo( String.join( TUA, "java.util. @", " Map. @", " Entry" ) );
    }

    @Test
    public void annotatedEquivalence()
    {
        annotatedEquivalence( VOID_PRIMITIVE );
        annotatedEquivalence( TypeName.from( Object [].class ) );
        annotatedEquivalence( ClassName.from( Object.class ) );
        annotatedEquivalence( ParameterizedTypeName.from( List.class, Object.class ) );
        annotatedEquivalence( TypeName.from( Object.class ) );
        annotatedEquivalence( TypeName.from( Object.class ) );
    }

    private void annotatedEquivalence( final TypeName type )
    {
        assertFalse( type.isAnnotated() );
        assertEquals( type, type );
        assertEquals( type.annotated( TYPE_USE_ANNOTATION ), type.annotated( TYPE_USE_ANNOTATION ) );
        assertNotEquals( type, type.annotated( TYPE_USE_ANNOTATION ) );
        assertEquals( type.hashCode(), type.hashCode() );
        assertEquals( type.annotated( TYPE_USE_ANNOTATION ).hashCode(), type.annotated( TYPE_USE_ANNOTATION ).hashCode() );
        assertNotEquals( type.hashCode(), type.annotated( TYPE_USE_ANNOTATION ).hashCode() );
    }

    // https://github.com/square/javapoet/issues/614
    @Test
    public void annotatedInnerMultidimensionalArrayType()
    {
        final TypeName type = ArrayTypeName.of( ArrayTypeName.of( ClassName.from( Object.class ) ).annotated( TYPE_USE_ANNOTATION ) );
        assertThat( type.toString() ).isEqualTo( "java.lang.Object[] @" + TUA + " []" );
    }

    // https://github.com/square/javapoet/issues/431
    @Test
    public void annotatedNestedParameterizedType()
    {
        final var type = ParameterizedTypeName.from( Map.Entry.class, Byte.class, Byte.class ).annotated( TYPE_USE_ANNOTATION );
        assertThat( type.toString() ).isEqualTo( "java.util.Map. @" + TUA + " Entry<java.lang.Byte, java.lang.Byte>" );
    }

    // https://github.com/square/javapoet/issues/431
    @Test
    public void annotatedNestedType()
    {
        final var type = TypeName.from( Map.Entry.class ).annotated( TYPE_USE_ANNOTATION );
        assertThat( type.toString() ).isEqualTo( "java.util.Map. @" + TUA + " Entry" );
    }

    // https://github.com/square/javapoet/issues/614
    @Test
    public void annotatedOuterMultidimensionalArrayType()
    {
        final var type = ArrayTypeName.of( ArrayTypeName.of( ClassName.from( Object.class ) ) ).annotated( TYPE_USE_ANNOTATION );
        assertThat( type.toString() ).isEqualTo( "java.lang.Object @" + TUA + " [][]" );
    }

    @Test
    public void annotatedParameterizedType()
    {
        final TypeName type = ParameterizedTypeName.from( List.class, String.class );
        final var actual = type.annotated( TYPE_USE_ANNOTATION );
        assertThat( actual.toString() ).isEqualTo( "java.util. @" + TUA + " List<java.lang.String>" );
    }

    @Test
    public void annotatedTwice()
    {
        final var type = TypeName.from( String.class );
        final var actual = type.annotated( NEVER_NULL ).annotated( TYPE_USE_ANNOTATION );
        assertThat( actual.toString() ).isEqualTo( "java.lang. @" + NN + " @" + TUA + " String" );
    }

    @Test
    public void annotatedType()
    {
        final var type = TypeName.from( String.class );
        final var actual = type.annotated( TYPE_USE_ANNOTATION );
        assertThat( actual.toString() ).isEqualTo( "java.lang. @" + TUA + " String" );
    }

    @Test
    public void annotatedWildcardTypeNameWithExtends()
    {
        final var type = TypeName.from( String.class ).annotated( TYPE_USE_ANNOTATION );
        final TypeName actual = WildcardTypeName.subtypeOf( type );
        assertThat( actual.toString() ).isEqualTo( "? extends java.lang. @" + TUA + " String" );
    }

    @Test
    public void annotatedWildcardTypeNameWithSuper()
    {
        final var type = TypeName.from( String.class ).annotated( TYPE_USE_ANNOTATION );
        final TypeName actual = WildcardTypeName.supertypeOf( type );
        assertThat( actual.toString() ).isEqualTo( "? super java.lang. @" + TUA + " String" );
    }

    @Test
    public void nullAnnotationArray()
    {
        final Class<? extends Throwable> expectedException = NullArgumentException.class;
        try
        {
            BOOLEAN_PRIMITIVE.annotated( (AnnotationSpec []) null );
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
    public void nullAnnotationList()
    {
        final Class<? extends Throwable> expectedException = NullArgumentException.class;
        try
        {
            DOUBLE_PRIMITIVE.annotated( (List<AnnotationSpec>) null );
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
    public void withoutAnnotationsOnAnnotatedEnclosingAndNestedType()
    {
        final var type = ((ClassName) TypeName.from( Map.class ).annotated( TYPE_USE_ANNOTATION )).nestedClass( "Entry" ).annotated( TYPE_USE_ANNOTATION );
        assertThat( type.isAnnotated() ).isTrue();
        assertThat( type.withoutAnnotations() ).isEqualTo( TypeName.from( Map.Entry.class ) );
    }

    @Test
    public void withoutAnnotationsOnAnnotatedEnclosingType()
    {
        final TypeName type = ((ClassName) TypeName.from( Map.class ).annotated( TYPE_USE_ANNOTATION )).nestedClass( "Entry" );
        assertThat( type.isAnnotated() ).isTrue();
        assertThat( type.withoutAnnotations() ).isEqualTo( TypeName.from( Map.Entry.class ) );
    }

    @Test
    public void withoutAnnotationsOnAnnotatedNestedType()
    {
        final var type = ((ClassName) TypeName.from( Map.class )).nestedClass( "Entry" ).annotated( TYPE_USE_ANNOTATION );
        assertThat( type.isAnnotated() ).isTrue();
        assertThat( type.withoutAnnotations() ).isEqualTo( TypeName.from( Map.Entry.class ) );
    }
}
//  class TestAnnotatedTypeName

/*
 *  End of File
 */