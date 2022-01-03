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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.Util;

/**
 *  The tests for the methods in
 *  {@link org.tquadrat.foundation.javacomposer.internal.Util}
 *  that came with the original library.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestUtil.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestUtil.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestUtil" )
public class TestUtil
{
    /**
     *  Tests for
     *  {@link org.tquadrat.foundation.javacomposer.internal.Util#characterLiteralWithoutSingleQuotes(char)}.
     */
    @SuppressWarnings( "static-method" )
    @Test
    final void characterLiteral()
    {
        assertEquals( "a", Util.characterLiteralWithoutSingleQuotes( 'a' ) );
        assertEquals( "b", Util.characterLiteralWithoutSingleQuotes( 'b' ) );
        assertEquals( "c", Util.characterLiteralWithoutSingleQuotes( 'c' ) );
        assertEquals( "%", Util.characterLiteralWithoutSingleQuotes( '%' ) );
        // common escapes
        assertEquals( "\\b", Util.characterLiteralWithoutSingleQuotes( '\b' ) );
        assertEquals( "\\t", Util.characterLiteralWithoutSingleQuotes( '\t' ) );
        assertEquals( "\\n", Util.characterLiteralWithoutSingleQuotes( '\n' ) );
        assertEquals( "\\f", Util.characterLiteralWithoutSingleQuotes( '\f' ) );
        assertEquals( "\\r", Util.characterLiteralWithoutSingleQuotes( '\r' ) );
        assertEquals( "\"", Util.characterLiteralWithoutSingleQuotes( '"' ) );
        assertEquals( "\\'", Util.characterLiteralWithoutSingleQuotes( '\'' ) );
        assertEquals( "\\\\", Util.characterLiteralWithoutSingleQuotes( '\\' ) );
        // octal escapes
        assertEquals( "\\u0000", Util.characterLiteralWithoutSingleQuotes( '\0' ) );
        assertEquals( "\\u0007", Util.characterLiteralWithoutSingleQuotes( '\7' ) );
        assertEquals( "?", Util.characterLiteralWithoutSingleQuotes( '\77' ) );
        assertEquals( "\\u007f", Util.characterLiteralWithoutSingleQuotes( '\177' ) );
        assertEquals( "¿", Util.characterLiteralWithoutSingleQuotes( '\277' ) );
        assertEquals( "ÿ", Util.characterLiteralWithoutSingleQuotes( '\377' ) );
        // unicode escapes
        assertEquals( "\\u0000", Util.characterLiteralWithoutSingleQuotes( '\u0000' ) );
        assertEquals( "\\u0001", Util.characterLiteralWithoutSingleQuotes( '\u0001' ) );
        assertEquals( "\\u0002", Util.characterLiteralWithoutSingleQuotes( '\u0002' ) );
        assertEquals( "€", Util.characterLiteralWithoutSingleQuotes( '\u20AC' ) );
        assertEquals( "☃", Util.characterLiteralWithoutSingleQuotes( '\u2603' ) );
        assertEquals( "♠", Util.characterLiteralWithoutSingleQuotes( '\u2660' ) );
        assertEquals( "♣", Util.characterLiteralWithoutSingleQuotes( '\u2663' ) );
        assertEquals( "♥", Util.characterLiteralWithoutSingleQuotes( '\u2665' ) );
        assertEquals( "♦", Util.characterLiteralWithoutSingleQuotes( '\u2666' ) );
        assertEquals( "✵", Util.characterLiteralWithoutSingleQuotes( '\u2735' ) );
        assertEquals( "✺", Util.characterLiteralWithoutSingleQuotes( '\u273A' ) );
        assertEquals( "／", Util.characterLiteralWithoutSingleQuotes( '\uFF0F' ) );
    }   //  characterLiteral()

    /**
     *  Tests for
     *  {@link org.tquadrat.foundation.javacomposer.internal.Util#stringLiteralWithDoubleQuotes(String, String)}.
     */
    @Test
    final void stringLiteral()
    {
        stringLiteral( "abc" );
        stringLiteral( "♦♥♠♣" );
        stringLiteral( "€\\t@\\t$", "€\t@\t$", " " );
        stringLiteral( "abc();\\n\"\n + \"def();", "abc();\ndef();", " " );
        stringLiteral( "This is \\\"quoted\\\"!", "This is \"quoted\"!", " " );
        stringLiteral( "e^{i\\\\pi}+1=0", "e^{i\\pi}+1=0", " " );
    }   //  stringLiteral()

    @SuppressWarnings( "javadoc" )
    private void stringLiteral( final String string ) { stringLiteral( string, string, " " ); }

    @SuppressWarnings( {"javadoc", "static-method", "SameParameterValue"} )
    private void stringLiteral( final String expected, final String value, final String indent )
    {
        assertEquals( "\"" + expected + "\"", Util.stringLiteralWithDoubleQuotes( value, indent ) );
    }   //  stringLiteral()
}
//  class TestUtil

/*
 *  End of File
 */