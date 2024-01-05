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

package org.tquadrat.javapoet.helper;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkState;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.tquadrat.javapoet.TestTypesEclipse;

/**
 *  A JUnit4
 *  {@link Rule}
 *  that executes tests such that an instances of
 *  {@link Elements}
 *  and
 *  {@link Types}
 *  are available during execution.<br>
 *  <br>To use this rule in a test, just add the following
 *  field:<pre><code>  &#64;Rule
 *  public CompilationRule compilationRule = new CompilationRule();</code></pre>
 *
 *  @author Gregory Kick
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: CompilationRule.java 1085 2024-01-05 16:23:28Z tquadrat $
 */
public final class CompilationRule implements TestRule
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    private Elements m_Elements;

    private Types m_Types;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     * Creates a new {@code CompilationRule} instance.
     */
    @SuppressWarnings( "RedundantNoArgConstructor" )
    public CompilationRule() { super(); }

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    @SuppressWarnings( {"resource", "MethodOnlyUsedFromInnerClass"} )
    private static final boolean compile( final Iterable<? extends Processor> processors )
    {
        final JavaCompiler compiler = new EclipseCompiler();
        final var diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
        final JavaFileManager fileManager = compiler.getStandardFileManager( diagnosticCollector, Locale.getDefault(), UTF_8 );
        final var task = compiler.getTask( null, fileManager, diagnosticCollector, Set.of(), Set.of( TestTypesEclipse.class.getCanonicalName() ), Set.of() );
        task.setProcessors( processors );
        final var retValue = task.call();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  compile()

    /**
     * {@inheritDoc}
     */
    @Override
    public final Statement apply( final Statement base, final Description description )
    {
        final var retValue = new Statement()
        {
            /**
             *  {@inheritDoc}
             */
            @SuppressWarnings( {"InnerClassTooDeeplyNested", "AnonymousInnerClassWithTooManyMethods", "OverlyComplexAnonymousInnerClass", "ProhibitedExceptionThrown"} )
            @Override
            public final void evaluate() throws Throwable
            {
                final var thrown = new AtomicReference<Throwable>();
                final var successful = compile( List.of( new AbstractProcessor()
                {
                    /**
                     *  {@inheritDoc}
                     */
                    @Override
                    public final Set<String> getSupportedAnnotationTypes()
                    {
                        return Set.of( "*" );
                    }   //  getSupportedAnnotationTypes()

                    /**
                     *  {@inheritDoc}
                     */
                    @Override
                    public final SourceVersion getSupportedSourceVersion()
                    {
                        return SourceVersion.latest();
                    }   //  getSupportedSourceVersion()

                    /**
                     *  {@inheritDoc}
                     */
                    @Override
                    public final synchronized void init( @SuppressWarnings( "hiding" ) final ProcessingEnvironment processingEnv )
                    {
                        super.init( processingEnv );
                        m_Elements = processingEnv.getElementUtils();
                        m_Types = processingEnv.getTypeUtils();
                    }   //  init()

                    /**
                     *  {@inheritDoc}
                     */
                    @Override
                    public final boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv )
                    {
                        // just run the test on the last round after
                        // compilation is over
                        if( roundEnv.processingOver() )
                        {
                            try
                            {
                                base.evaluate();
                            }
                            catch( final Throwable e )
                            {
                                thrown.set( e );
                            }
                        }
                        return false;
                    }   //  process()
                } ) );
                checkState( successful );
                final var t = thrown.get();
                if( nonNull( t ) )
                {
                    throw t;
                }
            }
        };

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  apply()

    /**
     * Returns the
     * {@link Elements}
     * instance associated with the current execution of the rule.
     *
     * @throws IllegalStateException if this method is invoked outside the
     *     execution of the rule.
     */
    public final Elements getElements()
    {
        assumeFalse( isNull( m_Elements ) );
        checkState( nonNull( m_Elements ), "Not running within the rule" );
        return m_Elements;
    }   //  getElements()

    /**
     * Returns the
     * {@link Types}
     * instance associated with the current execution of the rule.
     *
     * @throws IllegalStateException if this method is invoked outside the
     *     execution of the rule.
     */
    public final Types getTypes()
    {
        assumeFalse( isNull( m_Elements ) );
        checkState( nonNull( m_Elements ), "Not running within the rule" );
        return m_Types;
    }   //  getTypes()
}
//  class CompilationRule

/*
 *  End of File
 */