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

package test.org.tquadrat.javacomposer.typename;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.tquadrat.foundation.javacomposer.Primitives.BOOLEAN;
import static org.tquadrat.foundation.javacomposer.Primitives.BYTE;
import static org.tquadrat.foundation.javacomposer.Primitives.CHAR;
import static org.tquadrat.foundation.javacomposer.Primitives.DOUBLE;
import static org.tquadrat.foundation.javacomposer.Primitives.FLOAT;
import static org.tquadrat.foundation.javacomposer.Primitives.INT;
import static org.tquadrat.foundation.javacomposer.Primitives.LONG;
import static org.tquadrat.foundation.javacomposer.Primitives.SHORT;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Some tests for the methods
 *  {@link TypeName#box()}
 *  and
 *  {@link TypeName#unbox()}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestBoxOnPrimitives.java 943 2021-12-21 01:34:32Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestBoxOnPrimitives.java 943 2021-12-21 01:34:32Z tquadrat $" )
@DisplayName( "TestBoxOnPrimitives" )
public class TestBoxOnPrimitives extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests the method
     *  {@link TypeName#box()}
     *  for some types.
     */
    @Test
    final void testBoxOnPrimitives()
    {
        skipThreadTest();

        TypeName actual;
        ClassName expected;

        expected = ClassName.from( Boolean.class );
        assertNotNull( expected );
        actual = BOOLEAN.box();
        assertNotNull( actual );
        assertEquals( expected, actual );

        expected = ClassName.from( Byte.class );
        assertNotNull( expected );
        actual = BYTE.box();
        assertNotNull( actual );
        assertEquals( expected, actual );

        expected = ClassName.from( Character.class );
        assertNotNull( expected );
        actual = CHAR.box();
        assertNotNull( actual );
        assertEquals( expected, actual );

        expected = ClassName.from( Double.class );
        assertNotNull( expected );
        actual = DOUBLE.box();
        assertNotNull( actual );
        assertEquals( expected, actual );

        expected = ClassName.from( Float.class );
        assertNotNull( expected );
        actual = FLOAT.box();
        assertNotNull( actual );
        assertEquals( expected, actual );

        expected = ClassName.from( Integer.class );
        assertNotNull( expected );
        actual = INT.box();
        assertNotNull( actual );
        assertEquals( expected, actual );

        expected = ClassName.from( Long.class );
        assertNotNull( expected );
        actual = LONG.box();
        assertNotNull( actual );
        assertEquals( expected, actual );

        expected = ClassName.from( Short.class );
        assertNotNull( expected );
        actual = SHORT.box();
        assertNotNull( actual );
        assertEquals( expected, actual );
    }   //   testBoxOnPrimitives()
}
//  class TestBoxOnPrimitives

/*
 *  End of File
 */