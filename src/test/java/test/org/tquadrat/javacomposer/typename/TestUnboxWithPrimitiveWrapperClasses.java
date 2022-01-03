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
 *  @version $Id: TestUnboxWithPrimitiveWrapperClasses.java 943 2021-12-21 01:34:32Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestUnboxWithPrimitiveWrapperClasses.java 943 2021-12-21 01:34:32Z tquadrat $" )
@DisplayName( "TestUnboxWithPrimitiveWrapperClasses" )
public class TestUnboxWithPrimitiveWrapperClasses extends TestBaseClass
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
    final void testUnboxWithPrimitiveWrapperClasses()
    {
        skipThreadTest();

        final TypeName actual;
        final ClassName candidate;
        final TypeName expected;

        expected = BOOLEAN;
        assertNotNull( expected );
        candidate = ClassName.from( Boolean.class );
        assertNotNull( candidate );
        actual = candidate.unbox();
        assertNotNull( actual );
        assertEquals( expected, actual );
    }   //  testUnboxWithPrimitiveWrapperClasses()
}
//  class TestUnboxWithPrimitiveWrapperClasses

/*
 *  End of File
 */