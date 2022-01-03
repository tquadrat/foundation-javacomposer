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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Some tests for the interface
 *  {@link CodeBlock}
 *  and its implementation.<br>
 *  <br>Be careful when changing this class as any change may break the tests
 *  with debug output!!
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestCodeBlock.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestCodeBlock.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "test.org.tquadrat.javacomposer.TestCodeBlock" )
public class TestCodeBlock extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests the method
     *  {@link CodeBlock.Builder#add(boolean, String, Object...)}
     *  with and without debug output.
     */
    @Test
    final void testCodeBlockBuilderAdd()
    {
        skipThreadTest();

        JavaComposer composer;

        CodeBlock.Builder candidate;
        CodeBlock result;
        String actual, expected;

        composer = new JavaComposer( false );
        candidate = composer.codeBlockBuilder();
        result = candidate.add( "$T value = $S", String.class, "value" )
            .build();
        expected =
            """
            java.lang.String value = "value"\
            """;
        actual = result.toString();
        assertEquals( expected, actual );

        composer = new JavaComposer( true );
        candidate = composer.codeBlockBuilder();
        result = candidate.add( "$T value = $S", String.class, "value" )
            .build(); // Line: 73!!
        expected =
            """
             /* [TestCodeBlock.java:76] */ java.lang.String value = "value"\
            """;
        actual = result.toString();
        assertEquals( expected, actual );
    }   //  testCodeBlockBuilderAdd()

    /**
     *  Tests the method
     *  {@link CodeBlock#of(boolean, String, Object...)}
     *  with and without debug output.
     */
    @Test
    final void testCodeBlockOf()
    {
        skipThreadTest();

        var composer = new JavaComposer();

        CodeBlock candidate;
        String actual, expected;

        candidate = composer.codeBlockOf( "$T value = $S", String.class, "value" );
        expected =
            """
            java.lang.String value = "value"\
            """;
        actual = candidate.toString();
        assertEquals( expected, actual );

        composer = new JavaComposer( true );
        candidate = composer.codeBlockOf( "$T value = $S", String.class, "value" );
        expected =
            """
             /* [TestCodeBlock.java:110] */ java.lang.String value = "value"\
            """;
        actual = candidate.toString();
        assertEquals( expected, actual );
    }   //  testCodeBlockOf()
}
//  class TestCodeBlock

/*
 *  End of File
 */