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

package test.org.tquadrat.javacomposer.typespec;

import static java.lang.String.format;
import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.testutil.TestBaseClass;
import org.tquadrat.javapoet.TestTypeSpec;

/**
 *  Tests for the class
 *  {@link TypeSpec}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestEnumConstants.java 1076 2023-10-03 18:36:07Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestEnumConstants.java 1076 2023-10-03 18:36:07Z tquadrat $" )
@DisplayName( "TestEnumConstants" )
public class TestEnumConstants extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  This test failed in
     *  {@link TestTypeSpec}
     *  because a
     *  {@link org.tquadrat.foundation.exception.NullArgumentException}
     *  was thrown instead of the expected
     *  {@link IllegalStateException}.
     *
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @Test
    final void onlyEnumsMayHaveEnumConstants() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = IllegalStateException.class;
        try
        {
            composer.classBuilder( "Roshambo" )
                .addEnumConstant( "ROCK" )
                .build();
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
    }   //  onlyEnumsMayHaveEnumConstants()
}
//  class TestEnumConstants

/*
 *  End of File
 */