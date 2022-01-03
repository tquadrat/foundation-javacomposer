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

package test.org.tquadrat.javacomposer.parameterspec;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the JavaDoc extension of ParameterSpec
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestUse.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestUse.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestUse" )
public class TestUse extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Test using the JavaDoc feature.
     */
    @Test
    final void testUse()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var parameter = composer.parameterBuilder( String.class, "param", FINAL )
            .addJavadoc( "The input parameter." )
            .build();
        final var method = composer.methodBuilder( "method" )
            .addJavadoc( "A method." )
            .addModifiers( PUBLIC, FINAL )
            .addParameter( parameter )
            .returns( String.class )
            .addStatement( "var retValue = $N.toUpperCase()", parameter )
            .addComment( "---* Done *----------------------------------------------------------" )
            .addStatement( "return retValue" )
            .build();

        final var expected =
            """
            /**
             * A method.
             * @param param The input parameter.
             */
            public final java.lang.String method(final java.lang.String param) {
              var retValue = param.toUpperCase();
              // ---* Done *----------------------------------------------------------
              return retValue;
            }
            """;
        final var actual = method.toString();
        assertEquals( expected, actual );
    }   //  testUse()
}
//  class TestUse

/*
 *  End of File
 */