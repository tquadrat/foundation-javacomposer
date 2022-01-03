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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.internal.ClassNameImpl;
import org.tquadrat.foundation.javacomposer.internal.CodeWriter;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Some test for the class
 *  {@link CodeWriter}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestCodeWriter.java 943 2021-12-21 01:34:32Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestCodeWriter.java 943 2021-12-21 01:34:32Z tquadrat $" )
@DisplayName( "test.org.tquadrat.javacomposer.internal.TestCodeWriter" )
public class TestCodeWriter extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Lookup a name for a class.<br>
     *  <br>This is a test for
     *  {@link CodeWriter#lookupName(ClassNameImpl)}.
     */
    @Test
    final void lookupName1()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var imports = List.of( Float.class, String.class, File.class, this.getClass() );

        //---* Create the CodeWriter instance *--------------------------------
        final var out = new StringBuilder();
        final Map<String,ClassNameImpl> importedTypes = new LinkedHashMap<>();
        for( final var c : imports )
        {
            importedTypes.put( c.getSimpleName(), ClassNameImpl.from( c ) );
        }
        final Set<String> staticImports = Set.of();
        final var candidate = new CodeWriter( composer, out, importedTypes, staticImports );
        assertNotNull( candidate );

        for( final var c : imports )
        {
            final var expected = c.getSimpleName();
            final var actual = candidate.lookupName( ClassNameImpl.from( c ) );
            assertEquals( expected, actual );
        }
    }   //  lookupName1()
}
//  class TestCodeWriter

/*
 *  End of File
 */