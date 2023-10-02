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

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.CLASS_WITH_TOO_MANY_FIELDS;
import static org.tquadrat.foundation.javacomposer.internal.ClassNameImpl.OBJECT;
import static org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl.Kind.ANNOTATION;
import static org.tquadrat.foundation.javacomposer.internal.Util.createDebugOutput;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.checkState;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidNonNullArgument;
import static org.tquadrat.foundation.util.JavaUtils.isValidName;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.MountPoint;
import org.tquadrat.foundation.exception.UnexpectedExceptionError;
import org.tquadrat.foundation.exception.UnsupportedEnumError;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.AnnotationSpec;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.MethodSpec;
import org.tquadrat.foundation.javacomposer.SuppressableWarnings;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.javacomposer.TypeVariableName;
import org.tquadrat.foundation.lang.Lazy;
import org.tquadrat.foundation.lang.Objects;

/**
 *  The abstract base class for the implementations of
 *  {@link TypeSpec}.
 *
 *  @author Square,Inc.
 *  @modified   Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TypeSpecImpl.java 1066 2023-09-28 19:51:53Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( {"ClassWithTooManyFields", "ClassWithTooManyMethods", "OverlyCoupledClass"} )
@ClassVersion( sourceVersion = "$Id: TypeSpecImpl.java 1066 2023-09-28 19:51:53Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public abstract sealed class TypeSpecImpl implements TypeSpec
    permits AnnotationTypeSpecImpl, ClassSpecImpl, EnumSpecImpl, InterfaceSpecImpl, RecordSpecImpl
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The abstract base class for the implementations of
     *  {@link org.tquadrat.foundation.javacomposer.TypeSpec.Builder}.
     *
     *  @author Square,Inc.
     *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: TypeSpecImpl.java 1066 2023-09-28 19:51:53Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @SuppressWarnings( "OverlyCoupledClass" )
    @ClassVersion( sourceVersion = "$Id: TypeSpecImpl.java 1066 2023-09-28 19:51:53Z tquadrat $" )
    @API( status = INTERNAL, since = "0.0.5" )
    public abstract static sealed class BuilderImpl implements TypeSpec.Builder
        permits AnnotationTypeSpecImpl.BuilderImpl, ClassSpecImpl.BuilderImpl, EnumSpecImpl.BuilderImpl, InterfaceSpecImpl.BuilderImpl, RecordSpecImpl.BuilderImpl
    {
            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  The annotations for the type spec.
         */
        private final Collection<AnnotationSpecImpl> m_Annotations = new ArrayList<>();

        /**
         *  The reference to the factory.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private final JavaComposer m_Composer;

        /**
         *  The attributes for this type spec.
         */
        private final Collection<FieldSpecImpl> m_FieldSpecs = new ArrayList<>();

        /**
         *  The initializer block.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private final CodeBlockImpl.BuilderImpl m_InitializerBlock;

        /**
         *  The Javadoc comment for the type.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private final CodeBlockImpl.BuilderImpl m_Javadoc;

        /**
         *  The kind of the type (enum, interface, regular class, &hellip;).
         *
         *  @see TypeSpecImpl.Kind
         */
        private final Kind m_Kind;

        /**
         *  The methods for this type.
         */
        private final Collection<MethodSpecImpl> m_MethodSpecs = new ArrayList<>();

        /**
         *  The modifiers.
         */
        private final Set<Modifier> m_Modifiers = EnumSet.noneOf( Modifier.class );

        /**
         *  The name of the type.
         */
        @SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
        private final Optional<String> m_Name;

        /**
         *  TODO Write the comment for the field m_OriginatingElements!!
         */
        private final List<Element> m_OriginatingElements = new ArrayList<>();

        /**
         *  The builder for the static block of the type.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private final CodeBlockImpl.BuilderImpl m_StaticBlock;

        /**
         *  The static imports.
         */
        private final Collection<String> m_StaticImports = new TreeSet<>();

        /**
         *  The superclass for the type.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private TypeNameImpl m_Superclass = OBJECT;

        /**
         *  The superinterfaces for the type.
         */
        private final Collection<TypeNameImpl> m_Superinterfaces = new ArrayList<>();

        /**
         *  The suppressable warnings for the new type.
         */
        private final Set<SuppressableWarnings> m_SuppressableWarnings = EnumSet.noneOf( SuppressableWarnings.class );

        /**
         *  The inner classes (embedded types) for the type.
         */
        private final Collection<TypeSpecImpl> m_TypeSpecs = new ArrayList<>();

        /**
         *  The type variables for the type.
         */
        private final Collection<TypeVariableNameImpl> m_TypeVariables = new ArrayList<>();

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code BuilderImpl} instance.
         *
         *  @param  composer    The reference to the factory that created this
         *      builder instance.
         *  @param  kind    The kind of the type to build.
         *  @param  name    The name of the type to build.
         */
        protected BuilderImpl( @SuppressWarnings( "UseOfConcreteClass" ) final JavaComposer composer, final Kind kind, final CharSequence name )
        {
            this( composer, kind, Optional.of( requireNotEmptyArgument( name, "name" ).toString() ) );
        }   //  BuilderImpl()

        /**
         *  Creates a new {@code BuilderImpl} instance for an anonymous type.
         *
         *  @param  composer    The reference to the factory that created this
         *      builder instance.
         *  @param  kind    The kind of the type to build.
         */
        protected BuilderImpl( @SuppressWarnings( "UseOfConcreteClass" ) final JavaComposer composer, final Kind kind )
        {
            this( composer, kind, Optional.empty() );
        }   //  BuilderImpl()

        /**
         *  Creates a new {@code BuilderImpl} instance.
         *
         *  @param  composer    The reference to the factory that created this
         *      builder instance.
         *  @param  kind    The kind of the type to build.
         *  @param  name    The name of the type to build.
         */
        @SuppressWarnings( {"OptionalUsedAsFieldOrParameterType", "OptionalGetWithoutIsPresent"} )
        protected BuilderImpl( @SuppressWarnings( "UseOfConcreteClass" ) final JavaComposer composer, final Kind kind, final Optional<String> name )
        {
            m_Composer = requireNonNullArgument( composer, "composer" );
            m_Kind = requireNonNullArgument( kind, "kind" );
            m_Name = requireValidNonNullArgument( name, "name", v -> v.isEmpty() || isValidName( v.get() ), $ -> "not a valid name: %s".formatted( name.get() ) );

            m_InitializerBlock = (CodeBlockImpl.BuilderImpl) m_Composer.codeBlockBuilder();
            m_Javadoc = (CodeBlockImpl.BuilderImpl) m_Composer.codeBlockBuilder();
            m_StaticBlock = (CodeBlockImpl.BuilderImpl) m_Composer.codeBlockBuilder();
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
            final var annotationSpecImpl = (AnnotationSpecImpl) requireNonNullArgument( annotationSpec, "annotationSpec" );
            m_Annotations.add( annotationSpecImpl );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addAnnotation()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addAnnotation( final Class<?> annotation )
        {
            return addAnnotation( ClassNameImpl.from( annotation ) );
        }   //  addAnnotation()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addAnnotation( final ClassName annotation )
        {
            return addAnnotation( m_Composer.annotationBuilder( annotation ).build() );
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
        @API( status = STABLE, since = "0.2.0" )
        @Override
        public abstract BuilderImpl addAttribute( final FieldSpec fieldSpec, final boolean readOnly );

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addEnumConstant( final CharSequence name )
        {
            return addEnumConstant( name, anonymousClassBuilder( EMPTY_STRING ).build() );
        }   //  addEnumConstant()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addEnumConstant( final CharSequence name, final CodeBlock javaDoc )
        {
            final var anonymousClass = anonymousClassBuilder( EMPTY_STRING )
                .addJavadoc( requireNonNullArgument( javaDoc, "javaDoc" ) )
                .build();
            addEnumConstant( name, anonymousClass );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addEnumConstant()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addEnumConstant( final CharSequence name, final String format, final Object... args )
        {
            final var anonymousClass = anonymousClassBuilder( EMPTY_STRING )
                .addJavadoc( format, args )
                .build();
            addEnumConstant( name, anonymousClass );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addEnumConstant()

        /**
         *  {@inheritDoc}
         */
        @Override
        public BuilderImpl addEnumConstant( final CharSequence name, final TypeSpec typeSpec )
        {
            throw new IllegalStateException( "%s is not enum".formatted( m_Name.orElse( NAME_ANONYMOUS_TYPE ) ) );
        }   //  addEnumConstant()

        /**
         *  {@inheritDoc}
         */
        @Override
        public BuilderImpl addField( final FieldSpec fieldSpec )
        {
            final var fieldSpecImpl = (FieldSpecImpl) requireNonNullArgument( fieldSpec, "fieldSpec" );
            if( composer().addDebugOutput() )
            {
                final var builder = fieldSpecImpl.toBuilder();
                createDebugOutput( true ).ifPresent( debug -> builder.addJavadoc( "\n$L\n", debug.asLiteral() ) );
                m_FieldSpecs.add( builder.build() );
            }
            else
            {
                m_FieldSpecs.add( fieldSpecImpl );
            }

            final var maxFields = composer().getMaxFields();
            if( (maxFields > 0) && (m_FieldSpecs.size() >= maxFields) )
            {
                addSuppressableWarning( CLASS_WITH_TOO_MANY_FIELDS );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addField()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addField( final Type type, final CharSequence name, final Modifier... modifiers )
        {
            return addField( TypeNameImpl.from( type ), name, modifiers );
        }   //  addField()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addField( final TypeName type, final CharSequence name, final Modifier... modifiers )
        {
            return addField( m_Composer.fieldBuilder( type, name, modifiers ).build() );
        }   //  addField()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addFields( final Iterable<? extends FieldSpec> fieldSpecs )
        {
            for( final var fieldSpec : requireNonNullArgument( fieldSpecs, "fieldSpecs" ) )
            {
                addField( fieldSpec );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addFields()

        /**
         *  {@inheritDoc}
         */
        @Override
        public BuilderImpl addInitializerBlock( final CodeBlock block )
        {
            m_InitializerBlock.beginControlFlow( EMPTY_STRING )
                .add( requireNonNullArgument( block, "block" ) )
                .endControlFlow();

            //---* Done *------------------------------------------------------
            return this;
        }   //  addInitializerBlock()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addJavadoc( final CodeBlock block )
        {
            createDebugOutput( m_Composer.addDebugOutput() )
                .ifPresent( debug -> m_Javadoc.addWithoutDebugInfo( debug.asLiteral() ) );
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
            createDebugOutput( m_Composer.addDebugOutput() )
                .ifPresent( debug -> m_Javadoc.addWithoutDebugInfo( debug.asLiteral() ) );
            m_Javadoc.addWithoutDebugInfo( format, args );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addJavadoc()

        /**
         *  {@inheritDoc}
         */
        @Override
        public abstract BuilderImpl addMethod( final MethodSpec methodSpec );

        /**
         *  {@inheritDoc}
         */
        @Override
        @SuppressWarnings( "BoundedWildcard" )
        public final BuilderImpl addMethods( final Iterable<MethodSpec> methodSpecs )
        {
            for( final var methodSpec : requireNonNullArgument( methodSpecs, "methodSpecs" ) )
            {
                addMethod( methodSpec );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addMethods()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addModifiers( final Modifier... modifiers )
        {
            checkState( !isAnonymousClass(), () -> new IllegalStateException( "forbidden on anonymous types." ) );
            for( final var modifier : requireNonNullArgument( modifiers, "modifiers" ) )
            {
                checkState( nonNull( modifier ), () -> new ValidationException( "modifiers contain null" ) );
                m_Modifiers.add( modifier );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addModifiers()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addOriginatingElement( final Element originatingElement )
        {
            m_OriginatingElements.add( originatingElement );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addOriginatingElement()

        /**
         *  {@inheritDoc}
         */
        @Override
        public abstract Builder addProperty( final FieldSpec fieldSpec, final boolean readOnly );

        /**
         *  {@inheritDoc}
         */
        @Override
        public BuilderImpl addStaticBlock( final CodeBlock block )
        {
            m_StaticBlock.beginControlFlow( "static" )
                .add( block )
                .endControlFlow();

            //---* Done *------------------------------------------------------
            return this;
        }   //  addStaticBlock()

        /**
         *  {@inheritDoc}
         */
        @API( status = STABLE, since = "0.2.0" )
        @Override
        public final BuilderImpl addStaticImport( final Class<?> clazz, final String... names )
        {
            return addStaticImport( ClassNameImpl.from( clazz ), names );
        }   //  addStaticImport()

        /**
         *  {@inheritDoc}
         */
        @API( status = STABLE, since = "0.2.0" )
        @Override
        public final BuilderImpl addStaticImport( final ClassName className, final String... names )
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
        public final BuilderImpl addStaticImport( final Enum<?> constant )
        {
            return addStaticImport( ClassNameImpl.from( requireNonNullArgument( constant, "constant" ).getDeclaringClass() ), constant.name() );
        }   //  addStaticImport()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addSuperinterface( final Class<?> superinterface )
        {
            addSuperinterface( TypeName.from( requireValidNonNullArgument( superinterface, "superinterface", Class::isInterface, $ -> "'%s' is not an interface".formatted( superinterface.getName() ) ) ) );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addSuperinterface()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addSuperinterface( final Type superinterface )
        {
            return addSuperinterface( TypeName.from( superinterface ) );
        }   //  addSuperinterface()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addSuperinterface( final TypeElement superinterface )
        {
            addSuperinterface( TypeName.from( requireValidNonNullArgument( superinterface, "superinterface", v -> v.getKind() == ElementKind.INTERFACE, $ -> "'%s' is not an interface".formatted( superinterface.getQualifiedName() ) ).asType() ) );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addSuperinterface()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addSuperinterface( final TypeName superinterface )
        {
            checkState( m_Kind != ANNOTATION, () -> new IllegalStateException( "Annotations do not implement interfaces: %s".formatted( getName() ) ) );
            m_Superinterfaces.add( (TypeNameImpl) requireNonNullArgument( superinterface, "superinterface" ) );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addSuperinterface()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addSuperinterfaces( final Iterable<? extends TypeName> superinterfaces )
        {
            for( final TypeName superinterface : requireNonNullArgument( superinterfaces, "superinterfaces" ) )
            {
                addSuperinterface( superinterface );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addSuperinterfaces()

        /**
         *  {@inheritDoc}
         */
        @Override
        public Builder addSuppressableWarning( final SuppressableWarnings... warnings )
        {
            if( nonNull( warnings ) )
            {
                m_SuppressableWarnings.addAll( asList( warnings ) );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addSuppressableWarning()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addType( final TypeSpec typeSpec )
        {
            final var typeSpecImpl = (TypeSpecImpl) requireNonNullArgument( typeSpec, "typeSpec" );
            checkState( typeSpecImpl.modifiers().containsAll( m_Kind.implicitTypeModifiers() ), () -> new ValidationException( "%s %s.%s requires modifiers %s".formatted( m_Kind, m_Name.orElse( NAME_ANONYMOUS_TYPE ), typeSpecImpl.name().orElse( NAME_ANONYMOUS_TYPE ), m_Kind.implicitTypeModifiers() ) ) );
            if( composer().addDebugOutput() )
            {
                final var builder = typeSpecImpl.toBuilder();
                createDebugOutput( true ).ifPresent( debug -> builder.addJavadoc( "\n$L\n", debug.asLiteral() ) );
                m_TypeSpecs.add( builder.build() );
            }
            else
            {
                m_TypeSpecs.add( typeSpecImpl );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addType()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addTypes( final Iterable<? extends TypeSpec> typeSpecs )
        {
            for( final var typeSpec : requireNonNullArgument( typeSpecs, "typeSpecs" ) )
            {
                addType( typeSpec );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addTypes()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addTypeVariable( final TypeVariableName typeVariable )
        {
            checkState( !isAnonymousClass(), () -> new IllegalStateException( "forbidden on anonymous types." ) );
            m_TypeVariables.add( (TypeVariableNameImpl) requireNonNullArgument( typeVariable, "typeVariable" ) );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addTypeVariable()

        /**
         *  {@inheritDoc}
         */
        @API( status = STABLE, since = "0.2.0" )
        @Override
        public final BuilderImpl addTypeVariable( final TypeVariableName typeVariable, final CharSequence javadoc )
        {
            final var retValue = addTypeVariable( typeVariable );
            createDebugOutput( m_Composer.addDebugOutput() )
                .ifPresent( v -> m_Javadoc.addWithoutDebugInfo( v.asLiteral() ) );
            m_Javadoc.addWithoutDebugInfo(
                """
                \n@param <$L> $S
                """, typeVariable, requireNotEmptyArgument( javadoc, "javadoc" ) );

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  addTypeVariable()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addTypeVariables( final Iterable<TypeVariableName> typeVariables )
        {
            requireNonNullArgument( typeVariables, "typeVariables" );
            checkState( !isAnonymousClass(), () -> new IllegalStateException( "forbidden on anonymous types." ) );
            for( final var typeVariable : typeVariables )
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
        public abstract TypeSpecImpl build();

        /**
         *  Returns the reference to the factory.
         *
         *  @return The factory reference.
         */
        protected final JavaComposer composer() { return m_Composer; }

        /**
         *  Provides access to the list of annotations for the new type.
         *
         *  @return The references to the annotations list.
         */
        @SuppressWarnings( "AssignmentOrReturnOfFieldWithMutableType" )
        protected final Collection<AnnotationSpecImpl> getAnnotations() { return m_Annotations; }

        /**
         *  Returns the anonymous type arguments for the new type.
         *
         *  @return An instance of
         *      {@link Optional}
         *      that holds the codeblock with the anonymous type arguments.
         */
        protected Optional<CodeBlockImpl> getAnonymousTypeArguments() { return Optional.empty(); }

        /**
         *  Returns the {@code enum} constants of the new type.
         *
         *  @return The {@code enum} constants.
         */
        protected Map<String,ClassSpecImpl> getEnumConstants() { return Map.of(); }

        /**
         *  Returns the factory for the JavaComposer artifacts.
         *
         *  @return The factory.
         */
        protected final JavaComposer getFactory() { return m_Composer; }

        /**
         *  Returns the fields for the new type.
         *
         *  @return The fields.
         */
        @SuppressWarnings( "AssignmentOrReturnOfFieldWithMutableType" )
        protected final Collection<FieldSpecImpl> getFieldSpecs() { return m_FieldSpecs; }

        /**
         *  Provides access to the
         *  {@linkplain CodeBlockImpl.BuilderImpl builder}
         *  for the initializer block of the new type.
         *
         *  @return The builder reference.
         */
        protected final CodeBlockImpl.BuilderImpl getInitializerBlock() { return m_InitializerBlock; }

        /**
         *  Provides access to the
         *  {@linkplain CodeBlockImpl.BuilderImpl builder}
         *  for the Javadoc of the new type.
         *
         *  @return The builder reference.
         */
        protected final CodeBlockImpl.BuilderImpl getJavadoc() { return m_Javadoc; }

        /**
         *  Returns the methods for the new type.
         *
         *  @return The methods.
         */
        @SuppressWarnings( "AssignmentOrReturnOfFieldWithMutableType" )
        protected final Collection<MethodSpecImpl> getMethodSpecs() { return m_MethodSpecs; }

        /**
         *  Returns the modifiers for the new type.
         *
         *  @return The modifiers.
         */
        @SuppressWarnings( "AssignmentOrReturnOfFieldWithMutableType" )
        protected final Set<Modifier> getModifiers() { return m_Modifiers; }

        /**
         *  Returns the name of the new type.
         *
         *  @return An instance of
         *      {@link Optional}
         *      that holds the name.
         */
        protected final Optional<String> getName() { return m_Name; }

        /**
         *  Provides access to the
         *  {@linkplain CodeBlockImpl.BuilderImpl builder}
         *  for the static initializer block of the new type.
         *
         *  @return The builder reference.
         */
        protected final CodeBlockImpl.BuilderImpl getStaticBlock() { return m_StaticBlock; }

        /**
         *  Provides access to the static imports.
         *
         *  @return The reference to the list with the static imports.
         */
        @SuppressWarnings( "AssignmentOrReturnOfFieldWithMutableType" )
        protected final Collection<String> getStaticImports() { return m_StaticImports; }

        /**
         *  Returns the superclass for the new type.
         *
         *  @return The superclass.
         */
        protected final TypeNameImpl getSuperclass() { return m_Superclass; }

        /**
         *  Returns the superinterfaces of the new type.
         *
         *  @return The superinterfaces.
         */
        @SuppressWarnings( "AssignmentOrReturnOfFieldWithMutableType" )
        protected final Collection<TypeNameImpl> getSuperinterfaces() { return m_Superinterfaces; }

        /**
         *  Return the inner classes for the new type.
         *
         *  @return The inner classes.
         */
        @SuppressWarnings( "AssignmentOrReturnOfFieldWithMutableType" )
        protected final Collection<TypeSpecImpl> getTypeSpecs() { return m_TypeSpecs; }

        /**
         *  Returns the type variables for the new type.
         *
         *  @return The type variables.
         */
        @SuppressWarnings( "AssignmentOrReturnOfFieldWithMutableType" )
        protected final Collection<TypeVariableNameImpl> getTypeVariables() { return m_TypeVariables; }

        /**
         *  Returns a flag whether this class is an anonymous class or not.
         *
         *  @return {@code true} if the class is an anonymous class,
         *      {@code false} if it is named.
         */
        @SuppressWarnings( {"PublicMethodNotExposedInInterface", "BooleanMethodIsAlwaysInverted"} )
        @MountPoint
        public boolean isAnonymousClass() { return false; }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl superclass( final Type superclass )
        {
            return superclass( TypeName.from( superclass ) );
        }   //  superclass()

        /**
         *  {@inheritDoc}
         */
        @Override
        public BuilderImpl superclass( final TypeName superclass )
        {
            checkState( m_Superclass == OBJECT, () -> new IllegalStateException( "superclass already set to %s".formatted( m_Superclass.toString() ) ));
            m_Superclass = (TypeNameImpl) requireValidNonNullArgument( superclass, "superclass", v -> !v.isPrimitive(), $ -> "superclass may not be a primitive" );

            //---* Done *------------------------------------------------------
            return this;
        }   //  superclass()
    }
    //  class BuilderImpl

    /**
     *  The kind of type specified by a
     *  {@link TypeSpecImpl}
     *  instance.
     *
     *  @author Square,Inc.
     *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: TypeSpecImpl.java 1066 2023-09-28 19:51:53Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @SuppressWarnings( "NewClassNamingConvention" )
    @ClassVersion( sourceVersion = "$Id: TypeSpecImpl.java 1066 2023-09-28 19:51:53Z tquadrat $" )
    @API( status = INTERNAL, since = "0.0.5" )
    public enum Kind
    {
            /*------------------*\
        ====** Enum Declaration **=============================================
            \*------------------*/
        /**
         *  The type is an annotation.
         */
        ANNOTATION( EnumSet.of( PUBLIC, STATIC, FINAL ), EnumSet.of( PUBLIC, ABSTRACT ), EnumSet.of( PUBLIC, STATIC ), EnumSet.of( STATIC ) ),

        /**
         *  The type is a regular class.
         */
        CLASS( EnumSet.noneOf( Modifier.class ), EnumSet.noneOf( Modifier.class ), EnumSet.noneOf( Modifier.class ), EnumSet.noneOf( Modifier.class ) ),

        /**
         *  The type is an {@code enum}.
         */
        ENUM( EnumSet.noneOf( Modifier.class ), EnumSet.noneOf( Modifier.class ), EnumSet.noneOf( Modifier.class ), EnumSet.of( STATIC ) ),

        /**
         *  The type is an interface.
         */
        INTERFACE( EnumSet.of( PUBLIC, STATIC, FINAL ), EnumSet.of( PUBLIC, ABSTRACT ), EnumSet.of( PUBLIC, STATIC ), EnumSet.of( STATIC ) ),

        /**
         *  The type is an interface.
         */
        RECORD( EnumSet.of( PRIVATE ), EnumSet.noneOf( Modifier.class ), EnumSet.of( FINAL ), EnumSet.of( STATIC ) );

            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  TODO Write the comment for the field m_AsMemberModifiers!!
         */
        private final Set<Modifier> m_AsMemberModifiers;

        /**
         *  The implicit field modifiers for a type of this kind.
         */
        private final Set<Modifier> m_ImplicitFieldModifiers;

        /**
         *  The implicit method modifiers for a type of this kind.
         */
        private final Set<Modifier> m_ImplicitMethodModifiers;

        /**
         *  The implicit type modifiers for a type of this kind.
         */
        private final Set<Modifier> m_ImplicitTypeModifiers;

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code Kind} instance.
         *
         *  @param  implicitFieldModifiers  The implicit field modifiers for a
         *      type of this kind.
         *  @param  implicitMethodModifiers The implicit method modifiers for a
         *      type of this kind.
         *  @param  implicitTypeModifiers   The implicit type modifiers for a
         *      type of this kind.
         *  @param  asMemberModifiers   The member modifiers for a type of this
         *      kind.
         */
        private Kind( final Collection<Modifier> implicitFieldModifiers, final Collection<Modifier> implicitMethodModifiers, final Collection<Modifier> implicitTypeModifiers, final Collection<Modifier> asMemberModifiers )
        {
            m_ImplicitFieldModifiers = Set.copyOf( implicitFieldModifiers );
            m_ImplicitMethodModifiers = Set.copyOf( implicitMethodModifiers );
            m_ImplicitTypeModifiers = Set.copyOf( implicitTypeModifiers );
            m_AsMemberModifiers = Set.copyOf( asMemberModifiers );
        }   //  Kind()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  TODO Write the comment for method Kind.asMemberModifiers()!!
         *
         *  @return ?
         */
        public final Set<Modifier> asMemberModifiers()
        {
            return m_AsMemberModifiers;
        }   //  asMemberModifiers()

        /**
         *  Returns the implicit field modifiers for this kind.
         *
         *  @return The implicit filed modifiers for this kind.
         */
        public final Set<Modifier> implicitFieldModifiers()
        {
            return m_ImplicitFieldModifiers;
        }   //  implicitFieldModifiers()

        /**
         *  Returns the implicit method modifiers for this kind.
         *
         *  @return The implicit method modifiers for this kind.
         */
        public final Set<Modifier> implicitMethodModifiers()
        {
            return m_ImplicitMethodModifiers;
        }   //  implicitMethodModifiers()

        /**
         *  Returns the implicit type modifiers for this kind.
         *
         *  @return The implicit type modifiers for this kind.
         */
        public final Set<Modifier> implicitTypeModifiers()
        {
            return m_ImplicitTypeModifiers;
        }   //  implicitTypeModifiers()
    }
    //  enum Kind

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The name for an anonymous type in error messages: {@value}.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final String NAME_ANONYMOUS_TYPE = "<anonymousType>";

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The annotations for this type.
     */
    private final List<AnnotationSpecImpl> m_Annotations;

    /**
     *  Lazily initialised return value of
     *  {@link #toString()}
     *  for this code block.
     */
    private final Lazy<String> m_CachedString;

    /**
     *  The reference to the factory.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final JavaComposer m_Composer;

    /**
     *  The fields for this type.
     */
    private final List<FieldSpecImpl> m_FieldSpecs;

    /**
     *  The initializer block for this type.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final CodeBlockImpl m_InitializerBlock;

    /**
     *  The Javadoc comment for this type.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final CodeBlockImpl m_Javadoc;

    /**
     *  The kind of this type.
     */
    private final Kind m_Kind;

    /**
     *  The methods for this type.
     */
    private final List<MethodSpecImpl> m_MethodSpecs;

    /**
     *  The modifiers for this type.
     */
    private final Set<Modifier> m_Modifiers;

    /**
     *  The name of this type.
     */
    @SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
    private final Optional<String> m_Name;

    /**
     *  TODO Write the comment for the field m_OriginatingElements!!
     */
    private final List<Element> m_OriginatingElements;

    /**
     *  The static initializer block for this type.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final CodeBlockImpl m_StaticBlock;

    /**
     *  The static imports.
     */
    private final Set<String> m_StaticImports;

    /**
     *  The superclass for this type.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final TypeNameImpl m_SuperClass;

    /**
     *  The super interfaces for this type.
     */
    private final List<TypeNameImpl> m_SuperInterfaces;

    /**
     *  The suppressable warnings for the new type.
     */
    private final Set<SuppressableWarnings> m_SuppressableWarnings = EnumSet.noneOf( SuppressableWarnings.class );

    /**
     *  The inner types for this type.
     */
    private final List<TypeSpecImpl> m_TypeSpecs;

    /**
     *  The type variables for this type.
     */
    private final List<TypeVariableNameImpl> m_TypeVariables;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code TypeSpecImpl} instance.
     *
     *  @param  builder The builder for this instance.
     */
    @SuppressWarnings( "AccessingNonPublicFieldOfAnotherObject" )
    protected TypeSpecImpl( final BuilderImpl builder )
    {
        m_Composer = builder.m_Composer;
        m_Kind = builder.m_Kind;
        m_Name = builder.m_Name;
        m_Javadoc = builder.m_Javadoc.build();
        m_Annotations = List.copyOf( builder.m_Annotations );
        m_Modifiers = Set.copyOf( builder.m_Modifiers );
        m_TypeVariables = List.copyOf( builder.m_TypeVariables );
        m_SuperClass = builder.m_Superclass;
        m_SuperInterfaces = List.copyOf( builder.m_Superinterfaces );
        m_FieldSpecs = List.copyOf( builder.m_FieldSpecs );
        m_StaticBlock = builder.m_StaticBlock.build();
        m_InitializerBlock = builder.m_InitializerBlock.build();
        m_MethodSpecs = List.copyOf( builder.m_MethodSpecs );
        m_TypeSpecs = List.copyOf( builder.m_TypeSpecs );

        final Set<String> staticImports = new TreeSet<>( builder.m_StaticImports );
        staticImports.addAll( m_Javadoc.getStaticImports() );
        m_FieldSpecs.forEach( fspec -> staticImports.addAll( fspec.getStaticImports() ) );
        staticImports.addAll( m_StaticBlock.getStaticImports() );
        staticImports.addAll( m_InitializerBlock.getStaticImports() );
        m_MethodSpecs.forEach( mspec -> staticImports.addAll( mspec.getStaticImports() ) );
        m_TypeSpecs.forEach( tspec -> staticImports.addAll( tspec.getStaticImports() ) );
        m_StaticImports = Set.copyOf( staticImports );

        final Collection<Element> originatingElementsMutable = new ArrayList<>( builder.m_OriginatingElements );
        for( final var typeSpec : builder.m_TypeSpecs )
        {
            originatingElementsMutable.addAll( typeSpec.m_OriginatingElements );
        }
        m_OriginatingElements = List.copyOf( originatingElementsMutable );

        m_SuppressableWarnings.addAll( builder.m_SuppressableWarnings );

        m_CachedString = Lazy.use( this::initializeCachedString );
    }   //  TypeSpecImpl()

    /**
     *  Creates a dummy type spec for type-resolution in CodeWriter only while
     *  emitting the type declaration but before entering the type body.
     *
     *  @param  type    The source type.
     */
    protected TypeSpecImpl( final TypeSpecImpl type )
    {
        m_Composer = type.m_Composer;
        m_Kind = type.m_Kind;
        m_Name = type.m_Name;
        m_Javadoc = type.m_Javadoc;
        m_Annotations = List.of();
        m_Modifiers = Set.of();
        m_TypeVariables = List.of();
        m_SuperClass = null;
        m_SuperInterfaces = List.of();
        m_FieldSpecs = List.of();
        m_StaticBlock = type.m_StaticBlock;
        m_InitializerBlock = type.m_InitializerBlock;
        m_MethodSpecs = List.of();
        m_TypeSpecs = List.of();
        m_OriginatingElements = List.of();
        m_StaticImports = type.m_StaticImports;

        m_CachedString = Lazy.use( this::initializeCachedString );
    }   //  TypeSpecImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a builder for an annotation (a type with the kind
     *  {@link TypeSpecImpl.Kind#ANNOTATION}).
     *
     *  @param  className   The name of the annotation.
     *  @return The builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    public static final BuilderImpl annotationBuilder( final ClassName className )
    {
        final var retValue = annotationBuilder( requireNonNullArgument( className, "className" ).simpleName() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  annotationBuilder()

    /**
     *  Creates a builder for an annotation (a type with the kind
     *  {@link TypeSpecImpl.Kind#ANNOTATION}).
     *
     *  @param  name   The name of the annotation.
     *  @return The builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    public static final BuilderImpl annotationBuilder( final CharSequence name )
    {
        final var retValue = new AnnotationTypeSpecImpl.BuilderImpl( new JavaComposer(), requireNonNullArgument( name, "name" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  annotationBuilder()

    /**
     *  Creates a builder for an anonymous class.
     *
     *  @param  typeArguments   The type arguments.
     *  @return The builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    public static final BuilderImpl anonymousClassBuilder( final CodeBlock typeArguments )
    {
        final var retValue = new ClassSpecImpl.BuilderImpl( new JavaComposer(), (CodeBlockImpl) requireNonNullArgument( typeArguments, "typeArguments" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  anonymousClassBuilder()

    /**
     *  Creates a builder for an anonymous class.
     *
     *  @param  format  The format.
     *  @param  args    The arguments.
     *  @return The builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @SuppressWarnings( "DeprecatedIsStillUsed" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    public static final BuilderImpl anonymousClassBuilder( final String format, final Object... args )
    {
        final var composer = new JavaComposer();
        final var retValue = anonymousClassBuilder( composer.codeBlockBuilder()
            .add( format, args )
            .build() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  anonymousClassBuilder()

    /**
     *  Returns the anonymous type arguments.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the anonymous type arguments.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public Optional<CodeBlock> anonymousTypeArguments() { return Optional.empty(); }

    /**
     *  Creates a builder for a regular class (a type with the kind
     *  {@link TypeSpecImpl.Kind#CLASS}).
     *
     *  @param  className   The name of the class.
     *  @return The builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    public static final BuilderImpl classBuilder( final ClassName className )
    {
        final var retValue = classBuilder( requireNonNullArgument( className, "className" ).simpleName() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  classBuilder()

    /**
     *  Creates a builder for a regular class (a type with the kind
     *  {@link TypeSpecImpl.Kind#CLASS}).
     *
     *  @param  name    The name of the class.
     *  @return The builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    public static final BuilderImpl classBuilder( final CharSequence name )
    {
        final var retValue = new ClassSpecImpl.BuilderImpl( new JavaComposer(), requireNotEmptyArgument( name, "name" ), null );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  classBuilder()

    /**
     *  Compares two instances of
     *  {@link MethodSpec}
     *  for their sort order.
     *
     *  @param  m1  The first method spec.
     *  @param  m2  The second method spec.
     *  @return -1 if the first method spec will be sorted before the second,
     *      1 if the first method spec will be sorted after the second, and
     *      0 if both are equals.
     */
    protected static final int compareMethodSpecs( final MethodSpec m1, final MethodSpec m2 )
    {
        var retValue = Integer.signum( m1.name().compareToIgnoreCase( m2.name() ) );
        if( retValue == 0 )
        {
            /*
             * MethodSpec.toString() returns the whole method, including the
             * modifiers and the return value. But for the sorting, we only
             * want to have the signature – that is the name plus the arguments
             * list.
             * We know already that the name is equal, and we know also that
             * the arguments list must be different (okay, we do NOT know that
             * for sure, but it does not make sense if not). So we can strip
             * everything before the name and compare the remainder.
             */
            final var name = m1.name();
            var s1 = m1.toString();
            var pos = s1.indexOf( name );
            s1 = s1.substring( pos );

            var s2 = m2.toString();
            pos = s2.indexOf( name );
            s2 = s2.substring( pos );

            retValue = Integer.signum( s1.compareToIgnoreCase( s2 ) );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  compareMethodSpecs()

    /**
     *  Creates a dummy copy of the type spec that is used by
     *  {@link #emit(CodeWriter, String, Set)}
     *  internally.
     *
     *  @return The dummy copy.
     */
    protected abstract TypeSpecImpl createCopy();

    /**
     *  Emit the type to the given code writer.
     *
     *  @param  codeWriter  The target code writer.
     *  @param  enumName    The name of the enum; will be {@code null} if not
     *      called to emit an enum constant.
     *  @param  implicitModifiers   The implicit modifiers.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( {"PublicMethodNotExposedInInterface"} )
    public final void emit( @SuppressWarnings( "UseOfConcreteClass" ) final CodeWriter codeWriter, final String enumName, final Set<Modifier> implicitModifiers ) throws UncheckedIOException
    {
        /*
         * Nested classes interrupt wrapped line indentation. Stash the current
         * wrapping state and put it back afterwards when this type is
         * complete.
         */
        final var previousStatementLine = codeWriter.statementLine();
        codeWriter.statementLine( -1 );

        final var layout = codeWriter.layout();

        try
        {
            switch( layout )
            {
                case LAYOUT_FOUNDATION -> emit4Foundation( codeWriter, enumName, implicitModifiers );
                case LAYOUT_DEFAULT, LAYOUT_JAVAPOET, LAYOUT_JAVAPOET_WITH_TAB -> emit4JavaPoet( codeWriter, enumName, implicitModifiers );
                default -> throw new UnsupportedEnumError( codeWriter.layout() );
            }
        }
        finally
        {
            //---* Reset the statement line *----------------------------------
            codeWriter.statementLine( previousStatementLine );
        }
    }   //  emit()

    /**
     *  Emits the type to the given code writer, using the layout as defined
     *  by the Foundation library code.
     *
     *  @param  codeWriter  The target code writer.
     *  @param  enumName    The name of the enum; can be {@code null}.
     *  @param  implicitModifiers   The implicit modifiers.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    protected abstract void emit4Foundation( @SuppressWarnings( "UseOfConcreteClass" ) final CodeWriter codeWriter, final String enumName, final Set<Modifier> implicitModifiers ) throws UncheckedIOException;

    /**
     *  Emits the type to the given code writer, using the layout as defined
     *  by the original JavaPoet code.
     *
     *  @param  codeWriter  The target code writer.
     *  @param  enumName    The name of the enum; can be {@code null}.
     *  @param  implicitModifiers   The implicit modifiers.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    protected abstract void emit4JavaPoet( @SuppressWarnings( "UseOfConcreteClass" ) final CodeWriter codeWriter, final String enumName, final Set<Modifier> implicitModifiers ) throws UncheckedIOException;

    /**
     *  Creates a builder for an {@code enum} type (a type with the kind
     *  {@link TypeSpecImpl.Kind#ENUM}).
     *
     *  @param  className   The name of the class.
     *  @return The builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    public static final BuilderImpl enumBuilder( final ClassName className )
    {
        final var retValue = enumBuilder( requireNonNullArgument( className, "className" ).simpleName() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  enumBuilder()

    /**
     *  Creates a builder for an {@code enum} type (a type with the kind
     *  {@link TypeSpecImpl.Kind#ENUM}).
     *
     *  @param  name   The name of the class.
     *  @return The builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    public static final BuilderImpl enumBuilder( final CharSequence name )
    {
        final var retValue = new EnumSpecImpl.BuilderImpl( new JavaComposer(), requireNonNullArgument( name, "name" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  enumBuilder()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "AbstractMethodOverridesAbstractMethod" )
    @Override
    public abstract boolean equals( final Object o );

    /**
     *  Returns the annotations for this type.
     *
     *  @return The annotations.
     */
    protected final Collection<AnnotationSpecImpl> getAnnotations() { return m_Annotations; }

    /**
     *  Returns the {@code enum} values for this type.
     *
     *  @return The {@code enum} values.
     */
    protected Map<String, ClassSpecImpl> getEnumConstants() { return Map.of(); }

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
     *  Returns the fields for this type.
     *
     *  @return The fields.
     */
    protected final Collection<FieldSpecImpl> getFieldSpecs() { return m_FieldSpecs; }

    /**
     *  Returns the initializer block for this type.
     *
     *  @return The initializer block.
     */
    protected final CodeBlockImpl getInitializerBlock() { return m_InitializerBlock; }

    /**
     *  Return the Javadoc for this type.
     *
     *  @return The Javadoc.
     */
    protected final CodeBlockImpl getJavadoc() { return m_Javadoc; }

    /**
     *  Returns the methods of this type.
     *
     *  @return The methods.
     */
    protected final Collection<MethodSpecImpl> getMethodSpecs() { return m_MethodSpecs; }

    /**
     *  Returns the static block for this type.
     *
     *  @return The static block.
     */
    protected final CodeBlockImpl getStaticBlock() { return m_StaticBlock; }

    /**
     *  Returns the static imports for this code block.
     *
     *  @return The static imports.
     */
    @SuppressWarnings( {"PublicMethodNotExposedInInterface", "AssignmentOrReturnOfFieldWithMutableType"} )
    @API( status = INTERNAL, since = "0.2.0" )
    public final Set<String> getStaticImports() { return m_StaticImports; }

    /**
     *  Returns the superclass for this type.
     *
     *  @return The superclass.
     */
    protected final TypeNameImpl getSuperClass() { return m_SuperClass; }

    /**
     *  Returns the superinterfaces for this type.
     *
     *  @return The superinterfaces.
     */
    protected final List<? extends TypeNameImpl> getSuperInterfaces() { return m_SuperInterfaces; }

    /**
     *  Returns the suppressable warnings that were set for this type.
     *
     *  @return The warnings; the collection can be empty, but not
     *      {@code null}.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final Collection<SuppressableWarnings> getSuppressableWarnings() { return EnumSet.copyOf( m_SuppressableWarnings ); }

    /**
     *  Returns the type variables for this type, if any.
     *
     *  @return The type variables.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final List<TypeVariableNameImpl> getTypeVariables() { return m_TypeVariables; }

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "AbstractMethodOverridesAbstractMethod" )
    @Override
    public abstract int hashCode();

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean hasModifier( final Modifier modifier )
    {
        final var retValue = m_Modifiers.contains( modifier );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  hasModifier()

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
            emit( codeWriter, null, Set.of() );
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
    public final List<TypeSpec> innerClasses() { return m_TypeSpecs.stream().map( t -> (TypeSpec) t ).collect( toList() ); }

    /**
     *  Creates a builder for an interface (a type with the kind
     *  {@link TypeSpecImpl.Kind#INTERFACE}).
     *
     *  @param  className   The name of the class.
     *  @return The builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    public static final BuilderImpl interfaceBuilder( final ClassName className )
    {
        final var retValue = interfaceBuilder( requireNonNullArgument( className, "className" ).simpleName() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  interfaceBuilder()

    /**
     *  Creates a builder for an interface (a type with the kind
     *  {@link TypeSpecImpl.Kind#INTERFACE}).
     *
     *  @param  name   The name of the class.
     *  @return The builder.
     *
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    public static BuilderImpl interfaceBuilder( final CharSequence name )
    {
        final var retValue = new InterfaceSpecImpl.BuilderImpl( new JavaComposer(), requireNotEmptyArgument( name, "name" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  interfaceBuilder()

    /**
     *  Returns the modifiers that are applied to this type spec.
     *
     *  @return The modifiers.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final Set<Modifier> modifiers() { return m_Modifiers; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<String> name() { return m_Name; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final List<Element> originatingElements() { return m_OriginatingElements; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public abstract BuilderImpl toBuilder();

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString() { return m_CachedString.get(); }

    /**
     *  Returns the inner types for this type.
     *
     *  @return The inner types.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final List<TypeSpecImpl> typeSpecs() { return m_TypeSpecs; }
}
//  class TypeSpecImpl

/*
 *  End of File
 */