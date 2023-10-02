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
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.CLASS_WITH_TOO_MANY_METHODS;
import static org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl.Kind.INTERFACE;
import static org.tquadrat.foundation.javacomposer.internal.Util.createDebugOutput;
import static org.tquadrat.foundation.javacomposer.internal.Util.requireExactlyOneOf;
import static org.tquadrat.foundation.javacomposer.internal.Util.union;
import static org.tquadrat.foundation.lang.Objects.checkState;
import static org.tquadrat.foundation.lang.Objects.hash;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;

import javax.lang.model.element.Modifier;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
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
 *  for an interface.
 *
 *  @author Square,Inc.
 *  @modified   Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: InterfaceSpecImpl.java 1064 2023-09-26 20:16:12Z tquadrat $
 *  @since 0.2.0
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: InterfaceSpecImpl.java 1064 2023-09-26 20:16:12Z tquadrat $" )
@API( status = INTERNAL, since = "0.2.0" )
public final class InterfaceSpecImpl extends TypeSpecImpl
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The implementation of
     *  {@link Builder}
     *  for an interface.
     *
     *  @author Square,Inc.
     *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: InterfaceSpecImpl.java 1064 2023-09-26 20:16:12Z tquadrat $
     *  @since 0.2.0
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: InterfaceSpecImpl.java 1064 2023-09-26 20:16:12Z tquadrat $" )
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
            this( composer, Optional.of( requireNotEmptyArgument( name, "name" ).toString() ) );
        }   //  BuilderImpl()

        /**
         *  Creates a new {@code BuilderImpl} instance.
         *
         *  @param  composer    The reference to the factory that created this
         *      builder instance.
         *  @param  name    The name of the type to build.
         */
        @SuppressWarnings( {"OptionalUsedAsFieldOrParameterType"} )
        public BuilderImpl( @SuppressWarnings( "UseOfConcreteClass" ) final JavaComposer composer, final Optional<String> name )
        {
            super( composer, INTERFACE, name );
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
            throw new ValidationException( "Attributes are not allowed for interface %s".formatted( getName() .orElse( NAME_ANONYMOUS_TYPE ) ) );
        }   //  addAttribute()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addField( final FieldSpec fieldSpec )
        {
            final var fieldSpecImpl = (FieldSpecImpl) requireNonNullArgument( fieldSpec, "fieldSpec" );
            final var modifiers = fieldSpecImpl.modifiers();
            requireExactlyOneOf( modifiers, PUBLIC, PRIVATE );
            final Set<Modifier> check = EnumSet.of( STATIC, FINAL );
            checkState( modifiers.containsAll( check ), () -> new ValidationException( "%s %s.%s requires modifiers %s".formatted( INTERFACE, getName().orElse( NAME_ANONYMOUS_TYPE ), fieldSpec.name(), check ) ) );
            super.addField( fieldSpecImpl );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addField()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addInitializerBlock( final CodeBlock block )
        {
            throw new UnsupportedOperationException( INTERFACE + " can't have initializer blocks" );
        }   //  addInitializerBlock()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addMethod( final MethodSpec methodSpec )
        {
            final var methodSpecImpl = (MethodSpecImpl) requireNonNullArgument( methodSpec, "methodSpec" );
            final var modifiers = methodSpecImpl.modifiers();
            requireExactlyOneOf( modifiers, ABSTRACT, STATIC, DEFAULT );
            requireExactlyOneOf( modifiers, PUBLIC, PRIVATE);
            checkState( methodSpecImpl.defaultValue().isEmpty(), () -> new IllegalStateException( "%s %s.%s cannot have a default value".formatted( INTERFACE, getName().orElse( NAME_ANONYMOUS_TYPE ), methodSpecImpl.name() ) ) );
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
            throw new ValidationException( "Properties are not allowed for interface %s".formatted( getName().orElse( NAME_ANONYMOUS_TYPE ) ) );
        }   //  addProperty()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final InterfaceSpecImpl build()
        {
            final var retValue = new InterfaceSpecImpl( this );

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  build()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl superclass( final TypeName superclass )
        {
            throw new IllegalStateException( "only classes have super classes, not " + INTERFACE );
        }   //  superclass()
    }
    //  class BuilderImpl

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code InterfaceSpecImpl} instance.
     *
     *  @param  builder The builder for this instance.
     */
    public InterfaceSpecImpl( @SuppressWarnings( "UseOfConcreteClass" ) final BuilderImpl builder )
    {
        super( builder );
    }   //  InterfaceSpecImpl()

    /**
     *  Creates a dummy type spec for type-resolution in CodeWriter only while
     *  emitting the type declaration but before entering the type body.
     *
     *  @param  type    The source type.
     */
    private InterfaceSpecImpl( @SuppressWarnings( "UseOfConcreteClass" ) final InterfaceSpecImpl type )
    {
        super( type );
    }   //  InterfaceSpecImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    protected final TypeSpecImpl createCopy() { return new InterfaceSpecImpl( this ); }

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
    @SuppressWarnings( {"BoundedWildcard", "OptionalGetWithoutIsPresent", "OverlyLongMethod", "OverlyComplexMethod"} )
    @Override
    protected final void emit4Foundation( @SuppressWarnings( "UseOfConcreteClass" ) final CodeWriter codeWriter, final String enumName, final Set<Modifier> implicitModifiers ) throws UncheckedIOException
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
        codeWriter.emitModifiers( modifiers(), union( implicitModifiers, INTERFACE.asMemberModifiers() ) );
        codeWriter.emit( "$L $L", INTERFACE.name().toLowerCase( ROOT ), name().get() );
        codeWriter.emitTypeVariables( getTypeVariables() );

        final var extendsTypes = getSuperInterfaces();
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

        codeWriter.popType();

        codeWriter.emit( "\n{\n" );

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
                .map( t -> (TypeSpecImpl) t )
                .forEachOrdered( t ->
                {
                    codeWriter.emit( "\n" );
                    t.emit( codeWriter, null, INTERFACE.implicitTypeModifiers() );
                } );
            firstMember = false;
        }

        //--- Constants and attributes *---------------------------------------
        if( !getFieldSpecs().isEmpty() )
        {
            final Collection<FieldSpecImpl> alreadyHandled = new HashSet<>();

            //---* Emit the constants *----------------------------------------
            final var constants = getFieldSpecs().stream()
                .filter( constantSpec -> constantSpec.hasModifier( PUBLIC ) )
                .filter( constantSpec -> constantSpec.hasModifier( STATIC ) )
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
                    constantSpec.emit( codeWriter, INTERFACE.implicitFieldModifiers() );
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
                    a.emit( codeWriter, INTERFACE.implicitFieldModifiers() );
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
                    staticSpec.emit( codeWriter, INTERFACE.implicitFieldModifiers() );
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

        //---* Methods (static and non-static) *-------------------------------
        final var methods = getMethodSpecs().stream()
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
                methodSpec.emit( codeWriter, name(), INTERFACE.implicitMethodModifiers() );
            } );
        }

        codeWriter.unindent();
        codeWriter.popType();

        codeWriter.emit(
            """
            }
            //  $L $N
            """, INTERFACE.name().toLowerCase( ROOT ), this );
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
    @SuppressWarnings( {"BoundedWildcard", "OptionalGetWithoutIsPresent", "OverlyComplexMethod"} )
    @Override
    protected final void emit4JavaPoet( @SuppressWarnings( "UseOfConcreteClass" ) final CodeWriter codeWriter, final String enumName, final Set<Modifier> implicitModifiers ) throws UncheckedIOException
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
        codeWriter.emitModifiers( modifiers(), union( implicitModifiers, INTERFACE.asMemberModifiers() ) );
        codeWriter.emit( "$L $L", INTERFACE.name().toLowerCase( ROOT ), name().get() );
        codeWriter.emitTypeVariables( getTypeVariables() );

        final var extendsTypes = getSuperInterfaces();
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

        codeWriter.popType();

        codeWriter.emit( " {\n" );

        codeWriter.pushType( this );

        //---* Emit the class body *-------------------------------------------
        codeWriter.indent();
        var firstMember = true;

        //---* Static fields *-------------------------------------------------
        for( final var fieldSpec : getFieldSpecs() )
        {
            if( !firstMember ) codeWriter.emit( "\n" );
            fieldSpec.emit( codeWriter, INTERFACE.implicitFieldModifiers() );
            firstMember = false;
        }

        if( !getStaticBlock().isEmpty() )
        {
            if( !firstMember ) codeWriter.emit( "\n" );
            codeWriter.emit( getStaticBlock() );
            firstMember = false;
        }

        //---* Methods (static and non-static) *-------------------------------
        for( final var methodSpec : getMethodSpecs() )
        {
            if( !firstMember ) codeWriter.emit( "\n" );
            methodSpec.emit( codeWriter, name(), INTERFACE.implicitMethodModifiers() );
            firstMember = false;
        }

        //---* Types (inner classes) *-----------------------------------------
        for( final var typeSpec : innerClasses() )
        {
            if( !firstMember ) codeWriter.emit( "\n" );
            ((TypeSpecImpl) typeSpec).emit( codeWriter, null, INTERFACE.implicitTypeModifiers() );
            firstMember = false;
        }

        codeWriter.unindent();
        codeWriter.popType();

        codeWriter.emit(
            """
            }
            """ );
    }   //  emit4JavaPoet()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = this == o;
        if( !retValue && (o instanceof final InterfaceSpecImpl other) )
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
    public final BuilderImpl toBuilder()
    {
        final var retValue = new BuilderImpl( getFactory(), name() );
        retValue.getJavadoc().addWithoutDebugInfo( getJavadoc() );
        retValue.getAnnotations().addAll( getAnnotations() );
        retValue.getModifiers().addAll( modifiers() );
        retValue.getTypeVariables().addAll( getTypeVariables() );
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
//  class InterfaceSpecImpl

/*
 *  End of File
 */