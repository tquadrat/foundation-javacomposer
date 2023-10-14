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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the method
 *  {@link org.tquadrat.foundation.javacomposer.Shortcuts#getHashCodeMethod(CodeBlock)}
 *  in the class
 *  {@link org.tquadrat.foundation.javacomposer.Shortcuts}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestGetHashCodeMethod.java 1076 2023-10-03 18:36:07Z tquadrat $
 */
@SuppressWarnings( {"MisorderedAssertEqualsArguments", "removal"} )
@ClassVersion( sourceVersion = "$Id: TestGetHashCodeMethod.java 1076 2023-10-03 18:36:07Z tquadrat $" )
@DisplayName( "TestGetHashCodeMethod" )
public class TestGetHashCodeMethod extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for the method
     *  {@link org.tquadrat.foundation.javacomposer.Shortcuts#getHashCodeMethod(CodeBlock)}.
     */
    @Test
    final void testGetHashCodeMethod()
    {
        skipThreadTest();

        final var body = CodeBlock.of( "return $L", -1 );
        final var candidate = org.tquadrat.foundation.javacomposer.Shortcuts.getHashCodeMethod( body );
        assertNotNull( candidate );
        final var expected =
            """
            /**
             * {@inheritDoc}
             */
            @java.lang.Override
            public int hashCode() {
              return -1}
            """;
        final var actual = candidate.toString();
        assertEquals( expected, actual );
    }   //  testGetHashCodeMethod()

    /**
     *  Tests for the method
     *  {@link org.tquadrat.foundation.javacomposer.Shortcuts#getHashCodeMethod(CodeBlock)}.
     */
    @Test
    final void testGetHashCodeMethodWithNullArgument()
    {
        skipThreadTest();

        final CodeBlock body = null;
        final Class<? extends Throwable> expectedException = NullArgumentException.class;
        try
        {
            final var candidate = org.tquadrat.foundation.javacomposer.Shortcuts.getHashCodeMethod( body );
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
    }   //  testGetHashCodeMethodWithNullArgument()
}
//  class TestGetHashCodeMethod

/*
 *  End of File
 */