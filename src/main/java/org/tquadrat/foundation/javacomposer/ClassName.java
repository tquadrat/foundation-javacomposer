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

package org.tquadrat.foundation.javacomposer;

import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.STABLE;

import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.ClassNameImpl;

/**
 *  The specification for a fully-qualified class name for top-level and
 *  member classes.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ClassName.java 943 2021-12-21 01:34:32Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: ClassName.java 943 2021-12-21 01:34:32Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public sealed interface ClassName extends TypeName, Comparable<ClassName>
    permits ClassNameImpl
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a new instance for an implementation of {@code ClassName} as a
     *  copy of this one, but with the given annotations added.
     *
     *  @param  annotations The annotations.
     *  @return The new instance.
     */
    @Override
    public ClassName annotated( final List<AnnotationSpec> annotations );

    /**
     *  Returns a new instance for an implementation of {@code ClassName} for
     *  the given fully-qualified class name string. This method assumes that
     *  the input is ASCII and follows typical Java style (lowercase package
     *  names, UpperCamelCase class names) and may produce incorrect results
     *  or throw
     *  {@link IllegalArgumentException}
     *  otherwise. For that reason,
     *  {@link #get(Class)},
     *  {@link #get(CharSequence,CharSequence,CharSequence...)},
     *  and
     *  {@link #get(TypeElement)}
     *  should be preferred as they can correctly create {@code ClassName}
     *  instances without such restrictions.
     *
     *  @param  classNameString The fully qualified class name.
     *  @return The new class name instance.
     *
     *  @deprecated Because of the limitations described above, this method is
     *      seen as inherently unsafe, and therefore it was decided to
     *      deprecate it.
     */
    @SuppressWarnings( {"ClassReferencesSubclass", "DeprecatedIsStillUsed"} )
    @Deprecated( since = "0.1.0" )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static ClassName bestGuess( final CharSequence classNameString )
    {
        final var retValue = ClassNameImpl.bestGuess( classNameString );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  bestGuess()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "AbstractMethodOverridesAbstractMethod" )
    @Override
    public int compareTo( final ClassName o );

    /**
     *  Returns the canonical form of the class name.
     *
     *  @return The canonical name.
     */
    public String canonicalName();

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "AbstractMethodOverridesAbstractMethod" )
    @Override
    public boolean equals( final Object o );

    /**
     *  Creates a new instance for an implementation of {@code ClassName} from
     *  an instance of
     *  {@link Class}.
     *
     *  @param  sourceClass The instance of {@code java.lang.Class}.
     *  @return The respective instance of {@code ClassName}.
     */
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.2.0" )
    public static ClassName from( final Class<?> sourceClass )
    {
        final var retValue = ClassNameImpl.from( sourceClass );

        // ---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns the class name for the given
     *  {@link TypeElement}
     *  instance.
     *
     *  @param  element The type element instance.
     *  @return The new class name instance.
     */
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.2.0" )
    public static ClassName from( final TypeElement element )
    {
        final var retValue = ClassNameImpl.from( element );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a class name created from the given parts.<br>
     *  <br>For example, calling this method with package name
     *  {@code "java.util"} and simple names {@code "Map"} and {@code "Entry"}
     *  yields {@code java.util.Map.Entry}.
     *
     *  @param  packageName The package name.
     *  @param  simpleName  The name of the top-level class.
     *  @param  simpleNames The names of the nested classes, from outer to
     *      inner.
     *  @return The new {@code ClassName} instance.
     */
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = STABLE, since = "0.2.0" )
    public static ClassName from( final CharSequence packageName, final CharSequence simpleName, final CharSequence... simpleNames )
    {
        final var retValue = ClassNameImpl.from( packageName, simpleName, simpleNames );

        // ---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Creates a new instance for an implementation of {@code ClassName} from
     *  an instance of
     *  {@link Class}.
     *
     *  @param  sourceClass The instance of {@code java.lang.Class}.
     *  @return The respective instance of {@code ClassName}.
     *
     *  @deprecated Use
     *      {@link #from(Class)}
     *      instead.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static ClassName get( final Class<?> sourceClass ) { return from( sourceClass ); }

    /**
     *  Returns the class name for the given
     *  {@link TypeElement}
     *  instance.
     *
     *  @param  element The type element instance.
     *  @return The new class name instance.
     *
     *  @deprecated Use
     *      {@link #from(TypeElement)}
     *      instead.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static ClassName get( final TypeElement element ) { return from( element ); }

    /**
     *  Returns a class name created from the given parts.<br>
     *  <br>For example, calling this method with package name
     *  {@code "java.util"} and simple names {@code "Map"} and {@code "Entry"}
     *  yields {@code java.util.Map.Entry}.
     *
     *  @param  packageName The package name.
     *  @param  simpleName  The name of the top-level class.
     *  @param  simpleNames The names of the nested classes, from outer to
     *      inner.
     *  @return The new {@code ClassName} instance.
     *
     *  @deprecated Use
     *      {@link #from(CharSequence, CharSequence, CharSequence...)}
     *      instead.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @SuppressWarnings( "ClassReferencesSubclass" )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static ClassName get( final CharSequence packageName, final CharSequence simpleName, final CharSequence... simpleNames )
    {
        final var retValue = from( packageName, simpleName, simpleNames );

        // ---* Done *----------------------------------------------------------
        return retValue;
    }   //  get()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "AbstractMethodOverridesAbstractMethod" )
    @Override
    public boolean isAnnotated();

    /**
     *  Returns a new instance for an implementation of {@code ClassName} for
     *  the specified {@code name} as nested inside this class.
     *
     *  @param  name    The name for the new nested class.
     *  @return The new instance.
     */
    public ClassName nestedClass( final CharSequence name );

    /**
     *  Returns the package name, like {@code java.util} for
     *  {@code java.util.Map.Entry}. Returns the empty String for the default
     *  package.
     *
     *  @return The package name.
     */
    public String packageName();

    /**
     *  Returns the enclosing class, like {@link Map} for
     *  {@code java.util.Map.Entry}. The return value will be
     *  {@linkplain Optional#empty() empty}
     *  if this class is not nested in another class.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the name of the enclosing class.
     */
    public Optional<ClassName> parentClass();

    /**
     *  Creates a class that shares the same enclosing package or class.
     *
     *  @param  name    The name of the peer class to create.
     *  @return The new instance for the peer class.
     */
    public ClassName peerClass( final CharSequence name );

    /**
     *  Returns the binary name of a class, as used by reflection.
     *
     *  @return The binary name.
     */
    public String reflectionName();

    /**
     *  Returns the simple name of this class, like {@code Entry} for
     *  {@code java.util.Map.Entry}.
     *
     *  @return The simple name.
     */
    public String simpleName();

    /**
     *  Returns a list of the simple names for this nesting group.
     *
     *  @return The simple names.
     */
    public List<String> simpleNames();

    /**
     *  Returns the top class in this nesting group.
     *
     *  @return The name of the top level class.
     */
    public ClassName topLevelClassName();

    /**
     *  Creates a new instance for an implementation of {@code ClassName} as a
     *  copy of this one, but without any annotations.
     *
     *  @return The new instance.
     */
    @Override
    public ClassName withoutAnnotations();
}
//  interface ClassName

/*
 *  End of File
 */