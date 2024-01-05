/*
 * ============================================================================
 *  Copyright © 2002-2024 by Thomas Thrien.
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

package test.org.tquadrat.javacomposer.lambdaspec;

import static java.lang.String.format;
import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_Object_ARRAY;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.LambdaSpec;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the methods
 *  {@link LambdaSpec.Builder#addStatement(CodeBlock)}
 *  and
 *  {@link LambdaSpec.Builder#addStatement(String, Object...)}
 *  from the class
 *  {@link LambdaSpec.Builder}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestBuilderAddStatement.java 1085 2024-01-05 16:23:28Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestBuilderAddStatement.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@DisplayName( "TestBuilderAddStatement" )
public class TestBuilderAddStatement extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for implementation of the method
     *  {@link LambdaSpec.Builder#addStatement(String, Object...)}.
     */
    @Test
    final void testBuilderAddStatementWithNullArgument()
    {
        skipThreadTest();

        final var composer = new JavaComposer();
        final var candidate = composer.lambdaBuilder();

        final Class<? extends Throwable> expectedException = NullArgumentException.class;

        String format;
        Object [] args;
        format = null;
        args = EMPTY_Object_ARRAY;
        try
        {
            candidate.addStatement( format, args );
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

        format = "Format";
        args = null;
        try
        {
            candidate.addStatement( format, args );
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
    }   //  testBuilderAddStatementWithNullArgument()
}
//  class TestLambdaSpec

/*
 *  End of File
 */