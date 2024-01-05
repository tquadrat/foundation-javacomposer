/*
 * ============================================================================
 * Copyright © 2015 Square, Inc.
 * Copyright for the modifications © 2018-2024 by Thomas Thrien.
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

import static java.lang.String.join;
import static java.util.Collections.unmodifiableMap;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.toCollection;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.JAVADOC;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.createSuppressWarningsAnnotation;
import static org.tquadrat.foundation.javacomposer.internal.Util.NULL_REFERENCE;
import static org.tquadrat.foundation.javacomposer.internal.Util.stringLiteralWithDoubleQuotes;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.CommonConstants.NULL_STRING;
import static org.tquadrat.foundation.lang.Objects.checkState;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidIntegerArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidNonNullArgument;
import static org.tquadrat.foundation.util.StringUtils.isEmpty;
import static org.tquadrat.foundation.util.StringUtils.isNotEmpty;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.UnsupportedEnumError;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.Layout;
import org.tquadrat.foundation.lang.Objects;

/**
 *  Converts a
 *  {@link org.tquadrat.foundation.javacomposer.JavaFile JavaFile}
 *  to a string suitable to both human- and javac-consumption. This honours
 *  imports, indentation, and deferred variable names.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: CodeWriter.java 1085 2024-01-05 16:23:28Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( {"ClassWithTooManyFields", "ClassWithTooManyMethods", "OverlyComplexClass"} )
@ClassVersion( sourceVersion = "$Id: CodeWriter.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public final class CodeWriter
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The comment types.
     *
     *  @extauthor  Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: CodeWriter.java 1085 2024-01-05 16:23:28Z tquadrat $
     *  @since 0.2.0
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: CodeWriter.java 1085 2024-01-05 16:23:28Z tquadrat $" )
    @API( status = INTERNAL, since = "0.0.5" )
    private static enum CommentType
    {
            /*------------------*\
        ====** Enum Declaration **=============================================
            \*------------------*/
        /**
         *  No comment at all.
         */
        NO_COMMENT,

        /**
         *  A Javadoc comment.
         */
        JAVADOC_COMMENT,

        /**
         *  A block comment.
         */
        BLOCK_COMMENT,

        /**
         *  A line comment.
         */
        LINE_COMMENT
    }
    //  enum CommentType

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  Sentinel value that indicates that no user-provided package has been
     *  set.
     */
    @SuppressWarnings( "StringOperationCanBeSimplified" )
    private static final String NO_PACKAGE = new String();

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The reference to the factory.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final JavaComposer m_Composer;

    /**
     *  Flag that indicates that we are currently writing a normal comment.
     */
    private CommentType m_CurrentlyEmittingComment = CommentType.NO_COMMENT;

    /**
     *  The types that can be imported.
     */
    private final Map<String,ClassNameImpl> m_ImportableTypes = new LinkedHashMap<>();

    /**
     *  The imported types.
     */
    private final Map<String,ClassNameImpl> m_ImportedTypes;

    /**
     *  The indentation.
     */
    private final String m_Indent;

    /**
     *  The indentation level.
     */
    private int m_IndentLevel;

    /**
     *  The layout for the output.
     */
    private final Layout m_Layout;

    /**
     *  The output target.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final LineWrapper m_LineWrapper;

    /**
     *  The current package name.
     */
    private String m_PackageName = NO_PACKAGE;

    /**
     *  The referenced names.
     */
    private final Collection<String> m_ReferencedNames = new LinkedHashSet<>();

    /**
     *  When a statement will be emitted, this is the line of the statement
     *  currently being written. The first line of a statement is indented
     *  normally and subsequent wrapped lines are double-indented. This is -1
     *  when the currently-written line isn't part of a statement.
     */
    private int m_StatementLine = -1;

    /**
     *  The names of statically imported classes.
     */
    private final Set<String> m_StaticImportClassNames;

    /**
     *  The static imports.
     */
    private final Set<String> m_StaticImports;

    /**
     *  A flag that controls the trailing new line.
     */
    private boolean m_TrailingNewline;

    /**
     *  The types.
     */
    private final List<TypeSpecImpl> m_TypeSpecStack = new ArrayList<>();

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code CodeWriter} instance.
     *
     *  @param  out The output target.
     *
     *  @deprecated Use
     *      {@link #CodeWriter(JavaComposer, Appendable)}
     *      instead.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    public CodeWriter( final Appendable out ) { this( new JavaComposer(), out, Set.of() ); }

    /**
     *  Creates a new {@code CodeWriter} instance.
     *
     *  @param  out The output target.
     *  @param  layout  The layout for the output.
     *  @param  staticImports   The static imports.
     *
     *  @deprecated Use
     *      {@link #CodeWriter(JavaComposer, Appendable)}
     *      instead.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    public CodeWriter( final Appendable out, final Layout layout, final Set<String> staticImports )
    {
        this( new JavaComposer( layout ), out, Map.of(), staticImports );
    }   //  CodeWriter()

    /**
     *  Creates a new {@code CodeWriter} instance.
     *
     *  @param  out The output target.
     *  @param  layout  The layout for the output.
     *  @param  indent  The indentation; will be ignored.
     *  @param  importedTypes   The imported types.
     *  @param  staticImports   The static imports.
     *
     *  @deprecated Use
     *      {@link #CodeWriter(JavaComposer, Appendable)}
     *      instead.
     */
    @SuppressWarnings( "unused" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    public CodeWriter( final Appendable out, final Layout layout, final String indent, final Map<String,ClassNameImpl> importedTypes, final Set<String> staticImports )
    {
        this( new JavaComposer( layout ), out, importedTypes, staticImports );
    }   //  CodeWriter()

    /**
     *  Creates a new {@code CodeWriter} instance.
     *
     *  @param  composer    The reference to the factory that created this
     *      code writer instance.
     *  @param  out The output target.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public CodeWriter( final JavaComposer composer, final Appendable out ) { this( composer, out, Set.of() ); }

    /**
     *  Creates a new {@code CodeWriter} instance.
     *
     *  @param  composer    The reference to the factory that created this
     *      code writer instance.
     *  @param  out The output target.
     *  @param  staticImports   The static imports.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public CodeWriter( final JavaComposer composer, final Appendable out, final Set<String> staticImports )
    {
        this( composer, out, Map.of(), staticImports );
    }   //  CodeWriter()

    /**
     *  Creates a new {@code CodeWriter} instance.
     *
     *  @param  composer    The reference to the factory that created this
     *      code writer instance.
     *  @param  out The output target.
     *  @param  importedTypes   The imported types.
     *  @param  staticImports   The static imports.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public CodeWriter( final JavaComposer composer, final Appendable out, final Map<String,ClassNameImpl> importedTypes, final Set<String> staticImports )
    {
        m_Composer = requireNonNullArgument( composer, "composer" );
        m_Layout = m_Composer.getLayout();
        m_Indent = m_Layout.indent();
        m_LineWrapper = new LineWrapper( requireNonNullArgument( out, "out" ), m_Indent, 100 );
        m_ImportedTypes = requireNonNullArgument( importedTypes, "importedTypes" );
        m_StaticImports = requireNonNullArgument( staticImports, "staticImports" );
        m_StaticImportClassNames = m_StaticImports.stream()
            .map( s -> s.substring( 0, s.lastIndexOf( '.' ) ) )
            .collect( toCollection( LinkedHashSet::new ) );
    }   //  CodeWriter()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  <p>{@summary Emits the given String to the output target.}</p>
     *  <p>Delegates to
     *  {@link #emitAndIndent(CharSequence)}.</p>
     *
     *  @param  input   The String.
     *  @return This {@code CodeWriter} instance.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    public final CodeWriter emit( final CharSequence input ) throws UncheckedIOException { return emitAndIndent( input ); }

    /**
     *  Emits a
     *  {@link CodeBlockImpl}
     *  instance to the output target that is created on the fly from the given
     *  arguments.
     *
     *  @param  format  The format.
     *  @param  args    The arguments.
     *  @return This {@code CodeWriter} instance.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    public final CodeWriter emit( final String format, final Object... args ) throws UncheckedIOException
    {
        final var builder = new CodeBlockImpl.BuilderImpl( m_Composer );
        builder.addWithoutDebugInfo( format, args );
        emit( builder.build() );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  emit()

    /**
     *  Emits the given
     *  {@link CodeBlockImpl}
     *  instance to the output target.
     *
     *  @param  codeBlock   The code block.
     *  @return This {@code CodeWriter} instance.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( {"AssignmentToNull", "OverlyNestedMethod", "OverlyComplexMethod", "UseOfConcreteClass"} )
    public final CodeWriter emit( final CodeBlockImpl codeBlock ) throws UncheckedIOException
    {
        var argIndex = 0;
        ClassNameImpl deferredTypeName = null; // used by "import static" logic
        final var partIterator = requireNonNullArgument( codeBlock, "codeBlock" ).formatParts().listIterator();
        while( partIterator.hasNext() )
        {
            final var part = partIterator.next();
            //noinspection SwitchStatementWithTooManyBranches
            switch( part )
            {
                case "$L" -> emitLiteral( codeBlock.args().get( argIndex++ ) );
                case "$N" -> emitAndIndent( (CharSequence) codeBlock.args().get( argIndex++ ) );
                case "$S" ->
                {
                    final var string = codeBlock.args().get( argIndex++ );

                    //---* Emit null as a literal null: no quotes *------------
                    emitAndIndent( string == NULL_REFERENCE ? "null" : stringLiteralWithDoubleQuotes( (String) string, m_Indent ) );
                }

                case "$T" ->
                {
                    final var typeName = (TypeNameImpl) codeBlock.args().get( argIndex++ );

                    /*
                     * Defer "typeName.emit(this)" if next format part will be
                     * handled by the default case.
                     */
                    deferredTypeName = null;
                    if( typeName instanceof final ClassNameImpl candidate && partIterator.hasNext() )
                    {
                        if( !codeBlock.formatParts().get( partIterator.nextIndex() ).startsWith( "$" ) )
                        {
                            if( m_StaticImportClassNames.contains( candidate.canonicalName() ) )
                            {
                                checkState( isNull( deferredTypeName ), () -> new IllegalStateException( "pending type for static import?!" ) );
                                deferredTypeName = candidate;
                            }
                        }
                    }
                    if( isNull( deferredTypeName ) ) typeName.emit( this );
                }

                case "$$" -> emitAndIndent( "$" );
                case "$>" -> indent();
                case "$<" -> unindent();
                case "$[" ->
                {
                    checkState( m_StatementLine == -1, () -> new IllegalStateException( "statement enter $[ followed by statement enter $[" ) );
                    m_StatementLine = 0;
                }

                case "$]" ->
                {
                    checkState( m_StatementLine != -1, () -> new IllegalStateException( "statement exit $] has no matching statement enter $[" ) );
                    if( m_StatementLine > 0 )
                    {
                        unindent( 2 ); // End a multi-line statement. Decrease
                                       // the indentation level.
                    }
                    m_StatementLine = -1;
                }

                case "$W" ->
                {
                    try
                    {
                        m_LineWrapper.wrappingSpace( m_IndentLevel + 2 );
                    }
                    catch( final IOException e )
                    {
                        throw new UncheckedIOException( e );
                    }
                }

                case "$Z" ->
                {
                    try
                    {
                        m_LineWrapper.zeroWidthSpace( m_IndentLevel + 2 );
                    }
                    catch( final IOException e )
                    {
                        throw new UncheckedIOException( e );
                    }
                }

                default ->
                {
                    //---* Handle deferred type *------------------------------
                    if( nonNull( deferredTypeName ) )
                    {
                        if( part.startsWith( "." ) )
                        {
                            if( emitStaticImportMember( deferredTypeName.canonicalName(), part ) )
                            {
                                /*
                                 * Okay, static import hit and all was emitted,
                                 * so clean-up and jump to next part.
                                 */
                                deferredTypeName = null;
                                break;
                            }
                        }
                        deferredTypeName.emit( this );
                        deferredTypeName = null;
                    }
                    emitAndIndent( part );
                }
            }
        }

        //---* Done *----------------------------------------------------------
        return this;
    }   //  emit()

    /**
     *  Emits the given String to the output target with indentation as
     *  required. It's important that all code that writes to
     *  {@link #m_LineWrapper}
     *  does it through here, since we emit indentation lazily in order to
     *  avoid unnecessary trailing whitespace.
     *
     *  @param  input   The String.
     *  @return This {@code CodeWriter} instance.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( "OverlyComplexMethod" )
    public final CodeWriter emitAndIndent( final CharSequence input ) throws UncheckedIOException
    {
        if( isNotEmpty( input ) )
        {
            var first = true;
            LineLoop: for( final var line : input.toString().split( "\n", -1 ) )
            {
                /*
                 * Emit a newline character. Make sure blank lines in Javadoc
                 * and comments look good.
                 */
                if( !first )
                {
                    if( (m_CurrentlyEmittingComment != CommentType.NO_COMMENT) && m_TrailingNewline )
                    {
                        emitIndentation();
                        try
                        {
                            m_LineWrapper.append( m_CurrentlyEmittingComment == CommentType.LINE_COMMENT ? "//" : " *" );
                        }
                        catch( final IOException e )
                        {
                            throw new UncheckedIOException( e );
                        }
                    }
                    try
                    {
                        m_LineWrapper.append( "\n" );
                    }
                    catch( final IOException e )
                    {
                        throw new UncheckedIOException( e );
                    }
                    m_TrailingNewline = true;
                    if( m_StatementLine != -1 )
                    {
                        if( m_StatementLine == 0 )
                        {
                            /*
                             * Begin multiple-line statement. Increase the
                             * indentation level.
                             */
                            indent( 2 );
                        }
                        ++m_StatementLine;
                    }
                }

                first = false;
                if( line.isEmpty() )
                {
                    //---* Don't indent empty lines *--------------------------
                    continue LineLoop;
                }

                //---* Emit indentation and comment prefix if necessary *------
                if( m_TrailingNewline )
                {
                    emitIndentation();
                    try
                    {
                        switch( m_CurrentlyEmittingComment )
                        {
                            case BLOCK_COMMENT, JAVADOC_COMMENT -> m_LineWrapper.append( " * " );
                            case LINE_COMMENT -> m_LineWrapper.append( "// " );
                            case NO_COMMENT -> m_LineWrapper.append( EMPTY_STRING );
                            default -> throw new UnsupportedEnumError( m_CurrentlyEmittingComment );
                        }
                    }
                    catch( final IOException e )
                    {
                        throw new UncheckedIOException( e );
                    }
                }

                try
                {
                    m_LineWrapper.append( line );
                }
                catch( final IOException e )
                {
                    throw new UncheckedIOException( e );
                }
                m_TrailingNewline = false;
            }
        }   //  LineLoop:

        //---* Done *----------------------------------------------------------
        return this;
    }   //  emitAndIndent()

    /**
     *  Emits the given annotations to the output target.
     *
     *  @param  annotations The annotations.
     *  @param  inline  {@code true} if the annotations should be placed on the
     *      same line as the annotated element, {@code false} otherwise.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    public final void emitAnnotations( final Iterable<AnnotationSpecImpl> annotations, final boolean inline ) throws UncheckedIOException
    {
        for( final var annotationSpec : annotations )
        {
            annotationSpec.emit( this, inline );
            emit( inline ? " " : "\n" );
        }
    }   //  emitAnnotations()

    /**
     *  Emits the given
     *  {@link CodeBlockImpl}
     *  instance as a block comment to the output target.
     *
     *  @param  codeBlock   The code block with the comment.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( {"UseOfConcreteClass", "ThrowFromFinallyBlock"} )
    public final void emitBlockComment( final CodeBlockImpl codeBlock ) throws UncheckedIOException
    {
        emit( "/*\n" );
        m_TrailingNewline = true; // Force the ' *' prefix for the comment.
        m_CurrentlyEmittingComment = CommentType.BLOCK_COMMENT;
        try
        {
            emit( codeBlock );
            emit( "\n" );
        }
        finally
        {
            m_CurrentlyEmittingComment = CommentType.NO_COMMENT;
            emit( " */\n" );
        }
    }   //  emitBlockComment()

    /**
     *  Writes the indentation to the output target.
     *
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    private final void emitIndentation() throws UncheckedIOException
    {
        try
        {
            for( var i = 0; i < m_IndentLevel; ++i ) m_LineWrapper.append( m_Indent );
        }
        catch( final IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }   //  emitIndentation()

    /**
     *  Emits the given
     *  {@link CodeBlockImpl}
     *  instance as a JavaDoc comment to the output target.
     *
     *  @param  codeBlock   The code block with the JavaDoc comment.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public final void emitJavadoc( final CodeBlockImpl codeBlock ) throws UncheckedIOException
    {
        if( codeBlock.isEmpty() )
        {
            LayoutSwitch: switch( m_Layout )
            {
                case LAYOUT_DEFAULT:
                case LAYOUT_JAVAPOET:
                case LAYOUT_JAVAPOET_WITH_TAB: break;

                case LAYOUT_FOUNDATION:
                {
                    emitAnnotations( List.of( (AnnotationSpecImpl) createSuppressWarningsAnnotation( m_Composer, JAVADOC ) ), false );
                    break;
                }

                default: throw new UnsupportedEnumError( m_Layout );
            }   //  LayoutSwitch:
        }
        else
        {
            emit( "/**\n" );
            m_CurrentlyEmittingComment = CommentType.JAVADOC_COMMENT;
            try
            {
                emit( codeBlock );
            }
            finally
            {
                m_CurrentlyEmittingComment = CommentType.NO_COMMENT;
            }
            emit( " */\n" );
        }
    }   //  emitJavadoc()

    /**
     *  Emits the given
     *  {@link CodeBlockImpl}
     *  instance as a line comment to the output target.
     *
     *  @param  codeBlock   The code block with the comment.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public final void emitLineComment( final CodeBlockImpl codeBlock ) throws UncheckedIOException
    {
        m_TrailingNewline = true; // Force the '//' prefix for the comment.
        m_CurrentlyEmittingComment = CommentType.LINE_COMMENT;
        try
        {
            emit( codeBlock );
            emit( "\n" );
        }
        finally
        {
            m_CurrentlyEmittingComment = CommentType.NO_COMMENT;
        }
    }   //  emitLineComment()

    /**
     *  Emits the given argument literally to the output target.
     *
     *  @param  o   The object to emit.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( {"IfStatementWithTooManyBranches", "ChainOfInstanceofChecks"} )
    private final void emitLiteral( final Object o ) throws UncheckedIOException
    {
        if( o instanceof final TypeSpecImpl typeSpec )
        {
            typeSpec.emit( this, null, Set.of() );
        }
        else if( o instanceof final AnnotationSpecImpl annotationSpec )
        {
            annotationSpec.emit( this, true );
        }
        else if( o instanceof final CodeBlockImpl codeBlock )
        {
            emit( codeBlock );
        }
        else if( o == NULL_REFERENCE )
        {
            emitAndIndent( NULL_STRING );
        }
        else
        {
            emitAndIndent( String.valueOf( o ) );
        }
    }   //  emitLiteral()

    /**
     *  Emits {@code modifiers} to the output target in the standard order.
     *  Modifiers in {@code implicitModifiers} will not be emitted.
     *
     *  @param  modifiers   The modifiers to emit.
     *  @param  implicitModifiers   The modifiers to omit.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    public final void emitModifiers( final Collection<Modifier> modifiers, final Collection<Modifier> implicitModifiers ) throws UncheckedIOException
    {
        if( !modifiers.isEmpty() )
        {
            for( final var modifier : EnumSet.copyOf( modifiers ) )
            {
                if( !implicitModifiers.contains( modifier ) )
                {
                    emitAndIndent( modifier.name().toLowerCase( ROOT ) );
                    emitAndIndent( " " );
                }
            }
        }
    }   //  emitModifiers()

    /**
     *  Emits {@code modifiers} to the output target in the standard order.
     *
     *  @param  modifiers   The modifiers to emit.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    public final void emitModifiers( final Collection<Modifier> modifiers ) throws UncheckedIOException
    {
        emitModifiers( modifiers, Set.of() );
    }   //  emitModifiers()

    /**
     *  Emits a static import entry to the output target.
     *
     *  @param  canonical   The canonical name of the class to import.
     *  @param  part    The part to emit.
     *  @return {@code true} if something was emitted, {@code false} otherwise.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( "BooleanMethodNameMustStartWithQuestion" )
    private final boolean emitStaticImportMember( final String canonical, final String part ) throws UncheckedIOException
    {
        final var partWithoutLeadingDot = requireNonNullArgument( part, "part" ).substring( 1 );

        var retValue = !partWithoutLeadingDot.isEmpty();
        if( retValue )
        {
            final var first = partWithoutLeadingDot.charAt( 0 );
            //noinspection NestedAssignment
            if( (retValue = Character.isJavaIdentifierStart( first )) == true )
            {
                final var explicit = canonical + "." + extractMemberName( partWithoutLeadingDot );
                final var wildcard = canonical + ".*";
                //noinspection NestedAssignment
                if( (retValue = m_StaticImports.contains( explicit ) || m_StaticImports.contains( wildcard )) == true )
                {
                    emitAndIndent( partWithoutLeadingDot );
                }
            }
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  emitStaticImportMember()

    /**
     *  Emits type variables with their bounds. This should only be used when
     *  declaring type variables; everywhere else bounds are omitted.
     *
     *  @param  typeVariables   The type variables.
     *  @throws UncheckedIOException A problem occurred when writing to the output
     *      target.
     */
    public final void emitTypeVariables( final List<TypeVariableNameImpl> typeVariables ) throws UncheckedIOException
    {
        if( !requireNonNullArgument( typeVariables, "typeVariables" ).isEmpty() )
        {
            emit( "<" );
            var firstTypeVariable = true;
            for( final var typeVariable : typeVariables )
            {
                if( !firstTypeVariable ) emit( ", " );
                emitAnnotations( typeVariable.annotations(), true );
                emit( "$L", typeVariable.name() );
                var firstBound = true;
                for( final var bound : typeVariable.bounds() )
                {
                    emit( firstBound ? " extends $T" : " & $T", bound );
                    firstBound = false;
                }
                firstTypeVariable = false;
            }
            emit( ">" );
        }
    }   //  emitTypeVariables()

    /**
     *  Emits wrapping space to the output target.
     *
     *  @return This {@code CodeWriter} instance.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    public final CodeWriter emitWrappingSpace() throws UncheckedIOException
    {
        try
        {
            m_LineWrapper.wrappingSpace( m_IndentLevel + 2 );
        }
        catch( final IOException e )
        {
            throw new UncheckedIOException( e );
        }

        //---* Done *----------------------------------------------------------
        return this;
    }   //  emitWrappingSpace()

    /**
     *  Extracts a member name from the given part.
     *
     *  @param  part    The part.
     *  @return The member name, or if none could be found, the given part.
     */
    private static final String extractMemberName( final String part )
    {
        var retValue = requireValidNonNullArgument( part, "part", v -> Character.isJavaIdentifierStart( v.charAt( 0 ) ), _ -> "not an identifier: %s".formatted( part ) );
        CheckLoop: for( var i = 1; i <= part.length(); ++i )
        {
            if( !SourceVersion.isIdentifier( part.substring( 0, i ) ) )
            {
                retValue = part.substring( 0, i - 1 );
                break CheckLoop;
            }
        }   //  CheckLoop:

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  extractMemberName()

    /**
     *  Marks the given type as importable.
     *
     *  @param  className   The type.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final void importableType( final ClassNameImpl className )
    {
        if( !requireNonNullArgument( className, "className" ).packageName().isEmpty() )
        {
            final var topLevelClassName = className.topLevelClassName();
            final var simpleName = topLevelClassName.simpleName();
            m_ImportableTypes.putIfAbsent( simpleName, topLevelClassName );
        }
    }   //  importableType

    /**
     *  Returns the imported types.
     *
     *  @return The imported types.
     */
    /*
     * Originally, the return value was a reference to the internal field that
     * allowed the modification of the Map.
     */
    public final Map<String,ClassNameImpl> importedTypes() { return unmodifiableMap( m_ImportedTypes ); }

    /**
     *  Increments the indentation level.
     *
     *  @return This {@code CodeWriter} instance.
     */
    public final CodeWriter indent() { return indent( 1 ); }

    /**
     *  Increases the indentation level by the given value.
     *
     *  @param  levels  The increase value.
     *  @return This {@code CodeWriter} instance.
     */
    public final CodeWriter indent( final int levels )
    {
        m_IndentLevel += levels;

        //---* Done *----------------------------------------------------------
        return this;
    }   //  indent()

    /**
     *  Returns the layout for the output.
     *
     *  @return The layout.
     */
    public final Layout layout() { return m_Layout; }

    /**
     *  Returns the best name to identify {@code className} within the current
     *  context. This uses the available imports and the current scope to find
     *  the shortest name available. It does not honour names that are visible
     *  due to inheritance.
     *
     *  @param  className   The name of the class.
     *  @return The shortest possible name for the given class.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public final String lookupName( final ClassNameImpl className )
    {
        String retValue = null;
        /*
         * Find the shortest suffix of className that resolves to className.
         * This uses both local type names (so `Entry` in 'Map' refers to
         * 'Map.Entry'). Also uses imports.
         */
        var nameResolved = false;
        for( var currentClassName = className; nonNull( currentClassName ) && isEmpty( retValue ); currentClassName = currentClassName.enclosingClassName().orElse( null ) )
        {
            final var resolved = resolve( currentClassName.simpleName() );
            nameResolved = resolved.isPresent();

            if( nameResolved && Objects.equals( resolved.get().canonicalName(), currentClassName.canonicalName() ) )
            {
                final var suffixOffset = currentClassName.simpleNames().size() - 1;
                retValue = join( ".", className.simpleNames().subList( suffixOffset, className.simpleNames().size() ) );
            }
        }

        if( isEmpty( retValue ) )
        {
            /*
             * If the name resolved but wasn't a match, we're stuck with the
             * fully qualified name.
             */
            if( nameResolved )
            {
                retValue = className.canonicalName();
            }
            else
            //---* If the class is in the same package, we're done *-----------
            {
                if( Objects.equals( m_PackageName, className.packageName() ) )
                {
                    m_ReferencedNames.add( className.topLevelClassName().simpleName() );
                    retValue = join( ".", className.simpleNames() );
                }
                else
                {
                    /*
                     * We'll have to use the fully-qualified name. Mark the
                     * type as importable for a future pass.
                     */
                    if( m_CurrentlyEmittingComment != CommentType.JAVADOC_COMMENT ) importableType( className );
                    retValue = className.canonicalName();
                }
            }
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  lookupName()

    /**
     *  Pops the package name.
     *
     *  @return This {@code CodeWriter} instance.
     */
    @SuppressWarnings( {"UnusedReturnValue", "StringEquality"} )
    public final CodeWriter popPackage()
    {
        checkState( m_PackageName != NO_PACKAGE, () -> new IllegalStateException( "package not set" ) );
        m_PackageName = NO_PACKAGE;

        //---* Done *----------------------------------------------------------
        return this;
    }   //  popPackage()

    /**
     *  Pops the top most type.
     *
     *  @return This {@code CodeWriter} instance.
     */
    @SuppressWarnings( "UnusedReturnValue" )
    public final CodeWriter popType()
    {
        m_TypeSpecStack.removeLast();

        //---* Done *----------------------------------------------------------
        return this;
    }   //  popPackage()

    /**
     *  Pushes the given package name.
     *
     *  @param  packageName The name of the package.
     *  @return This {@code CodeWriter} instance.
     */
    @SuppressWarnings( {"UnusedReturnValue", "StringEquality"} )
    public final CodeWriter pushPackage( final String packageName )
    {
        checkState( m_PackageName == NO_PACKAGE, () -> new IllegalStateException( "package already set: %s".formatted( m_PackageName ) ) );
        m_PackageName = requireNonNullArgument( packageName, "packageName" );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  pushPackage()

    /**
     *  Pushes the give type.
     *
     *  @param  type    The type.
     *  @return This {@code CodeWriter} instance.
     */
    @SuppressWarnings( "UnusedReturnValue" )
    public final CodeWriter pushType( final TypeSpecImpl type )
    {
        m_TypeSpecStack.add( type );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  pushType()

    /**
     *  Returns the class referenced by {@code simpleName}, using the current
     *  nesting context and imports.
     *
     *  @param  simpleName  The name of the class we search for.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the {@code ClassName} instance for the resolved class.
     */
    // TODO(jwilson): also honour superclass members when resolving names.
    @SuppressWarnings( "OptionalGetWithoutIsPresent" )
    private final Optional<ClassNameImpl> resolve( final String simpleName )
    {
        Optional<ClassNameImpl> retValue = Optional.empty();

        //---* Match a child of the current (potentially nested) class *-------
        for( var i = m_TypeSpecStack.size() - 1; (i >= 0) && retValue.isEmpty(); --i )
        {
            final var typeSpec = m_TypeSpecStack.get( i );
            for( final var visibleChild : typeSpec.typeSpecs() )
            {
                if( Objects.equals( visibleChild.name().get(), simpleName ) )
                {
                    retValue = Optional.of( stackClassName( i, simpleName ) );
                }
            }
        }

        if( retValue.isEmpty() )
        {
            //---* Match the top-level class *---------------------------------
            if( (!m_TypeSpecStack.isEmpty()) && Objects.equals( m_TypeSpecStack.getFirst().name(), simpleName ) )
            {
                retValue = Optional.of( ClassNameImpl.from( m_PackageName, simpleName ) );
            }
            else
            {
                //---* Match an imported type *--------------------------------
                final var importedType = m_ImportedTypes.get( simpleName );
                retValue = Optional.ofNullable( importedType );
            }
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  resolve()

    /**
     *  Returns the class named {@code simpleName} when nested in the class at
     *  {@code stackDepth}.
     *
     *  @param  simpleName  The class name.
     *  @param  stackDepth  The search depth.
     *  @return The found class.
     */
    @SuppressWarnings( "OptionalGetWithoutIsPresent" )
    private final ClassNameImpl stackClassName( final int stackDepth, final String simpleName )
    {
        /*
         * The type spec stack may not contain anonymous types, so no check for
         * the name is required.
         */
        @SuppressWarnings( "OptionalGetWithoutIsPresent" )
        var className = ClassNameImpl.from( m_PackageName, m_TypeSpecStack.getFirst().name().get() );
        for( var i = 1; i <= stackDepth; ++i )
        {
            className = className.nestedClass( m_TypeSpecStack.get( i ).name().get() );
        }
        final var retValue = className.nestedClass( simpleName );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  stackClassName()

    /**
     *  <p>{@summary Returns the current statement line.}</p>
     *  <p>When a statement will be emitted, this method returns the line of
     *  the statement currently being written. The first line of a statement is
     *  indented normally and subsequent wrapped lines are double-indented.
     *  This is -1 when the currently-written line isn't part of a
     *  statement.</p>
     *
     *  @return The statement line, or -1.
     */
    public final int statementLine() { return m_StatementLine; }

    /**
     *  Sets the current statement line.
     *
     *  @param  statementLine   The new value for the current statement line.
     *
     *  @see #statementLine()
     */
    public final void statementLine( final int statementLine ) { m_StatementLine = statementLine; }

    /**
     *  Returns the types that should have been imported for this code. If
     *  there were any simple name collisions, that type's first use is
     *  imported.
     *
     *  @return The types that should have been imported.
     */
    public final Map<String,ClassNameImpl> suggestedImports()
    {
        final Map<String,ClassNameImpl> retValue = new LinkedHashMap<>( m_ImportableTypes );
        retValue.keySet().removeAll( m_ReferencedNames );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  suggestedImports()

    /**
     *  Decrements the indentation level.
     *
     *  @return This {@code CodeWriter} instance.
     */
    public final CodeWriter unindent() { return unindent( 1 ); }

    /**
     *  Decreases the indentation level by the given value.
     *
     *  @param  levels  The decrease value.
     *  @return This {@code CodeWriter} instance.
     */
    public final CodeWriter unindent( final int levels )
    {
        m_IndentLevel -= requireValidIntegerArgument( levels, "levels", _ -> m_IndentLevel - levels >= 0, _ -> "cannot unindent %d from %d".formatted( levels, m_IndentLevel ) );

        //---* Done *----------------------------------------------------------
        return this;
    }   //  unindent()
}
//  class CodeWriter

/*
 *  End of File
 */