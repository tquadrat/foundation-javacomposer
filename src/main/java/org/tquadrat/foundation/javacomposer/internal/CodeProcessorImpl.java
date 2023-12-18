/*
 * ============================================================================
 *  Copyright © 2002-2023 by Thomas Thrien.
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

package org.tquadrat.foundation.javacomposer.internal;

import static java.lang.String.join;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.CodeProcessor;
import org.tquadrat.foundation.javacomposer.JavaComposer;

/**
 *  An implementation of
 *  {@link CodeProcessor}
 *  that creates an instance of
 *  {@link CodeBlock}
 *  from the given String template.
 *
 *  @version $Id: CodeProcessorImpl.java 1079 2023-10-22 17:44:34Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.3.0
 */
@ClassVersion( sourceVersion = "$Id: CodeProcessorImpl.java 1079 2023-10-22 17:44:34Z tquadrat $" )
@API( status = INTERNAL, since = "0.3.0" )
public final class CodeProcessorImpl implements CodeProcessor
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The instance of
     *  {@link JavaComposer}
     *  that is used to create the
     *  {@link CodeBlock}
     *  instances.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final JavaComposer m_Composer;

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code CodeProcessor}.
     *
     *  @param  composer    The instance of
     *      {@link JavaComposer}
     *      that is used to create the
     *      {@link CodeBlock}
     *      instances.
     */
    public CodeProcessorImpl( @SuppressWarnings( "UseOfConcreteClass" ) final JavaComposer composer )
    {
        m_Composer = requireNonNullArgument( composer, "composer" );
    }   //  CodeProcessorImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public CodeBlock process( final StringTemplate template )
    {
        final var format = join( EMPTY_STRING, template.fragments() );
        final var args = template.values().toArray( Object []::new );
        final var builder = m_Composer.codeBlockBuilder().add( format, args );
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  process()
}
//  class CodeProcessorImpl

/*
 *  End of File
 */