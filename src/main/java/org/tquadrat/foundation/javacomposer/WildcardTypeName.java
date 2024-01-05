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

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.WildcardTypeNameImpl;

/**
 *  The specialisation of
 *  {@link TypeName}
 *  for wildcard named types.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: WildcardTypeName.java 1085 2024-01-05 16:23:28Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: WildcardTypeName.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public sealed interface WildcardTypeName extends TypeName
    permits WildcardTypeNameImpl
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public WildcardTypeName annotated( final List<AnnotationSpec> annotations );

    /**
     *  Returns an instance of
     *  {@link TypeName}
     *  for the given type mirror.
     *
     *  @param  mirror  The type mirror for a wildcard type.
     *  @return The respective {@code TypeName} instance.
     */
    @API( status = STABLE, since = "0.2.0" )
    public static TypeName from( final javax.lang.model.type.WildcardType mirror )
    {
        final var retValue = WildcardTypeNameImpl.from( mirror );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns an instance of
     *  {@link TypeName}
     *  for the given wildcard type.
     *
     *  @param  wildcardName    The wildcard type.
     *  @return The respective {@code TypeName} instance.
     */
    @API( status = STABLE, since = "0.2.0" )
    public static TypeName from( final WildcardType wildcardName )
    {
        final var retValue = WildcardTypeNameImpl.from( wildcardName );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a subtype for the given type.
     *
     *  @param  type    The type.
     *  @return The subtype.
     */
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.0.5" )
    public static WildcardTypeName subtypeOf( final Type type )
    {
        final var retValue = WildcardTypeNameImpl.subtypeOf( type );

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
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.0.5" )
    public static WildcardTypeName subtypeOf( final TypeName type )
    {
        final var retValue = WildcardTypeNameImpl.subtypeOf( type );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  subtypeOf()

    /**
     *  Returns a super type for the given type.
     *
     *  @param  type    The type.
     *  @return The super type.
     */
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.0.5" )
    public static WildcardTypeName supertypeOf( final Type type )
    {
        final var retValue = WildcardTypeNameImpl.supertypeOf( type );

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
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.0.5" )
    public static WildcardTypeName supertypeOf( final TypeName type )
    {
        final var retValue = WildcardTypeNameImpl.supertypeOf( type );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  supertypeOf()

    /**
     *  {@inheritDoc}
     */
    @Override
    public WildcardTypeName withoutAnnotations();
}
//  interface WildcardTypeName

/*
 *  End of File
 */