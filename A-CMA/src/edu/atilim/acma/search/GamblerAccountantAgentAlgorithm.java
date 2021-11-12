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


public class GamblerAccountantAgentAlgorithm extends AbstractAlgorithm {
	
	private SolutionDesign current;
	private SolutionDesign best;
	private SolutionDesign primitive;
	
	private int maxIters;

	public GamblerAccountantAgentAlgorithm(SolutionDesign initialDesign, AlgorithmObserver observer, int maxIters) {
		super(initialDesign, observer);
		
		primitive = current = best = initialDesign;
		this.maxIters = maxIters;
	}

	@Override
	public String getName() {
		return "Gambler Accountant Agent";
	}
	
	@Override
	protected void beforeStart() {
		AlgorithmObserver observer = getObserver();
		if (observer != null) {
			observer.onStart(this, initialDesign);
			observer.onAdvance(this, 0, maxIters);
			observer.onUpdateItems(this, current, best, AlgorithmObserver.UPDATE_BEST & AlgorithmObserver.UPDATE_CURRENT);
		}
	}

	
	@Override
	protected void afterFinish() {
		AlgorithmObserver observer = getObserver();
		if (observer != null) {
			observer.onAdvance(this, maxIters, maxIters);
			observer.onFinish(this, best);
		}
	}
	
	private int uselessSteps = 0;

	@Override
	public boolean step() {
		AlgorithmObserver observer = getObserver();
		
		log("Starting iteration %d. Current score: %.6f, Best score: %.6f", getStepCount(), current.getScore(), best.getScore());
		
		if (getStepCount() > maxIters || uselessSteps > 250) {
			log("Algorithm finished, the final design score: %.6f", best.getScore());
			finalDesign = best;
			return true;
		}
		
		Double[] oldState = current.getSolutionState();

		 Action action = (uselessSteps<10) ? current.getGreedyActionFromDQN() : current.getRandomAction();

		SolutionDesign neighbor = current.apply(action);
		
		Double[] newState = neighbor.getSolutionState();
		
		double reward = neighbor.compareScoreTo(current) * 100;
		
		
		if (neighbor.isBetterThan(best)) {
			best = neighbor;			
			if (observer != null) {
				observer.onUpdateItems(this, current, best, AlgorithmObserver.UPDATE_BEST);
			}
		}
				
		current = neighbor;

		if (reward==0) uselessSteps++; else uselessSteps = 0;

		
		if (observer != null) {
			observer.onUpdateItems(this, current, best, AlgorithmObserver.UPDATE_CURRENT);
		}
		
		if (observer != null) {
			observer.onAdvance(this, getStepCount() + 1, maxIters);
		}
		
		return false;
	}
}
