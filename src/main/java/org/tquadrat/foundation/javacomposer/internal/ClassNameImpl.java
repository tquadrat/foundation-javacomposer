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

import static java.util.Collections.reverse;
import static java.util.function.Function.identity;
import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.checkState;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidArgument;
import static org.tquadrat.foundation.util.StringUtils.format;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor9;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.AnnotationSpec;
import org.tquadrat.foundation.javacomposer.ClassName;

/**
 *  The implementation of
 *  {@link ClassName}
 *  for a fully-qualified class name for top-level and member classes.
 *
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ClassNameImpl.java 943 2021-12-21 01:34:32Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( {"ClassWithTooManyFields", "ClassWithTooManyMethods", "ComparableImplementedButEqualsNotOverridden"} )
@ClassVersion( sourceVersion = "$Id: ClassNameImpl.java 943 2021-12-21 01:34:32Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public final class ClassNameImpl extends TypeNameImpl implements ClassName
{
        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The class name for
     *  {@link Boolean}.
     */
    public static final ClassNameImpl BOXED_BOOLEAN = get( Boolean.class );

    /**
     *  The class name for
     *  {@link Byte}.
     */
    public static final ClassNameImpl BOXED_BYTE = get( Byte.class );

    /**
     *  The class name for
     *  {@link Character}.
     */
    public static final ClassNameImpl BOXED_CHAR = get( Character.class );

    /**
     *  The class name for
     *  {@link Double}.
     */
    public static final ClassNameImpl BOXED_DOUBLE = get( Double.class );

    /**
     *  The class name for
     *  {@link Float}.
     */
    public static final ClassNameImpl BOXED_FLOAT = get( Float.class );

    /**
     *  The class name for
     *  {@link Integer}.
     */
    public static final ClassNameImpl BOXED_INT = get( Integer.class );

    /**
     *  The class name for
     *  {@link Long}.
     */
    public static final ClassNameImpl BOXED_LONG = get( Long.class );

    /**
     *  The class name for
     *  {@link Short}.
     */
    public static final ClassNameImpl BOXED_SHORT = get( Short.class );

    /**
     *  The class name for
     *  {@link Void}.
     */
    public static final ClassNameImpl BOXED_VOID = get( Void.class );

    /**
     *  The class name for
     *  {@link Object}.
     */
    public static final ClassNameImpl OBJECT = get( Object.class );

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The full class name like {@code java.util.Map.Entry}.
     */
    private final String m_CanonicalName;

    /**
     *  The enclosing class, or empty if this is not enclosed in another class.
     */
    @SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
    private final Optional<ClassNameImpl> m_EnclosingClassName;

    /**
     * The package name of this class, or the empty String if this is in the
     * default package.
     */
    private final String m_PackageName;

    /**
     *  The name for this class name, like {@code Entry} for
     *  {@code java.util.Map.Entry}.
     */
    private final String m_SimpleName;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code ClassNameImpl} instance.
     *
     *  @param  packageName The name of the package for the new class name.
     *  @param  enclosingClassName  The name of the enclosing class; can be
     *      {@code null} in case of a top level class.
     *  @param  simpleName  The name of the class.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public ClassNameImpl( final CharSequence packageName, final ClassNameImpl enclosingClassName, final CharSequence simpleName )
    {
        this( packageName, enclosingClassName, simpleName, List.of() );
    }   //  ClassNameImpl()

    /**
     *  Creates a new {@code ClassNameImpl} instance.
     *
     *  @param  packageName The name of the package for the new class name.
     *  @param  enclosingClassName  The name of the enclosing class; can be
     *      empty in case of a top level class.
     *  @param  simpleName  The name of the class.
     */
    @SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
    public ClassNameImpl( final CharSequence packageName, final Optional<ClassNameImpl> enclosingClassName, final CharSequence simpleName )
    {
        this( packageName, enclosingClassName, simpleName, List.of() );
    }   //  ClassNameImpl()

    /**
     *  Creates a new {@code ClassNameImpl} instance.
     *
     *  @param  packageName The name of the package for the new class name.
     *  @param  enclosingClassName  The name of the enclosing class; can be
     *      {@code null} in case of a top level class.
     *  @param  simpleName  The name of the class.
     *  @param  annotations The annotations for this class name.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public ClassNameImpl( final CharSequence packageName, final ClassNameImpl enclosingClassName, final CharSequence simpleName, final List<AnnotationSpecImpl> annotations )
    {
        this( packageName, Optional.ofNullable( enclosingClassName ), simpleName, annotations );
    }   //  ClassNameImpl()

    /**
     *  Creates a new {@code ClassNameImpl} instance.
     *
     *  @param  packageName The name of the package for the new class name.
     *  @param  enclosingClassName  The name of the enclosing class.
     *  @param  simpleName  The name of the class.
     *  @param  annotations The annotations for this class name.
     */
    @SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
    public ClassNameImpl( final CharSequence packageName, final Optional<ClassNameImpl> enclosingClassName, final CharSequence simpleName, final List<AnnotationSpecImpl> annotations )
    {
        super( annotations );
        m_PackageName = requireNonNullArgument( packageName, "packageName" ).toString().intern();
        m_EnclosingClassName = requireNonNullArgument( enclosingClassName, "enclosingClassName" );
        m_SimpleName = requireNotEmptyArgument( simpleName, "simpleName" ).toString().intern();
        //noinspection AccessingNonPublicFieldOfAnotherObject
        m_CanonicalName = enclosingClassName.map( className -> (className.m_CanonicalName + '.' + m_SimpleName) )
            .orElseGet( () ->
                m_PackageName.isEmpty()
                ? m_SimpleName
                : m_PackageName + '.' + m_SimpleName
            );
    }   //  ClassNameImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Override
    public final ClassNameImpl annotated( final List<AnnotationSpec> annotations )
    {
        return new ClassNameImpl( m_PackageName, m_EnclosingClassName, m_SimpleName, concatAnnotations( annotations ) );
    }   //  annotated()

    /**
     *  Returns a new {@code ClassName} instance for the given fully-qualified
     *  class name string. This method assumes that the input is ASCII and
     *  follows typical Java style (lowercase package names, UpperCamelCase
     *  class names) and may produce incorrect results or throw
     *  {@link IllegalArgumentException}
     *  otherwise. For that reason,
     *  {@link #get(Class)},
     *  {@link #get(CharSequence,CharSequence,CharSequence...)},
     *  and
     *  {@link #get(TypeElement)}
     *  should be preferred as they can correctly create {@code ClassName}
     *  instances without such restrictions.
     *
     *  @param  className   The fully qualified class name.
     *  @return The new class name instance.
     *
     *  @deprecated Because of the limitations described above, this method is
     *      seen as inherently unsafe, and therefore it was decided to
     *      deprecate it.
     */
    @Deprecated( since = "0.1.0" )
    @API( status = DEPRECATED, since = "0.0.5" )
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final ClassNameImpl bestGuess( final CharSequence className )
    {
        final var classNameString = requireNotEmptyArgument( className, "className" ).toString().intern();
        /*
         * Add the package name, like "java.util.concurrent", or "" for no
         * package.
         */
        var p = 0;
        while( p < classNameString.length() && Character.isLowerCase( classNameString.codePointAt( p ) ) )
        {
            p = classNameString.indexOf( '.', p ) + 1;
            checkState( p != 0, () -> new ValidationException( format( "couldn't make a guess for %s", classNameString ) ) );
        }
        final var packageName = p == 0 ? EMPTY_STRING : classNameString.substring( 0, p - 1 );

        //---* Add class names like "Map" and "Entry" *------------------------
        ClassNameImpl retValue = null;
        for( final var simpleName : classNameString.substring( p ).split( "\\.", -1 ) )
        {
            checkState( !simpleName.isEmpty() && Character.isUpperCase( simpleName.codePointAt( 0 ) ), () -> new ValidationException( format( "couldn't make a guess for %s", classNameString ) ) );
            retValue = new ClassNameImpl( packageName, retValue, simpleName );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  bestGuess()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int compareTo( final ClassName o ) { return canonicalName().compareTo( o.canonicalName() ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String canonicalName() { return m_CanonicalName; }

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( {"PublicMethodNotExposedInInterface", "UseOfConcreteClass"} )
    @Override
    public final CodeWriter emit( final CodeWriter out ) throws UncheckedIOException
    {
        final var retValue = requireNonNullArgument( out, "out" );
        var charsEmitted = false;
        for( final var className : enclosingClasses() )
        {
            final String simpleName;
            if( charsEmitted )
            {
                //---* An enclosing class was already emitted *----------------
                retValue.emit( "." );
                //noinspection AccessingNonPublicFieldOfAnotherObject
                simpleName = className.m_SimpleName;

            }
            else if( className.isAnnotated() || className == this )
            {
                /*
                 * We encountered the first enclosing class that must be
                 * emitted.
                 */
                final var qualifiedName = retValue.lookupName( className );
                final var dot = qualifiedName.lastIndexOf( '.' );
                if( dot != -1 )
                {
                    retValue.emitAndIndent( qualifiedName.substring( 0, dot + 1 ) );
                    simpleName = qualifiedName.substring( dot + 1 );
                    charsEmitted = true;
                }
                else
                {
                    simpleName = qualifiedName;
                }

            }
            else
            {
                /*
                 * Don't emit this enclosing type. Keep going so we can be more
                 * precise.
                 */
                continue;
            }

            if( className.isAnnotated() )
            {
                if( charsEmitted ) retValue.emit( " " );
                className.emitAnnotations( retValue );
            }

            retValue.emit( simpleName );
            charsEmitted = true;
        }


        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  emit()

    /**
     *  Returns all enclosing classes in {@code this}, outermost first.
     *
     *  @return The enclosing classes.
     */
    @SuppressWarnings( "AccessingNonPublicFieldOfAnotherObject" )
    private final List<ClassNameImpl> enclosingClasses()
    {
        final List<ClassNameImpl> retValue = new ArrayList<>();
        for( var c = this; nonNull( c ); c = c.m_EnclosingClassName.orElse( null ) )
        {
            retValue.add( c );
        }
        reverse( retValue );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  enclosingClasses()

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
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final Optional<ClassNameImpl> enclosingClassName() { return m_EnclosingClassName; }

    /**
     *  Creates a new {@code ClassName} instance from an instance of
     *  {@link Class}.
     *
     *  @param  sourceClass The instance of {@code java.lang.Class}.
     *  @return The respective instance of {@code ClassName}.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @API( status = STABLE, since = "0.2.0" )
    public static final ClassNameImpl from( final Class<?> sourceClass )
    {
        var c = requireNonNullArgument( sourceClass, "sourceClass" );
        requireValidArgument( c, "sourceClass", v -> !v.isPrimitive(), $ -> "primitive types cannot be represented as a ClassName" );
        requireValidArgument( c, "sourceClass", v -> !void.class.equals( v ), $ -> "'void' type cannot be represented as a ClassName" );
        requireValidArgument( c, "sourceClass", v -> !v.isArray(), $ -> "array types cannot be represented as a ClassName" );

        final ClassNameImpl retValue;
        var anonymousSuffix = EMPTY_STRING;
        while( c.isAnonymousClass() )
        {
            final var lastDollar = c.getName().lastIndexOf( '$' );
            //noinspection CallToStringConcatCanBeReplacedByOperator
            anonymousSuffix = c.getName().substring( lastDollar ).concat( anonymousSuffix );
            c = c.getEnclosingClass();
        }
        final var name = c.getSimpleName() + anonymousSuffix;

        if( isNull( c.getEnclosingClass() ) )
        {
            /*
             * Avoid unreliable Class.getPackage():
             * https://github.com/square/javapoet/issues/295
             */
            final var lastDot = c.getName().lastIndexOf( '.' );
            final var packageName = (lastDot < 0) ? null : c.getName().substring( 0, lastDot );
            retValue = new ClassNameImpl( packageName, Optional.empty(), name );
        }
        else
        {
            retValue = get( c.getEnclosingClass() ).nestedClass( name );
        }

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
    @SuppressWarnings( {"AnonymousInnerClassWithTooManyMethods", "OverlyComplexAnonymousInnerClass", "UseOfConcreteClass"} )
    @API( status = STABLE, since = "0.2.0" )
    public static final ClassNameImpl from( final TypeElement element )
    {
        final var simpleName = requireNonNullArgument( element, "element" ).getSimpleName().toString();

        final var retValue = element.getEnclosingElement().accept( new SimpleElementVisitor9<ClassNameImpl,Void>()
        {
            /**
             *  {@inheritDoc}
             */
            @Override
            public final ClassNameImpl visitPackage( final PackageElement packageElement, final Void p )
            {
                return new ClassNameImpl( packageElement.getQualifiedName().toString(), Optional.empty(), simpleName );
            }   //  visitPackage()

            /**
             *  {@inheritDoc}
             */
            @Override
            public final ClassNameImpl visitType( final TypeElement enclosingClass, final Void p )
            {
                return from( enclosingClass ).nestedClass( simpleName );
            }   //  visitType()

            /**
             *  {@inheritDoc}
             */
            @Override
            public final ClassNameImpl visitUnknown( final Element unknown, final Void p )
            {
                return from( EMPTY_STRING, simpleName );
            }   //  visitUnknown()

            /**
             *  {@inheritDoc}
             */
            @Override
            public final ClassNameImpl defaultAction( final Element enclosingElement, final Void p )
            {
                throw new IllegalArgumentException( "Unexpected type nesting: " + element );
            }   //  defaultAction()
        }, null );

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
    @SuppressWarnings( "UseOfConcreteClass" )
    @API( status = STABLE, since = "0.2.0" )
    public static final ClassNameImpl from( final CharSequence packageName, final CharSequence simpleName, final CharSequence... simpleNames )
    {
        var retValue = new ClassNameImpl( packageName, Optional.empty(), simpleName );
        for( final var name : simpleNames )
        {
            retValue = retValue.nestedClass( name );
        }

        // ---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Creates a new {@code ClassName} instance from an instance of
     *  {@link Class}.
     *
     *  @param  sourceClass The instance of {@code java.lang.Class}.
     *  @return The respective instance of {@code ClassName}.
     *
     *  @deprecated Use
     *      {@link #from(Class)}
     *      instead.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final ClassNameImpl get( final Class<?> sourceClass ) { return from( sourceClass ); }

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
    @SuppressWarnings( "UseOfConcreteClass" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final ClassNameImpl get( final TypeElement element ) { return from( element ); }

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
    @SuppressWarnings( "UseOfConcreteClass" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final ClassNameImpl get( final CharSequence packageName, final CharSequence simpleName, final CharSequence... simpleNames )
    {
        final var retValue = from( packageName, simpleName, simpleNames );

        // ---* Done *----------------------------------------------------------
        return retValue;
    }   //  get()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean isAnnotated()
    {
        final var retValue = super.isAnnotated() || (m_EnclosingClassName.isPresent() && m_EnclosingClassName.get().isAnnotated());

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  isAnnotated()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Override
    public final ClassNameImpl nestedClass( final CharSequence name )
    {
        final var retValue = new ClassNameImpl( m_PackageName, this, requireNotEmptyArgument( name, "name" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  nestedClass()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String packageName() { return m_PackageName; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final Optional<ClassName> parentClass() { return m_EnclosingClassName.map( identity() ); }

    /**
     *  {@inheritDoc} If
     *  this class is enclosed by another class, this is equivalent to
     *  {@link #enclosingClassName()}.{@link Optional#get()}.{@link #nestedClass(CharSequence) nestedClass(name)}.
     *  Otherwise, it is equivalent to
     *  {@link #get(CharSequence,CharSequence,CharSequence...) get(packageName(),name)}.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Override
    public final ClassNameImpl peerClass( final CharSequence name )
    {
        final var retValue = new ClassNameImpl( m_PackageName, m_EnclosingClassName, requireNotEmptyArgument( name, "name" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //   peerClass()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String reflectionName()
    {
        final var retValue = m_EnclosingClassName.map( c -> c.reflectionName() + '$' + m_SimpleName )
            .orElse( m_PackageName.isEmpty() ? m_SimpleName : m_PackageName + '.' + m_SimpleName );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  reflectionName()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String simpleName() { return m_SimpleName; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final List<String> simpleNames()
    {
        final List<String> retValue = new ArrayList<>();
        m_EnclosingClassName.ifPresent( c -> retValue.addAll( c.simpleNames() ) );
        retValue.add( m_SimpleName );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  simpleNames()

    /**
     *  {@inheritDoc} Equivalent to chained calls to
     *  {@link #enclosingClassName()}
     *  until the result's enclosing class is not present.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Override
    public final ClassNameImpl topLevelClassName()
    {
        final var retValue = m_EnclosingClassName.map( ClassNameImpl::topLevelClassName ).orElse( this );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  topLevelClassName

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    @Override
    public final ClassNameImpl withoutAnnotations()
    {
        var retValue = this;
        if( isAnnotated() )
        {
            final var resultEnclosingClassName = m_EnclosingClassName.map( ClassNameImpl::withoutAnnotations ).orElse( null );
            retValue = new ClassNameImpl( m_PackageName, resultEnclosingClassName, m_SimpleName );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  withoutAnnotations()
}
//  class ClassNameImpl

/*
 *  End of File
 */