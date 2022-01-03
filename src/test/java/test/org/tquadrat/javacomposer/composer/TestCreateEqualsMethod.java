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
 *  {@link JavaComposer#createEqualsMethod(CodeBlock)}
 *  in the class
 *  {@link org.tquadrat.foundation.javacomposer.JavaComposer}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestCreateEqualsMethod.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestCreateEqualsMethod.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestCreateEqualsMethod" )
public class TestCreateEqualsMethod extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for the method
     *  {@link JavaComposer#createEqualsMethod(CodeBlock)}.
     */
    @Test
    final void testCreateEqualsMethod()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var body = composer.codeBlockOf( "return $L", true );
        final var candidate = composer.createEqualsMethod( body );
        assertNotNull( candidate );
        final var expected =
            """
            /**
             * {@inheritDoc}
             */
            @java.lang.Override
            public boolean equals(final java.lang.Object o) {
              return true}
            """;
        final var actual = candidate.toString();
        assertEquals( expected, actual );
    }   //  testCreateEqualsMethod()

    /**
     *  Tests for the method
     *  {@link JavaComposer#createEqualsMethod(CodeBlock)}.
     */
    @Test
    final void testCreateEqualsMethodWithNullArgument()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final CodeBlock body = null;
        final Class<? extends Throwable> expectedException = NullArgumentException.class;
        try
        {
            final var candidate = composer.createEqualsMethod( body );
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
    }   //  testCreateEqualsMethodWithNullArgument()
}
//  class TestCreateEqualsMethod

/*
 *  End of File
 */