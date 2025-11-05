// Copyright (c) 1996-99 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

// File: CmdSaveGraphics.java
// Classes: CmdSaveGraphics
// Original Author: wienberg@informatik.uni-hamburg.de

package org.tigris.gef.base;

import java.awt.FileDialog;
import java.awt.Rectangle;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract Cmd to save a diagram as Graphics in a supplied OutputStream.
 * <p>
 * Operates on the diagram in the current editor.
 * 
 * @deprecated in 0.12.3 use SaveGraphicsAction
 * @author Frank Wienberg, wienberg@informatik.uni-hamburg.de
 */

public abstract class CmdSaveGraphics extends Cmd implements FilenameFilter {

    private static Log LOG = LogFactory.getLog(LayerDiagram.class);

    private static final long serialVersionUID = 1L;

    protected int scale = 1;

    protected abstract void saveGraphics(OutputStream s, Editor ce, Rectangle drawingArea) 
            throws IOException;

    protected CmdSaveGraphics(String name) {
        super(name);
    }

    /**
     * Set the outputStream argument. This must be done prior to saving the
     * image.
     * 
     * @param s
     *                the OutputStream into which the image will be saved
     */

    public void setStream(OutputStream s) {
        setArg("outputStream", s);
    }

    /**
     * Increasing this number effectively improves the resolution of the result
     * in case it is pixel-based.
     * 
     * @param s
     *                the scale (default = 1)
     */
    public void setScale(int s) {
        scale = s;
    }

    /**
     * Write the diagram contained by the current editor into an OutputStream as
     * a GIF image. The "outputStream" argument must have been previously set
     * with setStream().
     */

    public void doIt() {
        // FIX - what's the global exception handling strategy?
        // Should this method ensure that no exceptions are propagated?

        Editor ce = Globals.curEditor();

        Rectangle drawingArea = ce.getLayerManager().getActiveLayer().calcDrawingArea();
        if (LOG.isDebugEnabled())
            LOG.debug("Bounding box: " + drawingArea);

        if (drawingArea.width <= 0 || drawingArea.height <= 0) {
            if (LOG.isDebugEnabled())
                LOG.debug("Graphics generation aborted.");
            return;
        }

        String path = "";
        String filename = "file"; 

        OutputStream s = (OutputStream) getArg("outputStream");
        if (s == null) {
            FileDialog fd = new FileDialog(ce.findFrame(),
                    "Save Diagram in PGML format", FileDialog.SAVE);
            fd.setFilenameFilter(this);
            fd.setDirectory(Globals.getLastDirectory());
            fd.setVisible(true);
            filename = fd.getFile(); // blocking
            path = fd.getDirectory(); // blocking

            if (filename != null) {
                try {
                    File file = fd.getFiles()[0]; // new File(path, filename);
                    s = new BufferedOutputStream(new FileOutputStream(file));
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                return;
            }
        }

        // Determine the bounds of the diagram.
        //
        // FIX - this is a little glitchy. It appears that some elements
        // will underreport their size and others will overreport. Various
        // line styles seem to have the problem. Haven't spent any time
        // trying to figure it out.
        /*
         * int xmin = 99999, ymin = 99999; Fig f = null; Rectangle rectSize =
         * null; Rectangle drawingArea = new Rectangle( 0, 0 ); Enumeration iter =
         * ce.figs(); while( iter.hasMoreElements() ) { f = (Fig)
         * iter.nextElement(); rectSize = f.getBounds(); xmin = Math.min( xmin,
         * rectSize.x ); ymin = Math.min( ymin, rectSize.y ); drawingArea.add(
         * rectSize ); }
         * 
         * drawingArea.width -= xmin; drawingArea.height -= ymin; drawingArea.x =
         * xmin; drawingArea.y = ymin; drawingArea.grow(4,4); // security border
         */

        Globals.showStatus("Writing " + path + filename + "...");

        // Tell the editor to hide the grid before exporting:

        boolean h = ce.getGridHidden();
        ce.setGridHidden(true);

        // Now, do the real work:
        try {
            saveGraphics(s, ce, drawingArea);
        } catch (java.io.IOException e) {
            LOG.error("Error while exporting Graphics:", e);
        } finally {
            try {
                s.close();
            } catch (IOException e) {
                LOG.error("Error while closing output stream:", e);
            }
        }

        // Restore old grid state:
        ce.setGridHidden(h);

//        System.out.println("save done");
        Globals.showStatus("Done Writing: " + path + filename);

//      System.out.println("Cmd save in SVG...");


        // ce.setTitle(filename);

    }

    /**
     * Undo stub. No useful implementation.
     */

    public void undoIt() {
        if (LOG.isWarnEnabled())
            LOG.warn("Undo does not make sense for CmdSaveGraphics");
    }

    @Override
    public boolean accept(File dir, String name) {
        // TODO Auto-generated method stub
        return false;
    }

} /* end class CmdSaveGraphics */
