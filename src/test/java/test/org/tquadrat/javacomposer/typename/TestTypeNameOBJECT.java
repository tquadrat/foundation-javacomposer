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
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.internal.ClassNameImpl;
import org.tquadrat.foundation.javacomposer.internal.TypeNameImpl;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for
 *  {@link ClassNameImpl#OBJECT}
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestTypeNameOBJECT.java 943 2021-12-21 01:34:32Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestTypeNameOBJECT.java 943 2021-12-21 01:34:32Z tquadrat $" )
public class TestTypeNameOBJECT extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests whether
     *  {@link TypeName#from(java.lang.reflect.Type)}
     *  will return always
     *  {@link TypeNameImpl#OBJECT}
     *  for the argument
     *  {@link Object Object.class}.
     */
    @Test
    final void testIdentity()
    {
        skipThreadTest();

        final var candidate = TypeName.from( Object.class );
        final var object = new Object();

        assertEquals( ClassNameImpl.OBJECT, candidate );
        assertEquals( TypeName.from( Object.class ), candidate );
        assertEquals( TypeName.from( object.getClass() ), candidate );

        assertSame( ClassNameImpl.OBJECT, candidate );
        assertSame( TypeName.from( Object.class ), candidate );
        assertSame( TypeName.from( object.getClass() ), candidate );
    }   //  testIdentity()
}
//  class TestTypeNameOBJECT

/*
 *  End of File
 */