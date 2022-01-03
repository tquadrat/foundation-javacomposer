/*
 * ============================================================================
 * Copyright © 2002-2021 by Thomas Thrien.
 * All Rights Reserved.
 * ============================================================================
 *
 * Licensed to the public under the agreements of the GNU Lesser General Public
 * License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.tquadrat.foundation.javacomposer;

import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.javacomposer.TypeName.from;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.UtilityClass;
import org.tquadrat.foundation.exception.PrivateConstructorForStaticClassCalledError;

/**
 *  The constants for the primitives.
 *
 *  @see TypeName
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: Primitives.java 943 2021-12-21 01:34:32Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@UtilityClass
@ClassVersion( sourceVersion = "$Id: Primitives.java 943 2021-12-21 01:34:32Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public final class Primitives
{
        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/
    /**
     *  The type name for {@code boolean}.
     */
    @API( status = STABLE, since = "0.0.5" )
    public static final TypeName BOOLEAN = from( boolean.class );

    /**
     *  The type name for {@code byte}.
     */
    @API( status = STABLE, since = "0.0.5" )
    public static final TypeName BYTE = from( byte.class );

    /**
     *  The type name for {@code char}.
     */
    @API( status = STABLE, since = "0.0.5" )
    public static final TypeName CHAR = from( char.class );

    /**
     *  The type name for {@code double}.
     */
    @API( status = STABLE, since = "0.0.5" )
    public static final TypeName DOUBLE = from( double.class );

    /**
     *  The type name for {@code float}.
     */
    @API( status = STABLE, since = "0.0.5" )
    public static final TypeName FLOAT = from( float.class );

    /**
     *  The type name for {@code int}.
     */
    @API( status = STABLE, since = "0.0.5" )
    public static final TypeName INT = from( int.class );

    /**
     *  The type name for {@code long}.
     */
    @API( status = STABLE, since = "0.0.5" )
    public static final TypeName LONG = from( long.class );

    /**
     *  The type name for {@code short}.
     */
    @API( status = STABLE, since = "0.0.5" )
    public static final TypeName SHORT = from( short.class );

    /**
     *  The type name for {@code void}.
     */
    @API( status = STABLE, since = "0.0.5" )
    public static final TypeName VOID = from( void.class );

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  No instance allowed for this class.
     */
    private Primitives() { throw new PrivateConstructorForStaticClassCalledError( Primitives.class ); }
}
//  class Primitives

/*
 *  End of File
 */