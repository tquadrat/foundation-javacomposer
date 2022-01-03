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

package test.org.tquadrat.javacomposer.lambdaspec;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_Object_ARRAY;
import static org.tquadrat.foundation.util.StringUtils.format;

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
 *  {@link LambdaSpec.Builder#addCode(CodeBlock)}
 *  and
 *  {@link LambdaSpec.Builder#addCode(String, Object...)}
 *  from the class
 *  {@link LambdaSpec.Builder}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestBuilderAddCode.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestBuilderAddCode.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestBuilderAddCode" )
public class TestBuilderAddCode extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for implementations of the methods
     *  {@link LambdaSpec.Builder#addCode(CodeBlock)}
     *  and
     *  {@link LambdaSpec.Builder#addCode(String, Object...)}.
     */
    @Test
    final void testBuilderAddCodeWithNullArgument()
    {
        skipThreadTest();

        final var composer = new JavaComposer();
        final var candidate = composer.lambdaBuilder();

        final Class<? extends Throwable> expectedException = NullArgumentException.class;

        final CodeBlock codeBlock = null;
        try
        {
            candidate.addCode( codeBlock );
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

        String format;
        Object [] args;
        format = null;
        args = EMPTY_Object_ARRAY;
        try
        {
            candidate.addCode( format, args );
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
            candidate.addCode( format, args );
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
    }   //  testBuilderAddCodeNull()
}
//  class testBuilderAddCodeWithNullArgument

/*
 *  End of File
 */