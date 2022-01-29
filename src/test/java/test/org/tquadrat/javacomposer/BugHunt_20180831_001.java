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

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.WildcardTypeName;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Some tests regarding some errors.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: BugHunt_20180831_001.java 943 2021-12-21 01:34:32Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: BugHunt_20180831_001.java 943 2021-12-21 01:34:32Z tquadrat $" )
@DisplayName( "test.org.tquadrat.javacomposer.BugHunt_20180831_001" )
public class BugHunt_20180831_001 extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests that "m_Registry" is a valid field name.
     */
    @Test
    final void testFieldName_m_Registry()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var name = "m_Registry";

        final var field = composer.fieldBuilder( String.class, name, PUBLIC, FINAL )
            .build();
        assertNotNull( field );
        assertEquals( name, field.name() );
    }   //  testFieldName_m_Registry()

    /**
     *  Tests with a bit more complex argument types.
     */
    @Test
    final void testParameterizedArguments()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var overrideAnnotation = ClassName.from( Override.class );
        final var throwException = composer.codeBlockBuilder()
            .addStatement( "throw new $T()", UnsupportedOperationException.class )
            .build();
        final TypeName argType = ParameterizedTypeName.from( ClassName.from( Map.class ), WildcardTypeName.subtypeOf( String.class ), WildcardTypeName.subtypeOf( Object.class ) );
        final var arg0 = composer.parameterBuilder( argType, "m", FINAL )
            .build();
        final var method = composer.methodBuilder( "putAll" )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( overrideAnnotation )
            .addParameter( arg0 )
            .addJavadoc( composer.createInheritDocComment() )
            .addCode( throwException )
            .build();
        final var expected =
            """
            /**
             * {@inheritDoc}
             */
            @java.lang.Override
            public final void putAll(final java.util.Map<? extends java.lang.String, ?> m) {
              throw new java.lang.UnsupportedOperationException();
            }
            """;
        final var actual = method.toString();
        assertEquals( expected, actual );
    }   //  testParameterizedArguments()
}
//  class BugHunt_20180831_001

/*
 *  End of File
 */