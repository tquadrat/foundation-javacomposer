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

package test.org.tquadrat.javacomposer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.Shortcuts;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the methods in the class
 *  {@link Shortcuts}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestShortcuts.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestShortcuts.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestShortcuts" )
public class TestShortcuts extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Validates whether the class is static.
     */
    @SuppressWarnings( "removal" )
    @Test
    final void validateClass()
    {
        assertTrue( validateAsStaticClass( Shortcuts.class ) );
    }   //  validateClass()
}
//  class TestShortcuts

/*
 *  End of File
 */