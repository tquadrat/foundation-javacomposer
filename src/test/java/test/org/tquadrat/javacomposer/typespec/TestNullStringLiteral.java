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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.testutil.TestBaseClass;
import org.tquadrat.javapoet.TestTypeSpec;

/**
 *  Tests for the class
 *  {@link TypeSpec}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestNullStringLiteral.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestNullStringLiteral.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestNullStringLiteral" )
public class TestNullStringLiteral extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  This test failed in
     *  {@link TestTypeSpec}
     *  because of an unexpected
     *  {@link NullPointerException}.
     *
     *  @param  format  The format to use.
     *
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @ParameterizedTest
    @ValueSource( strings = {"$L", "$S"} )
    final void nullStringLiteral( final String format ) throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var fieldBuilder = composer.fieldBuilder( String.class, "NULL" );

        fieldBuilder.initializer( format, (Object) null );

        final var field = fieldBuilder.build();
        final var taco = composer.classBuilder( "Taco" ).addField( field ).build();
        final var actual = TestTypeSpec.toString( taco );
        final var expected =
            """
            package com.squareup.tacos;

            import java.lang.String;

            class Taco {
              String NULL = null;
            }
            """;
        assertEquals( expected, actual );
    }   //  nullStringLiteral()
}
//  class TestNullStringLiteral

/*
 *  End of File
 */