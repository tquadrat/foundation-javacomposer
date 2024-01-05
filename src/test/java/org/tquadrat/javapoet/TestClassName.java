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
import static org.mockito.Mockito.when;
import static org.tquadrat.foundation.javacomposer.internal.ClassNameImpl.OBJECT;

import javax.lang.model.element.TypeElement;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.javapoet.helper.CompilationRule;

/**
 *  The tests for the class
 *  {@link ClassName}
 *  that came with the original library.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestClassName.java 1085 2024-01-05 16:23:28Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestClassName.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@SuppressWarnings( {"javadoc", "MisorderedAssertEqualsArguments", "InnerClassTooDeeplyNested"} )
@RunWith( JUnit4.class )
@DisplayName( "TestClassName" )
public final class TestClassName
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    @SuppressWarnings( "PackageVisibleInnerClass" )
    static class $Outer
    {
        @SuppressWarnings( {"InnerClassTooDeeplyNested", "PackageVisibleInnerClass"} )
        static class $Inner
        { /* Empty Class */ }
        //  class $Inner
    }
    //  class $Outer

    @SuppressWarnings( "PackageVisibleInnerClass" )
    static class OuterClass
    {
        @SuppressWarnings( {"InnerClassTooDeeplyNested", "PackageVisibleInnerClass"} )
        static class InnerClass
        { /* Empty Class */ }
        //  class InnerClass
    }
    //  class OuterRule

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    @SuppressWarnings( "InstanceVariableOfConcreteClass" )
    @Rule
    public CompilationRule m_Compilation = new CompilationRule();

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    @SuppressWarnings( "static-method" )
    @Test
    public void classNameFromClass()
    {
        assertThat( ClassName.from( Object.class ).toString() ).isEqualTo( "java.lang.Object" );

        var actual = ClassName.from( OuterClass.InnerClass.class ).toString();
        var expected = "TestClassName.OuterClass.InnerClass";
        assertEquals( expected, actual );

        actual = (ClassName.from( new Object(){/**/}.getClass() )).toString();
        expected = "TestClassName$1";
        assertEquals( expected, actual );

        actual = (ClassName.from( new Object()
        {
            final Object m_Inner = new Object()
            {/**/};
        }.m_Inner.getClass() )).toString();
        expected = "TestClassName$2$1";
        assertEquals( expected, actual );

        actual = (ClassName.from( $Outer.class )).toString();
        expected = "TestClassName.$Outer";
        assertEquals( expected, actual );

        actual = (ClassName.from( $Outer.$Inner.class )).toString();
        expected = "TestClassName.$Outer.$Inner";
        assertEquals( expected, actual );
    }

    @Test
    public void classNameFromTypeElement()
    {
        final var elements = m_Compilation.getElements();
        final var object = elements.getTypeElement( Object.class.getCanonicalName() );
        assertThat( ClassName.from( object ).toString() ).isEqualTo( "java.lang.Object" );
        final var outer = elements.getTypeElement( $Outer.class.getCanonicalName() );
        assertThat( ClassName.from( outer ).toString() ).isEqualTo( "com.squareup.javapoet.ClassNameTest.$Outer" );
        final var inner = elements.getTypeElement( $Outer.$Inner.class.getCanonicalName() );
        assertThat( ClassName.from( inner ).toString() ).isEqualTo( "com.squareup.javapoet.ClassNameTest.$Outer.$Inner" );
    }   //  classNameFromTypeElement()

    /**
     * Buck builds with "source-based ABI generation" and those builds don't
     * support
     * {@link TypeElement#getKind()}. Test to confirm that we don't use that
     * API.
     */
    @Test
    public void classNameFromTypeElementDoesntUseGetKind()
    {
        final var elements = m_Compilation.getElements();
        final var object = elements.getTypeElement( Object.class.getCanonicalName() );
        assertThat( ClassName.from( preventGetKind( object ) ).toString() ).isEqualTo( "java.lang.Object" );
        final var outer = elements.getTypeElement( $Outer.class.getCanonicalName() );
        assertThat( ClassName.from( preventGetKind( outer ) ).toString() ).isEqualTo( "com.squareup.javapoet.ClassNameTest.$Outer" );
        final var inner = elements.getTypeElement( $Outer.$Inner.class.getCanonicalName() );
        assertThat( ClassName.from( preventGetKind( inner ) ).toString() ).isEqualTo( "com.squareup.javapoet.ClassNameTest.$Outer.$Inner" );
    }   //  classNameFromTypeElementDoesntUseGetKind()

    @SuppressWarnings( "static-method" )
    @Test
    public void createNestedClass()
    {
        final var foo = ClassName.from( "com.example", "Foo" );
        final var bar = foo.nestedClass( "Bar" );
        assertThat( bar ).isEqualTo( ClassName.from( "com.example", "Foo", "Bar" ) );
        final var baz = bar.nestedClass( "Baz" );
        assertThat( baz ).isEqualTo( ClassName.from( "com.example", "Foo", "Bar", "Baz" ) );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void fromClassRejectionTypes()
    {
        try
        {
            ClassName.from( int.class );
            fail( "Expected Exception was not thrown" );
        }
        catch( @SuppressWarnings( "unused" ) final IllegalArgumentException ignored )
        { /* Deliberately ignored */}
        try
        {
            ClassName.from( void.class );
            fail( "Expected Exception was not thrown" );
        }
        catch( @SuppressWarnings( "unused" ) final IllegalArgumentException ignored )
        { /* Deliberately ignored */}
        try
        {
            ClassName.from( Object [].class );
            fail( "Expected Exception was not thrown" );
        }
        catch( @SuppressWarnings( "unused" ) final IllegalArgumentException ignored )
        { /* Deliberately ignored */}
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void peerClass()
    {
        assertThat( ClassName.from( Double.class ).peerClass( "Short" ) ).isEqualTo( ClassName.from( Short.class ) );
        assertThat( ClassName.from( "", "Double" ).peerClass( "Short" ) ).isEqualTo( ClassName.from( "", "Short" ) );
        assertThat( ClassName.from( "a.b", "Combo", "Taco" ).peerClass( "Burrito" ) ).isEqualTo( ClassName.from( "a.b", "Combo", "Burrito" ) );
    }

    /**
     * Returns a new instance like {@code object} that throws on
     * {@code getKind()}.
     */
    private TypeElement preventGetKind( final TypeElement object )
    {
        final var spy = Mockito.spy( object );
        when( spy.getKind() ).thenThrow( new AssertionError() );
        when( spy.getEnclosingElement() ).thenAnswer( invocation ->
        {
            final var enclosingElement = invocation.callRealMethod();
            return enclosingElement instanceof TypeElement ? preventGetKind( (TypeElement) enclosingElement ) : enclosingElement;
        } );
        return spy;
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void reflectionName()
    {
        assertEquals( "java.lang.Object", OBJECT.reflectionName() );
        assertEquals( "java.lang.Thread$State", ClassName.from( Thread.State.class ).reflectionName() );
        assertEquals( "java.util.Map$Entry", ClassName.from( Map.Entry.class ).reflectionName() );
        assertEquals( "Foo", ClassName.from( "", "Foo" ).reflectionName() );
        assertEquals( "Foo$Bar$Baz", ClassName.from( "", "Foo", "Bar", "Baz" ).reflectionName() );
        assertEquals( "a.b.c.Foo$Bar$Baz", ClassName.from( "a.b.c", "Foo", "Bar", "Baz" ).reflectionName() );
    }
}
//  class TestClassName

/*
 *  End of File
 */