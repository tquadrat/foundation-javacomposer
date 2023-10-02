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

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.MAINTAINED;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.javacomposer.internal.ClassNameImpl.BOXED_BOOLEAN;
import static org.tquadrat.foundation.javacomposer.internal.ClassNameImpl.BOXED_BYTE;
import static org.tquadrat.foundation.javacomposer.internal.ClassNameImpl.BOXED_CHAR;
import static org.tquadrat.foundation.javacomposer.internal.ClassNameImpl.BOXED_DOUBLE;
import static org.tquadrat.foundation.javacomposer.internal.ClassNameImpl.BOXED_FLOAT;
import static org.tquadrat.foundation.javacomposer.internal.ClassNameImpl.BOXED_INT;
import static org.tquadrat.foundation.javacomposer.internal.ClassNameImpl.BOXED_LONG;
import static org.tquadrat.foundation.javacomposer.internal.ClassNameImpl.BOXED_SHORT;
import static org.tquadrat.foundation.javacomposer.internal.ClassNameImpl.BOXED_VOID;
import static org.tquadrat.foundation.javacomposer.internal.ClassNameImpl.OBJECT;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor14;
import java.io.UncheckedIOException;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.UnexpectedExceptionError;
import org.tquadrat.foundation.javacomposer.AnnotationSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeVariableName;
import org.tquadrat.foundation.lang.Lazy;

/**
 *  The implementation of
 *  {@link TypeNameImpl}
 *  as representation of any type in Java's type system, plus {@code void}.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TypeNameImpl.java 1066 2023-09-28 19:51:53Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( "ClassWithTooManyFields" )
@ClassVersion( sourceVersion = "$Id: TypeNameImpl.java 1066 2023-09-28 19:51:53Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public sealed class TypeNameImpl implements TypeName
    permits ArrayTypeNameImpl, ClassNameImpl, ParameterizedTypeNameImpl, TypeVariableNameImpl, WildcardTypeNameImpl
{
        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The type name for {@code boolean}.
     */
    public static final TypeNameImpl BOOLEAN_PRIMITIVE = new TypeNameImpl( "boolean" );

    /**
     *  The type name for {@code byte}.
     */
    public static final TypeNameImpl BYTE_PRIMITIVE = new TypeNameImpl( "byte" );

    /**
     *  The type name for {@code char}.
     */
    public static final TypeNameImpl CHAR_PRIMITIVE = new TypeNameImpl( "char" );

    /**
     *  The type name for {@code double}.
     */
    public static final TypeNameImpl DOUBLE_PRIMITIVE = new TypeNameImpl( "double" );

    /**
     *  The type name for {@code float}.
     */
    public static final TypeNameImpl FLOAT_PRIMITIVE = new TypeNameImpl( "float" );

    /**
     *  The type name for {@code int}.
     */
    public static final TypeNameImpl INT_PRIMITIVE = new TypeNameImpl( "int" );

    /**
     *  The type name for {@code long}.
     */
    public static final TypeNameImpl LONG_PRIMITIVE = new TypeNameImpl( "long" );

    /**
     *  The type name for {@code short}.
     */
    public static final TypeNameImpl SHORT_PRIMITIVE = new TypeNameImpl( "short" );

    /**
     *  The type name for {@code void}.
     */
    public static final TypeNameImpl VOID_PRIMITIVE = new TypeNameImpl( "void" );

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The annotations for this type.
     */
    private final List<AnnotationSpecImpl> m_Annotations;

    /**
     *  Lazily initialised return value of
     *  {@link #toString()}
     *  of this type name.
     */
    private final Lazy<String> m_CachedString;

    /**
     *  The name of this type if it is a keyword.
     */
    @SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
    private final Optional<String> m_Keyword;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code TypeNameImpl} instance.
     *
     *  @param  keyword The name of this type if it is a keyword, {@code null}
     *      otherwise.
     */
    public TypeNameImpl( final String keyword ) { this( keyword, List.of() ); }

    /**
     *  Creates a new {@code TypeNameImpl} instance.
     *
     *  @param  keyword The name of this type if it is a keyword, {@code null}
     *      otherwise.
     *  @param  annotations The annotations for this type name.
     */
    public TypeNameImpl( final String keyword, final List<AnnotationSpecImpl> annotations )
    {
        m_Keyword = Optional.ofNullable( keyword );
        m_Annotations = List.copyOf( requireNonNullArgument( annotations, "annotations" ) );

        m_CachedString = Lazy.use( this::initialiseCachedString );
    }   //  TypeNameImpl()

    /**
     *  Creates a new {@code TypeNameImpl} instance.
     *
     *  @param  annotations The annotations for this type name.
     */
    public TypeNameImpl( final List<AnnotationSpecImpl> annotations ) { this( null, annotations ); }

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public TypeNameImpl annotated( final List<AnnotationSpec> annotations )
    {
        final var combined = concatAnnotations( requireNonNullArgument( annotations, "annotations" ) );
        final var retValue = new TypeNameImpl( m_Keyword.orElse( null ), combined );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  annotated()

    /**
     *  Returns the annotations for this type name.
     *
     *  @return The annotations.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final List<AnnotationSpecImpl> annotations() { return m_Annotations; }

    /**
     *  Returns the given type name as an array; the return value is empty if
     *  it is not an array.
     *
     *  @param  type    The type name.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the array type name.
     */
    @SuppressWarnings( "ClassReferencesSubclass" )
    public static final Optional<ArrayTypeNameImpl> asArray( final TypeName type )
    {
        final Optional<ArrayTypeNameImpl> retValue = type instanceof final ArrayTypeNameImpl arrayType
            ? Optional.of( arrayType )
            : Optional.empty();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  asArray()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( {"MethodWithMultipleReturnPoints", "OverlyComplexMethod"} )
    @Override
    public final TypeNameImpl box()
    {
        if( m_Keyword.isEmpty() ) return this; // Doesn't need boxing.
        if( this == VOID_PRIMITIVE ) return BOXED_VOID;
        if( this == BOOLEAN_PRIMITIVE ) return BOXED_BOOLEAN;
        if( this == BYTE_PRIMITIVE ) return BOXED_BYTE;
        if( this == SHORT_PRIMITIVE ) return BOXED_SHORT;
        if( this == INT_PRIMITIVE ) return BOXED_INT;
        if( this == LONG_PRIMITIVE ) return BOXED_LONG;
        if( this == CHAR_PRIMITIVE ) return BOXED_CHAR;
        if( this == FLOAT_PRIMITIVE ) return BOXED_FLOAT;
        if( this == DOUBLE_PRIMITIVE ) return BOXED_DOUBLE;

        //---* Something went awfully wrong ... *------------------------------
        throw new AssertionError( m_Keyword.get() );
    }   //  box()

    /**
     *  Combines the annotations of this instance with the given ones.
     *
     *  @param  annotations The annotations to add.
     *  @return The combined list of annotations.
     */
    protected final List<AnnotationSpecImpl> concatAnnotations( final Collection<AnnotationSpec> annotations )
    {
        final List<AnnotationSpecImpl> list = new ArrayList<>( m_Annotations );
        annotations.stream().map( a -> (AnnotationSpecImpl) a ).forEach( list::add );
        final var retValue = List.copyOf( list );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  concatAnnotations()

    /**
     *  Emits this type name instance to the given
     *  {@link CodeWriter}.
     *
     *  @param  out The code writer.
     *  @return The code writer.
     *  @throws UncheckedIOException Something went wrong when emitting to the
     *      output target.
     */
    @SuppressWarnings( {"PublicMethodNotExposedInInterface", "UseOfConcreteClass"} )
    public CodeWriter emit( final CodeWriter out ) throws UncheckedIOException
    {
        var retValue = requireNonNullArgument( out, "out" );
        if( m_Keyword.isEmpty() ) throw new AssertionError();

        if( isAnnotated() )
        {
            retValue = retValue.emit( "" );
            retValue = emitAnnotations( retValue );
        }
        retValue = retValue.emitAndIndent( m_Keyword.get() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  emit()

    /**
     *  Emits the annotation from this type name instance to the given
     *  {@link CodeWriter}.
     *
     *  @param  out The code writer.
     *  @return The code writer.
     *  @throws UncheckedIOException Something went wrong when emitting to the
     *      output target.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    protected final CodeWriter emitAnnotations( final CodeWriter out ) throws UncheckedIOException
    {
        var retValue = requireNonNullArgument( out, "out" );
        for( final var annotation : m_Annotations )
        {
            annotation.emit( retValue, true );
            retValue = retValue.emit( " " );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  emitAnnotations()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = this == o;
        if( !retValue && o instanceof TypeNameImpl )
        {
            retValue = (getClass() == o.getClass()) && toString().equals( o.toString() );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  equals()

    /**
     *  Returns a type name equivalent to that from the given
     *  {@link TypeMirror}
     *  instance.
     *
     *  @param  mirror  The given type mirror instance.
     *  @return The respective type name.
     */
    @API( status = STABLE, since = "0.2.0" )
    public static final TypeNameImpl from( final TypeMirror mirror )
    {
        final var retValue = from( requireNonNullArgument( mirror, "mirror" ), Map.of() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  get()

    /**
     *  Returns a type name equivalent to that of the given
     *  {@link Type}
     *  instance.
     *
     *  @param  type    The type.
     *  @return The respective type name for the given {@code Type} instance.
     */
    @API( status = STABLE, since = "0.2.0" )
    public static final TypeNameImpl from( final Type type )
    {
        final var retValue = from( requireNonNullArgument( type, "type" ), Map.of() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a type name equivalent to that from the given
     *  {@link TypeMirror}
     *  instance.
     *
     *  @param  mirror  The given type mirror instance.
     *  @param  typeVariables   The type variables.
     *
     *  @return The respective type name.
     */
    @API( status = MAINTAINED, since = "0.2.0" )
    public static final TypeNameImpl from( final TypeMirror mirror, final Map<TypeParameterElement,TypeVariableNameImpl> typeVariables )
    {
        @SuppressWarnings( {"AnonymousInnerClassWithTooManyMethods", "OverlyComplexAnonymousInnerClass", "AnonymousInnerClass"} )
        final var retValue = mirror.accept( new SimpleTypeVisitor14<TypeNameImpl,Void>()
        {
            /**
             *  {@inheritDoc}
             */
            @Override
            public final TypeNameImpl visitPrimitive( final PrimitiveType t, final Void p )
            {
                return switch( t.getKind() )
                {
                    case BOOLEAN -> BOOLEAN_PRIMITIVE;
                    case BYTE -> BYTE_PRIMITIVE;
                    case SHORT -> SHORT_PRIMITIVE;
                    case INT -> INT_PRIMITIVE;
                    case LONG -> LONG_PRIMITIVE;
                    case CHAR -> CHAR_PRIMITIVE;
                    case FLOAT -> FLOAT_PRIMITIVE;
                    case DOUBLE -> DOUBLE_PRIMITIVE;
                    //$CASES-OMITTED$
                    default -> throw new AssertionError();
                };
            }   //  visitPrimitive()

            /**
             *  {@inheritDoc}
             */
            @Override
            public final TypeNameImpl visitDeclared( final DeclaredType t, final Void p )
            {
                final var rawType = ClassNameImpl.from( (TypeElement) t.asElement() );
                final var enclosingType = t.getEnclosingType();
                final var enclosing = (enclosingType.getKind() != TypeKind.NONE) && !t.asElement().getModifiers().contains( Modifier.STATIC ) ? enclosingType.accept( this, null ) : null;
                @SuppressWarnings( "AnonymousClassVariableHidesContainingMethodVariable" )
                final TypeNameImpl retValue;
                if( t.getTypeArguments().isEmpty() && ! (enclosing instanceof ParameterizedTypeName) )
                {
                    retValue = rawType;
                }
                else
                {
                    final var typeArgumentNames = t.getTypeArguments()
                        .stream()
                        .map( typeMirror -> get( typeMirror, typeVariables ) )
                        .collect( toList() );
                    //noinspection CastConflictsWithInstanceof
                    retValue = enclosing instanceof ParameterizedTypeName
                        ? ((ParameterizedTypeNameImpl) enclosing)
                            .nestedClass( rawType.simpleName(), typeArgumentNames.stream().map( typeName -> (TypeName) typeName ).collect( toList() ) )
                        : new ParameterizedTypeNameImpl( null, rawType, typeArgumentNames );
                }

                //---* Done *----------------------------------------------------------
                return retValue;
            }   //  visitDeclared()

            /**
             *  {@inheritDoc}
             */
            @Override
            public final TypeNameImpl visitError( final ErrorType t, final Void p ) { return visitDeclared( t, p ); }

            /**
             *  {@inheritDoc}
             */
            @Override
            public final ArrayTypeNameImpl visitArray( final ArrayType t, final Void p ) { return ArrayTypeNameImpl.from( t, typeVariables ); }

            /**
             *  {@inheritDoc}
             */
            @Override
            public final TypeNameImpl visitTypeVariable( final javax.lang.model.type.TypeVariable t, final Void p ) { return TypeVariableNameImpl.from( t, typeVariables ); }

            /**
             *  {@inheritDoc}
             */
            @Override
            public final TypeNameImpl visitWildcard( final javax.lang.model.type.WildcardType t, final Void p ) { return WildcardTypeNameImpl.from( t, typeVariables ); }

            /**
             *  {@inheritDoc}
             */
            @Override
            public final TypeNameImpl visitNoType( final NoType t, final Void p )
            {
                @SuppressWarnings( "AnonymousClassVariableHidesContainingMethodVariable" )
                final var retValue = t.getKind() == TypeKind.VOID
                    ? VOID_PRIMITIVE
                    : visitUnknown( t, p );

                //---* Done *----------------------------------------------------------
                return retValue;
            }   //  visitNoType()

            /**
             *  {@inheritDoc}
             */
            @Override
            protected final TypeNameImpl defaultAction( final TypeMirror e, final Void p )
            {
                throw new IllegalArgumentException( "Unexpected type mirror: " + e );
            }
        }, null );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

    /**
     *  Returns a type name equivalent to that of the given
     *  {@link Type}
     *  instance.
     *
     *  @param  type    The type.
     *  @param  typeVariables   The type variables.
     *  @return The respective type name for the given {@code Type} instance.
     */
    @SuppressWarnings( {"IfStatementWithTooManyBranches", "ChainOfInstanceofChecks", "OverlyComplexMethod"} )
    @API( status = MAINTAINED, since = "0.2.0" )
    public static final TypeNameImpl from( final Type type, final Map<Type,TypeVariableName> typeVariables )
    {
        final var retValue = switch( type )
        {
            case final Class<?> aClass -> {
                if( aClass == void.class ) yield VOID_PRIMITIVE;
                else if( aClass == boolean.class ) yield BOOLEAN_PRIMITIVE;
                else if( aClass == byte.class ) yield BYTE_PRIMITIVE;
                else if( aClass == short.class ) yield SHORT_PRIMITIVE;
                else if( aClass == int.class ) yield INT_PRIMITIVE;
                else if( aClass == long.class ) yield LONG_PRIMITIVE;
                else if( aClass == char.class ) yield CHAR_PRIMITIVE;
                else if( aClass == float.class ) yield FLOAT_PRIMITIVE;
                else if( aClass == double.class ) yield DOUBLE_PRIMITIVE;
                else if( aClass == Object.class ) yield OBJECT;
                else if( aClass.isArray() ) yield ArrayTypeNameImpl.of( from( aClass.getComponentType(), typeVariables ) );
                else yield ClassNameImpl.from( aClass );
            }
            case final ParameterizedType parameterizedType -> ParameterizedTypeNameImpl.from( parameterizedType, typeVariables );
            case final WildcardType wildcardType -> WildcardTypeNameImpl.from( wildcardType, typeVariables );
            case final TypeVariable<?> typeVariable -> TypeVariableNameImpl.from( typeVariable, typeVariables );
            case final GenericArrayType genericArrayType -> ArrayTypeNameImpl.from( genericArrayType, typeVariables );
            case null -> throw new IllegalArgumentException( "type is null" );
            default -> throw new IllegalArgumentException( "unexpected type: " + type );
        };

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  from()

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
    public static final TypeNameImpl get( final TypeMirror mirror ) { return from( mirror ); }

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
    public static final TypeNameImpl get( final Type type ) { return from( type ); }

    /**
     *  Returns a type name equivalent to that from the given
     *  {@link TypeMirror}
     *  instance.
     *
     *  @param  mirror  The given type mirror instance.
     *  @param  typeVariables   The type variables.
     *
     *  @return The respective type name.
     *
     *  @deprecated Use
     *      {@link #from(TypeMirror,Map)}
     *      instead.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final TypeNameImpl get( final TypeMirror mirror, final Map<TypeParameterElement,TypeVariableNameImpl> typeVariables )
    {
        final var retValue = from( mirror, typeVariables );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  get()

    /**
     *  Returns a type name equivalent to that of the given
     *  {@link Type}
     *  instance.
     *
     *  @param  type    The type.
     *  @param  typeVariables   The type variables.
     *  @return The respective type name for the given {@code Type} instance.
     *
     *  @deprecated Use
     *      {@link #from(Type,Map)}
     *      instead.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final TypeNameImpl get( final Type type, final Map<Type,TypeVariableName> typeVariables )
    {
        final var retValue = from( type, typeVariables );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  get()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int hashCode() { return toString().hashCode(); }

    /**
     *  The initializer for
     *  {@link #m_CachedString}.
     *
     *  @return The return value for
     *      {@link #toString()}.
     */
    private final String initialiseCachedString()
    {
        final var resultBuilder = new StringBuilder();
        final var codeWriter = new CodeWriter( new JavaComposer(), resultBuilder );
        try
        {
            emit( codeWriter );
        }
        catch( final UncheckedIOException e )
        {
            throw new UnexpectedExceptionError( e.getCause() );
        }
        final var retValue = resultBuilder.toString();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  initialiseCachedString()

    /**
     *  {@inheritDoc}
     */
    @Override
    public boolean isAnnotated() { return !m_Annotations.isEmpty(); }

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "SuspiciousMethodCalls" )
    @Override
    public final boolean isBoxedPrimitive()
    {
        final var retValue = List.of( BOXED_BOOLEAN, BOXED_BYTE, BOXED_SHORT, BOXED_INT, BOXED_LONG, BOXED_CHAR, BOXED_FLOAT, BOXED_DOUBLE )
            .contains( this );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  isBoxedPrimitive()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean isPrimitive()
    {
        final var retValue = m_Keyword.isPresent() && (this != VOID_PRIMITIVE);

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  isPrimitive()

    /**
     *  Converts an array of types to a list of type names.
     *
     *  @param  types   The types.
     *  @return The type names.
     */
    public static final List<TypeNameImpl> list( final Type... types ) { return list( types, new LinkedHashMap<>() ); }

    /**
     *  Converts an array of types to a list of type names.
     *
     *  @param  types   The types.
     *  @param  typeVariables   The type variables.
     *  @return The type names.
     */
    public static final List<TypeNameImpl> list( final Type [] types, final Map<Type,TypeVariableName> typeVariables )
    {
        requireNonNullArgument( typeVariables, "typeVariables" );
        final var retValue = stream( requireNonNullArgument( types, "types" ) )
            .map( t -> get( t, typeVariables ) )
            .collect( toList() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  list()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString() { return m_CachedString.get(); }

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( {"MethodWithMultipleReturnPoints", "OverlyComplexMethod"} )
    @Override
    public final TypeNameImpl unbox()
    {
        if( m_Keyword.isPresent() ) return this; // Already unboxed.
        if( equals( BOXED_VOID ) ) return VOID_PRIMITIVE;
        if( equals( BOXED_BOOLEAN ) ) return BOOLEAN_PRIMITIVE;
        if( equals( BOXED_BYTE ) ) return BYTE_PRIMITIVE;
        if( equals( BOXED_SHORT ) ) return SHORT_PRIMITIVE;
        if( equals( BOXED_INT ) ) return INT_PRIMITIVE;
        if( equals( BOXED_LONG ) ) return LONG_PRIMITIVE;
        if( equals( BOXED_CHAR ) ) return CHAR_PRIMITIVE;
        if( equals( BOXED_FLOAT ) ) return FLOAT_PRIMITIVE;
        if( equals( BOXED_DOUBLE ) ) return DOUBLE_PRIMITIVE;
        throw new UnsupportedOperationException( "cannot unbox " + this );
    }   //  unbox()

    /**
     *  {@inheritDoc}
     */
    @Override
    public TypeNameImpl withoutAnnotations() { return new TypeNameImpl( m_Keyword.orElse( null ) ); }
}
//  class TypeNameImpl

/*
 *  End of File
 */