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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.javacomposer.TypeSpec.Builder;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the class
 *  {@link TypeSpec}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestToBuilder.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestToBuilder.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "test.org.tquadrat.javacomposer.typespec.TestToBuilder" )
public class TestToBuilder extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Some tests for
     *  {@link  org.tquadrat.foundation.javacomposer.TypeSpec#toBuilder()}
     *
     *  @param  builder The type builder.
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @ParameterizedTest
    @MethodSource( "test.org.tquadrat.javacomposer.typespec.TypeBuilderProvider#provideTypeBuilders" )
    final void testToBuilder( final Builder builder ) throws Exception
    {
        skipThreadTest();

        final var object = builder.build();
        final var otherBuilder = object.toBuilder();
        assertNotNull( otherBuilder );
        final var otherObject = otherBuilder.build();
        assertNotNull( otherObject );
        assertEquals( object, otherObject );
    }   //  testToBuilder()
}
//  class TestToBuilder

/*
 *  End of File
 */