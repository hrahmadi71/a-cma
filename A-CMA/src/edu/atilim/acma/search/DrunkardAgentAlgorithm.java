package edu.atilim.acma.search;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import edu.atilim.acma.transition.ActionRegistry;
import edu.atilim.acma.transition.ActionRegistry.Entry;
import edu.atilim.acma.transition.actions.Action;

import edu.atilim.acma.metrics.MetricSummary;
import edu.atilim.acma.util.DQNApis;


public class DrunkardAgentAlgorithm extends AbstractAlgorithm {
	
	private SolutionDesign current;
	private SolutionDesign best;
	
	private int maxIters;

	public DrunkardAgentAlgorithm(SolutionDesign initialDesign, AlgorithmObserver observer, int maxIters) {
		super(initialDesign, observer);
		
		current = best = initialDesign;
		this.maxIters = maxIters;
	}

	@Override
	public String getName() {
		return "Drunkard Agent";
	}
	
	@Override
	protected void beforeStart() {
		AlgorithmObserver observer = getObserver();
		if (observer != null) {
			observer.onStart(this, initialDesign);
			observer.onAdvance(this, 0, maxIters);
			observer.onUpdateItems(this, current, best, AlgorithmObserver.UPDATE_BEST & AlgorithmObserver.UPDATE_CURRENT);
		}
		DQNApis.sendPossibleActions(current.getAllActions());
	}

	
	@Override
	protected void afterFinish() {
		AlgorithmObserver observer = getObserver();
		if (observer != null) {
			observer.onAdvance(this, maxIters, maxIters);
			observer.onFinish(this, best);
		}
	}
	

	@Override
	public boolean step() {
		AlgorithmObserver observer = getObserver();
		
		log("Starting iteration %d. Current score: %.6f, Best score: %.6f", getStepCount(), current.getScore(), best.getScore());
		
		if (getStepCount() > maxIters) {
			log("Algorithm finished, the final design score: %.6f", best.getScore());
			finalDesign = best;
			return true;
		}
		
		Double[] oldState = current.getSolutionState();
		
		Action action = current.getRandomAction();
		
//		Action action = current.getGreedyActionFromDQN();
		
//		Action action = (Math.random()<(double)getStepCount()/maxIters) ? current.getGreedyActionFromDQN() : current.getRandomAction(); 
//		Action action = (Math.random()<0.05) ? current.getGreedyActionFromDQN() : current.getRandomAction(); 
		
		SolutionDesign neighbor = current.apply(action);
		
		Double[] newState = neighbor.getSolutionState();
//		
		double reward = neighbor.compareScoreTo(current) * 1000;
		
		
		if (neighbor.isBetterThan(best)) {
			best = neighbor;
			reward *= 1.2;
			
			if (observer != null) {
				observer.onUpdateItems(this, current, best, AlgorithmObserver.UPDATE_BEST);
			}
		}
		
//		if(reward<=0) {
//			reward += neighbor.compareScoreTo(best);
//		}
//		int t = action.getType();
//		int id = action.getId();
		System.out.println("reward: " + Double.toString(reward));
		
		DQNApis.train(action.getType(), action.getId(), oldState, newState, action.getParams(), reward);
		
		current = neighbor;
		
		if (observer != null) {
			observer.onUpdateItems(this, current, best, AlgorithmObserver.UPDATE_CURRENT);
		}
		
		if (observer != null) {
			observer.onAdvance(this, getStepCount() + 1, maxIters);
		}
		
		return false;
	}
}
