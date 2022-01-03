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
 *  {@link org.tquadrat.foundation.javacomposer.Shortcuts#getClassVersionAnnotation()}
 *  in the class
 *  {@link org.tquadrat.foundation.javacomposer.Shortcuts}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestGetClassVersionAnnotation.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( {"MisorderedAssertEqualsArguments", "removal", "deprecation"} )
@ClassVersion( sourceVersion = "$Id: TestGetClassVersionAnnotation.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestGetClassVersionAnnotation" )
public class TestGetClassVersionAnnotation extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for the method
     *  {@link org.tquadrat.foundation.javacomposer.Shortcuts#getClassVersionAnnotation()}.
     */
    @Test
    final void testGetClassVersionAnnotation()
    {
        skipThreadTest();

        String actual, expected;

        final var classVersion = org.tquadrat.foundation.javacomposer.Shortcuts.getClassVersionAnnotation();
        assertNotNull( classVersion );

        expected =
            """
            @org.tquadrat.foundation.annotation.ClassVersion(sourceVersion = "Generated with JavaComposer", isGenerated = true)""";
        actual = classVersion.toString();
        assertEquals( expected, actual );

        final var typeSpec = TypeSpec.classBuilder( "AnnotatedClass" )
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
        actual = JavaFile.builder( "org.tquadrat.test", typeSpec )
            .build()
            .toString();
        assertEquals( expected, actual );
    }   //  testGetClassVersionAnnotation()
}
//  class TestGetClassVersionAnnotation

/*
 *  End of File
 */