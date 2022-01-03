/*
 * ============================================================================
 * Copyright © 2015 Square, Inc.
 * Copyright for the modifications © 2018-2021 by Thomas Thrien.
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

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.JavaFileImpl;

/**
 *  The definition for a Java file containing a single top level class.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: JavaFile.java 854 2021-01-20 22:44:45Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: JavaFile.java 854 2021-01-20 22:44:45Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public sealed interface JavaFile
    permits JavaFileImpl
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The definition for a builder for an instance of an implementation of
     *  {@link JavaFile}.
     *
     *  @author Square,Inc.
     *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: JavaFile.java 854 2021-01-20 22:44:45Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @SuppressWarnings( "InnerClassOfInterface" )
    @ClassVersion( sourceVersion = "$Id: JavaFile.java 854 2021-01-20 22:44:45Z tquadrat $" )
    @API( status = STABLE, since = "0.0.5" )
    public static sealed interface Builder
        permits JavaFileImpl.BuilderImpl
    {
            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  Adds text to the file comment.
         *
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder addFileComment( final String format, final Object... args );

        /**
         *  Adds a static import.
         *
         *  @param  clazz   The class.
         *  @param  names   The names of the elements from the given class that
         *      are to be imported.
         *  @return This {@code Builder} instance.
         */
        public Builder addStaticImport( final Class<?> clazz, final String... names );

        /**
         *  Adds a static import.
         *
         *  @param  className   The class.
         *  @param  names   The names of the elements from the given class that
         *      are to be imported.
         *  @return This {@code Builder} instance.
         */
        public Builder addStaticImport( final ClassName className, final String... names );

        /**
         *  Adds a static import for the given {@code enum} value.
         *
         *  @param  constant    The {@code enum} value.
         *  @return This {@code Builder} instance.
         */
        public Builder addStaticImport( final Enum<?> constant );

        /**
         *  Builds an instance of
         *  {@link JavaFile}
         *  from this builder.
         *
         *  @return The {@code JavaFile} instance.
         */
        public JavaFile build();

        /**
         *  Sets the indentation value.
         *
         *  @param  indent  The indentation.
         *  @return This {@code Builder} instance.
         *
         *  @deprecated The indentation is determined by the layout only; it
         *      cannot be overwritten. This implementation of this method does
         *      nothing.
         */
        @Deprecated( since = "0.2.0", forRemoval = true )
        public Builder indent( final String indent );

        /**
         *  Sets the layout for the
         *  {@link JavaFile}.
         *
         *  @param  layout  The layout.
         *  @return This {@code Builder} instance.
         *
         *  @deprecated Got obsolete with the use of
         *      {@link JavaComposer}
         *      as the factory.
         */
        @Deprecated( since = "0.2.0", forRemoval = true )
        public Builder layout( Layout layout );

        /**
         *  <p>{@summary Call this to omit imports for classes from the package
         *  {@code java.lang}, such as
         *  {@link String}
         *  or
         *  {@link Math}.}</p>
         *  <p>By default, JavaComposer explicitly imports types in
         *  {@code java.lang} to defend against naming conflicts. Suppose an
         *  (ill-advised) class is named {@code com.example.String}. When
         *  {@code java.lang} imports are skipped, generated code in
         *  {@code com.example} that references {@code java.lang.String} will
         *  get {@code com.example.String} instead.</p>
         *
         *  @param  flag    {@code true} means that the imports for classes
         *      from the package {@code java.lang} are skipped, {@code false}
         *      means that the imports are added explicitly.
         *  @return This {@code Builder} instance.
         */
        public Builder skipJavaLangImports( final boolean flag );
    }
    //  interface Builder

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a builder for a new instance of {@code JavaFile} from the given
     *  package name and class definition.
     *
     *  @param  packageName The package name.
     *  @param  typeSpec    The class definition.
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#javaFileBuilder(CharSequence, TypeSpec)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder builder( final CharSequence packageName, final TypeSpec typeSpec )
    {
        final var retValue = JavaFileImpl.builder( packageName, typeSpec );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  builder()

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
     *  Returns a new builder that is initialised with this {@code JavaFile}
     *  instance.
     *
     *  @return The new builder.
     */
    public Builder toBuilder();

    /**
     *  Creates a
     *  {@link JavaFileObject}
     *  from this instance of {@code JavaFile}.
     *
     *  @return The {@code JavaFileObject}.
     */
    public JavaFileObject toJavaFileObject();

    /**
     *  {@inheritDoc}
     */
    @Override
    public String toString();

    /**
     *  Writes this {@code JavaFile} instance to the given
     *  {@link Appendable}.
     *
     *  @param  out The output target.
     *  @throws IOException A problem occurred when writing to the output
     *      target.
     */
    public void writeTo( final Appendable out ) throws IOException;

    /**
     *  Writes this {@code JavaFile} instance to the given target folder as a
     *  UTF-8 file, using the standard directory structure for the packages.
     *
     *  @param  directory   The target folder.
     *  @throws IOException A problem occurred when writing to the output
     *      target.
     */
    public void writeTo( final File directory ) throws IOException;

    /**
     *  Writes  {@code JavaFile} instance to the given
     *  {@link Filer}
     *  instance.
     *
     *  @param  filer   The target.
     *  @throws IOException A problem occurred when writing to the output
     *      target.
     */
    public void writeTo( final Filer filer ) throws IOException;

    /**
     *  Writes this {@code JavaFile} instance to the given target folder as a
     *  UTF-8 file, using the standard directory structure for the packages.
     *
     *  @param  directory   The target folder.
     *  @throws IOException A problem occurred when writing to the output
     *      target.
     */
    public void writeTo( final Path directory ) throws IOException;
}
//  interface JavaFile

/*
 *  End of File
 */