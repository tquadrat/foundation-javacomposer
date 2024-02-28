/*
 * ============================================================================
 * Copyright © 2015 Square, Inc.
 * Copyright for the modifications © 2018-2024 by Thomas Thrien.
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

package org.tquadrat.foundation.javacomposer.internal;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.Files.notExists;
import static javax.tools.JavaFileObject.Kind.SOURCE;
import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.javacomposer.Layout.LAYOUT_DEFAULT;
import static org.tquadrat.foundation.lang.CommonConstants.UTF8;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidNonNullArgument;
import static org.tquadrat.foundation.util.IOUtils.getNullAppendable;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.UnexpectedExceptionError;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.JavaFile;
import org.tquadrat.foundation.javacomposer.Layout;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.lang.Lazy;
import org.tquadrat.foundation.lang.Objects;

/**
 *  The implementation of
 *  {@link JavaFile}
 *  for a Java file containing a single top level class.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: JavaFileImpl.java 1085 2024-01-05 16:23:28Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: JavaFileImpl.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public final class JavaFileImpl implements JavaFile
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The builder for an instance of
     *  {@link JavaFileImpl}
     *  as an implementation of
     *  {@link org.tquadrat.foundation.javacomposer.JavaFile.Builder}.
     *
     *  @author Square,Inc.
     *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: JavaFileImpl.java 1085 2024-01-05 16:23:28Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: JavaFileImpl.java 1085 2024-01-05 16:23:28Z tquadrat $" )
    @API( status = INTERNAL, since = "0.0.5" )
    public static final class BuilderImpl implements JavaFile.Builder
    {
            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  The reference to the factory.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private final JavaComposer m_Composer;

        /**
         *  The file comment.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        private final CodeBlockImpl.BuilderImpl m_FileComment;

        /**
         *  The layout for the output of
         *  {@link JavaFile}.
         */
        private Layout m_Layout = LAYOUT_DEFAULT;

        /**
         *  The name of the package for the class in the
         *  {@link JavaFileImpl}.
         */
        private final String m_PackageName;

        /**
         *  Flag that determines whether to skip the imports for classes from
         *  the package {@code java.lang}.
         */
        private boolean m_SkipJavaLangImports;

        /**
         *  The static imports.
         */
        private final Collection<String> m_StaticImports = new TreeSet<>();

        /**
         *  The
         *  {@link TypeSpecImpl}
         *  for the class in the
         *  {@link JavaFileImpl}.
         */
        private final TypeSpecImpl m_TypeSpec;

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code BuilderImpl} instance.
         *
         *  @param  composer    The reference to the factory that created this
         *      builder instance.
         *  @param  packageName The name of the package for the class in the
         *      {@linkplain JavaFileImpl Java file}.
         *      May be empty for the default package.
         *  @param  typeSpec    The
         *      {@link TypeSpecImpl TypeSpec}
         *      instance for the class in the
         *      {@linkplain JavaFileImpl Java file}.
         */
        @SuppressWarnings( "UseOfConcreteClass" )
        public BuilderImpl( final JavaComposer composer, final CharSequence packageName, final TypeSpecImpl typeSpec )
        {
            m_Composer = requireNonNullArgument( composer, "composer" );
            m_PackageName = requireNonNullArgument( packageName, "packageName" ).toString().intern();
            m_TypeSpec = requireNonNullArgument( typeSpec, "typeSpec" );
            m_FileComment = new CodeBlockImpl.BuilderImpl( m_Composer );

            m_Layout = composer.getLayout();
        }   //  BuilderImpl()

        /**
         *  Creates a new {@code BuilderImpl} instance.
         *
         *  @param  composer    The reference to the factory that created this
         *      builder instance.
         *  @param  packageName The name of the package for the class in the
         *      {@link JavaFileImpl}.
         *  @param  typeSpec    The
         *      {@link TypeSpecImpl}
         *      instance for the class in the
         *      {@link JavaFileImpl}.
         *  @param  fileComment The already existing file comments.
         *  @param  layout  The layout for the output of
         *      {@link JavaFile}.
         *  @param  skipJavaLangImports {@code true} means that the imports for
         *      classes from the package {@code java.lang} are skipped,
         *      {@code false} means that the imports are added explicitly.
         */
        @SuppressWarnings( {"TypeMayBeWeakened", "UseOfConcreteClass", "ConstructorWithTooManyParameters"} )
        public BuilderImpl( final JavaComposer composer, final CharSequence packageName, final TypeSpecImpl typeSpec, final CodeBlockImpl fileComment, final Layout layout, final boolean skipJavaLangImports )
        {
            this( composer, packageName, typeSpec );
            if( !fileComment.isEmpty() ) m_FileComment.add( fileComment );
            m_SkipJavaLangImports = skipJavaLangImports;
            m_Layout = layout;
        }   //  BuilderImpl()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addFileComment( final String format, final Object... args )
        {
            m_FileComment.addWithoutDebugInfo( format, args );

            //---* Done *------------------------------------------------------
            return this;
        }   //  addFileComment()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addStaticImport( final Class<?> clazz, final String... names )
        {
            return addStaticImport( ClassNameImpl.from( clazz ), names );
        }   //  addStaticImport()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addStaticImport( final ClassName className, final String... names )
        {
            final var canonicalName = requireNonNullArgument( className, "className" ).canonicalName();
            for( final var name : requireValidNonNullArgument( names, "names", v -> v.length > 0, "%s array is empty"::formatted ) )
            {
                m_StaticImports.add(
                    format(
                        "%s.%s",
                        canonicalName,
                        requireValidArgument(
                            name,
                            "name",
                            Objects::nonNull,
                            $ -> "null entry in names array: %s".formatted( Arrays.toString( names ) )
                        )
                    )
                );
            }

            //---* Done *------------------------------------------------------
            return this;
        }   //  addStaticImport()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl addStaticImport( final Enum<?> constant )
        {
            return addStaticImport( ClassNameImpl.from( requireNonNullArgument( constant, "constant" ).getDeclaringClass() ), constant.name() );
        }   //  addStaticImport()

        /**
         *  {@inheritDoc}
         */
        @Override
        public final JavaFileImpl build() { return new JavaFileImpl( this ); }

        /**
         *  Returns the file comment.
         *
         *  @return The file comment.
         */
        @SuppressWarnings( {"PublicMethodNotExposedInInterface"} )
        public final CodeBlockImpl fileComment() { return m_FileComment.build(); }

        /**
         *  Returns the layout for the output of the
         *  {@link JavaFile}.
         *
         *  @return The layout.
         */
        @SuppressWarnings( "PublicMethodNotExposedInInterface" )
        public final Layout layout() { return m_Layout; }

        /**
         *  Returns the package name.
         *
         *  @return The package name.
         */
        @SuppressWarnings( "PublicMethodNotExposedInInterface" )
        public final String packageName() { return m_PackageName; }

        /**
         *  {@inheritDoc}
         */
        @Override
        public final BuilderImpl skipJavaLangImports( final boolean flag )
        {
            m_SkipJavaLangImports = flag;

            //---* Done *------------------------------------------------------
            return this;
        }   //  skipJavaLangImports()

        /**
         *  Returns the flag that rules whether imports for classes from the
         *  package {@code java.lang} will be omitted.
         *
         *  @return {@code true} means that the imports for classes from the
         *      package {@code java.lang} are skipped, {@code false} means that
         *      the imports are added explicitly.
         *
         *  @see #skipJavaLangImports(boolean)
         */
        @SuppressWarnings( {"PublicMethodNotExposedInInterface", "BooleanMethodNameMustStartWithQuestion"} )
        public final boolean skipJavaLangImports() { return m_SkipJavaLangImports; }

        /**
         *  Returns the static imports.
         *
         *  @return The static imports.
         */
        @SuppressWarnings( "PublicMethodNotExposedInInterface" )
        public final Set<String> staticImports()
        {
            final Collection<String> staticImports = new HashSet<>( m_StaticImports );
            staticImports.addAll( m_FileComment.build().getStaticImports() );
            staticImports.addAll( m_TypeSpec.getStaticImports() );
            final var retValue = Set.copyOf( staticImports );

            //---* Done *----------------------------------------------------------
            return retValue;
        }   //  staticImports()

        /**
         *  Returns the specification of the type for the Java file.
         *
         *  @return The type specification.
         */
        @SuppressWarnings( "PublicMethodNotExposedInInterface" )
        public final TypeSpecImpl typeSpec() { return m_TypeSpec; }
    }
    //  class BuilderImpl

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  An implementation of
     *  {@link Appendable}
     *  that places any data that is put to it into the void.
     */
    private static final Appendable NULL_APPENDABLE = getNullAppendable();

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
     *  The reference to the factory.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final JavaComposer m_Composer;

    /**
     *  The file comment.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final CodeBlockImpl m_FileComment;

    /**
     *  The layout for the output of this {@code JavaFile}.
     */
    private final Layout m_Layout;

    /**
     *  The name of the package for the class.
     */
    private final String m_PackageName;

    /**
     *  Flag that determines whether to skip the imports for classes from
     *  the package {@code java.lang}.
     */
    private final boolean m_SkipJavaLangImports;

    /**
     *  The static imports.
     */
    private final Set<String> m_StaticImports;

    /**
     *  The
     *  {@link TypeSpecImpl}
     *  for the class in the
     *  {@link JavaFileImpl}.
     */
    private final TypeSpecImpl m_TypeSpec;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code JavaFileImpl} instance.
     *
     *  @param  builder The builder that was used to collect the data for the
     *      new instance.
     */
    @SuppressWarnings( {"AccessingNonPublicFieldOfAnotherObject", "UseOfConcreteClass"} )
    private JavaFileImpl( final BuilderImpl builder )
    {
        m_Composer = builder.m_Composer;
        m_FileComment = builder.fileComment();
        m_PackageName = builder.packageName();
        m_TypeSpec = builder.typeSpec();
        m_SkipJavaLangImports = builder.skipJavaLangImports();
        m_StaticImports = builder.staticImports();
        m_Layout = builder.layout();

        m_CachedString = Lazy.use( this::initializeCachedString );
    }   //  JavaFileImpl()

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
     *  @deprecated Got obsolete with the introduction of
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static BuilderImpl builder( final CharSequence packageName, final TypeSpec typeSpec )
    {
        final var typeSpecImpl = (TypeSpecImpl) requireNonNullArgument( typeSpec, "typeSpec" );
        final var composer = typeSpecImpl.getFactory();
        final var retValue = new BuilderImpl( composer, requireNonNullArgument( packageName, "packageName" ), typeSpecImpl );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  builder()

    /**
     *  Writes this instance of {@code JavaFile} to the given
     *  {@link CodeWriter} instance.
     *
     *  @param  codeWriter  The code writer.
     *  @throws UncheckedIOException A problem occurred when writing to the
     *      output target.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final void emit( final CodeWriter codeWriter ) throws UncheckedIOException
    {
        codeWriter.pushPackage( m_PackageName );

        if( !m_FileComment.isEmpty() )
        {
            codeWriter.emitBlockComment( m_FileComment );
            codeWriter.emit( "\n" );
        }

        if( !m_PackageName.isEmpty() )
        {
            codeWriter.emit( "package $L;\n", m_PackageName );
            codeWriter.emit( "\n" );
        }

        if( !m_StaticImports.isEmpty() )
        {
            m_StaticImports.stream()
                .sorted()
                .forEachOrdered( signature -> codeWriter.emit( "import static $L;\n", signature ) );
            codeWriter.emit( "\n" );
        }

        var importedTypesCount = 0;
        for( final var className : new TreeSet<>( codeWriter.importedTypes().values() ) )
        {
            if( m_SkipJavaLangImports && "java.lang".equals( className.packageName() ) ) continue;
            codeWriter.emit( "import $L;\n", className.withoutAnnotations() );
            ++importedTypesCount;
        }
        if( importedTypesCount > 0 ) codeWriter.emit( "\n" );

        m_TypeSpec.emit( codeWriter, null, Set.of() );

        codeWriter.popPackage();

        switch( m_Layout )
        {
            case LAYOUT_JAVAPOET: break;

            case LAYOUT_FOUNDATION:
            {
                codeWriter.emit(
                    """
                    
                    /*
                     * End of File
                     */""" );
                break;
            }

            //$CASES-OMITTED$
            default: break;
        }
    }   //  emit()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = this == o;
        if( !retValue && (o instanceof final JavaFileImpl other) )
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
    @SuppressWarnings( {"PublicMethodNotExposedInInterface"} )
    public final JavaComposer getFactory() { return m_Composer; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int hashCode() { return toString().hashCode(); }

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
        try
        {
            writeTo( resultBuilder );
        }
        catch( final IOException e )
        {
            throw new UnexpectedExceptionError( e.getCause() );
        }
        final var retValue = resultBuilder.toString();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  initializeCachedString()

    /**
     *  Returns a new builder that is initialised with this {@code JavaFile}
     *  instance.
     *
     *  @return The new builder.
     */
    @Override
    public final Builder toBuilder()
    {
        final var retValue = new BuilderImpl( m_Composer, m_PackageName, m_TypeSpec, m_FileComment, m_Layout, m_SkipJavaLangImports );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toBuilder()

    /**
     *  Creates a
     *  {@link JavaFileObject}
     *  from this instance of {@code JavaFile}.
     *
     *  @return The {@code JavaFileObject}.
     */
    @Override
    public final JavaFileObject toJavaFileObject()
    {
        /*
         * This does not work for anonymous types (how?) so no check for the
         * name is required.
         */
        @SuppressWarnings( "OptionalGetWithoutIsPresent" )
        final var name = m_TypeSpec.name().get();
        final var uri = URI.create( (m_PackageName.isEmpty() ? name : m_PackageName.replace( '.', '/' ) + '/' + name) + SOURCE.extension );
        @SuppressWarnings( "AnonymousInnerClass" )
        final var retValue = new SimpleJavaFileObject( uri, SOURCE )
        {
            /**
             *  Time of the last modification of this instance.
             */
            private final long m_LastModified = currentTimeMillis();

            /**
             *  {@inheritDoc}
             */
            @Override
            public final String getCharContent( final boolean ignoreEncodingErrors ) { return JavaFileImpl.this.toString(); }

            /**
             *  {@inheritDoc}
             */
            @Override
            public final long getLastModified() { return m_LastModified; }

            /**
             *  {@inheritDoc}
             */
            @Override
            public final InputStream openInputStream() throws IOException
            {
                return new ByteArrayInputStream( getCharContent( true ).getBytes( UTF8 ) );
            }   //  openInputStream()
        };

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toJavaFileObject()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString() { return m_CachedString.get(); }

    /**
     *  Writes this {@code JavaFile} instance to the given
     *  {@link Appendable}.
     *
     *  @param  out The output target.
     *  @throws IOException A problem occurred when writing to the output
     *      target.
     */
    @Override
    public final void writeTo( final Appendable out ) throws IOException
    {
        requireNonNullArgument( out, "out" );

        /*
         * First pass: emit the entire class, just to collect the types we'll
         * need to import.
         */
        final var importsCollector = new CodeWriter( m_Composer, NULL_APPENDABLE, m_StaticImports );
        emit( importsCollector );
        final var suggestedImports = importsCollector.suggestedImports();

        /*
         * Second pass: write the code, taking advantage of the imports.
         */
        final var codeWriter = new CodeWriter( m_Composer, out, suggestedImports, m_StaticImports );
        try
        {
            emit( codeWriter );
        }
        catch( final UncheckedIOException e )
        {
            throw e.getCause();
        }
    }   //  writeTo()

    /**
     *  Writes this {@code JavaFile} instance to the given target folder as a
     *  UTF-8 file, using the standard directory structure for the packages.
     *
     *  @param  directory   The target folder.
     *  @throws IOException A problem occurred when writing to the output
     *      target.
     */
    @Override
    public final void writeTo( final File directory ) throws IOException
    {
        writeTo( requireNonNullArgument( directory, "directory" ).toPath() );
    }   //  writeTo()

    /**
     *  Writes  {@code JavaFile} instance to the given
     *  {@link Filer}
     *  instance.
     *
     *  @param  filer   The target.
     *  @throws IOException A problem occurred when writing to the output
     *      target.
     */
    @Override
    public final void writeTo( final Filer filer ) throws IOException
    {
        /*
         * This does not work for anonymous types (how?) so no check for the
         * name is required.
         */
        @SuppressWarnings( "OptionalGetWithoutIsPresent" )
        final var name = m_TypeSpec.name().get();
        final var fileName = m_PackageName.isEmpty() ? name : m_PackageName + "." + name;
        final var originatingElements = m_TypeSpec.originatingElements();
        final var filerSourceFile = filer.createSourceFile( fileName, originatingElements.toArray( Element []::new ) );
        try( final var writer = filerSourceFile.openWriter() )
        {
            writeTo( writer );
        }
        catch( final IOException e )
        {
            filerSourceFile.delete();
            throw e;
        }
    }   //  writeTo

    /**
     *  Writes this {@code JavaFile} instance to the given target folder as a
     *  UTF-8 file, using the standard directory structure for the packages.
     *
     *  @param  directory   The target folder.
     *  @throws IOException A problem occurred when writing to the output
     *      target.
     */
    @Override
    public void writeTo( final Path directory ) throws IOException
    {
        var outputDirectory = requireValidNonNullArgument( directory, "directory", v -> notExists( v ) || isDirectory( v ), $ -> "path %s exists but is not a directory.".formatted( directory ) );
        if( !m_PackageName.isEmpty() )
        {
            for( final var packageComponent : m_PackageName.split( "\\." ) )
            {
                outputDirectory = outputDirectory.resolve( packageComponent );
            }
            createDirectories( outputDirectory );
        }

        /*
         * This does not work for anonymous types (how?) so no check for the
         * name is required.
         */
        @SuppressWarnings( "OptionalGetWithoutIsPresent" )
        final var outputPath = outputDirectory.resolve( m_TypeSpec.name().get() + SOURCE.extension );
        try( final var writer = new OutputStreamWriter( newOutputStream( outputPath ), UTF8 ) )
        {
            writeTo( writer );
        }
    }   //  writeTo()
}
//  class JavaFileImpl

/*
 *  End of File
 */