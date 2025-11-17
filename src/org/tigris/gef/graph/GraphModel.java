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

// File: GraphModel.java
// Interfaces: GraphModel
// Original Author: jrobbins@ics.uci.edu
// $Id$

package org.tigris.gef.graph;

import java.util.*;

/**
 * This interface provides a facade to a net-level representation. 
 * Similiar in concept to the Swing class TreeModel.
 * <p>
 * The idea is not to have a widget (like JGraph) storing all the information
 * that it should display, and the programmer having to keep the widget's data
 * in synch with the application's data. Instead, the programmer defines a Model
 * class that gives the widget access to the application data. That way there is
 * only one copy of the data and nothing can get out of synch. If you don't have
 * your own application data objects, there is a Default implementation of the
 * Model that will store it for you.
 * <p>
 * Instead of asking application programmers to subclass their data objects from
 * some predefined base class (like NetNode), this interface allows the use of
 * any application object as a node, port, or edge. This makes it much easier to
 * add a visualization to an existing application.
 */
public interface GraphModel<N, E, P> extends BaseGraphModel<N, E, P> {

    /** Return all nodes in the graph */
    List<N> getNodes();

    /** Return all nodes in the graph - adds them to the collection given. */
    Collection<N> getNodes(Collection<N> c);

    /** Return all edges in the graph */
    List<E> getEdges();

    /** Return all edges in the graph - adds them to the collection given. */
    Collection<E> getEdges(Collection<E> c);

    /** 
     * Return all ports on node or edge.
     * <p>
     * NOTE: 0.14 marking this deprecated to help with cleanup,
     * but it will probably take some time to clean it all up.
     * Being replaced by better class specific methods:
     * getNodePorts() and getEdgePorts().
     * p.s.  After starting to refactor a bit, I may have to research the codebase
     * a bit more because it seems almost ridiculous to have this method
     * when both nodes and edges have their own getPorts() method.  Thus,
     * this really just appears to defer to those one-liners.  It seems logical
     * enough that the client can be responsible for calling the correct method
     * making all three(3) of these interface methods pointless.
     * 
     * @deprecated
     */
    List<P> getPorts(Object nodeOrEdge);

    /**
     * Node specific replacement for getPorts().
     * 
     * @param node
     * @return
     */
    List<P> getNodePorts(N node);

    /**
     * Edge specific replacement for getPorts().
     * 
     * @param nodeOrEdge
     * @return
     */
    List<P> getEdgePorts(E nodeOrEdge);

    /** Return all edges going to given port */
    List<E> getInEdges(P port);

    /** Return all edges going from given port */
    List<E> getOutEdges(P port);

    /**
     * Set the rules dictating which ports can be connected by which edge
     * 
     * @param cc A connection constrainer
     */
    public void setConnectionConstrainer(ConnectionConstrainer cc);

} /* end interface GraphModel */
