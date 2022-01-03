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

package test.org.tquadrat.javacomposer.shortcuts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.JavaFile;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the method
 *  {@link org.tquadrat.foundation.javacomposer.Shortcuts#getUtilityClassAnnotation()}
 *  in the class
 *  {@link org.tquadrat.foundation.javacomposer.Shortcuts}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestGetUtilityClassAnnotation.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( {"MisorderedAssertEqualsArguments", "removal"} )
@ClassVersion( sourceVersion = "$Id: TestGetUtilityClassAnnotation.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestGetUtilityClassAnnotation" )
public class TestGetUtilityClassAnnotation extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for the method
     *  {@link org.tquadrat.foundation.javacomposer.Shortcuts#getUtilityClassAnnotation()}.
     */
    @Test
    final void testGetUtilityClassAnnotation()
    {
        skipThreadTest();

        String actual, expected;

        final var utility = org.tquadrat.foundation.javacomposer.Shortcuts.getUtilityClassAnnotation();
        assertNotNull( utility );

        expected = "@org.tquadrat.foundation.annotation.UtilityClass";
        actual = utility.toString();
        assertEquals( expected, actual );

        final var typeSpec = TypeSpec.classBuilder( "AnnotatedClass" )
            .addAnnotation( utility )
            .build();
        expected =
            """
            package org.tquadrat.test;

            import org.tquadrat.foundation.annotation.UtilityClass;

            @UtilityClass
            class AnnotatedClass {
            }
            """;
        actual = JavaFile.builder( "org.tquadrat.test", typeSpec )
            .build()
            .toString();
        assertEquals( expected, actual );
    }   //  testGetUtilityClassAnnotation()
}
//  class TestGetUtilityClassAnnotation

/*
 *  End of File
 */