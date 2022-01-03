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

package test.org.tquadrat.javacomposer.internal.recordspecimpl;

import java.io.Serializable;

import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  A record that is used to check what is valid.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *
 *  @param  field1  Field 1.
 *  @param  field2  Field 2.
 */
@ClassVersion( sourceVersion = "$Id: ARecord.java 937 2021-12-14 21:59:00Z tquadrat $" )
public record ARecord( String field1, String field2 ) implements Serializable
{
        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  An additional constructor.
     */
    public ARecord() { this( "DEFAULT" ); }

    /**
     *  An additional constructor.
     *
     *  @param  field1  Field 1.
     */
    public ARecord( final String field1 ) { this( field1, "DEFAULT" ); }

         /*---------*\
    ====** Methods **==========================================================
         \*---------*/
    /**
     * The program entry point.
     *
     * @param args The command line arguments.
     */
    public static final void main( final String... args )
    {
        try
        {
            final var r = new ARecord( "eins", "zwei" );
        }
        catch( final Throwable t )
        {
            //---* Handle previously unhandled exceptions *--------------------
            t.printStackTrace( System.err );
        }
    }  //  main()
}
//  record ARecord

/*
 *  End of File
 */