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

package org.tquadrat.foundation.javacomposer;

import static org.apiguardian.api.API.Status.STABLE;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.ParameterizedTypeNameImpl;

/**
 *  The specialisation of
 *  {@link TypeName}
 *  for parameterised types.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ParameterizedTypeName.java 1085 2024-01-05 16:23:28Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: ParameterizedTypeName.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public sealed interface ParameterizedTypeName extends TypeName
    permits ParameterizedTypeNameImpl
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public ParameterizedTypeName annotated( final List<AnnotationSpec> annotations );

    /**
     *  Returns a parameterised type, applying the given type arguments to the
     *  given raw type.
     *
     *  @param  rawType The class name for the new type.
     *  @param  typeArguments   The type arguments.
     *  @return The new instance of {@code ParameterizedTypeName}.
     */
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.2.0" )
    public static ParameterizedTypeName from( final Class<?> rawType, final Type... typeArguments )
    {
        final var retValue = ParameterizedTypeNameImpl.from( rawType, typeArguments );

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
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.2.0" )
    public static ParameterizedTypeName from( final ClassName rawType, final TypeName... typeArguments )
    {
        final var retValue = ParameterizedTypeNameImpl.from( rawType, typeArguments );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a parameterised type equivalent to the given type.
     *
     *  @param  type    The other type.
     *  @return The new instance of {@code ParameterizedTypeName}.
     */
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.2.0" )
    public static ParameterizedTypeName from( final ParameterizedType type )
    {
        final var retValue = ParameterizedTypeNameImpl.from( type, new LinkedHashMap<>() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a new {@code ParameterizedTypeName} instance for the specified
     *  name as nested inside this class.
     *
     *  @param  name    The name for the nested class.
     *  @return The new nested class.
     */
    public ParameterizedTypeName nestedClass( final CharSequence name );

    /**
     *  Returns a new {@code ParameterizedTypeName} instance for the specified
     *  name as nested inside this class, with the specified type arguments.
     *
     *  @param  name    The name for the nested class.
     *  @param  typeArguments   The type arguments.
     *  @return The new nested class.
     */
    public ParameterizedTypeName nestedClass( final CharSequence name, final List<TypeName> typeArguments );

    /**
     *  Returns the raw type for this parameterised type name.
     *
     *  @return The raw type.
     */
    public ClassName rawType();

    /**
     *  Returns the type arguments for this parameterised type name.
     *
     *  @return The type arguments.
     */
    public List<TypeName> typeArguments();

    /**
     *  {@inheritDoc}
     */
    @Override
    public TypeName withoutAnnotations();
}
//  interface ParameterizedTypeName

/*
 *  End of File
 */