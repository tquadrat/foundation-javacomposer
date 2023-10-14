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

package test.org.tquadrat.javacomposer.composer;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.javacomposer.Layout.LAYOUT_FOUNDATION;

import java.util.Collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.EmptyArgumentException;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the methods
 *  {@link JavaComposer#createHashCodeMethod(CodeBlock)},
 *  {@link JavaComposer#createHashCodeMethod(FieldSpec...)}
 *  and
 *  {@link JavaComposer#createHashCodeMethod(Collection)}
 *  in the class
 *  {@link org.tquadrat.foundation.javacomposer.JavaComposer}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestCreateHashCodeMethod.java 1076 2023-10-03 18:36:07Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestCreateHashCodeMethod.java 1076 2023-10-03 18:36:07Z tquadrat $" )
@DisplayName( "TestCreateHashCodeMethod" )
public class TestCreateHashCodeMethod extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for the methods
     *  {@link JavaComposer#createHashCodeMethod(CodeBlock)},
     *  {@link JavaComposer#createHashCodeMethod(FieldSpec...)}
     *  and
     *  {@link JavaComposer#createHashCodeMethod(Collection)}.
     */
    @Test
    final void testCreateHashCodeMethod()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var body = composer.statementOf( "return $L", -1 );
        final var fields = new FieldSpec [] { composer.fieldBuilder( String.class, "field1" ).build(), composer.fieldBuilder( int.class, "field2" ).build() };
        var candidate = composer.createHashCodeMethod( body );
        assertNotNull( candidate );
        var expected =
            """
            /**
             * {@inheritDoc}
             */
            @java.lang.Override
            public int hashCode() {
              return -1;
            }
            """;
        var actual = candidate.toString();
        assertEquals( expected, actual );

        candidate = composer.createHashCodeMethod( fields );
        assertNotNull( candidate );
        expected =
            """
            /**
             * {@inheritDoc}
             */
            @java.lang.Override
            public int hashCode() {
              return hash( field1, field2 );
            }
            """;
        actual = candidate.toString();
        assertEquals( expected, actual );

        candidate = composer.createHashCodeMethod( asList( fields ) );
        assertNotNull( candidate );
        actual = candidate.toString();
        assertEquals( expected, actual );
    }   //  testCreateHashCodeMethod()

    /**
     *  Tests for the methods
     *  {@link JavaComposer#createHashCodeMethod(CodeBlock)},
     *  {@link JavaComposer#createHashCodeMethod(FieldSpec...)}
     *  and
     *  {@link JavaComposer#createHashCodeMethod(Collection)}.
     */
    @Test
    final void testCreateHashCodeMethodFoundation()
    {
        skipThreadTest();

        final var composer = new JavaComposer( LAYOUT_FOUNDATION);

        final var body = composer.statementOf( "return $L", -1 );
        final var fields = new FieldSpec [] { composer.fieldBuilder( String.class, "field1" ).build(), composer.fieldBuilder( int.class, "field2" ).build() };
        var candidate = composer.createHashCodeMethod( body );
        assertNotNull( candidate );
        var expected =
            """
            /**
             * {@inheritDoc}
             */
            @java.lang.Override
            public int hashCode() {
              return -1;
            }
            """;
        var actual = candidate.toString();
        assertEquals( expected, actual );

        candidate = composer.createHashCodeMethod( fields );
        assertNotNull( candidate );
        expected =
            """
            /**
             * {@inheritDoc}
             */
            @java.lang.Override
            public int hashCode() {
              return hash( field1, field2 );
            }
            """;
        actual = candidate.toString();
        assertEquals( expected, actual );

        candidate = composer.createHashCodeMethod( asList( fields ) );
        assertNotNull( candidate );
        actual = candidate.toString();
        assertEquals( expected, actual );
    }   //  testCreateHashCodeMethodFoundation()

    /**
     *  Tests for the methods
     *  {@link JavaComposer#createHashCodeMethod(FieldSpec...)}
     *  and
     *  {@link JavaComposer#createHashCodeMethod(Collection)}.
     */
    @Test
    final void testCreateHashCodeMethodWithEmptyArgument()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = EmptyArgumentException.class;
        try
        {
            final var candidate = composer.createHashCodeMethod();
            assertNotNull( candidate );
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
        try
        {
            final var candidate = composer.createHashCodeMethod( emptyList() );
            assertNotNull( candidate );
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
    }   //  testCreateHashCodeMethodWithNullArgument()

    /**
     *  Tests for the methods
     *  {@link JavaComposer#createHashCodeMethod(CodeBlock)},
     *  {@link JavaComposer#createHashCodeMethod(FieldSpec...)}
     *  and
     *  {@link JavaComposer#createHashCodeMethod(Collection)}.
     */
    @Test
    final void testCreateHashCodeMethodWithNullArgument()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final CodeBlock body = null;
        final FieldSpec [] fieldSpecsArray = null;
        final Collection<FieldSpec> fieldSpecsList = null;

        final Class<? extends Throwable> expectedException = NullArgumentException.class;
        try
        {
            final var candidate = composer.createHashCodeMethod( body );
            assertNotNull( candidate );
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
        try
        {
            final var candidate = composer.createHashCodeMethod( fieldSpecsArray );
            assertNotNull( candidate );
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
        try
        {
            final var candidate = composer.createHashCodeMethod( fieldSpecsList );
            assertNotNull( candidate );
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
    }   //  testCreateHashCodeMethodWithNullArgument()
}
//  class TestCreateHashCodeMethod

/*
 *  End of File
 */