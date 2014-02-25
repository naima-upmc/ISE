package ise;

import java.util.HashMap;
import java.util.List;

public class Algorithm {
	private Network net;
	private List<Integer> integer;
	
	public Node firstNodeVisitedByJonI(Path J, Path I) {
		return null;
	}
	
	public Node lastNodeVisitedByJonI(Path J, Path I) {
		return null;
	}
	
	public Node firstNodeVisitedByJonI(Flow J, Flow I) {
		return null;
	}
	
	public Node lastNodeVisitedByJonI(Flow J, Flow I) {
		return null;
	}
	
	public Node firstNodeVisitedByJonIRestrictedToH(Flow J, Flow I, Node H) {
		return null;
	}
	
	public Node lastNodeVisitedByJonIRestrictedToH(Flow J, Flow I, Node H) {
		return null;
	}	
	public int minTimeTakenFromSourceToH(Flow f, Node h) {
		return 0;
	}
	
	public int maxTimeTakenFromSourceToH(Flow f, Node h) {
		return 0;
	}
	
	Node slowestNodeVisitedByJonI(Flow i, Flow j) {
		return null;
	}
	
	Node slowestNodeVisitedByIonHisPathRestrictedToH(Flow I, Node H) {
		return null;
	}
	
	Node slowestNodeVisitedByJonIRestrictedToH(Flow J, Flow I, Node H) {
		return null;
	}
	
	int computeM(Flow i, Node h) {
		return 0;
	}
	
	int computeA(Flow i, Flow j) {
		int jitter = j.getJitter();
		Node first = firstNodeVisitedByJonI(i, j);
		int m = computeM(i, first);
		int smax = maxTimeTakenFromSourceToH(j, first);
		int result = smax - m + jitter;
		return result;
	}
	
	int computeARestrictedToH(Flow i, Flow j, Node H) {
		return 0;
	}
	
	int computeBetaSlow(Flow i) {
		return 0;
	}
	
	int computeDelta(Flow i, Node h) {
		return 0;
	}
	
	int computeW(Flow i, int t) {
		HashMap<Node, Integer> w = new HashMap<Node, Integer>();
		int w1 = 0;
		int w2 = 0;
		for(Node h : i.getPath().getNodes()) {
			w1 = subfunction_computeW_initialize_sequence(i, t, h);
			w2 = subfunction_computeW_nextof_sequence(i, t, h, w1, w);
			while(w1 != w2) {
				w1 = w2;
				w2 = subfunction_computeW_nextof_sequence(i, t, h, w1, w);
			}
			w.put(h, w1);
		}
		return w1;
	}
	
	int subfunction_computeW_initialize_sequence(Flow i, int t, Node h){
		int w0 = 0;
		for(Flow j : i.getHigherPriorityFlows()) {
			Node slow = slowestNodeVisitedByJonIRestrictedToH(j, i, h);
			w0+=slow.getCapacity().get(j);
		}
		for(Flow j : i.getSamePriorityFlows()) {
			Node slow = slowestNodeVisitedByJonIRestrictedToH(j, i, h);
			w0+=slow.getCapacity().get(j);
		}
		Node slow = slowestNodeVisitedByIonHisPathRestrictedToH(i, h);
		w0+=(1+(int)Math.floor(((double)(t+i.getJitter()))/(double)(i.getPeriod()))) * slow.getCapacity().get(i); 
		Path p = i.getPath().pathRestrictedToH(h);
		for(Node k : p.getNodes()) {
			if (k!=slow) {
				int max = 0;
				for(Flow j : i.getHigherPriorityFlows()) {
					if(firstNodeVisitedByJonIRestrictedToH(j, i, h) == firstNodeVisitedByJonIRestrictedToH(i, j, h)){
						int cap = k.getCapacity().get(j);
						if(cap>max) {
							max = cap;
						}
					}
				}
				for(Flow j : i.getSamePriorityFlows()) {
					if(firstNodeVisitedByJonIRestrictedToH(j, i, h) == firstNodeVisitedByJonIRestrictedToH(i, j, h)){
						int cap = k.getCapacity().get(j);
						if(cap>max) {
							max = cap;
						}
					}
				}
				int cap = k.getCapacity().get(i);
				if(cap>max) {
					max = cap;
				}
				w0+= max;
			}
		}
		w0-=h.getCapacity().get(i);
		w0+=computeDelta(i, h);
		w0+=(p.getNodes().size() - 1)*net.getLmax();
		return w0;
	}
	
	int subfunction_computeW_nextof_sequence(Flow i, int t, Node h, int w1, HashMap<Node, Integer> w){
		int w2 = 0;
		for(Flow j : i.getHigherPriorityFlows()) {
			Node slow = slowestNodeVisitedByJonIRestrictedToH(j, i, h);
			/* Please note that this is lastijh and not last"jih" */
			Node lastijh = lastNodeVisitedByJonIRestrictedToH(i, j, h);
			int val = 0;
			if (lastijh == h) {
				val = 1 + (int) Math.floor((double)(w1-minTimeTakenFromSourceToH(j, h)+computeARestrictedToH(i, j, h))/(double)(j.getJitter()));
			} else {
				val = 1 + (int) Math.floor((double)(w.get(lastijh)-minTimeTakenFromSourceToH(j, lastijh)+computeARestrictedToH(i, j, h))/(double)(j.getJitter()));
			}
			if(val<0){
				val = 0;
			}
			w2 += val * slow.getCapacity().get(j);
		}
		for(Flow j : i.getSamePriorityFlows()) {
			Node slow = slowestNodeVisitedByJonIRestrictedToH(j, i, h);
			Node firstjih = firstNodeVisitedByJonIRestrictedToH(j, i, h);
			int val = 1 + (int) Math.floor((double)(t+maxTimeTakenFromSourceToH(i, firstjih)-minTimeTakenFromSourceToH(j, firstjih)+computeARestrictedToH(i, j, h))/(double)(j.getJitter()));
			if(val<0){
				val = 0;
			}
			w2 += val * slow.getCapacity().get(j);
		}
		Node slow = slowestNodeVisitedByIonHisPathRestrictedToH(i, h);
		Node firsti = i.getPath().getNodes().get(0);
		int val = 1 + (int) Math.floor((double)(t+maxTimeTakenFromSourceToH(i, firsti)-minTimeTakenFromSourceToH(i, firsti)+computeARestrictedToH(i, i, h))/(double)(i.getJitter()));
		if(val<0){
			val = 0;
		}
		w2 += val * slow.getCapacity().get(i);
		Path p = i.getPath().pathRestrictedToH(h);
		for(Node k : p.getNodes()) {
			if(k != slow){
				int max = 0;
				for(Flow j : i.getHigherPriorityFlows()) {
					if(firstNodeVisitedByJonIRestrictedToH(j, i, h) == firstNodeVisitedByJonIRestrictedToH(i, j, h)){
						int cap = k.getCapacity().get(j);
						if(cap>max) {
							max = cap;
						}
					}
				}
				for(Flow j : i.getSamePriorityFlows()) {
					if(firstNodeVisitedByJonIRestrictedToH(j, i, h) == firstNodeVisitedByJonIRestrictedToH(i, j, h)){
						int cap = k.getCapacity().get(j);
						if(cap>max) {
							max = cap;
						}
					}
				}
				int cap = k.getCapacity().get(i);
				if(cap>max) {
					max = cap;
				}
				w2+= max;
			}
		}
		w2-=h.getCapacity().get(i);
		w2+=computeDelta(i, h);
		w2+=(p.getNodes().size() - 1)*net.getLmax();
		return w2;
	}
}
