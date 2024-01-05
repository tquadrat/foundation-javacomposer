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

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.Objects.checkState;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import java.io.UncheckedIOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.AnnotationSpec;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeVariableName;

/**
 *  The implementation of
 *  {@link TypeNameImpl}
 *  for parameterised types.
 *
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ParameterizedTypeNameImpl.java 1085 2024-01-05 16:23:28Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: ParameterizedTypeNameImpl.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public final class ParameterizedTypeNameImpl extends TypeNameImpl implements ParameterizedTypeName
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The enclosing type, if any.
     */
    @SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
    private final Optional<ParameterizedTypeNameImpl> m_EnclosingType;

    /**
     *  The class name for this type.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final ClassNameImpl m_RawType;

    /**
     *  The type arguments.
     */
    private final List<TypeNameImpl> m_TypeArguments;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code ParameterizedTypeNameImpl} instance.
     *
     *  @param  enclosingType   The optional enclosing type for this instance.
     *  @param  rawType The class name for this instance.
     *  @param  typeArguments   The type arguments.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public ParameterizedTypeNameImpl( final ParameterizedTypeNameImpl enclosingType, final ClassNameImpl rawType, final List<? extends TypeNameImpl> typeArguments )
    {
        this( enclosingType, rawType, typeArguments, new ArrayList<>() );
    }   //  ParameterizedTypeNameImpl()

    /**
     *  Creates a new {@code ParameterizedTypeNameImpl} instance.
     *
     *  @param  enclosingType   The optional enclosing type for this instance.
     *  @param  rawType The class name for this instance.
     *  @param  typeArguments   The type arguments.
     *  @param  ignored The annotations.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private ParameterizedTypeNameImpl( final ParameterizedTypeNameImpl enclosingType, final ClassNameImpl rawType, final List<? extends TypeNameImpl> typeArguments, final List<AnnotationSpecImpl> ignored )
    {
        this( Optional.ofNullable( enclosingType ), rawType, typeArguments, new ArrayList<>() );
    }   //  ParameterizedTypeNameImpl()

    /**
     *  Creates a new {@code ParameterizedTypeNameImpl} instance.
     *
     *  @param  enclosingType   The optional enclosing type for this instance.
     *  @param  rawType The class name for this instance.
     *  @param  typeArguments   The type arguments.
     *  @param  annotations The annotations.
     */
    @SuppressWarnings( {"OptionalUsedAsFieldOrParameterType", "UseOfConcreteClass"} )
    private ParameterizedTypeNameImpl( final Optional<ParameterizedTypeNameImpl> enclosingType, final ClassNameImpl rawType, final List<? extends TypeNameImpl> typeArguments, final List<AnnotationSpecImpl> annotations )
    {
        super( annotations );
        m_RawType = requireNonNullArgument( rawType, "rawType" ).annotated( annotations.stream().map( a -> (AnnotationSpec) a ).collect( toList() ) );
        m_EnclosingType = enclosingType;
        m_TypeArguments = List.copyOf( requireNonNullArgument( typeArguments, "typeArguments" ) );

        checkState( !m_TypeArguments.isEmpty() || enclosingType.isPresent(), () -> new ValidationException( "no type arguments: %s".formatted( rawType ) ) );
        for( final var typeArgument : m_TypeArguments )
        {
            checkState( !typeArgument.isPrimitive() && (typeArgument != VOID_PRIMITIVE), () -> new ValidationException( "invalid type parameter: %s".formatted( typeArgument ) ) );
        }
    }   //  ParameterizedTypeNameImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final ParameterizedTypeNameImpl annotated( final List<AnnotationSpec> annotations )
    {
        final var retValue = new ParameterizedTypeNameImpl( m_EnclosingType, m_RawType, m_TypeArguments, concatAnnotations( annotations ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  annotated()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( {"PublicMethodNotExposedInInterface", "UseOfConcreteClass"} )
    @Override
    public final CodeWriter emit( final CodeWriter out ) throws UncheckedIOException
    {
        final var retValue = requireNonNullArgument( out, "out" );
        if( m_EnclosingType.isPresent() )
        {
            m_EnclosingType.get().emit( retValue );
            retValue.emit( "." );
            if( isAnnotated() )
            {
                retValue.emit( " " );
                emitAnnotations( retValue );
            }
            retValue.emit( m_RawType.simpleName() );
        }
        else
        {
            m_RawType.emit( retValue );
        }

        if( !m_TypeArguments.isEmpty() )
        {
            retValue.emitAndIndent( "<" );
            var firstParameter = true;
            for( final var parameter : m_TypeArguments )
            {
                if( !firstParameter ) retValue.emitAndIndent( ", " );
                parameter.emit( retValue );
                firstParameter = false;
            }
            retValue.emitAndIndent( ">" );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  emit()

    /**
     *  Returns a parameterised type, applying the given type arguments to the
     *  given raw type.
     *
     *  @param  rawType The class name for the new type.
     *  @param  typeArguments   The type arguments.
     *  @return The new instance of {@code ParameterizedTypeName}.
     */
    @API( status = STABLE, since = "0.2.0" )
    public static final ParameterizedTypeNameImpl from( final Class<?> rawType, final Type... typeArguments )
    {
        final var retValue = new ParameterizedTypeNameImpl( null, (ClassNameImpl) ClassName.from( rawType ), list( typeArguments ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a parameterised type, applying the given type arguments to the
     *  given raw type.
     *
     *  @param  rawType The class name for the new type.
     *  @param  typeArguments   The type arguments.
     *  @return The new instance of {@code ParameterizedTypeName}.
     */
    @API( status = STABLE, since = "0.2.0" )
    public static final ParameterizedTypeNameImpl from( final ClassName rawType, final TypeName... typeArguments )
    {
        final var retValue = new ParameterizedTypeNameImpl( null, (ClassNameImpl) rawType, stream( requireNonNullArgument( typeArguments, "typeArguments" ) ).map( t -> (TypeNameImpl) t ).collect( toList() ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a parameterised type equivalent to the given type, with the
     *  given type arguments added.
     *
     *  @param  type    The other type.
     *  @param  typeArguments   The type arguments.
     *  @return The new instance of {@code ParameterizedTypeName}.
     */
    @API( status = STABLE, since = "0.2.0" )
    public static final ParameterizedTypeNameImpl from( final ParameterizedType type, final Map<Type,TypeVariableName> typeArguments )
    {
        final var rawType = (ClassNameImpl) ClassName.from( (Class<?>) type.getRawType() );
        final var ownerType = (type.getOwnerType() instanceof ParameterizedType) && !Modifier.isStatic( ((Class<?>) type.getRawType()).getModifiers() ) ? (ParameterizedType) type.getOwnerType() : null;
        final var typeArgumentList = TypeNameImpl.list( type.getActualTypeArguments(), typeArguments );
        final var retValue = nonNull( ownerType )
            ? from( ownerType, typeArguments ).nestedClass( rawType.simpleName(), typeArgumentList.stream().map( t -> (TypeName) t ).collect( toList() ) )
            : new ParameterizedTypeNameImpl( null, rawType, typeArgumentList );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final ParameterizedTypeNameImpl nestedClass( final CharSequence name )
    {
        final var retValue = new ParameterizedTypeNameImpl( this, m_RawType.nestedClass( requireNonNullArgument( name, "name" ) ), new ArrayList<>(), new ArrayList<>() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  nestedClass()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final ParameterizedTypeNameImpl nestedClass( final CharSequence name, final List<TypeName> typeArguments )
    {
        final var retValue = new ParameterizedTypeNameImpl( this, m_RawType.nestedClass( requireNonNullArgument( name, "name" ) ), typeArguments.stream().map( t -> (TypeNameImpl) t ).collect( toList() ), new ArrayList<>() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  nestedClass()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final ClassNameImpl rawType() { return m_RawType; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final List<TypeName> typeArguments()
    {
        final var retValue = m_TypeArguments.stream()
            .map( typeArgument -> (TypeName) typeArgument )
            .toList();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  typeArguments()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final ParameterizedTypeNameImpl withoutAnnotations()
    {
        final var retValue = new ParameterizedTypeNameImpl( m_EnclosingType, m_RawType.withoutAnnotations(), m_TypeArguments, new ArrayList<>() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  withoutAnnotations()
}
//  class ParameterizedTypeNameImpl

/*
 *  End of File
 */