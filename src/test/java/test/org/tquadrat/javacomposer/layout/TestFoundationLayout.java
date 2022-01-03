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

package test.org.tquadrat.javacomposer.layout;

import static java.lang.Boolean.getBoolean;
import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.tquadrat.foundation.javacomposer.Layout.LAYOUT_FOUNDATION;
import static org.tquadrat.foundation.lang.CommonConstants.PROPERTY_IS_TEST;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the Foundation layout.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestFoundationLayout.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestFoundationLayout.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestFoundationLayout" )
public class TestFoundationLayout extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Test the layouts for an annotation.
     */
    @Test
    final void testLayoutAnnotation()
    {
        skipThreadTest();

        final var composer = new JavaComposer( LAYOUT_FOUNDATION );

        final var typeSpec = ElementProvider.createAnnotation( composer );

        final var candidate = ElementProvider.createJavaFile( composer, typeSpec );
        final var expected =
            """
            package org.tquadrat.test;

            import java.lang.String;
            import java.lang.SuppressWarnings;

            @SuppressWarnings( "javadoc" )
            @interface TestAnnotation
            {
                    /*------------*\\
                ====** Attributes **=======================================================
                    \\*------------*/
                @SuppressWarnings( "javadoc" )
                String member1();

                /**
                 * The second member for this annotation.
                 */
                String member2() default "Fußpilz";
            }
            //  annotation TestAnnotation

            /*
             * End of File
             */""";
        final var actual = candidate.toString();
        if( getBoolean( PROPERTY_IS_TEST ) )
        {
            out.println( "----< annotation >--------------------------------------" );
            out.println( actual );
        }
        assertEquals( expected, actual );
    }   //  testLayoutAnnotation()

    /**
     *  Test the layouts for a regular class.
     */
    @Test
    final void testLayoutClass()
    {
        skipThreadTest();

        final var composer = new JavaComposer( LAYOUT_FOUNDATION );

        final var typeSpec = ElementProvider.createClass( composer );

        final var candidate = ElementProvider.createJavaFile( composer, typeSpec );
        final var expected =
            """
            package org.tquadrat.test;

            import java.lang.String;
            import java.lang.SuppressWarnings;
            import java.util.Date;
            import java.util.Locale;
            import java.util.UUID;
            import org.tquadrat.foundation.Option;
            import org.tquadrat.foundation.Param;
            import org.tquadrat.foundation.annotation.ClassVersion;

            @SuppressWarnings( "javadoc" )
            @ClassVersion( sourceVersion = "Generated with JavaComposer", isGenerated = true )
            public class TestClass
            {
                    /*---------------*\\
                ====** Inner Classes **====================================================
                    \\*---------------*/
                /**
                 * This is an inner class.
                 *
                 * @since 10
                 */
                @ClassVersion( sourceVersion = "Generated with JavaComposer", isGenerated = true )
                private class InnerClass
                {
                }
                //  class InnerClass

                /**
                 * This is an inner interface.
                 *
                 * @since 10
                 */
                @ClassVersion( sourceVersion = "Generated with JavaComposer", isGenerated = true )
                interface InnerInterface
                {
                }
                //  interface InnerInterface

                    /*-----------*\\
                ====** Constants **========================================================
                    \\*-----------*/
                @SuppressWarnings( "javadoc" )
                public static final Date DATE_CONSTANT = Date.now();

                /**
                 * Constant value: {@value}.
                 */
                public static final int INTEGER_CONSTANT = 42;

                /**
                 * A String constant: {@value}
                 */
                public static final String STRING_CONSTANT1 = "value";

                /**
                 * Another String constant
                 * <p>The value is: {@value}.</p>
                 */
                public static final String STRING_CONSTANT2 = "other_value";

                    /*------------*\\
                ====** Attributes **=======================================================
                    \\*------------*/
                @SuppressWarnings( "javadoc" )
                @Option( member1 = "value", member2 = "value" )
                @Param(
                    member1 = "value",
                    member2 = "value"
                )
                private final UUID m_Id;

                @SuppressWarnings( "javadoc" )
                private Locale m_Locale = Locale.getDefault();

                /**
                 * A property.
                 */
                private int m_Property;

                @SuppressWarnings( "javadoc" )
                private static InnerClass m_StaticField;

                    /*------------------------*\\
                ====** Static Initialisations **===========================================
                    \\*------------------------*/
                @SuppressWarnings( "javadoc" )
                private static final InnerClass m_FinalStaticField;

                static {
                    m_StaticField = new InnerClass();
                }

                    /*--------------*\\
                ====** Constructors **=====================================================
                    \\*--------------*/
                {
                    m_Id = new UUID.randomUUID();
                }

                @SuppressWarnings( "javadoc" )
                TestClass()
                {}  //  TestClass()

                    /*---------*\\
                ====** Methods **==========================================================
                    \\*---------*/
                @SuppressWarnings( "javadoc" )
                public final Locale getLocale()
                {
                    return m_Locale;
                }  //  getLocale()

                @SuppressWarnings( "javadoc" )
                public final int getProperty()
                {
                    return m_Property;
                }  //  getProperty()

                @SuppressWarnings( "javadoc" )
                void method1()
                {
                    if( flag ) {
                        doThis();
                    } else {
                        doSomethingDifferent();
                    }
                }  //  method1()

                @SuppressWarnings( "javadoc" )
                String method2()
                {
                    return "value";
                }  //  method2()

                @SuppressWarnings( "javadoc" )
                public final void setLocale( final Locale value )
                {
                    m_Locale = value;
                }  //  setLocale()

                @SuppressWarnings( "javadoc" )
                public final void setProperty( final int value )
                {
                    m_Property = value;
                }  //  setProperty()

                /**
                 *
                 * @param uuid The id
                 */
                public final void setter( final UUID uuid )
                {
                    m_Id = uuid;
                }  //  setter()
            }
            //  class TestClass

            /*
             * End of File
             */""";
        final var actual = candidate.toString();
        if( getBoolean( PROPERTY_IS_TEST ) )
        {
            out.println( "----< class >-------------------------------------------" );
            out.println( actual );
        }
        assertEquals( expected, actual );
    }   //  testLayoutClass()

    /**
     *  Test the layouts for an enum class.
     */
    @Test
    final void testLayoutEnum()
    {
        skipThreadTest();

        final var composer = new JavaComposer( LAYOUT_FOUNDATION );

        final var typeSpec = ElementProvider.createEnum( composer );

        final var candidate = ElementProvider.createJavaFile( composer, typeSpec );
        final var expected =
            """
            package org.tquadrat.test;

            import java.lang.Override;
            import java.lang.String;
            import java.lang.SuppressWarnings;
            import org.tquadrat.foundation.annotation.ClassVersion;

            @SuppressWarnings( "javadoc" )
            @ClassVersion( sourceVersion = "Generated with JavaComposer", isGenerated = true )
            enum TestEnum
            {
                    /*------------------*\\
                ====** Enum Declaration **=================================================
                    \\*------------------*/
                @SuppressWarnings( "javadoc" )
                VALUE1,

                /**
                 * Another Value */
                VALUE2,

                /**
                 * A value that overrides toString() */
                VALUE3
                {
                        /*---------*\\
                    ====** Methods **==========================================================
                        \\*---------*/
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public String toString()
                    {
                        return "something else";
                    }  //  toString()
                };

                    /*---------*\\
                ====** Methods **==========================================================
                    \\*---------*/
                /**
                 * {@inheritDoc}
                 */
                @Override
                public String toString()
                {
                    return name();
                }  //  toString()
            }
            //  enum TestEnum

            /*
             * End of File
             */""";
        final var actual = candidate.toString();
        if( getBoolean( PROPERTY_IS_TEST ) )
        {
            out.println( "----< enum >--------------------------------------------" );
            out.println( actual );
        }
        assertEquals( expected, actual );
    }   //  testLayoutEnum()

    /**
     *  Test the layouts for an interface.
     */
    @Test
    final void testLayoutInterface()
    {
        skipThreadTest();

        final var composer = new JavaComposer( LAYOUT_FOUNDATION );

        final var typeSpec = ElementProvider.createInterface( composer );

        final var candidate = ElementProvider.createJavaFile( composer, typeSpec );
        final var expected =
            """
            package org.tquadrat.test;

            import java.lang.Override;
            import java.lang.String;
            import java.lang.SuppressWarnings;
            import java.util.Date;
            import org.tquadrat.foundation.annotation.ClassVersion;

            @SuppressWarnings( "javadoc" )
            @ClassVersion( sourceVersion = "Generated with JavaComposer", isGenerated = true )
            public interface TestInterface
            {
                    /*---------------*\\
                ====** Inner Classes **====================================================
                    \\*---------------*/
                /**
                 * This is an inner class.
                 *
                 * @since 10
                 */
                @ClassVersion( sourceVersion = "Generated with JavaComposer", isGenerated = true )
                class InnerClass
                {
                }
                //  class InnerClass

                    /*-----------*\\
                ====** Constants **========================================================
                    \\*-----------*/
                @SuppressWarnings( "javadoc" )
                Date DATE_CONSTANT = Date.now();

                /**
                 * Constant value: {@value}.
                 */
                int INTEGER_CONSTANT = 42;

                /**
                 * A String constant: {@value}
                 */
                String STRING_CONSTANT1 = "value";

                /**
                 * Another String constant
                 * <p>The value is: {@value}.</p>
                 */
                String STRING_CONSTANT2 = "other_value";

                    /*---------*\\
                ====** Methods **==========================================================
                    \\*---------*/
                /**
                 * {@inheritDoc}
                 */
                @Override
                String toString();
            }
            //  interface TestInterface

            /*
             * End of File
             */""";
        final var actual = candidate.toString();
        if( getBoolean( PROPERTY_IS_TEST ) )
        {
            out.println( "----< interface >---------------------------------------" );
            out.println( actual );
        }
        assertEquals( expected, actual );
    }   //  testLayoutInterface()
}
//  class TestFoundationLayout

/*
 *  End of File
 */