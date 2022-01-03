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
import static org.junit.jupiter.api.Assertions.fail;

import javax.lang.model.element.Modifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.javacomposer.JavaComposer;

@SuppressWarnings( {"static-method", "javadoc"} )
@ClassVersion( sourceVersion = "$Id: TestFieldSpec.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestFieldSpec" )
public class TestFieldSpec
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    @Test
    public void equalsAndHashCode()
    {
        final var composer = new JavaComposer();

        var a = composer.fieldBuilder( int.class, "foo" ).build();
        var b = composer.fieldBuilder( int.class, "foo" ).build();
        assertThat( a.equals( b ) ).isTrue();
        assertThat( a.hashCode() ).isEqualTo( b.hashCode() );
        a = composer.fieldBuilder( int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC ).build();
        b = composer.fieldBuilder( int.class, "FOO", Modifier.PUBLIC, Modifier.STATIC ).build();
        assertThat( a.equals( b ) ).isTrue();
        assertThat( a.hashCode() ).isEqualTo( b.hashCode() );
    }

    @Test
    public void nullAnnotationsAddition()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.fieldBuilder( int.class, "foo" ).addAnnotations( null );
            fail( "Expected Exception was not thrown");
        }
        catch( final NullArgumentException expected )
        {
            assertThat( expected.getMessage() ).isEqualTo( "Argument 'annotationSpecs' must not be null" );
        }
    }
}
//  class TestFieldSpec

/*
 *  End of File
 */