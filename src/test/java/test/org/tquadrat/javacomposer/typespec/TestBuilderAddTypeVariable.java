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

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.util.StringUtils.format;

import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.EmptyArgumentException;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.javacomposer.TypeVariableName;
import org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Some tests for the methods
 *  {@link org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl.BuilderImpl#addTypeVariable(TypeVariableName)}
 *  and
 *  {@link org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl.BuilderImpl#addTypeVariable(TypeVariableName, CharSequence)}
 *  from the class
 *  {@link org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl.BuilderImpl}
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestBuilderAddTypeVariable.java 943 2021-12-21 01:34:32Z tquadrat $" )
public class TestBuilderAddTypeVariable extends TestBaseClass
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
     *  Some tests for the methods
     *  {@link org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl.BuilderImpl#addTypeVariable(TypeVariableName)}
     *  and
     *  {@link org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl.BuilderImpl#addTypeVariable(TypeVariableName, CharSequence)}.
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @Test
    final void testAddTypeVariable() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var typeVariable = TypeVariableName.from( "T" );
        final var javadoc = "Comment";

        TypeSpec.Builder candidate;
        String actual, expected;

        candidate = composer.classBuilder( "CandidateClass" )
            .addTypeVariable( typeVariable );
        expected =
            """
            package org.tquadrat.foundation;
            
            class CandidateClass<T> {
            }
            """;
        actual = toString( candidate.build() );
        assertEquals( expected, actual );

        candidate = composer.classBuilder( "CandidateClass" )
            .addTypeVariable( typeVariable, javadoc );
        expected =
            """
            package org.tquadrat.foundation;
            
            /**
             *
             * @param <T> "Comment"
             */
            class CandidateClass<T> {
            }
            """;
        actual = toString( candidate.build() );
        assertEquals( expected, actual );
    }   //  testAddTypeVariable()

    /**
     *  Some tests for the method
     *  {@link org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl.BuilderImpl#addTypeVariable(TypeVariableName, CharSequence)}.
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @Test
    final void testAddTypeVariableWithEmptyArgument() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var typeVariable = TypeVariableName.from( "T" );

        final var candidate = composer.classBuilder( "CandidateClass" );

        final Class<? extends Throwable> expectedException = EmptyArgumentException.class;
        try
        {
            candidate.addTypeVariable( typeVariable, EMPTY_STRING );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }   //  testAddTypeVariableWithEmptyArgument()

    /**
     *  Some tests for the methods
     *  {@link org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl.BuilderImpl#addTypeVariable(TypeVariableName)}
     *  and
     *  {@link org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl.BuilderImpl#addTypeVariable(TypeVariableName, CharSequence)}.
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @Test
    final void testAddTypeVariableWithNullArgument() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        TypeVariableName typeVariable;
        CharSequence javadoc;

        final var candidate = composer.classBuilder( "CandidateClass" );

        final Class<? extends Throwable> expectedException = NullArgumentException.class;

        typeVariable = null;
        javadoc = "Comment";
        try
        {
            candidate.addTypeVariable( typeVariable );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
        try
        {
            candidate.addTypeVariable( typeVariable, javadoc );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        typeVariable = TypeVariableName.from( "T" );
        javadoc = null;
        try
        {
            candidate.addTypeVariable( typeVariable, javadoc );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }   //  testAddTypeVariableWithNullArgument()

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
//  class TestBuilderAddTypeVariable

/*
 *  End of File
 */