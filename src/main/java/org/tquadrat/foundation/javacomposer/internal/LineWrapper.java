/*
 * ============================================================================
 * Copyright © 2015 Square, Inc.
 * Copyright for the modifications © 2018-2023 by Thomas Thrien.
 * ============================================================================
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tquadrat.foundation.javacomposer.internal;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.require;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.util.StringUtils.isNotEmpty;

import java.io.Closeable;
import java.io.IOException;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.UnsupportedEnumError;

/**
 *  Implements soft line wrapping on an
 *  {@link Appendable}.
 *  To use, append characters using
 *  {@link #append(CharSequence)}
 *  or soft-wrapping spaces using
 *  {@link #wrappingSpace(int)}.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: LineWrapper.java 1065 2023-09-28 06:16:50Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: LineWrapper.java 1065 2023-09-28 06:16:50Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public final class LineWrapper implements Closeable
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The flush types.
     *
     *  @see LineWrapper#flush(FlushType)
     *
     *  @author Square,Inc.
     *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: LineWrapper.java 1065 2023-09-28 06:16:50Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    private enum FlushType
    {
            /*------------------*\
        ====** Enum Declaration **=============================================
            \*------------------*/
        /**
         *  Add nothing.
         */
        EMPTY,

        /**
         *  Add a single blank space.
         */
        SPACE,

        /**
         *  Add a new line.
         */
        WRAP
    }
    //  enum FlushType

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  Characters written since the last wrapping space that haven't yet been
     *  flushed.
     */
    @SuppressWarnings( "StringBufferField" )
    private final StringBuilder m_Buffer = new StringBuilder();

    /**
     *  The flag that indicates whether this line wrapper was already closed.
     */
    private boolean m_Closed = false;

    /**
     *  The number of characters since the most recent newline. Includes both
     *  {@link #m_Out}
     *  and
     *  {@link #m_Buffer}.
     */
    private int m_Column = 0;

    /**
     *  The maximum line length.
     */
    private final int m_ColumnLimit;

    /**
     *  The indentation String.
     */
    private final String m_Indent;

    /**
     * -1 if we have no buffering; otherwise the number of
     * {@link #m_Indent}s
     * to write after wrapping.
     */
    private int m_IndentLevel = -1;

    /**
     * {@code null} if we have no buffering; otherwise the type to pass to the
     * next call to
     * {@link #flush}.
     */
    private FlushType m_NextFlush;

    /**
     *  The output target.
     */
    private final Appendable m_Out;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code LineWrapper} instance.
     *
     *  @param  out The output target.
     *  @param  indent  The indentation string.
     *  @param  columnLimit The maximum line length.
     */
    public LineWrapper( final Appendable out, final String indent, final int columnLimit )
    {
        m_Out = requireNonNullArgument( out, "out" );
        m_Indent = requireNonNullArgument( indent, "indent" );
        m_ColumnLimit = require( columnLimit, v -> "columnLimit is 0 or negative: %d".formatted( columnLimit ), v -> v > 0 );
    }   //  LineWrapper()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Emits the given String. This may be buffered to permit line wraps to be
     *  inserted.
     *
     *  @param  input The string to emit.
     *  @throws IOException A problem occurred when writing to the
     *      output target.
     */
    public final void append( final CharSequence input ) throws IOException
    {
        if( m_Closed ) throw new IllegalStateException( "closed" );

        if( isNotEmpty( input ) )
        {
            final var data = input.toString();
            final var len = data.length();
            var buffered = false;
            if( nonNull( m_NextFlush ) )
            {
                final var nextNewline = data.indexOf( '\n' );

                /*
                 * If data doesn't cause the current line to cross the limit,
                 * buffer it and return. We'll decide later whether we have to
                 * wrap it or not.
                 */
                if( (nextNewline == -1) && (m_Column + len <= m_ColumnLimit) )
                {
                    m_Buffer.append( data );
                    m_Column += len;
                    buffered = true;
                }
                else
                {
                    /*
                     * Wrap if appending s would overflow the current line.
                     */
                    final var wrap = nextNewline == -1 || m_Column + nextNewline > m_ColumnLimit;
                    flush( wrap ? FlushType.WRAP : m_NextFlush );
                }
            }

            if( !buffered )
            {
                m_Out.append( data );
                final var lastNewline = data.lastIndexOf( '\n' );
                //noinspection ConditionalExpressionWithNegatedCondition
                m_Column = lastNewline != -1 ? len - lastNewline - 1 : m_Column + len;
            }
        }
    }   //  append()

    /**
     *  This implementation flushes any outstanding text and forbid future
     *  writes to this line wrapper.
     */
    @Override
    public final void close() throws IOException
    {
        if( !m_Closed )
        {
            if( nonNull( m_NextFlush ) ) flush( m_NextFlush );
            m_Closed = true;
        }
    }   //  close()

    /**
     *  Writes the space followed by any buffered text that follows it.
     *
     *  @param  flushType   The flush type.
     *  @throws IOException A problem occurred when writing to the output
     *      target.
     */
    private final void flush( final FlushType flushType ) throws IOException
    {
        switch( flushType )
        {
            case WRAP:
            {
                m_Out.append( '\n' );
                for( var i = 0; i < m_IndentLevel; ++i )
                {
                    m_Out.append( m_Indent );
                }
                m_Column = m_IndentLevel * m_Indent.length();
                m_Column += m_Buffer.length();
                break;
            }

            case SPACE:
            {
                m_Out.append( ' ' );
                break;
            }

            case EMPTY:
                break;

            default:
                throw new UnsupportedEnumError( flushType );
        }

        m_Out.append( m_Buffer );

        /*
         * Originally, this was:
         *
         *    m_Buffer.delete( 0, m_Buffer.length() );
         */
        m_Buffer.setLength( 0 );
        /*
         * This implementation will keep the internal size of the
         * StringBuilder, so some heap space is wasted until the LineWrapper
         * will be garbage collected.
         */
        m_IndentLevel = -1;
        m_NextFlush = null;
    }   //  flush()

    /**
     *  Emits either a space or a newline character.
     *
     *  @param  indentLevel The indentation level.
     *  @throws IOException A problem occurred when writing to the output
     *      target.
     */
    public final void wrappingSpace( final int indentLevel ) throws IOException
    {
        if( m_Closed ) throw new IllegalStateException( "closed" );

        if( nonNull( m_NextFlush ) ) flush( m_NextFlush );

        /*
         * Increment the column even though the space is deferred to next call
         * to flush().
         */
        ++m_Column;

        m_NextFlush = FlushType.SPACE;
        m_IndentLevel = indentLevel;
    }   //  wrappingSpace()

    /**
     *  Emits a newline character if the line will exceed its limit, otherwise
     *  do nothing.
     *
     *  @param  indentLevel The indentation level.
     *  @throws IOException A problem occurred when writing to the output
     *      target.
     */
    public final void zeroWidthSpace( final int indentLevel ) throws IOException
    {
        if( m_Closed ) throw new IllegalStateException( "closed" );

        if( m_Column > 0 )
        {
            if( nonNull( m_NextFlush ) ) flush( m_NextFlush );
            m_NextFlush = FlushType.EMPTY;
            m_IndentLevel = indentLevel;
        }
    }   //  zeroWidthSpace()
}
//  class LineWrapper

/*
 *  End of File
 */