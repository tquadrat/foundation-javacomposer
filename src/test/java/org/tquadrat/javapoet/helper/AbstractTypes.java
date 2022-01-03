/*
 * ============================================================================
 *  Copyright © 2002-2021 by Thomas Thrien.
 *  All Rights Reserved.
 * ============================================================================
 *  Licensed to the public under the agreements of the GNU Lesser General Public
 *  License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *       http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 */

package org.tquadrat.javapoet.helper;

import static com.google.common.truth.Truth.assertThat;
import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static javax.lang.model.util.ElementFilter.fieldsIn;
import static org.junit.Assert.fail;
import static org.tquadrat.foundation.javacomposer.Primitives.BOOLEAN;
import static org.tquadrat.foundation.javacomposer.Primitives.BYTE;
import static org.tquadrat.foundation.javacomposer.Primitives.CHAR;
import static org.tquadrat.foundation.javacomposer.Primitives.DOUBLE;
import static org.tquadrat.foundation.javacomposer.Primitives.FLOAT;
import static org.tquadrat.foundation.javacomposer.Primitives.INT;
import static org.tquadrat.foundation.javacomposer.Primitives.LONG;
import static org.tquadrat.foundation.javacomposer.Primitives.SHORT;
import static org.tquadrat.foundation.javacomposer.Primitives.VOID;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.ArrayTypeName;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeVariableName;
import org.tquadrat.foundation.javacomposer.WildcardTypeName;
import org.tquadrat.foundation.javacomposer.internal.ClassNameImpl;
import org.tquadrat.foundation.javacomposer.internal.ParameterizedTypeNameImpl;
import org.tquadrat.foundation.javacomposer.internal.TypeNameImpl;
import org.tquadrat.foundation.javacomposer.internal.TypeVariableNameImpl;
import com.google.testing.compile.JavaFileObjects;

@SuppressWarnings( {"static-method", "javadoc", "ClassWithTooManyMethods"} )
@ClassVersion( sourceVersion = "$Id: AbstractTypes.java 943 2021-12-21 01:34:32Z tquadrat $" )
public abstract class AbstractTypes
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    @SuppressWarnings( "unused" )
    public static class Parameterized<Simple,ExtendsClass extends Number,ExtendsInterface extends Runnable,ExtendsTypeVariable extends Simple,Intersection extends Number & Runnable,IntersectionOfInterfaces extends Runnable & Serializable>
    {/* Empty Block */}
    //  class Parameterized

    @SuppressWarnings( "unused" )
    public static class Recursive<T extends Map<List<T>,Set<T []>>>
    {/* Empty Block*/}
    //  class Recursive

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    @Test
    public void arrayType() throws Exception
    {
        final var type = ArrayTypeName.of( String.class );
        assertThat( type.toString() ).isEqualTo( "java.lang.String[]" );
    }

    @Test
    public void box() throws Exception
    {
        assertThat( INT.box() ).isEqualTo( ClassName.from( Integer.class ) );
        assertThat( VOID.box() ).isEqualTo( ClassName.from( Void.class ) );
        assertThat( ClassName.from( Integer.class ).box() ).isEqualTo( ClassName.from( Integer.class ) );
        assertThat( ClassName.from( Void.class ).box() ).isEqualTo( ClassName.from( Void.class ) );
        assertThat( ClassNameImpl.OBJECT.box() ).isEqualTo( ClassNameImpl.OBJECT );
        assertThat( ClassName.from( String.class ).box() ).isEqualTo( ClassName.from( String.class ) );
    }

    @SuppressWarnings( "AnonymousInnerClassWithTooManyMethods" )
    @Test
    public void errorTypes()
    {
        final var hasErrorTypes = JavaFileObjects.forSourceLines( "com.squareup.tacos.ErrorTypes", "package com.squareup.tacos;", "", "@SuppressWarnings(\"hook-into-compiler\")", "class ErrorTypes {", "  Tacos tacos;", "  Ingredients.Guacamole guacamole;", "}" );
        final var compilation = javac().withProcessors( new AbstractProcessor()
        {
            @Override
            public Set<String> getSupportedAnnotationTypes()
            {
                return Collections.singleton( "*" );
            }

            @Override
            public boolean process( final Set<? extends TypeElement> set, final RoundEnvironment roundEnvironment )
            {
                final var classFile = processingEnv.getElementUtils().getTypeElement( "com.squareup.tacos.ErrorTypes" );
                final var fields = fieldsIn( classFile.getEnclosedElements() );
                final var topLevel = (ErrorType) fields.get( 0 ).asType();
                final var member = (ErrorType) fields.get( 1 ).asType();

                assertThat( TypeName.from( topLevel ) ).isEqualTo( ClassName.from( "", "Tacos" ) );
                assertThat( TypeName.from( member ) ).isEqualTo( ClassName.from( "Ingredients", "Guacamole" ) );
                return false;
            }
        } ).compile( hasErrorTypes );

        assertThat( compilation ).failed();
    }

    @Test
    public void getArrayTypeMirror()
    {
        assertThat( TypeName.from( getTypes().getArrayType( getMirror( Object.class ) ) ) ).isEqualTo( ArrayTypeName.of( ClassNameImpl.OBJECT ) );
    }

    @Test
    public void getBasicTypeMirror()
    {
        assertThat( TypeName.from( getMirror( Object.class ) ) ).isEqualTo( ClassName.from( Object.class ) );
        assertThat( TypeName.from( getMirror( Charset.class ) ) ).isEqualTo( ClassName.from( Charset.class ) );
        assertThat( TypeName.from( getMirror( AbstractTypes.class ) ) ).isEqualTo( ClassName.from( AbstractTypes.class ) );
    }

    private TypeElement getElement( final Class<?> clazz )
    {
        return getElements().getTypeElement( clazz.getCanonicalName() );
    }

    protected abstract Elements getElements();

    private TypeMirror getMirror( final Class<?> clazz )
    {
        return getElement( clazz ).asType();
    }

    @Test
    public void getNullTypeMirror()
    {
        try
        {
            TypeName.from( getTypes().getNullType() );
            fail( "Expected Exception was not thrown" );
        }
        catch( @SuppressWarnings( "unused" ) final IllegalArgumentException expected )
        {/* Empty Block*/}
    }

    @Test
    public void getParameterizedTypeMirror()
    {
        final var setType = getTypes().getDeclaredType( getElement( Set.class ), getMirror( Object.class ) );
        assertThat( TypeName.from( setType ) ).isEqualTo( ParameterizedTypeName.from( ClassName.from( Set.class ), ClassNameImpl.OBJECT ) );
    }

    @Test
    public void getPrimitiveTypeMirror()
    {
        assertThat( TypeName.from( getTypes().getPrimitiveType( TypeKind.BOOLEAN ) ) ).isEqualTo( BOOLEAN );
        assertThat( TypeName.from( getTypes().getPrimitiveType( TypeKind.BYTE ) ) ).isEqualTo( BYTE );
        assertThat( TypeName.from( getTypes().getPrimitiveType( TypeKind.SHORT ) ) ).isEqualTo( SHORT );
        assertThat( TypeName.from( getTypes().getPrimitiveType( TypeKind.INT ) ) ).isEqualTo( INT );
        assertThat( TypeName.from( getTypes().getPrimitiveType( TypeKind.LONG ) ) ).isEqualTo( LONG );
        assertThat( TypeName.from( getTypes().getPrimitiveType( TypeKind.CHAR ) ) ).isEqualTo( CHAR );
        assertThat( TypeName.from( getTypes().getPrimitiveType( TypeKind.FLOAT ) ) ).isEqualTo( FLOAT );
        assertThat( TypeName.from( getTypes().getPrimitiveType( TypeKind.DOUBLE ) ) ).isEqualTo( DOUBLE );
    }

    protected abstract Types getTypes();

    @SuppressWarnings( "CastToConcreteClass" )
    @Test
    public void getTypeVariableTypeMirror()
    {
        final var typeVariables = getElement( Parameterized.class ).getTypeParameters();

        // Members of converted types use ClassName and not Class<?>.
        final var number = ClassName.from( Number.class );
        final var runnable = ClassName.from( Runnable.class );
        final var serializable = ClassName.from( Serializable.class );

        assertThat( TypeName.from( typeVariables.get( 0 ).asType() ) ).isEqualTo( TypeVariableName.from( "Simple" ) );
        assertThat( TypeName.from( typeVariables.get( 1 ).asType() ) ).isEqualTo( TypeVariableName.from( "ExtendsClass", number ) );
        assertThat( TypeName.from( typeVariables.get( 2 ).asType() ) ).isEqualTo( TypeVariableName.from( "ExtendsInterface", runnable ) );
        assertThat( TypeName.from( typeVariables.get( 3 ).asType() ) ).isEqualTo( TypeVariableName.from( "ExtendsTypeVariable", TypeVariableName.from( "Simple" ) ) );
        assertThat( TypeName.from( typeVariables.get( 4 ).asType() ) ).isEqualTo( TypeVariableName.from( "Intersection", number, runnable ) );
        assertThat( TypeName.from( typeVariables.get( 5 ).asType() ) ).isEqualTo( TypeVariableName.from( "IntersectionOfInterfaces", runnable, serializable ) );
        assertThat( ((TypeVariableNameImpl) TypeName.from( typeVariables.get( 4 ).asType() )).bounds() ).containsExactly( number, runnable );
    }

    @SuppressWarnings( "CastToConcreteClass" )
    @Test
    public void getTypeVariableTypeMirrorRecursive()
    {
        final var typeMirror = getElement( Recursive.class ).asType();
        final var typeName = (ParameterizedTypeNameImpl) TypeNameImpl.from( typeMirror );
        final var className = Recursive.class.getCanonicalName();
        assertThat( typeName.toString() ).isEqualTo( className + "<T>" );

        final var typeVariableName = (TypeVariableNameImpl) typeName.typeArguments().get( 0 );

        try
        {
            typeVariableName.bounds().set( 0, null );
            fail( "Expected UnsupportedOperationException" );
        }
        catch( @SuppressWarnings( "unused" ) final UnsupportedOperationException expected )
        {/* Empty Block*/}

        assertThat( typeVariableName.toString() ).isEqualTo( "T" );
        assertThat( typeVariableName.bounds().toString() ).isEqualTo( "[java.util.Map<java.util.List<T>, java.util.Set<T[]>>]" );
    }

    @Test
    public void getVoidTypeMirror()
    {
        assertThat( TypeName.from( getTypes().getNoType( TypeKind.VOID ) ) ).isEqualTo( VOID );
    }

    @Test
    public void parameterizedType() throws Exception
    {
        final var type = ParameterizedTypeName.from( Map.class, String.class, Long.class );
        assertThat( type.toString() ).isEqualTo( "java.util.Map<java.lang.String, java.lang.Long>" );
    }

    @Test
    public void typeVariable() throws Exception
    {
        final var type = TypeVariableName.from( "T", CharSequence.class );
        assertThat( type.toString() ).isEqualTo( "T" ); // (Bounds are only
                                                        // emitted in
                                                        // declaration.)
    }

    @Test
    public void unbox() throws Exception
    {
        assertThat( INT ).isEqualTo( INT.unbox() );
        assertThat( VOID ).isEqualTo( VOID.unbox() );
        assertThat( ClassName.from( Integer.class ).unbox() ).isEqualTo( INT.unbox() );
        assertThat( ClassName.from( Void.class ).unbox() ).isEqualTo( VOID.unbox() );
        try
        {
            ClassNameImpl.OBJECT.unbox();
            fail( "Expected Exception was not thrown" );
        }
        catch( @SuppressWarnings( "unused" ) final UnsupportedOperationException expected )
        {/* Empty Block*/}

        try
        {
            ClassName.from( String.class ).unbox();
            fail( "Expected Exception was not thrown" );
        }
        catch( @SuppressWarnings( "unused" ) final UnsupportedOperationException expected )
        {/* Empty Block*/}
    }

    @Test
    public void wildcardExtendsObject() throws Exception
    {
        final var type = WildcardTypeName.subtypeOf( Object.class );
        assertThat( type.toString() ).isEqualTo( "?" );
    }

    @Test
    public void wildcardExtendsType() throws Exception
    {
        final var type = WildcardTypeName.subtypeOf( CharSequence.class );
        assertThat( type.toString() ).isEqualTo( "? extends java.lang.CharSequence" );
    }

    @Test
    public void wildcardMirrorExtendsType() throws Exception
    {
        final var types = getTypes();
        final var elements = getElements();
        final var charSequence = elements.getTypeElement( CharSequence.class.getName() ).asType();
        final var wildcard = types.getWildcardType( charSequence, null );
        final var type = TypeName.from( wildcard );
        assertThat( type.toString() ).isEqualTo( "? extends java.lang.CharSequence" );
    }

    @Test
    public void wildcardMirrorNoBounds() throws Exception
    {
        final var wildcard = getTypes().getWildcardType( null, null );
        final var type = TypeName.from( wildcard );
        assertThat( type.toString() ).isEqualTo( "?" );
    }

    @Test
    public void wildcardMirrorSuperType() throws Exception
    {
        final var types = getTypes();
        final var elements = getElements();
        final var string = elements.getTypeElement( String.class.getName() ).asType();
        final var wildcard = types.getWildcardType( null, string );
        final var type = TypeName.from( wildcard );
        assertThat( type.toString() ).isEqualTo( "? super java.lang.String" );
    }

    @Test
    public void wildcardSuperType() throws Exception
    {
        final var type = WildcardTypeName.supertypeOf( String.class );
        assertThat( type.toString() ).isEqualTo( "? super java.lang.String" );
    }
}
//  class AbstractTypes

/*
 *  End of File
 */