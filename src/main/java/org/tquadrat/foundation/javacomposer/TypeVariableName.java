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
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeVariable;
import java.lang.reflect.Type;
import java.util.List;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.TypeVariableNameImpl;

/**
 *  The specialisation of
 *  {@link TypeName}
 *  for type variable names.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TypeVariableName.java 1085 2024-01-05 16:23:28Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: TypeVariableName.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public sealed interface TypeVariableName extends TypeName
    permits TypeVariableNameImpl
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public TypeVariableName annotated( final List<AnnotationSpec> annotations );

    /**
     *  Returns type variable equivalent to the given type.
     *
     *  @param  type    The type.
     *  @return The type variable name.
     */
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.2.0" )
    public static TypeVariableName from( final java.lang.reflect.TypeVariable<?> type )
    {
        final var retValue = TypeVariableNameImpl.from( type );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a new type variable named {@code name} without bounds.
     *
     *  @param  name    The name.
     *  @return The type variable name.
     */
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.2.0" )
    public static TypeVariableName from( final String name )
    {
        final var retValue = TypeVariableNameImpl.from( name );

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
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.2.0" )
    public static TypeVariableName from( final String name, final Type... bounds )
    {
        final var retValue = TypeVariableNameImpl.from( name, bounds );

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
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.2.0" )
    public static TypeVariableName from( final String name, final TypeName... bounds )
    {
        final var retValue = TypeVariableNameImpl.from( name, bounds );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a type variable name that is equivalent to the given element.
     *
     *  @param  element The element.
     *  @return The new type variable name.
     */
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.2.0" )
    public static TypeVariableName from( final TypeParameterElement element )
    {
        final var retValue = TypeVariableNameImpl.from( element );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a type variable name that is equivalent to the given mirror.
     *
     *  @param  mirror  The mirror.
     *  @return The new type variable name.
     */
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.2.0" )
    public static TypeVariableName from( final TypeVariable mirror )
    {
        final var retValue = TypeVariableNameImpl.from( (TypeParameterElement) requireNonNullArgument( mirror, "mirror" ).asElement() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a new instance for an implementation of
     *  {@code TypeVariableName} as a copy of this one, but with the given
     *  bounds added.
     *
     *  @param  bounds  The additional bounds.
     *  @return The new type variable name.
     */
    public TypeVariableName withBounds( final List<TypeName> bounds );

    /**
     *  Returns a new instance for an implementation of
     *  {@code TypeVariableName} as a copy of this one, but with the given
     *  bounds added.
     *
     *  @param  bounds  The additional bounds.
     *  @return The new type variable name.
     */
    public TypeVariableName withBounds( final Type... bounds );

    /**
     *  Returns a new instance for an implementation of
     *  {@code TypeVariableName} as a copy of this one, but with the given
     *  bounds added.
     *
     *  @param  bounds  The additional bounds.
     *  @return The new type variable name.
     */
    public TypeVariableName withBounds( final TypeName... bounds );

    /**
     *  {@inheritDoc}
     */
    @Override
    public TypeName withoutAnnotations();
}
//  interface TypeVariableName

/*
 *  End of File
 */