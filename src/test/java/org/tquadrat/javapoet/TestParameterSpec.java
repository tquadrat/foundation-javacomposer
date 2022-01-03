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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.util.StringUtils.format;

import javax.lang.model.element.Modifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.testutil.TestBaseClass;

@SuppressWarnings( "javadoc" )
@ClassVersion( sourceVersion = "$Id: TestParameterSpec.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestParameterSpec" )
public class TestParameterSpec extends TestBaseClass
{
    @Test
    public void equalsAndHashCode()
    {
        skipThreadTest();

        final var composer = new JavaComposer();
        var a = composer.parameterBuilder( int.class, "foo" ).build();
        var b = composer.parameterBuilder( int.class, "foo" ).build();
        assertThat( a.equals( b ) ).isTrue();
        assertThat( a.hashCode() ).isEqualTo( b.hashCode() );
        a = composer.parameterBuilder( int.class, "i" ).addModifiers( Modifier.STATIC ).build();
        b = composer.parameterBuilder( int.class, "i" ).addModifiers( Modifier.STATIC ).build();
        assertThat( a.equals( b ) ).isTrue();
        assertThat( a.hashCode() ).isEqualTo( b.hashCode() );
    }   //  equalsAndHashCode()

    @Test
    public void nullAnnotationsAddition()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = NullArgumentException.class;
        try
        {
            composer.parameterBuilder( int.class, "foo" ).addAnnotations( null );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }   //  nullAnnotationsAddition()
}
//  class TestParameterSpec

/*
 *  End of File
 */