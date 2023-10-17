/*
 * ============================================================================
 * Copyright © 2015 Square, Inc.
 * Copyright for the modifications © 2018-2023 by Thomas Thrien.
 * ============================================================================
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tquadrat.foundation.javacomposer;

import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.javacomposer.internal.Util.createDebugOutput;

import java.util.Map;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.CodeBlockImpl;

/**
 *  <p>{@summary The definition of a fragment for a {@code *.java} file,
 *  potentially containing declarations, statements, and documentation.} Code
 *  blocks are not necessarily well-formed Java code, and they are not
 *  validated. Implementations of this interface assume that {@code javac} will
 *  check correctness later!</p>
 *  <p>Code blocks do support placeholders like
 *  {@link java.text.Format}.
 *  Where
 *  {@link String#format(String, Object...) String.format()}
 *  uses percent {@code %} to reference target values, this class uses dollar
 *  sign {@code $} and has its own set of permitted placeholders:</p>
 *  <ul>
 *  <li>{@code $L} emits a <em>literal</em> value with no escaping. Arguments
 *  for literals may be strings, primitives,
 *  {@linkplain TypeSpec type declarations},
 *  {@linkplain AnnotationSpec annotations}
 *  and even other code blocks.</li>
 *  <li>{@code $N} emits a <em>name</em>, using name collision avoidance where
 *  necessary. Arguments for names may be Strings (actually any
 *  {@linkplain CharSequence character sequence}),
 *  {@linkplain ParameterSpec parameters},
 *  {@linkplain FieldSpec fields},
 *  {@linkplain MethodSpec methods},
 *  and
 *  {@linkplain TypeSpec types}.</li>
 *  <li>{@code $S} escapes the value as a <em>String</em>, wraps it with double
 *  quotes, and emits that. For example, {@code 6" sandwich} is emitted
 *  {@code "6\" sandwich"}.</li>
 *  <li>{@code $T} emits a <em>type</em> reference. Types will be imported if
 *  possible. Arguments for types may be
 *  {@linkplain Class classes},
 *  {@linkplain javax.lang.model.type.TypeMirror type mirrors},
 *  and
 *  {@linkplain javax.lang.model.element.Element elements}.</li>
 *  <li>{@code $$} emits a dollar sign.</li>
 *  <li>{@code $W} emits a space or a newline, depending on its position on the
 *  line. This prefers to wrap lines before 100 columns.</li>
 *  <li>{@code $Z} acts as a zero-width space. This prefers to wrap lines
 *  before 100 columns.</li>
 *  <li>{@code $>} increases the indentation level.</li>
 *  <li>{@code $<} decreases the indentation level.</li>
 *  <li>{@code $[} begins a statement. For multi-line statements, every line
 *  after the first line is double-indented.</li>
 *  <li>{@code $]} ends a statement.</li>
 *  </ul>
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: CodeBlock.java 1067 2023-09-28 21:09:15Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: CodeBlock.java 1067 2023-09-28 21:09:15Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public sealed interface CodeBlock
    permits CodeBlockImpl
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The definition of a builder for a new instance of an implementation of
     *  {@link CodeBlock}.
     *
     *  @author Square,Inc.
     *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: CodeBlock.java 1067 2023-09-28 21:09:15Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @SuppressWarnings( {"InnerClassOfInterface", "ClassWithTooManyMethods"} )
    @ClassVersion( sourceVersion = "$Id: CodeBlock.java 1067 2023-09-28 21:09:15Z tquadrat $" )
    @API( status = STABLE, since = "0.0.5" )
    public static interface Builder
    {
            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  Adds a
         *  {@link CodeBlock}
         *  instance.
         *
         *  @param  codeBlock   The code block.
         *  @return This {@code Builder} instance.
         */
        public Builder add( final CodeBlock codeBlock );

        /**
         *  <p>{@summary Adds code with positional or relative arguments.}</p>
         *  <p>Relative arguments map 1:1 with the placeholders in the format
         *  string.</p>
         *  <p>Positional arguments use an index after the placeholder to
         *  identify which argument index to use. For example, for a literal to
         *  reference the 3<sup>rd</sup> argument, use {@code "$3L"} (1 based
         *  index).</p>
         *  <p>Mixing relative and positional arguments in a call to add is
         *  illegal and will result in an error.</p>
         *
         *  @param  format  The format; may be empty.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder add( final String format, final Object... args );

        /**
         *  <p>{@summary Adds code with positional or relative arguments.}</p>
         *  <p>Relative arguments map 1:1 with the placeholders in the format
         *  string.</p>
         *  <p>Positional arguments use an index after the placeholder to
         *  identify which argument index to use. For example, for a literal to
         *  reference the 3<sup>rd</sup> argument, use {@code "$3L"} (1 based
         *  index).</p>
         *  <p>Mixing relative and positional arguments in a call to add is
         *  illegal and will result in an error.</p>
         *
         *  @param  addDebugOutput  {@code true} if debug output should be
         *      added to the generated code, {@code false} if not.
         *  @param  format  The format; may be empty.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.0.6
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public Builder add( final boolean addDebugOutput, final String format, final Object... args );

        /**
         *  <p>{@summary Adds code using named arguments.}</p>
         *  <p>Named arguments specify their name after the '$' followed by a
         *  colon {@code ":"} and the corresponding type character. Argument
         *  names consist of characters in {@code a-z, A-Z, 0-9, and _} and
         *  must start with a lowercase character.</p>
         *  <p>For example, to refer to the type
         *  {@link java.lang.Integer}
         *  with the argument name {@code clazz} use a format string containing
         *  {@code $clazz:T} and include the key {@code clazz} with value
         *  {@code java.lang.Integer.class} in the argument map.</p>
         *
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder addNamed( final String format, final Map<String,?> args );

        /**
         *  <p>{@summary Adds code using named arguments.}</p>
         *  <p>Named arguments specify their name after the '$' followed by a
         *  colon {@code ":"} and the corresponding type character. Argument
         *  names consist of characters in {@code a-z, A-Z, 0-9, and _} and
         *  must start with a lowercase character.</p>
         *  <p>For example, to refer to the type
         *  {@link java.lang.Integer}
         *  with the argument name {@code clazz} use a format string containing
         *  {@code $clazz:T} and include the key {@code clazz} with value
         *  {@code java.lang.Integer.class} in the argument map.</p>
         *
         *  @param  addDebugOutput  {@code true} if debug output should be
         *      added to the generated code, {@code false} if not.
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.0.6
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public Builder addNamed( final boolean addDebugOutput, final String format, final Map<String,?> args );

        /**
         *  <p>{@summary Adds a {@code CodeBlock} as a statement.}</p>
         *  <p>Do not use this method when the resulting code should be used
         *  as a field initializer. Use
         *  {@link #add(CodeBlock)}
         *  instead.</p>
         *
         *  @param  codeBlock   The statement.
         *  @return This {@code Builder} instance.
         *
         *  @see FieldSpec.Builder#initializer(CodeBlock)
         *
         *  @deprecated The code fails when the
         *      {@link CodeBlock}
         *      was created with calls to
         *      {@link CodeBlock.Builder#addStatement(String, Object...)}.
         *      Use
         *      {@link CodeBlock.Builder#add(CodeBlock)}
         *      instead.
         */
        @Deprecated( since = "0.1.0", forRemoval = false )
        public Builder addStatement( final CodeBlock codeBlock );

        /**
         *  <p>{@summary Adds a statement.}</p>
         *  <p>Do not use this method when the resulting code should be used
         *  as a field initializer. Use
         *  {@link #add(String, Object...)}
         *  instead.</p>
         *
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @see FieldSpec.Builder#initializer(CodeBlock)
         */
        public default Builder addStatement( final String format, final Object... args )
        {
            return addStatement( false, format, args );
        }   //  addStatement()

        /**
         *  <p>{@summary Adds a statement.}</p>
         *  <p>Do not use this method when the resulting code should be used
         *  as a field initializer. Use
         *  {@link #add(String, Object...)}
         *  instead.</p>
         *
         *  @param  addDebugOutput  {@code true} if debug output should be
         *      added to the generated code, {@code false} if not.
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @see FieldSpec.Builder#initializer(CodeBlock)
         *
         *  @since 0.0.6
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public Builder addStatement( final boolean addDebugOutput, final String format, final Object... args );

        /**
         *  Adds a static import.
         *
         *  @param  clazz   The class.
         *  @param  names   The names of the elements from the given class that
         *      are to be imported.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.2.0
         */
        @API( status = STABLE, since = "0.2.0" )
        public Builder addStaticImport( final Class<?> clazz, final String... names );

        /**
         *  Adds a static import.
         *
         *  @param  className   The class.
         *  @param  names   The names of the elements from the given class that
         *      are to be imported.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.2.0
         */
        @API( status = STABLE, since = "0.2.0" )
        public Builder addStaticImport( final ClassName className, final String... names );

        /**
         *  Adds a static import for the given {@code enum} value.
         *
         *  @param  constant    The {@code enum} value.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.2.0
         */
        @API( status = STABLE, since = "0.2.0" )
        public Builder addStaticImport( final Enum<?> constant );

        /**
         *  Starts a control flow construct.
         *
         *  @param  controlFlow <p>The control flow construct and its code, such
         *      as {@code if (foo == 5)}.</p>
         *      <p>Shouldn't contain braces or newline characters.</p>
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @see #endControlFlow()
         *  @see #endControlFlow(String, Object...)
         *  @see #endControlFlow(boolean,String, Object...)
         *  @see #nextControlFlow(String, Object...)
         *  @see #nextControlFlow(boolean,String, Object...)
         */
        public Builder beginControlFlow( final String controlFlow, final Object... args );

        /**
         *  Starts a control flow construct.
         *
         *  @param  addDebugOutput  {@code true} if debug output should be
         *      added to the generated code, {@code false} if not.
         *  @param  controlFlow <p>The control flow construct and its code, such
         *      as {@code if (foo == 5)}.</p>
         *      <p>Shouldn't contain braces or newline characters.</p>
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @see #endControlFlow()
         *  @see #endControlFlow(String, Object...)
         *  @see #endControlFlow(boolean,String, Object...)
         *  @see #nextControlFlow(String, Object...)
         *  @see #nextControlFlow(boolean,String, Object...)
         *
         *  @since 0.0.6
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public Builder beginControlFlow( final boolean addDebugOutput, final String controlFlow, final Object... args );

        /**
         *  Creates the
         *  {@link CodeBlock}
         *  from the added components.
         *
         *  @return The new {@code CodeBlock} instance.
         */
        public CodeBlock build();

        /**
         *  Ends a control flow construct that was previously begun with a call
         *  to
         *  {@link #beginControlFlow(String, Object...)}
         *  or
         *  {@link #beginControlFlow(boolean, String, Object...)}.
         *
         *  @return This {@code Builder} instance.
         */
        public Builder endControlFlow();

        /**
         *  <p>{@summary Ends a control flow construct that was previously
         *  started with a call to
         *  {@link #beginControlFlow(String, Object...)}
         *  or
         *  {@link #beginControlFlow(boolean, String, Object...)}.}</p>
         *  <p>This form is only used for {@code do/while} control flows.</p>
         *
         *  @param controlFlow  The optional control flow construct and its
         *      code, such as {@code while(foo == 20)}.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder endControlFlow( final String controlFlow, final Object... args );

        /**
         *  <p>{@summary Ends a control flow construct that was previously
         *  started with a call to
         *  {@link #beginControlFlow(String, Object...)}
         *  or
         *  {@link #beginControlFlow(boolean, String, Object...)}.}</p>
         *  <p>This form is only used for {@code do/while} control flows.</p>
         *
         *  @param  addDebugOutput  {@code true} if debug output should be
         *      added to the generated code, {@code false} if not.
         *  @param controlFlow  The optional control flow construct and its
         *      code, such as {@code while(foo == 20)}.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.0.6
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public Builder endControlFlow( final boolean addDebugOutput, final String controlFlow, final Object... args );

        /**
         *  Adds an indentation level to the code block.
         *
         *  @return This {@code Builder} instance.
         */
        public Builder indent();

        /**
         *  Checks whether the code block to build would be empty.
         *
         *  @return {@code true} if the code block would be empty,
         *      {@code false} otherwise.
         */
        public boolean isEmpty();

        /**
         *  Adds another control flow construct to an already existing one.
         *
         *  @param controlFlow  <p>The control flow construct and its code, such
         *      as {@code else if (foo == 10)}.</p>
         *      <p>Shouldn't contain braces or newline characters.</p>
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder nextControlFlow( final String controlFlow, final Object... args );

        /**
         *  Adds another control flow construct to an already existing one.
         *
         *  @param  addDebugOutput  {@code true} if debug output should be
         *      added to the generated code, {@code false} if not.
         *  @param controlFlow  The control flow construct and its code, such
         *      as {@code else if (foo == 10)}.<br>
         *      <br>Shouldn't contain braces or newline characters.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.0.6
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public Builder nextControlFlow( final boolean addDebugOutput, final String controlFlow, final Object... args );

        /**
         *  Removes an indentation level from the code block.
         *
         *  @return This {@code Builder} instance.
         */
        public Builder unindent();
    }
    //  interface Builder

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  An empty code block.
     *
     *  @deprecated Use
     *      {@link JavaComposer#emptyCodeBlock()}
     *      instead.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final CodeBlock EMPTY_CODEBLOCK = builder().build();

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a builder for an instance of {@code CodeBlock}.
     *
     *  @return The new builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#codeBlockBuilder()}
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder builder() { return CodeBlockImpl.builder(); }

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
     *  Checks whether this code block is empty.
     *
     *  @return {@code true} if the code block is empty, {@code false}
     *      otherwise.
     */
    public boolean isEmpty();

    /**
     *  <p>{@summary Joins this code block with the given code blocks into a
     *  single new {@code CodeBlock} instance, each separated by the given
     *  separator.}</p>
     *  <p>For example, joining &quot;{@code String s}&quot;,
     *  &quot;{@code Object o}&quot; and &quot;{@code int i}&quot; using
     *  &quot;{@code , }&quot; as the separator would produce
     *  &quot;{@code String s, Object o, int i}&quot;.</p>
     *
     *  @param  separator   The separator.
     *  @param  codeBlocks  The code blocks to join with this one.
     *  @return The new code block.
     */
    @API( status = STABLE, since = "0.2.0" )
    public CodeBlock join( final String separator, final CodeBlock... codeBlocks );

    /**
     *  <p>{@summary Joins this code block with the given code blocks into a
     *  single new {@code CodeBlock} instance, each separated by the given
     *  separator.} The given prefix will be prepended to the new
     *  {@code CodeBloc}, and the given suffix will be appended to it.</p>
     *  <p>For example, joining &quot;{@code String s}&quot;,
     *  &quot;{@code Object o}&quot; and &quot;{@code int i}&quot; using
     *  &quot;{@code , }&quot; as the separator would produce
     *  &quot;{@code String s, Object o, int i}&quot;.</p>
     *
     *  @param  separator   The separator.
     *  @param  prefix  The prefix.
     *  @param  suffix  The suffix.
     *  @param  codeBlocks  The code blocks to join.
     *  @return The new code block.
     */
    @API( status = STABLE, since = "0.2.0" )
    public CodeBlock join( final String separator, final String prefix, final String suffix, final CodeBlock... codeBlocks  );

    /**
     *  <p>{@summary Joins the given code blocks into a single
     *  {@code CodeBlock} instance, each separated by the given separator.}</p>
     *  <p>For example, joining &quot;{@code String s}&quot;,
     *  &quot;{@code Object o}&quot; and &quot;{@code int i}&quot; using
     *  &quot;{@code , }&quot; as the separator would produce
     *  &quot;{@code String s, Object o, int i}&quot;.</p>
     *
     *  @param  codeBlocks  The code blocks to join.
     *  @param  separator   The separator.
     *  @return The new code block.
     *
     *  @deprecated Replaced by
     *      {@link #join(String, CodeBlock...)}.
     */
    @SuppressWarnings( {"removal", "DeprecatedIsStillUsed"} )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static CodeBlock join( final Iterable<CodeBlock> codeBlocks, final String separator )
    {
        return CodeBlockImpl.join( codeBlocks, separator );
    }   //  join()

    /**
     *  <p>{@summary Joins the given code blocks into a single
     *  {@code CodeBlock} instance, each separated by the given separator.}
     *  The given prefix will be prepended to the new {@code CodeBloc}, and the
     *  given suffix will be appended to it.</p>
     *  <p>For example, joining &quot;{@code String s}&quot;,
     *  &quot;{@code Object o}&quot; and &quot;{@code int i}&quot; using
     *  &quot;{@code , }&quot; as the separator would produce
     *  &quot;{@code String s, Object o, int i}&quot;.</p>
     *
     *  @param  codeBlocks  The code blocks to join.
     *  @param  separator   The separator.
     *  @param  prefix  The prefix.
     *  @param  suffix  The suffix.
     *  @return The new code block.
     *
     *  @deprecated Replaced by
     *      {@link #join(String, String, String, CodeBlock...)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static CodeBlock join( final Iterable<CodeBlock> codeBlocks, final String separator, final String prefix, final String suffix  )
    {
        return CodeBlockImpl.join( codeBlocks, separator, prefix, suffix );
    }   //  join()

    /**
     *  Creates a new {@code CodeBlock} instance from the given format and
     *  arguments.
     *
     *  @param  format  The format.
     *  @param  args    The arguments.
     *  @return The new code block.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#codeBlockOf(String, Object...)}
     */
    @SuppressWarnings( {"removal", "ClassReferencesSubclass"} )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static CodeBlock of( final String format, final Object... args )
    {
        final var retValue = CodeBlockImpl.of( format, args );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  of()

    /**
     *  Creates a new {@code CodeBlock} instance from the given format and
     *  arguments.
     *
     *  @param  addDebugOutput  {@code true} if debug output should be added to
     *      the generated code, {@code false} if not.
     *  @param  format  The format.
     *  @param  args    The arguments.
     *  @return The new code block.
     *
     *  @since 0.0.6
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#codeBlockOf(String, Object...)}
     */
    @SuppressWarnings( {"removal", "ClassReferencesSubclass"} )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.6" )
    public static CodeBlock of( final boolean addDebugOutput, final String format, final Object... args )
    {
        final var retValue = CodeBlockImpl.of( createDebugOutput( addDebugOutput, false ), format, args );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  of()

    /**
     *  Creates a new builder that is initialised with the components of this
     *  code block.
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
//  interface CodeBlock

/*
 *  End of File
 */