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

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.util.Comparator.comparing;
import static java.util.Locale.ROOT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.CLASS_WITH_TOO_MANY_METHODS;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.JAVADOC;
import static org.tquadrat.foundation.javacomposer.internal.TypeNameImpl.VOID_PRIMITIVE;
import static org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl.Kind.RECORD;
import static org.tquadrat.foundation.javacomposer.internal.Util.createDebugOutput;
import static org.tquadrat.foundation.javacomposer.internal.Util.union;
import static org.tquadrat.foundation.lang.Objects.checkState;
import static org.tquadrat.foundation.lang.Objects.hash;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidNonNullArgument;
import static org.tquadrat.foundation.util.StringUtils.capitalize;
import static org.tquadrat.foundation.util.StringUtils.decapitalize;

import javax.lang.model.element.Modifier;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.MethodSpec;
import org.tquadrat.foundation.javacomposer.SuppressableWarnings;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeSpec;

/**
 *  The implementation of
 *  {@link TypeSpec}
 *  for a record.
 *
 *  @extauthor  Thomas Thrien - thomas.thrien@tquadrat.org
 *  @thanks Square,Inc.
 *  @version $Id: RecordSpecImpl.java 1085 2024-01-05 16:23:28Z tquadrat $
 *  @since 0.2.0
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: RecordSpecImpl.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@API( status = INTERNAL, since = "0.2.0" )
public final class RecordSpecImpl extends TypeSpecImpl
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The implementation of
     *  {@link Builder}
     *  for a class.
     *
     *  @extauthor  Thomas Thrien - thomas.thrien@tquadrat.org
     *  @thanks Square,Inc.
     *  @version $Id: RecordSpecImpl.java 1085 2024-01-05 16:23:28Z tquadrat $
     *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: RecordSpecImpl.java 1085 2024-01-05 16:23:28Z tquadrat $
     *  @since 0.2.0
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: RecordSpecImpl.java 1085 2024-01-05 16:23:28Z tquadrat $" )
    @API( status = INTERNAL, since = "0.2.0" )
    public static final class BuilderImpl extends TypeSpecImpl.BuilderImpl
    {
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
        public BuilderImpl( @SuppressWarnings( "UseOfConcreteClass" ) final JavaComposer composer, final CharSequence name )
        {
            super( composer, RECORD, Optional.of( requireNotEmptyArgument( name, "name" ).toString() ) );
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
            final var fieldSpecImpl = (FieldSpecImpl) requireValidNonNullArgument( fieldSpec, "fieldSpec", v -> v.hasModifier( PRIVATE ), _ -> "Field %s needs to be private".formatted( fieldSpec.name() ) );
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
        public TypeSpecImpl.BuilderImpl addField( final FieldSpec fieldSpec )
        {
            final var validation = (Predicate<FieldSpec>) spec ->
            {
                if( spec.hasModifier( STATIC ) )
                {
                    return !spec.hasModifier( PROTECTED );
                }
                else
                {
                    return spec.hasModifier( PRIVATE ) && !spec.hasModifier( FINAL );
                }
            };
            super.addField( requireValidNonNullArgument( fieldSpec, "fieldSpec", validation, _ -> "Invalid modifiers for record field" ) );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addField()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addMethod( final MethodSpec methodSpec )
        {
            final var methodSpecImpl = (MethodSpecImpl) requireNonNullArgument( methodSpec, "methodSpec" );
            checkState( methodSpecImpl.defaultValue().isEmpty(), () -> new IllegalStateException( "%s %s.%s cannot have a default value".formatted( RECORD, getName().orElse( NAME_ANONYMOUS_TYPE ), methodSpecImpl.name() ) ) );
            checkState( !methodSpecImpl.hasModifier( DEFAULT ), () -> new IllegalStateException( "%s %s.%s cannot be default".formatted( RECORD, getName().orElse( NAME_ANONYMOUS_TYPE ), methodSpecImpl.name() ) ) );

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
            final var fieldSpecImpl = (FieldSpecImpl) requireValidNonNullArgument( fieldSpec, "fieldSpec", v -> v.hasModifier( PRIVATE ), _ -> "Field %s needs to be private".formatted( fieldSpec.name() ) );
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
        public final RecordSpecImpl build()
        {
            checkState( getFieldSpecs().stream().anyMatch( fs -> !fs.hasModifier( STATIC ) ), () -> new IllegalStateException( "records without fields are not allowed" ) );

            final var retValue = new RecordSpecImpl( this );

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  build()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl superclass( final TypeName superclass )
        {
            throw new IllegalStateException( "only classes have super classes, not " + RECORD );
        }   //  superclass()
    }
    //  class BuilderImpl

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code RecordSpecImpl} instance.
     *
     *  @param  builder The builder for this instance.
     */
    public RecordSpecImpl( @SuppressWarnings( "UseOfConcreteClass" ) final BuilderImpl builder )
    {
        super( builder );
    }   //  RecordSpecImpl()

    /**
     *  Creates a dummy type spec for type-resolution in CodeWriter only while
     *  emitting the type declaration but before entering the type body.
     *
     *  @param  type    The source type.
     */
    private RecordSpecImpl( @SuppressWarnings( "UseOfConcreteClass" ) final RecordSpecImpl type )
    {
        super( type );
    }   //  RecordSpecImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    protected final TypeSpecImpl createCopy() { return new RecordSpecImpl( this ); }

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
    @Override
    @SuppressWarnings( {"BoundedWildcard", "OptionalGetWithoutIsPresent", "OverlyLongMethod", "OverlyComplexMethod"} )
    protected final void emit4Foundation( @SuppressWarnings( "UseOfConcreteClass" ) final CodeWriter codeWriter, final String enumName, final Set<Modifier> implicitModifiers ) throws UncheckedIOException
    {
        /*
         * Push an empty type (specifically without nested types) for
         * type-resolution.
         */
        codeWriter.pushType( createCopy() );

        var javadoc = (CodeBlockImpl) getJavadoc();
        var needSuppressWarningJavadoc = javadoc.isEmpty();

        //--* Non-static fields *----------------------------------------------
        final Collection<ParameterSpecImpl> nonStaticFields = new ArrayList<>();
        for( final var fieldSpec : getFieldSpecs() )
        {
            if( fieldSpec.hasModifier( STATIC ) ) continue;
            final var paramSpec = (ParameterSpecImpl) getFactory().parameterBuilder( fieldSpec.type(), fieldSpec.name() )
                .build();
            nonStaticFields.add( paramSpec );
            final var fieldJavaDoc = fieldSpec.getJavadoc();
            if( fieldJavaDoc.isEmpty() )
            {
                needSuppressWarningJavadoc = true;
            }
            else
            {
                final var codeBlock = (CodeBlockImpl) getFactory().codeBlockOf( "@param $N", fieldSpec )
                    .join( " ", fieldJavaDoc );
                javadoc = (CodeBlockImpl) javadoc.join( "\n", codeBlock );
            }
        }

        needSuppressWarningJavadoc = needSuppressWarningJavadoc && !javadoc.isEmpty();
        codeWriter.emitJavadoc( javadoc );

        final Set<SuppressableWarnings> suppressableWarnings = EnumSet.copyOf( getSuppressableWarnings() );
        if( needSuppressWarningJavadoc ) suppressableWarnings.add( JAVADOC );
        final Collection<AnnotationSpecImpl> annotations = new ArrayList<>( getAnnotations() );
        getFactory().createSuppressWarningsAnnotation( suppressableWarnings )
            .map( a -> (AnnotationSpecImpl) a )
            .ifPresent( annotations::add );
        codeWriter.emitAnnotations( annotations, false );
        codeWriter.emitModifiers( modifiers(), union( implicitModifiers, RECORD.asMemberModifiers() ) );
        codeWriter.emit( "$1L $2L", RECORD.name().toLowerCase( ROOT ), name().get() );
        codeWriter.emitTypeVariables( getTypeVariables() );

        var firstMember = true;
        for( final var paramSpec : nonStaticFields )
        {
            codeWriter.emit( firstMember ? "( " : ", " );
            paramSpec.emit( codeWriter, false );
            firstMember = false;
        }
        codeWriter.emit( " )" );

        //---* The implemented interfaces *------------------------------------
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

        //---* Get the static fields *-----------------------------------------
        final var staticFields = getFieldSpecs().stream()
            .filter( fs -> fs.hasModifier( STATIC ) )
            .toList();

        if( staticFields.isEmpty() && getStaticBlock().isEmpty() && getInitializerBlock().isEmpty() && getMethodSpecs().isEmpty() )
        {
            //---* We do not have a body for the record … *--------------------
            codeWriter.emit( " { /* Empty */ " );
        }
        else
        {
            codeWriter.pushType( this );

            codeWriter.emit(
                """
                \n{
                """ );

            //---* Emit the class body *---------------------------------------
            codeWriter.indent();
            firstMember = true;

            //---* Emit the inner types *--------------------------------------
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
                        t.emit( codeWriter, null, RECORD.implicitTypeModifiers() );
                    } );
                firstMember = false;
            }

            //--- Constants and attributes *-----------------------------------
            if( !staticFields.isEmpty() )
            {
                final Collection<FieldSpecImpl> alreadyHandled = new HashSet<>();

                //---* Emit the constants *----------------------------------------
                final var constants = staticFields.stream()
                    .filter( constantSpec -> constantSpec.hasModifier( PUBLIC ) )
                    .filter( constantSpec -> constantSpec.hasModifier( FINAL ) )
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
                        constantSpec.emit( codeWriter, RECORD.implicitFieldModifiers() );
                        alreadyHandled.add( constantSpec );
                    } );
                    firstMember = false;
                }

                //---* Static fields *-----------------------------------------
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
                        staticSpec.emit( codeWriter, RECORD.implicitFieldModifiers() );
                        alreadyHandled.add( staticSpec );
                    } );

                    //---* Static Block *--------------------------------------
                    if( !getStaticBlock().isEmpty() )
                    {
                        codeWriter.emit( "\n" );
                        codeWriter.emit( getStaticBlock() );
                    }
                    firstMember = false;
                }
            }

            //---* Constructors *----------------------------------------------
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

                //---* Initializer block *-------------------------------------
                if( !getInitializerBlock().isEmpty() )
                {
                    codeWriter.emit( "\n" );
                    codeWriter.emit( getInitializerBlock() );
                }

                //---* Emit the constructors *---------------------------------
                constructors.forEach( constructorSpec ->
                {
                    codeWriter.emit( "\n" );
                    constructorSpec.emit( codeWriter, name(), RECORD.implicitMethodModifiers() );
                } );
                firstMember = false;
            }

            //---* Methods (static and non-static) *---------------------------
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
                    methodSpec.emit( codeWriter, name(), RECORD.implicitMethodModifiers() );
                } );
            }

            codeWriter.unindent();
            codeWriter.popType();
        }

        codeWriter.emit(
            """
            }
            //  $L $N
            """, RECORD.name().toLowerCase( ROOT ), this );
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
    @Override
    @SuppressWarnings( {"BoundedWildcard", "OptionalGetWithoutIsPresent", "OverlyLongMethod", "OverlyComplexMethod"} )
    protected final void emit4JavaPoet( @SuppressWarnings( "UseOfConcreteClass" ) final CodeWriter codeWriter, final String enumName, final Set<Modifier> implicitModifiers ) throws UncheckedIOException
    {
        /*
         * Push an empty type (specifically without nested types) for
         * type-resolution.
         */
        codeWriter.pushType( createCopy() );

        var javadoc = getJavadoc();

        //--* Non-static fields *----------------------------------------------
        final Collection<ParameterSpecImpl> nonStaticFields = new ArrayList<>();
        for( final var fieldSpec : getFieldSpecs() )
        {
            if( fieldSpec.hasModifier( STATIC ) ) continue;
            final var paramSpec = (ParameterSpecImpl) getFactory().parameterBuilder( fieldSpec.type(), fieldSpec.name() )
                .build();
            nonStaticFields.add( paramSpec );
            final var fieldJavaDoc = fieldSpec.getJavadoc();
            if( !fieldJavaDoc.isEmpty() )
            {
                final var codeBlock = (CodeBlockImpl) getFactory().codeBlockOf( "@param $N", fieldSpec )
                    .join( " ", fieldJavaDoc );
                javadoc = (CodeBlockImpl) javadoc.join( "\n", codeBlock );
            }
        }

        codeWriter.emitJavadoc( javadoc );
        final Collection<AnnotationSpecImpl> annotations = new ArrayList<>( getAnnotations() );
        getFactory().createSuppressWarningsAnnotation( getSuppressableWarnings() )
            .map( a -> (AnnotationSpecImpl) a )
            .ifPresent( annotations::add );
        codeWriter.emitAnnotations( annotations, false );
        codeWriter.emitModifiers( modifiers(), union( implicitModifiers, RECORD.asMemberModifiers() ) );
        codeWriter.emit( "$1L $2L", RECORD.name().toLowerCase( ROOT ), name().get() );
        codeWriter.emitTypeVariables( getTypeVariables() );

        var firstMember = true;
        for( final var paramSpec : nonStaticFields )
        {
            codeWriter.emit( firstMember ? "(" : ", " );
            paramSpec.emit( codeWriter, false );
            firstMember = false;
        }
        codeWriter.emit( ")" );

        //---* The implemented interfaces *------------------------------------
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

        //---* Get the static fields *-----------------------------------------
        final var staticFields = getFieldSpecs().stream()
            .filter( fs -> fs.hasModifier( STATIC ) )
            .toList();

        if( staticFields.isEmpty() && getStaticBlock().isEmpty() && getInitializerBlock().isEmpty() && getMethodSpecs().isEmpty() )
        {
            //---* We do not have a body for the record … *--------------------
            codeWriter.emit( " {}\n" );
        }
        else
        {
            codeWriter.pushType( this );

            //---* Emit the record body *--------------------------------------
            codeWriter.emit( " {\n" );

            codeWriter.indent();

            //---* Static fields *---------------------------------------------
            firstMember = true;
            for( final var fieldSpec : staticFields )
            {
                if( !firstMember ) codeWriter.emit( "\n" );
                fieldSpec.emit( codeWriter, EnumSet.noneOf( Modifier.class ) );
                firstMember = false;
            }

            if( !getStaticBlock().isEmpty() )
            {
                if( !firstMember ) codeWriter.emit( "\n" );
                codeWriter.emit( getStaticBlock() );
                firstMember = false;
            }

            //---* Initializer block *-----------------------------------------
            if( !getInitializerBlock().isEmpty() )
            {
                if( !firstMember ) codeWriter.emit( "\n" );
                codeWriter.emit( getInitializerBlock() );
                firstMember = false;
            }

            //---* Constructors *----------------------------------------------
            for( final var methodSpec : getMethodSpecs() )
            {
                if( !methodSpec.isConstructor() ) continue;
                if( !firstMember ) codeWriter.emit( "\n" );
                methodSpec.emit( codeWriter, name(), RECORD.implicitMethodModifiers() );
                firstMember = false;
            }

            //---* Methods (static and non-static) *---------------------------
            for( final var methodSpec : getMethodSpecs() )
            {
                if( methodSpec.isConstructor() ) continue;
                if( !firstMember ) codeWriter.emit( "\n" );
                methodSpec.emit( codeWriter, name(), RECORD.implicitMethodModifiers() );
                firstMember = false;
            }

            //---* Types (inner classes) *-------------------------------------
            for( final var typeSpec : innerClasses() )
            {
                if( !firstMember ) codeWriter.emit( "\n" );
                ((TypeSpecImpl) typeSpec).emit( codeWriter, null, RECORD.implicitTypeModifiers() );
                firstMember = false;
            }

            codeWriter.unindent();
            codeWriter.emit( "}\n" );
            codeWriter.popType();
        }
    }   //  emit4JavaPoet()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = this == o;
        if( !retValue && (o instanceof final RecordSpecImpl other) )
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
    @SuppressWarnings( {"OptionalGetWithoutIsPresent"} )
    @Override
    public final TypeSpecImpl.BuilderImpl toBuilder()
    {
        final var retValue = new BuilderImpl( getFactory(), name().get() );
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
        retValue.addSuppressableWarning( getSuppressableWarnings().toArray( SuppressableWarnings[]::new ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toBuilder()
}
//  class RecordSpecImpl

/*
 *  End of File
 */