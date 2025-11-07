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

package org.tigris.gef.graph.presentation;

import java.util.*;

import org.tigris.gef.graph.*;

/**
 * This class is an example of an alternative way to implement MutableGraphModel. 
 * Needs-more-work: this code has not been used or tested.
 * <p>
 * Refactor with generics:  This graph model defines "edges" as object arrays
 * where: Object[] edge = new Object[3];
 *         e[0] = srcPort; NetPort
 *         e[1] = destPort; NetPort
 *         e[2] = label; Object (probably a String but leaving Object for now)
 *         
 * Note:  It would probably be a simple matter to introduce a new class
 * that defines an edge like this but I am just trying to get this to 
 * compile for now; especially since I do not think there is a usage
 * of this class to test based on the comment above.
 * 
 * @see DefaultGraphModel
 */
@SuppressWarnings("serial")
public abstract class AdjacencyListGraphModel implements 
    MutableGraphModel<NetNode, Object[], NetPort>,
    java.io.Serializable {

    // //////////////////////////////////////////////////////////////
    // constants

    public static String UNLABELED = "Unlabeled";

    // //////////////////////////////////////////////////////////////
    // instance variables

    protected Vector<NetNode> _nodes = new Vector<>();
    protected Vector<Object[]> _edges = new Vector<>();

    // //////////////////////////////////////////////////////////////
    // constructors

    public AdjacencyListGraphModel() {
    }

    // //////////////////////////////////////////////////////////////
    // invariants

    public boolean OK() {
        if (_nodes == null)
            return false;
        if (_edges == null)
            return false;

        // all edges must start and end on some port on a node in this graph
        for (Object[] e : _edges) {
            if (!containsPort((NetPort)e[0] ) || !containsPort((NetPort)e[1]))
                return false;
        }
        // all edges must start and end on some port on a node in this graph
//        Enumeration<NetEdge> edgeNum = _edges.elements();
//        while (edgeNum.hasMoreElements()) {
//            Object[] e = (Object[]) edgeNum.nextElement();
//            if (!containsPort(e[0]) || !containsPort(e[1]))
//                return false;
//        }
        return true;
    }

    public boolean containsPort(NetPort port) {
        return containsNodePort(port) || containsEdgePort(port);
    }

    // Not defined in interface?
    public boolean containsNodePort(NetPort port) {
        List<NetNode> nodes = getNodes();
        if (nodes == null) {
            return false;
        }
        for (int i = 0; i < nodes.size(); ++i) {
            List<NetPort> ports = getPorts(nodes.get(i));
            if (ports != null && ports.contains(port)) {
                return true;
            }
        }
        return false;
    }

    // Not defined in interface?
    public boolean containsEdgePort(NetPort port) {
        List<NetNode> edges = getNodes();
        if (edges == null) {
            return false;
        }
        for (int i = 0; i < edges.size(); ++i) {
            List<NetPort> ports = getPorts(edges.get(i));
            if (ports != null && ports.contains(port)) {
                return true;
            }
        }
        return false;
    }

    // //////////////////////////////////////////////////////////////
    // GraphModel implementation

    @Override
    public List<NetNode> getNodes() {
        return _nodes;
    }

    @Override
    public List<Object[]> getEdges() {
        return _edges;
    }

    public abstract List<NetPort> getPorts(Object nodeOrEdge);

    public abstract NetNode getOwner(NetPort port);

    @Override
    public NetPort getSourcePort(Object[] edge) {
        Object[] labeledEgde = (Object[]) edge;
        return (NetPort)labeledEgde[0];
    }

    public NetPort getDestPort(Object[] edge) {
        Object[] labeledEgde = (Object[]) edge;
        return (NetPort)labeledEgde[1];
    }

    public List<Object[]> getInEdges(NetPort port) {
        Vector<Object[]> res = new Vector<>();
        Enumeration<Object[]> edgeEnum = _edges.elements();
        while (edgeEnum.hasMoreElements()) {
            Object[] e = (Object[]) edgeEnum.nextElement();
            if (port == e[1])
                res.addElement(e);
        }
        return res;
    }

    public List<Object[]> getOutEdges(NetPort port) {
        Vector<Object[]> res = new Vector<>();
        Enumeration<Object[]> edgeEnum = _edges.elements();
        while (edgeEnum.hasMoreElements()) {
            Object[] e = (Object[]) edgeEnum.nextElement();
            if (port == e[0])
                res.addElement(e);
        }
        return res;
    }

    // //////////////////////////////////////////////////////////////
    // MutableGraphModel implementation

    // needs-more-work: notifications

    @Override
    public boolean canAddNode(NetNode node) {
        return true;
    }

    @Override
    public boolean canAddEdge(Object[] edge) {
        return (edge instanceof Object[]) && ((Object[]) edge).length == 3;
    }

    public void addNode(NetNode node) {
        _nodes.addElement(node);
    }

    public void addEdge(Object[] edge) {
        if (canAddEdge(edge))
            _edges.addElement(edge);
    }

    public void removeNode(NetNode node) {
        _nodes.removeElement(node);
        // needs-more-work: remove associated edges
    }

    public void removeEdge(Object[] edge) {
        _edges.removeElement(edge);
    }

    public boolean canConnect(NetNode srcNode, NetNode destNode) {
        return true;
    }

    public Object[] connect(NetPort srcPort, NetPort destPort) {
        return addLabeledEdge(srcPort, destPort, UNLABELED);
    }

    // //////////////////////////////////////////////////////////////
    // labeled edges

    public Object getEdgeLabel(Object edge) {
        Object[] labeledEgde = (Object[]) edge;
        return labeledEgde[2];
    }

    public Object[] addLabeledEdge(NetPort srcPort, NetPort destPort, Object label) {
        Object[] e = new Object[3];
        e[0] = srcPort;
        e[1] = destPort;
        e[2] = label;
        addEdge(e);
        return e;
    }

    public Vector<Object[]> getEdgesLabeled(Object label) {
        Vector<Object[]> res = new Vector<>();
        for (Object[] edge : _edges) {
            if (label == getEdgeLabel(edge))
                res.addElement(edge);
        }
//        Enumeration<NetEdge> edgeEnum = _edges.elements();
//        while (edgeEnum.hasMoreElements()) {
//            Object[] e = (Object[]) edgeEnum.nextElement();
//            if (label == getEdgeLabel(e))
//                res.addElement(e);
//        }
        return res;
    }

} /* end class AdjacencyListGraphModel */
