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

package test.org.tquadrat.javacomposer.shortcuts;

import static java.lang.String.format;
import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.EmptyArgumentException;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.JavaFile;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the method
 *  {@link org.tquadrat.foundation.javacomposer.Shortcuts#getStaticClassBuilder(CharSequence)}
 *  in the class
 *  {@link org.tquadrat.foundation.javacomposer.Shortcuts}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestGetStaticClassBuilder.java 1076 2023-10-03 18:36:07Z tquadrat $
 */
@SuppressWarnings( {"MisorderedAssertEqualsArguments", "removal"} )
@ClassVersion( sourceVersion = "$Id: TestGetStaticClassBuilder.java 1076 2023-10-03 18:36:07Z tquadrat $" )
@DisplayName( "TestGetStaticClassBuilder" )
public class TestGetStaticClassBuilder extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for the method
     *  {@link org.tquadrat.foundation.javacomposer.Shortcuts#getStaticClassBuilder(CharSequence)}.
     */
    @Test
    final void testGetStaticClassBuilder()
    {
        skipThreadTest();

        final var name = "StaticClass";
        final var builder = org.tquadrat.foundation.javacomposer.Shortcuts.getStaticClassBuilder( name );
        final var expected =
            """
            package org.tquadrat.test;

            import org.tquadrat.foundation.annotation.ClassVersion;
            import org.tquadrat.foundation.annotation.UtilityClass;
            import org.tquadrat.foundation.exception.PrivateConstructorForStaticClassCalledError;

            @UtilityClass
            @ClassVersion(sourceVersion = "Generated with JavaComposer", isGenerated = true)
            public final class StaticClass {
              /**
               * No instance allowed for this class!
               */
              private StaticClass() {
                throw new PrivateConstructorForStaticClassCalledError( StaticClass.class );
              }
            }
            """;
        final var actual = JavaFile.builder( "org.tquadrat.test", builder.build() ).build().toString();
        assertEquals( expected, actual );
    }   //  testGetStaticClassBuilder()

    /**
     *  Tests for the method
     *  {@link org.tquadrat.foundation.javacomposer.Shortcuts#getStaticClassBuilder(CharSequence)}.
     */
    @Test
    final void testGetStaticClassBuilderWithEmptyArgument()
    {
        skipThreadTest();

        final Class<? extends Throwable> expectedException = EmptyArgumentException.class;

        final var name = EMPTY_STRING;
        try
        {
            final var builder = org.tquadrat.foundation.javacomposer.Shortcuts.getStaticClassBuilder( name );
            assertNotNull( builder );
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
    }   //  testGetStaticClassBuilderWithEmptyArgument()

    /**
     *  Tests for the method
     *  {@link org.tquadrat.foundation.javacomposer.Shortcuts#getStaticClassBuilder(CharSequence)}.
     */
    @Test
    final void testGetStaticClassBuilderWithInvalidArgument()
    {
        skipThreadTest();

        final Class<? extends Throwable> expectedException = ValidationException.class;

        final var name = "1234Class";
        try
        {
            final var builder = org.tquadrat.foundation.javacomposer.Shortcuts.getStaticClassBuilder( name );
            assertNotNull( builder );
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
    }   //  testGetStaticClassBuilderWithInvalidArgument()

    /**
     *  Tests for the method
     *  {@link org.tquadrat.foundation.javacomposer.Shortcuts#getStaticClassBuilder(CharSequence)}.
     */
    @Test
    final void testGetStaticClassBuilderWithNullArgument()
    {
        skipThreadTest();

        final Class<? extends Throwable> expectedException = NullArgumentException.class;

        final CharSequence name = null;
        try
        {
            final var builder = org.tquadrat.foundation.javacomposer.Shortcuts.getStaticClassBuilder( name );
            assertNotNull( builder );
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
    }   //  testGetStaticClassBuilderWithNullArgument()
}
//  class TestShortcuts

/*
 *  End of File
 */