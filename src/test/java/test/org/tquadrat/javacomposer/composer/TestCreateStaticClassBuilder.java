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

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.util.StringUtils.format;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.EmptyArgumentException;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the method
 *  {@link JavaComposer#createStaticClassBuilder(CharSequence)}
 *  in the class
 *  {@link org.tquadrat.foundation.javacomposer.JavaComposer}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestCreateStaticClassBuilder.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestCreateStaticClassBuilder.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestCreateStaticClassBuilder" )
public class TestCreateStaticClassBuilder extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for the method
     *  {@link JavaComposer#createStaticClassBuilder(CharSequence)}.
     */
    @Test
    final void testCreateStaticClassBuilder()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var name = "StaticClass";
        final var builder = composer.createStaticClassBuilder( name );
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
        final var actual = composer.javaFileBuilder( "org.tquadrat.test", builder.build() )
            .build()
            .toString();
        assertEquals( expected, actual );
    }   //  testCreateStaticClassBuilder()

    /**
     *  Tests for the method
     *  {@link JavaComposer#createStaticClassBuilder(CharSequence)}.
     */
    @Test
    final void testCreateStaticClassBuilderWithEmptyArgument()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = EmptyArgumentException.class;

        final var name = EMPTY_STRING;
        try
        {
            final var builder = composer.createStaticClassBuilder( name );
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
    }   //  testCreateStaticClassBuilderWithEmptyArgument()

    /**
     *  Tests for the method
     *  {@link JavaComposer#createStaticClassBuilder(CharSequence)}.
     */
    @Test
    final void testCreateStaticClassBuilderWithInvalidArgument()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = ValidationException.class;

        final var name = "1234Class";
        try
        {
            final var builder = composer.createStaticClassBuilder( name );
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
    }   //  testCreateStaticClassBuilderWithInvalidArgument()

    /**
     *  Tests for the method
     *  {@link JavaComposer#createStaticClassBuilder(CharSequence)}.
     */
    @Test
    final void testCreateStaticClassBuilderWithNullArgument()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = NullArgumentException.class;

        final CharSequence name = null;
        try
        {
            final var builder = composer.createStaticClassBuilder( name );
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
    }   //  testCreateStaticClassBuilderWithNullArgument()
}
//  class TestCreateStaticClassBuilder

/*
 *  End of File
 */