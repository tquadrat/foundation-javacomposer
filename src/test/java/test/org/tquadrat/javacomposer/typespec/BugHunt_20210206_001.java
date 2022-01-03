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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.UnsupportedEnumError;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.Layout;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  This class provides some tests for
 *  {@link org.tquadrat.foundation.javacomposer.TypeSpec}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: BugHunt_20210206_001.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( {"MisorderedAssertEqualsArguments", "DuplicateBranchesInSwitch"} )
@ClassVersion( sourceVersion = "$Id: BugHunt_20210206_001.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "BugHunt_20210206_001" )
public class BugHunt_20210206_001 extends TestBaseClass
{
        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The name of the package that is used for the new records: {@value}.
     */
    public static final String PACKAGE_NAME = "org.tquadrat.foundation";

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  The most simplistic record.
     *
     *  @param  layout  The layout to use.
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @ParameterizedTest
    @EnumSource( Layout.class )
    final void testMostSimplisticClass( final Layout layout ) throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer( layout );

        final var field = composer.fieldBuilder( int.class, "field" )
            .addModifiers( Modifier.PRIVATE )
            .build();

        final var candidate = composer.classBuilder( "MyClass" )
            .addField( field )
            .build();

        final var expected = switch( layout )
            {
                case LAYOUT_DEFAULT ->
                    """
                    package org.tquadrat.foundation;
                                
                    class MyClass {
                      private int field;
                    }
                    """;

                case LAYOUT_FOUNDATION ->
                    """
                    package org.tquadrat.foundation;
                     
                    import java.lang.SuppressWarnings;
                     
                    @SuppressWarnings( "javadoc" )
                    class MyClass
                    {
                            /*------------*\\
                        ====** Attributes **=======================================================
                            \\*------------*/
                        @SuppressWarnings( "javadoc" )
                        private int field;
                    }
                    //  class MyClass
                     
                    /*
                     * End of File
                     */""";

                case LAYOUT_JAVAPOET ->
                    """
                    package org.tquadrat.foundation;
                                
                    class MyClass {
                      private int field;
                    }
                    """;

                case LAYOUT_JAVAPOET_WITH_TAB ->
                    """
                    package org.tquadrat.foundation;
                                
                    class MyClass {
                    \tprivate int field;
                    }
                    """;

                default -> throw new UnsupportedEnumError( layout );
            };

        final var actual = toString( candidate );

        assertEquals( expected, actual );
    }   //  testMostSimplisticClass()

    /**
     *  Returns the built class as a String.
     *
     *  @param  typeSpec    The record.
     *  @return The generated source code.
     */
    @SuppressWarnings( "CastToConcreteClass" )
    final String toString( final TypeSpec typeSpec )
    {
        final var typeSpecImpl = (TypeSpecImpl) typeSpec;
        final var composer = typeSpecImpl.getFactory();
        final var retValue = composer.javaFileBuilder( PACKAGE_NAME, typeSpec )
            .build()
            .toString();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toString()
}
//  class BugHunt_20210206_001

/*
 *  End of File
 */