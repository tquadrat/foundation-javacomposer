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

import static java.lang.String.format;
import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import javax.lang.model.element.Modifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.UnsupportedEnumError;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.Layout;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.javacomposer.TypeVariableName;
import org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  This class provides the tests for
 *  {@link org.tquadrat.foundation.javacomposer.internal.RecordSpecImpl}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestBuildRecord.java 1076 2023-10-03 18:36:07Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestBuildRecord.java 1076 2023-10-03 18:36:07Z tquadrat $" )
@DisplayName( "test.org.tquadrat.javacomposer.internal.recordspecimpl.TestBuildRecord" )
public class TestBuildRecord extends TestBaseClass
{
        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The name of the package that is used for the new records: {@value}.
     */
    public static final String PACKAGE_NAME = "org.tquadrat.foundation";

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  The most simplistic record.
     *
     *  @param  layout  The layout to use.
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @ParameterizedTest
    @EnumSource( Layout.class )
    final void testGenericRecord( final Layout layout ) throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer( layout );

        final var typeVariable = TypeVariableName.from( "T" );

        final var field = composer.fieldBuilder( typeVariable, "field" )
            .addModifiers( Modifier.PRIVATE )
            .build();

        final var candidate = composer.recordBuilder( "Record" )
            .addTypeVariable( typeVariable )
            .addField( field )
            .build();

        final var expected = switch( layout )
            {
                case LAYOUT_DEFAULT ->
                    """
                    package org.tquadrat.foundation;
                                    
                    record Record<T>(T field) {}
                    """;
                case LAYOUT_FOUNDATION ->
                    """
                    package org.tquadrat.foundation;
                                    
                    import java.lang.SuppressWarnings;
                                    
                    @SuppressWarnings( "javadoc" )
                    record Record<T>( T field ) { /* Empty */ }
                    //  record Record
                    
                    /*
                     * End of File
                     */""";
                case LAYOUT_JAVAPOET ->
                    """
                    package org.tquadrat.foundation;
                                    
                    record Record<T>(T field) {}
                    """;
                case LAYOUT_JAVAPOET_WITH_TAB ->
                    """
                    package org.tquadrat.foundation;
                                    
                    record Record<T>(T field) {}
                    """;
                default -> throw new UnsupportedEnumError( layout );
            };

        final var actual = toString( candidate );

        assertEquals( expected, actual );
    }   //  testGenericRecord()

    /**
     *  A record must have at least on field.
     *
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @Test
    final void testMinimumFields() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();
        final var builder = composer.recordBuilder( "Record" );

        final Class<? extends Throwable> expectedException = IllegalStateException.class;
        try
        {
            builder.build();
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException ) t.printStackTrace( out );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        builder.addField( int.class, "staticField", Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC );
        try
        {
            builder.build();
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException ) t.printStackTrace( out );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }   //  testMinimumFields()

    /**
     *  The most simplistic record.
     *
     *  @param  layout  The layout to use.
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @ParameterizedTest
    @EnumSource( Layout.class )
    final void testMostSimplisticRecord( final Layout layout ) throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer( layout );

        final var field = composer.fieldBuilder( int.class, "field" )
            .addModifiers( Modifier.PRIVATE )
            .build();

        final var candidate = composer.recordBuilder( "Record" )
            .addField( field )
            .build();

        final var expected = switch( layout )
        {
            case LAYOUT_DEFAULT ->
                """
                package org.tquadrat.foundation;
                                
                record Record(int field) {}
                """;
            case LAYOUT_FOUNDATION ->
                """
                package org.tquadrat.foundation;
                                
                import java.lang.SuppressWarnings;
                                
                @SuppressWarnings( "javadoc" )
                record Record( int field ) { /* Empty */ }
                //  record Record
                
                /*
                 * End of File
                 */""";
            case LAYOUT_JAVAPOET ->
                """
                package org.tquadrat.foundation;
                                
                record Record(int field) {}
                """;
            case LAYOUT_JAVAPOET_WITH_TAB ->
                """
                package org.tquadrat.foundation;
                                
                record Record(int field) {}
                """;
            default -> throw new UnsupportedEnumError( layout );
        };

        final var actual = toString( candidate );

        assertEquals( expected, actual );
    }   //  testMostSimplisticRecord()

    /**
     *  The most simplistic record with Javadoc comments.
     *
     *  @param  layout  The layout to use.
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @ParameterizedTest
    @EnumSource( Layout.class )
    final void testMostSimplisticRecordWithJavadoc( final Layout layout ) throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer( layout );

        final var field = composer.fieldBuilder( int.class, "field" )
            .addJavadoc( "A field.\n" )
            .addModifiers( Modifier.PRIVATE )
            .build();

        final var candidate = composer.recordBuilder( "Record" )
            .addJavadoc( "A Record.\n" )
            .addField( field )
            .build();

        final var expected = switch( layout )
            {
                case LAYOUT_DEFAULT ->
                    """
                    package org.tquadrat.foundation;
                                    
                    /**
                     * A Record.
                     *
                     * @param field A field.  
                     */
                    record Record(int field) {}
                    """;
                case LAYOUT_FOUNDATION ->
                    """
                    package org.tquadrat.foundation;
                                    
                    /**
                     * A Record.
                     *
                     * @param field A field.  
                     */
                    record Record( int field ) { /* Empty */ }
                    //  record Record
                    
                    /*
                     * End of File
                     */""";
                case LAYOUT_JAVAPOET ->
                    """
                    package org.tquadrat.foundation;
                                    
                    /**
                     * A Record.
                     *
                     * @param field A field.  
                     */
                    record Record(int field) {}
                    """;
                case LAYOUT_JAVAPOET_WITH_TAB ->
                    """
                    package org.tquadrat.foundation;
                                    
                    /**
                     * A Record.
                     *
                     * @param field A field.  
                     */
                    record Record(int field) {}
                    """;
                default -> throw new UnsupportedEnumError( layout );
            };

        final var actual = toString( candidate );

        assertEquals( expected, actual );
    }   //  testMostSimplisticRecordWithJavadoc()

    /**
     *  The most simplistic record with Javadoc comments.
     *
     *  @param  layout  The layout to use.
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @ParameterizedTest
    @EnumSource( Layout.class )
    final void testMostSimplisticRecordWithJavadocOnlyForTheField( final Layout layout ) throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer( layout );

        final var field = composer.fieldBuilder( int.class, "field" )
            .addJavadoc( "A field.\n" )
            .addModifiers( Modifier.PRIVATE )
            .build();

        final var candidate = composer.recordBuilder( "Record" )
            .addField( field )
            .build();

        final var expected = switch( layout )
            {
                case LAYOUT_DEFAULT ->
                    """
                    package org.tquadrat.foundation;
                                    
                    /**
                     *
                     * @param field A field.
                     */
                    record Record(int field) {}
                    """;
                case LAYOUT_FOUNDATION ->
                    """
                    package org.tquadrat.foundation;
                                    
                    import java.lang.SuppressWarnings;
                                    
                    /**
                     *
                     * @param field A field.
                     */
                    @SuppressWarnings( "javadoc" )
                    record Record( int field ) { /* Empty */ }
                    //  record Record
                    
                    /*
                     * End of File
                     */""";
                case LAYOUT_JAVAPOET ->
                    """
                    package org.tquadrat.foundation;
                                    
                    /**
                     *
                     * @param field A field.
                     */
                    record Record(int field) {}
                    """;
                case LAYOUT_JAVAPOET_WITH_TAB ->
                    """
                    package org.tquadrat.foundation;
                                    
                    /**
                     *
                     * @param field A field.
                     */
                    record Record(int field) {}
                    """;
                default -> throw new UnsupportedEnumError( layout );
            };

        final var actual = toString( candidate );

        assertEquals( expected, actual );
    }   //  testMostSimplisticRecordWithJavadocOnlyForTheField()

    /**
     *  The most simplistic record with Javadoc comments.
     *
     *  @param  layout  The layout to use.
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @ParameterizedTest
    @EnumSource( Layout.class )
    final void testMostSimplisticRecordWithJavadocOnlyForTheRecord( final Layout layout ) throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer( layout );

        final var field = composer.fieldBuilder( int.class, "field" )
            .addModifiers( Modifier.PRIVATE )
            .build();

        final var candidate = composer.recordBuilder( "Record" )
            .addJavadoc( "A Record.\n" )
            .addField( field )
            .build();

        final var expected = switch( layout )
            {
                case LAYOUT_DEFAULT ->
                    """
                    package org.tquadrat.foundation;
                                    
                    /**
                     * A Record.
                     */
                    record Record(int field) {}
                    """;
                case LAYOUT_FOUNDATION ->
                    """
                    package org.tquadrat.foundation;
                                    
                    import java.lang.SuppressWarnings;
                                    
                    /**
                     * A Record.
                     */
                    @SuppressWarnings( "javadoc" )
                    record Record( int field ) { /* Empty */ }
                    //  record Record
                    
                    /*
                     * End of File
                     */""";
                case LAYOUT_JAVAPOET ->
                    """
                    package org.tquadrat.foundation;
                                    
                    /**
                     * A Record.
                     */
                    record Record(int field) {}
                    """;
                case LAYOUT_JAVAPOET_WITH_TAB ->
                    """
                    package org.tquadrat.foundation;
                                    
                    /**
                     * A Record.
                     */
                    record Record(int field) {}
                    """;
                default -> throw new UnsupportedEnumError( layout );
            };

        final var actual = toString( candidate );

        assertEquals( expected, actual );
    }   //  testMostSimplisticRecordWithJavadocOnlyForTheRecord()

    /**
     *  A record with two fields.
     *
     *  @param  layout  The layout to use.
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @ParameterizedTest
    @EnumSource( Layout.class )
    final void testRecordWithTwoFields( final Layout layout ) throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer( layout );

        final var field1 = composer.fieldBuilder( int.class, "field1" )
            .addModifiers( Modifier.PRIVATE )
            .build();
        final var field2 = composer.fieldBuilder( String.class, "field2" )
            .addModifiers( Modifier.PRIVATE )
            .build();

        final var candidate = composer.recordBuilder( "Record" )
            .addField( field1 )
            .addField( field2 )
            .build();

        final var expected = switch( layout )
            {
                case LAYOUT_DEFAULT ->
                    """
                    package org.tquadrat.foundation;
                                    
                    import java.lang.String;
                    
                    record Record(int field1, String field2) {}
                    """;
                case LAYOUT_FOUNDATION ->
                    """
                    package org.tquadrat.foundation;
                                    
                    import java.lang.String;
                    import java.lang.SuppressWarnings;
                                    
                    @SuppressWarnings( "javadoc" )
                    record Record( int field1, String field2 ) { /* Empty */ }
                    //  record Record
                    
                    /*
                     * End of File
                     */""";
                case LAYOUT_JAVAPOET ->
                    """
                    package org.tquadrat.foundation;
                                    
                    import java.lang.String;
                    
                    record Record(int field1, String field2) {}
                    """;
                case LAYOUT_JAVAPOET_WITH_TAB ->
                    """
                    package org.tquadrat.foundation;
                                    
                    import java.lang.String;
                    
                    record Record(int field1, String field2) {}
                    """;
                default -> throw new UnsupportedEnumError( layout );
            };

        final var actual = toString( candidate );

        assertEquals( expected, actual );
    }   //  testRecordWithTwoFields()

    /**
     *  Returns the built record as a String.
     *
     *  @param  typeSpec    The record.
     *  @return The generated source code.
     */
    @SuppressWarnings( "CastToConcreteClass" )
    final String toString( final TypeSpec typeSpec )
    {
        final var typeSpecImpl = (TypeSpecImpl) typeSpec;
        final var composer = typeSpecImpl.getFactory();
        final var retValue = composer.javaFileBuilder( PACKAGE_NAME, typeSpec )
            .build()
            .toString();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toString()
}
//  class TestBuildRecord

/*
 *  End of File
 */