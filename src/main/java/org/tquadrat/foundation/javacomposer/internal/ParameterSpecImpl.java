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

import static java.util.Arrays.stream;
import static java.util.Collections.addAll;
import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.lang.Objects.hash;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidArgument;
import static org.tquadrat.foundation.util.StringUtils.format;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.UnexpectedExceptionError;
import org.tquadrat.foundation.javacomposer.AnnotationSpec;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.ParameterSpec;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.lang.Lazy;
import org.tquadrat.foundation.util.JavaUtils;

/**
 *  The implementation of
 *  {@link ParameterSpec}.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ParameterSpecImpl.java 943 2021-12-21 01:34:32Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: ParameterSpecImpl.java 943 2021-12-21 01:34:32Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public final class ParameterSpecImpl implements ParameterSpec
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The implementation of
     *  {@link org.tquadrat.foundation.javacomposer.ParameterSpec.Builder}
     *
     *  @author Square,Inc.
     *  @modified   Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: ParameterSpecImpl.java 943 2021-12-21 01:34:32Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: ParameterSpecImpl.java 943 2021-12-21 01:34:32Z tquadrat $" )
    @API( status = INTERNAL, since = "0.0.5" )
    public static final class BuilderImpl implements ParameterSpec.Builder
    {
            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  The annotations for the parameter.
         */
        private final Collection<AnnotationSpecImpl> m_Annotations = new ArrayList<>();

        /**
         *  The reference to the factory.
         */
        @SuppressWarnings( "InstanceVariableOfConcreteClass" )
        private final JavaComposer m_Composer;

        /**
         *  The Javadoc comment for the parameter.
         */
        @SuppressWarnings( "InstanceVariableOfConcreteClass" )
        private final CodeBlockImpl.BuilderImpl m_Javadoc;

        /**
         *  The modifiers for the parameter.
         */
        private final Set<Modifier> m_Modifiers = EnumSet.noneOf( Modifier.class );

        /**
         *  The name for the parameter.
         */
        private final String m_Name;

        /**
         *  The type for the parameter.
         */
        @SuppressWarnings( "InstanceVariableOfConcreteClass" )
        private final TypeNameImpl m_Type;

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code BuilderImpl} instance.
         *
         *  @param  composer    The reference to the factory that created this
         *      builder instance.
         *  @param  type    The type for the new parameter.
         *  @param  name    The name for the new parameter.
         */
        @SuppressWarnings( "CastToConcreteClass" )
        public BuilderImpl( final JavaComposer composer, final TypeName type, final CharSequence name )
        {
            m_Composer = requireNonNullArgument( composer, "composer" );
            m_Type = (TypeNameImpl) requireNonNullArgument( type, "type" );
            m_Name = requireNotEmptyArgument( name, "name" ).toString().intern();

            m_Javadoc = (CodeBlockImpl.BuilderImpl) m_Composer.codeBlockBuilder();
        }   //  BuilderImpl()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "CastToConcreteClass" )
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
        @SuppressWarnings( "UseOfConcreteClass" )
        @Override
        public final BuilderImpl addAnnotation( final Class<?> annotation )
        {
            return addAnnotation( ClassNameImpl.from( requireNonNullArgument( annotation, "annotation" ) ) );
        }   //  addAnnotation()

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "CastToConcreteClass" )
        @Override
        public final BuilderImpl addAnnotation( final ClassName annotationClassName )
        {
            final var annotation = (AnnotationSpecImpl) m_Composer.annotationBuilder( requireNonNullArgument( annotationClassName, "annotationClassName" ) )
                .build();
            m_Annotations.add( annotation );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addAnnotation()

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "CastToConcreteClass" )
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
        @SuppressWarnings( "UseOfConcreteClass" )
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
        @SuppressWarnings( "UseOfConcreteClass" )
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
        @SuppressWarnings( {"BoundedWildcard", "UseOfConcreteClass"} )
        @Override
        public final BuilderImpl addModifiers( final Iterable<Modifier> modifiers )
        {
            for( final var modifier : requireNonNullArgument( modifiers, "modifiers" ) )
            {
                m_Modifiers.add( modifier );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addModifiers()

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "UseOfConcreteClass" )
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
        @SuppressWarnings( "UseOfConcreteClass" )
        @Override
        public final ParameterSpecImpl build() { return new ParameterSpecImpl( this ); }
    }
    //  class Builder

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The annotations of this parameter.
     */
    private final List<AnnotationSpecImpl> m_Annotations;

    /**
     *  Lazily initialised return value of
     *  {@link #toString()}
     *  for this instance.
     */
    private final Lazy<String> m_CachedString;

    /**
     *  The reference to the factory.
     */
    @SuppressWarnings( "InstanceVariableOfConcreteClass" )
    private final JavaComposer m_Composer;

    /**
     *  The Javadoc comment for this type.
     */
    @SuppressWarnings( "InstanceVariableOfConcreteClass" )
    private final CodeBlockImpl m_Javadoc;

    /**
     *  The modifiers of this parameter.
     */
    private final Set<Modifier> m_Modifiers;

    /**
     *  The name of this parameter.
     */
    private final String m_Name;

    /**
     *  The type of this parameter.
     */
    @SuppressWarnings( "InstanceVariableOfConcreteClass" )
    private final TypeNameImpl m_Type;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code ParameterSpecImpl} instance.
     *
     *  @param  builder The builder.
     */
    @SuppressWarnings( {"AccessingNonPublicFieldOfAnotherObject", "UseOfConcreteClass"} )
    public ParameterSpecImpl( final BuilderImpl builder )
    {
        m_Composer = builder.m_Composer;
        m_Name = builder.m_Name;
        m_Annotations = List.copyOf( builder.m_Annotations );
        m_Javadoc = builder.m_Javadoc.build();
        m_Modifiers = Set.copyOf( builder.m_Modifiers );
        m_Type = builder.m_Type;

        m_CachedString = Lazy.use( this::initializeCachedString );
    }   //  ParameterSpecImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a builder for a new
     *  {@code ParameterSpecImpl}
     *  instance.
     *
     *  @param  type    The type of the new parameter.
     *  @param  name    The name of the new parameter.
     *  @param  modifiers   The modifiers for the new parameter.
     *  @return The builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final BuilderImpl builder( final Type type, final CharSequence name, final Modifier... modifiers )
    {
        return builder( TypeNameImpl.from( requireNonNullArgument( type, "type" ) ), name, modifiers );
    }   //  builder()

    /**
     *  Creates a builder for a new
     *  {@code ParameterSpec}
     *  instance.
     *
     *  @param  type    The type of the new parameter.
     *  @param  name    The name of the new parameter.
     *  @param  modifiers   The modifiers for the new parameter.
     *  @return The builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @SuppressWarnings( "CastToConcreteClass" )
    public static final BuilderImpl builder( final TypeName type, final CharSequence name, final Modifier... modifiers )
    {
        final var composer = new JavaComposer();
        final var retValue = new BuilderImpl( composer, type, requireValidArgument( name, "name", JavaUtils::isValidName, $ -> format( "not a valid name: %s", name ) ) )
            .addModifiers( modifiers );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  builder()

    /**
     *  Emits the parameter to the given code writer.
     *
     *  @param  codeWriter  The code writer.
     *  @param  varargs {@code true} if this parameter is a {@code vararg}
     *      parameter, {@code false} if it is a regular parameter.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( {"PublicMethodNotExposedInInterface", "UseOfConcreteClass"} )
    public final void emit( final CodeWriter codeWriter, final boolean varargs ) throws UncheckedIOException
    {
        codeWriter.emitAnnotations( m_Annotations, true );
        codeWriter.emitModifiers( m_Modifiers );
        if( varargs )
        {
            /*
             * varargs is true, so m_Type must be an array, and
             * TypeNameImpl.asArray() returns a non-empty Optional …
             */
            //noinspection OptionalGetWithoutIsPresent
            TypeNameImpl.asArray( m_Type ).get().emit( codeWriter, true );
        }
        else
        {
            m_Type.emit( codeWriter );
        }
        codeWriter.emit( " $L", m_Name );
    }   //  emit()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "AccessingNonPublicFieldOfAnotherObject" )
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = this == o;
        if( !retValue && (o instanceof ParameterSpecImpl other) )
        {
            retValue = m_Composer.equals( other.m_Composer )
                && toString().equals( other.toString() )
                && m_Javadoc.equals( other.m_Javadoc );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  equals()

    /**
     *  Creates an instance of {@code ParameterSpec} from the given
     *  {@link VariableElement}
     *  instance.
     *
     *  @param  element The variable element.
     *  @return The parameter spec.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @SuppressWarnings( "CastToConcreteClass" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    public static final ParameterSpecImpl get( final VariableElement element )
    {
        final var composer = new JavaComposer();
        final var retValue = (ParameterSpecImpl) composer.createParameter( element );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  get()

    /**
     *  Creates an instance of {@code ParameterSpec} from the given
     *  {@link Parameter}
     *  instance.
     *
     *  @param  parameter The variable element.
     *  @return The parameter spec.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @SuppressWarnings( "CastToConcreteClass" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    public static final ParameterSpecImpl get( final Parameter parameter )
    {
        final var composer = new JavaComposer();
        final var retValue = (ParameterSpecImpl) composer.createParameter( parameter );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  get()

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
     *  {@inheritDoc}
     */
    @Override
    public final int hashCode()
    {
        final var retValue = hash( m_Composer, toString(), m_Javadoc );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  hashCode()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean hasModifier( final Modifier modifier ) { return m_Modifiers.contains( requireNonNullArgument( modifier, "modifier" ) ); }

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
            emit( codeWriter, false );
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
     *  Returns the Javadoc comment for this parameter.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the JavaDoc comment.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final Optional<CodeBlock> javadoc()
    {
        final var comment = m_Javadoc.isEmpty() ? null : m_Composer.codeBlockOf( "\n@param $N $L", this, m_Javadoc );
        final var retValue = Optional.ofNullable( comment );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  javadoc()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String name() { return m_Name; }

    /**
     *  Creates a new
     *  {@code ParameterSpec}
     *  instance for the given arguments.
     *
     *  @param  type    The type of the new parameter.
     *  @param  name    The name of the new parameter.
     *  @param  modifiers   The modifiers for the new parameter.
     *  @return The parameter specification.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final ParameterSpecImpl of( final Type type, final CharSequence name, final Modifier... modifiers )
    {
        return of( TypeNameImpl.from( requireNonNullArgument( type, "type" ) ), name, modifiers );
    }   //  of()

    /**
     *  Creates a new
     *  {@code ParameterSpec}
     *  instance for the given arguments.
     *
     *  @param  type    The type of the new parameter.
     *  @param  name    The name of the new parameter.
     *  @param  modifiers   The modifiers for the new parameter.
     *  @return The parameter specification.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final ParameterSpecImpl of( final TypeName type, final CharSequence name, final Modifier... modifiers )
    {
        final var retValue = builder( type, name ).addModifiers( modifiers ).build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  of()

    /**
     *  Retrieves the parameters from the given method.
     *
     *  @param  method  The method.
     *  @return The parameters of the given method; the returned list can be
     *      empty, but it will not be {@code null}.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final List<ParameterSpecImpl> parametersOf( final ExecutableElement method )
    {
        final var retValue = method.getParameters().stream()
            .map( ParameterSpecImpl::get )
            .toList();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  parametersOf()

    /**
     *  Retrieves the parameters from the given method.
     *
     *  @param  method  The method.
     *  @return The parameters of the given method; the returned list can be
     *      empty, but it will not be {@code null}.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    @API( status = DEPRECATED, since = "0.0.8" )
    public static final List<ParameterSpecImpl> parametersOf( final Method method )
    {
        final var retValue = stream( method.getParameters() )
            .map( ParameterSpecImpl::get )
            .toList();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  parametersOf()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Override
    public final BuilderImpl toBuilder()
    {
        final var retValue = toBuilder( m_Type, m_Name, true );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toBuilder()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( {"CastToConcreteClass", "AccessingNonPublicFieldOfAnotherObject"} )
    @Override
    public final BuilderImpl toBuilder( final TypeName type, final CharSequence name, final boolean keepJavadoc )
    {
        final var retValue = new BuilderImpl( m_Composer, type, name );
        retValue.m_Annotations.addAll( m_Annotations );
        retValue.m_Modifiers.addAll( m_Modifiers );
        if( keepJavadoc ) retValue.m_Javadoc.addWithoutDebugInfo( m_Javadoc );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toBuilder()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString() { return m_CachedString.get(); }

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Override
    public final TypeNameImpl type() { return m_Type; }
}
//  class ParameterSpecImpl

/*
 *  End of File
 */