/*
 * ============================================================================
 * Copyright © 2002-2021 by Thomas Thrien.
 * All Rights Reserved.
 * ============================================================================
 *
 * Licensed to the public under the agreements of the GNU Lesser General Public
 * License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.tquadrat.foundation.javacomposer.internal;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.util.StringUtils.format;

import java.util.Optional;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  A helper class holding the debug output that is added to the generated
 *  code. This allows a quick reference to the source code that was responsible
 *  for the generation of the respective element.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: DebugOutput.java 922 2021-05-23 18:32:17Z tquadrat $
 *  @since 0.0.6
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: DebugOutput.java 922 2021-05-23 18:32:17Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.6" )
public final class DebugOutput
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The text for the debug output.
     */
    private final String m_Text;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code DebugOutput} instance.
     *
     *  @param  stackTraceElement   The stack trace element for the caller's
     *      caller.
     */
    @SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
    public DebugOutput( final Optional<StackTraceElement> stackTraceElement )
    {
        m_Text = requireNonNullArgument( stackTraceElement, "stackTraceElement" )
            .map( ste -> format( "%1$s:%2$d", ste.getFileName(), ste.getLineNumber() ) )
            .orElse( "Unknown Location" );
    }   //  DebugOutput()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Returns the text for the debug output.
     *
     *  @return The comment.
     */
    public final String asComment() { return format( " /* [%s] */ ", m_Text ); }

    /**
     *  Returns the text for the debug output.
     *
     *  @return The text.
     */
    public final String asLiteral() { return format( " [%s] ", m_Text ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString() { return asLiteral(); }
}
//  class DebugOutput

/*
 *  End of File
 */