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

package org.tquadrat.javapoet;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.truth.Truth.assertThat;
import static javax.lang.model.util.ElementFilter.methodsIn;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.internal.MethodSpecImpl;
import org.tquadrat.javapoet.helper.CompilationRule;

@ClassVersion( sourceVersion = "$Id: TestMethodSpec.java 943 2021-12-21 01:34:32Z tquadrat $" )
@SuppressWarnings( {"static-method", "javadoc"} )
@DisplayName( "TestMethodSpec" )
@RunWith( JUnit4.class )
public final class TestMethodSpec
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    @SuppressWarnings( {"AbstractClassNeverImplemented", "PackageVisibleInnerClass"} )
    abstract static class Everything
    {
        @Deprecated
        protected abstract <T extends Runnable & Closeable> Runnable everything( @Nullable String thing, List<? extends T> things ) throws IOException, SecurityException;
    }
    //  class Everything

    @SuppressWarnings( {"InterfaceNeverImplemented", "InterfaceMayBeAnnotatedFunctional", "PackageVisibleInnerClass"} )
    interface ExtendsIterableWithDefaultMethods extends Iterable<Object>
    {/* Empty Block*/}
    //  interface ExtendsIterableWithDefaultMethods

    @SuppressWarnings( {"InterfaceNeverImplemented", "PackageVisibleInnerClass"} )
    interface ExtendsOthers extends Callable<Integer>, Comparable<ExtendsOthers>, Throws<IllegalStateException>
    {/* Empty Block*/}
    //  interface ExtendsOthers

    @SuppressWarnings( "PackageVisibleInnerClass" )
    static final class FinalClass
    {
        @SuppressWarnings( "unused" )
        void method() {/* Empty Block*/}
    }
    //  class FinalClass

    @SuppressWarnings( {"AbstractClassNeverImplemented", "AbstractClassWithoutAbstractMethods", "PackageVisibleInnerClass"} )
    abstract static class Generics
    {
        @SuppressWarnings( "unused" )
        <T,R,V extends Throwable> T run( final R param ) throws V { return null; }
    }
    //  class Generics

    @SuppressWarnings( {"AbstractClassNeverImplemented", "PackageVisibleInnerClass"} )
    abstract static class HasAnnotation
    {
        @Override
        public abstract String toString();
    }
    //  class HasAnnotation

    @SuppressWarnings( {"AbstractClassNeverImplemented", "AbstractClassWithoutAbstractMethods", "PackageVisibleInnerClass"} )
    abstract static class InvalidOverrideMethods
    {
        @SuppressWarnings( {"unused", "NoopMethodInAbstractClass"} )
        static void staticMethod() {/* Empty Block*/}

        @SuppressWarnings( "unused" )
        final void finalMethod() {/* Empty Block*/}

        @SuppressWarnings( {"unused", "NoopMethodInAbstractClass"} )
        private void privateMethod() {/* Empty Block*/}
    }
    //  class InvalidOverrideMethods

    @SuppressWarnings( "PackageVisibleInnerClass" )
    @Target( ElementType.PARAMETER )
    @interface Nullable
    {/* Empty Block */}
    //  annotation Nullable

    @SuppressWarnings( {"InterfaceNeverImplemented", "InterfaceMayBeAnnotatedFunctional", "PackageVisibleInnerClass"} )
    interface Throws<R extends RuntimeException>
    {
        void fail() throws R;
    }
    //  interface Throws

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    @SuppressWarnings( "InstanceVariableOfConcreteClass" )
    @Rule
    public final CompilationRule m_Compilation = new CompilationRule();

    private Elements m_Elements;

    private Types m_Types;

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    @Test
    public void addModifiersVarargsShouldNotBeNull()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.methodBuilder( "taco" )
                .addModifiers( (Modifier []) null );
            fail( "NullPointerException expected" );
        }
        catch( final NullArgumentException e )
        {
            assertThat( e.getMessage() ).isEqualTo( "Argument 'modifiers' must not be null" );
        }
    }

    @SuppressWarnings( "CastToConcreteClass" )
    @Test
    public void duplicateExceptionsIgnored()
    {
        final var composer = new JavaComposer();

        final var ioException = ClassName.from( IOException.class );
        final var timeoutException = ClassName.from( TimeoutException.class );
        final var methodSpec = (MethodSpecImpl) composer.methodBuilder( "duplicateExceptions" )
            .addException( ioException )
            .addException( timeoutException )
            .addException( timeoutException )
            .addException( ioException )
            .build();
        assertThat( methodSpec.exceptions() ).isEqualTo( Arrays.asList( ioException, timeoutException ) );
        assertThat( methodSpec.toBuilder( false ).addException( ioException ).build().exceptions() ).isEqualTo( Arrays.asList( ioException, timeoutException ) );
    }

    @Test
    public void equalsAndHashCode()
    {
        final var composer = new JavaComposer();

        var a = composer.constructorBuilder().build();
        var b = composer.constructorBuilder().build();
        assertThat( a.equals( b ) ).isTrue();
        assertThat( a.hashCode() ).isEqualTo( b.hashCode() );
        a = composer.methodBuilder( "taco" ).build();
        b = composer.methodBuilder( "taco" ).build();
        assertThat( a.equals( b ) ).isTrue();
        assertThat( a.hashCode() ).isEqualTo( b.hashCode() );
        final var classElement = getElement( Everything.class );
        final var methodElement = getOnlyElement( methodsIn( classElement.getEnclosedElements() ) );
        a = composer.overridingMethodBuilder( methodElement ).build();
        b = composer.overridingMethodBuilder( methodElement ).build();
        assertThat( a.equals( b ) ).isTrue();
        assertThat( a.hashCode() ).isEqualTo( b.hashCode() );
    }   //  equalsAndHashCode()

    private ExecutableElement findFirst( final Iterable<? extends ExecutableElement> elements, final String name )
    {
        for( final var executableElement : elements )
        {
            if( executableElement.getSimpleName().toString().equals( name ) ){ return executableElement; }
        }
        throw new IllegalArgumentException( name + " not found in " + elements );
    }

    private TypeElement getElement( final Class<?> clazz )
    {
        assumeFalse( isNull( m_Elements ) );
        return m_Elements.getTypeElement( requireNonNullArgument( clazz, "clazz" ).getCanonicalName() );
    }

    @Test
    public void nullAnnotationsAddition()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.methodBuilder( "doSomething" ).addAnnotations( null );
            fail();
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "Argument 'annotationSpecs' must not be null" );
        }
    }

    @Test
    public void nullExceptionsAddition()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.methodBuilder( "doSomething" ).addExceptions( null );
            fail();
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "Argument 'exceptions' must not be null" );
        }
    }

    @Test
    public void nullIsNotAValidMethodName()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.methodBuilder( null );
            fail( "NullArgumentException expected" );
        }
        catch( final NullArgumentException e )
        {
            assertThat( e.getMessage() ).isEqualTo( "Argument 'name' must not be null" );
        }
    }

    @Test
    public void nullParametersAddition()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.methodBuilder( "doSomething" )
                .addParameters( null );
            fail();
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "Argument 'parameterSpecs' must not be null" );
        }
    }

    @Test
    public void nullTypeVariablesAddition()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.methodBuilder( "doSomething" )
                .addTypeVariables( null );
            fail();
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "Argument 'typeVariables' must not be null" );
        }
    }

    @Test
    public void overrideDoesNotCopyDefaultModifier()
    {
        final var composer = new JavaComposer();

        assumeFalse( isNull( m_Elements ) );
        final var classElement = getElement( ExtendsIterableWithDefaultMethods.class );
        final var classType = (DeclaredType) classElement.asType();
        final var methods = methodsIn( m_Elements.getAllMembers( classElement ) );
        final var exec = findFirst( methods, "spliterator" );
        final var method = composer.overridingMethodBuilder( exec, classType, m_Types ).build();
        assertThat( method.toString() ).isEqualTo(
            """
            @java.lang.Override
            public java.util.Spliterator<java.lang.Object> spliterator() {
            }
            """ );
    }

    @Test
    public void overrideDoesNotCopyOverrideAnnotation()
    {
        final var composer = new JavaComposer();

        assumeFalse( isNull( m_Elements ) );
        final var classElement = getElement( HasAnnotation.class );
        final var exec = getOnlyElement( methodsIn( classElement.getEnclosedElements() ) );
        final var method = composer.overridingMethodBuilder( exec ).build();
        assertThat( method.toString() ).isEqualTo(
            """
            @java.lang.Override
            public java.lang.String toString() {
            }
            """ );
    }

    @Test
    public void overrideEverything()
    {
        final var composer = new JavaComposer();

        assumeFalse( isNull( m_Elements ) );
        final var classElement = getElement( Everything.class );
        final var methodElement = getOnlyElement( methodsIn( classElement.getEnclosedElements() ) );
        final var method = composer.overridingMethodBuilder( methodElement ).build();
        assertThat( method.toString() ).isEqualTo(
            """
            @java.lang.Override
            protected <T extends java.lang.Runnable & java.io.Closeable> java.lang.Runnable everything(
                java.lang.String arg0, java.util.List<? extends T> arg1) throws java.io.IOException,
                java.lang.SecurityException {
            }
            """ );
    }

    @Test
    public void overrideExtendsOthersWorksWithActualTypeParameters()
    {
        final var composer = new JavaComposer();

        assumeFalse( isNull( m_Elements ) );
        final var classElement = getElement( ExtendsOthers.class );
        final var classType = (DeclaredType) classElement.asType();
        final var methods = methodsIn( m_Elements.getAllMembers( classElement ) );
        var exec = findFirst( methods, "call" );
        var method = composer.overridingMethodBuilder( exec, classType, m_Types ).build();
        assertThat( method.toString() ).isEqualTo(
            """
            @java.lang.Override
            public java.lang.Integer call() throws java.lang.Exception {
            }
            """ );
        exec = findFirst( methods, "compareTo" );
        method = composer.overridingMethodBuilder( exec, classType, m_Types ).build();
        assertThat( method.toString() ).isEqualTo( "" + "@java.lang.Override\n" + "public int compareTo(" + ExtendsOthers.class.getCanonicalName() + " arg0) {\n" + "}\n" );
        exec = findFirst( methods, "fail" );
        method = composer.overridingMethodBuilder( exec, classType, m_Types ).build();
        assertThat( method.toString() ).isEqualTo(
            """
            @java.lang.Override
            public void fail() throws java.lang.IllegalStateException {
            }
            """ );
    }

    @Test
    public void overrideFinalClassMethod()
    {
        final var composer = new JavaComposer();

        assumeFalse( isNull( m_Elements ) );
        final var classElement = getElement( FinalClass.class );
        final var methods = methodsIn( m_Elements.getAllMembers( classElement ) );
        try
        {
            composer.overridingMethodBuilder( findFirst( methods, "method" ) );
            fail();
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "Cannot override method on final class com.squareup.javapoet.MethodSpecTest.FinalClass" );
        }
    }

    @Test
    public void overrideGenerics()
    {
        final var composer = new JavaComposer();

        assumeFalse( isNull( m_Elements ) );
        final var classElement = getElement( Generics.class );
        final var methodElement = getOnlyElement( methodsIn( classElement.getEnclosedElements() ) );
        final var method = composer.overridingMethodBuilder( methodElement )
            .addStatement( "return null" )
            .build();
        assertThat( method.toString() ).isEqualTo(
            """
            @java.lang.Override
            <T, R, V extends java.lang.Throwable> T run(R param) throws V {
              return null;
            }
            """ );
    }

    @Test
    public void overrideInvalidModifiers()
    {
        final var composer = new JavaComposer();

        assumeFalse( isNull( m_Elements ) );
        final var classElement = getElement( InvalidOverrideMethods.class );
        final var methods = methodsIn( m_Elements.getAllMembers( classElement ) );
        try
        {
            composer.overridingMethodBuilder( findFirst( methods, "finalMethod" ) );
            fail();
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "cannot override method with modifiers: [final]" );
        }
        try
        {
            composer.overridingMethodBuilder( findFirst( methods, "privateMethod" ) );
            fail();
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "cannot override method with modifiers: [private]" );
        }
        try
        {
            composer.overridingMethodBuilder( findFirst( methods, "staticMethod" ) );
            fail();
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "cannot override method with modifiers: [static]" );
        }
    }

    @Before
    public void setUp()
    {
        m_Elements = m_Compilation.getElements();
        m_Types = m_Compilation.getTypes();
    }

    /**
     *  Ensure that at least some tests will be executed.
     *
     *  @throws Exception   Something went awfully wrong.
     */
    @Test
    public final void testEnsureExecution() throws Exception
    {
        assumeTrue( false );
        fail( "This test was executed" );
    }   //  testEnsureExecution()
}
//  class TestMethodSpec

/*
 *  End of File
 */