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

import static java.lang.Character.charCount;
import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.util.StringUtils.format;
import static org.tquadrat.foundation.util.UniqueIdUtils.randomUUID;

import javax.lang.model.SourceVersion;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.ValidationException;

/**
 *  <p>{@summary Assigns Java identifier names to avoid collisions, 'abuse' of
 *  keywords, and invalid characters.} To use it, first create an instance of
 *  this class and allocate all of the names that are needed. Typically this is
 *  a mix of user-supplied names and constants:</p>
 *  <pre><code>  NameAllocator nameAllocator = new NameAllocator();
 *  for( final var property : properties )
 *  {
 *      nameAllocator.newName( property.name(), property );
 *  }
 *  nameAllocator.newName( "sb", "string builder" );</code></pre>
 *  <p>Pass a unique tag object to each allocation. The tag scopes the name,
 *  and can be used to look up the allocated name later. Typically the tag is
 *  the object that is being named. In the above example we use
 *  {@code property} for the user-supplied property names, and
 *  {@code "string builder"} for our constant string builder.</p>
 *  <p>Once we've allocated names we can use them when generating code:</p>
 *  <pre><code>  MethodSpec.Builder builder = MethodSpec.methodBuilder( "toString" )
 *      .addAnnotation( Override.class )
 *      .addModifiers( Modifier.PUBLIC )
 *      .returns( String.class );
 *
 *  builder.addStatement( "$1T $2N = new $1T()", StringBuilder.class, nameAllocator.get( "string builder" ) );
 *  for( var property : properties )
 *  {
 *      builder.addStatement( "$N.append( $N )", nameAllocator.get( "string builder" ), nameAllocator.get( property ) );
 *  }
 *  builder.addStatement( "return $N", nameAllocator.get( "string builder" ) );
 *  return builder.build();</code></pre>
 *  <p>The above code generates unique names if presented with conflicts. Given
 *  user-supplied properties with names {@code ab} and {@code sb} this
 *  generates the following code:</p>
 *  <pre><code>  &#64;Override
 *  public String toString()
 *  {
 *    StringBuilder sb_ = new StringBuilder();
 *    sb_.append( ab );
 *    sb_.append( sb );
 *    return sb_.toString();
 *  }</code></pre>
 *  <p>The underscore is appended to {@code sb} to avoid conflicting with the
 *  user-supplied {@code sb} property. Underscores are also prefixed for names
 *  that start with a digit, and used to replace name-unsafe characters like
 *  space or dash.</p>
 *  <p>When dealing with multiple independent inner scopes, use a
 *  {@link #clone()}
 *  of the {@code NameAllocator} used for the outer scope to further refine
 *  name allocation for a specific inner scope.</p>
 *
 *  @author Square,Inc.
 *  @modified   Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: NameAllocator.java 840 2021-01-10 21:37:03Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: NameAllocator.java 840 2021-01-10 21:37:03Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public final class NameAllocator implements Cloneable
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The allocated names.
     */
    private final Set<String> m_AllocatedNames;

    /**
     *  The registry for the names.
     */
    private final Map<Object,String> m_TagToName;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code NameAllocator} instance.
     */
    public NameAllocator() { this( new HashSet<>(), new HashMap<>() ); }

    /**
     *  Creates a new {@code NameAllocator} instance.
     *
     *  @param  allocatedNames  The allocated names.
     *  @param  tagToName   The registry for names.
     */
    @SuppressWarnings( {"CollectionDeclaredAsConcreteClass", "TypeMayBeWeakened"} )
    private NameAllocator( final HashSet<String> allocatedNames, final HashMap<Object,String> tagToName )
    {
        m_AllocatedNames = allocatedNames;
        m_TagToName = tagToName;
    }   //  NameAllocator()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Translates the given suggestion for an identifier to a valid Java
     *  identifier by replacing invalid characters by an underscore
     *  (&quot;_&quot;). If the {@code suggestion} starts with a character that
     *  is not allowed to start a Java identifier, but is otherwise valid, the
     *  resulting identifier is prepended by an underscore.
     *
     *  @param  suggestion  The suggestion for an identifier.
     *  @return A valid Java identifier.
     */
    public static final String toJavaIdentifier( final String suggestion )
    {
        final var buffer = new StringBuilder();
        var codePoint = requireNotEmptyArgument( suggestion, "suggestion" ).codePointAt( 0 );
        if( isJavaIdentifierStart( codePoint ) )
        {
            buffer.appendCodePoint( codePoint );
        }
        else
        {
            buffer.append( '_' );
            if( isJavaIdentifierPart( codePoint ) ) buffer.appendCodePoint( codePoint );
        }
        //noinspection ForLoopWithMissingComponent
        for( var i = charCount( codePoint ); i < suggestion.length(); )
        {
            codePoint = suggestion.codePointAt( i );
            buffer.appendCodePoint( isJavaIdentifierPart( codePoint ) ? codePoint : '_' );
            i += charCount( codePoint );
        }
        final var retValue = buffer.toString();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toJavaIdentifier()

    /**
     *  Creates a deep copy of this {@code NameAllocator}. Useful to create
     *  multiple independent refinements of a {@code NameAllocator} to be used
     *  in the respective definition of multiples, independently-scoped, inner
     *  code blocks.
     *
     *  @return A deep copy of this NameAllocator.
     *
     *  @see Object#clone()
     */
    @SuppressWarnings( "MethodDoesntCallSuperMethod" )
    @Override
    public final NameAllocator clone()
    {
        final var retValue = new NameAllocator( new HashSet<>( m_AllocatedNames ), new HashMap<>( m_TagToName ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  clone()

    /**
     *  Retrieves a name that was previously created with
     *  {@link #newName(String, Object)}.
     *
     *  @param  tag The identifier for the name.
     *  @return The name.
     *  @throws ValidationException  The tag was unknown.
     */
    public final String get( final Object tag )
    {
        final var retValue = m_TagToName.get( requireNonNullArgument( tag, "tag" ) );
        if( isNull( retValue ) ) throw new ValidationException( "unknown tag: " + tag );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  get()

    /**
     *  Returns a new name using the given suggestion that will not be a Java
     *  keyword or clash with other names.
     *
     *  @param  suggestion  The suggestion.
     *  @return The new name.
     */
    public final String newName( final String suggestion )
    {
        return newName( suggestion, randomUUID().toString() );
    }   //  newName()

    /**
     *  Returns a new name based on the given suggestion that will not be a
     *  Java keyword or clash with other names. The returned value can be
     *  queried multiple times by passing the given tag to
     *  {@link #get(Object)}.
     *
     *  @param  suggestion  The suggestion for the new name.
     *  @param  tag The tag for the new name.
     *  @return The new name.
     */
    public final String newName( final String suggestion, final Object tag )
    {
        requireNonNullArgument( tag, "tag" );

        var retValue = toJavaIdentifier( requireNotEmptyArgument( suggestion, "suggestion" ) );

        while( SourceVersion.isKeyword( retValue ) || !m_AllocatedNames.add( retValue ) )
        {
            retValue += "_";
        }

        final var replaced = m_TagToName.put( tag, retValue );
        if( nonNull( replaced ) )
        {
            m_TagToName.put( tag, replaced ); // Put things back as they were!
            throw new ValidationException( format( "tag '%s' cannot be used for both '%s' and '%s'", tag, replaced, suggestion ) );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  newName()
}
//  class NameAllocator

/*
 *  End of File
 */