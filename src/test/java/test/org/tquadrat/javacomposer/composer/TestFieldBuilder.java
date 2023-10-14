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

package test.org.tquadrat.javacomposer.composer;

import static java.lang.String.format;
import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.EmptyArgumentException;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the methods
 *  {@link JavaComposer#fieldBuilder(TypeSpec, CharSequence, Modifier...)}
 *  {@link JavaComposer#fieldBuilder(TypeName, CharSequence, Modifier...)}
 *  and
 *  {@link JavaComposer#fieldBuilder(Type, CharSequence, Modifier...)}
 *  from the class
 *  {@link JavaComposer}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestFieldBuilder.java 1076 2023-10-03 18:36:07Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestFieldBuilder.java 1076 2023-10-03 18:36:07Z tquadrat $" )
@DisplayName( "TestFieldBuilder" )
public class TestFieldBuilder extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for the methods
     *  {@link JavaComposer#fieldBuilder(TypeSpec, CharSequence, Modifier...)}
     *  {@link JavaComposer#fieldBuilder(TypeName, CharSequence, Modifier...)}
     *  and
     *  {@link JavaComposer#fieldBuilder(Type, CharSequence, Modifier...)}.
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @Test
    final void testFieldBuilderWithEmptyArgument() throws Exception
    {
        skipThreadTest();

        final var candidate = new JavaComposer();

        final Class<?> type = Class.class;
        final var typeName = ClassName.from( type );
        final var typeSpec = candidate.classBuilder( typeName )
            .build();
        final var modifiers = new Modifier [0];

        final Class<? extends Throwable> expectedException = EmptyArgumentException.class;
        try
        {
            candidate.fieldBuilder( type, EMPTY_STRING, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
        try
        {
            candidate.fieldBuilder( typeName, EMPTY_STRING, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
        try
        {
            candidate.fieldBuilder( typeSpec, EMPTY_STRING, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }   //  testFieldBuilderWithEmptyArgument()

    /**
     *  Tests for the methods
     *  {@link JavaComposer#fieldBuilder(TypeSpec, CharSequence, Modifier...)}
     *  {@link JavaComposer#fieldBuilder(TypeName, CharSequence, Modifier...)}
     *  and
     *  {@link JavaComposer#fieldBuilder(Type, CharSequence, Modifier...)}.
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @Test
    final void testFieldBuilderWithNullArgument() throws Exception
    {
        skipThreadTest();

        final var candidate = new JavaComposer();

        Class<?> type;
        ClassName typeName;
        TypeSpec typeSpec;
        CharSequence name;
        Modifier [] modifiers;

        final Class<? extends Throwable> expectedException = NullArgumentException.class;

        type = null;
        typeName = null;
        typeSpec = null;
        name = "name";
        modifiers = new Modifier [0];
        try
        {
            candidate.fieldBuilder( type, name, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
        try
        {
            candidate.fieldBuilder( typeName, name, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
        try
        {
            candidate.fieldBuilder( typeSpec, name, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        type = Class.class;
        typeName = ClassName.from( type );
        typeSpec = candidate.classBuilder( typeName )
            .build();
        name = null;
        modifiers = new Modifier [0];
        try
        {
            candidate.fieldBuilder( type, name, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
        try
        {
            candidate.fieldBuilder( typeName, name, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
        try
        {
            candidate.fieldBuilder( typeSpec, name, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        type = Class.class;
        typeName = ClassName.from( type );
        typeSpec = candidate.classBuilder( typeName )
            .build();
        name = "name";
        modifiers = null;
        try
        {
            candidate.fieldBuilder( type, name, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
        try
        {
            candidate.fieldBuilder( typeName, name, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
        try
        {
            candidate.fieldBuilder( typeSpec, name, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }   //  testFieldBuilderWithNullArgument()
}
//  class TestFieldBuilder

/*
 *  End of File
 */