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
 *  {@link JavaComposer#createClassVersionAnnotation()}
 *  in the class
 *  {@link org.tquadrat.foundation.javacomposer.JavaComposer}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestCreateClassVersionAnnotation.java 1085 2024-01-05 16:23:28Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestCreateClassVersionAnnotation.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@DisplayName( "TestCreateClassVersionAnnotation" )
public class TestCreateClassVersionAnnotation extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for the method
     *  {@link JavaComposer#createClassVersionAnnotation()}.
     */
    @Test
    final void testCreateClassVersionAnnotation()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        String actual, expected;

        final var classVersion = composer.createClassVersionAnnotation();
        assertNotNull( classVersion );

        expected =
            """
            @org.tquadrat.foundation.annotation.ClassVersion(sourceVersion = "Generated with JavaComposer", isGenerated = true)""";
        actual = classVersion.toString();
        assertEquals( expected, actual );

        final var typeSpec = composer.classBuilder( "AnnotatedClass" )
            .addAnnotation( classVersion )
            .build();
        expected =
            """
            package org.tquadrat.test;

            import org.tquadrat.foundation.annotation.ClassVersion;

            @ClassVersion(sourceVersion = "Generated with JavaComposer", isGenerated = true)
            class AnnotatedClass {
            }
            """;
        actual = composer.javaFileBuilder( "org.tquadrat.test", typeSpec )
            .build()
            .toString();
        assertEquals( expected, actual );
    }   //  testCreateClassVersionAnnotation()
}
//  class TestCreateClassVersionAnnotation

/*
 *  End of File
 */