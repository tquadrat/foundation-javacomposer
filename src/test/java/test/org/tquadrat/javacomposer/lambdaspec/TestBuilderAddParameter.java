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

package test.org.tquadrat.javacomposer.lambdaspec;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.javacomposer.Primitives.INT;
import static org.tquadrat.foundation.javacomposer.Primitives.VOID;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.util.StringUtils.format;

import java.lang.reflect.Type;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.EmptyArgumentException;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.LambdaSpec;
import org.tquadrat.foundation.javacomposer.ParameterSpec;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the methods
 *  {@link LambdaSpec.Builder#addParameter(String)},
 *  {@link LambdaSpec.Builder#addParameter(Type, String)}
 *  and
 *  {@link LambdaSpec.Builder#addParameter(TypeName, String)}
 *  from the class
 *  {@link LambdaSpec.Builder}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestBuilderAddParameter.java 943 2021-12-21 01:34:32Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestBuilderAddParameter.java 943 2021-12-21 01:34:32Z tquadrat $" )
@DisplayName( "TestBuilderAddParameter" )
public class TestBuilderAddParameter extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for the implementations of the methods
     *  {@link LambdaSpec.Builder#addParameter(String)},
     *  {@link LambdaSpec.Builder#addParameter(Type, String)}
     *  and
     *  {@link LambdaSpec.Builder#addParameter(TypeName, String)}.
     */
    @Test
    final void testBuilderAddParameterWithEmptyArgument()
    {
        skipThreadTest();

        final var composer = new JavaComposer();
        final var candidate = composer.lambdaBuilder();

        final Class<? extends Throwable> expectedException = EmptyArgumentException.class;

        final var name = EMPTY_STRING;
        try
        {
            candidate.addParameter( name );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        try
        {
            candidate.addParameter( Object.class, name );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        try
        {
            candidate.addParameter( INT, name );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }   //  testBuilderAddParameterWithEmptyArgument()

    /**
     *  Tests for the implementations of the methods
     *  {@link LambdaSpec.Builder#addParameter(ParameterSpec)},
     *  {@link LambdaSpec.Builder#addParameter(String)},
     *  {@link LambdaSpec.Builder#addParameter(Type, String)}
     *  and
     *  {@link LambdaSpec.Builder#addParameter(TypeName, String)}.<br>
     *  <br>As
     *  {@link LambdaSpec.Builder#addParameter(String)},
     *  {@link LambdaSpec.Builder#addParameter(Type, String)}
     *  and
     *  {@link LambdaSpec.Builder#addParameter(TypeName, String)}
     *  will all call
     *  {@link LambdaSpec.Builder#addParameter(ParameterSpec)},
     *  the test code will only use that method.<br>
     *  <br>Either the type for all parameters are inferred, or all parameters
     *  do need to have an explicit type.
     */
    @Test
    final void testBuilderAddParameterWithInvalidArgument()
    {
        skipThreadTest();

        final Class<? extends Throwable> expectedException = ValidationException.class;

        final var composer = new JavaComposer();

        LambdaSpec.Builder candidate;
        ParameterSpec parameterSpec;

        //---* Inferring types *-----------------------------------------------
        candidate = composer.lambdaBuilder();
        parameterSpec = composer.parameterOf( VOID, "a" );
        candidate.addParameter( parameterSpec );
        parameterSpec = composer.parameterOf( String.class, "b" );
        try
        {
            candidate.addParameter( parameterSpec );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        //---* Explicit types *------------------------------------------------
        candidate = composer.lambdaBuilder();
        parameterSpec = composer.parameterOf( String.class, "a" );
        candidate.addParameter( parameterSpec );
        parameterSpec = composer.parameterOf( VOID, "b" );
        try
        {
            candidate.addParameter( parameterSpec );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }   //  testBuilderAddParameterWithInvalidArgument()

    /**
     *  Tests for the implementations of the methods
     *  {@link LambdaSpec.Builder#addParameter(ParameterSpec)},
     *  {@link LambdaSpec.Builder#addParameter(String)},
     *  {@link LambdaSpec.Builder#addParameter(Type, String)}
     *  and
     *  {@link LambdaSpec.Builder#addParameter(TypeName, String)}.
     */
    @Test
    final void testBuilderAddParameterWithNullArgument()
    {
        skipThreadTest();

        final var composer = new JavaComposer();
        final var candidate = composer.lambdaBuilder();

        final Class<? extends Throwable> expectedException = NullArgumentException.class;

        String name;
        Type type;
        TypeName typeName;

        final ParameterSpec parameterSpec = null;
        try
        {
            candidate.addParameter( parameterSpec );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        name = null;
        try
        {
            candidate.addParameter( name );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        type = null;
        name = "name";
        try
        {
            candidate.addParameter( type, name );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        type = Object.class;
        name = null;
        try
        {
            candidate.addParameter( type, name );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        typeName = null;
        name = "name";
        try
        {
            candidate.addParameter( typeName, name );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        typeName = TypeName.from( Object.class );
        name = null;
        try
        {
            candidate.addParameter( typeName, name );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }   //  testBuilderAddParameterWithNullArgument()
}
//  class TestBuilderAddParameter

/*
 *  End of File
 */