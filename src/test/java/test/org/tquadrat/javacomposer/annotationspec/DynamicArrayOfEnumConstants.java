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

package test.org.tquadrat.javacomposer.annotationspec;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.AnnotationSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.javapoet.TestAnnotationSpec.HasDefaultsAnnotation;

/**
 *  A tests for the class
 *  {@link AnnotationSpec}
 *  that was created to validate the migration.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: DynamicArrayOfEnumConstants.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: DynamicArrayOfEnumConstants.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "org.tquadrat.foundation.javacomposer.annotationspec.DynamicArrayOfEnumConstants" )
public final class DynamicArrayOfEnumConstants
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    @SuppressWarnings( "javadoc" )
    public enum Breakfast
    {
            /*------------------*\
        ====** Enum Declaration **=============================================
            \*------------------*/
        WAFFLES, PANCAKES;

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         */
        @Override
        public String toString()
        {
            return name() + " with cherries!";
        }   //  toString()
    }
    //  enum Breakfast

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    @SuppressWarnings( {"javadoc", "static-method"} )
    @Test
    public void dynamicArrayOfEnumConstants()
    {
        final var composer = new JavaComposer();

        var builder = composer.annotationBuilder( HasDefaultsAnnotation.class );
        builder.addMember( "n", "$T.$L", Breakfast.class, Breakfast.PANCAKES.name() );
        final var annotation = builder.build();
        var actual = annotation.toString();
        var expected = "@org.tquadrat.javapoet.TestAnnotationSpec.HasDefaultsAnnotation(n = test.org.tquadrat.javacomposer.annotationspec.DynamicArrayOfEnumConstants.Breakfast.PANCAKES)";
        assertEquals( expected, actual );

        // builder = AnnotationSpec.builder(HasDefaultsAnnotation.class);
        builder.addMember( "n", "$T.$L", Breakfast.class, Breakfast.WAFFLES.name() );
        builder.addMember( "n", "$T.$L", Breakfast.class, Breakfast.PANCAKES.name() );
        actual = builder.build().toString();
        expected = "@org.tquadrat.javapoet.TestAnnotationSpec.HasDefaultsAnnotation(n = {test.org.tquadrat.javacomposer.annotationspec.DynamicArrayOfEnumConstants.Breakfast.PANCAKES, test.org.tquadrat.javacomposer.annotationspec.DynamicArrayOfEnumConstants.Breakfast.WAFFLES, test.org.tquadrat.javacomposer.annotationspec.DynamicArrayOfEnumConstants.Breakfast.PANCAKES})";
        assertEquals( expected, actual );

        builder = builder.build().toBuilder(); // idempotent
        actual = builder.build().toString();
        expected = "@org.tquadrat.javapoet.TestAnnotationSpec.HasDefaultsAnnotation(n = {test.org.tquadrat.javacomposer.annotationspec.DynamicArrayOfEnumConstants.Breakfast.PANCAKES, test.org.tquadrat.javacomposer.annotationspec.DynamicArrayOfEnumConstants.Breakfast.WAFFLES, test.org.tquadrat.javacomposer.annotationspec.DynamicArrayOfEnumConstants.Breakfast.PANCAKES})";
        assertEquals( expected, actual );

        builder.addMember( "n", "$T.$L", Breakfast.class, Breakfast.WAFFLES.name() );
        actual = builder.build().toString();
        expected = "@org.tquadrat.javapoet.TestAnnotationSpec.HasDefaultsAnnotation(n = {test.org.tquadrat.javacomposer.annotationspec.DynamicArrayOfEnumConstants.Breakfast.PANCAKES, test.org.tquadrat.javacomposer.annotationspec.DynamicArrayOfEnumConstants.Breakfast.WAFFLES, test.org.tquadrat.javacomposer.annotationspec.DynamicArrayOfEnumConstants.Breakfast.PANCAKES, test.org.tquadrat.javacomposer.annotationspec.DynamicArrayOfEnumConstants.Breakfast.WAFFLES})";
        assertEquals( expected, actual );
    }
}
//  DynamicArrayOfEnumConstants

/*
 *  End of File
 */