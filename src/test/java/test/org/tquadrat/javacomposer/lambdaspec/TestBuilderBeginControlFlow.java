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

import static java.lang.String.format;
import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_Object_ARRAY;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.LambdaSpec;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the method
 *  {@link LambdaSpec.Builder#beginControlFlow(String, Object...)}
 *  from the class
 *  {@link LambdaSpec.Builder}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestBuilderBeginControlFlow.java 1076 2023-10-03 18:36:07Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestBuilderBeginControlFlow.java 1076 2023-10-03 18:36:07Z tquadrat $" )
@DisplayName( "TestBuilderBeginControlFlow" )
public class TestBuilderBeginControlFlow extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for the implementation of the method
     *  {@link LambdaSpec.Builder#beginControlFlow(String, Object...)}.
     */
    @Test
    final void testBuilderBeginControlFlowWithNullArgument()
    {
        skipThreadTest();

        final var composer = new JavaComposer();
        final var candidate = composer.lambdaBuilder();

        final Class<? extends Throwable> expectedException = NullArgumentException.class;

        String controlFlow;
        Object [] args;
        controlFlow = null;
        args = EMPTY_Object_ARRAY;
        try
        {
            candidate.beginControlFlow( controlFlow, args );
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

        controlFlow = "Format";
        args = null;
        try
        {
            candidate.beginControlFlow( controlFlow, args );
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
    }   //  testBuilderBeginControlFlowWithNullArgument()
}
//  class TestBuilderBeginControlFlow

/*
 *  End of File
 */