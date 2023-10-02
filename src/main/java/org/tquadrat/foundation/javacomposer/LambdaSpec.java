/*
 * ============================================================================
 * Copyright © 2002-2023 by Thomas Thrien.
 * All Rights Reserved.
 * ============================================================================
 *
 * Licensed to the public under the agreements of the GNU Lesser General Public
 * License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.tquadrat.foundation.javacomposer;

import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.STABLE;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.LambdaSpecImpl;

/**
 *  <p>{@summary The specification for a generated lambda construct.}</p>
 *  <h2>Parameters and Types</h2>
 *  <p>When adding more than one parameter, all of them must have a specified
 *  type or none of them may have one, leaving the compiler to infer the type.
 *  As
 *  {@link JavaComposer#parameterBuilder(Type, CharSequence, Modifier...) paramterBuilder()}
 *  does require a type, we take
 *  {@link Primitives#VOID}
 *  instead of a concrete type if we want to get the type being inferred.</p>
 *  <h2>Formatting</h2>
 *  <p>Basically, a lambda expression has two different formats, one without
 *  curly braces</p>
 *  <pre><code>  a -&gt; modify( a )</code></pre>
 *  <p>and the other with with curly braces, requiring a return statement:</p>
 *  <pre><code>  a -&gt;
 *  {
 *      modify( a );
 *      return a.result;
 *  }</code></pre>
 *  <p>The body of the second form usually has more than one statement.</p>
 *  <p>JavaComposer emits the first form when only one of the methods</p>
 *  <ul>
 *      <li>{@link LambdaSpec.Builder#addCode(String, Object...)}</li>
 *      <li>{@link LambdaSpec.Builder#addCode(CodeBlock)}</li>
 *  </ul>
 *  <p>is called only once on the builder instance. The second form is forced,
 *  when one of the methods above is called again, or when one of the
 *  methods</p>
 *  <ul>
 *      <li>{@link LambdaSpec.Builder#addComment(String, Object...)}</li>
 *      <li>{@link LambdaSpec.Builder#addStatement(CodeBlock)}</li>
 *      <li>{@link LambdaSpec.Builder#addStatement(String, Object...)}</li>
 *      <li>{@link LambdaSpec.Builder#beginControlFlow(String, Object...)}</li>
 *      <li>{@link LambdaSpec.Builder#endControlFlow()}</li>
 *      <li>{@link LambdaSpec.Builder#endControlFlow(String, Object...)}</li>
 *      <li>{@link LambdaSpec.Builder#nextControlFlow(String, Object...)}</li>
 *  </ul>
 *  <p>is called.</p>
 *  <p>There is no validation on the code; this means it is in the caller's
 *  responsibility to ensure that there is a {@code return} statement in the
 *  second case.</p>
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: LambdaSpec.java 1067 2023-09-28 21:09:15Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: LambdaSpec.java 1067 2023-09-28 21:09:15Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public sealed interface LambdaSpec
    permits LambdaSpecImpl
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The builder for an instance of
     *  {@link LambdaSpec}
     *
     *  @extauthor  Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: LambdaSpec.java 1067 2023-09-28 21:09:15Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @SuppressWarnings( "InnerClassOfInterface" )
    @ClassVersion( sourceVersion = "$Id: LambdaSpec.java 1067 2023-09-28 21:09:15Z tquadrat $" )
    @API( status = STABLE, since = "0.0.5" )
    public static sealed interface Builder
        permits LambdaSpecImpl.BuilderImpl
    {
            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  <p>{@summary Adds code for the lambda body.}</p>
         *  <p>If only this method is called only once to add code to the
         *  lambda body, the short, single line form for the lambda expression
         *  will be emitted. That this will result in valid code requires that
         *  the given code block has an appropriate contents.</p>
         *
         *  @param  codeBlock   The code.
         *  @return This {@code Builder} instance.
         */
        public Builder addCode( final CodeBlock codeBlock );

        /**
         *  <p>{@summary Adds code for the lambda body.}</p>
         *  <p>If only this method is called only once to add code to the
         *  lambda body, the short, single line form for the lambda expression
         *  will be emitted.</p>
         *
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder addCode( final String format, final Object... args );

        /**
         *  <p>{@summary Adds a comment for the lambda body.}</p>
         *  <p>A call to this method forces the multi-line emit format for the
         *  lambda expression.</p>
         *
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder addComment( final String format, final Object... args );

        /**
         *  <p>{@summary Adds a parameter for the lambda.}</p>
         *  <p>The type of the parameter is inferred.</p>
         *
         *  @param  name    The name of the parameter.
         *  @return This {@code Builder} instance.
         */
        public Builder addParameter( final String name );

        /**
         *  Adds a parameter for the lambda. Only type and name of the given
         *  parameter are considered, annotations or modifiers will be ignored.
         *
         *  @param  parameterSpec   The parameter.
         *  @return This {@code Builder} instance.
         */
        public Builder addParameter( final ParameterSpec parameterSpec );

        /**
         *  Adds a parameter for the lambda.
         *
         *  @param  type    The type of the parameter.
         *  @param  name    The name of the parameter.
         *  @return This {@code Builder} instance.
         */
        public Builder addParameter( final Type type, final String name );

        /**
         *  Adds a parameter for the lambda.
         *
         *  @param  type    The type of the parameter.
         *  @param  name    The name of the parameter.
         *  @return This {@code Builder} instance.
         */
        public Builder addParameter( final TypeName type, final String name );

        /**
         *  Adds parameters for the lambda. Only type and name of the given
         *  parameters are considered, annotations or modifiers will be
         *  ignored.
         *
         *  @param  parameterSpecs  The parameters.
         *  @return This {@code Builder} instance.
         */
        public Builder addParameters( final Iterable<? extends ParameterSpec> parameterSpecs );

        /**
         *  <p>{@summary Adds a statement to the code for the lambda body.}</p>
         *  <p>A call to this method forces the multi-line emit format for the
         *  lambda expression.</p>
         *
         *  @param  statement   The statement.
         *  @return This {@code Builder} instance.
         */
        public Builder addStatement( final CodeBlock statement );

        /**
         *  <p>{@summary Adds a statement to the code for the lambda body.}</p>
         *  <p>A call to this method forces the multi-line emit format for the
         *  lambda expression.</p>
         *
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder addStatement( final String format, final Object... args );

        /**
         *  <p>{@summary Adds the begin of a control flow for the lambda
         *  body.}</p>
         *  <p>A call to this method forces the multi-line emit format for the
         *  lambda expression.</p>
         *
         *  @param  controlFlow The control flow construct and its code, such
         *      as &quot;{@code if (foo == 5)}&quot;; it should not contain
         *      braces or newline characters.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @see #endControlFlow()
         *  @see #endControlFlow(String, Object...)
         *  @see #nextControlFlow(String, Object...)
         */
        public Builder beginControlFlow( final String controlFlow, final Object... args );

        /**
         *  Creates a new
         *  {@link LambdaSpec}
         *  instance from the components that have been added to this builder.
         *
         *  @return The {@code MethodSpec} instance.
         */
        public LambdaSpec build();

        /**
         *  <p>{@summary Ends the current control flow for the lambda
         *  body.}</p>
         *  <p>A call to this method forces the multi-line emit format for the
         *  lambda expression.</p>
         *
         *  @return This {@code Builder} instance.
         *
         *  @see #beginControlFlow(String, Object...)
         *  @see #endControlFlow(String, Object...)
         *  @see #nextControlFlow(String, Object...)
         */
        public Builder endControlFlow();

        /**
         *  <p>{@summary Ends the current control flow for the lambda body;
         *  this version is only used for {@code do-while} constructs.}</p>
         *  <p>A call to this method forces the multi-line emit format for the
         *  lambda expression.</p>
         *
         *  @param  controlFlow The optional control flow construct and its
         *      code, such as &quot;{@code while(foo == 20)}&quot;; it should
         *      not contain braces or newline characters.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @see #beginControlFlow(String, Object...)
         *  @see #endControlFlow()
         */
        public Builder endControlFlow( final String controlFlow, final Object... args );

        /**
         *  <p>{@summary Begins another control flow for the lambda body.}</p>
         *  <p>A call to this method forces the multi-line emit format for the
         *  lambda expression.</p>
         *
         *  @param  controlFlow The control flow construct and its code, such
         *      as &quot;{@code else if (foo == 10)}&quot;; it should not
         *      contain braces or newline characters.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @see #beginControlFlow(String, Object...)
         *  @see #endControlFlow()
         */
        public Builder nextControlFlow( final String controlFlow, final Object... args );
    }
    //  interface Builder

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a builder for an instance of {@code LambdaSpec}.
     *
     *  @return The new builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#lambdaBuilder()}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder builder() { return LambdaSpecImpl.builder(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public boolean equals( final Object o );

    /**
     *  {@inheritDoc}
     */
    @Override
    public int hashCode();

    /**
     *  Creates a new builder that is initialised with the components of this
     *  lambda.
     *
     *  @return The new builder.
     */
    public Builder toBuilder();

    /**
     *  {@inheritDoc}
     */
    @Override
    public String toString();
}
//  class LambdaSpec

/*
 *  End of File
 */