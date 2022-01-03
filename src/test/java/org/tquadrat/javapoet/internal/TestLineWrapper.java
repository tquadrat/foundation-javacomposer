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

package org.tquadrat.javapoet.internal;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.LineWrapper;

/**
 *  The tests for the class
 *  {@link LineWrapper}
 *  that came with the original library.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestLineWrapper.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestLineWrapper.java 937 2021-12-14 21:59:00Z tquadrat $" )
@SuppressWarnings( "javadoc" )
@RunWith( JUnit4.class )
@DisplayName( "TestLineWrapper" )
public final class TestLineWrapper
{
    @SuppressWarnings( "static-method" )
    @Test
    public void fencepost() throws Exception
    {
        final var out = new StringBuffer();
        @SuppressWarnings( "resource" )
        final var lineWrapper = new LineWrapper( out, "  ", 10 );
        lineWrapper.append( "abcde" );
        lineWrapper.append( "fghij" );
        lineWrapper.wrappingSpace( 2 );
        lineWrapper.append( "k" );
        lineWrapper.append( "lmnop" );
        lineWrapper.close();
        assertThat( out.toString() ).isEqualTo( "abcdefghij\n    klmnop" );
    }   //  fencepost()

    @SuppressWarnings( "static-method" )
    @Test
    public void fencepostZeroWidth() throws Exception
    {
        final var out = new StringBuffer();
        @SuppressWarnings( "resource" )
        final var lineWrapper = new LineWrapper( out, "  ", 10 );
        lineWrapper.append( "abcde" );
        lineWrapper.append( "fghij" );
        lineWrapper.zeroWidthSpace( 2 );
        lineWrapper.append( "k" );
        lineWrapper.append( "lmnop" );
        lineWrapper.close();
        assertThat( out.toString() ).isEqualTo( "abcdefghij\n    klmnop" );
    }   //  fencepostZeroWidth()

    @SuppressWarnings( "static-method" )
    @Test
    public void multipleWrite() throws Exception
    {
        final var out = new StringBuffer();
        @SuppressWarnings( "resource" )
        final var lineWrapper = new LineWrapper( out, "  ", 10 );
        lineWrapper.append( "ab" );
        lineWrapper.wrappingSpace( 1 );
        lineWrapper.append( "cd" );
        lineWrapper.wrappingSpace( 1 );
        lineWrapper.append( "ef" );
        lineWrapper.wrappingSpace( 1 );
        lineWrapper.append( "gh" );
        lineWrapper.wrappingSpace( 1 );
        lineWrapper.append( "ij" );
        lineWrapper.wrappingSpace( 1 );
        lineWrapper.append( "kl" );
        lineWrapper.wrappingSpace( 1 );
        lineWrapper.append( "mn" );
        lineWrapper.wrappingSpace( 1 );
        lineWrapper.append( "op" );
        lineWrapper.wrappingSpace( 1 );
        lineWrapper.append( "qr" );
        lineWrapper.close();
        assertThat( out.toString() ).isEqualTo( "ab cd ef\n  gh ij kl\n  mn op qr" );
    }   //  multipleWrite()

    @SuppressWarnings( "static-method" )
    @Test
    public void noSpaceWrapMax() throws Exception
    {
        final var out = new StringBuffer();
        @SuppressWarnings( "resource" )
        final var lineWrapper = new LineWrapper( out, "  ", 10 );
        lineWrapper.append( "abcde" );
        lineWrapper.zeroWidthSpace( 2 );
        lineWrapper.append( "fghijk" );
        lineWrapper.close();
        assertThat( out.toString() ).isEqualTo( "abcde\n    fghijk" );
    }   //  noSpaceWrapMax()

    @SuppressWarnings( "static-method" )
    @Test
    public void noWrap() throws Exception
    {
        final var out = new StringBuffer();
        @SuppressWarnings( "resource" )
        final var lineWrapper = new LineWrapper( out, "  ", 10 );
        lineWrapper.append( "abcde" );
        lineWrapper.wrappingSpace( 2 );
        lineWrapper.append( "fghi" );
        lineWrapper.close();
        assertThat( out.toString() ).isEqualTo( "abcde fghi" );
    }   //  noWrap()

    @SuppressWarnings( "static-method" )
    @Test
    public void noWrapEmbeddedNewlines() throws Exception
    {
        final var out = new StringBuffer();
        @SuppressWarnings( "resource" )
        final var lineWrapper = new LineWrapper( out, "  ", 10 );
        lineWrapper.append( "abcde" );
        lineWrapper.wrappingSpace( 2 );
        lineWrapper.append( "fghi\njklmn" );
        lineWrapper.append( "opqrstuvwxy" );
        lineWrapper.close();
        assertThat( out.toString() ).isEqualTo( "abcde fghi\njklmnopqrstuvwxy" );
    }   //  oWrapEmbeddedNewlines()

    @SuppressWarnings( "static-method" )
    @Test
    public void noWrapEmbeddedNewlines_ZeroWidth() throws Exception
    {
        final var out = new StringBuffer();
        @SuppressWarnings( "resource" )
        final var lineWrapper = new LineWrapper( out, "  ", 10 );
        lineWrapper.append( "abcde" );
        lineWrapper.zeroWidthSpace( 2 );
        lineWrapper.append( "fghij\nklmn" );
        lineWrapper.append( "opqrstuvwxyz" );
        lineWrapper.close();
        assertThat( out.toString() ).isEqualTo( "abcdefghij\nklmnopqrstuvwxyz" );
    }   //  noWrapEmbeddedNewlines_ZeroWidth()

    @SuppressWarnings( "static-method" )
    @Test
    public void noWrapMultipleNewlines() throws Exception
    {
        final var out = new StringBuffer();
        @SuppressWarnings( "resource" )
        final var lineWrapper = new LineWrapper( out, "  ", 10 );
        lineWrapper.append( "abcde" );
        lineWrapper.wrappingSpace( 2 );
        lineWrapper.append( "fghi\nklmnopq\nr" );
        lineWrapper.wrappingSpace( 2 );
        lineWrapper.append( "stuvwxyz" );
        lineWrapper.close();
        assertThat( out.toString() ).isEqualTo( "abcde fghi\nklmnopq\nr stuvwxyz" );
    }   //  oWrapMultipleNewlines()

    @SuppressWarnings( "static-method" )
    @Test
    public void overlyLongLinesWithLeadingSpace() throws Exception
    {
        final var out = new StringBuffer();
        @SuppressWarnings( "resource" )
        final var lineWrapper = new LineWrapper( out, "  ", 10 );
        lineWrapper.wrappingSpace( 2 );
        lineWrapper.append( "abcdefghijkl" );
        lineWrapper.close();
        assertThat( out.toString() ).isEqualTo( "\n    abcdefghijkl" );
    }   //  overlyLongLinesWithLeadingSpace()

    @SuppressWarnings( "static-method" )
    @Test
    public void overlyLongLinesWithLeadingZeroWidth() throws Exception
    {
        final var out = new StringBuffer();
        @SuppressWarnings( "resource" )
        final var lineWrapper = new LineWrapper( out, "  ", 10 );
        lineWrapper.zeroWidthSpace( 2 );
        lineWrapper.append( "abcdefghijkl" );
        lineWrapper.close();
        assertThat( out.toString() ).isEqualTo( "abcdefghijkl" );
    }   //  overlyLongLinesWithLeadingZeroWidth()

    @SuppressWarnings( "static-method" )
    @Test
    public void overlyLongLinesWithoutLeadingSpace() throws Exception
    {
        final var out = new StringBuffer();
        @SuppressWarnings( "resource" )
        final var lineWrapper = new LineWrapper( out, "  ", 10 );
        lineWrapper.append( "abcdefghijkl" );
        lineWrapper.close();
        assertThat( out.toString() ).isEqualTo( "abcdefghijkl" );
    }   //  overlyLongLinesWithoutLeadingSpace()

    @SuppressWarnings( "static-method" )
    @Test
    public void wrap() throws Exception
    {
        final var out = new StringBuffer();
        @SuppressWarnings( "resource" )
        final var lineWrapper = new LineWrapper( out, "  ", 10 );
        lineWrapper.append( "abcde" );
        lineWrapper.wrappingSpace( 2 );
        lineWrapper.append( "fghij" );
        lineWrapper.close();
        assertThat( out.toString() ).isEqualTo( "abcde\n    fghij" );
    }   //  wrap()

    @SuppressWarnings( "static-method" )
    @Test
    public void wrapEmbeddedNewlines() throws Exception
    {
        final var out = new StringBuffer();
        @SuppressWarnings( "resource" )
        final var lineWrapper = new LineWrapper( out, "  ", 10 );
        lineWrapper.append( "abcde" );
        lineWrapper.wrappingSpace( 2 );
        lineWrapper.append( "fghij\nklmn" );
        lineWrapper.append( "opqrstuvwxy" );
        lineWrapper.close();
        assertThat( out.toString() ).isEqualTo( "abcde\n    fghij\nklmnopqrstuvwxy" );
    }   //  wrapEmbeddedNewlines()

    @SuppressWarnings( "static-method" )
    @Test
    public void wrapEmbeddedNewlines_ZeroWidth() throws Exception
    {
        final var out = new StringBuffer();
        @SuppressWarnings( "resource" )
        final var lineWrapper = new LineWrapper( out, "  ", 10 );
        lineWrapper.append( "abcde" );
        lineWrapper.zeroWidthSpace( 2 );
        lineWrapper.append( "fghijk\nlmn" );
        lineWrapper.append( "opqrstuvwxy" );
        lineWrapper.close();
        assertThat( out.toString() ).isEqualTo( "abcde\n    fghijk\nlmnopqrstuvwxy" );
    }   //  wrapEmbeddedNewlines_ZeroWidth()

    @SuppressWarnings( "static-method" )
    @Test
    public void wrapMultipleNewlines() throws Exception
    {
        final var out = new StringBuffer();
        @SuppressWarnings( "resource" )
        final var lineWrapper = new LineWrapper( out, "  ", 10 );
        lineWrapper.append( "abcde" );
        lineWrapper.wrappingSpace( 2 );
        lineWrapper.append( "fghi\nklmnopq\nrs" );
        lineWrapper.wrappingSpace( 2 );
        lineWrapper.append( "tuvwxyz1" );
        lineWrapper.close();
        assertThat( out.toString() ).isEqualTo( "abcde fghi\nklmnopq\nrs\n    tuvwxyz1" );
    }   //  wrapMultipleNewlines()

    @SuppressWarnings( "static-method" )
    @Test
    public void zeroWidthNoWrap() throws Exception
    {
        final var out = new StringBuffer();
        @SuppressWarnings( "resource" )
        final var lineWrapper = new LineWrapper( out, "  ", 10 );
        lineWrapper.append( "abcde" );
        lineWrapper.zeroWidthSpace( 2 );
        lineWrapper.append( "fghij" );
        lineWrapper.close();
        assertThat( out.toString() ).isEqualTo( "abcdefghij" );
    }   //  zeroWidthNoWrap()
}
//  class TestLineWrapper

/*
 *  End of File
 */