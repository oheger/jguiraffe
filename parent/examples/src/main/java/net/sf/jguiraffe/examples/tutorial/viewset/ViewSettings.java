/*
 * Copyright 2006-2025 The JGUIraffe Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.jguiraffe.examples.tutorial.viewset;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.sf.jguiraffe.examples.tutorial.model.FileData;
import net.sf.jguiraffe.gui.builder.components.Color;
import net.sf.jguiraffe.gui.builder.components.ColorHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * A data class for storing a view definition.
 * </p>
 * <p>
 * The tutorial application allows creating a view definition for each
 * directory. A view definition stores several properties which define how a
 * specific directory is displayed, e.g. colors, sort order, filter conditions,
 * etc. This class stores all the data of a view definition. An instance is used
 * by the view settings dialog as model bean.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ViewSettings.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ViewSettings implements Serializable
{
    /** Constant for the name of the file with the view settings. */
    public static final String VIEW_SETTINGS_FILE = ".viewsettings";

    /**
     * Constant for the name under which this instance is stored in the context.
     */
    public static final String CTX_NAME = "viewSettingsModel";

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 20100111L;

    /** The logger. */
    private static final Log LOG = LogFactory.getLog(ViewSettings.class);

    /** The background color. */
    private Color backgroundColor;

    /** The foreground color. */
    private Color foregroundColor;

    /** The selection background color. */
    private Color selectionBackground;

    /** The selection foreground color. */
    private Color selectionForeground;

    /** The index of the sort color. */
    private Integer sortColumn;

    /** The sort mode for directories. */
    private Integer sortDirectories;

    /** The sort descending flag. */
    private boolean sortDescending;

    /** A flag whether the filter for file types is active. */
    private boolean filterTypes;

    /** An array with the file types to filter for. */
    private String[] fileTypes;

    /** A flag whether the filter for file size is active. */
    private boolean filterSize;

    /** The minimum file size for the size filter. */
    private Integer minFileSize;

    /** A flag whether the date filter is active. */
    private boolean filterDate;

    /** The from date for the date filter. */
    private Date fileDateFrom;

    /** The to date for the date filter. */
    private Date fileDateTo;

    /**
     * Creates a new instance of {@code ViewSettings} and initializes it with
     * default values.
     */
    public ViewSettings()
    {
        initDefaults();
    }

    /**
     * Stores this object in the specified directory. Using this method the view
     * settings for this directory can be made persistent.
     *
     * @param directory the target directory
     * @throws IOException if an error occurs
     */
    public void save(File directory) throws IOException
    {
        File settingsFile = new File(directory, VIEW_SETTINGS_FILE);
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(
                settingsFile));

        try
        {
            os.writeObject(this);
            LOG.info("Saved settings file: " + settingsFile);
        }
        finally
        {
            os.close();
        }
    }

    /**
     * Returns the {@code ViewSettings} object for the specified directory. If
     * in this directory a file with the reserved name for the {@code
     * ViewSettings} object exists, it is loaded. If this file does not exist or
     * if loading this file causes an error, a default instance is returned.
     *
     * @param directory the directory in question
     * @return the {@code ViewSettings} object for this directory
     */
    public static ViewSettings forDirectory(File directory)
    {
        File settingsFile = new File(directory, VIEW_SETTINGS_FILE);

        if (settingsFile.exists())
        {
            LOG.info("Trying to load view settings from " + settingsFile);
            ObjectInputStream is = null;
            try
            {
                is = new ObjectInputStream(new FileInputStream(settingsFile));
                return (ViewSettings) is.readObject();
            }
            catch (IOException ioex)
            {
                LOG.error("Error when reading view settings!", ioex);
            }
            catch (ClassNotFoundException cfex)
            {
                LOG.error("Class not found when reading view settings!", cfex);
            }
            finally
            {
                if (is != null)
                {
                    try
                    {
                        is.close();
                    }
                    catch (IOException ioex)
                    {
                        LOG.warn("Error when closing stream.", ioex);
                    }
                }
            }
        }

        return new ViewSettings(); // return default settings
    }

    public Color getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public Color getForegroundColor()
    {
        return foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor)
    {
        this.foregroundColor = foregroundColor;
    }

    public Color getSelectionBackground()
    {
        return selectionBackground;
    }

    public void setSelectionBackground(Color selectionBackground)
    {
        this.selectionBackground = selectionBackground;
    }

    public Color getSelectionForeground()
    {
        return selectionForeground;
    }

    public void setSelectionForeground(Color selectionForeground)
    {
        this.selectionForeground = selectionForeground;
    }

    public Integer getSortColumn()
    {
        return sortColumn;
    }

    public void setSortColumn(Integer sortColumn)
    {
        this.sortColumn = sortColumn;
    }

    public Integer getSortDirectories()
    {
        return sortDirectories;
    }

    public void setSortDirectories(Integer sortDirectories)
    {
        this.sortDirectories = sortDirectories;
    }

    public boolean isSortDescending()
    {
        return sortDescending;
    }

    public void setSortDescending(boolean sortDescending)
    {
        this.sortDescending = sortDescending;
    }

    public boolean isFilterTypes()
    {
        return filterTypes;
    }

    public void setFilterTypes(boolean filterTypes)
    {
        this.filterTypes = filterTypes;
    }

    public String[] getFileTypes()
    {
        return fileTypes;
    }

    public void setFileTypes(String[] fileTypes)
    {
        this.fileTypes = fileTypes;
    }

    public boolean isFilterSize()
    {
        return filterSize;
    }

    public void setFilterSize(boolean filterSize)
    {
        this.filterSize = filterSize;
    }

    public Integer getMinFileSize()
    {
        return minFileSize;
    }

    public void setMinFileSize(Integer minFileSize)
    {
        this.minFileSize = minFileSize;
    }

    public boolean isFilterDate()
    {
        return filterDate;
    }

    public void setFilterDate(boolean filterDate)
    {
        this.filterDate = filterDate;
    }

    public Date getFileDateFrom()
    {
        return fileDateFrom;
    }

    public void setFileDateFrom(Date fileDateFrom)
    {
        this.fileDateFrom = fileDateFrom;
    }

    public Date getFileDateTo()
    {
        return fileDateTo;
    }

    public void setFileDateTo(Date fileDateTo)
    {
        this.fileDateTo = fileDateTo;
    }

    /**
     * Creates a {@code Comparator} object that implements the sort order
     * specified in this object.
     *
     * @return the {@code Comparator}
     */
    public Comparator<FileData> createComparator()
    {
        int sortCol = (getSortColumn() == null) ? 0 : getSortColumn()
                .intValue();
        Comparator<FileData> fileComp;
        switch (sortCol)
        {
        case 2:
            fileComp = new FileSizeComparator();
            break;
        case 1:
            fileComp = new FileDateComparator();
            break;
        default:
            fileComp = new FileNameComparator();
        }

        int dirComp = (getSortDirectories() == null) ? 0 : getSortDirectories()
                .intValue();
        return new FileDataComparator(new FileDirComparator(dirComp), fileComp,
                isSortDescending());
    }

    /**
     * Creates a {@code FileFilter} object that implements the filter criteria
     * defined for this object. This filter can be used directly when listing
     * the directory this {@code ViewSettings} object is associated with.
     *
     * @return a {@code FileFilter} for filtering a directory listing
     */
    public FileFilter createFileFilter()
    {
        List<FileFilter> filters = new ArrayList<FileFilter>(3);

        if (isFilterTypes())
        {
            filters.add(new FileTypeFilter(new HashSet<String>(Arrays
                    .asList(fileTypes))));
        }
        if (isFilterDate())
        {
            filters.add(new FileDateFilter(fileDateFrom, fileDateTo));
        }
        if (isFilterSize())
        {
            filters.add(new FileSizeFilter(minFileSize));
        }

        return new CombinedFileFilter(filters);
    }

    /**
     * Sets default values for most of the properties. This method is called
     * when a new instance is created. It ensures that meaningful default values
     * are set when the view settings dialog is displayed.
     */
    private void initDefaults()
    {
        setBackgroundColor(ColorHelper.NamedColor.WHITE.getColor());
        setForegroundColor(ColorHelper.NamedColor.BLACK.getColor());
        setSelectionBackground(ColorHelper.NamedColor.BLUE.getColor());
        setSelectionForeground(ColorHelper.NamedColor.BLACK.getColor());
        setSortColumn(0);
        setSortDirectories(null);
        setSortDescending(false);
        setMinFileSize(1);
    }

    /**
     * A specialized comparator implementation that compares files by their
     * name.
     */
    private static class FileNameComparator implements Comparator<FileData>
    {
        @Override
        public int compare(FileData o1, FileData o2)
        {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }

    /**
     * A specialized comparator implementation that compares files by their
     * size.
     */
    private static class FileSizeComparator implements Comparator<FileData>
    {
        @Override
        public int compare(FileData o1, FileData o2)
        {
            return (int) (o1.getSize() - o2.getSize());
        }
    }

    /**
     * A specialized comparator implementation that compares files by their
     * date.
     */
    private static class FileDateComparator implements Comparator<FileData>
    {
        @Override
        public int compare(FileData o1, FileData o2)
        {
            return o1.getLastModified().compareTo(o2.getLastModified());
        }
    }

    /**
     * A comparator for comparing files with directories. It applies the sort
     * order for directories.
     */
    private static class FileDirComparator implements Comparator<FileData>
    {
        /** The sort order for directories. */
        private final int directorySortOrder;

        public FileDirComparator(int dirSortOrder)
        {
            directorySortOrder = dirSortOrder;
        }

        @Override
        public int compare(FileData o1, FileData o2)
        {
            if (directorySortOrder == 2)
            {
                // directories are sorted as files
                return 0;
            }

            boolean dir1 = o1.getFile().isDirectory();
            boolean dir2 = o2.getFile().isDirectory();
            if (dir1 ^ dir2)
            {
                // one is a file and one is a directory
                int result = (directorySortOrder == 0) ? -1 : 1;
                if (dir2)
                {
                    result = -result;
                }
                return result;
            }
            else
            {
                return 0;
            }
        }
    }

    /**
     * A specialized comparator implementation for comparing {@code FileData}
     * objects. This implementation takes all sort criteria into account
     * specified for the file list.
     */
    private static class FileDataComparator implements Comparator<FileData>
    {
        /** The comparator for directories and files. */
        private final Comparator<FileData> fileDirComparator;

        /** The comparator for files. */
        private final Comparator<FileData> fileComparator;

        /** A flag for reverse sort order. */
        private final boolean reverseOrder;

        public FileDataComparator(Comparator<FileData> dirComp,
                Comparator<FileData> fileComp, boolean reverse)
        {
            fileDirComparator = dirComp;
            fileComparator = fileComp;
            reverseOrder = reverse;
        }

        @Override
        public int compare(FileData o1, FileData o2)
        {
            int c = fileDirComparator.compare(o1, o2);
            if (c != 0)
            {
                return c;
            }

            c = fileComparator.compare(o1, o2);
            if (reverseOrder)
            {
                c = -c;
            }

            return c;
        }
    }

    /**
     * A filter implementation that deals with the file date range specified for
     * the current {@code ViewSettings} object.
     */
    private static class FileDateFilter implements FileFilter
    {
        /** The from date. */
        private final Date dtFrom;

        /** The to date. */
        private final Date dtTo;

        public FileDateFilter(Date from, Date to)
        {
            dtFrom = from;
            dtTo = to;
        }

        @Override
        public boolean accept(File file)
        {
            Date fileDate = new Date(file.lastModified());
            return (dtFrom == null || dtFrom.before(fileDate) || dtFrom
                    .equals(fileDate))
                    && (dtTo == null || dtTo.after(fileDate));
        }
    }

    /**
     * A file filter implementation that corresponds to the file size filter
     * criterion specified for the current {@code ViewSettings} object.
     */
    private static class FileSizeFilter implements FileFilter
    {
        /** The minimum file size (in bytes). */
        private final long minimumSize;

        /**
         * Creates a new instance of {@code FileSizeFilter} and initializes it
         * with the minimum file size to be accepted.
         *
         * @param minSize the minimum file size (in KB)
         */
        public FileSizeFilter(Integer minSize)
        {
            minimumSize = 1024L * minSize.longValue();
        }

        @Override
        public boolean accept(File file)
        {
            return file.length() >= minimumSize;
        }
    }

    /**
     * A file filter implementation that corresponds to the file types criterion
     * specified for the current {@code ViewSettings} object. This filter
     * accepts only files whose name extension is part of a given set.
     */
    private static class FileTypeFilter implements FileFilter
    {
        /** The set with name extensions to be accepted. */
        private final Set<String> extensions;

        /**
         * Creates a new instance of {@code FileTypeFilter} and initializes it
         * with the file name extensions to be accepted.
         *
         * @param exts the set with the allowed extensions
         */
        public FileTypeFilter(Set<String> exts)
        {
            extensions = exts;
        }

        @Override
        public boolean accept(File file)
        {
            int extpos = file.getName().lastIndexOf('.');
            if (extpos < 0)
            {
                // no extension
                return false;
            }

            String ext = file.getName().substring(extpos + 1).toLowerCase(
                    Locale.ENGLISH);
            return extensions.contains(ext);
        }
    }

    /**
     * A file filter implementation that combines multiple other filter objects.
     * An instance of this class is used to realize the filter conditions
     * defined for a {@code ViewSettings} object. For each filter criterion
     * specified by the user a sub filter is added to the combined filter.
     */
    private static class CombinedFileFilter implements FileFilter
    {
        /** A collection with the sub filters. */
        private final Collection<FileFilter> subFilters;

        /**
         * Creates a new instance of {@code CombinedFileFilter} and sets the
         * collection with the sub filters.
         *
         * @param subs the sub filters
         */
        public CombinedFileFilter(Collection<FileFilter> subs)
        {
            subFilters = subs;
        }

        @Override
        public boolean accept(File file)
        {
            for (FileFilter filter : subFilters)
            {
                if (!filter.accept(file))
                {
                    return false;
                }
            }

            return true;
        }
    }
}
