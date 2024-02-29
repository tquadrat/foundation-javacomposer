/*
 * ============================================================================
 * Copyright © 2002-2024 by Thomas Thrien.
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

package org.tquadrat.foundation.javacomposer.internal;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.joining;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.javacomposer.internal.TypeNameImpl.VOID_PRIMITIVE;
import static org.tquadrat.foundation.lang.Objects.checkState;
import static org.tquadrat.foundation.lang.Objects.hash;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.UnexpectedExceptionError;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.CodeBlock;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.LambdaSpec;
import org.tquadrat.foundation.javacomposer.ParameterSpec;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.lang.Lazy;

/**
 *  The implementation for
 *  {@link LambdaSpec}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: LambdaSpecImpl.java 1105 2024-02-28 12:58:46Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: LambdaSpecImpl.java 1105 2024-02-28 12:58:46Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public final class LambdaSpecImpl implements LambdaSpec
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The implementation for
     *  {@link org.tquadrat.foundation.javacomposer.LambdaSpec.Builder}.
     *
     *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: LambdaSpecImpl.java 1105 2024-02-28 12:58:46Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: LambdaSpecImpl.java 1105 2024-02-28 12:58:46Z tquadrat $" )
    @API( status = INTERNAL, since = "0.0.5" )
    public static final class BuilderImpl implements Builder
    {
            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  The code for the lambda body.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private final CodeBlockImpl.BuilderImpl m_Code;

        /**
         *  The reference to the factory.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private final JavaComposer m_Composer;

        /**
         *  Flag that indicates whether the parameter types will be inferred.
         */
        private Boolean m_InferTypes = null;

        /**
         *  The flag is used to determine the emit format.
         */
        private int m_Lines = 0;

        /**
         *  The parameters for the lambda.
         */
        private final Collection<ParameterSpecImpl> m_Parameters = new ArrayList<>();

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code BuilderImpl} instance.
         *
         *  @param  composer    The reference to the factory that created this
         *      builder instance.
         */
        public BuilderImpl( @SuppressWarnings( "UseOfConcreteClass" ) final JavaComposer composer )
        {
            m_Composer = requireNonNullArgument( composer, "composer" );
            m_Code = (CodeBlockImpl.BuilderImpl) m_Composer.codeBlockBuilder();
        }   //  BuilderImpl()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addCode( final CodeBlock codeBlock )
        {
            m_Code.add( requireNonNullArgument( codeBlock, "codeBlock" ) );
            ++m_Lines;
            if( m_Composer.addDebugOutput() ) ++m_Lines;

            //---* Done *------------------------------------------------------
            return this;
        }   //  addCode()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addCode( final String format, final Object... args )
        {
            m_Code.add( format, args );
            ++m_Lines;
            if( m_Composer.addDebugOutput() ) ++m_Lines;

            //---* Done *------------------------------------------------------
            return this;
        }   //  addCode()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addComment( final String format, final Object... args )
        {
            m_Code.addWithoutDebugInfo( "// " + requireNonNullArgument( format, "format" ) + "\n", args );
            m_Lines += 2;

            //---* Done *------------------------------------------------------
            return this;
        }   //  addComment()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addParameter( final String name )
        {
            return addParameter( m_Composer.parameterOf( VOID_PRIMITIVE, name ) );
        }   //  addParameter()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addParameter( final ParameterSpec parameterSpec )
        {
            final var parameter = (ParameterSpecImpl) requireNonNullArgument( parameterSpec, "parameterSpec" );
            final var type = parameter.type();
            final var name = parameter.name();
            if( type == VOID_PRIMITIVE )
            {
                checkState( isNull( m_InferTypes) || m_InferTypes.booleanValue(), () -> new ValidationException( "Not inferring types; type required for %s".formatted( name ) ) );
                m_InferTypes = TRUE;
            }
            else
            {
                checkState( isNull( m_InferTypes ) || !m_InferTypes.booleanValue(), () -> new ValidationException( "Inferring types; no type allowed for %s".formatted( name ) ) );
                m_InferTypes = FALSE;
            }
            m_Parameters.add( parameter );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addParameter()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addParameter( final Type type, final String name )
        {
            return addParameter( m_Composer.parameterOf( type, name ) );
        }   //  addParameter()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addParameter( final TypeName type, final String name )
        {
            return addParameter( m_Composer.parameterOf( type, name ) );
        }   //  addParameter()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addParameters( final Iterable<? extends ParameterSpec> parameterSpecs )
        {
            for( final var parameterSpec : requireNonNullArgument( parameterSpecs, "parameterSpecs" ) )
            {
                m_Parameters.add( (ParameterSpecImpl) parameterSpec );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addParameters()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addStatement( final String format, final Object... args )
        {
            m_Code.addStatement( format, args );
            m_Lines += 2;
            if( m_Composer.addDebugOutput() ) ++m_Lines;

            //---* Done *------------------------------------------------------
            return this;
        }   //  addStatement()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl beginControlFlow( final String controlFlow, final Object... args )
        {
            m_Code.beginControlFlow( controlFlow, args );
            m_Lines += 2;
            if( m_Composer.addDebugOutput() ) ++m_Lines;

            //---* Done *------------------------------------------------------
            return this;
        }   //  beginControlFlow()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final LambdaSpecImpl build() { return new LambdaSpecImpl( this ); }

        /**
         *  Returns the code for the lambda body.
         *
         *  @return The body code.
         */
        @SuppressWarnings( "PublicMethodNotExposedInInterface" )
        public final CodeBlockImpl code() { return m_Code.build(); }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl endControlFlow()
        {
            m_Code.endControlFlow();
            m_Lines += 2;
            if( m_Composer.addDebugOutput() ) ++m_Lines;

            //---* Done *------------------------------------------------------
            return this;
        }   //  endControlFlow()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl endControlFlow( final String controlFlow, final Object... args )
        {
            m_Code.endControlFlow( controlFlow, args );
            m_Lines += 2;
            if( m_Composer.addDebugOutput() ) ++m_Lines;

            //---* Done *------------------------------------------------------
            return this;
        }   //  endControlFlow()

        /**
         *  Returns the flag that indicates whether the types of the parameters
         *  will be inferred.
         *
         *  @return {@code true} if the parameter types are inferred,
         *      {@code false} if they are explicit.
         */
        @SuppressWarnings( {"PublicMethodNotExposedInInterface", "BooleanMethodNameMustStartWithQuestion"} )
        public final boolean inferTypes() { return isNull( m_InferTypes ) || m_InferTypes.booleanValue(); }

        /**
         *  Return the flag that indicates the emit format.
         *
         *  @return {@code true} if the multi-line format with curly braces and
         *  a return statement is to be emitted, {@code false} if the single
         *  line format can be used.
         */
        @SuppressWarnings( "PublicMethodNotExposedInInterface" )
        public final boolean isMultiLine() { return m_Lines > 1; }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl nextControlFlow( final String controlFlow, final Object... args )
        {
            m_Code.nextControlFlow( controlFlow, args );
            m_Lines += 2;
            if( m_Composer.addDebugOutput() ) ++m_Lines;

            //---* Done *------------------------------------------------------
            return this;
        }   //  nextControlFlow()

        /**
         *  Returns the parameters for the lambda.
         *
         *  @return The parameters.
         */
        @SuppressWarnings( "PublicMethodNotExposedInInterface" )
        public final List<ParameterSpecImpl> parameters() { return List.copyOf( m_Parameters ); }
    }
    //  class BuilderImpl

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  Lazily initialised return value of
     *  {@link #toString()}
     *  for this instance.
     */
    private final Lazy<String> m_CachedString;

    /**
     *  The code of the body for this lambda.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final CodeBlockImpl m_Code;

    /**
     *  The reference to the factory.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final JavaComposer m_Composer;

    /**
     *  Flag that indicates whether the parameter types will be inferred.
     */
    private final boolean m_InferTypes;

    /**
     *  The flag that indicates the emit format. {@code true} stands for the
     *  multi-line format with curly braces and a return statement,
     *  {@code false} for the single line format.
     */
    private final boolean m_IsMultiLine;

    /**
     *  The parameters of this method.
     */
    private final List<ParameterSpecImpl> m_Parameters;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code LambdaSpecImpl} instance.
     *
     *  @param  builder The builder.
     */
    @SuppressWarnings( "AccessingNonPublicFieldOfAnotherObject" )
    public LambdaSpecImpl( @SuppressWarnings( "UseOfConcreteClass" ) final BuilderImpl builder )
    {
        m_Composer = builder.m_Composer;
        m_Code = builder.code();
        m_Parameters = builder.parameters();
        m_InferTypes = builder.inferTypes();
        m_IsMultiLine = builder.isMultiLine();

        m_CachedString = Lazy.use( this::initializeCachedString );
    }   //  LambdaSpecImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Emits this lambda to the given code writer.
     *
     *  @param  codeWriter  The code writer.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final void emit( @SuppressWarnings( "UseOfConcreteClass" ) final CodeWriter codeWriter ) throws UncheckedIOException
    {
        //---* Get the parameters *--------------------------------------------
        if( m_Parameters.isEmpty() )
        {
            codeWriter.emit( "()" );
        }
        else if( m_Parameters.size() == 1 )
        {
            final var parameter = m_Parameters.getFirst();
            final var name = parameter.name();
            final var type = parameter.type();

            if( m_InferTypes )
            {
                codeWriter.emit( "$L", name );
            }
            else
            {
                codeWriter.emit( "($T $L)", type, name );
            }
        }
        else
        {
            final String format;
            final Object [] args;
            if( m_InferTypes )
            {
                format = m_Parameters.stream().map( $ -> "$L" ).collect( joining(",", "(", ")" ) );
                args = m_Parameters.stream().map( ParameterSpecImpl::name ).toArray();
            }
            else
            {
                format = m_Parameters.stream().map( $ -> "$T $L" ).collect( joining(", ", "(", ")" ) );
                args = m_Parameters.stream().flatMap( p -> Stream.of( p.type(), p.name() ) ).toArray();
            }
            codeWriter.emit( format, args );
        }

        //---* The arrow operator *--------------------------------------------
        codeWriter.emit( " ->" );

        //---* The body *------------------------------------------------------
        if( m_Code.isEmpty() )
        {
            codeWriter.emit( " null" );
        }
        else
        {
            if( m_IsMultiLine )
            {
                codeWriter.emit( "\n{\n" )
                    .indent()
                    .emit( "$L", m_Code )
                    .unindent()
                    .emit( "}" );
            }
            else
            {
                codeWriter.emit( " $L", m_Code );
            }
        }
    }   //  emit()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = this == o;
        if( !retValue && (o instanceof final LambdaSpecImpl other) )
        {
            retValue = m_Composer.equals( other.m_Composer ) && toString().equals( o.toString() );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  equals()

    /**
     *  Returns the
     *  {@link JavaComposer}
     *  factory.
     *
     *  @return The reference to the factory.
     */
    @SuppressWarnings( "PublicMethodNotExposedInInterface" )
    public final JavaComposer getFactory() { return m_Composer; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int hashCode() { return hash( m_Composer, toString() ); }

    /**
     *  The initializer for
     *  {@link #m_CachedString}.
     *
     *  @return The return value for
     *      {@link #toString()}.
     */
    private final String initializeCachedString()
    {
        final var resultBuilder = new StringBuilder();
        final var codeWriter = new CodeWriter( m_Composer, resultBuilder );
        try
        {
            emit( codeWriter );
        }
        catch( final UncheckedIOException e )
        {
            throw new UnexpectedExceptionError( e.getCause() );
        }
        final var retValue = resultBuilder.toString();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  initializeCachedString()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "AccessingNonPublicFieldOfAnotherObject" )
    @Override
    public final BuilderImpl toBuilder()
    {
        final var retValue = new BuilderImpl( m_Composer );
        retValue.m_Code.addWithoutDebugInfo( m_Code );
        retValue.m_Parameters.addAll( m_Parameters );
        retValue.m_InferTypes = m_Parameters.isEmpty() ? null : Boolean.valueOf( m_InferTypes );

        /*
         * If the lambda is multiline, any positive number greater than 1 goes;
         * we honour Douglas Adams and his 'Hitchhiker's Guide to the Galaxy'.
         */
        //noinspection MagicNumber
        retValue.m_Lines = m_Code.isEmpty() ? 0 : m_IsMultiLine ? 42 : 1;

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toBuilder()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString() { return m_CachedString.get(); }
}
//  class LambdaSpecImpl

/*
 *  End of File
 */