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

import javax.lang.model.type.ArrayType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.ArrayTypeNameImpl;

/**
 *  The specialisation of
 *  {@link TypeName}
 *  for array types.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ArrayTypeName.java 1085 2024-01-05 16:23:28Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: ArrayTypeName.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public sealed interface ArrayTypeName extends TypeName
    permits ArrayTypeNameImpl
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public ArrayTypeName annotated( final List<AnnotationSpec> annotations );

    /**
     *  Returns this array's component type.
     *
     *  @return The component type.
     */
    public TypeName componentType();

    /**
     *  Returns an instance of {@code ArrayTypeName} for an array type that is
     *  equivalent to {@code mirror}.
     *
     *  @param  mirror  The mirror for the array.
     *  @return The new instance of {@code ArrayTypeName}.
     */
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.2.0" )
    public static ArrayTypeName from( final ArrayType mirror )
    {
        final var retValue = ArrayTypeNameImpl.from( mirror, new LinkedHashMap<>() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns an instance of {@code ArrayTypeName} for an array type that is
     *  equivalent to {@code type}.
     *
     *  @param  type    The array type.
     *  @return The new instance of {@code ArrayTypeName}.
     */
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.2.0" )
    public static ArrayTypeName from( final GenericArrayType type )
    {
        final var retValue = ArrayTypeNameImpl.from( type, new LinkedHashMap<>() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns an array type whose elements are all instances of the given
     *  component type.
     *
     *  @param  componentType   The component type.
     *  @return The new instance of {@code ArrayTypeName}.
     */
    @API( status = STABLE, since = "0.0.5" )
    public static ArrayTypeName of( final TypeName componentType )
    {
        return ArrayTypeNameImpl.of( componentType );
    }   //  of()

    /**
     *  Returns an array type whose elements are all instances of the given
     *  component type.
     *
     *  @param  componentType   The component type.
     *  @return The new instance of {@code ArrayTypeName}.
     */
    @API( status = STABLE, since = "0.0.5" )
    public static ArrayTypeName of( final Type componentType )
    {
        return ArrayTypeNameImpl.of( componentType );
    }   //  of()

    /**
     *  {@inheritDoc}
     */
    @Override
    public TypeName withoutAnnotations();
}
//  interface ArrayTypeName

/*
 *  End of File
 */