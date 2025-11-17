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

// File: DefaultGraphModel.java
// Interfaces: DefaultGraphModel
// Original Author: jrobbins@ics.uci.edu
// $Id$

package org.tigris.gef.graph.presentation;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tigris.gef.graph.ConnectionConstrainer;
import org.tigris.gef.graph.GraphEdge;
import org.tigris.gef.graph.GraphNodeHooks;
import org.tigris.gef.graph.MutableGraphSupport;

/**
 * This implementation of GraphModel uses the following GEF classes:<br>
 * NetList, NetNode, NetPort, and NetEdge. 
 * <p>
 * If you implement your own GraphModel, you can use your own application-specific classes.
 * 
 * @see NetList
 * @see NetNode
 * @see NetPort
 * @see NetEdge
 * @see AdjacencyListGraphModel
 */

public class DefaultGraphModel extends MutableGraphSupport implements
        java.io.Serializable {

    private NetList netList;

    private static Log LOG = LogFactory.getLog(DefaultGraphModel.class);

    private static final long serialVersionUID = 8098329898758384131L;

    // //////////////////////////////////////////////////////////////
    // constructors

    public DefaultGraphModel() {
        netList = new NetList();
    }

    public DefaultGraphModel(ConnectionConstrainer cc) {
        super(cc);
        netList = new NetList();
    }

    public DefaultGraphModel(NetList nl) {
        netList = nl;
    }

    /**
     * Return all nodes in the graph
     */
    @Override
    public List<NetNode> getNodes() {
        return netList.getNodes();
    }

    /**
     * Return all edges in the graph
     */
    @Override
    public List<GraphEdge> getEdges() {
        return netList.getEdges();
    }

    /*
     * Not sure what use case the next two methods satisfy?
     * They are not part of any interface (currently)
     * (small update) see NetList javadoc that I added for
     * apparent reason these methods exist.
     */

    /** Return all nodes in the graph */
    @Override
    public Collection<NetNode> getNodes(Collection<NetNode> c) {
        return netList.getNodes(c);
    }

    /** Return all edges in the graph */
    @Override
    public Collection<GraphEdge> getEdges(Collection<GraphEdge> c) {
        return netList.getEdges(c);
    }

    /** Return all ports on node or edge */
    @Override
    public List<NetPort> getPorts(Object nodeOrEdge) {
        if (nodeOrEdge instanceof NetNode)
            return getNodePorts((NetNode) nodeOrEdge);
        if (nodeOrEdge instanceof GraphEdge)
            return getEdgePorts((GraphEdge) nodeOrEdge);

        return null; // raise exception
    }

    /** Return all ports on node */
    @Override
    public List<NetPort> getNodePorts(NetNode node) {
        return node.getPorts();
    }

    /** Return all ports on edge */
    @Override
    public List<NetPort> getEdgePorts(GraphEdge edge) {
        return edge.getPorts();
    }

    
    /** Return the node or edge that owns the given port */
    @Override
    public NetNode getOwner(NetPort port) {
        return port.getParent();
    }

    /** Return all edges going to given port */
    @Override
    public List<GraphEdge> getInEdges(NetPort port) {
        Vector<GraphEdge> res = new Vector<>();

        for (GraphEdge ne: port.getEdges()) {
            if (ne.getDestPort() == port) {
                res.add(ne);
            }
        }
//        Vector<NetEdge> edge = port.getEdges();
//        for (int i = 0; i < edge.size(); i++) {
//            NetEdge ne = (NetEdge) edge.elementAt(i);
//            if (ne.getDestPort() == port) {
//                res.add(ne);
//            }
//        }

        return res;
    }

    /** Return all edges going from given port */
    @Override
    public List<GraphEdge> getOutEdges(NetPort port) {
        Vector<GraphEdge> res = new Vector<>();

        for (GraphEdge ne: port.getEdges()) {
            if (ne.getSourcePort() == port) {
                res.add(ne);
            }
        }
//        Vector<NetEdge> edge = port.getEdges();
//        for (int i = 0; i < edge.size(); i++) {
//            NetEdge ne = (NetEdge) edge.elementAt(i);
//            if (ne.getSourcePort() == port) {
//                res.add(ne);
//            }
//        }

        return res;
    }

    /** Return one end of an edge */
    @Override
    public NetPort getSourcePort(GraphEdge edge) {
        return edge.getSourcePort();
    }

    /** Return the other end of an edge */
    @Override
    public NetPort getDestPort(GraphEdge edge) {
        return edge.getDestPort();
    }

    // //////////////////////////////////////////////////////////////
    // interface MutableGraphModel

    /** Return a valid node in this graph */
    @Override
    public NetNode createNode(String name, @SuppressWarnings("rawtypes") Hashtable args) {
        Object newNode;
        // Class nodeClass = (Class) getArg("className", DEFAULT_NODE_CLASS);
        // assert _nodeClass != null
        try {
            newNode = Class.forName(name).newInstance();
        } catch (java.lang.ClassNotFoundException ignore) {
            return null;
        } catch (java.lang.IllegalAccessException ignore) {
            return null;
        } catch (java.lang.InstantiationException ignore) {
            return null;
        }

        if (newNode instanceof GraphNodeHooks)
            ((GraphNodeHooks) newNode).initialize(args);
        return (NetNode) newNode;
    }

    /** Return true if the given object is a valid node in this graph */
    @Override
    public boolean canAddNode(NetNode node) {
        return (node instanceof NetNode);
    }

    /** Return true if the given object is a valid edge in this graph */
    @Override
    public boolean canAddEdge(GraphEdge edge) {
        return (edge instanceof GraphEdge);
    }

    /** Remove the given node from the graph. */
    @Override
    public void removeNode(NetNode node) {
        netList.removeNode(node);
        LOG.debug("Removed node from graph model");
        super.removeNode(node);
    }

    /** Return true if dragging the given object is a valid in this graph */
    @Override
    public boolean canDragNode(NetNode node) {
        return (node instanceof NetNode);
    }

    /** Add the given node to the graph, if valid. */
    @Override
    public void addNode(NetNode node) {
        netList.addNode(node);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Added a node. There are now "
                    + netList.getNodes(null).size() + " edges");
        }
        super.addNode(node);
    }

    /** Add the given edge to the graph, if valid. */
    @Override
    public void addEdge(GraphEdge e) {
        netList.addEdge(e);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Added an edge. There are now "
                    + netList.getEdges(null).size() + " edges");
        }
        super.addEdge(e);
    }

    @Override
    public void addNodeRelatedEdges(NetNode node) {
    }

    /** Remove the given edge from the graph. */
    @Override
    public void removeEdge(GraphEdge edge) {
        LOG.debug("DefaultGraphModel::removeEdge");
        netList.removeEdge(edge);
        super.removeEdge(edge);
    }

    /** Remove all the nodes from the graph. */
    @Override
    public void removeAllNodes() {
        LOG.debug("Removing all the nodes from the graph.");
        netList.removeAllNodes();
        super.removeAllNodes();
    }

    /** Remove all the edges from the graph. */
    @Override
    public void removeAllEdges() {
        LOG.debug("Removing all the edges from the graph.");
        netList.removeAllEdges();
        super.removeAllEdges();
    }

    /** Remove all nodes and edges to reset the graph. */
    @Override
    public void removeAll() {
        netList.removeAllEdges();
        netList.removeAllNodes();
        super.removeAll();
    }

    @Override
    public void dragNode(NetNode node) {
        addNode(node);
    }

    /**
     * Return true if the two given ports can be connected by a kind of edge to
     * be determined by the ports.
     */
    @Override
    public boolean canConnect(NetPort srcPort, NetPort destPort) {
        if (srcPort instanceof NetPort && destPort instanceof NetPort) {
            NetPort s = (NetPort) srcPort;
            NetPort d = (NetPort) destPort;
            if (LOG.isDebugEnabled())
                LOG.debug("Checking with ports to see if connection valid");
            return s.canConnectTo(this, d) && d.canConnectTo(this, s);
        } else {
            if (LOG.isDebugEnabled())
                LOG.debug("By default, cannot connect non-NetPort objects");
            return false;
        }
    }

    /** Construct and add a new edge of a kind determined by the ports */
    @Override
    public GraphEdge connect(NetPort srcPort, NetPort destPort) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Attempting to connect " + srcPort + " to " + destPort);
        }
        if (!canConnect(srcPort, destPort)) {
            LOG.warn("Connection not allowed");
            return null;
        }
        if (srcPort instanceof NetPort && destPort instanceof NetPort) {
            NetPort s = (NetPort) srcPort;
            NetPort d = (NetPort) destPort;
            // System.out.println("calling makeEdgeFor:" +
            // s.getClass().getName());
            GraphEdge e = s.makeEdgeFor(d);
            return connectInternal(s, d, e);
        } else
            return null;
    }

    /**
     * Construct and add a new edge of the given kind.
     * The default is to assume the edge type is a Class.
     */
    @Override
    public GraphEdge connect(NetPort srcPort, NetPort destPort, Object edgeType) {
        return connect(srcPort, destPort, edgeType);
    }

    /** Construct and add a new edge of the given kind */
    @Override
    @SuppressWarnings("rawtypes")
    public GraphEdge connect(NetPort srcPort, NetPort destPort, Class edgeClass) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Attempting to connect " + srcPort + " to " + destPort
                    + " with " + edgeClass);
        }
        if (!canConnect(srcPort, destPort, edgeClass)) {
            LOG.warn("Connection not allowed");
            return null;
        }
        if (srcPort instanceof NetPort && destPort instanceof NetPort) {
            NetPort s = (NetPort) srcPort;
            NetPort d = (NetPort) destPort;
            try {
                GraphEdge e = (GraphEdge) edgeClass.newInstance();
                return connectInternal(s, d, e);
            } catch (java.lang.InstantiationException e) {
            } catch (java.lang.IllegalAccessException e) {
            }
        }
        return null;
    }

    /**
     * Asks the given edge to attempt to connect itself to the given ports.
     * 
     * @param s
     *                source port
     * @param d
     *                destination port
     * @param e
     *                edge
     * @return the edge or null if the edge rejects the connection.
     */
    protected GraphEdge connectInternal(NetPort s, NetPort d, GraphEdge e) {
        if (e.connect(this, s, d)) {
            addEdge(e);
            return e;
        } else {
            return null;
        }
    }

    /**
     * Return true if the connection to the old node can be rerouted to the new
     * node.
     */
    @Override
    public boolean canChangeConnectedNode(Object newNode, Object oldNode, Object edge) {
        return false;
    }

    /**
     * Reroutes the connection to the old node to be connected to the new node.
     */
    @Override
    public void changeConnectedNode(NetNode newNode, NetNode oldNode, GraphEdge edge, boolean isSource) {
    }

    @Override
    public GraphEdge connect(NetPort fromPort, NetPort toPort, Object edgeType, Map<?, ?> attributes) {
        // TODO Auto-generated method stub
        return null;
    }

} /* end class DefaultGraphModel */
