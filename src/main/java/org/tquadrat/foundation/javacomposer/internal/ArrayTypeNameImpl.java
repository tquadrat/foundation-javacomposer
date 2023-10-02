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

import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.ArrayType;
import java.io.UncheckedIOException;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.AnnotationSpec;
import org.tquadrat.foundation.javacomposer.ArrayTypeName;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeVariableName;

/**
 *  The implementation of
 *  {@link TypeNameImpl}
 *  for array types.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ArrayTypeNameImpl.java 1062 2023-09-25 23:11:41Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: ArrayTypeNameImpl.java 1062 2023-09-25 23:11:41Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public final class ArrayTypeNameImpl extends TypeNameImpl implements ArrayTypeName
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The array component type.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final TypeNameImpl m_ComponentType;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code ArrayTypeNameImpl} instance.
     *
     *  @param  componentType   The type of the array components.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public ArrayTypeNameImpl( final TypeNameImpl componentType ) { this( componentType, new ArrayList<>() ); }

    /**
     *  Creates a new {@code ArrayTypeNameImpl} instance.
     *
     *  @param  componentType   The type of the array components.
     *  @param  annotations The annotations for this array type name.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public ArrayTypeNameImpl( final TypeNameImpl componentType, final List<AnnotationSpecImpl> annotations )
    {
        super( annotations );
        m_ComponentType = requireNonNullArgument( componentType, "componentType" );
    }   //  ArrayTypeNameImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final ArrayTypeNameImpl annotated( final List<AnnotationSpec> annotations )
    {
        return new ArrayTypeNameImpl( getComponentType(), concatAnnotations( annotations ) );
    }   //  annotated()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final TypeName componentType() { return getComponentType(); }

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( {"PublicMethodNotExposedInInterface", "UseOfConcreteClass"} )
    @Override
    public final CodeWriter emit( final CodeWriter out ) throws UncheckedIOException { return emit( out, false ); }

    /**
     *  Emits this type name instance to the given
     *  {@link CodeWriter}.
     *
     *  @param  out The code writer.
     *  @param  varargs {@code true} if the array should be emitted as varargs
     *      (obviously this affects only arrays used in method signatures),
     *      {@code false} for the usual representation.
     *  @return The code writer.
     *  @throws UncheckedIOException Something went wrong when writing to the
     *      output target.
     */
    @SuppressWarnings( {"PublicMethodNotExposedInInterface", "UseOfConcreteClass"} )
    public final CodeWriter emit( final CodeWriter out, final boolean varargs ) throws UncheckedIOException
    {
        final var retValue = requireNonNullArgument( out, "out" );
        emitLeafType( retValue );
        emitBrackets( retValue, varargs );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  emit()

    /**
     *  Emits the annotations and the brackets for the array.
     *
     *  @param  out The code writer.
     *  @param  varargs {@code true} if the array should be emitted as varargs
     *      (obviously this affects only arrays used in method signatures),
     *      {@code false} for the usual representation.
     *  @return The code writer.
     *  @throws UncheckedIOException Something went wrong when writing to the
     *      output target.
     */
    @SuppressWarnings( {"UnusedReturnValue", "UseOfConcreteClass"} )
    private final CodeWriter emitBrackets( final CodeWriter out, final boolean varargs ) throws UncheckedIOException
    {
        final var retValue = requireNonNullArgument( out, "out" );
        if( isAnnotated() )
        {
            retValue.emit( " " );
            emitAnnotations( retValue );
        }

        final var arrayInArray = asArray( getComponentType() );
        if( arrayInArray.isPresent() )
        {
            retValue.emit( "[]" );
            arrayInArray.get().emitBrackets( retValue, varargs );
        }
        else
        {
            //---* Last bracket *----------------------------------------------
            retValue.emit( varargs ? "..." : "[]" );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  emitBrackets()

    /**
     *  Emits the array's component type.
     *
     *  @param  out The code writer.
     *  @return The code writer.
     *  @throws UncheckedIOException Something went wrong when writing to the
     *      output target.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final CodeWriter emitLeafType( final CodeWriter out ) throws UncheckedIOException
    {
        final var arrayInArray = asArray( getComponentType() );
        final var retValue = arrayInArray.isPresent()
            ? arrayInArray.get().emitLeafType( out )
            : getComponentType().emit( out );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  emitLeafType()

    /**
     *  Returns an instance of {@code ArrayTypeNameImpl} for an array type
     *  that is equivalent to {@code mirror}.
     *
     *  @param  mirror  The mirror for the array.
     *  @param  typeVariables   The type variables.
     *  @return The new instance of {@code ArrayTypeName}.
     */
    @API( status = STABLE, since = "0.2.0" )
    public static final ArrayTypeNameImpl from( final ArrayType mirror, final Map<TypeParameterElement,TypeVariableNameImpl> typeVariables )
    {
        final var retValue = new ArrayTypeNameImpl( TypeNameImpl.from( requireNonNullArgument( mirror, "mirror" ).getComponentType(), typeVariables ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns an instance of {@code ArrayTypeNameImpl} for an array type that
     *  is equivalent to {@code type}.
     *
     *  @param  type    The array type.
     *  @param  typeVariables   The type variables.
     *  @return The new instance of {@code ArrayTypeName}.
     */
    @API( status = STABLE, since = "0.2.0" )
    public static final ArrayTypeNameImpl from( final GenericArrayType type, final Map<Type,TypeVariableName> typeVariables )
    {
        final var retValue = of( TypeNameImpl.from( requireNonNullArgument( type, "type" ).getGenericComponentType(), typeVariables ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns an instance of {@code ArrayTypeNameImpl} for an array type
     *  that is equivalent to {@code mirror}.
     *
     *  @param  mirror  The mirror for the array.
     *  @param  typeVariables   The type variables.
     *  @return The new instance of {@code ArrayTypeName}.
     *
     *  @deprecated Use
     *      {@link #from(ArrayType,Map)}
     *      instead.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final ArrayTypeNameImpl get( final ArrayType mirror, final Map<TypeParameterElement,TypeVariableNameImpl> typeVariables )
    {
        final var retValue = from( mirror, typeVariables);

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  get()

    /**
     *  Returns an instance of {@code ArrayTypeNameImpl} for an array type that
     *  is equivalent to {@code type}.
     *
     *  @param  type    The array type.
     *  @param  typeVariables   The type variables.
     *  @return The new instance of {@code ArrayTypeName}.
     *
     *  @deprecated Use
     *      {@link #from(GenericArrayType,Map)}
     *      instead.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final ArrayTypeNameImpl get( final GenericArrayType type, final Map<Type,TypeVariableName> typeVariables )
    {
        final var retValue = from( type, typeVariables );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  get()

    /**
     *  Returns the array component type.
     *
     * @return The array component type.
     */
    @SuppressWarnings( {"PublicMethodNotExposedInInterface"} )
    public final TypeNameImpl getComponentType() { return m_ComponentType; }

    /**
     *  Returns an array type whose elements are all instances of the given
     *  component type.
     *
     *  @param  componentType   The component type.
     *  @return The new instance of {@code ArrayTypeName}.
     */
    public static ArrayTypeNameImpl of( final Type componentType )
    {
        return of( TypeNameImpl.from( requireNonNullArgument( componentType, "componentType" ) ) );
    }   //  of()

    /**
     *  Returns an array type whose elements are all instances of the given
     *  component type.
     *
     *  @param  componentType   The component type.
     *  @return The new instance of {@code ArrayTypeName}.
     */
    public static final ArrayTypeNameImpl of( final TypeName componentType )
    {
        return new ArrayTypeNameImpl( (TypeNameImpl) componentType );
    }   //  of()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final ArrayTypeNameImpl withoutAnnotations() { return new ArrayTypeNameImpl( getComponentType() ); }
}
//  class ArrayTypeNameImpl

/*
 *  End of File
 */