package org.vcell.sybil.models.bpimport.edges;

/*   EdgeSBTray  --- by Oliver Ruebenacker, UCHC --- July to September 2009
 *   An SBBox and some process edges
 */

import java.util.Vector;

import org.vcell.sybil.models.sbbox.SBBox;

public class EdgeSBTray  {

	protected SBBox box;
	protected Vector<MutableEdge> edges = new Vector<MutableEdge>();

	public EdgeSBTray(SBBox box) { 
		this.box = box;
	}
	
	public SBBox box() { return box; }
	public Vector<MutableEdge> edges() { return edges; }
		
}
