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
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.javacomposer.internal.ClassNameImpl.OBJECT;
import static org.tquadrat.foundation.lang.Objects.checkState;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.util.StringUtils.format;

import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.AnnotationSpec;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeVariableName;

/**
 *  The implementation of
 *  {@link TypeNameImpl}
 *  for type variable names.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TypeVariableNameImpl.java 943 2021-12-21 01:34:32Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: TypeVariableNameImpl.java 943 2021-12-21 01:34:32Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public final class TypeVariableNameImpl extends TypeNameImpl implements TypeVariableName
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The bounds.
     */
    private final List<TypeNameImpl> m_Bounds;

    /**
     *  The name of this type variable.
     */
    private final String m_Name;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code TypeVariableNameImpl} instance.
     *
     *  @param  name    The name.
     *  @param  bounds  The bounds.
     */
    public TypeVariableNameImpl( final String name, final List<TypeNameImpl> bounds )
    {
        this( name, bounds, new ArrayList<>() );
    }   //  TypeVariableNameImpl()

    /**
     *  Creates a new {@code TypeVariableNameImpl} instance.
     *
     *  @param  name    The name.
     *  @param  bounds  The bounds.
     *  @param  annotations The annotations.
     */
    public TypeVariableNameImpl( final String name, final List<TypeNameImpl> bounds, final List<AnnotationSpecImpl> annotations )
    {
        super( annotations );
        m_Name = requireNonNullArgument( name, "name" );
        m_Bounds = requireNonNullArgument( bounds, "bounds" );

        for( final var bound : m_Bounds )
        {
            checkState( !bound.isPrimitive() && bound != VOID_PRIMITIVE, () -> new ValidationException( format( "invalid bound: %s", bound ) ) );
        }
    }   //  TypeVariableNameImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "CastToConcreteClass" )
    @Override
    public final TypeVariableNameImpl annotated( final List<AnnotationSpec> annotations )
    {
        final var retValue = new TypeVariableNameImpl( m_Name, m_Bounds, requireNonNullArgument( annotations, "annotations" ).stream().map( a -> (AnnotationSpecImpl) a ).collect( toList() ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  annotated()

    /**
     *  Return the bounds for this type variable name.
     *
     *  @return The bounds.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final List<TypeNameImpl> bounds() { return List.copyOf( m_Bounds ); }

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( {"PublicMethodNotExposedInInterface", "UseOfConcreteClass"} )
    @Override
    public final CodeWriter emit( final CodeWriter out ) throws UncheckedIOException
    {
        final var retValue = requireNonNullArgument( out, "out" );
        emitAnnotations( retValue );
        retValue.emitAndIndent( m_Name );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  emit()

    /**
     *  Returns type variable equivalent to the given type.
     *
     *  @param  type    The type.
     *  @return The type variable name.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @API( status = STABLE, since = "0.2.0" )
    public static final TypeVariableNameImpl from( final java.lang.reflect.TypeVariable<?> type )
    {
        final var retValue = from( type, Map.of() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns type variable equivalent to the given type, after applying the
     *  given type variables
     *  .
     *  @param  type    The type.
     *  @param  typeVariables   The type variables.
     *  @return The type variable name.
     */
    @SuppressWarnings( {"BoundedWildcard", "CastToConcreteClass"} )
    @API( status = STABLE, since = "0.2.0" )
    public static final TypeVariableNameImpl from( final java.lang.reflect.TypeVariable<?> type, final Map<Type,TypeVariableName> typeVariables )
    {
        var retValue = (TypeVariableNameImpl) requireNonNullArgument( typeVariables, "typeVariables" )
            .get( requireNonNullArgument( type, "type" ) );
        if( isNull( retValue) )
        {
            final List<TypeNameImpl> bounds = new ArrayList<>();
            final var visibleBounds = unmodifiableList( bounds );
            retValue = new TypeVariableNameImpl( type.getName(), visibleBounds );
            final Map<Type,TypeVariableName> typeVariablesCopy = new HashMap<>( typeVariables );
            typeVariablesCopy.put( type, retValue );
            for( final var bound : type.getBounds() )
            {
                bounds.add( TypeNameImpl.from( bound, Map.copyOf( typeVariablesCopy ) ) );
            }
            bounds.remove( OBJECT );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a new type variable named {@code name} without bounds.
     *
     *  @param  name    The name.
     *  @return The type variable name.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @API( status = STABLE, since = "0.2.0" )
    public static final TypeVariableNameImpl from( final String name )
    {
        final var retValue = of( name, List.of() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a new type variable named {@code name} with {@code bounds}.
     *
     *  @param  name    The name.
     *  @param  bounds  The bounds.
     *  @return The type variable name.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @API( status = STABLE, since = "0.2.0" )
    public static final TypeVariableNameImpl from( final String name, final Type... bounds )
    {
        final var retValue = of( name, list( requireNonNullArgument( bounds, "bounds" ) ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a new type variable named {@code name} with {@code bounds}.
     *
     *  @param  name    The name.
     *  @param  bounds  The bounds.
     *  @return The type variable name.
     */
    @SuppressWarnings( "CastToConcreteClass" )
    @API( status = STABLE, since = "0.2.0" )
    public static final TypeVariableNameImpl from( final String name, final TypeName... bounds )
    {
        final var retValue = of( name, stream( requireNonNullArgument( bounds, "bounds" ) ).map( b -> (TypeNameImpl) b ).collect( toList() ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a type variable name that is equivalent to the given element.
     *
     *  @param  element The element.
     *  @return The new type variable name.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @API( status = STABLE, since = "0.2.0" )
    public static final TypeVariableNameImpl from( final TypeParameterElement element )
    {
        final var name = requireNonNullArgument( element, "element" ).getSimpleName().toString();
        final var boundsMirrors = element.getBounds();

        final var boundsTypeNames = boundsMirrors.stream()
            .map( m -> TypeNameImpl.from( m, Map.of() ) )
            .toList();
        final var retValue = of( name, boundsTypeNames );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a type variable name that is equivalent to the given mirror.
     *
     *  @param  mirror  The mirror.
     *  @return The new type variable name.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @API( status = STABLE, since = "0.2.0" )
    public static TypeVariableNameImpl from( final TypeVariable mirror )
    {
        final var retValue = from( (TypeParameterElement) requireNonNullArgument( mirror, "mirror" ).asElement() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Makes an instance of {@code TypeVariableName} for the given
     *  {@link javax.lang.model.type.TypeMirror}.
     *  This form is used internally to avoid infinite recursion in cases like
     *  {@code Enum<E extends Enum<E>>}. When such a thing is encountered, a
     *  {@code TypeVariableName} without bounds is made, and that is added to
     *  the {@code typeVariables} map before looking up the bounds. Then if
     *  encountered again while constructing the bounds, this
     *  {@code TypeVariableName} can be just returned from the map. And, the
     *  code that put the entry in {@code variables} will make sure that the
     *  bounds are filled in before returning.
     *
     *  @param  mirror  The {@code TypeMirror}.
     *  @param  typeVariables   The type variables.
     *  @return The new type variable name.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @API( status = STABLE, since = "0.2.0" )
    public static final TypeVariableNameImpl from( final TypeVariable mirror, final Map<TypeParameterElement,TypeVariableNameImpl> typeVariables )
    {
        final var element = (TypeParameterElement) requireNonNullArgument( mirror, "mirror" ).asElement();
        var retValue = requireNonNullArgument( typeVariables, "typeVariables" ).get( element );
        if( isNull( retValue ) )
        {
            /*
             * This code is a dirty hack: bounds will be modified after the
             * return value was created with that list.
             */
            final List<TypeNameImpl> bounds = new ArrayList<>();
            retValue = new TypeVariableNameImpl( element.getSimpleName().toString(), bounds );
            typeVariables.put( element, retValue );
            for( final TypeMirror typeMirror : element.getBounds() )
            {
                bounds.add( TypeNameImpl.from( typeMirror, typeVariables ) );
            }
            bounds.remove( OBJECT );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns type variable equivalent to the given type.
     *
     *  @param  type    The type.
     *  @return The type variable name.
     *
     *  @deprecated Use
     *      {@link #from(java.lang.reflect.TypeVariable)}
     *      instead.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final TypeVariableNameImpl get( final java.lang.reflect.TypeVariable<?> type ) { return from( type ); }

    /**
     *  Returns type variable equivalent to the given type, after applying the
     *  given type variables
     *  .
     *  @param  type    The type.
     *  @param  typeVariables   The type variables.
     *  @return The type variable name.
     *
     *  @deprecated Use
     *      {@link #from(java.lang.reflect.TypeVariable, Map)}
     *      instead.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    @SuppressWarnings( {"BoundedWildcard", "UseOfConcreteClass"} )
    public static final TypeVariableNameImpl get( final java.lang.reflect.TypeVariable<?> type, final Map<Type,TypeVariableName> typeVariables )
    {
        final var retValue = from( type, typeVariables );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  get()

    /**
     *  Returns a new type variable named {@code name} without bounds.
     *
     *  @param  name    The name.
     *  @return The type variable name.
     *
     *  @deprecated Use
     *      {@link #from(String)}
     *      instead.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final TypeVariableNameImpl get( final String name ) { return from( name ); }

    /**
     *  Returns a new type variable named {@code name} with {@code bounds}.
     *
     *  @param  name    The name.
     *  @param  bounds  The bounds.
     *  @return The type variable name.
     *
     *  @deprecated Use
     *      {@link #from(String,Type...)}
     *      instead.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final TypeVariableNameImpl get( final String name, final Type... bounds )
    {
        final var retValue = from( name, bounds );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  get()

    /**
     *  Returns a new type variable named {@code name} with {@code bounds}.
     *
     *  @param  name    The name.
     *  @param  bounds  The bounds.
     *  @return The type variable name.
     *
     *  @deprecated Use
     *      {@link #from(String,TypeName...)}
     *      instead.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final TypeVariableNameImpl get( final String name, final TypeName... bounds )
    {
        final var retValue = from( name, bounds );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  get()

    /**
     *  Returns a type variable name that is equivalent to the given element.
     *
     *  @param  element The element.
     *  @return The new type variable name.
     *
     *  @deprecated Use
     *      {@link #from(TypeParameterElement)}
     *      instead.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final TypeVariableNameImpl get( final TypeParameterElement element ) { return from( element ); }

    /**
     *  Returns a type variable name that is equivalent to the given mirror.
     *
     *  @param  mirror  The mirror.
     *  @return The new type variable name.
     *
     *  @deprecated Use
     *      {@link #from(TypeVariable)}
     *      instead.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static TypeVariableNameImpl get( final TypeVariable mirror ) { return from( mirror ); }

    /**
     *  Makes an instance of {@code TypeVariableName} for the given
     *  {@link javax.lang.model.type.TypeMirror}.
     *  This form is used internally to avoid infinite recursion in cases like
     *  {@code Enum<E extends Enum<E>>}. When such a thing is encountered, a
     *  {@code TypeVariableName} without bounds is made, and that is added to
     *  the {@code typeVariables} map before looking up the bounds. Then if
     *  encountered again while constructing the bounds, this
     *  {@code TypeVariableName} can be just returned from the map. And, the
     *  code that put the entry in {@code variables} will make sure that the
     *  bounds are filled in before returning.
     *
     *  @param  mirror  The {@code TypeMirror}.
     *  @param  typeVariables   The type variables.
     *  @return The new type variable name.
     *
     *  @deprecated Use
     *      {@link #from(TypeVariable, Map)}
     *      instead.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final TypeVariableNameImpl get( final TypeVariable mirror, final Map<TypeParameterElement,TypeVariableNameImpl> typeVariables )
    {
        final var retValue = from( mirror, typeVariables );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  get()

    /**
     *  Returns the name of this type variable.
     *
     *  @return The name.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final String name() { return m_Name; }

    /**
     *  Creates a new instance of {@code TypeVariableName} from the given name
     *  and the given bounds.
     *
     *  @param  name    The name.
     *  @param  bounds  The bounds.
     *  @return The new instance.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private static final TypeVariableNameImpl of( final String name, final Collection<TypeNameImpl> bounds )
    {
        //---* Strip java.lang.Object from bounds if it is present *-----------
        final var boundsNoObject = bounds.stream()
            .filter( b -> !b.equals( OBJECT ) )
            .toList();
        final var retValue = new TypeVariableNameImpl( name, boundsNoObject );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  of()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "CastToConcreteClass" )
    @Override
    public final TypeVariableNameImpl withBounds( final List<TypeName> bounds )
    {
        final var newBounds = new ArrayList<>( m_Bounds );
        newBounds.addAll( requireNonNullArgument( bounds, "bounds" )
            .stream()
            .map( t -> (TypeNameImpl) t )
            .toList()
        );
        final var retValue = new TypeVariableNameImpl( m_Name, List.copyOf( newBounds ), annotations() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  withBounds()

    /**
     *  Returns a new {@code TypeVariableName} instance as a copy of this one,
     *  with the given bounds added.
     *
     *  @param  bounds  The additional bounds.
     *  @return The new type variable name.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Override
    public final TypeVariableNameImpl withBounds( final Type... bounds )
    {
        final var newBounds = new ArrayList<>( m_Bounds );
        newBounds.addAll( TypeNameImpl.list( requireNonNullArgument( bounds, "bounds" ) ) );
        final var retValue = new TypeVariableNameImpl( m_Name, List.copyOf( newBounds ), annotations() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  withBounds()

    /**
     *  Returns a new {@code TypeVariableName} instance as a copy of this one,
     *  with the given bounds added.
     *
     *  @param  bounds  The additional bounds.
     *  @return The new type variable name.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Override
    public final TypeVariableNameImpl withBounds( final TypeName... bounds )
    {
        final var retValue = withBounds( List.of( bounds ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  withBounds()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Override
    public final TypeVariableNameImpl withoutAnnotations() { return new TypeVariableNameImpl( m_Name, m_Bounds ); }
}
//  class TypeVariableNameImpl

/*
 *  End of File
 */