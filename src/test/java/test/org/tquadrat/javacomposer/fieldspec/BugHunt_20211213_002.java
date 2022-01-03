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

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.apiguardian.api.API.Status.STABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.tquadrat.foundation.javacomposer.Layout.LAYOUT_FOUNDATION;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.apiguardian.api.API;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  A field spec does not have a type applied to it.
 *  @version $Id: BugHunt_20211213_002.java 943 2021-12-21 01:34:32Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: BugHunt_20211213_002.java 943 2021-12-21 01:34:32Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
@DisplayName( "BugHunt_20211213_002" )
public class BugHunt_20211213_002 extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
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

        final var composer = new JavaComposer( LAYOUT_FOUNDATION );
        final var expected =
            """
            @java.lang.SuppressWarnings( "javadoc" )
            class DummyClass
            {
                    /*------------------------*\\
                ====** Static Initialisations **===========================================
                    \\*------------------------*/
                @java.lang.SuppressWarnings( "javadoc" )
                public static final java.lang.String m_Charset;

                @java.lang.SuppressWarnings( "javadoc" )
                public static final java.lang.String m_CLIDefinitions;

                @java.lang.SuppressWarnings( "javadoc" )
                public static final java.lang.String m_Clock;
            }
            //  class DummyClass
            """;

        final var typeName = ClassName.from( "org.tquadrat.test", "DummyClass" );
        final var candidate = composer.classBuilder( typeName )
            .addField( composer.fieldBuilder( String.class, "m_CLIDefinitions", Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC ).build() )
            .addField( composer.fieldBuilder( String.class, "m_Charset", Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC ).build() )
            .addField( composer.fieldBuilder( String.class, "m_Clock", Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC ).build() ).build();
        assertNotNull( candidate );
        final var actual = candidate.toString();
        assertEquals( expected, actual );
    }   //  testCreateFieldSpec()

    /**
     *  The problematic codes uses
     *  {@link java.util.Comparator#comparing(Function)}
     *  to sort the field specs.
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @Test
    final void checkComparing() throws Exception
    {
        skipThreadTest();

        final Collection<String> candidate = new ArrayList<>( List.of( "m_Clock", "m_Charset", "m_CLIDefinition" ) );
        var actual = candidate.stream()
            .sorted( comparing( String::toString ) )
            .collect( joining( ", " ) );
        var expected = "m_CLIDefinition, m_Charset, m_Clock";
        assertEquals( expected, actual );

        candidate.add( "m_CLock" );
        candidate.add( "m_CLoak" );
        candidate.add( "m_CLOak" );
        actual = candidate.stream()
            .sorted( comparing( String::toString, CASE_INSENSITIVE_ORDER ) )
            .collect( joining( ", " ) );
        expected = "m_Charset, m_CLIDefinition, m_CLoak, m_CLOak, m_Clock, m_CLock";
        assertEquals( expected, actual );
    }   //  checkComparing()
}
//  class BugHunt_20211213_002

/*
 *  End of File
 */