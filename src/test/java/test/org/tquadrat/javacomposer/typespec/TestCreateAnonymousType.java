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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the class
 *  {@link TypeSpec}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestCreateAnonymousType.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestCreateAnonymousType.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestCreateAnonymousType" )
public class TestCreateAnonymousType extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests the creation of an anonymous type.
     *
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @Test
    final void createAnonymousType() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var candidate = composer.anonymousClassBuilder( EMPTY_STRING );
        assertNotNull( candidate );
    }   //  createAnonymousType()
}
//  class TestCreateAnonymousType

/*
 *  End of File
 */