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

package org.tquadrat.javapoet;

import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.tquadrat.foundation.lang.Objects.isNull;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.junit.Rule;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.tquadrat.javapoet.helper.AbstractTypes;
import org.tquadrat.javapoet.helper.CompilationRule;

@SuppressWarnings( "javadoc" )
@DisplayName( "TestTypesEclipse" )
@RunWith( JUnit4.class )
public final class TestTypesEclipse extends AbstractTypes
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
    @Override
    protected final Elements getElements()
    {
        final var retValue = m_Compilation.getElements();
        assumeFalse( isNull( retValue ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getElements()

    @Override
    protected final Types getTypes()
    {
        final var retValue = m_Compilation.getTypes();
        assumeFalse( isNull( retValue ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getTypes()
}
//  class TestTypesEclipse

/*
 *  End of File
 */