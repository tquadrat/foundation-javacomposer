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

package test.org.tquadrat.javacomposer.fieldspec;

import static org.apiguardian.api.API.Status.STABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.lang.model.element.Modifier;

import org.apiguardian.api.API;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  A field spec does not have a type applied to it.
 *  @version $Id: BugHunt_20210117_001.java 943 2021-12-21 01:34:32Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: BugHunt_20210117_001.java 943 2021-12-21 01:34:32Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
@DisplayName( "BugHunt_20210117_001" )
public class BugHunt_20210117_001 extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a type spec for the given type name.
     *
     *  @param  composer    The factory for the composer artifacts.
     *  @param  typeName    The type name.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private TypeSpec createTypeSpec( final JavaComposer composer, final String typeName )
    {
        final var builder = composer.classBuilder( typeName );
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createTypeSpec()

    /**
     *  Some tests for the generation of
     *  {@link FieldSpec}
     *  instances.
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @Test
    final void testCreateFieldSpec() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();
        final var expected =
            """
            public static final java.lang.String constant;
            """;

        final var type = String.class;
        final var typeName = TypeName.from( type );
        final var fake = createTypeSpec( composer, type.getName() );

        var builder = composer.fieldBuilder( type, "constant", Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC );
        assertNotNull( builder );
        var candidate = builder.build();
        assertNotNull( candidate );
        var actual = candidate.toString();
        assertEquals( expected, actual );

        builder = composer.fieldBuilder( typeName, "constant", Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC );
        assertNotNull( builder );
        candidate = builder.build();
        assertNotNull( candidate );
        actual = candidate.toString();
        assertEquals( expected, actual );

        builder = composer.fieldBuilder( fake, "constant", Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC );
        assertNotNull( builder );
        candidate = builder.build();
        assertNotNull( candidate );
        actual = candidate.toString();
        assertEquals( expected, actual );
    }   //  testCreateFieldSpec()
}
//  class BugHunt_20210117_001

/*
 *  End of File
 */