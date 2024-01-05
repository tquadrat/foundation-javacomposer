/*
 * ============================================================================
 *  Copyright © 2002-2024 by Thomas Thrien.
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

package org.tquadrat.javapoet;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.JavaComposer;

/**
 *  The tests for the class
 *  {@link CodeBlock}
 *  that came with the original library.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestCodeBlock.java 1085 2024-01-05 16:23:28Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestCodeBlock.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@SuppressWarnings( {"ClassWithTooManyMethods"} )
@DisplayName( "org.tquadrat.javapoet.TestCodeBlock" )
public final class TestCodeBlock
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    @SuppressWarnings( "static-method" )
    @Test
    public void danglingNamed()
    {
        final var composer = new JavaComposer();

        final Map<String,Object> map = new LinkedHashMap<>();
        map.put( "clazz", Integer.class );
        try
        {
            composer.codeBlockBuilder()
                .addNamed( "$clazz:T$", map )
                .build();
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "dangling $ at end" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void deindentCannotBeIndexed()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.codeBlockBuilder()
                .add( "$1<", "taco" )
                .build();
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException exp )
        {
            assertThat( exp ).hasMessageThat().isEqualTo( "$$, $>, $<, $[, $], $W, and $Z may not have an index" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void dollarSignEscapeCannotBeIndexed()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.codeBlockBuilder()
                .add( "$1$", "taco" )
                .build();
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException exp )
        {
            assertThat( exp ).hasMessageThat().isEqualTo( "$$, $>, $<, $[, $], $W, and $Z may not have an index" );
        }
    }

    @SuppressWarnings( {"static-method", "NonBooleanMethodNameMayNotStartWithQuestion"} )
    @Test
    public void equalsAndHashCode()
    {
        final var composer = new JavaComposer();

        var a = composer.codeBlockBuilder().build();
        var b = composer.codeBlockBuilder().build();
        assertThat( a.equals( b ) ).isTrue();
        assertThat( a.hashCode() ).isEqualTo( b.hashCode() );
        a = composer.codeBlockBuilder()
            .add( "$L", "taco" )
            .build();
        b = composer.codeBlockBuilder()
            .add( "$L", "taco" )
            .build();
        assertThat( a.equals( b ) ).isTrue();
        assertThat( a.hashCode() ).isEqualTo( b.hashCode() );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void formatIndicatorAlone()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.codeBlockBuilder().add( "$", String.class ).build();
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "dangling format characters in '$'" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void formatIndicatorWithoutIndexOrFormatType()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.codeBlockBuilder().add( "$ tacoString", String.class ).build();
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "invalid format string: '$ tacoString'" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void indentCannotBeIndexed()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.codeBlockBuilder().add( "$1>", "taco" ).build();
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException exp )
        {
            assertThat( exp ).hasMessageThat().isEqualTo( "$$, $>, $<, $[, $], $W, and $Z may not have an index" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void indexButNoArguments()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.codeBlockBuilder().add( "$1T" ).build();
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "index 1 for '$1T' not in range (received 0 arguments)" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void indexIsNegative()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.codeBlockBuilder().add( "$-1T", String.class ).build();
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "invalid format string: '$-1T'" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void indexIsZero()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.codeBlockBuilder().add( "$0T", String.class ).build();
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "index 0 for '$0T' not in range (received 1 arguments)" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void indexTooHigh()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.codeBlockBuilder().add( "$2T", String.class ).build();
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "index 2 for '$2T' not in range (received 1 arguments)" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void indexWithoutFormatType()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.codeBlockBuilder().add( "$1", String.class ).build();
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "dangling format characters in '$1'" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void indexWithoutFormatTypeNotAtStringEnd()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.codeBlockBuilder().add( "$1 taco", String.class ).build();
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "invalid format string: '$1 taco'" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void isEmpty()
    {
        final var composer = new JavaComposer();

        assertTrue( composer.codeBlockBuilder().isEmpty() );
        assertTrue( composer.codeBlockBuilder().add( "" ).isEmpty() );
        assertFalse( composer.codeBlockBuilder().add( " " ).isEmpty() );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void join()
    {
        final var composer = new JavaComposer();

        final var codeBlock = composer.codeBlockOf( "$S", "hello" );
        final var codeBlocks =
            new CodeBlock[] {
                composer.codeBlockOf( "$T", ClassName.from( "world", "World" ) ),
                composer.codeBlockOf( "need tacos" )
            };
        final var joined = codeBlock.join( " || ", codeBlocks );
        assertThat( joined.toString() ).isEqualTo(
            """
             "hello" || world.World || need tacos\
             """ );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void joiningSingle()
    {
        final var composer = new JavaComposer();

        final var codeBlock = composer.codeBlockOf( "$S", "hello" );
        final var joined = codeBlock.join( " || " );
        assertThat( joined.toString() ).isEqualTo( "\"hello\"" );
    }   //  joiningSingle()

    @SuppressWarnings( "static-method" )
    @Test
    public void joiningWithPrefixAndSuffix()
    {
        final var composer = new JavaComposer();

        final var codeBlock = composer.codeBlockOf( "$S", "hello" );
        final var codeBlocks =
            new CodeBlock[] {
                composer.codeBlockOf( "$T", ClassName.from( "world", "World" ) ),
                composer.codeBlockOf( "need tacos" )
            };
        final var joined = codeBlock.join( " || ", "start {", "} end", codeBlocks );
        assertThat( joined.toString() ).isEqualTo(
            """
            start {"hello" || world.World || need tacos} end""" );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void literalFormatCanBeIndexed()
    {
        final var composer = new JavaComposer();

        final var block = composer.codeBlockBuilder().add( "$1L", "taco" ).build();
        assertThat( block.toString() ).isEqualTo( "taco" );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void lowerCaseNamed()
    {
        final var composer = new JavaComposer();

        try
        {
            final Map<String,Object> map = new LinkedHashMap<>();
            map.put( "Text", "tacos" );
            @SuppressWarnings( "unused" )
            final var block = composer.codeBlockBuilder().addNamed( "$Text:S", map ).build();
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "argument 'Text' must start with a lowercase character" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void missingNamedArgument()
    {
        final var composer = new JavaComposer();

        try
        {
            final Map<String,Object> map = new LinkedHashMap<>();
            composer.codeBlockBuilder().addNamed( "$text:S", map ).build();
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "Missing named argument for $text" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void multipleNamedArguments()
    {
        final var composer = new JavaComposer();

        final Map<String,Object> map = new LinkedHashMap<>();
        map.put( "pipe", System.class );
        map.put( "text", "tacos" );

        final var block = composer.codeBlockBuilder().addNamed( "$pipe:T.out.println(\"Let's eat some $text:L\");", map ).build();

        assertThat( block.toString() ).isEqualTo( "java.lang.System.out.println(\"Let's eat some tacos\");" );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void namedAndNoArgFormat()
    {
        final var composer = new JavaComposer();

        final Map<String,Object> map = new LinkedHashMap<>();
        map.put( "text", "tacos" );
        final var block = composer.codeBlockBuilder()
            .addNamed( "$>\n$text:L for $$3.50", map )
            .build();

        final var actual = block.toString();
        final var expected = "\n  tacos for $3.50";
        assertEquals( expected, actual );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void namedNewline()
    {
        final var composer = new JavaComposer();

        final Map<String,Object> map = new LinkedHashMap<>();
        map.put( "clazz", Integer.class );
        final var block = composer.codeBlockBuilder()
            .addNamed( "$clazz:T\n", map )
            .build();
        assertThat( block.toString() ).isEqualTo( "java.lang.Integer\n" );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void nameFormatCanBeIndexed()
    {
        final var composer = new JavaComposer();

        final var block = composer.codeBlockBuilder()
            .add( "$1N", "taco" )
            .build();
        assertThat( block.toString() ).isEqualTo( "taco" );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void of()
    {
        final var composer = new JavaComposer();

        final var a = composer.codeBlockOf( "$L taco", "delicious" );
        assertThat( a.toString() ).isEqualTo( "delicious taco" );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void repeatedNamedArgument()
    {
        final var composer = new JavaComposer();

        final Map<String,Object> map = new LinkedHashMap<>();
        map.put( "text", "tacos" );
        final var block = composer.codeBlockBuilder()
            .addNamed(
                """
                "I like " + $text:S + ". Do you like " + $text:S + "?"\
                """, map )
            .build();
        assertThat( block.toString() ).isEqualTo(
            """
            "I like " + "tacos" + ". Do you like " + "tacos" + "?"\
            """ );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void sameIndexCanBeUsedWithDifferentFormats()
    {
        final var composer = new JavaComposer();

        final var block = composer.codeBlockBuilder()
            .add( "$1T.out.println($1S)", ClassName.from( System.class ) )
            .build();
        assertThat( block.toString() ).isEqualTo( "java.lang.System.out.println(\"java.lang.System\")" );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void simpleNamedArgument()
    {
        final var composer = new JavaComposer();

        final Map<String,Object> map = new LinkedHashMap<>();
        map.put( "text", "taco" );
        final var block = composer.codeBlockBuilder()
            .addNamed( "$text:S", map )
            .build();
        assertThat( block.toString() ).isEqualTo( "\"taco\"" );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void statementBeginningCannotBeIndexed()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.codeBlockBuilder()
                .add( "$1[", "taco" )
                .build();
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException exp )
        {
            assertThat( exp ).hasMessageThat().isEqualTo( "$$, $>, $<, $[, $], $W, and $Z may not have an index" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void statementEndingCannotBeIndexed()
    {
        final var composer = new JavaComposer();

        try
        {
            composer.codeBlockBuilder()
                .add( "$1]", "taco" )
                .build();
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalArgumentException exp )
        {
            assertThat( exp ).hasMessageThat().isEqualTo( "$$, $>, $<, $[, $], $W, and $Z may not have an index" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void statementExitWithoutStatementEnter()
    {
        final var composer = new JavaComposer();

        final var codeBlock = composer.codeBlockBuilder()
            .add( "$]" )
            .build();
        try
        {
            // We can't report this error until rendering type because code
            // blocks might be composed.
            assertNotNull( codeBlock.toString() );
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalStateException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "statement exit $] has no matching statement enter $[" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void stringFormatCanBeIndexed()
    {
        final var composer = new JavaComposer();

        final var block = composer.codeBlockBuilder()
            .add( "$1S", "taco" )
            .build();
        assertThat( block.toString() ).isEqualTo( "\"taco\"" );
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void tooManyStatementEnters()
    {
        final var composer = new JavaComposer();

        final var codeBlock = composer.codeBlockBuilder()
            .add( "$[$[" )
            .build();
        try
        {
            // We can't report this error until rendering type because code
            // blocks might be composed.
            assertNotNull( codeBlock.toString() );
            fail( "Expected exception was not thrown" );
        }
        catch( final IllegalStateException expected )
        {
            assertThat( expected ).hasMessageThat().isEqualTo( "statement enter $[ followed by statement enter $[" );
        }
    }

    @SuppressWarnings( "static-method" )
    @Test
    public void typeFormatCanBeIndexed()
    {
        final var composer = new JavaComposer();

        final var block = composer.codeBlockBuilder()
            .add( "$1T", String.class )
            .build();
        assertThat( block.toString() ).isEqualTo( "java.lang.String" );
    }
}
//  class TestCodeBlock

/*
 *  End of File
 */