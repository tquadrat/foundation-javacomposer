/*
 * ============================================================================
 * Copyright © 2015 Square, Inc.
 * Copyright for the modifications © 2018-2ß21 by Thomas Thrien.
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

import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import javax.lang.model.type.TypeMirror;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.TypeNameImpl;

/**
 *  <p>{@summary The specification for a representation of any type in Java's
 *  type system, plus {@code void}.} The implementations of this interface
 *  class are identifiers for primitive types like {@code int} and raw
 *  reference types like
 *  {@link String}
 *  and
 *  {@link java.util.List List}.
 *  It also identifies composite types like
 *  {@code char[]}
 *  and
 *  {@link java.util.Set Set&lt;Long&gt;}.</p>
 *  <p>Type names are dumb identifiers only and do not model the values they
 *  name. For example, the type name for {@code java.lang.List} doesn't know
 *  about the
 *  {@link java.util.List#size() size()}
 *  method, the fact that lists are collections, or even that it accepts a
 *  single type parameter.</p>
 *  <p>Instances of this class are immutable value objects that implement
 *  {@link Object#equals(Object) equals()}
 *  and
 *  {@link Object#hashCode()}
 *  properly.</p>
 *
 *  <h2>Referencing existing types</h2>
 *  <p>Primitives and {@code void} are constants that can be referenced directly:
 *  see for example
 *  {@link Primitives#INT},
 *  {@link Primitives#DOUBLE},
 *  and
 *  {@link Primitives#VOID}.</p>
 *  <p>In an annotation processor, a type name instance for a type mirror can
 *  be obtained by calling
 *  {@link #get(TypeMirror)}.
 *  In reflection code,
 *  {@link #get(Type)}
 *  can be used.</p>
 *
 *  <h2>Defining new types</h2>
 *  <p>New reference types like {@code com.example.HelloWorld} can be created
 *  with
 *  {@link ClassName#from(CharSequence,CharSequence,CharSequence...)}.
 *  To build composite types like {@code char[]} and {@code Set<Long>}, the
 *  factory methods on
 *  {@link ArrayTypeName},
 *  {@link ParameterizedTypeName},
 *  {@link TypeVariableName},
 *  and
 *  {@link WildcardTypeName}
 *  should be used.</p>
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TypeName.java 997 2022-01-26 14:55:05Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: TypeName.java 997 2022-01-26 14:55:05Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public sealed interface TypeName
    permits ArrayTypeName, ClassName, ParameterizedTypeName, TypeVariableName, WildcardTypeName, TypeNameImpl
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a new instance for an implementation of {@code TypeName} as a
     *  copy of this one, but with the given annotations added.
     *
     *  @param  annotations The annotations.
     *  @return The new instance.
     */
    public default TypeName annotated( final AnnotationSpec... annotations )
    {
        return annotated( List.of( requireNonNullArgument( annotations, "annotations" ) ) );
    }   //  annotated()

    /**
     *  Creates a new instance for an implementation of {@code TypeName} as a
     *  copy of this one, but with the given annotations added.
     *
     *  @param  annotations The annotations.
     *  @return The new instance.
     */
    public TypeName annotated( final List<AnnotationSpec> annotations );

    /**
     *  Returns the array component for the given type name; the return value
     *  is empty if the given type is not an array.
     *
     *  @param  type    The type name.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the type name of the array component.
     */
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.0.5" )
    public static Optional<TypeName> arrayComponent( final TypeName type )
    {
        return type instanceof ArrayTypeName arrayType ? Optional.of( arrayType.componentType() ) : Optional.empty();
    }   //  arrayComponent()

    /**
     *  Returns the given type name as an array; the return value is empty if
     *  it is not an array.
     *
     *  @param  type    The type name.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the array type name.
     */
    @SuppressWarnings( {"ClassReferencesSubclass", "StaticMethodOnlyUsedInOneClass"} )
    @API( status = STABLE, since = "0.0.5" )
    public static Optional<ArrayTypeName> asArray( final TypeName type )
    {
        final var retValue = TypeNameImpl.asArray( type ).map( a -> (ArrayTypeName) a );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  asArray()

    /**
     *  Returns a boxed type if this is a primitive type (like
     *  {@link Integer}
     *  for {@code int}) or {@code void}. Returns this type if boxing doesn't
     *  apply.
     *
     *  @return The boxed if necessary, otherwise this.
     */
    public TypeName box();

    /**
     *  {@inheritDoc}
     */
    @Override
    public boolean equals( final Object o );

    /**
     *  Returns a type name equivalent to that from the given
     *  {@link TypeMirror}
     *  instance.
     *
     *  @param  mirror  The given type mirror instance.
     *  @return The respective type name.
     */
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.2.0" )
    public static TypeName from( final TypeMirror mirror )
    {
        final var retValue = TypeNameImpl.from( mirror );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a type name equivalent to that of the given
     *  {@link Type}
     *  instance.
     *
     *  @param  type    The type.
     *  @return The respective type name for the given {@code Type} instance.
     */
    @API( status = STABLE, since = "0.2.0" )
    public static TypeName from( final Type type ) { return TypeNameImpl.from( type ); }

    /**
     *  Returns a type name equivalent to that from the given
     *  {@link TypeMirror}
     *  instance.
     *
     *  @param  mirror  The given type mirror instance.
     *  @return The respective type name.
     *
     *  @deprecated Use
     *      {@link #from(TypeMirror)}
     *      instead.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static TypeName get( final TypeMirror mirror ) { return from( mirror ); }

    /**
     *  Returns a type name equivalent to that of the given
     *  {@link Type}
     *  instance.
     *
     *  @param  type    The type.
     *  @return The respective type name for the given {@code Type} instance.
     *
     *  @deprecated Use
     *      {@link #from(Type)}
     *      instead.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static TypeName get( final Type type ) { return from( type ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public int hashCode();

    /**
     *  Checks whether this type name is annotated.
     *
     *  @return {@code true} if it is annotated, {@code false} otherwise.
     */
    public boolean isAnnotated();

    /**
     *  Checks whether this type name represents a box primitive type.
     *
     *  @return {@code true} if this is a boxed primitive type like
     *      {@link Integer}.
     *      {@code false} for all other types including unboxed primitives and
     *      {@link java.lang.Void}.
     */
    public boolean isBoxedPrimitive();

    /**
     *  Checks whether this type name represents a primitive type.
     *
     *  @return {@code true} if this is a primitive type like {@code int}.
     *      {@code false} for all other types including boxed primitives and
     *      {@code void}.
     */
    public boolean isPrimitive();

    /**
     *  {@inheritDoc}
     */
    @Override
    public String toString();

    /**
     *  Returns an unboxed type if this is a boxed primitive type (like
     *  {@code int} for
     *  {@link Integer})
     *  or {@code Void}. Returns this type if it is already unboxed.
     *
     *  @return The unboxed type if applicable, or this.
     *  @throws UnsupportedOperationException   This type isn't eligible for
     *      unboxing.
     */
    public TypeName unbox();

    /**
     *  Creates a new instance for an implementation of {@code TypeName} as a
     *  copy of this one, but without any annotations.
     *
     *  @return The new instance.
     */
    public TypeName withoutAnnotations();
}
//  interface TypeName

/*
 *  End of File
 */