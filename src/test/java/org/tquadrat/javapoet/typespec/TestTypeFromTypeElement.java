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

package org.tquadrat.javapoet.typespec;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.javapoet.helper.CompilationRule;

@SuppressWarnings( {"javadoc", "MisorderedAssertEqualsArguments"} )
@RunWith( JUnit4.class )
@DisplayName( "TestTypeFromTypeElement" )
public final class TestTypeFromTypeElement
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    @SuppressWarnings( "InstanceVariableOfConcreteClass" )
    @Rule
    public final CompilationRule m_Compilation = new CompilationRule();

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    @SuppressWarnings( "SameParameterValue" )
    private final TypeElement getElement( final Class<?> clazz )
    {
        Elements elements = null;
        try
        {
            elements = m_Compilation.getElements();
        }
        catch( @SuppressWarnings( "unused" ) final IllegalStateException e ) { /* Deliberately ignored */ }
        assumeFalse( elements == null );
        final var retValue = elements.getTypeElement( requireNonNullArgument( clazz, "clazz" ).getCanonicalName() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getElements()

    @Test
    public void typeFromTypeElement()
    {
        final var composer = new JavaComposer();

        final var element = getElement( String.class );
        assertThat( composer.codeBlockOf( "$T", element ).toString() ).isEqualTo( "java.lang.String" );
    }   //  typeFromTypeElement()
}
//  class TestTypeFromTypeElement

/*
 *  End of File
 */