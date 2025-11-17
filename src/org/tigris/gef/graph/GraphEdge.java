package org.tigris.gef.graph;

import java.util.List;

import org.tigris.gef.graph.presentation.DefaultGraphModel;
import org.tigris.gef.graph.presentation.NetPort;

/**
 * This is the interface that defines "edges".
 * <p>
 * This is experimental to solve an issue that arose once implementing generics.
 * It is expected to work so would be expected to become part of the codebase
 * but I am also unsure of its final name and package.
 * <p>
 * p.s. Although this is being implemented to solve a particular design issue
 * I have a feeling that something like it should have originally been implemented.
 * I guess I'll know more once I get this in place and test it.
 * 
 * @author rwellman
 * @since [[]]<since
 *
 */
public interface GraphEdge {

    /**
     * Satisfies current implementation of both NetEdge and 
     * ArgoUML CommentEdge (with a little help).  Chose getDest() instead of
     * getDestination() just to prefer the current implementation of NetEdge.
     * 
     * @return
     */
    Object getDest();

    /**
     * Satisfies current implementation of both NetEdge and 
     * ArgoUML CommentEdge (with a little help).
     * 
     * @return
     */
    Object getSource();

    /**
     * I don't really think edges should have ports but maybe this
     * refers to ports it is connected to.  Anyway, this is necessary
     * to support DefaultGraphModel. more reseach may be needed.
     * 
     * @return
     */
    List<NetPort> getPorts();

    /**
     * 
     * @return
     */
    NetPort getDestPort();

    /**
     * 
     * @return
     */
    NetPort getSourcePort();

    /**
     * Ugh, I don't like this interface needing to reference DefaultGraphModel
     * but there is a method there that requires it.  Hopefully I can refactor
     * this out at some point.
     * 
     * @param defaultGraphModel
     * @param s
     * @param d
     * @return
     */
    <T extends DefaultGraphModel> boolean connect(T graphModel, NetPort s, NetPort d);

}
