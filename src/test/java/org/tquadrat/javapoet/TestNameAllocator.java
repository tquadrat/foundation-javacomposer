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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.NameAllocator;

@ClassVersion( sourceVersion = "$Id: TestNameAllocator.java 937 2021-12-14 21:59:00Z tquadrat $" )
@SuppressWarnings( {"static-method"} )
@DisplayName( "TestNameAllocator" )
public final class TestNameAllocator
{
    @Test
    public void characterMappingInvalidStartButValidPart() throws Exception
    {
        final var nameAllocator = new NameAllocator();
        assertThat( nameAllocator.newName( "1ab", 1 ) ).isEqualTo( "_1ab" );
    }

    @Test
    public void characterMappingInvalidStartIsInvalidPart() throws Exception
    {
        final var nameAllocator = new NameAllocator();
        final var actual = nameAllocator.newName( "&ab", 1 );
        final var expected = "_ab";
        assertEquals( expected, actual );
    }

    @Test
    public void characterMappingSubstitute() throws Exception
    {
        final var nameAllocator = new NameAllocator();
        assertThat( nameAllocator.newName( "a-b", 1 ) ).isEqualTo( "a_b" );
    }

    @Test
    public void characterMappingSurrogate() throws Exception
    {
        final var nameAllocator = new NameAllocator();
        assertThat( nameAllocator.newName( "a\uD83C\uDF7Ab", 1 ) ).isEqualTo( "a_b" );
    }

    @Test
    public void cloneUsage() throws Exception
    {
        final var outerAllocator = new NameAllocator();
        outerAllocator.newName( "foo", 1 );

        final var innerAllocator1 = outerAllocator.clone();
        assertThat( innerAllocator1.newName( "bar", 2 ) ).isEqualTo( "bar" );
        assertThat( innerAllocator1.newName( "foo", 3 ) ).isEqualTo( "foo_" );

        final var innerAllocator2 = outerAllocator.clone();
        assertThat( innerAllocator2.newName( "foo", 2 ) ).isEqualTo( "foo_" );
        assertThat( innerAllocator2.newName( "bar", 3 ) ).isEqualTo( "bar" );
    }

    @Test
    public void javaKeyword() throws Exception
    {
        final var nameAllocator = new NameAllocator();
        assertThat( nameAllocator.newName( "public", 1 ) ).isEqualTo( "public_" );
        assertThat( nameAllocator.get( 1 ) ).isEqualTo( "public_" );
    }

//    @Test
//    public void nameCollision() throws Exception
//    {
//        final var nameAllocator = new NameAllocator();
//        assertThat( nameAllocator.newName( "foo" ) ).isEqualTo( "foo" );
//        assertThat( nameAllocator.newName( "foo" ) ).isEqualTo( "foo_" );
//        assertThat( nameAllocator.newName( "foo" ) ).isEqualTo( "foo__" );
//    }

    @Test
    public void nameCollisionWithTag() throws Exception
    {
        final var nameAllocator = new NameAllocator();
        assertThat( nameAllocator.newName( "foo", 1 ) ).isEqualTo( "foo" );
        assertThat( nameAllocator.newName( "foo", 2 ) ).isEqualTo( "foo_" );
        assertThat( nameAllocator.newName( "foo", 3 ) ).isEqualTo( "foo__" );
        assertThat( nameAllocator.get( 1 ) ).isEqualTo( "foo" );
        assertThat( nameAllocator.get( 2 ) ).isEqualTo( "foo_" );
        assertThat( nameAllocator.get( 3 ) ).isEqualTo( "foo__" );
    }

    @Test
    public void tagReuseForbidden() throws Exception
    {
        final var nameAllocator = new NameAllocator();
        nameAllocator.newName( "foo", 1 );
        try
        {
            nameAllocator.newName( "bar", 1 );
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "tag '1' cannot be used for both 'foo' and 'bar'" );
        }
    }

    @Test
    public void usage() throws Exception
    {
        final var nameAllocator = new NameAllocator();
        assertThat( nameAllocator.newName( "foo", 1 ) ).isEqualTo( "foo" );
        assertThat( nameAllocator.newName( "bar", 2 ) ).isEqualTo( "bar" );
        assertThat( nameAllocator.get( 1 ) ).isEqualTo( "foo" );
        assertThat( nameAllocator.get( 2 ) ).isEqualTo( "bar" );
    }

    @Test
    public void useBeforeAllocateForbidden() throws Exception
    {
        final var nameAllocator = new NameAllocator();
        try
        {
            nameAllocator.get( 1 );
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "unknown tag: 1" );
        }
    }
}
//  class TestNameAllocator

/*
 *  End of File
 */