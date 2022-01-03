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

package org.tquadrat.foundation.javacomposer;

import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.LayoutWriter;

/**
 *  The various possible layouts for the output created by
 *  {@link JavaFile}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: Layout.java 855 2021-01-21 20:22:52Z tquadrat $
 *
 *  @UMLGraph.link
 *  @since 0.0.5
 */
@ClassVersion( sourceVersion = "$Id: Layout.java 855 2021-01-21 20:22:52Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public enum Layout implements LayoutWriter
{
        /*------------------*\
    ====** Enum Declaration **=================================================
        \*------------------*/
    /**
     *  <p>{@summary The layout as used for the Foundation library.}</p>
     *  <p>The layout of the generated Java file does not follow that of
     *  the Foundation library source completely as this would require to much
     *  effort to implement. Additionally, the code that is provided for the
     *  method bodies will not be parsed, so variations in the layout here will
     *  not be corrected.</p>
     *  <p>Here must be especially mentioned the methods
     *  {@link CodeBlock.Builder#beginControlFlow(String, Object...)},
     *  {@link CodeBlock.Builder#nextControlFlow(String, Object...)}
     *  and
     *  {@link CodeBlock.Builder#endControlFlow(String, Object...)}:
     *  these will generate code like this:</p>
     *  <pre><code>  if( flag ) {
     *      doThis();
     *  } else {
     *      doSomethingDifferent(); }</code></pre>
     *  <p>while the Foundation layout would look like this:</p>
     *  <pre><code>  if( flag )
     *  {
     *      doThis();
     *  }
     *  else
     *  {
     *      doSomethingDifferent();
     *  }</code></pre>
     *  <p>But these differences are seen as neglectable.</p>
     *
     *  TODO Adjust this comment!!
     */
    @API( status = STABLE, since = "0.0.5" )
    LAYOUT_FOUNDATION( false, 4 ),

    /**
     *  The layout for the original JavaPoet.
     */
    @API( status = STABLE, since = "0.0.5" )
    LAYOUT_JAVAPOET( false, 2 ),

    /**
     *  The layout for the original JavaPoet, but using the tabulator character
     *  for the indentation, instead of blanks.
     */
    @API( status = STABLE, since = "0.2.0" )
    LAYOUT_JAVAPOET_WITH_TAB( true, 4 ),

    /**
     *  The default layout; same as
     *  {@link #LAYOUT_JAVAPOET}.
     */
    @API( status = STABLE, since = "0.0.5" )
    LAYOUT_DEFAULT( "\t".equals( LAYOUT_JAVAPOET.m_Indentation ), LAYOUT_JAVAPOET.m_TabSize );

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The indentation String.
     */
    private final String m_Indentation;

    /**
     *  The tabulator size.
     */
    private final int m_TabSize;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance for {@code Layout}.
     *
     *  @param  useTab  {@code true} if the tabulator should be used for the
     *      indentation, {@code false} if blanks should be used.
     *  @param  tabSize The tabulator size.
     */
    private Layout( final boolean useTab, final int tabSize )
    {
        assert tabSize >= 0 : "Invalid tabulator size";
        m_TabSize = tabSize;
        m_Indentation =  useTab ? "\t" : tabSize > 0 ? " ".repeat( tabSize ) : EMPTY_STRING;
    }   //  Layout()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     * {@inheritDoc}
     */
    @Override
    public final String indent() { return m_Indentation; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int tabsize() { return m_TabSize; }
}
//  enum Layout

/*
 *  End of File
 */