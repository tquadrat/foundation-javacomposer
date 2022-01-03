/*
 * ============================================================================
 * Copyright © 2015 Square, Inc.
 * Copyright for the modifications © 2018-2021 by Thomas Thrien.
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

import static java.lang.Math.min;
import static java.util.stream.Collectors.toList;
import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.javacomposer.internal.Util.NO_DEBUG_OUTPUT;
import static org.tquadrat.foundation.javacomposer.internal.Util.NULL_REFERENCE;
import static org.tquadrat.foundation.javacomposer.internal.Util.createDebugOutput;
import static org.tquadrat.foundation.lang.Objects.checkState;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidNonNullArgument;
import static org.tquadrat.foundation.util.StringUtils.format;
import static org.tquadrat.foundation.util.StringUtils.isNotEmpty;
import static org.tquadrat.foundation.util.StringUtils.isNotEmptyOrBlank;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.UnexpectedExceptionError;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.MethodSpec;
import org.tquadrat.foundation.javacomposer.ParameterSpec;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.lang.Lazy;
import org.tquadrat.foundation.lang.Objects;

/**
 *  The implementation of
 *  {@link CodeBlock}
 *  for a fragment of a {@code *.java} file.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: CodeBlockImpl.java 943 2021-12-21 01:34:32Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( "ClassWithTooManyMethods" )
@ClassVersion( sourceVersion = "$Id: CodeBlockImpl.java 943 2021-12-21 01:34:32Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public final class CodeBlockImpl implements CodeBlock
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The implementation of
     *  {@link org.tquadrat.foundation.javacomposer.CodeBlock.Builder}
     *  as the builder for a new
     *  {@link CodeBlockImpl}
     *  instance.
     *
     *  @author Square,Inc.
     *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: CodeBlockImpl.java 943 2021-12-21 01:34:32Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @SuppressWarnings( {"ClassWithTooManyMethods", "OverlyComplexClass"} )
    @ClassVersion( sourceVersion = "$Id: CodeBlockImpl.java 943 2021-12-21 01:34:32Z tquadrat $" )
    @API( status = INTERNAL, since = "0.0.5" )
    public static final class BuilderImpl implements CodeBlock.Builder
    {
            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  The arguments.
         */
        private final Collection<Object> m_Args = new ArrayList<>();

        /**
         *  The reference to the factory.
         */
        @SuppressWarnings( "InstanceVariableOfConcreteClass" )
        private final JavaComposer m_Composer;

        /**
         *  The format Strings.
         */
        private final List<String> m_FormatParts = new ArrayList<>();

        /**
         *  The static imports.
         */
        private final Collection<String> m_StaticImports = new TreeSet<>();

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code BuilderImpl} instance.
         *
         *  @param  composer    The reference to the factory that created this
         *      builder instance.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        public BuilderImpl( final JavaComposer composer )
        {
            m_Composer = requireNonNullArgument( composer, "composer" );
        }   //  BuilderImpl()

        /**
         *  Creates a new {@code BuilderImpl} instance.
         *
         *  @param  composer    The reference to the factory that created this
         *      builder instance.
         *  @param  formatParts The format parts.
         *  @param  args    The arguments.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        public BuilderImpl( final JavaComposer composer, final List<String> formatParts, final List<Object> args )
        {
            this( composer );
            m_FormatParts.addAll( requireNonNullArgument( formatParts, "formatParts" ) );
            m_Args.addAll( requireNonNullArgument( args, "args" ) );
        }   //  BuilderImpl()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        @Override
        public final BuilderImpl add( final CodeBlock codeBlock )
        {
            addDebug();
            final var retValue = addWithoutDebugInfo( codeBlock );

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  add()

        /**
         *  Adds the given debug output.
         *
         *  @param  debugOutput The debug output.
         *  @return This {@code Builder} instance.
         *
         *  @deprecated  Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @SuppressWarnings( {"PublicMethodNotExposedInInterface", "OptionalUsedAsFieldOrParameterType", "UseOfConcreteClass"} )
        @Deprecated( since = "0.2.0", forRemoval = true )
        public final BuilderImpl add( final Optional<DebugOutput> debugOutput )
        {
            requireNonNullArgument( debugOutput, "debugOutput" ).ifPresent( v -> m_FormatParts.add( v.asComment() ) );

            //---* Done *----------------------------------------------------------
            return this;
        }   //  add()

        /**
         *  {@inheritDoc}
         *
         *  @deprecated  Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @SuppressWarnings( {"removal", "UseOfConcreteClass"} )
        @Override
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public final BuilderImpl add( final boolean addDebugOutput, final String format, final Object... args )
        {
            return add( createDebugOutput( addDebugOutput, true ), format, args );
        }   //  add()

        /**
         *  <p>{@summary Adds code with positional or relative arguments.}</p>
         *  <p>Relative arguments map 1:1 with the placeholders in the format
         *  string.</p>
         *  <p>Positional arguments use an index after the placeholder to
         *  identify which argument index to use. For example, for a literal to
         *  reference the 3<sup>rd</sup> argument, use {@code "$3L"} (1 based
         *  index).</p>
         *  <p>Mixing relative and positional arguments in a call to add is
         *  invalid and will result in an error.</p>
         *
         *  @param  debugOutput The debug output that is added to the generated
         *      code.
         *  @param  format  The format; may be empty.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.0.6
         *
         *  @deprecated We do no longer expose the debug output generation.
         */
        @Deprecated( since = "0.2.0", forRemoval = true )
        @SuppressWarnings( {"PublicMethodNotExposedInInterface", "OptionalUsedAsFieldOrParameterType", "UseOfConcreteClass"} )
        @API( status = DEPRECATED, since = "0.0.6" )
        public final BuilderImpl add( final Optional<DebugOutput> debugOutput, final String format, final Object... args )
        {
            add( debugOutput );

            var hasRelative = false;
            var hasIndexed = false;
            var relativeParameterCount = 0;

            final var length = requireNonNullArgument( format, "format" ).length();
            final var indexedParameterCount = new int [requireNonNullArgument( args, "args" ).length];

            ParseLoop:
            //noinspection ForLoopWithMissingComponent
            for( var pos = 0; pos < length; /* Update is inside the loop body */ )
            {
                if( format.charAt( pos ) != '$' )
                {
                    var nextPos = format.indexOf( '$', pos + 1 );
                    if( nextPos == -1 ) nextPos = format.length();
                    m_FormatParts.add( format.substring( pos, nextPos ) );
                    pos = nextPos;
                    continue ParseLoop;
                }

                //---* The update for the for-loop … *-------------------------
                ++pos ; // '$'.

                /*
                 * Consume zero or more digits, leaving 'c' as the first
                 * non-digit char after the '$'.
                 */
                final var indexStart = pos;
                char c;
                do
                {
                    checkState( pos < format.length(), () -> new ValidationException( format( "dangling format characters in '%s'", format ) ) );
                    c = format.charAt( pos++ );
                }
                while( c >= '0' && c <= '9' );
                final var indexEnd = pos - 1;

                //---* If 'c' doesn't take an argument, we're done *-----------
                if( isNoArgPlaceholder( c ) )
                {
                    checkState( indexStart == indexEnd, () -> new ValidationException( format( "$$, $>, $<, $[, $], $W, and $Z may not have an index" ) ) );
                    m_FormatParts.add( "$" + c );
                    continue ParseLoop;
                }

                /*
                 * Find either the indexed argument, or the relative argument
                 * (0-based).
                 */
                final int index;
                if( indexStart < indexEnd )
                {
                    index = Integer.parseInt( format.substring( indexStart, indexEnd ) ) - 1;
                    hasIndexed = true;
                    if( args.length > 0 )
                    {
                        //---* modulo is needed, checked below anyway *--------
                        ++indexedParameterCount [index % args.length];
                    }
                }
                else
                {
                    index = relativeParameterCount++;
                    hasRelative = true;
                }

                checkState( index >= 0 && index < args.length, () -> new ValidationException( format( "index %d for '%s' not in range (received %s arguments)", index + 1, format.substring( indexStart - 1, indexEnd + 1 ), args.length ) ) );
                checkState( !hasIndexed || !hasRelative, () -> new ValidationException( format( "cannot mix indexed and positional parameters" ) ) );

                addArgument( format, c, args [index] );

                m_FormatParts.add( "$" + c );
            }   //  ParseLoop:

            if( hasRelative && (relativeParameterCount < args.length) )
            {
                throw new ValidationException( format( "unused arguments: expected %s, received %s", relativeParameterCount, args.length ) );
            }
            if( hasIndexed )
            {
                final Collection<String> unused = IntStream.range( 0, args.length )
                    .filter( i -> indexedParameterCount[i] == 0 )
                    .mapToObj( i -> "$" + (i + 1) )
                    .collect( toList() );
                final var s = unused.size() == 1 ? "" : "s";
                if( !unused.isEmpty() )
                {
                    throw new ValidationException( format( "unused argument%s: %s", s, String.join( ", ", unused ) ) );
                }
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  add()

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        @API( status = INTERNAL, since = "0.2.0" )
        @Override
        public final BuilderImpl add( final String format, final Object... args )
        {
            addDebug();

            final var retValue = addWithoutDebugInfo( format, args );

            //---* Done *----------------------------------------------------------
            return retValue;
        }   //  add()

        /**
         *  Adds the placeholder's argument.
         *
         *  @param  format  The format.
         *  @param  placeholder The placeholder character.
         *  @param  arg     The argument.
         */
        private final void addArgument( final String format, final char placeholder, final Object arg )
        {
            final var argument = switch( placeholder )
            {
                case 'N' -> argToName( arg );
                case 'L' -> argToLiteral( arg );
                case 'S' -> argToString( arg );
                case 'T' -> argToType( arg );
                default -> throw new IllegalArgumentException( String.format( "invalid format string: '%s'", format ) );
            };
            m_Args.add( argument );
        }   //  addArgument()

        /**
         *  Adds debug output.
         */
        private final void addDebug()
        {
            createDebugOutput( m_Composer.addDebugOutput() )
                .ifPresent( v -> m_FormatParts.add( v.asComment() ) );
        }   //  addDebug()

        /**
         *  {@inheritDoc}
         *
         *  @deprecated  Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @SuppressWarnings( {"removal", "UseOfConcreteClass"} )
        @Override
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public final BuilderImpl addNamed( final boolean addDebugOutput, final String format, final Map<String,?> args )
        {
            return addNamed( createDebugOutput( addDebugOutput, true ), format, args );
        }   //  addNamed()

        /**
         *  <p>{@summary Adds code using named arguments.}</p>
         *  <p>Named arguments specify their name after the '$' followed by a
         *  colon {@code ":"} and the corresponding type character. Argument
         *  names consist of characters in {@code a-z, A-Z, 0-9, and _} and
         *  must start with a lowercase character.</p>
         *  <p>For example, to refer to the type
         *  {@link java.lang.Integer}
         *  with the argument name {@code clazz} use a format string containing
         *  {@code $clazz:T} and include the key {@code clazz} with value
         *  {@code java.lang.Integer.class} in the argument map.</p>
         *
         *  @param  debugOutput The debug output that is added to the generated
         *      code.
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.0.6
         *
         *  @deprecated We do no longer expose the debug output generation.
         */
        @Deprecated( since = "0.2.0", forRemoval = true )
        @SuppressWarnings( {"PublicMethodNotExposedInInterface", "OptionalUsedAsFieldOrParameterType", "UseOfConcreteClass"} )
        @API( status = DEPRECATED, since = "0.0.6" )
        public final BuilderImpl addNamed( final Optional<DebugOutput> debugOutput, final String format, final Map<String,?> args )
        {
            add( debugOutput );

            for( final var argument : requireNonNullArgument( args, "args" ).keySet() )
            {
                checkState( LOWERCASE.matcher( argument ).matches(), () -> new ValidationException( format( "argument '%s' must start with a lowercase character", argument ) ) );
            }
            if( isNotEmpty( requireNonNullArgument( format, "format" ) ) )
            {
                var p = 0;
                ParseLoop: while( p < format.length() )
                {
                    final var nextP = format.indexOf( "$", p );
                    if( nextP == -1 )
                    {
                        m_FormatParts.add( format.substring( p ) );
                        break ParseLoop;
                    }

                    if( p != nextP )
                    {
                        m_FormatParts.add( format.substring( p, nextP ) );
                        p = nextP;
                    }

                    Matcher matcher = null;
                    final var colon = format.indexOf( ':', p );
                    if( colon != -1 )
                    {
                        final var endIndex = min( colon + 2, format.length() );
                        matcher = NAMED_ARGUMENT.matcher( format.substring( p, endIndex ) );
                    }
                    if( nonNull( matcher ) && matcher.lookingAt() )
                    {
                        final var argumentName = matcher.group( "argumentName" );
                        checkState( args.containsKey( argumentName ), () -> new ValidationException( format( "Missing named argument for $%s", argumentName ) ) );
                        final var formatChar = matcher.group( "typeChar" ).charAt( 0 );
                        addArgument( format, formatChar, args.get( argumentName ) );
                        m_FormatParts.add( "$" + formatChar );
                        p += matcher.regionEnd();
                    }
                    else
                    {
                        checkState( p < format.length() - 1, () -> new ValidationException( format( "dangling $ at end" ) ) );
                        if( !isNoArgPlaceholder( format.charAt( p + 1 ) ) )
                        {
                            throw new ValidationException( format( "unknown format $%s at %s in '%s'", format.charAt( p + 1 ), p + 1, format ) );
                        }
                        m_FormatParts.add( format.substring( p, p + 2 ) );
                        p += 2;
                    }
                }   //  ParseLoop:
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addNamed()

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        @API( status = INTERNAL, since = "0.2.0" )
        @Override
        public final BuilderImpl addNamed( final String format, final Map<String,?> args )
        {
            addDebug();

            for( final var argument : requireNonNullArgument( args, "args" ).keySet() )
            {
                checkState( LOWERCASE.matcher( argument ).matches(), () -> new ValidationException( format( "argument '%s' must start with a lowercase character", argument ) ) );
            }
            if( isNotEmpty( requireNonNullArgument( format, "format" ) ) )
            {
                var p = 0;
                ParseLoop: while( p < format.length() )
                {
                    final var nextP = format.indexOf( "$", p );
                    if( nextP == -1 )
                    {
                        m_FormatParts.add( format.substring( p ) );
                        break ParseLoop;
                    }

                    if( p != nextP )
                    {
                        m_FormatParts.add( format.substring( p, nextP ) );
                        p = nextP;
                    }

                    Matcher matcher = null;
                    final var colon = format.indexOf( ':', p );
                    if( colon != -1 )
                    {
                        final var endIndex = min( colon + 2, format.length() );
                        matcher = NAMED_ARGUMENT.matcher( format.substring( p, endIndex ) );
                    }
                    if( nonNull( matcher ) && matcher.lookingAt() )
                    {
                        final var argumentName = matcher.group( "argumentName" );
                        checkState( args.containsKey( argumentName ), () -> new ValidationException( format( "Missing named argument for $%s", argumentName ) ) );
                        final var formatChar = matcher.group( "typeChar" ).charAt( 0 );
                        addArgument( format, formatChar, args.get( argumentName ) );
                        m_FormatParts.add( "$" + formatChar );
                        p += matcher.regionEnd();
                    }
                    else
                    {
                        checkState( p < format.length() - 1, () -> new ValidationException( format( "dangling $ at end" ) ) );
                        if( !isNoArgPlaceholder( format.charAt( p + 1 ) ) )
                        {
                            throw new ValidationException( format( "unknown format $%s at %s in '%s'", format.charAt( p + 1 ), p + 1, format ) );
                        }
                        m_FormatParts.add( format.substring( p, p + 2 ) );
                        p += 2;
                    }
                }   //  ParseLoop:
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addNamed()

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "CastToConcreteClass" )
        @Override
        public final BuilderImpl addStatement( final CodeBlock codeBlock )
        {
            addStatement( NO_DEBUG_OUTPUT, "$L", requireNonNullArgument( codeBlock, "codeBlock" ) );
            m_StaticImports.addAll( ((CodeBlockImpl) codeBlock).getStaticImports() );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addStatement()

        /**
         *  {@inheritDoc}
         *
         *  @deprecated  Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @SuppressWarnings( {"removal", "UseOfConcreteClass"} )
        @Override
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public final BuilderImpl addStatement( final boolean addDebugOutput, final String format, final Object... args )
        {
            return addStatement( createDebugOutput( addDebugOutput, true ), format, args );
        }   //  addStatement()

        /**
         *  <p>{@summary Adds a statement.}</p>
         *  <p>Do not use this method when the resulting code should be used
         *  as a field initializer. Use
         *  {@link #add(String, Object...)}
         *  instead.</p>
         *
         *  @param  debugOutput The debug output that is added to the generated
         *      code.
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @see org.tquadrat.foundation.javacomposer.FieldSpec.Builder#initializer(CodeBlock)
         *
         *  @since 0.0.6
         *
         *  @deprecated We do no longer expose the debug output generation.
         */
        @Deprecated( since ="0.2.0", forRemoval = true )
        @SuppressWarnings( {"PublicMethodNotExposedInInterface", "OptionalUsedAsFieldOrParameterType", "UseOfConcreteClass"} )
        @API( status = DEPRECATED, since = "0.0.6" )
        public final BuilderImpl addStatement( final Optional<DebugOutput> debugOutput, final String format, final Object... args )
        {
            return add( debugOutput, "$[" ).add( NO_DEBUG_OUTPUT, format, args ).add( NO_DEBUG_OUTPUT, ";\n$]" );
        }   //  addStatement()

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( {"PublicMethodNotExposedInInterface", "OptionalUsedAsFieldOrParameterType", "UseOfConcreteClass"} )
        @API( status = STABLE, since = "0.2.0" )
        @Override
        public final BuilderImpl addStatement( final String format, final Object... args )
        {
            final var retValue = add( "$[" )
                .addWithoutDebugInfo( format, args )
                .addWithoutDebugInfo( ";\n$]" );

            //---* Done *----------------------------------------------------------
            return retValue;
        }   //  addStatement()

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        @API( status = STABLE, since = "0.2.0" )
        @Override
        public final BuilderImpl addStaticImport( final Class<?> clazz, final String... names )
        {
            return addStaticImport( ClassNameImpl.from( clazz ), names );
        }   //  addStaticImport()

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        @API( status = STABLE, since = "0.2.0" )
        @Override
        public final BuilderImpl addStaticImport( final ClassName className, final String... names )
        {
            final var canonicalName = requireNonNullArgument( className, "className" ).canonicalName();
            for( final var name : requireValidNonNullArgument( names, "names", v -> v.length > 0, n -> format( "%s array is empty", n ) ) )
            {
                m_StaticImports.add(
                    format(
                        "%s.%s",
                        canonicalName,
                        requireValidArgument(
                            name,
                            "name",
                            Objects::nonNull,
                            $ -> format( "null entry in names array: %s", Arrays.toString( names ) )
                        )
                    )
                );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addStaticImport()

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        @API( status = STABLE, since = "0.2.0" )
        @Override
        public final BuilderImpl addStaticImport( final Enum<?> constant )
        {
            return addStaticImport( ClassNameImpl.from( requireNonNullArgument( constant, "constant" ).getDeclaringClass() ), constant.name() );
        }   //  addStaticImport()

        /**
         *  Adds a
         *  {@link CodeBlock}
         *  instance without prepending any debug output.
         *
         *  @param  codeBlock   The code block.
         *  @return This {@code Builder} instance.
         */
        @SuppressWarnings( {"PublicMethodNotExposedInInterface", "CastToConcreteClass"} )
        public final BuilderImpl addWithoutDebugInfo( final CodeBlock codeBlock )
        {
            final var builder = (BuilderImpl) requireNonNullArgument( codeBlock, "codeBlock" )
                .toBuilder();
            m_FormatParts.addAll( builder.formatParts() );
            m_Args.addAll( builder.args() );
            m_StaticImports.addAll( ((CodeBlockImpl) codeBlock).getStaticImports() );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addWithoutDebugInfo()

        /**
         *  <p>{@summary Adds code with positional or relative arguments,
         *  without prepending any debug output.}</p>
         *  <p>Relative arguments map 1:1 with the placeholders in the format
         *  string.</p>
         *  <p>Positional arguments use an index after the placeholder to
         *  identify which argument index to use. For example, for a literal to
         *  reference the 3<sup>rd</sup> argument, use {@code "$3L"} (1 based
         *  index).</p>
         *  <p>Mixing relative and positional arguments in a call to add is
         *  illegal and will result in an error.</p>
         *
         *  @param  format  The format; may be empty.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        @SuppressWarnings( {"PublicMethodNotExposedInInterface", "UseOfConcreteClass"} )
        @API( status = INTERNAL, since = "0.2.0" )
        public final BuilderImpl addWithoutDebugInfo( final String format, final Object... args )
        {
            var hasRelative = false;
            var hasIndexed = false;
            var relativeParameterCount = 0;

            final var length = requireNonNullArgument( format, "format" ).length();
            final var indexedParameterCount = new int [requireNonNullArgument( args, "args" ).length];

            ParseLoop:
            //noinspection ForLoopWithMissingComponent
            for( var pos = 0; pos < length; /* Update is inside the loop body */ )
            {
                if( format.charAt( pos ) != '$' )
                {
                    var nextPos = format.indexOf( '$', pos + 1 );
                    if( nextPos == -1 ) nextPos = format.length();
                    m_FormatParts.add( format.substring( pos, nextPos ) );
                    pos = nextPos;
                    continue ParseLoop;
                }

                //---* The update for the for-loop … *-------------------------
                ++pos ; // '$'.

                /*
                 * Consume zero or more digits, leaving 'c' as the first
                 * non-digit char after the '$'.
                 */
                final var indexStart = pos;
                char c;
                do
                {
                    checkState( pos < format.length(), () -> new ValidationException( format( "dangling format characters in '%s'", format ) ) );
                    c = format.charAt( pos++ );
                }
                while( c >= '0' && c <= '9' );
                final var indexEnd = pos - 1;

                //---* If 'c' doesn't take an argument, we're done *-----------
                if( isNoArgPlaceholder( c ) )
                {
                    checkState( indexStart == indexEnd, () -> new ValidationException( format( "$$, $>, $<, $[, $], $W, and $Z may not have an index" ) ) );
                    m_FormatParts.add( "$" + c );
                    continue ParseLoop;
                }

                /*
                 * Find either the indexed argument, or the relative argument
                 * (0-based).
                 */
                final int index;
                if( indexStart < indexEnd )
                {
                    index = Integer.parseInt( format.substring( indexStart, indexEnd ) ) - 1;
                    hasIndexed = true;
                    if( args.length > 0 )
                    {
                        //---* modulo is needed, checked below anyway *--------
                        ++indexedParameterCount [index % args.length];
                    }
                }
                else
                {
                    index = relativeParameterCount++;
                    hasRelative = true;
                }

                checkState( index >= 0 && index < args.length, () -> new ValidationException( format( "index %d for '%s' not in range (received %s arguments)", index + 1, format.substring( indexStart - 1, indexEnd + 1 ), args.length ) ) );
                checkState( !hasIndexed || !hasRelative, () -> new ValidationException( format( "cannot mix indexed and positional parameters" ) ) );

                addArgument( format, c, args [index] );

                m_FormatParts.add( "$" + c );
            }   //  ParseLoop:

            if( hasRelative && (relativeParameterCount < args.length) )
            {
                throw new ValidationException( format( "unused arguments: expected %s, received %s", relativeParameterCount, args.length ) );
            }
            if( hasIndexed )
            {
                final Collection<String> unused = IntStream.range( 0, args.length )
                    .filter( i -> indexedParameterCount[i] == 0 )
                    .mapToObj( i -> "$" + (i + 1) )
                    .collect( toList() );
                final var s = unused.size() == 1 ? "" : "s";
                if( !unused.isEmpty() )
                {
                    throw new ValidationException( format( "unused argument%s: %s", s, String.join( ", ", unused ) ) );
                }
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addWithoutDebugInfo()

        /**
         *  Returns the arguments.
         *
         *  @return The arguments.
         */
        @SuppressWarnings( "PublicMethodNotExposedInInterface" )
        public final List<Object> args() { return List.copyOf( m_Args ); }

        /**
         *  Returns the given object literally.
         *
         *  @param  o   The object.
         *  @return The literal.
         */
        private static final Object argToLiteral( final Object o )
        {
            final var retValue = nonNull( o ) ? o : NULL_REFERENCE;

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  argToLiteral()

        /**
         *  Translates the given object to a name.
         *
         *  @param  o   The object.
         *  @return The name.
         */
        @SuppressWarnings( {"IfStatementWithTooManyBranches", "ChainOfInstanceofChecks"} )
        private static final Object argToName( final Object o )
        {
            final var retValue = switch( o )
                {
                    case CharSequence charSequence -> charSequence.toString();
                    case ParameterSpec parameterSpec -> parameterSpec.name();
                    case FieldSpec fieldSpec -> fieldSpec.name();
                    case MethodSpec methodSpec -> methodSpec.name();
                    case TypeSpec typeSpec ->
                        /*
                         * Does not work for anonymous types, so no check for the name
                         * is required.
                         */
                        //noinspection OptionalGetWithoutIsPresent
                        typeSpec.name().get();
                    case null, default -> throw new IllegalArgumentException( "expected name but was " + o );
                };

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  argToName()

        /**
         *  Translates the given object to a String.
         *
         *  @param  o   The object.
         *  @return The resulting String, or
         *      {@link Util#NULL_REFERENCE}
         *      if the object is
         *      {@code null}.
         */
        private static final Object argToString( final Object o )
        {
            final var retValue = isNull( o ) ? NULL_REFERENCE : Objects.toString( o );

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  argToString()

        /**
         *  Translates the given object to a type.
         *
         *  @param  o   The object.
         *  @return The resulting type.
         */
        @SuppressWarnings( {"IfStatementWithTooManyBranches", "ChainOfInstanceofChecks", "InstanceofConcreteClass"} )
        private static final TypeNameImpl argToType( final Object o )
        {
            final var retValue = switch( o )
                {
                    case TypeNameImpl typeName -> typeName;
                    case TypeMirror typeMirror -> TypeNameImpl.from( typeMirror );
                    case Element element -> TypeNameImpl.from( element.asType() );
                    case Type type -> TypeNameImpl.from( type );
                    case null, default -> throw new IllegalArgumentException( "expected type but was " + o );
                };

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  argToType()

        /**
         *  {@inheritDoc}
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @SuppressWarnings( {"DeprecatedIsStillUsed", "removal", "UseOfConcreteClass"} )
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        @Override
        public final BuilderImpl beginControlFlow( final boolean addDebugOutput, final String controlFlow, final Object... args )
        {
            return beginControlFlow( createDebugOutput( addDebugOutput, true ), controlFlow, args );
        }   //  beginControlFlow()

        /**
         *  Starts a control flow construct.
         *
         *  @param  debugOutput The debug output that is added to the generated
         *      code.
         *  @param  controlFlow The control flow construct and its code, such
         *      as {@code if (foo == 5)}.<br>
         *      <br>Shouldn't contain braces or newline characters.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.0.6
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        @SuppressWarnings( {"PublicMethodNotExposedInInterface", "OptionalUsedAsFieldOrParameterType", "DeprecatedIsStillUsed", "UseOfConcreteClass"} )
        public final BuilderImpl beginControlFlow( final Optional<DebugOutput> debugOutput, final String controlFlow, final Object... args )
        {
            if( isNotEmptyOrBlank( requireNonNullArgument( controlFlow, "controlFlow" ) ) )
            {
                add( debugOutput, controlFlow, args );
                if( !controlFlow.endsWith( "\n" ) ) add( " " );
                add( "{\n" );
            }
            else
            {
                add( debugOutput, "{\n" );
            }
            indent();

            //---* Done *------------------------------------------------------
            return this;
        }   //  beginControlFlow()

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        @API( status = INTERNAL, since = "0.0.5" )
        @Override
        public final BuilderImpl beginControlFlow( final String controlFlow, final Object... args )
        {
            addDebug();
            if( isNotEmptyOrBlank( requireNonNullArgument( controlFlow, "controlFlow" ) ) )
            {
                addWithoutDebugInfo( controlFlow, args );
                if( !controlFlow.endsWith( "\n" ) ) addWithoutDebugInfo( " " );
            }
            addWithoutDebugInfo( "{\n" );
            indent();

            //---* Done *------------------------------------------------------
            return this;
        }   //  beginControlFlow()

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        @Override
        public final CodeBlockImpl build() { return new CodeBlockImpl( this ); }

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        @Override
        public final BuilderImpl endControlFlow()
        {
            addDebug();
            unindent();
            addWithoutDebugInfo( "}\n" );

            //---* Done *------------------------------------------------------
            return this;
        }   //  endControlFlow()

        /**
         *  {@inheritDoc}
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @SuppressWarnings( {"DeprecatedIsStillUsed", "removal", "UseOfConcreteClass"} )
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public final BuilderImpl endControlFlow( final boolean addDebugOutput, final String controlFlow, final Object... args )
        {
            return endControlFlow( createDebugOutput( addDebugOutput, true ), controlFlow, args );
        }   //  endControlFlow()

        /**
         *  <p>{@summary Ends a control flow construct that was previously
         *  started with a call to
         *  {@link #beginControlFlow(String, Object...)}
         *  or
         *  {@link #beginControlFlow(boolean, String, Object...)}.}</p>
         *  <p>This form is only used for {@code do/while} control flows.</p>
         *
         *  @param  debugOutput The debug output that is added to the generated
         *      code.
         *  @param controlFlow  The optional control flow construct and its
         *      code, such as {@code while(foo == 20)}.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.0.6
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @SuppressWarnings( {"PublicMethodNotExposedInInterface", "DeprecatedIsStillUsed", "OptionalUsedAsFieldOrParameterType", "UseOfConcreteClass"} )
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public final BuilderImpl endControlFlow( final Optional<DebugOutput> debugOutput, final String controlFlow, final Object... args )
        {
            unindent();
            add( debugOutput, "} " + requireNonNullArgument( controlFlow, "controlFlow" ) + ";\n", args );

            //---* Done *------------------------------------------------------
            return this;
        }   //  endControlFlow()

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        @API( status = INTERNAL, since = "0.0.5" )
        @Override
        public final BuilderImpl endControlFlow( final String controlFlow, final Object... args )
        {
            addDebug();
            unindent();
            addWithoutDebugInfo( "} " + requireNonNullArgument( controlFlow, "controlFlow" ) + ";\n", args );

            //---* Done *------------------------------------------------------
            return this;
        }   //  endControlFlow()

        /**
         *  Returns the format parts.
         *
         *  @return The format parts.
         */
        @SuppressWarnings( "PublicMethodNotExposedInInterface" )
        public final List<String> formatParts() { return List.copyOf( m_FormatParts ); }

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        @Override
        public final BuilderImpl indent()
        {
            m_FormatParts.add( "$>" );

            //---* Done *------------------------------------------------------
            return this;
        }   //  indent()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final boolean isEmpty() { return m_FormatParts.isEmpty(); }

        /**
         *  Checks whether the given placeholder character would expect an
         *  argument.
         *
         *  @param  placeholder The placeholder character.
         *  @return {@code true} if there is no argument expected,
         *      {@code false} otherwise.
         */
        private static final boolean isNoArgPlaceholder( final char placeholder )
        {
            final var retValue = IntStream.of( '$', '>', '<', '[', ']', 'W', 'Z' )
                .anyMatch( p -> p == placeholder );

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  isNoArgPlaceholder()

        /**
         *  {@inheritDoc}
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @SuppressWarnings( {"DeprecatedIsStillUsed", "removal", "UseOfConcreteClass"} )
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        @Override
        public final BuilderImpl nextControlFlow( final boolean addDebugOutput, final String controlFlow, final Object... args )
        {
            return nextControlFlow( createDebugOutput( addDebugOutput, true ), controlFlow, args );
        }   //  nextControlFlow()

        /**
         *  Adds another control flow construct to an already existing one.
         *
         *  @param  debugOutput The debug output that is added to the generated
         *      code.
         *  @param controlFlow  The control flow construct and its code, such
         *      as {@code else if (foo == 10)}.<br>
         *      <br>Shouldn't contain braces or newline characters.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.0.6
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @SuppressWarnings( {"PublicMethodNotExposedInInterface", "DeprecatedIsStillUsed", "OptionalUsedAsFieldOrParameterType", "UseOfConcreteClass"} )
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public final BuilderImpl nextControlFlow( final Optional<DebugOutput> debugOutput, final String controlFlow, final Object... args )
        {
            unindent();
            add( "}" );
            if( !requireNonNullArgument( controlFlow, "controlFlow" ).startsWith( "\n" ) ) add( " " );
            add( debugOutput, controlFlow, args );
            if( !controlFlow.endsWith( "\n" ) ) add(" " );
            add( "{\n" );
            indent();

            //---* Done *------------------------------------------------------
            return this;
        }   //  nextControlFlow()

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        @API( status = INTERNAL, since = "0.0.5" )
        @Override
        public final BuilderImpl nextControlFlow( final String controlFlow, final Object... args )
        {
            unindent();
            addWithoutDebugInfo( "}" );
            if( !requireNonNullArgument( controlFlow, "controlFlow" ).startsWith( "\n" ) ) addWithoutDebugInfo( " " );
            add( controlFlow, args );
            if( !controlFlow.endsWith( "\n" ) ) addWithoutDebugInfo(" " );
            addWithoutDebugInfo( "{\n" );
            indent();

            //---* Done *------------------------------------------------------
            return this;
        }   //  nextControlFlow()

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        @Override
        public final BuilderImpl unindent()
        {
            m_FormatParts.add( "$<" );

            //---* Done *------------------------------------------------------
            return this;
        }   //  unindent()
    }
    //  class BuilderImpl

    /**
     *  A helper class that supports to join code blocks.
     *
     *  @author Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: CodeBlockImpl.java 943 2021-12-21 01:34:32Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: CodeBlockImpl.java 943 2021-12-21 01:34:32Z tquadrat $" )
    @API( status = INTERNAL, since = "0.0.5" )
    private static final class CodeBlockJoiner
    {
            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  The builder that is used to deliver the final code block.
         */
        @SuppressWarnings( "InstanceVariableOfConcreteClass" )
        private final BuilderImpl m_Builder;

        /**
         *  The separator for the joined code blocks.
         */
        private final String m_Delimiter;

        /**
         *  Flag that indicates whether to add the delimiter on adding a new
         *  code block.
         */
        private boolean m_First = true;

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code CodeBlockJoiner} instance.
         *
         *  @param  delimiter   The separator for the joined code blocks.
         *  @param  builder The builder that is used to deliver the final code
         *      block.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        public CodeBlockJoiner( final String delimiter, final BuilderImpl builder )
        {
            m_Delimiter = requireNonNullArgument( delimiter, "delimiter" );
            m_Builder = requireNonNullArgument( builder, "builder" );
        }   //  CodeBlockJoiner()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  Adds another code block.
         *
         *  @param  codeBlock   The new code block.
         *  @return This {@code CodeBlockJoiner} instance.
         */
        @SuppressWarnings( {"TypeMayBeWeakened", "UseOfConcreteClass"} )
        public final CodeBlockJoiner add( final CodeBlockImpl codeBlock )
        {
            if( !m_First ) m_Builder.addWithoutDebugInfo( m_Delimiter );
            m_First = false;

            m_Builder.add( codeBlock );

            //---* Done *------------------------------------------------------
            return this;
        }   //  add()

        /**
         *  Returns the new code block with the joined ones.
         *
         *  @return The new code block.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        public final CodeBlockImpl join() { return m_Builder.build(); }

        /**
         *  Merges this code block joiner with the given other one.
         *
         *  @param  other   The other code block joiner.
         *  @return This {@code CodeBlockJoiner} instance.
         */
        @SuppressWarnings( {"AccessingNonPublicFieldOfAnotherObject", "UseOfConcreteClass"} )
        public final CodeBlockJoiner merge( final CodeBlockJoiner other )
        {
            final var otherBlock = requireNonNullArgument( other, "other" ).m_Builder.build();
            if( !otherBlock.isEmpty() ) add( otherBlock );

            //---* Done *------------------------------------------------------
            return this;
        }   //  merge()
    }
    //  class CodeBlockJoiner

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The arguments.
     */
    private final List<Object> m_Args;

    /**
     *  Lazily initialised return value of
     *  {@link #toString()}
     *  for this code block.
     */
    private final Lazy<String> m_CachedString;

    /**
     *  The reference to the factory.
     */
    @SuppressWarnings( "InstanceVariableOfConcreteClass" )
    private final JavaComposer m_Composer;

    /**
     *  A heterogeneous list containing string literals and value placeholders.
     */
    private final List<String> m_FormatParts;

    /**
     *  The static imports.
     */
    private final Set<String> m_StaticImports;

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/
    /**
     *  The regular expression that is used to determine whether a parameter
     *  name starts with a lowercase character.
     */
    public static final Pattern LOWERCASE;

    /**
     *  The regular expression that is used to obtain the argument name from
     *  a format string.
     */
    public static final Pattern NAMED_ARGUMENT;

    static
    {
        try
        {
            LOWERCASE = Pattern.compile( "[a-z]+[\\w_]*" );
            NAMED_ARGUMENT = Pattern.compile( "\\$(?<argumentName>[\\w_]+):(?<typeChar>[\\w]).*" );
        }
        catch( final PatternSyntaxException e )
        {
            throw new ExceptionInInitializerError( e );
        }
    }

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code CodeBlockImpl} instance.
     *
     *  @param  builder The builder for this instance.
     */
    @SuppressWarnings( {"AccessingNonPublicFieldOfAnotherObject", "UseOfConcreteClass"} )
    public CodeBlockImpl( final BuilderImpl builder )
    {
        m_Composer = builder.m_Composer;
        m_FormatParts = builder.formatParts();
        m_Args = builder.args();
        m_StaticImports = Set.copyOf( builder.m_StaticImports );

        m_CachedString = Lazy.use( this::initialiseCachedString );
    }   //  CodeBlockImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Returns the arguments.
     *
     *  @return The arguments.
     */
    /*
     * Originally, this was the reference to the internal collection!
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final List<Object> args() { return List.copyOf( m_Args ); }

    /**
     *  Creates a builder for an instance of {@code CodeBlock}.
     *
     *  @return The new builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.6" )
    public static BuilderImpl builder() { return new BuilderImpl( new JavaComposer() ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = this == o;
        if( !retValue && (o instanceof CodeBlockImpl other) )
        {
            retValue = m_Composer.equals( other.m_Composer ) && toString().equals( o.toString() );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  equals()

    /**
     *  Returns the format parts from this code block.
     *
     *  @return The format parts.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final List<String> formatParts() { return List.copyOf( m_FormatParts ); }

    /**
     *  Returns the
     *  {@link JavaComposer}
     *  factory.
     *
     *  @return The reference to the factory.
     */
    @SuppressWarnings( {"PublicMethodNotExposedInInterface", "UseOfConcreteClass"} )
    public final JavaComposer getFactory() { return m_Composer; }

    /**
     *  Returns the static imports for this code block.
     *
     *  @return The static imports.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    @API( status = INTERNAL, since = "0.2.0" )
    public final Set<String> getStaticImports() { return m_StaticImports; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int hashCode() { return toString().hashCode(); }

    /**
     *  The initializer for
     *  {@link #m_CachedString}.
     *
     *  @return The return value for
     *      {@link #toString()}.
     */
    private final String initialiseCachedString()
    {
        final var resultBuilder = new StringBuilder();
        final var codeWriter = new CodeWriter( m_Composer, resultBuilder );
        try
        {
            codeWriter.emit( this );
        }
        catch( final UncheckedIOException e )
        {
            throw new UnexpectedExceptionError( e.getCause() );
        }
        final var retValue = resultBuilder.toString();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  initialiseCachedString()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean isEmpty() { return m_FormatParts.isEmpty(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final CodeBlock join( final String separator, final CodeBlock... codeBlocks )
    {
        final var retValue = makeCodeBlockStream( this, requireNonNullArgument( codeBlocks, "codeBlocks" ) )
            .collect( joining( separator ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  join()

    /**
     * <p>{@summary Joins this code block with the given code blocks into a
     * single new {@code CodeBlock} instance, each separated by the given
     * separator.} The given prefix will be prepended to the new
     * {@code CodeBloc}, and the given suffix will be appended to it.</p>
     * <p>For example, joining &quot;{@code String s}&quot;,
     * &quot;{@code Object o}&quot; and &quot;{@code int i}&quot; using
     * &quot;{@code , }&quot; as the separator would produce
     * &quot;{@code String s, Object o, int i}&quot;.</p>
     *
     * @param separator The separator.
     * @param prefix The prefix.
     * @param suffix The suffix.
     * @param codeBlocks The code blocks to join.
     * @return The new code block.
     */
    @Override
    public final CodeBlock join( final String separator, final String prefix, final String suffix, final CodeBlock... codeBlocks )
    {
        final var retValue = makeCodeBlockStream( this, requireNonNullArgument( codeBlocks, "codeBlocks" ) )
            .collect( joining( separator, prefix, suffix ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  join()

    /**
     *  <p>{@summary Joins the given code blocks into a single
     *  {@code CodeBlockImpl} instance, each separated the given
     *  separator.}</p>
     *  <p>For example, joining &quot;{@code String s}&quot;,
     *  &quot;{@code Object o}&quot; and &quot;{@code int i}&quot; using
     *  &quot;{@code , }&quot; as the separator would produce
     *  &quot;{@code String s, Object o, int i}&quot;.</p>
     *
     *  @param  codeBlocks  The code blocks to join.
     *  @param  separator   The separator.
     *  @return The new code block.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @SuppressWarnings( "CastToConcreteClass" )
    public static final CodeBlockImpl join( final Iterable<CodeBlock> codeBlocks, final String separator )
    {
        final var retValue = StreamSupport.stream( codeBlocks.spliterator(), false )
            .map( c -> (CodeBlockImpl) c )
            .collect( joiningStatic( separator ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  join()

    /**
     *  <p>{@summary Joins the given code blocks into a single
     *  {@code CodeBlock} instance, each separated the given separator. The
     *  given prefix will be prepended to the new {@code CodeBloc}, and the
     *  given suffix will be appended to it.}</p>
     *  <p>For example, joining &quot;{@code String s}&quot;,
     *  &quot;{@code Object o}&quot; and &quot;{@code int i}&quot; using
     *  &quot;{@code , }&quot; as the separator would produce
     *  &quot;{@code String s, Object o, int i}&quot;.</p>
     *
     *  @param  codeBlocks  The code blocks to join.
     *  @param  separator   The separator.
     *  @param  prefix  The prefix.
     *  @param  suffix  The suffix.
     *  @return The new code block.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @SuppressWarnings( "CastToConcreteClass" )
    public static CodeBlockImpl join( final Iterable<CodeBlock> codeBlocks, final String separator, final String prefix, final String suffix  )
    {
        final var retValue = StreamSupport.stream( codeBlocks.spliterator(), false )
            .map( c -> (CodeBlockImpl) c )
            .collect( joiningStatic( separator, prefix, suffix ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  join()

    /**
     *  <p>{@summary A
     *  {@link Collector}
     *  implementation that joins {@code CodeBlock} instances together into one
     *  new code block, separated by the given separator.}</p>
     *  <p>For example, joining &quot;{@code String s}&quot;,
     *  &quot;{@code Object o}&quot; and &quot;{@code int i}&quot; using
     *  &quot;{@code , }&quot; as the separator would produce
     *  &quot;{@code String s, Object o, int i}&quot;.</p>
     *
     *  @param  separator   The separator.
     *  @return The new collector.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final Collector<CodeBlockImpl,?,CodeBlockImpl> joining( final String separator )
    {
        final Collector<CodeBlockImpl,?,CodeBlockImpl> retValue = Collector.of
        (
            () -> new CodeBlockJoiner( separator, new BuilderImpl( m_Composer ) ), CodeBlockJoiner::add, CodeBlockJoiner::merge, CodeBlockJoiner::join
        );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  joining()

    /**
     *  <p>{@summary A
     *  {@link Collector}
     *  implementation that joins {@code CodeBlock} instances together into one
     *  new code block, separated by the given separator.} The given prefix
     *  will be prepended to the new {@code CodeBloc}, and the given suffix
     *  will be appended to it.</p>
     *  <p>For example, joining &quot;{@code String s}&quot;,
     *  &quot;{@code Object o}&quot; and &quot;{@code int i}&quot; using
     *  &quot;{@code , }&quot; as the separator, and
     *  &quot;{@code int func( }&quot; as the prefix and &quot; {@code )}&quot;
     *  as the suffix respectively would produce
     *  &quot;{@code int func( String s, Object o, int i )}&quot;.</p>
     *
     *  @param  separator   The separator.
     *  @param  prefix  The prefix.
     *  @param  suffix  The suffix.
     *  @return The new collector.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final Collector<CodeBlockImpl,?,CodeBlockImpl> joining( final String separator, final String prefix, final String suffix )
    {
        final var builder = new BuilderImpl( m_Composer );
        builder.add( "$N", prefix );
        final Collector<CodeBlockImpl,?,CodeBlockImpl> retValue = Collector.of
        (
            () -> new CodeBlockJoiner( separator, builder ), CodeBlockJoiner::add, CodeBlockJoiner::merge, joiner ->
            {
                builder.add( m_Composer.codeBlockOf( "$N", suffix ) );
                return joiner.join();
            }
        );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  joining()

    /**
     *  <p>{@summary A
     *  {@link Collector}
     *  implementation that joins {@code CodeBlock} instances together into one
     *  new code block, separated by the given separator.}</p>
     *  <p>For example, joining &quot;{@code String s}&quot;,
     *  &quot;{@code Object o}&quot; and &quot;{@code int i}&quot; using
     *  &quot;{@code , }&quot; as the separator would produce
     *  &quot;{@code String s, Object o, int i}&quot;.</p>
     *
     *  @param  separator   The separator.
     *  @return The new collector.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @SuppressWarnings( "DeprecatedIsStillUsed" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final Collector<CodeBlockImpl,?,CodeBlockImpl> joiningStatic( final String separator )
    {
        final Collector<CodeBlockImpl,?,CodeBlockImpl> retValue = Collector.of
            (
                () -> new CodeBlockJoiner( separator, new BuilderImpl( new JavaComposer() ) ), CodeBlockJoiner::add, CodeBlockJoiner::merge, CodeBlockJoiner::join
            );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  joiningStatic()

    /**
     *  <p>{@summary A
     *  {@link Collector}
     *  implementation that joins {@code CodeBlock} instances together into one
     *  new code block, separated by the given separator.} The given prefix
     *  will be prepended to the new {@code CodeBloc}, and the given suffix
     *  will be appended to it.</p>
     *  <p>For example, joining &quot;{@code String s}&quot;,
     *  &quot;{@code Object o}&quot; and &quot;{@code int i}&quot; using
     *  &quot;{@code , }&quot; as the separator, and
     *  &quot;{@code int func( }&quot; as the prefix and &quot; {@code )}&quot;
     *  as the suffix respectively would produce
     *  &quot;{@code int func( String s, Object o, int i )}&quot;.</p>
     *
     *  @param  separator   The separator.
     *  @param  prefix  The prefix.
     *  @param  suffix  The suffix.
     *  @return The new collector.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @SuppressWarnings( "DeprecatedIsStillUsed" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final Collector<CodeBlockImpl,?,CodeBlockImpl> joiningStatic( final String separator, final String prefix, final String suffix )
    {
        final var composer = new JavaComposer();
        final var builder = new BuilderImpl( composer );
        builder.add( "$N", prefix );
        final Collector<CodeBlockImpl,?,CodeBlockImpl> retValue = Collector.of
            (
                () -> new CodeBlockJoiner( separator, builder ), CodeBlockJoiner::add, CodeBlockJoiner::merge, joiner ->
                {
                    builder.add( composer.codeBlockOf( "$N", suffix ) );
                    return joiner.join();
                }
            );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  joiningStatic()

    /**
     *  Composes a stream from the given {@code CodeBlock} instances.
     *
     *  @param  head    The first code block.
     *  @param  tail    The other code blocks.
     *  @return The
     *      {@link Stream}
     *      instance with the {@code CodeBlock} instances.
     */
    @SuppressWarnings( "CastToConcreteClass" )
    private static final Stream<CodeBlockImpl> makeCodeBlockStream( final CodeBlock head, final CodeBlock... tail )
    {
        final var builder = Stream.<CodeBlockImpl>builder();
        builder.add( (CodeBlockImpl) requireNonNullArgument( head, "head" ) );
        for( final var block : requireNonNullArgument( tail, "tail" ) )
        {
            builder.add( (CodeBlockImpl) block );
        }
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  makeCodeBlockStream()

    /**
     *  Creates a new {@code CodeBlock} instance from the given format and
     *  arguments.
     *
     *  @param  format  The format.
     *  @param  args    The arguments.
     *  @return The new code block.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.6" )
    public static final CodeBlockImpl of( final String format, final Object... args )
    {
        final var retValue = of( NO_DEBUG_OUTPUT, format, args );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  of()

    /**
     *  Creates a new {@code CodeBlock} instance from the given format and
     *  arguments, and adds the given debug output if any.
     *
     *  @param  debugOutput The debug output.
     *  @param  format  The format.
     *  @param  args    The arguments.
     *  @return The new code block.
     *
     *  @since 0.0.6
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @SuppressWarnings( {"OptionalUsedAsFieldOrParameterType", "UseOfConcreteClass"} )
    @API( status = DEPRECATED, since = "0.0.6" )
    public static final CodeBlockImpl of( final Optional<DebugOutput> debugOutput, final String format, final Object... args )
    {
        final var retValue = builder().add( requireNonNullArgument( debugOutput, "debugOutput" ), format, args ).build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  of()

    /**
     *  Creates a new builder that is initialised with the components of this
     *  code block.
     *
     *  @return The new builder.
     */
    @SuppressWarnings( {"AccessingNonPublicFieldOfAnotherObject", "UseOfConcreteClass"} )
    @Override
    public final BuilderImpl toBuilder()
    {
        final var retValue = new BuilderImpl( m_Composer, m_FormatParts, m_Args );
        retValue.m_StaticImports.addAll( m_StaticImports );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toBuilder()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString() { return m_CachedString.get(); }
}
//  class CodeBlockImpl

/*
 *  End of File
 */