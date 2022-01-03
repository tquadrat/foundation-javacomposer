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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.tquadrat.foundation.javacomposer.Primitives.VOID;

import java.util.function.UnaryOperator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Using a lambda expression.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestUseLambda.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestUseLambda.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestUseLambda" )
public class TestUseLambda extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Test to use a lambda expression.
     */
    @Test
    final void testUseLambda()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var parameter = composer.parameterOf( VOID, "s" );
        final var candidate = composer.lambdaBuilder()
            .addParameter( parameter )
            .addCode( "upperCase( $N )" , parameter )
            .build();

        final var codeBlock = composer.codeBlockOf( "$T<$T> function = $L;", UnaryOperator.class, String.class, candidate );

        final var expected = "java.util.function.UnaryOperator<java.lang.String> function = s -> upperCase( s );";
        final var actual = codeBlock.toString();
        assertEquals( expected, actual );
    }   //  testUseLambda()
}
//  class TestLambdaSpec

/*
 *  End of File
 */