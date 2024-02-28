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

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.javacomposer.internal.ClassNameImpl.OBJECT;
import static org.tquadrat.foundation.lang.Objects.checkState;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidNonNullArgument;

import javax.lang.model.element.TypeParameterElement;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.AnnotationSpec;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeVariableName;
import org.tquadrat.foundation.javacomposer.WildcardTypeName;

/**
 *  The implementation of
 *  {@link TypeNameImpl}
 *  for wildcard named types.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: WildcardTypeNameImpl.java 1085 2024-01-05 16:23:28Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: WildcardTypeNameImpl.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public final class WildcardTypeNameImpl extends TypeNameImpl implements WildcardTypeName
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The lower bounds.
     */
    private final List<TypeNameImpl> m_LowerBounds;

    /**
     *  The upper bounds.
     */
    private final List<TypeNameImpl> m_UpperBounds;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code WildcardTypeNameImpl} instance.
     *
     *  @param  upperBounds The upper bounds.
     *  @param  lowerBounds The lower bounds.
     */
    public WildcardTypeNameImpl( final List<? extends TypeNameImpl> upperBounds, final List<? extends TypeNameImpl> lowerBounds )
    {
        this( upperBounds, lowerBounds, new ArrayList<>() );
    }   //  WildcardTypeNameImpl()

    /**
     *  Creates a new {@code WildcardTypeNameImpl} instance.
     *
     *  @param  upperBounds The upper bounds.
     *  @param  lowerBounds The lower bounds.
     *  @param  annotations The annotations.
     */
    public WildcardTypeNameImpl( final List<? extends TypeNameImpl> upperBounds, final List<? extends TypeNameImpl> lowerBounds, final List<AnnotationSpecImpl> annotations )
    {
        super( annotations );

        m_UpperBounds = List.copyOf(
            requireValidNonNullArgument( upperBounds,
                "upperBounds",
                u -> u.size() ==1,
                $ -> "unexpected extends bounds: %s".formatted( upperBounds ) ) );
        m_LowerBounds = List.copyOf( requireNonNullArgument( lowerBounds, "lowerBounds" ) );

        for( final var upperBound : m_UpperBounds )
        {
            checkState( !upperBound.isPrimitive() && upperBound != VOID_PRIMITIVE, () -> new ValidationException( "invalid upper bound: %s".formatted( upperBound ) ) );
        }
        for( final var lowerBound : m_LowerBounds )
        {
            checkState( !lowerBound.isPrimitive() && lowerBound != VOID_PRIMITIVE, () -> new ValidationException( "invalid lower bound: %s".formatted( lowerBound ) ) );
        }
    }   //  WildcardTypeNameImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final WildcardTypeNameImpl annotated( final List<AnnotationSpec> annotations )
    {
        final var retValue = new WildcardTypeNameImpl( m_UpperBounds, m_LowerBounds, concatAnnotations( annotations ) );

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
        if( m_LowerBounds.size() == 1 )
        {
            retValue.emit( "? super $T", m_LowerBounds.getFirst() );
        }
        else
        {
            if( m_UpperBounds.getFirst().equals( OBJECT ) )
            {
                retValue.emit( "?" );
            }
            else
            {
                retValue.emit( "? extends $T", m_UpperBounds.getFirst() );
            }
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  emit()

    /**
     *  Returns an instance of
     *  {@link TypeNameImpl}
     *  for the given type mirror.
     *
     *  @param  mirror  The type mirror for a wildcard type.
     *  @return The respective {@code TypeName} instance.
     */
    @API( status = STABLE, since = "0.2.0" )
    public static final TypeNameImpl from( final javax.lang.model.type.WildcardType mirror )
    {
        final var retValue = from( mirror, Map.of() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns an instance of
     *  {@link TypeNameImpl}
     *  for the given type mirror, with the given type variables applied to it.
     *
     *  @param  mirror  The type mirror for a wildcard type.
     *  @param  typeVariables   The type variables.
     *  @return The respective {@code TypeName} instance.
     */
    @API( status = STABLE, since = "0.2.0" )
    static final TypeNameImpl from( final javax.lang.model.type.WildcardType mirror, final Map<TypeParameterElement,TypeVariableNameImpl> typeVariables )
    {
        final var extendsBound = requireNonNullArgument( mirror, "mirror" ).getExtendsBound();
        requireNonNullArgument( typeVariables, "typeVariables" );
        final TypeNameImpl retValue;
        if( isNull( extendsBound ) )
        {
            final var superBound = mirror.getSuperBound();
            if( isNull( superBound ) )
            {
                retValue = subtypeOf( Object.class );
            }
            else
            {
                retValue = supertypeOf( TypeNameImpl.from( superBound, typeVariables ) );
            }
        }
        else
        {
            retValue = subtypeOf( TypeNameImpl.from( extendsBound, typeVariables ) );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns an instance of
     *  {@link TypeNameImpl}
     *  for the given wildcard type.
     *
     *  @param  wildcardName    The wildcard type.
     *  @return The respective {@code TypeName} instance.
     */
    @API( status = STABLE, since = "0.2.0" )
    public static final TypeNameImpl from( final WildcardType wildcardName )
    {
        final var retValue = from( wildcardName, Map.of() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns an instance of
     *  {@link TypeNameImpl}
     *  for the given wildcard type, with the given type variables applied to it.
     *
     *  @param  wildcardName    The wildcard type.
     *  @param  typeVariables   The type variables.
     *  @return The respective {@code TypeName} instance.
     */
    @API( status = STABLE, since = "0.2.0" )
    static final TypeNameImpl from( final WildcardType wildcardName, final Map<Type,TypeVariableName> typeVariables )
    {
        final var retValue = new WildcardTypeNameImpl( list( wildcardName.getUpperBounds(), typeVariables ), list( wildcardName.getLowerBounds(), typeVariables ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a subtype for the given type.
     *
     *  @param  type    The type.
     *  @return The subtype.
     */
    public static final WildcardTypeNameImpl subtypeOf( final Type type )
    {
        final var retValue = subtypeOf( TypeNameImpl.from( type ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  subtypeOf()

    /**
     *  Returns a type that represents an unknown type that extends the given
     *  type. For example, if {@code type} is
     *  {@link CharSequence CharSequence.class},
     *  this returns {@code ? extends CharSequence}. If {@code type} is
     *  {@link Object Object.class},
     *  this returns {@code ?}, which is shorthand for
     *  {@code ? extends Object}.
     *
     *  @param  type    The type.
     *  @return The subtype.
     */
    public static final WildcardTypeNameImpl subtypeOf( final TypeName type )
    {
        final var retValue = new WildcardTypeNameImpl( List.of( (TypeNameImpl) type ), List.of() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  subtypeOf()

    /**
     *  Returns a super type for the given type.
     *
     *  @param  type    The type.
     *  @return The super type.
     */
    public static final WildcardTypeNameImpl supertypeOf( final Type type )
    {
        final var retValue = supertypeOf( TypeName.from( type ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  supertypeOf()

    /**
     *  Returns a type that represents an unknown supertype of the given type.
     *  For example, if {@code type} is
     *  {@link String String.class},
     *  this returns {@code ? super String}.
     *
     *  @param  type    The type.
     *  @return The super type.
     */
    public static final WildcardTypeNameImpl supertypeOf( final TypeName type )
    {
        final var retValue = new WildcardTypeNameImpl( List.of( OBJECT ), List.of( (TypeNameImpl) type ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  supertypeOf()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final WildcardTypeNameImpl withoutAnnotations()
    {
        final var retValue = new WildcardTypeNameImpl( m_UpperBounds, m_LowerBounds );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  withoutAnnotations()
}
//  class WildcardTypeNameImpl

/*
 *  End of File
 */