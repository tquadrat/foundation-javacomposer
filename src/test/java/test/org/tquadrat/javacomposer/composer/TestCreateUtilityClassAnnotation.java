/*
 * ============================================================================
 *  Copyright © 2002-2024 by Thomas Thrien.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the method
 *  {@link JavaComposer#createUtilityClassAnnotation()}
 *  in the class
 *  {@link org.tquadrat.foundation.javacomposer.JavaComposer}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestCreateUtilityClassAnnotation.java 1085 2024-01-05 16:23:28Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestCreateUtilityClassAnnotation.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@DisplayName( "TestCreateUtilityClassAnnotation" )
public class TestCreateUtilityClassAnnotation extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for the method
     *  {@link JavaComposer#createUtilityClassAnnotation()}.
     */
    @Test
    final void testCreateUtilityClassAnnotation()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        String actual, expected;

        final var utility = composer.createUtilityClassAnnotation();
        assertNotNull( utility );

        expected = "@org.tquadrat.foundation.annotation.UtilityClass";
        actual = utility.toString();
        assertEquals( expected, actual );

        final var typeSpec = composer.classBuilder( "AnnotatedClass" )
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
        actual = composer.javaFileBuilder( "org.tquadrat.test", typeSpec )
            .build()
            .toString();
        assertEquals( expected, actual );
    }   //  testCreateUtilityClassAnnotation()
}
//  class TestCreateUtilityClassAnnotation

/*
 *  End of File
 */