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

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.util.Comparator.comparing;
import static java.util.Locale.ROOT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.CLASS_WITH_TOO_MANY_METHODS;
import static org.tquadrat.foundation.javacomposer.internal.TypeNameImpl.VOID_PRIMITIVE;
import static org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl.Kind.ENUM;
import static org.tquadrat.foundation.javacomposer.internal.Util.createDebugOutput;
import static org.tquadrat.foundation.javacomposer.internal.Util.union;
import static org.tquadrat.foundation.lang.Objects.checkState;
import static org.tquadrat.foundation.lang.Objects.hash;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.util.JavaUtils.isValidName;
import static org.tquadrat.foundation.util.StringUtils.capitalize;
import static org.tquadrat.foundation.util.StringUtils.decapitalize;
import static org.tquadrat.foundation.util.StringUtils.format;

import javax.lang.model.element.Modifier;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.MethodSpec;
import org.tquadrat.foundation.javacomposer.SuppressableWarnings;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeSpec;

/**
 *  The implementation of
 *  {@link TypeSpec}
 *  for an {@code enum} type.
 *
 *  @author Square,Inc.
 *  @modified   Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: EnumSpecImpl.java 936 2021-12-13 16:08:37Z tquadrat $
 *  @since 0.2.0
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: EnumSpecImpl.java 936 2021-12-13 16:08:37Z tquadrat $" )
@API( status = INTERNAL, since = "0.2.0" )
public final class EnumSpecImpl extends TypeSpecImpl
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The implementation of
     *  {@link Builder}
     *  for {@code enum} types.
     *
     *  @author Square,Inc.
     *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: EnumSpecImpl.java 936 2021-12-13 16:08:37Z tquadrat $
     *  @since 0.2.0
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: EnumSpecImpl.java 936 2021-12-13 16:08:37Z tquadrat $" )
    @API( status = INTERNAL, since = "0.2.0" )
    public static final class BuilderImpl extends TypeSpecImpl.BuilderImpl
    {
            /*------------*\
        ====** Attributes **=======================================================
            \*------------*/
        /**
         *  The enum constants.
         */
        private final Map<String,ClassSpecImpl> m_EnumConstants = new HashMap<>();

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code BuilderImpl} instance.
         *
         *  @param  composer    The reference to the factory that created this
         *      builder instance.
         *  @param  name    The name of the type to build.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        public BuilderImpl( final JavaComposer composer, final CharSequence name )
        {
            this( composer, Optional.of( requireNotEmptyArgument( name, "name" ).toString() ) );
        }   //  BuilderImpl()

        /**
         *  Creates a new {@code BuilderImpl} instance.
         *
         *  @param  composer    The reference to the factory that created this
         *      builder instance.
         *  @param  name    The name of the type to build.
         */
        @SuppressWarnings( {"OptionalUsedAsFieldOrParameterType", "UseOfConcreteClass"} )
        public BuilderImpl( final JavaComposer composer, final Optional<String> name )
        {
            super( composer, ENUM, name );
        }   //  BuilderImpl()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "CastToConcreteClass" )
        @API( status = STABLE, since = "0.2.0" )
        @Override
        public final BuilderImpl addAttribute( final FieldSpec fieldSpec, final boolean readOnly )
        {
            final var fieldSpecImpl = (FieldSpecImpl) requireNonNullArgument( fieldSpec, "fieldSpec" );
            checkState( fieldSpecImpl.hasModifier( PRIVATE ), () -> new ValidationException( format( "Property %s needs to be private", fieldSpecImpl.name() ) ) );
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
        @SuppressWarnings( "CastToConcreteClass" )
        public final BuilderImpl addEnumConstant( final CharSequence name, final TypeSpec typeSpec )
        {
            final var typeSpecImpl = (ClassSpecImpl) requireNonNullArgument( typeSpec, "typeSpec" );
            checkState( typeSpecImpl.anonymousTypeArguments().isPresent(), () -> new ValidationException( format( "enum constants must have anonymous type arguments" ) ) );
            checkState( isValidName( name ), () -> new ValidationException( format( "not a valid enum constant: %s", name ) ) );
            if( composer().addDebugOutput() )
            {
                final var builder = typeSpecImpl.toBuilder();
                createDebugOutput( true ).ifPresent( debug -> builder.addJavadoc( "\n$L\n", debug.asLiteral() ) );
                getEnumConstants().put( name.toString().intern(), (ClassSpecImpl) builder.build() );
            }
            else
            {
                getEnumConstants().put( name.toString().intern(), typeSpecImpl );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addEnumConstant()

        /**
         *  {@inheritDoc}
         */
        @Override
        @SuppressWarnings( "CastToConcreteClass" )
        public final BuilderImpl addField( final FieldSpec fieldSpec )
        {
            final var fieldSpecImpl = (FieldSpecImpl) requireNonNullArgument( fieldSpec, "fieldSpec" );
            super.addField( fieldSpecImpl );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addField()

        /**
         *  {@inheritDoc}
         */
        @Override
        @SuppressWarnings( "CastToConcreteClass" )
        public final BuilderImpl addMethod( final MethodSpec methodSpec )
        {
            final var methodSpecImpl = (MethodSpecImpl) requireNonNullArgument( methodSpec, "methodSpec" );
            checkState( methodSpecImpl.defaultValue().isEmpty(), () -> new IllegalStateException( format( "%s %s.%s cannot have a default value", ENUM, getName().orElse( NAME_ANONYMOUS_TYPE ), methodSpecImpl.name() ) ) );
            checkState( !methodSpecImpl.hasModifier( DEFAULT ), () -> new IllegalStateException( format( "%s %s.%s cannot be default", ENUM, getName().orElse( NAME_ANONYMOUS_TYPE ), methodSpecImpl.name() ) ) );
            if( composer().addDebugOutput() )
            {
                final var builder = methodSpecImpl.toBuilder();
                createDebugOutput( true ).ifPresent( debug -> builder.addJavadoc( "\n$L\n", debug.asLiteral() ) );
                getMethodSpecs().add( (MethodSpecImpl) builder.build() );
            }
            else
            {
                getMethodSpecs().add( methodSpecImpl );
            }

            final var maxMethods = composer().getMaxMethods();
            if( (maxMethods > 0) && (getMethodSpecs().size() >= maxMethods) )
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
        @SuppressWarnings( "CastToConcreteClass" )
        public final Builder addProperty( final FieldSpec fieldSpec, final boolean readOnly )
        {
            final var fieldSpecImpl = (FieldSpecImpl) requireNonNullArgument( fieldSpec, "fieldSpec" );
            checkState( fieldSpecImpl.hasModifier( PRIVATE ), () -> new ValidationException( format( "Property %s needs to be private", fieldSpecImpl.name() ) ) );
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
        @SuppressWarnings( "UseOfConcreteClass" )
        public final EnumSpecImpl build()
        {
            checkState( !getEnumConstants().isEmpty(), () -> new ValidationException( format( "at least one enum constant is required for %s", getName().orElse( NAME_ANONYMOUS_TYPE ) ) ) );

            final var retValue = new EnumSpecImpl( this );

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  build()

        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "AssignmentOrReturnOfFieldWithMutableType" )
        @Override
        protected final Map<String,ClassSpecImpl> getEnumConstants() { return m_EnumConstants; }

        /**
         *  {@inheritDoc}
         */
        @Override
        @SuppressWarnings( "UseOfConcreteClass" )
        public final BuilderImpl superclass( final TypeName superclass )
        {
            throw new IllegalStateException( format( "only classes have super classes, not " + ENUM ) );
        }   //  superclass()
    }
    //  class BuilderImpl

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The {@code enum} constants for this type.
     */
    private final Map<String,ClassSpecImpl> m_EnumConstants;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code TypeSpecImpl} instance.
     *
     *  @param  builder The builder for this instance.
     */
    @SuppressWarnings( {"AccessingNonPublicFieldOfAnotherObject", "UseOfConcreteClass"} )
    public EnumSpecImpl( final BuilderImpl builder )
    {
        super( builder );
        m_EnumConstants = builder.m_EnumConstants;
    }   //  TypeSpecImpl()

    /**
     *  Creates a dummy type spec for type-resolution in CodeWriter only while
     *  emitting the type declaration but before entering the type body.
     *
     *  @param  type    The source type.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private EnumSpecImpl( final EnumSpecImpl type )
    {
        super( type );
        m_EnumConstants = Map.of();
    }   //  TypeSpecImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    protected final TypeSpecImpl createCopy() { return new EnumSpecImpl( this ); }

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
    @SuppressWarnings( {"BoundedWildcard", "OptionalGetWithoutIsPresent", "CastToConcreteClass"} )
    protected final void emit4Foundation( final CodeWriter codeWriter, final String enumName, final Set<Modifier> implicitModifiers ) throws UncheckedIOException
    {
        assert isNull( enumName ) : "enumName has to be null";

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
        codeWriter.emitModifiers( modifiers(), union( implicitModifiers, ENUM.asMemberModifiers() ) );
        codeWriter.emit( "$L $L", ENUM.name().toLowerCase( ROOT ), name().get() );
        codeWriter.emitTypeVariables( getTypeVariables() );

        final var implementsTypes = getSuperInterfaces();
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

        codeWriter.emit( "\n{\n" );

        codeWriter.pushType( this );

        //---* Emit the class body *-------------------------------------------
        codeWriter.indent();
        var firstMember = true;

        //---* Emit the enum constants *---------------------------------------
        if( !getEnumConstants().isEmpty() )
        {
            codeWriter.emit(
                """
                    /*------------------*\\
                ====** Enum Declaration **=================================================
                    \\*------------------*/""" );
            EmitEnumLoop:
            //noinspection ForLoopWithMissingComponent
            for( final var i = new TreeMap<>( getEnumConstants() ).entrySet().iterator(); i.hasNext(); )
            {
                final var enumConstant = i.next();
                codeWriter.emit( "\n" );
                enumConstant.getValue().emit( codeWriter, enumConstant.getKey(), Collections.emptySet() );
                if( i.hasNext() )
                {
                    codeWriter.emit( ",\n" );
                }
                else if( !getFieldSpecs().isEmpty() || !getMethodSpecs().isEmpty() || !innerClasses().isEmpty() )
                {
                    codeWriter.emit( ";\n" );
                }
                else
                {
                    codeWriter.emit( "\n" );
                }
            }   //  EmitEnumLoop:
            firstMember = false;
        }

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
                .map( t -> (TypeSpecImpl) t )
                .forEachOrdered( t ->
                {
                    codeWriter.emit( "\n" );
                    t.emit( codeWriter, null, ENUM.implicitTypeModifiers() );
                } );
            firstMember = false;
        }

        //--- Constants and attributes *---------------------------------------
        if( !getFieldSpecs().isEmpty() )
        {
            final Collection<FieldSpecImpl> alreadyHandled = new HashSet<>();

            //---* Emit the constants *----------------------------------------
            final var constants = getFieldSpecs().stream()
                .filter( f -> f.hasModifier( PUBLIC ) )
                .filter( f -> f.hasModifier( STATIC ) )
                .filter( f -> f.hasModifier( FINAL ) )
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
                constants.forEach( c ->
                {
                    codeWriter.emit( "\n" );
                    c.emit( codeWriter, ENUM.implicitFieldModifiers() );
                    alreadyHandled.add( c );
                } );
                firstMember = false;
            }

            //---* Emit the attributes *---------------------------------------
            final var attributes = getFieldSpecs().stream()
                .filter( f -> !alreadyHandled.contains( f ) )
                .filter( f -> !(f.hasModifier( STATIC ) && f.hasModifier( FINAL )) )
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
                    a.emit( codeWriter, ENUM.implicitFieldModifiers() );
                    alreadyHandled.add( a );
                } );
                firstMember = false;
            }

            //---* Static fields *---------------------------------------------
            final var statics = getFieldSpecs().stream()
                .filter( f -> !alreadyHandled.contains( f ) )
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

                statics.forEach( s ->
                {
                    codeWriter.emit( "\n" );
                    s.emit( codeWriter, ENUM.implicitFieldModifiers() );
                    alreadyHandled.add( s );
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
            constructors.forEach( c ->
            {
                codeWriter.emit( "\n" );
                c.emit( codeWriter, name(), ENUM.implicitMethodModifiers() );
            } );
            firstMember = false;
        }

        //---* Methods (static and non-static) *-------------------------------
        final var methods = getMethodSpecs().stream()
            .filter( m -> !m.isConstructor() )
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

            methods.forEach( m ->
            {
                codeWriter.emit( "\n" );
                m.emit( codeWriter, name(), ENUM.implicitMethodModifiers() );
            } );
        }

        codeWriter.unindent();
        codeWriter.popType();

        codeWriter.emit(
            """
            }
            //  $L $N
            """, ENUM.name().toLowerCase( ROOT ), this );
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
    @SuppressWarnings( {"BoundedWildcard", "OptionalGetWithoutIsPresent", "CastToConcreteClass"} )
    protected final void emit4JavaPoet( final CodeWriter codeWriter, final String enumName, final Set<Modifier> implicitModifiers ) throws UncheckedIOException
    {
        assert isNull( enumName ) : "enumName has to be null";

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
        codeWriter.emitModifiers( modifiers(), union( implicitModifiers, ENUM.asMemberModifiers() ) );
        codeWriter.emit( "$L $L", ENUM.name().toLowerCase( ROOT ), name().get() );
        codeWriter.emitTypeVariables( getTypeVariables() );

        final var implementsTypes = getSuperInterfaces();
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

        codeWriter.pushType( this );

        //---* Emit the class body *-------------------------------------------
        codeWriter.indent();
        var firstMember = true;

        //---* Emit the enum constants *---------------------------------------
        //noinspection ForLoopWithMissingComponent
        for( final var i = new TreeMap<>( getEnumConstants() ).entrySet().iterator(); i.hasNext(); )
        {
            final var enumConstant = i.next();
            if( !firstMember ) codeWriter.emit( "\n" );
            enumConstant.getValue().emit( codeWriter, enumConstant.getKey(), Collections.emptySet() );
            firstMember = false;
            if( i.hasNext() )
            {
                codeWriter.emit( ",\n" );
            }
            else if( !getFieldSpecs().isEmpty() || !getMethodSpecs().isEmpty() || !innerClasses().isEmpty() )
            {
                codeWriter.emit( ";\n" );
            }
            else
            {
                codeWriter.emit( "\n" );
            }
        }

        //---* Static fields *-------------------------------------------------
        for( final var fieldSpec : getFieldSpecs() )
        {
            if( !fieldSpec.hasModifier( STATIC ) ) continue;
            if( !firstMember ) codeWriter.emit( "\n" );
            fieldSpec.emit( codeWriter, ENUM.implicitFieldModifiers() );
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
            fieldSpec.emit( codeWriter, ENUM.implicitFieldModifiers() );
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
            methodSpec.emit( codeWriter, name(), ENUM.implicitMethodModifiers() );
            firstMember = false;
        }

        //---* Methods (static and non-static) *-------------------------------
        for( final var methodSpec : getMethodSpecs() )
        {
            if( methodSpec.isConstructor() ) continue;
            if( !firstMember ) codeWriter.emit( "\n" );
            methodSpec.emit( codeWriter, name(), ENUM.implicitMethodModifiers() );
            firstMember = false;
        }

        //---* Types (inner classes) *-----------------------------------------
        for( final var typeSpec : innerClasses() )
        {
            if( !firstMember ) codeWriter.emit( "\n" );
            ((TypeSpecImpl) typeSpec).emit( codeWriter, null, ENUM.implicitTypeModifiers() );
            firstMember = false;
        }

        codeWriter.unindent();
        codeWriter.popType();

        codeWriter.emit( "}\n" );
    }   //  emit4JavaPoet()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = this == o;
        if( !retValue && (o instanceof EnumSpecImpl other) )
        {
            retValue = getFactory().equals( other.getFactory() ) && toString().equals( o.toString() );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  equals()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "AssignmentOrReturnOfFieldWithMutableType" )
    @Override
    protected final Map<String, ClassSpecImpl> getEnumConstants() { return m_EnumConstants; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int hashCode() { return hash( getFactory(), toString() ); }

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( {"AccessingNonPublicFieldOfAnotherObject", "UseOfConcreteClass"} )
    @Override
    public final BuilderImpl toBuilder()
    {
        final var retValue = new BuilderImpl( getFactory(), name() );
        retValue.getJavadoc().addWithoutDebugInfo( getJavadoc() );
        retValue.getAnnotations().addAll( getAnnotations() );
        retValue.getModifiers().addAll( modifiers() );
        retValue.getTypeVariables().addAll( getTypeVariables() );
        retValue.getSuperinterfaces().addAll( getSuperInterfaces() );
        retValue.getEnumConstants().putAll( getEnumConstants() );
        retValue.getFieldSpecs().addAll( getFieldSpecs() );
        retValue.getMethodSpecs().addAll( getMethodSpecs() );
        retValue.getTypeSpecs().addAll( typeSpecs() );
        retValue.getInitializerBlock().addWithoutDebugInfo( getInitializerBlock() );
        retValue.getStaticBlock().addWithoutDebugInfo( getStaticBlock() );
        retValue.getStaticImports().addAll( getStaticImports() );
        retValue.addSuppressableWarning( getSuppressableWarnings().toArray( SuppressableWarnings[]::new ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toBuilder()
}
//  class EnumSpecImpl

/*
 *  End of File
 */