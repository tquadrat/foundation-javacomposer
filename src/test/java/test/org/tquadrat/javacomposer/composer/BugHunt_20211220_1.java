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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the methods
 *  {@link JavaComposer#annotationBuilder(ClassName)}
 *  and
 *  {@link JavaComposer#annotationBuilder(Class)}
 *  from the class
 *  {@link JavaComposer}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: BugHunt_20211220_1.java 943 2021-12-21 01:34:32Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: BugHunt_20211220_1.java 943 2021-12-21 01:34:32Z tquadrat $" )
@DisplayName( "test.org.tquadrat.javacomposer.composer.BugHunt_20211220_1" )
public class BugHunt_20211220_1 extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  <p>{@summary Tests for the methods
     *  {@link JavaComposer#annotationBuilder(ClassName)}
     *  and
     *  {@link JavaComposer#annotationBuilder(Class)}.</p>}
     *  <p>A composer in DEBUG mode will create annotations that does not
     *  compile:</p>
     *  <code><pre>@org.tquadrat.foundation.annotation.ClassVersion(sourceVersion = { /&#42; [Optional.java:178] &#42;/  /&#42; [BugHunt_20211220_1.java:75] &#42;/ , "$Id: BugHunt_20211220_1.java 943 2021-12-21 01:34:32Z tquadrat $"})</pre></code>
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @Test
    final void testAnnotationBuilder() throws Exception
    {
        skipThreadTest();

        final var candidate = new JavaComposer( true );

        var actual = candidate.annotationBuilder( Override.class )
            .build()
            .toString();
        var expected =
            """
            @java.lang.Override\
            """;
        assertEquals( expected, actual );

        var builder = candidate.annotationBuilder( ClassVersion.class );
        builder.addMember( "sourceVersion", "$S", "$Id: BugHunt_20211220_1.java 943 2021-12-21 01:34:32Z tquadrat $" );
        actual = builder.build()
            .toString();
        expected =
            """
            @org.tquadrat.foundation.annotation.ClassVersion(sourceVersion =  /* [BugHunt_20211220_1.java:76] */ "$Id: BugHunt_20211220_1.java 943 2021-12-21 01:34:32Z tquadrat $")\
            """;
        assertEquals( expected, actual );
    }   //  testAnnotationBuilderWithNullArgument()
}
//  class BugHunt_20211220_1

/*
 *  End of File
 */