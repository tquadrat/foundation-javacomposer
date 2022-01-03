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

package test.org.tquadrat.javacomposer.typespec;

import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;

import java.util.stream.Stream;

import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.UtilityClass;
import org.tquadrat.foundation.exception.PrivateConstructorForStaticClassCalledError;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl;
import org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl.BuilderImpl;

/**
 *  A provider for type builders.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TypeBuilderProvider.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TypeBuilderProvider.java 937 2021-12-14 21:59:00Z tquadrat $" )
@UtilityClass
public final class TypeBuilderProvider
{
        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  No instance allowed for this class.
     */
    private TypeBuilderProvider() { throw new PrivateConstructorForStaticClassCalledError( TypeBuilderProvider.class ); }

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/

    /**
     *  Provides some type builders.
     *
     *  @return The type builders.
     */
    @SuppressWarnings( "CastToConcreteClass" )
    public static final Stream<BuilderImpl> provideTypeBuilders()
    {
        final var composer = new JavaComposer();
        final Stream.Builder<BuilderImpl> streamBuilder = Stream.builder();

        BuilderImpl builder;

        //---* The builder for a regular class *-------------------------------
        builder = (TypeSpecImpl.BuilderImpl) composer.classBuilder( "RegularClass" );
        streamBuilder.add( builder );

        //---* The builder for an anonymous class *----------------------------
        builder = (TypeSpecImpl.BuilderImpl) composer.anonymousClassBuilder( EMPTY_STRING );
        streamBuilder.add( builder );

        //---* The builder for an interface *----------------------------------
        builder = (TypeSpecImpl.BuilderImpl) composer.interfaceBuilder( "AnInterface" );
        streamBuilder.add( builder );

        //---* The builder for an enum *---------------------------------------
        builder = (TypeSpecImpl.BuilderImpl) composer.enumBuilder( "AnEnum" )
            .addEnumConstant( "ENUM_CONSTANT" );
        streamBuilder.add( builder );

        //---* The builder for an annotation *---------------------------------
        builder = (TypeSpecImpl.BuilderImpl) composer.annotationTypeBuilder( "AnAnnotation" );
        streamBuilder.add( builder );

        final var retValue = streamBuilder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  provideTypeBuilders()
}
//  class TypeBuilderProvider

/*
 *  End of File
 */