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

import static java.util.Collections.addAll;
import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.javacomposer.internal.Util.createDebugOutput;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.checkState;
import static org.tquadrat.foundation.lang.Objects.hash;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidArgument;

import javax.lang.model.element.Modifier;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.UnexpectedExceptionError;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.AnnotationSpec;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.lang.Lazy;
import org.tquadrat.foundation.util.JavaUtils;

/**
 *  The implementation for
 *  {@link FieldSpec}.
 *
 *  @author Square,Inc.
 *  @modified   Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: FieldSpecImpl.java 1085 2024-01-05 16:23:28Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: FieldSpecImpl.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public final class FieldSpecImpl implements FieldSpec
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The implementation of
     *  {@link org.tquadrat.foundation.javacomposer.FieldSpec.Builder}
     *
     *  @author Square,Inc.
     *  @modified   Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: FieldSpecImpl.java 1085 2024-01-05 16:23:28Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: FieldSpecImpl.java 1085 2024-01-05 16:23:28Z tquadrat $" )
    @API( status = INTERNAL, since = "0.0.5" )
    public static final class BuilderImpl implements FieldSpec.Builder
    {
            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  The annotations for the field.
         */
        private final Collection<AnnotationSpecImpl> m_Annotations = new ArrayList<>();

        /**
         *  The reference to the factory.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private final JavaComposer m_Composer;

        /**
         *  The initializer for the field.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private CodeBlockImpl m_Initializer = null;

        /**
         *  The Javadoc comment for the field.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private final CodeBlockImpl.BuilderImpl m_Javadoc;

        /**
         *  The modifiers for the field.
         */
        private final Set<Modifier> m_Modifiers = EnumSet.noneOf( Modifier.class );

        /**
         *  The name for the field.
         */
        private final String m_Name;

        /**
         *  The type for the field.
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
         *  @param  type    The type for the new field.
         *  @param  name    The name for the new field.
         */
        public BuilderImpl( @SuppressWarnings( "UseOfConcreteClass" ) final JavaComposer composer, @SuppressWarnings( "UseOfConcreteClass" ) final TypeNameImpl type, final CharSequence name )
        {
            m_Composer = requireNonNullArgument( composer, "composer" );
            m_Type = requireNonNullArgument( type, "type" );
            m_Name = requireNotEmptyArgument( name, "name" ).toString().intern();

            m_Javadoc = (CodeBlockImpl.BuilderImpl) m_Composer.codeBlockBuilder();
        }   //  BuilderImpl()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addAnnotation( final AnnotationSpec annotationSpec )
        {
            m_Annotations.add( (AnnotationSpecImpl) requireNonNullArgument( annotationSpec, "annotationSpec" ) );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addAnnotation()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addAnnotation( final Class<?> annotation )
        {
            return addAnnotation( ClassNameImpl.from( requireNonNullArgument( annotation, "annotation" ) ) );
        }   //  addAnnotation()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addAnnotation( final ClassName annotation )
        {
            m_Annotations.add( (AnnotationSpecImpl) m_Composer.annotationBuilder( requireNonNullArgument( annotation, "annotation" ) )
                .build() );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addAnnotation()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addAnnotations( final Iterable<AnnotationSpec> annotationSpecs )
        {
            for( final var annotationSpec : requireNonNullArgument( annotationSpecs, "annotationSpecs" ) )
            {
                m_Annotations.add( (AnnotationSpecImpl) annotationSpec );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addAnnotations()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addJavadoc( final CodeBlock block )
        {
            m_Javadoc.addWithoutDebugInfo( block );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addJavadoc()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addJavadoc( final String format, final Object... args )
        {
            m_Javadoc.addWithoutDebugInfo( format, args );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addJavadoc()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addModifiers( final Modifier... modifiers )
        {
            addAll( m_Modifiers, requireNonNullArgument( modifiers, "modifiers" ) );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addModifiers()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final FieldSpecImpl build() { return new FieldSpecImpl( this ); }

        /**
         *  Sets the initializer for the field.
         *
         *  @param  codeBlock   The code that initialises the field.
         *  @return This {@code Builder} instance.
         */
        @Override
        public final BuilderImpl initializer( final CodeBlock codeBlock )
        {
            checkState( isNull( m_Initializer ), () -> new IllegalStateException( "initializer was already set" ) );
            var codeBlockImpl = (CodeBlockImpl) requireNonNullArgument( codeBlock, "codeBlock" );
            if( m_Composer.addDebugOutput() )
            {
                codeBlockImpl = (CodeBlockImpl) createDebugOutput( true )
                    .map( DebugOutput::asComment )
                    .map( m_Composer::codeBlockOf )
                    .map( block -> block.join( "\n", codeBlock ) )
                    .orElse( codeBlock );
            }
            m_Initializer = codeBlockImpl;

            //---* Done *------------------------------------------------------
            return this;
        }   //  initializer()

        /**
         *  Sets the initializer for the field.
         *
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        @Override
        public final BuilderImpl initializer( final String format, final Object... args )
        {
            return initializer( m_Composer.codeBlockOf( format, args ) );
        }   //  initializer()
    }
    //  class Builder

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The annotations for the field.
     */
    private final List<AnnotationSpecImpl> m_Annotations;

    /**
     *  The reference to the factory.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final JavaComposer m_Composer;

    /**
     *  Lazily initialised return value of
     *  {@link #toString()}
     *  for this instance.
     */
    private final Lazy<String> m_CachedString;

    /**
     *  The initializer for the field.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final CodeBlockImpl m_Initializer;

    /**
     *  The Javadoc comment for the field.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final CodeBlockImpl m_Javadoc;

    /**
     *  The modifiers for the field.
     */
    private final Set<Modifier> m_Modifiers;

    /**
     *  The name for the field.
     */
    private final String m_Name;

    /**
     *  The static imports.
     */
    private final Set<String> m_StaticImports;

    /**
     *  The type of the field.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final TypeNameImpl m_Type;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code FieldSpecImpl} instance.
     *
     *  @param  builder The builder.
     */
    @SuppressWarnings( {"AccessingNonPublicFieldOfAnotherObject"} )
    public FieldSpecImpl( @SuppressWarnings( "UseOfConcreteClass" ) final BuilderImpl builder )
    {
        m_Composer = builder.m_Composer;
        m_Type = builder.m_Type;
        m_Name = builder.m_Name;
        m_Javadoc = builder.m_Javadoc.build();
        m_Annotations = List.copyOf( builder.m_Annotations );
        m_Modifiers = Set.copyOf( builder.m_Modifiers );
        m_Initializer = isNull( builder.m_Initializer )
            ? (CodeBlockImpl) m_Composer.emptyCodeBlock()
            : builder.m_Initializer;

        final Collection<String> staticImports = new HashSet<>( m_Javadoc.getStaticImports() );
        staticImports.addAll( m_Initializer.getStaticImports() );
        m_StaticImports = Set.copyOf( staticImports );

        m_CachedString = Lazy.use( this::initializeCachedString );
    }   //  FieldSpecImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a builder for an instance of {@code FieldSpec} from the given
     *  type, name and modifiers.
     *
     *  @param  type    The type of the {@code FieldSpec} to build.
     *  @param  name    The name for the new field.
     *  @param  modifiers   The modifiers.
     *  @return The new builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final BuilderImpl builder( final Type type, final CharSequence name, final Modifier... modifiers )
    {
        final var retValue = builder( TypeNameImpl.from( requireNonNullArgument( type, "type" ) ), name, modifiers );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  builder()

    /**
     *  Creates a builder for an instance of {@code FieldSpec} from the given
     *  type, name and modifiers.
     *
     *  @param  type    The type of the {@code FieldSpec} to build.
     *  @param  name    The name for the new field.
     *  @param  modifiers   The modifiers.
     *  @return The new builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final BuilderImpl builder( final TypeName type, final CharSequence name, final Modifier... modifiers )
    {
        final var composer = new JavaComposer();

        final var retValue = new BuilderImpl( composer, (TypeNameImpl) requireNonNullArgument( type, "type" ), requireValidArgument( name, "name", JavaUtils::isValidName, $ -> "not a valid name: %s".formatted( name ) ) )
            .addModifiers( requireNonNullArgument( modifiers, "modifiers" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  builder()

    /**
     *  Creates a builder for an instance of {@code FieldSpec} from the given
     *  type, name and modifiers.
     *
     *  @param  type    The type of the {@code FieldSpec} to build.
     *  @param  name    The name for the new field.
     *  @param  modifiers   The modifiers.
     *  @return The new builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static BuilderImpl builder( final TypeSpec type, final CharSequence name, final Modifier... modifiers )
    {
        final var typeName = ClassNameImpl.from( EMPTY_STRING, requireNonNullArgument( type, "type" ).name().orElseThrow( () -> new ValidationException( "Anonymous class cannot be used as type for a field" ) ) );
        final var retValue = builder( typeName, name, modifiers );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  builder()

    /**
     *  Emits this {@code FieldSpec} instance to the given code writer.
     *
     *  @param  codeWriter  The code writer.
     *  @param  implicitModifiers   The implicit modifiers.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( {"PublicMethodNotExposedInInterface", "UseOfConcreteClass"} )
    public final void emit( final CodeWriter codeWriter, final Collection<Modifier> implicitModifiers ) throws UncheckedIOException
    {
        codeWriter.emitJavadoc( m_Javadoc );
        codeWriter.emitAnnotations( m_Annotations, false );
        codeWriter.emitModifiers( m_Modifiers, implicitModifiers );
        codeWriter.emit( "$T $L", m_Type, m_Name );
        if( !m_Initializer.isEmpty() )
        {
            codeWriter.emit( " = " );
            codeWriter.emit( m_Initializer );
        }
        codeWriter.emit( ";\n" );
    }   //  emit()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = this == o;
        if( !retValue && (o instanceof FieldSpecImpl) )
        {
            retValue = toString().equals( o.toString() );
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
     *  Returns the Javadoc for this field.
     *
     *  @return The Javadoc.
     *
     *  @since 0.2.0
     */
    @API( status = INTERNAL, since = "0.2.0" )
    @SuppressWarnings( {"PublicMethodNotExposedInInterface"} )
    public final CodeBlockImpl getJavadoc() { return m_Javadoc; }

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
    public final int hashCode() { return hash( m_Composer, toString() ); }

    /**
     *  Checks whether the field has an initializer.
     *
     *  @return {@code true} if the field has an initializer, {@code false}
     *      otherwise.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final boolean hasInitializer() { return !m_Initializer.isEmpty(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean hasModifier( final Modifier modifier ) { return m_Modifiers.contains( modifier ); }

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
            emit( codeWriter, Set.of() );
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
     *  {@inheritDoc}
     */
    @Override
    public final Set<Modifier> modifiers() { return m_Modifiers; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String name() { return m_Name; }

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( {"AccessingNonPublicFieldOfAnotherObject"} )
    @Override
    public final BuilderImpl toBuilder()
    {
        final var retValue = new BuilderImpl( m_Composer, m_Type, m_Name );
        retValue.m_Javadoc.addWithoutDebugInfo( m_Javadoc );
        retValue.m_Annotations.addAll( m_Annotations );
        retValue.m_Modifiers.addAll( m_Modifiers );
        retValue.m_Initializer = m_Initializer.isEmpty() ? null : m_Initializer;
        return retValue;
    }   //  toBuilder()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString()
    {
        final var retValue = m_CachedString.get();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toString()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final TypeNameImpl type() { return m_Type; }
}
//  class FieldSpecImpl

/*
 *  End of File
 */