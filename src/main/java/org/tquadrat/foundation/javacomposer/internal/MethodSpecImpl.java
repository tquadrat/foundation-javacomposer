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

import static java.lang.String.format;
import static java.util.Collections.addAll;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.NATIVE;
import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.javacomposer.internal.TypeNameImpl.VOID_PRIMITIVE;
import static org.tquadrat.foundation.javacomposer.internal.Util.createDebugOutput;
import static org.tquadrat.foundation.lang.Objects.checkState;
import static org.tquadrat.foundation.lang.Objects.hash;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidNonNullArgument;
import static org.tquadrat.foundation.util.JavaUtils.isValidName;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Types;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.UnexpectedExceptionError;
import org.tquadrat.foundation.exception.UnsupportedEnumError;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.AnnotationSpec;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.MethodSpec;
import org.tquadrat.foundation.javacomposer.ParameterSpec;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeVariableName;
import org.tquadrat.foundation.lang.Lazy;
import org.tquadrat.foundation.lang.Objects;

/**
 *  The implementation for
 *  {@link MethodSpec}.
 *
 *  @author Square,Inc.
 *  @modified   Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: MethodSpecImpl.java 1105 2024-02-28 12:58:46Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( {"ClassWithTooManyFields"} )
@ClassVersion( sourceVersion = "$Id: MethodSpecImpl.java 1105 2024-02-28 12:58:46Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public final class MethodSpecImpl implements MethodSpec
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The implementation for
     *  {@link org.tquadrat.foundation.javacomposer.MethodSpec.Builder}
     *
     *  @author Square,Inc.
     *  @modified   Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: MethodSpecImpl.java 1105 2024-02-28 12:58:46Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: MethodSpecImpl.java 1105 2024-02-28 12:58:46Z tquadrat $" )
    @API( status = INTERNAL, since = "0.0.5" )
    public static final class BuilderImpl implements MethodSpec.Builder
    {
            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  The annotations for the method.
         */
        private final Collection<AnnotationSpecImpl> m_Annotations = new ArrayList<>();

        /**
         *  The code for the method.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private final CodeBlockImpl.BuilderImpl m_Code;

        /**
         *  The reference to the factory.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private final JavaComposer m_Composer;

        /**
         *  The default value for the method; this is for annotations only.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private CodeBlockImpl m_DefaultValue;

        /**
         *  The declared exceptions for the method.
         */
        private final Collection<TypeNameImpl> m_Exceptions = new LinkedHashSet<>();

        /**
         *  The Javadoc comment for the method.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private final CodeBlockImpl.BuilderImpl m_Javadoc;

        /**
         *  The modifiers for the method.
         */
        private final Set<Modifier> m_Modifiers = EnumSet.noneOf( Modifier.class );

        /**
         *  The name for the method.
         */
        private final String m_Name;

        /**
         *  The parameters for the method.
         */
        private final List<ParameterSpecImpl> m_Parameters = new ArrayList<>();

        /**
         *  The comment for the return value for the method.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private final CodeBlockImpl.BuilderImpl m_ReturnComment;

        /**
         *  The return type for the method.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private TypeNameImpl m_ReturnType;

        /**
         *  The static imports.
         */
        private final Collection<String> m_StaticImports = new TreeSet<>();

        /**
         *  The type variables for the method.
         */
        private final Collection<TypeVariableNameImpl> m_TypeVariables = new ArrayList<>();

        /**
         *  The flag that indicates whether a parameter (the last one) is a
         *  {@code varargs} parameter.
         */
        private boolean m_Varargs = false;

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code BuilderImpl} instance.
         *
         *  @param  composer    The reference to the factory that created this
         *      builder instance.
         *  @param  name    The name for the method.
         */
        public BuilderImpl( @SuppressWarnings( "UseOfConcreteClass" ) final JavaComposer composer, final CharSequence name )
        {
            m_Composer = requireNonNullArgument( composer, "composer" );
            m_Name = requireValidArgument( requireNotEmptyArgument( name, "name" ), "name", v -> v.equals( CONSTRUCTOR ) || isValidName( v ), $ -> "not a valid name: %s".formatted( name ) ).toString().intern();
            m_ReturnType = name.equals( CONSTRUCTOR ) ? null : VOID_PRIMITIVE;

            m_Code = (CodeBlockImpl.BuilderImpl) m_Composer.codeBlockBuilder();
            m_Javadoc = (CodeBlockImpl.BuilderImpl) m_Composer.codeBlockBuilder();
            m_ReturnComment = (CodeBlockImpl.BuilderImpl) m_Composer.codeBlockBuilder();
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
        public final BuilderImpl addAnnotation( final ClassName annotationClass )
        {
            final var annotation = (AnnotationSpecImpl) m_Composer.annotationBuilder( requireNonNullArgument( annotationClass, "annotationClass" ) )
                .build();
            m_Annotations.add( annotation );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addAnnotation()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addAnnotations( final Iterable<? extends AnnotationSpec> annotationSpecs )
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
        public final BuilderImpl addCode( final CodeBlock codeBlock )
        {
            m_Code.add( requireNonNullArgument( codeBlock, "codeBlock" ) );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addCode()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addCode( final String format, final Object... args )
        {
            m_Code.add( format, args );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addCode()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addComment( final String format, final Object... args )
        {
            m_Code.addWithoutDebugInfo( "// " + requireNonNullArgument( format, "format" ) + "\n", args );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addComment()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addException( final Type exception )
        {
            return addException( TypeNameImpl.from( requireNonNullArgument( exception, "exception" ) ) );
        }   //  addException()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addException( final TypeName exception )
        {
            m_Exceptions.add( (TypeNameImpl) requireNonNullArgument( exception, "exception" ) );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addException()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addExceptions( final Iterable<? extends TypeName> exceptions )
        {
            for( final var exception : requireNonNullArgument( exceptions, "exceptions" ) )
            {
                m_Exceptions.add( (TypeNameImpl) exception );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addExceptions()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addJavadoc( final CodeBlock block )
        {
            m_Javadoc.add( block );

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
        @SuppressWarnings( {"BoundedWildcard"} )
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
        public final BuilderImpl addNamedCode( final String format, final Map<String,?> args )
        {
            m_Code.addNamed( format, args );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addNamedCode()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addParameter( final ParameterSpec parameterSpec )
        {
            m_Parameters.add( (ParameterSpecImpl) requireNonNullArgument( parameterSpec, "parameterSpec" ) );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addParameter()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addParameter( final Type type, final String name, final Modifier... modifiers )
        {
            return addParameter( TypeName.from( requireNonNullArgument( type, "type" ) ), name, modifiers );
        }   //  addParameter()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addParameter( final TypeName type, final String name, final Modifier... modifiers )
        {
            final var parameter =  m_Composer.parameterBuilder( type, name, modifiers )
                .build();
            final var retValue = addParameter( parameter );

            //---* Done *----------------------------------------------------------
            return retValue;
        }   //  addParameter()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addParameters( final Iterable<? extends ParameterSpec> parameterSpecs )
        {
            for( final var parameterSpec : requireNonNullArgument( parameterSpecs, "parameterSpecs" ) )
            {
                m_Parameters.add( (ParameterSpecImpl) parameterSpec );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addParameters()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addStatement( final String format, final Object... args )
        {
            m_Code.addStatement( format, args );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addStatement()

        /**
         *  {@inheritDoc}
         */
        @API( status = STABLE, since = "0.2.0" )
        @Override
        public final MethodSpecImpl.BuilderImpl addStaticImport( final Class<?> clazz, final String... names )
        {
            return addStaticImport( ClassNameImpl.from( clazz ), names );
        }   //  addStaticImport()

        /**
         *  {@inheritDoc}
         */
        @API( status = STABLE, since = "0.2.0" )
        @Override
        public final MethodSpecImpl.BuilderImpl addStaticImport( final ClassName className, final String... names )
        {
            final var canonicalName = requireNonNullArgument( className, "className" ).canonicalName();
            for( final var name : requireValidNonNullArgument( names, "names", v -> v.length > 0, "%s array is empty"::formatted ) )
            {
                m_StaticImports.add(
                    format(
                        "%s.%s",
                        canonicalName,
                        requireValidArgument(
                            name,
                            "name",
                            Objects::nonNull,
                            $ -> "null entry in names array: %s".formatted( Arrays.toString( names ) )
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
        @API( status = STABLE, since = "0.2.0" )
        @Override
        public final MethodSpecImpl.BuilderImpl addStaticImport( final Enum<?> constant )
        {
            return addStaticImport( ClassNameImpl.from( requireNonNullArgument( constant, "constant" ).getDeclaringClass() ), constant.name() );
        }   //  addStaticImport()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addTypeVariable( final TypeVariableName typeVariable )
        {
            m_TypeVariables.add( (TypeVariableNameImpl) typeVariable );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addTypeVariable()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addTypeVariables( final Iterable<TypeVariableName> typeVariables )
        {
            for( final var typeVariable : requireNonNullArgument( typeVariables, "typeVariables" ) )
            {
                m_TypeVariables.add( (TypeVariableNameImpl) typeVariable );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addTypeVariables()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl beginControlFlow( final String controlFlow, final Object... args )
        {
            m_Code.beginControlFlow( controlFlow, args );

            //---* Done *------------------------------------------------------
            return this;
        }   //  beginControlFlow()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final MethodSpecImpl build()
        {
            /*
             * A method with a body cannot be abstract.
             * We remove the modifier abstract here so that
             * MethodSpec.toBuilder() will work properly.
             */
            if( !m_Code.isEmpty() )
            {
                m_Modifiers.remove( ABSTRACT );
            }
            final var retValue = new MethodSpecImpl( this );

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  build()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl defaultValue( final CodeBlock defaultValue )
        {
            checkState( isNull( m_DefaultValue ), () -> new IllegalStateException( "defaultValue was already set" ) );
            var codeBlockImpl = (CodeBlockImpl) requireNonNullArgument( defaultValue, "defaultValue" );
            if( m_Composer.addDebugOutput() )
            {
                codeBlockImpl = (CodeBlockImpl) createDebugOutput( true )
                    .map( DebugOutput::asComment )
                    .map( m_Composer::codeBlockOf )
                    .map( block -> block.join( " ", defaultValue ) )
                    .orElse( defaultValue );
            }
            m_DefaultValue = codeBlockImpl;

            //---* Done *------------------------------------------------------
            return this;
        }   //  defaultValue()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl defaultValue( final String format, final Object... args )
        {
            return defaultValue( m_Composer.codeBlockOf( format, args ) );
        }   //  defaultValue()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl endControlFlow()
        {
            m_Code.endControlFlow();

            //---* Done *------------------------------------------------------
            return this;
        }   //  endControlFlow()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl endControlFlow( final String controlFlow, final Object... args )
        {
            m_Code.endControlFlow( controlFlow, args );

            //---* Done *------------------------------------------------------
            return this;
        }   //  endControlFlow()

        /**
         *  Returns <i>a reference to</i> the declared exceptions.
         *
         *  @return The exceptions.
         */
        @SuppressWarnings( {"PublicMethodNotExposedInInterface", "AssignmentOrReturnOfFieldWithMutableType"} )
        public final Collection<TypeNameImpl> exceptions()
        {
            return m_Exceptions ;
        }   //  exceptions()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl nextControlFlow( final String controlFlow, final Object... args )
        {
            m_Code.nextControlFlow( controlFlow, args );

            //---* Done *------------------------------------------------------
            return this;
        }   //  nextControlFlow()

        /**
         *  Returns <i>a reference to</i>the parameters.
         *
         *  @return The parameters.
         */
        @SuppressWarnings( {"PublicMethodNotExposedInInterface", "AssignmentOrReturnOfFieldWithMutableType"} )
        public final List<ParameterSpecImpl> parameters()
        {
            return m_Parameters;
        }   //  parameters()
        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl returns( final Type returnType )
        {
            return returns( TypeNameImpl.from( requireNonNullArgument( returnType, "returnType" ) ) );
        }   //  returns()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl returns( final Type returnType, final String format, final Object... args )
        {
            return returns( TypeNameImpl.from( requireNonNullArgument( returnType, "returnType" ) ), format, args );
        }   //  returns()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl returns( final TypeName returnType )
        {
            checkState( !m_Name.equals( CONSTRUCTOR ), () -> new IllegalStateException( "constructor cannot have return type." ) );
            m_ReturnType = (TypeNameImpl) requireNonNullArgument( returnType, "returnType" );
            createDebugOutput( m_Composer.addDebugOutput() )
                .ifPresent( debug -> m_ReturnComment.addWithoutDebugInfo( debug.asLiteral() ) );

            //---* Done *------------------------------------------------------
            return this;
        }   //  returns()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl returns( final TypeName returnType, final String format, final Object... args )
        {
            returns( returnType );
            m_ReturnComment.addWithoutDebugInfo( format, args );

            //---* Done *------------------------------------------------------
            return this;
        }   //  returns()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl varargs() { return varargs( true ); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl varargs( final boolean varargs )
        {
            m_Varargs = varargs;

            //---* Done *------------------------------------------------------
            return this;
        }   //  varargs()
    }
    //  class BuilderImpl

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The annotations of this method.
     */
    private final List<AnnotationSpecImpl> m_Annotations;

    /**
     *  Lazily initialised return value of
     *  {@link #toString()}
     *  for this instance.
     */
    private final Lazy<String> m_CachedString;

    /**
     *  The code of this method.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final CodeBlockImpl m_Code;

    /**
     *  The reference to the factory.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final JavaComposer m_Composer;

    /**
     *  The default value of this method; only applicable for annotations.
     */
    @SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
    private final Optional<CodeBlockImpl> m_DefaultValue;

    /**
     *  The declared exceptions for this method.
     */
    private final List<TypeNameImpl> m_Exceptions;

    /**
     *  The Javadoc comment of this method.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final CodeBlockImpl m_Javadoc;

    /**
     *  The modifiers of this method.
     */
    private final Set<Modifier> m_Modifiers;

    /**
     *  The name of this method.
     */
    private final String m_Name;

    /**
     *  The parameters of this method.
     */
    private final List<ParameterSpecImpl> m_Parameters;

    /**
     *  The comment for the return value of the method.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final CodeBlockImpl m_ReturnComment;

    /**
     *  The return type of the method.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final TypeNameImpl m_ReturnType;

    /**
     *  The static imports.
     */
    private final Set<String> m_StaticImports;

    /**
     *  The type variables of this method.
     */
    private final List<TypeVariableNameImpl> m_TypeVariables;

    /**
     *  The flag that indicates whether a parameter (the last one) is a
     *  {@code varargs} parameter.
     */
    private final boolean m_Varargs;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code MethodSpecImpl} instance.
     *
     *  @param  builder The builder.
     */
    @SuppressWarnings( {"AccessingNonPublicFieldOfAnotherObject"} )
    public MethodSpecImpl( @SuppressWarnings( "UseOfConcreteClass" ) final BuilderImpl builder )
    {
        checkState( !builder.m_Varargs || lastParameterIsArray( builder.m_Parameters ), () -> new ValidationException( "last parameter of varargs method %s must be an array".formatted( builder.m_Name ) ) );

        final var code = builder.m_Code.build();
        checkState( code.isEmpty() || !builder.m_Modifiers.contains( ABSTRACT ), () -> new ValidationException( "abstract method %s cannot have code".formatted( builder.m_Name ) ) );

        m_Composer = builder.m_Composer;
        m_Name = builder.m_Name;
        m_Javadoc = builder.m_Javadoc.build();
        m_Annotations = List.copyOf( builder.m_Annotations );
        m_Modifiers = Set.copyOf( builder.m_Modifiers );
        m_TypeVariables = List.copyOf( builder.m_TypeVariables );
        m_ReturnType = builder.m_ReturnType;
        m_ReturnComment = builder.m_ReturnComment.build();
        m_Parameters = List.copyOf( builder.parameters() );
        m_Varargs = builder.m_Varargs;
        m_Exceptions = List.copyOf( builder.exceptions() );
        m_DefaultValue = Optional.ofNullable( builder.m_DefaultValue );
        m_Code = code;

        final Collection<String> staticImports = new HashSet<>( builder.m_StaticImports );
        staticImports.addAll( m_Javadoc.getStaticImports() );
        staticImports.addAll( m_ReturnComment.getStaticImports() );
        m_Parameters.stream()
            .map( ParameterSpecImpl::javadoc )
            .filter( Optional::isPresent )
            .map( codeBlock -> codeBlock.map( block -> (CodeBlockImpl) block ).get().getStaticImports() )
            .forEach( staticImports::addAll );
        m_DefaultValue.ifPresent( codeBlock -> staticImports.addAll( codeBlock.getStaticImports() ) );
        staticImports.addAll( m_Code.getStaticImports() );
        m_StaticImports = Set.copyOf( staticImports );

        m_CachedString = Lazy.use( this::initializeCachedString );
    }   //  MethodSpecImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Returns the default value of this method.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the default value.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final Optional<CodeBlockImpl> defaultValue() { return m_DefaultValue; }

    /**
     *  Emits the Javadoc to the given code writer.
     *
     *  @param  codeWriter  The code writer.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    private final void emitJavadoc( @SuppressWarnings( "UseOfConcreteClass" ) final CodeWriter codeWriter ) throws UncheckedIOException
    {
        final var javaDocBuilder = m_Javadoc.toBuilder();
        var addNewLine = false;
        @SuppressWarnings( {"OptionalGetWithoutIsPresent"} )
        final var paramComments = m_Parameters.stream()
            .filter( p -> p.javadoc().isPresent() )
            .map( p -> (CodeBlockImpl) p.javadoc().get() )
            .toArray( CodeBlockImpl[]::new );
        addNewLine = paramComments.length > 0;
        for( final var comment : paramComments )
        {
            javaDocBuilder.add( comment );
        }
        if( !m_ReturnComment.isEmpty() )
        {
            javaDocBuilder.add( m_Composer.codeBlockOf( "\n@return $L", m_ReturnComment ) );
            addNewLine = true;
        }
        if( addNewLine ) javaDocBuilder.add( "\n" );
        codeWriter.emitJavadoc( javaDocBuilder.build() );
    }   //  emitJavadoc()

    /**
     *  Emits this method to the given code writer.
     *
     *  @param  codeWriter  The code writer.
     *  @param  enclosingName   The name of the type that owns this method.
     *  @param  implicitModifiers   The implicit modifiers for this method.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( {"PublicMethodNotExposedInInterface", "OptionalUsedAsFieldOrParameterType"} )
    public final void emit( @SuppressWarnings( "UseOfConcreteClass" ) final CodeWriter codeWriter, final Optional<String> enclosingName, final Collection<Modifier> implicitModifiers ) throws UncheckedIOException
    {
        switch( codeWriter.layout() )
        {
            case LAYOUT_FOUNDATION -> emit4Foundation( codeWriter, enclosingName, implicitModifiers );
            case LAYOUT_DEFAULT, LAYOUT_JAVAPOET, LAYOUT_JAVAPOET_WITH_TAB -> emit4JavaPoet( codeWriter, enclosingName, implicitModifiers );
            default -> throw new UnsupportedEnumError( codeWriter.layout() );
        }
    }   //  emit()

    /**
     *  Emits this method to the given code writer, using the Foundation
     *  layout.
     *
     *  @param  codeWriter  The code writer.
     *  @param  enclosingName   The name of the type that owns this method.
     *  @param  implicitModifiers   The implicit modifiers for this method.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( {"OptionalUsedAsFieldOrParameterType", "OverlyComplexMethod"} )
    private final void emit4Foundation( @SuppressWarnings( "UseOfConcreteClass" ) final CodeWriter codeWriter, final Optional<String> enclosingName, final Collection<Modifier> implicitModifiers ) throws UncheckedIOException
    {
        emitJavadoc( codeWriter );
        codeWriter.emitAnnotations( m_Annotations, false );
        codeWriter.emitModifiers( m_Modifiers, implicitModifiers );

        if( !m_TypeVariables.isEmpty() )
        {
            codeWriter.emitTypeVariables( m_TypeVariables );
            codeWriter.emit( " " );
        }

        if( isConstructor() )
        {
            /*
             * Anonymous types do not have a constructor, therefore there is
             * no check required for the name.
             */
            //noinspection OptionalGetWithoutIsPresent
            codeWriter.emit( "$L", enclosingName.get() );
        }
        else
        {
            codeWriter.emit( "$T $L", m_ReturnType, m_Name );
        }

        if( !m_Parameters.isEmpty() )
        {
            codeWriter.emit( "( " );
            var firstParameter = true;
            for( final var iterator = m_Parameters.iterator(); iterator.hasNext(); )
            {
                final var parameter = iterator.next();
                if( !firstParameter ) codeWriter.emit( "," ).emitWrappingSpace();
                parameter.emit( codeWriter, !iterator.hasNext() && m_Varargs );
                firstParameter = false;
            }
            codeWriter.emit( " )" );
        }
        else
        {
            codeWriter.emit( "()" );
        }

        if( m_DefaultValue.isPresent() && !m_DefaultValue.get().isEmpty() )
        {
            codeWriter.emit( " default " );
            codeWriter.emit( m_DefaultValue.get() );
        }

        if( !m_Exceptions.isEmpty() )
        {
            codeWriter.emitWrappingSpace().emit( "throws" );
            var firstException = true;
            for( final var exception : m_Exceptions )
            {
                if( !firstException ) codeWriter.emit( "," );
                codeWriter.emitWrappingSpace().emit( "$T", exception );
                firstException = false;
            }
        }

        if( hasModifier( ABSTRACT ) )
        {
            codeWriter.emit( ";\n" );
        }
        else if( hasModifier( NATIVE ) )
        {
            //---* Code is allowed to support stuff like GWT JSNI *------------
            codeWriter.emit( m_Code );
            codeWriter.emit( ";\n" );
        }
        else
        {
            codeWriter.emit( "\n{" );

            if( !m_Code.isEmpty() )
            {
                codeWriter.emit( "\n" )
                    .indent()
                    .emit( m_Code )
                    .unindent();
            }

            if( isConstructor() )
            {
                //noinspection OptionalGetWithoutIsPresent
                codeWriter.emit( "}  //  $L()\n", enclosingName.get() );
            }
            else
            {
                codeWriter.emit( "}  //  $L()\n", m_Name );
            }
        }
    }   //  emit4Foundation()

    /**
     *  Emits this method to the given code writer, using the JavaPoet layout.
     *
     *  @param  codeWriter  The code writer.
     *  @param  enclosingName   The name of the type that owns this method.
     *  @param  implicitModifiers   The implicit modifiers for this method.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( {"OptionalUsedAsFieldOrParameterType", "OverlyComplexMethod"} )
    private final void emit4JavaPoet( @SuppressWarnings( "UseOfConcreteClass" ) final CodeWriter codeWriter, final Optional<String> enclosingName, final Collection<Modifier> implicitModifiers ) throws UncheckedIOException
    {
        emitJavadoc( codeWriter );
        codeWriter.emitAnnotations( m_Annotations, false );
        codeWriter.emitModifiers( m_Modifiers, implicitModifiers );

        if( !m_TypeVariables.isEmpty() )
        {
            codeWriter.emitTypeVariables( m_TypeVariables );
            codeWriter.emit( " " );
        }

        if( isConstructor() )
        {
            /*
             * Anonymous types do not have a constructor, therefore there is
             * no check required for the name.
             */
            //noinspection OptionalGetWithoutIsPresent
            codeWriter.emit( "$L($Z", enclosingName.get() );
        }
        else
        {
            codeWriter.emit( "$T $L($Z", m_ReturnType, m_Name );
        }

        var firstParameter = true;
        for( final var iterator = m_Parameters.iterator(); iterator.hasNext(); )
        {
            final var parameter = iterator.next();
            if( !firstParameter ) codeWriter.emit( "," ).emitWrappingSpace();
            parameter.emit( codeWriter, !iterator.hasNext() && m_Varargs );
            firstParameter = false;
        }

        codeWriter.emit( ")" );

        if( m_DefaultValue.isPresent() && !m_DefaultValue.get().isEmpty() )
        {
            codeWriter.emit( " default " );
            codeWriter.emit( m_DefaultValue.get() );
        }

        if( !m_Exceptions.isEmpty() )
        {
            codeWriter.emitWrappingSpace().emit( "throws" );
            var firstException = true;
            for( final var exception : m_Exceptions )
            {
                if( !firstException ) codeWriter.emit( "," );
                codeWriter.emitWrappingSpace().emit( "$T", exception );
                firstException = false;
            }
        }

        if( hasModifier( ABSTRACT ) )
        {
            codeWriter.emit( ";\n" );
        }
        else if( hasModifier( NATIVE ) )
        {
            //---* Code is allowed to support stuff like GWT JSNI *------------
            codeWriter.emit( m_Code );
            codeWriter.emit( ";\n" );
        }
        else
        {
            codeWriter.emit( " {\n" );

            codeWriter.indent();
            codeWriter.emit( m_Code );
            codeWriter.unindent();

            codeWriter.emit( "}\n" );
        }
    }   //  emit4JavaPoet()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = this == o;
        if( !retValue && (o instanceof final MethodSpecImpl other) )
        {
            retValue = m_Composer.equals( other.m_Composer ) && toString().equals( o.toString() );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  equals()

    /**
     *  Returns the declared exceptions for this method.
     *
     *  @return The declared exceptions.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final List<TypeNameImpl> exceptions() { return m_Exceptions; }

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
            emit( codeWriter, Optional.of( "Constructor" ), Set.of() );
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
    public final boolean isConstructor() { return m_Name.equals( CONSTRUCTOR ); }

    /**
     *  Checks whether the last entry of the given parameter list is an array.
     *
     *  @param  parameters  The parameter list.
     *  @return {@code true} if the last entry of the given parameter list is
     *      an array type, {@code false} if not.
     */
    @SuppressWarnings( "BooleanMethodNameMustStartWithQuestion" )
    private static final boolean lastParameterIsArray( final SequencedCollection<ParameterSpecImpl> parameters )
    {
        final var retValue = !parameters.isEmpty() && TypeName.asArray( (parameters.getLast().type() ) ).isPresent();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  lastParameterIsArray()

    /**
     *  Returns a builder for a regular method.
     *
     *  @param  name    The name for the method.
     *  @return The builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final BuilderImpl methodBuilder( final CharSequence name )
    {
        final var retValue = new BuilderImpl( new JavaComposer(), name );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  methodBuilder()

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
     *  <p>{@summary Returns a new method spec builder for a method that
     *  overrides the given method.}</p>
     *  <p>This new builder will copy visibility modifiers, type parameters,
     *  return type, name, parameters, and throws declarations. An
     *  {@link Override}
     *  annotation will be added.</p>
     *
     *  @note   In JavaPoet&nbsp;1.2 through 1.7 this method retained
     *      annotations from the method and parameters of the overridden
     *      method. Since JavaPoet&nbsp;1.8 and in JavaComposer annotations
     *      must be added separately.
     *
     *  @param  method  The method to override.
     *  @return The builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final BuilderImpl overriding( final ExecutableElement method )
    {
        final var composer = new JavaComposer();
        final var retValue = (BuilderImpl) composer.overridingMethodBuilder( method );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  overriding()

    /**
     *  <p>{@summary Returns a new method spec builder that overrides the given
     *  method as a member of of the given enclosing class.} This will resolve
     *  type parameters: for example overriding
     *  {@link Comparable#compareTo}
     *  in a type that implements {@code Comparable<Movie>}, the {@code T}
     *  parameter will be resolved to {@code Movie}.</p>
     *  <p>This will copy its visibility modifiers, type parameters, return
     *  type, name, parameters, and throws declarations. An
     *  {@link Override}
     *  annotation will be added.</p>
     *
     *  @note   In JavaPoet&nbsp;1.2 through 1.7 this method retained
     *      annotations from the method and parameters of the overridden
     *      method. Since JavaPoet&nbsp;1.8 and in JavaComposer annotations
     *      must be added separately.
     *
     *  @param  method  The method to override.
     *  @param  enclosing   The enclosing class for the method.
     *  @param  types   The type variables.
     *  @return The builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final BuilderImpl overriding( final ExecutableElement method, final DeclaredType enclosing, final Types types )
    {
        final var composer = new JavaComposer();
        final var retValue = (BuilderImpl) composer.overridingMethodBuilder( method, enclosing, types );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  overriding()

    /**
     *  <p>{@summary Returns a new method spec builder for a method that
     *  overrides the given method.}</p>
     *  <p>This new builder will copy visibility modifiers, type parameters,
     *  return type, name, parameters, and throws declarations. An
     *  {@link Override}
     *  annotation will be added, but any other annotation will be omitted;
     *  this is consistent with the behaviour of
     *  {@link #overriding(ExecutableElement)}
     *  and
     *  {@link #overriding(ExecutableElement, DeclaredType, Types)}.</p>
     *
     *  @param  method  The method to override.
     *  @return The builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.8" )
    public static final BuilderImpl overriding( final Method method )
    {
        final var composer = new JavaComposer();
        final var retValue = (BuilderImpl) composer.overridingMethodBuilder( method );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  overriding()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Collection<ParameterSpec> parameters() { return List.copyOf( m_Parameters ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final TypeName returnType()
    {
        final var retValue = nonNull( m_ReturnType ) ? m_ReturnType : VOID_PRIMITIVE;

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  returnType()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String signature()
    {
        final var joiner = new StringJoiner( ", ", format( "%s( ", name() ), " )" );
        joiner.setEmptyValue( format( "%s()", name() ) );
        m_Parameters.stream()
            .map( parameter -> parameter.type().toString() )
            .forEach( joiner::add );
        final var retValue = joiner.toString();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  signature()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( {"AccessingNonPublicFieldOfAnotherObject"} )
    @Override
    public final BuilderImpl toBuilder( final boolean omitCode )
    {
        final var retValue = new BuilderImpl( m_Composer, m_Name );
        retValue.m_Javadoc.addWithoutDebugInfo( m_Javadoc );
        retValue.m_Annotations.addAll( m_Annotations );
        retValue.m_Modifiers.addAll( m_Modifiers );
        if( omitCode ) retValue.m_Modifiers.add( ABSTRACT );
        retValue.m_TypeVariables.addAll( m_TypeVariables );
        retValue.m_ReturnType = m_ReturnType;
        retValue.m_ReturnComment.addWithoutDebugInfo( m_ReturnComment );
        retValue.m_Parameters.addAll( m_Parameters );
        retValue.m_Exceptions.addAll( m_Exceptions );
        if( !omitCode ) retValue.m_Code.addWithoutDebugInfo( m_Code );
        retValue.m_Varargs = m_Varargs;
        retValue.m_DefaultValue = m_DefaultValue.orElse( null );
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
//  class MethodSpecImpl

/*
 *  End of File
 */