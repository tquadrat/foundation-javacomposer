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

import javax.lang.model.element.Modifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
 *  @version $Id: TestInterfaceDefaultMethods.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestInterfaceDefaultMethods.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestInterfaceDefaultMethods" )
public class TestInterfaceDefaultMethods extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  This test failed in
     *  {@link TestTypeSpec}
     *  because of a failed validation.
     *
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @Test
    final void interfaceDefaultMethods() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var method = composer.methodBuilder( "test" )
            .addModifiers( Modifier.PUBLIC, Modifier.DEFAULT )
            .returns( int.class )
            .addCode( composer.codeBlockBuilder()
                .addStatement( "return 0" )
                .build() )
            .build();
        final var builder = composer.interfaceBuilder( "Tacos" );
        builder.addMethod( method );
        final var bar = builder.build();

        final var actual = TestTypeSpec.toString( bar );
        final var expected =
            """
            package com.squareup.tacos;

            interface Tacos {
              default int test() {
                return 0;
              }
            }
            """;
        assertEquals( expected, actual );
    }   //  interfaceDefaultMethods()
}
//  class TestInterfaceDefaultMethods

/*
 *  End of File
 */