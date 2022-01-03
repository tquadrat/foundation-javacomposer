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

package test.org.tquadrat.javacomposer;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.DEPRECATION;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.JAVADOC;
import static org.tquadrat.foundation.javacomposer.SuppressableWarnings.createSuppressWarningsAnnotation;
import static org.tquadrat.foundation.util.StringUtils.format;

import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.SuppressableWarnings;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the methods in
 *  {@link SuppressableWarnings}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestSuppressableWarnings.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestSuppressableWarnings.java 937 2021-12-14 21:59:00Z tquadrat $" )
public class TestSuppressableWarnings extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Calls
     *  {@link SuppressableWarnings#createSuppressWarningsAnnotation(SuppressableWarnings...)}
     *  with valid arguments.
     */
    @Test
    final void testCreateSuppressWarningsAnnotation()
    {
        skipThreadTest();

        final var composer = new JavaComposer();
        var expected = "@java.lang.SuppressWarnings(\"deprecation\")";
        var actual = createSuppressWarningsAnnotation( composer, DEPRECATION ).toString();
        assertEquals( expected, actual );

        expected = "@java.lang.SuppressWarnings({\"deprecation\", \"javadoc\"})";
        actual = createSuppressWarningsAnnotation( composer, DEPRECATION, JAVADOC ).toString();
        assertEquals( expected, actual );
    }   //  testCreateSuppressWarningsAnnotationNull()

    /**
     *  Calls
     *  {@link SuppressableWarnings#createSuppressWarningsAnnotation(SuppressableWarnings...)}
     *  with {@code null}.
     */
    @Test
    final void testCreateSuppressWarningsAnnotationWithNullArgument()
    {
        skipThreadTest();

        JavaComposer composer;
        SuppressableWarnings [] warnings;

        final Class<? extends Throwable> expectedException = NullArgumentException.class;

        composer = new JavaComposer();
        warnings = null;
        try
        {
            createSuppressWarningsAnnotation( composer, warnings );
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

        composer = null;
        warnings = new SuppressableWarnings [0];
        try
        {
            createSuppressWarningsAnnotation( composer, warnings );
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
    }   //  testCreateSuppressWarningsAnnotationWithNullArgument()
}
//  class TestSuppressableWarnings

/*
 *  End of File
 */