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

package test.org.tquadrat.javacomposer.methodspec;

import static org.apiguardian.api.API.Status.STABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.tquadrat.foundation.util.StringUtils.format;

import org.apiguardian.api.API;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.ArrayTypeName;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.MethodSpec;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for
 *  {@link MethodSpec#signature()}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestSignature.java 943 2021-12-21 01:34:32Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestSignature.java 943 2021-12-21 01:34:32Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
@DisplayName( "TestSignature" )
public class TestSignature extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for
     *  {@link MethodSpec#signature()}.
     *
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @Test
    final void testSignature() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        MethodSpec candidate;

        final var name = "method";

        String actual, expected;

        candidate = composer.methodBuilder( name )
            .build();
        expected = format( "%s()", name );
        actual = candidate.signature();
        assertNotNull( actual );
        assertFalse( actual.isBlank() );
        assertEquals( expected, actual );

        final var param1 = TypeName.from( String.class );

        candidate = composer.methodBuilder( name )
            .addParameter( param1, "param1" )
            .build();
        expected = format( "%s( %s )", name, param1.toString() );
        actual = candidate.signature();
        assertNotNull( actual );
        assertFalse( actual.isBlank() );
        assertEquals( expected, actual );

        final var param2 = ArrayTypeName.of( String.class );

        candidate = composer.methodBuilder( name )
            .addParameter( param1, "param1" )
            .addParameter( param2, "param2" )
            .build();
        expected = format( "%s( %s, %s )", name, param1.toString(), param2.toString() );
        actual = candidate.signature();
        assertNotNull( actual );
        assertFalse( actual.isBlank() );
        assertEquals( expected, actual );
    }   //  testSignature()
}
//  class TestSignature

/*
 *  End of File
 */