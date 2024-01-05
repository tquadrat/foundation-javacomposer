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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.ArrayTypeName;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.Primitives;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeVariableName;
import org.tquadrat.foundation.javacomposer.WildcardTypeName;
import org.tquadrat.foundation.javacomposer.internal.TypeNameImpl;

/**
 *  The tests for the class
 *  {@link TypeName}
 *  that came with the original library.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestTypeName.java 1085 2024-01-05 16:23:28Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestTypeName.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@SuppressWarnings( {"javadoc", "ClassEscapesDefinedScope"} )
@DisplayName( "TestTypeName" )
public class TestTypeName
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    @SuppressWarnings( {"unused", "ProtectedInnerClass"} )
    protected static class TestGeneric<T>
    {
            /*---------------*\
        ====** Inner Classes **================================================
            \*---------------*/
        @SuppressWarnings( {"InnerClassTooDeeplyNested", "InnerClassMayBeStatic", "PackageVisibleInnerClass"} )
        class Inner
        { /* Empty class */ }
        //  class Inner

        @SuppressWarnings( {"InnerClassTooDeeplyNested", "PackageVisibleInnerClass", "InnerClassMayBeStatic"} )
        class InnerGeneric<T2>
        { /* Empty class */ }
        //  class InnerGeneric

        @SuppressWarnings( {"InnerClassTooDeeplyNested", "PackageVisibleInnerClass"} )
        static class NestedNonGeneric
        { /* Empty class */ }
        //  class NestedNonGeneric
    }
    //  class TestGeneric()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    @SuppressWarnings( "static-method" )
    private void assertEqualsHashCodeAndToString( final TypeName a, final TypeName b )
    {
        assertEquals( a.toString(), b.toString() );
        assertThat( a.equals( b ) ).isTrue();
        assertThat( a.hashCode() ).isEqualTo( b.hashCode() );
        //noinspection SimplifiableAssertion
        assertFalse( a.equals( null ) );
    }   // assertEqualsHashCodeAndToString()

    @Test
    public void equalsAndHashCodeArrayTypeName()
    {
        assertEqualsHashCodeAndToString( ArrayTypeName.of( Object.class ), ArrayTypeName.of( Object.class ) );
        assertEqualsHashCodeAndToString( TypeName.from( Object [].class ), ArrayTypeName.of( Object.class ) );
    }   //  equalsAndHashCodeArrayTypeName()

    @Test
    public void equalsAndHashCodeParameterizedTypeName()
    {
        assertEqualsHashCodeAndToString( TypeName.from( Object.class ), TypeName.from( Object.class ) );
        assertEqualsHashCodeAndToString( ParameterizedTypeName.from( Set.class, UUID.class ), ParameterizedTypeName.from( Set.class, UUID.class ) );
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals( ClassName.from( List.class ), ParameterizedTypeName.from( List.class, String.class ) );
    }   //  equalsAndHashCodeParameterizedTypeName()

    @Test
    public void equalsAndHashCodePrimitive()
    {
        assertEqualsHashCodeAndToString( Primitives.BOOLEAN, TypeNameImpl.BOOLEAN_PRIMITIVE );
        assertEqualsHashCodeAndToString( Primitives.BYTE, TypeNameImpl.BYTE_PRIMITIVE );
        assertEqualsHashCodeAndToString( Primitives.CHAR, TypeNameImpl.CHAR_PRIMITIVE );
        assertEqualsHashCodeAndToString( Primitives.DOUBLE, TypeNameImpl.DOUBLE_PRIMITIVE );
        assertEqualsHashCodeAndToString( Primitives.FLOAT, TypeNameImpl.FLOAT_PRIMITIVE );
        assertEqualsHashCodeAndToString( Primitives.INT, TypeNameImpl.INT_PRIMITIVE );
        assertEqualsHashCodeAndToString( Primitives.LONG, TypeNameImpl.LONG_PRIMITIVE );
        assertEqualsHashCodeAndToString( Primitives.SHORT, TypeNameImpl.SHORT_PRIMITIVE );
        assertEqualsHashCodeAndToString( Primitives.VOID, TypeNameImpl.VOID_PRIMITIVE );
    }   //  equalsAndHashCodePrimitive()

    @Test
    public void equalsAndHashCodeTypeVariableName()
    {
        assertEqualsHashCodeAndToString( TypeName.from( Object.class ), TypeName.from( Object.class ) );
        final var typeVar1 = TypeVariableName.from( "T", Comparator.class, Serializable.class );
        final var typeVar2 = TypeVariableName.from( "T", Comparator.class, Serializable.class );
        assertEqualsHashCodeAndToString( typeVar1, typeVar2 );
    }   //  equalsAndHashCodeTypeVariableName()

    @Test
    public void equalsAndHashCodeWildcardTypeName()
    {
        assertEqualsHashCodeAndToString( WildcardTypeName.subtypeOf( Object.class ), WildcardTypeName.subtypeOf( Object.class ) );
        assertEqualsHashCodeAndToString( WildcardTypeName.subtypeOf( Serializable.class ), WildcardTypeName.subtypeOf( Serializable.class ) );
        assertEqualsHashCodeAndToString( WildcardTypeName.supertypeOf( String.class ), WildcardTypeName.supertypeOf( String.class ) );
    }   //  equalsAndHashCodeWildcardTypeName()

    @SafeVarargs
    @SuppressWarnings( "static-method" )
    protected final <E extends Enum<E>> E generic( final E... values ) { return values [0]; }

    @Test
    public void genericType() throws Exception
    {
        final var recursiveEnum = getClass().getDeclaredMethod( "generic", Enum [].class );
        TypeName.from( recursiveEnum.getReturnType() );
        TypeName.from( recursiveEnum.getGenericReturnType() );
        final var genericTypeName = TypeName.from( recursiveEnum.getParameterTypes() [0] );
        TypeName.from( recursiveEnum.getGenericParameterTypes() [0] );

        //---* Make sure the generic argument is present *---------------------
        assertThat( genericTypeName.toString() ).contains( "Enum" );
    }   //  genericType()

    @Test
    public void innerClassInGenericType() throws Exception
    {
        final var genericStringInner = getClass().getDeclaredMethod( "testGenericStringInner" );
        TypeName.from( genericStringInner.getReturnType() );
        final var genericTypeName = TypeName.from( genericStringInner.getGenericReturnType() );
        assertNotEquals( TypeName.from( genericStringInner.getGenericReturnType() ), TypeName.from( getClass().getDeclaredMethod( "testGenericIntInner" ).getGenericReturnType() ) );

        //---* Make sure the generic argument is present *---------------------
        assertThat( genericTypeName.toString() ).isEqualTo( TestGeneric.class.getCanonicalName() + "<java.lang.String>.Inner" );
    }   //  innerClassInGenericType()

    @Test
    public void innerGenericInGenericType() throws Exception
    {
        final var genericStringInner = getClass().getDeclaredMethod( "testGenericInnerLong" );
        TypeName.from( genericStringInner.getReturnType() );
        final var genericTypeName = TypeName.from( genericStringInner.getGenericReturnType() );
        assertNotEquals( TypeName.from( genericStringInner.getGenericReturnType() ), TypeName.from( getClass().getDeclaredMethod( "testGenericInnerInt" ).getGenericReturnType() ) );

        //---* Make sure the generic argument is present *---------------------
        assertThat( genericTypeName.toString() ).isEqualTo( TestGeneric.class.getCanonicalName() + "<java.lang.Short>.InnerGeneric<java.lang.Long>" );
    }   //  innerGenericInGenericType()

    @Test
    public void innerStaticInGenericType() throws Exception
    {
        final var staticInGeneric = getClass().getDeclaredMethod( "testNestedNonGeneric" );
        TypeName.from( staticInGeneric.getReturnType() );
        final var typeName = TypeName.from( staticInGeneric.getGenericReturnType() );

        //---* Make sure there are no generic arguments *----------------------
        assertThat( typeName.toString() ).isEqualTo( TestGeneric.class.getCanonicalName() + ".NestedNonGeneric" );
    }   //  innerStaticInGenericType()

    @SuppressWarnings( "static-method" )
    @Test
    public void isBoxedPrimitive() throws Exception
    {
        assertThat( Primitives.INT.isBoxedPrimitive() ).isFalse();
        assertThat( ClassName.from( "java.lang", "Integer" ).isBoxedPrimitive() ).isTrue();
        assertThat( ClassName.from( "java.lang", "String" ).isBoxedPrimitive() ).isFalse();
        assertThat( Primitives.VOID.isBoxedPrimitive() ).isFalse();
        assertThat( ClassName.from( "java.lang", "Void" ).isBoxedPrimitive() ).isFalse();
    }   //  isBoxedPrimitive()

    @SuppressWarnings( "static-method" )
    @Test
    public void isPrimitive() throws Exception
    {
        assertThat( Primitives.INT.isPrimitive() ).isTrue();
        assertThat( ClassName.from( "java.lang", "Integer" ).isPrimitive() ).isFalse();
        assertThat( ClassName.from( "java.lang", "String" ).isPrimitive() ).isFalse();
        assertThat( Primitives.VOID.isPrimitive() ).isFalse();
        assertThat( ClassName.from( "java.lang", "Void" ).isPrimitive() ).isFalse();
    }   //  isPrimitive()

    @SuppressWarnings( "UseOfConcreteClass" )
    protected static TestGeneric<Short>.InnerGeneric<Integer> testGenericInnerInt() { return null; }

    @SuppressWarnings( "UseOfConcreteClass" )
    protected static TestGeneric<Short>.InnerGeneric<Long> testGenericInnerLong() { return null; }

    @SuppressWarnings( "UseOfConcreteClass" )
    protected static TestGeneric<Integer>.Inner testGenericIntInner() { return null; }

    @SuppressWarnings( "UseOfConcreteClass" )
    protected static TestGeneric<String>.Inner testGenericStringInner() { return null; }

    @SuppressWarnings( "UseOfConcreteClass" )
    protected static TestGeneric.NestedNonGeneric testNestedNonGeneric() { return null; }
}
//  class TestTypeName

/*
 *  End of File
 */