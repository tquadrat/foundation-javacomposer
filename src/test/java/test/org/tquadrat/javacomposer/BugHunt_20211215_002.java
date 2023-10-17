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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Some tests regarding some errors.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: BugHunt_20211215_002.java 938 2021-12-15 14:42:53Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: BugHunt_20211215_002.java 938 2021-12-15 14:42:53Z tquadrat $" )
@DisplayName( "test.org.tquadrat.javacomposer.BugHunt_20211215_002" )
public class BugHunt_20211215_002 extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for
     *  {@link CodeBlock.Builder#addStatement(CodeBlock)}
     *
     *  @throws Exception   Something went wrong unexpectedly.
     *
     *  @deprecated {@link CodeBlock.Builder#addStatement(CodeBlock)} is
     *      deprecated.
     */
    @Test
    @Deprecated
    final void testCodeBlockAddStatement1() throws Exception
    {
        skipThreadTest();

        /*
         * It looks like that CodeBlock.Builder.addStatement(CodeBlock) does
         * not work properly.
         */
        final var composer = new JavaComposer();

        final var codeBlock = composer.codeBlockOf(
            """
            final var s = "text";
             """ );

        final var candidate = composer.codeBlockBuilder()
            .addStatement( codeBlock )
            .build();

        final var actual = candidate.toString();
        // That's not what we want …
        final var expected =
            """
            final var s = "text";
                ;
            """;
        assertEquals( expected, actual );
    }   //  testCodeBlockAddStatement1()

    /**
     *  Tests for
     *  {@link CodeBlock.Builder#addStatement(CodeBlock)}
     *
     *  @throws Exception   Something went wrong unexpectedly.
     *
     *  @deprecated {@link CodeBlock.Builder#addStatement(CodeBlock)} is
     *      deprecated.
     */
    @Deprecated
    @Test
    final void testCodeBlockAddStatement2() throws Exception
    {
        skipThreadTest();

        /*
         * CodeBlock.Builder.addStatement(CodeBlock) causes an
         * IllegalStateException if the code block was created with a call to
         * CodeBlock.addStatement().
         */
        final var composer = new JavaComposer();

        final var codeBlock = composer.codeBlockOf(
            """
            final var s = "text";
             """ );

        final var candidate = composer.codeBlockBuilder()
            .addStatement( codeBlock )
            .addStatement( composer.createReturnStatement() )
            .build();

        assertThrows( IllegalStateException.class, candidate::toString );
    }   //  testCodeBlockAddStatement2()
}
//  class BugHunt_20211215_002

/*
 *  End of File
 */