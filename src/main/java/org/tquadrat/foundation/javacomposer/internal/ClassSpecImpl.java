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

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.util.Comparator.comparing;
import static java.util.Locale.ROOT;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.CLASS_WITH_TOO_MANY_METHODS;
import static org.tquadrat.foundation.javacomposer.internal.ClassNameImpl.OBJECT;
import static org.tquadrat.foundation.javacomposer.internal.TypeNameImpl.VOID_PRIMITIVE;
import static org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl.Kind.CLASS;
import static org.tquadrat.foundation.javacomposer.internal.Util.createDebugOutput;
import static org.tquadrat.foundation.javacomposer.internal.Util.union;
import static org.tquadrat.foundation.lang.Objects.checkState;
import static org.tquadrat.foundation.lang.Objects.hash;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidNonNullArgument;
import static org.tquadrat.foundation.util.StringUtils.capitalize;
import static org.tquadrat.foundation.util.StringUtils.decapitalize;
import static org.tquadrat.foundation.util.StringUtils.isEmpty;
import static org.tquadrat.foundation.util.StringUtils.isNotEmptyOrBlank;

import javax.lang.model.element.Modifier;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.MethodSpec;
import org.tquadrat.foundation.javacomposer.SuppressableWarnings;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeSpec;

/**
 *  The implementation of
 *  {@link TypeSpec}
 *  for a class.
 *
 *  @author Square,Inc.
 *  @modified   Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ClassSpecImpl.java 1062 2023-09-25 23:11:41Z tquadrat $
 *  @since 0.2.0
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: ClassSpecImpl.java 1062 2023-09-25 23:11:41Z tquadrat $" )
@API( status = INTERNAL, since = "0.2.0" )
public final class ClassSpecImpl extends TypeSpecImpl
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The implementation of
     *  {@link TypeSpec.Builder}
     *  for a class.
     *
     *  @author Square,Inc.
     *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: ClassSpecImpl.java 1062 2023-09-25 23:11:41Z tquadrat $
     *  @since 0.2.0
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: ClassSpecImpl.java 1062 2023-09-25 23:11:41Z tquadrat $" )
    @API( status = INTERNAL, since = "0.2.0" )
    public static final class BuilderImpl extends TypeSpecImpl.BuilderImpl
    {
            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  The anonymous type arguments.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private final CodeBlockImpl m_AnonymousTypeArguments;

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code BuilderImpl} instance.
         *
         *  @param  composer    The reference to the factory that created this
         *      builder instance.
         *  @param  name    The name of the type to build.
         *  @param  anonymousTypeArguments  Anonymous type arguments.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        public BuilderImpl( final JavaComposer composer, final CharSequence name, final CodeBlockImpl anonymousTypeArguments )
        {
            this( composer, Optional.of( requireNotEmptyArgument( name, "name" ).toString() ), anonymousTypeArguments );
        }   //  BuilderImpl()

        /**
         *  Creates a new {@code BuilderImpl} instance for an anonymous type.
         *
         *  @param  composer    The reference to the factory that created this
         *      builder instance.
         *  @param  anonymousTypeArguments  Anonymous type arguments.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        public BuilderImpl( final JavaComposer composer, final CodeBlockImpl anonymousTypeArguments )
        {
            this( composer, Optional.empty(), anonymousTypeArguments );
        }   //  BuilderImpl()

        /**
         *  Creates a new {@code BuilderImpl} instance.
         *
         *  @param  composer    The reference to the factory that created this
         *      builder instance.
         *  @param  name    The name of the type to build.
         *  @param  anonymousTypeArguments  Anonymous type arguments.
         */
        @SuppressWarnings( {"OptionalUsedAsFieldOrParameterType"} )
        public BuilderImpl( @SuppressWarnings( "UseOfConcreteClass" ) final JavaComposer composer, final Optional<String> name, @SuppressWarnings( "UseOfConcreteClass" ) final CodeBlockImpl anonymousTypeArguments )
        {
            super( composer, CLASS, name );
            m_AnonymousTypeArguments = anonymousTypeArguments;
        }   //  BuilderImpl()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         */
        @API( status = STABLE, since = "0.2.0" )
        @Override
        public final BuilderImpl addAttribute( final FieldSpec fieldSpec, final boolean readOnly )
        {
            final var fieldSpecImpl = (FieldSpecImpl) requireValidNonNullArgument( fieldSpec, "fieldSpec", v -> v.hasModifier( PRIVATE ), $ -> "Field %s needs to be private".formatted( fieldSpec.name() ) );
            addField( fieldSpecImpl );

            final var fieldName = fieldSpecImpl.name();
            final var propertyName = decapitalize( fieldName.startsWith( "m_" ) ? fieldName.substring( 2 )  : fieldName );

            final Set<Modifier> modifiers = EnumSet.of( PUBLIC, FINAL );
            if( fieldSpec.hasModifier( STATIC ) ) modifiers.add( STATIC );

            final var accessor = getFactory().methodBuilder( propertyName )
                .addModifiers( modifiers )
                .returns( fieldSpecImpl.type() )
                .addStatement( "return $N", fieldSpecImpl )
                .build();
            addMethod( accessor );

            if( !(readOnly || fieldSpecImpl.hasModifier( FINAL )) )
            {
                final var param = getFactory().parameterBuilder( fieldSpecImpl.type(), "value", FINAL )
                    .build();
                final var setter = getFactory().methodBuilder( propertyName )
                    .addModifiers( modifiers )
                    .addParameter( param )
                    .returns( VOID_PRIMITIVE )
                    .addStatement( "$N = $N", fieldSpecImpl, param )
                    .build();
                addMethod( setter );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addAttribute()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addMethod( final MethodSpec methodSpec )
        {
            final var methodSpecImpl = (MethodSpecImpl) requireNonNullArgument( methodSpec, "methodSpec" );
            checkState( methodSpecImpl.defaultValue().isEmpty(), () -> new IllegalStateException( "%s %s.%s cannot have a default value".formatted( CLASS, getName().orElse( NAME_ANONYMOUS_TYPE ), methodSpecImpl.name() ) ) );
            checkState( !methodSpecImpl.hasModifier( DEFAULT ), () -> new IllegalStateException( "%s %s.%s cannot be default".formatted( CLASS, getName().orElse( NAME_ANONYMOUS_TYPE ), methodSpecImpl.name() ) ) );

            final var methodSpecs = getMethodSpecs();
            if( composer().addDebugOutput() )
            {
                final var builder = methodSpecImpl.toBuilder( false );
                createDebugOutput( true ).ifPresent( debug -> builder.addJavadoc( "\n$L\n", debug.asLiteral() ) );
                methodSpecs.add( builder.build() );
            }
            else
            {
                methodSpecs.add( methodSpecImpl );
            }

            final var maxMethods = composer().getMaxMethods();
            if( (maxMethods > 0) && (methodSpecs.size() >= maxMethods) )
            {
                addSuppressableWarning( CLASS_WITH_TOO_MANY_METHODS );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addMethod()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final Builder addProperty( final FieldSpec fieldSpec, final boolean readOnly )
        {
            final var fieldSpecImpl = (FieldSpecImpl) requireValidNonNullArgument( fieldSpec, "fieldSpec", v -> v.hasModifier( PRIVATE ), $ -> "Field %s needs to be private".formatted( fieldSpec.name() ) );
            addField( fieldSpecImpl );

            final var fieldName = fieldSpecImpl.name();
            final var propertyName = fieldName.startsWith( "m_" ) ? fieldName.substring( 2 ) : capitalize( fieldName );

            final Set<Modifier> modifiers = EnumSet.of( PUBLIC, FINAL );
            if( fieldSpec.hasModifier( STATIC ) ) modifiers.add( STATIC );

            final var getter = getFactory().methodBuilder( "get" + propertyName )
                .addModifiers( modifiers )
                .returns( fieldSpecImpl.type() )
                .addStatement( "return $N", fieldSpecImpl )
                .build();
            addMethod( getter );

            if( !(readOnly || fieldSpecImpl.hasModifier( FINAL )) )
            {
                final var param = getFactory().parameterBuilder( fieldSpecImpl.type(), "value", FINAL )
                    .build();
                final var setter = getFactory().methodBuilder( "set" + propertyName )
                    .addModifiers( modifiers )
                    .addParameter( param )
                    .returns( VOID_PRIMITIVE )
                    .addStatement( "$N = $N", fieldSpecImpl, param )
                    .build();
                addMethod( setter );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addProperty()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final ClassSpecImpl build()
        {
            final var isAbstract = getModifiers().contains( ABSTRACT );

            for( final var methodSpec : getMethodSpecs() )
            {
                checkState( isAbstract || !methodSpec.hasModifier( ABSTRACT ), () -> new ValidationException( "non-abstract type %s cannot declare abstract method %s".formatted( getName().orElse( NAME_ANONYMOUS_TYPE ), methodSpec.name() ) ) );
            }

            final var superclassIsObject = getSuperclass().equals( OBJECT );
            final var interestingSupertypeCount = (superclassIsObject ? 0 : 1) + getSuperinterfaces().size();
            checkState( getAnonymousTypeArguments().isEmpty() || interestingSupertypeCount <= 1, () -> new ValidationException( "anonymous type has too many supertypes" ) );

            final var retValue = new ClassSpecImpl( this );

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  build()

        /**
         *  {@inheritDoc}
         */
        @Override
        protected final Optional<CodeBlockImpl> getAnonymousTypeArguments() { return Optional.ofNullable( m_AnonymousTypeArguments ); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final boolean isAnonymousClass() { return nonNull( m_AnonymousTypeArguments ); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl superclass( final TypeName superclass )
        {
            super.superclass( superclass );

            //---* Done *------------------------------------------------------
            return this;
        }   //  superclass()
    }
    //  class BuilderImpl

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The anonymous type arguments for this type.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final CodeBlockImpl m_AnonymousTypeArguments;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code TypeClassSpecImpl} instance.
     *
     *  @param  builder The builder for this instance.
     */
    @SuppressWarnings( {"AccessingNonPublicFieldOfAnotherObject", "UseOfConcreteClass"} )
    public ClassSpecImpl( final BuilderImpl builder )
    {
        super( builder );
        m_AnonymousTypeArguments = builder.m_AnonymousTypeArguments;
    }   //  ClassSpecImpl()

    /**
     *  Creates a dummy type spec for type-resolution in CodeWriter only while
     *  emitting the type declaration but before entering the type body.
     *
     *  @param  type    The source type.
     */
    @SuppressWarnings( {"UseOfConcreteClass"} )
    private ClassSpecImpl( final ClassSpecImpl type )
    {
        super( type );
        assert isNull( type.m_AnonymousTypeArguments ) : "Not for anonymous classes";
        m_AnonymousTypeArguments = type.m_AnonymousTypeArguments;
    }   //  ClassSpecImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<CodeBlock> anonymousTypeArguments()
    {
        return Optional.ofNullable( m_AnonymousTypeArguments );
    }   //  anonymousTypeArguments()

    /**
     *  {@inheritDoc}
     */
    @Override
    protected final TypeSpecImpl createCopy() { return new ClassSpecImpl( this ); }

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
    @SuppressWarnings( {"BoundedWildcard", "OptionalGetWithoutIsPresent", "MethodWithMultipleReturnPoints", "OverlyLongMethod", "OverlyComplexMethod"} )
    @Override
    protected final void emit4Foundation( @SuppressWarnings( "UseOfConcreteClass" ) final CodeWriter codeWriter, final String enumName, final Set<Modifier> implicitModifiers ) throws UncheckedIOException
    {
        if( isNotEmptyOrBlank( enumName ) )
        {
            //---* Emit an enum constant *-------------------------------------
            /*
             * enum constants are implemented as anonymous types so this is
             * called from an outer type spec.
             */
            codeWriter.emitJavadoc( getJavadoc() );
            final Collection<AnnotationSpecImpl> annotations = new ArrayList<>( getAnnotations() );
            getFactory().createSuppressWarningsAnnotation( getSuppressableWarnings() )
                .map( a -> (AnnotationSpecImpl) a )
                .ifPresent( annotations::add );
            codeWriter.emitAnnotations( annotations, false );
            codeWriter.emit( "$L", enumName );
            if( !m_AnonymousTypeArguments.formatParts().isEmpty() )
            {
                codeWriter.emit( "( " );
                codeWriter.emit( m_AnonymousTypeArguments );
                codeWriter.emit( " )" );
            }

            if( getFieldSpecs().isEmpty() && getMethodSpecs().isEmpty() && innerClasses().isEmpty() )
            {
                //---* Avoid unnecessary braces "{}" *-------------------------
                return;
            }

            codeWriter.emit(
                """

                {
                """ );
        }
        else if( nonNull( m_AnonymousTypeArguments ) )
        {
            //---* Emit an anonymous type that is not an enum constant *-------
            final var supertype = getSuperInterfaces().isEmpty() ? getSuperClass() : getSuperInterfaces().get( 0 );

            codeWriter.emit( "new $T( ", supertype );
            codeWriter.emit( m_AnonymousTypeArguments );
            codeWriter.emit( """
                 )
                {
                """ );
        }
        else
        {
            /*
             * Push an empty type (specifically without nested types) for
             * type-resolution.
             */
            codeWriter.pushType( createCopy() );

            codeWriter.emitJavadoc( getJavadoc() );
            final Collection<AnnotationSpecImpl> annotations = new ArrayList<>( getAnnotations() );
            getFactory().createSuppressWarningsAnnotation( getSuppressableWarnings() )
                .map( a -> (AnnotationSpecImpl) a )
                .ifPresent( annotations::add );
            codeWriter.emitAnnotations( annotations, false );
            codeWriter.emitModifiers( modifiers(), union( implicitModifiers, CLASS.asMemberModifiers() ) );
            codeWriter.emit( "$L $L", CLASS.name().toLowerCase( ROOT ), name().get() );
            codeWriter.emitTypeVariables( getTypeVariables() );

            final List<TypeNameImpl> extendsTypes = getSuperClass().equals( OBJECT ) ? List.of() : List.of( getSuperClass() );
            final var implementsTypes = getSuperInterfaces();

            if( !extendsTypes.isEmpty() )
            {
                codeWriter.emit( " extends" );
                var firstType = true;
                for( final var type : extendsTypes )
                {
                    if( !firstType ) codeWriter.emit( "," );
                    codeWriter.emit( " $T", type );
                    firstType = false;
                }
            }

            if( !implementsTypes.isEmpty() )
            {
                codeWriter.emit( " implements" );
                var firstType = true;
                for( final var type : implementsTypes )
                {
                    if( !firstType ) codeWriter.emit( "," );
                    codeWriter.emit( " $T", type );
                    firstType = false;
                }
            }

            codeWriter.popType();

            codeWriter.emit(
                """

                {
                """ );
        }

        codeWriter.pushType( this );

        //---* Emit the class body *-------------------------------------------
        codeWriter.indent();
        var firstMember = true;

        //---* Emit the inner types *------------------------------------------
        if( !innerClasses().isEmpty() )
        {
            if( !firstMember ) codeWriter.emit( "\n" );
            codeWriter.emit(
                """
                    /*---------------*\\
                ====** Inner Classes **====================================================
                    \\*---------------*/""" );
            innerClasses().stream()
                .sorted( comparing( t -> t.name().get(), CASE_INSENSITIVE_ORDER ) )
                .map( t -> (TypeSpecImpl) t)
                .forEachOrdered( t ->
                {
                    codeWriter.emit( "\n" );
                    t.emit( codeWriter, null, CLASS.implicitTypeModifiers() );
                } );
            firstMember = false;
        }

        //--- Constants and attributes *---------------------------------------
        if( !getFieldSpecs().isEmpty() )
        {
            final Collection<FieldSpecImpl> alreadyHandled = new HashSet<>();

            //---* Emit the constants *----------------------------------------
            final var constants = getFieldSpecs().stream()
                .filter( fieldSpec -> fieldSpec.hasModifier( PUBLIC ) )
                .filter( fieldSpec -> fieldSpec.hasModifier( STATIC ) )
                .filter( fieldSpec -> fieldSpec.hasModifier( FINAL ) )
                .filter( FieldSpecImpl::hasInitializer )
                .sorted( comparing( FieldSpecImpl::name, CASE_INSENSITIVE_ORDER ) )
                .toList();

            if( !constants.isEmpty() )
            {
                if( !firstMember ) codeWriter.emit( "\n" );
                codeWriter.emit(
                    """    
                        /*-----------*\\
                    ====** Constants **========================================================
                        \\*-----------*/""" );
                constants.forEach( constantSpec ->
                {
                    codeWriter.emit( "\n" );
                    constantSpec.emit( codeWriter, CLASS.implicitFieldModifiers() );
                    alreadyHandled.add( constantSpec );
                } );
                firstMember = false;
            }

            //---* Emit the attributes *---------------------------------------
            final var attributes = getFieldSpecs().stream()
                .filter( fieldSpec -> !alreadyHandled.contains( fieldSpec ) )
                .filter( fieldSpec -> !(fieldSpec.hasModifier( STATIC ) && fieldSpec.hasModifier( FINAL )) )
                .sorted( comparing( FieldSpecImpl::name, CASE_INSENSITIVE_ORDER ) )
                .toList();

            if( !attributes.isEmpty() )
            {
                if( !firstMember ) codeWriter.emit( "\n" );
                codeWriter.emit(
                    """
                        /*------------*\\
                    ====** Attributes **=======================================================
                        \\*------------*/""" );

                attributes.forEach( a ->
                {
                    codeWriter.emit( "\n" );
                    a.emit( codeWriter, CLASS.implicitFieldModifiers() );
                    alreadyHandled.add( a );
                } );
                firstMember = false;
            }

            //---* Static fields *---------------------------------------------
            final var statics = getFieldSpecs().stream()
                .filter( staticSpec -> !alreadyHandled.contains( staticSpec ) )
                .sorted( comparing( FieldSpecImpl::name, CASE_INSENSITIVE_ORDER ) )
                .toList();
            if( !statics.isEmpty() || !getStaticBlock().isEmpty() )
            {
                if( !firstMember ) codeWriter.emit( "\n" );
                codeWriter.emit(
                    """
                        /*------------------------*\\
                    ====** Static Initialisations **===========================================
                        \\*------------------------*/""" );

                statics.forEach( staticSpec ->
                {
                    codeWriter.emit( "\n" );
                    staticSpec.emit( codeWriter, CLASS.implicitFieldModifiers() );
                    alreadyHandled.add( staticSpec );
                } );

                //---* Static Block *------------------------------------------
                if( !getStaticBlock().isEmpty() )
                {
                    codeWriter.emit( "\n" );
                    codeWriter.emit( getStaticBlock() );
                }
                firstMember = false;
            }
        }

        //---* Constructors *--------------------------------------------------
        final var constructors = getMethodSpecs().stream()
            .filter( MethodSpecImpl::isConstructor )
            .sorted( comparing( MethodSpecImpl::toString, CASE_INSENSITIVE_ORDER ) )
            .toList();
        if( !getInitializerBlock().isEmpty() || !constructors.isEmpty() )
        {
            if( !firstMember ) codeWriter.emit( "\n" );
            codeWriter.emit(
                """
                    /*--------------*\\
                ====** Constructors **=====================================================
                    \\*--------------*/""" );

            //---* Initializer block *-----------------------------------------
            if( !getInitializerBlock().isEmpty() )
            {
                codeWriter.emit( "\n" );
                codeWriter.emit( getInitializerBlock() );
            }

            //---* Emit the constructors *-------------------------------------
            constructors.forEach( constructorSpec ->
            {
                codeWriter.emit( "\n" );
                constructorSpec.emit( codeWriter, name(), CLASS.implicitMethodModifiers() );
            } );
            firstMember = false;
        }

        //---* Methods (static and non-static) *-------------------------------
        final var methods = getMethodSpecs().stream()
            .filter( methodSpec -> !methodSpec.isConstructor() )
            .sorted( TypeSpecImpl::compareMethodSpecs )
            .toList();
        if( !methods.isEmpty() )
        {
            if( !firstMember ) codeWriter.emit( "\n" );
            codeWriter.emit(
                """
                    /*---------*\\
                ====** Methods **==========================================================
                    \\*---------*/""" );

            methods.forEach( methodSpec ->
            {
                codeWriter.emit( "\n" );
                methodSpec.emit( codeWriter, name(), CLASS.implicitMethodModifiers() );
            } );
        }

        codeWriter.unindent();
        codeWriter.popType();

        codeWriter.emit( "}" );
        if( isEmpty( enumName ) && isNull( m_AnonymousTypeArguments ) )
        {
            codeWriter.emit( "\n//  $L $N", CLASS.name().toLowerCase( ROOT ), this );

            /*
             * If this type isn't also a value, include a trailing newline.
             */
            codeWriter.emit( "\n" );
        }
    }   //  emit4Foundation()

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
    @SuppressWarnings( {"BoundedWildcard", "OptionalGetWithoutIsPresent", "MethodWithMultipleReturnPoints", "OverlyLongMethod", "OverlyComplexMethod"} )
    @Override
    protected final void emit4JavaPoet( @SuppressWarnings( "UseOfConcreteClass" ) final CodeWriter codeWriter, final String enumName, final Set<Modifier> implicitModifiers ) throws UncheckedIOException
    {
        if( isNotEmptyOrBlank( enumName ) )
        {
            //---* Emit an enum constant *-------------------------------------
            /*
             * enum constants are implemented as anonymous types so this is
             * called from an outer type spec.
             */
            codeWriter.emitJavadoc( getJavadoc() );
            final Collection<AnnotationSpecImpl> annotations = new ArrayList<>( getAnnotations() );
            getFactory().createSuppressWarningsAnnotation( getSuppressableWarnings() )
                .map( a -> (AnnotationSpecImpl) a )
                .ifPresent( annotations::add );
            codeWriter.emitAnnotations( annotations, false );
            codeWriter.emit( "$L", enumName );
            if( !m_AnonymousTypeArguments.formatParts().isEmpty() )
            {
                codeWriter.emit( "(" );
                codeWriter.emit( m_AnonymousTypeArguments );
                codeWriter.emit( ")" );
            }

            if( getFieldSpecs().isEmpty() && getMethodSpecs().isEmpty() && innerClasses().isEmpty() )
            {
                //---* Avoid unnecessary braces "{}" *-------------------------
                return;
            }

            codeWriter.emit( " {\n" );
        }
        else if( nonNull( m_AnonymousTypeArguments ) )
        {
            //---* Emit an anonymous type that is not an enum constant *-------
            final var supertype = getSuperInterfaces().isEmpty() ? getSuperClass() : getSuperInterfaces().get( 0 );

            codeWriter.emit( "new $T(", supertype );
            codeWriter.emit( m_AnonymousTypeArguments );
            codeWriter.emit( ") {\n" );
        }
        else
        {
            /*
             * Push an empty type (specifically without nested types) for
             * type-resolution.
             */
            codeWriter.pushType( createCopy() );

            codeWriter.emitJavadoc( getJavadoc() );
            final Collection<AnnotationSpecImpl> annotations = new ArrayList<>( getAnnotations() );
            getFactory().createSuppressWarningsAnnotation( getSuppressableWarnings() )
                .map( a -> (AnnotationSpecImpl) a )
                .ifPresent( annotations::add );
            codeWriter.emitAnnotations( annotations, false );
            codeWriter.emitModifiers( modifiers(), union( implicitModifiers, CLASS.asMemberModifiers() ) );
            codeWriter.emit( "$L $L", CLASS.name().toLowerCase( ROOT ), name().get() );
            codeWriter.emitTypeVariables( getTypeVariables() );

            final List<TypeNameImpl> extendsTypes = getSuperClass().equals( OBJECT ) ? List.of() : List.of( getSuperClass() );
            final var implementsTypes = getSuperInterfaces();

            if( !extendsTypes.isEmpty() )
            {
                codeWriter.emit( " extends" );
                var firstType = true;
                for( final var type : extendsTypes )
                {
                    if( !firstType ) codeWriter.emit( "," );
                    codeWriter.emit( " $T", type );
                    firstType = false;
                }
            }

            if( !implementsTypes.isEmpty() )
            {
                codeWriter.emit( " implements" );
                var firstType = true;
                for( final var type : implementsTypes )
                {
                    if( !firstType ) codeWriter.emit( "," );
                    codeWriter.emit( " $T", type );
                    firstType = false;
                }
            }

            codeWriter.popType();

            codeWriter.emit( " {\n" );
        }

        codeWriter.pushType( this );

        //---* Emit the class body *-------------------------------------------
        codeWriter.indent();
        var firstMember = true;

        //---* Static fields *-------------------------------------------------
        for( final var fieldSpec : getFieldSpecs() )
        {
            if( !fieldSpec.hasModifier( STATIC ) ) continue;
            if( !firstMember ) codeWriter.emit( "\n" );
            fieldSpec.emit( codeWriter, CLASS.implicitFieldModifiers() );
            firstMember = false;
        }

        if( !getStaticBlock().isEmpty() )
        {
            if( !firstMember ) codeWriter.emit( "\n" );
            codeWriter.emit( getStaticBlock() );
            firstMember = false;
        }

        //--* Non-static fields *----------------------------------------------
        for( final var fieldSpec : getFieldSpecs() )
        {
            if( fieldSpec.hasModifier( STATIC ) ) continue;
            if( !firstMember ) codeWriter.emit( "\n" );
            fieldSpec.emit( codeWriter, CLASS.implicitFieldModifiers() );
            firstMember = false;
        }

        //---* Initializer block *---------------------------------------------
        if( !getInitializerBlock().isEmpty() )
        {
            if( !firstMember ) codeWriter.emit( "\n" );
            codeWriter.emit( getInitializerBlock() );
            firstMember = false;
        }

        //---* Constructors *--------------------------------------------------
        for( final var methodSpec : getMethodSpecs() )
        {
            if( !methodSpec.isConstructor() ) continue;
            if( !firstMember ) codeWriter.emit( "\n" );
            methodSpec.emit( codeWriter, name(), CLASS.implicitMethodModifiers() );
            firstMember = false;
        }

        //---* Methods (static and non-static) *-------------------------------
        for( final var methodSpec : getMethodSpecs() )
        {
            if( methodSpec.isConstructor() ) continue;
            if( !firstMember ) codeWriter.emit( "\n" );
            methodSpec.emit( codeWriter, name(), CLASS.implicitMethodModifiers() );
            firstMember = false;
        }

        //---* Types (inner classes) *-----------------------------------------
        for( final var typeSpec : innerClasses() )
        {
            if( !firstMember ) codeWriter.emit( "\n" );
            ((TypeSpecImpl) typeSpec).emit( codeWriter, null, CLASS.implicitTypeModifiers() );
            firstMember = false;
        }

        codeWriter.unindent();
        codeWriter.popType();

        codeWriter.emit( "}" );
        if( isEmpty( enumName ) && isNull( m_AnonymousTypeArguments ) )
        {
            /*
             * If this type isn't also a value, include a trailing newline.
             */
            codeWriter.emit( "\n" );
        }
    }   //  emit4JavaPoet()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = this == o;
        if( !retValue && (o instanceof final ClassSpecImpl other) )
        {
            retValue = getFactory().equals( other.getFactory() ) && toString().equals( o.toString() );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  equals()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int hashCode() { return hash( getFactory(), toString() ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final TypeSpecImpl.BuilderImpl toBuilder()
    {
        final var retValue = new BuilderImpl( getFactory(), name(), (CodeBlockImpl) anonymousTypeArguments().orElse( null ) );
        retValue.getJavadoc().addWithoutDebugInfo( getJavadoc() );
        retValue.getAnnotations().addAll( getAnnotations() );
        retValue.getModifiers().addAll( modifiers() );
        retValue.getTypeVariables().addAll( getTypeVariables() );
        retValue.superclass( getSuperClass() );
        retValue.getSuperinterfaces().addAll( getSuperInterfaces() );
        retValue.getFieldSpecs().addAll( getFieldSpecs() );
        retValue.getMethodSpecs().addAll( getMethodSpecs() );
        retValue.getTypeSpecs().addAll( typeSpecs() );
        retValue.getInitializerBlock().addWithoutDebugInfo( getInitializerBlock() );
        retValue.getStaticBlock().addWithoutDebugInfo( getStaticBlock() );
        retValue.getStaticImports().addAll( getStaticImports() );
        retValue.addSuppressableWarning( getSuppressableWarnings().toArray( SuppressableWarnings []::new ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toBuilder()
}
//  class ClassSpecImpl

/*
 *  End of File
 */