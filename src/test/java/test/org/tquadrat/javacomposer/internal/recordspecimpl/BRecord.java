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

import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  A record that is used to check what is valid.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *
 *  @param  <T>  The type for the field.
 *  @param  field   A field.
 */
@ClassVersion( sourceVersion = "$Id: BRecord.java 937 2021-12-14 21:59:00Z tquadrat $" )
public record BRecord<T>( T field ) { /* Empty */ }
//  record BRecord

/*
 *  End of File
 */