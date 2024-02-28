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

import static java.util.Collections.unmodifiableMap;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.javacomposer.internal.Util.characterLiteralWithoutSingleQuotes;
import static org.tquadrat.foundation.javacomposer.internal.Util.createDebugOutput;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.hash;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidArgument;

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.UnexpectedExceptionError;
import org.tquadrat.foundation.javacomposer.AnnotationSpec;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.lang.Lazy;
import org.tquadrat.foundation.util.JavaUtils;

/**
 *  The implementation of
 *  {@link AnnotationSpec}
 *  for a generated annotation on a declaration.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: AnnotationSpecImpl.java 1085 2024-01-05 16:23:28Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: AnnotationSpecImpl.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public final class AnnotationSpecImpl implements AnnotationSpec
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The implementation of
     *  {@link org.tquadrat.foundation.javacomposer.AnnotationSpec.Builder}
     *  for a builder of an
     *  {@link AnnotationSpecImpl}
     *  instance.
     *
     *  @author Square,Inc.
     *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: AnnotationSpecImpl.java 1085 2024-01-05 16:23:28Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: AnnotationSpecImpl.java 1085 2024-01-05 16:23:28Z tquadrat $" )
    @API( status = INTERNAL, since = "0.0.5" )
    public static final class BuilderImpl implements AnnotationSpec.Builder
    {
            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  The building blocks.
         */
        private final Map<String,List<CodeBlockImpl>> m_CodeBlocks = new LinkedHashMap<>();

        /**
         *  The reference to the factory.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private final JavaComposer m_Composer;

        /**
         *  A flag that indicates whether the inline representation is forced
         *  for this annotation.
         *
         *  @see org.tquadrat.foundation.javacomposer.AnnotationSpec.Builder#forceInline(boolean)
         */
        private boolean m_ForceInline = false;

        /**
         *  The name of the annotation type to build.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private final TypeNameImpl m_Type;

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code BuilderImpl} instance.
         *
         *  @param  composer    The reference to the factory that created this
         *      builder instance.
         *  @param  type    The name of the annotation type to build.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        public BuilderImpl( final JavaComposer composer, final TypeName type )
        {
            m_Composer = requireNonNullArgument( composer, "composer" );
            m_Type = (TypeNameImpl) requireNonNullArgument( type, "type" );
        }   //  BuilderImpl()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addMember( final CharSequence name, final String format, final Object... args )
        {
            final var codeBlock = ((CodeBlockImpl.BuilderImpl) m_Composer.codeBlockBuilder())
                .addWithoutDebugInfo( format, args )
                .build();
            final var retValue = addMember( name, codeBlock );

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  addMember()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addMember( final CharSequence name, final CodeBlock codeBlock )
        {
            final var validatedName = requireValidArgument( name, "name", JavaUtils::isValidName, $ -> "not a valid name: %s".formatted( name ) )
                .toString()
                .intern();
            requireNonNullArgument( codeBlock, "codeBlock" );
            final var values = m_CodeBlocks.computeIfAbsent( validatedName, $ -> new ArrayList<>() );
            values.add( createDebugOutput( m_Composer.addDebugOutput() )
                .map( output -> ((CodeBlockImpl.BuilderImpl) m_Composer.codeBlockBuilder())
                    .addWithoutDebugInfo( output.asComment() )
                    .addWithoutDebugInfo( codeBlock )
                    .build() )
                .orElse( (CodeBlockImpl) codeBlock ) );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addMember()

        /**
         *  Delegates to
         *  {@link #addMember(CharSequence,String,Object...)},
         *  with parameter {@code format} depending on the given {@code value}
         *  object. Falls back to {@code "$L"} literal format if the class of
         *  the given {@code value} object is not supported.
         *
         *  @param  name    The name for the new member.
         *  @param  value   The value for the new member.
         *  @return This {@code Builder} instance.
         */
        @SuppressWarnings( {"PublicMethodNotExposedInInterface", "UnusedReturnValue", "IfStatementWithTooManyBranches", "ChainOfInstanceofChecks"} )
        public final BuilderImpl addMemberForValue( final String name, final Object value )
        {
            requireValidArgument( name, "name", JavaUtils::isValidName, $ -> "not a valid name: %s".formatted( name ) );
            if( requireNonNullArgument( value, "value" ) instanceof Class<?> )
            {
                addMember( name, "$T.class", value );
            }
            else if( value instanceof final Enum<?> enumValue )
            {
                addMember( name, "$T.$L", value.getClass(), enumValue.name() );
            }
            else if( value instanceof String )
            {
                addMember( name, "$S", value );
            }
            else if( value instanceof Float )
            {
                addMember( name, "$Lf", value );
            }
            else if( value instanceof final Character charValue )
            {
                addMember( name, "'$L'", characterLiteralWithoutSingleQuotes( charValue.charValue() ) );
            }
            else
            {
                addMember( name, "$L", value );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addMemberForValue()

        /**
         *  Creates the {@code AnnotationSpec} instance from the added members.
         *
         *  @return The built instance.
         */
        @Override
        public final AnnotationSpecImpl build() { return new AnnotationSpecImpl( this ); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl forceInline( final boolean flag )
        {
            m_ForceInline = flag;

            //---* Done *------------------------------------------------------
            return this;
        }   //  forceInline()

        /**
         *  Returns the flag that indicates whether this annotation is
         *  presented inline or multiline.
         *
         *  @return {@code true} for the inline presentation, {@code false} for
         *      multi-line.
         */
        @SuppressWarnings( {"PublicMethodNotExposedInInterface", "BooleanMethodNameMustStartWithQuestion"} )
        public final boolean forceInline() { return m_ForceInline; }

        /**
         *  Returns the
         *  {@link JavaComposer}
         *  factory.
         *
         *  @return The reference to the factory.
         */
        @SuppressWarnings( {"PublicMethodNotExposedInInterface"} )
        public final JavaComposer getFactory() { return m_Composer; }

        /**
         *  Returns the members.
         *
         *  @return The members.
         */
        @SuppressWarnings( "PublicMethodNotExposedInInterface" )
        public final Map<String,List<CodeBlockImpl>> members()
        {
            final Map<String,List<CodeBlockImpl>> members = new LinkedHashMap<>();
            m_CodeBlocks.forEach( (key,value) -> members.put( key, List.copyOf( value ) ) );
            final var retValue = unmodifiableMap( members );

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  members()

        /**
         *  Returns the type of the annotation.
         *
         *  @return The type.
         */
        @SuppressWarnings( {"PublicMethodNotExposedInInterface"} )
        public final TypeNameImpl type() { return m_Type; }
    }
    //  class BuilderImpl

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  Lazily initialised return value of
     *  {@link #toString()}
     *  for this annotation.
     */
    private final Lazy<String> m_CachedString;

    /**
     *  The reference to the factory.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final JavaComposer m_Composer;

    /**
     *  A flag that indicates whether the inline representation is forced for
     *  this annotation.
     *
     *  @see org.tquadrat.foundation.javacomposer.AnnotationSpec.Builder#forceInline(boolean)
     */
    private final boolean m_ForceInline;

    /**
     *  The code blocks that define this annotation.
     */
    private final Map<String,List<CodeBlockImpl>> m_Members;

    /**
     *  The name of this annotation.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final TypeNameImpl m_Type;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code AnnotationSpecImpl} instance.
     *
     *  @param  builder The builder for this instance.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public AnnotationSpecImpl( final BuilderImpl builder )
    {
        m_Composer = builder.getFactory();
        m_Type = builder.type();
        m_Members = builder.members();
        m_ForceInline = builder.forceInline();

        m_CachedString = Lazy.use( this::initializeCachedString );
    }   //  AnnotationSpecImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Emits this annotation to the given code writer.
     *
     *  @param  codeWriter  The code writer.
     *  @param  inline  {@code true} if the annotation should be placed on the
     *      same line as the annotated element, {@code false} otherwise.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( {"PublicMethodNotExposedInInterface", "UseOfConcreteClass"} )
    public final void emit( final CodeWriter codeWriter, final boolean inline ) throws UncheckedIOException
    {
        final var layout = requireNonNullArgument( codeWriter, "codeWriter" ).layout();
        switch( layout )
        {
            case LAYOUT_FOUNDATION -> emit4Foundation( codeWriter, inline );
            case LAYOUT_JAVAPOET -> emit4JavaPoet( codeWriter, inline );
            //case LAYOUT_DEFAULT ->
            default -> emit4JavaPoet( codeWriter, inline );
        }
    }   //  emit()

    /**
     *  Emits this annotation to the given code writer.
     *
     *  @param  codeWriter  The code writer.
     *  @param  inline  {@code true} if the annotation should be placed on the
     *      same line as the annotated element, {@code false} otherwise.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final void emit4Foundation( final CodeWriter codeWriter, final boolean inline ) throws UncheckedIOException
    {
        requireNonNullArgument( codeWriter, "codeWriter" );

        final var whitespace = inline || m_ForceInline ? " " : "\n";
        final var memberSeparator = inline || m_ForceInline ? ", " : ",\n";

        if( m_Members.isEmpty() )
        {
            //---* @Singleton *------------------------------------------------
            codeWriter.emit( "@$T", m_Type );
        }
        else if( m_Members.size() == 1 && m_Members.containsKey( "value" ) )
        {
            //---* @Named("foo") *---------------------------------------------
            codeWriter.emit( "@$T( ", m_Type );
            emitAnnotationValues( codeWriter, whitespace, memberSeparator, m_Members.get( "value" ) );
            codeWriter.emit( " )" );
        }
        else
        {
            /*
             * Inline:
             * @Column( name = "updated_at", nullable = false )
             *
             * Not inline:
             * @Column(
             *     name = "updated_at",
             *     nullable = false
             * )
             */
            codeWriter.emit( "@$T(" + whitespace, m_Type );
            codeWriter.indent( 1 );
            for( final var iterator = m_Members.entrySet().iterator(); iterator.hasNext(); )
            {
                final var entry = iterator.next();
                codeWriter.emit( "$L = ", entry.getKey() );
                emitAnnotationValues( codeWriter, whitespace, memberSeparator, entry.getValue() );
                if( iterator.hasNext() ) codeWriter.emit( memberSeparator );
            }
            codeWriter.unindent( 1 );
            codeWriter.emit( whitespace + ")" );
        }
    }   //  emit4Foundation()

    /**
     *  Emits this annotation to the given code writer using the original
     *  JavaPoet layout.
     *
     *  @param  codeWriter  The code writer.
     *  @param  inline  {@code true} if the annotation should be placed on the
     *      same line as the annotated element, {@code false} otherwise.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final void emit4JavaPoet( final CodeWriter codeWriter, final boolean inline ) throws UncheckedIOException
    {
        requireNonNullArgument( codeWriter, "codeWriter" );

        final var whitespace = inline || m_ForceInline ? EMPTY_STRING : "\n";
        final var memberSeparator = inline || m_ForceInline ? ", " : ",\n";

        if( m_Members.isEmpty() )
        {
            //---* @Singleton *------------------------------------------------
            codeWriter.emit( "@$T", m_Type );
        }
        else if( m_Members.size() == 1 && m_Members.containsKey( "value" ) )
        {
            //---* @Named("foo") *---------------------------------------------
            codeWriter.emit( "@$T(", m_Type );
            emitAnnotationValues( codeWriter, whitespace, memberSeparator, m_Members.get( "value" ) );
            codeWriter.emit( ")" );
        }
        else
        {
            /*
             * Inline:
             * @Column(name = "updated_at", nullable = false)
             *
             * Not inline:
             * @Column(
             *     name = "updated_at",
             *     nullable = false
             * )
             */
            codeWriter.emit( "@$T(" + whitespace, m_Type );
            codeWriter.indent( 2 );
            for( final var iterator = m_Members.entrySet().iterator(); iterator.hasNext(); )
            {
                final var entry = iterator.next();
                codeWriter.emit( "$L = ", entry.getKey() );
                emitAnnotationValues( codeWriter, whitespace, memberSeparator, entry.getValue() );
                if( iterator.hasNext() ) codeWriter.emit( memberSeparator );
            }
            codeWriter.unindent( 2 );
            codeWriter.emit( whitespace + ")" );
        }
    }   //  emit4JavaPoet()

    /**
     *  Emits the values of this annotation to the given code writer.
     *
     *  @param  codeWriter  The code writer.
     *  @param  whitespace  The whitespace to emit.
     *  @param  memberSeparator The separator for the members.
     *  @param  values  The members to emit.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private static final void emitAnnotationValues( final CodeWriter codeWriter, final String whitespace, final String memberSeparator, final List<CodeBlockImpl> values ) throws UncheckedIOException
    {
        if( values.size() == 1 )
        {
            codeWriter.indent( 2 );
            codeWriter.emit( values.getFirst() );
            codeWriter.unindent( 2 );
        }
        else
        {
            codeWriter.emit( "{" + whitespace );
            codeWriter.indent( 2 );
            var first = true;
            for( final var codeBlock : values )
            {
                if( !first ) codeWriter.emit( memberSeparator );
                codeWriter.emit( codeBlock );
                first = false;
            }
            codeWriter.unindent( 2 );
            codeWriter.emit( whitespace + "}" );
        }
    }   //  emitAnnotationValues()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = this == o;
        if( !retValue && (o instanceof final AnnotationSpecImpl other) )
        {
            retValue = m_Composer.equals( other.m_Composer ) && toString().equals( o.toString() );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  equals()

    /**
     *  Returns the
     *  {@link JavaComposer}
     *  factory.
     *
     *  @return The reference to the factory.
     */
    @SuppressWarnings( {"PublicMethodNotExposedInInterface"} )
    public final JavaComposer getFactory() { return m_Composer; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int hashCode() { return hash( m_Composer, toString() ); }

    /**
     *  The initializer for
     *  {@link #m_CachedString}.
     *
     *  @return The return value for
     *      {@link #toString()}.
     */
    private final String initializeCachedString()
    {
        final var resultBuilder = new StringBuilder();
        final var codeWriter = new CodeWriter( m_Composer, resultBuilder );
        try
        {
            codeWriter.emit( "$L", this );
        }
        catch( final UncheckedIOException e )
        {
            throw new UnexpectedExceptionError( e.getCause() );
        }
        final var retValue = resultBuilder.toString();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  initializeCachedString()

    /**
     *  Creates a new builder that is initialised with the components of this
     *  annotation.
     *
     *  @return The new builder.
     */
    @SuppressWarnings( "AccessingNonPublicFieldOfAnotherObject" )
    @Override
    public final Builder toBuilder()
    {
        final var retValue = new BuilderImpl( m_Composer, m_Type );
        for( final var entry : m_Members.entrySet() )
        {
            retValue.m_CodeBlocks.put( entry.getKey(), new ArrayList<>( entry.getValue() ) );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toBuilder()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString() { return m_CachedString.get(); }
}
//  class AnnotationSpecImpl

/*
 *  End of File
 */