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

import javax.lang.model.element.AnnotationMirror;
import java.lang.annotation.Annotation;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.AnnotationSpecImpl;

/**
 *  The specification for a generated annotation on a declaration.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: AnnotationSpec.java 1066 2023-09-28 19:51:53Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: AnnotationSpec.java 1066 2023-09-28 19:51:53Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public sealed interface AnnotationSpec
    permits AnnotationSpecImpl
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The specification of a builder for an instance of an implementation of
     *  {@link AnnotationSpec}.
     *
     *  @author Square,Inc.
     *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: AnnotationSpec.java 1066 2023-09-28 19:51:53Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @SuppressWarnings( "InnerClassOfInterface" )
    @ClassVersion( sourceVersion = "$Id: AnnotationSpec.java 1066 2023-09-28 19:51:53Z tquadrat $" )
    @API( status = STABLE, since = "0.0.5" )
    public static sealed interface Builder
        permits AnnotationSpecImpl.BuilderImpl
    {
            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  Adds a building block.
         *
         *  @param  name    The name.
         *  @param  format  The format for the
         *      {@link CodeBlock}.
         *  @param  args  The arguments for the
         *      {@link CodeBlock}.
         *  @return This {@code Builder} instance.
         *
         *  @see JavaComposer#codeBlockOf(String, Object...)
         */
        public default Builder addMember( final CharSequence name, final String format, final Object... args )
        {
            return addMember( name, false, format, args );
        }   //  addMember()

        /**
         *  Adds a building block.
         *
         *  @param  name    The name.
         *  @param  addDebugOutput  {@code true} if debug output should be
         *      added to the generated code, {@code false} if not.
         *  @param  format  The format for the
         *      {@link CodeBlock}.
         *  @param  args  The arguments for the
         *      {@link CodeBlock}.
         *  @return This {@code Builder} instance.
         *
         *  @see CodeBlock#of(String, Object...)
         *  @since 0.0.6
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @SuppressWarnings( "removal" )
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public Builder addMember( final CharSequence name, final boolean addDebugOutput, final String format, final Object... args );

        /**
         *  Adds a building block.
         *
         *  @param  name    The name.
         *  @param  codeBlock   The
         *      {@link CodeBlock}
         *      representing the new member.
         *  @return This {@code Builder} instance.
         */
        public Builder addMember( final CharSequence name, final CodeBlock codeBlock );

        /**
         *  Creates the {@code AnnotationSpec} instance from the added members.
         *
         *  @return The built instance.
         */
        public AnnotationSpec build();

        /**
         *  Sets a flag that forces the inline presentation of the
         *  annotation.<br>
         *  <br>Inline:<pre><code>@Column(name = "updated_at", nullable = false)</code></pre>
         *
         *  Not inline:<pre><code>  @Column(
         *      name = "updated_at",
         *      nullable = false
         *  )</code></pre>
         *
         *  @param  flag    {@code true} for the forced inline presentation,
         *      {@code false} for the multi-line presentation.
         *  @return This {@code Builder} instance.
         */
        public Builder forceInline( boolean flag );
    }
    //  interface Builder

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a builder for an instance of an implementation for
     *  {@code AnnotationSpec} from the given
     *  {@link ClassName}
     *  instance.
     *
     *  @param  type    The class name.
     *  @return The new builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#annotationBuilder(ClassName)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder builder( final ClassName type )
    {
        return AnnotationSpecImpl.builder( type );
    }   //  builder()

    /**
     *  Creates a builder for an instance of an implementation for
     *  {@code AnnotationSpec} from the given
     *  {@link Class}
     *  instance.
     *
     *  @param  type    The class.
     *  @return The new builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#annotationBuilder(ClassName)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder builder( final Class<?> type )
    {
        return AnnotationSpecImpl.builder( type );
    }   //  builder()

    /**
     *  {@inheritDoc}
     */
    @Override
    public boolean equals( final Object o );

    /**
     *  Creates an instance of {@code AnnotationSpec} from the given
     *  {@link Annotation}
     *  instance.
     *
     *  @param  annotation  The annotation.
     *  @return The new instance of {@code AnnotationSpec}.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#createAnnotation(Annotation)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static AnnotationSpec get( final Annotation annotation )
    {
        return AnnotationSpecImpl.get( annotation );
    }   //  get()

    /**
     *  Creates an instance of {@code AnnotationSpec} from the given
     *  {@link Annotation}
     *  instance.
     *
     *  @param  annotation  The annotation.
     *  @param  includeDefaultValues    {@code true} to include the
     *      annotation's default values, {@code false} to ignore them.
     *  @return The new instance of {@code AnnotationSpec}.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#createAnnotation(Annotation,boolean)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static AnnotationSpec get( final Annotation annotation, final boolean includeDefaultValues )
    {
        @SuppressWarnings( "ClassReferencesSubclass" )
        final var retValue = AnnotationSpecImpl.get( annotation, includeDefaultValues );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  get()

    /**
     *  Creates an instance of {@code AnnotationSpec} from the given
     *  {@link AnnotationMirror}
     *  instance.
     *
     *  @param  annotation  The annotation mirror.
     *  @return The new instance of {@code AnnotationSpec}.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#createAnnotation(AnnotationMirror)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static AnnotationSpec get( final AnnotationMirror annotation )
    {
        @SuppressWarnings( "ClassReferencesSubclass" )
        final var retValue = AnnotationSpecImpl.get( annotation );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  get()

    /**
     *  {@inheritDoc}
     */
    @Override
    public int hashCode();

    /**
     *  Creates a new builder that is initialised with the components of this
     *  annotation.
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
//  interface AnnotationSpec

/*
 *  End of File
 */