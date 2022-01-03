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

package test.org.tquadrat.javacomposer.lambdaspec;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.BiConsumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.WildcardTypeName;
import org.tquadrat.foundation.testutil.TestBaseClass;
import org.tquadrat.foundation.util.stringconverter.StringStringConverter;

/**
 *  Using a lambda expression.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: BugHunt_20211213_001.java 943 2021-12-21 01:34:32Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: BugHunt_20211213_001.java 943 2021-12-21 01:34:32Z tquadrat $" )
@DisplayName( "BugHunt_20211213_001" )
public class BugHunt_20211213_001 extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Test to use a lambda expression.
     */
    @Test
    final void testUseLambda()
    {
        skipThreadTest();

        final var fieldName = "m_Field";

        final var composer = new JavaComposer();

        final var lambdaType = ParameterizedTypeName.from( BiConsumer.class, String.class, String.class );
        final var handlerType = ParameterizedTypeName.from( ClassName.from( "org.tquadrat.test", "CmdLineValueHandler" ), WildcardTypeName.subtypeOf( Object.class ) );
        final var handlerClass = ClassName.from( "org.tquadrat.test", "StringValueHandler" );
        final var converterClass = ClassName.from( StringStringConverter.class );

        final var candidate = composer.lambdaBuilder()
            .addParameter( "propertyName" )
            .addParameter( "value" )
            .addCode( "$N = value", fieldName )
            .build();

        var codeBlock = composer.codeBlockOf( "$T function = $L;", lambdaType, candidate );
        var expected = "java.util.function.BiConsumer<java.lang.String, java.lang.String> function = (propertyName,value) -> m_Field = value;";
        var actual = codeBlock.toString();
        assertEquals( expected, actual );

        codeBlock = composer.codeBlockBuilder()
            .addStatement( "final $T retValue = new $T( $L )", handlerType, handlerClass, candidate )
            .build();
        expected =
            """
            final org.tquadrat.test.CmdLineValueHandler<?> retValue = new org.tquadrat.test.StringValueHandler( (propertyName,value) -> m_Field = value );
            """;
        actual = codeBlock.toString();
        assertEquals( expected, actual );

        codeBlock = composer.codeBlockBuilder()
            .addStatement( "final $T retValue = new $T( $L, $T )", handlerType, handlerClass, candidate, converterClass )
            .build();
        expected =
            """
            final org.tquadrat.test.CmdLineValueHandler<?> retValue = new org.tquadrat.test.StringValueHandler( (propertyName,value) -> m_Field = value, org.tquadrat.foundation.util.stringconverter.StringStringConverter );
            """;
        actual = codeBlock.toString();
        assertEquals( expected, actual );
    }   //  testUseLambda()
}
//  class BugHunt_20211213_001

/*
 *  End of File
 */