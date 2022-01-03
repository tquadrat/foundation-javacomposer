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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.tquadrat.foundation.javacomposer.Layout.LAYOUT_JAVAPOET;
import static test.org.tquadrat.javacomposer.layout.ElementProvider.createAnnotation;
import static test.org.tquadrat.javacomposer.layout.ElementProvider.createClass;
import static test.org.tquadrat.javacomposer.layout.ElementProvider.createEnum;
import static test.org.tquadrat.javacomposer.layout.ElementProvider.createInterface;
import static test.org.tquadrat.javacomposer.layout.ElementProvider.createJavaFile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the Javapoet layout.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestJavapoetLayout.java 937 2021-12-14 21:59:00Z tquadrat $
 */
@SuppressWarnings( "MisorderedAssertEqualsArguments" )
@ClassVersion( sourceVersion = "$Id: TestJavapoetLayout.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestJavapoetLayout" )
public class TestJavapoetLayout extends TestBaseClass
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

        final var composer = new JavaComposer( LAYOUT_JAVAPOET );

        final var typeSpec = createAnnotation( composer );

        final var candidate = createJavaFile( composer, typeSpec );
        final var expected =
            """
            package org.tquadrat.test;

            import java.lang.String;

            @interface TestAnnotation {
              String member1();

              /**
               * The second member for this annotation.
               */
              String member2() default "Fußpilz";
            }
            """;
        final var actual = candidate.toString();
        assertEquals( expected, actual );
    }   //  testLayoutAnnotation()

    /**
     *  Test the layouts for a regular class.
     */
    @Test
    final void testLayoutClass()
    {
        skipThreadTest();

        final var composer = new JavaComposer( LAYOUT_JAVAPOET );

        final var typeSpec = createClass( composer );

        final var candidate = createJavaFile( composer, typeSpec );
        final var expected =
            """
            package org.tquadrat.test;

            import java.lang.String;
            import java.util.Date;
            import java.util.Locale;
            import java.util.UUID;
            import org.tquadrat.foundation.Option;
            import org.tquadrat.foundation.Param;
            import org.tquadrat.foundation.annotation.ClassVersion;

            @ClassVersion(sourceVersion = "Generated with JavaComposer", isGenerated = true)
            public class TestClass {
              private static InnerClass m_StaticField;

              private static final InnerClass m_FinalStaticField;

              /**
               * A String constant: {@value}
               */
              public static final String STRING_CONSTANT1 = "value";

              /**
               * Another String constant
               * <p>The value is: {@value}.</p>
               */
              public static final String STRING_CONSTANT2 = "other_value";

              /**
               * Constant value: {@value}.
               */
              public static final int INTEGER_CONSTANT = 42;

              public static final Date DATE_CONSTANT = Date.now();

              static {
                m_StaticField = new InnerClass();
              }

              /**
               * A property.
               */
              private int m_Property;

              private Locale m_Locale = Locale.getDefault();

              @Option(member1 = "value", member2 = "value")
              @Param(
                  member1 = "value",
                  member2 = "value"
              )
              private final UUID m_Id;

              {
                m_Id = new UUID.randomUUID();
              }

              TestClass() {
              }

              void method1() {
                if( flag ) {
                  doThis();
                } else {
                  doSomethingDifferent();
                }
              }

              /**
               *
               * @param uuid The id
               */
              public final void setter(final UUID uuid) {
                m_Id = uuid;
              }

              public final int getProperty() {
                return m_Property;
              }

              public final void setProperty(final int value) {
                m_Property = value;
              }

              public final Locale getLocale() {
                return m_Locale;
              }

              public final void setLocale(final Locale value) {
                m_Locale = value;
              }

              String method2() {
                return "value";
              }

              /**
               * This is an inner interface.
               *
               * @since 10
               */
              @ClassVersion(sourceVersion = "Generated with JavaComposer", isGenerated = true)
              interface InnerInterface {
              }

              /**
               * This is an inner class.
               *
               * @since 10
               */
              @ClassVersion(sourceVersion = "Generated with JavaComposer", isGenerated = true)
              private class InnerClass {
              }
            }
            """;
        final var actual = candidate.toString();
        assertEquals( expected, actual );
    }   //  testLayoutClass()

    /**
     *  Test the layouts for an enum class.
     */
    @Test
    final void testLayoutEnum()
    {
        skipThreadTest();

        final var composer = new JavaComposer( LAYOUT_JAVAPOET );

        final var typeSpec = createEnum( composer );

        final var candidate = createJavaFile( composer, typeSpec );
        final var expected =
            """
            package org.tquadrat.test;

            import java.lang.Override;
            import java.lang.String;
            import org.tquadrat.foundation.annotation.ClassVersion;

            @ClassVersion(sourceVersion = "Generated with JavaComposer", isGenerated = true)
            enum TestEnum {
              VALUE1,

              /**
               * Another Value */
              VALUE2,

              /**
               * A value that overrides toString() */
              VALUE3 {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public String toString() {
                  return "something else";
                }
              };

              /**
               * {@inheritDoc}
               */
              @Override
              public String toString() {
                return name();
              }
            }
            """;
        final var actual = candidate.toString();
        assertEquals( expected, actual );
    }   //  testLayoutEnum()

    /**
     *  Test the layouts for an interface.
     */
    @Test
    final void testLayoutInterface()
    {
        skipThreadTest();

        final var composer = new JavaComposer( LAYOUT_JAVAPOET );

        final var typeSpec = createInterface( composer );

        final var candidate = createJavaFile( composer, typeSpec );
        final var expected =
            """
            package org.tquadrat.test;

            import java.lang.Override;
            import java.lang.String;
            import java.util.Date;
            import org.tquadrat.foundation.annotation.ClassVersion;

            @ClassVersion(sourceVersion = "Generated with JavaComposer", isGenerated = true)
            public interface TestInterface {
              /**
               * A String constant: {@value}
               */
              String STRING_CONSTANT1 = "value";

              /**
               * Another String constant
               * <p>The value is: {@value}.</p>
               */
              String STRING_CONSTANT2 = "other_value";

              /**
               * Constant value: {@value}.
               */
              int INTEGER_CONSTANT = 42;

              Date DATE_CONSTANT = Date.now();

              /**
               * {@inheritDoc}
               */
              @Override
              String toString();

              /**
               * This is an inner class.
               *
               * @since 10
               */
              @ClassVersion(sourceVersion = "Generated with JavaComposer", isGenerated = true)
              class InnerClass {
              }
            }
            """;
        final var actual = candidate.toString();
        assertEquals( expected, actual );
    }   //  testLayoutInterface()
}
//  class TestJavapoetLayout

/*
 *  End of File
 */