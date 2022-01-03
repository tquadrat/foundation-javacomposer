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

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.util.StringUtils.format;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Types;
import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the methods
 *  {@link JavaComposer#overridingMethodBuilder(Method)}
 *  {@link JavaComposer#overridingMethodBuilder(ExecutableElement, DeclaredType, Types)}
 *  and
 *  {@link JavaComposer#overridingMethodBuilder(ExecutableElement)}
 *  from the class
 *  {@link JavaComposer}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestOverridingMethodBuilder.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestOverridingMethodBuilder.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestOverridingMethodBuilder" )
public class TestOverridingMethodBuilder extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for the methods
     *  {@link JavaComposer#overridingMethodBuilder(Method)}
     *  {@link JavaComposer#overridingMethodBuilder(ExecutableElement, DeclaredType, Types)}
     *  and
     *  {@link JavaComposer#overridingMethodBuilder(ExecutableElement)}.
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @Test
    final void testOverridingMethodBuilderWithNullArgument() throws Exception
    {
        skipThreadTest();

        final var candidate = new JavaComposer();

        final Class<? extends Throwable> expectedException = NullArgumentException.class;

        final Method method = null;
        try
        {
            candidate.overridingMethodBuilder( method );
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

        ExecutableElement executableElement;
        DeclaredType declaredType;
        Types types;

        executableElement = null;
        declaredType = mock( "declaredType", DeclaredType.class );
        types = mock( "types", Types.class );
        replayAll();
        try
        {
            candidate.overridingMethodBuilder( executableElement );
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
            candidate.overridingMethodBuilder( executableElement, declaredType, types );
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
        resetAll();

        executableElement = mock( "executableElement", ExecutableElement.class );
        declaredType = null;
        types = mock( "types", Types.class );
        replayAll();
        try
        {
            candidate.overridingMethodBuilder( executableElement, declaredType, types );
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
        resetAll();

        executableElement = mock( "executableElement", ExecutableElement.class );
        declaredType = mock( "declaredType", DeclaredType.class );
        types = null;
        replayAll();
        try
        {
            candidate.overridingMethodBuilder( executableElement, declaredType, types );
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
        resetAll();
    }   //  testOverridingMethodBuilderWithNullArgument()
}
//  class TestOverridingMethodBuilder

/*
 *  End of File
 */