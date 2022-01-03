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

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.util.StringUtils.format;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.EmptyArgumentException;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.javacomposer.Shortcuts;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the methods
 *  {@link org.tquadrat.foundation.javacomposer.Shortcuts#createReturnStatement()}
 *  and
 *  {@link Shortcuts#createReturnStatement(String)}
 *  in the class
 *  {@link org.tquadrat.foundation.javacomposer.Shortcuts}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestCreateReturnStatement.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( {"MisorderedAssertEqualsArguments", "removal"} )
@ClassVersion( sourceVersion = "$Id: TestCreateReturnStatement.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestCreateReturnStatement" )
public class TestCreateReturnStatement extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for the methods
     *  {@link org.tquadrat.foundation.javacomposer.Shortcuts.Shortcuts#createReturnStatement()}
     *  and
     *  {@link org.tquadrat.foundation.javacomposer.Shortcuts.Shortcuts#createReturnStatement(String)}.
     */
    @Test
    final void testCreateReturnStatement()
    {
        skipThreadTest();

        final var expected =
            """

            //---* Done *----------------------------------------------------------
            return retValue;
            """;
        final var actual = org.tquadrat.foundation.javacomposer.Shortcuts.createReturnStatement().toString();
        assertEquals( expected, actual );
    }   //  testCreateReturnStatement()

    /**
     *  Tests for the methods
     *  {@link org.tquadrat.foundation.javacomposer.Shortcuts.Shortcuts#createReturnStatement()}
     *  and
     *  {@link org.tquadrat.foundation.javacomposer.Shortcuts.Shortcuts#createReturnStatement(String)}.
     */
    @Test
    final void testCreateReturnStatementWithEmptyArgument()
    {
        skipThreadTest();

        final Class<? extends Throwable> expectedException = EmptyArgumentException.class;

        final var name = EMPTY_STRING;
        try
        {
            org.tquadrat.foundation.javacomposer.Shortcuts.createReturnStatement( name );
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
    }   //  testCreateReturnStatementWithEmptyArgument()

    /**
     *  Tests for the methods
     *  {@link org.tquadrat.foundation.javacomposer.Shortcuts.Shortcuts#createReturnStatement()}
     *  and
     *  {@link org.tquadrat.foundation.javacomposer.Shortcuts.Shortcuts#createReturnStatement(String)}.
     */
    @Test
    final void testCreateReturnStatementWithNullArgument()
    {
        skipThreadTest();

        final Class<? extends Throwable> expectedException = NullArgumentException.class;

        final String name = null;
        try
        {
            org.tquadrat.foundation.javacomposer.Shortcuts.createReturnStatement( name );
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
    }   //  testCreateReturnStatement()
}
//  class TestCreateReturnStatement

/*
 *  End of File
 */