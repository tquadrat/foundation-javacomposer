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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import javax.lang.model.element.VariableElement;
import java.lang.reflect.Parameter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the methods
 *  {@link JavaComposer#createParameter(VariableElement)}
 *  and
 *  {@link JavaComposer#createParameter(Parameter)}
 *  from the class
 *  {@link JavaComposer}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestCreateParameter.java 1076 2023-10-03 18:36:07Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestCreateParameter.java 1076 2023-10-03 18:36:07Z tquadrat $" )
@DisplayName( "TestCreateParameter" )
public class TestCreateParameter extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for the methods
     *  {@link JavaComposer#createParameter(VariableElement)}
     *  and
     *  {@link JavaComposer#createParameter(Parameter)}.
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @Test
    final void testCreateParameterWithNullArgument() throws Exception
    {
        skipThreadTest();

        final var candidate = new JavaComposer();

        final Class<? extends Throwable> expectedException = NullArgumentException.class;
        try
        {
            candidate.createParameter( (VariableElement) null );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
        try
        {
            candidate.createParameter( (Parameter) null );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }   //  testCreateParameterWithNullArgument()
}
//  class TestCreateParameter

/*
 *  End of File
 */