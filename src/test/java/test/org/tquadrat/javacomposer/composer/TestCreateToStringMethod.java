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
import static org.tquadrat.foundation.util.StringUtils.format;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the method
 *  {@link org.tquadrat.foundation.javacomposer.JavaComposer#createsToStringMethod(CodeBlock)}
 *  in the class
 *  {@link org.tquadrat.foundation.javacomposer.JavaComposer}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestCreateToStringMethod.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestCreateToStringMethod.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "org.tquadrat.foundation.javacomposer.shortcuts.TestCreateToStringMethod" )
public class TestCreateToStringMethod extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for the method
     *  {@link org.tquadrat.foundation.javacomposer.JavaComposer#createsToStringMethod(CodeBlock)}.
     */
    @Test
    final void testCreateToStringMethod()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var body = composer.codeBlockOf( "return $S", "The String" );
        var candidate = composer.createToStringMethod( body );
        assertNotNull( candidate );
        var expected =
            """
            /**
             * {@inheritDoc}
             */
            @java.lang.Override
            public java.lang.String toString() {
              return "The String"}
            """;
        var actual = candidate.toString();
        assertEquals( expected, actual );

        candidate = composer.createToStringMethod( composer.emptyCodeBlock() );
        assertNotNull( candidate );
        expected =
            """
            /**
             * {@inheritDoc}
             */
            @java.lang.Override
            public java.lang.String toString() {
            }
            """;
        actual = candidate.toString();
        assertEquals( expected, actual );
    }   //  testCreateToStringMethod()

    /**
     *  Tests for the method
     *  {@link org.tquadrat.foundation.javacomposer.JavaComposer#createsToStringMethod(CodeBlock)}.
     */
    @Test
    final void testCreateToStringMethodWithNullArgument()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final CodeBlock body = null;
        final Class<? extends Throwable> expectedException = NullArgumentException.class;
        try
        {
            final var candidate = composer.createToStringMethod( body );
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
    }   //  testCreateToStringMethodWithNullArgument()
}
//  class TestCreateToStringMethod

/*
 *  End of File
 */