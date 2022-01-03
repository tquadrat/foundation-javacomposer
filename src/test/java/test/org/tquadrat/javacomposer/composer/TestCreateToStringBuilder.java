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

import static javax.lang.model.element.Modifier.FINAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the method
 *  {@link JavaComposer#createToStringBuilder()}
 *  in the class
 *  {@link org.tquadrat.foundation.javacomposer.JavaComposer}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestCreateToStringBuilder.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestCreateToStringBuilder.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestCreateToStringBuilder" )
public class TestCreateToStringBuilder extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for the method
     *  {@link JavaComposer#createToStringBuilder()}.
     */
    @Test
    final void testCreateToStringBuilder()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var candidate = composer.createToStringBuilder();
        assertNotNull( candidate );
        final var expected =
            """
            /**
             * {@inheritDoc}
             */
            @java.lang.Override
            public final java.lang.String toString() {
            }
            """;
        final var actual = candidate
            .addModifiers( FINAL )
            .build()
            .toString();
        assertEquals( expected, actual );
    }   //  testGetToStringBuilder()
}
//  class testCreateToStringBuilder

/*
 *  End of File
 */