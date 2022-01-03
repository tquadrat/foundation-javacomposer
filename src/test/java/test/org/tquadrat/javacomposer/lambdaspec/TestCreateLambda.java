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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.tquadrat.foundation.javacomposer.Primitives.VOID;

import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.LambdaSpec;
import org.tquadrat.foundation.javacomposer.ParameterSpec;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Building a lambda.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestCreateLambda.java 943 2021-12-21 01:34:32Z tquadrat $
 */
@SuppressWarnings( {"MisorderedAssertEqualsArguments", "UseOfObsoleteDateTimeApi"} )
@ClassVersion( sourceVersion = "$Id: TestCreateLambda.java 943 2021-12-21 01:34:32Z tquadrat $" )
@DisplayName( "TestCreateLambda" )
public class TestCreateLambda extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  We test to build a lambda body.
     */
    @Test
    final void testCreateLambdaBody()
    {
        skipThreadTest();

        final var composer = new JavaComposer();
        String actual, expected;
        LambdaSpec.Builder builder;
        LambdaSpec candidate;

        CodeBlock codeBlock;

        //---* No arguments, just a body -> a Supplier *-----------------------
        builder = composer.lambdaBuilder();
        assertNotNull( builder );
        candidate = builder
            .addCode( "$S", "Hello World" )
            .build();
        expected = "() -> \"Hello World\"";
        actual = candidate.toString();
        assertEquals( expected, actual );

        codeBlock = composer.codeBlockOf( "$S", "Hello World" );
        builder = composer.lambdaBuilder();
        assertNotNull( builder );
        candidate = builder
            .addCode( codeBlock )
            .build();
        expected = "() -> \"Hello World\"";
        actual = candidate.toString();
        assertEquals( expected, actual );

        builder = composer.lambdaBuilder();
        assertNotNull( builder );
        candidate = builder
            .addStatement( "return $S", "Hello World" )
            .build();
        expected =
            """
            () ->
            {
              return "Hello World";
            }""";
        actual = candidate.toString();
        assertEquals( expected, actual );

        codeBlock = composer.codeBlockOf( "return $S", "Hello World" );
        builder = composer.lambdaBuilder();
        assertNotNull( builder );
        candidate = builder
            .addStatement( codeBlock )
            .build();
        expected =
            """
            () ->
            {
              return "Hello World";
            }""";
        actual = candidate.toString();
        assertEquals( expected, actual );
    }   //  testCreateLambdaBody()

    /**
     *  We test to build a lambda parameter block.
     */
    @Test
    final void testCreateLambdaParameterBlock()
    {
        skipThreadTest();

        final var composer = new JavaComposer();
        String actual, expected;
        LambdaSpec.Builder builder;
        LambdaSpec candidate;

        ParameterSpec parameter;

        //---* The empty lambda: a Supplier that returns null constantly *-----
        builder = composer.lambdaBuilder();
        assertNotNull( builder );
        candidate = builder.build();
        expected = "() -> null";
        actual = candidate.toString();
        assertEquals( expected, actual );

        //---* One argument, type to be inferred *-----------------------------
        builder = composer.lambdaBuilder();
        assertNotNull( builder );
        candidate = builder
            .addParameter( "a" )
            .build();
        expected = "a -> null";
        actual = candidate.toString();
        assertEquals( expected, actual );

        builder = composer.lambdaBuilder();
        assertNotNull( builder );
        candidate = builder
            .addParameter( void.class, "a" )
            .build();
        expected = "a -> null";
        actual = candidate.toString();
        assertEquals( expected, actual );

        builder = composer.lambdaBuilder();
        assertNotNull( builder );
        candidate = builder
            .addParameter( VOID, "a" )
            .build();
        expected = "a -> null";
        actual = candidate.toString();
        assertEquals( expected, actual );

        parameter = composer.parameterOf( VOID, "a" );
        builder = composer.lambdaBuilder();
        assertNotNull( builder );
        candidate = builder
            .addParameter( parameter )
            .build();
        expected = "a -> null";
        actual = candidate.toString();
        assertEquals( expected, actual );

        //---* One argument, explicit type *-----------------------------------
        builder = composer.lambdaBuilder();
        assertNotNull( builder );
        candidate = builder
            .addParameter( String.class, "a" )
            .build();
        expected = "(java.lang.String a) -> null";
        actual = candidate.toString();
        assertEquals( expected, actual );

        builder = composer.lambdaBuilder();
        assertNotNull( builder );
        candidate = builder
            .addParameter( TypeName.from( String.class ), "a" )
            .build();
        expected = "(java.lang.String a) -> null";
        actual = candidate.toString();
        assertEquals( expected, actual );

        parameter = composer.parameterOf( String.class, "a" );
        builder = composer.lambdaBuilder();
        assertNotNull( builder );
        candidate = builder
            .addParameter( parameter )
            .build();
        expected = "(java.lang.String a) -> null";
        actual = candidate.toString();
        assertEquals( expected, actual );

        //---* Two and more arguments, type to be inferred *-------------------
        builder = composer.lambdaBuilder();
        assertNotNull( builder );
        candidate = builder
            .addParameter( "a" )
            .addParameter( "b" )
            .build();
        expected = "(a,b) -> null";
        actual = candidate.toString();
        assertEquals( expected, actual );

        candidate = builder
            .addParameter( void.class, "c" )
            .build();
        expected = "(a,b,c) -> null";
        actual = candidate.toString();
        assertEquals( expected, actual );

        candidate = builder
            .addParameter( VOID, "d" )
            .build();
        expected = "(a,b,c,d) -> null";
        actual = candidate.toString();
        assertEquals( expected, actual );

        parameter = composer.parameterOf( VOID, "e" );
        candidate = builder
            .addParameter( parameter )
            .build();
        expected = "(a,b,c,d,e) -> null";
        actual = candidate.toString();
        assertEquals( expected, actual );

        //---* One argument, explicit type *-----------------------------------
        builder = composer.lambdaBuilder();
        assertNotNull( builder );
        candidate = builder
            .addParameter( String.class, "a" )
            .build();
        expected = "(java.lang.String a) -> null";
        actual = candidate.toString();
        assertEquals( expected, actual );

        candidate = builder
            .addParameter( TypeName.from( Date.class ), "b" )
            .build();
        expected = "(java.lang.String a, java.util.Date b) -> null";
        actual = candidate.toString();
        assertEquals( expected, actual );

        parameter = composer.parameterOf( Boolean.class, "c" );
        candidate = builder
            .addParameter( parameter )
            .build();
        expected = "(java.lang.String a, java.util.Date b, java.lang.Boolean c) -> null";
        actual = candidate.toString();
        assertEquals( expected, actual );
    }   //  testCreateLambdaParameterBlock()
}
//  class TestCreateLambda

/*
 *  End of File
 */