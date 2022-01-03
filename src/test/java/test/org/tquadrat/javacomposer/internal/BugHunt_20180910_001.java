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

package test.org.tquadrat.javacomposer.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  The code
 *  <pre><code>  codeBuilder.addStatement( "$L = new $T( $S, $L, $S, $S, $S, $L, $L, $L, $S )", definitionName, CLIOptionDefinition.class,
 *      property.getPropertyName(),
 *      names,
 *      usage,
 *      usageKey,
 *      metaVar,
 *      required,
 *      handlerName,
 *      multiValued,
 *      format );</code></pre> renders to
 *  <pre><code>  cliDefinition = new CLIArgumentDefinition( "cmdLineArg0", 0, "null", "null", "CMD_LINE_ARG0", false, valueHandler, false, "null" );</code></pre>
 *  when {@code usage}, {@code usageKey}, and {@code format} are
 *  {@code null}.<br>
 *  <br>But the expected result would be
 *  <pre><code>  cliDefinition = new CLIArgumentDefinition( "cmdLineArg0", 0, null, null, "CMD_LINE_ARG0", false, valueHandler, false, null );</code></pre>
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: BugHunt_20180910_001.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: BugHunt_20180910_001.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "test.org.tquadrat.javacomposer.internal.BugHunt_20180910_001" )
public class BugHunt_20180910_001 extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Test the rendering of {@code null}.
     */
    @Test
    final void testNullRendering()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var name = "name";
        final String value = null;

        final var candidate = composer.codeBlockBuilder();
        candidate.addStatement( "$L = $S", name, value );
        final var expected =
            """
            name = null;
            """;
        final var actual = candidate.build().toString();
        assertEquals( expected, actual );
    }   //  testNullRendering()
}
//  class BugHunt_20180910_001

/*
 *  End of File
 */